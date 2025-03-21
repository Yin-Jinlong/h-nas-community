import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_breadcrumb/flutter_breadcrumb.dart';
import 'package:h_nas/components/file_preview_view.dart';
import 'package:h_nas/components/image_viewer.dart';
import 'package:h_nas/main.dart';
import 'package:h_nas/model/thumbnail_model.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/file_utils.dart';
import 'package:h_nas/utils/media_type.dart';
import 'package:h_nas/utils/storage_size.dart';
import 'package:provider/provider.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<StatefulWidget> createState() {
    return _HomePageState();
  }
}

class _HomePageState extends State<HomePage> {
  List<FileInfo> files = [];
  List<String> dirs = [];
  late ModalRoute route;

  @override
  void initState() {
    super.initState();
    updateFiles();
  }

  updateFiles() {
    API.getPublicFiles('/${dirs.join('/')}').then((v) {
      setState(() {
        files = v;
      });
    });
  }

  enterFolder(String dir) {
    setState(() {
      dirs.add(dir);
    });
    updateFiles();
  }

  var hover = false;

  showImage(FilePreview file) {
    final overlay = navigatorKey.currentState?.overlay;
    if (overlay == null) return;
    OverlayEntry? entry;
    entry = OverlayEntry(
      builder: (context) {
        return _ImageViewerOverlayWidget(
          file: file,
          route: route,
          onClose: () {
            entry?.remove();
          },
        );
      },
    );
    overlay.insert(entry);
  }

  @override
  Widget build(BuildContext context) {
    final textTheme = Theme.of(context).textTheme;

    final thumbnailCache = ThumbnailModel();
    route = ModalRoute.of(context)!;

    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.primary,
        title: Text('h-nas'),
        actions: [
          Tooltip(
            message: '刷新',
            child: IconButton(
              onPressed: () {
                setState(() {
                  thumbnailCache.clear();
                  files = [];
                });
                updateFiles();
              },
              icon: Icon(Icons.refresh),
            ),
          ),
          Tooltip(
            message: '登录',
            child: IconButton(onPressed: () {}, icon: const Icon(Icons.person)),
          ),
        ],
      ),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Text(
                '当前：',
                style: textTheme.titleMedium?.copyWith(
                  fontWeight: FontWeight.normal,
                ),
              ),
              BreadCrumb(
                divider: Icon(Icons.chevron_right),
                items: [
                  BreadCrumbItem(
                    content: Text(
                      '/',
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    onTap: () {
                      setState(() {
                        dirs.clear();
                        updateFiles();
                      });
                    },
                  ),
                  for (var i = 0; i < dirs.length; i++)
                    BreadCrumbItem(
                      content: Text(
                        dirs[i],
                        style: Theme.of(context).textTheme.titleMedium,
                      ),
                      onTap: () {
                        setState(() {
                          dirs.removeRange(i + 1, dirs.length);
                          updateFiles();
                        });
                      },
                    ),
                ],
              ),
            ],
          ),
          ChangeNotifierProvider.value(
            value: thumbnailCache,
            child: Expanded(
              child:
                  files.isNotEmpty
                      ? ListView(
                        children: [
                          for (var file in files)
                            ListTile(
                              title: Text(file.name),
                              subtitle: Text(
                                DateTime.fromMillisecondsSinceEpoch(
                                  file.createTime,
                                ).toString(),
                              ),
                              leading: FilePreviewView(fileInfo: file),
                              trailing: Text(file.size.storageSizeStr),
                              onTap: () {
                                if (file.isFolder) {
                                  enterFolder(file.name);
                                } else {
                                  switch (MediaType.parse(
                                    file.mediaType ?? '',
                                  ).type) {
                                    case MediaType.typeImage:
                                      thumbnailCache.get(file, (f) {
                                        showImage(f);
                                      }, (_) {});
                                      break;
                                  }
                                }
                              },
                            ),
                        ],
                      )
                      : const Center(child: Text('No Data')),
            ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        child: Icon(Icons.add),
        onPressed: () {},
      ),
    );
  }
}

class _ImageViewerOverlayWidget extends StatefulWidget {
  final FilePreview file;
  final Function() onClose;
  final ModalRoute route;

  const _ImageViewerOverlayWidget({
    required this.file,
    required this.route,
    required this.onClose,
  });

  @override
  State createState() {
    return _ImageViewerOverlayWidgetState();
  }
}

class _ImageViewerOverlayWidgetState extends State<_ImageViewerOverlayWidget>
    with SingleTickerProviderStateMixin {
  bool hover = false;

  late AnimationController layerController;

  double layerProgress = 0;

  bool close = false;

  @override
  void initState() {
    super.initState();
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
                    url: API.publicFilePreviewURL(widget.file.preview!),
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
