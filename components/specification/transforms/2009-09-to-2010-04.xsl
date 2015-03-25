<?xml version = "1.0" encoding = "UTF-8"?>
<!--
  #%L
  BSD implementations of Bio-Formats readers and writers
  %%
  Copyright (C) 2005 - 2015 Open Microscopy Environment:
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
	xmlns:ROI="http://www.openmicroscopy.org/Schemas/ROI/2009-09"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xml="http://www.w3.org/XML/1998/namespace"
	exclude-result-prefixes="OME CLI MLI STD Bin CA SPW SA ROI"
	xmlns:exsl="http://exslt.org/common"
	extension-element-prefixes="exsl" version="1.0">
	<!-- xmlns="http://www.openmicroscopy.org/Schemas/OME/2009-09"-->
	<xsl:variable name="newOMENS">http://www.openmicroscopy.org/Schemas/OME/2010-04</xsl:variable>
	<xsl:variable name="newSPWNS">http://www.openmicroscopy.org/Schemas/SPW/2010-04</xsl:variable>
	<xsl:variable name="newBINNS"
		>http://www.openmicroscopy.org/Schemas/BinaryFile/2010-04</xsl:variable>
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
		<mapping name="ShapeFillRule">
			<map from="even-odd" to="EvenOdd"/>
			<map from="Even-Odd" to="EvenOdd"/>
			<map from="evenodd" to="EvenOdd"/>
			<map from="Non-Zero" to="NonZero"/>
			<map from="Nonzero" to="NonZero"/>
			<map from="nonzero" to="NonZero"/>
			<map from="non-zero" to="NonZero"/>
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

	<!-- Actual schema changes -->

	<!-- Create PlateAcquisition, Remove DefaultSample -->
	<xsl:template match="SPW:Plate">
		<xsl:variable name="plateID">
			<xsl:value-of select="@* [name() = 'ID']"/>
		</xsl:variable>
		<xsl:element name="{name()}" namespace="{$newSPWNS}">
			<!-- copy all attributes except DefaultSample -->
			<xsl:apply-templates select="@*"/>
			<xsl:for-each select="@* [not(name(.) = 'DefaultSample')]">
				<xsl:attribute name="{local-name(.)}">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:for-each>

			<!-- Copy unchanged children -->
			<xsl:apply-templates select="* [local-name(.)='Description']"/>
			<xsl:apply-templates select="* [local-name(.)='ScreenRef']"/>


			<!-- begin copying Well (and adding WellSampleIndex) -->
			<xsl:variable name="wellCount">
				<xsl:value-of select="count(* [local-name(.)='Well'])"/>
			</xsl:variable>
			<xsl:for-each select="* [local-name(.)='Well']">
				<xsl:variable name="wellNumber">
					<xsl:number value="position()"/>
				</xsl:variable>
				<xsl:call-template name="convertWell">
					<xsl:with-param name="wellNode" select="."/>
					<xsl:with-param name="wellCount" select="$wellCount"/>
					<xsl:with-param name="wellNumber" select="$wellNumber"/>
				</xsl:call-template>
			</xsl:for-each>
			<!-- end copying Well -->

			<!-- Copy unchanged children -->
			<xsl:apply-templates select="* [local-name(.)='AnnotationRef']"/>

			<!-- begin creating PlateAcquisition -->
			<xsl:variable name="allWellSamplesInCurrentPlate" select="descendant::SPW:WellSample"/>
			<xsl:for-each select="* [local-name(.)='ScreenRef']">
				<!--
					get a list of all the ScreenAcquisitions
					in screens referenced by the current plate that
					have a WellSampleRef
					to a WellSample
					in a Well
					in the current Plate
				-->
				<xsl:variable name="theScreenID"><xsl:value-of select="@ID"/></xsl:variable>
				<xsl:call-template name="getAssociatedScreenAcquisitions">
					<xsl:with-param name="allAcquisitionsInReferencedScreens"
						select="//SPW:ScreenAcquisition [ancestor::node()/@ID=$theScreenID]"/>
					<xsl:with-param name="allWellSamplesInCurrentPlate" select="$allWellSamplesInCurrentPlate"/>
					<xsl:with-param name="plateID" select="$plateID"/>
				</xsl:call-template>
			</xsl:for-each>
			<!-- end creating PlateAcquisition -->
		</xsl:element>
	</xsl:template>

	<xsl:template name="getAssociatedScreenAcquisitions">
		<xsl:param name="allAcquisitionsInReferencedScreens"/>
		<xsl:param name="allWellSamplesInCurrentPlate"/>
		<xsl:param name="plateID"/>
		<xsl:for-each select="$allAcquisitionsInReferencedScreens">

			<!--
				if
				the ID WellSampleRef in the ScreenAcquisition
				matches an ID in the list allWellSamplesInCurrentPlate
				then
				make a PlateAcquisition
				else
				do nothing
			-->

			<xsl:variable name="myFlag">
				<xsl:for-each select="child::SPW:WellSampleRef">
					<xsl:variable name="theSearchID">
						<xsl:value-of select="@ID"/>
					</xsl:variable>
					<xsl:if test="count($allWellSamplesInCurrentPlate [@ID = $theSearchID] ) &gt; 0">
						<!-- <xsl:comment> Match: <xsl:value-of select="$theSearchID"/></xsl:comment> -->
						<xsl:value-of select="'hit'"/>
					</xsl:if>
				</xsl:for-each>
			</xsl:variable>

			<xsl:if test="contains($myFlag,'hit')">
				<xsl:element name="PlateAcquisition" namespace="{$newSPWNS}">
					<xsl:attribute name="ID">PlateAcquisition:<xsl:value-of select="$plateID"
							/>:<xsl:value-of select="@* [name() = 'ID']"/></xsl:attribute>
					<xsl:for-each select="@* [not(name() = 'ID')]">
						<xsl:attribute name="{local-name(.)}">
							<xsl:value-of select="."/>
						</xsl:attribute>
					</xsl:for-each>
					<xsl:apply-templates select="*"/>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

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

	<xsl:template match="SPW:ScreenAcquisition"/>
	<!-- Remove as converted to PlateAcquisition -->

	<!--
		Althought FilterSet has changed the old content is compatible with the
		new structure so no processing needed.
	-->

	<!-- Remove Secondary Filters, Create LightPath, Update AcquisitionMode enumeration -->
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

	<!-- Convert ChannelRef to TheC, Move ShapeDisplayOptions attributes onto Shape -->
	<xsl:template match="ROI:Shape">
		<xsl:element name="Shape" namespace="{$newROINS}">
			<xsl:apply-templates select="@*"/>
			<!-- Move ShapeDisplayOptions attributes onto Shape -->
			<xsl:for-each select="* [local-name(.)='ShapeDisplayOptions']">
				<xsl:for-each select="@*">
					<xsl:choose>
						<xsl:when test="local-name(.)='FillRule'">
							<xsl:attribute name="{local-name(.)}">
								<xsl:call-template name="transformEnumerationValue">
									<xsl:with-param name="mappingName" select="'ShapeFillRule'"/>
									<xsl:with-param name="value">
										<xsl:value-of select="."/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:attribute>
						</xsl:when>
						<xsl:when test="local-name(.)='Text'">
							<xsl:attribute name="Label">
								<xsl:value-of select="."/>
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="{local-name(.)}">
								<xsl:value-of select="."/>
							</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</xsl:for-each>

			<!-- If there is only ONE ChannelRef -->
			<!-- Find the Channel number of the ChannelRef and store it in TheC -->
			<xsl:if test="count(* [local-name(.)='ChannelRef']) = 1">
				<xsl:for-each select="* [local-name(.)='ChannelRef']">
					<xsl:variable name="theChannelID">
						<xsl:value-of select="@ID"/>
					</xsl:variable>
					<xsl:variable name="theChannelNumber">
						<xsl:for-each select="//OME:Pixels [./OME:Channel/@ID=$theChannelID]">
							<xsl:for-each select="* [local-name(.)='Channel']">
								<xsl:if test="@ID=$theChannelID">
									<xsl:value-of select="position() - 1"/>
								</xsl:if>
							</xsl:for-each>
						</xsl:for-each>
					</xsl:variable>
					<xsl:attribute name="TheC">
						<xsl:value-of select="$theChannelNumber"/>
					</xsl:attribute>
				</xsl:for-each>
			</xsl:if>
			<!-- Else do not store a TheC value so the ROI applies to all channels of the image-->

			<!--
				TODO: Enhancement
				If there are multiple ChannelRef make duplicate shapes
				under Union each with one TheC
			-->

			<!-- Copy any children apart from ChannelRef and ShapeDisplayOptions -->
			<xsl:apply-templates
				select="node() [not((local-name(.)='ChannelRef') or (local-name(.)='ShapeDisplayOptions'))]"
			/>
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
			xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2010-04 http://www.openmicroscopy.org/Schemas/OME/2010-04/ome.xsd">
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

	<xsl:template match="ROI:*">
		<xsl:element name="{name()}" namespace="{$newROINS}">
			<xsl:apply-templates select="@*|node()"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="CA:*">
		<!-- Removed -->
		<xsl:comment> Old Custom Attributes Removed </xsl:comment>
	</xsl:template>

	<xsl:template match="STD:*">
		<!-- Removed -->
		<xsl:comment> Old Semantic Type Definitions Removed </xsl:comment>
	</xsl:template>

	<xsl:template match="MLI:*">
		<!-- Removed -->
		<xsl:comment> Old Matlab Interface Removed </xsl:comment>
	</xsl:template>

	<xsl:template match="CLI:*">
		<!-- Removed -->
		<xsl:comment> Old Command Line Interface Removed </xsl:comment>
	</xsl:template>

	<!-- Default processing -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
