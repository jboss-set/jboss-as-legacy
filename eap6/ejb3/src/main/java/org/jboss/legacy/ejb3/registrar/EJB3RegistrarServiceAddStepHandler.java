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

package org.jboss.legacy.ejb3.registrar;

//import static org.jboss.legacy.jnp.JNPSubsystemModel.LEGACY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.core.security.ServerSecurityManager;
import org.jboss.as.security.service.SimpleSecurityManagerService;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.as.server.deployment.Phase;
import org.jboss.dmr.ModelNode;
import org.jboss.legacy.connector.remoting.RemotingConnectorService;
import org.jboss.legacy.ejb3.EJB3Extension;
//import org.jboss.legacy.jnp.server.JNPServerModel;
//import org.jboss.legacy.jnp.server.JNPServerService;
import org.jboss.legacy.spi.connector.ConnectorProxy;
import org.jboss.legacy.spi.ejb3.registrar.EJB3RegistrarProxy;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceTarget;

/**
 * @author baranowb
 */
public class EJB3RegistrarServiceAddStepHandler extends AbstractBoottimeAddStepHandler {

    public static final EJB3RegistrarServiceAddStepHandler INSTANCE = new EJB3RegistrarServiceAddStepHandler();

    public EJB3RegistrarServiceAddStepHandler() {
    }

    @Override
    protected void performBoottime(OperationContext context, ModelNode operation, ModelNode model,
            ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers)
            throws OperationFailedException {
        newControllers.addAll(this.installRuntimeServices(context, operation, model, verificationHandler));
    }

    Collection<ServiceController<?>> installRuntimeServices(final OperationContext context, final ModelNode operation,
            final ModelNode model, final ServiceVerificationHandler verificationHandler) throws OperationFailedException {

        final EJB3RegistrarService service = new EJB3RegistrarService();
        final ServiceTarget serviceTarget = context.getServiceTarget();
        final ServiceBuilder<EJB3RegistrarProxy> serviceBuilder = serviceTarget.addService(EJB3RegistrarService.SERVICE_NAME, service);
        serviceBuilder.addDependency(RemotingConnectorService.SERVICE_NAME,ConnectorProxy.class,service.getInjectedValueConnector());
        serviceBuilder.addDependency(SimpleSecurityManagerService.SERVICE_NAME,ServerSecurityManager.class,service.getServerSecurityManagerInjectedValue());
        //serviceBuilder.addDependency(ServiceName.JBOSS.append(LEGACY).append(JNPServerModel.SERVICE_NAME));
        //serviceBuilder.addDependency(ServiceName.JBOSS.append("legacy").append("jnp-server"));
        
        if (verificationHandler != null) {
            serviceBuilder.addListener(verificationHandler);
        }
        final ServiceController<EJB3RegistrarProxy> registrarServiceController = serviceBuilder.install();
        final List<ServiceController<?>> installedServices = new ArrayList<ServiceController<?>>();
        installedServices.add(registrarServiceController);

        context.addStep(new AbstractDeploymentChainStep() {
            @Override
            protected void execute(DeploymentProcessorTarget processorTarget) {
                processorTarget.addDeploymentProcessor(EJB3Extension.SUBSYSTEM_NAME, Phase.INSTALL, 1000,
                        EJB3DeploymentProcessor.INSTANCE);
            }
        }, OperationContext.Stage.RUNTIME);

        return installedServices;
    }
    
    @Override
    protected void populateModel(final ModelNode operation, final ModelNode model) throws OperationFailedException {
        model.setEmptyObject();
    }
}
