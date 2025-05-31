use crate::intl::Intl;
use std::path::PathBuf;
use std::time::SystemTime;

pub struct IntlItem {
    pub intl: Intl,
    pub path:PathBuf,
    pub last_modified:SystemTime,
}
