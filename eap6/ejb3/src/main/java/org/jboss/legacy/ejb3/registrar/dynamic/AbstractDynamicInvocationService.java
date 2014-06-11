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

package org.jboss.legacy.ejb3.registrar.dynamic;

import static org.jboss.as.ejb3.EjbMessages.MESSAGES;

import java.lang.reflect.Method;

import org.jboss.as.naming.InitialContext;
import javax.security.auth.Subject;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.jboss.as.core.security.ServerSecurityManager;
import org.jboss.as.ee.component.Component;
import org.jboss.as.ee.component.ComponentView;
import org.jboss.as.ee.component.EEModuleDescription;
import org.jboss.as.ee.utils.DescriptorUtils;
import org.jboss.as.ejb3.component.EJBComponent;
import org.jboss.as.ejb3.component.EJBComponentDescription;
import org.jboss.as.ejb3.deployment.DeploymentModuleIdentifier;
import org.jboss.as.ejb3.deployment.DeploymentRepository;
import org.jboss.as.ejb3.deployment.EjbDeploymentInformation;
import org.jboss.as.ejb3.deployment.ModuleDeployment;
import org.jboss.invocation.InterceptorContext;
import org.jboss.legacy.common.DeploymentEJBDataProxyMap;
import org.jboss.legacy.common.EJB3Logger;
import org.jboss.legacy.common.EJB3Messages;
import org.jboss.legacy.common.ExtendedEJBDataProxy;
import org.jboss.legacy.spi.ejb3.dynamic.DynamicInvocationProxy;
import org.jboss.legacy.spi.ejb3.dynamic.DynamicInvocationTarget;
import org.jboss.legacy.spi.ejb3.registrar.EJB3RegistrarProxy;
import org.jboss.modules.ModuleClassLoader;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

import com.arjuna.ats.arjuna.common.Uid;

/**
 * Base for invocation service. Service starts EAP5 invocation proxy which handle invocation translation from EAP5 to EAP5+.
 * Concrete implementation provide instance of proxy via {@link #createInvocationProxy()}.
 * @author baranowb
 */
public abstract class AbstractDynamicInvocationService implements DynamicInvocationTarget{

    protected final InjectedValue<ServerSecurityManager> serverSecurityManagerInjectedValue = new InjectedValue<ServerSecurityManager>();
    protected final InjectedValue<TransactionManager> transactionManagerInjectedValue = new InjectedValue<TransactionManager>();
    protected final InjectedValue<EJB3RegistrarProxy> ejb3RegistrarInjectedValue = new InjectedValue<EJB3RegistrarProxy>();
    protected final InjectedValue<DeploymentRepository> deploymentRepositoryInjectedValue = new InjectedValue<DeploymentRepository>();
    protected final InjectedValue<ComponentView> viewInjectedValue = new InjectedValue<ComponentView>();

    protected final ServiceName serviceName;
    protected final String applicationName;
    protected final String moduleName;
    protected final String distinctName;
    protected final String componentName;
    protected final ExtendedEJBDataProxy ejb3Data;
    
    protected ModuleClassLoader namingClassLoader = null;

    protected DynamicInvocationProxy dynamicInvocationProxy;
    protected EjbDeploymentInformation ejbDeploymentInformation;
    public AbstractDynamicInvocationService(ExtendedEJBDataProxy ejb3Data, EEModuleDescription moduleDescription,
            EJBComponentDescription ejbComponentDescription) {
        this.ejb3Data = ejb3Data;
        this.serviceName = DeploymentEJBDataProxyMap.getServiceName(moduleDescription, ejbComponentDescription);
        this.applicationName = moduleDescription.getEarApplicationName() != null ? moduleDescription.getEarApplicationName() : "";
        this.moduleName = moduleDescription.getModuleName();
        this.distinctName = moduleDescription.getDistinctName();
        this.componentName = ejbComponentDescription.getComponentName();
    }

    @Override
    public String toString() {
        return "DynamicInvocationService [serviceName=" + serviceName + "]@"+this.hashCode();
    }

