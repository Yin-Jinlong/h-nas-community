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
    var playingThis = player.nowPlay.value == file;
    return ListTile(
      leading:
          playingThis
              ? Icon(player.playing ? Icons.pause_circle : Icons.play_circle)
              : Text('$num', style: TextStyle().copyWith(fontSize: 20)),
      title: Text(
        file.audioInfo?.title ?? '?',
        style: TextStyle().copyWith(fontWeight: FontWeight.bold),
      ),
      subtitle: Text(file.audioInfo?.artists ?? '?'),
      selected: playingThis,
      onTap: () {
        if (player.nowPlay.value == file) {
          player.playPause().then((value) {
            setState(() {});
          });
        } else {
          player.jump(player.playList.value.indexOf(file));
          Navigator.of(context).pop();
        }
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
