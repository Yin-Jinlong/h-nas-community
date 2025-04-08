import 'package:flutter/cupertino.dart';

import 'pages/pages.dart';

class Routes {
  static const String home = '/';
  static const String audioPlayer = '/audio_player';
  static const String languages = '/languages';
  static const String loginOn = '/login_on';
  static const String settings = '/settings';
  static const String theme = '/theme';
  static const String transmission = '/transmission';

  static pageBuilder(
    RouteSettings routeSettings,
    BuildContext context, {
    required void Function(Locale) onLocaleChanged,
  }) {
    return switch (routeSettings.name) {
      home => const HomePage(),
      audioPlayer => const AudioPlayerPage(),
      languages => LanguagesPage(onLocaleChanged: onLocaleChanged),
      loginOn => const LogInOnPage(),
      settings => const SettingsPage(),
      theme => const ThemePage(),
      transmission => const TransmissionPage(),
      _ => const HomePage(),
    };
  }
}
