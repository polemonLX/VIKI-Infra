package com.polemon.viki.communication.http.consumer;

import com.polemon.viki.commons.VikiException;
import com.polemon.viki.commons.VikiProperties;
import com.polemon.viki.commons.communication.consumer.IVikiCommunicationConsumer;
import com.polemon.viki.commons.communication.exception.VikiCommunicationConsumerException;
import com.polemon.viki.commons.communication.utils.*;
import com.polemon.viki.communication.http.utils.Deserializer;
import com.polemon.viki.communication.http.utils.DeserializerEnum;
import com.polemon.viki.communication.http.utils.Serializer;
import com.polemon.viki.communication.http.utils.SerializerEnum;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Consumer for HTTP.
 *
 * @see IVikiCommunicationConsumer
 */
@Slf4j
public class HttpConsumer implements IVikiCommunicationConsumer {

    private static final String LOG_TITLE = "[HTTP-LIB] -";

    /**
     * Singleton instance of this class.
     */
    private static HttpConsumer INSTANCE;

    /**
     * Jetty server to expose endpoints.
     */
    private final HttpServer httpServer;

    /**
     * Each endpoint creates a new Servlet. Each of those has a map for multiple methods on the same endpoint.
     */
    private final Map<String, Servlet> servletMap;

    private HttpConsumer() throws VikiException {
        this.httpServer = new HttpServer();
        this.servletMap = new HashMap<>();
    }

