/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.legacy.spi.ejb3.dynamic;

import org.jboss.aop.InstanceAdvisor;
import org.jboss.aop.joinpoint.Invocation;
import org.jboss.aop.joinpoint.InvocationResponse;
import org.jboss.aop.proxy.ClassProxy;
import org.jboss.aop.proxy.ProxyMixin;
import org.jboss.ejb3.proxy.spi.container.InvokableContext;

/**
 * Exposes InvokableContext.dynamicInvoke to Remoting
 * <p/>
 * org.jboss.aop.Dispatcher can handle only certain types.
 * 
 * Copy of the one in Core org.jboss.ejb3.session.InvokableContextClassProxyHack.
 * 
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class InvokableContextClassProxyHack implements ClassProxy {
    // TODO: check if this can be removed.
    private InvokableContext container;

    public InvokableContextClassProxyHack(InvokableContext container) {
        assert container != null : "container is null";

        this.container = container;
    }

    public InvocationResponse _dynamicInvoke(Invocation invocation) throws Throwable {
        return container.dynamicInvoke(invocation);
    }

    public void setMixins(ProxyMixin[] mixins) {
        throw new UnsupportedOperationException();
    }

    public InstanceAdvisor _getInstanceAdvisor() {
        throw new UnsupportedOperationException();
    }

    public void _setInstanceAdvisor(InstanceAdvisor newAdvisor) {
        throw new UnsupportedOperationException();
    }
}
