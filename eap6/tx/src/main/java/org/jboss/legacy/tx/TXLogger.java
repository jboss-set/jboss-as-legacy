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

package org.jboss.legacy.tx;

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
public interface TXLogger extends BasicLogger {

    /**
     * A logger with a category of the package name.
     */
    TXLogger ROOT_LOGGER = Logger.getMessageLogger(TXLogger.class, TXLogger.class.getPackage().getName());

    @LogMessage(level = INFO)
    @Message(id = 53000, value = "Starting legacy user transaction service.")
    void startUserTransactionService();
    
    @LogMessage(level = INFO)
    @Message(id = 53001, value = "Stopping legacy user transaction service.")
    void stopUserTransactionService();
    
    @LogMessage(level = WARN)
    @Message(id = 53002, value = "Failed to stop legacy user transaction service.")
    void failedToStopUserTransactionService(@Cause Exception e);

    
    @LogMessage(level = INFO)
    @Message(id = 53003, value = "Starting legacy client user transaction service.")
    void startClientUserTransactionService();
    
    @LogMessage(level = INFO)
    @Message(id = 53004, value = "Stopping legacy client user transaction service.")
    void stopClientUserTransactionService();
    
    @LogMessage(level = WARN)
    @Message(id = 53005, value = "Failed to stop legacy client user transaction service.")
    void failedToStopClientUserTransactionService(@Cause Exception e);
}
