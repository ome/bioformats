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

	<!-- The Enumeration terms to be modified. -->
	<xsl:variable name="enumeration-maps">
		<mapping name="ChannelAcquisitionMode">
			<map from="LaserScanningMicroscopy" to="LaserScanningConfocalMicroscopy"/>
			<map from="LaserScanningConfocal" to="LaserScanningConfocalMicroscopy"/>
		</mapping>
	</xsl:variable>

	<!-- Transform the value coming from an enumeration -->
	<xsl:template name="transformEnumerationValue">
		<xsl:param name="mappingName"/>
		<xsl:param name="value"/>
		<!-- read the values from the mapping node -->
		<xsl:variable name="mappingNode"
			select="exsl:node-set($enumeration-maps)/mapping[@name=$mappingName]"/>
		<xsl:variable name="newValue" select="($mappingNode)/map[@from=$value]/@to"/>
		<xsl:variable name="isOptional" select="($mappingNode)/@optional"/>
		<xsl:choose>
			<xsl:when test="string-length($newValue) > 0">
				<xsl:value-of select="$newValue"/>
			</xsl:when>
			<xsl:when test="$value = 'Unknown'">
				<xsl:value-of select="'Other'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$value"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Actual schema changes -->

	<xsl:template match="SPW:Plate">
		<xsl:variable name="plateID"><xsl:value-of select="@* [name() = 'ID']"/></xsl:variable>
		<xsl:element name="{name()}" namespace="{$newSPWNS}">
			<xsl:apply-templates select="@*"/>
			<xsl:variable name="wellCount"><xsl:value-of select="count(* [local-name(.)='Well'])"/></xsl:variable>
			<xsl:apply-templates select="* [local-name(.)='Description']"/>
			<xsl:apply-templates select="* [local-name(.)='ScreenRef']"/>
			
			<!-- unused wellSampleCount
			<xsl:variable name="wellSampleCount">
				<xsl:call-template name="maxWellSampleCount">
					<xsl:with-param name="wellList" select="* [local-name(.)='Well']"/>
					<xsl:with-param name="wellIndex" select="count(* [local-name(.)='Well'])"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:comment> Max Well Sample: <xsl:number value="$wellSampleCount"/> </xsl:comment>
			-->
			
			<xsl:comment> Total Wells: <xsl:number value="$wellCount"/> </xsl:comment>
			<xsl:for-each select="* [local-name(.)='Well']">
				<xsl:variable name="wellNumber"><xsl:number value="position()"/></xsl:variable>
				<xsl:comment> Process Well #<xsl:number value="$wellNumber"/> </xsl:comment>
				<xsl:call-template name="convertWell">
					<xsl:with-param name="wellNode" select="."/>
					<xsl:with-param name="wellCount" select="$wellCount"/>
					<xsl:with-param name="wellNumber" select="$wellNumber"/>
				</xsl:call-template>
			</xsl:for-each>
			<xsl:apply-templates select="* [local-name(.)='AnnotationRef']"/>
			
			<!--
				get a list of all the ScreenAcquisitions that 
				have a WellSampleRef 
				to a WellSample 
				in a Well 
				in the current Plate
			-->
			<xsl:variable name="allWellSamples">
				<xsl:value-of select="descendant::* [local-name(.)='WellSample']"/>
			</xsl:variable>
			<xsl:for-each select="descendant::* [local-name(.)='WellSample']">
				<xsl:comment>Wellsample1</xsl:comment>
			</xsl:for-each>
			<xsl:for-each select="$allWellSamples">
				<xsl:comment>Wellsample2</xsl:comment>
			</xsl:for-each>
			<xsl:for-each select="* [local-name(.)='ScreenRef']">
				<xsl:variable name="associatedScreenAcquisitions">
					<xsl:call-template name="getAssociatedScreenAcquisitions">
						<xsl:with-param name="allScreenAcquisitions"><xsl:value-of select="$allWellSamples"/></xsl:with-param>
						<xsl:with-param name="allWellSamples"><xsl:value-of select="$allWellSamples"/></xsl:with-param>
					</xsl:call-template>
				</xsl:variable>
				<xsl:for-each select="$associatedScreenAcquisitions">
					<xsl:element name="PlateAcquisition" namespace="{$newSPWNS}">
						<xsl:attribute name="ID">PlateAcquisition:<xsl:value-of select="$plateID"/>:<xsl:value-of select="@* [name() = 'ID']"/></xsl:attribute>
						<xsl:for-each select="@* [not(name() = 'ID')]">
							<xsl:attribute name="{local-name(.)}">
								<xsl:value-of select="."/>
							</xsl:attribute>
						</xsl:for-each>
					</xsl:element>
				</xsl:for-each>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="getAssociatedScreenAcquisitions">
		<xsl:param name="allScreenAcquisitions"/>
		<xsl:param name="allWellSamples"/>
	</xsl:template>
	
	<!-- unused maxWellSampleCount
	<xsl:template name="maxWellSampleCount">
		<xsl:param name="wellList"/>
		<xsl:param name="wellIndex"/>
		<xsl:variable name="currentWellCount">
			<xsl:for-each select="$wellList">
				<xsl:if test="position() = $wellIndex">
					<xsl:value-of select="count(* [local-name(.)='WellSample'])"/>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="currentMaxWellCount">
			<xsl:choose>
				<xsl:when test="$wellIndex &lt; 0">
					<xsl:value-of select="0"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="maxWellSampleCount">
						<xsl:with-param name="wellList" select="$wellList"/>
						<xsl:with-param name="wellIndex" select="$wellIndex - 1"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$currentMaxWellCount &lt; $currentWellCount">
				<xsl:value-of select="$currentWellCount"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$currentMaxWellCount"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	-->
	
	<!-- SPW:Well - passing values through to well sample template -->
	<xsl:template name="convertWell">
		<xsl:param name="wellNode"/>
		<xsl:param name="wellCount"/>
		<xsl:param name="wellNumber"/>
		<xsl:element name="SPW:Well" namespace="{$newSPWNS}">
			<xsl:apply-templates select="@*"/>
			<xsl:for-each select="* [local-name(.)='WellSample']">
				<xsl:call-template name="convertWellSample">
					<xsl:with-param name="wellSampleNode" select="."/>
					<xsl:with-param name="wellCount" select="$wellCount"/>
					<xsl:with-param name="wellNumber" select="$wellNumber"/>
				</xsl:call-template>
			</xsl:for-each>
			<xsl:apply-templates select="* [local-name(.)='ReagentRef']"/>
			<xsl:apply-templates select="* [local-name(.)='AnnotationRef']"/>
		</xsl:element>
	</xsl:template>

	<!-- SPW:WellSample - adding index to well sample -->
	<xsl:template name="convertWellSample">
		<xsl:param name="wellSampleNode"/>
		<xsl:param name="wellCount"/>
		<xsl:param name="wellNumber"/>
		<xsl:element name="SPW:WellSample" namespace="{$newSPWNS}">
			<xsl:apply-templates select="@*"/>
			<xsl:attribute name="Index">
				<xsl:value-of select="(($wellNumber) + ($wellCount * (position() - 1)))"/>
			</xsl:attribute>
			<xsl:apply-templates select="*"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="SPW:ScreenAcquisition"/> <!-- Remove as converted to PlateAcquisition -->
	
	<xsl:template match="OME:Channel">
		<xsl:element name="Channel" namespace="{$newOMENS}">
			<xsl:for-each
				select="@* [not((name(.) = 'SecondaryEmissionFilter') or (name(.) = 'SecondaryExcitationFilter'))]">
				<xsl:choose>
					<xsl:when test="local-name(.)='AcquisitionMode'">
						<xsl:attribute name="{local-name(.)}">
							<xsl:call-template name="transformEnumerationValue">
								<xsl:with-param name="mappingName" select="'ChannelAcquisitionMode'"/>
								<xsl:with-param name="value">
									<xsl:value-of select="."/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="{local-name(.)}">
							<xsl:value-of select="."/>
						</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
			<xsl:apply-templates select="node()"/>
			<xsl:if test="@SecondaryEmissionFilter or @SecondaryExcitationFilter">
				<xsl:element name="LightPath" namespace="{$newOMENS}">
					<xsl:for-each select="@* [(name(.) = 'SecondaryExcitationFilter')]">
						<xsl:element name="ExcitationFilterRef" namespace="{$newOMENS}">
							<xsl:attribute name="ID">
								<xsl:value-of select="."/>
							</xsl:attribute>
						</xsl:element>
						<xsl:comment> Was SecondaryExcitationFilter</xsl:comment>
					</xsl:for-each>
					<xsl:for-each select="@* [(name(.) = 'SecondaryEmissionFilter')]">
						<xsl:element name="EmissionFilterRef" namespace="{$newOMENS}">
							<xsl:attribute name="ID">
								<xsl:value-of select="."/>
							</xsl:attribute>
						</xsl:element>
						<xsl:comment> Was SecondaryEmissionFilter</xsl:comment>
					</xsl:for-each>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>

	<xsl:template match="OME:Instrument">
		<xsl:element name="Instrument" namespace="{$newOMENS}">
			<xsl:apply-templates select="@*"/>
			<xsl:apply-templates select="node()"/>
			<!-- FIX ME
			<xsl:for-each
				select="@* [not((name(.) = 'AAA') or (name(.) = 'BBB'))]">
				<xsl:attribute name="{local-name(.)}">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:for-each>
			-->
		</xsl:element>
	</xsl:template>

	<!-- Rewriting all namespaces -->

	<xsl:template match="OME:OME">
		<OME xmlns="http://www.openmicroscopy.org/Schemas/OME/2010-04"
			xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2010-04"
			xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2010-04"
			xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2010-04"
			xmlns:ROI="http://www.openmicroscopy.org/Schemas/ROI/2010-04"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2010-04 http://cvs.openmicroscopy.org.uk/svn/specification/Xml/Working/ome.xsd">
<!-- FIX SCHEMA LOCATION -->
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

</xsl:stylesheet>
