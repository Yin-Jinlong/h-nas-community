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
        Toast.showSuccess(S.current.action_success(S.current.logon));
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
            labelText: S.current.username,
            hintText: S.current.username,
            prefixIcon: Icon(Icons.person),
          ),
          validator: (value) {
            if (value == null || value.isEmpty) {
              return S.current.error_empty(S.current.username);
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
          maxLength: 18,
          obscureText: true,
          controller: password,
          decoration: InputDecoration(
            labelText: S.current.password,
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
        TextFormField(
          maxLength: 18,
          obscureText: true,
          controller: password2,
          decoration: InputDecoration(
            labelText: S.current.password2,
            prefixIcon: Icon(Icons.lock),
            suffixIcon: EditFieldUtils.clearButton(password2, () {
              validate();
            }),
          ),
          validator: (value) {
            if (value == null || value.isEmpty) {
              return S.current.error_empty(S.current.password2);
            }
            if (value != password.text) {
              return S.current.password_not_match;
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
          onPressed: isValid ? _logon : null,
          child: Text(S.current.logon),
        ),
      ],
    );
  }
}
