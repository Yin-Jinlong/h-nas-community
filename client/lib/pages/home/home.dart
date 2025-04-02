import 'dart:async';

import 'package:context_menus/context_menus.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_breadcrumb/flutter_breadcrumb.dart';
import 'package:flutter_speed_dial/flutter_speed_dial.dart';
import 'package:h_nas/components/file_preview_view.dart';
import 'package:h_nas/components/image_viewer.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/main.dart';
import 'package:h_nas/model/thumbnail_model.dart';
import 'package:h_nas/model/user_model.dart';
import 'package:h_nas/pages/home/new_folder_dialog.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/file_utils.dart';
import 'package:h_nas/utils/media_type.dart';
import 'package:h_nas/utils/nerd.dart';
import 'package:h_nas/utils/storage_size.dart';
import 'package:h_nas/utils/toast.dart';
import 'package:provider/provider.dart';
import 'package:universal_html/html.dart' as web;
import 'package:universal_platform/universal_platform.dart';

import '../../generated/l10n.dart';
import '../../routes.dart';

part 'home.app_bar.dart';
part 'home.context_menu.dart';
part 'home.drawer.dart';
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
  final _openFloatingMenu = ValueNotifier(false);

  @override
  void initState() {
    super.initState();

    updateFiles();
  }

  updateFiles() {
    if (API.API_ROOT.isEmpty) {
      Toast.showError(S.current.error_set_server_addr);
      return;
    }
    FileAPI.getPublicFiles('/${dirs.join('/')}').then((v) {
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

  TableRow _infoRow(String label, Widget value) {
    return TableRow(
      children: [
        TableCell(
          child: Padding(
            padding: EdgeInsets.only(right: 4),
            child: Text(
              label,
              style: TextStyle().copyWith(fontWeight: FontWeight.bold),
            ),
          ),
        ),
        TableCell(child: value),
      ],
    );
  }

  _showFileInfo(FileInfo file) {
    showDialog(
      context: context,
      builder: (context) {
        return SimpleDialog(
          title: Text(file.name),
          contentPadding: const EdgeInsets.fromLTRB(12, 8, 12, 12),
          children: [
            Table(
              columnWidths: {0: IntrinsicColumnWidth(), 1: FlexColumnWidth(1)},
              defaultVerticalAlignment: TableCellVerticalAlignment.middle,
              children: [
                _infoRow(
                  S.current.file_info_path,
                  Text('${file.dir}${file.dir == '/' ? '' : '/'}${file.name}'),
                ),
                _infoRow(S.current.file_info_file_type, Text(file.fileType)),
                _infoRow(
                  S.current.file_info_media_type,
                  Text(file.mediaType ?? '?'),
                ),
                _infoRow(
                  S.current.file_info_create_time,
                  Text(
                    DateTime.fromMillisecondsSinceEpoch(
                      file.createTime,
                    ).toString(),
                  ),
                ),
                _infoRow(
                  S.current.file_info_update_time,
                  Text(
                    DateTime.fromMillisecondsSinceEpoch(
                      file.updateTime,
                    ).toString(),
                  ),
                ),
                _infoRow(
                  S.current.file_info_file_size,
                  Text(file.size.storageSizeStr),
                ),
              ],
            ),
          ],
        );
      },
    );
  }

  _newFolderMenu(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) {
        return NewFolderDialog(
          onCreate: (name) {
            FileAPI.newFolder('${dirs.join('/')}/$name').then((v) {
              if (v == true) {
                Navigator.of(context).pop();
                updateFiles();
              }
            });
          },
        );
      },
    );
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
        onTransmission: () {},
        onLogin: () {
          Navigator.of(navigatorKey.currentContext!).pushNamed(Routes.loginOn);
        },
        onLogout: () {
          Provider.of<UserModel>(context, listen: false).set(null);
          setState(() {});
        },
      ),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Text(
                S.current.now,
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
                                    onDownload: () {
                                      _download(file);
                                    },
                                    onInfo: () {
                                      _showFileInfo(file);
                                    },
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
                      : Center(child: Text(S.current.no_data)),
            ),
          ),
        ],
      ),
      drawer: _drawer(context),
      floatingActionButton: SpeedDial(
        openCloseDial: _openFloatingMenu,
        overlayColor: Colors.black,
        overlayOpacity: 0.5,
        icon: Icons.add,
        activeIcon: Icons.close,
        children: [
          SpeedDialChild(
            label: S.current.create_new_folder,
            child: Icon(Icons.create_new_folder),
            onTap: () {
              _newFolderMenu(context);
            },
          ),
          SpeedDialChild(
            foregroundColor: ColorScheme.of(context).onSecondary,
            backgroundColor: ColorScheme.of(context).secondary,
            label: S.current.upload,
            child: Icon(Icons.upload),
            onTap: _onUploadMenu,
          ),
        ],
      ),
    );
  }
}

_onUploadMenu() {
  if (UniversalPlatform.isWeb) {
    Toast.showError(S.current.web_not_support_upload);
  }
}

_download(FileInfo file) {
  if (UniversalPlatform.isWeb) {
    web.window.open(
      FileAPIURL.publicFile(file.fullPath, download: true),
      '_blank',
    );
  }
}
