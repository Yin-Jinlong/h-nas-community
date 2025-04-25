import 'package:flutter/material.dart';
import 'package:h_nas/utils/api.dart';

class UserAvatar extends StatefulWidget {
  final UserInfo? user;
  final double size;
  final bool withHero;

  const UserAvatar({
    super.key,
    required this.user,
    this.size = 80,
    this.withHero = false,
  });

  @override
  State createState() => _UserAvatarState();
}

class _UserAvatarState extends State<UserAvatar> {
  Widget _avatar() {
    return Icon(Icons.person, size: widget.size * 0.6, color: Colors.white);
  }

  @override
  Widget build(BuildContext context) {
    return SizedBox.square(
      dimension: widget.size,
      child: CircleAvatar(
        child: Container(
          padding: const EdgeInsets.all(8),
          child:
              widget.withHero
                  ? Hero(tag: 'avatar', child: _avatar())
                  : _avatar(),
        ),
      ),
    );
  }
}
