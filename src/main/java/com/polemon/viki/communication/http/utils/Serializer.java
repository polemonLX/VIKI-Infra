package com.polemon.viki.communication.http.utils;

import com.polemon.viki.commons.communication.exception.VikiCommunicationProducerException;

/**
 * Interface for serializer implementations.
 */
public interface Serializer {

    /**
     * Serialize an object into an Array of bytes.
     *
     * @param payload the object to be serialized
     * @return Array of bytes
     * @throws VikiCommunicationProducerException when occurs an error in serialization process
     */
    byte[] serialize(Object payload) throws VikiCommunicationProducerException;

}
