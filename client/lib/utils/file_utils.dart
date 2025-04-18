import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/media_type.dart';

extension FileInfoExt on FileInfo {
  bool get isFolder {
    return fileType == 'FOLDER';
  }

  bool get isFile {
    return fileType == 'FILE';
  }

  String get fullPath => dir == '/' ? '/$name' : '$dir/$name';

  MediaType? get fileMediaType =>
      mediaType == null ? null : MediaType.parse(mediaType!);

  bool get canPlay {
    final type = fileMediaType;
    if (type == null) {
      return false;
    }
    return type.type == MediaType.typeAudio || type.type == MediaType.typeVideo;
  }
}
