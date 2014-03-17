/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import org.jboss.common.TransactionMandatoryRemote;
import org.jboss.tm.usertx.client.ClientUserTransaction;

/**
 *
 * @author <a href="mailto:ehugonne@redhat.com">Emmanuel Hugonnet</a> (c) 2013 Red Hat, inc.
 */
public class UserTxClient {

    private static final Logger log = Logger.getLogger(UserTxClient.class.getName());

    private static final String JNDI_CONFIG = "jndi-eap6.properties";
    private static final String USER_TRANSACTION = "UserTransaction";
    private static final String EJB_NAME = "TransactionMandatoryBean/remote-org.jboss.test.transactional.TransactionMandatoryRemote";

    private InitialContext getInitialContext() throws NamingException, IOException {
        Properties jndiProperties = new Properties();
        jndiProperties.load(this.getClass().getClassLoader().getResourceAsStream(System.getProperty("jndi_config", JNDI_CONFIG)));
        return new javax.naming.InitialContext(jndiProperties);
    }

    public void commitTxMandatoryEJB() throws Exception {
        InitialContext initialContext = getInitialContext();
        UserTransaction xact = (UserTransaction) initialContext.lookup(USER_TRANSACTION);
        try {
            System.out.println("*********************************************************************");
            System.out.println("******************************COMMIT*********************************");
            TransactionMandatoryRemote ejb = (TransactionMandatoryRemote) initialContext.lookup(EJB_NAME);
            checkClientTransactionStatus(xact);
            checkServerTransactionStatus(ejb);
            xact.begin();
            checkClientTransactionStatus(xact);
            checkServerTransactionStatus(ejb);
            ejb.mandatoryTxOp();
            xact.commit();
            checkClientTransactionStatus(xact);
            checkServerTransactionStatus(ejb);
            System.out.println("*********************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            log.severe(e.getMessage());
            throw e;
        } finally {
            initialContext.close();
        }
    }

    public void rollbackTxMandatoryEJB() throws Exception {
        InitialContext initialContext = getInitialContext();
        UserTransaction xact = (UserTransaction) initialContext.lookup(USER_TRANSACTION);
        try {
            System.out.println("*********************************************************************");
            System.out.println("**************************ROLLBACK***********************************");
            TransactionMandatoryRemote ejb = (TransactionMandatoryRemote) initialContext.lookup(EJB_NAME);
            checkClientTransactionStatus(xact);
            checkServerTransactionStatus(ejb);
            xact.begin();
            checkClientTransactionStatus(xact);
            checkServerTransactionStatus(ejb);
            ejb.mandatoryTxOp();
            xact.setRollbackOnly();
            checkClientTransactionStatus(xact);
            checkServerTransactionStatus(ejb);
            xact.rollback();
            checkClientTransactionStatus(xact);
            checkServerTransactionStatus(ejb);            
            System.out.println("*********************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            log.severe(e.getMessage());
            throw e;
        } finally {
            initialContext.close();
        }
    }

    private void checkServerTransactionStatus(TransactionMandatoryRemote ejb) {
        try {
            System.out.println("We have a transaction on the server " + ejb.currentTransaction());
        } catch (javax.ejb.EJBTransactionRequiredException ex) {
            System.out.println("We have NO transaction on the server");
        }
    }
    
    private void checkClientTransactionStatus(UserTransaction xact) throws SystemException {
          System.out.println("We have local transaction " +  ((ClientUserTransaction)xact).getTransactionPropagationContext() 
                  + " " + getTransactionStatus(xact.getStatus()));

    }

    public String getTransactionStatus(int status) {
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
