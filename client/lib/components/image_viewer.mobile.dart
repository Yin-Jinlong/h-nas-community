part of 'image_viewer.dart';

class _MobileImageViewer extends StatefulWidget {
  final List<Future<String> Function()> urls, rawUrls;
  final List<FileInfo> files;
  final int index;
  final void Function(int index) onChangeIndex;
  final Widget? loadingWidget;

  const _MobileImageViewer({
    required this.urls,
    required this.rawUrls,
    required this.files,
    required this.index,
    required this.onChangeIndex,
    required this.loadingWidget,
  });

  @override
  State createState() => _MobileImageViewerState();
}

class _MobileImageViewerState extends State<_MobileImageViewer> {
  final cacheManager = DefaultCacheManager();
  Map<int, String> urlMap = {}, rawUrlMap = {};
  Map<int, bool> rawMap = {};
  late final pageController = PageController(initialPage: widget.index);

  int index = 0;

  @override
  void initState() {
    super.initState();
    index = widget.index;

    for (int i = 0; i < widget.rawUrls.length; i++) {
      final index = i;
      widget.rawUrls[i]().then((url) {
        cacheManager.getFileFromCache(url).then((value) {
          if (value != null) {
            rawMap[index] = true;
          }
        });
      });
    }
  }

  void _showRaw() {
    setState(() {
      rawMap[index] = true;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        PhotoViewGallery.builder(
          itemCount: widget.urls.length,
          pageController: pageController,
          enableRotation: true,
          backgroundDecoration: BoxDecoration(
            color: Colors.black.withValues(alpha: 0.4),
          ),
          onPageChanged: (index) {
            setState(() {
              this.index = index;
              widget.onChangeIndex(index);
            });
          },
          builder: (context, index) {
            final urls = rawMap[index] != true ? widget.urls : widget.rawUrls;
            final urlsMap = rawMap[index] != true ? urlMap : rawUrlMap;
            final url = urlsMap[index];
            if (url == null) {
              urls[index]().then((v) {
                setState(() {
                  urlsMap[index] = v;
                });
              });
            }
            return PhotoViewGalleryPageOptions.customChild(
              child: CachedNetworkImage(
                imageUrl: url ?? '',
                httpHeaders: API.tokenHeader(),
                fadeInDuration: durationFast,
                fadeOutDuration: durationMedium,
                errorWidget: (context, url, error) {
                  return Icon(
                    url.isEmpty ? Icons.image : Icons.broken_image,
                    color: Colors.white,
                  );
                },
              ),
            );
          },
          loadingBuilder:
              (context, event) =>
                  widget.loadingWidget ?? CircularProgressIndicator(),
        ),
        Align(
          alignment: Alignment.bottomCenter,
          child: Padding(
            padding: EdgeInsets.all(12),
            child: Text(
              '${widget.index + 1}/${widget.urls.length}',
              style: TextTheme.of(context).bodyMedium?.copyWith(
                color: Colors.white.withValues(alpha: 0.4),
              ),
            ),
          ),
        ),
        if (rawMap[index] != true)
          Align(
            alignment: Alignment.bottomLeft,
            child: ShowRawButton(onTap: _showRaw, file: widget.files[index]),
          ),
      ],
    );
  }
}
