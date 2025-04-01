import 'package:flutter/material.dart';
import 'package:h_nas/prefs.dart';

abstract class Global {
  static Locale locale = Prefs.locale;
  static ValueNotifier<ThemeData> theme = ValueNotifier(Prefs.theme);
}
