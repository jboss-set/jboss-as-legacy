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

import java.util.Hashtable;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.aop.Advisor;
import org.jboss.aop.AspectManager;
import org.jboss.aop.ClassAdvisor;
import org.jboss.aop.Dispatcher;
import org.jboss.ejb3.common.metadata.MetadataUtil;
import org.jboss.ejb3.proxy.impl.jndiregistrar.JndiSessionRegistrarBase;
import org.jboss.ejb3.proxy.spi.container.InvokableContext;
import org.jboss.legacy.spi.common.LegacyBean;
import org.jboss.legacy.spi.ejb3.common.EJBDataProxy;
import org.jboss.legacy.spi.ejb3.registrar.EJB3RegistrarProxy;
import org.jboss.metadata.ejb.jboss.JBossEnterpriseBeansMetaData;
import org.jboss.metadata.ejb.jboss.JBossMetaData;
import org.jboss.metadata.ejb.jboss.JBossSessionBeanMetaData;
import org.jboss.metadata.ejb.jboss.jndipolicy.spi.DeploymentSummary;
import org.jboss.metadata.ejb.spec.BusinessRemotesMetaData;

/**
 * @author baranowb
 * 
 */
public abstract class DynamicInvocationProxy extends LegacyBean {

    protected EJBDataProxy ejb3Data;
    protected EJB3RegistrarProxy ejb3RegistrarProxy;
    protected JBossSessionBeanMetaData metadata;
    protected DynamicInvocationTarget dynamicInvocationTarget;

    /**
     * @return the ejb3Data
     */
    public EJBDataProxy getEjb3Data() {
        return ejb3Data;
    }

    /**
     * @param ejb3Data the ejb3Data to set
     */
    public void setEjb3Data(EJBDataProxy ejb3Data) {
        this.ejb3Data = ejb3Data;
    }

    /**
     * @return the ejb3RegistrarProxy
     */
    public EJB3RegistrarProxy getEjb3RegistrarProxy() {
        return ejb3RegistrarProxy;
    }

    /**
     * @param ejb3RegistrarProxy the ejb3RegistrarProxy to set
     */
    public void setEjb3RegistrarProxy(EJB3RegistrarProxy ejb3RegistrarProxy) {
        this.ejb3RegistrarProxy = ejb3RegistrarProxy;
    }

    /**
     * @return the metadata
     */
    public JBossSessionBeanMetaData getMetadata() {
        return metadata;
    }

    /**
     * @return the dynamicInvocationTarget
     */
    public DynamicInvocationTarget getDynamicInvocationTarget() {
        return dynamicInvocationTarget;
    }

    /**
     * @param dynamicInvocationTarget the dynamicInvocationTarget to set
     */
    public void setDynamicInvocationTarget(DynamicInvocationTarget dynamicInvocationTarget) {
        this.dynamicInvocationTarget = dynamicInvocationTarget;
    }

    @Override
    protected void internalStart() throws Exception {
        if (ejb3Data == null)
            throw new IllegalArgumentException("EJB3 Data must not be null");
        if (ejb3RegistrarProxy == null)
            throw new IllegalArgumentException("EJB3 Registrar proxy must not be null");
        if (dynamicInvocationTarget == null)
            throw new IllegalArgumentException("Dynamic Target must not be null");
        createLegacyBinding();
    }

    @Override
    protected void internalStop() throws Exception {
        removeLegacyBinding();
    }

    protected void createLegacyBinding() throws NamingException {
        final JBossSessionBeanMetaData metaData = createMetaData(ejb3Data);
        final String containerName = metaData.getName();
        final ClassLoader beanClassLoader = ejb3Data.getBeanClassLoader();
        Dispatcher.singleton.registerTarget(containerName, new InvokableContextClassProxyHack(createInvokableContext()));

        final JndiSessionRegistrarBase registrar = getJndiSessionRegistrarBase(this.ejb3Data, this.ejb3RegistrarProxy);
        final String containerGuid = containerName + ":" + UUID.randomUUID().toString();
        final AspectManager aspectManager = AspectManager.instance(beanClassLoader);
        // TODO: probably ClassAdvisor won't do
        final Advisor advisor = new ClassAdvisor(Object.class, aspectManager);
        final ClassLoader oldLoader = switchLoader(this.getClass().getClassLoader());
        InitialContext context = null;
        try {
            context = createJNPLocalContext();
            registrar.bindEjb(context, metaData, beanClassLoader, containerName, containerGuid, advisor);
        } finally {
            if (context != null)
                try {
                    context.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            try {
                Thread.currentThread().setContextClassLoader(oldLoader);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    protected void removeLegacyBinding() throws NamingException {

        final JBossSessionBeanMetaData metaData = createMetaData(this.ejb3Data);
        final JndiSessionRegistrarBase registrar = getJndiSessionRegistrarBase(this.ejb3Data, this.ejb3RegistrarProxy);
        final ClassLoader old = switchLoader(this.getClass().getClassLoader());
        InitialContext context = null;
        try {
            context = createJNPLocalContext();
            registrar.unbindEjb(context, metaData);
        } finally {
            if (context != null)
                try {
                    context.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            try {
                Thread.currentThread().setContextClassLoader(old);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract InvokableContext createInvokableContext();

    protected JBossSessionBeanMetaData createMetaData(final EJBDataProxy data) {
        if (this.metadata != null) {
            return (JBossSessionBeanMetaData) metadata;
        }

        final JBossMetaData jarMetaData = new JBossMetaData();
        DeploymentSummary deploymentSumary = new DeploymentSummary();
        deploymentSumary.setDeploymentName(data.getDeploymentName());
        if (data.getDeploymentScopeBaseName() != null) {
            deploymentSumary.setDeploymentScopeBaseName(data.getDeploymentScopeBaseName());
        }
        // TODO: deploymentSummary.packagingType && deploymentSummary.loader ?
        jarMetaData.setDeploymentSummary(deploymentSumary);
        jarMetaData.setEjbVersion(data.getEJBVersion());
        final JBossEnterpriseBeansMetaData enterpriseBeansMetaData = new JBossEnterpriseBeansMetaData();
        jarMetaData.setEnterpriseBeans(enterpriseBeansMetaData);
        enterpriseBeansMetaData.setEjbJarMetaData(jarMetaData);
        final JBossSessionBeanMetaData smd = new JBossSessionBeanMetaData();
        smd.setEnterpriseBeansMetaData(enterpriseBeansMetaData);
        smd.setEjbName(data.getName());
        enterpriseBeansMetaData.add(smd);
        final BusinessRemotesMetaData businessRemotes = new BusinessRemotesMetaData();
        businessRemotes.add(data.getRemoteInterfaceClass());
        smd.setBusinessRemotes(businessRemotes);
        MetadataUtil.decorateEjbsWithJndiPolicy(jarMetaData, data.getBeanClassLoader());
        this.metadata = (JBossSessionBeanMetaData) jarMetaData.getEnterpriseBean(smd.getName());
        return (JBossSessionBeanMetaData) this.metadata;
    }

    public static JndiSessionRegistrarBase getJndiSessionRegistrarBase(final EJBDataProxy data,
            final EJB3RegistrarProxy registrarService) {
        return data.isStateful() ? registrarService.getJndiStatefulSessionRegistrar() : registrarService
                .getJndiStatelessSessionRegistrar();
    }
}
