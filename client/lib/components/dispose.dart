import 'package:flutter/cupertino.dart';

abstract class DisposeFlagState<T extends StatefulWidget> extends State<T> {
  bool _disposed = false;

  bool get disposed => _disposed;

  @override
  void dispose() {
    _disposed = true;
    super.dispose();
  }
}
