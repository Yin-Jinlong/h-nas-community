import 'package:flutter/material.dart';
import 'package:h_nas/md/builder.dart';
import 'package:markdown/markdown.dart' as md;

InlineSpan _list(BuildContext context, md.Element ele) {
  final children = <InlineSpan>[];
  for (final child in ele.children ?? <md.Node>[]) {
    children.add(
      md_block([
        WidgetSpan(
          child: Text(
            '◆',
            style: TextStyle(color: ColorScheme.of(context).onSurface),
          ),
        ),
        ...md_inline(context, child as  md.Element),
      ], style: TextStyle()),
    );
  }
  return md_block(children, style: TextStyle(fontSize: 14));
}

InlineSpan md_ul(BuildContext context, md.Element ele) {
  return _list(context, ele);
}
