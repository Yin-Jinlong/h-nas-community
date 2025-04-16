part of 'home.dart';

_showAboutDialog(BuildContext context) {
  showAdaptiveDialog<void>(
    context: context,
    builder: (BuildContext context) {
      return AboutDialog.adaptive(
        applicationName: Global.packageInfo.appName,
        applicationVersion: Global.packageInfo.version,
        applicationLegalese: Global.copyright,
      );
    },
  ).then((v) {
    S.load(Global.locale);
  });
}

Drawer _drawer(
  BuildContext context, {
  required VoidCallback onLogin,
  required VoidCallback onLogout,
}) {
  final user = Provider.of<UserModel>(context, listen: false);
  final taskCount =
      Global.uploadTasks.where((e) => !e.isDone).length +
      Global.downloadTasks.where((e) => !e.isDone).length;

  return Drawer(
    child: ListView(
      padding: EdgeInsets.zero,
      children: [
        DrawerHeader(
          decoration: BoxDecoration(
            color: Theme.of(context).colorScheme.secondary,
          ),
          child: IntrinsicHeight(
            child: Column(
              spacing: 8,
              children: [
                SizedBox.square(
                  dimension: 80,
                  child: CircleAvatar(
                    child: Container(
                      padding: const EdgeInsets.all(8),
                      child:
                          user.user == null
                              ? Hero(tag: 'login', child: Icon(Icons.person))
                              : Text(user.user!.nick),
                    ),
                  ),
                ),
                ElevatedButton(
                  onPressed: () {
                    if (user.user == null) {
                      onLogin();
                    } else {
                      onLogout();
                    }
                  },
                  child: Text(
                    user.user == null ? S.current.login : S.current.logout,
                  ),
                ),
              ],
            ),
          ),
        ),
        Tooltip(
          message: S.current.theme,
          child: ListTile(
            leading: Icon(TDTxNFIcons.nf_md_tshirt_crew),
            title: Text(S.current.theme),
            onTap: () {
              Navigator.of(
                navigatorKey.currentContext!,
              ).pushNamed(Routes.theme);
            },
          ),
        ),
        if (!UniversalPlatform.isWeb)
          Tooltip(
            message: S.current.transmission,
            child: ListTile(
              leading: Icon(Icons.swap_vert),
              title: Badge(
                label: Text('$taskCount'),
                isLabelVisible: taskCount > 0,
                child: Text(S.current.transmission),
              ),
              onTap: () {
                Navigator.of(
                  navigatorKey.currentContext!,
                ).pushNamed(Routes.transmission);
              },
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
          message: S.current.about,
          child: ListTile(
            leading: Icon(Icons.info),
            title: Text(S.current.about),
            onTap: () {
              _showAboutDialog(context);
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
