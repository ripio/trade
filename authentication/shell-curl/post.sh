#!/bin/bash

ENV_FILE="/usr/local/bin/.env"

if [ -f "$ENV_FILE" ]; then
    export $(grep -v '^#' "$ENV_FILE" | xargs)
else
    echo "Error: .env file not found at $ENV_FILE"
    exit 1
fi

if [ -z "$API_KEY" ] || [ -z "$SECRET_KEY" ]; then
    echo "Error: Required environment variables (API_KEY, SECRET_KEY) are missing."
    exit 1
fi

apiKey=$API_KEY
secretKey=$SECRET_KEY
method='POST'
path='/v4/orders'
urlBase='https://api.ripiotrade.co'

body='{
    "amount": 0.01,
    "pair": "ABC_DEF",
    "price": 300000,
    "side": "buy",
    "type": "limit"
}'

timestamp=$(date +%s%3N)
url="${urlBase}${path}"
bodyMessage=$(echo $body | tr -d ' ')
message="${timestamp}${method}${path}${bodyMessage}"
signature=$(printf %s "$message" | openssl dgst -sha256 -hmac "$secretKey" -binary | openssl base64)

curl -s --insecure -X $method "$url" \
    -H "Content-Type: application/json" \
    -H "Authorization: $apiKey" \
    -H "Signature: $signature" \
    -H "Timestamp: $timestamp" \
    -d "$body"
