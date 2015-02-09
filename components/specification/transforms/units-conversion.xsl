<?xml version="1.0" encoding="UTF-8"?>
<!--
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#
# Copyright (C) 2014 Open Microscopy Environment
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
# Written by:  Andrew Patterson: ajpatterson at lifesci.dundee.ac.uk
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes="xs"
	version="2.0">

	<!-- getDefault function (AttributeName, NodeName, ParentNodeName?) -->
	<!-- 	returns the default as string -->
	<xsl:template name="GetDefaultUnit">
		<xsl:param name="theAttributeName"/>
		<xsl:param name="theElementName"/>
		<xsl:choose>
			<!-- codegen begin - defaults -->
			<xsl:when test="$theAttributeName = 'Wavelength' and $theElementName = 'Laser'">nm</xsl:when>
			<xsl:when test="$theAttributeName = 'Wavelength' and $theElementName = 'LightSourceSettings'">nm</xsl:when>
			<xsl:when test="$theAttributeName = 'AirPressure' and $theElementName = 'ImagingEnvironment'">mbar</xsl:when>
			<xsl:when test="$theAttributeName = 'Temperature' and $theElementName = 'ImagingEnvironment'">°C</xsl:when>
			<xsl:when test="$theAttributeName = 'PhysicalSizeX' and $theElementName = 'Pixels'">nm</xsl:when>
			<xsl:when test="$theAttributeName = 'PhysicalSizeY' and $theElementName = 'Pixels'">nm</xsl:when>
			<!-- codegen end - defaults -->
			<xsl:otherwise>
				<xsl:message terminate="yes">OME-XSLT: units-conversion.xsl - ERROR - GetDefaultUnit, the default for [<xsl:value-of select="$theAttributeName"/>] in element [<xsl:value-of select="$theElementName"/>] is not supported.</xsl:message>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- 	returns the value in the default units -->
	<!-- 	error and terminate if conversion not possible e.g. pixel->nm -->
	<xsl:template name="ConvertValueToDefault">
		<xsl:param name="theValue"/>
		<xsl:param name="theCurrentUnit"/>
		<xsl:param name="theAttributeName"/>
		<xsl:param name="theElementName"/>
		<xsl:message>OME-XSLT: units-conversion.xsl - Message - ConvertValueToDefault, (<xsl:value-of select="$theValue"/>, <xsl:value-of select="$theCurrentUnit"/>, <xsl:value-of select="$theAttributeName"/>, <xsl:value-of select="$theElementName"/>).</xsl:message>
		<xsl:choose>
			<xsl:when test="$theCurrentUnit = ''">
				<!-- Already using default units so no conversion necessary -->
				<xsl:value-of select="$theValue"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="ConvertValueToUnit">
					<xsl:with-param name="theValue"><xsl:value-of select="$theValue"/></xsl:with-param>
					<xsl:with-param name="theCurrentUnit"><xsl:value-of select="$theCurrentUnit"/></xsl:with-param>
					<xsl:with-param name="theNewUnit">
						<xsl:call-template name="GetDefaultUnit">
							<xsl:with-param name="theAttributeName"><xsl:value-of select="$theAttributeName"/></xsl:with-param>
							<xsl:with-param name="theElementName"><xsl:value-of select="$theElementName"/></xsl:with-param>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="ConvertValueToUnit">
		<xsl:param name="theValue"/>
		<xsl:param name="theCurrentUnit"/>
		<xsl:param name="theNewUnit"/>
		<xsl:choose>
			<xsl:when test="$theNewUnit = ''">
				<xsl:message terminate="yes">OME-XSLT: units-conversion.xsl - ERROR - ConvertValueToUnit, cannot perform conversion of [<xsl:value-of select="$theCurrentUnit"/>] to an unknown unit.</xsl:message>
			</xsl:when>
			<xsl:when test="$theNewUnit = $theCurrentUnit"><xsl:value-of select="$theValue"/></xsl:when>
			<!-- codegen begin - convert -->
			<xsl:when test="$theNewUnit = 'nm'">
				<xsl:choose>
					<xsl:when test="$theCurrentUnit = 'mm'"><xsl:value-of select="$theValue * 1000000"/></xsl:when>
					<xsl:when test="$theCurrentUnit = 'pm'"><xsl:value-of select="$theValue div 1000"/></xsl:when>
					<xsl:otherwise>
						<xsl:message terminate="yes">OME-XSLT: units-conversion.xsl - ERROR - ConvertValueToUnit, cannot perform conversion, [<xsl:value-of select="$theCurrentUnit"/>] to [<xsl:value-of select="$theNewUnit"/>] is not supported.</xsl:message>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$theNewUnit = '°C'">
				<xsl:choose>
					<xsl:when test="$theCurrentUnit = '°F'"><xsl:value-of select="(($theValue - 32) * 5) div 9"/></xsl:when>
					<xsl:otherwise>
						<xsl:message terminate="yes">OME-XSLT: units-conversion.xsl - ERROR - ConvertValueToUnit, cannot perform conversion, [<xsl:value-of select="$theCurrentUnit"/>] to [<xsl:value-of select="$theNewUnit"/>] is not supported.</xsl:message>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<!-- codegen end - convert -->
			<xsl:otherwise>
				<xsl:message terminate="yes">OME-XSLT: units-conversion.xsl - ERROR - ConvertValueToUnit, cannot perform conversion from any unit to [<xsl:value-of select="$theNewUnit"/>].</xsl:message>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>