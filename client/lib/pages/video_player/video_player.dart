import 'package:async/async.dart';
import 'package:flutter/material.dart';
import 'package:h_nas/anim/scale_animated_switcher.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/media/media_player.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/time_utils.dart';
import 'package:media_kit_video/media_kit_video.dart';
import 'package:tdtx_nf_icons/tdtx_nf_icons.dart';

class VideoPlayerPage extends StatefulWidget {
  const VideoPlayerPage({super.key});

  @override
  State createState() => _VideoPlayerPageState();
}

class _VideoPlayerPageState extends State<VideoPlayerPage> {
  late final VideoController _controller;
  FileInfo? file;

  @override
  void initState() {
    super.initState();
    Global.player.stop();
    _controller = VideoController(Global.player.nativePlayer);
  }

  _load() {
    Global.player.open(file!);
  }

  @override
  void dispose() {
    Global.player.stop();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (file == null) {
      file = ModalRoute.of(context)?.settings.arguments as FileInfo;
      _load();
    }

    return Scaffold(
      body: Video(
        controller: _controller,
        controls: (state) => _VideoControls(state: state, file: file!),
      ),
    );
  }
}

class _VideoControls extends StatefulWidget {
  final VideoState state;
  final FileInfo file;

  const _VideoControls({required this.state, required this.file});

  @override
  State<StatefulWidget> createState() => _VideoControlsState();
}

class _VideoControlsState extends State<_VideoControls>
    with TickerProviderStateMixin {
  late final AnimationController _playPauseController;
  final MediaPlayer player = Global.player;
  bool _showControls = false, _disposed = false;
  CancelableOperation? _showControlsOperation;

  @override
  void initState() {
    super.initState();
    _playPauseController = AnimationController(
      value: player.playing ? 0 : 1,
      vsync: this,
      duration: durationFast,
    );

    player.playState.addListener(_onPlay);
    player.position.addListener(_render);
    _show();
  }

  _render() {
    setState(() {});
  }

  _onPlay() {
    if (player.playing) {
      _playPauseController.forward();
    } else {
      _playPauseController.reverse();
    }
    setState(() {});
  }

  Widget _bottomControls() {
    return Row(
      children: [
        IconButton(
          tooltip: player.playing ? S.current.pause : S.current.media_play,
          onPressed: () {
            player.playPause();
            setState(() {});
          },
          icon: AnimatedIcon(
            icon: AnimatedIcons.play_pause,
            progress: _playPauseController,
            size: 30,
          ),
        ),
        Text(
          '${(player.position.value / 1000).shortTimeStr}/${(player.duration.value / 1000).shortTimeStr}',
        ),
      ],
    );
  }

  Widget _topControls() {
    return IntrinsicHeight(
      child: Row(children: [BackButton(), Text(widget.file.name)]),
    );
  }

  @override
  void dispose() {
    _disposed = true;

    _playPauseController.dispose();
    player.playState.removeListener(_onPlay);
    player.position.removeListener(_render);
    super.dispose();
  }

  void _show() {
    setState(() {
      _showControls = true;
    });

    _showControlsOperation?.cancel();
    _showControlsOperation = CancelableOperation.fromFuture(
      Future.delayed(const Duration(seconds: 2)),
    ).then((v) {
      if (_disposed) return;
      setState(() {
        _showControls = false;
      });
    });
  }

  Widget _miniProgressBar(BuildContext context, double progress) {
    return Transform.scale(
      scaleX: progress,
      alignment: Alignment.bottomLeft,
      child: Container(
        color: ColorScheme.of(context).primary,
        child: SizedBox(width: double.infinity, height: 2),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return DefaultTextStyle(
      style: TextStyle(fontSize: 16, color: Colors.white),
      child: IconTheme(
        data: IconThemeData(size: 30, color: Colors.white),
        child: MouseRegion(
          onHover: (event) {
            _show();
          },
          child: InkWell(
            highlightColor: Colors.transparent,
            splashColor: Colors.transparent,
            onTap: () {
              player.playPause();
              setState(() {});
            },
            child: Stack(
              children: [
                Align(
                  alignment: Alignment.bottomLeft,
                  child: _miniProgressBar(context, player.progress ?? 0),
                ),
                if (_showControls)
                  Align(
                    alignment: Alignment.topCenter,
                    child: Container(
                      decoration: BoxDecoration(
                        gradient: LinearGradient(
                          begin: Alignment.topCenter,
                          end: Alignment.bottomCenter,
                          colors: [
                            Colors.black.withValues(alpha: 0.6),
                            Colors.transparent,
                          ],
                        ),
                      ),
                      child: Padding(
                        padding: EdgeInsets.only(
                          bottom: 20,
                          left: 8,
                          right: 8,
                          top: 8,
                        ),
                        child: _topControls(),
                      ),
                    ),
                  ),
                if (_showControls)
                  Align(
                    alignment: Alignment.bottomCenter,
                    child: Container(
                      decoration: BoxDecoration(
                        gradient: LinearGradient(
                          begin: Alignment.bottomCenter,
                          end: Alignment.topCenter,
                          colors: [
                            Colors.black.withValues(alpha: 0.6),
                            Colors.transparent,
                          ],
                        ),
                      ),
                      child: Padding(
                        padding: EdgeInsets.only(
                          top: 30,
                          left: 8,
                          right: 8,
                          bottom: 8,
                        ),
                        child: SizedBox(
                          width: double.infinity,
                          child: IntrinsicHeight(
                            child: Column(children: [_bottomControls()]),
                          ),
                        ),
                      ),
                    ),
                  ),
                Align(
                  alignment: Alignment.bottomRight,
                  child: Padding(
                    padding: EdgeInsets.only(right: 20, bottom: 60),
                    child: ScaleAnimatedSwitcher(
                      child:
                          player.playing
                              ? null
                              : Icon(
                                TDTxNFIcons.nf_md_presentation_play,
                                size: 50,
                                color: Colors.white.withValues(alpha: 0.7),
                              ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
