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

package org.jboss.legacy.spi.tx.user;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Properties;

import javax.naming.CompoundName;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.jboss.aspects.remoting.RemotingProxyFactory;
import org.jboss.legacy.spi.common.LegacyBean;
import org.jboss.legacy.spi.connector.ConnectorProxy;
import org.jboss.legacy.spi.tx.session.UserSessionTransactionProxy;
import org.jboss.aop.advice.Interceptor;
import org.jboss.aop.proxy.Proxy;
import org.jboss.aspects.remoting.InvokeRemoteInterceptor;
import org.jboss.aspects.remoting.MergeMetaDataInterceptor;
import org.jboss.tm.usertx.client.ClientUserTransaction;
import org.jboss.tm.usertx.interfaces.UserTransactionSessionFactory;

/**
 * @author baranowb
 * @author <a href="mailto:ehugonne@redhat.com">Emmanuel Hugonnet</a> (c) 2013 Red Hat, inc.
 */
public class ClientUserTransactionProxy extends LegacyBean {
    private static final String JNDI_UT_SESSION_FACTORY = "UserTransactionSessionFactory";
    private static final String JNDI_UT = "UserTransaction";
    private static final String JNDI_JAVA_CONTEXT = "java:";
    private static final String JNDI_JBOSS_CONTEXT = "java:jboss";
    private static final String JNDI_COMP_CONTEXT = "java:comp";
    private ConnectorProxy connector;
    private UserSessionTransactionProxy userSessionTransactionProxy;

    private RemotingProxyFactory remotingProxyFactory;

    public ConnectorProxy getConnector() {
        return connector;
    }

    public void setConnector(ConnectorProxy connector) {
        this.connector = connector;
    }

    public UserSessionTransactionProxy getUserSessionTransactionProxy() {
        return userSessionTransactionProxy;
    }

    public void setUserSessionTransactionProxy(UserSessionTransactionProxy userSessionTransactionProxy) {
        this.userSessionTransactionProxy = userSessionTransactionProxy;
    }

    public RemotingProxyFactory getRemotingProxyFactory() {
        return remotingProxyFactory;
    }

    @Override
    protected void internalStart() throws Exception {
        if (this.connector == null || this.connector.getConnector() == null) {
            throw new IllegalArgumentException("Connector not found: " + this.connector);
        }

        if (this.userSessionTransactionProxy == null || this.userSessionTransactionProxy.getRemotingProxyFactory() == null) {
            throw new IllegalArgumentException("User Session Proxy not found: " + this.connector);
        }

        this.remotingProxyFactory = new RemotingProxyFactory();
        final InitialContext ctx = createJNPLocalContext();
        try {
            // TODO check if this hacks can now be removed
            createSubcontext(ctx, JNDI_JAVA_CONTEXT);
            createSubcontext(ctx, JNDI_JBOSS_CONTEXT);
            createSubcontext(ctx, JNDI_COMP_CONTEXT);
            bindGlobally(ctx, JNDI_UT, ClientUserTransaction.getSingleton().getReference()/*
                                                                                           * ,
                                                                                           * ClientUserTransaction.class.getName
                                                                                           * ()
                                                                                           */);
            this.remotingProxyFactory.setConnector(this.connector.getConnector());
            this.remotingProxyFactory.setInvokerLocator(this.connector.getConnector().getInvokerLocator());
            ArrayList<Interceptor> proxyInterceptors = new ArrayList<Interceptor>(2);
            proxyInterceptors.add(MergeMetaDataInterceptor.singleton);
            proxyInterceptors.add(InvokeRemoteInterceptor.singleton);
            this.remotingProxyFactory.setInterceptors(proxyInterceptors);
            this.remotingProxyFactory.setTarget(new LegacyUserTransactionSessionFactory(this.userSessionTransactionProxy
                    .getRemotingProxyFactory()));
            this.remotingProxyFactory.setInterfaces(new Class<?>[] { UserTransactionSessionFactory.class });
            this.remotingProxyFactory.setDispatchName("UserTransactionSessionFactory");
            this.remotingProxyFactory.start();
            Proxy ut = this.remotingProxyFactory.getProxy();
            bindGlobally(ctx, JNDI_UT_SESSION_FACTORY, ut/* , UserTransactionSessionFactory.class.getName() */);
        } finally {
            ctx.close();
        }
    }

    @Override
    protected void internalStop() throws Exception {
        // TODO Auto-generated method stub

    }

    private void createSubcontext(final InitialContext ctx, final String name) throws NamingException, RemoteException {
        final Name compoundName = new CompoundName(name, new Properties());
        try {
            if (ctx.lookup(compoundName) != null) {
                return;
            }
            ctx.createSubcontext(compoundName);
        } catch (NameNotFoundException ex) {
            ctx.createSubcontext(compoundName);
        }
    }

    private void bindGlobally(final InitialContext ctx, final String name, final Object object) throws NamingException,
            RemoteException {
        bind(ctx, JNDI_JBOSS_CONTEXT + '/' + name, object);
        bind(ctx, JNDI_COMP_CONTEXT + '/' + name, object);
        bind(ctx, name, object);
    }

    private void bind(final InitialContext ctx, final String name, final Object object) throws NamingException, RemoteException {
        Name compoundName = new CompoundName(name, new Properties());
        try {
            if (ctx.lookup(compoundName) != null) {
                return;
            }
            ctx.bind(compoundName, object);
        } catch (NameNotFoundException ex) {
            ctx.bind(compoundName, object);
        }
    }
}
