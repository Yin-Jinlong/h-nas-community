import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class Tag extends StatelessWidget {
  final Color backgroundColor;
  final double borderRadius;

  /// 文本，如果有child则忽略
  final String? text;

  /// 子组件
  final Widget? child;
  final EdgeInsetsGeometry padding;

  const Tag({
    super.key,
    this.backgroundColor = const Color.from(
      alpha: 0.2,
      red: 0.5,
      green: 0.5,
      blue: 0.5,
    ),
    this.borderRadius = 5,
    this.padding = const EdgeInsets.all(3),
    this.text,
    this.child,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: backgroundColor,
        borderRadius: BorderRadius.circular(borderRadius),
      ),
      child: Padding(padding: padding, child: child ?? Text(text!)),
    );
  }
}
