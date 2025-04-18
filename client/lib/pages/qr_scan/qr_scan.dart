import 'package:flutter/material.dart';
import 'package:flutter_scankit/flutter_scankit.dart';
import 'package:h_nas/components/dispose.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/main.dart';
import 'package:h_nas/utils/api.dart';
import 'package:permission_handler/permission_handler.dart';

class QRScanPage extends StatefulWidget {
  const QRScanPage({super.key});

  @override
  State createState() => _QRScanPageState();
}

class _QRScanPageState extends DisposeFlagState<QRScanPage> {
  late final ScanKitController _controller;
  String? _result;
  QRGrantInfo? _info;
  bool _requesting = false;

  @override
  void initState() {
    super.initState();

    _controller = ScanKitController();
    _controller.onResult.listen((event) {
      setState(() {
        _result = event.originalValue;
        _controller.pauseContinuouslyScan();
        _getInfo();
      });
    });

    _check();
  }

  Future<void> _check() async {
    if (await Permission.camera.isDenied) {
      await Permission.camera.request();
      if (await Permission.camera.isDenied) {
        navigatorKey.currentState?.pop();
      }
    }
  }

  void _getInfo() async {
    final r = await UserAPI.getQRGrantInfo(_result!);
    if (r == null) {
      return;
    }
    setState(() {
      _info = r;
    });
  }

  void _grant(bool grant) async {
    setState(() {
      _requesting = grant;
    });
    final r = await UserAPI.grant(_result!, grant);
    if (disposed) return;
    if (r != null) {
      navigatorKey.currentState?.pop();
    }
    setState(() {
      _requesting = false;
    });
  }

  Widget _scanView() {
    return Stack(
      children: [
        ScanKitWidget(controller: _controller),
        SizedBox.expand(
          child: CustomPaint(
            size: Size.infinite,
            painter: _QRScanViewPainter(),
          ),
        ),
      ],
    );
  }

  Widget _grantView() {
    return _info == null
        ? Center(child: CircularProgressIndicator())
        : SizedBox.expand(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            spacing: 12,
            children: [
              Text(_info!.ip),
              ElevatedButton(
                onPressed:
                    _requesting
                        ? null
                        : () {
                          _grant(false);
                        },
                child: Text(S.current.cancel),
              ),
              FilledButton(
                onPressed:
                    _requesting
                        ? null
                        : () {
                          _grant(true);
                        },
                child: Text(S.current.login),
              ),
            ],
          ),
        );
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        actions: [
          if (_result == null)
            IconButton(
              tooltip: S.current.photo_album,
              onPressed: () {
                _controller.pickPhoto();
              },
              icon: const Icon(Icons.image),
            ),
        ],
      ),
      body: _result == null ? _scanView() : _grantView(),
    );
  }
}

class _QRScanViewPainter extends CustomPainter {
  final Paint _paint = Paint();

  _QRScanViewPainter() {
    _paint.color = Colors.black.withValues(alpha: 0.6);
  }

  @override
  void paint(Canvas canvas, Size size) {
    final path = Path();
    path.fillType = PathFillType.evenOdd;
    path.moveTo(0, 0);
    path.lineTo(size.width, 0);
    path.lineTo(size.width, size.height);
    path.lineTo(0, size.height);

    final rectSize = Size(size.shortestSide, size.shortestSide) * 0.75;

    final rect = Rect.fromCenter(
      center: Offset(size.width / 2, size.height / 2),
      width: rectSize.width,
      height: rectSize.height,
    );

    path.moveTo(rect.left, rect.top);
    path.lineTo(rect.left, rect.bottom);
    path.lineTo(rect.right, rect.bottom);
    path.lineTo(rect.right, rect.top);
    path.close();

    canvas.clipPath(path);
    canvas.drawPath(path, _paint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
