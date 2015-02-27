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
# Written by:  Josh Moore, josh at glencoesoftware.com
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
			<map from="Uncaging" to="???"/>
			<map from="Optical-Trapping" to="???"/>
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
			<map from="Metal Vapor" to="MetalVapor"/>
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
	In TransmittanceRange convert Transmittance attribute from int to PercentageFraction.
	-->
	<xsl:template match="OME:TransmittanceRange">
		<xsl:element name="TransmittanceRange" namespace="{$newOMENS}">
			<xsl:for-each select="@*">
				<xsl:attribute name="{local-name(.)}">
					<xsl:choose>
						<xsl:when test="local-name(.) ='Transmittance'">
							<xsl:call-template name="convertPercentFraction">
								<xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
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
		<xsl:variable name="theShapeIDRoot"><xsl:number value="position()"/>:<xsl:value-of select="$parentID"/></xsl:variable>
		<xsl:element name="ROI" namespace="{$newOMENS}">
			<xsl:variable name="shapeEndID"><xsl:number value="position()"/>:<xsl:value-of select="$parentID"/></xsl:variable>
			<xsl:attribute name="ID">ROI:<xsl:number value="position()"/>:<xsl:value-of select="$parentID"/></xsl:attribute>
			<xsl:element name="Union" namespace="{$newOMENS}">
				<xsl:choose>
					<xsl:when test="(($theMaxT = $theMinT) and ($theMaxZ = $theMinZ) and not(( $T0 = 'NaN') or ( $Z0 = 'NaN') or ( $T1 = 'NaN') or ( $Z1 = 'NaN') ))">
						<xsl:element name="Shape" namespace="{$newOMENS}">
							<xsl:attribute name="ID">Shape:SingleRect:<xsl:value-of select="$theShapeIDRoot"/></xsl:attribute>
							<xsl:choose>
								<xsl:when test="not( $theMinZ = 'NaN')">
									<xsl:attribute name="theZ"><xsl:value-of select="$theMinZ"/></xsl:attribute>
								</xsl:when>
							</xsl:choose>
							<xsl:choose>
								<xsl:when test="not( $theMinT = 'NaN')">
									<xsl:attribute name="theT"><xsl:value-of select="$theMinT"/></xsl:attribute>
								</xsl:when>
							</xsl:choose>
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
							<xsl:with-param name="parentID"><xsl:value-of select="$theShapeIDRoot"/></xsl:with-param>
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

	<!-- Rewriting all namespaces -->

	<xsl:template match="OME:OME">
		<OME xmlns="http://www.openmicroscopy.org/Schemas/OME/2008-09"
			xmlns:CA="http://www.openmicroscopy.org/Schemas/CA/2008-09"
			xmlns:STD="http://www.openmicroscopy.org/Schemas/STD/2008-09"
			xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2008-09"
			xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2008-09"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2008-09 http://www.openmicroscopy.org/Schemas/OME/2008-09/ome.xsd">
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
