import 'package:flutter/material.dart';
import 'package:h_nas/anim/scale_animated_switcher.dart';
import 'package:h_nas/components/empty.dart';
import 'package:h_nas/components/file_preview_view.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/main.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/dispose.dart';
import 'package:h_nas/utils/file_utils.dart';
import 'package:h_nas/utils/storage_size.dart';

part 'search.file_list.dart';

class SearchPage extends StatefulWidget {
  const SearchPage({super.key});

  @override
  State createState() => _SearchPageState();
}

class _SearchPageState extends State<SearchPage> {
  late final TextEditingController _searchController = TextEditingController();
  late final ScrollController _scrollController = ScrollController();
  String _cacheText = '';
  List<FileInfo> files = [];
  bool private = false, loading = false, hasMore = true;

  @override
  void initState() {
    super.initState();
    _searchController.addListener(() {
      if (_searchController.text.isEmpty) {
        setState(() {
          files.clear();
          hasMore = true;
          loading = false;
        });
        return;
      }
      _cacheText = _searchController.text;
      _update();
    });
    _scrollController.addListener(_onEnd);
  }

  void _onEnd() {
    if (!hasMore || loading) return;
    if (_scrollController.position.maxScrollExtent < 10 ||
        _scrollController.position.pixels >=
            _scrollController.position.maxScrollExtent) {
      setState(() {
        _loadMore();
      });
    }
  }

  void _loadMore() async {
    if (files.isEmpty) return;
    loading = true;
    await Future.delayed(const Duration(milliseconds: 500));
    final list = await FileAPI.searchFiles(
      _searchController.text,
      private: private,
      lastPath: files.last.fullPath,
    );
    if (disposed) return;
    setState(() {
      loading = false;
      hasMore = list.isNotEmpty;
      if (hasMore) files.addAll(list);
    });
  }

  void _update() async {
    await Future.delayed(const Duration(seconds: 1));
    if (disposed) return;
    if (_cacheText != _searchController.text) return;
    setState(() {
      files.clear();
    });
    _cacheText = '';
    hasMore = true;
    final list = await FileAPI.searchFiles(
      _searchController.text,
      private: private,
    );
    files = list;
    if (disposed) return;
    setState(() {});
    if (files.isNotEmpty) {
      WidgetsBinding.instance.addPostFrameCallback((timeStamp) {
        _onEnd();
      });
    }
  }

  @override
  void didChangeDependencies() {
    private = ModalRoute.of(context)?.settings.arguments as bool;
    super.didChangeDependencies();
  }

  @override
  void dispose() {
    _searchController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          Container(
            color: ColorScheme.of(context).primary,
            padding: const EdgeInsets.all(8),
            child: SafeArea(
              child: Row(
                children: [
                  Expanded(
                    child: TextSelectionTheme(
                      data: TextSelectionThemeData(
                        selectionColor: ColorScheme.of(context).onPrimary,
                      ),
                      child: TextField(
                        controller: _searchController,
                        cursorColor: ColorScheme.of(context).onPrimary,
                        decoration: InputDecoration(
                          prefixIcon: Hero(
                            tag: 'search',
                            child: const Icon(Icons.search),
                          ),
                          suffixIcon: ScaleAnimatedSwitcher(
                            child:
                                _searchController.text.isEmpty
                                    ? null
                                    : IconButton(
                                      onPressed: () {
                                        _searchController.clear();
                                        setState(() {});
                                      },
                                      icon: const Icon(Icons.clear),
                                    ),
                          ),
                          border: OutlineInputBorder(
                            borderSide: BorderSide(
                              color: ColorScheme.of(context).onPrimary,
                            ),
                          ),
                          focusedBorder: OutlineInputBorder(
                            borderSide: BorderSide(),
                          ),
                        ),
                      ),
                    ),
                  ),
                  TextButton(
                    style: TextButton.styleFrom(
                      foregroundColor: ColorScheme.of(context).onPrimary,
                    ),
                    onPressed: () {
                      navigatorKey.currentState?.pop();
                    },
                    child: Text(S.current.cancel),
                  ),
                ],
              ),
            ),
          ),
          Expanded(
            child: Empty(
              isEmpty: files.isEmpty,
              child: ListView.separated(
                controller: _scrollController,
                padding: EdgeInsets.zero,
                itemCount: files.length + (hasMore ? 0 : 1),
                itemBuilder: (context, index) {
                  if (index == files.length) {
                    return Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [Text(S.current.no_more)],
                    );
                  }
                  final file = files[index];
                  return _fileListItem(
                    context,
                    file,
                    private: private,
                    onTap: () {},
                  );
                },
                separatorBuilder: (BuildContext context, int index) {
                  return Divider(
                    height: 0,
                    color: Colors.grey.withValues(alpha: 0.5),
                  );
                },
              ),
            ),
          ),
          if (loading)
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                SizedBox.square(
                  dimension: 20,
                  child: CircularProgressIndicator(strokeWidth: 2),
                ),
                Text(S.current.loading),
              ],
            ),
        ],
      ),
    );
  }
}
