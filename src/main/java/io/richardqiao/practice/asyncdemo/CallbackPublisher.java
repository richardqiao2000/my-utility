package io.richardqiao.practice.asyncdemo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Flow;

/**
 * Generic class to wrap a {@link CallbackHandler} up as a JDK9 reactive {@link Flow.Publisher}
 * for any type T
 */
public class CallbackPublisher<T> implements Flow.Publisher<T> {
    private final List<Flow.Subscriber<? super T>> subscribers = new CopyOnWriteArrayList<>();
    private final Handler handler = new Handler();

    @Override
    public void subscribe(Flow.Subscriber<? super T> subscriber) {
        subscribers.add(subscriber);
    }

    public CallbackHandler<T> getHandler() {
        return handler;
    }

    private class Handler implements CallbackHandler<T> {
        @Override
        public void handleItem(T t) {
            for (Flow.Subscriber<? super T> subscriber : subscribers) {
                subscriber.onNext(t);
            }
        }

        @Override
        public void streamComplete() {
            for (Flow.Subscriber<? super T> subscriber : subscribers) {
                subscriber.onComplete();
            }
        }
    }
}
