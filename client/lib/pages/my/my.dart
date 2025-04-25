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

    Widget trailing(Widget child) {
      return IntrinsicWidth(
        child: Row(children: [child, Icon(Icons.keyboard_arrow_right)]),
      );
    }

    return Scaffold(
      appBar: AppBar(title: Text(S.current.my)),
      body: Padding(
        padding: EdgeInsets.only(top: 12),
        child: ListView(
          children:
              ListTile.divideTiles(
                context: context,
                color: Colors.grey,
                tiles: [
                  InkWell(
                    onTap: () {},
                    child: Padding(
                      padding: EdgeInsets.symmetric(
                        horizontal: 16,
                        vertical: 6,
                      ),
                      child: Row(
                        children: [
                          Text(S.current.avatar),
                          Expanded(child: Container()),
                          trailing(
                            UserAvatar(user: UserS.user, withHero: true),
                          ),
                        ],
                      ),
                    ),
                  ),
                  ListTile(
                    title: Text('ID'),
                    trailing: Text(
                      user.uid.toString(),
                      style: TextTheme.of(context).bodyLarge,
                    ),
                  ),
                  ListTile(
                    title: Text(S.current.info_username),
                    trailing: trailing(
                      Text(
                        user.username,
                        style: TextTheme.of(context).bodyLarge,
                      ),
                    ),
                    onTap: () {},
                  ),
                  ListTile(
                    title: Text(S.current.info_nick),
                    trailing: trailing(
                      Text(user.nick, style: TextTheme.of(context).bodyLarge),
                    ),
                    onTap: () {},
                  ),
                ],
              ).toList(),
        ),
      ),
    );
  }
}
