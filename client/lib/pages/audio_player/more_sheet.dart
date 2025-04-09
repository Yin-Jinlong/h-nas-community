import 'package:flutter/material.dart';
import 'package:h_nas/media/media_player.dart';

import '../../global.dart';

class MoreSheet extends StatefulWidget {
  const MoreSheet({super.key});

  @override
  State createState() => _MoreSheetState();
}

class _MoreSheetState extends State<MoreSheet> {
  late final MediaPlayer player;

  @override
  void initState() {
    super.initState();
    player = Global.player;
    player.volume.addListener(_render);
  }

  _render() {
    setState(() {});
  }

  Widget _volume() {
    return Row(
      children: [
        Icon(Icons.volume_down),
        Expanded(
          child: Slider(
            value: player.volume.value,
            label: '${player.volume.value.toInt()}%',
            max: 100,
            divisions: 14,
            inactiveColor: Colors.grey.withValues(alpha: 0.5),
            onChanged: (value) {
              player.setVolume(value);
            },
          ),
        ),
        Icon(Icons.volume_up),
      ],
    );
  }

  @override
  void dispose() {
    player.volume.removeListener(_render);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: double.infinity,
      child: Padding(
        padding: EdgeInsets.all(8),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [_volume()],
        ),
      ),
    );
  }
}
