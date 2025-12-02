# Ripio Trade | WebSocket API | JavaScript

This directory contains WebSocket API examples for trading operations.

## Available Examples

1. **create-order.js** - Create a new order via WebSocket
2. **cancel-order.js** - Cancel an existing order via WebSocket
3. **update-order.js** - Update an existing order's price and/or amount

**IMPORTANT!** For the create order example, an invalid pair (ABC_DEF) is used by default for safety. You can change this if needed.

## Setup

1. Copy the environment file:
```bash
cp ../.env.sample .env
```

2. Edit `.env` and add your API credentials

3. Install dependencies:
```bash
npm install
```

## Running

### Option 1: Using Docker

```bash
# From the websocket-api directory
docker-compose up -d javascript
docker-compose exec javascript npm run create
docker-compose exec javascript npm run cancel
docker-compose exec javascript npm run update
```

### Option 2: Local Installation

You can execute the examples using npm scripts:

```bash
# Create an order
npm run create

# Cancel an order (replace order ID in cancel-order.js first)
npm run cancel

# Update an order (replace order ID in update-order.js first)
npm run update
```

Or run directly with Node.js:

```bash
node create-order.js
node cancel-order.js
node update-order.js
```

## Notes

- The WebSocket endpoint is: `wss://ws-api.ripio.com`
- Authentication is done using API Token, Secret, and Timestamp
- The signature only includes timestamp + business parameters (no auth fields)
- Connections timeout after 60 seconds of inactivity
