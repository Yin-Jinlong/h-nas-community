import 'package:flutter/material.dart';
import 'package:toastification/toastification.dart';

abstract class Toast {
  static ToastificationItem show(
    String text, {
    Duration duration = const Duration(seconds: 3),
  }) {
    return toastification.show(description: Text(text), icon: Container());
  }

  static ToastificationItem showSuccess(
    String text, {
    Duration duration = const Duration(seconds: 3),
  }) {
    return toastification.show(
      description: Text(text),
      autoCloseDuration: duration,
      primaryColor: Colors.green,
      icon: const Icon(Icons.check_circle),
    );
  }

  static ToastificationItem showError(
    String text, {
    Duration duration = const Duration(seconds: 3),
  }) {
    return toastification.show(
      description: Text(text),
      autoCloseDuration: duration,
      primaryColor: Colors.red,
      icon: const Icon(Icons.error),
    );
  }
}
