import 'package:flutter/material.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/media/media_player.dart';

class MoreDrawer extends StatefulWidget {
  const MoreDrawer({super.key});

  @override
  State createState() => _MoreDrawerState();
}

class _MoreDrawerState extends State<MoreDrawer> {
  late final MediaPlayer player;

  @override
  void initState() {
    super.initState();
    player = Global.player;
    player.codec.addListener(_render);
  }

  void _render() {
    setState(() {});
  }

  @override
  void dispose() {
    player.codec.removeListener(_render);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Drawer(
      child: SafeArea(
        child: Column(
          children: [
            Text(S.current.video_codec),
            for (var codec in player.codecs.value)
              Row(
                children: [
                  Radio(
                    value: codec,
                    groupValue: player.codec.value,
                    onChanged: (value) {
                      player.codec.value = value as String;
                    },
                  ),
                  Text(codec),
                ],
              ),
            Divider(),
          ],
        ),
      ),
    );
  }
}
