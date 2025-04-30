import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_markdown/flutter_markdown.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/file_utils.dart';
import 'package:h_nas/utils/media_type.dart';

class TextReaderPage extends StatefulWidget {
  const TextReaderPage({super.key});

  @override
  State createState() => _TextReaderPageState();
}

class _TextReaderPageState extends State<TextReaderPage> {
  FileInfo? file;
  bool private = false;
  String text = '';

  @override
  void initState() {
    super.initState();
  }

  void _load() {
    FileAPI.getFileData(
      file!.fullPath,
      private: private,
      contentType: ResponseType.stream,
    ).then((data) {
      if (data != null) {
        data.listen((event) {
          final str = utf8.decode(event);
          setState(() {
            text += str;
          });
        });
      }
    });
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    final args = ModalRoute.of(context)!.settings.arguments as List;
    final f = args[0] as FileInfo;
    final p = args[1] as bool;
    if (file != f) {
      setState(() {
        file = f;
        private = p;
        _load();
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final markdown = file?.fileMediaType?.subType == MediaType.subTypeMarkdown;
    return Scaffold(
      appBar: AppBar(title: Text(file?.name ?? '')),
      body: Padding(
        padding: EdgeInsets.all(8),
        child:
            markdown
                ? Markdown(data: text)
                : SingleChildScrollView(
                  child: SizedBox(width: double.infinity, child: Text(text)),
                ),
      ),
    );
  }
}
