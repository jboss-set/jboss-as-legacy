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
package org.jboss.legacy.connector;

import javax.xml.stream.XMLStreamException;

import org.jboss.as.controller.persistence.SubsystemMarshallingContext;
import org.jboss.dmr.ModelNode;
import org.jboss.legacy.connector.remoting.RemotingModel;
import org.jboss.legacy.connector.remoting.RemotingResourceDefinition;
import org.jboss.staxmapper.XMLElementWriter;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

/**
 * @author baranowb
 */
public class ConnectorSubsystemXMLPersister implements XMLElementWriter<SubsystemMarshallingContext> {

    public static final ConnectorSubsystemXMLPersister INSTANCE = new ConnectorSubsystemXMLPersister();

    @Override
    public void writeContent(XMLExtendedStreamWriter xmlExtendedStreamWriter,
            SubsystemMarshallingContext subsystemMarshallingContext) throws XMLStreamException {
        subsystemMarshallingContext.startSubsystemElement(ConnectorSubsystemNamespace.LEGACY_CONNECTOR_1_0.getUriString(),
                false);

        writeElements(xmlExtendedStreamWriter, subsystemMarshallingContext);

        // write the subsystem end element
        xmlExtendedStreamWriter.writeEndElement();
    }

    private void writeElements(XMLExtendedStreamWriter xmlExtendedStreamWriter,
            SubsystemMarshallingContext subsystemMarshallingContext) throws XMLStreamException {
        if (subsystemMarshallingContext.getModelNode().hasDefined(RemotingModel.SERVICE)) {
            final ModelNode model = subsystemMarshallingContext.getModelNode().get(RemotingModel.SERVICE);

            if (model.hasDefined(RemotingModel.SERVICE_NAME)) {
                writeRemotingRegistrar(xmlExtendedStreamWriter, subsystemMarshallingContext);
            }
        }
    }

    private void writeRemotingRegistrar(XMLExtendedStreamWriter xmlExtendedStreamWriter,
            SubsystemMarshallingContext subsystemMarshallingContext) throws XMLStreamException {
        final ModelNode model = subsystemMarshallingContext.getModelNode().get(RemotingModel.SERVICE)
                .get(RemotingModel.SERVICE_NAME);
        xmlExtendedStreamWriter.writeStartElement(ConnectorSubsystemXMLElement.REMOTING.getLocalName());
        if (model.hasDefined(RemotingModel.SOCKET_BINDING)) {
            RemotingResourceDefinition.SOCKET_BINDING.marshallAsAttribute(model, true, xmlExtendedStreamWriter);
        }
        xmlExtendedStreamWriter.writeEndElement();
    }

}
