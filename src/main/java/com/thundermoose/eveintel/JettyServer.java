package com.thundermoose.eveintel;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class JettyServer {
  public static void main(String[] args) throws Exception {
    Server server = new Server();
    ServerConnector connector = new ServerConnector(server);
    connector.setPort(8080);
    server.addConnector(connector);

    ResourceHandler resource_handler = new ResourceHandler();
    resource_handler.setDirectoriesListed(true);
    resource_handler.setWelcomeFiles(new String[]{"index.html"});

    resource_handler.setResourceBase("src/main/webapp");

    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[]{resource_handler, new DefaultHandler()});
    server.setHandler(handlers);

    server.start();
    server.join();
  }
}
