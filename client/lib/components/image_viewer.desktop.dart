part of 'image_viewer.dart';

class _DesktopImageViewer extends StatefulWidget {
  final List<Future<String> Function()> urls;
  final int index;
  final VoidCallback onLastImage, onNextImage;
  final Widget? loadingWidget;

  const _DesktopImageViewer({
    required this.urls,
    required this.index,
    required this.onLastImage,
    required this.onNextImage,
    required this.loadingWidget,
  });

  @override
  State createState() {
    return _DesktopImageViewerState();
  }
}

class _DesktopImageViewerState extends State<_DesktopImageViewer>
    with TickerProviderStateMixin {
  double offX = 0, offY = 0, mouseX = 0, mouseY = 0, downX = 0, downY = 0;

  double scale = 1, rotate = 0, _targetRotate = 0, flipX = 1, flipY = 1;

  ui.Image? img;

  final Dio _dio = Dio();

  Size _screenSize = Size.zero;

  int imgIndex = -1;

  late AnimationController _scaleAnimController,
      _flipXAnimController,
      _flipYAnimController,
      _rotateAnimController;

  @override
  void initState() {
    super.initState();
    _scaleAnimController = AnimationController(
      value: 1,
      vsync: this,
      lowerBound: 0.1,
      upperBound: 10,
      duration: const Duration(milliseconds: 1500),
    )..addListener(() {
      setState(() {
        scale = _scaleAnimController.value;
      });
    });
    _flipXAnimController = AnimationController(
      value: 1,
      vsync: this,
      lowerBound: -1,
      upperBound: 1,
      duration: durationFast,
    )..addListener(() {
      setState(() {
        flipX = _flipXAnimController.value;
      });
    });
    _flipYAnimController = AnimationController(
      value: 1,
      vsync: this,
      lowerBound: -1,
      upperBound: 1,
      duration: durationFast,
    )..addListener(() {
      setState(() {
        flipY = _flipYAnimController.value;
      });
    });
    _rotateAnimController =
        AnimationController(
            value: 0,
            vsync: this,
            lowerBound: -270,
            upperBound: 270,
            duration: const Duration(seconds: 1),
          )
          ..drive(CurveTween(curve: Curves.easeOutExpo))
          ..addStatusListener((state) {
            if (state == AnimationStatus.completed) {
              if (_rotateAnimController.value > 180) {
                _rotateAnimController.value -= 360;
              } else if (_rotateAnimController.value < -180) {
                _rotateAnimController.value += 360;
              }
              _targetRotate = _rotateAnimController.value;
              rotate = _targetRotate;
            }
          })
          ..addListener(() {
            setState(() {
              rotate = _rotateAnimController.value;
            });
          });
  }

  load() {
    if (imgIndex == widget.index) return;
    img = null;
    if (widget.index < 0) return;
    final i = widget.index;
    widget.urls[widget.index]().then((url) {
      _dio
          .get(
            url,
            options: Options(
              responseType: ResponseType.bytes,
              headers: API.tokenHeader(),
            ),
          )
          .then((res) {
            ui.decodeImageFromList(res.data, (image) {
              setState(() {
                img = image;
                imgIndex = i;
                _reset(image);
              });
            });
          });
    });
  }

  _reset(ui.Image img) {
    offX = 0;
    offY = 0;
    _flipXAnimController.animateTo(1);
    _flipYAnimController.animateTo(1);
    _targetRotate = 0;
    _rotateAnimController.animateTo(0);

    final size = Size(img.width.toDouble(), img.height.toDouble());

    if (size.aspectRatio > _screenSize.aspectRatio) {
      _scaleAnimController.animateTo(_screenSize.width / size.width);
    } else {
      _scaleAnimController.animateTo(_screenSize.height / size.height);
    }
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
  void didUpdateWidget(covariant _DesktopImageViewer oldWidget) {
    super.didUpdateWidget(oldWidget);
    load();
  }

  @override
  void dispose() {
    _scaleAnimController.dispose();
    _flipXAnimController.dispose();
    _flipYAnimController.dispose();
    _rotateAnimController.dispose();
    super.dispose();
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
                  _scaleAnimController.animateTo(
                    scale * (event.scrollDelta.dy > 0 ? 0.95 : 1.05),
                  );
                  _packOff();
                });
              }
            },
            child:
                img != null
                    ? CustomPaint(
                      size: ui.Size.infinite,
                      painter: _ImageViewerPainter(state: this),
                    )
                    : Center(
                      child:
                          widget.loadingWidget ?? CircularProgressIndicator(),
                    ),
          ),
        ),
        Align(
          alignment: Alignment.bottomCenter,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children:
                MediaQuery.of(context).size.width > 500
                    ? [
                      _ImageViewerController(
                        onFlip: () {
                          setState(() {
                            if (_targetRotate % 180 == 0) {
                              _flipXAnimController.animateTo(
                                _flipXAnimController.value < 0 ? 1 : -1,
                              );
                            } else {
                              _flipYAnimController.animateTo(
                                _flipYAnimController.value < 0 ? 1 : -1,
                              );
                            }
                          });
                        },
                        onLeftRotate: () {
                          setState(() {
                            _targetRotate -= 90;
                            _rotateAnimController.animateTo(_targetRotate);
                          });
                        },
                        onLastImage: widget.onLastImage,
                        infoText: '${widget.index + 1}/${widget.urls.length}',
                        onNextImage: widget.onNextImage,
                        onRightRotate: () {
                          setState(() {
                            _targetRotate += 90;
                            _rotateAnimController.animateTo(_targetRotate);
                          });
                        },
                        onReset: () {
                          setState(() {
                            _reset(img!);
                          });
                        },
                      ),
                    ]
                    : [],
          ),
        ),
      ],
    );
  }
}

