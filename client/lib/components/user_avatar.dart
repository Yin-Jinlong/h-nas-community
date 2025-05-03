import 'package:cached_network_image/cached_network_image.dart';
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
    final user = widget.user;
    return LayoutBuilder(
      builder: (context, constraints) {
        return ClipOval(
          child: CachedNetworkImage(
            imageUrl:user != null ? FileAPIURL.userAvatar(user.uid) : '',
            width: constraints.biggest.width,
            height: constraints.biggest.height,
            fit: BoxFit.cover,
            errorWidget: (context, url, error) {
              final size = constraints.biggest.shortestSide * 0.8;
              return Icon(Icons.person, size: size, color: Colors.white);
            },
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return ConstrainedBox(
      constraints: BoxConstraints(
        maxWidth: widget.size,
        maxHeight: widget.size,
      ),
      child: SizedBox.square(
        dimension: widget.size,
        child: CircleAvatar(
          child:
              widget.withHero
                  ? Hero(tag: 'avatar', child: _avatar())
                  : _avatar(),
        ),
      ),
    );
  }
}
