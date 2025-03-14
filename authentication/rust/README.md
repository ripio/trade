### Ripio Trade | Authentication | Rust

**IMPORTANT!** For the post example, `Create Order` is the endpoint being called, but to be safe it uses an invalid pair by default, you can change this if needed.

#### Requirements

- Rust 1.85 or later
- Docker and Docker Compose (for running with Docker)

#### Running Locally

To run the examples locally, you'll need to:

1. Copy the `.env.sample` file from the parent directory to this directory and rename it to `.env`
2. Fill in your API key and secret key in the `.env` file
3. Build and run the examples:

```bash
# For the GET example
cargo run --bin get

# For the POST example
cargo run --bin post
```

#### Running with Docker Compose

You can execute the examples in this directory using Docker Compose with the following command:

```bash
docker-compose up --build
```

This will build and run both the GET and POST examples.

#### Implementation Details

This implementation uses:
- `reqwest` for HTTP requests
- `hmac` and `sha2` for HMAC-SHA256 signature generation
- `base64` for encoding the signature
- `serde` and `serde_json` for JSON serialization/deserialization
- `dotenv` for loading environment variables
