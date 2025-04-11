import 'package:flutter/material.dart';
import 'package:h_nas/media/media_file.dart';
import 'package:h_nas/media/media_player.dart';

import '../../global.dart';

class PLayListSheet extends StatefulWidget {
  const PLayListSheet({super.key});

  @override
  State createState() => _MoreSheetState();
}

class _MoreSheetState extends State<PLayListSheet> {
  late final MediaPlayer player;

  @override
  void initState() {
    super.initState();
    player = Global.player;
  }

  _render() {
    setState(() {});
  }

  ListTile _item(BuildContext context, int num, MediaFile file) {
    if (file.audioInfo == null) {
      file.loadInfo().then((v) {
        setState(() {});
      });
    }
    return ListTile(
      leading: Text('$num'),
      title: Text(
        file.audioInfo?.title ?? '?',
        style: TextTheme.of(
          context,
        ).titleMedium?.copyWith(fontWeight: FontWeight.bold),
      ),
      subtitle: Text(file.audioInfo?.artists ?? '?'),
      onTap: () {
        player.jump(player.playList.value.indexOf(file));
        Navigator.of(context).pop();
      },
    );
  }

  @override
  void dispose() {
    player.volume.removeListener(_render);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final list = player.playList.value;
    return SizedBox(
      width: double.infinity,
      child: Padding(
        padding: EdgeInsets.all(8),
        child: ListView(
          children: [
            for (var i = 0; i < list.length; i++)
              _item(context, i + 1, list[i]),
          ],
        ),
      ),
    );
  }
}
