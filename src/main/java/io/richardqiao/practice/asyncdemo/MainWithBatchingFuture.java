package io.richardqiao.practice.asyncdemo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class MainWithBatchingFuture {
    private static final Logger log = LogManager.getLogger(MainWithBatchingFuture.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // futures are single-shot, so we use this to collect all of the batches in the source's stream
        // and then process them all at once
        CallbackBatchingFuture<Batch> future = new CallbackBatchingFuture<Batch>();

        log.info("Creating source");
        AsyncSource source = new AsyncSource(future.getHandler(), 1000, 10);

        log.info("Starting source");
        source.start();
        log.info("Received batches: {}", future.get().stream()
                .map((batch) -> Long.toString(batch.getBatchNumber()))
                .collect(Collectors.joining(", ")));
    }
}
