import 'dart:ui';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:h_nas/components/cover_view.dart';
import 'package:h_nas/components/spring_draggable_container.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/main.dart';
import 'package:h_nas/media/media_player.dart';
import 'package:h_nas/routes.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/audio_info_exts.dart';

import 'marquee.dart';

class MiniAudioPlayer extends StatefulWidget {
  final VoidCallback onClose;

  const MiniAudioPlayer({super.key, required this.onClose});

  @override
  State createState() => _MiniAudioPlayerState();
}

class _MiniAudioPlayerState extends State<MiniAudioPlayer>
    with TickerProviderStateMixin {
  late final AnimationController _playPauseController;
  late final Animation<double> _coverShadowAnimation;
  late final MediaPlayer player;
  final FocusNode _rootNode = FocusNode();
  AudioFileInfo? _cachedAudioInfo;

  @override
  void initState() {
    super.initState();
    player = Global.player;
    _playPauseController = AnimationController(
      value: player.playing ? 1 : 0,
      vsync: this,
      duration: durationMedium,
    );
    _coverShadowAnimation = _playPauseController.drive(
      CurveTween(curve: Curves.easeInOut),
    )..addListener(_render);

    player.nowPlay.addListener(_onNowPlayChange);
    player.playState.addListener(_onPlay);
    player.position.addListener(_render);

    _onNowPlayChange();
  }

  _render() {
    setState(() {});
  }

  _onNowPlayChange() {
    player.nowPlay.value?.addListener(_onAudioInfo);
    _onAudioInfo();
    setState(() {});
  }

  _onAudioInfo() {
    final info = player.nowPlay.value?.audioInfo;
    if (info == null) return;
    _cachedAudioInfo = info;
    setState(() {});
  }

  _onPlay() {
    setState(() {});
    if (player.playing) {
      _playPauseController.forward();
    } else {
      _playPauseController.reverse();
    }
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
    }
  }

  @override
  void dispose() {
    _playPauseController.dispose();
    _coverShadowAnimation.removeListener(_render);

    player.nowPlay.value?.removeListener(_onAudioInfo);
    player.nowPlay.removeListener(_onNowPlayChange);
    player.playState.removeListener(_onPlay);
    player.position.removeListener(_render);
    super.dispose();
  }

  Widget _content(BuildContext context) {
    var info = _cachedAudioInfo;
    var title = info?.userTitle ?? '?';
    var artists = info?.userArtist ?? '?';

    return Container(
      decoration: _MiniProgressDecoration(
        progress: player.progress ?? 0,
        color: ColorScheme.of(context).primary,
        lineHeight: 2,
        padding: const EdgeInsets.symmetric(horizontal: 5),
        offset: 50,
      ),
      child: Padding(
        padding: EdgeInsets.all(6),
        child: Row(
          children: [
            info == null
                ? Icon(Icons.image)
                : SpringDraggableContainer(
                  child: Padding(
                    padding: EdgeInsets.symmetric(horizontal: 5),
                    child: Transform.scale(
                      scale: 1.2,
                      origin: Offset(20, 0),
                      child: Hero(
                        tag: 'audio_cover',
                        child: SizedBox(
                          width: 40,
                          height: 40,
                          child: CoverView(
                            rotate: player.playing,
                            shadow: BoxShadow(
                              color: Colors.black.withValues(alpha: 0.4),
                              blurRadius: _coverShadowAnimation.value * 8,
                              blurStyle: BlurStyle.outer,
                            ),
                            child: CachedNetworkImage(
                              imageUrl: FileAPIURL.publicAudioCover(info.path),
                              fit: BoxFit.cover,
                              errorWidget: (context, error, stackTrace) {
                                return Icon(Icons.broken_image);
                              },
                            ),
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
            IntrinsicHeight(
              child: Column(
                children: [
                  ConstrainedBox(
                    constraints: BoxConstraints(minWidth: 50, maxWidth: 150),
                    child: Hero(
                      tag: 'audio_title',
                      child: Marquee.text(
                        key: ValueKey(title),
                        text: title,
                        space: 30,
                        maxWidth: 150,
                        style: TextTheme.of(
                          context,
                        ).titleMedium?.copyWith(fontWeight: FontWeight.bold),
                      ),
                    ),
                  ),
                  ConstrainedBox(
                    constraints: BoxConstraints(minWidth: 50, maxWidth: 150),
                    child: Hero(
                      tag: 'audio_artists',
                      child: Marquee.text(
                        key: ValueKey(artists),
                        text: artists,
                        space: 30,
                        maxWidth: 150,
                        style: TextTheme.of(context).titleMedium,
                      ),
                    ),
                  ),
                ],
              ),
            ),
            Hero(
              tag: 'play_pause',
              child: IconButton(
                tooltip:
                    player.playing ? S.current.pause : S.current.media_play,
                onPressed: () {
                  player.playPause();
                },
                icon: AnimatedIcon(
                  icon: AnimatedIcons.play_pause,
                  progress: _playPauseController,
                ),
              ),
            ),
            IconButton(
              tooltip: S.current.close,
              onPressed: widget.onClose,
              icon: Icon(Icons.close),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return KeyboardListener(
      focusNode: _rootNode,
      autofocus: true,
      onKeyEvent: (value) {
        switch (value) {
          case KeyDownEvent e:
            _onKeyDown(e);
            break;
        }
      },
      child: Card(
        elevation: 5,
        child: InkWell(
          hoverColor: Colors.transparent,
          highlightColor: Colors.transparent,
          splashColor: Colors.transparent,
          onTap: () {
            navigatorKey.currentState?.pushNamed(Routes.audioPlayer);
          },
          child: _content(context),
        ),
      ),
    );
  }
}

class _MiniProgressDecoration extends Decoration {
  final double lineHeight, progress, offset;
  final Color color;

  @override
  final EdgeInsetsGeometry padding;

  const _MiniProgressDecoration({
    required this.progress,
    required this.offset,
    required this.color,
    required this.lineHeight,
    required this.padding,
  });

  @override
  BoxPainter createBoxPainter([VoidCallback? onChanged]) =>
      _MiniProgressDecorationPainter(onChanged, decoration: this);
}

class _MiniProgressDecorationPainter extends BoxPainter {
  final _MiniProgressDecoration decoration;

  _MiniProgressDecorationPainter(super.onChanged, {required this.decoration});

  final Paint _paint =
      Paint()
        ..style = PaintingStyle.stroke
        ..strokeCap = StrokeCap.round;

  @override
  void paint(Canvas canvas, Offset offset, ImageConfiguration configuration) {
    final lineHeight = decoration.lineHeight;
    final off = decoration.offset;

    _paint.color = decoration.color;
    _paint.strokeWidth = lineHeight;
    final size = configuration.size ?? Size(0, 0);

    final path = Path();
    path.addRRect(RRect.fromLTRBXY(0, 0, size.width, size.height, 10, 10));

    final metric = path.computeMetrics().first;
    final len = decoration.progress * metric.length;

    final drawPath = Path();
    if (len < off) {
      metric.extractTo(drawPath, off - len, off, offset);
    } else {
      metric.extractTo(drawPath, off, off - len, offset);
    }

    canvas.drawPath(drawPath, _paint);
  }
}

extension _PathMetric on PathMetric {
  double _pack(double v) =>
      v < 0
          ? v + length
          : v > length
          ? v - length
          : v;

  void extractTo(Path dst, double start, double end, Offset offset) {
    if (start == end) return;
    final s = _pack(start);
    final e = _pack(end);

    if (start < end) {
      dst.addPath(extractPath(s, e), offset);
    } else {
      dst.addPath(extractPath(e, length), offset);
      dst.addPath(extractPath(0, s), offset);
    }
  }
}
