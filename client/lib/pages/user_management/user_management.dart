import 'package:flutter/material.dart';
import 'package:h_nas/api/api.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/utils/storage_size.dart';
import 'package:syncfusion_flutter_datagrid/datagrid.dart';

class UserManagementPage extends StatefulWidget {
  const UserManagementPage({super.key});

  @override
  State<UserManagementPage> createState() => _UserManagementPageState();
}

class _UserManagementPageState extends State<UserManagementPage> {
  final _userDatSource = _UserDatSource();

  @override
  void initState() {
    super.initState();

    _userDatSource.load();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(L.current.user_management)),
      body: SizedBox.expand(
        child: Column(
          children: [
            Expanded(
              child: SfDataGrid(
                rowsPerPage: _userDatSource.rowsPerPage,
                columns: [
                  GridColumn(
                    columnName: _UserData.idName,
                    label: Center(child: Text('ID')),
                  ),
                  GridColumn(
                    columnName: _UserData.usernameName,
                    label: Center(child: Text(L.current.info_username)),
                  ),
                  GridColumn(
                    columnName: _UserData.nickName,
                    label: Center(child: Text(L.current.info_nick)),
                  ),
                  GridColumn(
                    columnName: _UserData.usageName,
                    label: Center(
                      child: Text(L.current.info_user_storage_usage),
                    ),
                  ),
                ],
                source: _userDatSource,
              ),
            ),
            Row(
              children: [
                Expanded(
                  child: SfDataPager(
                    pageCount:
                        (_userDatSource.count - 1) ~/
                            _userDatSource.rowsPerPage +
                        1,
                    delegate: _userDatSource,
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _UserData extends UserInfo {
  static const String idName = 'id';
  static const String usernameName = 'user_name';
  static const String nickName = 'nick';
  static const String adminName = 'admin';
  static const String usageName = 'usage';

  int? usage;

  _UserData({
    required super.uid,
    required super.username,
    required super.nick,
    required super.admin,
  });

  factory _UserData.of(UserInfo e) =>
      _UserData(uid: e.uid, username: e.username, nick: e.nick, admin: e.admin);
}

class _UserDatSource extends DataGridSource {
  int rowsPerPage = 10;
  int count = -1;

  List<_UserData> data = [];

  void _updateRows() {
    rows =
        data.map((e) {
          return DataGridRow(
            cells: [
              DataGridCell(columnName: _UserData.idName, value: e.uid),
              DataGridCell(
                columnName: _UserData.usernameName,
                value: e.username,
              ),
              DataGridCell(columnName: _UserData.nickName, value: e.nick),
              DataGridCell(
                columnName: _UserData.usageName,
                value: e.usage?.storageSizeStr ?? L.current.loading,
              ),
            ],
          );
        }).toList();
    notifyListeners();
  }

  void load() {
    UserAPI.getUserCount().then((value) {
      count = value;
      notifyListeners();
    });
    UserAPI.getUsers(0, 10).then((value) {
      data = value.map(_UserData.of).toList();
      _updateRows();
      for (var e in data) {
        FileAPI.getUserStorageUsage(uid: e.uid).then((value) {
          e.usage = value;
          _updateRows();
        });
      }
    });
  }

  @override
  DataGridRowAdapter buildRow(DataGridRow row) {
    return DataGridRowAdapter(
      cells:
          row.getCells().map<Widget>((e) {
            return Container(
              alignment: Alignment.center,
              child: Text(e.value.toString()),
            );
          }).toList(),
    );
  }

  @override
  List<DataGridRow> rows = [];
}
