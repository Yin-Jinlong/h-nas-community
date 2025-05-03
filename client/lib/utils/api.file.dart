part of 'api.dart';

abstract class FileAPI extends API {
  static final String root = '/file';

  static QueryParameters _base(String path, bool private) => {
    'private': private,
    'path': path,
  };

  static Future<int> getUserStorageUsage({int? uid}) {
    return API
        ._get<int>(
          '$root/storage/user/usage',
          {if (uid != null) 'uid': uid},
          options: Options(headers: {...API.tokenHeader()}),
        )
        .then((data) {
          return data ?? 0;
        });
  }

  static Future<List<FileInfo>> getFiles(String path, {required bool private}) {
    return API
        ._get<List<dynamic>>(
          '$root/files',
          _base(path, private),
          options: Options(headers: {...API.tokenHeader()}),
        )
        .then((data) {
          if (data == null) return [];
          return data.map((e) => FileInfo.fromJson(e as JsonObject)).toList();
        });
  }

  static Future<List<FileInfo>> searchFiles(
    String name, {
    String? lastPath,
    required bool private,
  }) {
    return API
        ._get<List<dynamic>>('$root/search', {
          'private': private,
          'name': name,
          if (lastPath != null) 'lastPath': lastPath,
        }, options: Options(headers: {...API.tokenHeader()}))
        .then((data) {
          if (data == null) return [];
          return data.map((e) => FileInfo.fromJson(e as JsonObject)).toList();
        });
  }

  static Future<FileInfo?> getFile(String path, {required bool private}) {
    return API
        ._get<JsonObject>(
          '$root/info',
          _base(path, private),
          options: Options(headers: {...API.tokenHeader()}),
        )
        .then((data) {
          if (data == null) return null;

          return FileInfo.fromJson(data);
        });
  }

  static Future<Stream<Uint8List>?> getFileData(
    String path, {
    required bool private,
    required ResponseType contentType,
  }) {
    return API._get<Stream<Uint8List>>(
      root,
      _base(path, private),
      options: Options(
        headers: {...API.tokenHeader()},
        responseType: contentType,
      ),
      then: (res) async {
        return (res.data as ResponseBody).stream;
      },
    );
  }

  static Future<FilePreview?> getFilePreviewInfo(
    String path, {
    required bool private,
  }) {
    return API
        ._get<JsonObject>(
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
        ._get<JsonObject>(
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
        ._get<JsonObject>(
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
    CancelToken? cancelToken,
  }) {
    return API
        ._post(
          '$root/upload',
          bytes,
          parms: {'private': private},
          cancelToken: cancelToken,
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
    CancelToken? cancelToken,
  }) {
    return API._download(
      root,
      dst,
      onProgress,
      cancelToken: cancelToken,
      parms: {..._base(path, private), 'download': true},
    );
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

  static Future<List<HLSStreamList>> getVideoStreams(
    String path, {
    required bool private,
  }) {
    return API
        ._get<List<dynamic>>(
          '$root/video/streams',
          _base(path, private),
          options: Options(headers: {...API.tokenHeader()}),
        )
        .then((res) {
          if (res == null) return [];
          return res
              .map((e) => HLSStreamList.fromJson(e as Map<String, dynamic>))
              .toList();
        });
  }

  static Future<HLSStreamInfo?> getVideoStreamInfo(
    String path, {
    required String codec,
    required int bitrate,
    required bool private,
  }) {
    return API
        ._get<JsonObject>(
          '$root/video/stream/info',
          {..._base(path, private), 'codec': codec, 'bitrate': bitrate},
          options: Options(headers: {...API.tokenHeader()}),
        )
        .then((res) {
          return res == null ? null : HLSStreamInfo.fromJson(res);
        });
  }

  static Future setAvatar(File file) async {
    final bytes = await file.readAsBytes();
    return API._post(
      '$root/user/avatar',
      bytes,
      options: Options(
        headers: API.tokenHeader(),
        contentType: ExtraHeaders.contentTypeOctetStream,
      ),
    );
  }
}
