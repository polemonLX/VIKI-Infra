package com.polemon.viki.commons.communication.utils;

/**
 * Class that extends SubscriptionInfo. Useful to pass arguments that are needed in an async communication.
 */
public class KafkaSubscriptionInfo extends SubscriptionInfo {

    public KafkaSubscriptionInfo(String source, Class<?> modelType) {
        super(source, modelType);
    }

}
