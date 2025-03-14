using System;
using System.Net.Http;
using System.Security.Cryptography;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

class Post
{
    public static async Task ExecutePost()
    {
        // Load environment variables
        DotNetEnv.Env.Load();
        
        string apiKey = Environment.GetEnvironmentVariable("API_KEY");
        string secretKey = Environment.GetEnvironmentVariable("SECRET_KEY");
        
        if (string.IsNullOrEmpty(apiKey) || string.IsNullOrEmpty(secretKey))
        {
            throw new Exception("Required environment variables (API_KEY, SECRET_KEY) are missing.");
        }
        
        // Define request parameters
        string method = "POST";
        string path = "/v4/orders";
        
        // Create request body
        var bodyObject = new
        {
            amount = 0.01,
            pair = "ABC_DEF",
            price = 300000,
            side = "buy",
            type = "limit"
        };
        
        string body = JsonSerializer.Serialize(bodyObject);
        
        // Generate timestamp
        string timestamp = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds().ToString();
        
        // Create signature
        string message = $"{timestamp}{method}{path}{body}";
        string signature = CreateSignature(message, secretKey);
        
        // Create HttpClient with SSL validation disabled
        var handler = new HttpClientHandler
        {
            ServerCertificateCustomValidationCallback = (sender, cert, chain, sslPolicyErrors) => true
        };
        
        using var client = new HttpClient(handler);
        
        // Set up request
        var request = new HttpRequestMessage(new HttpMethod(method), $"https://api.ripiotrade.co{path}");
        // Use TryAddWithoutValidation for headers that might have special formats
        request.Headers.TryAddWithoutValidation("Authorization", apiKey);
        request.Headers.TryAddWithoutValidation("timestamp", timestamp);
        request.Headers.TryAddWithoutValidation("signature", signature);
        
        // For POST requests, set Content-Type on the content object
        request.Content = new StringContent(body, Encoding.UTF8, "application/json");
        
        // Send request
        var response = await client.SendAsync(request);
        var responseContent = await response.Content.ReadAsStringAsync();
        
        // Pretty print the JSON response
        var jsonDocument = JsonDocument.Parse(responseContent);
        var formattedJson = JsonSerializer.Serialize(
            jsonDocument.RootElement, 
            new JsonSerializerOptions { WriteIndented = true }
        );
        
        Console.WriteLine(formattedJson);
    }
    
    private static string CreateSignature(string message, string secretKey)
    {
        using var hmac = new HMACSHA256(Encoding.UTF8.GetBytes(secretKey));
        byte[] signatureBytes = hmac.ComputeHash(Encoding.UTF8.GetBytes(message));
        return Convert.ToBase64String(signatureBytes);
    }
}
