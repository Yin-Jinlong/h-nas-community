part of 'login_logon.dart';

class _LoginWidget extends StatefulWidget {
  final void Function(int? uid) onMaybeUid;
  final VoidCallback onGotoLogon;

  const _LoginWidget({required this.onMaybeUid, required this.onGotoLogon});

  @override
  State createState() {
    return _LoginState();
  }
}

class _LoginState extends _BaseState<_LoginWidget> {
  final RegExp uidRex = RegExp(r'^[0-9]{12}$');
  final logid = TextEditingController(), password = TextEditingController();

  _login() {
    UserAPI.login(logid.text, password.text).then((v) {
      if (v != null) {
        Toast.showSuccess('Hi ${v.nick}!');
        UserS.user = v;
        navigatorKey.currentState?.pop();
      }
    });
  }

  @override
  void initState() {
    super.initState();
    logid.addListener(() {
      if (uidRex.hasMatch(logid.text)) {
        widget.onMaybeUid(int.parse(logid.text));
      } else {
        widget.onMaybeUid(null);
      }
    });
  }

  @override
  void dispose() {
    logid.dispose();
    password.dispose();
    super.dispose();
  }

  @override
  Widget child(BuildContext context) {
    return Column(
      spacing: 12,
      children: [
        TextFormField(
          controller: logid,
          decoration: InputDecoration(
            labelText: L.current.username,
            hintText: '${L.current.username}/id',
            prefixIcon: Icon(Icons.person),
          ),
          validator: (value) {
            if (value == null || value.isEmpty) {
              return L.current.error_empty('${L.current.username}/id');
            }
            return null;
          },
          onTapOutside: (event) {
            validate();
          },
          onChanged: (value) {
            validate();
          },
        ),
        PasswordInput(
          label: L.current.password,
          password: password,
          onValidate: validate,
        ),
        Stack(
          children: [
            Align(
              alignment: Alignment.centerRight,
              child: TextButton(
                onPressed: widget.onGotoLogon,
                child: Text(L.current.goto_logon),
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
          onPressed: isValid ? _login : null,
          child: Text(L.current.login),
        ),
      ],
    );
  }
}
