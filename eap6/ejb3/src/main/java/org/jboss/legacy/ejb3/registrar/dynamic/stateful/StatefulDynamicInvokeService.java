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

package org.jboss.legacy.ejb3.registrar.dynamic.stateful;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.jboss.as.ee.component.Component;
import org.jboss.as.ee.component.EEModuleDescription;
import org.jboss.as.ejb3.component.EJBComponentDescription;
import org.jboss.as.ejb3.component.stateful.StatefulSessionComponent;
import org.jboss.ejb.client.SessionID;
import org.jboss.invocation.InterceptorContext;
import org.jboss.legacy.common.ExtendedEJBDataProxy;
import org.jboss.legacy.ejb3.registrar.dynamic.AbstractDynamicInvocationService;
import org.jboss.legacy.spi.ejb3.dynamic.DynamicInvocationProxy;
import org.jboss.legacy.spi.ejb3.dynamic.stateful.StatefulDynamicInvocationProxy;
import org.jboss.legacy.spi.ejb3.dynamic.stateful.StatefulDynamicInvocationTarget;
import org.jboss.msc.service.Service;
import org.jboss.msc.value.InjectedValue;

/**
 * @author baranowb
 */
public class StatefulDynamicInvokeService extends AbstractDynamicInvocationService implements StatefulDynamicInvocationTarget,
        Service<StatefulDynamicInvocationTarget> {

    private final InjectedValue<Component> componentCreateServiceInjectedValue = new InjectedValue<Component>();

    public StatefulDynamicInvokeService(final ExtendedEJBDataProxy ejb3Data, final EEModuleDescription moduleDescription,
            final EJBComponentDescription ejbComponentDescription) {
        super(ejb3Data, moduleDescription, ejbComponentDescription);
    }

    @Override
    public StatefulDynamicInvocationTarget getValue() throws IllegalStateException, IllegalArgumentException {
        // context.putPrivateData(SessionID.class, SessionID.createSessionID((byte[])sessionID));
        return this;
    }

    @Override
    public Object invoke(Method method, Object[] arguments, Object sessionID) throws Exception{
        InterceptorContext ic = createInterceptorContext(method, arguments);
        ic.putPrivateData(SessionID.class, SessionID.createSessionID((byte[])sessionID));
        return viewInjectedValue.getValue().invoke(ic);
    }

    @Override
    public Serializable createSession() {
        StatefulSessionComponent component = (StatefulSessionComponent) componentCreateServiceInjectedValue
              .getValue();
      return component.createSession().getEncodedForm();
    }

    @Override
    protected DynamicInvocationProxy createInvocationProxy() {
        StatefulDynamicInvocationProxy value = new StatefulDynamicInvocationProxy();
        value.setDynamicInvocationTarget(this);
        value.setEjb3Data(ejb3Data);
        value.setEjb3RegistrarProxy(super.ejb3RegistrarInjectedValue.getValue());
        return value;
    }

    public InjectedValue<Component> getComponentCreateInjectedValue() {
        return componentCreateServiceInjectedValue;
    }
//    @Override
//    protected void createLegacyBinding() throws NamingException {
//        super.createLegacyBinding();
//        final EJB3Registrar value = this.ejb3RegistrarInjectedValue.getValue();
//        final JBossSessionBeanMetaData metaData = createMetaData(ejb3Data);
//        final ClassLoader old = switchLoader(getClass().getClassLoader());
//        try {
//
//            // this is wrong
//            value.getRegistrar().bind(metaData.getJndiName().replace("/remote", ""), new StatefulSessionFactory() {
//                @Override
//                public Serializable createSession() {
//                    try {
//                        StatefulSessionComponent component = (StatefulSessionComponent) componentCreateServiceInjectedValue
//                                .getValue();
//                        return component.createSession().getEncodedForm();
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                Thread.currentThread().setContextClassLoader(old);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    protected void removeLegacyBinding() throws NamingException {
//        super.removeLegacyBinding();
//        final EJB3Registrar value = this.ejb3RegistrarInjectedValue.getValue();
//        final JBossSessionBeanMetaData metaData = createMetaData(ejb3Data);
//        final ClassLoader old = switchLoader(getClass().getClassLoader());
//        try {
//            value.getRegistrar().unbind(metaData.getJndiName().replace("/remote", ""));
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                Thread.currentThread().setContextClassLoader(old);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private ClassLoader switchLoader(final ClassLoader loader) {
//        ClassLoader current = Thread.currentThread().getContextClassLoader();
//        Thread.currentThread().setContextClassLoader(loader);
//        return current;
//    }
//
//    private JndiSessionRegistrarBase getJndiSessionRegistrarBase(final ExtendedEJBDataProxy data,
//            final EJB3Registrar registrarService) {
//        return data.isStateful() ? registrarService.getJndiStatefulSessionRegistrar() : registrarService
//                .getJndiStatelessSessionRegistrar();
//    }
}