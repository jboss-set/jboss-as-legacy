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

package org.jboss.legacy.spi.tx.session;

import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.Reference;

import org.jboss.aop.advice.Interceptor;
import org.jboss.aspects.remoting.InvokeRemoteInterceptor;
import org.jboss.aspects.remoting.MergeMetaDataInterceptor;
import org.jboss.aspects.remoting.RemotingProxyFactory;
import org.jboss.legacy.spi.common.LegacyBean;
import org.jboss.legacy.spi.connector.ConnectorProxy;
import org.jboss.tm.usertx.interfaces.UserTransactionSession;

import com.arjuna.ats.internal.jbossatx.jta.PropagationContextManager;

/**
 * @author baranowb
 * 
 */
public class UserSessionTransactionProxy extends LegacyBean {
    private static final String JNDI_IMPORTER = "java:/TransactionPropagationContextImporter";
    private static final String JNDI_EXPORTER = "java:/TransactionPropagationContextExporter";

    private ConnectorProxy connector;

    private RemotingProxyFactory remotingProxyFactory;

    public ConnectorProxy getConnector() {
        return connector;
    }

    public void setConnector(ConnectorProxy connector) {
        this.connector = connector;
    }

    public RemotingProxyFactory getRemotingProxyFactory() {
        return remotingProxyFactory;
    }

    @Override
    protected void internalStart() throws Exception {
        if (this.connector == null || this.connector.getConnector() == null) {
            throw new IllegalArgumentException("Connector not found: " + this.connector);
        }
        this.remotingProxyFactory = new RemotingProxyFactory();
        final InitialContext ctx = createJNPLocalContext();
        try {
            bindRef(ctx, JNDI_IMPORTER, PropagationContextManager.class.getName());
            bindRef(ctx, JNDI_EXPORTER, PropagationContextManager.class.getName());
        } finally {
            ctx.close();
        }
        this.remotingProxyFactory.setConnector(this.connector.getConnector());
        this.remotingProxyFactory.setInvokerLocator(this.connector.getConnector().getInvokerLocator());
        ArrayList<Interceptor> proxyInterceptors = new ArrayList<Interceptor>(2);
        proxyInterceptors.add(MergeMetaDataInterceptor.singleton);
        proxyInterceptors.add(InvokeRemoteInterceptor.singleton);
        this.remotingProxyFactory.setInterceptors(proxyInterceptors);
        this.remotingProxyFactory.setTarget(new LegacyUserSessionTransaction());
        this.remotingProxyFactory.setInterfaces(new Class<?>[] { UserTransactionSession.class });
        this.remotingProxyFactory.setDispatchName("UserTransactionSession");
        this.remotingProxyFactory.start();
    }

    @Override
    protected void internalStop() throws Exception {
        this.remotingProxyFactory.stop();
        final InitialContext ctx = createJNPLocalContext();
        try {
            unbindRef(ctx, JNDI_IMPORTER);
            unbindRef(ctx, JNDI_EXPORTER);
        } finally {
            ctx.close();
        }
    }

    private void bindRef(final InitialContext ctx, final String jndiName, final String className) throws Exception {
        Reference ref = new Reference(className, className, null);
        ctx.bind(jndiName, ref);
    }

    private void unbindRef(final InitialContext ctx, final String jndiName) throws Exception {
        ctx.unbind(jndiName);
    }
}
