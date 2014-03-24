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

package org.jboss.legacy.tx;

import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.controller.registry.OperationEntry;
import static org.jboss.legacy.tx.UserTransactionExtension.SUBSYSTEM_NAME;
import org.jboss.legacy.tx.txsession.UserSessionTransactionResourceDefinition;
import org.jboss.legacy.tx.usertx.ClientUserTransactionResourceDefinition;

/**
 *
 * @author <a href="mailto:ehugonne@redhat.com">Emmanuel Hugonnet</a> (c) 2013 Red Hat, inc.
 */
public class UserTransactionSubsystemRootResourceDefinition extends SimpleResourceDefinition {

    public static final UserTransactionSubsystemRootResourceDefinition INSTANCE = new UserTransactionSubsystemRootResourceDefinition();

    UserTransactionSubsystemRootResourceDefinition() {
        super(UserTransactionExtension.SUBSYSTEM_PATH, UserTransactionExtension.getResourceDescriptionResolver(SUBSYSTEM_NAME),
                UserTransactionSubsystemAdd.INSTANCE, UserTransactionSubsystemRemove.INSTANCE, OperationEntry.Flag.RESTART_ALL_SERVICES,
                OperationEntry.Flag.RESTART_ALL_SERVICES);
    }

    @Override
    public void registerChildren(ManagementResourceRegistration resourceRegistration) {
        super.registerChildren(resourceRegistration);
        resourceRegistration.registerSubModel(ClientUserTransactionResourceDefinition.INSTANCE);
        resourceRegistration.registerSubModel(UserSessionTransactionResourceDefinition.INSTANCE);
    }

}
