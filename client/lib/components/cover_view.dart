import 'package:flutter/cupertino.dart';

/// 封面视图
class CoverView extends StatefulWidget {
  /// 是否旋转
  final bool rotate;
  final Duration duration;
  final Widget child;

  const CoverView({
    super.key,
    required this.rotate,
    this.duration = const Duration(seconds: 15),
    required this.child,
  });

  @override
  State createState() => _RecordViewState();
}

class _RecordViewState extends State<CoverView>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(vsync: this, duration: widget.duration);
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (widget.rotate) {
      _controller.repeat();
    } else {
      _controller.stop();
    }
    return AspectRatio(
      aspectRatio: 1,
      child: RotationTransition(
        turns: _controller,
        child: ClipOval(child: widget.child),
      ),
    );
  }
}
