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
package org.jboss.legacy.connector;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;
import static org.jboss.as.controller.parsing.ParseUtils.requireNoAttributes;
import static org.jboss.as.controller.parsing.ParseUtils.requireNoContent;
import static org.jboss.as.controller.parsing.ParseUtils.requireNoNamespaceAttribute;
import static org.jboss.as.controller.parsing.ParseUtils.unexpectedAttribute;
import static org.jboss.as.controller.parsing.ParseUtils.unexpectedElement;

import java.util.EnumSet;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.jboss.as.controller.operations.common.Util;
import org.jboss.dmr.ModelNode;
import org.jboss.legacy.connector.remoting.RemotingModel;
import org.jboss.legacy.connector.remoting.RemotingResourceDefinition;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLExtendedStreamReader;

/**
 */
public class ConnectorSubsystem10Parser implements XMLElementReader<List<ModelNode>> {

    public static final ConnectorSubsystem10Parser INSTANCE = new ConnectorSubsystem10Parser();

    protected ConnectorSubsystem10Parser() {
    }

    @Override
    public void readElement(final XMLExtendedStreamReader xmlExtendedStreamReader, final List<ModelNode> result)
            throws XMLStreamException {
        // no attributes
        requireNoAttributes(xmlExtendedStreamReader);
        
        final ModelNode connectorSubsystemAddOperation = Util.createAddOperation();
        connectorSubsystemAddOperation.get(OP_ADDR).add(SUBSYSTEM, ConnectorExtension.SUBSYSTEM_NAME);
        result.add(connectorSubsystemAddOperation);
        
        final ModelNode remotginAddOperation = Util.createAddOperation();
        remotginAddOperation.get(OP_ADDR).add(SUBSYSTEM, ConnectorExtension.SUBSYSTEM_NAME).add(RemotingModel.SERVICE, RemotingModel.SERVICE_NAME);
        result.add(remotginAddOperation);
        // elements
        final EnumSet<ConnectorSubsystemXMLElement> encountered = EnumSet.noneOf(ConnectorSubsystemXMLElement.class);
        while (xmlExtendedStreamReader.hasNext() && xmlExtendedStreamReader.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (ConnectorSubsystemNamespace.forUri(xmlExtendedStreamReader.getNamespaceURI()) != ConnectorSubsystemNamespace.LEGACY_CONNECTOR_1_0) {
                throw unexpectedElement(xmlExtendedStreamReader);
            }
            final ConnectorSubsystemXMLElement element = ConnectorSubsystemXMLElement.forName(xmlExtendedStreamReader
                    .getLocalName());
            if (!encountered.add(element)) {
                throw unexpectedElement(xmlExtendedStreamReader);
            }
            switch (element) {
                case REMOTING:
                    this.parseRemoting(xmlExtendedStreamReader,remotginAddOperation);
                    break;
                case UNKNOWN:
                default:
                    throw unexpectedElement(xmlExtendedStreamReader);

            }

        }

    }

    private void parseRemoting(XMLExtendedStreamReader xmlExtendedStreamReader, ModelNode remotginAddOperation) throws XMLStreamException {
        for (int i = 0; i < xmlExtendedStreamReader.getAttributeCount(); i++) {
            requireNoNamespaceAttribute(xmlExtendedStreamReader, i);
            final String value = xmlExtendedStreamReader.getAttributeValue(i);
            switch (ConnectorSubsystemXMLAttribute.forName(xmlExtendedStreamReader.getAttributeLocalName(i))) {
                case SOCKET_BINDING:
                    RemotingResourceDefinition.SOCKET_BINDING.parseAndSetParameter(value, remotginAddOperation, xmlExtendedStreamReader);
                    break;
                case UNKNOWN:
                default: {
                    throw unexpectedAttribute(xmlExtendedStreamReader, i);
                }
            }
        }
        requireNoContent(xmlExtendedStreamReader);
    }
}
