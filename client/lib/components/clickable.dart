import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class Clickable extends StatelessWidget {
  final MouseCursor cursor;
  final HitTestBehavior behavior;
  final PointerEnterEventListener? onEnter;
  final PointerExitEventListener? onExit;
  final GestureTapCallback? onTap, onDoubleTap;
  final PointerHoverEventListener? onHover;
  final Widget child;

  const Clickable({
    super.key,
    this.cursor = SystemMouseCursors.click,
    this.behavior = HitTestBehavior.opaque,
    this.onTap,
    this.onDoubleTap,
    this.onEnter,
    this.onExit,
    this.onHover,
    required this.child,
  });

  @override
  Widget build(BuildContext context) {
    return MouseRegion(
      cursor: cursor,
      onEnter: onEnter,
      onExit: onExit,
      onHover: onHover,
      child: GestureDetector(
        behavior: behavior,
        onTap: onTap,
        onDoubleTap: onDoubleTap,
        child: child,
      ),
    );
  }
}
