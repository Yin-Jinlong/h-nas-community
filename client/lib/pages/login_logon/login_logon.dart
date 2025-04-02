import 'package:flutter/material.dart';
import 'package:h_nas/components/tab_page.dart';
import 'package:h_nas/model/user_model.dart';
import 'package:h_nas/utils/api.dart';
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: ColorScheme.of(context).secondary,
      body: Center(
        child: IntrinsicWidth(
          child: IntrinsicHeight(
            child: ConstrainedBox(
              constraints: BoxConstraints(minWidth: 300),
              child: Card(
                child: Padding(
                  padding: EdgeInsets.all(12),
                  child: Stack(
                    children: [
                      Column(
                        children: [
                          Row(
                            children: [
                              Tooltip(
                                message: S.current.back,
                                child: IconButton(
                                  onPressed: () {
                                    Navigator.of(context).pop();
                                  },
                                  icon: Hero(
                                    tag: 'menu_back',
                                    child: Icon(Icons.arrow_back),
                                  ),
                                ),
                              ),
                              Text(
                                pageIndex == 0
                                    ? S.current.login
                                    : S.current.logon,
                                style: TextTheme.of(context).headlineSmall,
                              ),
                            ],
                          ),
                          Hero(
                            tag: 'login',
                            child: const Icon(Icons.person, size: 50),
                          ),
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
                        ],
                      ),
                      pageIndex == 0 ? _qrLayout() : Container(),
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
