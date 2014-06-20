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

package org.jboss.legacy.common;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jboss.as.ee.component.EEModuleDescription;
import org.jboss.as.ejb3.component.EJBComponentDescription;
import org.jboss.as.server.deployment.AttachmentKey;
import org.jboss.msc.service.ServiceName;

/**
 * Simple map like object with additional method to calculate keys. This class is used to store ejb-service->EAP5-data-proxy
 * values in deploy context and information about last legacy service processed.
 * 
 * @author baranowb
 */
public class DeploymentEJBDataProxyMap extends HashMap<ServiceName, ExtendedEJBDataProxy> {
    public static final ServiceName SERVICE_NAME_BASE = ServiceName.of("jboss", "legacy");
    public static final AttachmentKey<DeploymentEJBDataProxyMap> ATTACHMENT_KEY = AttachmentKey
            .create(DeploymentEJBDataProxyMap.class);

    public static ServiceName getServiceName(final EEModuleDescription moduleDescription,
            final EJBComponentDescription ejbComponentDescription) {
        // TODO: what about ear/war/jar/ejb ?
        if (moduleDescription.getEarApplicationName() == null) {
            return SERVICE_NAME_BASE.of(SERVICE_NAME_BASE, 
                    ejbComponentDescription.getComponentName());
        } else {
            return SERVICE_NAME_BASE.of(SERVICE_NAME_BASE, moduleDescription.getEarApplicationName(), ejbComponentDescription.getComponentName());
        }
    }

    @Override
    public ExtendedEJBDataProxy put(ServiceName key, ExtendedEJBDataProxy value) {
        if(super.containsKey(key)){
            throw EJB3Messages.MESSAGES.ejbNameCollision(key);
        }
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends ServiceName, ? extends ExtendedEJBDataProxy> m) {
        Set<ServiceName> intersection = intersection(m.keySet());
        if(intersection.size() > 0){
            throw EJB3Messages.MESSAGES.multipleEjbNameCollision(intersection); 
        }
        super.putAll(m);
    }

    private Set<ServiceName> intersection(Set<? extends ServiceName> otherSet){
        TreeSet<ServiceName> tree = new TreeSet<ServiceName>(new Comparator<ServiceName>() {

            @Override
            public int compare(ServiceName o1, ServiceName o2) {
                if(o1 == null && o2 == null)
                    return 0;
                if(o1 !=null && o2 == null)
                    return 1;
                if(o1== null && o2 != null)
                    return -1;
                return o1.getCanonicalName().compareTo(o2.getCanonicalName());
            }
        });
        tree.addAll(this.keySet());
        tree.retainAll(otherSet);
        return tree;
    }
    private ServiceName processTail;

    /**
     * Return last processed service name.
     * @return the processTail
     */
    public ServiceName getProcessTail() {
        return processTail;
    }

    /**
     * @param processTail the processTail to set
     */
    public void setProcessTail(ServiceName processTail) {
        this.processTail = processTail;
    }

}
