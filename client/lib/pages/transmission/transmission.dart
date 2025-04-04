import 'package:flutter/material.dart';
import 'package:h_nas/generated/l10n.dart';

class TransmissionPage extends StatefulWidget {
  const TransmissionPage({super.key});

  @override
  State createState() => _TransmissionPageState();
}

class _TransmissionPageState extends State<TransmissionPage>
    with SingleTickerProviderStateMixin {
  late final TabController _tabController = TabController(
    length: 3,
    vsync: this,
  );

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(S.current.transmission),
        actions: [
          IconButton(
            tooltip: S.current.start_all,
            onPressed: () {},
            icon: Icon(Icons.play_arrow),
          ),
          IconButton(
            tooltip: S.current.pause_all,
            onPressed: () {},
            icon: Icon(Icons.pause),
          ),
          IconButton(
            tooltip: S.current.delete_all,
            onPressed: () {},
            icon: Icon(Icons.delete_forever),
          ),
        ],
        bottom: TabBar(
          labelColor: ColorScheme.of(context).tertiary,
          dividerColor: ColorScheme.of(context).tertiary,
          dividerHeight: 1,
          controller: _tabController,
          tabs: [
            Tab(
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [Icon(Icons.upload), Text(S.current.upload)],
              ),
            ),
            Tab(
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [Icon(Icons.download), Text(S.current.download)],
              ),
            ),
            Tab(
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [Icon(Icons.done), Text(S.current.done)],
              ),
            ),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        physics: const AlwaysScrollableScrollPhysics(),
        children: [
          for (int i = 0; i < 3; i++) Center(child: Text(S.current.no_data)),
        ],
      ),
    );
  }
}
