part of 'home.dart';

AppBar _appBar(
  BuildContext context, {
  required Function() onSort,
  required Function() onRefresh,
}) {
  return AppBar(
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
