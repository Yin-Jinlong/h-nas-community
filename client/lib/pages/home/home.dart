import 'dart:async';
import 'dart:io';

import 'package:context_menus/context_menus.dart';
import 'package:desktop_drop/desktop_drop.dart';
import 'package:dotted_decoration/dotted_decoration.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_breadcrumb/flutter_breadcrumb.dart';
import 'package:flutter_speed_dial/flutter_speed_dial.dart';
import 'package:h_nas/components/empty.dart';
import 'package:h_nas/components/file_preview_view.dart';
import 'package:h_nas/components/image_viewer.dart';
import 'package:h_nas/components/mini_audio_player.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/main.dart';
import 'package:h_nas/model/thumbnail_model.dart';
import 'package:h_nas/model/user_model.dart';
import 'package:h_nas/pages/home/info_dialog.dart';
import 'package:h_nas/pages/home/new_folder_dialog.dart';
import 'package:h_nas/pages/home/rename_dialog.dart';
import 'package:h_nas/pages/home/sort_dialog.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/file_task.dart';
import 'package:h_nas/utils/file_utils.dart';
import 'package:h_nas/utils/media_type.dart';
import 'package:h_nas/utils/storage_size.dart';
import 'package:h_nas/utils/toast.dart';
import 'package:provider/provider.dart';
import 'package:tdtx_nf_icons/tdtx_nf_icons.dart';
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

class _HomePageState extends State<HomePage> with TickerProviderStateMixin {
  List<FileInfo> files = [];
  List<FileInfo> images = [];
  List<FileInfo> audios = [];
  List<String> dirs = [];
  late ModalRoute route;
  final _openFloatingMenu = ValueNotifier(false);
  SortType sortType = SortType.name;
  bool sortAsc = true, _showPlayer = false;

  /// 正在拖拽
  bool _dragging = false;

  @override
  void initState() {
    super.initState();
    Global.player.playState.addListener(_render);
    Global.player.audioInfo.addListener(_render);
    Global.player.audioInfo.addListener(_render);

    updateFiles();
  }

  _render() {
    setState(() {});
  }

