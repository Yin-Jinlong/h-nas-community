import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';

class Toast {
  static CancelFunc showError(
    String text, {
    Duration duration = const Duration(seconds: 3),
    bool clickClose = true,
  }) {
    return BotToast.showText(
      text: text,
      duration: duration,
      contentColor: Colors.red.shade400,
      clickClose: clickClose,
    );
  }
}
