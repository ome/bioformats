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

<xsl:stylesheet
  xmlns:xsl = "http://www.w3.org/1999/XSL/Transform"
  xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2008-09"
  xmlns:AML="http://www.openmicroscopy.org/Schemas/AnalysisModule/2008-09"
  xmlns:STD="http://www.openmicroscopy.org/Schemas/STD/2008-09"
  xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2008-09"
  xmlns:CA="http://www.openmicroscopy.org/Schemas/CA/2008-09"
  xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2008-09"
  xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2008-09"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xml="http://www.w3.org/XML/1998/namespace"
  xmlns="http://www.openmicroscopy.org/Schemas/OME/2009-09"
  version="1.0">
 
  <xsl:variable name="newOMENS">http://www.openmicroscopy.org/Schemas/OME/2009-09</xsl:variable>
  <xsl:variable name="newSPWNS">http://www.openmicroscopy.org/Schemas/SPW/2009-09</xsl:variable>
  
  <xsl:output method="xml" indent="yes"/>

  <xsl:preserve-space elements="*"/>

  <!-- Actual schema changes -->

 <!-- data management -->
   <xsl:template match="OME:Dataset">
    <xsl:element name="Dataset" namespace="{$newOMENS}">
      <xsl:copy-of select="@* [not(name() = 'Locked')]"/>  
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>
  
 <xsl:template match="OME:Experimenter">
    <xsl:element name="Experimenter" namespace="{$newOMENS}">
      <xsl:for-each select="* [not(local-name(.) = 'GroupRef')]">
          <xsl:choose>
          <xsl:when test="local-name(.) = 'OMEName'">
             <xsl:attribute name="UserName">
              <xsl:value-of select="."/>
            </xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
             <xsl:attribute name="{local-name(.)}">
              <xsl:value-of select="."/>
            </xsl:attribute>
          </xsl:otherwise>
          </xsl:choose>
      </xsl:for-each>
      <xsl:apply-templates select="@*"/>
      <xsl:for-each select="* [name() = 'GroupRef']">
        <xsl:element name="{local-name(.)}" namespace="{$newOMENS}">
            <xsl:apply-templates select="@*"/>
            <xsl:value-of select="."/>
        </xsl:element>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>
 
 <!-- Acquisition Settings -->
 
  <xsl:template match="OME:ObjectiveRef">
    <xsl:element name="ObjectiveSettings" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:LightSourceRef">
    <xsl:element name="LightSettings" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:DetectorRef">
    <xsl:element name="DetectorSettings" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

<!-- Instrument components -->

 <xsl:template match="OME:Objective">
    <xsl:element name="Objective" namespace="{$newOMENS}">
      <xsl:for-each select="*">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="@*"/>
    </xsl:element>
  </xsl:template>
  
   <xsl:template match="OME:FilterSet">
    <xsl:element name="FilterSet" namespace="{$newOMENS}">
        <xsl:for-each select="@* [not(name() = 'EmFilterRef' or name() = 'ExFilterRef' or name() = 'DichroicRef')]">
          <xsl:attribute name="{local-name(.)}">
            <xsl:value-of select="."/>
          </xsl:attribute>
        </xsl:for-each>
       <xsl:for-each select="@* [name() = 'EmFilterRef' or name() = 'ExFilterRef' or name() = 'DichroicRef']">
       <xsl:choose>
        <xsl:when test="local-name(.) = 'EmFilterRef'">
            <xsl:element name="EmissionFilterRef">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:when>
        <xsl:when test="local-name(.) = 'ExFilterRef'">
            <xsl:element name="ExcitationFilterRef">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:when>
        <xsl:otherwise>
             <xsl:element name="{local-name(.)}">
              <xsl:value-of select="."/>
            </xsl:element>
          </xsl:otherwise>
       </xsl:choose>
        </xsl:for-each>
    </xsl:element>
  </xsl:template>

<!-- Image and Pixels -->
<!--
All elements except HashSHA1
-->
   <xsl:template match="OME:Plane">
    <xsl:element name="Plane" namespace="{$newOMENS}">
     <xsl:for-each select="* [not(local-name(.) = 'HashSHA1')]">
        <xsl:for-each select="./@*">
           <xsl:attribute name="{local-name(.)}">
            <xsl:value-of select="."/>
          </xsl:attribute>
          </xsl:for-each>
     </xsl:for-each>
    <xsl:apply-templates select="@*"/>
    <xsl:for-each select="* [name() = 'HashSHA1']">
         <xsl:element name="{local-name(.)}" namespace="{$newOMENS}">
            <xsl:apply-templates select="@*"/>
          <xsl:value-of select="."/>
        </xsl:element>
      </xsl:for-each>
   </xsl:element>
   </xsl:template>
   
    <xsl:template match="OME:Pixels">
    <xsl:element name="Pixels" namespace="{$newOMENS}">
     <xsl:for-each select="@*">
       <xsl:choose>
         <xsl:when test="local-name(.) = 'PixelType'">
          <xsl:attribute name="Type">
            <xsl:value-of select="."/>
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


 <!-- General -->
  <xsl:template match="OME:Description">
    <xsl:choose>
      <xsl:when test="local-name(..) = 'Screen'">
        <xsl:element name="Description" namespace="{$newSPWNS}">
          <xsl:apply-templates select="node()"/>
        </xsl:element>
      </xsl:when>
      <xsl:otherwise>
        <xsl:element name="Description" namespace="{$newOMENS}">
          <xsl:apply-templates select="node()"/>
        </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <!-- Rewriting all namespaces -->

  <xsl:template match="OME:OME">
<OME xmlns="http://www.openmicroscopy.org/Schemas/OME/2009-09"
    xmlns:CA="http://www.openmicroscopy.org/Schemas/CA/2009-09"
    xmlns:STD="http://www.openmicroscopy.org/Schemas/STD/2009-09"
    xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2009-09"
    xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2009-09"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2009-09 http://www.openmicroscopy.org/Schemas/OME/2009-09/ome.xsd">
      <xsl:apply-templates/>
   </OME>
  </xsl:template>

  <xsl:template match="OME:*">
    <xsl:element name="{name()}" namespace="http://www.openmicroscopy.org/Schemas/OME/2009-09">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="CA:*">
    <xsl:element name="{name()}" namespace="http://www.openmicroscopy.org/Schemas/CA/2009-09">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="Bin:*">
    <xsl:element name="{name()}" namespace="http://www.openmicroscopy.org/Schemas/BinaryFile/2009-09">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="SA:*">
    <xsl:element name="{name()}" namespace="http://www.openmicroscopy.org/Schemas/SA/2009-09">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="SPW:*">
    <xsl:element name="{name()}" namespace="http://www.openmicroscopy.org/Schemas/SPW/2009-09">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="STD:*">
    <xsl:element name="{name()}" namespace="http://www.openmicroscopy.org/Schemas/STD/2009-09">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="AML:*">
    <xsl:element name="{name()}" namespace="http://www.openmicroscopy.org/Schemas/AML/2009-09">
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

</xsl:stylesheet>
