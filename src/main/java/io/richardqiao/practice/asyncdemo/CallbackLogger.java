package io.richardqiao.practice.asyncdemo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CallbackLogger implements CallbackHandler<Batch> {
    private static final Logger log = LogManager.getLogger(CallbackLogger.class);

    @Override
    public void handleItem(Batch batch) {
        log.info("LoggingCallbackHandler received batch #{}", batch.getBatchNumber());
    }

    @Override
    public void streamComplete() {
        log.info("LoggingCallbackHandler notified that stream is complete");
    }
}
