package com.polemon.viki.api;

import com.polemon.viki.commons.VikiException;
import com.polemon.viki.commons.communication.consumer.IVikiCommunicationConsumer;
import com.polemon.viki.commons.communication.exception.VikiCommunicationConsumerException;
import com.polemon.viki.commons.communication.utils.*;
import com.polemon.viki.communication.http.consumer.HttpConsumer;
import com.polemon.viki.communication.http.producer.HttpProducer;

/**
 * Class used to register consumers for this application.
 */
public class VikiConsumer {

    /**
     * Sync consumer used for the registrations.
     */
    private static IVikiCommunicationConsumer syncConsumer;

    /**
     * Async consumer used for the registrations.
     */
    private static IVikiCommunicationConsumer asyncConsumer;

    /**
     * Register a sync consumer.
     *
     * @param source    where to consume
     * @param method    expected method for this source
     * @param modelType the request must be converted to this modelType
     * @param handler   the request must be sent to this handler
     * @throws VikiCommunicationConsumerException if registration encounters a problem
     */
    public static void registerSyncConsumer(String source, Method method, Class<?> modelType, IVikiCommunicationConsumerHandler handler) throws VikiException {
        if (VikiApplication.isVikiStarted()) {
            throw new VikiCommunicationConsumerException("VIKI is already started! Cannot subscribe while VIKI is running.");
        }
        if (syncConsumer == null) {
            syncConsumer = HttpConsumer.getINSTANCE();
        }

        SubscriptionInfo subscriptionInfo = new HTTPSubscriptionInfo(source, method, modelType, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
        syncConsumer.subscribeEvent(subscriptionInfo, handler);
    }

    /**
     * Register an async consumer.
     *
     * @param source    where to consume
     * @param modelType the request must be converted to this modelType
     * @param handler   the request must be sent to this handler
     * @throws VikiCommunicationConsumerException if registration encounters a problem
     */
    public static void registerAsyncConsumer(String source, Class<?> modelType, IVikiCommunicationConsumerHandler handler) throws VikiCommunicationConsumerException {
        if (VikiApplication.isVikiStarted()) {
            throw new VikiCommunicationConsumerException("VIKI is already started! Cannot subscribe while VIKI is running.");
        }
        if (asyncConsumer == null) {
            // TODO: 21/03/2024 IMPLEMENT
        }
    }

    protected static void start() throws VikiException {
        syncConsumer.start();
    }

}
