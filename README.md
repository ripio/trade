### Ripio Trade

The purpose of this repository is to provide and maintain examples for using the Ripio Trade API.

Here are the types of examples currently available:

1. [Authentication](authentication)

For each example, you can choose from the following programming languages:

1. Java
2. JavaScript
3. Python
4. Shell + cURL

#### Environment variables

Before you begin, please configure your environment variables using the sample dotenv file:

```
$ cp .env.sample authentication/java/.env
```

You'll need to create a separate file for each language you want to run. Once you’ve created your `.env` file, update the API Key and Secret. 

#### Dependencies

To run the examples, you'll need to install two dependencies: Docker and Docker Compose. Below, you'll find the links with step-by-step installation instructions for both:

1. Docker: https://docs.docker.com/engine/install/
2. Docker Compose: https://docs.docker.com/compose/install/

#### Running

Afterward, you can access the specific example and language you want to test. Instructions for each example are available in the corresponding `README.md` file, [such as this one](authentication/java).
