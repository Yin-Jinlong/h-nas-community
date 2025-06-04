import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:h_nas/api/rhttp_http_provider.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/plugin/notifications_plugin.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/routes.dart';
import 'package:h_nas/settings/theme.dart';
import 'package:h_nas/settings/user.dart';
import 'package:h_nas/utils/security.dart';
import 'package:h_nas/utils/theme.dart';
import 'package:media_kit/media_kit.dart';
import 'package:package_info_plus/package_info_plus.dart';
import 'package:toastification/toastification.dart';
import 'package:universal_platform/universal_platform.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Security.init();
  Security.installCas();

  if (!UniversalPlatform.isWeb) {
    RHttpHttpProvider.init();
  }
  MediaKit.ensureInitialized();
  await Prefs.init();
  await Global.init();
  NotificationsPlugin.init();
  Global.packageInfo = await PackageInfo.fromPlatform();

  UserS.load();

  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();

    Global.locale.addListener(() {
      if (!mounted) return;
      var locale = Global.locale.value;
      L.load(locale);
      Prefs.setLocale(locale);
    });

    ThemeS.addThemeModeListener(() {
      setState(() {});
    });
    ThemeS.addThemeColorListener(() {
      setState(() {});
    });

    Global.isDark.value =
        PlatformDispatcher.instance.platformBrightness == Brightness.dark;
    PlatformDispatcher.instance.onPlatformBrightnessChanged = () {
      Global.isDark.value =
          PlatformDispatcher.instance.platformBrightness == Brightness.dark;
    };

    if (UniversalPlatform.isAndroid) {
      NotificationsPlugin.hasPermission().then((v) {
        if (!v) {
          NotificationsPlugin.requestPermission();
        }
      });
    }
  }

  @override
  void dispose() {
    Global.dispose();
    ThemeS.dispose();
    UserS.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return ToastificationWrapper(
      child: MaterialApp(
        debugShowCheckedModeBanner: false,
        title: 'H NAS',
        locale: Global.locale.value,
        localizationsDelegates: L.localizationsDelegates,
        supportedLocales: L.supportedLocales,
        themeMode: ThemeS.themeMode,
        theme: ThemeUtils.fromColor(ThemeS.themeColor, Brightness.light),
        darkTheme: ThemeUtils.fromColor(ThemeS.themeColor, Brightness.dark),
        scrollBehavior: const _ScrollBehavior(),
        navigatorKey: navigatorKey,
        onGenerateRoute: (settings) {
          return AppPageRoute(settings: settings);
        },
      ),
    );
  }
}

class _ScrollBehavior extends MaterialScrollBehavior {
  const _ScrollBehavior();

  @override
  final Set<PointerDeviceKind> dragDevices = const {
    PointerDeviceKind.touch,
    PointerDeviceKind.mouse,
  };
}
