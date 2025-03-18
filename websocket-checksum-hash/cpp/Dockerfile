FROM gcc:11

WORKDIR /app

# Install CMake
RUN apt-get update && \
    apt-get install -y cmake && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY . .

# Build and debug
RUN mkdir -p build && \
    cd build && \
    cmake .. && \
    make && \
    echo "Contents of build directory:" && \
    ls -la && \
    echo "Contents of parent directory:" && \
    ls -la ..

# Run the executable directly with the shell
CMD ["sh", "-c", "cd /app/build && ./test_checksum"]
