package io.richardqiao.practice.asyncdemo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainWithCallbacks {
    private static final Logger log = LogManager.getLogger(MainWithCallbacks.class);

    // if you watch the log messages, you'll see that the "received batch" messages all show
    // up in the main SourceThread. This SourceThread will block while the events are processed.
    // This is fine and even preferable to using multiple threads, if the event can be processed
    // immediately without blocking. Usually, the synchronous callback is just used to dispatch
    // to another pool (see MainWithCallbacksParallel)
    public static void main(String[] args) {
        CallbackHandler<Batch> handler = new CallbackLogger();

        log.info("Creating source");
        AsyncSource source = new AsyncSource(handler, 1000, 10);

        log.info("Starting source");
        source.start();
    }
}
