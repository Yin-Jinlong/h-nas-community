import 'dart:async';

import 'package:context_menus/context_menus.dart';
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

part 'home.app_bar.dart';

part 'home.context_menu.dart';

part 'home.file_list.dart';

part 'home.image_viewer.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<StatefulWidget> createState() {
    return _HomePageState();
  }
}

class _HomePageState extends State<HomePage> {
  List<FileInfo> files = [];
  List<FileInfo> images = [];
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
        images = [];
        for (var file in v) {
          if (MediaType.parse(file.mediaType ?? '').type ==
              MediaType.typeImage) {
            images.add(file);
          }
        }
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
  var index = 0;

  showImage(ThumbnailModel thumbnailCache, FileInfo file) {
    final overlay = navigatorKey.currentState?.overlay;
    if (overlay == null) return;
    OverlayEntry? entry;
    index = images.indexOf(file);
    entry = OverlayEntry(
      builder: (context) {
        return _ImageViewerOverlayWidget(
          index: index,
          files: images,
          route: route,
          thumbnailCache: thumbnailCache,
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
      appBar: _appBar(
        context,
        onRefresh: () {
          setState(() {
            thumbnailCache.clear();
            files = [];
          });
          updateFiles();
        },
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
                      ? ContextMenuOverlay(
                        child: ListView(
                          children: [
                            for (var file in files)
                              ContextMenuRegion(
                                contextMenu: GenericContextMenu(
                                  buttonConfigs: _fileContextMenuButtons(
                                    file,
                                    onDownload: () {},
                                    onInfo: () {},
                                    onDelete: () {},
                                  ),
                                ),
                                child: _fileListItem(
                                  context,
                                  file,
                                  onTap: () {
                                    if (file.isFolder) {
                                      enterFolder(file.name);
                                    } else {
                                      switch (MediaType.parse(
                                        file.mediaType ?? '',
                                      ).type) {
                                        case MediaType.typeImage:
                                          showImage(thumbnailCache, file);
                                          break;
                                      }
                                    }
                                  },
                                ),
                              ),
                          ],
                        ),
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
  final int index;
  final Function() onClose;
  final ModalRoute route;
  final List<FileInfo> files;
  final ThumbnailModel thumbnailCache;

  const _ImageViewerOverlayWidget({
    required this.index,
    required this.files,
    required this.route,
    required this.thumbnailCache,
    required this.onClose,
  });

  @override
  State createState() {
    return _ImageViewerOverlayWidgetState();
  }
}
