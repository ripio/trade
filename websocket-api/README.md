# Ripio Trade - WebSocket API

This example demonstrates how to use the Ripio Trade WebSocket API for trading operations. You can choose from the following programming languages:

1. JavaScript
2. Python
3. Java
4. C#
5. PHP
6. Rust
7. C++
8. Shell (Bash)

## WebSocket API Overview

The WebSocket API allows you to perform trading operations (create, cancel, and update orders) via WebSocket connection.

- **Base endpoint**: `wss://ws-api.ripio.com`
- Connections remain active as long as there is activity
- Idle connections are disconnected after **60 seconds** of inactivity
- Authentication uses API Token, Secret, and Timestamp (same as REST API)

## Key Differences from REST API

1. **Simpler Signature**: WebSocket signature only includes `Timestamp + JSON Body` (business params only)
2. **No HTTP Method/Path**: Unlike REST, you don't include the HTTP method or path in the signature
3. **Request Format**: All requests use a JSON format with `id`, `method`, and `params`

## Available Operations

- `order.create` - Create a new order
- `order.cancel` - Cancel an existing order
- `order.update` - Update an existing order's price and/or amount

## Environment variables

Before you begin, please configure your environment variables using the sample dotenv file:

```
$ cp .env.sample javascript/.env
```

You'll need to create a separate file for each language you want to run. Once you've created your `.env` file, update the API Key and Secret.

## Running Options

### Option 1: Using Docker (Recommended)

We provide Docker setup for easy execution without installing dependencies locally.

1. Install Docker and Docker Compose:
   - Docker: https://docs.docker.com/engine/install/
   - Docker Compose: https://docs.docker.com/compose/install/

2. Setup environment:
```bash
cp .env.sample .env
# Edit .env with your API credentials
```

3. Run examples with Docker:
```bash
# Start a specific language container
docker-compose up -d javascript

# Execute commands inside the container
docker-compose exec javascript npm run create-order
docker-compose exec python python create_order.py
docker-compose exec java mvn exec:java -Dexec.mainClass="CreateOrder"
docker-compose exec php php create-order.php
docker-compose exec rust cargo run --bin websocket-api-example

# Stop containers
docker-compose down
```

### Option 2: Local Installation

Install dependencies for your chosen language and run directly. Instructions for each language are available in the corresponding `README.md` file, [such as this one](javascript).

## Authentication

The signature for WebSocket API is generated using:

```
Timestamp + JSON Body (business parameters only)
```

**Important**: Do NOT include `apiToken`, `timestamp`, or `signature` in the body when generating the signature. Only include the business parameters (pair, side, type, amount, price, etc.).

### Example

**Request to send:**
```json
{
  "id": "req-001",
  "method": "order.create",
  "params": {
    "pair": "BTC_BRL",
    "side": "buy",
    "type": "limit",
    "amount": 0.001,
    "price": 100000,
    "apiToken": "your-api-token",
    "timestamp": 1634567890000,
    "signature": "calculated-signature"
  }
}
```

**Body to sign (only business params):**
```json
{
  "pair": "BTC_BRL",
  "side": "buy",
  "type": "limit",
  "amount": 0.001,
  "price": 100000
}
```

**Message to sign:**
```
1634567890000{"pair":"BTC_BRL","side":"buy","type":"limit","amount":0.001,"price":100000}
```
