import 'dart:async';
import 'dart:io';

import 'package:context_menus/context_menus.dart';
import 'package:desktop_drop/desktop_drop.dart';
import 'package:dotted_decoration/dotted_decoration.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_breadcrumb/flutter_breadcrumb.dart';
import 'package:flutter_speed_dial/flutter_speed_dial.dart';
import 'package:h_nas/api/api.dart';
import 'package:h_nas/components/clickable.dart';
import 'package:h_nas/components/empty.dart';
import 'package:h_nas/components/file_preview_view.dart';
import 'package:h_nas/components/image_viewer.dart';
import 'package:h_nas/components/mini_audio_player.dart';
import 'package:h_nas/components/spring_draggable_container.dart';
import 'package:h_nas/components/user_avatar.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/main.dart';
import 'package:h_nas/pages/home/info_dialog.dart';
import 'package:h_nas/pages/home/new_folder_dialog.dart';
import 'package:h_nas/pages/home/rename_dialog.dart';
import 'package:h_nas/pages/home/sort_dialog.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/routes.dart';
import 'package:h_nas/settings/user.dart';
import 'package:h_nas/utils/file_task.dart';
import 'package:h_nas/utils/file_utils.dart';
import 'package:h_nas/utils/media_type.dart';
import 'package:h_nas/utils/storage_size.dart';
import 'package:h_nas/utils/toast.dart';
import 'package:tdtx_nf_icons/tdtx_nf_icons.dart';
import 'package:universal_html/html.dart' as web;
import 'package:universal_platform/universal_platform.dart';

