package com.polemon.viki.api;

import com.polemon.viki.commons.VikiException;
import com.polemon.viki.commons.communication.exception.VikiCommunicationProducerException;
import com.polemon.viki.commons.communication.models.Event;
import com.polemon.viki.commons.communication.producer.IVikiCommunicationProducer;
import com.polemon.viki.commons.communication.utils.MediaType;
import com.polemon.viki.commons.communication.utils.Method;
import com.polemon.viki.commons.context.IVikiContext;
import com.polemon.viki.communication.http.producer.HttpProducer;

import java.util.Map;

/**
 * Class used to produce payloads.
 */
public class VikiProducer {

    /**
     * Synchronous producer.
     */
    private static IVikiCommunicationProducer syncProducer;

    /**
     * Asynchronous producer.
     */
    private static IVikiCommunicationProducer asyncProducer;

    /**
     * Context of the application to get the internal headers.
     */
    private static IVikiContext context;

    /**
     * Produce a sync event.
     *
     * @param address   source to be sent
     * @param method    to be used while sending
     * @param payload   to be sent
     * @param headers   of the event
     * @param modelType class used for the response
     * @return An instance of the modelType with the response
     * @throws VikiCommunicationProducerException if the server is unreachable
     */
    @SuppressWarnings("unchecked")
    public static <T> T produceSync(String address, Method method, Object payload, Map<String, String> headers, Class<T> modelType) throws VikiCommunicationProducerException {
        Event event = new Event(context.getSagaId(), address, payload);
        headers.forEach(event::addExternalHeader);
        event.addInternalHeader("method", method.getText());
        event.addInternalHeader("serializer", MediaType.APPLICATION_JSON);
        event.addInternalHeader("deserializer", MediaType.APPLICATION_JSON);
        Event responseEvent = syncProducer.produce(event, modelType);
        return (T) responseEvent.getPayload();
    }
    /**
     * Produce an async event.
     *
     * @param address   source to be sent
     * @param payload   to be sent
     * @param headers   of the event
     * @throws VikiCommunicationProducerException if there is a problem with the sending event
     */
    public static void produceAsync(String address, Object payload, Map<String, String> headers) throws VikiCommunicationProducerException {
        Event event = new Event(context.getSagaId(), address, payload);
        headers.forEach(event::addExternalHeader);
        asyncProducer.produce(event);
    }

    public static void setContext(IVikiContext context) {
        VikiProducer.context = context;
    }

    protected static void prepareProducers() throws VikiException {
        syncProducer = HttpProducer.getInstance();
    }

}
