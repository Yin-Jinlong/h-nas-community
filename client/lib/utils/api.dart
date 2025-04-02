import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/utils/headers.dart';
import 'package:h_nas/utils/toast.dart';

import 'api_response.dart';

part 'type.g.dart';

typedef OnResp = Function(Response);

abstract class API {
  static String API_ROOT = '';
  static Dio dio = Dio();

  static Future<T?> _get<T>(
    String path,
    Map<String, dynamic>? queryParameters, {
    OnResp? onResp,
  }) {
    return dio
        .get<String>('$API_ROOT$path', queryParameters: queryParameters)
        .then((res) => _then<T>(res, onResp))
        .catchError(_catchError<T>);
  }

  static Future<T?> _post<T>(
    String path,
    Object? data, {
    Options? options,
    OnResp? onResp,
  }) {
    return dio
        .post<String>('$API_ROOT$path', data: data, options: options)
        .then((res) => _then<T>(res, onResp))
        .catchError(_catchError<T>);
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

  static Future<UserInfo?> login(String logid, String password) {
    return _post<Map<String, dynamic>>(
      '/user/login',
      {'logId': logid, 'password': password},
      options: Options(
        contentType: Headers.formUrlEncodedContentType,
        responseType: ResponseType.json,
      ),
      onResp: (res) {
        final token = res.headers.value(ExtraHeaders.authorization);
        if (token != null) {
          Prefs.setString(Prefs.keyAuthToken, token);
        }
      },
    ).then((data) {
      return data == null ? null : UserInfo.fromJson(data);
    });
  }

  static String publicFileURL(String path, {bool download = false}) =>
      "$API_ROOT/file/public?path=${Uri.encodeQueryComponent(path)}&download=$download";

  static String publicFileThumbnailURL(String thumbnail) =>
      "$API_ROOT/file/public/thumbnail?path=${Uri.encodeQueryComponent(thumbnail)}";

  static String publicFilePreviewURL(String thumbnail) =>
      "$API_ROOT/file/public/preview?path=${Uri.encodeQueryComponent(thumbnail)}";
}

Future<T?> _then<T>(Response res, OnResp? onResp) async {
  final data = APIResponse.fromJson(jsonDecode(res.data ?? '{}'));

  if (data.code != 0) {
    Toast.showError(data.msg);
    return null;
  }

  onResp?.call(res);
  return data.data as T;
}

Future<T?> _catchError<T>(error) async {
  switch (error) {
    case DioException e when e.type == DioExceptionType.badResponse:
      final data = e.response?.data;
      if (data != null) {
        final resp = APIResponse.fromJson(jsonDecode(data!));
        Toast.showError(resp.data ?? resp.msg);
        break;
      }
    default:
      Toast.showError(error.toString());
  }
  return null;
}
