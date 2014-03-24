/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;
import static org.jboss.as.controller.parsing.ParseUtils.requireNoAttributes;

import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.controller.parsing.ParseUtils;
import org.jboss.dmr.ModelNode;
import org.jboss.legacy.tx.txsession.UserSessionTransactionModel;
import org.jboss.legacy.tx.usertx.ClientUserTransactionModel;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLExtendedStreamReader;

/**
 * 
 * @author <a href="mailto:ehugonne@redhat.com">Emmanuel Hugonnet</a> (c) 2013 Red Hat, inc.
 */
public class UserTransactionSubsystem10Parser implements XMLElementReader<List<ModelNode>> {

    public static final UserTransactionSubsystem10Parser INSTANCE = new UserTransactionSubsystem10Parser();

    protected UserTransactionSubsystem10Parser() {
    }

    @Override
    public void readElement(final XMLExtendedStreamReader xmlExtendedStreamReader, final List<ModelNode> result)
            throws XMLStreamException {
        requireNoAttributes(xmlExtendedStreamReader);
        final ModelNode txSubsystemAddOperation = Util.createAddOperation();
        txSubsystemAddOperation.get(OP_ADDR).add(SUBSYSTEM, UserTransactionExtension.SUBSYSTEM_NAME);
        result.add(txSubsystemAddOperation);
        final ModelNode clientUserTransactionServiceAddOperation = Util.createAddOperation();
        clientUserTransactionServiceAddOperation.get(OP_ADDR).add(SUBSYSTEM, UserTransactionExtension.SUBSYSTEM_NAME)
                .add(ClientUserTransactionModel.SERVICE, ClientUserTransactionModel.SERVICE_NAME);
        result.add(clientUserTransactionServiceAddOperation);

        final ModelNode userSessionTransactionServiceAddOperation = Util.createAddOperation();
        userSessionTransactionServiceAddOperation.get(OP_ADDR).add(SUBSYSTEM, UserTransactionExtension.SUBSYSTEM_NAME)
                .add(UserSessionTransactionModel.SERVICE, UserSessionTransactionModel.SERVICE_NAME);
        result.add(userSessionTransactionServiceAddOperation);
        ParseUtils.requireNoContent(xmlExtendedStreamReader);
    }
}
