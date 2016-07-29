<?xml version = "1.0" encoding = "UTF-8"?>
<!--
  #%L
  OME Data Model transforms
  %%
  Copyright (C) 2009 - 2016 Open Microscopy Environment:
    - Massachusetts Institute of Technology
    - National Institutes of Health
    - University of Dundee
    - Board of Regents of the University of Wisconsin-Madison
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
# Written by:  Andrew Patterson: ajpatterson at lifesci.dundee.ac.uk
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2013-06"
                xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2013-06"
                xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2013-06"
                xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2013-06"
                xmlns:ROI="http://www.openmicroscopy.org/Schemas/ROI/2013-06"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:xml="http://www.w3.org/XML/1998/namespace"
                exclude-result-prefixes="OME Bin SPW SA ROI"
                xmlns:exsl="http://exslt.org/common"
                extension-element-prefixes="exsl" version="1.0">

  <xsl:variable name="newOMENS">http://www.openmicroscopy.org/Schemas/OME/2012-06</xsl:variable>
  <xsl:variable name="newSPWNS">http://www.openmicroscopy.org/Schemas/SPW/2012-06</xsl:variable>
  <xsl:variable name="newBINNS"
                >http://www.openmicroscopy.org/Schemas/BinaryFile/2012-06</xsl:variable>
  <xsl:variable name="newROINS">http://www.openmicroscopy.org/Schemas/ROI/2012-06</xsl:variable>
  <xsl:variable name="newSANS">http://www.openmicroscopy.org/Schemas/SA/2012-06</xsl:variable>

  <xsl:output method="xml" indent="yes"/>
  <xsl:preserve-space elements="*"/>

  <!-- Actual schema changes -->

  <!-- The Enumeration terms to be modified. -->
  <xsl:variable name="enumeration-maps">
    <mapping name="FilterType">
      <!-- process Filter Type enumeration 'Tuneable' -->
      <map
          from="Tuneable"
          to="Other"
          />
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

  <!-- remove Filter Type enumeration 'Tuneable' -->
  <xsl:template match="OME:Filter">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name() = 'Type')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:for-each select="@* [name(.) = 'Type']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:call-template name="transformEnumerationValue">
            <xsl:with-param name="mappingName" select="'FilterType'"/>
            <xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
          </xsl:call-template>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <!-- strip Integration and Zoom -->
  <xsl:template match="OME:DetectorSettings">
    <xsl:element name="OME:DetectorSettings" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name() = 'Integration' or name() = 'Zoom')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>       
  
  <!-- strip SignificantBits, Interleaved and BigEndian -->
  <xsl:template match="OME:Pixels">
    <xsl:element name="OME:Pixels" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name() = 'SignificantBits' or name() = 'Interleaved' or name() = 'BigEndian')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>       

  <!-- strip Annotator from all child nodes of StructuredAnnotations -->
  <xsl:template match="SA:*[@Annotator]">
    <xsl:element name="{name()}" namespace="{$newSANS}">
      <xsl:for-each select="@* [not(name() = 'Annotator')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>       

  <!-- strip NominalMagnification ONLY if it is not an integer -->
  <xsl:template match="OME:Objective">
    <xsl:element name="OME:Objective" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name() = 'NominalMagnification')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:variable name="theValue" select="@NominalMagnification"/>
      <xsl:for-each select="@* [name() = 'NominalMagnification']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="round($theValue)"/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>       
  

  <!-- strip Rights and turn into comments -->
  <xsl:template match="OME:Rights">
    <xsl:for-each select="* [name() = 'RightsHolder']">
      <xsl:comment>
        Rights Holder: 
        <xsl:value-of select="."/>
      </xsl:comment>
    </xsl:for-each>     
    <xsl:for-each select="* [name() = 'RightsHeld']">
      <xsl:comment>
        Rights Held: 
        <xsl:value-of select="."/>
      </xsl:comment>
    </xsl:for-each>     
  </xsl:template>       
  

  <!-- Rewriting all namespaces -->

  <xsl:template match="OME:OME">
    <OME:OME xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2012-06"
             xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2012-06"
             xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2012-06"
             xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2012-06"
             xmlns:ROI="http://www.openmicroscopy.org/Schemas/ROI/2012-06"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2012-06
                                 http://www.openmicroscopy.org/Schemas/OME/2012-06/ome.xsd">
      <xsl:apply-templates select="@UUID|@Creator|node()"/> <!-- copy UUID and Creator attributes and nodes -->
    </OME:OME>
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
