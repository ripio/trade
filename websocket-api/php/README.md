# Ripio Trade | WebSocket API | PHP

WebSocket API examples for trading operations in PHP.

**IMPORTANT!** Uses invalid pair (ABC_DEF) for safety (create example).

## Available Examples

1. **create-order.php** - Create order
2. **cancel-order.php** - Cancel order (replace order ID first)
3. **update-order.php** - Update order (replace order ID first)

## Setup

1. Copy environment file:
```bash
cp ../.env.sample .env
```

2. Edit `.env` with your API credentials

3. Install dependencies:
```bash
composer install
```

## Running

### Option 1: Using Docker

```bash
# From the websocket-api directory
docker-compose up -d php
docker-compose exec php php create-order.php
docker-compose exec php php cancel-order.php
docker-compose exec php php update-order.php
```

### Option 2: Local Installation

```bash
# Create order
php create-order.php

# Cancel order
php cancel-order.php

# Update order
php update-order.php
```

## Notes

- Uses Ratchet WebSocket client
- HMAC SHA256 signature generation
- Endpoint: `wss://ws-api.ripio.com`
