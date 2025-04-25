import 'package:async/async.dart';
import 'package:flutter/material.dart';
import 'package:h_nas/anim/scale_animated_switcher.dart';
import 'package:h_nas/components/clickable.dart';
import 'package:h_nas/components/dispose.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/media/media_player.dart';
import 'package:h_nas/pages/video_player/more_drawer.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/time_utils.dart';
import 'package:media_kit_video/media_kit_video_controls/media_kit_video_controls.dart'
    as media_kit_video_controls;
import 'package:tdtx_nf_icons/tdtx_nf_icons.dart';
import 'package:universal_platform/universal_platform.dart';

mixin VideoControlState {
  List<int> get bitrates;

  int get streamIndex;
}

class VideoControlsScaffold extends StatefulWidget {
  final VideoControlState state;
  final FileInfo file;
  final HLSStreamInfo? info;
  final void Function(int) onBitrateIndex;

  const VideoControlsScaffold({
    super.key,
    required this.file,
    required this.state,
    required this.info,
    required this.onBitrateIndex,
  });

  @override
  State<StatefulWidget> createState() => _VideoControlsScaffoldState();
}

class _VideoControlsScaffoldState
    extends DisposeFlagState<VideoControlsScaffold> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: DefaultTextStyle(
        style: TextStyle(fontSize: 16, color: Colors.white),
        child: IconTheme(
          data: IconThemeData(size: 30, color: Colors.white),
          child: _VideoControlsContent(
            file: widget.file,
            state: widget.state,
            info: widget.info,
            onBitrateIndex: widget.onBitrateIndex,
          ),
        ),
      ),
      endDrawer: MoreDrawer(),
    );
  }
}

class _VideoControlsContent extends StatefulWidget {
  final VideoControlState state;
  final FileInfo file;
  final HLSStreamInfo? info;
  final void Function(int) onBitrateIndex;

  const _VideoControlsContent({
    required this.file,
    required this.state,
    required this.info,
    required this.onBitrateIndex,
  });

  @override
  State<StatefulWidget> createState() => _VideoControlsState();
}

class _VideoControlsState extends DisposeFlagState<_VideoControlsContent>
    with TickerProviderStateMixin {
  late VideoControlState _state;
  late final AnimationController _playPauseController;
  final MediaPlayer player = Global.player;
  bool _showControls = false;
  CancelableOperation? _showControlsOperation;

  @override
  void initState() {
    super.initState();
    _state = widget.state;
    _playPauseController = AnimationController(
      value: player.playing ? 1 : 0,
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

  bool _isFullscreen(BuildContext context) {
    return media_kit_video_controls.isFullscreen(context);
  }

  Future<void> _toggleFullscreen(BuildContext context) {
    return media_kit_video_controls.toggleFullscreen(context);
  }

  Widget _progressBar() {
    return SizedBox(
      height: 20,
      child: SliderTheme(
        data: SliderThemeData(showValueIndicator: ShowValueIndicator.always),
        child: Slider(
          value: player.progress ?? 0,
          label: (player.position.value / 1000).shortTimeStr,
          onChanged: (value) {
            _show();
            player.seek(
              Duration(milliseconds: (value * player.duration.value).toInt()),
            );
          },
        ),
      ),
    );
  }

  Widget _bottomControls(BuildContext context) {
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
        Expanded(child: Container()),
        if (_state.bitrates.isNotEmpty)
          DropdownButton(
            value: _state.streamIndex,
            onTap: () {
              _show(duration: const Duration(seconds: 3));
            },
            selectedItemBuilder: (context) {
              return [
                for (var i = 0; i < _state.bitrates.length; i++)
                  Text(
                    '${_state.bitrates[i] ~/ 1000} Mbps',
                    style: TextStyle(color: Colors.white),
                  ),
              ];
            },
            items: [
              for (var i = 0; i < _state.bitrates.length; i++)
                DropdownMenuItem(
                  value: i,
                  child: Text('${_state.bitrates[i] ~/ 1000} Mbps'),
                ),
            ],
            onChanged: (value) {
              setState(() {
                widget.onBitrateIndex(value as int);
              });
            },
          ),
        IconButton(
          tooltip:
              _isFullscreen(context)
                  ? S.current.exit_fullscreen
                  : S.current.fullscreen,
          onPressed: () {
            setState(() {
              _toggleFullscreen(context);
            });
          },
          icon: Icon(
            _isFullscreen(context) ? Icons.fullscreen_exit : Icons.fullscreen,
          ),
        ),
      ],
    );
  }

  Widget _topControls() {
    return IntrinsicHeight(
      child: Row(
        children: [
          BackButton(),
          Text(widget.file.name),
          Expanded(child: Container()),
          IconButton(
            onPressed: () {
              Scaffold.of(context).openEndDrawer();
            },
            icon: Icon(Icons.more_vert),
          ),
        ],
      ),
    );
  }

  @override
  void didChangeDependencies() {
    _state = widget.state;
    super.didChangeDependencies();
  }

  @override
  void dispose() {
    _playPauseController.dispose();
    player.playState.removeListener(_onPlay);
    player.position.removeListener(_render);
    super.dispose();
  }

  void _show({Duration duration = const Duration(seconds: 2)}) {
    setState(() {
      _showControls = true;
    });

    _showControlsOperation?.cancel();
    _showControlsOperation = CancelableOperation.fromFuture(
      Future.delayed(duration),
    ).then((v) {
      if (disposed) return;
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
        color: ColorScheme.of(context).primary.withValues(alpha: 0.5),
        child: SizedBox(width: double.infinity, height: 2),
      ),
    );
  }

  Widget _loading() {
    return Align(
      alignment: Alignment.center,
      child: IntrinsicHeight(
        child: Column(
          children: [
            CircularProgressIndicator(),
            Text(
              widget.info == null
                  ? S.current.loading
                  : '${((int.tryParse(widget.info!.data) ?? 0) / 10).toStringAsFixed(1)}%',
              style: TextStyle(color: Colors.white),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return MouseRegion(
      onHover: (event) {
        _show();
      },
      child: Clickable(
        onTap: () {
          if (UniversalPlatform.isDesktopOrWeb) {
            player.playPause();
          } else {
            if (_showControls) {
              _showControlsOperation?.cancel();
              setState(() {
                _showControls = false;
              });
            } else {
              _show();
            }
          }
        },
        onDoubleTap: () {
          if (!UniversalPlatform.isDesktopOrWeb) {
            player.playPause();
          } else {
            _toggleFullscreen(context);
          }
        },
        child: Stack(
          children: [
            Align(
              alignment: Alignment.bottomLeft,
              child: _miniProgressBar(context, player.progress ?? 0),
            ),
            if (_showControls && _isFullscreen(context))
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
            if (widget.info == null ||
                widget.info?.status != HLSStreamStatus.done)
              _loading(),
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
                        child: Column(
                          children: [_progressBar(), _bottomControls(context)],
                        ),
                      ),
                    ),
                  ),
                ),
              ),
            Align(
              alignment: Alignment.bottomRight,
              child: Padding(
                padding: EdgeInsets.only(right: 20, bottom: 80),
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
    );
  }
}
