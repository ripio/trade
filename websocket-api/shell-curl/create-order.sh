#!/bin/bash

# Note: WebSocket connections via curl require websocat or similar tools
# This example demonstrates the message format - use with websocat:
# websocat wss://ws-api.ripio.com

ENV_FILE=".env"

if [ -f "$ENV_FILE" ]; then
    export $(grep -v '^#' "$ENV_FILE" | xargs)
else
    echo "Error: .env file not found"
    exit 1
fi

if [ -z "$API_KEY" ] || [ -z "$SECRET_KEY" ]; then
    echo "Error: Required environment variables (API_KEY, SECRET_KEY) are missing."
    exit 1
fi

timestamp=$(date +%s%3N)

# Body params for signature
body='{"pair":"ABC_DEF","side":"buy","type":"limit","amount":0.01,"price":300000}'

# Generate signature
message="${timestamp}${body}"
signature=$(printf %s "$message" | openssl dgst -sha256 -hmac "$SECRET_KEY" -binary | openssl base64)

# Create complete request (compact JSON on single line)
request="{\"id\":\"req-create-001\",\"method\":\"order.create\",\"params\":{\"pair\":\"ABC_DEF\",\"side\":\"buy\",\"type\":\"limit\",\"amount\":0.01,\"price\":300000,\"api_token\":\"$API_KEY\",\"timestamp\":$timestamp,\"signature\":\"$signature\"}}"

echo "Sending request to WebSocket API..."
echo "$request"
echo ""
echo "Response:"
echo "$request" | websocat -n1 wss://ws-api.ripio.com
