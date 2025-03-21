import 'dart:math';

import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'dart:ui' as ui;

class ImageViewer extends StatefulWidget {
  final String url;

  const ImageViewer({super.key, required this.url});

  @override
  State createState() {
    return _ImageViewerState();
  }
}

class _ImageViewerState extends State<ImageViewer> {
  double offX = 0, offY = 0, mouseX = 0, mouseY = 0, downX = 0, downY = 0;

  double scale = 1;

  ui.Image? img;

  final Dio _dio = Dio();

  Size _screenSize = Size.zero;

  load() {
    _dio
        .get(widget.url, options: Options(responseType: ResponseType.bytes))
        .then((res) {
          ui.decodeImageFromList(res.data, (image) {
            setState(() {
              img = image;
              resetScale(image);
            });
          });
        });
  }

  resetScale(ui.Image img) {
    final size = Size(img.width.toDouble(), img.height.toDouble());

    if (size.aspectRatio > _screenSize.aspectRatio) {
      scale = _screenSize.width / size.width;
    } else {
      scale = _screenSize.height / size.height;
    }
  }

  @override
  void initState() {
    super.initState();
    load();
  }

  _packOff() {
    if (img != null) {
      final maxDx = max(img!.width * scale, _screenSize.width) / 2;
      final maxDy = max(img!.height * scale, _screenSize.height) / 2;

      if (offX > maxDx) {
        offX = maxDx;
      } else if (offX < -maxDx) {
        offX = -maxDx;
      }
      if (offY > maxDy) {
        offY = maxDy;
      } else if (offY < -maxDy) {
        offY = -maxDy;
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    _screenSize = MediaQuery.of(context).size;
    return Stack(
      children: [
        GestureDetector(
          onLongPressDown: (details) {
            setState(() {
              downX = details.localPosition.dx;
              downY = details.localPosition.dy;
            });
          },
          onLongPressMoveUpdate: (details) {
            setState(() {
              offX += details.localPosition.dx - downX;
              offY += details.localPosition.dy - downY;
            });
          },
          child: Listener(
            onPointerMove: (event) {
              setState(() {
                offX += event.delta.dx;
                offY += event.delta.dy;
                _packOff();
              });
            },
            onPointerSignal: (event) {
              if (event is PointerScrollEvent) {
                setState(() {
                  mouseX = event.localPosition.dx;
                  mouseY = event.localPosition.dy;
                  scale *= event.scrollDelta.dy > 0 ? 0.95 : 1.05;
                  if (scale < 0.1) {
                    scale = 0.1;
                  } else if (scale > 10) {
                    scale = 10;
                  }
                  _packOff();
                });
              }
            },
            child: CustomPaint(
              size: ui.Size.infinite,
              painter: _ImageViewerPainter(state: this),
            ),
          ),
        ),
      ],
    );
  }
}

class _ImageViewerPainter extends CustomPainter {
  final _ImageViewerState state;

  final Paint _paint = ui.Paint();

  _ImageViewerPainter({required this.state}) {
    _paint.isAntiAlias = true;
  }

  @override
  void paint(Canvas canvas, ui.Size size) {
    if (state.img == null) return;
    final img = state.img!;
    final src = Rect.fromLTWH(
      0,
      0,
      img.width.toDouble(),
      img.height.toDouble(),
    );
    var dst = Rect.fromCenter(
      center: Offset(state._screenSize.width / 2, state._screenSize.height / 2),
      width: src.width * state.scale,
      height: src.height * state.scale,
    ).translate(state.offX, state.offY);
    canvas.drawImageRect(state.img!, src, dst, _paint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) {
    return true;
  }
}
