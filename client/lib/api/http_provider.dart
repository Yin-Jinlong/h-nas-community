import 'dart:typed_data';

typedef QueryParameters = Map<String, Object>;
typedef HttpHeaders = Map<String, String>;
typedef ProgressCallback = void Function(int count, int total);

abstract class HttpProvider {
  static const get = 'GET';
  static const post = 'POST';
  static const patch = 'PATCH';
  static const delete = 'DELETE';

  static const text = 'text';
  static const bytes = 'bytes';
  static const stream = 'stream';

  CancelToken cancelToken();

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
  });
}

mixin CancelToken<T> {
  late final T token;

  Future<void> cancel() async {}
}

class Response {
  final int statusCode;
  final HttpHeaders headers;
  final dynamic data;

  Response({
    required this.statusCode,
    required this.headers,
    required this.data,
  });

  String get text => data;

  Uint8List get bytes => data;

  Stream<Uint8List> get stream => data;
}
