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
package org.jboss.legacy.connector.remoting;

import org.jboss.as.network.SocketBinding;
import org.jboss.legacy.spi.connector.ConnectorProxy;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

/**
 * Service which manages remoting connector.
 * @author baranowb
 * 
 */
public class RemotingConnectorService implements Service<ConnectorProxy> {

    public static final ServiceName SERVICE_NAME = ServiceName.JBOSS.append(RemotingModel.LEGACY).append(
            RemotingModel.SERVICE_NAME);

    private ConnectorProxy connector;
    private final InjectedValue<SocketBinding> binding = new InjectedValue<SocketBinding>();

    public RemotingConnectorService() {
        super();
        this.connector = new ConnectorProxy();
    }

    public InjectedValue<SocketBinding> getBinding() {
        return binding;
    }

    @Override
    public ConnectorProxy getValue() throws IllegalStateException, IllegalArgumentException {
        return this.connector;
    }

    @Override
    public void start(StartContext startContext) throws StartException {
        try {
            this.connector.setHost(this.getBinding().getValue().getAddress().getHostName());
            this.connector.setPort(String.valueOf(this.getBinding().getValue().getAbsolutePort()));
            this.connector.setTcpNoDelay(true);
            this.connector.start();
        } catch (Exception e) {
            throw new StartException(e);
        }
    }

    @Override
    public void stop(StopContext stopContext) {
        try {
            this.connector.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
