<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : enableJNP.xsl
    Created on : 11 février 2014, 09:28
    Author     : ehsavoie
    Description: The aim is to enable the JNP extension with the matching socket-bindings
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
                xmlns:jnp10="urn:jboss:domain:legacy-jnp:1.0"
                xmlns:connector10="urn:jboss:domain:legacy-connector:1.0"
                xmlns:ejb3-proxy10="urn:jboss:domain:legacy-ejb3:1.0"
                xmlns:ejb3-bridge10="urn:jboss:domain:legacy-ejb3-bridge:1.0"
                xmlns:usertx10="urn:jboss:domain:legacy-tx:1.0"
                xmlns:domain15="urn:jboss:domain:1.5"
                exclude-result-prefixes="domain15 jnp10 ejb3-bridge10 ejb3-proxy10 connector10 usertx10">

    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

    <xsl:param name="remoting-socket-binding" select="'4873'"/>
    <xsl:param name="jnpPort" select="'5599'"/>
    <xsl:param name="rmijnpPort" select="'1099'"/>

    <xsl:template match="node()|@*" exclude-result-prefixes="yes">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="//domain15:extensions">
        <xsl:copy>
            <xsl:apply-templates />
            <xsl:choose>
                <xsl:when test="//domain15:extension/@module='org.jboss.legacy.jnp'">
                </xsl:when>
                <xsl:otherwise>
                    <extension module="org.jboss.legacy.jnp" />
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="//domain15:extension/@module='org.jboss.legacy.ejb3.bridge'">
                </xsl:when>
                <xsl:otherwise>
                    <extension module="org.jboss.legacy.ejb3.bridge" />
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="//domain15:extension/@module='org.jboss.legacy.ejb3.connector'">
                </xsl:when>
                <xsl:otherwise>
                    <extension module="org.jboss.legacy.ejb3.connector" />
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="//domain15:extension/@module='org.jboss.legacy.ejb3.proxy'">
                </xsl:when>
                <xsl:otherwise>
                    <extension module="org.jboss.legacy.ejb3.proxy" />
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="//domain15:extension/@module='org.jboss.legacy.ejb3.tx'">
                </xsl:when>
                <xsl:otherwise>
                    <extension module="org.jboss.legacy.ejb3.tx" />
                </xsl:otherwise>
            </xsl:choose>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="node() | @*" name="copy">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>


    <xsl:template match="//*[local-name()='subsystem'][last()]">
        <xsl:call-template name="copy" />
        <xsl:choose>
            <xsl:when test="//jnp10:subsystem">
            </xsl:when>
            <xsl:otherwise>
                <subsystem xmlns="urn:jboss:domain:legacy-jnp:1.0">
                    <jnp-server/>
                    <jnp-connector socket-binding="jnp" rmi-socket-binding="rmi-jnp" />
                </subsystem>
                <subsystem xmlns="urn:jboss:domain:legacy-tx:1.0" />
                <subsystem xmlns="urn:jboss:domain:legacy-ejb3-bridge:1.0" />
                <subsystem xmlns="urn:jboss:domain:legacy-ejb3:1.0">
                    <ejb3-registrar/>
                </subsystem>
                <subsystem xmlns="urn:jboss:domain:legacy-connector:1.0">
                    <remoting socket-binding="remoting-socket-binding"/>
                </subsystem>
            </xsl:otherwise>
        </xsl:choose>
        <!--<xsl:choose>
            <xsl:when test="//ejb3-bridge10:subsystem">
            </xsl:when>
            <xsl:otherwise>
                <subsystem xmlns="urn:jboss:domain:legacy-ejb3-bridge:1.0" />
            </xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="//ejb3-proxy10:subsystem">
            </xsl:when>
            <xsl:otherwise>
                <subsystem xmlns="urn:jboss:domain:legacy-ejb3:1.0">
                    <ejb3-registrar/>
                </subsystem>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="//connector10:subsystem">
            </xsl:when>
            <xsl:otherwise>
                <subsystem xmlns="urn:jboss:domain:legacy-connector:1.0">
                    <remoting socket-binding="remoting-socket-binding"/>
                </subsystem>
            </xsl:otherwise>
        </xsl:choose>-->
    </xsl:template>

    <xsl:template match="//domain15:socket-binding-group/domain15:socket-binding[last()]">
        <xsl:call-template name="copy" />
        <xsl:choose>
            <xsl:when test="//domain15:socket-binding/@name='jnp'">
            </xsl:when>
            <xsl:when test="//domain15:socket-binding/@name='rmi-jnp'">
            </xsl:when>
            <xsl:when test="//domain15:socket-binding/@name='remoting-socket-binding'">
            </xsl:when>
            <xsl:otherwise>
                <socket-binding>
                    <xsl:attribute name="name">jnp</xsl:attribute>
                    <xsl:attribute name="port">
                        <xsl:value-of select="$jnpPort" />
                    </xsl:attribute>
                    <xsl:attribute name="interface">public</xsl:attribute>
                </socket-binding>
                <socket-binding>
                    <xsl:attribute name="name">rmi-jnp</xsl:attribute>
                    <xsl:attribute name="port">
                        <xsl:value-of select="$rmijnpPort" />
                    </xsl:attribute>
                    <xsl:attribute name="interface">public</xsl:attribute>
                </socket-binding>
                <socket-binding>
                    <xsl:attribute name="name">remoting-socket-binding</xsl:attribute>
                    <xsl:attribute name="port">
                        <xsl:value-of select="$remoting-socket-binding" />
                    </xsl:attribute>
                    <xsl:attribute name="interface">public</xsl:attribute>
                </socket-binding>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
