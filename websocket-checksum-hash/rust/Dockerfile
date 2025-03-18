FROM rust:1.67

WORKDIR /app

COPY . .

RUN cargo build

CMD ["cargo", "test"]
