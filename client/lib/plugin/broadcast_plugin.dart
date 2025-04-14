import 'dart:convert';

import 'package:flutter/services.dart';

abstract class BroadcastPlugin {
  static const MethodChannel _channel = MethodChannel('broadcast_plugin');

  static Future<String> receiveBroadcast() async {
    final String result = await _channel.invokeMethod('receiveBroadcast');
    return result;
  }

  static final RegExp _ip4RegExp = RegExp(
    r'.*/((?:[0-9]{1,3}\.){3}[0-9]{1,3})$',
  );

  static Future<String?> receiveAPIURL() async {
    while (true) {
      final result = await receiveBroadcast();
      if (!result.contains('\n')) return null;
      final lines = result.split('\n');
      final ipLine = lines[0];
      if (!_ip4RegExp.hasMatch(ipLine)) return null;
      final match = _ip4RegExp.firstMatch(ipLine)!;

      final ip = match.group(1);

      final json = jsonDecode(lines[1]);

      final schema = json['schema'] as String;
      final port = json['port'] as int;
      final path = json['path'] as String;
      return '$schema://$ip:$port$path';
    }
  }
}
