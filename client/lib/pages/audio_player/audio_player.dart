import 'dart:math';
import 'dart:ui';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:h_nas/anim/scale_animated_switcher.dart';
import 'package:h_nas/api/api.dart';
import 'package:h_nas/components/clickable.dart';
import 'package:h_nas/components/cover_view.dart';
import 'package:h_nas/components/marquee.dart';
import 'package:h_nas/components/tag.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/media/media_player.dart';
import 'package:h_nas/pages/audio_player/more_sheet.dart';
import 'package:h_nas/pages/audio_player/play_sheet.dart';
import 'package:h_nas/utils/audio_info_exts.dart';
import 'package:h_nas/utils/dispose.dart';
import 'package:h_nas/utils/file_utils.dart';
import 'package:h_nas/utils/lrc_utils.dart';
import 'package:h_nas/utils/time_utils.dart';
import 'package:lrc/lrc.dart';
import 'package:wakelock_plus/wakelock_plus.dart';

import 'lrc_view.dart';

class AudioPlayerPage extends StatefulWidget {
  const AudioPlayerPage({super.key});

  @override
  State<AudioPlayerPage> createState() => _AudioPlayerPageState();
}

class _AudioPlayerPageState extends State<AudioPlayerPage>
    with TickerProviderStateMixin {
  late final AnimationController _playPauseController;
  late final MediaPlayer player;
  bool _changing = false, _showLrcView = false;
  double _progress = 0;
  Lrc? lrc;
  final FocusScopeNode _rootNode = FocusScopeNode();

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

    player.nowPlay.addListener(_newAudio);
    player.position.addListener(_render);
    player.buffer.addListener(_render);
    player.speed.addListener(_render);

    player.playState.addListener(_onPlay);

    _newAudio();
    WakelockPlus.enable();

    Future.delayed(durationMedium, () {
      SystemChrome.setSystemUIOverlayStyle(SystemUiOverlayStyle.light);
    });
  }

  _render() {
    if (!_changing) {
      _progress = player.progress ?? 0;
    }
    setState(() {});
  }

  _newAudio() {
    lrc = null;
    var file = player.nowPlay.value;
    if (file == null) return;
    if (file.audioInfo?.lrc == true) {
      FileAPI.getAudioLrc(file.file.fullPath, private: file.private).then((
        value,
      ) {
        if (disposed) return;
        setState(() {
          if (value.isNotEmpty) {
            lrc = Lrc.parse(value);
          }
        });
      });
    }
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
    player.nowPlay.removeListener(_newAudio);
    player.position.removeListener(_render);
    player.playState.removeListener(_onPlay);
    player.buffer.removeListener(_render);
    player.speed.removeListener(_render);

    _playPauseController.dispose();

    WakelockPlus.disable();

    super.dispose();
  }

  Widget _playModeIcon(double size) {
    return switch (player.playMode.value) {
      PlayMode.none => Transform.translate(
        key: ValueKey(0),
        offset: Offset(0, -5),
        child: Icon(
          const IconData(0x21c9, fontFamily: 'JetBrainsMonoNerd'),
          size: size,
        ),
      ),
      PlayMode.single => Icon(key: ValueKey(1), Icons.repeat_one, size: size),
      PlayMode.loop => Icon(key: ValueKey(2), Icons.repeat, size: size),
      PlayMode.random => Icon(key: ValueKey(3), Icons.shuffle, size: size),
    };
  }

  void _seekBackward(Duration t) {
    var duration = player.state.position - t;
    player.seek(duration < const Duration() ? const Duration() : duration);
  }

  void _seekForward(Duration t) {
    var duration = player.state.position + t;
    player.seek(
      duration > player.state.duration ? player.state.duration : duration,
    );
  }

  List<Widget> _controllers(BuildContext context) {
    final wide = MediaQuery.of(context).size.width > 650;
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
        icon: ScaleAnimatedSwitcher(child: _playModeIcon(size)),
      ),
      if (wide)
        IconButton(
          tooltip: L.current.replay_10s,
          onPressed: () {
            _seekBackward(const Duration(seconds: 10));
          },
          icon: Icon(Icons.replay_10, size: size),
        ),
      IconButton(
        tooltip: L.current.audio_previous,
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
            tooltip: player.playing ? L.current.pause : L.current.media_play,
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
        tooltip: L.current.audio_next,
        onPressed: () {
          player.next();
        },
        icon: Icon(Icons.skip_next, size: size),
      ),
      if (wide)
        IconButton(
          tooltip: L.current.forward_10s,
          onPressed: () {
            _seekForward(const Duration(seconds: 10));
          },
          icon: Icon(Icons.forward_10, size: size),
        ),
      IconButton(
        tooltip: L.current.playlist,
        onPressed: () {
          _showPlayListSheet(context);
        },
        icon: Icon(Icons.playlist_play, size: size),
      ),
    ];
  }

  Widget _cover() {
    final nowPlay = player.nowPlay.value;
    final path = nowPlay?.audioInfo?.path;
    return Hero(
      tag: 'audio_cover',
      child:
          path == null
              ? Icon(Icons.broken_image)
              : CoverView(
                rotate: player.playing,
                child: CachedNetworkImage(
                  imageUrl: FileAPIURL.audioCover(
                    path,
                    private: nowPlay!.private,
                  ),
                  httpHeaders: {...API.tokenHeader()},
                  fit: BoxFit.cover,
                  fadeInDuration: durationFast,
                  errorWidget: (context, error, stackTrace) {
                    return Icon(Icons.broken_image);
                  },
                ),
              ),
    );
  }

  Widget _miniLrc(BuildContext context) {
    final colorSchema = ColorScheme.of(context);
    final lines =
        lrc?.getLines(player.state.position) ??
        [
          LrcLine(
            timestamp: Duration(),
            lyrics: L.current.no_lrc,
            type: LrcTypes.simple,
          ),
        ];
    return Padding(
      padding: EdgeInsets.symmetric(horizontal: 6),
      child: ConstrainedBox(
        constraints: BoxConstraints(minHeight: 55),
        child: IntrinsicHeight(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              for (var i = 0; i < min(lines.length, 2); i++)
                Text(
                  lines[i].lyrics,
                  style: TextStyle(
                    fontSize: i == 0 ? 20 : 16,
                    color: i == 0 ? colorSchema.primary : colorSchema.tertiary,
                  ),
                  textAlign: TextAlign.center,
                ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _info(BuildContext context) {
    final info = player.nowPlay.value?.audioInfo;
    return Padding(
      padding: EdgeInsets.symmetric(horizontal: 12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Hero(
            tag: 'audio_title',
            child: Text(
              info?.title ?? '?',
              style: TextTheme.of(
                context,
              ).headlineSmall?.copyWith(color: Colors.white),
            ),
          ),
          Row(
            spacing: 12,
            children: [
              Hero(
                tag: 'audio_artists',
                child: Text(
                  info?.artists ?? '?',
                  style: TextTheme.of(
                    context,
                  ).bodyLarge?.copyWith(color: Colors.white),
                ),
              ),
              Tag(text: '${info?.bitrate ?? '?'} kbps'),
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
        if (player.speed.value != 1)
          Tag(text: '${player.speed.value.toStringAsFixed(2)}×'),
        IconButton(
          tooltip: L.current.more,
          onPressed: () {
            _showMoreSheet(context);
          },
          icon: Icon(Icons.more_vert),
        ),
      ],
    );
  }

  Widget _progressInfo() {
    var dur = player.duration.value / 1000;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        SliderTheme(
          data: SliderThemeData(showValueIndicator: ShowValueIndicator.always),
          child: Slider(
            value: _progress,
            secondaryTrackValue: player.bufferProgress ?? 0,
            inactiveColor: Colors.grey.withValues(alpha: 0.3),
            label: (dur * _progress).shortTimeStr,
            onChangeStart: (value) {
              _changing = true;
            },
            onChanged: (value) {
              _progress = value;
              _render();
            },
            onChangeEnd: (value) {
              player.seek(
                Duration(milliseconds: (player.duration.value * value).toInt()),
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
              child: Text((player.position.value / 1000).shortTimeStr),
            ),
            Padding(
              padding: EdgeInsets.only(right: 12),
              child: Text(dur.shortTimeStr),
            ),
          ],
        ),
      ],
    );
  }

  Widget _lrcView({required VoidCallback onTap}) {
    return lrc != null
        ? LrcView(lrc: lrc!, onTap: onTap)
        : Clickable(onTap: onTap, child: Center(child: Text(L.current.no_lrc)));
  }

  Widget _narrowModeMain() {
    return AnimatedSwitcher(
      duration: durationFast,
      child:
          _showLrcView
              ? _lrcView(
                onTap: () {
                  setState(() {
                    _showLrcView = !_showLrcView;
                  });
                },
              )
              : Padding(
                padding: EdgeInsets.only(
                  left: 40,
                  right: 40,
                  top: 30,
                  bottom: 20,
                ),
                child: Clickable(
                  onTap: () {
                    setState(() {
                      _showLrcView = !_showLrcView;
                    });
                  },
                  child: Center(child: _cover()),
                ),
              ),
    );
  }

  Widget _wideModeMain() {
    return Row(
      children: [
        Expanded(
          flex: 1,
          child: Padding(
            padding: EdgeInsets.all(30),
            child: Center(child: _cover()),
          ),
        ),
        Expanded(
          flex: 1,
          child: Padding(
            padding: EdgeInsets.all(30),
            child: _lrcView(onTap: () {}),
          ),
        ),
      ],
    );
  }

  Widget _content(BuildContext context) {
    final info = player.nowPlay.value?.audioInfo;
    bool wideMode = MediaQuery.of(context).size.aspectRatio > 1;
    return Column(
      children: [
        IntrinsicHeight(
          child: Stack(
            children: [
              Row(children: [BackButton()]),
              if (!wideMode && _showLrcView)
                SizedBox(
                  width: double.infinity,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Hero(
                        tag: 'audio_title',
                        child: Marquee.text(
                          text: info?.userTitle ?? '?',
                          maxWidth: MediaQuery.of(context).size.width * 0.7,
                          space: 30,
                          style: TextTheme.of(
                            context,
                          ).titleMedium?.copyWith(color: Colors.white),
                        ),
                      ),
                      Hero(
                        tag: 'audio_artists',
                        child: Marquee.text(
                          text: info?.userArtist ?? '?',
                          maxWidth: MediaQuery.of(context).size.width * 0.6,
                          space: 20,
                          style: TextTheme.of(context).bodySmall?.copyWith(
                            color: Colors.white.withValues(alpha: 0.6),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
            ],
          ),
        ),
        Expanded(child: wideMode ? _wideModeMain() : _narrowModeMain()),
        if (!wideMode && !_showLrcView) _miniLrc(context),
        if (wideMode || !_showLrcView)
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
        return MoreSheet(
          info: player.nowPlay.value?.audioInfo,
          private: player.private,
        );
      },
    );
  }

  _showPlayListSheet(BuildContext context) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      constraints: BoxConstraints(minWidth: 100, maxHeight: double.infinity),
      builder: (context) {
        return PLayListSheet();
      },
    );
  }

  void _onKeyDown(KeyDownEvent e) {
    switch (e.logicalKey) {
      case LogicalKeyboardKey.mediaPlay:
        player.play();
        break;
      case LogicalKeyboardKey.mediaPause:
        player.pause();
        break;
      case LogicalKeyboardKey.space:
      case LogicalKeyboardKey.mediaPlayPause:
        player.playPause();
        break;
      case LogicalKeyboardKey.escape:
        navigatorKey.currentState?.pop();
        break;
    }
  }

  @override
  Widget build(BuildContext context) {
    final player = Global.player;
    final nowPlay = player.nowPlay.value;

    return Scaffold(
      backgroundColor:
          HSVColor.fromColor(
            ColorScheme.of(context).primary,
          ).withValue(0.3).toColor(),
      body: KeyboardListener(
        focusNode: _rootNode,
        autofocus: true,
        onKeyEvent: (value) {
          switch (value) {
            case KeyDownEvent e:
              _onKeyDown(e);
              break;
          }
        },
        child: Stack(
          children: [
            CachedNetworkImage(
              imageUrl:
                  nowPlay?.audioInfo != null
                      ? FileAPIURL.audioCover(
                        nowPlay!.audioInfo!.path,
                        private: nowPlay.private,
                      )
                      : '',
              httpHeaders: {...API.tokenHeader()},
              fit: BoxFit.cover,
              width: double.infinity,
              height: double.infinity,
              fadeInDuration: durationMedium,
              errorWidget: (context, error, stackTrace) {
                return Container(
                  color:
                      HSVColor.fromColor(
                        ColorScheme.of(context).primary,
                      ).withValue(0.3).toColor(),
                );
              },
            ),
            BackdropFilter(
              filter: ImageFilter.blur(sigmaX: 50, sigmaY: 50),
              child: Container(color: Colors.black.withValues(alpha: 0.4)),
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
                child: SafeArea(
                  child: Padding(
                    padding: EdgeInsets.all(8),
                    child: _content(context),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
