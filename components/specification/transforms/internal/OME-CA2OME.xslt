<?xml version = "1.0" encoding = "UTF-8"?>
<!--
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#
# Copyright (C) 2003 - 2015 Open Microscopy Environment
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
	xmlns:CA = "http://www.openmicroscopy.org/XMLschemas/CA/RC1/CA.xsd"
	xmlns:Bin = "http://www.openmicroscopy.org/XMLschemas/BinaryFile/RC1/BinaryFile.xsd"
	xmlns:STD = "http://www.openmicroscopy.org/XMLschemas/STD/RC2/STD.xsd"
	xmlns:AML = "http://www.openmicroscopy.org/XMLschemas/AnalysisModule/RC1/AnalysisModule.xsd"
	xmlns:DH = "http://www.openmicroscopy.org/XMLschemas/DataHistory/IR3/DataHistory.xsd"
	xmlns:OME = "http://www.openmicroscopy.org/XMLschemas/OME/FC/ome.xsd"
	xmlns = "http://www.openmicroscopy.org/XMLschemas/OME/FC/ome.xsd">

	<xsl:template match = "*">
		<xsl:copy-of select="."/>
	</xsl:template>

	<!--
		Pass through Custom attributes that we don't deal with explicitly in the stylesheet
		This utility template copies the contents of a generic CA.
	-->
	<xsl:template match = "CA:CustomAttributes/*" mode = "pass-through-CAs">
		<xsl:element name = "{name()}">
			<xsl:for-each select = "@*">
				<xsl:if test = "string-length() > 0">
					<xsl:attribute name = "{name()}">
						<xsl:value-of select = "."/>
					</xsl:attribute>
				</xsl:if>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<!-- Global CAs -->
	<xsl:template match = "CA:OME/CA:CustomAttributes" mode = "pass-through-CAs">
		<xsl:element name = "CustomAttributes">
			<xsl:apply-templates select = "*
				[name() != 'Experiment']
				[name() != 'Plate']
				[name() != 'Screen']
				[name() != 'PlateScreen']
				[name() != 'Experimenter']
				[name() != 'Group']
				[name() != 'ExperimenterGroup']
				[name() != 'Repository']
				[name() != 'OTF']
				[name() != 'Objective']
				[name() != 'LightSource']
				[name() != 'Laser']
				[name() != 'Filament']
				[name() != 'Arc']
				[name() != 'Filter']
				[name() != 'ExcitationFilter']
				[name() != 'EmissionFilter']
				[name() != 'FilterSet']
				[name() != 'Detector']
				[name() != 'Instrument']"
				mode = "pass-through-CAs"/>
		</xsl:element>
	</xsl:template>

	<!-- Dataset CAs -->
	<xsl:template match = "CA:Dataset/CA:CustomAttributes" mode = "pass-through-CAs">
		<xsl:element name = "CustomAttributes">
			<xsl:apply-templates select = "*" mode = "pass-through-CAs"/>
		</xsl:element>
	</xsl:template>

	<!-- Image CAs -->
	<xsl:template match = "CA:Image/CA:CustomAttributes" mode = "pass-through-CAs">
		<xsl:element name = "CustomAttributes">
			<xsl:apply-templates select = "*
				[name() != 'Dimensions']
				[name() != 'PixelChannelComponent']
				[name() != 'DisplayChannel']
				[name() != 'DisplayROI']
				[name() != 'ImageInstrument']
				[name() != 'ImageExperiment']
				[name() != 'ImageExperimenter']
				[name() != 'ImageGroup']
				[name() != 'ImagingEnvironment']
				[name() != 'Thumbnail']
				[name() != 'LogicalChannel']
				[name() != 'DisplayOptions']
				[name() != 'StageLabel']
				[name() != 'ImagePlate']
				[name() != 'Pixels']"
				mode = "pass-through-CAs"/>
		</xsl:element>
	</xsl:template>

	<!-- Feature CAs -->
	<xsl:template match = "CA:Feature/CA:CustomAttributes" mode = "pass-through-CAs">
		<xsl:element name = "CustomAttributes">
			<xsl:apply-templates select = "*" mode = "pass-through-CAs"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match = "CA:OME">
		<xsl:element name = "OME"
			namespace = "http://www.openmicroscopy.org/XMLschemas/OME/FC/ome.xsd"
			xmlns = "http://www.openmicroscopy.org/XMLschemas/OME/FC/ome.xsd">
			<xsl:attribute name = "xsi:schemaLocation">
				<xsl:value-of select = "@xsi:schemaLocation"/>
			</xsl:attribute>

			<xsl:apply-templates select = "CA:Project"/>
			<xsl:apply-templates select = "CA:Dataset"/>
			<xsl:apply-templates select = "CA:CustomAttributes/CA:Experiment"/>
			<xsl:apply-templates select = "CA:CustomAttributes/CA:Plate"/>
			<xsl:apply-templates select = "CA:CustomAttributes/CA:Screen"/>
			<xsl:apply-templates select = "CA:CustomAttributes/CA:Experimenter"/>
			<xsl:apply-templates select = "CA:CustomAttributes/CA:Group"/>
			<xsl:apply-templates select = "CA:CustomAttributes/CA:Instrument"/>
			<xsl:apply-templates select = "CA:Image"/>
			<xsl:apply-templates select = "CA:CustomAttributes" mode = "pass-through-CAs"/>
			<xsl:apply-templates select = "STD:*"/>
			<xsl:apply-templates select = "AML:*"/>
			<xsl:apply-templates select = "DH:*"/>
		</xsl:element>
	</xsl:template>


	<!-- This is a general template for the Description elements -->
	<xsl:template match = "@Description">
		<xsl:if test = "string-length(.) > 0">
			<xsl:element name = "{name()}">
				<xsl:value-of select = "."/>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<!-- This is a general template for attributes that become optional elements
		(check for string length of the attribute value) -->
	<xsl:template match = "@*" mode = "Attribute2OptionalElement">
		<xsl:if test = "string-length(.) > 0">
			<xsl:element name = "{name()}">
				<xsl:value-of select = "."/>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<!-- This is a general template for attributes that become optional attributes
		(check for string length of the attribute value) -->
	<xsl:template match = "@*" mode = "Attribute2OptionalAttribute">
		<xsl:param name="AttrName" select = "name(.)"/>
		<xsl:if test = "string-length(.) > 0">
			<xsl:attribute name = "{$AttrName}">
				<xsl:value-of select = "."/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>

	<!-- General template for making a reference in an attribute -->
	<xsl:template match = "@*" mode = "MakeOMEref">
		<xsl:param name = "RefName" select = "concat(name(),'Ref')"/>
		<xsl:param name = "RefIDName">ID</xsl:param>
		<xsl:param name = "RefID" select = "."/>
		<xsl:if test="string-length($RefID) > 0">
			<xsl:element name = "{$RefName}">
				<xsl:attribute name = "{$RefIDName}">
					<xsl:value-of select = "$RefID"/>
				</xsl:attribute>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<!-- General template for making a reference in an element from a reference in an element -->
	<xsl:template match = "*" mode = "MakeOMEref">
		<xsl:param name = "RefName" select = "name()"/>
		<xsl:param name = "RefIDName">ID</xsl:param>
		<xsl:param name = "RefID" select = "@ID"/>
		<xsl:if test="string-length($RefID) > 0">
			<xsl:element name = "{$RefName}">
				<xsl:attribute name = "{$RefIDName}">
					<xsl:value-of select = "$RefID"/>
				</xsl:attribute>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<!-- Specific sections of the OME Schema -->
	<xsl:template match = "CA:Project">
		<xsl:element name = "Project">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Name">
				<xsl:value-of select = "@Name"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@Description" mode = "Attribute2OptionalElement"/>
			<xsl:apply-templates select = "@Experimenter" mode = "MakeOMEref"/>
			<xsl:apply-templates select = "@Group" mode = "MakeOMEref"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:Dataset">
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
			<xsl:apply-templates select = "@Description" mode = "Attribute2OptionalElement"/>
			<xsl:apply-templates select = "@Experimenter" mode = "MakeOMEref"/>
			<xsl:apply-templates select = "@Group" mode = "MakeOMEref"/>
			<xsl:apply-templates select = "CA:ProjectRef" mode = "MakeOMEref"/>
			<xsl:apply-templates select = "CA:CustomAttributes" mode = "pass-through-CAs"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:Image">
		<xsl:variable name = "ID" select = "@ID"/>
		<xsl:element name = "Image">
			<xsl:attribute name = "Name">
				<xsl:value-of select = "@Name"/>
			</xsl:attribute>
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "DefaultPixels">
				<xsl:value-of select = "@DefaultPixels"/>
			</xsl:attribute>
			<xsl:if test = "string-length(CA:CustomAttributes/CA:Dimensions/@PixelSizeC) > 0">
				<xsl:attribute name = "WaveIncrement">
					<xsl:value-of select = "round(CA:CustomAttributes/CA:Dimensions/@PixelSizeC)"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test = "string-length(CA:CustomAttributes/CA:Dimensions/@PixelSizeT) > 0">
				<xsl:attribute name = "TimeIncrement">
					<xsl:value-of select = "round(CA:CustomAttributes/CA:Dimensions/@PixelSizeT)"/>
				</xsl:attribute>
			</xsl:if>
