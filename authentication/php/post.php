<?php
// Load environment variables from .env file
require_once __DIR__ . '/vendor/autoload.php';

$dotenv = Dotenv\Dotenv::createImmutable(__DIR__);
$dotenv->load();

// Check if required environment variables are set
if (!isset($_ENV['API_KEY']) || !isset($_ENV['SECRET_KEY'])) {
    throw new Exception("Required environment variables (API_KEY, SECRET_KEY) are missing.");
}

$apiKey = $_ENV['API_KEY'];
$secretKey = $_ENV['SECRET_KEY'];

$payload = [
    'method' => 'POST',
    'path' => '/trade/orders',
    'body' => [
        'amount' => 0.01,
        'pair' => 'ABC_DEF',
        'price' => 300000,
        'side' => 'buy',
        'type' => 'limit'
    ]
];

$method = $payload['method'];
$path = parse_url($payload['path'], PHP_URL_PATH);
$body = json_encode($payload['body'], JSON_UNESCAPED_SLASHES);
$timestamp = (string)round(microtime(true) * 1000);
$message = $timestamp . $method . $path . $body;
$signature = base64_encode(hash_hmac('sha256', $message, $secretKey, true));
$url = "https://api.ripio.com" . $payload['path'];

// Initialize cURL session
$ch = curl_init($url);

// Set cURL options
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, $body);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, false);
curl_setopt($ch, CURLOPT_HTTPHEADER, [
    'Content-Type: application/json',
    'Authorization: ' . $apiKey,
    'Signature: ' . $signature,
    'Timestamp: ' . $timestamp
]);

// Execute cURL session and get the response
$response = curl_exec($ch);

// Check for cURL errors
if (curl_errno($ch)) {
    echo 'cURL error: ' . curl_error($ch);
} else {
    // Decode and print the response
    $result = json_decode($response, true);
    echo "response: ";
    print_r($result);
}

// Close cURL session
curl_close($ch);
