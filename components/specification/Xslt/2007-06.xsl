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

<xsl:transform
  xmlns:xsl = "http://www.w3.org/1999/XSL/Transform"
  xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2007-06"
  xmlns:AML="http://www.openmicroscopy.org/Schemas/AnalysisModule/2007-06"
  xmlns:STD="http://www.openmicroscopy.org/Schemas/STD/2007-06"
  xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2007-06"
  xmlns:CA="http://www.openmicroscopy.org/Schemas/CA/2007-06"
  xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2007-06"
  xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2007-06"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xml="http://www.w3.org/XML/1998/namespace"
  xmlns="http://www.openmicroscopy.org/Schemas/OME/2008-02"
  version="2.0">

  <xsl:output method="xml" indent="yes"/>

  <xsl:preserve-space elements="*"/>

  <!-- Actual schema changes -->

  <xsl:template match="OME:ObjectiveSettingsRef">
    <xsl:element name="ObjectiveRef" namespace="http://www.openmicroscopy.org/Schemas/OME/2008-02">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:ObjectiveSettingsRef/@ID">
    <xsl:choose>
      <xsl:when test="contains(., 'Settings')">
        <xsl:attribute name="{name()}" namespace="{namespace-uri()}">
          <xsl:value-of select="substring-before(., 'Settings')"/>
          <xsl:value-of select="substring-after(., 'Settings')"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="{name()}" namespace="{namespace-uri()}">
          <xsl:value-of select="@value"/>
        </xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
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
    <xsl:element name="{name()}" namespace="http://www.openmicroscopy.org/Schemas/OME/2008-02">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="CA:*">
    <xsl:element name="{name()}" namespace="http://www.openmicroscopy.org/Schemas/CA/2008-02">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="Bin:*">
    <xsl:element name="{name()}" namespace="http://www.openmicroscopy.org/Schemas/BinaryFile/2008-02">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="SA:*">
    <xsl:element name="{name()}" namespace="http://www.openmicroscopy.org/Schemas/SA/2008-02">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="SPW:*">
    <xsl:element name="{name()}" namespace="http://www.openmicroscopy.org/Schemas/SPW/2008-02">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="STD:*">
    <xsl:element name="{name()}" namespace="http://www.openmicroscopy.org/Schemas/STD/2008-02">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="AML:*">
    <xsl:element name="{name()}" namespace="http://www.openmicroscopy.org/Schemas/AML/2008-02">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <!-- Default processing -->

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="text()|processing-instruction()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:transform>
