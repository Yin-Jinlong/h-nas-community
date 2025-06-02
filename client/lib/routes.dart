import 'package:flutter/material.dart';
import 'package:h_nas/global.dart';

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
  static const String storage = '/storage';
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
      storage => const StoragePage(),
      textReader => const TextReaderPage(),
      theme => const ThemePage(),
      transmission => const TransmissionPage(),
      userManagement => const UserManagementPage(),
      videoPlayer => const VideoPlayerPage(),
      _ => const HomePage(),
    };
  }
}

class AppPageRoute extends PageRoute<dynamic> {
  AppPageRoute({required this.onLocaleChanged, super.settings});

  final void Function(Locale) onLocaleChanged;
  @override
  Color? barrierColor;

  @override
  String? barrierLabel;

  @override
  bool maintainState = true;

  @override
  Duration get transitionDuration => durationMedium;

  @override
  Widget buildPage(
    BuildContext context,
    Animation<double> animation,
    Animation<double> secondaryAnimation,
  ) {
    return Routes.pageBuilder(
      settings,
      context,
      onLocaleChanged: onLocaleChanged,
    );
  }

  @override
  TickerFuture didPush() {
    controller?.duration = transitionDuration;
    return super.didPush();
  }

  @override
  bool didPop(dynamic result) {
    controller?.reverseDuration = reverseTransitionDuration;
    return super.didPop(result);
  }

  @override
  DelegatedTransitionBuilder? get delegatedTransition => _delegatedTransition;

  static Widget? _delegatedTransition(
    BuildContext context,
    Animation<double> animation,
    Animation<double> secondaryAnimation,
    bool allowSnapshotting,
    Widget? child,
  ) {
    final PageTransitionsTheme theme = Theme.of(context).pageTransitionsTheme;
    final TargetPlatform platform = Theme.of(context).platform;
    final DelegatedTransitionBuilder? themeDelegatedTransition = theme
        .delegatedTransition(platform);
    return themeDelegatedTransition != null
        ? themeDelegatedTransition(
          context,
          animation,
          secondaryAnimation,
          allowSnapshotting,
          child,
        )
        : null;
  }

  @override
  Widget buildTransitions(
    BuildContext context,
    Animation<double> animation,
    Animation<double> secondaryAnimation,
    Widget child,
  ) {
    return _AppTransitionsBuilder.instance.buildTransitions(
      this,
      context,
      animation,
      secondaryAnimation,
      child,
    );
  }
}

class _AppTransitionsBuilder extends PageTransitionsBuilder {
  const _AppTransitionsBuilder();

  static const _AppTransitionsBuilder instance = _AppTransitionsBuilder();

  @override
  Widget buildTransitions<T>(
    PageRoute<T> route,
    BuildContext context,
    Animation<double> animation,
    Animation<double> secondaryAnimation,
    Widget child,
  ) {
    return FadeTransition(
      opacity: CurvedAnimation(parent: animation, curve: Curves.easeInOut),
      child: MatrixTransition(
        animation: animation,
        child: child,
        onTransform:
            (animationValue) =>
                Matrix4.identity()
                  ..scale(0.9 + 0.1 * Curves.easeOut.transform(animationValue)),
      ),
    );
  }
}
