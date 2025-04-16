part of 'home.dart';

AppBar _appBar(
  BuildContext context, {
  required Function() onSort,
  required Function() onRefresh,
}) {
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
    ],
  );
}
