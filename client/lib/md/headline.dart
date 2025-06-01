import 'package:flutter/material.dart';
import 'package:h_nas/md/builder.dart';
import 'package:markdown/markdown.dart' as md;

InlineSpan _headline(BuildContext context, md.Element ele, double size) {
  final children = <InlineSpan>[];
  final style = TextStyle(
    color: ColorScheme.of(context).onSurface,
    fontSize: size,
    fontWeight: FontWeight.bold,
  );
  for (final child in ele.children ?? []) {
    children.add(markdownBuilders.build(context, child, style: style));
  }
  return md_block(children, style: style);
}

InlineSpan md_h1(BuildContext context, md.Element ele) {
  return _headline(context, ele, 30);
}

InlineSpan md_h2(BuildContext context, md.Element ele) {
  return _headline(context, ele, 26);
}

InlineSpan md_h3(BuildContext context, md.Element ele) {
  return _headline(context, ele, 22);
}

InlineSpan md_h4(BuildContext context, md.Element ele) {
  return _headline(context, ele, 18);
}

InlineSpan md_h5(BuildContext context, md.Element ele) {
  return _headline(context, ele, 16);
}

InlineSpan md_h6(BuildContext context, md.Element ele) {
  return _headline(context, ele, 14);
}
