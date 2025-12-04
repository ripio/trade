# Ripio Trade | WebSocket API | Shell

WebSocket API examples using bash scripts with websocat.

**IMPORTANT!** Uses invalid pair (ABC_DEF) for safety (create example).

## Available Examples

1. **create-order.sh** - Create order
2. **cancel-order.sh** - Cancel order (replace order ID first)
3. **update-order.sh** - Update order (replace order ID first)

## Setup

1. Install websocat:
```bash
# macOS
brew install websocat

# Linux
cargo install websocat
```

2. Copy environment file:
```bash
cp ../.env.sample .env
```

3. Edit `.env` with your API credentials

4. Make scripts executable:
```bash
chmod +x *.sh
```

## Running

### Option 1: Using Docker Compose (Build & Run)

Run all examples:

```bash
docker-compose up --build
```

Or run individual examples:

```bash
# Create order
docker-compose up --build create

# Cancel order (replace order ID in cancel-order.sh first)
docker-compose up --build cancel

# Update order (replace order ID in update-order.sh first)
docker-compose up --build update
```

### Option 2: Local Installation

```bash
# Create order
./create-order.sh

# Cancel order
./cancel-order.sh

# Update order
./update-order.sh
```

## Notes

- Uses openssl for HMAC SHA256 signature generation
- Requires websocat for WebSocket connections
- Endpoint: `wss://ws-api.ripio.com`
