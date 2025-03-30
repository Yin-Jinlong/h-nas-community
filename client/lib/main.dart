import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:flutter_settings_screens/flutter_settings_screens.dart';
import 'package:h_nas/pages/home.dart';
import 'package:h_nas/pages/login_logon.dart';
import 'package:h_nas/pages/settings.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/routes.dart';

import 'generated/l10n.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Prefs.init();
  await Settings.init();

  runApp(const MyApp());
}

GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'H NAS',
      builder: BotToastInit(),
      localizationsDelegates: [
        S.delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
      supportedLocales: S.delegate.supportedLocales,
      theme: ThemeData(
        colorScheme: ColorScheme(
          brightness: Brightness.light,
          primary: Colors.orange.shade300,
          onPrimary: Colors.white,
          secondary: Colors.orange.shade200,
          onSecondary: Colors.black87,
          error: Colors.red,
          onError: Colors.black54,
          surface: Colors.grey.shade200,
          onSurface: Colors.black87,
        ),
        iconTheme: IconThemeData(
          color:
              HSLColor.fromColor(
                Colors.orange.shade300,
              ).withLightness(0.15).toColor(),
        ),
      ),
      navigatorKey: navigatorKey,
      navigatorObservers: [BotToastNavigatorObserver()],
      onGenerateRoute: (settings) {
        return PageRouteBuilder(
          pageBuilder: (context, animation, secondaryAnimation) {
            return switch (settings.name) {
              Routes.home => const HomePage(),
              Routes.loginOn => const LogInOnPage(),
              Routes.settings => const SettingsPage(),
              _ => const HomePage(),
            };
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
