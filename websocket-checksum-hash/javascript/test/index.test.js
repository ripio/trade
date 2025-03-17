const checksumLib = require('../index.js')

test('should generate all checksum hashes accurately', () => {
  const orderbook1 = {
    asks: [
      { price: 0.3965, amount: 44149.815 },
      { price: 0.3967, amount: 16000 },
    ],
    bids: [
      { price: 0.396, amount: 51 },
      { price: 0.396, amount: 25 },
      { price: 0.3958, amount: 18570 },
    ],
  }
  expect(checksumLib.getOrderbookChecksum(orderbook1)).toBe('3802968298')

  const orderbook2 = {
    asks: [
      { price: 16015.39, amount: 0.15 },
      { price: 16015.4, amount: 1.73639097 },
      { price: 16015.56, amount: 2 },
    ],
    bids: [
      { price: 15977.45, amount: 0.00620606 },
      { price: 15977.44, amount: 0.1 },
      { price: 15977.4, amount: 0.35 },
    ],
  }
  expect(checksumLib.getOrderbookChecksum(orderbook2)).toBe('2863372953')

  const orderbook3 = { asks: [{ price: 16015.39, amount: 0.15 }], bids: [] }
  expect(checksumLib.getOrderbookChecksum(orderbook3)).toBe('3417282216')

  const orderbook4 = { asks: [], bids: [] }
  expect(checksumLib.getOrderbookChecksum(orderbook4)).toBe('0')

  const orderbook5 = { asks: [{ price: 0.99, amount: 1 }], bids: [] }
  expect(checksumLib.getOrderbookChecksum(orderbook5)).toBe('2342619789')

  const orderbook6 = { bids: [{ price: 0.99, amount: 1 }], asks: [{ price: 1.01, amount: 2 }] }
  expect(checksumLib.getOrderbookChecksum(orderbook6)).toBe('2111165642')

  const orderbook7 = { bids: [{ price: 10.01, amount: 1 }], asks: [] }
  expect(checksumLib.getOrderbookChecksum(orderbook7)).toBe('429948050')

  const orderbook8 = {
    bids: [],
    asks: [
      { price: 1, amount: 100 },
      { price: 2, amount: 100 },
      { price: 3, amount: 100 },
    ],
  }
  expect(checksumLib.getOrderbookChecksum(orderbook8)).toBe('158396744')

  const orderbook9 = { bids: [], asks: [{ price: 1, amount: 99.9 }] }
  expect(checksumLib.getOrderbookChecksum(orderbook9)).toBe('334011253')

  const orderbook10 = {
    bids: [
      { price: 58, amount: 1.9599 },
      { price: 57.45, amount: 0.01969999 },
      { price: 57.39, amount: 1.88 },
      { price: 57.31, amount: 1.48 },
      { price: 57.29, amount: 1.07 },
      { price: 57.18, amount: 0.384 },
      { price: 57.14, amount: 0.345 },
      { price: 57.12, amount: 0.9 },
      { price: 57.1, amount: 1.72 },
      { price: 56.97, amount: 4.91 },
    ],
    asks: [
      { price: 60, amount: 3 },
      { price: 60.5, amount: 1 },
      { price: 61, amount: 3 },
      { price: 62, amount: 2 },
      { price: 63, amount: 1 },
      { price: 66, amount: 0.1 },
      { price: 67, amount: 0.1 },
      { price: 68, amount: 2 },
      { price: 69, amount: 1 },
      { price: 70, amount: 2.1 },
    ],
  }
  expect(checksumLib.getOrderbookChecksum(orderbook10)).toBe('1666031697')
})

test('should trim all values to the appropriate format', () => {
  expect(checksumLib.trimValue('0.1234')).toBe('1234')
  expect(checksumLib.trimValue('0.00001234')).toBe('1234')
  expect(checksumLib.trimValue('32.00001234')).toBe('3200001234')
  expect(checksumLib.trimValue('0')).toBe('')
  expect(checksumLib.trimValue('0.0')).toBe('')
  expect(checksumLib.trimValue('1.0')).toBe('10')
  expect(checksumLib.trimValue('1.00')).toBe('100')
  expect(checksumLib.trimValue('0.3965')).toBe('3965')
  expect(checksumLib.trimValue('16000.0')).toBe('160000')
  expect(checksumLib.trimValue('0.0019')).toBe('19')
  expect(checksumLib.trimValue('')).toBe('')
  expect(checksumLib.trimValue('1.01')).toBe('101')
})
