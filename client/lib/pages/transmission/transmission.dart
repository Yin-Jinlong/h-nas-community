import 'package:flutter/material.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/global.dart';
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
    length: 3,
    vsync: this,
  );

  @override
  void initState() {
    super.initState();
    _tabController.addListener(() {
      setState(() {});
    });
  }

  List<DataColumn> _tableColumns({List<DataColumn> extras = const []}) {
    return [
      DataColumn(label: Text(S.current.file_name)),
      DataColumn(label: Text(S.current.file_size)),
      DataColumn(label: Text(S.current.status)),
      ...extras,
      DataColumn(label: Text(S.current.operation)),
    ];
  }

  List<DataCell> _tableDataCells(
    FileTask task, {
    required Function(bool? value) onSelectedChanged,
    required Function() onStart,
    required Function() onPause,
    required Function() onRemove,
    List<DataCell> extras = const [],
  }) {
    return [
      DataCell(Text(task.name)),
      DataCell(Text(task.size.storageSizeStr)),
      DataCell(Text(task.status.text)),
      ...extras,
      DataCell(
        Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            IconButton(
              tooltip: S.current.start,
              onPressed: () {
                if (task.canStart) {
                  task.controller!.reverse();
                  onStart();
                } else if (task.canPause) {
                  task.controller!.forward();
                  onPause();
                }
              },
              icon: AnimatedIcon(
                icon: AnimatedIcons.play_pause,
                progress: task.controller!,
              ),
            ),
            IconButton(
              tooltip: S.current.cancel,
              onPressed: onRemove,
              icon: Icon(Icons.clear),
            ),
          ],
        ),
      ),
    ];
  }

  Widget _uploadView() {
    if (Global.uploadTasks.isEmpty) {
      return Center(child: Text(S.current.no_data));
    }
    return Container();
  }

  Widget _downloadView() {
    if (Global.downloadTasks.isEmpty) {
      return Center(child: Text(S.current.no_data));
    }
    return DataTable(
      dividerThickness: 0,
      columns: _tableColumns(
        extras: [
          DataColumn(
            label: Text(S.current.progress),
            numeric: true,
            tooltip: S.current.progress,
          ),
        ],
      ),
      rows: [
        for (var task in Global.downloadTasks)
          DataRow(
            selected: task.selected,
            onSelectChanged: (value) {
              setState(() {
                task.selected = value ?? false;
              });
            },
            cells: _tableDataCells(
              task,
              extras: [DataCell(Text('${task.progressStr}%'))],
              onSelectedChanged: (value) {
                task.selected = value ?? false;
                setState(() {});
              },
              onStart: () {},
              onPause: () {},
              onRemove: () {
                // TODO
                Global.downloadTasks.remove(task);
                setState(() {});
              },
            ),
          ),
      ],
    );
  }

  Widget _doneView() {
    if (Global.doneTasks.isEmpty) {
      return Center(child: Text(S.current.no_data));
    }
    return Container();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(S.current.transmission),
        actions: [
          IconButton(
            tooltip: S.current.start_all,
            onPressed: () {},
            icon: Icon(Icons.play_arrow),
          ),
          IconButton(
            tooltip: S.current.pause_all,
            onPressed: () {},
            icon: Icon(Icons.pause),
          ),
          IconButton(
            tooltip: S.current.delete_all,
            onPressed: () {},
            icon: Icon(Icons.delete_forever),
          ),
        ],
        bottom: TabBar(
          labelColor: ColorScheme.of(context).tertiary,
          dividerColor: ColorScheme.of(context).tertiary,
          dividerHeight: 1,
          controller: _tabController,
          tabs: [
            Tab(
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [Icon(Icons.upload), Text(S.current.upload)],
              ),
            ),
            Tab(
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [Icon(Icons.download), Text(S.current.download)],
              ),
            ),
            Tab(
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [Icon(Icons.done), Text(S.current.done)],
              ),
            ),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        physics: const AlwaysScrollableScrollPhysics(),
        children: [_uploadView(), _downloadView(), _doneView()],
      ),
      floatingActionButton: AnimatedSwitcher(
        duration: Duration(milliseconds: 200),
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
                ? FloatingActionButton(
                  tooltip: S.current.upload,
                  shape: const CircleBorder(),
                  onPressed: () {},
                  child: Icon(Icons.upload_file),
                )
                : null,
      ),
    );
  }
}
