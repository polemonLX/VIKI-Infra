package com.polemon.viki.commons.communication.exception;

import com.polemon.viki.commons.VikiException;

/**
 * Exception used to inform a generic problem in the consumer.
 */
public class VikiCommunicationConsumerException extends VikiException {

    public VikiCommunicationConsumerException(String message) {
        super(message);
    }

}
