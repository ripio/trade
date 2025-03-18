<?php

require_once 'ChecksumHash.php';

/**
 * Simple test runner for the ChecksumHash class
 */
function runTests() {
    testGetOrderbookChecksum();
    testTrimValue();
    echo "All tests passed!\n";
}

/**
 * Test the getOrderbookChecksum method
 */
function testGetOrderbookChecksum() {
    // Test case 1
    $orderbook1 = [
        'asks' => [
            ['price' => 0.3965, 'amount' => 44149.815],
            ['price' => 0.3967, 'amount' => 16000],
        ],
        'bids' => [
            ['price' => 0.396, 'amount' => 51],
            ['price' => 0.396, 'amount' => 25],
            ['price' => 0.3958, 'amount' => 18570],
        ],
    ];
    assertEqual(ChecksumHash::getOrderbookChecksum($orderbook1), '3802968298', 'Test case 1');

    // Test case 2 - Empty orderbook
    $orderbook2 = [
        'asks' => [],
        'bids' => [],
    ];
    assertEqual(ChecksumHash::getOrderbookChecksum($orderbook2), '0', 'Test case 2');

    // Test case 3 - Only asks, no bids
    $orderbook3 = [
        'asks' => [
            ['price' => 16015.39, 'amount' => 0.15],
        ],
        'bids' => [],
    ];
    assertEqual(ChecksumHash::getOrderbookChecksum($orderbook3), '3417282216', 'Test case 3');

    // Test case 4 - Only bids, no asks
    $orderbook4 = [
        'asks' => [],
        'bids' => [
            ['price' => 10.01, 'amount' => 1],
        ],
    ];
    assertEqual(ChecksumHash::getOrderbookChecksum($orderbook4), '429948050', 'Test case 4');
}

/**
 * Test the trimValue method
 */
function testTrimValue() {
    assertEqual(ChecksumHash::trimValue('0.1234'), '1234', 'Trim test 1');
    assertEqual(ChecksumHash::trimValue('0.00001234'), '1234', 'Trim test 2');
    assertEqual(ChecksumHash::trimValue('32.00001234'), '3200001234', 'Trim test 3');
    assertEqual(ChecksumHash::trimValue('0'), '', 'Trim test 4');
    assertEqual(ChecksumHash::trimValue('0.0'), '', 'Trim test 5');
    assertEqual(ChecksumHash::trimValue('1.0'), '10', 'Trim test 6');
    assertEqual(ChecksumHash::trimValue('1.00'), '100', 'Trim test 7');
    assertEqual(ChecksumHash::trimValue('0.3965'), '3965', 'Trim test 8');
    assertEqual(ChecksumHash::trimValue('16000.0'), '160000', 'Trim test 9');
    assertEqual(ChecksumHash::trimValue('0.0019'), '19', 'Trim test 10');
    assertEqual(ChecksumHash::trimValue(''), '', 'Trim test 11');
    assertEqual(ChecksumHash::trimValue('1.01'), '101', 'Trim test 12');
}

/**
 * Assert that two values are equal
 */
function assertEqual($actual, $expected, $testName) {
    if ($actual !== $expected) {
        throw new Exception("$testName failed: expected '$expected', got '$actual'");
    }
}

// Run the tests
runTests();
