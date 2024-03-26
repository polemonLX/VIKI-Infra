package com.polemon.viki.commons.communication.models;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Headers of the Event class. It is used to store the meta info about the Event.
 * Has an id and a destiny already in the key/value pair, since it is a must in each Event. If the keyValue is called,
 * it will return a Map with the id and destiny in it.
 * The rest of the key/value pairs are stored in a Map<String,String>.
 */
public class Headers {

    /**
     * ID to be used as a correlation between events from the same request.
     */
    @Getter
    private final String sagaId;

    /**
     * Address of the event
     */
    @Getter
    private final String address;

    /**
     * All the internal metadata stored here.
     */
    private final Map<String, String> internalMetadata;

    /**
     * All the external metadata stored here.
     */
    private final Map<String, String> externalMetadata;

    public Headers(String sagaId, String address) {
        this.sagaId = sagaId;
        this.address = address;
        this.internalMetadata = new HashMap<>();
        this.externalMetadata = new HashMap<>();
    }

    /**
     * Add a key value pair into the internal headers.
     *
     * @param key   to be added
     * @param value to be added
     */
    public void addInternalKeyValue(String key, String value) {
        internalMetadata.put(key, value);
    }

    /**
     * Add a key value pair into the external headers.
     *
     * @param key   to be added
     * @param value to be added
     */
    public void addExternalKeyValue(String key, String value) {
        externalMetadata.put(key, value);
    }

    /**
     * Get the internal value of a given key.
     *
     * @param key to be searched
     * @return the value or null if key doesn't exist
     */
    public String getInternalValue(String key) {
        return internalMetadata.get(key);
    }

    /**
     * Get the external value of a given key.
     *
     * @param key to be searched
     * @return the value or null if key doesn't exist
     */
    public String getExternalValue(String key) {
        return externalMetadata.get(key);
    }

    /**
     * Get all internal headers including id and address.
     *
     * @return map with all headers, id and address
     */
    public Map<String, String> getInternalHeaders() {
        return new HashMap<>(internalMetadata) {{
            put("id", sagaId);
            put("address", address);
        }};
    }

    /**
     * Get all external headers including id and address.
     *
     * @return map with all headers, id and address
     */
    public Map<String, String> getExternalHeaders() {
        return new HashMap<>(externalMetadata);
    }

    @Override
    public String toString() {
        return "Headers{" +
                "id='" + sagaId + '\'' +
                ", address='" + address + '\'' +
                ", internalMetadata=" + internalMetadata +
                ", externalMetadata=" + externalMetadata +
                '}';
    }

}
