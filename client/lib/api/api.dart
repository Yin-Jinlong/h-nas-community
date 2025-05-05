import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';

import 'package:dio/dio.dart' as dio;
import 'package:h_nas/api/dio_http_provider.dart';
import 'package:h_nas/api/http_provider.dart';
import 'package:h_nas/api/rhttp_http_provider.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/main.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/routes.dart';
import 'package:h_nas/settings/user.dart';
import 'package:h_nas/type.g.dart';
import 'package:h_nas/utils/api_response.dart';
import 'package:h_nas/utils/headers.dart';
import 'package:h_nas/utils/toast.dart';
import 'package:rhttp/rhttp.dart' as rhttp;
import 'package:universal_platform/universal_platform.dart';

export 'package:h_nas/type.g.dart';

part 'api.ai.dart';
part 'api.ai.url.dart';
part 'api.file.dart';
part 'api.file.url.dart';
part 'api.user.dart';

typedef OnResp = Function(Response);

abstract class API {
  static String API_ROOT = '';
  static HttpProvider http =
      UniversalPlatform.isWeb ? DioHttpProvider() : RHttpHttpProvider();

  static HttpHeaders tokenHeader() => {
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

  static CancelToken cancelToken() => http.cancelToken();

  static Future<T> _request<T>({
    required String method,
    required String path,
    Uint8List? data,
    HttpHeaders? headers,
    QueryParameters? query,
    String? contentType,
    String? responseType,
    CancelToken? cancelToken,
    OnResp? onResp,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) {
    return http
        .request(
          method: method,
          url: '$API_ROOT$path',
          data: data,
          headers: headers,
          query: query,
          contentType: contentType,
          responseType: responseType,
          cancelToken: cancelToken,
          onSendProgress: onSendProgress,
          onReceiveProgress: onReceiveProgress,
        )
        .then(
          (res) async =>
              responseType == null || responseType == HttpProvider.text
                  ? await _then(res, onResp) as T
                  : res.data as T,
        )
        .catchError(_catchError);
  }

  static Future<T> _get<T>(
    String path, {
    Uint8List? data,
    HttpHeaders? headers,
    QueryParameters? query,
    String? contentType,
    String? responseType,
    CancelToken? cancelToken,
    OnResp? onResp,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) {
    return _request(
      method: HttpProvider.get,
      path: path,
      data: data,
      headers: headers,
      query: query,
      contentType: contentType,
      responseType: responseType,
      cancelToken: cancelToken,
      onResp: onResp,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );
  }

  static Future<T> _post<T>(
    String path, {
    Uint8List? data,
    HttpHeaders? headers,
    QueryParameters? query,
    String? contentType,
    String? responseType,
    CancelToken? cancelToken,
    OnResp? onResp,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) {
    return _request(
      method: HttpProvider.post,
      path: path,
      data: data,
      headers: headers,
      query: query,
      contentType: contentType,
      responseType: responseType,
      cancelToken: cancelToken,
      onResp: onResp,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );
  }

  static Future<T> _patch<T>(
    String path, {
    Uint8List? data,
    HttpHeaders? headers,
    QueryParameters? query,
    String? contentType,
    String? responseType,
    CancelToken? cancelToken,
    OnResp? onResp,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) {
    return _request(
      method: HttpProvider.patch,
      path: path,
      data: data,
      headers: headers,
      query: query,
      contentType: contentType,
      responseType: responseType,
      cancelToken: cancelToken,
      onResp: onResp,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );
  }

  static Future<T> _delete<T>(
    String path, {
    Uint8List? data,
    HttpHeaders? headers,
    QueryParameters? query,
    String? contentType,
    String? responseType,
    CancelToken? cancelToken,
    OnResp? onResp,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) {
    return _request(
      method: HttpProvider.delete,
      path: path,
      data: data,
      headers: headers,
      query: query,
      contentType: contentType,
      responseType: responseType,
      cancelToken: cancelToken,
      onResp: onResp,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );
  }

  static Future<bool> _boolThen(dynamic value) async {
    return value != false;
  }
}

Future _then(Response res, OnResp? onResp) async {
  final data = APIResponse.fromJson(jsonDecode(res.text));

  if (data.code != 0) {
    throw data.msg;
  }

  onResp?.call(res);
  return (data.data ?? true);
}

void _parseData(String data) {
  try {
    final resp = APIResponse.fromJson(jsonDecode(data));
    if (resp.code == 100) {
      Future.delayed(const Duration(milliseconds: 600), () {
        UserS.user = null;
        Prefs.token = null;
        Toast.showError(S.current.please_login);
        if (navigatorKey.currentState?.canPop() ?? false) {
          navigatorKey.currentState?.pop();
        }
        navigatorKey.currentState?.pushNamed(Routes.loginOn);
      });
    }
    Toast.showError(resp.msg + (resp.data != null ? ': ${resp.data}' : ''));
  } catch (e) {
    Toast.showError(data.toString());
  }
}

Future<Never> _catchError(error) async {
  switch (error) {
    case dio.DioException e when e.type == dio.DioExceptionType.badResponse:
      final data = e.response?.data;
      if (data != null) {
        _parseData(data);
      }
    case rhttp.RhttpStatusCodeException e:
      final data = e.body;
      if (data != null) {
        _parseData(data.toString());
      }
      break;
    default:
      Toast.showError(error.toString());
  }
  throw error;
}
