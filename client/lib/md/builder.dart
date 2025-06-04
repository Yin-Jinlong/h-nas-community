import 'package:flutter/material.dart';
import 'package:h_nas/md/code.dart';
import 'package:h_nas/md/list.dart';
import 'package:markdown/markdown.dart' as md;

import 'headline.dart';

typedef MarkdownElementBuilder =
    InlineSpan Function(
      BuildContext context,
      md.Element element,
      MarkdownStyle style,
    );

final Map<String, MarkdownElementBuilder> markdownBuilders = {
  'h1': md_h1,
  'h2': md_h2,
  'h3': md_h3,
  'h4': md_h4,
  'p': md_p,
  'ul': md_ul,
  'code': md_code,
  'pre': md_pre,
};

class MarkdownTextSizes {
  const MarkdownTextSizes({
    this.headline1 = 30,
    this.headline2 = 26,
    this.headline3 = 22,
    this.headline4 = 18,
    this.headline5 = 16,
    this.headline6 = 14,
    this.body = 14,
  });

  final double headline1;
  final double headline2;
  final double headline3;
  final double headline4;
  final double headline5;
  final double headline6;
  final double body;
}

class MarkdownStyle {
  MarkdownStyle({
    this.color = Colors.black,
    this.headlineBase = const TextStyle(fontWeight: FontWeight.bold),
    this.codeBase = const TextStyle(fontFamily: 'JetBrainsMapleMono'),
    this.textSizes = const MarkdownTextSizes(),
    this.paragraphBase = const TextStyle(),
  }) : headline1 = headlineBase.copyWith(
         color: color,
         fontSize: textSizes.headline1,
       ),
       headline2 = headlineBase.copyWith(
         color: color,
         fontSize: textSizes.headline2,
       ),
       headline3 = headlineBase.copyWith(
         color: color,
         fontSize: textSizes.headline3,
       ),
       headline4 = headlineBase.copyWith(
         color: color,
         fontSize: textSizes.headline4,
       ),
       headline5 = headlineBase.copyWith(
         color: color,
         fontSize: textSizes.headline5,
       ),
       headline6 = headlineBase.copyWith(
         color: color,
         fontSize: textSizes.headline6,
       ),
       paragraph = paragraphBase.copyWith(
         color: color,
         fontSize: textSizes.body,
       ),
       code = codeBase.copyWith(color: color);

  final Color color;
  final MarkdownTextSizes textSizes;
  final TextStyle headlineBase, paragraphBase, codeBase;

  late final TextStyle headline1,
      headline2,
      headline3,
      headline4,
      headline5,
      headline6,
      paragraph,
      code;
}

extension Builder on Map<String, MarkdownElementBuilder> {
  InlineSpan build(
    BuildContext context,
    md.Node node, {
    required MarkdownStyle style,
  }) {
    if (node is md.Element) {
      final builder = this[node.tag];
      if (builder != null) {
        return builder(context, node, style);
      }
    }
    return TextSpan(text: node.textContent);
  }
}

InlineSpan md_block(
  List<InlineSpan> children, {
  required TextStyle defTextStyle,
}) {
  return WidgetSpan(
    child: SizedBox(
      width: double.infinity,
      child: DefaultTextStyle(
        style: defTextStyle,
        child: Text.rich(TextSpan(children: children)),
      ),
    ),
  );
}

List<InlineSpan> md_inline(
  BuildContext context,
  md.Element ele,
  MarkdownStyle style,
) {
  final children = <InlineSpan>[];
  for (final child in ele.children ?? []) {
    children.add(markdownBuilders.build(context, child, style: style));
  }
  return children;
}

InlineSpan md_p(BuildContext context, md.Element ele, MarkdownStyle style) {
  return md_block(
    md_inline(context, ele, style),
    defTextStyle: style.paragraph,
  );
}
