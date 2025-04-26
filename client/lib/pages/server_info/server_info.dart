import 'package:flutter/material.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/storage_size.dart';

class ServerInfoPage extends StatefulWidget {
  const ServerInfoPage({super.key});

  @override
  State<ServerInfoPage> createState() => _UserManagementPageState();
}

class _UserManagementPageState extends State<ServerInfoPage> {
  FolderChildrenCount? rootCount;
  FileInfo? rootInfo;
  int? userCount;
  int? userUsage;

  @override
  void initState() {
    super.initState();
    FileAPI.getFile('/', private: false).then((value) {
      setState(() {
        rootInfo = value;
      });
    });
    FileAPI.getFolderChildrenCount('/', private: false).then((value) {
      setState(() {
        rootCount = value;
      });
    });
    UserAPI.getUserCount().then((value) {
      setState(() {
        userCount = value;
      });
    });
    FileAPI.getUserStorageUsage().then((value) {
      setState(() {
        userUsage = value;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(S.current.server_info)),
      body: SingleChildScrollView(
        child: DataTable(
          columns: [
            DataColumn(label: Text(S.current.info_item)),
            DataColumn(label: Text(S.current.info_value)),
          ],
          rows: [
            DataRow(
              cells: [
                DataCell(Text(S.current.info_root_sub)),
                DataCell(
                  Text(rootCount?.subCount.toString() ?? S.current.loading),
                ),
              ],
            ),
            DataRow(
              cells: [
                DataCell(Text(S.current.info_root_subs)),
                DataCell(
                  Text(rootCount?.subsCount.toString() ?? S.current.loading),
                ),
              ],
            ),
            DataRow(
              cells: [
                DataCell(Text(S.current.info_root_size)),
                DataCell(
                  Text(rootInfo?.size.storageSizeStr ?? S.current.loading),
                ),
              ],
            ),
            DataRow(
              cells: [
                DataCell(Text(S.current.info_user_count)),
                DataCell(Text(userCount?.toString() ?? S.current.loading)),
              ],
            ),
            DataRow(
              cells: [
                DataCell(Text(S.current.info_user_storage_usage)),
                DataCell(Text(userUsage?.storageSizeStr ?? S.current.loading)),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
