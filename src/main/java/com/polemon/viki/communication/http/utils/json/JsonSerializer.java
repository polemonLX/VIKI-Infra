package com.polemon.viki.communication.http.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.polemon.viki.commons.communication.exception.VikiCommunicationProducerException;
import com.polemon.viki.communication.http.utils.Serializer;

/**
 * This class converts objects into json format.
 *
 * @see Serializer
 */
public class JsonSerializer implements Serializer {

    /**
     * Mapper for serialization.
     */
    private final ObjectMapper mapper;

    public JsonSerializer() {
        this.mapper = new ObjectMapper();
        this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] serialize(Object payload) throws VikiCommunicationProducerException {
        if (payload == null) {
            return new byte[]{};
        }

        try {
            return mapper.writeValueAsBytes(payload);
        } catch (JsonProcessingException e) {
            throw new VikiCommunicationProducerException(e.getMessage());
        }
    }

}
