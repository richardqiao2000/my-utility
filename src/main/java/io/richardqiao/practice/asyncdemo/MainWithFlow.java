package io.richardqiao.practice.asyncdemo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Flow;

public class MainWithFlow {
    private static final Logger log = LogManager.getLogger(MainWithFlow.class);

    public static void main(String[] args) {
        LoggingSubscriber subscriber = new LoggingSubscriber();
        CallbackPublisher<Batch> publisher = new CallbackPublisher<>();
        publisher.subscribe(subscriber);

        log.info("Creating source");
        AsyncSource source = new AsyncSource(publisher.getHandler(), 1000, 10);

        log.info("Starting source");
        source.start();
    }

    public static class LoggingSubscriber implements Flow.Subscriber<Batch> {
        private Flow.Subscription subscription;

        @Override
        public void onNext(Batch batch) {
            log.info("Received batch #{}", batch.getBatchNumber());
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            log.info("Flow subscribed");
        }

        @Override
        public void onError(Throwable throwable) {
            log.error("Flow error", throwable);
        }

        @Override
        public void onComplete() {
            log.info("Flow complete");
        }
    }
}
