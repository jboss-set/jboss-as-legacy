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

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.common.TransactionMandatoryRemote;
import org.jboss.ejb.transactional.TransactionMandatoryBean;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:ehugonne@redhat.com">Emmanuel Hugonnet</a> (c) 2013 Red Hat, inc.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class UserTransactionTestCase {

    private final Pattern pattern = Pattern.compile(".*jboss-logging-[^spi].*");

    @Deployment
    public static Archive createDeployment() {
        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "usertx.jar");
        jar.addClasses(TransactionMandatoryBean.class, TransactionMandatoryRemote.class);
        return jar;
    }


    private ClassLoader buildEap5ClassLoader() throws Exception {
        List<URL> urls = new ArrayList<URL>();
        String classpath = System.getProperty("java.class.path");
        StringTokenizer tokenizer = new StringTokenizer(classpath, File.pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            String fileName = tokenizer.nextToken();
            if (!pattern.matcher(fileName).find()) {
                urls.add(new File(fileName).toURI().toURL());
            }
        }
        URLClassLoader testCl = new URLClassLoader(urls.toArray(new URL[urls.size()]), null);
        return testCl;
    }

    @Test
    public void commitTxMandatoryEJB() throws Exception {
        ClassLoader originCl = Thread.currentThread().getContextClassLoader();
        ClassLoader cl = buildEap5ClassLoader();
        Thread.currentThread().setContextClassLoader(cl);
        try {
            Class userTxCheckerClass = cl.loadClass(UserTransactionChecker.class.getName());
            Object checker = userTxCheckerClass.newInstance();
            Method method = userTxCheckerClass.getMethod("commitTxMandatoryEJB", new Class[0]);
            method.invoke(checker, new Object[0]);
        } finally {
            Thread.currentThread().setContextClassLoader(originCl);
        }
    }

    @Test
    public void rollbackTxMandatoryEJB() throws Exception {
        ClassLoader originCl = Thread.currentThread().getContextClassLoader();
        ClassLoader cl = buildEap5ClassLoader();
        Thread.currentThread().setContextClassLoader(cl);
        try {
            Class userTxCheckerClass = cl.loadClass(UserTransactionChecker.class.getName());
            Object checker = userTxCheckerClass.newInstance();
            Method method = userTxCheckerClass.getMethod("rollbackTxMandatoryEJB", new Class[0]);
            method.invoke(checker, new Object[0]);
        } finally {
            Thread.currentThread().setContextClassLoader(originCl);
        }
    }
}
