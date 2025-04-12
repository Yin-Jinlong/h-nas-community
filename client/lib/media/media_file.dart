import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/file_utils.dart';
import 'package:h_nas/utils/media_type.dart';
import 'package:media_kit/media_kit.dart';

class MediaFile extends Media {
  FileInfo file;
  late final MediaType? type;
  AudioFileInfo? audioInfo;

  MediaFile({required this.file})
    : super(FileAPIURL.publicFile(file.fullPath)) {
    type = file.fileMediaType;
  }

  Future<AudioFileInfo?> loadInfo() async {
    if (audioInfo == null && type?.isAudio == true) {
      if (file.fileMediaType?.isAudio == true) {
        final v = await FileAPI.getPublicAudioInfo(file.fullPath);
        audioInfo = v;
      }
    }
    return audioInfo;
  }
}
