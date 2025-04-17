import 'package:flutter/cupertino.dart';

class SpringDraggableContainer extends StatefulWidget {
  final Duration duration;
  final Widget child;

  const SpringDraggableContainer({
    super.key,
    required this.child,
    this.duration = const Duration(milliseconds: 400),
  });

  @override
  State createState() => _SpringContainerState();
}

class _SpringContainerState extends State<SpringDraggableContainer>
    with SingleTickerProviderStateMixin {
  Offset _down = Offset(0, 0), _off = Offset(0, 0);
  late final AnimationController _controller;
  late final Animation<double> _animation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(vsync: this, duration: widget.duration)
      ..addListener(() {
        setState(() {});
      });
    _animation = _controller.drive(CurveTween(curve: Curves.elasticIn));
  }

  void _start() {
    _controller.reverse();
  }

  @override
  void didUpdateWidget(covariant SpringDraggableContainer oldWidget) {
    super.didUpdateWidget(oldWidget);
    _controller.duration = widget.duration;
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onPanDown: (event) {
        setState(() {
          _controller.value = 1;
          _down = event.localPosition;
          _off = Offset(0, 0);
        });
      },
      onPanUpdate: (event) {
        setState(() {
          _off = event.localPosition - _down;
        });
      },
      onPanEnd: (details) {
        _start();
      },
      onPanCancel: _start,
      child: Transform.translate(
        offset: _off * _animation.value,
        child: widget.child,
      ),
    );
  }
}
