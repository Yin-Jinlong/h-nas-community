import 'package:flutter/cupertino.dart';

import 'pages/pages.dart';

class Routes {
  static const String home = '/';
  static const String ai = '/ai';
  static const String audioPlayer = '/audio_player';
  static const String languages = '/languages';
  static const String loginOn = '/login_on';
  static const String my = '/my';
  static const String qrScan = '/qr_scan';
  static const String search = '/search';
  static const String serverInfo = '/server_info';
  static const String settings = '/settings';
  static const String textReader = '/text_reader';
  static const String theme = '/theme';
  static const String transmission = '/transmission';
  static const String userManagement = '/user_management';
  static const String videoPlayer = '/video_player';

  static pageBuilder(
    RouteSettings routeSettings,
    BuildContext context, {
    required void Function(Locale) onLocaleChanged,
  }) {
    return switch (routeSettings.name) {
      home => const HomePage(),
      ai => const AIPage(),
      audioPlayer => const AudioPlayerPage(),
      languages => LanguagesPage(onLocaleChanged: onLocaleChanged),
      loginOn => const LogInOnPage(),
      my => const MyPage(),
      qrScan => const QRScanPage(),
      search => const SearchPage(),
      serverInfo => const ServerInfoPage(),
      settings => const SettingsPage(),
      textReader => const TextReaderPage(),
      theme => const ThemePage(),
      transmission => const TransmissionPage(),
      userManagement => const UserManagementPage(),
      videoPlayer => const VideoPlayerPage(),
      _ => const HomePage(),
    };
  }
}
