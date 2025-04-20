part of 'api.dart';

extension FileAPI on API {
  static Future<List<FileInfo>> getFiles(String path, {required bool private}) {
    return API
        ._get<List<dynamic>>('/file/files', {
          'path': path,
          'private': private,
        }, options: Options(headers: {...API.tokenHeader()}))
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
        ._get<Map<String, dynamic>>('/file/info', {
          'path': path,
          'private': private,
        }, options: Options(headers: {...API.tokenHeader()}))
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
        ._get<Map<String, dynamic>>('/file/preview/info', {
          'path': path,
          'private': private,
        }, options: Options(headers: {...API.tokenHeader()}))
        .then((data) {
          return data == null ? null : FilePreview.fromJson(data);
        });
  }

  static Future<FolderChildrenCount?> getFolderChildrenCount(
    String path, {
    required bool private,
  }) {
    return API
        ._get<Map<String, dynamic>>('/file/folder/count', {
          'path': path,
          'private': private,
        }, options: Options(headers: {...API.tokenHeader()}))
        .then((data) {
          return data == null ? null : FolderChildrenCount.fromJson(data);
        });
  }

  static Future<AudioFileInfo?> getAudioInfo(
    String path, {
    required bool private,
  }) {
    return API
        ._get<Map<String, dynamic>>('/file/audio/info', {
          'path': path,
          'private': private,
        }, options: Options(headers: {...API.tokenHeader()}))
        .then((data) {
          return data == null ? null : AudioFileInfo.fromJson(data);
        });
  }

  static Future<bool?> newFolder(String path, {required bool private}) {
    return API._post<bool>('/file/folder', {
      'path': path,
      'private': private,
    }, options: Options(headers: {...API.tokenHeader()}));
  }

  static Future<bool?> rename(
    String path,
    String name, {
    required bool private,
  }) {
    return API._post<bool>('/file/rename', {
      'path': path,
      'name': name,
    }, options: Options(headers: {...API.tokenHeader()}));
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
          '/file/upload',
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
    return API._download(
      '/file',
      dst,
      onProgress,
      parms: {'path': path, 'private': private},
    );
  }

  static Future<bool?> delete(String path, {required bool private}) {
    return API._delete<bool>(
      '/file',
      {'path': path},
      options: Options(
        contentType: Headers.formUrlEncodedContentType,
        headers: {ExtraHeaders.authorization: Prefs.token},
      ),
    );
  }
}
