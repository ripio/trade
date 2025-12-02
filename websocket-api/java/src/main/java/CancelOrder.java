import java.net.URI;
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

public class CancelOrder extends WebSocketClient {
    
    private static String apiKey;
    private static String secretKey;
    
    public CancelOrder(URI serverUri) {
        super(serverUri);
    }
    
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to WebSocket API");
        
        long timestamp = System.currentTimeMillis();
        JsonObject bodyParams = new JsonObject();
        bodyParams.addProperty("id", "7155ED34-9EC4-4733-8B32-1E4319CB662F"); // Replace with actual order ID
        
        String signature = generateSignature(timestamp, bodyParams);
        
        JsonObject request = new JsonObject();
        request.addProperty("id", "req-cancel-001");
        request.addProperty("method", "order.cancel");
        
        JsonObject params = bodyParams.deepCopy();
        params.addProperty("apiToken", apiKey);
        params.addProperty("timestamp", timestamp);
        params.addProperty("signature", signature);
        request.add("params", params);
        
        System.out.println("\nSending cancel order request: " + new Gson().toJson(request));
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
            SecretKeySpec secretKey = new SecretKeySpec(CancelOrder.secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
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
            CancelOrder client = new CancelOrder(new URI("wss://ws-api.ripio.com"));
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
