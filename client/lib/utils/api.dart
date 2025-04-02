import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/utils/headers.dart';
import 'package:h_nas/utils/toast.dart';

import '../generated/l10n.dart';
import 'api_response.dart';

part 'api.file.dart';
part 'api.file.url.dart';
part 'api.user.dart';
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

  static Future<T?> _delete<T>(
    String path,
    Object? data, {
    Options? options,
    OnResp? onResp,
  }) {
    return dio
        .delete<String>('$API_ROOT$path', data: data, options: options)
        .then((res) => _then<T>(res, onResp))
        .catchError(_catchError<T>);
  }
}

Future<T?> _then<T>(Response res, OnResp? onResp) async {
  final data = APIResponse.fromJson(jsonDecode(res.data ?? '{}'));

  if (data.code != 0) {
    Toast.showError(data.msg);
    return null;
  }

  onResp?.call(res);
  return (data.data ?? true) as T;
}

Future<T?> _catchError<T>(error) async {
  switch (error) {
    case DioException e when e.type == DioExceptionType.badResponse:
      final data = e.response?.data;
      if (data != null) {
        final resp = APIResponse.fromJson(jsonDecode(data!));
        if (Prefs.authToken != null && resp.code == 100) {
          UserAPI.auth();
          Future.delayed(Duration(seconds: 1), () {
            Toast.show(S.current.please_retry);
          });
        }
        Toast.showError(resp.msg + (resp.data != null ? ': ${resp.data}' : ''));
        break;
      }
    default:
      Toast.showError(error.toString());
  }
  return null;
}
