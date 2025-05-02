import 'package:flutter/cupertino.dart';

extension Disposed on State {
  bool get disposed => !mounted;
}
