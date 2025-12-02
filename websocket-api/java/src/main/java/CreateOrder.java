import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.cdimascio.dotenv.Dotenv;

public class CreateOrder extends WebSocketClient {
    
    private static String apiKey;
    private static String secretKey;
    
    public CreateOrder(URI serverUri) {
        super(serverUri);
    }
    
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to WebSocket API");
        
        // Prepare order parameters (business params only for signature)
        long timestamp = System.currentTimeMillis();
        JsonObject bodyParams = new JsonObject();
        bodyParams.addProperty("pair", "ABC_DEF");
        bodyParams.addProperty("side", "buy");
        bodyParams.addProperty("type", "limit");
        bodyParams.addProperty("amount", 0.01);
        bodyParams.addProperty("price", 300000);
        
        // Generate signature
        String signature = generateSignature(timestamp, bodyParams);
        
        // Create complete request
        JsonObject request = new JsonObject();
        request.addProperty("id", "req-create-001");
        request.addProperty("method", "order.create");
        
        JsonObject params = bodyParams.deepCopy();
        params.addProperty("apiToken", apiKey);
        params.addProperty("timestamp", timestamp);
        params.addProperty("signature", signature);
        request.add("params", params);
        
        System.out.println("\nSending create order request: " + new Gson().toJson(request));
        send(new Gson().toJson(request));
    }
    
    @Override
    public void onMessage(String message) {
        System.out.println("\nReceived response: " + message);
        close();
    }
    
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("\nWebSocket connection closed");
    }
    
    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
    }
    
    private static String generateSignature(long timestamp, JsonObject bodyParams) {
        try {
            String message = timestamp + new Gson().toJson(bodyParams);
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(CreateOrder.secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] hash = sha256Hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }
    
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();
        
        if (dotenv.get("API_KEY") == null || dotenv.get("SECRET_KEY") == null) {
            throw new IllegalArgumentException("Required environment variables (API_KEY, SECRET_KEY) are missing.");
        }
        
        apiKey = dotenv.get("API_KEY");
        secretKey = dotenv.get("SECRET_KEY");
        
        try {
            CreateOrder client = new CreateOrder(new URI("wss://ws-api.ripio.com"));
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
