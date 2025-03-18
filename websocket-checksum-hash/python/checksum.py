import binascii

def trim_value(value):
    """
    Remove decimal points and leading zeros from a string value.
    """
    return value.replace('.', '').lstrip('0')

def get_orderbook_checksum(orderbook):
    """
    Generate a checksum hash for an orderbook.
    
    Args:
        orderbook: A dictionary with 'bids' and 'asks' arrays, each containing
                  dictionaries with 'price' and 'amount' keys.
    
    Returns:
        A string representation of the CRC32 checksum.
    """
    def checksum_input(orders):
        orders_str = ''
        iterations = min(len(orders), 10)
        for i in range(iterations):
            price = str(orders[i]['price'])
            amount = str(orders[i]['amount'])
            orders_str += trim_value(price) + trim_value(amount)
        return orders_str
    
    bids = checksum_input(orderbook.get('bids', []))
    asks = checksum_input(orderbook.get('asks', []))
    
    if not bids and not asks:
        return '0'
    
    checksum = binascii.crc32((bids + asks).encode())
    return str(checksum)
