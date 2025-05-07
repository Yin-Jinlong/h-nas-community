import 'package:flutter/material.dart';
import 'package:flutter_highlight/flutter_highlight.dart';
import 'package:flutter_highlight/themes/github.dart';
import 'package:flutter_markdown/flutter_markdown.dart';
import 'package:h_nas/components/tag.dart';
import 'package:markdown/markdown.dart' as md;

class CodeBuilder extends MarkdownElementBuilder {
  final MarkdownStyleSheet styleSheet;

  CodeBuilder(this.styleSheet);

  @override
  Widget? visitElementAfterWithContext(
    BuildContext context,
    md.Element element,
    TextStyle? preferredStyle,
    TextStyle? parentStyle,
  ) {
    Widget child;
    if (element.attributes.isNotEmpty) {
      final lang = element.attributes['class']?.substring(9);
      var text = element.textContent;
      if (text.endsWith("\n")) text = text.substring(0, text.length - 1);
      child = Stack(
        children: [
          HighlightView(
            text,
            language: lang,
            tabSize: 4,
            textStyle: preferredStyle,
            theme: {
              ...githubTheme,
              'root': TextStyle(
                color: Color(0xff333333),
                backgroundColor: Colors.transparent,
              ),
            },
          ),
          if (lang != null)
            Align(
              alignment: Alignment.topRight,
              child: Tag(
                child: Text(
                  lang,
                  style: TextStyle(fontSize: styleSheet.code?.fontSize),
                ),
              ),
            ),
        ],
      );
    } else {
      child = Text(element.textContent, style: preferredStyle);
    }
    return Container(
      padding: styleSheet.blockquotePadding,
      decoration: BoxDecoration(
        color: Colors.grey.withValues(alpha: 0.1),
        borderRadius: BorderRadius.circular(8),
      ),
      child: child,
    );
  }
}
