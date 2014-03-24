/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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

package org.jboss.legacy.connector.remoting;

import org.jboss.as.controller.ReloadRequiredWriteAttributeHandler;
import org.jboss.as.controller.SimpleAttributeDefinition;
import static org.jboss.as.controller.SimpleAttributeDefinitionBuilder.create;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.access.management.SensitiveTargetAccessConstraintDefinition;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.legacy.connector.ConnectorExtension;

import static org.jboss.dmr.ModelType.STRING;
import static org.jboss.legacy.connector.remoting.RemotingModel.REMOTING_PATH;
/**
 * @author baranowb
 */
public class RemotingResourceDefinition extends SimpleResourceDefinition {

    public static final SimpleAttributeDefinition SOCKET_BINDING = create(RemotingModel.SOCKET_BINDING, STRING)
            .setAllowNull(false)
            .setRestartAllServices()
            .addAccessConstraint(SensitiveTargetAccessConstraintDefinition.SOCKET_BINDING_REF)
            .build();

    public static final RemotingResourceDefinition INSTANCE = new RemotingResourceDefinition();

    private RemotingResourceDefinition() {
        super(REMOTING_PATH, ConnectorExtension.getResourceDescriptionResolver(RemotingModel.SERVICE_NAME), RemotingServiceAddStepHandler.INSTANCE,
                RemotingServiceRemoveStepHandler.INSTANCE);
    }

    @Override
    public void registerAttributes(ManagementResourceRegistration resourceRegistration) {
        super.registerAttributes(resourceRegistration);
        final ReloadRequiredWriteAttributeHandler handler = new ReloadRequiredWriteAttributeHandler(SOCKET_BINDING);
        resourceRegistration.registerReadWriteAttribute(SOCKET_BINDING, null, handler);
    }
}