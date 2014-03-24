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

package org.jboss.legacy.spi.ejb3.dynamic.stateful;

import org.jboss.aop.joinpoint.MethodInvocation;
import org.jboss.ejb3.common.lang.SerializableMethod;
import org.jboss.ejb3.proxy.impl.remoting.SessionSpecRemotingMetadata;
import org.jboss.legacy.spi.ejb3.dynamic.stateles.StatelesDynamicInvokableContext;

/**
 * @author baranowb
 * 
 */
public class StatefulDynamicInvokableContext extends StatelesDynamicInvokableContext {
    public static final String LEGACY_MD_SFSB = "SFSBInvocation";
    public static final String LEGACY_MD_SESSION_ID = "SessionID";

    /**
     * @param proxy
     */
    public StatefulDynamicInvokableContext(StatefulDynamicInvocationProxy proxy) {
        super(proxy);
    }

    protected Object invoke(MethodInvocation si) throws Exception {
        final SerializableMethod invokedMethod = (SerializableMethod) si.getMetaData(
                SessionSpecRemotingMetadata.TAG_SESSION_INVOCATION, SessionSpecRemotingMetadata.KEY_INVOKED_METHOD);
        final Object sessionID = si.getMetaData(LEGACY_MD_SFSB, LEGACY_MD_SESSION_ID);
        if (sessionID instanceof byte[]) {
            return ((StatefulDynamicInvocationTarget) super.dynamicInvocationProxy.getDynamicInvocationTarget()).invoke(
                    invokedMethod.toMethod(), si.getArguments(), sessionID);
        } else {
            throw new RuntimeException("Wrong session ID: " + sessionID.getClass());
        }

    }

}
