package com.polemon.viki.communication.http.consumer;

import com.polemon.viki.communication.http.utils.Serializer;
import com.polemon.viki.commons.communication.exception.VikiCommunicationConsumerException;
import com.polemon.viki.commons.communication.models.Event;
import com.polemon.viki.commons.communication.utils.IVikiCommunicationConsumerHandler;
import com.polemon.viki.communication.http.utils.Deserializer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class used to process a specific endpoint.
 */
@Slf4j
public class HttpEndpoint {

    private static final String LOG_TITLE = "[HTTP-LIB] -";

    /**
     * List of url parameters.
     */
    private final List<String> endpointParametersList;

    /**
     * Class to get the request body.
     */
    private final Class<?> modelType;

    /**
     * Handler to execute the post-process.
     */
    private final IVikiCommunicationConsumerHandler handler;

    /**
     * Serializer of the request.
     */
    private final Serializer serializer;

    /**
     * Deserializer of the request.
     */
    private final Deserializer deserializer;

    public HttpEndpoint(String endpoint, Class<?> modelType, IVikiCommunicationConsumerHandler handler, Serializer serializer, Deserializer deserializer) {
        this.endpointParametersList = new LinkedList<>(List.of(endpoint.substring(1).split("/")));
        this.modelType = modelType;
        this.handler = handler;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    /**
     * Process the http requests that arrive to jetty server.
     *
     * @param req  contains info about the request
     * @param resp to response to request
     * @throws IOException if the response couldn't be processed
     */
    public void process(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        byte[] bytes = new byte[]{};
        try {
            Event eventRequest = getEvent(req);

            Event event = handler.execute(eventRequest);

            addResponseInfo(event, resp);
            bytes = serializer.serialize(event.getPayload());
        } catch (VikiCommunicationConsumerException e) {
            resp.setStatus(400);
            bytes = ("Body malformed!\n" + e.getMessage()).getBytes();
        } catch (Exception e) {
            resp.setStatus(500);
            bytes = e.getMessage().getBytes();
            log.error(e.getMessage(), e);
        } finally {
            resp.getWriter().println(new String(bytes, StandardCharsets.UTF_8));

            if (log.isDebugEnabled())
                log.debug("{} Request processed for url {}", LOG_TITLE, req.getRequestURI());
        }
    }

    /**
     * Verify if endpoint, from the request, belongs to this instance.
     *
     * @param endpoint the endpoint from the request
     * @return true if belongs
     */
    public boolean hasEndpoint(String endpoint) {
        endpoint = endpoint.substring(1);
        String[] splitEndpoint = endpoint.split("/");

        if (splitEndpoint.length != endpointParametersList.size()) {
            return false;
        }

        for (int x = 0; x < endpointParametersList.size(); x++) {
            String endpointParameter = endpointParametersList.get(x);
            if (endpointParameter.startsWith("{")) {
                continue;
            }
            if (!endpointParameter.equals(splitEndpoint[x])) {
                return false;
            }
        }

        return true;
    }

    /**
     * Verify if endpoint is already registered by matching the pattern.
     *
     * @param endpoint is the pattern to verify
     * @return true if already exist
     */
    public boolean matchEndpoint(String endpoint) {
        endpoint = endpoint.substring(1);
        String[] splitEndpoint = endpoint.split("/");

        if (splitEndpoint.length != endpointParametersList.size()) {
            return false;
        }

        for (int x = 0; x < endpointParametersList.size(); x++) {
            String endpointParameter = endpointParametersList.get(x);
            if (endpointParameter.startsWith("{") && splitEndpoint[x].startsWith("{")) {
                continue;
            }
            if (!endpointParameter.equals(splitEndpoint[x])) {
                return false;
            }
        }

        return true;
    }

    /**
     * Create a new event from a request.
     *
     * @param req to get the payload
     * @return Event with a newly created payload
     * @throws VikiCommunicationConsumerException if the payload can't be deserialized
     */
    private Event getEvent(HttpServletRequest req) throws VikiCommunicationConsumerException {
        try {
            String json = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            Object object = deserializer.deserialize(json, modelType);

            Event event = new Event(null, null, object);

            getRequestHeaders(event, req);
            getRequestPathParameters(event, req);
            getRequestQueryParameters(event, req);

            return event;
        } catch (IOException e) {
            throw new VikiCommunicationConsumerException(e.getMessage());
        }
    }

    /**
     * Insert headers that come in the request into the Event.
     *
     * @param eventRequest to add the headers
     * @param req          to get the headers
     */
    private void getRequestHeaders(Event eventRequest, HttpServletRequest req) {
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = req.getHeader(key);
            eventRequest.addExternalHeader(key, value);
        }

        eventRequest.addInternalHeader("request_url", req.getRequestURL().toString());
        eventRequest.addInternalHeader("method", req.getMethod());
    }

    /**
     * Insert variables into the event headers.
     *
     * @param eventRequest to add the variables
     * @param req          to get the variables
     */
    private void getRequestPathParameters(Event eventRequest, HttpServletRequest req) {
        String[] parameters = req.getRequestURI().substring(1).split("/");
        for (int x = 0; x < parameters.length; x++) {
            String param = endpointParametersList.get(x);
            if (param.startsWith("{")) {
                String key = param.substring(1, param.length() - 1);
                String value = parameters[x];
                eventRequest.addExternalHeader(key, value);
            }
        }
    }

    /**
     * Insert query parameters into the event headers.
     *
     * @param eventRequest to add the variables
     * @param req          to get the variables
     */
    private void getRequestQueryParameters(Event eventRequest, HttpServletRequest req) {
        req.getParameterMap().forEach((key, value) -> eventRequest.addExternalHeader(key, Arrays.toString(value)));
    }

    /**
     * Get info from the Event and put it in the http response headers.
     *
     * @param event to get the info
     * @param resp  to place the info
     */
    private void addResponseInfo(Event event, HttpServletResponse resp) {
        String code = event.getInternalValue("httpCode");
        if (code == null) {
            code = "200";
        }
        resp.setStatus(Integer.parseInt(code));

        Map<String, String> headers = event.getExternalHeaders();
        for (String key : headers.keySet()) {
            if (!key.equalsIgnoreCase("httpCode")) {
                resp.addHeader(key, headers.get(key));
            }
        }
    }

}
