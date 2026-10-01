# Project Idea

Build a **Producer-Consumer** system that demonstrates safe multi-threaded communication using Java Threads and `BlockingQueue`.

# Problem Statement

In concurrent applications, one part of the system produces data (e.g., a message queue, a download buffer) while another part consumes it. Without proper coordination, producers can overwhelm consumers or consumers can read from an empty buffer — both causing crashes or wasted CPU cycles.

The **Producer-Consumer** project solves this using Java's `BlockingQueue`, which automatically blocks the producer when the queue is full and blocks the consumer when it's empty — eliminating the need for manual `wait()`/`notify()` synchronization.

# Objectives

- Create a Producer thread that generates items and places them into a shared queue.
- Create a Consumer thread that takes items from the queue and processes them.
- Use `BlockingQueue` to coordinate access between threads safely.
- Demonstrate with multiple producers feeding one consumer.
- Handle `InterruptedException` gracefully.

# Features

- Producer Thread (with configurable item count)
- Consumer Thread (with configurable item count)
- Shared `LinkedBlockingQueue` with bounded capacity
- Multiple Producer Support
- Graceful Thread Interruption Handling
- Thread join for clean shutdown

# Tech Stack

- Java (Intermediate)
- java.lang.Thread / Runnable
- java.util.concurrent.BlockingQueue
- java.util.concurrent.LinkedBlockingQueue
- Thread.sleep() for simulated work
- Thread.join() for clean teardown

# Suggested Class Structure

Producer implements Runnable
-----------------------------
queue
itemCount
name
run()

Consumer implements Runnable
-----------------------------
queue
itemCount
name
run()

Main
----
main()
scenario1()  // 1 Producer, 1 Consumer
scenario2()  // 2 Producers, 1 Consumer

# Sample Output

--- Scenario 1: Single Producer, Single Consumer ---
[Producer-1] Producing: 1
[Consumer-1] Consumed: 1
[Producer-1] Producing: 2
[Producer-1] Producing: 3
[Consumer-1] Consumed: 2
[Scenario 1 Done] Queue is empty: true

--- Scenario 2: Multiple Producers, Single Consumer ---
[Producer-A] Producing: 1
[Producer-B] Producing: 1
[Consumer-1] Consumed: 1
[Scenario 2 Done] Queue is empty: true

# Learning Outcomes

By completing this project, you will learn:

- The Producer-Consumer concurrency design pattern
- Creating and starting threads using `Runnable`
- How `BlockingQueue.put()` blocks when the queue is full
- How `BlockingQueue.take()` blocks when the queue is empty
- Using `Thread.join()` to wait for thread completion
- Handling `InterruptedException` properly
- The danger of race conditions and how `BlockingQueue` prevents them

# Project Details

Difficulty: ⭐⭐⭐☆☆ (Intermediate)

Estimated Time: 6–10 hours

Skills Gained:
- Java Threading
- BlockingQueue
- Concurrency Patterns
- Thread Safety
