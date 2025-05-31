import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:h_nas/global.dart';
import 'package:tdtx_nf_icons/tdtx_nf_icons.dart';

class Empty extends StatelessWidget {
  /// 是否为空
  final bool isEmpty;

  final bool enableScroll;

  /// 空页面
  final Widget? empty;

  /// 内容
  final Widget child;

  const Empty({
    super.key,
    required this.isEmpty,
    this.enableScroll = true,
    this.empty,
    required this.child,
  });

  Widget _content() => Center(
    child: Column(
      mainAxisAlignment: MainAxisAlignment.center,
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Icon(TDTxNFIcons.nf_oct_inbox, size: 40),
        Text(L.current.no_data),
      ],
    ),
  );

  @override
  Widget build(BuildContext context) {
    return isEmpty
        ? empty ??
            (enableScroll
                ? Stack(children: [_content(), CustomScrollView()])
                : _content())
        : child;
  }
}
