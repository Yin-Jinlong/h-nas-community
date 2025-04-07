import 'package:flutter/material.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/media/media_player.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/time_utils.dart';

class MiniAudioPlayer extends StatefulWidget {
  const MiniAudioPlayer({super.key});

  @override
  State createState() => _MiniAudioPlayerState();
}

class _MiniAudioPlayerState extends State<MiniAudioPlayer>
    with TickerProviderStateMixin {
  late final AnimationController _playPauseController;
  late final AnimationController _coverController;
  late final MediaPlayer player;

  Widget? cover;
  AudioFileInfo? _lastInfo;

  @override
  void initState() {
    super.initState();
    player = Global.player;
    _playPauseController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 300),
    );
    _coverController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 10),
    );
    player.audioInfo.addListener(() {
      setState(() {});
    });
    player.playState.addListener(() {
      setState(() {
        _onPlay();
      });
    });
    player.position.addListener(() {
      setState(() {});
    });

    _onPlay();
  }

  _onPlay() {
    if (player.audioInfo.value != _lastInfo) {
      cover = ClipOval(
        child: Image.network(
          FileAPIURL.publicAudioCover(player.audioInfo.value!.path),
          width: 40,
          height: 40,
          fit: BoxFit.cover,
          errorBuilder: (context, error, stackTrace) {
            return Icon(Icons.broken_image);
          },
        ),
      );
    }
    if (player.playing) {
      _playPauseController.forward();
      _coverController.repeat();
    } else {
      _playPauseController.reverse();
      _coverController.stop();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 5,
      child: Padding(
        padding: EdgeInsets.all(6),
        child: Row(
          children: [
            player.audioInfo.value == null || cover == null
                ? Icon(Icons.image)
                : Padding(
                  padding: EdgeInsets.symmetric(horizontal: 10),
                  child: Transform.scale(
                    scale: 1.5,
                    origin: Offset(0, 20),
                    child: RotationTransition(
                      turns: _coverController,
                      child: cover!,
                    ),
                  ),
                ),
            Text(player.audioInfo.value?.title ?? '?'),
            IconButton(
              tooltip: player.playing ? S.current.pause : S.current.media_play,
              onPressed: () {
                player.playPause();
              },
              icon: AnimatedIcon(
                icon: AnimatedIcons.play_pause,
                progress: _playPauseController,
              ),
            ),
            Padding(
              padding: EdgeInsets.all(4),
              child: Text(player.position.value.shortTimeStr),
            ),
            IntrinsicWidth(
              child: Padding(
                padding: EdgeInsets.all(4),
                child: ConstrainedBox(
                  constraints: BoxConstraints(minWidth: 200),
                  child: LinearProgressIndicator(value: player.progress),
                ),
              ),
            ),
            Text(player.duration.value.shortTimeStr),
          ],
        ),
      ),
    );
  }
}
