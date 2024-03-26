package com.polemon.viki.commons;

/**
 * Exception thrown when VIKI infra encounters a problem.
 */
public class VikiException extends Exception {

    public VikiException(String message) {
        super(message);
    }

    public VikiException(String message, Throwable cause) {
        super(message, cause);
    }

}
