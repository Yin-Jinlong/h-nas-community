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
  double _downX = 0, _downY = 0, _offX = 0, _offY = 0;
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
    return Listener(
      onPointerDown: (event) {
        setState(() {
          _controller.value = 1;
          _downX = event.localPosition.dx;
          _downY = event.localPosition.dy;
          _offX = 0;
          _offY = 0;
        });
      },
      onPointerMove: (event) {
        setState(() {
          _offX = event.localPosition.dx - _downX;
          _offY = event.localPosition.dy - _downY;
        });
      },
      onPointerUp: (event) {
        _controller.animateTo(0);
      },
      child: Transform.translate(
        offset: Offset(_offX, _offY) * _animation.value,
        child: widget.child,
      ),
    );
  }
}
