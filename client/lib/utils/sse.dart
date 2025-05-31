import 'dart:async';
import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:h_nas/type.g.dart';
import 'package:http/http.dart' as http;

class SSEClient {
  http.Client _client = http.Client();

  void _log(Object object) {
    if (kDebugMode) {
      print(object);
    }
  }

  Stream<String> open({
    required String url,
    required Map<String, String> header,
    JsonObject? body,
  }) async* {
    StreamController<String> streamController = StreamController();

    try {
      _client = http.Client();
      var request = http.Request("POST", Uri.parse(url));

      header.forEach((key, value) {
        request.headers[key] = value;
      });

      if (body != null) {
        request.body = jsonEncode(body);
      }

      Future<http.StreamedResponse> response = _client
          .send(request)
          .timeout(Duration(minutes: 5));

      response.asStream().listen(
        (data) {
          data.stream
              .transform(Utf8Decoder())
              .listen(
                (dataLine) {
                  streamController.sink.add(dataLine);
                },
                onDone: () {
                  streamController.sink.close();
                },
                onError: (e, s) {
                  _log('---ERROR---');
                  _log(e);
                },
              );
        },
        onError: (e, s) {
          _log('---ERROR---');
          _log(e);
        },
      );
    } catch (e) {
      _log('---ERROR---');
      _log(e);
    }
    yield* streamController.stream;
  }

  void dispose() {
    _client.close();
  }
}
