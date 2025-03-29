part of 'home.dart';

class _ImageViewerOverlayWidgetState extends State<_ImageViewerOverlayWidget>
    with SingleTickerProviderStateMixin {
  bool hover = false;

  late AnimationController layerController;

  double layerProgress = 0;

  bool close = false;

  late int index;

  _onLastImage() {
    setState(() {
      if (index == 0) {
        index = widget.files.length - 1;
      } else {
        index--;
      }
    });
  }

  _onNextImage() {
    setState(() {
      if (index == widget.files.length - 1) {
        index = 0;
      } else {
        index++;
      }
    });
  }

  @override
  void initState() {
    super.initState();
    index = widget.index;
    layerController =
    AnimationController(vsync: this, duration: Duration(milliseconds: 200))
      ..addStatusListener((status) {
        if (status == AnimationStatus.completed && close) {
          widget.onClose();
        }
      })
      ..addListener(() {
        setState(() {
          layerProgress = layerController.value;
        });
      });
    widget.route.registerPopEntry(
      _ImageViewerOverlayPopEntry((e) {
        _close();
        widget.route.unregisterPopEntry(e);
      }),
    );
    Future.delayed(Duration(milliseconds: 16)).then((_) {
      close = false;
      layerController.forward();
    });
  }

  _close() {
    close = true;
    layerController.animateTo(0);
  }

  @override
  Widget build(BuildContext context) {
    return Opacity(
      opacity: layerProgress,
      child: Scaffold(
        backgroundColor: Colors.black.withAlpha(180),
        body: PopScope(
          canPop: false,
          onPopInvokedWithResult: (didPop, result) {
            _close();
          },
          child: SafeArea(
            child: Stack(
              children: [
                Transform.scale(
                  scale: 0.95 + 0.05 * layerProgress,
                  child: ImageViewer(
                    index: index,
                    urls: [
                      for (var file in widget.files)
                            () async {
                          final c = Completer<String>();
                          widget.thumbnailCache.get(file, (f) {
                            c.complete(API.publicFilePreviewURL(f.preview!));
                          }, (_) {});
                          return c.future;
                        },
                    ],
                    onLastImage: _onLastImage,
                    onNextImage: _onNextImage,
                  ),
                ),
                Align(
                  alignment: Alignment.topRight,
                  child: Tooltip(
                    message: '关闭',
                    child: InkWell(
                      onTap: () {},
                      onHover: (isHover) {
                        setState(() {
                          hover = isHover;
                        });
                      },
                      child: AnimatedOpacity(
                        duration: Duration(milliseconds: 200),
                        opacity: hover ? 0.9 : 0.1,
                        child: IconButton(
                          icon: Icon(Icons.close, color: Colors.white),
                          onPressed: () {
                            _close();
                          },
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _ImageViewerOverlayPopEntry extends PopEntry {
  final ValueNotifier<bool> value = ValueNotifier(false);

  Function(_ImageViewerOverlayPopEntry) _onPopInvoked;

  _ImageViewerOverlayPopEntry(this._onPopInvoked);

  @override
  void onPopInvoked(bool didPop) {
    _onPopInvoked(this);
  }

  @override
  ValueListenable<bool> get canPopNotifier {
    return value;
  }
}
