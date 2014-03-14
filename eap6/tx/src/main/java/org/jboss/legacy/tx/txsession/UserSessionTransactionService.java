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
package org.jboss.legacy.tx.txsession;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.transaction.TransactionManager;

import org.jboss.legacy.spi.connector.ConnectorProxy;
import org.jboss.legacy.spi.tx.session.UserSessionTransactionProxy;
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
public class UserSessionTransactionService implements Service<UserSessionTransactionProxy> {

    public static final ServiceName SERVICE_NAME = ServiceName.JBOSS.append(UserSessionTransactionModel.LEGACY).append(UserSessionTransactionModel.SERVICE_NAME);

    private UserSessionTransactionProxy service;
    private final InjectedValue<ConnectorProxy> injectedConnector = new InjectedValue<ConnectorProxy>();

    private final InjectedValue<TransactionManager> injectedTransactionManager = new InjectedValue<TransactionManager>();

    public InjectedValue<ConnectorProxy> getInjectedConnector() {
        return injectedConnector;
    }


    public InjectedValue<TransactionManager> getInjectedTransactionManager() {
        return injectedTransactionManager;
    }

    @Override
    public void start(StartContext context) throws StartException {

        try {
            service = new UserSessionTransactionProxy();
            service.setConnector(this.injectedConnector.getValue());
            service.start();
        } catch (Exception ex) {
            throw new StartException(ex);
        }
    }

    @Override
    public void stop(StopContext context) {
        try {
            service.stop();
        } catch (Exception ex) {
            Logger.getLogger(UserSessionTransactionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public UserSessionTransactionProxy getValue() throws IllegalStateException, IllegalArgumentException {
        return service;
    }

}
