### Ripio Trade | Authentication | C#

**IMPORTANT!** For the post example, `Create Order` is the endpoint being called, but to be safe it uses an invalid pair by default (ABC_DEF), you can change this if needed.

#### Requirements

- .NET 8.0 SDK
- Docker (optional, for containerized execution)

#### Environment Variables

Create a `.env` file in the current directory with the following variables:

```
API_KEY=YOUR_API_KEY
SECRET_KEY=YOUR_SECRET_KEY
```

#### Running Locally

##### GET Request

```bash
dotnet run Get
```

##### POST Request

```bash
dotnet run Post
```

#### Running with Docker Compose

You can execute the examples in this directory using Docker Compose with the following command:

```bash
docker-compose up --build
```

Or run specific examples:

```bash
docker-compose up get   # For GET request
docker-compose up post  # For POST request
```

#### Implementation Details

This implementation uses:
- DotNetEnv for loading environment variables
- System.Security.Cryptography for HMAC-SHA256 signature generation
- System.Net.Http for making HTTP requests
- System.Text.Json for JSON serialization/deserialization
