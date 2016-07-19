<?xml version = "1.0" encoding = "UTF-8"?>
<!--
  #%L
  OME Data Model transforms
  %%
  Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

  <xsl:variable name="newOMENS">http://www.openmicroscopy.org/Schemas/OME/2015-01</xsl:variable>
  <xsl:variable name="newSPWNS">http://www.openmicroscopy.org/Schemas/SPW/2015-01</xsl:variable>
  <xsl:variable name="newBINNS"
                >http://www.openmicroscopy.org/Schemas/BinaryFile/2015-01</xsl:variable>
  <xsl:variable name="newROINS">http://www.openmicroscopy.org/Schemas/ROI/2015-01</xsl:variable>
  <xsl:variable name="newSANS">http://www.openmicroscopy.org/Schemas/SA/2015-01</xsl:variable>

  <xsl:output method="xml" indent="yes"/>
  <xsl:preserve-space elements="*"/>

  <!-- Actual schema changes -->

  <!-- strip AnnotationRef on WellSample -->
  <xsl:template match="SPW:WellSample">
    <xsl:element name="{name()}" namespace="{$newSPWNS}">
      <xsl:apply-templates select="@*"/>
      <xsl:for-each select="* [not(local-name() = 'AnnotationRef')]">
        <xsl:apply-templates select="."/>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- strip AnnotationRef on Pixels -->
  <xsl:template match="OME:Pixels">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*"/>
      <xsl:for-each select="* [not(local-name() = 'AnnotationRef')]">
        <xsl:apply-templates select="."/>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- add any AnnotationRef to Image that are on Pixels and WellSample -->
  <xsl:template match="OME:Image">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="*"/>
      <xsl:variable name="theImageID"><xsl:value-of select="@ID"/></xsl:variable>
      <xsl:for-each select="//SPW:WellSample/OME:ImageRef [@ID=$theImageID]">
        <xsl:for-each select="parent::* [local-name() = 'AnnotationRef']">
          <xsl:apply-templates select="."/>
        </xsl:for-each>
      </xsl:for-each>
      <xsl:for-each select="* [local-name() = 'Pixels']">
        <xsl:for-each select="* [local-name() = 'AnnotationRef']">
          <xsl:apply-templates select="."/>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- Rewriting all namespaces -->

  <xsl:template match="OME:OME">
    <OME xmlns="http://www.openmicroscopy.org/Schemas/OME/2015-01"
         xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2015-01"
         xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2015-01"
         xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2015-01"
         xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2015-01"
         xmlns:ROI="http://www.openmicroscopy.org/Schemas/ROI/2015-01"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01
                             http://www.openmicroscopy.org/Schemas/OME/2015-01/ome.xsd">
      <xsl:apply-templates select="@UUID|@Creator|node()"/> <!-- copy UUID and Creator attributes and nodes -->
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
