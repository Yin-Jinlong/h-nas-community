import 'package:flutter/material.dart';
import 'package:flutter_breadcrumb/flutter_breadcrumb.dart';
import 'package:h_nas/components/file_preview_view.dart';
import 'package:h_nas/model/thumbnail_model.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/file_utils.dart';
import 'package:h_nas/utils/storage_size.dart';
import 'package:provider/provider.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<StatefulWidget> createState() {
    return _HomePageState();
  }
}

class _HomePageState extends State<HomePage> {
  List<FileInfo> files = [];
  List<String> dirs = [];

  @override
  void initState() {
    super.initState();
    updateFiles();
  }

  updateFiles() {
    API.getPublicFiles('/${dirs.join('/')}').then((v) {
      setState(() {
        files = v;
      });
    });
  }

  enterFolder(String dir) {
    setState(() {
      dirs.add(dir);
    });
    updateFiles();
  }

  @override
  Widget build(BuildContext context) {
    final textTheme = Theme.of(context).textTheme;

    final thumbnailCache = ThumbnailModel();

    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text('h-nas'),
        actions: [
          Tooltip(
            message: '刷新',
            child: IconButton(
              onPressed: () {
                setState(() {
                  thumbnailCache.clear();
                  files = [];
                });
                updateFiles();
              },
              icon: Icon(Icons.refresh),
            ),
          ),
          Tooltip(
            message: '登录',
            child: IconButton(onPressed: () {}, icon: const Icon(Icons.person)),
          ),
        ],
      ),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Text(
                '当前：',
                style: textTheme.titleMedium?.copyWith(
                  fontWeight: FontWeight.normal,
                ),
              ),
              BreadCrumb(
                divider: Icon(Icons.chevron_right),
                items: [
                  BreadCrumbItem(
                    content: Text(
                      '/',
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    onTap: () {
                      setState(() {
                        dirs.clear();
                        updateFiles();
                      });
                    },
                  ),
                  for (var i = 0; i < dirs.length; i++)
                    BreadCrumbItem(
                      content: Text(
                        dirs[i],
                        style: Theme.of(context).textTheme.titleMedium,
                      ),
                      onTap: () {
                        setState(() {
                          dirs.removeRange(i + 1, dirs.length);
                          updateFiles();
                        });
                      },
                    ),
                ],
              ),
            ],
          ),
          ChangeNotifierProvider.value(
            value: thumbnailCache,
            child: Expanded(
              child:
                  files.isNotEmpty
                      ? ListView(
                        children: [
                          for (var file in files)
                            ListTile(
                              title: Text(file.name),
                              subtitle: Text(
                                DateTime.fromMillisecondsSinceEpoch(
                                  file.createTime,
                                ).toString(),
                              ),
                              leading: FilePreviewView(fileInfo: file),
                              trailing: Text(file.size.storageSizeStr),
                              onTap: () {
                                if (file.isFolder) {
                                  enterFolder(file.name);
                                }
                              },
                            ),
                        ],
                      )
                      : const Center(child: Text('No Data')),
            ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        child: Icon(Icons.add),
        onPressed: () {},
      ),
    );
  }
}
