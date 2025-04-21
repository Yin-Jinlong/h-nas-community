import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/media_type.dart';

extension FileInfoExt on FileInfo {
  bool get isFolder => fileType == IVirtualFile$Type.folder;

  bool get isFile => fileType == IVirtualFile$Type.file;

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
