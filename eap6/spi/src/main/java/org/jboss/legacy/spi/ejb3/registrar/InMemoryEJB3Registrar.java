/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.legacy.spi.ejb3.registrar;

import org.jboss.ejb3.common.registrar.spi.DuplicateBindException;
import org.jboss.ejb3.common.registrar.spi.Ejb3Registrar;
import org.jboss.ejb3.common.registrar.spi.NotBoundException;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class InMemoryEJB3Registrar implements Ejb3Registrar {

    final Map<Object, Object> registrar = new HashMap<Object, Object>();

    @Override
    public Map<Object, Object> list() {
        throw new RuntimeException("NYI: org.jboss.ejb.legacy.stateless.InMemoryEjb3Registrar.list");
    }

    @Override
    public Object lookup(Object name) throws NotBoundException {
        // this is for stateful...
        final Object value = registrar.get(name);
        if (value == null)
            throw new NotBoundException("Can't find " + name + "!");
        return value;
    }

    @Override
    public <T> T lookup(Object name, Class<T> type) throws NotBoundException {
        final Object value = registrar.get(name);
        if (value == null)
            throw new NotBoundException("Can't find " + name + " of type " + type);
        return type.cast(value);
    }

    @Override
    public void bind(Object name, Object value) throws DuplicateBindException {
        if (registrar.containsKey(name))
            throw new DuplicateBindException(name + " already has an object bound");
        invokeOptionalMethod(value, "start");
        registrar.put(name, value);
    }

    @Override
    public void rebind(Object name, Object value) {
        throw new RuntimeException("NYI: org.jboss.ejb.legacy.stateless.InMemoryEjb3Registrar.rebind");
    }

    @Override
    public void unbind(Object name) throws NotBoundException {
        if (this.registrar.remove(name) == null) {
            throw new NotBoundException("Name not bound: " + name);
        }
    }

    @Override
    public Object invoke(Object name, String methodName, Object[] arguments, String[] signature) throws NotBoundException {
        throw new RuntimeException("NYI: org.jboss.ejb.legacy.stateless.InMemoryEjb3Registrar.invoke");
    }

    @Override
    public Object getProvider() {
        throw new RuntimeException("NYI: org.jboss.ejb.legacy.stateless.InMemoryEjb3Registrar.getProvider");
    }

    private void invokeOptionalMethod(final Object object, final String methodName) {
        try {
            final Method method = object.getClass().getMethod("start");
            method.invoke(object);
        } catch (NoSuchMethodException e) {
            return;
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
