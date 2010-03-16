<?xml version = "1.0" encoding = "UTF-8"?>
<!--
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#
# Copyright (C) 2009-2010 Open Microscopy Environment
#       Massachusetts Institute of Technology,
#       National Institutes of Health,
#       University of Dundee,
#       University of Wisconsin at Madison
#
#    This library is free software; you can redistribute it and/or
#    modify it under the terms of the GNU Lesser General Public
#    License as published by the Free Software Foundation; either
#    version 2.1 of the License, or (at your option) any later version.
#
#    This library is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
#    Lesser General Public License for more details.
#
#    You should have received a copy of the GNU Lesser General Public
#    License along with this library; if not, write to the Free Software
#    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
#
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-->

<!--
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# Written by:  Andrew Patterson: ajpatterson at lifesci.dundee.ac.uk,
#              Josh Moore, Jean-Marie Burel, Donald McDonald, Chris Allan
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2009-09"
	xmlns:CLI="http://www.openmicroscopy.org/Schemas/CLI/2009-09"
	xmlns:MLI="http://www.openmicroscopy.org/Schemas/MLI/2009-09"
	xmlns:STD="http://www.openmicroscopy.org/Schemas/STD/2009-09"
	xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2009-09"
	xmlns:CA="http://www.openmicroscopy.org/Schemas/CA/2009-09"
	xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2009-09"
	xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2009-09"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xml="http://www.w3.org/XML/1998/namespace" xmlns:exsl="http://exslt.org/common"
	extension-element-prefixes="exsl" version="1.0">
	<!-- xmlns="http://www.openmicroscopy.org/Schemas/OME/2009-09"-->
	<xsl:variable name="newOMENS">http://www.openmicroscopy.org/Schemas/OME/2010-04</xsl:variable>
	<xsl:variable name="newSPWNS">http://www.openmicroscopy.org/Schemas/SPW/2010-04</xsl:variable>
	<xsl:variable name="newBINNS">http://www.openmicroscopy.org/Schemas/BinaryFile/2010-04</xsl:variable>
	<xsl:variable name="newROINS">http://www.openmicroscopy.org/Schemas/ROI/2010-04</xsl:variable>
	<xsl:variable name="newSANS">http://www.openmicroscopy.org/Schemas/SA/2010-04</xsl:variable>

	<xsl:output method="xml" indent="yes"/>
	<xsl:preserve-space elements="*"/>

	<!-- default value for points attribute for Polygon and PolyLine -->
	<xsl:variable name="pointsDefault" select="'0,0 1,1'"/>

	<!-- default value for non-numerical value when transforming the attribute of concrete shape -->
	<xsl:variable name="numberDefault" select="1"/>

	<!-- Actual schema changes -->

	<!-- Rewriting all namespaces -->

	<xsl:template match="OME:OME">
		<OME xmlns="http://www.openmicroscopy.org/Schemas/OME/2009-09"
			xmlns:CA="http://www.openmicroscopy.org/Schemas/CA/2009-09"
			xmlns:STD="http://www.openmicroscopy.org/Schemas/STD/2009-09"
			xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2009-09"
			xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2009-09"
			xmlns:AML="http://www.openmicroscopy.org/Schemas/AnalysisModule/2009-09"
			xmlns:CLI="http://www.openmicroscopy.org/Schemas/CLI/2009-09"
			xmlns:MLI="http://www.openmicroscopy.org/Schemas/MLI/2009-09"
			xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2009-09"
			xmlns:ROI="http://www.openmicroscopy.org/Schemas/ROI/2009-09"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2009-09 http://www.openmicroscopy.org/Schemas/OME/2009-09/ome.xsd">
			<xsl:apply-templates/>
		</OME>
	</xsl:template>

	<xsl:template match="OME:*">
		<xsl:element name="{name()}" namespace="{$newOMENS}">
			<xsl:apply-templates select="@*|node()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="Bin:*">
		<xsl:element name="{name()}" namespace="{$newBINNS}">
			<xsl:apply-templates select="@*|node()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="SA:*">
		<xsl:element name="{name()}" namespace="{$newSANS}">
			<xsl:apply-templates select="@*|node()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="SPW:*">
		<xsl:element name="{name()}" namespace="{$newSPWNS}">
			<xsl:apply-templates select="@*|node()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="CA:*"> <!-- Removed -->
		<xsl:comment> Old Custom Attributes Removed </xsl:comment>
	</xsl:template>
	
	<xsl:template match="STD:*"> <!-- Removed -->
		<xsl:comment> Old Semantic Type Definitions Removed </xsl:comment>
	</xsl:template>

	<xsl:template match="MLI:*"> <!-- Removed -->
		<xsl:comment> Old Matlab Interface Removed </xsl:comment>
	</xsl:template>

	<xsl:template match="CLI:*"> <!-- Removed -->
		<xsl:comment> Old Command Line Interface Removed </xsl:comment>
	</xsl:template>

	<!-- Default processing -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="text()|processing-instruction()|comment()">
		<xsl:copy>
			<xsl:apply-templates select="node()"/>
		</xsl:copy>
	</xsl:template>

	<!-- Follow useful list of functions -->

	<!--
	convert the value of the color domain attribute of ChannelComponent.
	A limited number of strings is supported.
	-->
	<xsl:template name="convertColorDomain">
		<xsl:param name="cc"/>
		<xsl:choose>
			<xsl:when test="contains($cc,'red') or contains($cc,'r')">4278190335</xsl:when>
			<xsl:when test="contains($cc,'green') or contains($cc,'g')">16711935</xsl:when>
			<xsl:when test="contains($cc,'blue') or contains($cc,'b')">65535</xsl:when>
			<xsl:otherwise>4294967295</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Replace string -->
	<xsl:template name="replace-string-id">
		<xsl:param name="text"/>
		<xsl:param name="replace"/>
		<xsl:param name="replacement"/>
		<xsl:choose>
			<xsl:when test="contains($text, $replace)">
				<xsl:value-of select="substring-before($text, $replace)"/>
				<xsl:value-of select="$replacement"/>
				<xsl:value-of select="substring-after($text, $replace)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Control if a value is in the specified interval -->
	<xsl:template name="valueInInterval">
		<xsl:param name="value"/>
		<xsl:param name="min"/>
		<xsl:param name="max"/>
		<xsl:choose>
			<xsl:when test="$value &lt; $min">
				<xsl:value-of select="$min"/>
			</xsl:when>
			<xsl:when test="$value &gt; $max">
				<xsl:value-of select="$max"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$value"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Convert value to PercentFration -->
	<xsl:template name="convertPercentFraction">
		<xsl:param name="value"/>
		<xsl:variable name="min" select="0"/>
		<xsl:variable name="max" select="1"/>
		<xsl:choose>
			<xsl:when test="$value &lt; $min">
				<xsl:value-of select="$min"/>
			</xsl:when>
			<xsl:when test="$value &gt; $max">
				<xsl:call-template name="convertPercentFraction">
					<xsl:with-param name="value">
						<xsl:value-of select="$value div 100"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$value"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- 
	Controls if a value is greater than or less than depending on the type. 
	The types are greater or less.
	-->
	<xsl:template name="isValueValid">
		<xsl:param name="value"/>
		<xsl:param name="control"/>
		<xsl:param name="type"/>
		<xsl:choose>
			<xsl:when test="$type = 'less'">
				<xsl:choose>
					<xsl:when test="$value &lt; $control">
						<xsl:value-of select="$control"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$value"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$value &gt; $control">
						<xsl:value-of select="$control"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$value"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Check if the passed value is a number, if not extract number if any -->
	<xsl:template name="formatNumber">
		<xsl:param name="value"/>
		<xsl:choose>
			<!-- number already -->
			<xsl:when test="number($value)=number($value)">
				<xsl:value-of select="$value"/>
			</xsl:when>
			<xsl:otherwise>
				<!-- try to find a number -->
				<xsl:value-of select="$numberDefault"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
