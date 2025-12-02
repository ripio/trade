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
body='{"id":"7155ED34-9EC4-4733-8B32-1E4319CB662F"}'

# Generate signature
message="${timestamp}${body}"
signature=$(printf %s "$message" | openssl dgst -sha256 -hmac "$SECRET_KEY" -binary | openssl base64)

# Create complete request
request=$(cat <<EOF
{
  "id": "req-cancel-001",
  "method": "order.cancel",
  "params": {
    "id": "7155ED34-9EC4-4733-8B32-1E4319CB662F",
    "apiToken": "$API_KEY",
    "timestamp": $timestamp,
    "signature": "$signature"
  }
}
EOF
)

echo "Request to send via websocat:"
echo "$request"
echo ""
echo "Run: echo '$request' | websocat wss://ws-api.ripio.com"
echo ""
echo "Note: Replace the order ID with an actual order from your account"
