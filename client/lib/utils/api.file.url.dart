part of 'api.dart';

extension FileAPIURL on API {
  static String publicFile(String path, {bool download = false}) =>
      "${API.API_ROOT}/file/public?path=${Uri.encodeQueryComponent(path)}&download=$download";

  static String publicFileThumbnail(String thumbnail) =>
      "${API.API_ROOT}/file/public/thumbnail?path=${Uri.encodeQueryComponent(thumbnail)}";

  static String publicFilePreview(String thumbnail) =>
      "${API.API_ROOT}/file/public/preview?path=${Uri.encodeQueryComponent(thumbnail)}";
}
