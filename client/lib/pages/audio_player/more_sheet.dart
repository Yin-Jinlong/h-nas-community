import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:h_nas/media/media_player.dart';
import 'package:h_nas/utils/api.dart';

import '../../global.dart';

class MoreSheet extends StatefulWidget {
  final AudioFileInfo info;

  const MoreSheet({super.key, required this.info});

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

  Widget _info(BuildContext context) {
    return IntrinsicHeight(
      child: Padding(
        padding: EdgeInsets.all(12),
        child: Row(
          children: [
            ClipRRect(
              borderRadius: BorderRadius.all(Radius.circular(10)),
              child: CachedNetworkImage(
                imageUrl: FileAPIURL.publicAudioCover(widget.info.path),
                fit: BoxFit.cover,
                width: 80,
                height: 80,
              ),
            ),
            Expanded(
              child: Padding(
                padding: EdgeInsets.symmetric(horizontal: 12),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    Text(
                      widget.info.title ?? '?',
                      style: TextTheme.of(
                        context,
                      ).titleLarge?.copyWith(fontWeight: FontWeight.bold),
                    ),
                    Text(
                      '${widget.info.artists ?? '?'} - 《${widget.info.album ?? '?'}》',
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
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
          children: [_info(context), _volume()],
        ),
      ),
    );
  }
}
