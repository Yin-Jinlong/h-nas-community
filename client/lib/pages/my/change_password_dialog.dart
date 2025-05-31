import 'package:flutter/material.dart';
import 'package:h_nas/components/password_input.dart';
import 'package:h_nas/global.dart';

class ChangePasswordDialog extends StatefulWidget {
  final void Function(String o, String n) onChangePassword;

  const ChangePasswordDialog({super.key, required this.onChangePassword});

  @override
  State createState() => _ChangePasswordDialogState();
}

class _ChangePasswordDialogState extends State<ChangePasswordDialog> {
  final _formKey = GlobalKey<FormState>();
  final passwordOld = TextEditingController(),
      password = TextEditingController(),
      password2 = TextEditingController();

  bool get isValid => _formKey.currentState?.validate() ?? false;

  void validate() {
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text(L.current.change_password),
      content: Form(
        key: _formKey,
        child: IntrinsicHeight(
          child: Column(
            spacing: 12,
            children: [
              PasswordInput(
                label: L.current.password_old,
                password: passwordOld,
                onValidate: validate,
              ),
              PasswordInput(
                label: L.current.password_new,
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
              ElevatedButton(
                style: ElevatedButton.styleFrom(
                  backgroundColor: ColorScheme.of(context).primary,
                  foregroundColor: ColorScheme.of(context).onPrimary,
                  minimumSize: Size(double.infinity, 50),
                ),
                onPressed:
                    isValid
                        ? () {
                          widget.onChangePassword(
                            passwordOld.text,
                            password.text,
                          );
                        }
                        : null,
                child: Text(L.current.ok),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
