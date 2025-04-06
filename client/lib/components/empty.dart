import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:h_nas/utils/nerd.dart';

import '../generated/l10n.dart';

class Empty extends StatelessWidget {
  /// 是否为空
  final bool isEmpty;

  /// 空页面
  final Widget? empty;

  /// 内容
  final Widget child;

  const Empty({
    super.key,
    required this.isEmpty,
    this.empty,
    required this.child,
  });

  @override
  Widget build(BuildContext context) {
    return isEmpty
        ? empty ??
            Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  const Icon(Nerd.empty, size: 40),
                  Text(S.current.no_data),
                ],
              ),
            )
        : child;
  }
}
