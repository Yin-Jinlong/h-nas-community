use serde::{Deserialize, Serialize};
use std::fs;

#[derive(Debug, Serialize, Deserialize)]
pub(crate) struct API {
    schema: String,
    port: i16,
    path: String,
}

impl API {
    pub(crate) fn load(file: &str) -> API {
        let str = fs::read_to_string(file).unwrap();
        serde_yaml::from_str(&str).unwrap()
    }
}
