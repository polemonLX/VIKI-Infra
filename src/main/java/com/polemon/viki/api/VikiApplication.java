package com.polemon.viki.api;

import com.polemon.viki.commons.VikiException;
import com.polemon.viki.commons.context.IVikiContext;
import com.polemon.viki.context.ContextImpl;

/**
 * Class used to start and stop the whole infra.
 */
public class VikiApplication {

    /**
     * Flag for the state of VIKI.
     */
    private static boolean vikiStarted;

    /**
     * Context that will be used in this application instance.
     */
    private static IVikiContext context;

    /**
     * Start VIKI infra.
     */
    public static void start() throws VikiException {
        context = ContextImpl.getINSTANCE();
        context.start();

        VikiConsumer.start();
        VikiProducer.prepareProducers();
        VikiProducer.setContext(context);

        vikiStarted = true;
    }

    /**
     * Stop VIKI infra.
     */
    public static void stop() {
        context.stop();

        vikiStarted = false;
    }

    protected static boolean isVikiStarted() {
        return vikiStarted;
    }

}
