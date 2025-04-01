part of 'login_logon.dart';

class _LogonWidget extends StatefulWidget {
  final Function() onGotoLogin;

  const _LogonWidget({required this.onGotoLogin});

  @override
  State createState() {
    return _LogonState();
  }
}

class _LogonState extends State<_LogonWidget> {
  final logid = TextEditingController(),
      password = TextEditingController(),
      password2 = TextEditingController();

  _logon() {}

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
                hintText: S.current.username,
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
        Expanded(
          child: Padding(
            padding: EdgeInsets.all(8),
            child: TextField(
              maxLength: 18,
              obscureText: true,
              controller: password2,
              decoration: InputDecoration(
                labelText: S.current.password2,
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.lock),
              ),
            ),
          ),
        ),
        Stack(
          children: [
            Align(
              alignment: Alignment.centerLeft,
              child: TextButton(
                onPressed: widget.onGotoLogin,
                child: Text(S.current.goto_login),
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
          onPressed: _logon,
          child: Text(S.current.logon),
        ),
      ],
    );
  }
}
