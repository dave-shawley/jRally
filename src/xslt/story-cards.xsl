<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="xml"/>


	<xsl:template name="separator">
		<xsl:choose>
			<xsl:when test="position() mod 2 = 0"></xsl:when>
			<xsl:when test="position() mod 2 = 1">
				<fo:block-container height="0.25in"><fo:block/></fo:block-container>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="ul|ol">
		<fo:block-container>
			<fo:list-block provisional-distance-between-starts="15mm"
				provisional-label-separation="5mm">
				<xsl:apply-templates/>
			</fo:list-block>
		</fo:block-container>
	</xsl:template>

	<xsl:template match="ul/li">
		<fo:list-item>
			<fo:list-item-label start-indent="0.25in">
				<fo:block>&#x2022;</fo:block>
			</fo:list-item-label>
			<fo:list-item-body start-indent="0.5in">
				<fo:block><xsl:apply-templates/></fo:block>
			</fo:list-item-body>
		</fo:list-item>
	</xsl:template>
	
	<xsl:template match="ol/li">
		<fo:list-item>
			<fo:list-item-label start-indent="0.25in">
				<fo:block><xsl:number format="1."/></fo:block>
			</fo:list-item-label>
			<fo:list-item-body start-indent="0.5in">
				<fo:block><xsl:apply-templates/></fo:block>
			</fo:list-item-body>
		</fo:list-item>
	</xsl:template>

	<xsl:template match="b">
		<fo:inline font-weight="bold"><xsl:apply-templates/></fo:inline>
	</xsl:template>

	<xsl:template match="i">
		<fo:inline font-style="italic"><xsl:apply-templates/></fo:inline>
	</xsl:template>

	<xsl:template match="tt">
		<fo:inline font-family="monospace"><xsl:apply-templates/></fo:inline>
	</xsl:template>

	<xsl:template match="br">
		<fo:block></fo:block>
	</xsl:template>

	<xsl:template match="p|div">
		<fo:block><xsl:apply-templates/></fo:block>
	</xsl:template>
	
	<xsl:template match="story">
		<fo:block-container height="4.5in" width="100%" border="4pt solid black" margin-bottom="0.25in">
			<fo:table border-collapse="collapse" table-layout="fixed">
				<fo:table-column column-width="6in"/>
				<fo:table-column column-width="1.5in"/>
				<fo:table-body>
					<fo:table-row font-size="24pt" font-weight="bold" line-height="1in">
						<fo:table-cell width="6in">
							<fo:block wrap-option="no-wrap" text-indent="1em">
								<xsl:apply-templates select="short-name"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="center">
								<xsl:apply-templates select="identifier"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row height="3in" border-top="2pt black solid">
						<fo:table-cell number-columns-spanned="2">
							<fo:block margin="0.25in" font-weight="bold">
								<xsl:apply-templates select="full-name"/>
							</fo:block>
							<fo:block margin="0.125in">
								<xsl:apply-templates select="description"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row font-size="14pt" font-weight="bold" line-height="0.5in">
						<fo:table-cell width="6in">
							<fo:block font-style="italic" text-indent="1em">
								<xsl:apply-templates select="owner"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="center">
								<xsl:variable name="num-points" select="estimate/text()"/>
								<xsl:value-of select="$num-points"/>
								<xsl:text> point</xsl:text>
								<xsl:if test="$num-points &gt; 1">
									<xsl:text>s</xsl:text>
								</xsl:if>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block-container>
	</xsl:template>

	<xsl:template match="story-list">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="spm" margin="0.5in" page-width="8.5in"
					page-height="11in">
					<fo:region-body/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="spm">
				<fo:flow flow-name="xsl-region-body">
					<xsl:apply-templates select="story"/>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

	<xsl:template match="task">
		<xsl:element name="fo:table-cell">
			<xsl:if test="position() mod 2 = 0">
				<xsl:attribute name="ends-row">true</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="padding-bottom">0.125in</xsl:attribute>
			<fo:block-container height="4.75in" width="3.75in" border="4pt solid black">
				<fo:table table-layout="fixed" width="100%">
					<fo:table-column column-width="1.875in"/>
					<fo:table-column column-width="1.875in"/>
					<fo:table-body>
						<fo:table-row font-size="10pt" font-style="italic" line-height="0.25in">
							<fo:table-cell>
								<fo:block text-align="left" margin-left="1em">
									<xsl:value-of select="parent-identifier/text()"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell>
								<fo:block text-align="right" margin-right="1em">
									<xsl:value-of select="owner/text()"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row  font-size="14pt" font-weight="bold">
							<fo:table-cell height="0.5in" number-columns-spanned="2" border-bottom="2pt black solid">
								<fo:block wrap-option="no-wrap" text-indent="1em">
									<xsl:value-of select="short-name/text()"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row font-size="10pt" font-weight="bold">
							<fo:table-cell height="0.25in" border-bottom="1pt solid black">
								<fo:block text-align="center" margin-top="3pt">TODO</fo:block>
							</fo:table-cell>
							<fo:table-cell border-left="1pt solid black" border-bottom="1pt solid black">
								<fo:block text-align="center" margin-top="3pt">Effort</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell height="0.5in">
								<fo:block text-align="center" margin-top="0.125in">
									<xsl:value-of select="todo-remaining/text()"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-left="1pt solid black">
								<fo:block text-align="center" margin-top="0.125in">
									<xsl:value-of select="effort-applied/text()"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
								<fo:block></fo:block>
							</fo:table-cell>
							<fo:table-cell height="3.2in" border-left="1pt solid black">
								<fo:block></fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
			</fo:block-container>
		</xsl:element>
		<xsl:if test="position() mod 2 = 1">
			<fo:table-cell><fo:block/></fo:table-cell>
		</xsl:if>
	</xsl:template>

	<xsl:template match="task-list">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="spm" margin="0.5in" page-width="8.5in"
					page-height="11in">
					<fo:region-body/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="spm">
				<fo:flow flow-name="xsl-region-body">
					<fo:table table-layout="fixed">
						<fo:table-column column-width="3.75in"/>
						<fo:table-column column-width="0.125in"/>
						<fo:table-column column-width="3.75in"/>
						<fo:table-body>
							<xsl:apply-templates select="task"/>
						</fo:table-body>
					</fo:table>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

</xsl:stylesheet>
