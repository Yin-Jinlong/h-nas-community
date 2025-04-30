import 'package:flutter/material.dart';

class SwitchButton extends StatelessWidget {
  final bool selected;
  final VoidCallback onPressed;
  final Widget child;

  const SwitchButton({
    super.key,
    required this.selected,
    required this.onPressed,
    required this.child,
  });

  @override
  Widget build(BuildContext context) {
    final colorScheme = ColorScheme.of(context);
    return FilledButton(
      onPressed: onPressed,
      style: FilledButton.styleFrom(
        backgroundColor: selected ? colorScheme.primary : colorScheme.surface,
        foregroundColor:
            selected
                ? colorScheme.onPrimary
                : colorScheme.onSurface.withValues(alpha: 0.6),
        elevation: selected ? 0 : 1,
      ),
      child: child,
    );
  }
}
