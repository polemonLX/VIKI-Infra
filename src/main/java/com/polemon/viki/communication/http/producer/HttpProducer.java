package com.polemon.viki.communication.http.producer;

import com.polemon.viki.commons.VikiException;
import com.polemon.viki.commons.VikiProperties;
import com.polemon.viki.commons.communication.exception.VikiCommunicationConsumerException;
import com.polemon.viki.commons.communication.exception.VikiCommunicationProducerException;
import com.polemon.viki.commons.communication.models.Event;
import com.polemon.viki.commons.communication.producer.IVikiCommunicationProducer;
import com.polemon.viki.commons.communication.utils.MediaType;
import com.polemon.viki.commons.communication.utils.Method;
import com.polemon.viki.communication.http.utils.DeserializerEnum;
import com.polemon.viki.communication.http.utils.SerializerEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Http producer.
 *
 * @see IVikiCommunicationProducer
 */
@Slf4j
public class HttpProducer implements IVikiCommunicationProducer {

    private static final String LOG_TITLE = "[HTTPProducer] - ";

    /**
     * Singleton instance.
     */
    private static HttpProducer INSTANCE;

    /**
     * Timeout to wait for the response.
     */
    private final long timeout;

    /**
     * Java http client.
     */
    private final HttpClient httpClient;

    private HttpProducer() throws VikiException {
        this.httpClient = HttpClient.newHttpClient();
        this.timeout = VikiProperties.getINSTANCE().getProducerTimeout();
    }

    public static HttpProducer getInstance() throws VikiException {
        if (INSTANCE == null) {
            INSTANCE = new HttpProducer();
        }
        return INSTANCE;
    }

    @Override
    public void produce(Event event) throws VikiCommunicationProducerException {
        new Thread(() -> {
            try {
                Method method = getMethod(event.getInternalValue("method"));
                produceRequest(method, event.getAddress(), event.getPayload(), event.getExternalHeaders(), event.getInternalValue("serializer"), event.getInternalValue("deserializer"), null);
            } catch (VikiCommunicationProducerException e) {
                log.error("{} Error producing async event: {}", LOG_TITLE, e.getMessage(), e);
            }
        }).start();
    }

    @Override
    public Event produce(Event event, Class<?> bodyType) throws VikiCommunicationProducerException {
        Method method = getMethod(event.getInternalValue("method"));
        return produceRequest(method, event.getAddress(), event.getPayload(), event.getExternalHeaders(), event.getInternalValue("serializer"), event.getInternalValue("deserializer"), bodyType);
    }

    /**
     * This method builds the HTTP request, sends it, receives the HTTP response, processes it and returns an Event
     * with the HTTP response headers and body.
     *
     * @param method       HTTP request method
     * @param address      HTTP address
     * @param body         HTTP request body
     * @param headers      HTTP request headers
     * @param serializer   Serializer for the request body
     * @param deserializer Deserializer for the response body
     * @param bodyType     Class of the response body
     * @return Event with the HTTP headers (including a "httpCode" header with the HTTP status code) and the body of the HTTP response
     * @throws VikiCommunicationProducerException if there's a problem sending the HTTP request or processing the HTTP response
     */
    private Event produceRequest(Method method, String address, Object body, Map<String, String> headers, String serializer, String deserializer, Class<?> bodyType) throws VikiCommunicationProducerException {
        HttpRequest.Builder request;

        if (body == null) {
            request = buildRequestWithoutBody(method, address);
        } else {
            request = buildRequestWithBody(method, address, body, serializer);
        }

        request.timeout(Duration.ofSeconds(timeout));

        if (headers != null) {
            if (log.isDebugEnabled())
                log.debug("{}Adding headers to the request.", LOG_TITLE);

            headers.forEach(request::header);
        }

        try {
            if (log.isDebugEnabled())
                log.debug("{}Sending the request.", LOG_TITLE);

            HttpResponse<byte[]> httpResponse = httpClient.send(request.build(), HttpResponse.BodyHandlers.ofByteArray());
            return buildResponseEvent(httpResponse, deserializer, bodyType);
        } catch (IOException | InterruptedException e) {
            if (log.isDebugEnabled())
                log.debug("{}An exception was thrown while trying to send the request. Message: {}", LOG_TITLE, e.getMessage());

            throw new VikiCommunicationProducerException(e.getMessage());
        }
    }

