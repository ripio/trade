# Ripio Trade | WebSocket API | Rust

WebSocket API examples for trading operations in Rust.

**IMPORTANT!** Uses invalid pair (ABC_DEF) for safety (create example).

## Available Examples

1. **src/main.rs** - Create order
2. **src/cancel.rs** - Cancel order (replace order ID first)
3. **src/update.rs** - Update order (replace order ID first)

## Setup

1. Copy environment file:
```bash
cp ../.env.sample .env
```

2. Edit `.env` with your API credentials

## Running

### Option 1: Using Docker

```bash
# From the websocket-api directory
docker-compose up -d rust
docker-compose exec rust cargo run --bin websocket-api-example
docker-compose exec rust cargo run --bin cancel
docker-compose exec rust cargo run --bin update
```

### Option 2: Local Installation

```bash
# Create order
cargo run --bin websocket-api-example

# Cancel order
cargo run --bin cancel

# Update order
cargo run --bin update
```

Note: To run individual binaries, update Cargo.toml to include:
```toml
[[bin]]
name = "websocket-api-example"
path = "src/main.rs"

[[bin]]
name = "cancel"
path = "src/cancel.rs"

[[bin]]
name = "update"
path = "src/update.rs"
```

## Notes

- Uses tokio-tungstenite for async WebSocket
- HMAC SHA256 signature generation
- Endpoint: `wss://ws-api.ripio.com`
