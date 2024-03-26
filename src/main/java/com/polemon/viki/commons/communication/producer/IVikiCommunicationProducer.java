package com.polemon.viki.commons.communication.producer;

import com.polemon.viki.commons.communication.exception.VikiCommunicationProducerException;
import com.polemon.viki.commons.communication.models.Event;

/**
 * Interface producer to be implemented by a communication module.
 * It's only purpose is to produce/send an Event to the storage.
 */
public interface IVikiCommunicationProducer {

    /**
     * This method will produce an async Event.
     *
     * @param event to be produced
     * @throws VikiCommunicationProducerException if ID, topic or payload are null
     */
    void produce(Event event) throws VikiCommunicationProducerException;

    /**
     * This method will send a sync Event.
     *
     * @param bodyType Class of the response body
     * @return Event with the HTTP headers (including a "httpCode" header with the HTTP status code) and the body of the HTTP response
     * @throws VikiCommunicationProducerException if any of the parameters sent has an error or the server is unreachable
     */
    Event produce(Event event, Class<?> bodyType) throws VikiCommunicationProducerException;

}