    /**
     * Builds the HTTP request without a body
     *
     * @param method  HTTP method
     * @param address HTTP address
     * @return HttpRequest.Builder of the HTTP request
     * @throws VikiCommunicationProducerException if the Http Method equals POST or PUT
     */
    private HttpRequest.Builder buildRequestWithoutBody(Method method, String address) throws VikiCommunicationProducerException {
        if (method.equals(Method.POST) || method.equals(Method.PUT)) {
            if (log.isDebugEnabled())
                log.debug("{}The HTTP PUT/POST requests requires a body.", LOG_TITLE);

            throw new VikiCommunicationProducerException("The HTTP PUT/POST requests requires a body.");
        }

        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(address));
        if (method.equals(Method.GET))
            request.GET();
        else if (method.equals(Method.DELETE))
            request.DELETE();
        return request;
    }

    /**
     * Builds the HTTP request with a body
     *
     * @param method     HTTP method
     * @param address    HTTP address
     * @param body       HTTP request body
     * @param serializer HTTP request body serializer
     * @return HttpRequest.Builder of the HTTP request
     * @throws VikiCommunicationProducerException if the HTTP method equals GET
     */
    private HttpRequest.Builder buildRequestWithBody(Method method, String address, Object body, String serializer) throws VikiCommunicationProducerException {
        if (method.equals(Method.GET)) {
            if (log.isDebugEnabled())
                log.debug("{}The HTTP GET request doesn't have a body.", LOG_TITLE);

            throw new VikiCommunicationProducerException("The HTTP GET request doesn't have a body.");
        }

        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(address));

        //Configure content Header
        if (serializer.equals(MediaType.APPLICATION_JSON)) {
            request.header("Content-Type", "application/json");
        } else if (serializer.equals(MediaType.APPLICATION_XML)) {
            request.header("Content-Type", "application/xml");
        }

        byte[] payload;
        try {
            payload = serializeBody(body, serializer);
        } catch (VikiCommunicationProducerException e) {
            if (log.isDebugEnabled())
                log.debug("{}An exception was thrown while trying to serialize the body of the request. Message: {}", LOG_TITLE, e.getMessage());

            throw new VikiCommunicationProducerException(e.getMessage());
        }

        if (method.equals(Method.POST)) {
            request.POST(HttpRequest.BodyPublishers.ofByteArray(payload));
        } else if (method.equals(Method.PUT)) {
            request.PUT(HttpRequest.BodyPublishers.ofByteArray(payload));
        } else if (method.equals(Method.DELETE)) {
            request.method("DELETE", HttpRequest.BodyPublishers.ofByteArray(payload));
        }

        return request;
    }

    /**
     * Builds the HTTP response Event
     *
     * @param httpResponse HTTP response
     * @param deserializer HTTP response body deserializer
     * @param bodyType     HTTP response body class
     * @return Event with the HTTP response headers and body
     * @throws VikiCommunicationProducerException if there's a problem deserializing the HTTP response body
     */
    private Event buildResponseEvent(HttpResponse<byte[]> httpResponse, String deserializer, Class<?> bodyType) throws VikiCommunicationProducerException {
        if (log.isDebugEnabled())
            log.debug("{}Building response event.", LOG_TITLE);

        Object responseBody;
        try {
            responseBody = deserializeBody(httpResponse.body(), deserializer, bodyType);
        } catch (VikiCommunicationProducerException e) {
            if (log.isDebugEnabled())
                log.debug("{}An exception was thrown while trying to deserialize the body of the HTTP response. Message: {}", LOG_TITLE, e.getMessage());

            throw e;
        }

        Event responseEvent = new Event(null, null, responseBody);
        HttpHeaders headers = httpResponse.headers();
        responseEvent.addInternalHeader("httpCode", String.valueOf(httpResponse.statusCode()));

        //HTTP protocol has multi-value headers, and they are comma separated
        for (String key : headers.map().keySet()) {
            List<String> values = headers.allValues(key);
            String concatenatedValues = String.join(", ", values);
            responseEvent.addExternalHeader(key, concatenatedValues);
        }

        return responseEvent;
    }

    /**
     * Serializes the body of the HTTP requests
     *
     * @param body       HTTP request body
     * @param serializer HTTP request body serializer
     * @return byte array of the body
     * @throws VikiCommunicationProducerException if there's a problem serializing the body
     */
    private byte[] serializeBody(Object body, String serializer) throws VikiCommunicationProducerException {
        if (log.isDebugEnabled())
            log.debug("{}Serializing {} class to {}.", LOG_TITLE, body.getClass().getSimpleName(), serializer);

        byte[] payload = new byte[]{};
        if (serializer.equals(MediaType.APPLICATION_JSON)) {
            payload = (body instanceof String) ? ((String) body).getBytes() : SerializerEnum.JSON.getSerializer().serialize(body);
        } else if (serializer.equals(MediaType.APPLICATION_XML)) {
            payload = (body instanceof String) ? ((String) body).getBytes() : SerializerEnum.XML.getSerializer().serialize(body);
        }

        return payload;
    }

    /**
     * Deserializes the body of the HTTP responses
     *
     * @param body         HTTP response body
     * @param deserializer HTTP response body deserializer
     * @param bodyType     HTTP response body class
     * @return Object corresponding to the HTTP request body
     * @throws VikiCommunicationProducerException if there's a problem deserializing the body
     */
    private Object deserializeBody(byte[] body, String deserializer, Class<?> bodyType) throws VikiCommunicationProducerException {
        if (log.isDebugEnabled())
            log.debug("{}Deserializing response body to class {}.", LOG_TITLE, bodyType.getSimpleName());

        Object bodyObject = new Object();
        try {
            if (deserializer.equals(MediaType.APPLICATION_JSON)) {
                bodyObject = DeserializerEnum.JSON.getDeserializer().deserialize(body, bodyType);

            } else if (deserializer.equals(MediaType.APPLICATION_XML)) {
                bodyObject = DeserializerEnum.XML.getDeserializer().deserialize(body, bodyType);
            }
        } catch (VikiCommunicationConsumerException e) {
            throw new VikiCommunicationProducerException(e.getMessage());
        }

        return bodyObject;
    }

    /**
     * Deserialize method from String to the ENUM Method
     *
     * @param methodString to be converted
     * @return Method ENUM
     * @throws VikiCommunicationProducerException if the string is null, empty or doesn't match any valid ENUM
     */
    private Method getMethod(String methodString) throws VikiCommunicationProducerException {
        try {
            if (methodString == null || methodString.isBlank()) {
                throw new VikiCommunicationProducerException("Method can't be null or empty");
            }
            return Method.valueOf(methodString);
        } catch (IllegalArgumentException e) {
            throw new VikiCommunicationProducerException("Unknown method \"" + methodString + "\"");
        }
    }

}
