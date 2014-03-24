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

package org.jboss.legacy.spi.ejb3.registrar;

import java.net.URL;

import org.jboss.aop.AspectXmlLoader;
import org.jboss.ejb3.common.registrar.spi.Ejb3Registrar;
import org.jboss.ejb3.common.registrar.spi.Ejb3RegistrarLocator;
import org.jboss.ejb3.proxy.impl.jndiregistrar.JndiStatefulSessionRegistrar;
import org.jboss.ejb3.proxy.impl.jndiregistrar.JndiStatelessSessionRegistrar;
import org.jboss.ejb3.proxy.impl.objectfactory.session.stateful.StatefulSessionProxyObjectFactory;
import org.jboss.ejb3.proxy.impl.objectfactory.session.stateless.StatelessSessionProxyObjectFactory;

import org.jboss.legacy.spi.common.LegacyBean;
import org.jboss.legacy.spi.connector.ConnectorProxy;

/**
 * Proxy which hides all registrar magic.
 * 
 * @author baranowb
 * 
 */
public class EJB3RegistrarProxy extends LegacyBean {
    //private static final String AOP_FILE = "ejb3-interceptors-aop.xml";
    private static final String CONNECTOR_BIND_NAME = "org.jboss.ejb3.RemotingConnector";
    // main thing
    private Ejb3Registrar registrar;
    // hacks
    private JndiStatelessSessionRegistrar jndiStatelessSessionRegistrar;
    private JndiStatefulSessionRegistrar jndiStatefulSessionRegistrar;
    private URL ejb3AOPInterceptorsURL;
    // deps
    private ConnectorProxy connector;

    public URL getEjb3AOPInterceptorsURL() {
        return ejb3AOPInterceptorsURL;
    }

    public void setEjb3AOPInterceptorsURL(URL ejb3aopInterceptorsURL) {
        ejb3AOPInterceptorsURL = ejb3aopInterceptorsURL;
    }

    public ConnectorProxy getConnector() {
        return connector;
    }

    public void setConnector(ConnectorProxy connector) {
        this.connector = connector;
    }

    public Ejb3Registrar getRegistrar() {
        return this.registrar;
    }

    public JndiStatelessSessionRegistrar getJndiStatelessSessionRegistrar() {
        return this.jndiStatelessSessionRegistrar;
    }

    public JndiStatefulSessionRegistrar getJndiStatefulSessionRegistrar() {
        return this.jndiStatefulSessionRegistrar;
    }

    @Override
    protected void internalStart() throws Exception {
        if (this.connector == null || this.connector.getConnector() == null) {
            throw new IllegalArgumentException("Connector not found: " + this.connector);
        }
        this.registrar = new InMemoryEJB3Registrar();
        Ejb3RegistrarLocator.bindRegistrar(this.registrar);
        Ejb3RegistrarLocator.locateRegistrar().bind(CONNECTOR_BIND_NAME, this.connector.getConnector());
        //this.ejb3AOPInterceptorsURL = Thread.currentThread().getContextClassLoader().getResource(AOP_FILE);
        AspectXmlLoader.deployXML(this.ejb3AOPInterceptorsURL);
        this.jndiStatelessSessionRegistrar = new JndiStatelessSessionRegistrar(
                StatelessSessionProxyObjectFactory.class.getName());
        this.jndiStatefulSessionRegistrar = new JndiStatefulSessionRegistrar(StatefulSessionProxyObjectFactory.class.getName());
    }

    @Override
    protected void internalStop() throws Exception {
        if (Ejb3RegistrarLocator.isRegistrarBound() && Ejb3RegistrarLocator.locateRegistrar() == this.registrar) {
            Ejb3RegistrarLocator.locateRegistrar().unbind(CONNECTOR_BIND_NAME);
            Ejb3RegistrarLocator.unbindRegistrar();
        }
        if (ejb3AOPInterceptorsURL != null) {
            try {
                AspectXmlLoader.undeployXML(ejb3AOPInterceptorsURL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ejb3AOPInterceptorsURL = null;
        }
        this.jndiStatelessSessionRegistrar = null;
        this.jndiStatefulSessionRegistrar = null;

    }

}
