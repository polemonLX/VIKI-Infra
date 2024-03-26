package com.polemon.viki.commons.communication.utils;

import lombok.Getter;

/**
 * This abstract class must be extended by specific classes of a technology.
 * Each technology has different parameters that it must need to work.
 */
@Getter
public abstract class SubscriptionInfo {

    /**
     * Source of the expected request.
     */
    private final String source;

    /**
     * Model type to convert the request's body.
     */
    private final Class<?> modelType;

    public SubscriptionInfo(String source, Class<?> modelType) {
        this.source = source;
        this.modelType = modelType;
    }

}
