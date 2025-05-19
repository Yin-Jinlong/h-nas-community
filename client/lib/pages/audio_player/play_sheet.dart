import 'package:flutter/material.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/main.dart';
import 'package:h_nas/media/media_file.dart';
import 'package:h_nas/media/media_player.dart';
import 'package:h_nas/utils/audio_info_exts.dart';

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
    player.nowPlay.addListener(_render);
    player.playState.addListener(_render);
  }

  _render() {
    setState(() {});
  }

  ListTile _item(BuildContext context, int num, MediaFile file) {
    var playingThis = player.nowPlay.value == file;
    return ListTile(
      leading:
          playingThis
              ? Icon(player.playing ? Icons.pause_circle : Icons.play_circle)
              : Text('$num', style: TextStyle().copyWith(fontSize: 20)),
      title: Text(
        file.audioInfo?.userTitle ?? '?',
        style: TextStyle().copyWith(fontWeight: FontWeight.bold),
      ),
      subtitle: Text(file.audioInfo?.artistAlbum ?? '?'),
      selected: playingThis,
      onTap: () {
        if (player.nowPlay.value == file) {
          player.playPause();
        } else {
          player.jump(player.playList.value.indexOf(file));
          navigatorKey.currentState?.pop();
        }
      },
    );
  }

  @override
  void dispose() {
    player.nowPlay.removeListener(_render);
    player.playState.removeListener(_render);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final list = player.playList.value;
    return DraggableScrollableSheet(
      initialChildSize: 0.5,
      minChildSize: 0.3,
      maxChildSize: 0.9,
      expand: false,
      builder: (context, scrollController) {
        return SizedBox(
          width: double.infinity,
          child: Padding(
            padding: EdgeInsets.all(8),
            child: Column(
              children: [
                Text(S.current.playlist),
                Expanded(
                  child: ListView(
                    controller: scrollController,
                    children: [
                      for (var i = 0; i < list.length; i++)
                        _item(context, i + 1, list[i]),
                    ],
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}
