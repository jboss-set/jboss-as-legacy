/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.legacy.spi.connector;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.aspects.remoting.AOPRemotingInvocationHandler;
import org.jboss.legacy.spi.common.LegacyBean;

import org.jboss.remoting.ServerConfiguration;
import org.jboss.remoting.transport.Connector;

/**
 * @author baranowb
 * 
 */
public class ConnectorProxy extends LegacyBean {
    private static final String INVOCATION_HANDLER_KEY = "AOP";
    private static final String INVOCATION_HANDLER_CLASS = AOPRemotingInvocationHandler.class.getName();
    private static final String TRANSPORT = "socket";
    private String host;
    private String port;
    private boolean tcpNoDelay;

    private Connector connector;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public Connector getConnector() {
        return connector;
    }

    @Override
    protected void internalStart() throws Exception {
        if(this.host == null)
            throw new IllegalArgumentException("Host value must not be null");
        if(this.port == null)
            throw new IllegalArgumentException("Port value must not be null");
        Logger logger = Logger.getLogger("org.jboss.remoting");
        logger.setLevel(Level.ALL);
        logger = Logger.getLogger("org.jnp");
        logger.setLevel(Level.ALL);
        final ServerConfiguration serverConfiguration = new ServerConfiguration(TRANSPORT);
        Map<String, String> parameters = new HashMap<String, String>(6);
        parameters.put("serverBindAddress", this.host);
        parameters.put("serverBindPort", this.port);
        parameters.put("dataType", "invocation");
        parameters.put("marshaller", "org.jboss.invocation.unified.marshall.InvocationMarshaller");
        parameters.put("unmarshaller", "org.jboss.invocation.unified.marshall.InvocationUnMarshaller");
        parameters.put("enableTcpNoDelay", Boolean.toString(this.tcpNoDelay));
        serverConfiguration.setInvokerLocatorParameters(parameters);
        final Map<String, String> invocationHandlers = new HashMap<String, String>(1);
        invocationHandlers.put(INVOCATION_HANDLER_KEY, INVOCATION_HANDLER_CLASS);
        serverConfiguration.setInvocationHandlers(invocationHandlers);
        this.connector = new Connector();
        this.connector.setServerConfiguration(serverConfiguration);
        this.connector.start();
    }

    @Override
    protected void internalStop() throws Exception {
        this.connector.stop();
        this.connector = null;
    }

}
