use clap::{Parser, ValueEnum};
use serde::{Deserialize, Serialize};
use std::net::Ipv4Addr;

const PORT: u16 = 12000;
const ADDR: Ipv4Addr = Ipv4Addr::new(225, 1, 2, 9);
const PATH: &str = "/api";

#[derive(Parser, Debug)]
#[command(version, about, long_about = None)]
pub(crate) struct API {
    /// Service group broadcast address
    #[arg(short, long, default_value_t = ADDR)]
    pub(crate) addr: Ipv4Addr,

    /// Service group broadcast count
    #[arg(short, long, default_value_t = u16::MAX)]
    pub(crate) count: u16,

    /// Broadcast send duration in ms
    #[arg(short, long, default_value_t = 2000)]
    pub(crate) duration: u64,

    /// Service group broadcast port
    #[arg(short, long, default_value_t = PORT)]
    pub(crate) group_port: u16,

    /// API port
    #[arg(short='P', long, default_value_t = PORT)]
    pub(crate) port: u16,

    /// API path
    #[arg(short, long, default_value_t = PATH.to_string())]
    pub(crate) path: String,

    /// API schema
    #[arg(short, long,value_enum,  default_value_t = APISchema::HTTP)]
    pub(crate) schema: APISchema,
}

#[derive(Debug, Clone, ValueEnum)]
pub(crate) enum APISchema {
    HTTP,
    HTTPS,
}

#[derive(Debug, Serialize, Deserialize)]
pub(crate) struct APIInfo {
    schema: String,
    port: u16,
    path: String,
}

impl API {
    pub(crate) fn to_info(&self) -> APIInfo {
        APIInfo {
            schema: match self.schema {
                APISchema::HTTP => "http".to_string(),
                APISchema::HTTPS => "https".to_string(),
            },
            port: self.port,
            path: self.path.clone(),
        }
    }
}
