package com.polemon.viki.communication.http.consumer;

import com.polemon.viki.communication.http.utils.Serializer;
import com.polemon.viki.commons.communication.utils.IVikiCommunicationConsumerHandler;
import com.polemon.viki.communication.http.utils.Deserializer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpMethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Servlet that is created for each endpoint in order to pre-process the request and process the response.
 */
@Slf4j
public class Servlet extends HttpServlet {

    private static final String LOG_TITLE = "[HTTP-LIB] -";

    /**
     * This map holds info about each endpoint at each method.
     * Key is the method.
     */
    private final Map<String, List<HttpEndpoint>> endpointList;

    public Servlet() {
        this.endpointList = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (log.isDebugEnabled())
            log.debug("{} Arrived request {}", LOG_TITLE, req.getRequestURI());

        List<HttpEndpoint> endpoints = endpointList.get(req.getMethod());
        if (endpoints == null) {
            endpoints = new LinkedList<>();
        }

        for (HttpEndpoint endpoint : endpoints) {
            if (endpoint.hasEndpoint(req.getRequestURI())) {
                endpoint.process(req, resp);
                return;
            }
        }

        if (log.isDebugEnabled())
            log.debug("{} No handlers found for {}", LOG_TITLE, req.getRequestURI());
    }

    /**
     * Method used to add a new handler to this servlet. Since there is a new ServletSpec for each endpoint, it means
     * that this method is only called if it is a different method for the same endpoint.
     *
     * @param source       String of the endpoint
     * @param method       of the request
     * @param modelType    class to be converted when the request is received
     * @param handler      to process the request
     * @param serializer   to serialize the response
     * @param deserializer to deserialize the request
     */
    public void addEndpoint(String source, HttpMethod method, Class<?> modelType, IVikiCommunicationConsumerHandler handler, Serializer serializer, Deserializer deserializer) {
        List<HttpEndpoint> endpoints = endpointList.computeIfAbsent(method.asString(), x -> new LinkedList<>());
        for (HttpEndpoint endpoint : endpoints) {
            if (endpoint.matchEndpoint(source)) {
                return;
            }
        }

        endpoints.add(new HttpEndpoint(source, modelType, handler, serializer, deserializer));
    }

}
