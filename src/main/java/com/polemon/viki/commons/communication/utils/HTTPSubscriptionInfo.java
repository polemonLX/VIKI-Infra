package com.polemon.viki.commons.communication.utils;

import lombok.Getter;

/**
 * Class that extends SubscriptionInfo. Useful to pass arguments that are needed in a sync communication. Plus, this class
 * has an extra argument, method, only used in this specific module.
 */
@Getter
public class HTTPSubscriptionInfo extends SubscriptionInfo {

    /**
     * Method of the request.
     */
    private final Method method;

    /**
     * Serializer to convert the response object into the request's response.
     */
    private final String serializer;

    /**
     * Deserializer to convert the payload into the model type.
     */
    private final String deserializer;

    public HTTPSubscriptionInfo(String source, Method method, Class<?> modelType, String serializer, String deserializer) {
        super(source, modelType);
        this.method = method;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

}
