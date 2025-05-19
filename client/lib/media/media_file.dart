import 'package:flutter/foundation.dart';
import 'package:h_nas/api/api.dart';
import 'package:h_nas/utils/file_utils.dart';
import 'package:h_nas/utils/media_type.dart';
import 'package:media_kit/media_kit.dart';

class MediaFile extends Media with ChangeNotifier {
  final FileInfo file;
  late final MediaType? type;
  AudioFileInfo? audioInfo;
  final bool private;

  MediaFile({required this.file, required this.private, String? url})
    : super(
        url ?? FileAPIURL.file(file.fullPath, private: private),
        httpHeaders: private ? API.tokenHeader() : null,
      ) {
    type = file.fileMediaType;
    audioInfo = file.audioFileInfo;
    if (kDebugMode) {
      print(url);
    }
  }
}
