import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:h_nas/md/builder.dart';
import 'package:markdown/markdown.dart' as md;

class Markdown extends StatefulWidget {
  final String data;

  const Markdown({super.key, required this.data});

  @override
  State createState() => _MarkdownState();
}

class _MarkdownState extends State<Markdown> {
  final List<md.BlockSyntax> _blocks = [];
  final List<md.InlineSyntax> _inlines = [];
  final List<md.Node> _astNodes = [];

  @override
  void initState() {
    super.initState();
    _parser();
  }

  void _parser() {
    _blocks.clear();
    _inlines.clear();
    _astNodes.clear();

    final doc = md.Document(
      blockSyntaxes: _blocks,
      inlineSyntaxes: _inlines,
      extensionSet: md.ExtensionSet.gitHubWeb,
      encodeHtml: false,
    );

    final lines = const LineSplitter().convert(widget.data);

    _astNodes.addAll(doc.parseLines(lines));
  }

  @override
  void didUpdateWidget(covariant Markdown oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.data != widget.data) {
      _parser();
    }
  }

  @override
  Widget build(BuildContext context) {
    return DefaultTextStyle(
      style: TextStyle(color: ColorScheme.of(context).onSurface),
      child: SizedBox(
        width: double.infinity,
        child: SelectableText.rich(
          TextSpan(
            children: [
              for (final node in _astNodes)
                markdownBuilders.build(context, node),
            ],
          ),
        ),
      ),
    );
  }
}
