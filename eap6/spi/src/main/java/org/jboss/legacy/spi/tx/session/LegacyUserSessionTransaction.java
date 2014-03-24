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
package org.jboss.legacy.spi.tx.session;

import java.rmi.RemoteException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.jboss.legacy.spi.common.LegacyBean;
import org.jboss.tm.usertx.interfaces.UserTransactionSession;
import org.jboss.tm.usertx.server.UserTransactionSessionImpl;

/**
 * 
 * @author <a href="mailto:ehugonne@redhat.com">Emmanuel Hugonnet</a> (c) 2013 Red Hat, inc.
 */
public class LegacyUserSessionTransaction implements UserTransactionSession {

    @Override
    public void destroy() throws RemoteException {
        /*
         * We do nothing as the tx will timeout and the tx map is shared across all sessions as we have no association with the
         * txs a given client has started.
         */
    }

    @Override
    public Object begin(int timeout) throws RemoteException, NotSupportedException, SystemException {
        UserTransactionSession session = UserTransactionSessionImpl.getInstance();
        return session.begin(timeout);
    }

    @Override
    public void commit(Object tpc) throws RemoteException, RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        UserTransactionSession session = UserTransactionSessionImpl.getInstance();
        session.commit(tpc);
    }

    @Override
    public void rollback(Object tpc) throws RemoteException, SecurityException, IllegalStateException, SystemException {
        UserTransactionSession session = UserTransactionSessionImpl.getInstance();
        session.rollback(tpc);
    }

    @Override
    public void setRollbackOnly(Object tpc) throws RemoteException, IllegalStateException, SystemException {
        UserTransactionSession session = UserTransactionSessionImpl.getInstance();
        session.setRollbackOnly(tpc);
    }

    @Override
    public int getStatus(Object tpc) throws RemoteException, SystemException {
        UserTransactionSession session = UserTransactionSessionImpl.getInstance();
        return session.getStatus(tpc);
    }

}
