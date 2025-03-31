import 'package:flutter/material.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/toast.dart';
import 'package:universal_platform/universal_platform.dart';

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
                  child: Stack(children: [_LoginWidget(), _qrLayout()]),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class _LoginWidget extends StatefulWidget {
  @override
  State createState() {
    return _LoginState();
  }
}

class _LoginState extends State<_LoginWidget> {
  final logid = TextEditingController(), password = TextEditingController();

  _login() {
    API.login(logid.text, password.text).then((v) {
      if (v != null) Toast.showSuccess('登录成功：${v.nick}');
    });
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Row(
          children: [
            IconButton(
              onPressed: () {
                Navigator.of(context).pop();
              },
              icon: Hero(tag: 'menu_back', child: Icon(Icons.arrow_back)),
            ),
            Text(S.current.login, style: TextTheme.of(context).headlineSmall),
          ],
        ),
        Hero(tag: 'login', child: const Icon(Icons.person, size: 50)),
        Expanded(
          child: Padding(
            padding: EdgeInsets.all(8),
            child: TextField(
              controller: logid,
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
              maxLength: 18,
              obscureText: true,
              controller: password,
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
          onPressed: _login,
          child: Text(S.current.login),
        ),
      ],
    );
  }
}

Widget _qrLayout() {
  if (UniversalPlatform.isDesktopOrWeb) {
    return Align(
      alignment: Alignment.topRight,
      child: Tooltip(
        message: S.current.scan_login,
        child: ClipPath(
          clipper: _QRClipper(),
          child: TextButton(
            onPressed: () {},
            child: Icon(Icons.qr_code, size: 60),
          ),
        ),
      ),
    );
  } else {
    return Container();
  }
}

class _QRClipper extends CustomClipper<Path> {
  @override
  Path getClip(Size size) {
    // 右上三角
    return Path()
      ..moveTo(0, 0)
      ..lineTo(size.width, 0)
      ..lineTo(size.width, size.height)
      ..close();
  }

  @override
  bool shouldReclip(covariant CustomClipper<Path> oldClipper) {
    return true;
  }
}
