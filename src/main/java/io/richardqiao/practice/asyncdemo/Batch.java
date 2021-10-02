package io.richardqiao.practice.asyncdemo;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Dummy class that represents some unit of data we can do work on
 */
public class Batch {
    private static final AtomicLong batchCounter = new AtomicLong();

    private final long batchNumber;

    public Batch() {
        batchNumber = batchCounter.incrementAndGet();
    }

    public long getBatchNumber() {
        return batchNumber;
    }
}
