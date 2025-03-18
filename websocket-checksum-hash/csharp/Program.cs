using System;
using System.Collections.Generic;

namespace WebSocketChecksumHash
{
    class Program
    {
        static void Main(string[] args)
        {
            TestGetOrderbookChecksum();
            TestTrimValue();
            Console.WriteLine("All tests passed!");
        }

        static void TestGetOrderbookChecksum()
        {
            // Test case 1
            var orderbook1 = new Dictionary<string, List<Dictionary<string, object>>>();
            
            var asks1 = new List<Dictionary<string, object>>();
            asks1.Add(new Dictionary<string, object> {
                { "price", 0.3965 },
                { "amount", 44149.815 }
            });
            asks1.Add(new Dictionary<string, object> {
                { "price", 0.3967 },
                { "amount", 16000 }
            });
            
            var bids1 = new List<Dictionary<string, object>>();
            bids1.Add(new Dictionary<string, object> {
                { "price", 0.396 },
                { "amount", 51 }
            });
            bids1.Add(new Dictionary<string, object> {
                { "price", 0.396 },
                { "amount", 25 }
            });
            bids1.Add(new Dictionary<string, object> {
                { "price", 0.3958 },
                { "amount", 18570 }
            });
            
            orderbook1.Add("asks", asks1);
            orderbook1.Add("bids", bids1);
            
            var checksum1 = ChecksumHash.GetOrderbookChecksum(orderbook1);
            AssertEqual(checksum1, "3802968298", "Test case 1");
            
            // Test case 2 - Empty orderbook
            var orderbook2 = new Dictionary<string, List<Dictionary<string, object>>>();
            orderbook2.Add("asks", new List<Dictionary<string, object>>());
            orderbook2.Add("bids", new List<Dictionary<string, object>>());
            
            var checksum2 = ChecksumHash.GetOrderbookChecksum(orderbook2);
            AssertEqual(checksum2, "0", "Test case 2");
            
            // Test case 3 - Only asks, no bids
            var orderbook3 = new Dictionary<string, List<Dictionary<string, object>>>();
            
            var asks3 = new List<Dictionary<string, object>>();
            asks3.Add(new Dictionary<string, object> {
                { "price", 16015.39 },
                { "amount", 0.15 }
            });
            
            orderbook3.Add("asks", asks3);
            orderbook3.Add("bids", new List<Dictionary<string, object>>());
            
            var checksum3 = ChecksumHash.GetOrderbookChecksum(orderbook3);
            AssertEqual(checksum3, "3417282216", "Test case 3");
        }
        
        static void TestTrimValue()
        {
            AssertEqual(ChecksumHash.TrimValue("0.1234"), "1234", "Trim test 1");
            AssertEqual(ChecksumHash.TrimValue("0.00001234"), "1234", "Trim test 2");
            AssertEqual(ChecksumHash.TrimValue("32.00001234"), "3200001234", "Trim test 3");
            AssertEqual(ChecksumHash.TrimValue("0"), "", "Trim test 4");
            AssertEqual(ChecksumHash.TrimValue("0.0"), "", "Trim test 5");
            AssertEqual(ChecksumHash.TrimValue("1.0"), "10", "Trim test 6");
            AssertEqual(ChecksumHash.TrimValue("1.00"), "100", "Trim test 7");
            AssertEqual(ChecksumHash.TrimValue("0.3965"), "3965", "Trim test 8");
            AssertEqual(ChecksumHash.TrimValue("16000.0"), "160000", "Trim test 9");
            AssertEqual(ChecksumHash.TrimValue("0.0019"), "19", "Trim test 10");
            AssertEqual(ChecksumHash.TrimValue(""), "", "Trim test 11");
            AssertEqual(ChecksumHash.TrimValue("1.01"), "101", "Trim test 12");
        }
        
        static void AssertEqual(string actual, string expected, string testName)
        {
            if (actual != expected)
            {
                throw new Exception($"{testName} failed: expected '{expected}', got '{actual}'");
            }
        }
    }
}
