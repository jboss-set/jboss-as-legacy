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
package org.jboss.legacy.tx.usertx;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.CompoundName;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.jboss.aop.advice.Interceptor;
import org.jboss.aop.proxy.Proxy;
import org.jboss.aspects.remoting.InvokeRemoteInterceptor;
import org.jboss.aspects.remoting.MergeMetaDataInterceptor;
import org.jboss.aspects.remoting.RemotingProxyFactory;

import org.jboss.legacy.spi.connector.ConnectorProxy;
import org.jboss.legacy.spi.tx.session.UserSessionTransactionProxy;
import org.jboss.legacy.spi.tx.user.ClientUserTransactionProxy;
import org.jboss.legacy.tx.TXLogger;
import org.jboss.legacy.tx.TXMessages;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;


/**
 *
 * @author <a href="mailto:ehugonne@redhat.com">Emmanuel Hugonnet</a> (c) 2013 Red Hat, inc.
 */
public class ClientUserTransactionService implements Service<ClientUserTransactionProxy> {

    public static final ServiceName SERVICE_NAME = ServiceName.JBOSS.append(ClientUserTransactionModel.LEGACY).append(ClientUserTransactionModel.SERVICE_NAME);

    private ClientUserTransactionProxy service;
    private final InjectedValue<ConnectorProxy> injectedConnector = new InjectedValue<ConnectorProxy>();
        private final InjectedValue<UserSessionTransactionProxy> injectedUserSessionTransactionProxyFactory = new InjectedValue<UserSessionTransactionProxy>();

    public InjectedValue<ConnectorProxy> getInjectedConnector() {
        return injectedConnector;
    }

    public InjectedValue<UserSessionTransactionProxy> getInjectedUserSessionTransactionProxyFactory() {
        return injectedUserSessionTransactionProxyFactory;
    }

    @Override
    public void start(StartContext context) throws StartException {
        TXLogger.ROOT_LOGGER.startClientUserTransactionService();
        try {
            this.service = new ClientUserTransactionProxy();
            this.service.setConnector(this.injectedConnector.getValue());
            this.service.setUserSessionTransactionProxy(this.injectedUserSessionTransactionProxyFactory.getValue());
            this.service.start();
        } catch (Exception ex) {
            throw TXMessages.MESSAGES.failedToStartClientUserTransactionService(ex);
        }
    }

    @Override
    public void stop(StopContext context) {
        TXLogger.ROOT_LOGGER.stopClientUserTransactionService();
        try {
            this.service.stop();
        } catch (Exception ex) {
            TXLogger.ROOT_LOGGER.failedToStopClientUserTransactionService(ex);
        }
    }

    @Override
    public ClientUserTransactionProxy getValue() throws IllegalStateException, IllegalArgumentException {
        return service;
    }
}
