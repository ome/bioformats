<?xml version = "1.0" encoding = "UTF-8"?>
<!--
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#
# Copyright (C) 2009 - 2016 Open Microscopy Environment
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
                xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2011-06"
                xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2011-06"
                xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2011-06"
                xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2011-06"
                xmlns:ROI="http://www.openmicroscopy.org/Schemas/ROI/2011-06"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:xml="http://www.w3.org/XML/1998/namespace"
                exclude-result-prefixes="OME Bin SPW SA ROI"
                xmlns:exsl="http://exslt.org/common"
                extension-element-prefixes="exsl" version="1.0">

  <xsl:variable name="newOMENS">http://www.openmicroscopy.org/Schemas/OME/2010-06</xsl:variable>
  <xsl:variable name="newSPWNS">http://www.openmicroscopy.org/Schemas/SPW/2010-06</xsl:variable>
  <xsl:variable name="newBINNS"
                >http://www.openmicroscopy.org/Schemas/BinaryFile/2010-06</xsl:variable>
  <xsl:variable name="newROINS">http://www.openmicroscopy.org/Schemas/ROI/2010-06</xsl:variable>
  <xsl:variable name="newSANS">http://www.openmicroscopy.org/Schemas/SA/2010-06</xsl:variable>

  <xsl:output method="xml" indent="yes"/>
  <xsl:preserve-space elements="*"/>

  <!-- default value for non-numerical value when transforming the attribute of concrete shape -->
  <xsl:variable name="numberDefault" select="1"/>

  <!-- The Enumeration terms to be modified. -->
  <xsl:variable name="enumeration-maps">
    <mapping name="FontFamily">
      <map from="sans-serif" to="Arial"/>
      <map from="monospace" to="Courier"/>
      <map from="serif" to="TimesNewRoman"/>
      <map from="cursive" to="Arial"/>
      <map from="fantasy" to="Arial"/>
    </mapping>
    <mapping name="FontStyle">
      <map from="Normal" to="Regular"/>
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

  <xsl:template match="OME:MicrobeamManipulation">
    <xsl:element name="OME:MicrobeamManipulation" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*"/>
      <xsl:for-each select="* [not(local-name(.) = 'Description')]">
        <xsl:apply-templates select="."/>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <xsl:template match="ROI:Shape">
    <xsl:element name="ROI:Shape" namespace="{$newROINS}">
      <xsl:for-each select="@*">
        <xsl:choose>
          <xsl:when test="name() = 'FontFamily' or name() = 'FontStyle'">
            <xsl:attribute name="{local-name(.)}">
              <xsl:call-template name="transformEnumerationValue">
                <xsl:with-param name="mappingName"><xsl:value-of select="local-name(.)"/></xsl:with-param>
                <xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
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

  <xsl:template match="OME:BinaryOnly">
    <xsl:comment>BinaryOnly elements cannot be converted to 2010-06 Schema, they are not supported.</xsl:comment>
    <xsl:message terminate="yes">OME-XSLT: 2011-06-to-2010-06.xsl - ERROR - BinaryOnly elements cannot be converted to 2010-06 Schema, they are not supported.</xsl:message>
  </xsl:template>

  <!-- Rewriting all namespaces -->

  <xsl:template match="OME:OME">
    <OME xmlns="http://www.openmicroscopy.org/Schemas/OME/2010-06"
         xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2010-06"
         xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2010-06"
         xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2010-06"
         xmlns:ROI="http://www.openmicroscopy.org/Schemas/ROI/2010-06"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2010-06
                             http://www.openmicroscopy.org/Schemas/OME/2010-06/ome.xsd">
      <xsl:apply-templates select="@UUID|node()"/> <!-- copy UUID attribute and nodes -->
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

  <!-- Default processing -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
