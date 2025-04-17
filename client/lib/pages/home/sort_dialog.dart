import 'package:flutter/material.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/main.dart';

enum SortType { name, createTime, updateTime, size }

class SortDialog extends StatefulWidget {
  final SortType initType;
  final bool initAsc;
  final Function(SortType type, bool asc) onSort;

  const SortDialog({
    super.key,
    required this.initType,
    required this.initAsc,
    required this.onSort,
  });

  @override
  State createState() => _SortDialogState();
}

class _SortDialogState extends State<SortDialog> {
  late SortType type;
  late bool isAsc;

  @override
  void initState() {
    super.initState();
    type = widget.initType;
    isAsc = widget.initAsc;
  }

  IconButton _sortItem(
    SortType type,
    IconData icon, {
    required String tooltip,
  }) {
    return IconButton(
      tooltip: tooltip,
      isSelected: this.type == type,
      onPressed: () {
        setState(() {
          this.type = type;
        });
      },
      icon: Icon(icon),
      selectedIcon: Icon(icon, color: ColorScheme.of(context).primary),
    );
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text(S.current.sort),
      content: IntrinsicHeight(
        child: Column(
          children: [
            Wrap(
              children: [
                _sortItem(
                  SortType.name,
                  Icons.sort_by_alpha,
                  tooltip: S.current.name,
                ),
                _sortItem(
                  SortType.createTime,
                  Icons.timer,
                  tooltip: S.current.create_time,
                ),
                _sortItem(
                  SortType.updateTime,
                  Icons.av_timer,
                  tooltip: S.current.update_time,
                ),
                _sortItem(
                  SortType.size,
                  Icons.storage,
                  tooltip: S.current.file_size,
                ),
              ],
            ),
            Divider(),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                IconButton(
                  isSelected: isAsc,
                  tooltip: S.current.sort_asc,
                  onPressed: () {
                    setState(() {
                      isAsc = true;
                    });
                  },
                  icon: Icon(Icons.arrow_upward),
                  selectedIcon: Icon(
                    Icons.arrow_upward,
                    color: ColorScheme.of(context).primary,
                  ),
                ),
                IconButton(
                  isSelected: !isAsc,
                  tooltip: S.current.sort_desc,
                  onPressed: () {
                    setState(() {
                      isAsc = false;
                    });
                  },
                  icon: Icon(Icons.arrow_downward),
                  selectedIcon: Icon(
                    Icons.arrow_downward,
                    color: ColorScheme.of(context).primary,
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
      actions: [
        TextButton(
          onPressed: () {
            setState(() {
              type = SortType.name;
              isAsc = true;
            });
          },
          child: Text(S.current.default_),
        ),
        TextButton(
          onPressed: () {
            navigatorKey.currentState?.pop();
          },
          child: Text(S.current.cancel),
        ),
        TextButton(
          onPressed: () {
            widget.onSort(type, isAsc);
            navigatorKey.currentState?.pop();
          },
          child: Text(S.current.ok),
        ),
      ],
    );
  }
}
