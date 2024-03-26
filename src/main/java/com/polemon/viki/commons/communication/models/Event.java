package com.polemon.viki.commons.communication.models;

import lombok.Getter;

import java.util.Map;

/**
 * Event is a pojo to be used in communication. All technologies should use this class
 * to store/send events.
 * This class contains a header (metadata of the Event) and a body (payload of the event).
 * The kernel should never use the Event. Only the payload should be used.
 */
public class Event {

    /**
     * Headers of the Event. Contains correlationId, address and metadata
     */
    private final Headers headers;

    /**
     * Payload of the event
     */
    @Getter
    private final Object payload;

    /**
     * Constructor with all needed information to build an Event
     *
     * @param sagaId      to be used as a correlation between events from the same Saga
     * @param address of the event
     * @param payload of the event
     */
    public Event(String sagaId, String address, Object payload) {
        this.headers = new Headers(sagaId, address);
        this.payload = payload;
    }

    /**
     * Add new metadata for the internal headers.
     *
     * @param key   The key of the header
     * @param value The value of the header
     */
    public void addInternalHeader(String key, String value) {
        headers.addInternalKeyValue(key, value);
    }

    /**
     * Add new metadata for the external headers.
     *
     * @param key   The key of the header
     * @param value The value of the header
     */
    public void addExternalHeader(String key, String value) {
        headers.addExternalKeyValue(key, value);
    }

    /**
     * Get the internal value corresponding to a given key
     *
     * @param key that maps the value
     * @return the String of the value
     */
    public String getInternalValue(String key) {
        return headers.getInternalValue(key);
    }

    /**
     * Get the external value corresponding to a given key
     *
     * @param key that maps the value
     * @return the String of the value
     */
    public String getExternalValue(String key) {
        return headers.getExternalValue(key);
    }

    /**
     * Get a collection of all internal metadata present in the headers
     *
     * @return a new Map with all the internal data
     */
    public Map<String, String> getInternalHeaders() {
        return headers.getInternalHeaders();
    }

    /**
     * Get a collection of all external metadata present in the headers
     *
     * @return a new Map with all the internal data
     */
    public Map<String, String> getExternalHeaders() {
        return headers.getExternalHeaders();
    }

    /**
     * Get sagaId of the event.
     *
     * @return String with sagaId
     */
    public String getSagaId() {
        return headers.getSagaId();
    }

    /**
     * Get the address of this event.
     *
     * @return String with the address
     */
    public String getAddress() {
        return headers.getAddress();
    }

    @Override
    public String toString() {
        return "Event{" +
                "headers=" + headers +
                ", payload=" + payload +
                '}';
    }

}
