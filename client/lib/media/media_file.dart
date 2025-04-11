import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/file_utils.dart';
import 'package:media_kit/media_kit.dart';

class MediaFile extends Media {
  FileInfo file;

  MediaFile({required this.file}) : super(FileAPIURL.publicFile(file.fullPath));
}
