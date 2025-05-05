import 'package:flutter/services.dart';

abstract class StoragePlugin {
  static const MethodChannel _channel = MethodChannel('storage_plugin');

  static Future<int> getAppSize() async {
    return await _channel.invokeMethod('getAppSize');
  }

  static Future<String> getExternalDownloadDir() async {
    return await _channel.invokeMethod('getExternalDownloadDir');
  }
}
