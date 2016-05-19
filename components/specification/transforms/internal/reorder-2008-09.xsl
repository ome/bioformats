<?xml version = "1.0" encoding = "UTF-8"?>
<!--
  #%L
  BSD implementations of Bio-Formats readers and writers
  %%
  Copyright (C) 2005 - 2016 Open Microscopy Environment:
    - Board of Regents of the University of Wisconsin-Madison
    - Glencoe Software, Inc.
    - University of Dundee
  %%
  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are met:
  
  1. Redistributions of source code must retain the above copyright notice,
     this list of conditions and the following disclaimer.
  2. Redistributions in binary form must reproduce the above copyright notice,
     this list of conditions and the following disclaimer in the documentation
     and/or other materials provided with the distribution.
  
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
  POSSIBILITY OF SUCH DAMAGE.
  #L%
  -->

<!--
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# Written by:  Josh Moore, josh at glencoesoftware.com
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2008-09"
	xmlns:AML="http://www.openmicroscopy.org/Schemas/AnalysisModule/2008-09"
	xmlns:CLI="http://www.openmicroscopy.org/Schemas/CLI/2008-09"
	xmlns:MLI="http://www.openmicroscopy.org/Schemas/MLI/2008-09"
	xmlns:STD="http://www.openmicroscopy.org/Schemas/STD/2008-09"
	xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2008-09"
	xmlns:CA="http://www.openmicroscopy.org/Schemas/CA/2008-09"
	xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2008-09"
	xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2008-09"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xml="http://www.w3.org/XML/1998/namespace"
	exclude-result-prefixes="OME AML CLI MLI STD Bin CA SPW SA"
	xmlns:exsl="http://exslt.org/common"
	extension-element-prefixes="exsl" version="1.0">
	<!-- xmlns="http://www.openmicroscopy.org/Schemas/OME/2008-09"-->
	<xsl:variable name="newOMENS">http://www.openmicroscopy.org/Schemas/OME/2008-09</xsl:variable>
	<xsl:variable name="newSPWNS">http://www.openmicroscopy.org/Schemas/SPW/2008-09</xsl:variable>
	<xsl:variable name="newBINNS">http://www.openmicroscopy.org/Schemas/BinaryFile/2008-09</xsl:variable>
	<xsl:variable name="newCANS">http://www.openmicroscopy.org/Schemas/CA/2008-09</xsl:variable>
	<xsl:variable name="newSANS">http://www.openmicroscopy.org/Schemas/SA/2008-09</xsl:variable>
	<xsl:variable name="newSTDNS">http://www.openmicroscopy.org/Schemas/STD/2008-09</xsl:variable>
	<xsl:variable name="newAMLNS">http://www.openmicroscopy.org/Schemas/AnalysisModule/2008-09</xsl:variable>
	<xsl:variable name="newMLINS">http://www.openmicroscopy.org/Schemas/MLI/2008-09</xsl:variable>
	<xsl:variable name="newCLINS">http://www.openmicroscopy.org/Schemas/CLI/2008-09</xsl:variable>

	<xsl:output method="xml" indent="yes"/>
	<xsl:preserve-space elements="*"/>

	<!-- The order of all the Node with more then one type of children. -->
	<xsl:variable name="fixedOrders">
		<parentOrder name="OME">
			<child name="Project"/>
			<child name="Dataset"/>
			<child name="Experiment"/>
			<child name="Plate"/>
			<child name="Screen"/>
			<child name="Experimenter"/>
			<child name="Group"/>
			<child name="Instrument"/>
			<child name="Image"/>
			<child name="SemanticTypeDefinitions"/>
			<child name="AnalysisModuleLibrary"/>
			<child name="CustomAttributes"/>
			<child name="StructuredAnnotations"/>
		</parentOrder>
		<parentOrder name="Image">
			<child name="CreationDate"/>
			<child name="ExperimenterRef"/>
			<child name="Description"/>
			<child name="ExperimentRef"/>
			<child name="GroupRef"/>
			<child name="DatasetRef"/>
			<child name="InstrumentRef"/>
			<child name="ObjectiveRef"/>
			<child name="ImagingEnvironment"/>
			<child name="Thumbnail"/>
			<child name="LogicalChannel"/>
			<child name="DisplayOptions"/>
			<child name="StageLabel"/>
			<child name="Pixels"/>
			<child name="Region"/>
			<child name="CustomAttributes"/>
			<child name="ROI"/>
			<child name="MicrobeamManipulation"/>
		</parentOrder>
		<parentOrder name="Pixels">
			<child name="BinData"/>
			<child name="TiffData"/>
			<child name="Plane"/>
		</parentOrder>
		<parentOrder name="Plane">
			<child name="PlaneTiming"/>
			<child name="StagePosition"/>
			<child name="HashSHA1"/>
		</parentOrder>
		<parentOrder name="Experiment">
			<child name="Description"/>
			<child name="ExperimenterRef"/>
			<child name="MicrobeamManipulationRef"/>
		</parentOrder>
		<parentOrder name="MicrobeamManipulation">
			<child name="ROIRef"/>
			<child name="ExperimenterRef"/>
			<child name="LightSourceRef"/>
		</parentOrder>
		<parentOrder name="LogicalChannel">
			<child name="LightSourceRef"/>
			<child name="OTFRef"/>
			<child name="DetectorRef"/>
			<child name="FilterSetRef"/>
			<child name="ChannelComponent"/>
		</parentOrder>
		<parentOrder name="DisplayOptions">
			<child name="RedChannel"/>
			<child name="GreenChannel"/>
			<child name="BlueChannel"/>
			<child name="GreyChannel"/>
			<child name="Projection"/>
			<child name="Time"/>
			<child name="ROI"/>
		</parentOrder>
		<parentOrder name="Instrument">
			<child name="Microscope"/>
			<child name="LightSource"/>
			<child name="Detector"/>
			<child name="Objective"/>
			<child name="FilterSet"/>
			<child name="Filter"/>
			<child name="Dichroic"/>
			<child name="OTF"/>
		</parentOrder>
		<parentOrder name="Experimenter">
			<child name="FirstName"/>
			<child name="LastName"/>
			<child name="Email"/>
			<child name="Institution"/>
			<child name="OMEName"/>
			<child name="GroupRef"/>
		</parentOrder>
		<parentOrder name="Objective">
			<child name="Correction"/>
			<child name="Immersion"/>
			<child name="LensNA"/>
			<child name="NominalMagnification"/>
			<child name="CalibratedMagnification"/>
			<child name="WorkingDistance"/>
		</parentOrder>
		<parentOrder name="Project">
			<child name="Description"/>
			<child name="ExperimenterRef"/>
			<child name="GroupRef"/>
		</parentOrder>
		<parentOrder name="Group">
			<child name="Leader"/>
			<child name="Contact"/>
		</parentOrder>
		<parentOrder name="Shape">
			<child name="Channels"/>
			<child name="Rect"/>
			<child name="Mask"/>
			<child name="Ellipse"/>
			<child name="Circle"/>
			<child name="Point"/>
			<child name="Polygon"/>
			<child name="Polyline"/>
			<child name="Line"/>
		</parentOrder>
		<parentOrder name="MaskPixels">
			<child name="BinData"/>
			<child name="TiffData"/>
		</parentOrder>
		<parentOrder name="OTF">
			<child name="ObjectiveRef"/>
			<child name="FilterSetRef"/>
			<child name="BinaryFile"/>
		</parentOrder>
		<parentOrder name="Dataset">
			<child name="Description"/>
			<child name="ExperimenterRef"/>
			<child name="GroupRef"/>
			<child name="ProjectRef"/>
			<child name="CustomAttributes"/>
		</parentOrder>
		<parentOrder name="Region">
			<child name="Region"/>
			<child name="CustomAttributes"/>
		</parentOrder>
	</xsl:variable>

	<!-- Rewriting all namespaces -->

	<xsl:template match="OME:OME">
		<OME xmlns="http://www.openmicroscopy.org/Schemas/OME/2008-09"
			xmlns:CA="http://www.openmicroscopy.org/Schemas/CA/2008-09"
			xmlns:STD="http://www.openmicroscopy.org/Schemas/STD/2008-09"
			xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2008-09"
			xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2008-09"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2008-09 http://www.openmicroscopy.org/Schemas/OME/2008-09/ome.xsd">

			<xsl:apply-templates select="@*"/>
			<xsl:variable name="parentName">
				<xsl:value-of select="local-name(.)"/>
			</xsl:variable>
			<xsl:variable name="parentNode" select="."/>
			<xsl:variable name="parentOrderNode"
				select="exsl:node-set($fixedOrders)/parentOrder[@name=$parentName]"/>
			<xsl:for-each select="$parentOrderNode">
				<xsl:for-each select="*">
					<xsl:variable name="childName">
						<xsl:value-of select="@name"/>
					</xsl:variable>
					<xsl:for-each select="$parentNode/*[local-name(.)=$childName]">
						<xsl:apply-templates select="."/>
					</xsl:for-each>
				</xsl:for-each>
			</xsl:for-each>
		</OME>
	</xsl:template>

	<xsl:template match="OME:*">
		<xsl:element name="{name()}">
			<xsl:apply-templates select="@*"/>
			<xsl:variable name="parentName">
				<xsl:value-of select="local-name(.)"/>
			</xsl:variable>
			<xsl:variable name="parentNode" select="."/>
			<xsl:variable name="parentOrderNode"
				select="exsl:node-set($fixedOrders)/parentOrder[@name=$parentName]"/>
			<xsl:choose>
				<xsl:when test="count($parentOrderNode) = 0">
					<xsl:apply-templates select="node()"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:for-each select="$parentOrderNode">
						<xsl:for-each select="*">
							<xsl:variable name="childName">
								<xsl:value-of select="@name"/>
							</xsl:variable>
							<xsl:for-each select="$parentNode/*[local-name(.)=$childName]">
								<xsl:apply-templates select="."/>
							</xsl:for-each>
						</xsl:for-each>
					</xsl:for-each>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>

	<xsl:template match="CA:*">
		<xsl:element name="{name()}" namespace="{$newCANS}">
			<xsl:apply-templates select="@*|node()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="Bin:*">
		<xsl:element name="{name()}" namespace="{$newBINNS}">
			<xsl:apply-templates select="@*|node()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="STD:*">
		<xsl:element name="{name()}" namespace="{$newSTDNS}">
			<xsl:apply-templates select="@*|node()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="SPW:*">
		<xsl:element name="{name()}" namespace="{$newSPWNS}">
			<xsl:apply-templates select="@*|node()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="CLI:*">
		<xsl:element name="{name()}" namespace="{$newCLINS}">
			<xsl:apply-templates select="@*|node()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="MLI:*">
		<xsl:element name="{name()}" namespace="{$newMLINS}">
			<xsl:apply-templates select="@*|node()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="AML:*">
		<xsl:element name="{name()}" namespace="{$newAMLNS}">
			<xsl:apply-templates select="@*|node()"/>
		</xsl:element>
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
