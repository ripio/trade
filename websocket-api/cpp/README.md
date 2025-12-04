# Ripio Trade | WebSocket API | C++

WebSocket API examples for trading operations in C++.

**IMPORTANT!** Uses invalid pair (ABC_DEF) for safety (create example).

## Available Examples

1. **create-order.cpp** - Create order
2. **cancel-order.cpp** - Cancel order (replace order ID first)
3. **update-order.cpp** - Update order (replace order ID first)

## Setup

1. Copy environment file:
```bash
cp ../.env.sample .env
```

2. Edit `.env` with your API credentials

3. Install dependencies:
- Boost.Beast
- OpenSSL
- nlohmann/json

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

# Cancel order (replace order ID in cancel-order.cpp first)
docker-compose up --build cancel

# Update order (replace order ID in update-order.cpp first)
docker-compose up --build update
```

### Option 2: Local Installation

```bash
# Create order
g++ -std=c++17 create-order.cpp -o create-order -lssl -lcrypto -lboost_system -lpthread
./create-order

# Cancel order
g++ -std=c++17 cancel-order.cpp -o cancel-order -lssl -lcrypto -lboost_system -lpthread
./cancel-order

# Update order
g++ -std=c++17 update-order.cpp -o update-order -lssl -lcrypto -lboost_system -lpthread
./update-order
```

## Notes

- Uses Boost.Beast for WebSocket over SSL
- HMAC SHA256 signature generation with OpenSSL
- Endpoint: `wss://ws-api.ripio.com`
