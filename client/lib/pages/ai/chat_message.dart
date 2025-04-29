import 'package:flutter/cupertino.dart';

enum ChatRole { user, llm }

class ChatMessage with ChangeNotifier {
  final ChatRole role;
  String _value;

  String get content => _value;

  ChatMessage(this.role, String content) : _value = content;

  factory ChatMessage.user(String content) {
    return ChatMessage(ChatRole.user, content);
  }

  factory ChatMessage.llm({String content = ''}) {
    return LlmChatMessage()..append(content);
  }

  void append(String content) {
    _value += content;
    notifyListeners();
  }
}

class LlmChatMessage extends ChatMessage {
  static const String thinkStart = '<think>';
  static const String thinkEnd = '</think>';

  bool showThink = false;

  bool get hasThink => _value.startsWith(thinkStart);

  bool get thinking => hasThink && !_value.contains(thinkEnd);

  LlmChatMessage() : super(ChatRole.llm, '');

  String get think {
    if (!hasThink) return '';
    final endIndex = _value.indexOf(thinkEnd);
    return endIndex < 0
        ? _value.substring(thinkStart.length)
        : _value.substring(thinkStart.length, endIndex);
  }

  @override
  String get content {
    if (!hasThink) return _value;
    final endIndex = _value.indexOf(thinkEnd);
    return endIndex < 0 ? '' : _value.substring(endIndex + thinkEnd.length);
  }
}
