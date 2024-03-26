package com.polemon.viki.communication.http.consumer;

import com.polemon.viki.commons.VikiException;
import com.polemon.viki.commons.VikiProperties;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Class that extends Server.
 * Class used to register new servlets and expose a specific port.
 *
 * @see Server
 */
public class HttpServer extends Server {

    /**
     * Context of the exposed port.
     */
    private final ServletContextHandler context;

    public HttpServer() throws VikiException {
        super(VikiProperties.getINSTANCE().getServerPort());
        this.context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        this.setHandler(context);
    }

    /**
     * Method used to add new servlets.
     *
     * @param servlet to be added
     * @param path    of the endpoint to register
     */
    public void addServlet(Servlet servlet, String path) {
        context.addServlet(new ServletHolder(servlet), path);
    }

}
