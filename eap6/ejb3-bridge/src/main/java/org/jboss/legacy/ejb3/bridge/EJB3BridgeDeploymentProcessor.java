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

package org.jboss.legacy.ejb3.bridge;

import java.util.Set;
import org.jboss.as.ee.component.Attachments;
import org.jboss.as.ee.component.ComponentConfiguration;
import org.jboss.as.ee.component.ComponentConfigurator;
import org.jboss.as.ee.component.ComponentDescription;
import org.jboss.as.ee.component.EEModuleDescription;
import org.jboss.as.ee.component.ViewDescription;
import org.jboss.as.ejb3.component.EJBComponentDescription;
import org.jboss.as.ejb3.component.EJBViewDescription;
import org.jboss.as.ejb3.component.MethodIntf;
import org.jboss.as.ejb3.component.messagedriven.MessageDrivenComponentDescription;
import org.jboss.as.ejb3.component.session.SessionBeanComponentDescription;
import org.jboss.as.ejb3.component.session.SessionBeanComponentDescription.SessionBeanType;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.EjbDeploymentMarker;
import org.jboss.legacy.common.DeploymentEJBDataProxyMap;
import org.jboss.legacy.common.ExtendedEJBDataProxy;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.value.InjectedValue;
import org.jboss.msc.value.Values;

/**
 * Processor to hook up EJB with nice JNP/AOP binding.
 *
 * @author baranowb
 */
public class EJB3BridgeDeploymentProcessor implements DeploymentUnitProcessor {

    public static final EJB3BridgeDeploymentProcessor INSTANCE = new EJB3BridgeDeploymentProcessor();

    @Override
    public void deploy(final DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        final DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
        if (!EjbDeploymentMarker.isEjbDeployment(deploymentUnit)) {
            return;
        }
        final EEModuleDescription moduleDescription = deploymentUnit.getAttachment(Attachments.EE_MODULE_DESCRIPTION);
        if (moduleDescription != null) {

            for (final ComponentDescription componentDescription : moduleDescription.getComponentDescriptions()) {
                if(componentDescription instanceof MessageDrivenComponentDescription){
                    continue;
                }
                if (componentDescription instanceof EJBComponentDescription) {
                    try {
                        final EJBComponentDescription ejbComponentDescription = (EJBComponentDescription) componentDescription;
                        final DeploymentEJBDataProxyMap deploymentEJBDataProxyMap = getEJBDAtaProxyMap(deploymentUnit);
                        final InjectedValue<ClassLoader> viewClassLoader = new InjectedValue<ClassLoader>();
                        ejbComponentDescription.getConfigurators().add(new ComponentConfigurator() {
                            @Override
                            public void configure(DeploymentPhaseContext context, ComponentDescription description,
                                    ComponentConfiguration configuration) throws DeploymentUnitProcessingException {

                                viewClassLoader.setValue(Values.immediateValue(configuration.getModuleClassLoader()));
                                final SessionBeanComponentDescription sessionBeanComponentDescription = (SessionBeanComponentDescription) description;

                                Set<ViewDescription> views = ejbComponentDescription.getViews();
                                for (ViewDescription vd : views) {
                                    final MethodIntf viewType = ((EJBViewDescription) vd).getMethodIntf();
                                    if (viewType == MethodIntf.REMOTE) {
                                        final ViewDescription viewDescription = vd;
                                        final String globalBinding = getGlobalBinding(viewDescription.getBindingNames());
                                        deploymentEJBDataProxyMap.put(deploymentEJBDataProxyMap.getServiceName(moduleDescription, ejbComponentDescription), new ExtendedEJBDataProxy() {

                                            @Override
                                            public String getName() {
                                                return ejbComponentDescription.getComponentName();
                                            }

                                            @Override
                                            public String getRemoteInterfaceClass() {
                                                return viewDescription.getViewClassName();
                                            }

                                            @Override
                                            public String getEJBVersion() {
                                                return "3.0";
                                            }

                                            @Override
                                            public ClassLoader getBeanClassLoader() {
                                                return viewClassLoader.getValue();
                                            }

                                            @Override
                                            public boolean isStateful() {
                                                return sessionBeanComponentDescription.getSessionBeanType() != SessionBeanType.STATELESS;
                                            }

                                            @Override
                                            public String getLocalASBinding() {
                                                return globalBinding;
                                            }

                                            @Override
                                            public ServiceName getViewServiceName() {
                                                return viewDescription.getServiceName();
                                            }

                                            @Override
                                            public String getDeploymentName() {
                                                return deploymentUnit.getName();
                                            }

                                            @Override
                                            public String getDeploymentScopeBaseName() {
                                                if (deploymentUnit.getParent() != null) {
                                                    return stripExtension(deploymentUnit.getParent().getName());
                                                }
                                                return getDeploymentName();
                                            }

                                            String stripExtension(String name) {
                                                int index = name.lastIndexOf('.');
                                                if (index > 0) {
                                                    name = name.substring(0, index);
                                                }
                                                return name;
                                            }
                                        });
                                        // break from loop
                                        break;
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * @param deploymentUnit
     * @return
     */
    private DeploymentEJBDataProxyMap getEJBDAtaProxyMap(DeploymentUnit deploymentUnit) {
        DeploymentEJBDataProxyMap data = deploymentUnit.getAttachment(DeploymentEJBDataProxyMap.ATTACHMENT_KEY);
        if(data == null){
            data = new DeploymentEJBDataProxyMap();
            deploymentUnit.putAttachment(data.ATTACHMENT_KEY, data);
        }
        return data;
    }

    @Override
    public void undeploy(DeploymentUnit context) {

    }

    private String getGlobalBinding(Set<String> bindings) {
        for (String binding : bindings) {
            if (binding.startsWith("java:global") && binding.contains("!")) {
                return binding;
            }
        }
        return null;
    }
}