part 'home.context_menu.dart';
part 'home.drawer.dart';
part 'home.file_list.dart';
part 'home.floating_action_button.dart';
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
  bool sortAsc = true;
  bool private = false;

  /// 正在拖拽
  bool _dragging = false;

  String get nowDir => '/${dirs.join('/')}';

  @override
  void initState() {
    super.initState();
    Global.player.playList.addListener(_render);
    Global.player.playState.addListener(_render);
    Global.player.nowPlay.addListener(_render);

    updateFiles();
  }

  _render() {
    setState(() {});
  }

  Future<void> updateFiles() async {
    // 没有配置服务器
    if (API.API_ROOT.isEmpty) {
      Toast.showError(S.current.error_set_server_addr);
      return;
    }
    // 先清空数据
    setState(() {
      Global.thumbnailCache.clear();
      files.clear();
      images.clear();
      audios.clear();
    });
    final values = await FileAPI.getFiles(nowDir, private: private);
    setState(() {
      files = values;
      _sort(); // 按照当前分类排序，后端返回默认文件名升序
      // 把图片和音频进行缓存
      for (var file in values) {
        final type = file.fileMediaType?.type;
        if (type == MediaType.typeImage) {
          images.add(file);
        } else if (type == MediaType.typeAudio) {
          audios.add(file);
        }
      }
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

  void _showTextReader(FileInfo file) {
    navigatorKey.currentState?.pushNamed(
      Routes.textReader,
      arguments: [file, private],
    );
  }

  _showImage(FileInfo file) {
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
          private: private,
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
      Global.player.openList(audios, index: index, private: private);
    });
  }

  _playVideo(FileInfo file) {
    setState(() {});
    navigatorKey.currentState?.pushNamed(
      Routes.videoPlayer,
      arguments: [file, private],
    );
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
        return InfoDialog(file: file, infoRow: _infoRow, private: private);
      },
    );
  }

  _newFolderMenu(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) {
        return NewFolderDialog(
          onCreate: (name) {
            FileAPI.newFolder('$nowDir/$name', private: private).then((v) {
              if (v == true) {
                navigatorKey.currentState?.pop();
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
            FileAPI.rename(file.fullPath, newName, private: private).then((v) {
              if (v != true) return;
              navigatorKey.currentState?.pop();
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
        FileAPIURL.file(file.fullPath, download: true, private: private),
        '_blank',
      );
      return;
    }
    final task = DownloadFileTask(
      dst:
          '${Global.downloadDir}${file.fullPath}${file.isFolder ? '.tar.gz' : ''}',
      name: file.name,
      size: file.size,
      createTime: DateTime.now(),
      file: file,
      private: private,
    );
    Global.downloadTasks.add(task);
    task.start();
    setState(() {});
  }

  _delete(FileInfo file) {
    FileAPI.delete(file.fullPath, private: private).then((v) {
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
      private: private,
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

  Widget _dropContent() {
    return RefreshIndicator(
      displacement: 20,
      child: Empty(
        isEmpty: files.isEmpty,
        child: ContextMenuOverlay(
          child: ListView.separated(
            itemCount: files.length,
            itemBuilder: (context, index) {
              final file = files[index];
              return ContextMenuRegion(
                contextMenu: GenericContextMenu(
                  buttonConfigs: _fileContextMenuButtons(
                    file,
                    onPlay: () {
                      switch (file.fileMediaType?.type) {
                        case MediaType.typeAudio:
                          _playAudio(file);
                          break;
                        case MediaType.typeVideo:
                          _playVideo(file);
                          break;
                      }
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
                  private: private,
                  onTap: () {
                    if (file.isFolder) {
                      enterFolder(file.name);
                    } else {
                      switch (MediaType.parse(file.mediaType ?? '').type) {
                        case MediaType.typeText:
                          _showTextReader(file);
                          break;
                        case MediaType.typeImage:
                          _showImage(file);
                          break;
                        case MediaType.typeAudio:
                          _playAudio(file);
                          break;
                        case MediaType.typeVideo:
                          _playVideo(file);
                          break;
                      }
                    }
                  },
                ),
              );
            },
            separatorBuilder: (BuildContext context, int index) {
              return Divider(
                height: 0,
                color: Colors.grey.withValues(alpha: 0.5),
              );
            },
          ),
        ),
      ),
      onRefresh: () async {
        await Future.delayed(durationSlow);
        await updateFiles();
      },
    );
  }

  _uploadFiles(List<File> items) async {
    for (var item in items) {
      final file = File(item.path);
      final stat = await file.stat();
      if (stat.type == FileSystemEntityType.file) {
        final name = file.path.split(Platform.pathSeparator).last;
        final task = UploadFileTask(
          file: file,
          path: '$nowDir/$name',
          name: name,
          size: file.lengthSync(),
          createTime: DateTime.now(),
          private: private,
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
    Global.player.playList.removeListener(_render);
    Global.player.playState.removeListener(_render);
    Global.player.nowPlay.removeListener(_render);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final nowPlay = Global.player.nowPlay.value;
    route = ModalRoute.of(context)!;

    return Scaffold(
      appBar: AppBar(
        actions: [
          IconButton(
            tooltip: S.current.search,
            onPressed: () {
              navigatorKey.currentState?.pushNamed(
                Routes.search,
                arguments: private,
              );
            },
            icon: Hero(tag: 'search', child: const Icon(Icons.search)),
          ),
          IconButton(
            onPressed: () {
              navigatorKey.currentState?.pushNamed(Routes.ai);
            },
            icon: Icon(TDTxNFIcons.nf_md_robot),
          ),
          IconButton(
            tooltip: S.current.sort,
            onPressed: () {
              _showSortDialog(context);
            },
            icon: Icon(Icons.sort),
          ),
        ],
      ),
      body: Stack(
        children: [
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  DropdownButton(
                    value: private,
                    items: [
                      DropdownMenuItem(
                        value: false,
                        child: Text(S.current.root_public),
                      ),
                      DropdownMenuItem(
                        value: true,
                        child: Text(S.current.root_private),
                      ),
                    ],
                    onChanged: (value) {
                      setState(() {
                        private = value!;
                        Global.thumbnailCache.private = private;
                        dirs.clear();
                        updateFiles();
                      });
                    },
                  ),
                  Expanded(
                    child: SingleChildScrollView(
                      scrollDirection: Axis.horizontal,
                      child: Padding(
                        padding: EdgeInsets.only(right: 6),
                        child: BreadCrumb(
                          divider: Icon(Icons.chevron_right),
                          items: [
                            BreadCrumbItem(
                              content: Text(
                                S.current.folder_root,
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
                                  style:
                                      Theme.of(context).textTheme.titleMedium,
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
                      ),
                    ),
                  ),
                ],
              ),
              DropTarget(
                enable: UniversalPlatform.isWindows,
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
                    _uploadFiles(
                      details.files.map((e) => File(e.path)).toList(),
                    );
                  });
                },
                child: Expanded(
                  child: Stack(children: [_dropContent(), _dropTip(context)]),
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
                    nowPlay != null && (nowPlay.type?.isAudio ?? false)
                        ? SpringDraggableContainer(
                          child: IntrinsicWidth(
                            child: MiniAudioPlayer(
                              onClose: () {
                                Global.player.stop();
                              },
                            ),
                          ),
                        )
                        : null,
              ),
            ),
          ),
        ],
      ),
      drawer: _drawer(
        context,
        onLogin: () {
          navigatorKey.currentState?.pushNamed(Routes.loginOn);
        },
        onLogout: () {
          UserS.user = null;
          Prefs.remove(Prefs.keyToken);
          setState(() {});
        },
      ),
      floatingActionButtonLocation: const _HomeFloatingActionButtonLocation(),
      floatingActionButton: _floatingActionButton(context),
    );
  }
}
