import 'package:flutter/cupertino.dart';

enum ChatRole { user, llm }

class ChatMessage with ChangeNotifier {
  final ChatRole role;
  String _value;

  bool _end = false;

  bool get isEnded => _end;

  String get content => _value;

  ChatMessage(this.role, String content) : _value = content;

  factory ChatMessage.user(String content) {
    var message = ChatMessage(ChatRole.user, content);
    message.end();
    return message;
  }

  factory ChatMessage.llm({String content = ''}) {
    return LlmChatMessage()..append(content);
  }

  void append(String content) {
    if (_end) throw Exception('消息已结束');
    _value += content;
    notifyListeners();
  }

  void end() {
    _end = true;
  }
}

class LlmChatMessage extends ChatMessage {
  static const String thinkStart = '<think>';
  static const String thinkEnd = '</think>';

  bool showThink = false;

  bool get hasThink => _value.startsWith(thinkStart);

  bool get thinking => hasThink && _count(thinkStart) != _count(thinkEnd);

  LlmChatMessage() : super(ChatRole.llm, '');

  String get think {
    if (!hasThink) return '';
    if (thinking) {
      return _value.replaceAll(thinkStart, '').replaceAll(thinkEnd, '');
    }
    final endIndex = _value.lastIndexOf(thinkEnd);
    return endIndex < 0
        ? _value.substring(thinkStart.length)
        : _value
            .substring(thinkStart.length, endIndex)
            .replaceAll(thinkStart, '')
            .replaceAll(thinkEnd, '');
  }

  @override
  String get content {
    if (!hasThink) return _value;
    if (thinking) return '';
    final endIndex = _value.lastIndexOf(thinkEnd);
    return endIndex < 0 ? '' : _value.substring(endIndex + thinkEnd.length);
  }

  int _count(String str) {
    var count = 0;
    var index = 0;
    while (index < _value.length) {
      final startIndex = _value.indexOf(str, index);
      if (startIndex < 0) break;
      count++;
      index = startIndex + str.length;
    }
    return count;
  }
}
