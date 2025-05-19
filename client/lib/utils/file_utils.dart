import 'package:h_nas/type.g.dart';
import 'package:h_nas/utils/media_type.dart';

extension FileInfoExt on FileInfo {
  bool get isFolder => fileType == IVirtualFile$Type.folder;

  bool get isFile => fileType == IVirtualFile$Type.file;

  String get fullPath => dir == '/' ? '/$name' : '$dir/$name';

  MediaType? get fileMediaType =>
      mediaType == null ? null : MediaType.parse(mediaType!);

  AudioFileInfo? get audioFileInfo {
    if (fileMediaType?.isAudio == true && extra is Map) {
      return AudioFileInfo.fromJson(extra);
    }
    return null;
  }

  bool get canPlay {
    final type = fileMediaType;
    if (type == null) {
      return false;
    }
    return type.type == MediaType.typeAudio || type.type == MediaType.typeVideo;
  }
}
