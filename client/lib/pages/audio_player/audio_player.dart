import 'dart:ui';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:h_nas/components/record_view.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/media/media_player.dart';
import 'package:h_nas/utils/time_utils.dart';

import '../../generated/l10n.dart';
import '../../utils/api.dart';

class AudioPlayerPage extends StatefulWidget {
  const AudioPlayerPage({super.key});

  @override
  State<AudioPlayerPage> createState() => _AudioPlayerPageState();
}

class _AudioPlayerPageState extends State<AudioPlayerPage>
    with TickerProviderStateMixin {
  late final AnimationController _playPauseController;
  late final MediaPlayer player;

  @override
  void initState() {
    super.initState();

    player = Global.player;

    _playPauseController = AnimationController(
      value: Global.player.playing ? 1 : 0,
      vsync: this,
      duration: durationFast,
    );

    player.position.addListener(_render);

    player.playState.addListener(_onPlay);
  }

  _render() {
    setState(() {});
  }

  _onPlay() {
    setState(() {
      if (player.playing) {
        _playPauseController.forward();
      } else {
        _playPauseController.reverse();
      }
    });
  }

  @override
  void dispose() {
    player.position.removeListener(_render);
    player.playState.removeListener(_onPlay);

    _playPauseController.dispose();

    super.dispose();
  }

  List<Widget> _controllers() {
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

  Widget _cover() {
    return Hero(
      tag: 'audio_cover',
      child: RecordView(
        rotate: player.playing,
        size: MediaQuery.of(context).size.width * 0.5,
        child: CachedNetworkImage(
          imageUrl: FileAPIURL.publicAudioCover(player.audioInfo.value!.path),
          fit: BoxFit.cover,
          errorWidget: (context, error, stackTrace) {
            return Icon(Icons.broken_image);
          },
        ),
      ),
    );
  }

  Widget _info(BuildContext context) {
    final info = player.audioInfo.value;
    return Padding(
      padding: EdgeInsets.symmetric(horizontal: 12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Text(
            info?.title ?? '?',
            style: TextTheme.of(
              context,
            ).headlineSmall?.copyWith(color: Colors.white),
          ),
          Text(
            info?.artists ?? '?',
            style: TextTheme.of(
              context,
            ).bodyLarge?.copyWith(color: Colors.white),
          ),
        ],
      ),
    );
  }

  Widget _progressInfo() {
    return Row(
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
                Duration(seconds: (player.duration.value * value).toInt()),
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
    );
  }

  Widget _content(BuildContext context) {
    return Column(
      children: [
        Row(children: [BackButton()]),
        Expanded(child: Center(child: _cover())),
        _info(context),
        _progressInfo(),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: _controllers(),
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    final player = Global.player;

    return Scaffold(
      body: Stack(
        children: [
          CachedNetworkImage(
            imageUrl:
                player.audioInfo.value != null
                    ? FileAPIURL.publicAudioCover(player.audioInfo.value!.path)
                    : '',
            fit: BoxFit.cover,
            width: double.infinity,
            height: double.infinity,
            errorWidget: (context, error, stackTrace) {
              return Container();
            },
          ),
          BackdropFilter(
            filter: ImageFilter.blur(sigmaX: 50, sigmaY: 50),
            child: Container(color: Colors.black.withValues(alpha: 0.3)),
          ),
          IconTheme(
            data: IconThemeData(
              color:
                  HSVColor.fromColor(
                    ColorScheme.of(context).primary,
                  ).withSaturation(0.1).withValue(1).toColor(),
            ),
            child: DefaultTextStyle(
              style: TextStyle(color: Colors.white),
              child: Padding(
                padding: EdgeInsets.all(8),
                child: _content(context),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
