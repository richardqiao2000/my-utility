package io.richardqiao.practice.asyncdemo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is a {@link CompletableFuture} which will batch all of the items from a callback and then
 * complete with the full list.
 */
public class CallbackBatchingFuture<T> extends CompletableFuture<List<T>> {
    private static final Logger log = LogManager.getLogger(CallbackBatchingFuture.class);

    private final CallbackHandler<T> handler = new Handler();
    private final List<T> list = new ArrayList<>();
    private final AtomicBoolean done = new AtomicBoolean();

    public CallbackHandler<T> getHandler() {
        return handler;
    }

    private class Handler implements CallbackHandler<T> {
        @Override
        public void handleItem(T t) {
            if (done.get()) {
                throw new IllegalStateException("completed future received another callback");
            }
            list.add(t);
            log.info("BatchingCallbackFuture received item, now buffering {} items", list.size());
        }

        @Override
        public void streamComplete() {
            done.set(true);
            complete(list);
        }
    }
}
