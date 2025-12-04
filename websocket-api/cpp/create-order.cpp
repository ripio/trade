#include <iostream>
#include <string>
#include <fstream>
#include <chrono>
#include <openssl/hmac.h>
#include <openssl/buffer.h>
#include <nlohmann/json.hpp>
#include <boost/beast/core.hpp>
#include <boost/beast/websocket.hpp>
#include <boost/asio/connect.hpp>
#include <boost/asio/ip/tcp.hpp>
#include <boost/asio/ssl.hpp>

using json = nlohmann::json;
namespace beast = boost::beast;
namespace http = beast::http;
namespace websocket = beast::websocket;
namespace net = boost::asio;
namespace ssl = boost::asio::ssl;
using tcp = boost::asio::ip::tcp;

std::unordered_map<std::string, std::string> readEnvFile(const std::string& filename) {
    std::unordered_map<std::string, std::string> env;
    std::ifstream file(filename);
    std::string line;
    while (std::getline(file, line)) {
        if (line.empty() || line[0] == '#') continue;
        size_t pos = line.find('=');
        if (pos != std::string::npos) {
            env[line.substr(0, pos)] = line.substr(pos + 1);
        }
    }
    return env;
}

std::string generateSignature(long long timestamp, const std::string& bodyJson, const std::string& secretKey) {
    std::string message = std::to_string(timestamp) + bodyJson;
    unsigned char hash[EVP_MAX_MD_SIZE];
    unsigned int hashLen;
    
    HMAC(EVP_sha256(), secretKey.c_str(), secretKey.length(),
         reinterpret_cast<const unsigned char*>(message.c_str()), message.length(),
         hash, &hashLen);
    
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

int main() {
    auto env = readEnvFile(".env");
    if (env.find("API_KEY") == env.end() || env.find("SECRET_KEY") == env.end()) {
        std::cerr << "Required environment variables missing." << std::endl;
        return 1;
    }
    
    std::string apiKey = env["API_KEY"];
    std::string secretKey = env["SECRET_KEY"];
    
    try {
        net::io_context ioc;
        ssl::context ctx{ssl::context::tlsv12_client};
        ctx.set_verify_mode(ssl::verify_none);
        
        tcp::resolver resolver{ioc};
        auto const results = resolver.resolve("ws-api.ripio.com", "443");
        
        websocket::stream<beast::ssl_stream<tcp::socket>> ws{ioc, ctx};
        beast::get_lowest_layer(ws).connect(results);
        
        ws.next_layer().handshake(ssl::stream_base::client);
        ws.handshake("ws-api.ripio.com", "/");
        
        std::cout << "Connected to WebSocket API" << std::endl;
        
        auto timestamp = std::chrono::duration_cast<std::chrono::milliseconds>(
            std::chrono::system_clock::now().time_since_epoch()).count();
        
        json bodyParams = {
            {"pair", "ABC_DEF"},
            {"side", "buy"},
            {"type", "limit"},
            {"amount", 0.01},
            {"price", 300000}
        };
        
        std::string bodyJson = bodyParams.dump();
        std::string signature = generateSignature(timestamp, bodyJson, secretKey);
        
        json request = {
            {"id", "req-create-001"},
            {"method", "order.create"},
            {"params", {
                {"pair", "ABC_DEF"},
                {"side", "buy"},
                {"type", "limit"},
                {"amount", 0.01},
                {"price", 300000},
                {"api_token", apiKey},
                {"timestamp", timestamp},
                {"signature", signature}
            }}
        };
        
        std::string requestStr = request.dump();
        std::cout << "\nSending request: " << requestStr << std::endl;
        
        ws.write(net::buffer(requestStr));
        
        beast::flat_buffer buffer;
        ws.read(buffer);
        std::cout << "\nReceived response: " << beast::make_printable(buffer.data()) << std::endl;
        
        ws.close(websocket::close_code::normal);
        
    } catch (std::exception const& e) {
        std::cerr << "Error: " << e.what() << std::endl;
        return 1;
    }
    
    return 0;
}
