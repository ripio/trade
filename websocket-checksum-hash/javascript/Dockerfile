FROM node:20-alpine

WORKDIR /app

COPY . .

# Use npm instead of yarn with --no-strict-ssl flag to bypass SSL certificate verification
RUN npm install --no-strict-ssl

CMD ["npm", "test"]
