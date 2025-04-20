import 'package:flutter/cupertino.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/file_utils.dart';
import 'package:h_nas/utils/media_type.dart';
import 'package:media_kit/media_kit.dart';

class MediaFile extends Media with ChangeNotifier {
  final FileInfo file;
  late final MediaType? type;
  AudioFileInfo? audioInfo;
  final bool private;

  MediaFile({required this.file, required this.private})
    : super(
        FileAPIURL.file(file.fullPath, private: private),
        httpHeaders: private ? API.tokenHeader() : null,
      ) {
    type = file.fileMediaType;
  }

  Future<AudioFileInfo?> loadInfo() async {
    if (audioInfo == null && type?.isAudio == true) {
      if (file.fileMediaType?.isAudio == true) {
        final v = await FileAPI.getAudioInfo(file.fullPath, private: private);
        audioInfo = v;
        notifyListeners();
      }
    }
    return audioInfo;
  }
}
