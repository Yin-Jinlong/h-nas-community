part of 'login_logon.dart';

class _LoginWidget extends StatefulWidget {
  final Function() onGotoLogon;

  const _LoginWidget({required this.onGotoLogon});

  @override
  State createState() {
    return _LoginState();
  }
}

class _LoginState extends State<_LoginWidget> {
  final logid = TextEditingController(), password = TextEditingController();

  _login() {
    API.login(logid.text, password.text).then((v) {
      if (v != null) {
        Toast.showSuccess('Hi ${v.nick}!');
        Provider.of<UserModel>(context, listen: false).set(v);
        Navigator.of(context).pop();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Expanded(
          child: Padding(
            padding: EdgeInsets.all(8),
            child: TextField(
              controller: logid,
              decoration: InputDecoration(
                labelText: S.current.username,
                hintText: '${S.current.username}/id',
                hintStyle: TextStyle(color: Colors.grey),
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.person),
              ),
            ),
          ),
        ),
        Expanded(
          child: Padding(
            padding: EdgeInsets.all(8),
            child: TextField(
              maxLength: 18,
              obscureText: true,
              controller: password,
              decoration: InputDecoration(
                labelText: S.current.password,
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.lock),
              ),
            ),
          ),
        ),
        Stack(
          children: [
            Align(
              alignment: Alignment.centerRight,
              child: TextButton(
                onPressed: widget.onGotoLogon,
                child: Text(S.current.goto_logon),
              ),
            ),
          ],
        ),
        ElevatedButton(
          style: ElevatedButton.styleFrom(
            backgroundColor: ColorScheme.of(context).primary,
            foregroundColor: ColorScheme.of(context).onPrimary,
            minimumSize: Size(double.infinity, 50),
          ),
          onPressed: _login,
          child: Text(S.current.login),
        ),
      ],
    );
  }
}
