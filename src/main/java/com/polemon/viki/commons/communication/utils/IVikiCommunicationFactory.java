package com.polemon.viki.commons.communication.utils;

import com.polemon.viki.commons.communication.consumer.IVikiCommunicationConsumer;
import com.polemon.viki.commons.communication.producer.IVikiCommunicationProducer;

/**
 * Factory of async / sync consumers and producers.
 * Since it is possible to have different consumers and producers, the id passed in each
 * method will map to a given consumer/producer.
 */
public interface IVikiCommunicationFactory {

    /**
     * Map an id to a specific async consumer.
     *
     * @param id to identify the wanted consumer
     * @return IVikiCommunicationConsumer
     */
    IVikiCommunicationConsumer getAsyncConsumer(String id);

    /**
     * Map an id to a specific async producer.
     *
     * @param id to identify the wanted producer
     * @return IVikiCommunicationProducer
     */
    IVikiCommunicationProducer getAsyncProducer(String id);

    /**
     * Map an id to a specific sync consumer.
     *
     * @param id to identify the wanted consumer
     * @return IVikiCommunicationConsumer
     */
    IVikiCommunicationConsumer getSyncConsumer(String id);

    /**
     * Map an id to a specific sync producer.
     *
     * @param id to identify the wanted producer
     * @return IVikiCommunicationProducer
     */
    IVikiCommunicationProducer getSyncProducer(String id);

}
