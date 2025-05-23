import 'package:h_nas/api/api.dart';
import 'package:h_nas/utils/file_utils.dart';

import 'media_file.dart';

class VideoMediaFile extends MediaFile {
  final String codec;
  final int bitrate;

  VideoMediaFile({
    required super.file,
    required super.private,
    required this.codec,
    required this.bitrate,
  }) : super(
         url: FileAPIURL.videoStreamM3u8(
           file.fullPath,
           private: private,
           codec: codec,
           bitrate: bitrate,
         ),
       );
}
