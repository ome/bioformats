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
# Written by:  Andrew Patterson: ajpatterson at lifesci.dundee.ac.uk,
#              Josh Moore, Jean-Marie Burel, Donald McDonald, Chris Allan
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2010-04"
                xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2010-04"
                xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2010-04"
                xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2010-04"
                xmlns:ROI="http://www.openmicroscopy.org/Schemas/ROI/2010-04"
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
    <mapping name="theAtribute">
      <map from="oldName" to="newName"/>
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

  <!-- Transform the value from the unsigned int color range to the signed int color range -->
  <xsl:template name="transformColorToSignedValue">
    <xsl:param name="colorValue"/>
    <xsl:choose>
      <xsl:when test="$colorValue > 2147483647">
        <xsl:value-of select="0 - ( 4294967296 - $colorValue )"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$colorValue"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Actual schema changes -->

  <xsl:template match="ROI:Mask">
    <xsl:element name="ROI:Mask" namespace="{$newROINS}">
      <xsl:attribute name="Width">
        <xsl:value-of select="$numberDefault"/>
      </xsl:attribute>
      <xsl:attribute name="Height">
        <xsl:value-of select="$numberDefault"/>
      </xsl:attribute>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="ROI:Shape">
    <xsl:element name="ROI:Shape" namespace="{$newROINS}">
      <xsl:for-each select="@*">
        <xsl:choose>
          <xsl:when test="name() = 'Fill' or name() = 'Stroke'">
            <xsl:attribute name="{local-name(.)}">
              <xsl:call-template name="transformColorToSignedValue">
                <xsl:with-param name="colorValue">
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

  <xsl:template match="OME:Channel">
    <xsl:element name="OME:Channel" namespace="{$newOMENS}">
      <xsl:for-each select="@*">
        <xsl:choose>
          <xsl:when test="name() = 'Color'">
            <xsl:attribute name="Color">
              <xsl:call-template name="transformColorToSignedValue">
                <xsl:with-param name="colorValue">
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

  <xsl:template match="SPW:Well">
    <xsl:element name="SPW:Well" namespace="{$newSPWNS}">
      <xsl:for-each select="@*">
        <xsl:choose>
          <xsl:when test="name() = 'Color'">
            <xsl:attribute name="Color">
              <xsl:call-template name="transformColorToSignedValue">
                <xsl:with-param name="colorValue">
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

  <xsl:template match="SPW:WellSample">
    <xsl:element name="SPW:WellSample" namespace="{$newSPWNS}">
      <xsl:for-each select="@*">
        <xsl:choose>
          <xsl:when test="name() = 'Timepoint'">
            <!-- 1970-01-01T00:00:00.0Z -->
            <!--
                Currently the value Timepoint is stripped as we can see
                no valid way of transforming it to the new type.
            -->
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

  <xsl:template match="SA:StringAnnotation">
    <xsl:element name="SA:CommentAnnotation" namespace="{$newSANS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
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
      <xsl:apply-templates/>
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
