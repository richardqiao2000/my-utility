package io.richardqiao.practice.asyncdemo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;

public class MainWithFlux {
    private static final Logger log = LogManager.getLogger(MainWithFlux.class);

    public static void main(String[] args) {
        Flux<Batch> flux = Flux.create((emitter) -> {
            CallbackHandler<Batch> handler = new CallbackHandler<Batch>() {
                @Override
                public void handleItem(Batch batch) {
                    emitter.next(batch);
                }

                @Override
                public void streamComplete() {
                    emitter.complete();
                }
            };

            log.info("Creating source");
            AsyncSource source = new AsyncSource(handler, 1000, 10);

            log.info("Starting source");
            source.start();
        });

        // if you watch the log messages, you'll see that the "received batch" messages all show
        // up in the main SourceThread. This SourceThread will block while the events are processed.
        // This is fine and even preferable to using multiple threads, if the event can be processed
        // immediately without blocking. Usually, the synchronous callback is just used to dispatch
        // to another pool (see MainWithFluxParallel)
        flux.subscribe((batch) -> log.info("Received batch #{} from flux", batch.getBatchNumber()));
    }

}
