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
package org.jboss.legacy.tx.usertx;

import static org.jboss.legacy.tx.txsession.UserSessionTransactionModel.LEGACY;
import static org.jboss.legacy.tx.txsession.UserSessionTransactionModel.SERVICE_NAME_JNP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.dmr.ModelNode;
import org.jboss.legacy.connector.remoting.RemotingConnectorService;
import org.jboss.legacy.spi.connector.ConnectorProxy;
import org.jboss.legacy.spi.tx.session.UserSessionTransactionProxy;
import org.jboss.legacy.spi.tx.user.ClientUserTransactionProxy;
import org.jboss.legacy.tx.txsession.UserSessionTransactionService;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceTarget;
import org.jboss.msc.service.ServiceName;

/**
 *
 * @author <a href="mailto:ehugonne@redhat.com">Emmanuel Hugonnet</a> (c) 2013 Red Hat, inc.
 */
public class ClientUserTransactionServiceAddStepHandler extends AbstractBoottimeAddStepHandler {

    public static final ClientUserTransactionServiceAddStepHandler INSTANCE = new ClientUserTransactionServiceAddStepHandler();

    public ClientUserTransactionServiceAddStepHandler() {
        super();
    }

    @Override
    protected void performBoottime(OperationContext context, ModelNode operation, ModelNode model,
            ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers)
            throws OperationFailedException {
        newControllers.addAll(this.installRuntimeServices(context, operation, model, verificationHandler));
    }

    Collection<ServiceController<?>> installRuntimeServices(final OperationContext context, final ModelNode operation, final ModelNode model, final ServiceVerificationHandler verificationHandler) throws OperationFailedException {
        final ClientUserTransactionService service = new ClientUserTransactionService();
        final ServiceTarget serviceTarget = context.getServiceTarget();
        final ServiceBuilder<ClientUserTransactionProxy> serviceBuilder = serviceTarget.addService(ClientUserTransactionService.SERVICE_NAME, service);
        serviceBuilder.addDependency(RemotingConnectorService.SERVICE_NAME, ConnectorProxy.class, service.getInjectedConnector())
                .addDependency(UserSessionTransactionService.SERVICE_NAME, UserSessionTransactionProxy.class,
                        service.getInjectedUserSessionTransactionProxyFactory())
                            .addDependency(ServiceName.JBOSS.append(LEGACY).append(SERVICE_NAME_JNP));
        if (verificationHandler != null) {
            serviceBuilder.addListener(verificationHandler);
        }
        final ServiceController<ClientUserTransactionProxy> clientUsertransactionServiceController = serviceBuilder.install();
        final List<ServiceController<?>> installedServices = new ArrayList<ServiceController<?>>();
        installedServices.add(clientUsertransactionServiceController);
        return installedServices;
    }

    @Override
    protected void populateModel(final ModelNode operation, final ModelNode model) throws OperationFailedException {
        model.setEmptyObject();
    }
}
