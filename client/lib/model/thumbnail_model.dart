import 'dart:math';

import 'package:flutter/cupertino.dart';
import 'package:h_nas/api/api.dart';
import 'package:h_nas/utils/file_utils.dart';

class ThumbnailModel with ChangeNotifier {
  final Map<FileInfo, FilePreview> _cache = {};
  bool private;

  ThumbnailModel({required this.private});

  get(
    FileInfo info,
    Function(FilePreview) onSuccess,
    Function(dynamic) onError,
  ) async {
    final v = _cache[info];
    if (v != null) {
      onSuccess(v);
      return;
    }

    await Future.delayed(Duration(milliseconds: Random().nextInt(500) + 500));
    FilePreview? fp;
    do {
      fp = await FileAPI.getFilePreviewInfo(info.fullPath, private: private);
      if (fp.thumbnail == '') {
        await Future.delayed(Duration(milliseconds: 500));
      } else {
        break;
      }
    } while (true);
    _cache[info] = fp;
    onSuccess(fp);
    notifyListeners();
  }

  clear() {
    _cache.clear();
    notifyListeners();
  }
}
