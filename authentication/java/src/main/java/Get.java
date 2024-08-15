import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.github.cdimascio.dotenv.Dotenv;

public class Get {
    public static void get() {
        Dotenv dotenv = null;
        try {
            dotenv = Dotenv
                .configure()
                .load();
        } catch (Exception e) {
            System.err.println("Error loading .env file: " + e.getMessage());
            throw e;
        }

        if (dotenv == null || dotenv.get("API_KEY") == null || dotenv.get("SECRET_KEY") == null) {
            throw new IllegalArgumentException("Required environment variables (API_KEY, SECRET_KEY) are missing.");
        }

        String apiKey = dotenv.get("API_KEY");
        String secretKey = dotenv.get("SECRET_KEY");

        String method = "GET";
        String path = "/v4/orders?pair=BTC_BRL";
        String pathname = path.split("\\?")[0];
        String timestamp = String.valueOf(System.currentTimeMillis());
        String message = timestamp + method + pathname;
        String signature = hmacSha256(message, secretKey);
        String url = "https://api.ripiotrade.co" + path;

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new TrustAllTrustManager()}, new java.security.SecureRandom());

            HttpClient client = HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .build();

            HttpRequest httpGet = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", apiKey)
                    .header("timestamp", timestamp)
                    .header("signature", signature)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(httpGet, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String hmacSha256(String message, String secret) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKeySpec);
            byte[] hash = sha256Hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error generating HMAC signature", e);
        }
    }

    private static class TrustAllTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) { }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) { }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}
