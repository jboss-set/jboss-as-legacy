jboss-as-legacy-ejb3
======================

#Legacy EJB3 extension

The full documentation is at https://mojo.redhat.com/docs/DOC-928901

EAP5 used a different naming, ejb3 implementation and remote protocols than EAP6. 
Thus when you want to interact with EAP6 from an EAP5 client (or server) you are confronted with a lot of issues.

One solution would be to upgrade your client to EAP6, but sometimes this is not as simple as it seems.

For these cases we have developed a set of extensions for seamless integration of legacy clients with EAP6 and above.

#Full configuration example

    <extensions>  
    ...  
        <extension module="org.jboss.legacy.jnp"/>
        <extension module="org.jboss.legacy.ejb3.connector"/>
        <extension module="org.jboss.legacy.ejb3.proxy"/>
        <extension module="org.jboss.legacy.ejb3.bridge"/>
    </extensions>  
    ...  
    <subsystem xmlns="urn:jboss:domain:legacy-jnp:1.0">  
      <jnp-server/>  
      <jnp-connector socket-binding="jnp" rmi-socket-binding="rmi-jnp" />
    </subsystem>
    <subsystem xmlns="urn:jboss:domain:legacy-connector:1.0">
        <remoting socket-binding="remoting-socket-binding"/>
    </subsystem>
    <subsystem xmlns="urn:jboss:domain:legacy-ejb3:1.0">
        <ejb3-registrar/>
    </subsystem>
    <subsystem xmlns="urn:jboss:domain:legacy-ejb3-bridge:1.0"/>
     ...  
    <socket-binding-group>  
      ...  
      <socket-binding name="jnp" port="5599" interface="jnp"/>  
      <socket-binding name="rmi-jnp" port="1099" interface="jnp"/>
      <socket-binding name="remoting-socket-binding" port="4873"/>
      ...  
    </socket-binding-group> 
    

NOTE: jnp-connector is capable of using single IP/port pair - the rmi-socket-binding is not mandatory, it can be configured as follows:
<jnp-connector socket-binding="jnp"/>

#Build

##Simple build

Run _build.sh_

##Build and Test

Download a zip archive of EAP 6.2.1 (at least).

Run _build.sh -Djbossas.eap6.zip=/path to archive/jboss-eap-6.2.1-full-build.zip_

#Build and deploy

Download and install EAP 6.2.1 (at least).

Define the environment variable $JBOSS_HOME pointing towards your EAP6 installation.

Run _deploy.sh_

Define your configuration in the EAP6 server.

#Installation

Download the zip or tar.gz archive of the extension.

Unarchive it in the EAP6 installation directory.

Define your configuration in the EAP6 server. 
