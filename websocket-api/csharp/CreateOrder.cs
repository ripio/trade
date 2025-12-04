using System;
using System.Net.WebSockets;
using System.Security.Cryptography;
using System.Text;
using System.Text.Json;
using System.Threading;
using System.Threading.Tasks;

class CreateOrder
{
    static async Task Main(string[] args)
    {
        DotNetEnv.Env.Load();
        
        string apiKey = Environment.GetEnvironmentVariable("API_KEY");
        string secretKey = Environment.GetEnvironmentVariable("SECRET_KEY");
        
        if (string.IsNullOrEmpty(apiKey) || string.IsNullOrEmpty(secretKey))
        {
            throw new Exception("Required environment variables (API_KEY, SECRET_KEY) are missing.");
        }
        
        using (var ws = new ClientWebSocket())
        {
            await ws.ConnectAsync(new Uri("wss://ws-api.ripio.com"), CancellationToken.None);
            Console.WriteLine("Connected to WebSocket API");
            
            // Prepare order parameters
            long timestamp = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
            var bodyParams = new
            {
                pair = "ABC_DEF",
                side = "buy",
                type = "limit",
                amount = 0.01,
                price = 300000
            };
            
            string bodyJson = JsonSerializer.Serialize(bodyParams);
            string signature = GenerateSignature(timestamp, bodyJson, secretKey);
            
            var request = new
            {
                id = "req-create-001",
                method = "order.create",
                @params = new
                {
                    pair = bodyParams.pair,
                    side = bodyParams.side,
                    type = bodyParams.type,
                    amount = bodyParams.amount,
                    price = bodyParams.price,
                    api_token = apiKey,
                    timestamp = timestamp,
                    signature = signature
                }
            };
            
            string requestJson = JsonSerializer.Serialize(request);
            Console.WriteLine($"\nSending request: {requestJson}");
            
            byte[] buffer = Encoding.UTF8.GetBytes(requestJson);
            await ws.SendAsync(new ArraySegment<byte>(buffer), WebSocketMessageType.Text, true, CancellationToken.None);
            
            // Receive response
            var receiveBuffer = new byte[4096];
            var result = await ws.ReceiveAsync(new ArraySegment<byte>(receiveBuffer), CancellationToken.None);
            string response = Encoding.UTF8.GetString(receiveBuffer, 0, result.Count);
            Console.WriteLine($"\nReceived response: {response}");
            
            await ws.CloseAsync(WebSocketCloseStatus.NormalClosure, "Done", CancellationToken.None);
        }
    }
    
    static string GenerateSignature(long timestamp, string bodyJson, string secretKey)
    {
        string message = timestamp.ToString() + bodyJson;
        using (var hmac = new HMACSHA256(Encoding.UTF8.GetBytes(secretKey)))
        {
            byte[] hash = hmac.ComputeHash(Encoding.UTF8.GetBytes(message));
            return Convert.ToBase64String(hash);
        }
    }
}
