/*
 * Copyright (C) 2014 Red Hat, inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.jboss.client;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import org.jboss.common.TransactionMandatoryRemote;
import org.jboss.tm.usertx.client.ClientUserTransaction;
import static org.junit.Assert.assertThat;

/**
 *
 * @author <a href="mailto:ehugonne@redhat.com">Emmanuel Hugonnet</a> (c) 2013 Red Hat, inc.
 */
public class UserTransactionChecker {

    private static final Logger log = Logger.getLogger(UserTransactionChecker.class.getName());

    private static final String JNDI_CONFIG = "jndi-eap6.properties";
    private static final String USER_TRANSACTION = "UserTransaction";
    private static final String EJB_NAME = "TransactionMandatoryBean/remote-org.jboss.common.TransactionMandatoryRemote";

    private InitialContext getInitialContext() throws NamingException, IOException {
        Properties jndiProperties = new Properties();
        jndiProperties.load(this.getClass().getClassLoader().getResourceAsStream(System.getProperty("jndi_config", JNDI_CONFIG)));
        return new javax.naming.InitialContext(jndiProperties);
    }

    public void commitTxMandatoryEJB() throws Exception {
        InitialContext initialContext = null;
        try {
            initialContext = getInitialContext();
            UserTransaction xact = (UserTransaction) initialContext.lookup(USER_TRANSACTION);
            System.out.println("******************************COMMIT*********************************");
            TransactionMandatoryRemote ejb = (TransactionMandatoryRemote) initialContext.lookup(EJB_NAME);
            checkClientTransactionStatus(xact, false, "NO_TRANSACTION");
            checkServerTransactionStatus(ejb, false, "NO_TRANSACTION");
            xact.begin();
            checkClientTransactionStatus(xact, true, "ACTIVE");
            checkServerTransactionStatus(ejb, true, "ACTIVE");
            ejb.mandatoryTxOp();
            xact.commit();
            checkClientTransactionStatus(xact, false, "NO_TRANSACTION");
            checkServerTransactionStatus(ejb, false, "NO_TRANSACTION");
            System.out.println("*********************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            log.severe(e.getMessage());
            throw e;
        } finally {
            if (initialContext != null) {
                initialContext.close();
            }
        }
    }

    public void rollbackTxMandatoryEJB() throws Exception {
        InitialContext initialContext = null;
        try {
            initialContext = getInitialContext();
            UserTransaction xact = (UserTransaction) initialContext.lookup(USER_TRANSACTION);
            System.out.println("**************************ROLLBACK***********************************");
            TransactionMandatoryRemote ejb = (TransactionMandatoryRemote) initialContext.lookup(EJB_NAME);
            checkClientTransactionStatus(xact, false, "NO_TRANSACTION");
            checkServerTransactionStatus(ejb, false, "NO_TRANSACTION");
            xact.begin();
            checkClientTransactionStatus(xact, true, "ACTIVE");
            checkServerTransactionStatus(ejb, true, "ACTIVE");
            ejb.mandatoryTxOp();
            xact.setRollbackOnly();
            checkClientTransactionStatus(xact, true, "MARKED_ROLLBACK");
            checkServerTransactionStatus(ejb, true, "MARKED_ROLLBACK");
            xact.rollback();
            checkClientTransactionStatus(xact, false, "NO_TRANSACTION");
            checkServerTransactionStatus(ejb, false, "NO_TRANSACTION");
            System.out.println("*********************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            log.severe(e.getMessage());
            throw e;
        } finally {
            if (initialContext != null) {
                initialContext.close();
            }
        }
    }

    private void checkServerTransactionStatus(TransactionMandatoryRemote ejb, boolean hasContext, String expectedStatus) {
        try {
            System.out.println("We have a transaction on the server " + ejb.currentTransaction());
        } catch (javax.ejb.EJBTransactionRequiredException ex) {
            System.out.println("We have NO transaction on the server");
        }
        String transaction =  ejb.currentTransaction();
        int index = transaction.indexOf(' ');
        String transactionId = transaction.substring(0, transaction.indexOf(' '));
        String status = transaction.substring(index +1);
         if (hasContext) {
            assertThat(transactionId, is(not("null")));
        } else {
            assertThat(transactionId, is("null"));
        }
        assertThat(status, is(expectedStatus));
    }

    private void checkClientTransactionStatus(UserTransaction xact, boolean hasContext, String expectedStatus) throws SystemException {
        System.out.println("We have local transaction " + ((ClientUserTransaction) xact).getTransactionPropagationContext()
                + " " + getTransactionStatus(xact.getStatus()));
        if (hasContext) {
            assertThat(((ClientUserTransaction) xact).getTransactionPropagationContext(), is(notNullValue()));
        } else {
            assertThat(((ClientUserTransaction) xact).getTransactionPropagationContext(), is(nullValue()));
        }
        assertThat(getTransactionStatus(xact.getStatus()), is(expectedStatus));

    }

    private String getTransactionStatus(int status) {
        switch (status) {
            case Status.STATUS_ACTIVE:
                return "ACTIVE";
            case Status.STATUS_COMMITTED:
                return "COMMITTED";
            case Status.STATUS_COMMITTING:
                return "COMMITTING";
            case Status.STATUS_MARKED_ROLLBACK:
                return "MARKED_ROLLBACK";
            case Status.STATUS_NO_TRANSACTION:
                return "NO_TRANSACTION";
            case Status.STATUS_PREPARED:
                return "PREPARED";
            case Status.STATUS_PREPARING:
                return "PREPARING";
            case Status.STATUS_ROLLEDBACK:
                return "ROLLEDBACK";
            case Status.STATUS_ROLLING_BACK:
                return "ROLLING_BACK";
            case Status.STATUS_UNKNOWN:
            default:
                return "UNKOWN";
        }
    }
}
