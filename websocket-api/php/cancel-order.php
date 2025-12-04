<?php
require_once __DIR__ . '/vendor/autoload.php';

use Ratchet\Client\Connector;
use React\EventLoop\Loop;

$dotenv = Dotenv\Dotenv::createImmutable(__DIR__);
$dotenv->load();

if (!isset($_ENV['API_KEY']) || !isset($_ENV['SECRET_KEY'])) {
    throw new Exception("Required environment variables (API_KEY, SECRET_KEY) are missing.");
}

$apiKey = $_ENV['API_KEY'];
$secretKey = $_ENV['SECRET_KEY'];

function generateSignature($timestamp, $bodyParams, $secretKey) {
    $message = $timestamp . json_encode($bodyParams);
    return base64_encode(hash_hmac('sha256', $message, $secretKey, true));
}

$loop = Loop::get();
$connector = new Connector($loop);

$connector('wss://ws-api.ripio.com')->then(function($conn) use ($apiKey, $secretKey) {
    echo "Connected to WebSocket API\n";
    
    $timestamp = round(microtime(true) * 1000);
    $bodyParams = [
        'id' => '7155ED34-9EC4-4733-8B32-1E4319CB662F' // Replace with actual order ID
    ];
    
    $signature = generateSignature($timestamp, $bodyParams, $GLOBALS['secretKey']);
    
    $request = [
        'id' => 'req-cancel-001',
        'method' => 'order.cancel',
        'params' => array_merge($bodyParams, [
            'api_token' => $GLOBALS['apiKey'],
            'timestamp' => $timestamp,
            'signature' => $signature
        ])
    ];
    
    echo "\nSending request: " . json_encode($request) . "\n";
    $conn->send(json_encode($request));
    
    $conn->on('message', function($msg) use ($conn) {
        echo "\nReceived response: " . $msg . "\n";
        $conn->close();
    });
    
    $conn->on('close', function($code = null, $reason = null) {
        echo "\nWebSocket connection closed\n";
    });
    
}, function($e) {
    echo "Could not connect: {$e->getMessage()}\n";
});

$loop->run();
