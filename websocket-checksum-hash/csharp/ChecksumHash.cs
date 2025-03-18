using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;

namespace WebSocketChecksumHash
{
    public class ChecksumHash
    {
        /// <summary>
        /// Generate a checksum hash for an orderbook.
        /// </summary>
        /// <param name="orderbook">A dictionary with 'bids' and 'asks' lists, each containing
        /// dictionaries with 'price' and 'amount' keys.</param>
        /// <returns>A string representation of the CRC32 checksum.</returns>
        public static string GetOrderbookChecksum(Dictionary<string, List<Dictionary<string, object>>> orderbook)
        {
            var bids = ChecksumInput(orderbook.ContainsKey("bids") ? orderbook["bids"] : new List<Dictionary<string, object>>());
            var asks = ChecksumInput(orderbook.ContainsKey("asks") ? orderbook["asks"] : new List<Dictionary<string, object>>());
            
            if (string.IsNullOrEmpty(bids) && string.IsNullOrEmpty(asks))
            {
                return "0";
            }
            
            return CalculateCrc32(bids + asks).ToString();
        }
        
        private static string ChecksumInput(List<Dictionary<string, object>> orders)
        {
            var ordersStr = new StringBuilder();
            var iterations = Math.Min(orders.Count, 10);
            
            for (int i = 0; i < iterations; i++)
            {
                var price = orders[i]["price"].ToString();
                var amount = orders[i]["amount"].ToString();
                ordersStr.Append(TrimValue(price)).Append(TrimValue(amount));
            }
            
            return ordersStr.ToString();
        }
        
        /// <summary>
        /// Remove decimal points and leading zeros from a string value.
        /// </summary>
        /// <param name="value">The string value to trim.</param>
        /// <returns>The trimmed string.</returns>
        public static string TrimValue(string value)
        {
            return value.Replace(".", "").TrimStart('0');
        }
        
        /// <summary>
        /// Calculate CRC32 checksum for a string.
        /// </summary>
        /// <param name="input">Input string.</param>
        /// <returns>CRC32 checksum as uint.</returns>
        private static uint CalculateCrc32(string input)
        {
            // CRC-32 polynomial
            const uint polynomial = 0xEDB88320;
            var bytes = Encoding.UTF8.GetBytes(input);
            uint crc = 0xFFFFFFFF;
            
            foreach (var b in bytes)
            {
                crc ^= b;
                for (int i = 0; i < 8; i++)
                {
                    crc = (crc & 1) != 0 ? (crc >> 1) ^ polynomial : crc >> 1;
                }
            }
            
            return ~crc;
        }
    }
}
