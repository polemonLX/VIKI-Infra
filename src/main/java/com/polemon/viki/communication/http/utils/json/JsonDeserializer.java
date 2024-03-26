package com.polemon.viki.communication.http.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polemon.viki.commons.communication.exception.VikiCommunicationConsumerException;
import com.polemon.viki.communication.http.utils.Deserializer;

import java.io.IOException;

/**
 * This class accepts json as data.
 *
 * @see Deserializer
 */
public class JsonDeserializer implements Deserializer {

    /**
     * Mapper for deserialization.
     */
    private final ObjectMapper mapper;

    public JsonDeserializer() {
        this.mapper = new ObjectMapper();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object deserialize(String json, Class<?> modelType) throws VikiCommunicationConsumerException {
        if (json == null) {
            return null;
        }

        if (json.isEmpty() || json.isBlank()) {
            json = "{}";
        }

        try {
            return mapper.readValue(json, modelType);
        } catch (JsonProcessingException e) {
            throw new VikiCommunicationConsumerException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object deserialize(byte[] bytes, Class<?> modelType) throws VikiCommunicationConsumerException {
        if (bytes == null) {
            return null;
        }

        if (bytes.length == 0) {
            bytes = "{}".getBytes();
        }

        try {
            return mapper.readValue(bytes, modelType);
        } catch (IOException e) {
            throw new VikiCommunicationConsumerException(e.getMessage());
        }
    }

}
