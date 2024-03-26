package com.polemon.viki.commons.communication.utils;

import lombok.Getter;

/**
 * Enum for methods available for HTTP.
 */
@Getter
public enum Method {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH");

    private final String text;

    Method(String text) {
        this.text = text;
    }

}
