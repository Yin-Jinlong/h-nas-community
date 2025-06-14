import 'package:flutter/material.dart';
import 'package:h_nas/components/empty.dart';
import 'package:h_nas/components/spring_draggable_container.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/pages/transmission/transmission.view.dart';
import 'package:h_nas/utils/file_task.dart';
import 'package:h_nas/utils/storage_size.dart';

class TransmissionPage extends StatefulWidget {
  const TransmissionPage({super.key});

  @override
  State createState() => _TransmissionPageState();
}

class _TransmissionPageState extends State<TransmissionPage>
    with TickerProviderStateMixin {
  late final TabController _tabController = TabController(
    length: 2,
    vsync: this,
  );

  @override
  void initState() {
    super.initState();
    Global.uploadTasks.addListener(_render);
    Global.downloadTasks.addListener(_render);
    WidgetsBinding.instance.addPostFrameCallback((timestamp) {
      _render();
    });
    _tabController.addListener(() {
      setState(() {});
    });
  }

  _render() {
    if (!mounted) return;
    setState(() {});
    if (Global.downloadTasks.where((e) => !e.isDone).isNotEmpty ||
        Global.uploadTasks.where((e) => !e.isDone).isNotEmpty) {
      WidgetsBinding.instance.addPostFrameCallback((timestamp) {
        _render();
      });
    }
  }

  void _removeUpload(UploadFileTask task) {
    task.cancel();
    Global.uploadTasks.remove(task);
  }

  void _removeDownload(DownloadFileTask task) {
    task.cancel();
    Global.downloadTasks.remove(task);
  }

  List<DataColumn> _tableColumns({List<DataColumn> extras = const []}) {
    return [
      DataColumn(label: Text(L.current.file_name)),
      DataColumn(label: Text(L.current.file_size)),
      ...extras,
      DataColumn(label: Text(L.current.operation)),
    ];
  }

  List<DataCell> _tableDataCells(
    FileTask task, {
    required Function(bool? value) onSelectedChanged,
    VoidCallback? onStart,
    VoidCallback? onPause,
    required VoidCallback onRemove,
    List<DataCell> extras = const [],
  }) {
    return [
      DataCell(Text(task.name)),
      DataCell(Text(task.size.storageSizeStr)),
      ...extras,
      DataCell(
        Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            onStart != null
                ? IconButton(
                  tooltip: L.current.start,
                  onPressed: () {},
                  icon: Icon(Icons.play_arrow),
                )
                : Container(),
            onPause != null
                ? IconButton(
                  tooltip: L.current.pause,
                  onPressed: onPause,
                  icon: Icon(Icons.pause),
                )
                : Container(),
            IconButton(
              tooltip: L.current.cancel,
              onPressed: onRemove,
              icon: Icon(Icons.clear),
            ),
          ],
        ),
      ),
    ];
  }

  Widget _progressDataTable<T extends FileTask>(
    Iterable<T> progressing, {
    required Function(T task) onRemove,
  }) {
    return SingleChildScrollView(
      child: DataTable(
        dividerThickness: 0,
        columns: _tableColumns(
          extras: [
            DataColumn(label: Text(L.current.status)),
            DataColumn(
              label: Text(L.current.progress),
              numeric: true,
              tooltip: L.current.progress,
            ),
          ],
        ),
        rows: [
          for (var task in progressing)
            DataRow(
              selected: task.selected,
              onSelectChanged: (value) {
                setState(() {
                  task.selected = value ?? false;
                });
              },
              cells: _tableDataCells(
                task,
                extras: [
                  DataCell(Text(task.status.text)),
                  DataCell(
                    SizedBox(
                      width: 40,
                      height: 40,
                      child: Stack(
                        children: [
                          Align(
                            alignment: Alignment.center,
                            child: CircularProgressIndicator(
                              value: task.progress,
                            ),
                          ),
                          Align(
                            alignment: Alignment.center,
                            child: Text(
                              task.progressStr,
                              style: TextStyle(fontSize: 12),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ],
                onSelectedChanged: (value) {
                  task.selected = value ?? false;
                  setState(() {});
                },
                onRemove: () {
                  onRemove(task);
                  setState(() {});
                },
              ),
            ),
        ],
      ),
    );
  }

  Widget _doneDataTable<T extends FileTask>(
    Iterable<T> done, {
    required Function(T task) onRemove,
  }) {
    return SingleChildScrollView(
      child: DataTable(
        dividerThickness: 0,
        columns: _tableColumns(),
        rows: [
          for (var task in done)
            DataRow(
              selected: task.selected,
              onSelectChanged: (value) {
                setState(() {
                  task.selected = value ?? false;
                });
              },
              cells: _tableDataCells(
                task,
                onSelectedChanged: (value) {
                  task.selected = value ?? false;
                  setState(() {});
                },
                onRemove: () {
                  onRemove(task);
                  setState(() {});
                },
              ),
            ),
        ],
      ),
    );
  }

  Widget _uploadView() {
    final progressing = Global.uploadTasks.where((e) => !e.isDone);
    final done = Global.uploadTasks.where((e) => e.isDone);
    return TransmissionView(
      progressingPage: Empty(
        isEmpty: progressing.isEmpty,
        child: _progressDataTable(progressing, onRemove: _removeUpload),
      ),
      donePage: Empty(
        isEmpty: done.isEmpty,
        child: _doneDataTable(done, onRemove: _removeUpload),
      ),
    );
  }

  Widget _downloadView() {
    final progressing = Global.downloadTasks.where((e) => !e.isDone);
    final done = Global.downloadTasks.where((e) => e.isDone);

    return TransmissionView(
      progressingPage: Empty(
        isEmpty: progressing.isEmpty,
        child: _progressDataTable(progressing, onRemove: _removeDownload),
      ),
      donePage: Empty(
        isEmpty: done.isEmpty,
        child: _doneDataTable(done, onRemove: _removeDownload),
      ),
    );
  }

  @override
  void dispose() {
    _tabController.dispose();
    Global.uploadTasks.removeListener(_render);
    Global.downloadTasks.removeListener(_render);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final uploadProgressing = Global.uploadTasks.where((e) => !e.isDone);
    final downloadProgressing = Global.downloadTasks.where((e) => !e.isDone);

    return Scaffold(
      appBar: AppBar(
        title: Text(L.current.transmission),
        actions: [
          IconButton(
            tooltip: L.current.start_all,
            onPressed: () {},
            icon: Icon(Icons.play_arrow),
          ),
          IconButton(
            tooltip: L.current.pause_all,
            onPressed: () {},
            icon: Icon(Icons.pause),
          ),
          IconButton(
            tooltip: L.current.delete_all,
            onPressed: () {},
            icon: Icon(Icons.delete_forever),
          ),
        ],
        bottom: TabBar(
          labelColor: ColorScheme.of(context).onTertiary,
          dividerColor: ColorScheme.of(context).onPrimary,
          dividerHeight: 1,
          controller: _tabController,
          tabs: [
            Badge(
              label: Text('${uploadProgressing.length}'),
              isLabelVisible: uploadProgressing.isNotEmpty,
              child: Tab(
                child: Padding(
                  padding: EdgeInsets.only(right: 8),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [Icon(Icons.upload), Text(L.current.upload)],
                  ),
                ),
              ),
            ),
            Badge(
              label: Text('${downloadProgressing.length}'),
              isLabelVisible: downloadProgressing.isNotEmpty,
              child: Tab(
                child: Padding(
                  padding: EdgeInsets.only(right: 8),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [Icon(Icons.download), Text(L.current.download)],
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        physics: const AlwaysScrollableScrollPhysics(),
        children: [_uploadView(), _downloadView()],
      ),
      floatingActionButton: AnimatedSwitcher(
        duration: durationFast,
        transitionBuilder: (child, animation) {
          return ScaleTransition(
            scale: CurveTween(
              curve:
                  animation.isForwardOrCompleted
                      ? Curves.easeIn
                      : Curves.easeOutBack,
            ).animate(animation),
            child: child,
          );
        },
        child:
            _tabController.index == 0
                ? SpringDraggableContainer(
                  child: FloatingActionButton(
                    tooltip: L.current.upload,
                    shape: const CircleBorder(),
                    onPressed: () {},
                    child: Icon(Icons.upload_file),
                  ),
                )
                : null,
      ),
    );
  }
}