class _ImageViewerPainter extends CustomPainter {
  final _DesktopImageViewerState state;

  final Paint _paint = ui.Paint();

  _ImageViewerPainter({required this.state}) {
    _paint.isAntiAlias = true;
  }

  _drawImageRotate(
    Canvas canvas,
    ui.Image img,
    Rect src,
    Rect dst,
    double rotate,
  ) {
    canvas.save();
    canvas.translate(dst.center.dx, dst.center.dy);
    canvas.rotate(rotate * pi / 180);
    canvas.scale(state.flipX, state.flipY);
    canvas.drawImageRect(
      img,
      src,
      Rect.fromCenter(
        center: Offset(0, 0),
        width: dst.width,
        height: dst.height,
      ),
      _paint,
    );
    canvas.restore();
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
    _drawImageRotate(canvas, img, src, dst, state.rotate);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) {
    return true;
  }
}

class _ImageViewerController extends StatefulWidget {
  final VoidCallback onFlip,
      onLeftRotate,
      onLastImage,
      onNextImage,
      onRightRotate,
      onReset;

  final String infoText;

  const _ImageViewerController({
    required this.onFlip,
    required this.onLeftRotate,
    required this.onLastImage,
    required this.infoText,
    required this.onNextImage,
    required this.onRightRotate,
    required this.onReset,
  });

  @override
  State createState() {
    return _ImageViewerControllerState();
  }
}

class _ImageViewerControllerState extends State<_ImageViewerController> {
  bool _mouseIn = false;

  @override
  Widget build(BuildContext context) {
    return Clickable(
      onTap: () {},
      onEnter: (event) {
        setState(() {
          _mouseIn = true;
        });
      },
      onExit: (event) {
        setState(() {
          _mouseIn = false;
        });
      },
      child: Padding(
        padding: EdgeInsets.only(bottom: 8),
        child: AnimatedOpacity(
          opacity: _mouseIn ? 0.9 : 0.02,
          duration: Duration(milliseconds: 200),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            mainAxisAlignment: MainAxisAlignment.center,
            spacing: 8,
            children: [
              Tooltip(
                message: S.current.photo_flip,
                child: IconButton.filledTonal(
                  onPressed: () {
                    widget.onFlip();
                  },
                  icon: Icon(Icons.flip),
                ),
              ),
              Tooltip(
                message: S.current.photo_rotate_left,
                child: IconButton.filledTonal(
                  style: FilledButton.styleFrom(
                    backgroundColor: Colors.greenAccent,
                  ),
                  onPressed: () {
                    widget.onLeftRotate();
                  },
                  icon: Icon(Icons.rotate_left),
                ),
              ),
              Tooltip(
                message: S.current.photo_previous,
                child: IconButton.filledTonal(
                  style: FilledButton.styleFrom(backgroundColor: Colors.green),
                  onPressed: () {
                    widget.onLastImage();
                  },
                  icon: Icon(Icons.first_page),
                ),
              ),
              FilledButton(
                style: FilledButton.styleFrom(
                  backgroundColor: Colors.grey,
                  padding: EdgeInsets.all(12),
                ),
                onPressed: () {},
                child: Text(widget.infoText),
              ),
              Tooltip(
                message: S.current.photo_next,
                child: IconButton.filledTonal(
                  style: FilledButton.styleFrom(backgroundColor: Colors.green),
                  onPressed: () {
                    widget.onNextImage();
                  },
                  icon: Icon(Icons.last_page),
                ),
              ),
              Tooltip(
                message: S.current.photo_rotate_right,
                child: IconButton.filledTonal(
                  style: FilledButton.styleFrom(
                    backgroundColor: Colors.greenAccent,
                  ),
                  onPressed: () {
                    widget.onRightRotate();
                  },
                  icon: Icon(Icons.rotate_right),
                ),
              ),
              Tooltip(
                message: S.current.reset,
                child: IconButton.filledTonal(
                  style: FilledButton.styleFrom(
                    backgroundColor: Colors.redAccent,
                  ),
                  onPressed: () {
                    widget.onReset();
                  },
                  icon: Icon(TDTxNFIcons.nf_md_restore),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
