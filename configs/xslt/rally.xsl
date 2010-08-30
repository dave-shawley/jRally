<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="xml"/>

	<xsl:template match="html">
		<xsl:for-each select="child::*">
			<xsl:copy-of select="."/>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="Description">
		<xsl:apply-templates select="html"/>
	</xsl:template>

	<xsl:template match="HierarchicalRequirement|DomainObject[@type='HierarchicalRequirement']">
		<xsl:variable name="name" select="@refObjectName"/>
		<story>
			<short-name>
				<xsl:choose>
					<xsl:when test="string-length($name) &gt; 30">
						<xsl:value-of select="substring($name,1,30)"/>
						<xsl:text>&#x2026;</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$name"/>
					</xsl:otherwise>
				</xsl:choose>
			</short-name>
			<full-name><xsl:value-of select="$name"/></full-name>
			<identifier><xsl:value-of select="FormattedID/text()"/></identifier>
			<description>
				<xsl:apply-templates select="Description"/>
			</description>
			<owner><xsl:value-of select="Owner/text()"/></owner>
			<estimate><xsl:value-of select="PlanEstimate/text()"/></estimate>
		</story>
	</xsl:template>

</xsl:stylesheet>