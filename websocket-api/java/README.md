# Ripio Trade | WebSocket API | Java

WebSocket API examples for trading operations in Java.

**IMPORTANT!** Uses invalid pair (ABC_DEF) for safety (create example).

## Available Examples

1. **CreateOrder.java** - Create order
2. **CancelOrder.java** - Cancel order (replace order ID first)
3. **UpdateOrder.java** - Update order (replace order ID first)

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
docker-compose up -d java
docker-compose exec java mvn exec:java -Dexec.mainClass="CreateOrder"
docker-compose exec java mvn exec:java -Dexec.mainClass="CancelOrder"
docker-compose exec java mvn exec:java -Dexec.mainClass="UpdateOrder"
```

### Option 2: Local Installation

```bash
# Create order
mvn exec:java -Dexec.mainClass="CreateOrder"

# Cancel order
mvn exec:java -Dexec.mainClass="CancelOrder"

# Update order
mvn exec:java -Dexec.mainClass="UpdateOrder"
```

## Notes

- Uses Java-WebSocket library for WebSocket connections
- HMAC SHA256 signature generation
- Endpoint: `wss://ws-api.ripio.com`
