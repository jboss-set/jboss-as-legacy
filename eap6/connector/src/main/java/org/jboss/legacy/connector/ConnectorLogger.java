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

package org.jboss.legacy.connector;

import static org.jboss.logging.Logger.Level.INFO;
import static org.jboss.logging.Logger.Level.WARN;

import java.net.InetAddress;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * @author baranowb
 *
 */
@MessageLogger(projectCode = "LEGACY")
public interface ConnectorLogger extends BasicLogger {

    /**
     * A logger with a category of the package name.
     */
    ConnectorLogger ROOT_LOGGER = Logger.getMessageLogger(ConnectorLogger.class, ConnectorLogger.class.getPackage().getName());

    @LogMessage(level = INFO)
    @Message(id = 50000, value = "Starting legacy remoting connector, listening on '%s'->'%s:%s'.")
    void startConnectorService(final String name, final InetAddress hostName, final String port);
    
    @LogMessage(level = INFO)
    @Message(id = 50001, value = "Stopping legacy remoting connector.")
    void stopConnectorService();
    
    @LogMessage(level = WARN)
    @Message(id = 50002, value = "Failed to stop legacy remoting connector.")
    void couldNotStopConnectorService(@Cause Exception e);
}
