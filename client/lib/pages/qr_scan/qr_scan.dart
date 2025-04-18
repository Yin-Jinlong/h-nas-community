import 'package:flutter/material.dart';
import 'package:flutter_scankit/flutter_scankit.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/main.dart';
import 'package:permission_handler/permission_handler.dart';

class QRScanPage extends StatefulWidget {
  const QRScanPage({super.key});

  @override
  State createState() => _QRScanPageState();
}

class _QRScanPageState extends State<QRScanPage> {
  late final ScanKitController _controller;
  String? _result;

  @override
  void initState() {
    super.initState();

    _controller = ScanKitController();
    _controller.onResult.listen((event) {
      setState(() {
        _result = event.originalValue;
        _controller.pauseContinuouslyScan();
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
    return Column(children: [Text(_result!)]);
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
