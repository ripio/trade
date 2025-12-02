const dotenv = require('dotenv')
const WebSocket = require('ws')
const crypto = require('crypto')

dotenv.config()

if (!process.env.API_KEY || !process.env.SECRET_KEY) {
  throw new Error('Required environment variables (API_KEY, SECRET_KEY) are missing.')
}

const apiKey = process.env.API_KEY
const secretKey = process.env.SECRET_KEY

// WebSocket endpoint
const WS_URL = 'wss://ws-api.ripio.com'

// Generate signature for WebSocket API
function generateSignature(timestamp, bodyParams) {
  const message = timestamp.toString() + JSON.stringify(bodyParams)
  return crypto.createHmac('sha256', secretKey).update(message).digest('base64')
}

// Create WebSocket connection
const ws = new WebSocket(WS_URL)

ws.on('open', () => {
  console.log('Connected to WebSocket API')

  // Prepare order parameters (business params only for signature)
  // Using invalid pair ABC_DEF for safety
  const timestamp = Date.now()
  const bodyParams = {
    pair: 'ABC_DEF',
    side: 'buy',
    type: 'limit',
    amount: 0.01,
    price: 300000
  }

  // Generate signature
  const signature = generateSignature(timestamp, bodyParams)

  // Create complete request with authentication
  const request = {
    id: 'req-create-001',
    method: 'order.create',
    params: {
      ...bodyParams,
      apiToken: apiKey,
      timestamp: timestamp,
      signature: signature
    }
  }

  console.log('\nSending create order request:', JSON.stringify(request, null, 2))
  ws.send(JSON.stringify(request))
})

ws.on('message', (data) => {
  const response = JSON.parse(data.toString())
  console.log('\nReceived response:', JSON.stringify(response, null, 2))
  
  // Close connection after receiving response
  setTimeout(() => {
    ws.close()
  }, 1000)
})

ws.on('error', (error) => {
  console.error('WebSocket error:', error)
})

ws.on('close', () => {
  console.log('\nWebSocket connection closed')
})
