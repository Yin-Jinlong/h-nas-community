import 'package:flutter/cupertino.dart';

/// 唱片视图
class RecordView extends StatefulWidget {
  /// 是否旋转
  final bool rotate;
  final double size;
  final Duration duration;
  final Widget child;

  const RecordView({
    super.key,
    required this.rotate,
    required this.size,
    this.duration = const Duration(seconds: 15),
    required this.child,
  });

  @override
  State createState() => _RecordViewState();
}

class _RecordViewState extends State<RecordView>
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
    return SizedBox(
      width: widget.size,
      height: widget.size,
      child: RotationTransition(
        turns: _controller,
        child: ClipOval(child: widget.child),
      ),
    );
  }
}
