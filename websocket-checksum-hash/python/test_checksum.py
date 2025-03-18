import unittest
from checksum import get_orderbook_checksum, trim_value

class TestChecksum(unittest.TestCase):
    def test_get_orderbook_checksum(self):
        orderbook1 = {
            'asks': [
                {'price': 0.3965, 'amount': 44149.815},
                {'price': 0.3967, 'amount': 16000},
            ],
            'bids': [
                {'price': 0.396, 'amount': 51},
                {'price': 0.396, 'amount': 25},
                {'price': 0.3958, 'amount': 18570},
            ],
        }
        self.assertEqual(get_orderbook_checksum(orderbook1), '3802968298')

        orderbook2 = {
            'asks': [
                {'price': 16015.39, 'amount': 0.15},
                {'price': 16015.4, 'amount': 1.73639097},
                {'price': 16015.56, 'amount': 2},
            ],
            'bids': [
                {'price': 15977.45, 'amount': 0.00620606},
                {'price': 15977.44, 'amount': 0.1},
                {'price': 15977.4, 'amount': 0.35},
            ],
        }
        self.assertEqual(get_orderbook_checksum(orderbook2), '2863372953')

        orderbook3 = {'asks': [{'price': 16015.39, 'amount': 0.15}], 'bids': []}
        self.assertEqual(get_orderbook_checksum(orderbook3), '3417282216')

        orderbook4 = {'asks': [], 'bids': []}
        self.assertEqual(get_orderbook_checksum(orderbook4), '0')

        orderbook5 = {'asks': [{'price': 0.99, 'amount': 1}], 'bids': []}
        self.assertEqual(get_orderbook_checksum(orderbook5), '2342619789')

    def test_trim_value(self):
        self.assertEqual(trim_value('0.1234'), '1234')
        self.assertEqual(trim_value('0.00001234'), '1234')
        self.assertEqual(trim_value('32.00001234'), '3200001234')
        self.assertEqual(trim_value('0'), '')
        self.assertEqual(trim_value('0.0'), '')
        self.assertEqual(trim_value('1.0'), '10')
        self.assertEqual(trim_value('1.00'), '100')
        self.assertEqual(trim_value('0.3965'), '3965')
        self.assertEqual(trim_value('16000.0'), '160000')
        self.assertEqual(trim_value('0.0019'), '19')
        self.assertEqual(trim_value(''), '')
        self.assertEqual(trim_value('1.01'), '101')

if __name__ == '__main__':
    unittest.main()
