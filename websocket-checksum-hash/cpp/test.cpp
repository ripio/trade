#include <iostream>
#include <cassert>
#include <string>
#include <vector>
#include "checksum.cpp"

void testGetOrderbookChecksum() {
    // Test case 1
    Orderbook orderbook1;
    
    orderbook1.asks.push_back({0.3965, 44149.815});
    orderbook1.asks.push_back({0.3967, 16000});
    
    orderbook1.bids.push_back({0.396, 51});
    orderbook1.bids.push_back({0.396, 25});
    orderbook1.bids.push_back({0.3958, 18570});
    
    std::string checksum1 = getOrderbookChecksum(orderbook1);
    assert(checksum1 == "3802968298");
    
    // Test case 2 - Empty orderbook
    Orderbook orderbook2;
    std::string checksum2 = getOrderbookChecksum(orderbook2);
    assert(checksum2 == "0");
    
    // Test case 3 - Only asks, no bids
    Orderbook orderbook3;
    orderbook3.asks.push_back({16015.39, 0.15});
    
    std::string checksum3 = getOrderbookChecksum(orderbook3);
    assert(checksum3 == "3417282216");
    
    // Test case 4 - Only bids, no asks
    Orderbook orderbook4;
    orderbook4.bids.push_back({10.01, 1});
    
    std::string checksum4 = getOrderbookChecksum(orderbook4);
    assert(checksum4 == "429948050");
    
    std::cout << "All orderbook checksum tests passed!" << std::endl;
}

void testTrimValue() {
    assert(trimValue("0.1234") == "1234");
    assert(trimValue("0.00001234") == "1234");
    assert(trimValue("32.00001234") == "3200001234");
    assert(trimValue("0") == "");
    assert(trimValue("0.0") == "");
    assert(trimValue("1.0") == "10");
    assert(trimValue("1.00") == "100");
    assert(trimValue("0.3965") == "3965");
    assert(trimValue("16000.0") == "160000");
    assert(trimValue("0.0019") == "19");
    assert(trimValue("") == "");
    assert(trimValue("1.01") == "101");
    
    std::cout << "All trim value tests passed!" << std::endl;
}

int main() {
    testTrimValue();
    testGetOrderbookChecksum();
    
    std::cout << "All tests passed!" << std::endl;
    return 0;
}
