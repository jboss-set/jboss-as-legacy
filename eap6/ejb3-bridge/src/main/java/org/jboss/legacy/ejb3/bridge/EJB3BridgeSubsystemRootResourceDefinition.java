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

package org.jboss.legacy.ejb3.bridge;

import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.controller.registry.OperationEntry;

/**
 * @author baranowb
 *
 */
public class EJB3BridgeSubsystemRootResourceDefinition extends SimpleResourceDefinition {

    public static final EJB3BridgeSubsystemRootResourceDefinition INSTANCE = new EJB3BridgeSubsystemRootResourceDefinition();

    EJB3BridgeSubsystemRootResourceDefinition() {
        super(EJB3BridgeExtension.SUBSYSTEM_PATH,EJB3BridgeExtension.getResourceDescriptionResolver(EJB3BridgeExtension.SUBSYSTEM_NAME),
                EJB3BridgeSubsystemAdd.INSTANCE, EJB3BridgeSubsystemRemove.INSTANCE, OperationEntry.Flag.RESTART_ALL_SERVICES,
                OperationEntry.Flag.RESTART_ALL_SERVICES);
    }

    @Override
    public void registerChildren(ManagementResourceRegistration resourceRegistration) {
        super.registerChildren(resourceRegistration);
//
//        // subsystem=legacy-jnp/service=jnp-server
//        resourceRegistration.registerSubModel(JNPServerResourceDefinition.INSTANCE);
//        // subsystem=legacy-jnp/service=jnp-connector
//        resourceRegistration.registerSubModel(JNPServerConnectorResourceDefinition.INSTANCE);
    }

}
