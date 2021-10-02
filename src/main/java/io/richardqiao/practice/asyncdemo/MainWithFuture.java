package io.richardqiao.practice.asyncdemo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;

public class MainWithFuture {
    private static final Logger log = LogManager.getLogger(MainWithFuture.class);

    public static void main(String[] args) {
        log.info("Creating source");
        CompletableFuture<Void> future = CompletableFuture.runAsync(
                () -> new AsyncSource(new CallbackLogger(), 1000, 5).start()
        );
        future.join();
        log.info("Starting source");
    }
}
