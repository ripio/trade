package com.ripio.trade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChecksumHashTest {
    
    public static void main(String[] args) {
        testGetOrderbookChecksum();
        testTrimValue();
        System.out.println("All tests passed!");
    }
    
    private static void testGetOrderbookChecksum() {
        // Test case 1
        Map<String, List<Map<String, Object>>> orderbook1 = new HashMap<>();
        
        List<Map<String, Object>> asks1 = new ArrayList<>();
        Map<String, Object> ask1 = new HashMap<>();
        ask1.put("price", 0.3965);
        ask1.put("amount", 44149.815);
        asks1.add(ask1);
        
        Map<String, Object> ask2 = new HashMap<>();
        ask2.put("price", 0.3967);
        ask2.put("amount", 16000);
        asks1.add(ask2);
        
        List<Map<String, Object>> bids1 = new ArrayList<>();
        Map<String, Object> bid1 = new HashMap<>();
        bid1.put("price", 0.396);
        bid1.put("amount", 51);
        bids1.add(bid1);
        
        Map<String, Object> bid2 = new HashMap<>();
        bid2.put("price", 0.396);
        bid2.put("amount", 25);
        bids1.add(bid2);
        
        Map<String, Object> bid3 = new HashMap<>();
        bid3.put("price", 0.3958);
        bid3.put("amount", 18570);
        bids1.add(bid3);
        
        orderbook1.put("asks", asks1);
        orderbook1.put("bids", bids1);
        
        String checksum1 = ChecksumHash.getOrderbookChecksum(orderbook1);
        assert checksum1.equals("3802968298") : "Test case 1 failed: " + checksum1;
        
        // Test case 2 - Empty orderbook
        Map<String, List<Map<String, Object>>> orderbook2 = new HashMap<>();
        orderbook2.put("asks", new ArrayList<>());
        orderbook2.put("bids", new ArrayList<>());
        
        String checksum2 = ChecksumHash.getOrderbookChecksum(orderbook2);
        assert checksum2.equals("0") : "Test case 2 failed: " + checksum2;
        
        // Test case 3 - Only asks, no bids
        Map<String, List<Map<String, Object>>> orderbook3 = new HashMap<>();
        
        List<Map<String, Object>> asks3 = new ArrayList<>();
        Map<String, Object> ask3 = new HashMap<>();
        ask3.put("price", 16015.39);
        ask3.put("amount", 0.15);
        asks3.add(ask3);
        
        orderbook3.put("asks", asks3);
        orderbook3.put("bids", new ArrayList<>());
        
        String checksum3 = ChecksumHash.getOrderbookChecksum(orderbook3);
        assert checksum3.equals("3417282216") : "Test case 3 failed: " + checksum3;
    }
    
    private static void testTrimValue() {
        assert ChecksumHash.trimValue("0.1234").equals("1234") : "Trim test 1 failed";
        assert ChecksumHash.trimValue("0.00001234").equals("1234") : "Trim test 2 failed";
        assert ChecksumHash.trimValue("32.00001234").equals("3200001234") : "Trim test 3 failed";
        assert ChecksumHash.trimValue("0").equals("") : "Trim test 4 failed";
        assert ChecksumHash.trimValue("0.0").equals("") : "Trim test 5 failed";
        assert ChecksumHash.trimValue("1.0").equals("10") : "Trim test 6 failed";
        assert ChecksumHash.trimValue("1.00").equals("100") : "Trim test 7 failed";
        assert ChecksumHash.trimValue("0.3965").equals("3965") : "Trim test 8 failed";
        assert ChecksumHash.trimValue("16000.0").equals("160000") : "Trim test 9 failed";
        assert ChecksumHash.trimValue("0.0019").equals("19") : "Trim test 10 failed";
        assert ChecksumHash.trimValue("").equals("") : "Trim test 11 failed";
        assert ChecksumHash.trimValue("1.01").equals("101") : "Trim test 12 failed";
    }
}
