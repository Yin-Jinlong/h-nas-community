import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/file_utils.dart';
import 'package:media_kit/media_kit.dart';

class MediaFile extends Media {
  FileInfo file;
  AudioFileInfo? audioInfo;

  MediaFile({required this.file}) : super(FileAPIURL.publicFile(file.fullPath));

  Future<AudioFileInfo?> loadInfo() async {
    if (audioInfo == null) {
      if (file.fileMediaType?.isAudio == true) {
        final v = await FileAPI.getPublicAudioInfo(file.fullPath);
        audioInfo = v;
      }
    }
    return audioInfo;
  }
}
