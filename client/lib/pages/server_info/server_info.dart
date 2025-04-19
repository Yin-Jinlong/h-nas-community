import 'package:flutter/material.dart';
import 'package:h_nas/generated/l10n.dart';

class ServerInfoPage extends StatefulWidget {
  const ServerInfoPage({super.key});

  @override
  State<ServerInfoPage> createState() => _UserManagementPageState();
}

class _UserManagementPageState extends State<ServerInfoPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(appBar: AppBar(title: Text(S.current.server_info)));
  }
}
