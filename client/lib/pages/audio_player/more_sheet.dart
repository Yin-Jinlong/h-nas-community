import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:h_nas/media/media_player.dart';
import 'package:h_nas/utils/api.dart';
import 'package:syncfusion_flutter_sliders/sliders.dart';

import '../../global.dart';

class MoreSheet extends StatefulWidget {
  final AudioFileInfo info;

  const MoreSheet({super.key, required this.info});

  @override
  State createState() => _MoreSheetState();
}

class _MoreSheetState extends State<MoreSheet> {
  late final MediaPlayer player;
  final List<double> speeds = [0.5, 0.75, 1, 1.25, 1.5, 2, 3];
  int speedIndex = 2;

  @override
  void initState() {
    super.initState();
    player = Global.player;
    player.volume.addListener(_render);

    var d = speeds.last;
    var di = 2;
    final speed = player.speed.value;
    for (var i = 0; i < speeds.length; i++) {
      var v = speeds[i];
      var nd = (v - speed).abs();
      if (nd < d) {
        d = nd;
        di = i;
      }
    }
    speedIndex = di;
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

  Widget _speed() {
    return Row(
      children: [
        Icon(Icons.speed),
        Expanded(
          child: SfSlider(
            value: speedIndex,
            interval: 1,
            min: 0.0,
            max: speeds.length - 1.0,
            showDividers: true,
            showLabels: true,
            inactiveColor: Colors.grey.withValues(alpha: 0.5),
            labelFormatterCallback:
                (actualValue, formattedText) =>
                    '${speeds[(actualValue as double).round()]}',
            onChanged: (value) {
              setState(() {
                speedIndex = (value as double).round();
                player.setSpeed(speeds[speedIndex]);
              });
            },
          ),
        ),
        Icon(Icons.speed),
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
          children: [_info(context), _volume(), _speed()],
        ),
      ),
    );
  }
}
