import 'package:flutter/material.dart';

import '../generated/l10n.dart';

class LogInOnPage extends StatefulWidget {
  const LogInOnPage({super.key});

  @override
  State createState() {
    return _LogInOnPageState();
  }
}

class _LogInOnPageState extends State<LogInOnPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: ColorScheme.of(context).secondary,
      body: Center(
        child: IntrinsicWidth(
          child: IntrinsicHeight(
            child: ConstrainedBox(
              constraints: BoxConstraints(minWidth: 240),
              child: Card(
                child: Padding(
                  padding: EdgeInsets.all(12),
                  child: Column(
                    children: [
                      Row(
                        children: [
                          BackButton(),
                          Text(
                            S.current.login,
                            style: TextTheme.of(context).headlineSmall,
                          ),
                        ],
                      ),
                      Hero(
                        tag: 'login',
                        child: const Icon(Icons.person, size: 50),
                      ),
                      Expanded(
                        child: Padding(
                          padding: EdgeInsets.all(8),
                          child: TextField(
                            decoration: InputDecoration(
                              labelText: S.current.username,
                              hintText: '${S.current.username}/id',
                              hintStyle: TextStyle(color: Colors.grey),
                              border: OutlineInputBorder(),
                              prefixIcon: Icon(Icons.person),
                            ),
                          ),
                        ),
                      ),
                      Expanded(
                        child: Padding(
                          padding: EdgeInsets.all(8),
                          child: TextField(
                            obscureText: true,
                            decoration: InputDecoration(
                              labelText: S.current.password,
                              border: OutlineInputBorder(),
                              prefixIcon: Icon(Icons.lock),
                            ),
                          ),
                        ),
                      ),
                      ElevatedButton(
                        style: ElevatedButton.styleFrom(
                          backgroundColor: ColorScheme.of(context).primary,
                          foregroundColor: ColorScheme.of(context).onPrimary,
                          minimumSize: Size(double.infinity, 50),
                        ),
                        onPressed: () {},
                        child: Text(S.current.login),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}