  updateFiles() {
    if (API.API_ROOT.isEmpty) {
      Toast.showError(S.current.error_set_server_addr);
      return;
    }
    FileAPI.getPublicFiles('/${dirs.join('/')}').then((v) {
      setState(() {
        files = v;
        _sort();
        images = [];
        audios = [];
        for (var file in v) {
          final type = file.fileMediaType?.type;
          if (type == MediaType.typeImage) {
            images.add(file);
          } else if (type == MediaType.typeAudio) {
            audios.add(file);
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

  _showImage(ThumbnailModel thumbnailCache, FileInfo file) {
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

  _playAudio(FileInfo file) {
    setState(() {
      final index = audios.indexOf(file);
      if (index < 0) return;
      Global.player.openList(audios, index: index);
      _showPlayer = true;
    });
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
        return InfoDialog(file: file, infoRow: _infoRow);
      },
    );
  }

  _newFolderMenu(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) {
        return NewFolderDialog(
          onCreate: (name) {
            FileAPI.newPublicFolder('${dirs.join('/')}/$name').then((v) {
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

  int _fileCompare(
    FileInfo a,
    FileInfo b,
    Comparable Function(FileInfo f) fn,
  ) =>
      a.fileType == b.fileType
          ? fn(a).compareTo(fn(b)) * (sortAsc ? 1 : -1)
          : a.isFolder
          ? -1
          : 1;

  _sort() => switch (sortType) {
    SortType.name => files.sort((a, b) => _fileCompare(a, b, (f) => f.name)),
    SortType.createTime => files.sort(
      (a, b) => _fileCompare(a, b, (f) => f.createTime),
    ),

    SortType.updateTime => files.sort(
      (a, b) => _fileCompare(a, b, (f) => f.updateTime),
    ),
    SortType.size => files.sort((a, b) => _fileCompare(a, b, (f) => f.size)),
  };

  _showSortDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) {
        return SortDialog(
          initType: sortType,
          initAsc: sortAsc,
          onSort: (type, asc) {
            setState(() {
              sortType = type;
              sortAsc = asc;
              _sort();
            });
          },
        );
      },
    );
  }

  _showRenameDialog(BuildContext context, FileInfo file) {
    showDialog(
      context: context,
      builder: (context) {
        return RenameDialog(
          file: file,
          onRename: (newName) {
            FileAPI.renamePublicFolder(file.fullPath, newName).then((v) {
              if (v != true) return;
              Navigator.of(context).pop();
              updateFiles();
            });
          },
        );
      },
    );
  }

  _download(FileInfo file) {
    if (UniversalPlatform.isWeb) {
      web.window.open(
        FileAPIURL.publicFile(file.fullPath, download: true),
        '_blank',
      );
      return;
    }
    final task = DownloadFileTask(
      dst: '${Global.downloadDir}${file.fullPath}',
      name: file.name,
      size: file.size,
      createTime: DateTime.now(),
      file: file,
    );
    Global.downloadTasks.add(task);
    task.start();
    setState(() {});
  }

  _delete(FileInfo file) {
    FileAPI.deletePublic(file.fullPath).then((v) {
      if (v == true) {
        updateFiles();
      }
    });
  }

  _onUploadMenu(String dir) async {
    if (UniversalPlatform.isWeb) {
      Toast.showError(S.current.web_not_support(S.current.value_upload_file));
      return;
    }
    final result = await FilePicker.platform.pickFiles();
    if (result == null) return;
    final file = File(result.files.single.path!);
    final name = file.path.split(Platform.pathSeparator).last;
    final task = UploadFileTask(
      file: file,
      path: '$dir/$name',
      name: name,
      size: file.lengthSync(),
      createTime: DateTime.now(),
    );
    task.onDone = () {
      updateFiles();
    };
    Global.uploadTasks.add(task);
    task.start();
  }

  Widget _dropTip(BuildContext context) {
    return AnimatedSwitcher(
      duration: durationFast,
      transitionBuilder: (child, animation) {
        return FadeTransition(opacity: animation, child: child);
      },
      child:
          _dragging
              ? Container(
                color: Colors.white70.withValues(alpha: 0.95),
                child: Padding(
                  padding: EdgeInsets.all(2),
                  child: Container(
                    width: double.infinity,
                    height: double.infinity,
                    decoration: DottedDecoration(
                      shape: Shape.box,
                      strokeWidth: 4,
                      dash: [12, 4],
                      color: ColorScheme.of(context).primary,
                    ),
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.upload, size: 40),
                        Text(S.current.drop_to_upload),
                      ],
                    ),
                  ),
                ),
              )
              : Container(key: const ValueKey(0)),
    );
  }

  Widget _dropContent(ThumbnailModel thumbnailCache) {
    return Empty(
      isEmpty: files.isEmpty,
      child: ContextMenuOverlay(
        child: ListView(
          children: [
            for (var file in files)
              ContextMenuRegion(
                contextMenu: GenericContextMenu(
                  buttonConfigs: _fileContextMenuButtons(
                    file,
                    onPlay: () {
                      _playAudio(file);
                    },
                    onRename: () {
                      _showRenameDialog(context, file);
                    },
                    onDownload: () {
                      _download(file);
                    },
                    onInfo: () {
                      _showFileInfo(file);
                    },
                    onDelete: () {
                      _delete(file);
                    },
                  ),
                ),
                child: _fileListItem(
                  context,
                  file,
                  onTap: () {
                    if (file.isFolder) {
                      enterFolder(file.name);
                    } else {
                      switch (MediaType.parse(file.mediaType ?? '').type) {
                        case MediaType.typeImage:
                          _showImage(thumbnailCache, file);
                          break;
                        case MediaType.typeAudio:
                          _playAudio(file);
                          break;
                      }
                    }
                  },
                ),
              ),
          ],
        ),
      ),
    );
  }

  _uploadFiles(List<DropItem> items) async {
    for (var item in items) {
      final file = File(item.path);
      final stat = await file.stat();
      if (stat.type == FileSystemEntityType.file) {
        final name = file.path.split(Platform.pathSeparator).last;
        final task = UploadFileTask(
          file: file,
          path: '${dirs.join('/')}/$name',
          name: name,
          size: file.lengthSync(),
          createTime: DateTime.now(),
        );
        task.onDone = () {
          updateFiles();
        };
        Global.uploadTasks.add(task);
        task.start();
      } else {
        Toast.showError(S.current.only_support_upload_file);
      }
    }
  }

  @override
  void dispose() {
    Global.player.playState.removeListener(_render);
    Global.player.audioInfo.removeListener(_render);
    Global.player.audioInfo.removeListener(_render);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final textTheme = Theme.of(context).textTheme;
    final user = Provider.of<UserModel>(context);
    final thumbnailCache = ThumbnailModel();
    route = ModalRoute.of(context)!;

    return Scaffold(
      appBar: _appBar(
        context,
        onSort: () {
          _showSortDialog(context);
        },
        onRefresh: () {
          setState(() {
            thumbnailCache.clear();
            files = [];
          });
          updateFiles();
        },
        onTransmission: () {
          if (UniversalPlatform.isWeb) {
            Toast.showError(
              S.current.web_not_support(S.current.value_transmission),
            );
            return;
          }
          Navigator.of(context).pushNamed(Routes.transmission);
        },
        onLogin: () {
          Navigator.of(navigatorKey.currentContext!).pushNamed(Routes.loginOn);
        },
        onLogout: () {
          user.set(null);
          Prefs.remove(Prefs.keyToken);
          Prefs.remove(Prefs.keyAuthToken);
          setState(() {});
        },
      ),
      body: Stack(
        children: [
          Column(
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
              DropTarget(
                onDragEntered: (details) {
                  setState(() {
                    _dragging = true;
                  });
                },
                onDragExited: (details) {
                  setState(() {
                    _dragging = false;
                  });
                },
                onDragDone: (details) {
                  setState(() {
                    _dragging = false;
                    _uploadFiles(details.files);
                  });
                },
                child: ChangeNotifierProvider.value(
                  value: thumbnailCache,
                  child: Expanded(
                    child: Stack(
                      children: [
                        _dropContent(thumbnailCache),
                        _dropTip(context),
                      ],
                    ),
                  ),
                ),
              ),
            ],
          ),

          Align(
            alignment: Alignment.bottomCenter,
            child: Padding(
              padding: EdgeInsets.all(8),
              child: AnimatedSwitcher(
                duration: durationMedium,
                transitionBuilder: (child, animation) {
                  return MatrixTransition(
                    animation: CurveTween(
                      curve: Curves.easeOutBack,
                    ).animate(animation),
                    child: child,
                    onTransform: (animationValue) {
                      return Matrix4.identity()
                        ..scale(animationValue * 0.2 + 0.8)
                        ..translate(0.0, 100.0 * (1 - animationValue));
                    },
                  );
                },
                child:
                    _showPlayer
                        ? IntrinsicWidth(
                          child: MiniAudioPlayer(
                            onClose: () {
                              Global.player.stop();
                              setState(() {
                                _showPlayer = false;
                              });
                            },
                          ),
                        )
                        : null,
              ),
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
              if (user.user == null) {
                Toast.showError(S.current.please_login);
              } else {
                _newFolderMenu(context);
              }
            },
          ),
          SpeedDialChild(
            foregroundColor: ColorScheme.of(context).onSecondary,
            backgroundColor: ColorScheme.of(context).secondary,
            label: S.current.upload,
            child: Icon(Icons.upload),
            onTap: () {
              if (user.user == null) {
                Toast.showError(S.current.please_login);
              } else {
                _onUploadMenu(dirs.join('/'));
              }
            },
          ),
        ],
      ),
    );
  }
}
