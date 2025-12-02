use base64::{engine::general_purpose, Engine as _};
use dotenv::dotenv;
use futures_util::{SinkExt, StreamExt};
use hmac::{Hmac, Mac};
use serde_json::json;
use sha2::Sha256;
use std::env;
use tokio_tungstenite::{connect_async, tungstenite::protocol::Message};

type HmacSha256 = Hmac<Sha256>;

fn generate_signature(timestamp: u128, body_json: &str, secret_key: &str) -> String {
    let message = format!("{}{}", timestamp, body_json);
    let mut mac = HmacSha256::new_from_slice(secret_key.as_bytes())
        .expect("HMAC can take key of any size");
    mac.update(message.as_bytes());
    let result = mac.finalize();
    general_purpose::STANDARD.encode(result.into_bytes())
}

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    dotenv().ok();
    
    let api_key = env::var("API_KEY")?;
    let secret_key = env::var("SECRET_KEY")?;
    
    let (ws_stream, _) = connect_async("wss://ws-api.ripio.com").await?;
    println!("Connected to WebSocket API");
    
    let (mut write, mut read) = ws_stream.split();
    
    let timestamp = std::time::SystemTime::now()
        .duration_since(std::time::UNIX_EPOCH)?
        .as_millis();
    
    let body_params = json!({
        "id": "7155ED34-9EC4-4733-8B32-1E4319CB662F", // Replace with actual order ID
        "price": "350000",
        "amount": "0.02"
    });
    
    let body_json = serde_json::to_string(&body_params)?;
    let signature = generate_signature(timestamp, &body_json, &secret_key);
    
    let request = json!({
        "id": "req-update-001",
        "method": "order.update",
        "params": {
            "id": "7155ED34-9EC4-4733-8B32-1E4319CB662F",
            "price": "350000",
            "amount": "0.02",
            "apiToken": api_key,
            "timestamp": timestamp,
            "signature": signature
        }
    });
    
    println!("\nSending request: {}", serde_json::to_string_pretty(&request)?);
    write.send(Message::Text(request.to_string())).await?;
    
    if let Some(msg) = read.next().await {
        let msg = msg?;
        if let Message::Text(text) = msg {
            println!("\nReceived response: {}", text);
        }
    }
    
    Ok(())
}
