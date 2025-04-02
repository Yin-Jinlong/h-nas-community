import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';

abstract class Toast {
  static CancelFunc show(
    String text, {
    Duration duration = const Duration(seconds: 3),
    bool clickClose = true,
  }) {
    return BotToast.showText(
      text: text,
      duration: duration,
      clickClose: clickClose,
    );
  }

  static CancelFunc showSuccess(
    String text, {
    Duration duration = const Duration(seconds: 3),
    bool clickClose = true,
  }) {
    return BotToast.showText(
      text: text,
      duration: duration,
      contentColor: Colors.green.shade400,
      clickClose: clickClose,
    );
  }

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
