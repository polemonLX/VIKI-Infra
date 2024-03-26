package com.polemon.viki.commons.communication.consumer;

import com.polemon.viki.commons.IVikiDefaultAPI;
import com.polemon.viki.commons.communication.exception.VikiCommunicationConsumerException;
import com.polemon.viki.commons.communication.utils.IVikiCommunicationConsumerHandler;
import com.polemon.viki.commons.communication.utils.SubscriptionInfo;

import java.util.Map;

/**
 * Interface consumer to be implemented by a communication module.
 * It's only purpose is to consume/get an Event from the storage.
 */
public interface IVikiCommunicationConsumer extends IVikiDefaultAPI {

    /**
     * This method allows the user to subscribe a specific Event.
     *
     * @param subscriptionInfo                  must be extended by a specific class and filled with info about the Event
     * @param iVikiCommunicationConsumerHandler handler to process the incoming event
     * @throws VikiCommunicationConsumerException if something in SubscriptionInfo is null
     */
    void subscribeEvent(SubscriptionInfo subscriptionInfo, IVikiCommunicationConsumerHandler iVikiCommunicationConsumerHandler) throws VikiCommunicationConsumerException;

    /**
     * This method allows the user to subscribe more than one Event.
     *
     * @param subscriptionInfos map of SubscriptionInfo and ConsumerHandler
     * @throws VikiCommunicationConsumerException if something in SubscriptionInfo is null
     */
    void subscribeEvents(Map<SubscriptionInfo, IVikiCommunicationConsumerHandler> subscriptionInfos) throws VikiCommunicationConsumerException;

}
