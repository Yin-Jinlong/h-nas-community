part of 'api.dart';

abstract class FileAPIURL extends FileAPI {
  static String get root => '${API.API_ROOT}/file';

  static String _withPath(String path, String pathArg, bool private) =>
      "$root$path?${API._encodeQueryParms(FileAPI._base(pathArg, private))}";

  static String file(
    String path, {
    bool download = false,
    required bool private,
  }) =>
      "$root?${API._encodeQueryParms({...FileAPI._base(path, private), 'download': download})}";

  static String fileThumbnail(String path, {required bool private}) =>
      _withPath('/thumbnail', path, private);

  static String filePreview(String path, {required bool private}) =>
      _withPath('/preview', path, private);

  static String audioCover(String path, {required bool private}) =>
      _withPath('/audio/cover', path, private);

  static String videoStringM3u8(
    String path, {
    required bool private,
    required String codec,
    required int bitrate,
  }) =>
      '$root/video/stream/${Uri.encodeComponent(path)}/$codec/$bitrate/index.m3u8?private=$private';
}
