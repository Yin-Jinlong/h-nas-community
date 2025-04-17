import 'package:flutter/material.dart';
import 'package:h_nas/generated/l10n.dart';

class MyPage extends StatefulWidget{
  const MyPage({super.key});

  @override
  State createState()=> _MyPageState();
}

class _MyPageState extends State<MyPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(S.current.my),
      ),
    );
  }
}