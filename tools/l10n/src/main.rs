use crate::intl::Intl;
use crate::intl_item::IntlItem;
use crate::intl_writer::{IntlMainWriter, IntlWriter};
use crate::options::Options;
use crate::writer::Writer;
use clap::Parser;
use std::fs;
use std::fs::File;
use std::io::{BufWriter, Write};
use std::path::PathBuf;
use std::thread::sleep;
use std::time::Duration;

mod intl;
mod intl_item;
mod intl_writer;
mod options;
mod writer;

fn main() {
    let options = Options::parse();

    let mut intls: Vec<IntlItem> = vec![];

    for entry in fs::read_dir(options.dir.clone()).expect("Failed to read dir") {
        if let Ok(entry) = entry {
            if let Ok(file_type) = entry.file_type() {
                if file_type.is_file() {
                    let path = entry.path();
                    if path.extension().and_then(|s| s.to_str()) == Some("json") {
                        let r =
                            parse(fs::read_to_string(path.clone()).expect("Failed to read file"));
                        if let Some(intl) = r {
                            intls.push(IntlItem {
                                path: path.clone(),
                                intl,
                                last_modified: File::open(path)
                                    .unwrap()
                                    .metadata()
                                    .unwrap()
                                    .modified()
                                    .unwrap(),
                            });
                        }
                    }
                }
            }
        }
    }

    {
        let file = File::create(format!("{}/{}", options.dir.clone(), "intl.dart")).unwrap();
        let mut writer = BufWriter::new(file);
        println!("Generating intl.dart...");
        writer.write_intls(&intls);
    }

    let dir = PathBuf::from(options.dir);

    for item in &intls {
        item.intl.write_to(&dir);
    }

    if options.watch {
        loop {
            for item in &mut intls {
                let last_modified = File::open(item.path.clone())
                    .unwrap()
                    .metadata()
                    .unwrap()
                    .modified()
                    .unwrap();
                if last_modified != item.last_modified {
                    item.last_modified = last_modified;
                    println!("{} changed", item.path.display());
                    item.intl.write_to(&dir);
                }
            }
            sleep(Duration::from_secs(5));
        }
    }
}

impl Writer for BufWriter<File> {
    fn write_str(&mut self, text: &str) {
        self.write(text.as_bytes()).unwrap();
    }
}

impl IntlMainWriter for BufWriter<File> {}
impl IntlWriter for BufWriter<File> {}

fn parse(text: String) -> Option<Intl> {
    let res = json::parse(&text);
    let json = res.ok()?;

    let mut res = Intl::new();

    for (key, value) in json.entries() {
        match key {
            "@" => res.name = value.as_str().expect("Must is a string").to_string(),
            "@default" => res.default = value.as_bool().expect("Must is a bool"),
            "@language_code" => {
                res.language_code = value.as_str().expect("Must is a string").to_string();
            }
            "@country_code" => {
                res.country_code = Some(value.as_str().expect("Must is a string").to_string());
            }
            _ => {
                res.values.insert(
                    key.to_string(),
                    String::from(value.as_str().expect("Must is a string")),
                );
            }
        }
    }

    Some(res)
}
