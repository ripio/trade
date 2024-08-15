import time
import hmac
import hashlib
import base64
import requests
from urllib.parse import urlparse
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
    'method': 'GET',
    'path': '/v4/orders?pair=BTC_BRL',
}

timestamp = int(time.time() * 1000)
pathname = urlparse(payload['path']).path.split('?')[0]
message = f"{timestamp}{payload['method']}{pathname}"
signature = hmac.new(secret_key.encode(), message.encode(), hashlib.sha256).digest()
signature_base64 = base64.b64encode(signature).decode()

url = f"https://api.ripiotrade.co{payload['path']}"

headers = {
    'Content-Type': 'application/json',
    'Authorization': api_key,
    'Signature': signature_base64,
    'Timestamp': str(timestamp)
}

response = requests.get(url, headers=headers, verify=False)

print("response:", response.json())
