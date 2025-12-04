import asyncio
import json
import hmac
import hashlib
import base64
import time
import os
import websockets
from dotenv import load_dotenv

load_dotenv()

if os.getenv('API_KEY') is None or os.getenv('SECRET_KEY') is None:
    raise ValueError("Required environment variables (API_KEY, SECRET_KEY) are missing.")

api_key = os.getenv('API_KEY')
secret_key = os.getenv('SECRET_KEY')

# WebSocket endpoint
WS_URL = 'wss://ws-api.ripio.com'

def generate_signature(timestamp, body_params):
    """Generate HMAC SHA256 signature for WebSocket API"""
    message = str(timestamp) + json.dumps(body_params, separators=(',', ':'))
    signature = hmac.new(
        secret_key.encode(),
        message.encode(),
        hashlib.sha256
    ).digest()
    return base64.b64encode(signature).decode()

async def cancel_order():
    async with websockets.connect(WS_URL) as websocket:
        print('Connected to WebSocket API')

        # Prepare cancel parameters (business params only for signature)
        # Replace with an actual order ID from your account
        timestamp = int(time.time() * 1000)
        body_params = {
            'id': '7155ED34-9EC4-4733-8B32-1E4319CB662F'  # Replace with actual order ID
        }

        # Generate signature
        signature = generate_signature(timestamp, body_params)

        # Create complete request with authentication
        request = {
            'id': 'req-cancel-001',
            'method': 'order.cancel',
            'params': {
                **body_params,
                'api_token': api_key,
                'timestamp': timestamp,
                'signature': signature
            }
        }

        print('\nSending cancel order request:', json.dumps(request, indent=2))
        await websocket.send(json.dumps(request))

        # Receive response
        response = await websocket.recv()
        response_data = json.loads(response)
        print('\nReceived response:', json.dumps(response_data, indent=2))

if __name__ == '__main__':
    asyncio.run(cancel_order())
