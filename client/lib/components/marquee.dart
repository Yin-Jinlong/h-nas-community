import 'dart:math';

import 'package:flutter/cupertino.dart';
import 'package:h_nas/components/dispose.dart';

class Marquee extends StatefulWidget {
  final double maxWidth, space, speed;

  /// 每轮滚动次数
  ///
  /// 默认[3]
  final int count;

  /// 轮次间等待时间
  ///
  /// 默认[1s]
  final Duration turnDur;
  final InlineSpan text;

  const Marquee(
    this.text, {
    super.key,
    required this.maxWidth,
    this.space = 0,
    this.speed = 80,
    this.count = 3,
    this.turnDur = const Duration(seconds: 1),
  });

  factory Marquee.text({
    Key? key,
    required String text,
    TextStyle? style,
    required double maxWidth,
    double space = 0,
    double speed = 80,
    int count = 3,
    Duration turnDur = const Duration(seconds: 1),
  }) => Marquee(
    key: key,
    TextSpan(text: text, style: style),
    maxWidth: maxWidth,
    space: space,
    speed: speed,
    count: count,
    turnDur: turnDur,
  );

  @override
  State createState() => _MarqueeState();
}

class _MarqueeState extends DisposeFlagState<Marquee>
    with SingleTickerProviderStateMixin {
  late double contentWidth, contentHeight;
  late final AnimationController _controller;

  /// 是否需要滚动，超过最大宽度时滚动
  final shouldMarquee = ValueNotifier(false);

  @override
  void initState() {
    super.initState();

    // 计算文本尺寸
    final painter = _MarqueePainter(widget.text);
    contentWidth = painter.contentSize.width;
    contentHeight = painter.contentSize.height;

    _controller =
        AnimationController(
            vsync: this,
            duration: Duration(
              milliseconds: (contentWidth * 1000) ~/ widget.speed,
            ),
          )
          ..addListener(() {
            setState(() {});
          })
          ..addStatusListener((status) {
            if (status == AnimationStatus.completed) {
              _wait();
            }
          });

    shouldMarquee.addListener(_onChange);
  }

  _onChange() {
    if (shouldMarquee.value) {
      _wait();
    } else {
      _controller.reset();
    }
  }

  _wait() {
    Future.delayed(widget.turnDur, () {
      if (disposed) return;
      _controller.repeat(count: widget.count);
    });
  }

  @override
  void didUpdateWidget(covariant Marquee oldWidget) {
    // 计算文本尺寸
    final painter = _MarqueePainter(widget.text);
    contentWidth = painter.contentSize.width;
    contentHeight = painter.contentSize.height;

    _controller.duration = Duration(
      milliseconds: (contentWidth * 1000) ~/ widget.speed,
    );

    shouldMarquee.value = contentWidth > widget.maxWidth;

    super.didUpdateWidget(oldWidget);
  }

  @override
  void dispose() {
    _controller.dispose();
    shouldMarquee.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: min(contentWidth, widget.maxWidth),
      height: contentHeight,
      child: CustomPaint(
        painter: _MarqueePainter(
          widget.text,
          offP: _controller.value,
          space: widget.space,
        ),
      ),
    );
  }
}

class _MarqueePainter extends CustomPainter {
  final double offP, space;

  late final TextPainter textPainter;

  _MarqueePainter(InlineSpan text, {this.offP = 0, this.space = 0}) {
    textPainter = TextPainter(
      text: text,
      maxLines: 1,
      textDirection: TextDirection.ltr,
    );
    textPainter.layout();
    contentSize = textPainter.size;
  }

  late Size contentSize;

  @override
  void paint(Canvas canvas, Size size) {
    final w = contentSize.width + space;

    canvas.clipRect(Rect.fromLTWH(0, 0, size.width, size.height));
    textPainter.paint(canvas, Offset(-offP * w, 0));
    textPainter.paint(canvas, Offset((1 - offP) * w, 0));
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}
