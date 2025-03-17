const { crc32 } = require('crc')

exports.getOrderbookChecksum = (orderbook) => {
  const checksumInput = (orders) => {
    let ordersStr = ''
    const iterations = Math.min(orders.length, 10)
    for (let i = 0; i < iterations; i++) {
      const price = orders[i].price.toString()
      const amount = orders[i].amount.toString()
      ordersStr += trimValue(price) + trimValue(amount)
    }
    return ordersStr
  }
  const bids = checksumInput(orderbook.bids)
  const asks = checksumInput(orderbook.asks)
  const checksum = crc32(bids + asks).toString(10)
  return checksum
}

const trimValue = (value) => {
  return value.replace('.', '').replace(/^0+/, '')
}

exports.trimValue = trimValue
