# Ripio Trade | WebSocket API | C#

WebSocket API examples for trading operations in C#.

**IMPORTANT!** Uses invalid pair (ABC_DEF) for safety (create example).

## Available Examples

1. **CreateOrder.cs** - Create order
2. **CancelOrder.cs** - Cancel order (replace order ID first)
3. **UpdateOrder.cs** - Update order (replace order ID first)

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
docker-compose up -d csharp
docker-compose exec csharp dotnet run --project CreateOrder.csproj
docker-compose exec csharp dotnet run --project CancelOrder.csproj
docker-compose exec csharp dotnet run --project UpdateOrder.csproj
```

### Option 2: Local Installation

```bash
# Create order
dotnet run --project CreateOrder.csproj

# Cancel order  
dotnet run --project CancelOrder.csproj

# Update order
dotnet run --project UpdateOrder.csproj
```

## Notes

- Uses .NET 6.0 WebSocket client
- HMAC SHA256 signature generation
- Endpoint: `wss://ws-api.ripio.com`
