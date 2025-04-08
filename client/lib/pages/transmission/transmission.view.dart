import 'package:flutter/material.dart';
import 'package:h_nas/generated/l10n.dart';

class TransmissionView extends StatefulWidget {
  final Widget donePage;
  final Widget progressingPage;

  const TransmissionView({
    super.key,
    required this.donePage,
    required this.progressingPage,
  });

  @override
  State createState() => _TransmissionViewState();
}

class _TransmissionViewState extends State<TransmissionView>
    with SingleTickerProviderStateMixin {
  late final TabController _tabController = TabController(
    length: 2,
    vsync: this,
  );

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Padding(
          padding: EdgeInsets.all(8),
          child: Row(
            spacing: 4,
            children: [
              FilledButton(
                style: FilledButton.styleFrom(
                  backgroundColor:
                      _tabController.index == 0
                          ? ColorScheme.of(context).primary
                          : ColorScheme.of(context).onPrimary,
                  foregroundColor:
                      _tabController.index == 0
                          ? ColorScheme.of(context).onPrimary
                          : ColorScheme.of(context).primary,
                ),
                onPressed: () {
                  setState(() {
                    _tabController.animateTo(0);
                  });
                },
                child: Text(S.current.processing),
              ),
              FilledButton(
                style: FilledButton.styleFrom(
                  backgroundColor:
                      _tabController.index == 1
                          ? ColorScheme.of(context).primary
                          : ColorScheme.of(context).onPrimary,
                  foregroundColor:
                      _tabController.index == 1
                          ? ColorScheme.of(context).onPrimary
                          : ColorScheme.of(context).primary,
                ),
                onPressed: () {
                  setState(() {
                    _tabController.animateTo(1);
                  });
                },
                child: Text(S.current.done),
              ),
            ],
          ),
        ),
        Expanded(
          child: TabBarView(
            controller: _tabController,
            children: [widget.progressingPage, widget.donePage],
          ),
        ),
      ],
    );
  }
}
