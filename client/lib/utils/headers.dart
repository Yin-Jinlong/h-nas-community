import 'package:dio/dio.dart';

extension ExtraHeaders on Headers {
  static const String authorization = 'Authorization';
  static const String contentID = 'Content-ID';
  static const String contentRange = 'Content-Range';
  static const String hash = 'Hash';

  static const String contentTypeOctetStream = 'application/octet-stream';
}
