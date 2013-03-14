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
	xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2007-06"
	xmlns:AML="http://www.openmicroscopy.org/Schemas/AnalysisModule/2007-06"
	xmlns:CLI="http://www.openmicroscopy.org/Schemas/CLI/2007-06"
	xmlns:MLI="http://www.openmicroscopy.org/Schemas/MLI/2007-06"
	xmlns:STD="http://www.openmicroscopy.org/Schemas/STD/2007-06"
	xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2007-06"
	xmlns:CA="http://www.openmicroscopy.org/Schemas/CA/2007-06"
	xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2007-06"
	xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2007-06"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xml="http://www.w3.org/XML/1998/namespace"
	exclude-result-prefixes="OME AML CLI MLI STD Bin CA SPW SA"
	xmlns:exsl="http://exslt.org/common"
	extension-element-prefixes="exsl" version="1.0">
	<!-- xmlns="http://www.openmicroscopy.org/Schemas/OME/2008-02"-->
	<xsl:variable name="newOMENS">http://www.openmicroscopy.org/Schemas/OME/2008-02</xsl:variable>
	<xsl:variable name="newSPWNS">http://www.openmicroscopy.org/Schemas/SPW/2008-02</xsl:variable>
	<xsl:variable name="newBINNS">http://www.openmicroscopy.org/Schemas/BinaryFile/2008-02</xsl:variable>
	<xsl:variable name="newCANS">http://www.openmicroscopy.org/Schemas/CA/2008-02</xsl:variable>
	<xsl:variable name="newSANS">http://www.openmicroscopy.org/Schemas/SA/2008-02</xsl:variable>
	<xsl:variable name="newSTDNS">http://www.openmicroscopy.org/Schemas/STD/2008-02</xsl:variable>
	<xsl:variable name="newAMLNS">http://www.openmicroscopy.org/Schemas/AnalysisModule/2008-02</xsl:variable>
	<xsl:variable name="newMLINS">http://www.openmicroscopy.org/Schemas/MLI/2008-02</xsl:variable>
	<xsl:variable name="newCLINS">http://www.openmicroscopy.org/Schemas/CLI/2008-02</xsl:variable>

	<xsl:output method="xml" indent="yes"/>
	<xsl:preserve-space elements="*"/>

	<!-- default value for non-numerical value when transforming the attribute of concrete shape -->
	<xsl:variable name="numberDefault" select="1"/>

	<!-- The Enumeration terms to be modified. 	-->
	<xsl:variable name="enumeration-maps">
		<mapping name="ExperimentType" unsure="Other">
		</mapping>
		<mapping name="LogicalChannelPhotometricInterpretation" optional="true">
		</mapping>
		<mapping name="LogicalChannelMode" unsure="Other">
		</mapping>
		<mapping name="LogicalChannelContractMethod" optional="true">
		</mapping>
		<mapping name="PixelsPixelType">
		</mapping>
		<mapping name="DetectorType" unsure="Unknown">
		</mapping>
		<mapping name="ArcType" unsure="Unknown">
		</mapping>
		<mapping name="LaserPulse">
		</mapping>
		<mapping name="LaserType" unsure="Unknown">
		</mapping>
		<mapping name="LaserMedium" unsure="Unknown">
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
		<xsl:variable name="newUnsureValue" select="($mappingNode)/@unsure"/>
		<xsl:choose>
			<xsl:when test="string-length($newValue) > 0">
				<xsl:value-of select="$newValue"/>
			</xsl:when>
			<!-- If the input file is valid this case should never happen, but if it does fix it -->
			<xsl:when test="string-length($value) = 0">
				<xsl:value-of select="$newUnsureValue"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$value"/>
				<!--
					The isOptional value is not used in this transform
				-->
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Actual schema changes -->

	<!--
	Remove ObjectiveSettingsRef from Image as schema broken
	-->
	<xsl:template match="OME:ObjectiveSettingsRef">
		<xsl:variable name="objectiveSettingsID" select="@ID"/>
		<xsl:comment>ObjectiveSettingsRef removed as no ObjectiveSettings in file - <xsl:value-of select="$objectiveSettingsID"/></xsl:comment>
	</xsl:template>

	<!--
	Rename ChannelInfo, remove element AuxLightSourceRef, process enums
	-->
	<xsl:template match="OME:Image">
		<xsl:element name="Image" namespace="{$newOMENS}">
			<xsl:apply-templates select="@*"/>
			<xsl:variable name="acquiredPixels">
				<xsl:for-each select="*[local-name(.) = 'AcquiredPixelsRef']">
					<xsl:value-of select="@ID"/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($acquiredPixels) > 0">
				<xsl:attribute name="AcquiredPixels">
					<xsl:value-of select="$acquiredPixels"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="*[not(local-name(.) = 'AcquiredPixelsRef')]"/>
		</xsl:element>
	</xsl:template>

	<!--
	In LightSource get Power from child if not set
	-->
	<xsl:template match="OME:LightSource">
		<xsl:element name="LightSource" namespace="{$newOMENS}">
			<xsl:variable name="pLightSource" select="@Power"/>
			<xsl:variable name="power">
				<xsl:choose>
					<xsl:when test="$pLightSource != ''">
						<xsl:value-of select="$pLightSource"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:for-each select="*">
							<xsl:variable name="pChild" select="@Power"/>
							<xsl:choose>
								<xsl:when test="$pChild != ''">
									<xsl:value-of select="@Power"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:number value="0"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:for-each select="@* [not(name() = 'Power')]">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
			<xsl:attribute name="Power">
				<xsl:value-of select="$power"/>
			</xsl:attribute>

			<xsl:apply-templates select="node()"/>
		</xsl:element>
	</xsl:template>

	<!--
	In Laser remove Power
	-->
	<xsl:template match="OME:Laser">
		<xsl:element name="Laser" namespace="{$newOMENS}">
			<xsl:for-each select="@* [not(name() = 'Power')]">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
			<xsl:apply-templates select="node()"/>
		</xsl:element>
	</xsl:template>

	<!--
	In Arc remove Power
	-->
	<xsl:template match="OME:Arc">
		<xsl:element name="Arc" namespace="{$newOMENS}">
			<xsl:for-each select="@* [not(name() = 'Power')]">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<!--
	In Filament remove Power
	-->
	<xsl:template match="OME:Filament">
		<xsl:element name="Filament" namespace="{$newOMENS}">
			<xsl:for-each select="@* [not(name() = 'Power')]">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<!--
	Convert Pixels
	-->
	<xsl:template match="OME:Pixels">
		<xsl:element name="Pixels" namespace="{$newOMENS}">
			<xsl:apply-templates select="@*[not(local-name(.)='PixelType')]"/>
			<xsl:for-each select="@* [name() = 'PixelType']">
				<xsl:attribute name="{local-name(.)}">
					<xsl:call-template name="transformEnumerationValue">
						<xsl:with-param name="mappingName" select="'PixelsPixelType'"/>
						<xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
					</xsl:call-template>
				</xsl:attribute>
			</xsl:for-each>
			<xsl:for-each select="ancestor::OME:Image">
				<xsl:for-each select="@* [name() = 'PixelSizeX']">
					<xsl:attribute name="PhysicalSizeX">
						<xsl:value-of select="."/>
					</xsl:attribute>
				</xsl:for-each>
				<xsl:for-each select="@* [name() = 'PixelSizeY']">
					<xsl:attribute name="PhysicalSizeY">
						<xsl:value-of select="."/>
					</xsl:attribute>
				</xsl:for-each>
				<xsl:for-each select="@* [name() = 'PixelSizeZ']">
					<xsl:attribute name="PhysicalSizeZ">
						<xsl:value-of select="."/>
					</xsl:attribute>
				</xsl:for-each>
				<xsl:for-each select="@* [name() = 'TimeIncrement']">
					<xsl:attribute name="TimeIncrement">
						<xsl:value-of select="."/>
					</xsl:attribute>
				</xsl:for-each>
				<xsl:for-each select="@* [name() = 'WaveStart']">
					<xsl:attribute name="WaveStart">
						<xsl:value-of select="."/>
					</xsl:attribute>
				</xsl:for-each>
				<xsl:for-each select="@* [name() = 'WaveIncrement']">
					<xsl:attribute name="WaveIncrement">
						<xsl:value-of select="."/>
					</xsl:attribute>
				</xsl:for-each>
			</xsl:for-each>
			<xsl:choose>
				<xsl:when test="local-name(*[1])='BinData'">
					<xsl:for-each select="* [local-name() = 'BinData' or local-name() = 'TiffData']">
						<xsl:apply-templates select="." mode="OnlyBinData"/>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:for-each select="* [local-name() = 'BinData' or local-name() = 'TiffData']">
						<xsl:apply-templates select="." mode="OnlyTiffData"/>
					</xsl:for-each>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:for-each select="* [not(local-name() = 'BinData' or local-name() = 'TiffData')]">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<!--
	In Bin:BinData add Length attribute.
	-->
	<xsl:template match="Bin:BinData">
		<xsl:element name="Bin:BinData" namespace="{$newBINNS}">
			<xsl:apply-templates select="@*"/>
			<xsl:variable name="contentLength" select="."/>
			<xsl:attribute name="Length"><xsl:value-of select="string-length($contentLength)"/></xsl:attribute>
			<xsl:apply-templates select="node()"/>
		</xsl:element>
	</xsl:template>

	<!--
	In Bin:BinData add Length attribute.
	-->
	<xsl:template match="Bin:BinData" mode="OnlyBinData">
		<xsl:element name="Bin:BinData" namespace="{$newBINNS}">
			<xsl:apply-templates select="@*"/>
			<xsl:variable name="contentLength" select="."/>
			<xsl:attribute name="Length"><xsl:value-of select="string-length($contentLength)"/></xsl:attribute>
			<xsl:apply-templates select="node()"/>
		</xsl:element>
	</xsl:template>

	<!--
	Skip BinData
	-->
	<xsl:template match="Bin:BinData" mode="OnlyTiffData">
		<xsl:comment>Skip BinData</xsl:comment>
	</xsl:template>

	<!--
	Convert TiffData
	-->
	<xsl:template match="OME:TiffData" mode="OnlyTiffData">
		<xsl:element name="TiffData" namespace="{$newOMENS}">
			<xsl:apply-templates select="@*|node()"/>
		</xsl:element>
	</xsl:template>

	<!--
	Skip TiffData
	-->
	<xsl:template match="OME:TiffData" mode="OnlyBinData">
		<xsl:comment>Skip TiffData</xsl:comment>
	</xsl:template>

	<!--
	Move all AML:Description Elements the OME namespace
	-->
	<xsl:template match="AML:Description">
		<xsl:element name="Description" namespace="{$newOMENS}">
			<xsl:apply-templates select="node()"/>
		</xsl:element>
	</xsl:template>

	<!-- Rewriting all namespaces -->

	<xsl:template match="OME:OME">
		<OME xmlns="http://www.openmicroscopy.org/Schemas/OME/2008-02"
			xmlns:CA="http://www.openmicroscopy.org/Schemas/CA/2008-02"
			xmlns:STD="http://www.openmicroscopy.org/Schemas/STD/2008-02"
			xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2008-02"
			xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2008-02"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2008-02 http://www.openmicroscopy.org/Schemas/OME/2008-02/ome.xsd">
			<xsl:apply-templates/>
		</OME>
	</xsl:template>

	<xsl:template match="OME:*">
		<xsl:element name="{name()}" namespace="{$newOMENS}">
			<xsl:apply-templates select="@*|node()"/>
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

	<!-- Follow useful list of functions -->
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

	<!--Convert value to PercentFraction -->
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

