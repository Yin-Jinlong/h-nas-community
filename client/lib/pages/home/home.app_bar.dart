part of 'home.dart';

AppBar _appBar(
  BuildContext context, {
  required Function() onSort,
  required Function() onRefresh,
  required Function() onTransmission,
  required Function() onLogin,
  required Function() onLogout,
}) {
  final user = Provider.of<UserModel>(context, listen: false);
  final taskCount =
      Global.uploadTasks.where((e) => !e.isDone).length +
      Global.downloadTasks.where((e) => !e.isDone).length;

  return AppBar(
    leading: Builder(
      builder: (context) {
        return Center(
          child: IconButton(
            tooltip: S.current.open_menu,
            onPressed: () {
              Scaffold.of(context).openDrawer();
            },
            icon: Hero(tag: 'menu_back', child: const Icon(Icons.menu)),
          ),
        );
      },
    ),
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
      IconButton(
        tooltip: user.user == null ? S.current.login : user.user!.nick,
        onPressed: user.user == null ? onLogin : onLogout,
        icon: Hero(
          tag: 'login',
          child: Icon(user.user == null ? Icons.person : Icons.logout),
        ),
      ),
    ],
  );
}
