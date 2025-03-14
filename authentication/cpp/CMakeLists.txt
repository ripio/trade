cmake_minimum_required(VERSION 3.10)
project(ripio_trade_auth)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Find required packages
find_package(CURL REQUIRED)
find_package(OpenSSL REQUIRED)

# Include directories
include_directories(${CURL_INCLUDE_DIRS})
include_directories(${OPENSSL_INCLUDE_DIR})
include_directories(/usr/include) # For nlohmann/json

# Add get executable
add_executable(get get.cpp)
target_link_libraries(get ${CURL_LIBRARIES} ${OPENSSL_CRYPTO_LIBRARY})

# Add post executable
add_executable(post post.cpp)
target_link_libraries(post ${CURL_LIBRARIES} ${OPENSSL_CRYPTO_LIBRARY})

# Print debug information
message(STATUS "CURL_LIBRARIES: ${CURL_LIBRARIES}")
message(STATUS "OPENSSL_CRYPTO_LIBRARY: ${OPENSSL_CRYPTO_LIBRARY}")
