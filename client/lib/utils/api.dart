import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:h_nas/utils/toast.dart';

import 'api_response.dart';

part 'type.g.dart';

class API {
  static const String API_ROOT = 'http://127.0.0.1:8888/api';
  static Dio dio = Dio();

  static Future<T?> _get<T>(
    String path,
    Map<String, dynamic>? queryParameters,
  ) {
    return dio
        .get<String>('$API_ROOT$path', queryParameters: queryParameters)
        .then((res) {
          final data = APIResponse.fromJson(jsonDecode(res.data ?? '{}'));

          if (data.code != 0) {
            Toast.showError(data.msg);
            return null;
          }

          return data.data as T;
        })
        .catchError((error) async {
          Toast.showError(error.toString());
          return null;
        });
  }

  static Future<List<FileInfo>> getPublicFiles(String path) {
    return _get<List<dynamic>>('/file/public/files', {'path': path}).then((
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
    return _get<Map<String, dynamic>>('/file/public/preview/info', {
      'path': path,
    }).then((data) {
      return data == null ? null : FilePreview.fromJson(data);
    });
  }

  static String publicFileThumbnailURL(String thumbnail) =>
      "$API_ROOT/file/public/thumbnail?path=${Uri.encodeQueryComponent(thumbnail)}";
}
