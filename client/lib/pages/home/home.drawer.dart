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
  final taskCount =
      Global.uploadTasks.where((e) => !e.isDone).length +
      Global.downloadTasks.where((e) => !e.isDone).length;

  return Drawer(
    child: ListView(
      padding: EdgeInsets.zero,
      children: [
        _DrawerHeader(onLogin: onLogin, onLogout: onLogout),
        Tooltip(
          message: S.current.theme,
          child: ListTile(
            leading: Icon(TDTxNFIcons.nf_md_tshirt_crew),
            title: Text(S.current.theme),
            onTap: () {
              navigatorKey.currentState?.pushNamed(Routes.theme);
            },
          ),
        ),
        if (UserS.adminMode)
          Tooltip(
            message: S.current.server_info,
            child: ListTile(
              title: Text(S.current.server_info),
              leading: Icon(Icons.admin_panel_settings),
              onTap: () {
                navigatorKey.currentState?.pushNamed(Routes.serverInfo);
              },
            ),
          ),
        if (UserS.adminMode)
          Tooltip(
            message: S.current.user_management,
            child: ListTile(
              title: Text(S.current.user_management),
              leading: Icon(Icons.people),
              onTap: () {
                navigatorKey.currentState?.pushNamed(Routes.userManagement);
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
                navigatorKey.currentState?.pushNamed(Routes.transmission);
              },
            ),
          ),
        Tooltip(
          message: S.current.language,
          child: ListTile(
            leading: Icon(Icons.language),
            title: Text(S.current.language),
            onTap: () {
              navigatorKey.currentState?.pushNamed(Routes.languages);
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
              navigatorKey.currentState?.pushNamed(Routes.settings);
            },
          ),
        ),
      ],
    ),
  );
}

class _DrawerHeader extends StatefulWidget {
  final VoidCallback onLogin, onLogout;

  const _DrawerHeader({required this.onLogin, required this.onLogout});

  @override
  State createState() => _DrawerHeaderState();
}

class _DrawerHeaderState extends State<_DrawerHeader> {
  @override
  void initState() {
    super.initState();
    UserS.addUserListener(_render);
  }

  void _render() {
    setState(() {});
  }

  Widget _login() {
    return Column(
      spacing: 8,
      children: [
        UserAvatar(user: UserS.user?.uid, withHero: true),
        ElevatedButton(onPressed: widget.onLogin, child: Text(S.current.login)),
      ],
    );
  }

  Widget _copyIcon(BuildContext context) {
    return Icon(
      Icons.copy,
      size: 14,
      color: ColorScheme.of(context).onSecondary.withValues(alpha: 0.2),
    );
  }

  Widget _info(BuildContext context, UserInfo user) {
    return Stack(
      children: [
        Align(
          alignment: Alignment.centerLeft,
          child: Clickable(
            onTap: () {
              navigatorKey.currentState?.pushNamed(Routes.my);
            },
            child: Row(
              spacing: 12,
              children: [
                Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    UserAvatar(user: UserS.user?.uid, withHero: true),
                    Text(user.nick),
                  ],
                ),
                Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    InkWell(
                      onTap: () {
                        Clipboard.setData(ClipboardData(text: user.username));
                        Toast.showSuccess(
                          S.current.action_success(S.current.copy),
                        );
                      },
                      child: Row(
                        spacing: 4,
                        mainAxisSize: MainAxisSize.min,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          Text(
                            user.username,
                            style: TextTheme.of(context).titleLarge?.copyWith(
                              color: ColorScheme.of(context).onSecondary,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          _copyIcon(context),
                        ],
                      ),
                    ),
                    Row(
                      spacing: 4,
                      children: [
                        Text(
                          'ID:',
                          style: TextStyle(
                            color: ColorScheme.of(
                              context,
                            ).onSecondary.withValues(alpha: 0.6),
                          ),
                        ),
                        InkWell(
                          onTap: () {
                            Clipboard.setData(
                              ClipboardData(text: user.uid.toString()),
                            );
                            Toast.showSuccess(
                              S.current.action_success(S.current.copy),
                            );
                          },
                          child: Text(
                            user.uid.toString(),
                            style: TextStyle(
                              color: ColorScheme.of(
                                context,
                              ).onSecondary.withValues(alpha: 0.6),
                            ),
                          ),
                        ),
                        _copyIcon(context),
                      ],
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
        Align(
          alignment: Alignment.centerRight,
          child: Icon(Icons.keyboard_arrow_right),
        ),
        Align(
          alignment: Alignment.topRight,
          child: IconButton(
            tooltip: S.current.logout,
            onPressed: widget.onLogout,
            icon: Icon(Icons.logout),
          ),
        ),
        if (UniversalPlatform.isAndroid)
          Align(
            alignment: Alignment.bottomRight,
            child: IconButton(
              onPressed: () {
                navigatorKey.currentState?.pushNamed(Routes.qrScan);
              },
              icon: Icon(Icons.qr_code_scanner),
            ),
          ),
      ],
    );
  }

  @override
  void dispose() {
    UserS.removeUserListener(_render);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return DrawerHeader(
      decoration: BoxDecoration(color: Theme.of(context).colorScheme.secondary),
      child: UserS.user == null ? _login() : _info(context, UserS.user!),
    );
  }
}
