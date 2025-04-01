import 'package:flutter/cupertino.dart';

class TabPage extends StatefulWidget {
  final List<Widget> children;
  final int index;

  const TabPage({super.key, required this.children, required this.index});

  @override
  State createState() => _PageViewState();
}

class _PageViewState extends State<TabPage> {
  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: AnimatedSwitcher(
        duration: Duration(milliseconds: 300),
        child: widget.children[widget.index],
      ),
    );
  }
}
