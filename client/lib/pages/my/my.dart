import 'dart:io';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:h_nas/api/api.dart';
import 'package:h_nas/components/user_avatar.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/main.dart';
import 'package:h_nas/pages/my/nick_dialog.dart';
import 'package:h_nas/settings/user.dart';
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

  void _updateAvatar() async {
    final user = UserS.user;
    if (user != null) {
      await CachedNetworkImage.evictFromCache(FileAPIURL.userAvatar(user.uid));
      await CachedNetworkImage.evictFromCache(
        FileAPIURL.userAvatar(user.uid, raw: true),
      );
      if (disposed) return;
      _avatarKey = UniqueKey();
      navigatorKey.currentState?.pop();
      setState(() {});
    }
  }

  void _setAvatar(File file) {
    FileAPI.setAvatar(file).then((value) {
      if (disposed) return;
      _updateAvatar();
    });
  }

  Future<bool> _deleteAvatar() async {
    return await FileAPI.deleteAvatar();
  }

  Widget _dialogItem(List<Widget> children, VoidCallback onTap) {
    return InkWell(
      onTap: onTap,
      child: Padding(
        padding: EdgeInsets.all(8),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: children,
        ),
      ),
    );
  }

  void _showAvatarPreviewDialog(BuildContext context) {
    final uid = UserS.user?.uid;
    if (uid == null) return;
    showDialog(
      context: context,
      builder: (context) {
        return SafeArea(
          child: Stack(
            children: [
              Padding(
                padding: EdgeInsets.all(8),
                child: Center(
                  child: SizedBox.expand(
                    child: CachedNetworkImage(
                      imageUrl: FileAPIURL.userAvatar(uid, raw: true),
                      fit: BoxFit.contain,
                      errorWidget: (context, url, error) {
                        return Icon(Icons.person);
                      },
                    ),
                  ),
                ),
              ),
              Align(
                alignment: Alignment.topRight,
                child: IconButton(
                  onPressed: () {
                    navigatorKey.currentState?.pop();
                  },
                  icon: Icon(Icons.close, color: Colors.white70),
                ),
              ),
            ],
          ),
        );
      },
    );
  }

  void _showAvatarDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          content: IntrinsicHeight(
            child: DefaultTextStyle(
              style: TextTheme.of(context).bodyLarge ?? TextStyle(),
              child: Column(
                children: [
                  _dialogItem([Text(S.current.avatar_show)], () {
                    navigatorKey.currentState?.pop();
                    _showAvatarPreviewDialog(context);
                  }),
                  _dialogItem([Text(S.current.avatar_change)], () {
                    ImagePicker().pickImage(source: ImageSource.gallery).then((
                      value,
                    ) {
                      if (value == null) return;
                      final file = File(value.path);
                      _setAvatar(file);
                    });
                  }),
                  _dialogItem([Text(S.current.avatar_delete)], () {
                    _deleteAvatar().then((value) {
                      if (value) {
                        _updateAvatar();
                      }
                    });
                  }),
                  Divider(color: Colors.grey),
                  _dialogItem([Text(S.current.cancel)], () {
                    navigatorKey.currentState?.pop();
                  }),
                ],
              ),
            ),
          ),
        );
      },
    );
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
                      _showAvatarDialog(context);
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
                              user: user.uid,
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
