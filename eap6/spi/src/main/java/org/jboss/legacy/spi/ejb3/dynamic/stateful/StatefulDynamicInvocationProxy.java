/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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

package org.jboss.legacy.spi.ejb3.dynamic.stateful;

import java.io.Serializable;

import org.jboss.ejb3.proxy.spi.container.InvokableContext;
import org.jboss.ejb3.proxy.spi.container.StatefulSessionFactory;
import org.jboss.legacy.spi.ejb3.dynamic.DynamicInvocationProxy;

/**
 * @author baranowb
 * 
 */
public class StatefulDynamicInvocationProxy extends DynamicInvocationProxy {

    /**
     * @param containerName
     */
    public StatefulDynamicInvocationProxy() {
        super();
    }

    protected StatefulSessionFactory sessionFactory;

    protected StatefulSessionFactory getSessionFactory() {
        if (this.sessionFactory == null) {
            this.sessionFactory = new StatefulSessionFactory() {
                @Override
                public Serializable createSession() {
                    return ((StatefulDynamicInvocationTarget) dynamicInvocationTarget).createSession();
                }
            };
        }
        return this.sessionFactory;
    }

    @Override
    protected void internalStart() throws Exception {
        super.internalStart();
        getEjb3RegistrarProxy().getRegistrar().bind(getMetadata().getJndiName().replace("/remote", ""), getSessionFactory());
    }

    @Override
    protected void internalStop() throws Exception {
        getEjb3RegistrarProxy().getRegistrar().unbind(getMetadata().getJndiName().replace("/remote", ""));
        super.internalStop();
    }

    @Override
    protected InvokableContext createInvokableContext() {
        return new StatefulDynamicInvokableContext(this);
    }

}
