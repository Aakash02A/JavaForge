package com.javaforge.producer;

import com.javaforge.producer.thread.Consumer;
import com.javaforge.producer.thread.Producer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Java Forge Producer-Consumer (Threads) ===\n");

        // Scenario 1: 1 Producer + 1 Consumer (Shared queue capacity of 3)
        System.out.println("--- Scenario 1: Single Producer, Single Consumer ---");
        scenario1();

        // Scenario 2: 2 Producers + 1 Consumer
        System.out.println("\n--- Scenario 2: Multiple Producers, Single Consumer ---");
        scenario2();

        System.out.println("\nAll scenarios completed.");
    }

    private static void scenario1() throws InterruptedException {
        int queueCapacity = 3;
        int itemCount = 5;
        BlockingQueue<Integer> sharedQueue = new LinkedBlockingQueue<>(queueCapacity);

        Thread producerThread = new Thread(new Producer("Producer-1", sharedQueue, itemCount));
        Thread consumerThread = new Thread(new Consumer("Consumer-1", sharedQueue, itemCount));

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        consumerThread.join();
        System.out.println("[Scenario 1 Done] Queue is empty: " + sharedQueue.isEmpty());
    }

    private static void scenario2() throws InterruptedException {
        int queueCapacity = 5;
        int itemsPerProducer = 3;
        int totalItems = itemsPerProducer * 2; // 2 producers
        BlockingQueue<Integer> sharedQueue = new LinkedBlockingQueue<>(queueCapacity);

        Thread producer1 = new Thread(new Producer("Producer-A", sharedQueue, itemsPerProducer));
        Thread producer2 = new Thread(new Producer("Producer-B", sharedQueue, itemsPerProducer));
        Thread consumer  = new Thread(new Consumer("Consumer-1", sharedQueue, totalItems));

        producer1.start();
        producer2.start();
        consumer.start();

        producer1.join();
        producer2.join();
        consumer.join();
        System.out.println("[Scenario 2 Done] Queue is empty: " + sharedQueue.isEmpty());
    }
}
