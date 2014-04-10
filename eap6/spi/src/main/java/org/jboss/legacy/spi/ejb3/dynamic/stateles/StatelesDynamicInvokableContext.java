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
package org.jboss.legacy.spi.ejb3.dynamic.stateles;

import static org.jboss.legacy.spi.common.LegacyBean.switchLoader;

import java.security.Principal;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.jboss.aop.joinpoint.Invocation;
import org.jboss.aop.joinpoint.InvocationResponse;
import org.jboss.aop.joinpoint.MethodInvocation;
import org.jboss.aspects.tx.ClientTxPropagationInterceptor;
import org.jboss.ejb3.common.lang.SerializableMethod;
import org.jboss.ejb3.proxy.impl.remoting.SessionSpecRemotingMetadata;
import org.jboss.ejb3.proxy.spi.container.InvokableContext;
import org.jboss.legacy.spi.ejb3.dynamic.DynamicInvocationProxy;
import org.jboss.security.plugins.JBossSecurityContext;

/**
 * Handles magic moombo jumbo invocation.
 * 
 * @author baranowb
 */
public class StatelesDynamicInvokableContext implements InvokableContext {

    public static final String LEGACY_MD_SECURITY = "security";
    public static final String LEGACY_MD_KEY_PRINCIPIAL = "principal";
    public static final String LEGACY_MD_KEY_CREDENTIAL = "credential";
    public static final String LEGACY_MD_KEY_CONTEXT = "context";
    public static final String LEGACY_TX_KEY_CONTEXT = "TransactionPropagationContext";
    public static final String LEGACY_TX_KEY_ATTRIBUTE = "TransactionPropagationContext";
    protected final DynamicInvocationProxy dynamicInvocationProxy;

    /**
     */
    public StatelesDynamicInvokableContext(DynamicInvocationProxy proxy) {
        super();
        this.dynamicInvocationProxy = proxy;
    }

    @Override
    public Object invoke(Object proxy, SerializableMethod method, Object[] args) throws Throwable {
        throw new RuntimeException("NYI: .invoke");
    }

    @Override
    public InvocationResponse dynamicInvoke(Invocation invocation) throws Throwable {
        final TransactionManager tm = dynamicInvocationProxy.getDynamicInvocationTarget().getTransactionManager();
        final MethodInvocation si = (MethodInvocation) invocation;
        // deserialize old CTX in legacy loader
        final JBossSecurityContext context = (JBossSecurityContext) si.getMetaData(LEGACY_MD_SECURITY, LEGACY_MD_KEY_CONTEXT);
        String tpc = (String)invocation.getMetaData(ClientTxPropagationInterceptor.TRANSACTION_PROPAGATION_CONTEXT,
                ClientTxPropagationInterceptor.TRANSACTION_PROPAGATION_CONTEXT);
        ClassLoader invocationCL = switchLoader(this.dynamicInvocationProxy.getEjb3Data().getBeanClassLoader());
        try {
            Object returnValue = null;
            setupSecurity(si, context);
            if (tpc != null) {
                Transaction tx = tm.getTransaction();
                if (tx != null) {
                    throw new RuntimeException("cannot import a transaction context when a transaction is already associated with the thread");
                }
                Transaction importedTx = dynamicInvocationProxy.getDynamicInvocationTarget().importTransaction(tpc);
                tm.resume(importedTx);
                try {
                    returnValue = invoke(si);
                } finally {
                    tm.suspend();
                }
            } else {
                returnValue = invoke(si);
            }
            return new InvocationResponse(returnValue);
        } finally {
            switchLoader(invocationCL);
        }
    }

    /**
     * @param si
     * @return
     */
    protected Object invoke(MethodInvocation si)  throws Exception{
        final SerializableMethod invokedMethod = (SerializableMethod) si.getMetaData(
                SessionSpecRemotingMetadata.TAG_SESSION_INVOCATION, SessionSpecRemotingMetadata.KEY_INVOKED_METHOD);
        return ((StatelesDynamicInvocationTarget) this.dynamicInvocationProxy.getDynamicInvocationTarget()).invoke(
                invokedMethod.toMethod(), si.getArguments());
    }

    protected void setupSecurity(final MethodInvocation si, final JBossSecurityContext context) {
        final String securityDomain = context.getSecurityDomain();
        final Object principal = si.getMetaData(LEGACY_MD_SECURITY, LEGACY_MD_KEY_PRINCIPIAL);
        final Object credential = si.getMetaData(LEGACY_MD_SECURITY, LEGACY_MD_KEY_CREDENTIAL);
        if (principal != null && credential != null) {
            // TODO: Subject might need a proxy ?
            String stringPrincipal;
            if(principal instanceof Principal){
                stringPrincipal = ((Principal)principal).getName();
            } else {
                throw new IllegalArgumentException("Principal is not instanceof java.security.Principal! "+principal.getClass());
            }
            String stringCredential;
            if( credential instanceof String){
                stringCredential = (String)credential;
            } else {
                throw new IllegalArgumentException("Credential is not instanceof java.lang.String! "+credential.getClass());
            }
            dynamicInvocationProxy.getDynamicInvocationTarget().setupSecurity(securityDomain, stringPrincipal,
                    stringCredential.toCharArray(), context.getSubjectInfo().getAuthenticatedSubject());
        }
    }
}
