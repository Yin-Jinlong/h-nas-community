import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/media/media_player.dart';
import 'package:h_nas/utils/api.dart';

import 'marquee.dart';

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
      child: Container(
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
                        child: cover!,
                      ),
                    ),
                  ),
              IntrinsicHeight(
                child: Column(
                  children: [
                    ConstrainedBox(
                      constraints: BoxConstraints(minWidth: 50, maxWidth: 150),
                      child: Marquee.text(
                        text: player.audioInfo.value?.title ?? '?',
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
                        text: player.audioInfo.value?.artists ?? '?',
                        space: 30,
                        maxWidth: 150,
                        style: TextTheme.of(context).titleMedium,
                      ),
                    ),
                  ],
                ),
              ),
              IconButton(
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
            ],
          ),
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
