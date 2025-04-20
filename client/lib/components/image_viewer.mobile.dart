part of 'image_viewer.dart';

class _MobileImageViewer extends StatefulWidget {
  final List<Future<String> Function()> urls;
  final int index;
  final Widget? loadingWidget;

  const _MobileImageViewer({
    required this.urls,
    required this.index,
    required this.loadingWidget,
  });

  @override
  State createState() => _MobileImageViewerState();
}

class _MobileImageViewerState extends State<_MobileImageViewer> {
  Map<int, String> urlMap = {};
  late final pageController = PageController(initialPage: widget.index);

  @override
  Widget build(BuildContext context) {
    return PhotoViewGallery.builder(
      itemCount: widget.urls.length,
      pageController: pageController,
      enableRotation: true,
      backgroundDecoration: BoxDecoration(
        color: Colors.black.withValues(alpha: 0.4),
      ),
      builder: (context, index) {
        final url = urlMap[index];
        if (url == null) {
          widget.urls[index]().then((v) {
            setState(() {
              urlMap[index] = v;
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
    );
  }
}
