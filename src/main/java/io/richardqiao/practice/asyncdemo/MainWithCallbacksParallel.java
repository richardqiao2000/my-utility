package io.richardqiao.practice.asyncdemo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class MainWithCallbacksParallel {
    private static final Logger log = LogManager.getLogger(MainWithCallbacksParallel.class);

    public static void main(String[] args) throws InterruptedException {
        ForkJoinPool executor = new ForkJoinPool(5);

        // here we turn this synchronous callback interface into a parallel processing pipeline.
        // if you watch the log messages, you'll see that the "received batch" messages all show
        // up in the parallel ForkJoinPool threads. The main SourceThread is only running the calls
        // to the flux emitter and then immediately returning to its "fetch" work.
        CallbackHandler<Batch> executorSubmissionHandler = new CallbackHandler<>() {
            @Override
            public void handleItem(Batch batch) {
                executor.submit(() -> {
                    log.info("Received batch #{}. Processing.", batch.getBatchNumber());
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    log.info("Finished processing batch #{}", batch.getBatchNumber());
                });
            }

            @Override
            public void streamComplete() {
                executor.submit(() -> log.info("Stream ended"));
            }
        };

        log.info("Creating source");
        AsyncSource source = new AsyncSource(executorSubmissionHandler, 100, 10);

        log.info("Starting source");
        source.start();

        log.info("awaitQuiescence=" + executor.awaitQuiescence(30, TimeUnit.SECONDS));
    }
}
