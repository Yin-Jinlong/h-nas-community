import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:h_nas/api/api.dart';

class UserAvatar extends StatefulWidget {
  final int? user;
  final Color? backgroundColor, iconColor;
  final double size;
  final bool withHero;

  const UserAvatar({
    super.key,
    required this.user,
    this.size = 80,
    this.withHero = false,
    this.backgroundColor,
    this.iconColor,
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
            imageUrl: user != null ? FileAPIURL.userAvatar(user) : '',
            width: constraints.biggest.width,
            height: constraints.biggest.height,
            fit: BoxFit.cover,
            errorWidget: (context, url, error) {
              final size = constraints.biggest.shortestSide * 0.8;
              return Icon(
                Icons.person,
                size: size,
                color: widget.iconColor ?? ColorScheme.of(context).onPrimary,
              );
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
          backgroundColor: widget.backgroundColor,
          child:
              widget.withHero
                  ? Hero(tag: 'avatar', child: _avatar())
                  : _avatar(),
        ),
      ),
    );
  }
}
