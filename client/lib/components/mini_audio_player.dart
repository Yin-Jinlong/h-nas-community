import 'dart:ui';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/media/media_player.dart';
import 'package:h_nas/routes.dart';
import 'package:h_nas/utils/api.dart';

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
      duration: durationMedium,
    );
    _coverController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 10),
    );
    player.audioInfo.addListener(_render);
    player.playState.addListener(_onPlay);
    player.position.addListener(_render);
  }

  _render() {
    setState(() {});
  }

  _onPlay() {
    setState(() {});
    if (player.audioInfo.value != _lastInfo) {
      cover = ClipOval(
        child: CachedNetworkImage(
          imageUrl: FileAPIURL.publicAudioCover(player.audioInfo.value!.path),
          width: 40,
          height: 40,
          fit: BoxFit.cover,
          errorWidget: (context, error, stackTrace) {
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
  void dispose() {
    _coverController.dispose();
    _playPauseController.dispose();

    player.audioInfo.removeListener(_render);
    player.playState.removeListener(_onPlay);
    player.position.removeListener(_render);
    super.dispose();
  }

  Widget _content(BuildContext context) {
    var title = player.audioInfo.value?.title ?? '?';
    var artists = player.audioInfo.value?.artists ?? '?';

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
            player.audioInfo.value == null || cover == null
                ? Icon(Icons.image)
                : Padding(
                  padding: EdgeInsets.symmetric(horizontal: 5),
                  child: Transform.scale(
                    scale: 1.2,
                    origin: Offset(20, 0),
                    child: RotationTransition(
                      turns: _coverController,
                      child: Hero(tag: 'audio_cover', child: cover!),
                    ),
                  ),
                ),
            IntrinsicHeight(
              child: Column(
                children: [
                  ConstrainedBox(
                    constraints: BoxConstraints(minWidth: 50, maxWidth: 150),
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
                  ConstrainedBox(
                    constraints: BoxConstraints(minWidth: 50, maxWidth: 150),
                    child: Marquee.text(
                      key: ValueKey(artists),
                      text: artists,
                      space: 30,
                      maxWidth: 150,
                      style: TextTheme.of(context).titleMedium,
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
    return Card(
      elevation: 5,
      child: InkWell(
        hoverColor: Colors.transparent,
        highlightColor: Colors.transparent,
        splashColor: Colors.transparent,
        onTap: () {
          Navigator.of(context).pushNamed(Routes.audioPlayer);
        },
        child: _content(context),
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
