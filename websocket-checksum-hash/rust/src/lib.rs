use std::cmp::min;

/// Order structure representing a bid or ask in the orderbook
#[derive(Debug, Clone)]
pub struct Order {
    pub price: f64,
    pub amount: f64,
}

/// Orderbook structure containing bids and asks
#[derive(Debug, Default)]
pub struct Orderbook {
    pub bids: Vec<Order>,
    pub asks: Vec<Order>,
}

/// Remove decimal points and leading zeros from a string value.
///
/// # Arguments
///
/// * `value` - The string value to trim.
///
/// # Returns
///
/// The trimmed string.
pub fn trim_value(value: &str) -> String {
    let without_decimal = value.replace(".", "");
    without_decimal.trim_start_matches('0').to_string()
}

/// Process orders for checksum calculation.
///
/// # Arguments
///
/// * `orders` - Vector of orders.
///
/// # Returns
///
/// Processed string.
fn checksum_input(orders: &[Order]) -> String {
    let mut orders_str = String::new();
    let iterations = min(orders.len(), 10);
    
    for i in 0..iterations {
        // Convert directly to string without specifying precision
        let price = orders[i].price.to_string();
        let amount = orders[i].amount.to_string();
        orders_str.push_str(&trim_value(&price));
        orders_str.push_str(&trim_value(&amount));
    }
    
    orders_str
}

/// Generate a checksum hash for an orderbook.
///
/// # Arguments
///
/// * `orderbook` - An Orderbook struct with bids and asks vectors.
///
/// # Returns
///
/// A string representation of the CRC32 checksum.
pub fn get_orderbook_checksum(orderbook: &Orderbook) -> String {
    let bids = checksum_input(&orderbook.bids);
    let asks = checksum_input(&orderbook.asks);
    
    if bids.is_empty() && asks.is_empty() {
        return "0".to_string();
    }
    
    let combined = format!("{}{}", bids, asks);
    let checksum = crc32fast::hash(combined.as_bytes());
    checksum.to_string()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_trim_value() {
        assert_eq!(trim_value("0.1234"), "1234");
        assert_eq!(trim_value("0.00001234"), "1234");
        assert_eq!(trim_value("32.00001234"), "3200001234");
        assert_eq!(trim_value("0"), "");
        assert_eq!(trim_value("0.0"), "");
        assert_eq!(trim_value("1.0"), "10");
        assert_eq!(trim_value("1.00"), "100");
        assert_eq!(trim_value("0.3965"), "3965");
        assert_eq!(trim_value("16000.0"), "160000");
        assert_eq!(trim_value("0.0019"), "19");
        assert_eq!(trim_value(""), "");
        assert_eq!(trim_value("1.01"), "101");
    }

    #[test]
    fn test_get_orderbook_checksum() {
        // Test case 1
        let mut orderbook1 = Orderbook::default();
        
        orderbook1.asks.push(Order { price: 0.3965, amount: 44149.815 });
        orderbook1.asks.push(Order { price: 0.3967, amount: 16000.0 });
        
        orderbook1.bids.push(Order { price: 0.396, amount: 51.0 });
        orderbook1.bids.push(Order { price: 0.396, amount: 25.0 });
        orderbook1.bids.push(Order { price: 0.3958, amount: 18570.0 });
        
        assert_eq!(get_orderbook_checksum(&orderbook1), "3802968298");
        
        // Test case 2 - Empty orderbook
        let orderbook2 = Orderbook::default();
        assert_eq!(get_orderbook_checksum(&orderbook2), "0");
        
        // Test case 3 - Only asks, no bids
        let mut orderbook3 = Orderbook::default();
        orderbook3.asks.push(Order { price: 16015.39, amount: 0.15 });
        
        assert_eq!(get_orderbook_checksum(&orderbook3), "3417282216");
        
        // Test case 4 - Only bids, no asks
        let mut orderbook4 = Orderbook::default();
        orderbook4.bids.push(Order { price: 10.01, amount: 1.0 });
        
        assert_eq!(get_orderbook_checksum(&orderbook4), "429948050");
    }
}
