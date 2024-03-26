package com.polemon.viki.commons;

/**
 * Default interface for starting and stopping VIKI modules.
 * This interface should be extended by other specific interfaces of the specific modules.
 */
public interface IVikiDefaultAPI {

    /**
     * Method to start a module.
     * Imperative use after any configuration needed.
     *
     * @throws VikiException if the module has any problem starting
     */
    void start() throws VikiException;

    /**
     * Method to stop a module.
     *
     * @throws VikiException if the module has any problem stopping
     */
    void stop() throws VikiException;

}
