import 'dart:async';
import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:h_nas/utils/api.dart';
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
  }) {
    StreamController<String> streamController = StreamController();
    while (true) {
      try {
        _client = http.Client();
        var request = http.Request("POST", Uri.parse(url));

        header.forEach((key, value) {
          request.headers[key] = value;
        });

        if (body != null) {
          request.body = jsonEncode(body);
        }

        Future<http.StreamedResponse> response = _client.send(request);

        response.asStream().listen(
          (data) {
            data.stream
                .transform(Utf8Decoder())
                .listen(
                  (dataLine) {
                    streamController.add(dataLine);
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
      return streamController.stream;
    }
  }

  void dispose() {
    _client.close();
  }
}
