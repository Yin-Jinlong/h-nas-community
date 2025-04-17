part of 'login_logon.dart';

class _LoginWidget extends StatefulWidget {
  final Function() onGotoLogon;

  const _LoginWidget({required this.onGotoLogon});

  @override
  State createState() {
    return _LoginState();
  }
}

class _LoginState extends _BaseState<_LoginWidget> {
  final logid = TextEditingController(), password = TextEditingController();

  _login() {
    UserAPI.login(logid.text, password.text).then((v) {
      if (v != null) {
        Toast.showSuccess('Hi ${v.nick}!');
        Provider.of<UserModel>(context, listen: false).set(v);
        navigatorKey.currentState?.pop();
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
            labelText: S.current.username,
            hintText: '${S.current.username}/id',
            hintStyle: TextStyle(color: Colors.grey),
            border: OutlineInputBorder(),
            prefixIcon: Icon(Icons.person),
          ),
          validator: (value) {
            if (value == null || value.isEmpty) {
              return S.current.error_empty('${S.current.username}/id');
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
        TextFormField(
          controller: password,
          obscureText: true,
          maxLength: 18,
          decoration: InputDecoration(
            labelText: S.current.password,
            hintText: S.current.password,
            hintStyle: TextStyle(color: Colors.grey),
            border: OutlineInputBorder(),
            prefixIcon: Icon(Icons.lock),
            suffixIcon: EditFieldUtils.clearButton(password, () {
              validate();
            }),
          ),
          validator: (value) {
            if (value == null || value.isEmpty) {
              return S.current.error_empty(S.current.password);
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
          onPressed: isValid ? _login : null,
          child: Text(S.current.login),
        ),
      ],
    );
  }
}
