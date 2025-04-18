import 'package:flutter/services.dart';
import 'package:h_nas/global.dart';

abstract class NotificationsPlugin {
  static const MethodChannel _channel = MethodChannel('notifications_plugin');

  static void init() {
    _channel.setMethodCallHandler((call) async {
      switch (call.method) {
        case 'onPrevious':
          _onPrevious();
          break;
        case 'onPlayPause':
          _onPlayPause();
          break;
        case 'onNext':
          _onNext();
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
    String artists,
    String? cover,
    bool playing,
  ) async {
    await _channel.invokeMethod('showPlayerNotification', [
      title,
      artists,
      cover,
      playing,
    ]);
  }

  static Future<bool> closePlayerNotification() async {
    return await _channel.invokeMethod('close');
  }

  static void _onPrevious() {
    Global.player.previous();
  }

  static void _onPlayPause() {
    Global.player.playPause();
  }

  static void _onNext() {
    Global.player.next();
  }

  static void _onClose() {
    Global.player.stop();
  }
}
