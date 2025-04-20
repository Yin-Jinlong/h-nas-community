part of 'api.dart';

extension FileAPIURL on API {
  static String _withPath(String path, String pathArg, bool private) =>
      "${API.API_ROOT}/file$path?path=${Uri.encodeQueryComponent(pathArg)}&private=$private";

  static String file(
    String path, {
    bool download = false,
    required bool private,
  }) =>
      "${API.API_ROOT}/file?path=${Uri.encodeQueryComponent(path)}&download=$download&private=$private";

  static String fileThumbnail(String path, {required bool private}) =>
      _withPath('/thumbnail', path, private);

  static String filePreview(String path, {required bool private}) =>
      _withPath('/preview', path, private);

  static String audioCover(String path, {required bool private}) =>
      _withPath('/audio/cover', path, private);
}
