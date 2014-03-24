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

package org.jboss.legacy.ejb3.bridge;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;
import static org.jboss.as.controller.parsing.ParseUtils.requireNoAttributes;
import static org.jboss.as.controller.parsing.ParseUtils.requireNoContent;

import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.jboss.as.controller.operations.common.Util;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLExtendedStreamReader;

/**
 */
public class EJB3BridgeSubsystem10Parser implements XMLElementReader<List<ModelNode>> {

    public static final EJB3BridgeSubsystem10Parser INSTANCE = new EJB3BridgeSubsystem10Parser();

    protected EJB3BridgeSubsystem10Parser() {
    }

    @Override
    public void readElement(final XMLExtendedStreamReader xmlExtendedStreamReader, final List<ModelNode> result)
            throws XMLStreamException {
        // no attributes
        requireNoAttributes(xmlExtendedStreamReader);
        requireNoContent(xmlExtendedStreamReader);
        final ModelNode ejbBridgeSubsystemAddOperation = Util.createAddOperation();
        ejbBridgeSubsystemAddOperation.get(OP_ADDR).add(SUBSYSTEM, EJB3BridgeExtension.SUBSYSTEM_NAME);
        result.add(ejbBridgeSubsystemAddOperation);
    }
}
