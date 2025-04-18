import 'package:flutter/services.dart';
import 'package:h_nas/global.dart';

abstract class NotificationsPlugin {
  static const MethodChannel _channel = MethodChannel('notifications_plugin');

  static void init() {
    _channel.setMethodCallHandler((call) async {
      switch (call.method) {
        case 'onPlayPause':
          _onPlayPause();
          break;
        case 'onClose':
          _onClose();
          break;
      }
      return null;
    });
  }

  static Future<bool> hasPermission() async {
    return await _channel.invokeMethod('hasPermission');
  }

  static Future<bool> requestPermission() async {
    return await _channel.invokeMethod('requestPermission');
  }

  static Future<void> showPlayerNotification(
    String title,
    String subText,
    bool playing,
  ) async {
    await _channel.invokeMethod('showPlayerNotification', [
      title,
      subText,
      playing,
    ]);
  }

  static Future<bool> closePlayerNotification() async {
    return await _channel.invokeMethod('close');
  }

  static void _onPlayPause() {
    Global.player.playPause();
  }

  static void _onClose() {
    Global.player.stop();
  }
}
