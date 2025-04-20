import 'package:flutter/material.dart';
import 'package:h_nas/generated/l10n.dart';
import 'package:h_nas/utils/api.dart';
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
      appBar: AppBar(title: Text(S.current.user_management)),
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
                    label: Center(child: Text(S.current.info_username)),
                  ),
                  GridColumn(
                    columnName: _UserData.nickName,
                    label: Center(child: Text(S.current.info_nick)),
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
  static const String avatarName = 'avatar';
  static const String adminName = 'admin';

  _UserData({
    required super.uid,
    required super.username,
    required super.nick,
    super.avatar,
    required super.admin,
  });

  factory _UserData.of(UserInfo e) => _UserData(
    uid: e.uid,
    username: e.username,
    nick: e.nick,
    avatar: e.avatar,
    admin: e.admin,
  );
}

class _UserDatSource extends DataGridSource {
  int rowsPerPage = 10;
  int count = -1;

  List<_UserData> data = [];

  void load() {
    UserAPI.getUserCount().then((value) {
      count = value ?? 0;
      notifyListeners();
    });
    UserAPI.getUsers(0, 10).then((value) {
      data = value.map(_UserData.of).toList();
      rows =
          data
              .map(
                (e) => DataGridRow(
                  cells: [
                    DataGridCell(columnName: _UserData.idName, value: e.uid),
                    DataGridCell(
                      columnName: _UserData.usernameName,
                      value: e.username,
                    ),
                    DataGridCell(columnName: _UserData.nickName, value: e.nick),
                  ],
                ),
              )
              .toList();
      notifyListeners();
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
