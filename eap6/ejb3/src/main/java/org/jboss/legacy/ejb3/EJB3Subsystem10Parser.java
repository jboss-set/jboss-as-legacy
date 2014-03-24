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
package org.jboss.legacy.ejb3;

import java.util.EnumSet;
import java.util.List;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;
import org.jboss.as.controller.operations.common.Util;
import static org.jboss.as.controller.parsing.ParseUtils.requireNoAttributes;
import static org.jboss.as.controller.parsing.ParseUtils.requireNoContent;
import static org.jboss.as.controller.parsing.ParseUtils.unexpectedElement;
import org.jboss.dmr.ModelNode;
import org.jboss.legacy.ejb3.registrar.EJB3RegistrarModel;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLExtendedStreamReader;

/**
 */
public class EJB3Subsystem10Parser implements XMLElementReader<List<ModelNode>> {

    public static final EJB3Subsystem10Parser INSTANCE = new EJB3Subsystem10Parser();

    protected EJB3Subsystem10Parser() {
    }

    @Override
    public void readElement(final XMLExtendedStreamReader xmlExtendedStreamReader, final List<ModelNode> result)
            throws XMLStreamException {
        // no attributes
        requireNoAttributes(xmlExtendedStreamReader);
        final ModelNode ejb3SubsystemAddOperation = Util.createAddOperation();
        ejb3SubsystemAddOperation.get(OP_ADDR).add(SUBSYSTEM, EJB3Extension.SUBSYSTEM_NAME);
        result.add(ejb3SubsystemAddOperation);

        final ModelNode ejb3RegistrarServerServiceAddOperation = Util.createAddOperation();
        ejb3RegistrarServerServiceAddOperation.get(OP_ADDR).add(SUBSYSTEM, EJB3Extension.SUBSYSTEM_NAME)
                .add(EJB3RegistrarModel.SERVICE, EJB3RegistrarModel.SERVICE_NAME);
        result.add(ejb3RegistrarServerServiceAddOperation);

        // elements
        final EnumSet<EJB3SubsystemXMLElement> encountered = EnumSet.noneOf(EJB3SubsystemXMLElement.class);
        while (xmlExtendedStreamReader.hasNext() && xmlExtendedStreamReader.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (EJB3SubsystemNamespace.forUri(xmlExtendedStreamReader.getNamespaceURI()) != EJB3SubsystemNamespace.LEGACY_EJB3_1_0) {
                throw unexpectedElement(xmlExtendedStreamReader);
            }
            final EJB3SubsystemXMLElement element = EJB3SubsystemXMLElement.forName(xmlExtendedStreamReader
                    .getLocalName());
            if (!encountered.add(element)) {
                throw unexpectedElement(xmlExtendedStreamReader);
            }
            switch (element) {
                case EJB3_REGISTRAR:
                    this.parseRegistrar(xmlExtendedStreamReader);
                    break;
                case UNKNOWN:
                default:
                    throw unexpectedElement(xmlExtendedStreamReader);

            }

        }

    }

    private void parseRegistrar(XMLExtendedStreamReader xmlExtendedStreamReader) throws XMLStreamException {
        requireNoAttributes(xmlExtendedStreamReader);
        requireNoContent(xmlExtendedStreamReader);
    }
}
