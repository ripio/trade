### Ripio Trade - WebSocket Checksum Hash

This example serves as a guide for generating checksum hashes for order book WebSocket messages. It’s essential that the checksum is generated consistently to ensure reliable comparison with the checksum received from the WebSocket messages.

Assuming the order book is received in the following format:

```
{
  "body": {
    "asks": [
      {
        "amount": "0.002239",
        "price": "63721.68"
      }
    ],
    "bids": [
      {
        "amount": "0.000843",
        "price": "63598.18"
      }
    ],
    "hash": "12878522183081097146",
    "pair": "BTC_USDC"
  },
  "id": 2440342293,
  "timestamp": 1727719367437,
  "topic": "orderbook/level_2@BTC_USDC"
}
```

The following logic can be used to generate the checksum hash:

* Start with the top 10 bid orders (or fewer if there are less than 10);
* For each price and amount:
    * Remove any decimal points;
    * Eliminate leading zeros;
    * Concatenate the price and amount as a string;
* Aggregate the results for the top 10 bids (from highest to lowest price), if there are fewer than 10 orders, use whatever is available;
* Repeat the process for the top 10 ask orders (from lowest to highest price);
* Concatenate the results from both bids and asks;
* Calculate the checksum hash for the combined string;
* If your CRC32 library produces a leading zero, remove it before comparing;
* A checksum value of zero indicates that there are no bids or asks in the order book (which is unlikely);
