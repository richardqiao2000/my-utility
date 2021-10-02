package io.richardqiao.practice.asyncdemo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class MainWithFluxParallel {
    private static final Logger log = LogManager.getLogger(MainWithFluxParallel.class);

    public static void main(String[] args) throws InterruptedException {
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
            AsyncSource source = new AsyncSource(handler, 100, 10);

            log.info("Starting source");
            source.start();
        });

        // here we turn this synchronous callback interface into a parallel processing pipeline.
        // if you watch the log messages, you'll see that the "received batch" messages all show
        // up in the parallel reactor threads. The main SourceThread is only running the calls
        // to the flux emitter and then immediately returning to its "fetch" work.
        flux.parallel(5)
                .runOn(Schedulers.parallel())
                .doOnNext((batch) -> {
                    log.info("Received batch #{} from flux. Processing.", batch.getBatchNumber());
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    log.info("Finished processing batch #{}", batch.getBatchNumber());
                })
                .sequential()
                .blockLast()
        ;
    }
}
