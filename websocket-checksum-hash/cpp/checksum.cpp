#include <string>
#include <vector>
#include <map>
#include <algorithm>
#include <iostream>
#include <sstream>
#include <regex>
#include <cstdint>
#include <iomanip>

/**
 * Order structure representing a bid or ask in the orderbook
 */
struct Order {
    double price;
    double amount;
};

/**
 * Orderbook structure containing bids and asks
 */
struct Orderbook {
    std::vector<Order> bids;
    std::vector<Order> asks;
};

/**
 * Remove decimal points and leading zeros from a string value.
 * 
 * @param value The string value to trim.
 * @return The trimmed string.
 */
std::string trimValue(const std::string& value) {
    // Remove decimal points
    std::string result = std::regex_replace(value, std::regex("\\."), "");
    
    // Remove leading zeros
    result = std::regex_replace(result, std::regex("^0+"), "");
    
    return result;
}

/**
 * Calculate CRC32 checksum for a string.
 * 
 * @param data Input string.
 * @return CRC32 checksum as uint32_t.
 */
uint32_t crc32(const std::string& data) {
    // CRC-32 polynomial: x^32 + x^26 + x^23 + x^22 + x^16 + x^12 + x^11 + x^10 + x^8 + x^7 + x^5 + x^4 + x^2 + x + 1
    // Polynomial in binary: 100000100110000010001110110110111
    // Polynomial in hex: 0x104C11DB7
    // Reversed polynomial in hex: 0xEDB88320
    
    static uint32_t crc_table[256];
    static bool table_initialized = false;
    
    // Initialize the CRC table on the first call
    if (!table_initialized) {
        for (uint32_t i = 0; i < 256; i++) {
            uint32_t c = i;
            for (int j = 0; j < 8; j++) {
                c = (c & 1) ? (0xEDB88320 ^ (c >> 1)) : (c >> 1);
            }
            crc_table[i] = c;
        }
        table_initialized = true;
    }
    
    uint32_t crc = 0xFFFFFFFF;
    for (size_t i = 0; i < data.length(); i++) {
        crc = crc_table[(crc ^ static_cast<uint8_t>(data[i])) & 0xFF] ^ (crc >> 8);
    }
    
    return ~crc;
}

/**
 * Process orders for checksum calculation.
 * 
 * @param orders Vector of orders.
 * @return Processed string.
 */
std::string checksumInput(const std::vector<Order>& orders) {
    std::stringstream ordersStr;
    size_t iterations = std::min(orders.size(), static_cast<size_t>(10));
    
    for (size_t i = 0; i < iterations; i++) {
        // Convert price and amount to string with exact representation
        char priceBuffer[64], amountBuffer[64];
        snprintf(priceBuffer, sizeof(priceBuffer), "%.15g", orders[i].price);
        snprintf(amountBuffer, sizeof(amountBuffer), "%.15g", orders[i].amount);
        
        std::string priceStr(priceBuffer);
        std::string amountStr(amountBuffer);
        
        ordersStr << trimValue(priceStr) << trimValue(amountStr);
    }
    
    return ordersStr.str();
}

/**
 * Generate a checksum hash for an orderbook.
 * 
 * @param orderbook An Orderbook struct with bids and asks vectors.
 * @return A string representation of the CRC32 checksum.
 */
std::string getOrderbookChecksum(const Orderbook& orderbook) {
    std::string bids = checksumInput(orderbook.bids);
    std::string asks = checksumInput(orderbook.asks);
    
    if (bids.empty() && asks.empty()) {
        return "0";
    }
    
    uint32_t checksum = crc32(bids + asks);
    return std::to_string(checksum);
}
