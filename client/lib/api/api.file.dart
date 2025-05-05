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
          query: {if (uid != null) 'uid': uid},
          headers: {...API.tokenHeader()},
        )
        .then((data) {
          return data;
        });
  }

  static Future<List<FileInfo>> getFiles(String path, {required bool private}) {
    return API
        ._get<List<dynamic>>(
          '$root/files',
          query: _base(path, private),
          headers: {...API.tokenHeader()},
        )
        .then((data) {
          return data.map((e) => FileInfo.fromJson(e)).toList();
        });
  }

  static Future<List<FileInfo>> searchFiles(
    String name, {
    String? lastPath,
    required bool private,
  }) {
    return API
        ._get<List<dynamic>>(
          '$root/search',
          query: {
            'private': private,
            'name': name,
            if (lastPath != null) 'lastPath': lastPath,
          },
          headers: {...API.tokenHeader()},
        )
        .then((data) {
          return data.map((e) => FileInfo.fromJson(e)).toList();
        });
  }

  static Future<FileInfo> getFile(String path, {required bool private}) {
    return API
        ._get<JsonObject>(
          '$root/info',
          query: _base(path, private),
          headers: {...API.tokenHeader()},
        )
        .then((data) {
          return FileInfo.fromJson(data);
        });
  }

  static Future<Stream<Uint8List>> getFileData(
    String path, {
    required bool private,
  }) {
    return API._request<Stream<Uint8List>>(
      method: HttpProvider.get,
      path: root,
      query: _base(path, private),
      headers: {...API.tokenHeader()},
      responseType: HttpProvider.stream,
    );
  }

  static Future<FilePreview> getFilePreviewInfo(
    String path, {
    required bool private,
  }) {
    return API
        ._get<JsonObject>(
          '$root/preview/info',
          query: _base(path, private),
          headers: {...API.tokenHeader()},
        )
        .then((data) {
          return FilePreview.fromJson(data);
        });
  }

  static Future<FolderChildrenCount> getFolderChildrenCount(
    String path, {
    required bool private,
  }) {
    return API
        ._get<JsonObject>(
          '$root/folder/count',
          query: _base(path, private),
          headers: {...API.tokenHeader()},
        )
        .then((data) {
          return FolderChildrenCount.fromJson(data);
        });
  }

  static Future<AudioFileInfo> getAudioInfo(
    String path, {
    required bool private,
  }) {
    return API
        ._get<JsonObject>(
          '$root/audio/info',
          query: _base(path, private),
          headers: {...API.tokenHeader()},
        )
        .then((data) {
          return AudioFileInfo.fromJson(data);
        });
  }

  static Future<bool> newFolder(String path, {required bool private}) {
    return API
        ._post<bool>(
          '$root/folder',
          query: _base(path, private),
          headers: {...API.tokenHeader()},
          contentType: ExtraHeaders.contentTypeFormUrlEncoded,
        )
        .then(API._boolThen);
  }

  static Future<bool> rename(
    String path,
    String name, {
    required bool private,
  }) {
    return API
        ._post<bool>(
          '$root/rename',
          query: {..._base(path, private), 'name': name},
          headers: {...API.tokenHeader()},
          contentType: ExtraHeaders.contentTypeFormUrlEncoded,
        )
        .then(API._boolThen);
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
        ._post<bool>(
          '$root/upload',
          data: bytes,
          query: {'private': private},
          cancelToken: cancelToken,
          headers: {
            ...API.tokenHeader(),
            ExtraHeaders.contentID: base64Url.encode(utf8.encode(path)),
            ExtraHeaders.contentRange: '$start-$end/$size',
            ExtraHeaders.hash: hash,
            ExtraHeaders.contentType: ExtraHeaders.contentTypeOctetStream,
          },
        )
        .then(API._boolThen);
  }

  static Future<Stream<Uint8List>> download(
    String path,
    ProgressCallback onProgress, {
    required bool private,
    CancelToken? cancelToken,
  }) {
    return API._get(
      root,
      cancelToken: cancelToken,
      query: {..._base(path, private), 'download': true},
      onReceiveProgress: onProgress,
      responseType: HttpProvider.stream,
    );
  }

  static Future<bool> delete(String path, {required bool private}) {
    return API
        ._delete<bool>(
          root,
          query: _base(path, private),
          contentType: ExtraHeaders.contentTypeFormUrlEncoded,
          headers: API.tokenHeader(),
        )
        .then(API._boolThen);
  }

  static Future<List<HLSStreamList>> getVideoStreams(
    String path, {
    required bool private,
  }) {
    return API
        ._get<List>(
          '$root/video/streams',
          query: _base(path, private),
          headers: {...API.tokenHeader()},
        )
        .then((res) {
          return res.map((e) => HLSStreamList.fromJson(e)).toList();
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
          query: {..._base(path, private), 'codec': codec, 'bitrate': bitrate},
          headers: {...API.tokenHeader()},
        )
        .then((res) {
          return HLSStreamInfo.fromJson(res);
        });
  }

  static Future<bool> setAvatar(File file) async {
    final bytes = await file.readAsBytes();
    return API
        ._post(
          '$root/user/avatar',
          data: bytes,
          headers: API.tokenHeader(),
          contentType: ExtraHeaders.contentTypeOctetStream,
        )
        .then(API._boolThen);
  }

  static Future<bool> deleteAvatar() async {
    return API
        ._delete('$root/user/avatar', headers: API.tokenHeader())
        .then(API._boolThen);
  }
}
