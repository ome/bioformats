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
			<!-- codegen end - defaults -->
			<xsl:otherwise>
				<xsl:comment>GetDefaultUnit, the default for [<xsl:value-of select="$theAttributeName"/>] in element [<xsl:value-of select="$theElementName"/>] is not supported.</xsl:comment>
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
			<!-- codegen begin - convert -->
			<xsl:when test="$theNewUnit = ''">
				<xsl:comment>ConvertValueToUnit, cannot perform conversion of [<xsl:value-of select="$theCurrentUnit"/>] to an unknown unit.</xsl:comment>
				<xsl:message terminate="yes">OME-XSLT: units-conversion.xsl - ERROR - ConvertValueToUnit, cannot perform conversion of [<xsl:value-of select="$theCurrentUnit"/>] to an unknown unit.</xsl:message>
			</xsl:when>
			<xsl:when test="$theCurrentUnit = 'mm' and $theNewUnit = 'nm'"><xsl:value-of select="$theValue * 1000000"/></xsl:when>
			<xsl:when test="$theCurrentUnit = 'pm' and $theNewUnit = 'nm'"><xsl:value-of select="$theValue div 1000"/></xsl:when>
			<!-- codegen end - convert -->
			<xsl:otherwise>
				<xsl:comment>ConvertValueToUnit, cannot perform conversion [<xsl:value-of select="$theCurrentUnit"/>] to [<xsl:value-of select="$theNewUnit"/>] is not supported.</xsl:comment>
				<xsl:message terminate="yes">OME-XSLT: units-conversion.xsl - ERROR - ConvertValueToUnit, cannot perform conversion [<xsl:value-of select="$theCurrentUnit"/>] to [<xsl:value-of select="$theNewUnit"/>] is not supported.</xsl:message>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>