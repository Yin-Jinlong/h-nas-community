part of 'home.dart';

AppBar _appBar(BuildContext context, {required onRefresh, required onLogin}) {
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
        message: S.current.login,
        child: IconButton(
          onPressed: onLogin,
          icon: Hero(tag: 'login', child: const Icon(Icons.person)),
        ),
      ),
    ],
  );
}
