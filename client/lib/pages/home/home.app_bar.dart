part of 'home.dart';

AppBar _appBar(
  BuildContext context, {
  required Function() onSort,
  required Function() onRefresh,
  required Function() onTransmission,
}) {
  final taskCount =
      Global.uploadTasks.where((e) => !e.isDone).length +
      Global.downloadTasks.where((e) => !e.isDone).length;

  return AppBar(
    title: Text(S.current.app_name),
    actions: [
      IconButton(
        tooltip: S.current.sort,
        onPressed: onSort,
        icon: Icon(Icons.sort),
      ),
      IconButton(
        tooltip: S.current.refresh,
        onPressed: onRefresh,
        icon: Icon(Icons.refresh),
      ),
      Badge(
        label: Text('$taskCount'),
        isLabelVisible: taskCount > 0,
        child: IconButton(
          tooltip: S.current.transmission,
          onPressed: onTransmission,
          icon: Icon(Icons.swap_vert),
        ),
      ),
    ],
  );
}
