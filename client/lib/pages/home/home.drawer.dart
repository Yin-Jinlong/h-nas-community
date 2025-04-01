part of 'home.dart';

Drawer _drawer(BuildContext context) {
  return Drawer(
    child: ListView(
      padding: EdgeInsets.zero,
      children: [
        DrawerHeader(
          decoration: BoxDecoration(
            color: Theme.of(context).colorScheme.secondary,
          ),
          child: Center(
            child: SizedBox(
              width: 80,
              height: 80,
              child: CircleAvatar(
                child: Container(
                  padding: const EdgeInsets.all(8),
                  child: Text(S.current.app_name),
                ),
              ),
            ),
          ),
        ),
        Tooltip(
          message: S.current.language,
          child: ListTile(
            leading: Icon(Icons.language),
            title: Text(S.current.language),
            onTap: () {
              Navigator.of(
                navigatorKey.currentContext!,
              ).pushNamed(Routes.languages);
            },
          ),
        ),
        Tooltip(
          message: S.current.settings,
          child: ListTile(
            leading: Icon(Icons.settings),
            title: Text(S.current.settings),
            onTap: () {
              Navigator.of(
                navigatorKey.currentContext!,
              ).pushNamed(Routes.settings);
            },
          ),
        ),
      ],
    ),
  );
}
