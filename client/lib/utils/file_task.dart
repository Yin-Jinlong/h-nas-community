import 'package:flutter/animation.dart';
import 'package:h_nas/generated/l10n.dart';

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

class FileTask {
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

  Object? error;

  AnimationController? controller;

  bool selected = false;

  FileTask({required this.name, required this.size, required this.createTime});

  /// 是否已完成
  bool get isDone => status == FileTaskStatus.done;

  bool get canPause =>
      status == FileTaskStatus.processing || status == FileTaskStatus.pending;

  bool get canStart =>
      status == FileTaskStatus.paused || status == FileTaskStatus.error;

  bool get canOp => status != FileTaskStatus.done;
}

/// 上传文件任务
class UploadFileTask extends FileTask {
  UploadFileTask({
    required super.name,
    required super.size,
    required super.createTime,
  });
}

/// 下载文件任务
class DownloadFileTask extends FileTask {
  /// 已下载大小
  int downloaded = 0;

  DownloadFileTask({
    required super.name,
    required super.size,
    required super.createTime,
  });

  /// 进度[0-1]
  double get progress => downloaded.toDouble() / size;

  String get progressStr => (progress * 100).toStringAsFixed(2);
}
