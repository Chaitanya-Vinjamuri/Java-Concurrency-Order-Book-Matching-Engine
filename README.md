# Order Book Matching Engine

## Overview

The Order Book Matching Engine is a Java-based concurrency project that simulates a simplified stock exchange trading system.

Multiple trader threads submit BUY and SELL orders concurrently into a shared order queue. A dedicated matching engine processes incoming orders, matches compatible BUY and SELL requests, and triggers asynchronous trade confirmations. The project focuses on practical usage of Java concurrency utilities and demonstrates common multithreading challenges along with their solutions.

The implementation uses only Java standard libraries and follows the constraints specified in the assignment.

---

## System Architecture

```text
Trader Threads
(Runnable + ExecutorService)

        │
        ▼

BlockingQueue<Order>
(Producer-Consumer Pattern)

        │
        ▼

MatchingEngine
(Single Thread)

        │
        ▼

Matched Trade

        │
        ▼

TradeConfirmer
(CompletableFuture)

        │
        ▼

TradeHistoryManager
(synchronized)

        │
        ▼

Final Summary
```

---

## Key Features

- Concurrent order submission by multiple traders
- Single-threaded order matching engine
- Shared BlockingQueue for thread-safe communication
- Asynchronous trade confirmations using CompletableFuture
- Trade history tracking
- Random confirmation failure simulation
- File-based order loading
- Final execution summary and statistics
- Demonstration of concurrency pitfalls and solutions

---

## Project Structure

### Order.java

Represents a BUY or SELL order submitted by a trader.

### Trade.java

Represents a successfully matched trade between buyer and seller.

### TraderTask.java

Runnable task responsible for submitting trader orders into the shared queue.

### NaiveTrader.java

Basic thread implementation used to demonstrate simple thread creation and execution.

### MatchingEngine.java

The core component of the application.

Responsibilities:

- Consume orders from the queue
- Maintain BUY and SELL order books
- Match eligible orders
- Trigger trade confirmations

Concurrency concepts:

- BlockingQueue
- volatile
- ReentrantLock

### TradeConfirmer.java

Handles asynchronous trade confirmations.

Responsibilities:

- Simulate confirmation latency
- Simulate random failures
- Record confirmation results

Concurrency concepts:

- CompletableFuture
- ExecutorService

### TradeHistoryManager.java

Maintains trade history and confirmation statistics.

Responsibilities:

- Record successful trades
- Record failed confirmations
- Provide final metrics

Concurrency concepts:

- synchronized

### OrderFileReader.java

Loads and parses orders from an external text file.

### Pitfalls.java

Demonstrates common concurrency problems and their solutions.

Topics covered:

- Race Conditions
- Synchronization
- volatile visibility
- AtomicInteger
- Deadlocks
- Deadlock prevention
- CompletableFuture misuse
- Proper asynchronous coordination

### Main.java

Application entry point.

Responsibilities:

- Initialize components
- Create thread pools
- Start trader tasks
- Coordinate shutdown
- Generate final summary

Concurrency concepts:

- ExecutorService
- Callable
- Future
- CompletableFuture.allOf()

---

## Order Processing Flow

1. Orders are loaded from orders.txt.
2. Orders are grouped by trader.
3. Each trader is assigned a separate task.
4. Trader tasks submit orders into a shared BlockingQueue.
5. The MatchingEngine consumes orders from the queue.
6. BUY and SELL orders are matched when prices are compatible.
7. Matched trades are sent for asynchronous confirmation.
8. Confirmation results are recorded.
9. Final statistics are displayed after all processing completes.

---

## Sample Input

### orders.txt

```text
TRADER_A BUY 102 10
TRADER_B SELL 100 10
TRADER_C BUY 99 5
TRADER_D SELL 105 5
TRADER_E BUY 106 5
```

---

## Sample Output

```text
MATCH FOUND -> TRADE -> Buyer: TRADER_E, Seller: TRADER_D, Price: 105.0

MATCH FOUND -> TRADE -> Buyer: TRADER_A, Seller: TRADER_B, Price: 100.0

FAILED -> TRADE -> Buyer: TRADER_E, Seller: TRADER_D, Price: 105.0

CONFIRMED -> TRADE -> Buyer: TRADER_A, Seller: TRADER_B, Price: 100.0

===== FINAL SUMMARY =====

Orders Submitted : 5

Trades Confirmed : 1

Confirmations Success : 1

Confirmations Failed : 1

Unmatched Buy Orders : 1

Unmatched Sell Orders : 0

APPLICATION COMPLETED
```

The sample output demonstrates multiple matched trades, confirmation failure handling, successful confirmations, and final execution statistics as required by the assignment.

---

## Concurrency Concepts Demonstrated

This project demonstrates practical usage of Java concurrency APIs including:

- Thread creation and lifecycle management
- Runnable and Callable tasks
- ExecutorService thread pools
- Future-based task tracking
- Producer-Consumer pattern using BlockingQueue
- Visibility guarantees using volatile
- Thread synchronization using synchronized
- Explicit locking using ReentrantLock
- Asynchronous programming using CompletableFuture
- Atomic operations using AtomicInteger
- Deadlock scenarios and prevention techniques

---

## Design Decisions

### Why a FixedThreadPool?

A fixed-size thread pool was selected because the number of trader tasks is known at runtime. This provides predictable resource usage and avoids unnecessary thread creation.

### Why a Single Matching Engine?

The assignment explicitly requires a single-threaded matching engine. This also simplifies order-book consistency because matching logic executes sequentially.

### Why CompletableFuture?

Trade confirmations are independent of order matching. Executing confirmations asynchronously prevents the matching engine from being blocked by confirmation latency.

---

## Constraints

The project was implemented using Java 11 and only standard Java libraries.

The matching engine operates as a single thread as required by the assignment. Trade confirmation latency is simulated using Thread.sleep(), while confirmation failures are generated using Random.

No external frameworks or libraries were used, and deprecated thread control methods such as Thread.stop(), Thread.suspend(), and Thread.resume() are not used anywhere in the implementation.

---

## Running the Application

### IntelliJ IDEA

Open the project and run:

```text
Main.java
```

### Command Line

Compile:

```bash
javac -d out src/**/*.java
```

Run:

```bash
java Main
```

---

## Learning Outcomes

Through this project, I gained hands-on experience with:

- Concurrent task execution
- Thread coordination and communication
- Asynchronous workflows
- Synchronization strategies
- Deadlock prevention
- ExecutorService management
- Future and CompletableFuture usage
- Designing thread-safe applications

---

## Author

Chaitanya Vinjamuri 
SDE Trainee
