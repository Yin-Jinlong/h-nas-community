import 'package:h_nas/utils/api.dart';

extension FileTypeCheck on FileInfo {
  bool get isFolder {
    return fileType == 'FOLDER';
  }

  bool get isFile {
    return fileType == 'FILE';
  }

  String get fullPath {
    return '$dir/$name';
  }
}
