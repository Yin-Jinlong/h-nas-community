import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';

import 'package:cookie_jar/cookie_jar.dart';
import 'package:dio/dio.dart';
import 'package:dio_cookie_manager/dio_cookie_manager.dart';
import 'package:h_nas/main.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/routes.dart';
import 'package:h_nas/settings/user.dart';
import 'package:h_nas/utils/headers.dart';
import 'package:h_nas/utils/toast.dart';

import '../generated/l10n.dart';
import 'api_response.dart';

part 'api.ai.dart';
part 'api.ai.url.dart';
part 'api.file.dart';
part 'api.file.url.dart';
part 'api.user.dart';
part 'type.g.dart';

typedef OnResp = Function(Response);
typedef QueryParameters = Map<String, dynamic>;

abstract class API {
  static String API_ROOT = '';
  static Dio dio = Dio()..interceptors.add(CookieManager(CookieJar()));

  static Map<String, String> tokenHeader() => {
    if (Prefs.token != null) ExtraHeaders.authorization: Prefs.token!,
  };

  static String _encodeQueryParms(QueryParameters? parms) {
    return parms?.entries
            .map(
              (e) => '${e.key}=${Uri.encodeQueryComponent(e.value.toString())}',
            )
            .join('&') ??
        '';
  }

  static Future<T?> _get<T>(
    String path,
    QueryParameters? queryParameters, {
    Options? options,
    OnResp? onResp,
    Future<T?> Function(Response)? then,
  }) {
    return dio
        .get(
          '$API_ROOT$path',
          queryParameters: queryParameters,
          options: options,
        )
        .then(then ?? (res) async => await _then(res, onResp) as T?)
        .catchError(_catchError);
  }

  static Future<T?> _post<T>(
    String path,
    Object? data, {
    QueryParameters? parms,
    Options? options,
    CancelToken? cancelToken,
    OnResp? onResp,
  }) {
    return dio
        .post(
          '$API_ROOT$path',
          data: data,
          options: options,
          queryParameters: parms,
          cancelToken: cancelToken,
        )
        .then((res) async => await _then(res, onResp) as T?)
        .catchError(_catchError);
  }

  static Future<T?> _patch<T>(
    String path,
    Object? data, {
    QueryParameters? parms,
    Options? options,
    OnResp? onResp,
  }) {
    return dio
        .patch(
          '$API_ROOT$path',
          data: data,
          options: options,
          queryParameters: parms,
        )
        .then((res) async => await _then(res, onResp) as T?)
        .catchError(_catchError);
  }

  static Future _download(
    String path,
    String dst,
    ProgressCallback onProgress, {
    QueryParameters? parms,
    CancelToken? cancelToken,
  }) {
    return dio
        .download(
          '$API_ROOT$path',
          dst,
          onReceiveProgress: onProgress,
          queryParameters: parms,
          cancelToken: cancelToken,
          options: Options(responseType: ResponseType.stream),
        )
        .then((res) => res.data)
        .catchError(_catchError);
  }

  static Future<T?> _delete<T>(
    String path,
    Object? data, {
    Options? options,
    OnResp? onResp,
    QueryParameters? parms,
  }) {
    return dio
        .delete(
          '$API_ROOT$path',
          data: data,
          options: options,
          queryParameters: parms,
        )
        .then((res) async => await _then(res, onResp) as T?)
        .catchError(_catchError);
  }
}

Future _then(Response res, OnResp? onResp) async {
  final data = APIResponse.fromJson(res.data);

  if (data.code != 0) {
    Toast.showError(data.msg);
    return null;
  }

  onResp?.call(res);
  return (data.data ?? true);
}

Future<Never> _catchError(error) async {
  switch (error) {
    case DioException e when e.type == DioExceptionType.badResponse:
      final data = e.response?.data;
      if (data != null) {
        try {
          final resp = APIResponse.fromJson(data);
          if (resp.code == 100) {
            Future.delayed(const Duration(milliseconds: 600), () {
              UserS.user = null;
              Prefs.token = null;
              Toast.showError(S.current.please_login);
              navigatorKey.currentState?.popAndPushNamed(Routes.loginOn);
            });
          }
          Toast.showError(
            resp.msg + (resp.data != null ? ': ${resp.data}' : ''),
          );
        } catch (e) {
          Toast.showError(data.toString());
        }
        break;
      }
    default:
      Toast.showError(error.toString());
  }
  throw error;
}
