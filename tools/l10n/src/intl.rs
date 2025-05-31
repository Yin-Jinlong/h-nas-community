use crate::writer::Writer;
use regex::Regex;
use std::collections::BTreeMap;
use std::fs::File;
use std::io::BufWriter;
use std::path::PathBuf;

pub(crate) struct Intl {
    pub default: bool,
    pub name: String,
    pub language_code: String,
    pub script_code: Option<String>,
    pub country_code: Option<String>,
    pub values: BTreeMap<String, String>,
}

impl Intl {
    pub(crate) fn new() -> Self {
        Intl {
            default: false,
            name: "".to_string(),
            language_code: "".to_string(),
            script_code: None,
            country_code: None,
            values: BTreeMap::new(),
        }
    }

    pub fn file_name(&self) -> String {
        let mut res = format!("intl_{}", self.language_code.to_lowercase());
        if let Some(sc) = self.script_code.clone() {
            res += &format!("_{}", sc.to_lowercase());
        }
        if let Some(cc) = self.country_code.clone() {
            res += &format!("_{}", cc.to_lowercase());
        }
        res += ".dart";

        res
    }

    pub fn class_name(&self) -> String {
        let mut res = format!("L{}", Self::first_upper(&self.language_code));
        if let Some(sc) = self.script_code.clone() {
            res += &Self::first_upper(&sc);
        }
        if let Some(cc) = self.country_code.clone() {
            res += &Self::first_upper(&cc);
        }

        res
    }

    pub fn to_locale(&self) -> String {
        format!(
            "Locale.fromSubtags(languageCode: '{}',  scriptCode: {}, countryCode: {})",
            self.language_code,
            self.script_code_str(),
            self.country_code_str()
        )
    }

    fn script_code_str(&self) -> String {
        match self.script_code.clone() {
            None => "null".into(),
            Some(code) => format!("'{}'", code),
        }
    }

    fn country_code_str(&self) -> String {
        match self.country_code.clone() {
            None => "null".into(),
            Some(code) => format!("'{}'", code),
        }
    }

    pub fn write_to(&self, dir: &PathBuf) {
        let file =
            File::create(&format!("{}/{}", dir.to_str().unwrap(), self.file_name())).unwrap();
        println!("Generating {}...", self.file_name());
        let mut writer = BufWriter::new(file);

        writer.write_time_comment();
        writer.writeln();
        writer.write_str(
            r"import 'intl.dart';

class ",
        );
        writer.write_str(&self.class_name());
        writer.write_str(
            r" extends L{
  static const instance = ",
        );
        writer.write_str(&self.class_name());
        writer.write_str("();\n");

        writer.writeln();
        self.write_constructor(&mut writer);

        writer.writeln();
        for (key, value) in self.values.iter() {
            Self::write_message(&mut writer, key, value)
        }

        writer.writeln();
        writer.write_str("}");
        writer.writeln();
    }

    fn write_constructor(&self, writer: &mut impl Writer) {
        writer.write_str("  const ");
        writer.write_str(&self.class_name());
        writer.write_str("() : super(localName:'");
        writer.write_str(&self.name);
        writer.write_str("');");
        writer.writeln();
    }

    fn write_message(writer: &mut impl Writer, name: &str, value: &str) {
        writer.write_str("  @override\n");
        writer.write_str(&Self::message_define(name, value, ""));
        writer.writeln();
        writer.writeln();
    }

    fn first_upper(str: &String) -> String {
        let lower = str.to_lowercase();
        let mut chars = lower.chars();
        match chars.next() {
            None => "".to_string(),
            Some(c) => c.to_uppercase().collect::<String>() + chars.as_str(),
        }
    }

    pub fn message_define(name: &str, value: &str, caller: &str) -> String {
        let reg = Regex::new(r"\{(.*?)}").unwrap();
        let mut args: Vec<String> = vec![];

        for cap in reg.captures_iter(value) {
            args.push(cap[1].to_string());
        }

        let mut res = String::from("  String ");

        if args.is_empty() {
            res += &format!(
                "get {} => {};",
                name,
                if caller.is_empty() {
                    format!("'{}'", value.replace("{", "${"))
                } else {
                    format!("{}.{}", caller, name)
                }
            );
        } else {
            res += &format!(" {}(", name);

            for (_i, arg) in args.iter().enumerate() {
                res += "Object ";
                res += arg;
                res += ",";
            }

            res += ")=> ";

            if caller.is_empty() {
                res += &format!("'{}'", value.replace("{", "${"));
            } else {
                res += caller;
                res += ".";
                res += name;
                res += "(";

                for (_i, arg) in args.iter().enumerate() {
                    res += arg;
                    res += ",";
                }
                res += ")";
            }

            res += ";";
        }
        res
    }
}