<!--
			<xsl:attribute name = "TimeIncrement">
				<xsl:value-of select = "CA:CustomAttributes/CA:Dimensions/@PixelSizeT"/>
			</xsl:attribute>
-->
			<xsl:apply-templates select = "CA:CustomAttributes/CA:Dimensions/@PixelSizeX" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "CA:CustomAttributes/CA:Dimensions/@PixelSizeY" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "CA:CustomAttributes/CA:Dimensions/@PixelSizeZ" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "@CreationDate" mode = "Attribute2OptionalElement"/>
			<xsl:apply-templates select = "@Experimenter" mode = "MakeOMEref"/>
			<xsl:apply-templates select = "@Description" mode = "Attribute2OptionalElement"/>
			<xsl:apply-templates select = "CA:CustomAttributes/CA:ImageExperiment [string-length(@Experiment) > 0]"/>
			<xsl:apply-templates select = "@Group" mode = "MakeOMEref"/>
			<xsl:apply-templates select = "CA:DatasetRef" mode = "MakeOMEref"/>
			<xsl:apply-templates select = "CA:CustomAttributes/CA:ImageInstrument [string-length(@Instrument) > 0] [string-length(@Objective) > 0]"/>
			<xsl:apply-templates select = "CA:CustomAttributes/CA:ImagingEnvironment"/>
			<xsl:apply-templates select = "CA:CustomAttributes/CA:Thumbnail"/>
			<xsl:apply-templates select = "CA:CustomAttributes/CA:LogicalChannel"/>
			<xsl:apply-templates select = "CA:CustomAttributes/CA:DisplayOptions"/>
			<xsl:apply-templates select = "CA:CustomAttributes/CA:StageLabel"/>
			<xsl:apply-templates select = "CA:CustomAttributes/CA:ImagePlate"/>
			<xsl:apply-templates select = "CA:CustomAttributes/CA:Pixels"/>
			<xsl:apply-templates select = "CA:Feature"/>
			<xsl:apply-templates select = "CA:CustomAttributes" mode = "pass-through-CAs"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match = "CA:Feature">
		<xsl:element name = "Feature">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Tag">
				<xsl:value-of select = "@Tag"/>
			</xsl:attribute>
			<xsl:attribute name = "Name">
				<xsl:value-of select = "@Name"/>
			</xsl:attribute>
			<xsl:apply-templates select = "CA:Feature"/>
			<xsl:apply-templates select = "CA:CustomAttributes" mode = "pass-through-CAs"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match = "CA:ImageExperiment">
		<xsl:apply-templates select = "@Experiment" mode = "MakeOMEref"/>
	</xsl:template>
	<xsl:template match = "CA:Experiment">
		<xsl:element name = "Experiment">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Type">
				<xsl:value-of select = "@Type"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@Description" mode = "Attribute2OptionalElement"/>
			<xsl:apply-templates select = "@Experimenter" mode = "MakeOMEref"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:Plate">
		<xsl:variable name = "ID" select = "@ID"/>
		<xsl:element name = "Plate">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Name">
				<xsl:value-of select = "@Name"/>
			</xsl:attribute>
			<xsl:if test = "string-length(@ExternalReference) > 0">
				<xsl:attribute name = "ExternRef">
					<xsl:value-of select = "@ExternalReference"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select = "../CA:PlateScreen/@Screen [../@Plate=$ID]" mode = "MakeOMEref"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:Screen">
		<xsl:element name = "Screen">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Name">
				<xsl:value-of select = "@Name"/>
			</xsl:attribute>
			<xsl:if test = "string-length(@ExternalReference) > 0">
				<xsl:attribute name = "ExternRef">
					<xsl:value-of select = "@ExternalReference"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select = "@Description" mode = "Attribute2OptionalElement"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:Experimenter">
		<xsl:variable name = "ID" select = "@ID"/>
		<xsl:element name = "Experimenter">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:element name = "FirstName">
				<xsl:value-of select = "@FirstName"/>
			</xsl:element>
			<xsl:element name = "LastName">
				<xsl:value-of select = "@LastName"/>
			</xsl:element>
			<xsl:element name = "Email">
				<xsl:value-of select = "@Email"/>
			</xsl:element>
			<xsl:element name = "Institution">
				<xsl:value-of select = "@Institution"/>
			</xsl:element>
			<xsl:apply-templates select = "@Group" mode = "MakeOMEref"/>
			<xsl:apply-templates select = "../CA:ExperimenterGroup/@Group [../@Experimenter=$ID]" mode = "MakeOMEref"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:Group">
		<xsl:element name = "Group">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Name">
				<xsl:value-of select = "@Name"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@Leader" mode = "MakeOMEref">
				<xsl:with-param name = "RefName">
					<xsl:text>Leader</xsl:text>
				</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "@Contact" mode = "MakeOMEref">
				<xsl:with-param name = "RefName">
					<xsl:text>Contact</xsl:text>
				</xsl:with-param>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:Instrument">
		<xsl:variable name = "ID" select = "@ID"/>
		<xsl:element name = "Instrument">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "$ID"/>
			</xsl:attribute>
			<xsl:element name = "Microscope">
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
			</xsl:element>
			<xsl:apply-templates select = "../CA:LightSource [@Instrument=$ID]"/>
			<xsl:apply-templates select = "../CA:Detector [@Instrument=$ID]"/>
			<xsl:apply-templates select = "../CA:Objective [@Instrument=$ID]"/>
			<xsl:apply-templates select = "../CA:Filter [@Instrument=$ID]"/>
			<xsl:apply-templates select = "../CA:OTF [@Instrument=$ID]"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:LightSource">
		<xsl:variable name = "ID" select = "@ID"/>
		<xsl:element name = "LightSource">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "$ID"/>
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
			<xsl:apply-templates select = "/CA:OME/CA:CustomAttributes/CA:Laser [@LightSource=$ID]"/>
			<xsl:apply-templates select = "/CA:OME/CA:CustomAttributes/CA:Filament [@LightSource=$ID]"/>
			<xsl:apply-templates select = "/CA:OME/CA:CustomAttributes/CA:Arc [@LightSource=$ID]"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:Laser">
		<xsl:element name = "Laser">
			<xsl:attribute name = "Type">
				<xsl:value-of select = "@Type"/>
			</xsl:attribute>
			<xsl:attribute name = "Medium">
				<xsl:value-of select = "@Medium"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@Wavelength" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "@FrequencyDoubled" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "@Tunable" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "@Pulse" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "@Power" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "@Pump" mode = "MakeOMEref">
				<xsl:with-param name = "RefName">
					<xsl:text>Pump</xsl:text>
				</xsl:with-param>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:Filament">
		<xsl:element name = "Filament">
			<xsl:attribute name = "Type">
				<xsl:value-of select = "@Type"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@Power" mode = "Attribute2OptionalAttribute"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:Arc">
		<xsl:element name = "Arc">
			<xsl:attribute name = "Type">
				<xsl:value-of select = "@Type"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@Power" mode = "Attribute2OptionalAttribute"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:Detector">
		<xsl:element name = "Detector">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Type">
				<xsl:value-of select = "@Type"/>
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
			<xsl:apply-templates select = "@Gain" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "@Voltage" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "@Offset" mode = "Attribute2OptionalAttribute"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:Objective">
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
			<xsl:apply-templates select = "@LensNA" mode = "Attribute2OptionalElement"/>
			<xsl:apply-templates select = "@Magnification" mode = "Attribute2OptionalElement"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:Filter">
		<xsl:variable name = "ID" select = "@ID"/>
		<xsl:element name = "Filter">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "$ID"/>
			</xsl:attribute>
			<xsl:apply-templates select = "/CA:OME/CA:CustomAttributes/CA:FilterSet [@Filter=$ID]"/>
			<xsl:apply-templates select = "/CA:OME/CA:CustomAttributes/CA:ExcitationFilter [@Filter=$ID]"/>
			<xsl:apply-templates select = "/CA:OME/CA:CustomAttributes/CA:EmissionFilter [@Filter=$ID]"/>
			<xsl:apply-templates select = "/CA:OME/CA:CustomAttributes/CA:Dichroic [@Filter=$ID]"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:FilterSet">
		<xsl:element name = "FilterSet">
			<xsl:attribute name = "Manufacturer">
				<xsl:value-of select = "@Manufacturer"/>
			</xsl:attribute>
			<xsl:attribute name = "Model">
				<xsl:value-of select = "@Model"/>
			</xsl:attribute>
			<xsl:attribute name = "LotNumber">
				<xsl:value-of select = "@LotNumber"/>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:ExcitationFilter">
		<xsl:element name = "ExFilter">
			<xsl:attribute name = "Manufacturer">
				<xsl:value-of select = "@Manufacturer"/>
			</xsl:attribute>
			<xsl:attribute name = "Model">
				<xsl:value-of select = "@Model"/>
			</xsl:attribute>
			<xsl:attribute name = "LotNumber">
				<xsl:value-of select = "@LotNumber"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@Type" mode = "Attribute2OptionalAttribute"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:EmissionFilter">
		<xsl:element name = "EmFilter">
			<xsl:attribute name = "Manufacturer">
				<xsl:value-of select = "@Manufacturer"/>
			</xsl:attribute>
			<xsl:attribute name = "Model">
				<xsl:value-of select = "@Model"/>
			</xsl:attribute>
			<xsl:attribute name = "LotNumber">
				<xsl:value-of select = "@LotNumber"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@Type" mode = "Attribute2OptionalAttribute"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:Dichroic">
		<xsl:element name = "Dichroic">
			<xsl:attribute name = "Manufacturer">
				<xsl:value-of select = "@Manufacturer"/>
			</xsl:attribute>
			<xsl:attribute name = "Model">
				<xsl:value-of select = "@Model"/>
			</xsl:attribute>
			<xsl:attribute name = "LotNumber">
				<xsl:value-of select = "@LotNumber"/>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:OTF">
		<xsl:element name = "OTF">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "PixelType">
				<xsl:value-of select = "@PixelType"/>
			</xsl:attribute>
			<xsl:attribute name = "OpticalAxisAvrg">
				<xsl:value-of select = "@OpticalAxisAverage"/>
			</xsl:attribute>
			<xsl:attribute name = "SizeX">
				<xsl:value-of select = "@SizeX"/>
			</xsl:attribute>
			<xsl:attribute name = "SizeY">
				<xsl:value-of select = "@SizeY"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@Objective" mode = "MakeOMEref"/>
			<xsl:apply-templates select = "@Filter" mode = "MakeOMEref"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:ImagingEnvironment">
		<xsl:element name = "ImagingEnvironment">
			<xsl:apply-templates select = "@Temperature" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "@AirPressure" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "@Humidity" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "@CO2Percent" mode = "Attribute2OptionalAttribute"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:ImageInstrument">
		<xsl:element name = "InstrumentRef">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@Instrument"/>
			</xsl:attribute>
		</xsl:element>
		<xsl:element name = "ObjectiveRef">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@Objective"/>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:Thumbnail">
		<xsl:if test="string-length(@Path) > 0">
			<xsl:element name = "Thumbnail">
				<xsl:attribute name = "ID">
					<xsl:value-of select = "@ID"/>
				</xsl:attribute>
				<xsl:apply-templates select = "@Path" mode = "Attribute2OptionalAttribute">
					<xsl:with-param name="AttrName">href</xsl:with-param>
				</xsl:apply-templates>
				<xsl:apply-templates select = "@MimeType" mode = "Attribute2OptionalAttribute">
					<xsl:with-param name="AttrName">MIMEtype</xsl:with-param>
				</xsl:apply-templates>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<xsl:template match = "CA:LogicalChannel">
		<xsl:variable name = "ID" select = "@ID"/>
		<xsl:element name = "ChannelInfo">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "$ID"/>
			</xsl:attribute>
			<xsl:apply-templates select = "@Name" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "@SamplesPerPixel" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "@IlluminationType" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "@PinholeSize" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "@PhotometricInterpretation" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "@Mode" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "@ContrastMethod" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "@ExcitationWavelength" mode = "Attribute2OptionalAttribute">
				<xsl:with-param name="AttrName">ExWave</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "@EmissionWavelength" mode = "Attribute2OptionalAttribute">
				<xsl:with-param name="AttrName">EmWave</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "@Fluor" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "@NDFilter" mode = "Attribute2OptionalAttribute">
				<xsl:with-param name="AttrName">NDfilter</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "@LightSource"/>
			<xsl:apply-templates select = "@AuxLightSource"/>
			<xsl:apply-templates select = "@OTF" mode = "MakeOMEref"/>
			<xsl:apply-templates select = "@Detector"/>
			<xsl:apply-templates select = "@Filter" mode = "MakeOMEref"/>
			<xsl:apply-templates select = "../CA:PixelChannelComponent [@LogicalChannel=$ID]"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:LogicalChannel/@LightSource">
		<xsl:if test="string-length(.) > 0">
			<xsl:element name = "LightSourceRef">
				<xsl:attribute name = "ID">
					<xsl:value-of select="."/>
				</xsl:attribute>
				<xsl:apply-templates select = "../@LightAttenuation" mode = "Attribute2OptionalAttribute">
					<xsl:with-param name="AttrName">Attenuation</xsl:with-param>
				</xsl:apply-templates>
				<xsl:apply-templates select = "../@LightWavelength" mode = "Attribute2OptionalAttribute">
					<xsl:with-param name="AttrName">Wavelength</xsl:with-param>
				</xsl:apply-templates>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<xsl:template match = "CA:LogicalChannel/@AuxLightSource">
		<xsl:if test="string-length(.) > 0">
			<xsl:element name = "AuxLightSourceRef">
				<xsl:attribute name = "ID">
					<xsl:value-of select="."/>
				</xsl:attribute>
				<xsl:apply-templates select = "../@AuxLightAttenuation" mode = "Attribute2OptionalAttribute">
					<xsl:with-param name="AttrName">Attenuation</xsl:with-param>
				</xsl:apply-templates>
				<xsl:apply-templates select = "../@AuxLightWavelength" mode = "Attribute2OptionalAttribute">
					<xsl:with-param name="AttrName">Wavelength</xsl:with-param>
				</xsl:apply-templates>
				<xsl:apply-templates select = "../@AuxTechnique" mode = "Attribute2OptionalAttribute">
					<xsl:with-param name="AttrName">Technique</xsl:with-param>
				</xsl:apply-templates>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<xsl:template match = "CA:LogicalChannel/@Detector">
		<xsl:if test="string-length(.) > 0">
			<xsl:element name = "DetectorRef">
				<xsl:attribute name = "ID">
					<xsl:value-of select="."/>
				</xsl:attribute>
				<xsl:apply-templates select = "../@DetectorOffset" mode = "Attribute2OptionalAttribute">
					<xsl:with-param name="AttrName">Offset</xsl:with-param>
				</xsl:apply-templates>
				<xsl:apply-templates select = "../@DetectorGain" mode = "Attribute2OptionalAttribute">
					<xsl:with-param name="AttrName">Gain</xsl:with-param>
				</xsl:apply-templates>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<xsl:template match = "CA:PixelChannelComponent">
		<xsl:element name = "ChannelComponent">
			<xsl:attribute name = "Pixels">
				<xsl:value-of select = "@Pixels"/>
			</xsl:attribute>
			<xsl:attribute name = "Index">
				<xsl:value-of select = "@Index"/>
			</xsl:attribute>
			<xsl:attribute name = "ColorDomain">
				<xsl:value-of select = "@ColorDomain"/>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:DisplayOptions">
		<xsl:variable name = "ID" select = "@ID"/>
		<xsl:variable name = "RedChannelID" select = "@RedChannel"/>
		<xsl:variable name = "GreenChannelID" select = "@GreenChannel"/>
		<xsl:variable name = "BlueChannelID" select = "@BlueChannel"/>
		<xsl:variable name = "GreyChannelID" select = "@GreyChannel"/>
		<xsl:element name = "DisplayOptions">
			<xsl:attribute name = "ID">
				<xsl:value-of select = "@ID"/>
			</xsl:attribute>
			<xsl:attribute name = "Display">
				<xsl:choose>
					<xsl:when test="@DisplayRGB = 'true' or @DisplayRGB = '1'">
						<xsl:text>RGB</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Grey</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:apply-templates select = "@Zoom" mode = "Attribute2OptionalAttribute"/>
			<xsl:apply-templates select = "../CA:DisplayChannel [@ID=$RedChannelID]" mode="MakeDisplayChannel">
				<xsl:with-param name="Name">RedChannel</xsl:with-param>
				<xsl:with-param name="isOn">
					<xsl:value-of select="@RedChannelOn"/>
				</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "../CA:DisplayChannel [@ID=$GreenChannelID]" mode="MakeDisplayChannel">
				<xsl:with-param name="Name">GreenChannel</xsl:with-param>
				<xsl:with-param name="isOn">
					<xsl:value-of select="@GreenChannelOn"/>
				</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "../CA:DisplayChannel [@ID=$BlueChannelID]" mode="MakeDisplayChannel">
				<xsl:with-param name="Name">BlueChannel</xsl:with-param>
				<xsl:with-param name="isOn">
					<xsl:value-of select="@BlueChannelOn"/>
				</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select = "../CA:DisplayChannel [@ID=$GreyChannelID]" mode="MakeDisplayChannel">
				<xsl:with-param name="Name">GreyChannel</xsl:with-param>
			</xsl:apply-templates>
			<xsl:if test = "string-length(@ZStart) > 0 or string-length(@ZStop) > 0">
				<xsl:element name = "Projection">
					<xsl:if test = "string-length(@ZStart) > 0">
						<xsl:attribute name = "Zstart">
							<xsl:value-of select = "@ZStart"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test = "string-length(@ZStop) > 0">
						<xsl:attribute name = "Zstop">
							<xsl:value-of select = "@ZStop"/>
						</xsl:attribute>
					</xsl:if>
				</xsl:element>
			</xsl:if>
			<xsl:if test = "string-length(@TStart) > 0 or string-length(@TStop) > 0">
				<xsl:element name = "Time">
					<xsl:if test = "string-length(@TStart) > 0">
						<xsl:attribute name = "Tstart">
							<xsl:value-of select = "@TStart"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test = "string-length(@TStop) > 0">
						<xsl:attribute name = "Tstop">
							<xsl:value-of select = "@TStop"/>
						</xsl:attribute>
					</xsl:if>
				</xsl:element>
			</xsl:if>
			<xsl:apply-templates select = "../CA:DisplayROI [@DisplayOptions=$ID]"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:DisplayChannel" mode="MakeDisplayChannel">
		<xsl:param name = "Name"/>
		<xsl:param name = "isOn">true</xsl:param>
		<xsl:variable name = "ID" select = "@ID"/>
		<xsl:element name = "{$Name}">
			<xsl:apply-templates select = "@* [name() != 'ID']"/>
			<xsl:if test="$Name != 'GreyChannel'">
				<xsl:attribute name = "isOn">
					<xsl:choose>
						<xsl:when test="$isOn = 'true' or $isOn = '1'">
							<xsl:text>true</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>false</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:DisplayChannel/@*">
		<xsl:attribute name = "{name()}">
			<xsl:value-of select = "."/>
		</xsl:attribute>
	</xsl:template>
	<xsl:template match = "CA:DisplayROI">
		<xsl:element name = "ROI">
			<xsl:attribute name = "X0">
				<xsl:value-of select = "@X0"/>
			</xsl:attribute>
			<xsl:attribute name = "X1">
				<xsl:value-of select = "@X1"/>
			</xsl:attribute>
			<xsl:attribute name = "Y0">
				<xsl:value-of select = "@Y0"/>
			</xsl:attribute>
			<xsl:attribute name = "Y1">
				<xsl:value-of select = "@Y1"/>
			</xsl:attribute>
			<xsl:attribute name = "Z0">
				<xsl:value-of select = "@Z0"/>
			</xsl:attribute>
			<xsl:attribute name = "Z1">
				<xsl:value-of select = "@Z1"/>
			</xsl:attribute>
			<xsl:attribute name = "T0">
				<xsl:value-of select = "@T0"/>
			</xsl:attribute>
			<xsl:attribute name = "T1">
				<xsl:value-of select = "@T1"/>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:StageLabel">
		<xsl:element name = "StageLabel">
			<xsl:attribute name = "Name">
				<xsl:value-of select = "@Name"/>
			</xsl:attribute>
			<xsl:attribute name = "X">
				<xsl:value-of select = "@X"/>
			</xsl:attribute>
			<xsl:attribute name = "Y">
				<xsl:value-of select = "@Y"/>
			</xsl:attribute>
			<xsl:attribute name = "Z">
				<xsl:value-of select = "@Z"/>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
	<xsl:template match = "CA:ImagePlate">
		<xsl:if test="string-length(@Plate) > 0">
			<xsl:element name = "PlateRef">
				<xsl:attribute name = "ID">
					<xsl:value-of select = "@Plate"/>
				</xsl:attribute>
				<xsl:attribute name = "Well">
					<xsl:value-of select = "@Well"/>
				</xsl:attribute>
				<xsl:attribute name = "Sample">
					<xsl:value-of select = "@Sample"/>
				</xsl:attribute>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<xsl:template match = "CA:Pixels">
		<xsl:element name = "{name()}">
			<xsl:for-each select = "@*">
				<xsl:choose >
					<xsl:when test = "starts-with (string(),'uint')">
						<xsl:attribute name = "{name()}">
							<xsl:value-of select = "concat('Uint',substring(string(),5))"/>
						</xsl:attribute>
					</xsl:when>
					<xsl:when test = "string-length() > 0">
						<xsl:attribute name = "{name()}">
							<xsl:value-of select = "."/>
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise/>
				</xsl:choose >
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<xsl:template match = "*" mode = "print">
		<xsl:element name = "{name()}">
			<xsl:value-of select = "."/>
		</xsl:element>
	</xsl:template>
</xsl:transform>
