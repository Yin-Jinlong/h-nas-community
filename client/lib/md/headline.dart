import 'package:flutter/material.dart';
import 'package:h_nas/md/builder.dart';
import 'package:markdown/markdown.dart' as md;

InlineSpan _headline(
  BuildContext context,
  md.Element ele, {
  required TextStyle headlineStyle,
  required MarkdownStyle style,
}) {
  return md_block(
    md_inline(context, ele, style),
    defTextStyle: headlineStyle,
    padding: EdgeInsets.symmetric(vertical: headlineStyle.fontSize! * 0.25),
  );
}

InlineSpan md_h1(BuildContext context, md.Element ele, MarkdownStyle style) {
  return _headline(context, ele, headlineStyle: style.headline1, style: style);
}

InlineSpan md_h2(BuildContext context, md.Element ele, MarkdownStyle style) {
  return _headline(context, ele, headlineStyle: style.headline2, style: style);
}

InlineSpan md_h3(BuildContext context, md.Element ele, MarkdownStyle style) {
  return _headline(context, ele, headlineStyle: style.headline3, style: style);
}

InlineSpan md_h4(BuildContext context, md.Element ele, MarkdownStyle style) {
  return _headline(context, ele, headlineStyle: style.headline4, style: style);
}

InlineSpan md_h5(BuildContext context, md.Element ele, MarkdownStyle style) {
  return _headline(context, ele, headlineStyle: style.headline5, style: style);
}

InlineSpan md_h6(BuildContext context, md.Element ele, MarkdownStyle style) {
  return _headline(context, ele, headlineStyle: style.headline6, style: style);
}
