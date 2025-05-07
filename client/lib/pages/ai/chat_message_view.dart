import 'package:flutter/material.dart';
import 'package:flutter_markdown/flutter_markdown.dart';
import 'package:h_nas/md/code_builder.dart';
import 'package:h_nas/pages/ai/chat_message.dart';
import 'package:tdtx_nf_icons/tdtx_nf_icons.dart';

class ChatMessageView extends StatefulWidget {
  final ChatMessage message;

  const ChatMessageView({super.key, required this.message});

  @override
  State createState() => _ChatMessageViewState();
}

class _ChatMessageViewState extends State<ChatMessageView> {
  bool isUser = false;

  @override
  void initState() {
    super.initState();
    isUser = widget.message.role == ChatRole.user;
    widget.message.addListener(_render);
  }

  void _render() {
    setState(() {});
  }

  @override
  void didUpdateWidget(covariant ChatMessageView oldWidget) {
    super.didUpdateWidget(oldWidget);
    isUser = widget.message.role == ChatRole.user;
  }

  @override
  void dispose() {
    widget.message.removeListener(_render);
    super.dispose();
  }

  Widget _llmContent(LlmChatMessage message, MarkdownStyleSheet style) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (message.hasThink)
          Padding(
            padding: EdgeInsets.only(bottom: 8),
            child: IntrinsicHeight(
              child: Opacity(
                opacity: 0.7,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    IntrinsicWidth(
                      child: InkWell(
                        onTap: () {
                          setState(() {
                            message.showThink = !message.showThink;
                          });
                        },
                        child: Row(
                          children: [
                            Icon(Icons.lightbulb),
                            Text(message.thinking ? '正在思考' : '思考完成'),
                            if (message.thinking)
                              SizedBox.square(
                                dimension: 14,
                                child: CircularProgressIndicator(
                                  strokeWidth: 2,
                                ),
                              ),
                            Icon(
                              message.showThink
                                  ? Icons.arrow_drop_up
                                  : Icons.arrow_drop_down,
                            ),
                          ],
                        ),
                      ),
                    ),
                    if (message.showThink)
                      MarkdownBody(
                        data: message.think,
                        selectable: true,
                        styleSheet: style,
                        builders: {'code': CodeBuilder(style)},
                      ),
                  ],
                ),
              ),
            ),
          ),
        if (widget.message.content.isEmpty)
          SizedBox.square(
            dimension: 16,
            child: CircularProgressIndicator(strokeWidth: 2),
          )
        else
          MarkdownBody(
            data: message.content,
            selectable: true,
            styleSheet: style,
            builders: {'code': CodeBuilder(style)},
          ),
      ],
    );
  }

  Widget _chatContent(BuildContext context) {
    final style = MarkdownStyleSheet.fromTheme(
      Theme.of(context),
    ).copyWith(code: TextStyle(fontFamily: 'JetBrainsMapleMono'));
    return isUser
        ? MarkdownBody(
          data: widget.message.content,
          selectable: true,
          styleSheet: style,
          builders: {'code': CodeBuilder(style)},
        )
        : _llmContent(widget.message as LlmChatMessage, style);
  }

  List<Widget> _content(BuildContext context) {
    return [
      Icon(isUser ? Icons.person : TDTxNFIcons.nf_md_robot),
      Flexible(
        child: Card(
          child: Padding(
            padding: EdgeInsets.all(8),
            child: _chatContent(context),
          ),
        ),
      ),
      SizedBox.square(dimension: 24),
    ];
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        Align(
          alignment: isUser ? Alignment.topRight : Alignment.topLeft,
          child: Padding(
            padding: EdgeInsets.all(8),
            child: IntrinsicWidth(
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children:
                    isUser
                        ? _content(context).reversed.toList()
                        : _content(context),
              ),
            ),
          ),
        ),
      ],
    );
  }
}
