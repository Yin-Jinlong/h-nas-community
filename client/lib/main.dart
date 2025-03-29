import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:h_nas/pages/home.dart';
import 'package:h_nas/prefs.dart';

import 'generated/l10n.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Prefs.init();

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
      routes: {'/': (context) => const HomePage()},
    );
  }
}
