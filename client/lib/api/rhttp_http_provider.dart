import 'dart:convert';
import 'dart:typed_data';

import 'package:h_nas/utils/security.dart';
import 'package:rhttp/rhttp.dart' as rhttp;

import 'http_provider.dart';

class RHttpHttpProvider extends HttpProvider {
  static final List<String> _cas = [];

  static void init() async {
    await rhttp.Rhttp.init();
    for (final ca in Security.cas) {
      final text = utf8.decode(ca);
      _cas.add(text);
    }
  }

  final client = rhttp.RhttpClient.createSync(
    settings: rhttp.ClientSettings(
      cookieSettings: rhttp.CookieSettings(),
      tlsSettings: rhttp.TlsSettings(trustedRootCertificates: _cas),
    ),
  );

  @override
  CancelToken cancelToken() {
    return RHttpCancelToken();
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
    headers ??= {};
    if (contentType != null) {
      headers.putIfAbsent('content-type', () => contentType);
    }
    return client
        .request(
          method: rhttp.HttpMethod(method),
          url: url,
          body: data == null ? null : rhttp.HttpBody.bytes(data),
          expectBody: rhttp.HttpExpectBody.values.byName(
            responseType ?? 'text',
          ),
          headers: rhttp.HttpHeaders.rawMap(headers),
          query: query?.map((key, value) => MapEntry(key, value.toString())),
          cancelToken: cancelToken?.token,
          onReceiveProgress: onReceiveProgress,
          onSendProgress: onSendProgress,
        )
        .then(
          (res) => Response(
            statusCode: res.statusCode,
            headers: res.headerMap,
            data: switch (res) {
              rhttp.HttpTextResponse() => res.body,
              rhttp.HttpBytesResponse() => res.body,
              rhttp.HttpStreamResponse() => res.body,
            },
          ),
        );
  }
}

class RHttpCancelToken implements CancelToken<rhttp.CancelToken> {
  @override
  rhttp.CancelToken token = rhttp.CancelToken();

  @override
  Future<void> cancel() async {
    await token.cancel();
  }
}
