<?xml version = "1.0" encoding = "UTF-8"?>
<!--
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#
# Copyright (C) 2003 - 2016 Open Microscopy Environment
#       Massachusetts Institue of Technology,
#       National Institutes of Health,
#       University of Dundee
#
#
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
# Written by:  Ilya G. Goldberg <igg@nih.gov>
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-->



<xsl:transform
	xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0"
	xmlns:xsi = "http://www.w3.org/2001/XMLSchema-instance"
	xmlns:OME = "http://www.openmicroscopy.org/XMLschemas/OME/FC/ome.xsd"
	xmlns:STD = "http://www.openmicroscopy.org/XMLschemas/STD/RC2/STD.xsd"
	xmlns:STD3 = "http://www.openmicroscopy.org/XMLschemas/STD/RC3/STD.xsd"
	xmlns:Bin = "http://www.openmicroscopy.org/XMLschemas/BinaryFile/RC1/BinaryFile.xsd"
	xmlns:AML = "http://www.openmicroscopy.org/XMLschemas/AnalysisModule/RC1/AnalysisModule.xsd"
	xmlns:DH = "http://www.openmicroscopy.org/XMLschemas/DataHistory/IR3/DataHistory.xsd"
	xmlns = "http://www.openmicroscopy.org/XMLschemas/CA/RC1/CA.xsd">

	<!-- Pass everything through that doesn't match the defined OME namespace -->
	<xsl:template match = "*">
		<xsl:copy-of select = "."/>
	</xsl:template>
	<xsl:template match = "OME:OME">
		<xsl:element name = "OME" namespace = "http://www.openmicroscopy.org/XMLschemas/CA/RC1/CA.xsd"
			xmlns:Bin = "http://www.openmicroscopy.org/XMLschemas/BinaryFile/RC1/BinaryFile.xsd">
			<xsl:attribute name = "xsi:schemaLocation">
				<xsl:value-of select = "@xsi:schemaLocation"/>
			</xsl:attribute>
			<!-- Need to copy STD and AML also -->
			<!-- Deal with the hierarchy -->
			<xsl:apply-templates select = "OME:Project"/>
			<xsl:apply-templates select = "OME:Dataset"/>
			<xsl:apply-templates select = "OME:Image"/>

			<xsl:element name = "CustomAttributes">
				<xsl:apply-templates select = "OME:Experimenter"/>
				<xsl:apply-templates select = "OME:Group"/>
				<xsl:apply-templates select = "OME:Experiment"/>
				<xsl:apply-templates select = "OME:Instrument"/>
				<xsl:apply-templates select = "OME:Plate"/>
				<xsl:apply-templates select = "OME:Screen"/>
				<xsl:apply-templates select = "OME:CustomAttributes/*"/>
			</xsl:element>

			<xsl:apply-templates select = "STD:*"/>
			<xsl:apply-templates select = "STD3:*"/>
			<xsl:apply-templates select = "AML:*"/>
			<xsl:apply-templates select = "DH:*"/>
		</xsl:element>
	</xsl:template>
	<!-- Project -->
	<xsl:template match = "OME:Project">
		<xsl:element name = "Project">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Name">
				<xsl:value-of select = "@Name"/>
			</xsl:attribute>
			<xsl:attribute name = "Experimenter">
				<xsl:value-of select = "OME:ExperimenterRef/@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Group">
				<xsl:value-of select = "OME:GroupRef/@ID"/>
			</xsl:attribute>
			<xsl:apply-templates select = "OME:Description" mode = "OptionalAttribute"/>
		</xsl:element>
	</xsl:template>
	<!-- Dataset -->
	<xsl:template match = "OME:Dataset">
		<xsl:element name = "Dataset">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Name">
				<xsl:value-of select = "@Name"/>
			</xsl:attribute>
			<xsl:attribute name = "Locked">
				<xsl:value-of select = "@Locked"/>
			</xsl:attribute>
			<xsl:attribute name = "Experimenter">
				<xsl:value-of select = "OME:ExperimenterRef/@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Group">
				<xsl:value-of select = "OME:GroupRef/@ID"/>
			</xsl:attribute>
			<xsl:apply-templates select = "OME:Description" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "OME:ProjectRef" mode = "CopyRefs"/>

			<xsl:if test = "OME:CustomAttributes/*">
				<xsl:element name = "CustomAttributes">
					<xsl:copy-of select = "OME:CustomAttributes/*"/>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	<!-- Image and the required Dimensions Image attribute -->
	<xsl:template match = "OME:Image">
		<xsl:element name = "Image">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Name">
				<xsl:value-of select = "@Name"/>
			</xsl:attribute>
			<xsl:attribute name = "CreationDate">
				<xsl:value-of select = "OME:CreationDate"/>
			</xsl:attribute>
			<xsl:attribute name = "Experimenter">
				<xsl:value-of select = "OME:ExperimenterRef/@ID"/>
			</xsl:attribute>
			<xsl:if test = "string-length(OME:GroupRef/@ID) > 0">
				<xsl:attribute name = "Group">
					<xsl:value-of select = "OME:GroupRef/@ID"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test = "string-length(@DefaultPixels) > 0">
				<xsl:attribute name = "DefaultPixels">
					<xsl:value-of select = "@DefaultPixels"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test = "not (string-length(@DefaultPixels) > 0)">
				<xsl:if test = "string-length(OME:Pixels [1] /@ID) > 0">
					<xsl:attribute name = "DefaultPixels">
						<xsl:value-of select = "OME:Pixels [1] /@ID"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:if test = "not (string-length(OME:Pixels [1] /@ID) > 0)">
					<xsl:if test = "string-length(OME:CustomAttributes/OME:Pixels [1] /@ID) > 0">
						<xsl:attribute name = "DefaultPixels">
							<xsl:value-of select = "OME:CustomAttributes/OME:Pixels [1] /@ID"/>
						</xsl:attribute>
					</xsl:if>
				</xsl:if>
			</xsl:if>
			<xsl:apply-templates select = "OME:Description" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "OME:DatasetRef" mode = "CopyRefs"/>
			<xsl:element name = "CustomAttributes">
				<xsl:if test="@PixelSizeX">
					<xsl:element name = "Dimensions">
						<xsl:attribute name = "ID">
							<xsl:value-of select = "generate-id()"/>
						</xsl:attribute>
						<xsl:attribute name = "PixelSizeX">
							<xsl:value-of select = "@PixelSizeX"/>
						</xsl:attribute>
						<xsl:apply-templates select = "@PixelSizeY" mode = "OptionalAttribute"/>
						<xsl:apply-templates select = "@PixelSizeZ" mode = "OptionalAttribute"/>
						<xsl:apply-templates select = "@WaveIncrement" mode = "OptionalAttribute">
							<xsl:with-param name = "Name">PixelSizeC</xsl:with-param>
						</xsl:apply-templates>
						<xsl:apply-templates select = "@TimeIncrement" mode = "OptionalAttribute">
							<xsl:with-param name = "Name">PixelSizeT</xsl:with-param>
						</xsl:apply-templates>
					</xsl:element>
				</xsl:if>
				<xsl:apply-templates select = "OME:ExperimentRef"/>
				<xsl:apply-templates select = "OME:InstrumentRef"/>
				<xsl:apply-templates select = "OME:ImagingEnvironment"/>
				<xsl:apply-templates select = "OME:Thumbnail"/>
				<xsl:apply-templates select = "OME:ChannelInfo"/>
				<xsl:apply-templates select = "OME:DisplayOptions"/>
				<xsl:apply-templates select = "OME:StageLabel"/>
				<xsl:apply-templates select = "OME:PlateRef"/>
				<xsl:apply-templates select = "OME:Pixels"/>
				<xsl:copy-of select = "OME:Feature"/>
				<xsl:copy-of select = "OME:CustomAttributes/*"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<!-- Image attributes -->

	<!-- ExperimentRef -->
	<xsl:template match = "OME:Image/OME:ExperimentRef">
		<xsl:element name = "ImageExperiment">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "generate-id()"/>
			</xsl:attribute>
			<xsl:apply-templates select = "." mode = "MakeRefs"/>
		</xsl:element>
	</xsl:template>
	<!-- InstrumentRef -->
	<xsl:template match = "OME:Image/OME:InstrumentRef">
		<xsl:element name = "ImageInstrument">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "generate-id()"/>
			</xsl:attribute>
			<xsl:apply-templates select = "." mode = "MakeRefs"/>
			<xsl:apply-templates select = "../OME:ObjectiveRef" mode = "MakeRefs"/>
		</xsl:element>
	</xsl:template>
	<!-- ImagingEnvironment -->
	<xsl:template match = "OME:Image/OME:ImagingEnvironment">
		<xsl:apply-templates select = "." mode = "Element2Attributes"/>
	</xsl:template>
	<!-- Thumbnail -->
	<xsl:template match = "OME:Image/OME:Thumbnail">
		<xsl:element name = "Thumbnail">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "generate-id()"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@href" mode = "OptionalAttribute">
				<xsl:with-param name = "Name">Path</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "@MIMEtype" mode = "OptionalAttribute">
				<xsl:with-param name = "Name">MimeType</xsl:with-param>
			</xsl:apply-templates>
		</xsl:element>
		<!--<xsl:apply-templates select = "." mode = "Element2Attributes"/>-->
	</xsl:template>
	<!-- PixelChannelComponent -->
	<xsl:template match = "OME:ChannelInfo/OME:ChannelComponent">
		<xsl:element name = "PixelChannelComponent">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "generate-id()"/>
			</xsl:attribute>
			<xsl:attribute name = "Pixels">
				<xsl:value-of select = "@Pixels"/>
			</xsl:attribute>
			<xsl:attribute name = "Index">
				<xsl:value-of select = "@Index"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@ColorDomain" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = ".." mode = "MakeRefs">
				<xsl:with-param name = "Name">LogicalChannel</xsl:with-param>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
	<!-- ChannelInfo -->
	<xsl:template match = "OME:Image/OME:ChannelInfo">
		<xsl:element name = "LogicalChannel">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Name">
				<xsl:value-of select = "@Name"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@SamplesPerPixel" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "OME:LightSourceRef/@Attenuation" mode = "OptionalAttribute">
				<xsl:with-param name = "Name">LightAttenuation</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "OME:LightSourceRef/@Wavelength" mode = "OptionalAttribute">
				<xsl:with-param name = "Name">LightWavelength</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "OME:DetectorRef/@Offset" mode = "OptionalAttribute">
				<xsl:with-param name = "Name">DetectorOffset</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "OME:DetectorRef/@Gain" mode = "OptionalAttribute">
				<xsl:with-param name = "Name">DetectorGain</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "@IlluminationType" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "@PinholeSize" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "@PhotometricInterpretation" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "@Mode" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "@ContrastMethod" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "OME:AuxLightSourceRef/@Attenuation" mode = "OptionalAttribute">
				<xsl:with-param name = "Name">AuxLightAttenuation</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "OME:AuxLightSourceRef/@Technique" mode = "OptionalAttribute">
				<xsl:with-param name = "Name">AuxTechnique</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "OME:AuxLightSourceRef/@Wavelength" mode = "OptionalAttribute">
				<xsl:with-param name = "Name">AuxLightWavelength</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "@ExWave" mode = "OptionalAttribute">
				<xsl:with-param name = "Name">ExcitationWavelength</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "@EmWave" mode = "OptionalAttribute">
				<xsl:with-param name = "Name">EmissionWavelength</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "@Fluor" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "@NDfilter" mode = "OptionalAttribute">
				<xsl:with-param name = "Name">NDFilter</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "OME:LightSourceRef" mode = "MakeRefs"/>
			<xsl:apply-templates select = "OME:AuxLightSourceRef" mode = "MakeRefs"/>
			<xsl:apply-templates select = "OME:OTFRef" mode = "MakeRefs"/>
			<xsl:apply-templates select = "OME:DetectorRef" mode = "MakeRefs"/>
			<xsl:apply-templates select = "OME:FilterRef" mode = "MakeRefs"/>
		</xsl:element>
		<xsl:apply-templates select = "OME:ChannelComponent"/>
	</xsl:template>
	<!-- DisplayOptions - DisplayChannels -->
	<xsl:template match = "OME:DisplayOptions/*" mode = "MakeDisplayChannel">
		<xsl:element name = "DisplayChannel">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "generate-id()"/>
			</xsl:attribute>
			<xsl:attribute name = "ChannelNumber">
				<xsl:value-of select = "@ChannelNumber"/>
			</xsl:attribute>
			<xsl:attribute name = "BlackLevel">
				<xsl:value-of select = "@BlackLevel"/>
			</xsl:attribute>
			<xsl:attribute name = "WhiteLevel">
				<xsl:value-of select = "@WhiteLevel"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@Gamma" mode = "OptionalAttribute"/>
		</xsl:element>
	</xsl:template>
	<!-- DisplayOptions -->
	<xsl:template match = "OME:DisplayOptions">
		<xsl:element name = "DisplayOptions">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "DisplayRGB">
				<xsl:choose>
					<xsl:when test="@Display = 'RGB'">
						<xsl:text>true</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name = "RedChannelOn">
				<xsl:choose>
					<xsl:when test="OME:RedChannel/@isOn = 'true' or OME:RedChannel/@isOn = '1'">
						<xsl:text>true</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name = "GreenChannelOn">
				<xsl:choose>
					<xsl:when test="OME:GreenChannel/@isOn = 'true' or OME:GreenChannel/@isOn = '1'">
						<xsl:text>true</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name = "BlueChannelOn">
				<xsl:choose>
					<xsl:when test="OME:BlueChannel/@isOn = 'true' or OME:BlueChannel/@isOn = '1'">
						<xsl:text>true</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>false</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:apply-templates select = "@Zoom" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "OME:GreyChannel/@ColorMap" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "OME:Projection/@Zstart" mode = "OptionalAttribute">
				<xsl:with-param name = "Name">ZStart</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "OME:Projection/@Zstop" mode = "OptionalAttribute">
				<xsl:with-param name = "Name">ZStop</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "OME:Time/@Tstart" mode = "OptionalAttribute">
				<xsl:with-param name = "Name">TStart</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "OME:Time/@Tstop" mode = "OptionalAttribute">
				<xsl:with-param name = "Name">TStop</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "OME:RedChannel" mode = "MakeRefs">
				<xsl:with-param name = "Name">
					<xsl:text>RedChannel</xsl:text>
				</xsl:with-param>
				<xsl:with-param name = "ID">
					<xsl:value-of select = "generate-id(OME:RedChannel)"/>
				</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "OME:GreenChannel" mode = "MakeRefs">
				<xsl:with-param name = "Name">
					<xsl:text>GreenChannel</xsl:text>
				</xsl:with-param>
				<xsl:with-param name = "ID">
					<xsl:value-of select = "generate-id(OME:GreenChannel)"/>
				</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "OME:BlueChannel" mode = "MakeRefs">
				<xsl:with-param name = "Name">
					<xsl:text>BlueChannel</xsl:text>
				</xsl:with-param>
				<xsl:with-param name = "ID">
					<xsl:value-of select = "generate-id(OME:BlueChannel)"/>
				</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "OME:GreyChannel" mode = "MakeRefs">
				<xsl:with-param name = "Name">
					<xsl:text>GreyChannel</xsl:text>
				</xsl:with-param>
				<xsl:with-param name = "ID">
					<xsl:value-of select = "generate-id(OME:GreyChannel)"/>
				</xsl:with-param>
			</xsl:apply-templates>
		</xsl:element>
		<xsl:apply-templates select = "OME:RedChannel" mode = "MakeDisplayChannel"/>
		<xsl:apply-templates select = "OME:GreenChannel" mode = "MakeDisplayChannel"/>
		<xsl:apply-templates select = "OME:BlueChannel" mode = "MakeDisplayChannel"/>
		<xsl:apply-templates select = "OME:GreyChannel" mode = "MakeDisplayChannel"/>
		<xsl:apply-templates select = "OME:ROI"/>
	</xsl:template>
	<!-- DisplayOptions - ROI -->
	<xsl:template match = "OME:DisplayOptions/OME:ROI">
		<xsl:element name = "DisplayROI">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "generate-id()"/>
			</xsl:attribute>
			<xsl:attribute name = "X0">
				<xsl:value-of select = "@X0"/>
			</xsl:attribute>
			<xsl:attribute name = "Y0">
				<xsl:value-of select = "@Y0"/>
			</xsl:attribute>
			<xsl:attribute name = "X1">
				<xsl:value-of select = "@X1"/>
			</xsl:attribute>
			<xsl:attribute name = "Y1">
				<xsl:value-of select = "@Y1"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@Z0" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "@Z1" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "@T0" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "@T1" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = ".." mode = "MakeRefs">
				<xsl:with-param name = "Name">
					<xsl:text>DisplayOptions</xsl:text>
				</xsl:with-param>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
	<!-- PlateRef -->
	<xsl:template match = "OME:Image/OME:PlateRef">
		<xsl:element name = "ImagePlate">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "generate-id()"/>
			</xsl:attribute>
			<xsl:attribute name = "Well">
				<xsl:value-of select = "@Well"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@Sample" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "." mode = "MakeRefs"/>
		</xsl:element>
	</xsl:template>
	<!-- StageLabel -->
	<xsl:template match = "OME:StageLabel">
		<xsl:element name = "StageLabel">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "generate-id()"/>
			</xsl:attribute>
			<xsl:attribute name = "Name">
				<xsl:value-of select = "@Name"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@X" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "@Y" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "@Z" mode = "OptionalAttribute"/>
		</xsl:element>
	</xsl:template>
	<!-- Pixels -->
	<xsl:template match = "OME:Pixels">
		<xsl:element name = "Pixels">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<!--xsl:attribute name = "Method">
				<xsl:value-of select = "OME:DerivedFrom/@Method"/>
			</xsl:attribute-->
			<!--xsl:attribute name = "DimensionOrder">
				<xsl:value-of select = "@DimensionOrder"/>
			</xsl:attribute-->
			<xsl:attribute name = "SizeX">
				<xsl:value-of select = "@SizeX"/>
			</xsl:attribute>
			<xsl:attribute name = "SizeY">
				<xsl:value-of select = "@SizeY"/>
			</xsl:attribute>
			<xsl:attribute name = "SizeZ">
				<xsl:value-of select = "@SizeZ"/>
			</xsl:attribute>
			<xsl:attribute name = "SizeC">
				<xsl:value-of select = "@SizeC"/>
			</xsl:attribute>
			<xsl:attribute name = "SizeT">
				<xsl:value-of select = "@SizeT"/>
			</xsl:attribute>
			<xsl:attribute name = "PixelType">
				<xsl:value-of select = "@PixelType"/>
			</xsl:attribute>
			<!--xsl:attribute name = "BigEndian">
				<xsl:value-of select = "@BigEndian"/>
			</xsl:attribute-->
			<!--xsl:apply-templates select = "OME:DerivedFrom" mode = "MakeRefs">
				<xsl:with-param name = "Name">
					<xsl:text>DerivedFrom</xsl:text>
				</xsl:with-param>
			</xsl:apply-templates-->
		</xsl:element>
	</xsl:template>

	<!--

		Global Attributes

	-->

	<!-- Experimenter -->
	<xsl:template match = "OME:Experimenter">
		<xsl:element name = "Experimenter">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "FirstName">
				<xsl:value-of select = "OME:FirstName"/>
			</xsl:attribute>
			<xsl:attribute name = "LastName">
				<xsl:value-of select = "OME:LastName"/>
			</xsl:attribute>
			<xsl:attribute name = "Email">
				<xsl:value-of select = "OME:Email"/>
			</xsl:attribute>
			<xsl:attribute name = "Group">
				<xsl:value-of select = "OME:GroupRef/@ID [1]"/>
			</xsl:attribute>
			<xsl:apply-templates select = "OME:Institution" mode = "OptionalAttribute"/>
		</xsl:element>
		<xsl:apply-templates select = "OME:GroupRef" mode = "MakeMapRefs"/>
	</xsl:template>
	<!-- Group -->
	<xsl:template match = "OME:Group">
		<xsl:element name = "Group">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Name">
				<xsl:value-of select = "@Name"/>
			</xsl:attribute>
			<xsl:apply-templates select = "OME:Leader" mode = "MakeRefs">
				<xsl:with-param name = "Name">Leader</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "OME:Contact" mode = "MakeRefs">
				<xsl:with-param name = "Name">Contact</xsl:with-param>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
	<!-- Experiment -->
	<xsl:template match = "OME:Experiment">
		<xsl:element name = "Experiment">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Type">
				<xsl:value-of select = "@Type"/>
			</xsl:attribute>
			<xsl:apply-templates select = "OME:Description" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "OME:ExperimenterRef" mode = "MakeRefs"/>
		</xsl:element>
	</xsl:template>
	<!-- Instrument -->
	<xsl:template match = "OME:Instrument">
		<xsl:element name = "Instrument">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Manufacturer">
				<xsl:value-of select = "OME:Microscope/@Manufacturer"/>
			</xsl:attribute>
			<xsl:attribute name = "Model">
				<xsl:value-of select = "OME:Microscope/@Model"/>
			</xsl:attribute>
			<xsl:attribute name = "SerialNumber">
				<xsl:value-of select = "OME:Microscope/@SerialNumber"/>
			</xsl:attribute>
			<xsl:attribute name = "Type">
				<xsl:value-of select = "OME:Microscope/@Type"/>
			</xsl:attribute>
		</xsl:element>
		<xsl:apply-templates select = "OME:LightSource"/>
		<xsl:apply-templates select = "OME:Detector"/>
		<xsl:apply-templates select = "OME:Objective"/>
		<xsl:apply-templates select = "OME:Filter"/>
		<xsl:apply-templates select = "OME:OTF"/>
	</xsl:template>
	<!-- Instrument - LightSource -->
	<xsl:template match = "OME:Instrument/OME:LightSource">
		<xsl:element name = "LightSource">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Manufacturer">
				<xsl:value-of select = "@Manufacturer"/>
			</xsl:attribute>
			<xsl:attribute name = "Model">
				<xsl:value-of select = "@Model"/>
			</xsl:attribute>
			<xsl:attribute name = "SerialNumber">
				<xsl:value-of select = "@SerialNumber"/>
			</xsl:attribute>
			<xsl:apply-templates select = "." mode = "MakeParentRef"/>
		</xsl:element>
		<xsl:apply-templates select = "OME:Laser"/>
		<xsl:apply-templates select = "OME:Filament"/>
		<xsl:apply-templates select = "OME:Arc"/>
	</xsl:template>
	<!-- LightSource - Laser -->
	<xsl:template match = "OME:LightSource/OME:Laser">
		<xsl:element name = "Laser">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "generate-id()"/>
			</xsl:attribute>
			<xsl:attribute name = "Type">
				<xsl:value-of select = "@Type"/>
			</xsl:attribute>
			<xsl:attribute name = "Medium">
				<xsl:value-of select = "@Medium"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@Wavelength" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "@FrequencyDoubled" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "@Tunable" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "@Pulse" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "@Power" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "." mode = "MakeParentRef"/>
			<xsl:apply-templates select = "OME:Pump" mode = "MakeRefs">
				<xsl:with-param name = "Name">
					<xsl:text>Pump</xsl:text>
				</xsl:with-param>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
	<!-- LightSource - Filament -->
	<xsl:template match = "OME:LightSource/OME:Filament">
		<xsl:element name = "Filament">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "generate-id()"/>
			</xsl:attribute>
			<xsl:attribute name = "Type">
				<xsl:value-of select = "@Type"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@Power" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "." mode = "MakeParentRef"/>
		</xsl:element>
	</xsl:template>
	<!-- LightSource - Arc -->
	<xsl:template match = "OME:LightSource/OME:Arc">
		<xsl:element name = "Arc">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "generate-id()"/>
			</xsl:attribute>
			<xsl:attribute name = "Type">
				<xsl:value-of select = "@Type"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@Power" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "." mode = "MakeParentRef"/>
		</xsl:element>
	</xsl:template>
	<!-- Instrument - Dectector -->
	<xsl:template match = "OME:Instrument/OME:Detector">
		<xsl:element name = "Detector">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Manufacturer">
				<xsl:value-of select = "@Manufacturer"/>
			</xsl:attribute>
			<xsl:attribute name = "Model">
				<xsl:value-of select = "@Model"/>
			</xsl:attribute>
			<xsl:attribute name = "SerialNumber">
				<xsl:value-of select = "@SerialNumber"/>
			</xsl:attribute>
			<xsl:attribute name = "Type">
				<xsl:value-of select = "@Type"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@Gain" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "@Voltage" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "@Offset" mode = "OptionalAttribute"/>
			<xsl:apply-templates select = "." mode = "MakeParentRef"/>
		</xsl:element>
	</xsl:template>
	<!-- Instrument - Objective -->
	<xsl:template match = "OME:Instrument/OME:Objective">
		<xsl:element name = "Objective">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Manufacturer">
				<xsl:value-of select = "@Manufacturer"/>
			</xsl:attribute>
			<xsl:attribute name = "Model">
				<xsl:value-of select = "@Model"/>
			</xsl:attribute>
			<xsl:attribute name = "SerialNumber">
				<xsl:value-of select = "@SerialNumber"/>
			</xsl:attribute>
			<xsl:attribute name = "LensNA">
				<xsl:value-of select = "OME:LensNA"/>
			</xsl:attribute>
			<xsl:attribute name = "Magnification">
				<xsl:value-of select = "OME:Magnification"/>
			</xsl:attribute>
			<xsl:apply-templates select = "." mode = "MakeParentRef"/>
		</xsl:element>
	</xsl:template>
	<!-- Instrument - Filter -->
	<xsl:template match = "OME:Instrument/OME:Filter">
		<xsl:element name = "Filter">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:apply-templates select = "." mode = "MakeParentRef"/>
		</xsl:element>
		<xsl:apply-templates select = "OME:ExFilter"/>
		<xsl:apply-templates select = "OME:Dichroic"/>
		<xsl:apply-templates select = "OME:EmFilter"/>
		<xsl:apply-templates select = "OME:FilterSet"/>
	</xsl:template>
	<!-- Instrument - Filter - ExFilter -->
	<xsl:template match = "OME:Instrument/OME:Filter/OME:ExFilter">
		<xsl:element name = "ExcitationFilter">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "generate-id()"/>
			</xsl:attribute>
			<xsl:attribute name = "Manufacturer">
				<xsl:value-of select = "@Manufacturer"/>
			</xsl:attribute>
			<xsl:attribute name = "Model">
				<xsl:value-of select = "@Model"/>
			</xsl:attribute>
			<xsl:attribute name = "LotNumber">
				<xsl:value-of select = "@LotNumber"/>
			</xsl:attribute>
			<xsl:attribute name = "Type">
				<xsl:value-of select = "@Type"/>
			</xsl:attribute>
			<xsl:apply-templates select = "." mode = "MakeParentRef"/>
		</xsl:element>
	</xsl:template>
	<!-- Instrument - Filter - Dichroic -->
	<xsl:template match = "OME:Instrument/OME:Filter/OME:Dichroic">
		<xsl:element name = "Dichroic">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "generate-id()"/>
			</xsl:attribute>
			<xsl:attribute name = "Manufacturer">
				<xsl:value-of select = "@Manufacturer"/>
			</xsl:attribute>
			<xsl:attribute name = "Model">
				<xsl:value-of select = "@Model"/>
			</xsl:attribute>
			<xsl:attribute name = "LotNumber">
				<xsl:value-of select = "@LotNumber"/>
			</xsl:attribute>
			<xsl:apply-templates select = "." mode = "MakeParentRef"/>
		</xsl:element>
	</xsl:template>
	<!-- Instrument - Filter - EmFilter -->
	<xsl:template match = "OME:Instrument/OME:Filter/OME:EmFilter">
		<xsl:element name = "EmissionFilter">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "generate-id()"/>
			</xsl:attribute>
			<xsl:attribute name = "Manufacturer">
				<xsl:value-of select = "@Manufacturer"/>
			</xsl:attribute>
			<xsl:attribute name = "Model">
				<xsl:value-of select = "@Model"/>
			</xsl:attribute>
			<xsl:attribute name = "LotNumber">
				<xsl:value-of select = "@LotNumber"/>
			</xsl:attribute>
			<xsl:attribute name = "Type">
				<xsl:value-of select = "@Type"/>
			</xsl:attribute>
			<xsl:apply-templates select = "." mode = "MakeParentRef"/>
		</xsl:element>
	</xsl:template>
	<!-- Instrument - Filter - FilterSet -->
	<xsl:template match = "OME:Instrument/OME:Filter/OME:FilterSet">
		<xsl:element name = "FilterSet">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "generate-id()"/>
			</xsl:attribute>
			<xsl:attribute name = "Manufacturer">
				<xsl:value-of select = "@Manufacturer"/>
			</xsl:attribute>
			<xsl:attribute name = "Model">
				<xsl:value-of select = "@Model"/>
			</xsl:attribute>
			<xsl:attribute name = "LotNumber">
				<xsl:value-of select = "@LotNumber"/>
			</xsl:attribute>
			<xsl:apply-templates select = "." mode = "MakeParentRef"/>
		</xsl:element>
	</xsl:template>
	<!-- Instrument - OTF -->
	<xsl:template match = "OME:Instrument/OME:OTF">
		<xsl:element name = "OTF">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "SizeX">
				<xsl:value-of select = "@SizeX"/>
			</xsl:attribute>
			<xsl:attribute name = "SizeY">
				<xsl:value-of select = "@SizeY"/>
			</xsl:attribute>
			<xsl:attribute name = "PixelType">
				<xsl:value-of select = "@PixelType"/>
			</xsl:attribute>
			<xsl:attribute name = "OpticalAxisAverage">
				<xsl:value-of select = "@OpticalAxisAvrg"/>
			</xsl:attribute>
			<xsl:apply-templates select = "." mode = "MakeParentRef"/>
			<xsl:apply-templates select = "OME:ObjectiveRef" mode = "MakeRefs"/>
			<xsl:apply-templates select = "OME:FilterRef" mode = "MakeRefs"/>
		</xsl:element>
	</xsl:template>
	<!-- Screen -->
	<xsl:template match = "OME:Screen">
		<xsl:element name = "Screen">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Name">
				<xsl:value-of select = "@Name"/>
			</xsl:attribute>
			<xsl:attribute name = "ExternalReference">
				<xsl:value-of select = "@ExternRef"/>
			</xsl:attribute>
			<xsl:apply-templates select = "OME:Description" mode = "OptionalAttribute"/>
		</xsl:element>
	</xsl:template>
	<!-- Plate -->
	<xsl:template match = "OME:Plate">
		<xsl:element name = "Plate">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Name">
				<xsl:value-of select = "@Name"/>
			</xsl:attribute>
			<xsl:attribute name = "ExternalReference">
				<xsl:value-of select = "@ExternRef"/>
			</xsl:attribute>
		</xsl:element>
		<xsl:apply-templates select = "OME:ScreenRef" mode = "MakeMapRefs"/>
	</xsl:template>
	<!--

		Utility Templates

	-->

	<!--
		A utility template to convert child elements and attributes to attributes.
		Does not deal with grand-child elements correctly
	-->
	<xsl:template match = "*" mode = "Element2Attributes">
		<xsl:element name = "{name()}">
			<xsl:if test = "not (string-length(@ID) > 0)">
				<xsl:attribute name = "ID">
					<xsl:value-of select = "generate-id()"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:for-each select = "@*">
				<xsl:if test = "string-length() > 0">
					<xsl:attribute name = "{name()}">
						<xsl:value-of select = "."/>
					</xsl:attribute>
				</xsl:if>
			</xsl:for-each>
			<xsl:for-each select = "*[name() != 'CustomAttributes'][substring(name(),string-length(name())-2,3) != 'Ref']">
				<xsl:attribute name = "{name()}">
					<xsl:value-of select = "."/>
				</xsl:attribute>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>
	<!-- A utility template to make a reference to a parent element -->
	<xsl:template match = "*" mode = "MakeParentRef">
		<xsl:apply-templates select = ".." mode = "MakeRefs">
			<xsl:with-param name = "Name">
				<xsl:value-of select = "name(..)"/>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>
	<!-- A utility template to make references -->
	<xsl:template match = "*" mode = "MakeRefs">
		<!--
		By default, $Name is composed of the element name minus its last three letters
		(i.e. ExperimenterRef element will set $Name to 'Experimenter'.
		-->
		<xsl:param name = "Name" select = "substring(name(),1,string-length(name())-3)"/>
		<xsl:param name = "ID" select = "@ID"/>
		<xsl:attribute name = "{$Name}">
			<xsl:value-of select = "$ID"/>
		</xsl:attribute>
	</xsl:template>
	<!-- A utility template to copy elements and their attributes -->
	<xsl:template match = "*" mode = "CopyRefs">
		<xsl:element name = "{name()}">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
	<!--
		A utility template to make reference maps for many-to-many relationships.
		Given a Reference element (i.e. GroupRef), will construct a map element with two 'Ref' elements -
		one to the parent, and one to the passed-in element.
	-->
	<xsl:template match = "*" mode = "MakeMapRefs">
		<!--
		Defaults:
			$ParentName is the name of the parent element
			$ReferenceName is the name of the element minus the last three letters.
			$Ref1 is $ParentName.
			$Ref2 is $ReferenceName.
			$MapName is composed of the $ParentName concatenated with $ReferenceName.
				(i.e. GroupRef in an Experimenter element will set $MapName to 'ExperimenterGroup'.
			$ID1 is the value of the parent element's $Ref1+'ID' attribute
			$ID2 is the value of the element's $Ref2+'ID' attribute
		-->
		<xsl:param name = "ParentName" select = "name(..)"/>
		<xsl:param name = "ReferenceName" select = "substring(name(),1,string-length(name())-3)"/>
		<xsl:param name = "Ref1" select = "$ParentName"/>
		<xsl:param name = "Ref2" select = "$ReferenceName"/>
		<xsl:param name = "MapName" select = "concat($ParentName,$ReferenceName)"/>
		<xsl:param name = "ID1" select = "../@ID"/>
		<xsl:param name = "ID2" select = "@ID"/>
		<xsl:element name = "{$MapName}">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "generate-id()"/>
			</xsl:attribute>
			<xsl:attribute name = "{$Ref1}">
				<xsl:value-of select = "$ID1"/>
			</xsl:attribute>
			<xsl:attribute name = "{$Ref2}">
				<xsl:value-of select = "$ID2"/>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>

	<xsl:template match = "*" mode = "OptionalAttribute">
		<xsl:param name = "Name" select = "name()"/>
		<xsl:param name = "Value" select = "."/>
		<xsl:if test = "string-length($Value) > 0">
			<xsl:attribute name = "{$Name}">
				<xsl:value-of select = "$Value"/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
	<xsl:template match = "@*" mode = "OptionalAttribute">
		<xsl:param name = "Name" select = "name()"/>
		<xsl:param name = "Value" select = "."/>
		<xsl:if test = "string-length($Value) > 0">
			<xsl:attribute name = "{$Name}">
				<xsl:value-of select = "$Value"/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>

	<!-- This just prints out the names of whatever nodes it gets -->
	<xsl:template match = "*" mode = "print">
		<xsl:value-of select = "name()"/>
	</xsl:template>
	<!-- This copies whatever nodes it gets -->
	<xsl:template match = "*" mode = "copy">
		<xsl:copy/>
	</xsl:template>
</xsl:transform>
