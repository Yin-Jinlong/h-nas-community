import 'dart:math';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:h_nas/components/tab_page.dart';
import 'package:h_nas/main.dart';
import 'package:h_nas/model/user_model.dart';
import 'package:h_nas/pages/login_logon/login_qr.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/edit_field_utils.dart';
import 'package:h_nas/utils/toast.dart';
import 'package:provider/provider.dart';
import 'package:universal_platform/universal_platform.dart';

import '../../generated/l10n.dart';

part 'login_logon.base.dart';
part 'login_logon.login.dart';
part 'login_logon.logon.dart';

class LogInOnPage extends StatefulWidget {
  const LogInOnPage({super.key});

  @override
  State createState() {
    return _LogInOnPageState();
  }
}

class _LogInOnPageState extends State<LogInOnPage> {
  var pageIndex = 0;
  bool _qr = false;
  final List<_BackgroundItem> _bgItems = [];

  @override
  void initState() {
    super.initState();
    final random = Random();

    const count = 6;
    const offCount = 10;

    for (var i = 0; i < count; i++) {
      _bgItems.add(
        _BackgroundItem(
          pos: Offset(random.nextDouble(), random.nextDouble()),
          radius: random.nextDouble(),
          colorOff: (random.nextInt(offCount - 1) + 1) * 360 / offCount,
        ),
      );
    }
  }

  Widget _background(BuildContext context) {
    return SizedBox.expand(
      child: CustomPaint(
        painter: _BackgroundPainter(
          color: HSVColor.fromColor(ColorScheme.of(context).primary),
          items: _bgItems,
        ),
      ),
    );
  }

  Widget _content() {
    return Card(
      child: Padding(
        padding: EdgeInsets.all(12),
        child: Stack(
          children: [
            Column(
              children: [
                Row(
                  children: [
                    IconButton(
                      tooltip: S.current.back,
                      onPressed: () {
                        navigatorKey.currentState?.pop();
                      },
                      icon: Icon(Icons.arrow_back),
                    ),
                    Text(
                      pageIndex == 0 ? S.current.login : S.current.logon,
                      style: TextTheme.of(context).headlineSmall,
                    ),
                  ],
                ),
                if (!_qr)
                  Hero(
                    tag: 'avatar',
                    child: const Icon(Icons.person, size: 50),
                  ),
                if (!_qr)
                  TabPage(
                    index: pageIndex,
                    children: [
                      _LoginWidget(
                        onGotoLogon: () {
                          setState(() {
                            pageIndex = 1;
                          });
                        },
                      ),
                      _LogonWidget(
                        onGotoLogin: () {
                          setState(() {
                            pageIndex = 0;
                          });
                        },
                      ),
                    ],
                  ),
                if (_qr) _qrView(),
              ],
            ),
            pageIndex == 0 && !_qr ? _qrLayout() : Container(),
          ],
        ),
      ),
    );
  }

  Widget _qrView() {
    return Column(
      children: [
        LoginQR(),
        ElevatedButton(
          onPressed: () {
            setState(() {
              _qr = false;
            });
          },
          child: Text(S.current.back_password_login),
        ),
      ],
    );
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
              onPressed: () {
                setState(() {
                  _qr = true;
                });
              },
              child: Icon(Icons.qr_code, size: 60),
            ),
          ),
        ),
      );
    } else {
      return Container();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: ColorScheme.of(context).secondary,
      body: Stack(
        children: [
          _background(context),
          BackdropFilter(
            filter: ImageFilter.blur(sigmaX: 150, sigmaY: 150),
            child: Container(),
          ),
          Center(
            child: IntrinsicWidth(
              child: IntrinsicHeight(
                child: ConstrainedBox(
                  constraints: BoxConstraints(minWidth: 300),
                  child: _content(),
                ),
              ),
            ),
          ),
        ],
      ),
    );
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

class _BackgroundItem {
  Offset pos;
  double radius;
  double colorOff;
  late Paint _paint;

  _BackgroundItem({
    required this.pos,
    required this.radius,
    required this.colorOff,
  }) {
    _paint =
        Paint()
          ..isAntiAlias = true
          ..style = PaintingStyle.fill;
  }

  void draw(
    Canvas canvas,
    HSVColor color,
    double width,
    double height,
    double r,
  ) {
    _paint.color = color.withHue((color.hue + colorOff) % 360).toColor();
    canvas.drawCircle(
      Offset(pos.dx * width, pos.dy * height),
      radius * r,
      _paint,
    );
  }
}

class _BackgroundPainter extends CustomPainter {
  final HSVColor color;
  final List<_BackgroundItem> items;

  _BackgroundPainter({required this.color, required this.items});

  @override
  void paint(Canvas canvas, Size size) {
    final maxR = sqrt(size.width * size.width + size.height * size.height) / 3;

    for (final item in items) {
      item.draw(canvas, color, size.width, size.height, maxR);
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
