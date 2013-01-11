<?xml version="1.0" encoding = "UTF-8"?>
<!--
	#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	#
	# Copyright (C) 2003-2009 Open Microscopy Environment
	#       Massachusetts Institute of Technology,
	#       National Institutes of Health,
	#       University of Dundee,
	#       University of Wisconsin at Madison
	#
	#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-->

<!--
	#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	# Written by:  Will Moore
	#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="html" version="1.0" encoding="UTF-8" indent="yes"/>

	<xsl:template match="/">
		<html>
			<head>
				<title>OME Schema Files</title>
				<link rel="stylesheet" type="text/css" href="http://www.openmicroscopy.org/Schemas/Styling/2009-09/displayStyles.css"/>
				<script src="http://www.openmicroscopy.org/Schemas/Styling/2009-09/jquery.js" type="text/javascript"/>
				<script src="http://www.openmicroscopy.org/Schemas/Styling/2009-09/displayScript.js" type="text/javascript"/>
			</head>
			<body>
				<div id="header"/>
				<xsl:apply-templates/>
			</body>
		</html>
	</xsl:template>

	<!-- delegates to the next level of templates -->
	<xsl:template match="xsd:schema">
		<xsl:apply-templates/>
	</xsl:template>

	<!-- element may contain a reference or BE referenced -->
	<xsl:template match="xsd:element">
		<div class="element">
			<!-- if it has a name attribute, it is referenced: use ID. This *should* be unique in our schema-->
			<!-- although in practice, it is possible to have Eg. a complexType have the same name as an element -->
			<xsl:if test="@name != ''">
				<xsl:attribute name="id">
					<xsl:value-of select="@name"/>
				</xsl:attribute>
				<xsl:attribute name="class">namedElement</xsl:attribute>
			</xsl:if> Element: <!-- Show the name -->
			<xsl:if test="@name != ''"> name: <xsl:value-of select="@name"/>
			</xsl:if>
			<!-- if it has a ref attribute, it references another element (no id attribute) -->
			<!-- eg <xsd:element ref="Dataset" minOccurs="0" maxOccurs="unbounded"/> -->
			<xsl:if test="@ref != ''">
				<span class="ref">
					<!-- and add the ref attribute to identify the element we're referring to -->
					<xsl:attribute name="ref">
						<xsl:value-of select="@ref"/>
					</xsl:attribute> ref: <xsl:value-of select="@ref"/> (<xsl:value-of
						select="@minOccurs"/> - <xsl:value-of select="@maxOccurs"/>) </span> </xsl:if>
			<!-- if it has a type attribute, it references another element (no id attribute) -->
			<xsl:if test="@type != ''">
				<span class="type">
					<!-- and add the ref attribute to identify the element we're referring to -->
					<xsl:attribute name="ref">
						<xsl:value-of select="@type"/>
					</xsl:attribute> type: <xsl:value-of select="@type"/>
				</span> </xsl:if>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- complexContent - delegate below -->
	<xsl:template match="xsd:complexContent">
		<xsl:apply-templates/>
	</xsl:template>

	<!-- simpleContent - ignore -->
	<xsl:template match="xsd:simpleContent"> </xsl:template>

	<!-- extensions reference a complex type -->
	<xsl:template match="xsd:extension">
		<!-- add div to handle base -->
		<div> Extends: <span class="extension">
				<xsl:attribute name="ref">
					<xsl:value-of select="@base"/>
				</xsl:attribute>
				<xsl:value-of select="@base"/>
			</span>
		</div>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- attributes... -->
	<xsl:template match="xsd:attribute">
		<div class="attribute"> Attribute: <span class="name"><xsl:value-of select="@name"/></span>
			<!-- if required, add an asterisk -->
			<xsl:if test="@use = 'required'"> * </xsl:if>
			<!-- if it has a 'type' this is a reference to a simple type? -->
			<xsl:if test="@type != ''">
				<span class="type">
					<xsl:attribute name="ref">
						<xsl:value-of select="@type"/>
					</xsl:attribute> Type: <xsl:value-of select="@type"/>
				</span>
			</xsl:if>
		</div>
		<xsl:apply-templates/>
	</xsl:template>

	<!-- complex types -->
	<xsl:template match="xsd:complexType">
		<!-- if complex-type has a name attribute, it will be referenced within a div-->
		<xsl:choose>
			<xsl:when test="@name != ''">
				<div class="namedElement">
					<xsl:attribute name="id">
						<xsl:value-of select="@name"/>
					</xsl:attribute>
					<!-- display the name -->
					<div>Complex Type: <xsl:value-of select="@name"/></div>

					<!-- apply templates within the complexType div -->
					<xsl:apply-templates/>
				</div>
			</xsl:when>
			<!-- otherwise, simply apply templates -->
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- simple types -->
	<xsl:template match="xsd:simpleType">
		<div class="simpleType">
			<!-- if simple-type has a name attribute, it will be referenced -->
			<xsl:if test="@name != ''">
				<xsl:attribute name="id"><xsl:value-of select="@name"/></xsl:attribute>
				<xsl:attribute name="class">namedElement</xsl:attribute>
			</xsl:if>
			<!-- display the name --> Simple Type: <xsl:value-of select="@name"/>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- sequence types - simply delegate below... -->
	<xsl:template match="xsd:sequence">
		<xsl:apply-templates/>
	</xsl:template>

	<!-- choice types - simply delegate below, within div... -->
	<xsl:template match="xsd:choice">
		<div class="choice">
			<div class="choiceText">choice:</div>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

	<!-- restriction elements - can be xsd:enumeration, xsd:pattern or xsd: min/maxinclusive -->
	<xsl:template match="xsd:restriction">
		<div class="restriction">
			<xsl:if test="@base != ''">
				<!-- add div to handle base -->
				<div class="baseType">
					<xsl:attribute name="ref">
						<xsl:value-of select="@base"/>
					</xsl:attribute> Base: <b><xsl:value-of select="@base"/></b>
				</div>
			</xsl:if>
			<!-- display enumerations in a drop-down box -->
			<xsl:if test="count(xsd:enumeration) > 0">
				<form action="">Enumeration: <select name="">
						<xsl:for-each select="xsd:enumeration">
							<option><xsl:value-of select="@value"/></option>
						</xsl:for-each>
					</select>
				</form>
			</xsl:if>
			<!-- display min - max -->
			<xsl:if test="xsd:minInclusive != ''">
				<xsl:value-of select="xsd:minInclusive"/> - <xsl:value-of select="xsd:maxInclusive"
				/>
			</xsl:if>
			<xsl:if test="xsd:pattern"> Pattern: <xsl:value-of select="xsd:pattern/@value"/>
			</xsl:if>
		</div>
	</xsl:template>

	<!-- for annotations, simply display the documentataion -->
	<xsl:template match="xsd:annotation">
		<div class="doc">
			<xsl:value-of select="xsd:documentation"/>
		</div>
	</xsl:template>

</xsl:stylesheet>