import 'package:flutter/material.dart';
import 'package:h_nas/md/list.dart';
import 'package:markdown/markdown.dart' as md;

import 'headline.dart';

typedef MarkdownElementBuilder =
    InlineSpan Function(BuildContext context, md.Element element);

final Map<String, MarkdownElementBuilder> markdownBuilders = {
  'h1': md_h1,
  'h2': md_h2,
  'h3': md_h3,
  'h4': md_h4,
  'p': md_p,
  'ul': md_ul,
};

extension Builder on Map<String, MarkdownElementBuilder> {
  InlineSpan build(BuildContext context, md.Node node, {TextStyle? style}) {
    if (node is md.Element) {
      final builder = this[node.tag];
      if (builder != null) {
        return builder(context, node);
      }
    }
    return TextSpan(text: node.textContent, style: style);
  }
}

InlineSpan md_block(List<InlineSpan> children, {required TextStyle style}) {
  return WidgetSpan(
    child: SizedBox(
      width: double.infinity,
      child: DefaultTextStyle(
        style: style,
        child: SelectableText.rich(TextSpan(children: children)),
      ),
    ),
  );
}

List<InlineSpan> md_inline(BuildContext context, md.Element ele) {
  final children = <InlineSpan>[];
  for (final child in ele.children ?? []) {
    children.add(
      markdownBuilders.build(
        context,
        child,
        style: TextStyle(color: ColorScheme.of(context).onSurface),
      ),
    );
  }
  return children;
}

InlineSpan md_p(BuildContext context, md.Element ele) {
  return md_block(
    md_inline(context, ele),
    style: TextStyle(
      fontSize: 14,
      color: ColorScheme.of(context).onSurface,
      fontWeight: FontWeight.normal,
    ),
  );
}
