part of 'api.dart';

extension FileAPI on API {
  static Future<List<FileInfo>> getPublicFiles(String path) {
    return API._get<List<dynamic>>('/file/public/files', {'path': path}).then((
      data,
    ) {
      if (data == null) return [];
      List<FileInfo> list = [];

      for (var item in data) {
        list.add(FileInfo.fromJson(item as Map<String, dynamic>));
      }

      return list;
    });
  }

  static Future<FilePreview?> getPublicFilePreviewInfo(String path) {
    return API
        ._get<Map<String, dynamic>>('/file/public/preview/info', {'path': path})
        .then((data) {
          return data == null ? null : FilePreview.fromJson(data);
        });
  }

  static Future<FolderChildrenCount?> getPublicFolderChildrenCount(
    String path,
  ) {
    return API
        ._get<Map<String, dynamic>>('/file/public/folder/count', {'path': path})
        .then((data) {
          return data == null ? null : FolderChildrenCount.fromJson(data);
        });
  }

  static Future<AudioFileInfo?> getPublicAudioInfo(String path) {
    return API
        ._get<Map<String, dynamic>>('/file/public/audio/info', {'path': path})
        .then((data) {
          return data == null ? null : AudioFileInfo.fromJson(data);
        });
  }

  static Future<bool?> newPublicFolder(String path) {
    return API._post<bool>(
      '/file/public/folder',
      {'path': path},
      options: Options(
        contentType: Headers.formUrlEncodedContentType,
        headers: {ExtraHeaders.authorization: Prefs.token},
      ),
    );
  }

  static Future<bool?> renamePublicFolder(String path, String name) {
    return API._post<bool>(
      '/file/public/rename',
      {'path': path, 'name': name},
      options: Options(
        contentType: Headers.formUrlEncodedContentType,
        headers: {ExtraHeaders.authorization: Prefs.token},
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
  }) {
    return API
        ._post(
          '/file/public/upload',
          bytes,
          options: Options(
            headers: {
              ExtraHeaders.authorization: Prefs.token,
              ExtraHeaders.contentID: base64Url.encode(utf8.encode(path)),
              ExtraHeaders.contentRange: '$start-$end/$size',
              ExtraHeaders.hash: hash,
            },
            contentType: ExtraHeaders.contentTypeOctetStream,
          ),
        )
        .then((res) => res ?? false);
  }

  static Future downloadPublic(
    String path,
    String dst,
    ProgressCallback onProgress,
  ) {
    return API._download(
      '/file/public?path=${Uri.encodeQueryComponent(path)}',
      dst,
      onProgress,
    );
  }

  static Future<bool?> deletePublic(String path) {
    return API._delete<bool>(
      '/file/public',
      {'path': path},
      options: Options(
        contentType: Headers.formUrlEncodedContentType,
        headers: {ExtraHeaders.authorization: Prefs.token},
      ),
    );
  }
}
