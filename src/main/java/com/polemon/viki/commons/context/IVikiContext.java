package com.polemon.viki.commons.context;

import com.polemon.viki.commons.context.models.VikiSagaContext;

/**
 * VIKI has the need to save saga's information when it starts processing in the microservice
 * until it return a result for the system.
 * By definition, from the moment a Saga starts processing in a microservice, the programmer cannot
 * change thread, or at least, it cannot use VIKI tools in another thread other than the one
 * provided by the VIKI.
 * The management of the information is made by the implementation of this interface.
 *
 * @see VikiSagaContext
 */
public interface IVikiContext {

    /**
     * Start the context.
     */
    void start();

    /**
     * Stop the context.
     */
    void stop();

    /**
     * Saves all the information of a Saga at once.
     *
     * @param vikiSagaContext containing all the information
     */
    void saveSagaContext(VikiSagaContext vikiSagaContext);

    /**
     * Getter for the whole saga context.
     *
     * @return ExagonSagaContext
     */
    VikiSagaContext getSagaContext();

    /**
     * Save the sagaId of this Saga.
     *
     * @param sagaId to be saved
     */
    void saveSagaId(String sagaId);

    /**
     * Getter for sagaId.
     *
     * @return String with the sagaId
     */
    String getSagaId();

}
