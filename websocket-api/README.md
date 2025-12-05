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

## Environment Variables

Before you begin, configure your environment variables using the sample dotenv file. Each language directory needs its own `.env` file:

```bash
# Copy the sample .env to the language directory you want to use
cp .env.sample <language>/.env

# For example:
cp .env.sample javascript/.env
```

Edit the `.env` file and add your API credentials (API Token and Secret).

## Running Options

### Option 1: Using Docker Compose (Recommended)

Each language has its own Docker Compose setup for easy execution without installing dependencies locally.

#### Prerequisites

- Docker: https://docs.docker.com/engine/install/
- Docker Compose: https://docs.docker.com/compose/install/

#### Running Examples

Navigate to your chosen language directory and use Docker Compose:

```bash
# Navigate to the language directory
cd <language>  # e.g., cd javascript, cd python, cd java, etc.

# Run all examples
docker-compose up --build

# Or run individual examples:
docker-compose up --build create    # Create order
docker-compose up --build cancel    # Cancel order (update order ID in the file first)
docker-compose up --build update    # Update order (update order ID in the file first)
```

**Note**: Before running cancel or update examples, you need to replace the order ID in the respective source files with a valid order ID from your account.

### Option 2: Local Installation

Install dependencies for your chosen language and run directly. Detailed instructions for each language, including dependency installation and running commands, are available in the corresponding `README.md` file in each language directory.

## Authentication

The signature for WebSocket API is generated using:

```
Timestamp + JSON Body (business parameters only)
```

**Important**: Do NOT include `api_token`, `timestamp`, or `signature` in the body when generating the signature. Only include the business parameters (pair, side, type, amount, price, etc.).

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
    "api_token": "your-api-token",
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
