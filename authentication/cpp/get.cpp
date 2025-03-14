#include <iostream>
#include <fstream>
#include <string>
#include <chrono>
#include <sstream>
#include <iomanip>
#include <unordered_map>
#include <cstdlib>
#include <curl/curl.h>
#include <openssl/hmac.h>
#include <openssl/buffer.h>
#include <nlohmann/json.hpp>

using json = nlohmann::json;

// Function to read environment variables from .env file
std::unordered_map<std::string, std::string> readEnvFile(const std::string& filename) {
    std::unordered_map<std::string, std::string> env;
    std::ifstream file(filename);
    
    if (!file.is_open()) {
        std::cerr << "Error: Could not open .env file: " << filename << std::endl;
        return env;
    }
    
    std::string line;
    while (std::getline(file, line)) {
        // Skip empty lines and comments
        if (line.empty() || line[0] == '#') {
            continue;
        }
        
        // Find the equals sign
        size_t pos = line.find('=');
        if (pos != std::string::npos) {
            std::string key = line.substr(0, pos);
            std::string value = line.substr(pos + 1);
            env[key] = value;
        }
    }
    
    return env;
}

// Function to generate HMAC-SHA256 signature
std::string generateHmacSha256(const std::string& message, const std::string& key) {
    unsigned char hash[EVP_MAX_MD_SIZE];
    unsigned int hashLen;
    
    HMAC(EVP_sha256(), key.c_str(), static_cast<int>(key.length()),
         reinterpret_cast<const unsigned char*>(message.c_str()), message.length(),
         hash, &hashLen);
    
    // Convert to base64
    BIO* b64 = BIO_new(BIO_f_base64());
    BIO* bmem = BIO_new(BIO_s_mem());
    b64 = BIO_push(b64, bmem);
    BIO_set_flags(b64, BIO_FLAGS_BASE64_NO_NL);
    BIO_write(b64, hash, hashLen);
    BIO_flush(b64);
    
    BUF_MEM* bptr;
    BIO_get_mem_ptr(b64, &bptr);
    
    std::string result(bptr->data, bptr->length);
    BIO_free_all(b64);
    
    return result;
}

// Callback function for CURL to write response data
size_t writeCallback(char* ptr, size_t size, size_t nmemb, std::string* data) {
    data->append(ptr, size * nmemb);
    return size * nmemb;
}

int main() {
    // Initialize CURL
    curl_global_init(CURL_GLOBAL_DEFAULT);
    
    // Try to get API keys from environment variables first
    const char* envApiKey = std::getenv("API_KEY");
    const char* envSecretKey = std::getenv("SECRET_KEY");
    
    std::string apiKey;
    std::string secretKey;
    
    if (envApiKey && envSecretKey) {
        apiKey = envApiKey;
        secretKey = envSecretKey;
    } else {
        // Fall back to reading from .env file
        auto env = readEnvFile(".env");
        
        if (env.find("API_KEY") == env.end() || env.find("SECRET_KEY") == env.end()) {
            std::cerr << "Error: Required environment variables (API_KEY, SECRET_KEY) are missing." << std::endl;
            return 1;
        }
        
        apiKey = env["API_KEY"];
        secretKey = env["SECRET_KEY"];
    }
    
    // Define the request parameters
    std::string method = "GET";
    std::string path = "/v4/orders";
    std::string queryParams = "?pair=BTC_BRL";
    std::string pathWithParams = path + queryParams;
    std::string pathname = path; // Just the path without query parameters
    std::string body = "";
    
    // Generate timestamp (milliseconds since epoch)
    auto now = std::chrono::system_clock::now();
    auto millis = std::chrono::duration_cast<std::chrono::milliseconds>(now.time_since_epoch()).count();
    std::string timestamp = std::to_string(millis);
    
    // Create the message to sign
    std::string message = timestamp + method + pathname;
    
    // Generate the signature
    std::string signature = generateHmacSha256(message, secretKey);
    
    // Create the URL
    std::string url = "https://api.ripiotrade.co" + pathWithParams;
    
    // Initialize CURL session
    CURL* curl = curl_easy_init();
    std::string responseData;
    
    if (curl) {
        // Set URL
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        
        // Set callback function for response data
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, writeCallback);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, &responseData);
        
        // Skip SSL verification (for testing only)
        curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0L);
        curl_easy_setopt(curl, CURLOPT_SSL_VERIFYHOST, 0L);
        
        // Set headers
        struct curl_slist* headers = NULL;
        headers = curl_slist_append(headers, ("Authorization: " + apiKey).c_str());
        headers = curl_slist_append(headers, ("timestamp: " + timestamp).c_str());
        headers = curl_slist_append(headers, ("signature: " + signature).c_str());
        headers = curl_slist_append(headers, "Content-Type: application/json");
        curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);
        
        // Perform the request
        CURLcode res = curl_easy_perform(curl);
        
        // Check for errors
        if (res != CURLE_OK) {
            std::cerr << "curl_easy_perform() failed: " << curl_easy_strerror(res) << std::endl;
        } else {
            // Parse and print the JSON response
            try {
                json response = json::parse(responseData);
                std::cout << response.dump(2) << std::endl;
            } catch (const json::parse_error& e) {
                std::cerr << "JSON parse error: " << e.what() << std::endl;
                std::cerr << "Response data: " << responseData << std::endl;
            }
        }
        
        // Clean up
        curl_slist_free_all(headers);
        curl_easy_cleanup(curl);
    }
    
    curl_global_cleanup();
    return 0;
}