    public void start(StartContext context) throws StartException {
        EJB3Logger.ROOT_LOGGER.startDynamicInvocationService(this.componentName);
        try {
            this.dynamicInvocationProxy = createInvocationProxy();
            this.dynamicInvocationProxy.start();
        } catch (Exception e) {
            throw EJB3Messages.MESSAGES.couldNotStartDynamicInvocationService(this.componentName, e);
        }
    }

    public void stop(StopContext context) {
        EJB3Logger.ROOT_LOGGER.stoppingDynamicInvocationService(this.componentName);
        try {
            this.dynamicInvocationProxy.stop();
        } catch (Exception e) {
            EJB3Logger.ROOT_LOGGER.couldNotStopDynamicInvocationService(this.componentName, e);
        }
    }

    public InjectedValue<TransactionManager> getTransactionManagerInjectedValue() {
        return transactionManagerInjectedValue;
    }

    public InjectedValue<ServerSecurityManager> getServerSecurityManagerInjectedValue() {
        return serverSecurityManagerInjectedValue;
    }

    public InjectedValue<EJB3RegistrarProxy> getEJB3RegistrarInjectedValue() {
        return ejb3RegistrarInjectedValue;
    }

    public InjectedValue<DeploymentRepository> getDeploymentRepositoryInjectedValue() {
        return deploymentRepositoryInjectedValue;
    }

    public InjectedValue<ComponentView> getViewInjectedValue() {
        return this.viewInjectedValue;
    }

    public ServiceName getServiceName() {
        return this.serviceName;
    }
    
    protected InterceptorContext createInterceptorContext(Method method, Object[] arguments) throws Exception{
        final InitialContext ic = new InitialContext(null);
        try {
            final ComponentView view = viewInjectedValue.getValue();
            final InterceptorContext customContext = new InterceptorContext();
            // Just a copy paste: TODO: this is not very efficient
            final Method localMethod = view.getMethod(method.getName(), DescriptorUtils.methodDescriptor(method));
            customContext.setMethod(localMethod);
            customContext.setParameters(arguments);
            customContext.setTarget(ic.lookup(ejb3Data.getLocalASBinding()));
            // setup private data
            final EjbDeploymentInformation ejb = findBean();
            final EJBComponent ejbComponent = ejb.getEjbComponent();
            customContext.putPrivateData(ComponentView.class, view);
            customContext.putPrivateData(Component.class, ejbComponent);
            return customContext;
        } finally {
            ic.close();
        }
    }
    
    protected EjbDeploymentInformation findBean() {
        if (this.ejbDeploymentInformation != null) {
            return this.ejbDeploymentInformation;
        }
        final ModuleDeployment module = deploymentRepositoryInjectedValue.getValue().getModules().get(new DeploymentModuleIdentifier(this.applicationName, this.moduleName, this.distinctName));
        if (module == null) {
            throw MESSAGES.unknownDeployment(this.applicationName, this.moduleName, this.distinctName);
        }
        this.ejbDeploymentInformation = module.getEjbs().get(this.componentName);
        if (this.ejbDeploymentInformation == null) {
            throw MESSAGES.ejbNotFoundInDeployment(this.componentName, this.applicationName, this.moduleName, this.distinctName);
        }
        return this.ejbDeploymentInformation;
    }

    /**
     * @return
     */
    protected abstract DynamicInvocationProxy createInvocationProxy();

    @Override
    public void setupSecurity(final String securityDomain, final String principal, final char[] credential, final Subject subject) {
        if (principal != null && credential != null) {
            this.serverSecurityManagerInjectedValue.getValue().push(securityDomain, principal, credential, subject);
        }
    }

    @Override
    public TransactionManager getTransactionManager() {
        return ((EJBComponent)viewInjectedValue.getValue().getComponent()).getTransactionManager();
    }

    @Override
    public Transaction importTransaction(String id) {
        Uid importedTx = new Uid(id);
        return com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionImple.getTransaction(importedTx);
    }

}
