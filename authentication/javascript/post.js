const dotenv = require('dotenv')
dotenv.config()
Object.assign(process.env, dotenv.config().parsed)

const CryptoJS = require('crypto-js')
const https = require('https')
const Url = require('url')

if (!process.env.API_KEY || !process.env.SECRET_KEY) {
  throw new Error('Required environment variables (API_KEY, SECRET_KEY) are missing.')
}

const apiKey = process.env.API_KEY
const secretKey = process.env.SECRET_KEY

const payload = {
  method: 'POST',
  path: '/v4/orders',
  body: {
    amount: 0.01,
    pair: 'ABC_DEF',
    price: 300000,
    side: 'buy',
    type: 'limit',
  },
}

const method = payload.method
const path = Url.parse(payload.path).pathname
const body = JSON.stringify(payload.body)
const timestamp = Date.now().toString()
const message = `${timestamp}${method}${path}${body}`
const signature = CryptoJS.HmacSHA256(message, secretKey).toString(CryptoJS.enc.Base64)
const url = `https://api.ripiotrade.co${payload.path}`

void (async () => {
  const { default: fetch } = await import('node-fetch')
  const response = await fetch(url, {
    agent: new https.Agent({
      rejectUnauthorized: false,
    }),
    body,
    method,
    headers: {
      Authorization: apiKey,
      timestamp,
      signature,
      'Content-Type': 'application/json',
    },
  })

  const result = await response.json()
  console.log(result)
})()