    /**
     * This class is not intended to be instantiated more than one time.
     * To prevent that, this class is a singleton.
     *
     * @return HttpConsumer
     */
    public static HttpConsumer getINSTANCE() throws VikiException {
        if (INSTANCE == null) {
            INSTANCE = new HttpConsumer();
        }
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws VikiException {
        try {
            httpServer.start();
            if (log.isDebugEnabled())
                log.debug("{} Server HTTP connected on port {}", LOG_TITLE, VikiProperties.getINSTANCE().getServerPort());
        } catch (Exception e) {
            throw new VikiCommunicationConsumerException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() throws VikiException {
        try {
            httpServer.stop();
            if (log.isDebugEnabled())
                log.debug("{} Server HTTP has shutdown", LOG_TITLE);
        } catch (Exception e) {
            throw new VikiCommunicationConsumerException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void subscribeEvent(SubscriptionInfo subscriptionInfo, IVikiCommunicationConsumerHandler consumerHandler) throws VikiCommunicationConsumerException {
        HTTPSubscriptionInfo info = checkInfo(subscriptionInfo);

        String source = getSource(info.getSource());
        HttpMethod jettyMethod = getJettyMethod(info.getMethod());
        Class<?> modelType = info.getModelType();
        Serializer serializer = getSerializer(info.getSerializer());
        Deserializer deserializer = getDeserializer(info.getDeserializer());

        String convertedPath = getConvertedPath(source);

        Servlet servlet = servletMap.computeIfAbsent(convertedPath, x -> {
            Servlet servletAux = new Servlet();
            httpServer.addServlet(servletAux, convertedPath);
            return servletAux;
        });
        servlet.addEndpoint(source, jettyMethod, modelType, consumerHandler, serializer, deserializer);

        if (log.isDebugEnabled())
            log.debug("{} Server HTTP is now subscribed to endpoint \"{}\" with method \"{}\"", LOG_TITLE, source, jettyMethod.asString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void subscribeEvents(Map<SubscriptionInfo, IVikiCommunicationConsumerHandler> subscriptionInfos) throws VikiCommunicationConsumerException {
        for (SubscriptionInfo subscriptionInfo : subscriptionInfos.keySet()) {
            subscribeEvent(subscriptionInfo, subscriptionInfos.get(subscriptionInfo));
        }
    }

    /**
     * Convert the infra endpoint to an endpoint of jetty.
     *
     * @param method given by the infra
     * @return jetty method converted from the infra method
     * @throws VikiCommunicationConsumerException if the method is not supported
     */
    private HttpMethod getJettyMethod(Method method) throws VikiCommunicationConsumerException {
        HttpMethod methodToReturn;
        switch (method) {
            case GET -> methodToReturn = HttpMethod.GET;
            case POST -> methodToReturn = HttpMethod.POST;
            case PUT -> methodToReturn = HttpMethod.PUT;
            case DELETE -> methodToReturn = HttpMethod.DELETE;
            case PATCH -> methodToReturn = HttpMethod.PATCH;
            default -> throw new VikiCommunicationConsumerException("HTTP Method not supported");
        }
        return methodToReturn;
    }

    /**
     * The API receives a SubscriptionInfo, but each module can create a class that extends SubscriptionInfo.
     * This method helps to verify if all the data is valid.
     *
     * @param subscriptionInfo with all the data
     * @return HTTPSubscriptionInfo cast from the subscriptionInfo
     * @throws VikiCommunicationConsumerException if the cast is not valid or if any information is null
     */
    private HTTPSubscriptionInfo checkInfo(SubscriptionInfo subscriptionInfo) throws VikiCommunicationConsumerException {
        if (!(subscriptionInfo instanceof HTTPSubscriptionInfo info)) {
            throw new VikiCommunicationConsumerException("SubscriptionInfo is not an HTTPSubscriptionInfo!");
        }

        if (info.getSource() == null || info.getSource().isEmpty()) {
            throw new VikiCommunicationConsumerException("Source cannot be null or empty!");
        }
        if (info.getMethod() == null) {
            throw new VikiCommunicationConsumerException("Method cannot be null!");
        }
        if (info.getSerializer() == null || info.getSerializer().isEmpty()) {
            throw new VikiCommunicationConsumerException("Serializer cannot be empty or null!");
        }
        if (info.getDeserializer() == null || info.getDeserializer().isEmpty()) {
            throw new VikiCommunicationConsumerException("Deserializer cannot be empty or null!");
        }

        return info;
    }

    /**
     * Verify if the source is trimmed and starts with "/".
     *
     * @param source to be verified
     * @return String converted source with "/" at the beginning and trimmed
     */
    private String getSource(String source) {
        source = source.trim();

        if (!source.startsWith("/")) {
            source = "/" + source;
        }

        return source;
    }

    /**
     * If a source has some unknown arguments, it is possible to pass them with {}.
     * The final endpoint to register on jetty, cannot contain those brackets. This method converts an endpoint with
     * brackets in a valid endpoint for jetty.
     *
     * @param source with all the brackets needed
     * @return String of a new endpoint without brackets
     * @throws VikiCommunicationConsumerException if the source is malformed
     */
    private String getConvertedPath(String source) throws VikiCommunicationConsumerException {
        if (!source.contains("{")) {
            return source;
        }

        for (String variable : source.split("/")) {
            if (variable.contains("{") || variable.contains("}")) {
                if (!variable.startsWith("{") || !variable.endsWith("}")) {
                    throw new VikiCommunicationConsumerException("Source is not correct!");
                }
            }
        }

        return source.substring(0, source.indexOf("{")) + "*";
    }

    /**
     * This method abstracts a conversion of a MediaType string to a specific IExagonCommunicationSerializer.
     *
     * @param IExagonCommunicationSerializer string from MediaType
     * @return concrete IExagonCommunicationSerializer
     * @throws VikiCommunicationConsumerException if there is no mapped IExagonCommunicationSerializer for the given String
     */
    private Serializer getSerializer(String IExagonCommunicationSerializer) throws VikiCommunicationConsumerException {
        switch (IExagonCommunicationSerializer) {
            case MediaType.APPLICATION_JSON -> {
                return SerializerEnum.JSON.getSerializer();
            }
            case MediaType.APPLICATION_XML -> {
                return SerializerEnum.XML.getSerializer();
            }
        }

        throw new VikiCommunicationConsumerException("No serializer found!");
    }

    /**
     * This method abstracts a conversion of a MediaType string to a specific IExagonCommunicationDeserializer.
     *
     * @param IExagonCommunicationDeserializer string from MediaType
     * @return concrete IExagonCommunicationDeserializer
     * @throws VikiCommunicationConsumerException if there is no mapped IExagonCommunicationDeserializer for the given String
     */
    private Deserializer getDeserializer(String IExagonCommunicationDeserializer) throws VikiCommunicationConsumerException {
        switch (IExagonCommunicationDeserializer) {
            case MediaType.APPLICATION_JSON -> {
                return DeserializerEnum.JSON.getDeserializer();
            }
            case MediaType.APPLICATION_XML -> {
                return DeserializerEnum.XML.getDeserializer();
            }
        }
        throw new VikiCommunicationConsumerException("No deserializer found!");
    }

}
