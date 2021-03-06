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

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.msc.service.StartException;

/**
 * @author baranowb
 * 
 */
@MessageBundle(projectCode = "LEGACY")
public interface ConnectorMessages {

    /**
     * The messages
     */
    ConnectorMessages MESSAGES = Messages.getBundle(ConnectorMessages.class);

    @Message(id = 50100, value = "Could not start legacy connector.")
    StartException couldNotStartConnectorService(@Cause Exception e);
//    
//    @Message(id = 50101, value = "Could not stop legacy connector.")
//    RuntimeException couldNotStop(@Cause Exception e);
}
