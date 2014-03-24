/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

import java.util.HashMap;
import java.util.Map;

/**
 * An enumeration of the supported EJB3 Bridge subsystem namespaces
 *
 * @author baranowb
 */
public enum EJB3BridgeSubsystemNamespace {
    // must be first
    UNKNOWN(null),

    LEGACY_EJB3_BRIDGE_1_0("urn:jboss:domain:legacy-ejb3-bridge:1.0");


    private final String name;

    EJB3BridgeSubsystemNamespace(final String name) {
        this.name = name;
    }

    /**
     * Get the URI of this namespace.
     *
     * @return the URI
     */
    public String getUriString() {
        return name;
    }

    private static final Map<String, EJB3BridgeSubsystemNamespace> MAP;

    static {
        final Map<String, EJB3BridgeSubsystemNamespace> map = new HashMap<String, EJB3BridgeSubsystemNamespace>();
        for (EJB3BridgeSubsystemNamespace namespace : values()) {
            final String name = namespace.getUriString();
            if (name != null) map.put(name, namespace);
        }
        MAP = map;
    }

    public static EJB3BridgeSubsystemNamespace forUri(String uri) {
        final EJB3BridgeSubsystemNamespace element = MAP.get(uri);
        return element == null ? UNKNOWN : element;
    }

}
