import 'package:flutter/material.dart';
import 'package:h_nas/components/user_avatar.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/main.dart';
import 'package:h_nas/settings/user.dart';

class MyPage extends StatefulWidget {
  const MyPage({super.key});

  @override
  State createState() => _MyPageState();
}

class _MyPageState extends State<MyPage> {
  @override
  Widget build(BuildContext context) {
    final user = UserS.user;
    if (user == null) {
      navigatorKey.currentState?.pop();
      return Container();
    }

    return Scaffold(
      appBar: AppBar(
        title: Text(S.current.my),
        actions: [
          IconButton(
            tooltip: S.current.edit,
            onPressed: () {},
            icon: Icon(Icons.edit_note),
          ),
        ],
      ),
      body: SingleChildScrollView(
        child: SizedBox(
          width: double.infinity,
          child: Padding(
            padding: EdgeInsets.only(top: 12),
            child: Column(
              spacing: 12,
              children: [
                UserAvatar(user: UserS.user),
                Text(user.username),
                Text(user.nick),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [Text('ID:'), Text(user.uid.toString())],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
