import 'package:flutter/material.dart';
import 'package:h_nas/api/api.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/settings/user.dart';
import 'package:h_nas/utils/dispose.dart';
import 'package:qr_flutter/qr_flutter.dart';

class LoginQR extends StatefulWidget {
  const LoginQR({super.key});

  @override
  State createState() => _LoginQRState();
}

class _LoginQRState extends State<LoginQR> {
  String? _qrData;
  LoginQRInfoStatus? _status;

  @override
  void initState() {
    super.initState();
    _request();
  }

  void _request() async {
    setState(() {
      _status = null;
      _qrData = null;
    });
    await Future.delayed(Duration(milliseconds: 500));
    if (disposed) return;

    UserAPI.requestLoginQR().then((v) {
      if (disposed) return;
      setState(() {
        _qrData = v;
        _loopQuery();
      });
    });
  }

  void _loopQuery() async {
    await Future.delayed(Duration(seconds: 1));
    if (disposed) return;
    final r = await UserAPI.loginQR(_qrData!);
    _status = r.status;
    if (_status == LoginQRInfoStatus.invalid) {
    } else if (_status != LoginQRInfoStatus.success) {
      _loopQuery();
    } else if (_status == LoginQRInfoStatus.success && r.user != null) {
      Prefs.token = r.token;
      UserS.user = r.user;
      navigatorKey.currentState?.pop();
    }
    setState(() {});
  }

  Widget _layer(double alpha, Widget child) {
    return SizedBox.expand(
      child: Container(
        color: ColorScheme.of(context).surface.withValues(alpha: alpha),
        child: child,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return SizedBox.square(
      dimension: 200,
      child: Stack(
        children: [
          QrImageView(
            data: _qrData ?? Global.packageInfo.appName,
            errorCorrectionLevel: QrErrorCorrectLevel.H,
          ),
          if (_qrData == null)
            _layer(0.9, Center(child: CircularProgressIndicator())),
          if (_status == LoginQRInfoStatus.scanned)
            _layer(
              0.95,
              Center(
                child: IntrinsicHeight(
                  child: Column(
                    children: [
                      Icon(Icons.check, color: Colors.green, size: 50),
                      Text(
                        L.current.scanned,
                        style: TextStyle(color: Colors.green, fontSize: 20),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          if (_status == LoginQRInfoStatus.invalid ||
              _status == LoginQRInfoStatus.failed)
            _layer(
              0.95,
              Center(
                child: IconButton(
                  onPressed: () {
                    _request();
                  },
                  icon: Icon(
                    Icons.refresh,
                    size: 50,
                    color: ColorScheme.of(context).onSurface,
                  ),
                ),
              ),
            ),
        ],
      ),
    );
  }
}
