part of 'home.dart';

AppBar _appBar(
  BuildContext context, {
  required Function() onRefresh,
  required Function() onTransmission,
  required Function() onLogin,
  required Function() onLogout,
}) {
  final user = Provider.of<UserModel>(context, listen: false);
  return AppBar(
    leading: Builder(
      builder: (context) {
        return Center(
          child: IconButton(
            onPressed: () {
              Scaffold.of(context).openDrawer();
            },
            icon: Hero(tag: 'menu_back', child: const Icon(Icons.menu)),
          ),
        );
      },
    ),
    backgroundColor: Theme.of(context).colorScheme.primary,
    title: Text(S.current.app_name),
    actions: [
      Tooltip(
        message: S.current.refresh,
        child: IconButton(onPressed: onRefresh, icon: Icon(Icons.refresh)),
      ),
      Tooltip(
        message: S.current.transmission,
        child: IconButton(
          onPressed: onTransmission,
          icon: Icon(Icons.swap_vert),
        ),
      ),
      Tooltip(
        message: user.user == null ? S.current.login : user.user!.nick,
        child: IconButton(
          onPressed: user.user == null ? onLogin : onLogout,
          icon: Hero(
            tag: 'login',
            child: Icon(user.user == null ? Icons.person : Icons.logout),
          ),
        ),
      ),
    ],
  );
}
