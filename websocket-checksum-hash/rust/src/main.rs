use websocket_checksum_hash::{get_orderbook_checksum, Order, Orderbook};

fn main() {
    // Create an orderbook
    let mut orderbook = Orderbook::default();
    
    // Add some asks
    orderbook.asks.push(Order { price: 0.3965, amount: 44149.815 });
    orderbook.asks.push(Order { price: 0.3967, amount: 16000.0 });
    
    // Add some bids
    orderbook.bids.push(Order { price: 0.396, amount: 51.0 });
    orderbook.bids.push(Order { price: 0.396, amount: 25.0 });
    orderbook.bids.push(Order { price: 0.3958, amount: 18570.0 });
    
    // Generate checksum
    let checksum = get_orderbook_checksum(&orderbook);
    println!("Checksum: {}", checksum);  // Output: 3802968298
}
