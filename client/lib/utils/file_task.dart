import 'dart:convert';
import 'dart:io';

import 'package:async/async.dart';
import 'package:convert/convert.dart';
import 'package:crypto/crypto.dart';
import 'package:flutter/foundation.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/file_utils.dart';

enum FileTaskStatus {
  /// 等待中
  pending,

  /// 进行中
  processing,

  /// 暂停
  paused,

  /// 已完成
  done,

  /// 错误
  error;

  String get text => switch (this) {
    pending => S.current.pending,
    processing => S.current.processing,
    paused => S.current.paused,
    done => S.current.done,
    error => S.current.error,
  };
}

abstract class FileTask {
  /// 文件名
  String name;

  /// 文件大小
  int size;

  /// 任务创建时间
  DateTime createTime;

  /// 任务完成时间
  DateTime? doneTime;

  /// 状态
  FileTaskStatus status = FileTaskStatus.pending;

  bool private;

  Object? error;

  bool selected = false;

  VoidCallback? onDone;

  FileTask({
    required this.name,
    required this.size,
    required this.createTime,
    required this.private,
  });

  /// 是否已完成
  bool get isDone => status == FileTaskStatus.done;

  bool get canPause =>
      status == FileTaskStatus.processing || status == FileTaskStatus.pending;

  bool get canStart =>
      status == FileTaskStatus.paused || status == FileTaskStatus.error;

  bool get canOp => status != FileTaskStatus.done;

  double get progress;

  String get progressStr;
}

/// 上传文件任务
class UploadFileTask extends FileTask {
  /// 文件
  File file;

  /// 目标路径
  String path;

  int uploaded = 0;

  UploadFileTask({
    required this.file,
    required this.path,
    required super.name,
    required super.size,
    required super.createTime,
    required super.private,
  });

  @override
  double get progress => uploaded.toDouble() / size;

  @override
  String get progressStr => (progress * 100).toStringAsFixed(1);

  Future<List<int>> _readChunk(ChunkedStreamReader<int> reader) async {
    return await Future.delayed(const Duration(), () async {
      return await reader.readChunk(4 * 1024 * 1024);
    });
  }

  Future<String> _calcHash() async {
    final reader = ChunkedStreamReader(file.openRead());
    var output = AccumulatorSink<Digest>();
    var input = sha256.startChunkedConversion(output);
    try {
      while (true) {
        final chunk = await _readChunk(reader);
        if (chunk.isEmpty) {
          break;
        } else {
          input.add(chunk);
        }
      }
    } finally {
      reader.cancel();
    }
    input.close();
    return base64UrlEncode(output.events.single.bytes);
  }

  start() async {
    try {
      final hash = await _calcHash();
      status = FileTaskStatus.processing;

      final reader = ChunkedStreamReader(file.openRead());
      int start = 0, end = -1;
      try {
        while (true) {
          final chunk = await reader.readChunk(1024 * 256);
          if (chunk.isEmpty) {
            break;
          } else {
            end += chunk.length;
            final r = await FileAPI.upload(
              path,
              Uint8List.fromList(chunk),
              start: start,
              end: end,
              size: size,
              hash: hash,
              private: private,
            );
            if (r) {
              uploaded = end;
              status = FileTaskStatus.done;
              doneTime = DateTime.now();
              onDone?.call();
              return;
            }
            start += chunk.length;
            uploaded = start;
          }
        }
        await FileAPI.upload(
          path,
          null,
          start: start,
          end: start,
          size: size,
          hash: hash,
          private: private,
        );
      } finally {
        reader.cancel();
      }
      status = FileTaskStatus.done;
      doneTime = DateTime.now();
      onDone?.call();
    } catch (e) {
      status = FileTaskStatus.error;
      error = e;
      if (kDebugMode) {
        print(e);
      }
    }
  }
}

/// 下载文件任务
class DownloadFileTask extends FileTask {
  final FileInfo file;
  final String dst;

  /// 已下载大小
  int downloaded = 0;

  bool _started = false;

  DownloadFileTask({
    required this.file,
    required this.dst,
    required super.name,
    required super.size,
    required super.createTime,
    required super.private,
  });

  /// 进度[0-1]
  @override
  double get progress => downloaded.toDouble() / size;

  @override
  String get progressStr => (progress * 100).toStringAsFixed(1);

  start() {
    if (_started) return;
    _started = true;
    FileAPI.download(file.fullPath, dst, private: private, (count, total) {
      downloaded = count;
      size = total;
      if (count == total) {
        status = FileTaskStatus.done;
        doneTime = DateTime.now();
        onDone?.call();
      }
    });
  }
}
