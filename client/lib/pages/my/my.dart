import 'dart:io';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:h_nas/components/user_avatar.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/main.dart';
import 'package:h_nas/pages/my/nick_dialog.dart';
import 'package:h_nas/settings/user.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/dispose.dart';
import 'package:h_nas/utils/toast.dart';
import 'package:image_picker/image_picker.dart';

class MyPage extends StatefulWidget {
  const MyPage({super.key});

  @override
  State createState() => _MyPageState();
}

class _MyPageState extends State<MyPage> {
  var _avatarKey = UniqueKey();

  @override
  void initState() {
    super.initState();
    UserS.update().then((value) {
      if (disposed) return;
      setState(() {});
    });
  }

  void _showNickDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) {
        return NickDialog(
          onNick: (nick) {
            if (nick.isEmpty) {
              Toast.showError(S.current.error_empty(S.current.info_nick));
            } else {
              UserAPI.setNick(nick).then((value) {
                if (value) {
                  navigatorKey.currentState?.pop();
                  UserS.update().then((value) {
                    if (disposed) return;
                    setState(() {});
                  });
                }
              });
            }
          },
        );
      },
    );
  }

  void _setAvatar(File file) {
    FileAPI.setAvatar(file).then((value) {
      if (value == null || disposed) return;
      final user = UserS.user;
      if (user != null) {
        UserS.user = UserInfo(
          uid: user.uid,
          username: user.username,
          nick: user.nick,
          admin: user.admin,
        );
        CachedNetworkImage.evictFromCache(FileAPIURL.userAvatar(user.uid)).then(
          (value) {
            if (disposed) return;
            _avatarKey = UniqueKey();
            setState(() {});
          },
        );
      }
    });
  }

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
                    onTap: () {
                      ImagePicker().pickImage(source: ImageSource.gallery).then(
                        (value) {
                          if (value == null) return;
                          final file = File(value.path);
                          _setAvatar(file);
                        },
                      );
                    },
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
                            UserAvatar(
                              key: _avatarKey,
                              user: user,
                              withHero: true,
                            ),
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
                    onTap: () {
                      _showNickDialog(context);
                    },
                  ),
                ],
              ).toList(),
        ),
      ),
    );
  }
}
