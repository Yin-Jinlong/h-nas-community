import 'dart:ui';

import 'package:h_nas/prefs.dart';

abstract class Global {
  static Locale locale = Prefs.locale;
}
