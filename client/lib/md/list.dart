import 'package:flutter/material.dart';
import 'package:h_nas/md/builder.dart';
import 'package:markdown/markdown.dart' as md;

InlineSpan _list(BuildContext context, md.Element ele, MarkdownStyle style) {
  final children = <InlineSpan>[];
  for (final child in ele.children ?? <md.Node>[]) {
    children.add(
      md_block([
        WidgetSpan(child: Text('◆', style: style.paragraph)),
        ...md_inline(context, child as md.Element, style),
      ], defTextStyle: style.paragraph),
    );
  }
  return md_block(children, defTextStyle: style.paragraph);
}

InlineSpan md_ul(BuildContext context, md.Element ele, MarkdownStyle style) {
  return _list(context, ele, style);
}
