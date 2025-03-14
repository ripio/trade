### Ripio Trade | Authentication | PHP

**IMPORTANT!** For the post example, `Create Order` is the endpoint being called, but to be safe it uses an invalid pair by default, you can change this if needed.

#### Setup

Before running the examples, make sure to create a `.env` file with your API credentials:

```
$ cp ../.env.sample .env
```

Then update the `.env` file with your actual API_KEY and SECRET_KEY.

#### Running

You can execute the examples in this directory using Docker Compose with the following command:

```
$ docker-compose up --build
```

This will build and run both the GET and POST examples.

#### Requirements

- PHP 7.4 or higher
- Composer (for dependency management)
- Docker and Docker Compose (for containerized execution)

#### Dependencies

The PHP implementation uses:
- vlucas/phpdotenv: For loading environment variables from .env file
- PHP's built-in cURL extension: For making HTTP requests
