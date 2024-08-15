import time
import json
import hmac
import hashlib
import base64
import requests
import warnings
from dotenv import load_dotenv
import os
from os.path import join, dirname

warnings.filterwarnings("ignore")

load_dotenv()

if os.getenv('API_KEY') is None or os.getenv('SECRET_KEY') is None:
    raise ValueError("Required environment variables (API_KEY, SECRET_KEY) are missing.")

api_key = os.getenv('API_KEY')
secret_key = os.getenv('SECRET_KEY')

payload = {
    'method': 'POST',
    'path': '/v4/orders',
    'body': {
         'amount': 0.01,
         'pair': 'ABC_DEF',
         'price': 300000, 
         'side': 'buy', 
         'type': 'limit'
    }
}

timestamp = int(time.time() * 1000)
body_string = json.dumps(payload['body'], separators=(',', ':'))
message = f"{timestamp}{payload['method']}{payload['path']}{body_string}"
signature = hmac.new(secret_key.encode(), message.encode(), hashlib.sha256).digest()
signature_base64 = base64.b64encode(signature).decode()

url = f"https://api.ripiotrade.co{payload['path']}"

headers = {
    'Content-Type': 'application/json',
    'Authorization': api_key,
    'Signature': signature_base64,
    'Timestamp': str(timestamp)
}

response = requests.post(url, headers=headers, data=body_string, verify=False)

print('response:', response.json())
