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

package org.jboss.legacy.spi.ejb3.dynamic;

import javax.security.auth.Subject;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

/**
 * This interface defines methods that can be called from SPI in AS7 space. SPI is responsible for setting up proper CL
 * 
 * @author baranowb
 * 
 */
public interface DynamicInvocationTarget {

    // TODO: check if Subject must be removed after call
    void setupSecurity(String securityDomain, String string, char[] cs, Subject subject);

    TransactionManager getTransactionManager();
    
    Transaction importTransaction(String id);
    // void setupTrasaction;

}
