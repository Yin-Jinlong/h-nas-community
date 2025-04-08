import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/utils/time_utils.dart';

import '../../generated/l10n.dart';
import '../../utils/api.dart';

class AudioPlayerPage extends StatefulWidget {
  const AudioPlayerPage({super.key});

  @override
  State<AudioPlayerPage> createState() => _AudioPlayerPageState();
}

class _AudioPlayerPageState extends State<AudioPlayerPage>
    with SingleTickerProviderStateMixin {
  late final AnimationController _playPauseController;

  @override
  void initState() {
    super.initState();

    _playPauseController = AnimationController(
      value: Global.player.playing ? 1 : 0,
      vsync: this,
      duration: durationFast,
    );

    Global.player.position.addListener(_render);

    Global.player.playState.addListener(_onPlay);
  }

  _render() {
    setState(() {});
  }

  _onPlay() {
    setState(() {
      if (Global.player.playing) {
        _playPauseController.forward();
      } else {
        _playPauseController.reverse();
      }
    });
  }

  @override
  void dispose() {
    Global.player.position.removeListener(_render);
    Global.player.playState.removeListener(_onPlay);
    super.dispose();
  }

  List<Widget> _controllers() {
    final player = Global.player;
    return [
      Hero(
        tag: 'play_pause',
        child: IconButton(
          tooltip: player.playing ? S.current.pause : S.current.media_play,
          onPressed: () {
            player.playPause();
          },
          icon: AnimatedIcon(
            icon: AnimatedIcons.play_pause,
            progress: _playPauseController,
            size: 40,
          ),
        ),
      ),
    ];
  }

  @override
  Widget build(BuildContext context) {
    final player = Global.player;
    final audioInfo = player.audioInfo.value;

    return Scaffold(
      appBar: AppBar(
        title: Text(
          '${audioInfo?.title} - ${audioInfo?.album} - ${audioInfo?.artists}',
        ),
      ),
      body: Stack(
        children: [
          Column(
            children: [
              Hero(
                tag: 'audio_cover',
                child: ClipOval(
                  child: CachedNetworkImage(
                    imageUrl: FileAPIURL.publicAudioCover(
                      player.audioInfo.value!.path,
                    ),
                    width: 200,
                    height: 200,
                    fit: BoxFit.cover,
                    errorWidget: (context, error, stackTrace) {
                      return Icon(Icons.broken_image);
                    },
                  ),
                ),
              ),
              Row(
                children: [
                  Padding(
                    padding: EdgeInsets.only(left: 6),
                    child: Text(player.position.value.shortTimeStr),
                  ),
                  Expanded(
                    child: Slider(
                      value: player.progress ?? 0,
                      inactiveColor: Colors.grey.withValues(alpha: 0.3),
                      onChanged: (value) {
                        player.seek(
                          Duration(
                            seconds: (player.duration.value * value).toInt(),
                          ),
                        );
                        setState(() {});
                      },
                    ),
                  ),
                  Padding(
                    padding: EdgeInsets.only(right: 6),
                    child: Text(player.duration.value.shortTimeStr),
                  ),
                ],
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: _controllers(),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
