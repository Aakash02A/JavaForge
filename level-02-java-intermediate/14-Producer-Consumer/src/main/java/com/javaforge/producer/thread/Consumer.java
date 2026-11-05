package com.javaforge.producer.thread;

import java.util.concurrent.BlockingQueue;

/**
 * Consumer thread: takes items from the shared BlockingQueue and processes them.
 * BlockingQueue.take() automatically blocks if the queue is empty,
 * so the consumer waits patiently instead of spinning in a busy loop.
 */
public class Consumer implements Runnable {
    private final BlockingQueue<Integer> queue;
    private final int itemCount;
    private final String name;

    public Consumer(String name, BlockingQueue<Integer> queue, int itemCount) {
        this.name = name;
        this.queue = queue;
        this.itemCount = itemCount;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < itemCount; i++) {
                int item = queue.take(); // Blocks if queue is empty
                System.out.println("[" + name + "] Consumed: " + item);
                Thread.sleep(200); // Simulate processing time (slower than producer)
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[" + name + "] was interrupted.");
        }
    }
}
