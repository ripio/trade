use base64::{engine::general_purpose, Engine as _};
use dotenv::dotenv;
use hmac::{Hmac, Mac};
use reqwest::blocking::Client;
use reqwest::header::{HeaderMap, HeaderValue, CONTENT_TYPE};
use serde::{Deserialize, Serialize};
use sha2::Sha256;
use std::env;
use std::error::Error;
use std::time::{SystemTime, UNIX_EPOCH};

type HmacSha256 = Hmac<Sha256>;

#[derive(Serialize, Deserialize, Debug)]
struct OrderRequest {
    amount: f64,
    pair: String,
    price: u64,
    side: String,
    #[serde(rename = "type")]
    order_type: String,
}

fn main() -> Result<(), Box<dyn Error>> {
    // Load environment variables from .env file
    dotenv().ok();

    // Get API key and secret key from environment variables
    let api_key = env::var("API_KEY")?;
    let secret_key = env::var("SECRET_KEY")?;

    // Define the request parameters
    let method = "POST";
    let path = "/v4/orders";
    let url = format!("https://api.ripiotrade.co{}", path);

    // Create the request body
    let body = OrderRequest {
        amount: 0.01,
        pair: "ABC_DEF".to_string(),
        price: 300000,
        side: "buy".to_string(),
        order_type: "limit".to_string(),
    };
    let body_json = serde_json::to_string(&body)?;

    // Get current timestamp
    let timestamp = SystemTime::now()
        .duration_since(UNIX_EPOCH)?
        .as_millis()
        .to_string();

    // Create the message to sign
    let message = format!("{}{}{}{}", timestamp, method, path, body_json);

    // Create HMAC-SHA256 signature
    let mut mac = HmacSha256::new_from_slice(secret_key.as_bytes())?;
    mac.update(message.as_bytes());
    let result = mac.finalize();
    let signature = general_purpose::STANDARD.encode(result.into_bytes());

    // Set up headers
    let mut headers = HeaderMap::new();
    headers.insert("Authorization", HeaderValue::from_str(&api_key)?);
    headers.insert("timestamp", HeaderValue::from_str(&timestamp)?);
    headers.insert("signature", HeaderValue::from_str(&signature)?);
    headers.insert(CONTENT_TYPE, HeaderValue::from_static("application/json"));

    // Create the client with disabled SSL verification (for development only)
    let client = Client::builder()
        .danger_accept_invalid_certs(true)
        .build()?;

    // Make the request
    let response = client
        .post(url)
        .headers(headers)
        .body(body_json)
        .send()?;

    // Parse and print the response
    let result = response.json::<serde_json::Value>()?;
    println!("{}", serde_json::to_string_pretty(&result)?);

    Ok(())
}
