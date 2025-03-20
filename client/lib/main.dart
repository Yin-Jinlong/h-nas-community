import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';
import 'package:h_nas/pages/home.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Flutter Demo',
      builder: BotToastInit(),
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.orange),
      ),
      navigatorObservers: [BotToastNavigatorObserver()],
      routes: {
        '/': (context) => const HomePage(),
      },
    );
  }
}
