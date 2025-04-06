import 'package:flutter/material.dart';

extension EditFieldUtils on State {
  static Widget? clearButton(
    TextEditingController controller,
    Function()? afterClear,
  ) {
    if (controller.text.isEmpty) return null;
    return IconButton(
      icon: Icon(Icons.clear),
      onPressed: () {
        controller.clear();
        afterClear?.call();
      },
    );
  }
}
