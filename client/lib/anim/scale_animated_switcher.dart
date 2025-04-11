import 'package:flutter/cupertino.dart';
import 'package:h_nas/global.dart';

Widget ScaleAnimatedSwitcher({
  Duration duration = durationFast,
  required Widget? child,
}) {
  return AnimatedSwitcher(
    duration: duration,
    transitionBuilder: (child, animation) {
      return ScaleTransition(
        scale: CurveTween(curve: Curves.easeInOut).animate(animation),
        child: child,
      );
    },
    child: child,
  );
}
