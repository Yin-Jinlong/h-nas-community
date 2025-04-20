part of 'api.dart';

abstract class FileAPI extends API {
  static final String root = '/file';

  static QueryParameters _base(String path, bool private) => {
    'private': private,
    'path': path,
  };

  static Future<List<FileInfo>> getFiles(String path, {required bool private}) {
    return API
        ._get<List<dynamic>>(
          '$root/files',
          _base(path, private),
          options: Options(headers: {...API.tokenHeader()}),
        )
        .then((data) {
          if (data == null) return [];
          List<FileInfo> list = [];

          for (var item in data) {
            list.add(FileInfo.fromJson(item as Map<String, dynamic>));
          }

          return list;
        });
  }

  static Future<FileInfo?> getFile(String path, {required bool private}) {
    return API
        ._get<Map<String, dynamic>>(
          '$root/info',
          _base(path, private),
          options: Options(headers: {...API.tokenHeader()}),
        )
        .then((data) {
          if (data == null) return null;

          return FileInfo.fromJson(data);
        });
  }

  static Future<FilePreview?> getFilePreviewInfo(
    String path, {
    required bool private,
  }) {
    return API
        ._get<Map<String, dynamic>>(
          '$root/preview/info',
          _base(path, private),
          options: Options(headers: {...API.tokenHeader()}),
        )
        .then((data) {
          return data == null ? null : FilePreview.fromJson(data);
        });
  }

  static Future<FolderChildrenCount?> getFolderChildrenCount(
    String path, {
    required bool private,
  }) {
    return API
        ._get<Map<String, dynamic>>(
          '$root/folder/count',
          _base(path, private),
          options: Options(headers: {...API.tokenHeader()}),
        )
        .then((data) {
          return data == null ? null : FolderChildrenCount.fromJson(data);
        });
  }

  static Future<AudioFileInfo?> getAudioInfo(
    String path, {
    required bool private,
  }) {
    return API
        ._get<Map<String, dynamic>>(
          '$root/audio/info',
          _base(path, private),
          options: Options(headers: {...API.tokenHeader()}),
        )
        .then((data) {
          return data == null ? null : AudioFileInfo.fromJson(data);
        });
  }

  static Future<bool?> newFolder(String path, {required bool private}) {
    return API._post<bool>(
      '$root/folder',
      _base(path, private),
      options: Options(
        headers: {...API.tokenHeader()},
        contentType: Headers.formUrlEncodedContentType,
      ),
    );
  }

  static Future<bool?> rename(
    String path,
    String name, {
    required bool private,
  }) {
    return API._post<bool>(
      '$root/rename',
      {..._base(path, private), 'name': name},
      options: Options(
        headers: {...API.tokenHeader()},
        contentType: Headers.formUrlEncodedContentType,
      ),
    );
  }

  static Future<bool> upload(
    String path,
    Uint8List? bytes, {
    required int start,
    required int end,
    required int size,
    required String hash,
    required bool private,
  }) {
    return API
        ._post(
          '$root/upload',
          bytes,
          parms: {'private': private},
          options: Options(
            headers: {
              ...API.tokenHeader(),
              ExtraHeaders.contentID: base64Url.encode(utf8.encode(path)),
              ExtraHeaders.contentRange: '$start-$end/$size',
              ExtraHeaders.hash: hash,
            },
            contentType: ExtraHeaders.contentTypeOctetStream,
          ),
        )
        .then((res) => res ?? false);
  }

  static Future download(
    String path,
    String dst,
    ProgressCallback onProgress, {
    required bool private,
  }) {
    return API._download(root, dst, onProgress, parms: _base(path, private));
  }

  static Future<bool?> delete(String path, {required bool private}) {
    return API._delete<bool>(
      root,
      _base(path, private),
      options: Options(
        contentType: Headers.formUrlEncodedContentType,
        headers: {ExtraHeaders.authorization: Prefs.token},
      ),
    );
  }
}
