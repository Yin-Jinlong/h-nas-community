import 'dart:math';

import 'package:flutter/material.dart';
import 'package:h_nas/components/dispose.dart';
import 'package:h_nas/components/switch_button.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/pages/ai/chat_message_view.dart';
import 'package:h_nas/utils/api.dart';

import 'Llm.dart';
import 'chat_message.dart';

class AIPage extends StatefulWidget {
  const AIPage({super.key});

  @override
  State<StatefulWidget> createState() => _AIPageState();
}

class _AIPageState extends DisposeFlagState<AIPage> {
  final textController = TextEditingController();
  final provider = Llm();
  bool enableTool = false;
  late int hintIndex;
  static const List<String> hintsWithoutTool = [
    '你是谁',
    '你能做什么',
    '系统是干什么的',
    '系统介绍',
    '项目链接',
    '讲个笑话',
    '来玩成语接龙吧',
    '帮我翻译',
    '写篇小小说',
  ];
  static const List<String> hintsWithTool = [
    ...hintsWithoutTool,
    '现在几点了',
    '信阳市天气怎么样',
  ];
  final random = Random();

  List<String> get hints => enableTool ? hintsWithTool : hintsWithoutTool;

  @override
  void initState() {
    super.initState();
    hintIndex = random.nextInt(hints.length);
    provider.addListener(_render);
    final llm = ChatMessage.llm(content: '有什么问题欢迎向我提问。');
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
          'assistant' => ChatMessage.llm(content: msg.content),
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
                    decoration: InputDecoration(
                      hintText: hints[hintIndex],
                      hintStyle: TextStyle(color: Colors.grey),
                      border: OutlineInputBorder(),
                    ),
                  ),
                ),
                IconButton(
                  onPressed: () {
                    var text = textController.text;
                    if (text.isEmpty) {
                      text = hints[hintIndex];
                    }
                    provider
                        .send(text, enableTool: enableTool)
                        .listen((event) {});
                    textController.clear();
                  },
                  icon: Icon(Icons.send),
                ),
              ],
            ),
            Padding(
              padding: EdgeInsets.symmetric(vertical: 6),
              child: Row(
                children: [
                  SwitchButton(
                    selected: enableTool,
                    onPressed: () {
                      setState(() {
                        enableTool = !enableTool;
                        hintIndex = random.nextInt(hints.length);
                      });
                    },
                    child: Text(
                      S.current.ai_tool_status(
                        enableTool ? S.current.enabled : S.current.disabled,
                      ),
                    ),
                  ),
                ],
              ),
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
