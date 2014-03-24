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

package org.jboss.legacy.ejb3.registrar.dynamic.stateles;

import java.lang.reflect.Method;

import org.jboss.as.ee.component.EEModuleDescription;
import org.jboss.as.ejb3.component.EJBComponentDescription;
import org.jboss.invocation.InterceptorContext;
import org.jboss.legacy.common.ExtendedEJBDataProxy;
import org.jboss.legacy.ejb3.registrar.dynamic.AbstractDynamicInvocationService;
import org.jboss.legacy.spi.ejb3.dynamic.DynamicInvocationProxy;
import org.jboss.legacy.spi.ejb3.dynamic.stateles.StatelesDynamicInvocationProxy;
import org.jboss.legacy.spi.ejb3.dynamic.stateles.StatelesDynamicInvocationTarget;
import org.jboss.msc.service.Service;

/**
 * @author baranowb
 */
public class StatelesDynamicInvokeService extends AbstractDynamicInvocationService implements StatelesDynamicInvocationTarget,
        Service<StatelesDynamicInvocationTarget> {

    public StatelesDynamicInvokeService(final ExtendedEJBDataProxy ejb3Data, final EEModuleDescription moduleDescription,
            final EJBComponentDescription ejbComponentDescription) {
        super(ejb3Data, moduleDescription, ejbComponentDescription);
    }

    @Override
    public StatelesDynamicInvocationTarget getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }

    @Override
    public Object invoke(Method method, Object[] arguments) throws Exception {
        // TODO: check Exception propagation
        InterceptorContext ic = createInterceptorContext(method, arguments);
        return viewInjectedValue.getValue().invoke(ic);
    }

    @Override
    protected DynamicInvocationProxy createInvocationProxy() {
        StatelesDynamicInvocationProxy value = new StatelesDynamicInvocationProxy();
        value.setDynamicInvocationTarget(this);
        value.setEjb3Data(ejb3Data);
        value.setEjb3RegistrarProxy(super.ejb3RegistrarInjectedValue.getValue());
        return value;
    }

}