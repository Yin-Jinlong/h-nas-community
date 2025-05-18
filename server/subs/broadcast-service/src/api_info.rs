use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize)]
pub(crate) struct APIInfo {
    pub(crate) schema: String,
    pub(crate) port: u16,
    pub(crate) path: String,
}
