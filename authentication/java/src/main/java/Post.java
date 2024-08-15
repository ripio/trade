import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.config.CookieSpecs;

import com.google.gson.Gson;
import io.github.cdimascio.dotenv.Dotenv;

public class Post {
    public static void post() {
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

        Map<String, Object> payload = new HashMap<>();
        payload.put("method", "POST");
        payload.put("path", "/v4/orders");
        Map<String, Object> body = new HashMap<>();
        body.put("type", "limit");
        body.put("price", new BigDecimal(137000).toPlainString());
        body.put("side", "buy");
        body.put("pair", "ABC_DEF");
        body.put("amount", new BigDecimal(0.0001).toPlainString());
        payload.put("body", body);

        String method = (String) payload.get("method");
        String path = payload.get("path").toString();
        String bodyString = new Gson().toJson(payload.get("body"));
        String timestamp = String.valueOf(System.currentTimeMillis());
        String message = timestamp + method + path + bodyString;
        String signature = hmacSha256(message, secretKey);
        String url = "https://api.ripiotrade.co" + (String) payload.get("path");

        SSLContext sslContext = createSslContext();

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                .build()) {

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Authorization", apiKey);
            httpPost.setHeader("timestamp", timestamp);
            httpPost.setHeader("signature", signature);
            httpPost.setHeader("Content-Type", "application/json");

            StringEntity stringEntity = new StringEntity(bodyString);
            httpPost.setEntity(stringEntity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                String responseString = entity != null ? EntityUtils.toString(entity) : null;
                System.out.println(responseString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SSLContext createSslContext() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) { }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) { }

                @Override
                public X509Certificate[] getAcceptedIssuers() { return null; }
            }}, new java.security.SecureRandom());
            return sslContext;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    public static String hmacSha256(String message, String secret) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] hash = sha256Hmac.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}
