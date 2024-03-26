package com.polemon.viki.commons;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * All properties of the VIKI project are stored in this class.
 */
public class VikiProperties {

    private static VikiProperties INSTANCE;

    private long sagaTimeout;

    private int serverPort;

    private long producerTimeout;

    private VikiProperties() {
        Properties properties = new Properties();
        try (InputStream is = VikiProperties.class.getClassLoader().getResourceAsStream("application.yml")) {
            properties.load(is);

            sagaTimeout = convertToLong(properties, "viki.saga_timeout");
            serverPort = convertToInteger(properties, "http.server_port");
            producerTimeout = convertToLong(properties, "http.producer_timeout");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static VikiProperties getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new VikiProperties();
        }
        return INSTANCE;
    }

    public int getServerPort() throws VikiException {
        if (serverPort == 0) {
            throw new VikiException("Server port is empty");
        }
        return serverPort;
    }

    public long getProducerTimeout() throws VikiException {
        if (producerTimeout == 0) {
            throw new VikiException("Producer timeout is empty");
        }
        return producerTimeout;
    }

    public long getSagaTimeout() throws VikiException {
        if (sagaTimeout == 0) {
            throw new VikiException("Saga timeout is empty");
        }
        return sagaTimeout;
    }

    private Long convertToLong(Properties properties, String property) {
        if (properties.containsKey(property)) {
            return Long.parseLong(properties.getProperty(property));
        }
        return 0L;
    }

    private Integer convertToInteger(Properties properties, String property) {
        if (properties.containsKey(property)) {
            return Integer.parseInt(properties.getProperty(property));
        }
        return 0;
    }

}
