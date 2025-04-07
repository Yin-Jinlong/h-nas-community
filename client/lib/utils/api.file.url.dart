part of 'api.dart';

extension FileAPIURL on API {
  static String _withPath(String path, String pathArg) =>
      "${API.API_ROOT}/file/public$path?path=${Uri.encodeQueryComponent(pathArg)}";

  static String publicFile(String path, {bool download = false}) =>
      "${API.API_ROOT}/file/public?path=${Uri.encodeQueryComponent(path)}&download=$download";

  static String publicFileThumbnail(String path) =>
      _withPath('/thumbnail', path);

  static String publicFilePreview(String path) => _withPath('/preview', path);

  static String publicAudioCover(String path) =>
      _withPath('/audio/cover', path);
}
