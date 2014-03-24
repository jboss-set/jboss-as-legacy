<?xml version="1.0" encoding="UTF-8"?>

<!-- Document : dependency.xsl Created on : 21 03 2014, Author : baranowb 
    Description: add dep in naming eap5 module on ejb3 eap5 module, to enable 
    jboss-serialization to load ejb3 classes -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="2.0" xmlns:module10="urn:jboss:module:1.0"
    exclude-result-prefixes="module10">

    <xsl:output method="xml" version="1.0" encoding="UTF-8"
        indent="yes" />

    <xsl:template match="node()|@*"
        exclude-result-prefixes="yes">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="//module10:dependencies">
        <xsl:copy>
            <xsl:apply-templates />
            <xsl:choose>
                <xsl:when test="module/@name='org.jboss.legacy.ejb3.eap5'">
                </xsl:when>
                <xsl:otherwise>
                    <module name="org.jboss.legacy.ejb3.eap5"
                        optional="true" />
                </xsl:otherwise>
            </xsl:choose>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
