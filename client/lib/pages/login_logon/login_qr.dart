import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:h_nas/components/dispose.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/main.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/settings/user.dart';
import 'package:h_nas/utils/api.dart';
import 'package:qr_flutter/qr_flutter.dart';

class LoginQR extends StatefulWidget {
  const LoginQR({super.key});

  @override
  State createState() => _LoginQRState();
}

enum _Status {
  /// 等待扫码
  waiting,

  /// 已扫码
  scanned,

  /// 成功
  success,

  /// 失败
  failed,

  /// 无效
  invalid;

  static _Status? of(String? state) {
    if (kDebugMode) {
      print(state);
    }
    return switch (state) {
      'WAITING' => _Status.waiting,
      'SCANNED' => _Status.scanned,
      'SUCCESS' => _Status.success,
      'FAILED' => _Status.failed,
      'INVALID' => _Status.invalid,
      null => null,
      String() => null,
    };
  }
}

class _LoginQRState extends DisposeFlagState<LoginQR> {
  String? _qrData;
  _Status? _status;

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
    _status = _Status.of(r?.state);
    if (_status == _Status.invalid) {
    } else if (_status != _Status.success) {
      _loopQuery();
    } else if (_status == _Status.success && r?.user != null) {
      Prefs.token = r?.token;
      UserS.user = r?.user;
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
          if (_status == _Status.scanned)
            _layer(
              0.95,
              Center(
                child: IntrinsicHeight(
                  child: Column(
                    children: [
                      Icon(Icons.check, color: Colors.green, size: 50),
                      Text(
                        S.current.scanned,
                        style: TextStyle(color: Colors.green, fontSize: 20),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          if (_status == _Status.invalid || _status == _Status.failed)
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
