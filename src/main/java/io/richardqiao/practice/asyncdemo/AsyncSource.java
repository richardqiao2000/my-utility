package io.richardqiao.practice.asyncdemo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicReference;

/**
 * This is a class which asynchronously creates {@link Batch}es. It operates on a background thread while it
 * is running. The {@link #start} and {@link #stop} methods can be used to start and stop this thread.
 */
public class AsyncSource {
    private static final Logger log = LogManager.getLogger(AsyncSource.class);

    private final AtomicReference<BackgroundThread> thread = new AtomicReference<>();
    private final CallbackHandler<Batch> handler;
    private final int delay;
    private final int count;

    public AsyncSource(CallbackHandler<Batch> handler, int delay, int count) {
        this.handler = handler;
        this.delay = delay;
        this.count = count;
    }

    /**
     * This method will create and start a background thread to source batches, which will be sent to the
     * handler provided at construction time. If a thread is already running, it will log and do nothing.
     */
    public void start() {
        // methods should never just be flagged synchronized. this causes them to acquire their object's
        // monitor, which is exposed to other code. synchronization should be performed on final, private
        // objects so that external code cannot break synchronization-based thread safety or cause
        // deadlocks. here we synchronize on the AtomicReference object we use to track our thread.
        synchronized (thread) {
            if (thread.get() != null) {
                if (thread.get().isAlive()) {
                    log.warn("Background thread is already running");
                    return;
                } else {
                    log.info("Background thread is still associated with source but has stopped. Discarding it.");
                    thread.set(null);
                }
            }
            log.info("Creating new source background thread");
            BackgroundThread t = new BackgroundThread();
            t.setDaemon(false);
            thread.set(t);
            log.info("Starting new source background thread");
            t.start();
            log.info("Started new source background thread");
        }
    }

    /**
     * This method will interrupt the background thread and wait for it to terminate. If no background thread
     * is running, it will return immediately. If the thread that calls this method is interrupted, it will
     * re-throw and the state of the background thread will be unknown. If this method returns normally, the
     * caller can be sure that there is no background thread running.
     */
    public void stop() throws InterruptedException {
        synchronized (thread) {
            if (thread.get() != null) {
                log.info("Sending interrupt to source background thread");
                thread.get().interrupt();
                log.info("Waiting for source background thread to stop...");
                try {
                    thread.get().join();
                } catch (InterruptedException e) {
                    log.info("Thread waiting for source background thread to terminate was interrupted. Stopping.");
                    throw e;
                }
                log.info("Source background thread stopped.");
            }
        }
    }

    private class BackgroundThread extends Thread {
        public BackgroundThread() {
            super("SourceThread");
        }

        @Override
        public void run() {
            for (int i = 0; i < count; i++) {
                try {
                    Batch batch = getBatch();
                    log.info("Source sending batch {} to handler", batch.getBatchNumber());
                    handler.handleItem(batch);
                } catch (InterruptedException e) {
                    log.info("Source thread caught InterruptedException. Terminating.");
                    break;
                }
            }
            log.info("Source sent {} batches. Notifying handler that we're done.", count);
            handler.streamComplete();
            log.info("Source background thread done");
        }

        private Batch getBatch() throws InterruptedException {
            // simulate how a blocking operation would throw an InterruptedException if the thread is interrupted.
            // this is the same check blocking i/o methods use internally.
            if (isInterrupted()) {
                throw new InterruptedException("Source background thread noticed interrupted thread before blocking to get new batch");
            }
            Thread.sleep(delay);
            return new Batch();
        }
    }
}
