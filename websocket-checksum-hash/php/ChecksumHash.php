<?php

/**
 * WebSocket Checksum Hash implementation for PHP
 */
class ChecksumHash
{
    /**
     * Generate a checksum hash for an orderbook.
     *
     * @param array $orderbook An array with 'bids' and 'asks' arrays, each containing
     *                         arrays with 'price' and 'amount' keys.
     * @return string A string representation of the CRC32 checksum.
     */
    public static function getOrderbookChecksum(array $orderbook): string
    {
        $bids = self::checksumInput($orderbook['bids'] ?? []);
        $asks = self::checksumInput($orderbook['asks'] ?? []);
        
        if (empty($bids) && empty($asks)) {
            return '0';
        }
        
        $checksum = crc32($bids . $asks);
        // PHP's crc32 can return negative values, so we need to convert to unsigned
        if ($checksum < 0) {
            $checksum = sprintf('%u', $checksum);
        }
        
        return (string) $checksum;
    }
    
    /**
     * Process orders for checksum calculation.
     *
     * @param array $orders Array of orders.
     * @return string Processed string.
     */
    private static function checksumInput(array $orders): string
    {
        $ordersStr = '';
        
        // If there are no orders, return an empty string
        if (empty($orders)) {
            return $ordersStr;
        }
        
        // Create a copy of the orders array to avoid modifying the original
        $ordersCopy = $orders;
        
        // Sort the orders by price (we'll use the same order for both bids and asks)
        // This is a simplification - in a real implementation, bids would be sorted from highest to lowest
        // and asks from lowest to highest, but the JavaScript implementation doesn't do this sorting
        
        $iterations = min(count($ordersCopy), 10);
        
        for ($i = 0; $i < $iterations; $i++) {
            $price = (string) $ordersCopy[$i]['price'];
            $amount = (string) $ordersCopy[$i]['amount'];
            $ordersStr .= self::trimValue($price) . self::trimValue($amount);
        }
        
        return $ordersStr;
    }
    
    /**
     * Remove decimal points and leading zeros from a string value.
     *
     * @param string $value The string value to trim.
     * @return string The trimmed string.
     */
    public static function trimValue(string $value): string
    {
        $value = str_replace('.', '', $value);
        return ltrim($value, '0');
    }
}
