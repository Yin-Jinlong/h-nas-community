import 'dart:math';
import 'dart:ui';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:h_nas/components/cover_view.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/media/media_player.dart';
import 'package:h_nas/pages/audio_player/more_sheet.dart';
import 'package:h_nas/pages/audio_player/play_sheet.dart';
import 'package:h_nas/utils/lrc_utils.dart';
import 'package:h_nas/utils/time_utils.dart';
import 'package:lrc/lrc.dart';
import 'package:tdtx_nf_icons/tdtx_nf_icons.dart';
import 'package:wakelock_plus/wakelock_plus.dart';

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
  bool _changing = false;
  double _progress = 0;
  Lrc? lrc;

  @override
  void initState() {
    super.initState();

    player = Global.player;

    _progress = player.progress ?? 0;

    _playPauseController = AnimationController(
      value: Global.player.playing ? 1 : 0,
      vsync: this,
      duration: durationFast,
    );

    player.audioInfo.addListener(_newAudio);
    player.position.addListener(_render);
    player.buffer.addListener(_render);

    player.playState.addListener(_onPlay);

    _newAudio();
    WakelockPlus.enable();
  }

  _render() {
    if (!_changing) {
      _progress = player.progress ?? 0;
    }
    setState(() {});
  }

  _newAudio() {
    player.nowPlay.value?.loadInfo().then((v) {
      var lrcStr = v?.lrc;
      if (lrcStr?.isValidLrc == true) {
        lrc = lrcStr!.toLrc();
      }
      setState(() {});
    });
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
    player.audioInfo.removeListener(_newAudio);
    player.position.removeListener(_render);
    player.playState.removeListener(_onPlay);
    player.buffer.removeListener(_render);

    _playPauseController.dispose();

    WakelockPlus.disable();

    super.dispose();
  }

  Widget _playModeIcon(double size) {
    return switch (player.playMode.value) {
      PlayMode.none => Transform.translate(
        key: ValueKey(0),
        offset: Offset(0, -5),
        child: Icon(NFIconData(0x21c9), size: size),
      ),
      PlayMode.single => Icon(key: ValueKey(1), Icons.repeat_one, size: size),
      PlayMode.loop => Icon(key: ValueKey(2), Icons.repeat, size: size),
      PlayMode.random => Icon(key: ValueKey(3), Icons.shuffle, size: size),
    };
  }

  List<Widget> _controllers(BuildContext context) {
    const size = 40.0;

    return [
      IconButton(
        tooltip: player.playMode.value.name,
        onPressed: () {
          final index = PlayMode.values.indexOf(player.playMode.value);
          final v = PlayMode.values[(index + 1) % PlayMode.values.length];
          player.playMode.value = v;
          setState(() {});
        },
        icon: AnimatedSwitcher(
          duration: durationFast,
          transitionBuilder: (child, animation) {
            return ScaleTransition(
              scale: CurveTween(curve: Curves.easeInOut).animate(animation),
              child: child,
            );
          },
          child: _playModeIcon(size),
        ),
      ),
      IconButton(
        tooltip: S.current.audio_previous,
        onPressed: () {
          player.previous();
        },
        icon: Icon(Icons.skip_previous, size: size),
      ),
      Container(
        decoration: BoxDecoration(
          color: Colors.grey.withValues(alpha: 0.1),
          borderRadius: BorderRadius.circular(size),
        ),
        child: Hero(
          tag: 'play_pause',
          child: IconButton(
            tooltip: player.playing ? S.current.pause : S.current.media_play,
            onPressed: () {
              player.playPause();
            },
            icon: AnimatedIcon(
              icon: AnimatedIcons.play_pause,
              progress: _playPauseController,
              size: size,
            ),
          ),
        ),
      ),
      IconButton(
        tooltip: S.current.audio_next,
        onPressed: () {
          player.next();
        },
        icon: Icon(Icons.skip_next, size: size),
      ),
      IconButton(
        tooltip: S.current.playlist,
        onPressed: () {
          _showPlayListSheet(context);
        },
        icon: Icon(Icons.playlist_play, size: size),
      ),
    ];
  }

  Widget _cover() {
    return Hero(
      tag: 'audio_cover',
      child: CoverView(
        rotate: player.playing,
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

  Widget _miniLrc() {
    final lines =
        lrc?.getLines(player.state.position) ??
        [
          LrcLine(
            timestamp: Duration(),
            lyrics: S.current.no_lrc,
            type: LrcTypes.simple,
          ),
        ];
    return Padding(
      padding: EdgeInsets.symmetric(horizontal: 6),
      child: SizedBox(
        height: 55,
        child: IntrinsicHeight(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              for (var i = 0; i < min(lines.length, 2); i++)
                Text(
                  lines[i].lyrics,
                  style: TextStyle(fontSize: 20),
                  softWrap: false,
                ),
            ],
          ),
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
          Row(
            spacing: 12,
            children: [
              Text(
                info?.artists ?? '?',
                style: TextTheme.of(
                  context,
                ).bodyLarge?.copyWith(color: Colors.white),
              ),
              Container(
                decoration: BoxDecoration(
                  color: Colors.grey.withValues(alpha: 0.2),
                  borderRadius: BorderRadius.circular(5),
                ),
                child: Padding(
                  padding: EdgeInsets.all(2),
                  child: Text('${info?.bitrate ?? '?'} kbps'),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _infoControllers(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        IconButton(
          tooltip: S.current.more,
          onPressed: () {
            _showMoreSheet(context);
          },
          icon: Icon(Icons.more_vert),
        ),
      ],
    );
  }

  Widget _progressInfo() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        SliderTheme(
          data: SliderThemeData(showValueIndicator: ShowValueIndicator.always),
          child: Slider(
            value: _progress,
            secondaryTrackValue: player.bufferProgress ?? 0,
            inactiveColor: Colors.grey.withValues(alpha: 0.3),
            label: (player.duration.value * _progress).shortTimeStr,
            onChangeStart: (value) {
              _changing = true;
            },
            onChanged: (value) {
              _progress = value;
              _render();
            },
            onChangeEnd: (value) {
              player.seek(
                Duration(seconds: (player.duration.value * value).toInt()),
              );
              _changing = false;
              _render();
            },
          ),
        ),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Padding(
              padding: EdgeInsets.only(left: 12),
              child: Text(player.position.value.shortTimeStr),
            ),
            Padding(
              padding: EdgeInsets.only(right: 12),
              child: Text(player.duration.value.shortTimeStr),
            ),
          ],
        ),
      ],
    );
  }

  Widget _content(BuildContext context) {
    return Column(
      children: [
        Row(children: [BackButton()]),
        Expanded(
          child: Padding(
            padding: EdgeInsets.only(left: 40, right: 40, top: 30, bottom: 20),
            child: Center(child: _cover()),
          ),
        ),
        _miniLrc(),
        IntrinsicHeight(
          child: Stack(
            children: [
              _info(context),
              Align(
                alignment: Alignment.centerRight,
                child: _infoControllers(context),
              ),
            ],
          ),
        ),
        _progressInfo(),
        Padding(
          padding: EdgeInsets.only(bottom: 12),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: _controllers(context),
          ),
        ),
      ],
    );
  }

  _showMoreSheet(BuildContext context) {
    showModalBottomSheet(
      context: context,
      constraints: BoxConstraints(minWidth: 100),
      builder: (context) {
        return MoreSheet(info: player.audioInfo.value!);
      },
    );
  }

  _showPlayListSheet(BuildContext context) {
    showModalBottomSheet(
      context: context,
      constraints: BoxConstraints(minWidth: 100),
      builder: (context) {
        return PLayListSheet();
      },
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
              size: 30,
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
