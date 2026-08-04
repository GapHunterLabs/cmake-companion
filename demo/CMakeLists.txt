cmake_minimum_required(VERSION 3.20)
project(AcmePaymentGateway LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(OpenSSL REQUIRED)
find_package(Threads REQUIRED)

add_library(payment_core STATIC
    src/ledger.cpp
    src/fraud_detector.cpp
    src/tls_client.cpp
)

target_include_directories(payment_core PUBLIC ${CMAKE_SOURCE_DIR}/include)
target_link_libraries(payment_core PUBLIC OpenSSL::SSL OpenSSL::Crypto Threads::Threads)

add_executable(payment_gateway src/main.cpp)
target_link_libraries(payment_gateway PRIVATE payment_core)

add_executable(payment_gateway_tests
    tests/ledger_test.cpp
    tests/fraud_detector_test.cpp
)
target_link_libraries(payment_gateway_tests PRIVATE payment_core)

install(TARGETS payment_gateway DESTINATION bin)

if(NOT (CMAKE_BUILD_TYPE STREQUAL "Release" OR CMAKE_BUILD_TYPE STREQUAL "Debug"))
    message(WARNING "CMAKE_BUILD_TYPE not set, defaulting to Release")
    set(CMAKE_BUILD_TYPE "Release")
endif()
