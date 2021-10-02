package io.richardqiao.practice.asyncdemo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A functional interface for async callback handlers
 */
public interface CallbackHandler<T> {
    static final Logger log = LogManager.getLogger(CallbackHandler.class);

    public void handleItem(T t);

    // provide a default implementation of this so we can still use the interface as a functional interface.
    // handlers that care about stream lifecycle will still need to override this.
    default void streamComplete() {
        log.info("Stream ended");
    }
}
