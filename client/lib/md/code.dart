import 'package:flutter/material.dart';
import 'package:flutter_highlight/themes/github.dart';
import 'package:h_nas/md/builder.dart';
import 'package:highlight/highlight.dart';
import 'package:highlight/languages/all.dart';
import 'package:markdown/markdown.dart' as md;

final highlight = Highlight()..registerLanguages(allLanguages);

InlineSpan _code(
  BuildContext context, {
  required Widget child,
  required double padding,
  required Color backgroundColor,
  double borderRadius = 4,
}) {
  return WidgetSpan(
    child: Container(
      padding: EdgeInsets.all(padding),
      decoration: BoxDecoration(
        color: backgroundColor,
        borderRadius: BorderRadius.all(Radius.circular(borderRadius)),
      ),
      child: child,
    ),
  );
}

InlineSpan md_code(BuildContext context, md.Element ele, MarkdownStyle style) {
  return _code(
    context,
    padding: 2,
    child: Text(ele.textContent.trimRight(), style: style.code),
    backgroundColor: Colors.grey.withValues(alpha: 0.2),
  );
}

const _rootKey = 'root';

InlineSpan md_pre(BuildContext context, md.Element ele, MarkdownStyle style) {
  final theme = {...githubTheme, 'root': TextStyle(color: Color(0xff333333))};

  final code = ele.children![0] as md.Element;
  var codeText = code.textContent;
  if (codeText.endsWith('\n')) {
    codeText = codeText.trimRight();
  }
  var lang = 'text';
  if (code.attributes['class'] != null) {
    lang = code.attributes['class']!.substring(9);
  }
  return md_block([
    _code(
      context,
      padding: 8,
      backgroundColor:
          theme[_rootKey]?.backgroundColor ??
          Colors.grey.withValues(alpha: 0.2),
      borderRadius: 8,
      child: Text.rich(
        TextSpan(
          children: _convert(
            theme,
            highlight
                .parse(codeText.replaceAll('\t', ' ' * 4), language: lang)
                .nodes!,
          ),
        ),
      ),
    ),
  ], defTextStyle: style.code);
}

List<TextSpan> _convert(Map<String, TextStyle> theme, List<Node> nodes) {
  List<TextSpan> spans = [];
  var currentSpans = spans;
  List<List<TextSpan>> stack = [];

  void traverse(Node node) {
    if (node.value != null) {
      currentSpans.add(
        node.className == null
            ? TextSpan(text: node.value)
            : TextSpan(text: node.value, style: theme[node.className!]),
      );
    } else if (node.children != null) {
      List<TextSpan> tmp = [];
      currentSpans.add(TextSpan(children: tmp, style: theme[node.className!]));
      stack.add(currentSpans);
      currentSpans = tmp;

      for (var n in node.children!) {
        traverse(n);
        if (n == node.children!.last) {
          currentSpans = stack.isEmpty ? spans : stack.removeLast();
        }
      }
    }
  }

  for (var node in nodes) {
    traverse(node);
  }

  return spans;
}
