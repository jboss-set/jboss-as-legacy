#!/bin/sh

#mvn clean install -s ~/redhat/git/jboss-eap/tools/maven/conf/settings.xml -Dmaven.repo.local=~/redhat/tmp/maven-local2 
 
mvn clean install

if [ "x$JBOSS_HOME" = "x" ]; then
    # get the full path (without any relative bits)
    echo "Set ENV JBOSS_HOME!"
    return 1
fi

cp -Rf eap5/target/module/org $JBOSS_HOME/modules/system/layers/base/
cp -Rf eap6/spi/target/module/org $JBOSS_HOME/modules/system/layers/base/
cp -Rf eap6/connector/target/module/org $JBOSS_HOME/modules/system/layers/base/
cp -Rf eap6/ejb3/target/module/org $JBOSS_HOME/modules/system/layers/base/
cp -Rf eap6/ejb3-bridge/target/module/org $JBOSS_HOME/modules/system/layers/base/
#cp -Rf eap6/tx/target/module/org $JBOSS_HOME/modules/system/layers/base/


echo "Edit configuration file - for instance $JBOSS_HOME/standalone/configuration/standalone.xml"

echo
echo "To enable Remoting Connector:"
echo "1 Add extension definition in <extensions>."
echo
echo "<extension module=\"org.jboss.legacy.ejb3.connector\"/>"
echo
echo "2. Add subsystem definition(no args == default IP/port)"
echo
echo "<subsystem xmlns=\"urn:jboss:domain:legacy-connector:1.0\">"
echo "    <remoting socket-binding=\"remoting-socket-binding\"/>"
echo "</subsystem>"
echo
echo "3. Define socket binding"
echo
echo "<socket-binding name=\"remoting-socket-binding\" port=\"4873\"/>"
echo
echo "To enable EJB3:"
echo "1 Add extension definition in <extensions>."
echo
echo "<extension module=\"org.jboss.legacy.ejb3.proxy\"/>"
echo
echo "2. Add subsystem definition"
echo
echo "<subsystem xmlns=\"urn:jboss:domain:legacy-ejb3:1.0\">"
echo "    <ejb3-registrar/>"
echo "</subsystem>"
echo
echo "To enable EJB3-Bridge:"
echo "1 Add extension definition in <extensions>."
echo
echo "<extension module=\"org.jboss.legacy.ejb3.bridge\"/>"
echo
echo "2. Add subsystem definition(no args == default IP/port)"
echo
echo "<subsystem xmlns=\"urn:jboss:domain:legacy-ejb3-bridge:1.0\"/>"
echo
echo
echo "To enable User-transaction:"
echo "1 Add extension definition in <extensions>."
echo
echo "<extension module=\"org.jboss.legacy.tx\"/>"
echo
echo "2. Add subsystem definition"
echo
echo "<subsystem xmlns=\"urn:jboss:domain:legacy-tx:1.0\"/>"
echo
