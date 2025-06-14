import 'dart:async';

import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:h_nas/api/api.dart';
import 'package:h_nas/utils/sse.dart';

import 'chat_message.dart';

class Llm with ChangeNotifier {
  SSEClient? _client;

  final List<ChatMessage> _history = [];

  Iterable<ChatMessage> get history => _history;

  Stream<String> send(String prompt) async* {
    final userMessage = ChatMessage.user(prompt);
    final llmMessage = ChatMessage.llm();
    _history.addAll([userMessage, llmMessage]);

    notifyListeners();

    final stream = StreamController<String>();

    _client = SSEClient();
    _client!
        .open(
          url: AIAPIURL.chat(),
          header: {
            ...API.tokenHeader(),
            Headers.contentTypeHeader: Headers.jsonContentType,
          },
          body: {'message': prompt},
        )
        .listen(
          (event) {
            llmMessage.append(event);
            stream.add(event);
          },
          onDone: () {
            llmMessage.end();
          },
        );

    yield* stream.stream;

    notifyListeners();
  }

  void add(ChatMessage message) {
    _history.add(message);
    notifyListeners();
  }

  set history(Iterable<ChatMessage> history) {
    _history.clear();
    _history.addAll(history);
    notifyListeners();
  }

  void clear() {
    _history.clear();
    notifyListeners();
  }

  @override
  void dispose() {
    _client?.dispose();
    super.dispose();
  }
}
