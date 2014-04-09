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

package org.jboss.legacy.ejb3.bridge;

import static org.jboss.logging.Logger.Level.DEBUG;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * @author baranowb
 *
 */
@MessageLogger(projectCode = "LEGACY")
public interface EJB3BridgeLogger extends BasicLogger {

    /**
     * A logger with a category of the package name.
     */
    EJB3BridgeLogger ROOT_LOGGER = Logger.getMessageLogger(EJB3BridgeLogger.class, EJB3BridgeLogger.class.getPackage().getName());

    @LogMessage(level = DEBUG)
    @Message(id = 52000, value = "Legacy EJB3 pre-processing EJB deployment '%s'")
    void preProcessingDeployment(final String ejbModuleName);

}
