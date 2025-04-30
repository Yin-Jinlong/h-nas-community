import 'package:flutter/material.dart';
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

class _AIPageState extends State<AIPage> {
  final textController = TextEditingController();
  final provider = Llm();
  bool enableTool = false;

  @override
  void initState() {
    super.initState();
    provider.addListener(_render);
    final llm = ChatMessage.llm(content: '有什么问题欢迎向我提问。');
    provider.history = [llm];
    _loadHistory();
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
                    decoration: InputDecoration(border: OutlineInputBorder()),
                  ),
                ),
                IconButton(
                  onPressed: () {
                    provider
                        .send(textController.text, enableTool: enableTool)
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
