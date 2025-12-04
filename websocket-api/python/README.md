# Ripio Trade | WebSocket API | Python

This directory contains WebSocket API examples for trading operations using Python.

## Available Examples

1. **create_order.py** - Create a new order via WebSocket
2. **cancel_order.py** - Cancel an existing order via WebSocket
3. **update_order.py** - Update an existing order's price and/or amount

**IMPORTANT!** For the create order example, an invalid pair (ABC_DEF) is used by default for safety. You can change this if needed.

## Setup

1. Copy the environment file:
```bash
cp ../.env.sample .env
```

2. Edit `.env` and add your API credentials

3. Install dependencies:
```bash
pip install -r requirements.txt
```

## Running

### Option 1: Using Docker Compose (Build & Run)

Run all examples:

```bash
docker-compose up --build
```

Or run individual examples:

```bash
# Create an order
docker-compose up --build create

# Cancel an order (replace order ID in cancel_order.py first)
docker-compose up --build cancel

# Update an order (replace order ID in update_order.py first)
docker-compose up --build update
```

### Option 2: Local Installation

Execute the examples using Python:

```bash
# Create an order
python create_order.py

# Cancel an order (replace order ID in cancel_order.py first)
python cancel_order.py

# Update an order (replace order ID in update_order.py first)
python update_order.py
```

## Notes

- The WebSocket endpoint is: `wss://ws-api.ripio.com`
- Authentication is done using API Token, Secret, and Timestamp
- The signature only includes timestamp + business parameters (no auth fields)
- Connections timeout after 60 seconds of inactivity
- Uses asyncio and websockets library for async WebSocket communication
