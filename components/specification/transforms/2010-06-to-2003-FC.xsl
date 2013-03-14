<?xml version = "1.0" encoding = "UTF-8"?>
<!--
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#
# Copyright (C) 2009-2011 Open Microscopy Environment
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
	xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2010-06"
	xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2010-06"
	xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2010-06"
	xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2010-06"
	xmlns:ROI="http://www.openmicroscopy.org/Schemas/ROI/2010-06"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xml="http://www.w3.org/XML/1998/namespace"
	exclude-result-prefixes="OME Bin SPW SA ROI"
	xmlns:exsl="http://exslt.org/common"
	extension-element-prefixes="exsl" version="1.0">

	<xsl:variable name="newOMENS">http://www.openmicroscopy.org/XMLschemas/OME/FC/ome.xsd</xsl:variable>
	<xsl:variable name="newBINNS"
		>http://www.openmicroscopy.org/XMLschemas/BinaryFile/RC1/BinaryFile.xsd</xsl:variable>

	<xsl:output method="xml" indent="yes"/>
	<xsl:preserve-space elements="*"/>

	<!-- default value for non-numerical value when transforming the attribute of concrete shape -->
	<xsl:variable name="numberDefault" select="1"/>

	<!-- The Enumeration terms to be modified. -->
	<xsl:variable name="enumeration-maps">
		<mapping name="PixelsPixelType">
			<map
				from="uint8"
				to="Uint8"
			/>
			<map
				from="uint16"
				to="Uint16"
			/>
			<map
				from="uint32"
				to="Uint32"
			/>
		</mapping>
		<mapping name="LogicalChannelMode">
			<map
				to="Wide-field"
				from="WideField"
			/>
			<map to="Laser Scanning Confocal"
				from="LaserScanningConfocalMicroscopy"
			/>
			<map to="Spinning Disk Confocal"
				from="SpinningDiskConfocal"
			/>
			<map to="Slit Scan Confocal"
				from="SlitScanConfocal"
			/>
			<map to="Multi-Photon Microscopy"
				from="MultiPhotonMicroscopy"
			/>
			<map to="Structured Illumination"
				from="StructuredIllumination"
			/>
			<map to="Single Molecule Imaging"
				from="SingleMoleculeImaging"
			/>
			<map to="Total Internal Reflection"
				from="TotalInternalReflection"
			/>
			<map to="Fluorescence-Lifetime"
				from="FluorescenceLifetime"
			/>
			<map to="Spectral Imaging"
				from="SpectralImaging"
			/>
			<map
				to="Fluorescence Correlation Spectroscopy"
				from="FluorescenceCorrelationSpectroscopy"
			/>
			<map
				to="Near Field Scanning Optical Microscopy"
				from="NearFieldScanningOpticalMicroscopy"
			/>
			<map
				to="Second Harmonic Generation Imaging"
				from="SecondHarmonicGenerationImaging"
			/>
		</mapping>
		<mapping name="theAtribute">
			<map from="oldName" to="newName"/>
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
			<!-- If the input file is valid this case should never happen, but if it does fix it -->
			<xsl:when test="string-length($value) = 0">
				<xsl:value-of select="'Other'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$value"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
	convert the value of the Color attribute of Channel to a ColorDomain
	attribute on ChannelComponent.
	A limited number of colours are supported, others map to w for (white).
	-->
	<xsl:template name="convertToColorDomain">
		<xsl:param name="cc"/>
		<xsl:choose>
			<xsl:when test="contains($cc,'-16776961')">r</xsl:when>
			<xsl:when test="contains($cc,'16711935')">g</xsl:when>
			<xsl:when test="contains($cc,'65535')">b</xsl:when>
			<xsl:otherwise>w</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Actual schema changes -->
	<xsl:template match="OME:AcquiredDate">
		<xsl:element name="CreationDate" namespace="{$newOMENS}">
			<xsl:apply-templates select="node()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="OME:Image">
		<xsl:element name="{name()}" namespace="{$newOMENS}">
			<xsl:for-each select="@* [not(name(.) = 'ID')]">
				<xsl:attribute name="{local-name(.)}">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:for-each>
			<xsl:for-each select="@* [name(.) = 'ID']">
				<xsl:attribute name="ID">xslt.fix:Image:XSLT:<xsl:value-of select="."/></xsl:attribute>
			</xsl:for-each>
			<xsl:attribute name="DefaultPixels">
				<xsl:variable name="firstPixels">xslt.fix:Pixels:XSLT:<xsl:for-each select="* [name(.) = 'Pixels'][1]">
						<xsl:value-of select="@ID"/>
					</xsl:for-each>
				</xsl:variable>
				<xsl:value-of select="$firstPixels"/>
			</xsl:attribute>

			<xsl:choose>
				<xsl:when test="local-name(*[1])='AcquiredDate'">
					<xsl:apply-templates select="* [name(.) = 'AcquiredDate']"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:comment>Creation Date inserted by XSLT downgrade (set to 1st January 1970)</xsl:comment>
					<xsl:element name="CreationDate" namespace="{$newOMENS}">1970-01-01T00:00:00.0Z</xsl:element>
				</xsl:otherwise>
			</xsl:choose>

			<xsl:for-each select=" descendant::OME:Channel">
				<xsl:element name="ChannelInfo" namespace="{$newOMENS}">
					<xsl:for-each select="@* [name(.) = 'ID']">
						<xsl:attribute name="ID">xslt.fix:LogicalChannel:XSLT:<xsl:value-of select="."/></xsl:attribute>
					</xsl:for-each>
					<xsl:for-each select="@* [name(.) = 'AcquisitionMode']">
						<xsl:attribute name="Mode">
							<xsl:call-template name="transformEnumerationValue">
								<xsl:with-param name="mappingName" select="'LogicalChannelMode'"/>
								<xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
							</xsl:call-template>
						</xsl:attribute>
					</xsl:for-each>

					<xsl:element name="ChannelComponent" namespace="{$newOMENS}">
						<xsl:attribute name="Pixels">xslt.fix:Pixels:XSLT:<xsl:for-each select=" parent::node()">
								<xsl:value-of select="@ID"/>
							</xsl:for-each>
						</xsl:attribute>
						<xsl:attribute name="ColorDomain">
							<xsl:call-template name="convertToColorDomain">
								<xsl:with-param name="cc" select="@Color"/>
							</xsl:call-template>
						</xsl:attribute>
						<xsl:attribute name="Index"><xsl:value-of select="position()"/></xsl:attribute>
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
			<xsl:apply-templates select="* [name(.) = 'Pixels']"/>

		</xsl:element>
	</xsl:template>

	<xsl:template match="OME:Pixels">
		<xsl:element name="{name()}" namespace="{$newOMENS}">
			<xsl:for-each select="@* [name(.) = 'ID']">
				<xsl:attribute name="ID">xslt.fix:Pixels:XSLT:<xsl:value-of select="."/></xsl:attribute>
			</xsl:for-each>
			<xsl:for-each select="@* [name(.) = 'Type']">
				<xsl:attribute name="PixelType">
					<xsl:call-template name="transformEnumerationValue">
						<xsl:with-param name="mappingName" select="'PixelsPixelType'"/>
						<xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
					</xsl:call-template>
				</xsl:attribute>
			</xsl:for-each>
			<xsl:attribute name="BigEndian">
				<xsl:variable name="valueBigEndian">
					<xsl:for-each select="* [local-name(.) = 'BinData'][1]">
						<xsl:value-of select="@BigEndian"/>
					</xsl:for-each>
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="string-length($valueBigEndian) = 0">
						<xsl:value-of select="'false'"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$valueBigEndian"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:for-each select="@* [name(.) = 'DimensionOrder' or name(.) = 'SizeC' or name(.) = 'SizeT' or name(.) = 'SizeX' or name(.) = 'SizeY' or name(.) = 'SizeZ']">
				<xsl:attribute name="{local-name(.)}">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:for-each>
			<xsl:apply-templates select="* [local-name(.) = 'BinData']"/>
			<xsl:apply-templates select="* [local-name(.) = 'TiffData']"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="Bin:BinData">
		<xsl:element name="{name()}" namespace="{$newBINNS}">
			<xsl:for-each select="@* [not(name(.) = 'BigEndian' or name(.) = 'Length')]">
				<xsl:attribute name="{local-name(.)}">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:for-each>
			<xsl:apply-templates select="node()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="OME:TiffData">
		<xsl:element name="{name()}" namespace="{$newOMENS}">
			<xsl:for-each select="@* [not(name(.) = 'PlaneCount')]">
				<xsl:attribute name="{local-name(.)}">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:for-each>
			<xsl:for-each select="@* [name(.) = 'PlaneCount']">
				<xsl:attribute name="NumPlanes">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>


	<xsl:template match="ROI:*"/>
	<xsl:template match="SA:*"/>
	<xsl:template match="SPW:*"/>


	<!-- Rewriting all namespaces -->

	<xsl:template match="OME:OME">
		<OME xmlns="http://www.openmicroscopy.org/XMLschemas/OME/FC/ome.xsd"
			xmlns:Bin="http://www.openmicroscopy.org/XMLschemas/BinaryFile/RC1/BinaryFile.xsd"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://www.openmicroscopy.org/XMLschemas/OME/FC/ome.xsd
			http://www.openmicroscopy.org/XMLschemas/OME/FC/ome.xsd">
			<xsl:apply-templates select="* [local-name(.) = 'Image']"/>
		</OME>
	</xsl:template>

	<!-- Default processing -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
