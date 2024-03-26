package com.polemon.viki.commons.communication.exception;

import com.polemon.viki.commons.VikiException;

/**
 * Exception used to inform a generic problem in the producer.
 */
public class VikiCommunicationProducerException extends VikiException {

    public VikiCommunicationProducerException(String message) {
        super(message);
    }

}
