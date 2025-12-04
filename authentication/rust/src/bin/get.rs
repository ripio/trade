use base64::{engine::general_purpose, Engine as _};
use dotenvy::dotenv;
use hmac::{Hmac, Mac};
use reqwest::blocking::Client;
use reqwest::header::{HeaderMap, HeaderValue, CONTENT_TYPE};
use sha2::Sha256;
use std::env;
use std::error::Error;
use std::time::{SystemTime, UNIX_EPOCH};

type HmacSha256 = Hmac<Sha256>;

fn main() -> Result<(), Box<dyn Error>> {
    // Load environment variables from .env file
    dotenv().ok();

    // Get API key and secret key from environment variables
    let api_key = env::var("API_KEY")?;
    let secret_key = env::var("SECRET_KEY")?;

    // Define the request parameters
    let method = "GET";
    let path = "/trade/orders";
    let query = "pair=BTC_BRL";
    let full_path = format!("{}?{}", path, query);
    let url = format!("https://api.ripio.com{}", full_path);

    // Get current timestamp
    let timestamp = SystemTime::now()
        .duration_since(UNIX_EPOCH)?
        .as_millis()
        .to_string();

    // Create the message to sign
    let message = format!("{}{}{}", timestamp, method, path);

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
        .get(url)
        .headers(headers)
        .send()?;

    // Parse and print the response
    let result = response.json::<serde_json::Value>()?;
    println!("{}", serde_json::to_string_pretty(&result)?);

    Ok(())
}
