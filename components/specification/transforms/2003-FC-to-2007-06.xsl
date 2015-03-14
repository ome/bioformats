<?xml version = "1.0" encoding = "UTF-8"?>
<!--
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#
# Copyright (C) 2009 Glencoe Software, Inc.
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
# Written by:  Josh Moore, josh at glencoesoftware.com
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:OME="http://www.openmicroscopy.org/XMLschemas/OME/FC/ome.xsd"
	xmlns:AML="http://www.openmicroscopy.org/XMLschemas/AnalysisModule/RC1/AnalysisModule.xsd"
	xmlns:CLI="http://www.openmicroscopy.org/XMLschemas/CLI/RC1/CLI.xsd"
	xmlns:MLI="http://www.openmicroscopy.org/XMLschemas/MLI/IR2/MLI.xsd"
	xmlns:STD="http://www.openmicroscopy.org/XMLschemas/STD/RC2/STD.xsd"
	xmlns:Bin="http://www.openmicroscopy.org/XMLschemas/BinaryFile/RC1/BinaryFile.xsd"
	xmlns:CA="http://www.openmicroscopy.org/XMLschemas/CA/RC1/CA.xsd"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xml="http://www.w3.org/XML/1998/namespace"
	exclude-result-prefixes="OME AML CLI MLI STD Bin CA"
	xmlns:exsl="http://exslt.org/common"
	extension-element-prefixes="exsl" version="1.0">
	<!-- xmlns="http://www.openmicroscopy.org/Schemas/OME/2007-06"-->
	<xsl:variable name="newOMENS">http://www.openmicroscopy.org/Schemas/OME/2007-06</xsl:variable>
	<xsl:variable name="newSPWNS">http://www.openmicroscopy.org/Schemas/SPW/2007-06</xsl:variable>
	<xsl:variable name="newBINNS">http://www.openmicroscopy.org/Schemas/BinaryFile/2007-06</xsl:variable>
	<xsl:variable name="newCANS">http://www.openmicroscopy.org/Schemas/CA/2007-06</xsl:variable>
	<xsl:variable name="newSANS">http://www.openmicroscopy.org/Schemas/SA/2007-06</xsl:variable>
	<xsl:variable name="newSTDNS">http://www.openmicroscopy.org/Schemas/STD/2007-06</xsl:variable>
	<xsl:variable name="newAMLNS">http://www.openmicroscopy.org/Schemas/AnalysisModule/2007-06</xsl:variable>
	<xsl:variable name="newMLINS">http://www.openmicroscopy.org/Schemas/MLI/2007-06</xsl:variable>
	<xsl:variable name="newCLINS">http://www.openmicroscopy.org/Schemas/CLI/2007-06</xsl:variable>

	<xsl:output method="xml" indent="yes"/>
	<xsl:preserve-space elements="*"/>

	<!-- default value for non-numerical value when transforming the attribute of concrete shape -->
	<xsl:variable name="numberDefault" select="1"/>

	<!-- The Enumeration terms to be modified. -->
	<xsl:variable name="enumeration-maps">
		<mapping name="ExperimentType">
			<map from="Time-lapse" to="TimeLapse"/>
			<map from="4-D+" to="FourDPlus"/>
			<map from="Ion-Imaging" to="IonImaging"/>
			<map from="PGI/Documentation" to="PGIDocumentation"/>
			<map from="FRAP" to="Photobleaching"/>
			<map from="Photoablation" to="Photobleaching"/>
			<map from="Photoactivation" to="FRET"/>
			<map from="Uncaging" to="Other"/>
			<map from="Optical-Trapping" to="Other"/>
			<map from="Fluorescence-Lifetime" to="FluorescenceLifetime"/>
			<map from="Spectral-Imaging" to="SpectralImaging"/>
		</mapping>
		<mapping name="LogicalChannelPhotometricInterpretation">
			<map from="monochrome" to="Monochrome"/>
		</mapping>
		<mapping name="LogicalChannelMode">
			<map from="Wide-field" to="WideField"/>
			<map from="Laser Scanning Microscopy" to="LaserScanningMicroscopy"/>
			<map from="Laser Scanning Confocal" to="LaserScanningConfocal"/>
			<map from="Spinning Disk Confocal" to="SpinningDiskConfocal"/>
			<map from="Slit Scan Confocal" to="SlitScanConfocal"/>
			<map from="Multi-Photon Microscopy" to="MultiPhotonMicroscopy"/>
			<map from="Structured Illumination" to="StructuredIllumination"/>
			<map from="Single Molecule Imaging" to="SingleMoleculeImaging"/>
			<map from="Total Internal Reflection" to="TotalInternalReflection"/>
			<map from="Fluorescence-Lifetime" to="FluorescenceLifetime"/>
			<map from="Spectral Imaging" to="SpectralImaging"/>
			<map from="Fluorescence Correlation Spectroscopy"
				to="FluorescenceCorrelationSpectroscopy"/>
			<map from="Near Field Scanning Optical Microscopy"
				to="NearFieldScanningOpticalMicroscopy"/>
			<map from="Second Harmonic Generation Imaging" to="SecondHarmonicGenerationImaging"/>
		</mapping>
		<mapping name="LogicalChannelContractMethod">
			<map from="Hoffman Modulation" to="HoffmanModulation"/>
			<map from="Oblique Illumination" to="ObliqueIllumination"/>
			<map from="Polarized Light" to="PolarizedLight"/>
		</mapping>
		<mapping name="PixelsPixelType">
			<map from="Uint8" to="uint8"/>
			<map from="Uint16" to="uint16"/>
			<map from="Uint32" to="uint32"/>
		</mapping>
		<mapping name="DetectorType">
			<map from="Intensified-CCD" to="IntensifiedCCD"/>
			<map from="Analog-Video" to="AnalogVideo"/>
			<map from="Life-time-Imaging" to="LifetimeImaging"/>
			<map from="Correlation-Spectroscopy" to="CorrelationSpectroscopy"/>
		</mapping>
		<mapping name="ArcType">
			<map from="Hg-Xe" to="HgXe"/>
		</mapping>
		<mapping name="LaserPulse">
			<map from="Q-Switched" to="QSwitched"/>
			<map from="Mode-Locked" to="ModeLocked"/>
		</mapping>
		<mapping name="LaserType">
			<map from="Metal Vapor" to="MetalVapor"/>
			<map from="Solid State" to="SolidState"/>
			<map from="Free Electron" to="FreeElectron"/>
		</mapping>
		<mapping name="LaserMedium">
			<map from="Nitrogen" to="N"/>
			<map from="Argon" to="Ar"/>
			<map from="Krypton" to="Kr"/>
			<map from="Xenon" to="Xe"/>
			<map from="Nd-Glass" to="NdGlass"/>
			<map from="Nd-YAG" to="NdYAG"/>
			<map from="Er-Glass" to="ErGlass"/>
			<map from="Er-YAG" to="ErYAG"/>
			<map from="Ho-YLF" to="HoYLF"/>
			<map from="Ho-YAG" to="HoYAG"/>
			<map from="Ti-Sapphire" to="TiSapphire"/>
			<map from="Rhodamine-6G" to="Rhodamine6G"/>
			<map from="Coumarin-C30" to="CoumarinC30"/>
			<map from="e-" to="EMinus"/>
		</mapping>
		<mapping name="FormalOutputIBelongTo">
			<map from="[Feature]" to="[Region]"/>
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
				<!--
					The isOptional value is not used in this transform
				-->
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Actual schema changes -->

	<!--
	Move Plate to new name space and rename ExternRef attribute.
	-->
	<xsl:template match="OME:Plate">
		<xsl:element name="SPW:Plate" namespace="{$newSPWNS}">
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="local-name(.) = 'ExternRef'">
						<xsl:attribute name="ExternalIdentifier">
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
			<xsl:apply-templates select="node()"/>
		</xsl:element>
	</xsl:template>

	<!--
	Move Screen to new name space and remove ExternRef attribute.
	-->
	<xsl:template match="OME:Screen">
		<xsl:element name="SPW:Screen" namespace="{$newSPWNS}">
			<xsl:for-each select="@* [not(name() = 'ExternRef')]">
				<xsl:attribute name="{local-name(.)}">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:for-each>
			<xsl:apply-templates select="node()"/>
		</xsl:element>
	</xsl:template>

	<!--
	Renaming possible values of the attribute Type
	-->
	<xsl:template match="OME:Experiment">
		<xsl:element name="Experiment" namespace="{$newOMENS}">
			<xsl:for-each select="@*">
				<xsl:attribute name="{local-name(.)}">
					<xsl:choose>
						<xsl:when test="local-name(.)='Type'">
							<xsl:call-template name="transformEnumerationValue">
								<xsl:with-param name="mappingName" select="'ExperimentType'"/>
								<xsl:with-param name="value">
									<xsl:value-of select="."/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="."/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:for-each>
			<xsl:apply-templates select="node()"/>
		</xsl:element>
	</xsl:template>

	<!--
	Rename ChannelInfo, remove element AuxLightSourceRef, process enums
	-->
	<xsl:template match="OME:ChannelInfo">
		<xsl:element name="LogicalChannel" namespace="{$newOMENS}">
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="local-name(.)='PhotometricInterpretation'">
						<xsl:attribute name="{local-name(.)}">
							<xsl:call-template name="transformEnumerationValue">
								<xsl:with-param name="mappingName"
									select="'LogicalChannelPhotometricInterpretation'"/>
								<xsl:with-param name="value">
									<xsl:value-of select="."/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="local-name(.)='Mode'">
						<xsl:attribute name="{local-name(.)}">
							<xsl:call-template name="transformEnumerationValue">
								<xsl:with-param name="mappingName" select="'LogicalChannelMode'"/>
								<xsl:with-param name="value">
									<xsl:value-of select="."/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="local-name(.)='ContrastMethod'">
						<xsl:attribute name="{local-name(.)}">
							<xsl:call-template name="transformEnumerationValue">
								<xsl:with-param name="mappingName"
									select="'LogicalChannelContrastMethod'"/>
								<xsl:with-param name="value">
									<xsl:value-of select="."/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="local-name(.)='NDfilter'">
						<xsl:attribute name="NdFilter">
							<xsl:value-of select="."/>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="local-name(.)='ID'">
						<xsl:attribute name="{local-name(.)}">
							<xsl:call-template name="replace-string">
								<xsl:with-param name="text" select="substring-after(.,'xslt.fix:LogicalChannel:XSLT:')"/>
								<xsl:with-param name="replace" select="'Channel'"/>
								<xsl:with-param name="with" select="'LogicalChannel'"/>
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
			<xsl:for-each select="* [not(name() = 'AuxLightSourceRef' or name()='ChannelComponent')]">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
			<xsl:apply-templates select="* [local-name(.) = 'ChannelComponent']"/>
		</xsl:element>
	</xsl:template>

	<!-- function used to fix the LogicalChannel ID-->
	<xsl:template name="replace-string">
	    <xsl:param name="text"/>
	    <xsl:param name="replace"/>
	    <xsl:param name="with"/>
	    <xsl:choose>
	      <xsl:when test="contains($text,$replace)">
	        <xsl:value-of select="substring-before($text,$replace)"/>
	        <xsl:value-of select="$with"/>
	        <xsl:call-template name="replace-string">
	          <xsl:with-param name="text" select="substring-after($text,$replace)"/>
	          <xsl:with-param name="replace" select="$replace"/>
	          <xsl:with-param name="with" select="$with"/>
	        </xsl:call-template>
	      </xsl:when>
	      <xsl:otherwise>
	        <xsl:value-of select="$text"/>
	      </xsl:otherwise>
	    </xsl:choose>
  	</xsl:template>

	<xsl:template match="OME:ChannelComponent">
		<xsl:element name="{name()}" namespace="{$newOMENS}">
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="local-name(.)='Pixels'">
						<xsl:attribute name="{local-name(.)}">
							<xsl:value-of select="substring-after(.,'xslt.fix:Pixels:XSLT:')"/>
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="{local-name(.)}">
							<xsl:value-of select="."/>
						</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<!--
	In LightSource get Power from child
	-->
	<xsl:template match="OME:LightSource">
		<xsl:element name="LightSource" namespace="{$newOMENS}">
			<xsl:variable name="power">
				<xsl:for-each select="*">
					<xsl:variable name="p" select="@Power"/>
					<xsl:if test="$p != ''">
						<xsl:value-of select="@Power"/>
					</xsl:if>
				</xsl:for-each>
			</xsl:variable>
			<xsl:apply-templates select="@*"/>
      <xsl:if test="$power != ''">
			  <xsl:attribute name="Power">
				  <xsl:value-of select="$power"/>
			  </xsl:attribute>
      </xsl:if>
			<xsl:apply-templates select="node()"/>
		</xsl:element>
	</xsl:template>

	<!--
	In Laser rename Tunable, convert FrequencyDoubled, remove Power
	and update enumerations
	-->
	<xsl:template match="OME:Laser">
		<xsl:element name="Laser" namespace="{$newOMENS}">
			<xsl:for-each select="@* [not(name() = 'Power')]">
				<xsl:choose>
					<xsl:when test="local-name(.)='Pulse'">
						<xsl:attribute name="{local-name(.)}">
							<xsl:call-template name="transformEnumerationValue">
								<xsl:with-param name="mappingName"
									select="'LaserPulse'"/>
								<xsl:with-param name="value">
									<xsl:value-of select="."/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="local-name(.)='Type'">
						<xsl:attribute name="{local-name(.)}">
							<xsl:call-template name="transformEnumerationValue">
								<xsl:with-param name="mappingName" select="'LaserType'"/>
								<xsl:with-param name="value">
									<xsl:value-of select="."/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="local-name(.)='Tunable'">
						<xsl:attribute name="Tuneable">
							<xsl:value-of select="."/>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="local-name(.)='Medium'">
						<xsl:attribute name="LaserMedium">
							<xsl:call-template name="transformEnumerationValue">
								<xsl:with-param name="mappingName" select="'LaserMedium'"/>
								<xsl:with-param name="value">
									<xsl:value-of select="."/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="local-name(.)='FrequencyDoubled'">
						<xsl:attribute name="FrequencyMultiplication">
							<xsl:variable name="double">
								<xsl:value-of select="."/>
							</xsl:variable>
							<xsl:choose>
								<xsl:when test="$double='true' or $double='t'">2</xsl:when>
								<xsl:otherwise>1</xsl:otherwise>
							</xsl:choose>
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
		</xsl:element>
	</xsl:template>

	<!--
	In Arc update enumerations and remove Power
	-->
	<xsl:template match="OME:Arc">
		<xsl:element name="Arc" namespace="{$newOMENS}">
			<xsl:for-each select="@* [not(name() = 'Power')]">
				<xsl:choose>
					<xsl:when test="local-name(.)='Type'">
						<xsl:attribute name="{local-name(.)}">
							<xsl:call-template name="transformEnumerationValue">
								<xsl:with-param name="mappingName"
									select="'ArcType'"/>
								<xsl:with-param name="value">
									<xsl:value-of select="."/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:attribute>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<!--
	In Filament remove Power
	-->
	<xsl:template match="OME:Filament">
		<xsl:element name="Filament" namespace="{$newOMENS}">
			<xsl:for-each select="@* [not(name() = 'Power')]">
				<xsl:choose>
					<xsl:when test="local-name(.)='Type'">
						<xsl:attribute name="{local-name(.)}">
							<xsl:value-of select="."/>
						</xsl:attribute>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<!--
	In Detector update enumerations
	-->
	<xsl:template match="OME:Detector">
		<xsl:element name="Detector" namespace="{$newOMENS}">
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="local-name(.)='Type'">
						<xsl:attribute name="{local-name(.)}">
							<xsl:call-template name="transformEnumerationValue">
								<xsl:with-param name="mappingName"
									select="'DetectorType'"/>
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
		</xsl:element>
	</xsl:template>

	<!--
	Remove Objective
	-->
	<xsl:template match="OME:Objective">
		<xsl:variable name="objectiveID" select="@ID"/>
		<xsl:comment>Objective removed as it cannot be upgraded to 2007-06 schema - <xsl:value-of select="$objectiveID"/></xsl:comment>
	</xsl:template>

	<!--
	Turn old Filter into just FilterSet
	-->
	<xsl:template match="OME:Filter" mode="OnlyFilterSet">
	  <xsl:variable name="filterID" select="@ID"/>
		<xsl:for-each select="*  [local-name(.) = 'FilterSet']">
			<!-- make new but empty FilterSet -->
			<xsl:element name="FilterSet" namespace="{$newOMENS}">
				<xsl:attribute name="ID">FilterSet:<xsl:value-of select="$filterID"/></xsl:attribute>
				<xsl:attribute name="Manufacturer"><xsl:value-of select="@Manufacturer"/></xsl:attribute>
				<xsl:attribute name="Model"><xsl:value-of select="@Model"/></xsl:attribute>
				<xsl:attribute name="LotNumber"><xsl:value-of select="@LotNumber"/></xsl:attribute>
			</xsl:element>
		</xsl:for-each>
		<xsl:for-each select="*  [local-name(.) = 'ExFilter']">
			<!-- make new FilterSet to hold content of old Filter's contents -->
			<xsl:element name="FilterSet" namespace="{$newOMENS}">
				<xsl:attribute name="ID">FilterSet:<xsl:value-of select="$filterID"/></xsl:attribute>
				<xsl:attribute name="ExFilterRef">Filter:Ex:<xsl:value-of select="$filterID"/></xsl:attribute>
				<xsl:attribute name="DichroicRef">Dichroic:<xsl:value-of select="$filterID"/></xsl:attribute>
				<xsl:attribute name="EmFilterRef">Filter:Em:<xsl:value-of select="$filterID"/></xsl:attribute>
				<xsl:attribute name="Manufacturer">GeneratedByOMEXslt</xsl:attribute>
				<xsl:attribute name="Model">2003-FC-to-2008-9</xsl:attribute>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>

	<!--
	Turn old Filter into just new Filters
	-->
	<xsl:template match="OME:Filter" mode="OnlyFilter">
	  <xsl:variable name="filterID" select="@ID"/>
		<xsl:for-each select="*  [local-name(.) = 'ExFilter']">
			<xsl:element name="Filter" namespace="{$newOMENS}">
				<xsl:attribute name="ID">Filter:Ex:<xsl:value-of select="$filterID"/></xsl:attribute>
				<xsl:apply-templates select="@*"/>
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:for-each>
		<xsl:for-each select="*  [local-name(.) = 'EmFilter']">
			<xsl:element name="Filter" namespace="{$newOMENS}">
				<xsl:attribute name="ID">Filter:Em:<xsl:value-of select="$filterID"/></xsl:attribute>
				<xsl:apply-templates select="@*"/>
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>

	<!--
	Turn old Filter into just Dichroic
	-->
	<xsl:template match="OME:Filter" mode="OnlyDichroic">
	  <xsl:variable name="filterID" select="@ID"/>
		<xsl:for-each select="*  [local-name(.) = 'Dichroic']">
			<xsl:element name="Dichroic" namespace="{$newOMENS}">
				<xsl:attribute name="ID">Dichroic:<xsl:value-of select="$filterID"/></xsl:attribute>
				<xsl:apply-templates select="@*"/>
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>

	<!--
	Convert FilterRef to FilterSetRef
	-->
	<xsl:template match="OME:FilterRef">
		<xsl:variable name="filterRefID" select="@ID"/>
		<xsl:element name="FilterSetRef" namespace="{$newOMENS}">
			<xsl:attribute name="ID">FilterSet:<xsl:value-of select="$filterRefID"/></xsl:attribute>
		</xsl:element>
	</xsl:template>

	<!--
	Remove OTF as depends on ObjectiveRef and Objective cannot be upgraded to 2007-06
	-->
	<xsl:template match="OME:OTF">
		<xsl:variable name="otfID" select="@ID"/>
		<xsl:comment>OTF removed as it cannot be upgraded to 2007-06 schema - <xsl:value-of select="$otfID"/></xsl:comment>
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
	Convert namespace of ScreenRef to SPW
	-->
	<xsl:template match="OME:ScreenRef">
		<xsl:element name="SPW:ScreenRef" namespace="{$newSPWNS}">
			<xsl:apply-templates select="@*|node()"/>
		</xsl:element>
	</xsl:template>

	<!--
	Convert namespace of PlateRef to SPW
	-->
	<xsl:template match="OME:PlateRef">
		<!-- not in Image any more -->
		<!-- TODO convert to well sample? -->
		<xsl:comment>
		PlateRef ID="<xsl:value-of select="@ID"/>" not supported in this schema.
		</xsl:comment>
	</xsl:template>

	<!--
	Projection rename Zstart and Zstop
	-->
	<xsl:template match="OME:Projection">
		<xsl:element name="Projection" namespace="{$newOMENS}">
			<xsl:apply-templates select="node()"/>
				<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="local-name(.)='Zstart'">
						<xsl:attribute name="ZStart">
							<xsl:value-of select="."/>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="local-name(.)='Zstop'">
						<xsl:attribute name="ZStop">
							<xsl:value-of select="."/>
						</xsl:attribute>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<!--
	Time rename Tstart and Tstop
	-->
	<xsl:template match="OME:Time">
		<xsl:element name="Time" namespace="{$newOMENS}">
			<xsl:apply-templates select="node()"/>
				<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="local-name(.)='Tstart'">
						<xsl:attribute name="TStart">
							<xsl:value-of select="."/>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="local-name(.)='Tstop'">
						<xsl:attribute name="TStop">
							<xsl:value-of select="."/>
						</xsl:attribute>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<!--
	Instrument
	-->
	<xsl:template match="OME:Instrument">
		<xsl:element name="Instrument" namespace="{$newOMENS}">
			<xsl:apply-templates select="@*"/>
			<xsl:for-each select="* [not(local-name(.) = 'Filter' or local-name(.) = 'OTF')]">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
			<!--
			Currently ignore Filter only containing a FilterSet as cannot see
			how to make one with available info.
			-->
			<xsl:for-each select="* [local-name(.) = 'Filter']">
				<xsl:apply-templates select="." mode="OnlyFilterSet"/>
			</xsl:for-each>
			<xsl:for-each select="* [local-name(.) = 'Filter']">
				<xsl:apply-templates select="." mode="OnlyFilter"/>
			</xsl:for-each>
			<xsl:for-each select="* [local-name(.) = 'Filter']">
				<xsl:apply-templates select="." mode="OnlyDichroic"/>
			</xsl:for-each>
			<xsl:for-each select="* [local-name(.) = 'OTF']">
				<xsl:apply-templates select="."/>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<!--
	Convert Pixels
	-->
	<xsl:template match="OME:Pixels">
		<xsl:element name="Pixels" namespace="{$newOMENS}">
			<xsl:apply-templates select="@*[not(local-name(.)='PixelType' or local-name(.)='ID')]"/>
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="local-name(.)='PixelType'">
						<xsl:attribute name="{local-name(.)}">
							<xsl:call-template name="transformEnumerationValue">
							<xsl:with-param name="mappingName" select="'PixelsPixelType'"/>
							<xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
							</xsl:call-template>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="local-name(.)='ID'">
						<xsl:attribute name="{local-name(.)}">
							<xsl:value-of select="substring-after(.,'xslt.fix:Pixels:XSLT:')"/>
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="{local-name(.)}">
							<xsl:value-of select="."/>
						</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
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
					<xsl:apply-templates select="node()" mode="OnlyBinData"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="node()" mode="OnlyTiffData"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
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
	Convert Feature
	-->
	<xsl:template match="OME:Feature">
		<xsl:element name="Region" namespace="{$newOMENS}">
			<xsl:apply-templates select="@*[not(local-name(.)='ID')]"/>
			<xsl:for-each select="@* [name() = 'ID']">
				<xsl:attribute name="ID">Region:<xsl:value-of select="."/></xsl:attribute>
			</xsl:for-each>
			<xsl:apply-templates select="node()"/>
		</xsl:element>
	</xsl:template>

	<!--
		Skip DisplayOptions
	-->

	<xsl:template match="OME:DisplayOptions"/>

	<!--
	Convert ROI
	-->
	<xsl:template match="OME:ROI">
		<xsl:variable name="parentID">
			<xsl:for-each select="ancestor::OME:DisplayOptions">
				<xsl:for-each select="@* [name() = 'ID']">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="X0">
				<xsl:for-each select="@* [name() = 'X0']">
					<xsl:value-of select="."/>
				</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="Y0">
				<xsl:for-each select="@* [name() = 'Y0']">
					<xsl:value-of select="."/>
				</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="X1">
				<xsl:for-each select="@* [name() = 'X1']">
					<xsl:value-of select="."/>
				</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="Y1">
				<xsl:for-each select="@* [name() = 'Y1']">
					<xsl:value-of select="."/>
				</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="startX">
			<xsl:choose>
				<xsl:when test="$X1 > $X0">
					<xsl:value-of select="$X0"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$X1"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="width">
			<xsl:choose>
				<xsl:when test="$X1 > $X0">
					<xsl:number value="$X1 - $X0"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="$X0 - $X1"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="startY">
			<xsl:choose>
				<xsl:when test="$Y1 > $Y0">
					<xsl:value-of select="$Y0"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$Y1"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="height">
			<xsl:choose>
				<xsl:when test="$Y1 > $Y0">
					<xsl:number value="$Y1 - $Y0"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="$Y0 - $Y1"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="Z0">
				<xsl:for-each select="@* [name() = 'Z0']">
					<xsl:value-of select="."/>
				</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="T0">
				<xsl:for-each select="@* [name() = 'T0']">
					<xsl:value-of select="."/>
				</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="Z1">
				<xsl:for-each select="@* [name() = 'Z1']">
					<xsl:value-of select="."/>
				</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="T1">
				<xsl:for-each select="@* [name() = 'T1']">
					<xsl:value-of select="."/>
				</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="theMaxT">
			<xsl:choose>
				<xsl:when test="$T1 > $T0">
					<xsl:number value="$T1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="$T0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="theMaxZ">
			<xsl:choose>
				<xsl:when test="$Z1 > $Z0">
					<xsl:number value="$Z1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="$Z0"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="theMinT">
			<xsl:choose>
				<xsl:when test="$T1 > $T0">
					<xsl:number value="$T0"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="$T1"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="theMinZ">
			<xsl:choose>
				<xsl:when test="$Z1 > $Z0">
					<xsl:number value="$Z0"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:number value="$Z1"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="ROI" namespace="{$newOMENS}">
			<xsl:variable name="shapeEndID"><xsl:number value="position()"/>:<xsl:value-of select="$parentID"/></xsl:variable>
			<xsl:attribute name="ID">ROI:<xsl:number value="position()"/>:<xsl:value-of select="$parentID"/></xsl:attribute>
			<xsl:element name="Union" namespace="{$newOMENS}">
				<xsl:choose>
					<xsl:when test="(($theMaxT = $theMinT) and (($theMaxZ = $theMinZ)))">
						<xsl:element name="Shape" namespace="{$newOMENS}">
							<xsl:attribute name="ID">Shape:Z<xsl:value-of select="$theMinZ"/>:T<xsl:value-of select="$theMinT"/>:<xsl:value-of select="$parentID"/></xsl:attribute>
							<xsl:attribute name="theZ"><xsl:value-of select="$theMinZ"/></xsl:attribute>
							<xsl:attribute name="theT"><xsl:value-of select="$theMinT"/></xsl:attribute>
							<xsl:comment>Converted to single Rect</xsl:comment>
							<xsl:element name="Rect" namespace="{$newOMENS}">
								<xsl:attribute name="x"><xsl:value-of select="$startX"/></xsl:attribute>
								<xsl:attribute name="y"><xsl:value-of select="$startY"/></xsl:attribute>
								<xsl:attribute name="width"><xsl:value-of select="$width"/></xsl:attribute>
								<xsl:attribute name="height"><xsl:value-of select="$height"/></xsl:attribute>
							</xsl:element>
						</xsl:element>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="ByTZROI">
							<xsl:with-param name="theTEnd"><xsl:value-of select="$theMaxT"/></xsl:with-param>
							<xsl:with-param name="theZEnd"><xsl:value-of select="$theMaxZ"/></xsl:with-param>
							<xsl:with-param name="parentID"><xsl:value-of select="$parentID"/></xsl:with-param>
							<xsl:with-param name="theZ"><xsl:value-of select="$theMinZ"/></xsl:with-param>
							<xsl:with-param name="theT"><xsl:value-of select="$theMinT"/></xsl:with-param>
							<xsl:with-param name="x"><xsl:value-of select="$startX"/></xsl:with-param>
							<xsl:with-param name="y"><xsl:value-of select="$startY"/></xsl:with-param>
							<xsl:with-param name="width"><xsl:value-of select="$width"/></xsl:with-param>
							<xsl:with-param name="height"><xsl:value-of select="$height"/></xsl:with-param>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template name="ByZROI">
		<xsl:param name="theZEnd"/>
		<xsl:param name="parentID"/>
		<xsl:param name="theZ"/>
		<xsl:param name="theT"/>
		<xsl:param name="x"/>
		<xsl:param name="y"/>
		<xsl:param name="width"/>
		<xsl:param name="height"/>
		<xsl:if test="$theZEnd >= $theZ">
			<xsl:element name="Shape" namespace="{$newOMENS}">
				<xsl:attribute name="ID">Shape:Z<xsl:value-of select="$theZ"/>:T<xsl:value-of select="$theT"/>:<xsl:value-of select="$parentID"/></xsl:attribute>
				<xsl:attribute name="theZ"><xsl:value-of select="$theZ"/></xsl:attribute>
				<xsl:attribute name="theT"><xsl:value-of select="$theT"/></xsl:attribute>
				<xsl:element name="Rect" namespace="{$newOMENS}">
					<xsl:attribute name="x"><xsl:value-of select="$x"/></xsl:attribute>
					<xsl:attribute name="y"><xsl:value-of select="$y"/></xsl:attribute>
					<xsl:attribute name="width"><xsl:value-of select="$width"/></xsl:attribute>
					<xsl:attribute name="height"><xsl:value-of select="$height"/></xsl:attribute>
				</xsl:element>
			</xsl:element>
			<xsl:call-template name="ByZROI">
				<xsl:with-param name="theZEnd"><xsl:value-of select="$theZEnd"/></xsl:with-param>
				<xsl:with-param name="parentID"><xsl:value-of select="$parentID"/></xsl:with-param>
				<xsl:with-param name="theZ"><xsl:value-of select="$theZ + 1"/></xsl:with-param>
				<xsl:with-param name="theT"><xsl:value-of select="$theT"/></xsl:with-param>
				<xsl:with-param name="x"><xsl:value-of select="$x"/></xsl:with-param>
				<xsl:with-param name="y"><xsl:value-of select="$y"/></xsl:with-param>
				<xsl:with-param name="width"><xsl:value-of select="$width"/></xsl:with-param>
				<xsl:with-param name="height"><xsl:value-of select="$height"/></xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="ByTZROI">
		<xsl:param name="theTEnd"/>
		<xsl:param name="theZEnd"/>
		<xsl:param name="parentID"/>
		<xsl:param name="theZ"/>
		<xsl:param name="theT"/>
		<xsl:param name="x"/>
		<xsl:param name="y"/>
		<xsl:param name="width"/>
		<xsl:param name="height"/>
		<xsl:if test="$theTEnd >= $theT">
			<xsl:call-template name="ByTZROI">
				<xsl:with-param name="theTEnd"><xsl:value-of select="$theTEnd"/></xsl:with-param>
				<xsl:with-param name="theZEnd"><xsl:value-of select="$theZEnd"/></xsl:with-param>
				<xsl:with-param name="parentID"><xsl:value-of select="$parentID"/></xsl:with-param>
				<xsl:with-param name="theZ"><xsl:value-of select="$theZ"/></xsl:with-param>
				<xsl:with-param name="theT"><xsl:value-of select="$theT + 1"/></xsl:with-param>
				<xsl:with-param name="x"><xsl:value-of select="$x"/></xsl:with-param>
				<xsl:with-param name="y"><xsl:value-of select="$y"/></xsl:with-param>
				<xsl:with-param name="width"><xsl:value-of select="$width"/></xsl:with-param>
				<xsl:with-param name="height"><xsl:value-of select="$height"/></xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="ByZROI">
				<xsl:with-param name="theZEnd"><xsl:value-of select="$theZEnd"/></xsl:with-param>
				<xsl:with-param name="parentID"><xsl:value-of select="$parentID"/></xsl:with-param>
				<xsl:with-param name="theZ"><xsl:value-of select="$theZ"/></xsl:with-param>
				<xsl:with-param name="theT"><xsl:value-of select="$theT"/></xsl:with-param>
				<xsl:with-param name="x"><xsl:value-of select="$x"/></xsl:with-param>
				<xsl:with-param name="y"><xsl:value-of select="$y"/></xsl:with-param>
				<xsl:with-param name="width"><xsl:value-of select="$width"/></xsl:with-param>
				<xsl:with-param name="height"><xsl:value-of select="$height"/></xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--
	Rename ChannelInfo, remove element AuxLightSourceRef, process enums
	-->
	<xsl:template match="OME:Image">
		<xsl:element name="Image" namespace="{$newOMENS}">
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="local-name(.)='ID'">
						<xsl:attribute name="{local-name(.)}">
							<xsl:value-of select="substring-after(.,'xslt.fix:Image:XSLT:')"/>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="local-name(.)='Name'">
						<xsl:attribute name="{local-name(.)}">
							<xsl:value-of select="."/>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="local-name(.)='DefaultPixels'">
						<xsl:attribute name="{local-name(.)}">
							<xsl:value-of select="substring-after(.,'xslt.fix:Pixels:XSLT:')"/>
						</xsl:attribute>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
			<xsl:variable name="defaultPixels">
				<xsl:for-each select="@*[name(.) = 'DefaultPixels']">
					<xsl:value-of select="substring-after(.,'xslt.fix:Pixels:XSLT:')"/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:for-each select="*[local-name(.) = 'ObjectiveRef']">
				<xsl:variable name="objectiveRefID" select="@ID"/>
				<xsl:comment>ObjectiveRef removed as ObjectiveSettingsRef is broken in 2007-06 schema - <xsl:value-of select="$objectiveRefID"/></xsl:comment>
			</xsl:for-each>
			<xsl:apply-templates select="*[not(local-name(.) = 'ObjectiveRef')]"/>
		</xsl:element>
	</xsl:template>

	<!--
	In AML:AnalysisModule rename FeatureIterator to RegionIterator, NewFeatureName to NewRegionName
	-->
	<xsl:template match="AML:AnalysisModule">
		<xsl:element name="AnalysisModule" namespace="{$newAMLNS}">
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="local-name(.)='FeatureIterator'">
						<xsl:attribute name="RegionIterator">
							<xsl:value-of select="."/>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test="local-name(.)='NewFeatureName'">
						<xsl:attribute name="NewRegionName">
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
			<xsl:apply-templates select="node()"/>
		</xsl:element>
	</xsl:template>

	<!--
	In CLI:ExecutionInstructions rename MakesNewFeature to MakesNewRegion
	-->
	<xsl:template match="CLI:ExecutionInstructions">
		<xsl:element name="ExecutionInstructions" namespace="{$newCLINS}">
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="local-name(.)='MakesNewFeature'">
						<xsl:attribute name="MakesNewRegion">
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
			<xsl:apply-templates select="node()"/>
		</xsl:element>
	</xsl:template>

	<!--
	In AML:FormalOutput update enum IBelongTo
	-->
	<xsl:template match="AML:FormalOutput">
		<xsl:element name="FormalOutput" namespace="{$newAMLNS}">
			<xsl:for-each select="@*">
				<xsl:choose>
					<xsl:when test="local-name(.)='IBelongTo'">
						<xsl:attribute name="{local-name(.)}">
							<xsl:call-template name="transformEnumerationValue">
								<xsl:with-param name="mappingName" select="'FormalOutputIBelongTo'"/>
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
		</xsl:element>
	</xsl:template>

	<!-- Rewriting all namespaces -->

	<xsl:template match="OME:OME">
		<OME xmlns="http://www.openmicroscopy.org/Schemas/OME/2007-06"
			xmlns:CA="http://www.openmicroscopy.org/Schemas/CA/2007-06"
			xmlns:STD="http://www.openmicroscopy.org/Schemas/STD/2007-06"
			xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2007-06"
			xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2007-06"
			xmlns:AML="http://www.openmicroscopy.org/Schemas/AnalysisModule/2007-06"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2007-06 http://www.openmicroscopy.org/Schemas/OME/2007-06/ome.xsd">
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

	<xsl:template match="AML:*">
		<xsl:element name="{name()}" namespace="{$newAMLNS}">
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

	<!-- Default processing -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>

	<!-- data management -->



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
