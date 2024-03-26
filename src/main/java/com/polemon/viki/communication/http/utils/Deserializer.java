package com.polemon.viki.communication.http.utils;

import com.polemon.viki.commons.communication.exception.VikiCommunicationConsumerException;

/**
 * Interface for deserializer implementations.
 */
public interface Deserializer {

    /**
     * Deserialize a String value into a model type.
     *
     * @param value     as a String to be deserialized
     * @param modelType to deserialize value into
     * @return a new model type
     * @throws VikiCommunicationConsumerException when occurs an error in deserialization process
     */
    Object deserialize(String value, Class<?> modelType) throws VikiCommunicationConsumerException;

    /**
     * Deserialize an Array of bytes into a model type.
     *
     * @param bytes     as an Array of bytes to be deserialized
     * @param modelType to deserialize value into
     * @return a new model type
     * @throws VikiCommunicationConsumerException when occurs an error in deserialization process
     */
    Object deserialize(byte[] bytes, Class<?> modelType) throws VikiCommunicationConsumerException;

}
