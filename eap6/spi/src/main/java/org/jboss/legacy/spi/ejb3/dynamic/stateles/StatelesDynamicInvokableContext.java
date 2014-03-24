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
            // TODO: toString does not seem a good idea.
            // TODO: Subject might need a proxy ?
            dynamicInvocationProxy.getDynamicInvocationTarget().setupSecurity(securityDomain, principal.toString(),
                    credential.toString().toCharArray(), context.getSubjectInfo().getAuthenticatedSubject());
        }
    }

    // protected InterceptorContext createInterceptorContext(final MethodInvocation si) throws NamingException {
    // final InitialContext ic = new InitialContext();
    // final SerializableMethod invokedMethod = (SerializableMethod) si.getMetaData(
    // SessionSpecRemotingMetadata.TAG_SESSION_INVOCATION, SessionSpecRemotingMetadata.KEY_INVOKED_METHOD);
    // final ComponentView view = viewInjectedValue.getValue();
    // final InterceptorContext customContext = new InterceptorContext();
    // // Just a copy paste: TODO: this is not very efficient
    // final Method method = view.getMethod(invokedMethod.toMethod().getName(),
    // DescriptorUtils.methodDescriptor(invokedMethod.toMethod()));
    // customContext.setMethod(method);
    // customContext.setParameters(si.getArguments());
    // customContext.setTarget(ic.lookup(ejb3Data.getLocalASBinding()));
    // // setup private data
    // final EjbDeploymentInformation ejb = findBean();
    // final EJBComponent ejbComponent = ejb.getEjbComponent();
    // customContext.putPrivateData(ComponentView.class, view);
    // customContext.putPrivateData(Component.class, ejbComponent);
    // return customContext;
    // }
    //
    // public Object transactionalInvokation(final MethodInvocation invocation, final InterceptorContext customContext) throws
    // Throwable {
    // TransactionManager tm = ((EJBComponent) viewInjectedValue.getValue().getComponent()).getTransactionManager();
    // Object tpc = invocation.getMetaData(ClientTxPropagationInterceptor.TRANSACTION_PROPAGATION_CONTEXT,
    // ClientTxPropagationInterceptor.TRANSACTION_PROPAGATION_CONTEXT);
    // if (tpc != null) {
    // Transaction tx = tm.getTransaction();
    // if (tx != null) {
    // throw new
    // RuntimeException("cannot import a transaction context when a transaction is already associated with the thread");
    // }
    // Transaction importedTx = importTPC(tpc);
    // tm.resume(importedTx);
    // try {
    // return doRealInvocation(customContext);
    // } finally {
    // tm.suspend();
    // }
    // } else {
    // return doRealInvocation(customContext);
    // }
    // }
    //
    // private Object doRealInvocation(final InterceptorContext customContext) throws Throwable {
    // final ComponentView view = viewInjectedValue.getValue();
    // return view.invoke(customContext);
    // }
    //
    // protected Transaction importTPC(Object tpc) throws NamingException {
    // Uid importedTx = new Uid((String) tpc);
    // return com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionImple.getTransaction(importedTx);
    // }

}
