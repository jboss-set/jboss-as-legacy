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

package org.jboss.legacy.common;

import static org.jboss.logging.Logger.Level.DEBUG;
import static org.jboss.logging.Logger.Level.INFO;
import static org.jboss.logging.Logger.Level.WARN;

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
public interface EJB3Logger extends BasicLogger {

    /**
     * A logger with a category of the package name.
     */
    EJB3Logger ROOT_LOGGER = Logger.getMessageLogger(EJB3Logger.class, EJB3Logger.class.getPackage().getName());

    @LogMessage(level = DEBUG)
    @Message(id = 51000, value = "Legacy EJB3 post processing EJB deployment '%s'")
    void postProcessingDeployment(final String ejbModuleName);
    
    @LogMessage(level = INFO)
    @Message(id = 51001, value = "Starting legacy EJB3 registrar.")
    void startRegistrar();
    
    @LogMessage(level = INFO)
    @Message(id = 51002, value = "Stopping legacy EJB3 registrar.")
    void stoppingRegistrar();
    
    @LogMessage(level = WARN)
    @Message(id = 51003, value = "Failed to stop legacy EJB3 registrar.")
    void couldNotStopRegistrar(@Cause Exception e);
    
    @LogMessage(level = INFO)
    @Message(id = 51004, value = "Starting legacy invocation service for '%s' EJB.")
    void startDynamicInvocationService(final String ejbName);
    
    @LogMessage(level = INFO)
    @Message(id = 51005, value = "Stopping legacy invocation service for '%s' EJB.")
    void stoppingDynamicInvocationService(final String ejbName);
    
    @LogMessage(level = WARN)
    @Message(id = 51006, value = "Failed to stop legacy invocation service for '%s' EJB.")
    void couldNotStopDynamicInvocationService(String ejbName, @Cause Exception e);

}
