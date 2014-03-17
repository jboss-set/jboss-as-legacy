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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.common.RemoteHelloWorld;
import org.jboss.ejb.HelloWorldEjb;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:ehugonne@redhat.com">Emmanuel Hugonnet</a> (c) 2013 Red Hat, inc.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SimpleEJBCallTestCase {

    private static final String EJB_NAME = "HelloWorldEjb/remote-org.jboss.common.RemoteHelloWorld";
    private static final String JNDI_CONFIG = "jndi-eap6.properties";

    private InitialContext getInitialContext() throws NamingException, IOException {
        Properties jndiProperties = new Properties();
        jndiProperties.load(this.getClass().getClassLoader().getResourceAsStream(System.getProperty("jndi_config", JNDI_CONFIG)));
        return new javax.naming.InitialContext(jndiProperties);
    }

    @Deployment
    public static Archive createDeployment() {
        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "hello.jar");
        jar.addClasses(HelloWorldEjb.class, RemoteHelloWorld.class);
        return jar;
    }

    @Test
    public void sayHello() throws Exception {
        InitialContext initialContext = getInitialContext();
        try {
            RemoteHelloWorld ejb = (RemoteHelloWorld) initialContext.lookup(EJB_NAME);
            assertThat(ejb, is(notNullValue()));
            assertThat(ejb.sayHello("Bart"), is("Hello Bart"));
        } finally {
            initialContext.close();
        }
    }
}
