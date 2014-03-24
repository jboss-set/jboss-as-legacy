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

package org.jboss.legacy.spi.common;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Simple interface to define basic contract. Allows to inform legacy proxies about EAP6 start/stop actions.
 * 
 * @author baranowb
 * 
 */
public abstract class LegacyBean {

    public void start() throws Exception {
        ClassLoader old = switchLoader(this.getClass().getClassLoader());
        try {
            internalStart();
        } finally {
            switchLoader(old);
        }
    }

    public void stop() throws Exception {
        ClassLoader old = switchLoader(this.getClass().getClassLoader());
        try {
            internalStop();
        } finally {
            switchLoader(old);
        }
    }

    /**
     * Implementing class should allocate/start resource in this method.
     * 
     * @throws Exception
     */
    protected abstract void internalStart() throws Exception;

    /**
     * Implementing class should free/stop resource in this method.
     * 
     * @throws Exception
     */
    protected abstract void internalStop() throws Exception;

    public static ClassLoader switchLoader(final ClassLoader loader) {
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        return current;
    }
    
    public static InitialContext createJNPLocalContext() throws NamingException {
        final Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.LocalOnlyContextFactory");
        env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
        return new InitialContext(env);
    }
}
