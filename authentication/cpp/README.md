### Ripio Trade | Authentication | C++

**IMPORTANT!** For the post example, `Create Order` is the endpoint being called, but to be safe it uses an invalid pair by default, you can change this if needed.

#### Prerequisites

The C++ implementation requires the following libraries:
- libcurl (for HTTP requests)
- OpenSSL (for HMAC-SHA256 signature generation)
- nlohmann/json (for JSON handling)

However, you don't need to install these dependencies manually as they are included in the Docker setup.

#### Environment Setup

Before running the examples, make sure to create a `.env` file with your API credentials:

```
$ cp ../.env.sample ./.env
```

Then edit the `.env` file to include your actual API key and secret key.

#### Running

You can execute the examples in this directory using Docker Compose with the following command:

```
$ docker-compose up --build
```

This will build and run both the GET and POST examples.

#### Implementation Details

The C++ implementation includes:

1. **get.cpp**: Demonstrates how to make authenticated GET requests to the Ripio Trade API.
2. **post.cpp**: Demonstrates how to make authenticated POST requests with a JSON body.

Both examples:
- Read API credentials from a `.env` file
- Generate the appropriate HMAC-SHA256 signature
- Make HTTP requests with the required authentication headers
- Parse and display the JSON response

#### Building Without Docker

If you prefer to build and run the examples locally without Docker, you'll need to:

1. Install the required dependencies (libcurl, OpenSSL, nlohmann/json)
2. Build using CMake:

```
$ mkdir build
$ cd build
$ cmake ..
$ make
```

3. Run the examples:

```
$ ./get
$ ./post
```
