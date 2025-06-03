import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:h_nas/api/api.dart';
import 'package:h_nas/pages/ai/chat_message_view.dart';
import 'package:h_nas/utils/dispose.dart';

import 'Llm.dart';
import 'chat_message.dart';

class AIPage extends StatefulWidget {
  const AIPage({super.key});

  @override
  State<StatefulWidget> createState() => _AIPageState();
}

class _AIPageState extends State<AIPage> {
  final textController = TextEditingController();
  final provider = Llm();
  int hintIndex=0;

  final random = Random();

  List<String> hints = ['你是谁'];

  @override
  void initState() {
    super.initState();

    rootBundle.loadString('assets/ai/hints.txt').then((value) {
      hints = value.split('\n');
      hintIndex = random.nextInt(hints.length);
    });
    provider.addListener(_render);
    final llm = ChatMessage.llm(content: '有什么问题欢迎向我提问。')..end();
    provider.history = [llm];
    _loadHistory();
    _changeHint();
  }

  void _render() {
    setState(() {});
  }

  void _loadHistory() {
    AIAPI.getHistory().then((value) {
      for (final msg in value) {
        provider.add(switch (msg.role) {
          'user' => ChatMessage.user(msg.content),
          'assistant' => ChatMessage.llm(content: msg.content)..end(),
          _ => throw Exception('unknown role ${msg.role}'),
        });
      }
    });
  }

  void _changeHint() async {
    await Future.delayed(Duration(seconds: 5));
    if (disposed) return;
    setState(() {
      hintIndex = random.nextInt(hints.length);
    });
    _changeHint();
  }

  @override
  void dispose() {
    provider.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('AI'),
        actions: [
          IconButton(
            onPressed: () {
              AIAPI.clearHistory();
              provider.clear();
            },
            icon: Icon(Icons.delete_forever),
          ),
        ],
      ),
      body: Padding(
        padding: EdgeInsets.all(8),
        child: Column(
          verticalDirection: VerticalDirection.up,
          children: [
            Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: textController,
                    decoration: InputDecoration(hintText: hints[hintIndex]),
                  ),
                ),
                IconButton(
                  onPressed: () {
                    var text = textController.text;
                    if (text.isEmpty) {
                      text = hints[hintIndex];
                    }
                    provider.send(text).listen((event) {});
                    textController.clear();
                  },
                  icon: Icon(Icons.send),
                ),
              ],
            ),
            Padding(
              padding: EdgeInsets.symmetric(vertical: 6),
              child: Row(children: []),
            ),
            Expanded(
              child: SingleChildScrollView(
                child: Column(
                  spacing: 6,
                  children: [
                    for (final message in provider.history)
                      ChatMessageView(message: message),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
