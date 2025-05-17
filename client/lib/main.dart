import 'dart:ui';

import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
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
import 'package:universal_platform/universal_platform.dart';

import 'generated/l10n.dart';

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

GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  _setLocale(Locale l) {
    setState(() {
      S.delegate.load(l);
      Prefs.setLocale(l);
      Global.locale = l;
    });
  }

  @override
  void initState() {
    super.initState();
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
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'H NAS',
      builder: BotToastInit(),
      locale: Global.locale,
      localizationsDelegates: [
        S.delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
      supportedLocales: S.delegate.supportedLocales,
      themeMode: ThemeS.themeMode,
      theme: ThemeUtils.fromColor(ThemeS.themeColor, Brightness.light),
      darkTheme: ThemeUtils.fromColor(ThemeS.themeColor, Brightness.dark),
      scrollBehavior: const _ScrollBehavior(),
      navigatorKey: navigatorKey,
      navigatorObservers: [BotToastNavigatorObserver()],
      onGenerateRoute: (settings) {
        return PageRouteBuilder(
          settings: settings,
          pageBuilder: (context, animation, secondaryAnimation) {
            return Routes.pageBuilder(
              settings,
              context,
              onLocaleChanged: _setLocale,
            );
          },
          transitionsBuilder: (context, animation, secondaryAnimation, child) {
            return FadeTransition(
              opacity: CurvedAnimation(
                parent: animation,
                curve: Curves.easeInOut,
              ),
              child: MatrixTransition(
                animation: animation,
                child: child,
                onTransform:
                    (animationValue) =>
                        Matrix4.identity()..scale(
                          0.9 + 0.1 * Curves.easeOut.transform(animationValue),
                        ),
              ),
            );
          },
        );
      },
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
