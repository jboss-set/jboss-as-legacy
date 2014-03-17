/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
 *//*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.ejb.transactional;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.Status;
import javax.transaction.TransactionSynchronizationRegistry;
import org.jboss.common.TransactionMandatoryRemote;

/**
 *
 * @author <a href="mailto:ehugonne@redhat.com">Emmanuel Hugonnet</a> (c) 2013 Red Hat, inc.
 */
@Stateless
@Remote(TransactionMandatoryRemote.class)
@TransactionAttribute(value = TransactionAttributeType.MANDATORY)
public class TransactionMandatoryBean implements TransactionMandatoryRemote {

    @Resource(mappedName = "java:comp/TransactionSynchronizationRegistry")
    TransactionSynchronizationRegistry txReg;

    @Override
    public void mandatoryTxOp() {
        System.out.println("Hey, I got my transaction " + currentTransaction());
    }

    @Override
    public String currentTransaction() {
       return txReg.getTransactionKey() + " " + getTransactionStatus(txReg.getTransactionStatus());
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
