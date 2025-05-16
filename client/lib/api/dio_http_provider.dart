import 'dart:typed_data';

import 'package:dio/dio.dart' as dio;
import 'package:h_nas/api/http_provider.dart';

class DioHttpProvider extends HttpProvider {
  final dio.Dio client = dio.Dio();

  @override
  CancelToken cancelToken() {
    return DioCancelToken();
  }

  @override
  Future<Response> request({
    required String method,
    required String url,
    Uint8List? data,
    HttpHeaders? headers,
    QueryParameters? query,
    String? contentType,
    String? responseType,
    CancelToken? cancelToken,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) {
    var rt =
        responseType == 'text' || responseType == null
            ? dio.ResponseType.plain
            : dio.ResponseType.values.byName(responseType);
    return client
        .request(
          url,
          data: data,
          options: dio.Options(
            method: method,
            responseType: rt,
            headers: headers,
            contentType: contentType,
          ),
          queryParameters: query,
          cancelToken: cancelToken?.token,
          onSendProgress: onSendProgress,
          onReceiveProgress: onReceiveProgress,
        )
        .then(
          (res) => Response(
            statusCode: res.statusCode ?? 0,
            headers: res.headers.map.map(
              (key, value) => MapEntry(key, value.join(', ')),
            ),
            data: switch (rt) {
              dio.ResponseType.stream => (res.data as dio.ResponseBody).stream,
              _ => res.data,
            },
          ),
        );
  }
}

class DioCancelToken implements CancelToken<dio.CancelToken> {
  @override
  dio.CancelToken token = dio.CancelToken();

  @override
  Future<void> cancel() async {
    token.cancel();
  }
}
