import 'package:flutter/cupertino.dart';

class SpringDraggableContainer extends StatefulWidget {
  final Duration duration;

  /// 只监听，不消费事件
  /// 如果为true，子组件仍能响应pan事件
  /// 否则该组件会消费pan事件
  /// 默认为[false]
  final bool onlyListen;
  final Widget child;

  const SpringDraggableContainer({
    super.key,
    required this.child,
    this.onlyListen = false,
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

  void _init(Offset pos) {
    setState(() {
      _controller.value = 1;
      _down = pos;
      _off = Offset(0, 0);
    });
  }

  void _update(Offset pos) {
    setState(() {
      _off = pos - _down;
    });
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
    return widget.onlyListen
        ? Listener(
          onPointerDown: (event) {
            _init(event.localPosition);
          },
          onPointerMove: (event) {
            _update(event.localPosition);
          },
          onPointerUp: (event) {
            _start();
          },
          child: Transform.translate(
            offset: _off * _animation.value,
            child: widget.child,
          ),
        )
        : GestureDetector(
          onPanDown: (event) {
            _init(event.localPosition);
          },
          onPanUpdate: (event) {
            _update(event.localPosition);
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
