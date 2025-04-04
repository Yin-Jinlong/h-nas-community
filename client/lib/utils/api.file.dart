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

  static Future<bool?> newFolder(String path) {
    return API._post<bool>(
      '/file/public/folder',
      {'path': path},
      options: Options(
        contentType: Headers.formUrlEncodedContentType,
        headers: {ExtraHeaders.authorization: Prefs.token},
      ),
    );
  }

  static Future downloadPublic(
    String path,
    String dst,
    ProgressCallback onProgress,
  ) {
    return API._download('/file/public?path=${Uri.encodeQueryComponent(path)}', dst, onProgress);
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
