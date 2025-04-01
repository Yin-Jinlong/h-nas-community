import 'package:flutter/material.dart';
import 'package:flutter_settings_screens/flutter_settings_screens.dart';

import '../../generated/l10n.dart';
import '../../prefs.dart';
import '../../routes.dart';
import '../../utils/api.dart';

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
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _appBar(context),
      body: SettingsScreen(
        hasAppBar: false,
        children: [
          ListTile(
            leading: const Icon(Icons.language),
            trailing: const Icon(Icons.arrow_forward_ios),
            title: Text(S.current.language),
            onTap: () {
              Navigator.of(context).pushNamed(Routes.languages);
            },
          ),
          ListTile(
            title: Text(S.current.server_addr),
            subtitle: Text('${S.current.now}${API.API_ROOT}'),
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
