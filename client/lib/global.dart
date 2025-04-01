import 'package:flutter/material.dart';
import 'package:h_nas/prefs.dart';
import 'package:package_info_plus/package_info_plus.dart';

abstract class Global {
  static const String copyright =
      'Copyright © 2023 yin-jinlong@github. All rights reserved.';

  static PackageInfo packageInfo = PackageInfo(
    appName: '',
    packageName: '',
    version: '',
    buildNumber: '',
  );
  static Locale locale = Prefs.locale;
  static ValueNotifier<ThemeData> theme = ValueNotifier(Prefs.theme);
}
