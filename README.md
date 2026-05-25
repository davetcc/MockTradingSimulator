# Mock Trading System

This is a mock implementation of a trading system that I use to test my `byte-struct` library. This library is used to
parse struct based C++ messages while avoiding allocation.

The flow of the application is:

`C++ generate price -> Circular buffer -> Java price acquire -> Conflate Prices -> Price Distributor`

## Performance

I've tested the performance of the application and found that it can handle a high volume of price updates with 
minimal overhead, and no allocation in the critical flows.

License: Apache 2.0

1. It allows me to test `byte-struct` functionality and performance.
2. It serves as a learning tool for trading system development.

## How to run this?

1. Clone the repository: `git clone https://github.com/davetcc/mock-trading-simulator.git`
2. Navigate to the project directory: `cd mock-trading-simulator`
3. Clone the `byte-struct` repository into the root directory: `git clone https://github.com/davetcc/byte-struct.git`
4. Build the `byte-struct` project: `cd byte-struct && mvn clean install`
5. Load the C++ project `CMakeLists.txt` file in the root directory into an IDE and built.
6. Open the Java project directory `JavaTradingSim` in an IDE and build.



