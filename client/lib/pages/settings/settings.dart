import 'package:flutter/material.dart';
import 'package:h_nas/api/api.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/pages/settings/scan_dialog.dart';
import 'package:h_nas/prefs.dart';
import 'package:h_nas/routes.dart';
import 'package:h_nas/settings/user.dart';
import 'package:tdtx_nf_icons/tdtx_nf_icons.dart';
import 'package:universal_platform/universal_platform.dart';

part 'settings.api_host.dart';
part 'settings.app_bar.dart';

class SettingsPage extends StatefulWidget {
  const SettingsPage({super.key});

  @override
  State createState() {
    return _SettingsPageState();
  }
}

class _SettingsPageState extends State<SettingsPage> {
  void _onAdminMode(bool value) {
    if (value) {
      UserS.enableAdminMode();
    } else {
      UserS.disableAdminMode();
    }
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _appBar(context),
      body: ListView(
        children: [
          if (UserS.user?.admin ?? false)
            ListTile(
              title: Text(L.current.admin_mode),
              leading: const Icon(Icons.admin_panel_settings),
              trailing: Switch(value: UserS.adminMode, onChanged: _onAdminMode),
              onTap: () {
                _onAdminMode(!UserS.adminMode);
              },
            ),
          ListTile(
            leading: const Icon(Icons.language),
            trailing: const Icon(Icons.keyboard_arrow_right),
            title: Text(L.current.language),
            subtitle: Text(L.current.localName),
            onTap: () {
              navigatorKey.currentState?.pushNamed(Routes.languages).then((v) {
                setState(() {});
              });
            },
          ),
          if (!UniversalPlatform.isWeb)
            ListTile(
              title: Text(L.current.storage),
              leading: const Icon(Icons.storage),
              onTap: () {
                Navigator.of(context).pushNamed(Routes.storage);
              },
              trailing: const Icon(Icons.keyboard_arrow_right),
            ),
          ListTile(
            title: Text(L.current.server_addr),
            subtitle: Text('${L.current.now}${API.API_ROOT}'),
            onTap: () async {
              await _showHostDialog(context);
              setState(() {});
            },
          ),
        ],
      ),
    );
  }
}

_showHostDialog(BuildContext context) async {
  var host = Prefs.getString(Prefs.keyApiHost) ?? '';
  await showGeneralDialog(
    context: context,
    pageBuilder: (context, animation, secondaryAnimation) {
      return _ApiHostDialog(host: host);
    },
  );
}
