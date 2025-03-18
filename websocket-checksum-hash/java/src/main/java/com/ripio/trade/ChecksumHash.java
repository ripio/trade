package com.ripio.trade;

import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

public class ChecksumHash {
    
    /**
     * Generate a checksum hash for an orderbook.
     * 
     * @param orderbook A map with 'bids' and 'asks' lists, each containing
     *                  maps with 'price' and 'amount' keys.
     * @return A string representation of the CRC32 checksum.
     */
    public static String getOrderbookChecksum(Map<String, List<Map<String, Object>>> orderbook) {
        String bids = checksumInput(orderbook.getOrDefault("bids", List.of()));
        String asks = checksumInput(orderbook.getOrDefault("asks", List.of()));
        
        if (bids.isEmpty() && asks.isEmpty()) {
            return "0";
        }
        
        CRC32 crc = new CRC32();
        crc.update((bids + asks).getBytes());
        return String.valueOf(crc.getValue());
    }
    
    private static String checksumInput(List<Map<String, Object>> orders) {
        StringBuilder ordersStr = new StringBuilder();
        int iterations = Math.min(orders.size(), 10);
        
        for (int i = 0; i < iterations; i++) {
            Map<String, Object> order = orders.get(i);
            String price = order.get("price").toString();
            String amount = order.get("amount").toString();
            ordersStr.append(trimValue(price)).append(trimValue(amount));
        }
        
        return ordersStr.toString();
    }
    
    /**
     * Remove decimal points and leading zeros from a string value.
     * 
     * @param value The string value to trim.
     * @return The trimmed string.
     */
    public static String trimValue(String value) {
        return value.replace(".", "").replaceAll("^0+", "");
    }
}
