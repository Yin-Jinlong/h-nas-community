import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:flutter_settings_screens/flutter_settings_screens.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/model/user_model.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/routes.dart';
import 'package:intl/intl.dart';
import 'package:package_info_plus/package_info_plus.dart';
import 'package:provider/provider.dart';
import 'package:media_kit/media_kit.dart';

import 'generated/l10n.dart';

void main() async {
  Intl.defaultLocale = 'zh';
  MediaKit.ensureInitialized();
  WidgetsFlutterBinding.ensureInitialized();
  await Global.init();
  await Prefs.init();
  await Settings.init();
  Global.packageInfo = await PackageInfo.fromPlatform();

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
    Global.theme.addListener(() {
      setState(() {});
    });
  }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider.value(
      value: UserModel(),
      child: MaterialApp(
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
        theme: Global.theme.value,
        navigatorKey: navigatorKey,
        navigatorObservers: [BotToastNavigatorObserver()],
        onGenerateRoute: (settings) {
          return PageRouteBuilder(
            pageBuilder: (context, animation, secondaryAnimation) {
              return Routes.pageBuilder(
                settings,
                context,
                onLocaleChanged: _setLocale,
              );
            },
            transitionsBuilder: (
              context,
              animation,
              secondaryAnimation,
              child,
            ) {
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
                            0.9 +
                                0.1 * Curves.easeOut.transform(animationValue),
                          ),
                ),
              );
            },
          );
        },
      ),
    );
  }
}
