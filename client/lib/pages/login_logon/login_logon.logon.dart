part of 'login_logon.dart';

class _LogonWidget extends StatefulWidget {
  final VoidCallback onGotoLogin;

  const _LogonWidget({required this.onGotoLogin});

  @override
  State createState() {
    return _LogonState();
  }
}

class _LogonState extends _BaseState<_LogonWidget> {
  final logid = TextEditingController(),
      password = TextEditingController(),
      password2 = TextEditingController();

  void _logon() {
    UserAPI.logon(logid.text, password.text).then((res) {
      if (res == true) {
        Toast.showSuccess(L.current.action_success(L.current.logon));
        widget.onGotoLogin();
      }
    });
  }

  @override
  void dispose() {
    logid.dispose();
    password.dispose();
    password2.dispose();
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
            hintText: L.current.username,
            prefixIcon: Icon(Icons.person),
          ),
          validator: (value) {
            if (value == null || value.isEmpty) {
              return L.current.error_empty(L.current.username);
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
        PasswordInput(
          label: L.current.password2,
          password: password2,
          onValidate: validate,
          validator: (value) {
            if (value != password.text) {
              return L.current.password_not_match;
            }
            return null;
          },
        ),
        Stack(
          children: [
            Align(
              alignment: Alignment.centerLeft,
              child: TextButton(
                onPressed: widget.onGotoLogin,
                child: Text(L.current.goto_login),
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
          onPressed: isValid ? _logon : null,
          child: Text(L.current.logon),
        ),
      ],
    );
  }
}
