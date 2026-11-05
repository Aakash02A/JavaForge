package com.javaforge.producer.thread;

import java.util.concurrent.BlockingQueue;

/**
 * Producer thread: generates items and places them into the shared BlockingQueue.
 * BlockingQueue.put() automatically blocks if the queue is full, ensuring
 * the producer doesn't overwhelm the consumer.
 */
public class Producer implements Runnable {
    private final BlockingQueue<Integer> queue;
    private final int itemCount;
    private final String name;

    public Producer(String name, BlockingQueue<Integer> queue, int itemCount) {
        this.name = name;
        this.queue = queue;
        this.itemCount = itemCount;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= itemCount; i++) {
                System.out.println("[" + name + "] Producing: " + i);
                queue.put(i); // Blocks if queue is full
                Thread.sleep(100); // Simulate production time
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[" + name + "] was interrupted.");
        }
    }
}
