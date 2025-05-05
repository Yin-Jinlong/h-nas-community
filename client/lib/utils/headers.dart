import 'package:dio/dio.dart';

extension ExtraHeaders on Headers {
  static const authorization = 'authorization';
  static const contentID = 'content-id';
  static const contentType = 'content-type';
  static const contentRange = 'content-range';
  static const hash = 'hash';

  static const contentTypeOctetStream = 'application/octet-stream';
  static const contentTypeFormUrlEncoded = 'application/x-www-form-urlencoded';
}
