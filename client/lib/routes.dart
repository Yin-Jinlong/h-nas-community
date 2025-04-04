import 'package:flutter/cupertino.dart';

import 'pages/pages.dart';

class Routes {
  static const String home = '/';
  static const String languages = '/languages';
  static const String loginOn = '/login_on';
  static const String settings = '/settings';
  static const String theme = '/theme';

  static pageBuilder(
    RouteSettings settings,
    BuildContext context, {
    required void Function(Locale) onLocaleChanged,
  }) {
    return switch (settings.name) {
      Routes.home => const HomePage(),
      Routes.languages => LanguagesPage(onLocaleChanged: onLocaleChanged),
      Routes.loginOn => const LogInOnPage(),
      Routes.settings => const SettingsPage(),
      Routes.theme => const ThemePage(),
      _ => const HomePage(),
    };
  }
}
