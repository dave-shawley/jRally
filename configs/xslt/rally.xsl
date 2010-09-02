<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="xml"/>

	<xsl:template name="float-or-zero">
		<xsl:param name="value"/>
		<xsl:choose>
			<xsl:when test="string-length($value)=0">0.0</xsl:when>
			<xsl:otherwise><xsl:value-of select="$value"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="Description">
		<xsl:for-each select="child::*">
			<xsl:copy-of select="."/>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="HierarchicalRequirement|Defect">
		<xsl:variable name="name" select="@refObjectName"/>
		<xsl:variable name="state" select="ScheduleState/text()"/>
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
			<description/>
			<owner><xsl:value-of select="Owner/text()"/></owner>
			<estimate><xsl:value-of select="PlanEstimate/text()"/></estimate>
			<state>
				<xsl:choose>
					<!-- these are the valid story states -->
					<xsl:when test="$state = 'Backlog'">
						<xsl:text>NOT_STARTED</xsl:text>
					</xsl:when>
					<xsl:when test="$state = 'Defined'">
						<xsl:text>NOT_STARTED</xsl:text>
					</xsl:when>
					<xsl:when test="$state = 'In-Progress'">
						<xsl:text>IN_PROGRESS</xsl:text>
					</xsl:when>
					<xsl:when test="$state = 'Completed'">
						<xsl:text>COMPLETED</xsl:text>
					</xsl:when>
					<xsl:when test="$state = 'Accepted'">
						<xsl:text>ACCEPTED</xsl:text>
					</xsl:when>
					
					<!-- these are added for defects -->
					<xsl:when test="$state = 'Submitted'">
						<xsl:text>NOT_STARTED</xsl:text>
					</xsl:when>
					<xsl:when test="$state = 'Open'">
						<xsl:text>IN_PROGRESS</xsl:text>
					</xsl:when>
					<xsl:when test="$state = 'Fixed'">
						<xsl:text>COMPLETED</xsl:text>
					</xsl:when>
					<xsl:when test="$state = 'Closed'">
						<xsl:text>ACCEPTED</xsl:text>
					</xsl:when>
				</xsl:choose>
			</state>
		</story>
	</xsl:template>

	<xsl:template match="Task">
		<xsl:variable name="name" select="Name/text()"/>
		<xsl:variable name="state" select="State/text()"/>
		<task>
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
			<parent-identifier></parent-identifier>
			<description>
				<xsl:apply-templates select="Description"/>
			</description>
			<owner><xsl:value-of select="Owner/text()"/></owner>
			<detailed-estimate>
				<xsl:call-template name="float-or-zero">
					<xsl:with-param name="value" select="Estimate/text()"/>
				</xsl:call-template>
			</detailed-estimate>
			<todo-remaining>
				<xsl:call-template name="float-or-zero">
					<xsl:with-param name="value" select="ToDo/text()"/>
				</xsl:call-template>
			</todo-remaining>
			<effort-applied>
				<xsl:call-template name="float-or-zero">
					<xsl:with-param name="value" select="Actuals/text()"/>
				</xsl:call-template>
			</effort-applied>
			<state>
				<xsl:choose>
					<xsl:when test="$state = 'Defined'">
						<xsl:text>NOT_STARTED</xsl:text>
					</xsl:when>
					<xsl:when test="$state = 'In-Progress'">
						<xsl:text>IN_PROGRESS</xsl:text>
					</xsl:when>
					<xsl:when test="$state = 'Completed'">
						<xsl:text>FINISHED</xsl:text>
					</xsl:when>
					<xsl:when test="Blocked/text() = 'true'">
						<xsl:text>BLOCKED</xsl:text>
					</xsl:when>
				</xsl:choose>
			</state>
		</task>
	</xsl:template>

</xsl:stylesheet>