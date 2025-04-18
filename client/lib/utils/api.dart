import 'dart:convert';
import 'dart:typed_data';

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
    Options? options,
    OnResp? onResp,
  }) {
    return dio
        .get<String>(
          '$API_ROOT$path',
          queryParameters: queryParameters,
          options: options,
        )
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

  static Future<Response?> _download(
    String path,
    String dst,
    ProgressCallback onProgress,
  ) {
    return dio
        .download(
          '$API_ROOT$path',
          dst,
          onReceiveProgress: onProgress,
          options: Options(responseType: ResponseType.stream),
        )
        .catchError(_catchError);
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
        if (Prefs.token != null && resp.code == 100) {
          Future.delayed(Duration(seconds: 1), () {
            Toast.showError(S.current.please_login);
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

extension _JSonUtil on Map<String, dynamic>? {
  T? _to<T>(T Function(Map<String, dynamic> json) fn) {
    if (this == null) {
      return null;
    } else {
      return fn(this!);
    }
  }
}
