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
                xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2015-01"
                xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2015-01"
                xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2015-01"
                xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2015-01"
                xmlns:ROI="http://www.openmicroscopy.org/Schemas/ROI/2015-01"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:xml="http://www.w3.org/XML/1998/namespace"
                exclude-result-prefixes="OME Bin SPW SA ROI"
                xmlns:exsl="http://exslt.org/common"
                extension-element-prefixes="exsl" version="1.0">

  <xsl:import href="units-conversion.xsl"/>

  <xsl:variable name="newOMENS">http://www.openmicroscopy.org/Schemas/OME/2013-06</xsl:variable>
  <xsl:variable name="newSPWNS">http://www.openmicroscopy.org/Schemas/SPW/2013-06</xsl:variable>
  <xsl:variable name="newBINNS"
                >http://www.openmicroscopy.org/Schemas/BinaryFile/2013-06</xsl:variable>
  <xsl:variable name="newROINS">http://www.openmicroscopy.org/Schemas/ROI/2013-06</xsl:variable>
  <xsl:variable name="newSANS">http://www.openmicroscopy.org/Schemas/SA/2013-06</xsl:variable>

  <xsl:output method="xml" indent="yes"/>
  <xsl:preserve-space elements="*"/>

  <!-- Actual schema changes -->

  <!-- strip EmissionWavelength and ExcitationWavelength ONLY if it is not an integer -->
  <xsl:template match="OME:Channel">
    <xsl:element name="OME:Channel" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name() = 'EmissionWavelength' or name() = 'ExcitationWavelength' or name() = 'EmissionWavelengthUnit' or name() = 'ExcitationWavelengthUnit' or name() = 'PinholeSize' or name() = 'PinholeSizeUnit')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:variable name="theConvertedValuePinhole">
        <xsl:call-template name="ConvertValueToDefault">
          <xsl:with-param name="theValue"><xsl:value-of select="@PinholeSize"/></xsl:with-param>
          <xsl:with-param name="theCurrentUnit"><xsl:value-of select="@PinholeSizeUnit"/></xsl:with-param>
          <xsl:with-param name="theAttributeName">PinholeSize</xsl:with-param>
          <xsl:with-param name="theElementName">Channel</xsl:with-param>
        </xsl:call-template>
      </xsl:variable>
      <xsl:for-each select="@* [name() = 'PinholeSize']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="$theConvertedValuePinhole"/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:variable name="theConvertedValueEm">
        <xsl:call-template name="ConvertValueToDefault">
          <xsl:with-param name="theValue"><xsl:value-of select="@EmissionWavelength"/></xsl:with-param>
          <xsl:with-param name="theCurrentUnit"><xsl:value-of select="@EmissionWavelengthUnit"/></xsl:with-param>
          <xsl:with-param name="theAttributeName">EmissionWavelength</xsl:with-param>
          <xsl:with-param name="theElementName">Channel</xsl:with-param>
        </xsl:call-template>
      </xsl:variable>
      <xsl:for-each select="@* [name() = 'EmissionWavelength']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="round($theConvertedValueEm)"/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:variable name="theConvertedValueEx">
        <xsl:call-template name="ConvertValueToDefault">
          <xsl:with-param name="theValue"><xsl:value-of select="@ExcitationWavelength"/></xsl:with-param>
          <xsl:with-param name="theCurrentUnit"><xsl:value-of select="@ExcitationWavelengthUnit"/></xsl:with-param>
          <xsl:with-param name="theAttributeName">ExcitationWavelength</xsl:with-param>
          <xsl:with-param name="theElementName">Channel</xsl:with-param>
        </xsl:call-template>
      </xsl:variable>
      <xsl:for-each select="@* [name() = 'ExcitationWavelength']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="round($theConvertedValueEx)"/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <!-- strip Wavelength from LightSourceSettings ONLY if it is not an integer -->
  <xsl:template match="OME:LightSourceSettings">
    <xsl:element name="OME:LightSourceSettings" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name() = 'Wavelength' or name() = 'WavelengthUnit')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:variable name="theConvertedValue">
        <xsl:call-template name="ConvertValueToDefault">
          <xsl:with-param name="theValue"><xsl:value-of select="@Wavelength"/></xsl:with-param>
          <xsl:with-param name="theCurrentUnit"><xsl:value-of select="@WavelengthUnit"/></xsl:with-param>
          <xsl:with-param name="theAttributeName">Wavelength</xsl:with-param>
          <xsl:with-param name="theElementName">LightSourceSettings</xsl:with-param>
        </xsl:call-template>
      </xsl:variable>
      <xsl:for-each select="@* [name() = 'Wavelength']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="round($theConvertedValue)"/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <!-- strip Wavelength from Laser ONLY if it is not an integer -->
  <xsl:template match="OME:Laser">
    <xsl:element name="OME:Laser" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name() = 'Wavelength' or name() = 'WavelengthUnit' or name() = 'RepetitionRate' or name() = 'RepetitionRateUnit')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:variable name="theConvertedValueRepetitionRate">
        <xsl:call-template name="ConvertValueToDefault">
          <xsl:with-param name="theValue"><xsl:value-of select="@RepetitionRate"/></xsl:with-param>
          <xsl:with-param name="theCurrentUnit"><xsl:value-of select="@RepetitionRateUnit"/></xsl:with-param>
          <xsl:with-param name="theAttributeName">RepetitionRate</xsl:with-param>
          <xsl:with-param name="theElementName">Laser</xsl:with-param>
        </xsl:call-template>
      </xsl:variable>
      <xsl:for-each select="@* [name() = 'RepetitionRate']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="$theConvertedValueRepetitionRate"/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:variable name="theConvertedValue">
        <xsl:call-template name="ConvertValueToDefault">
          <xsl:with-param name="theValue"><xsl:value-of select="@Wavelength"/></xsl:with-param>
          <xsl:with-param name="theCurrentUnit"><xsl:value-of select="@WavelengthUnit"/></xsl:with-param>
          <xsl:with-param name="theAttributeName">Wavelength</xsl:with-param>
          <xsl:with-param name="theElementName">Laser</xsl:with-param>
        </xsl:call-template>
      </xsl:variable>
      <xsl:for-each select="@* [name() = 'Wavelength']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="round($theConvertedValue)"/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <!-- strip AnnotationRef on Shape -->
  <xsl:template match="ROI:Shape">
    <xsl:element name="{name()}" namespace="{$newROINS}">
      <xsl:apply-templates select="@*"/>
      <xsl:for-each select="* [not(local-name() = 'AnnotationRef')]">
        <xsl:apply-templates select="."/>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- strip AnnotationRef on Instrument -->
  <xsl:template match="OME:Instrument">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*"/>
      <xsl:for-each select="* [not(local-name() = 'AnnotationRef')]">
        <xsl:apply-templates select="."/>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- strip AnnotationRef on Objective -->
  <xsl:template match="OME:Objective">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name() = 'WorkingDistance' or name() = 'WorkingDistanceUnit' or name() = 'AnnotationRef')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:variable name="theConvertedValueWorkingDistance">
        <xsl:call-template name="ConvertValueToDefault">
          <xsl:with-param name="theValue"><xsl:value-of select="@WorkingDistance"/></xsl:with-param>
          <xsl:with-param name="theCurrentUnit"><xsl:value-of select="@WorkingDistanceUnit"/></xsl:with-param>
          <xsl:with-param name="theAttributeName">WorkingDistance</xsl:with-param>
          <xsl:with-param name="theElementName">Objective</xsl:with-param>
        </xsl:call-template>
      </xsl:variable>
      <xsl:for-each select="@* [name() = 'WorkingDistance']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="$theConvertedValueWorkingDistance"/>
        </xsl:attribute>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- strip AnnotationRef on Detector and voltage unit-->
  <xsl:template match="OME:Detector">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name() = 'Voltage' or name() = 'VoltageUnit' or name() = 'AnnotationRef')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:variable name="theConvertedValueVoltage">
        <xsl:call-template name="ConvertValueToDefault">
          <xsl:with-param name="theValue"><xsl:value-of select="@Voltage"/></xsl:with-param>
          <xsl:with-param name="theCurrentUnit"><xsl:value-of select="@VoltageUnit"/></xsl:with-param>
          <xsl:with-param name="theAttributeName">Voltage</xsl:with-param>
          <xsl:with-param name="theElementName">Detector</xsl:with-param>
        </xsl:call-template>
      </xsl:variable>
      <xsl:for-each select="@* [name() = 'Voltage']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="$theConvertedValueVoltage"/>
        </xsl:attribute>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>


  <!-- strip AnnotationRef on Filter -->
  <xsl:template match="OME:TransmittanceRange">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name() = 'CutIn' or name() = 'CutInUnit' or name() = 'CutOut' or name() = 'CutOutUnit' or name() = 'CutInTolerance' or name() = 'CutInToleranceUnit' or name() = 'CutOutTolerance' or name() = 'CutOutToleranceUnit')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:variable name="theConvertedValueCutIn">
        <xsl:call-template name="ConvertValueToDefault">
          <xsl:with-param name="theValue"><xsl:value-of select="@CutIn"/></xsl:with-param>
          <xsl:with-param name="theCurrentUnit"><xsl:value-of select="@CutInUnit"/></xsl:with-param>
          <xsl:with-param name="theAttributeName">CutIn</xsl:with-param>
          <xsl:with-param name="theElementName">TransmittanceRange</xsl:with-param>
        </xsl:call-template>
      </xsl:variable>
      <xsl:for-each select="@* [name() = 'CutIn']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="round($theConvertedValueCutIn)"/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:variable name="theConvertedValueCutOut">
        <xsl:call-template name="ConvertValueToDefault">
          <xsl:with-param name="theValue"><xsl:value-of select="@CutOut"/></xsl:with-param>
          <xsl:with-param name="theCurrentUnit"><xsl:value-of select="@CutOutUnit"/></xsl:with-param>
          <xsl:with-param name="theAttributeName">CutOut</xsl:with-param>
          <xsl:with-param name="theElementName">TransmittanceRange</xsl:with-param>
        </xsl:call-template>
      </xsl:variable>
      <xsl:for-each select="@* [name() = 'CutOut']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="round($theConvertedValueCutOut)"/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:variable name="theConvertedValueCutInTolerance">
        <xsl:call-template name="ConvertValueToDefault">
          <xsl:with-param name="theValue"><xsl:value-of select="@CutInTolerance"/></xsl:with-param>
          <xsl:with-param name="theCurrentUnit"><xsl:value-of select="@CutInToleranceUnit"/></xsl:with-param>
          <xsl:with-param name="theAttributeName">CutInTolerance</xsl:with-param>
          <xsl:with-param name="theElementName">TransmittanceRange</xsl:with-param>
        </xsl:call-template>
      </xsl:variable>
      <xsl:for-each select="@* [name() = 'CutInTolerance']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="round($theConvertedValueCutInTolerance)"/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:variable name="theConvertedValueCutOutTolerance">
        <xsl:call-template name="ConvertValueToDefault">
          <xsl:with-param name="theValue"><xsl:value-of select="@CutOutTolerance"/></xsl:with-param>
          <xsl:with-param name="theCurrentUnit"><xsl:value-of select="@CutOutToleranceUnit"/></xsl:with-param>
          <xsl:with-param name="theAttributeName">CutOutTolerance</xsl:with-param>
          <xsl:with-param name="theElementName">TransmittanceRange</xsl:with-param>
        </xsl:call-template>
      </xsl:variable>
      <xsl:for-each select="@* [name() = 'CutOutTolerance']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="round($theConvertedValueCutOutTolerance)"/>
        </xsl:attribute>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- strip AnnotationRef on Filter -->
  <xsl:template match="OME:Filter">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*"/>
      <xsl:for-each select="* [not(name() = 'AnnotationRef')]">
        <xsl:apply-templates select="."/>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- strip AnnotationRef on Dichroic -->
  <xsl:template match="OME:Dichroic">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*"/>
      <xsl:for-each select="* [not(local-name() = 'AnnotationRef')]">
        <xsl:apply-templates select="."/>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- strip AnnotationRef on LightPath -->
  <xsl:template match="OME:LightPath">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*"/>
      <xsl:for-each select="* [not(local-name() = 'AnnotationRef')]">
        <xsl:apply-templates select="."/>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- strip AnnotationRef on LightSource -->
  <xsl:template match="OME:LightSource">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name() = 'Power' or name() = 'PowerUnit' or name() = 'AnnotationRef')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:variable name="theConvertedValuePower">
        <xsl:call-template name="ConvertValueToDefault">
          <xsl:with-param name="theValue"><xsl:value-of select="@Power"/></xsl:with-param>
          <xsl:with-param name="theCurrentUnit"><xsl:value-of select="@PowerUnit"/></xsl:with-param>
          <xsl:with-param name="theAttributeName">Power</xsl:with-param>
          <xsl:with-param name="theElementName">LightSource</xsl:with-param>
        </xsl:call-template>
      </xsl:variable>
      <xsl:for-each select="@* [name() = 'Power']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="$theConvertedValuePower"/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <!-- strip GenericExcitationSource and terminate -->
  <xsl:template match="OME:GenericExcitationSource">
    <xsl:comment>GenericExcitationSource elements cannot be converted to 2013-06 Schema, they are not supported.</xsl:comment>
    <xsl:message terminate="yes">OME-XSLT: 2015-01-to-2013-06.xsl - ERROR - GenericExcitationSource elements cannot be converted to 2011-06 Schema, they are not supported.</xsl:message>
  </xsl:template>

  <!-- strip MapAnnotation and terminate -->
  <xsl:template match="SA:MapAnnotation">
    <xsl:comment>MapAnnotation elements cannot be converted to 2013-06 Schema, they are not supported.</xsl:comment>
    <xsl:message terminate="yes">OME-XSLT: 2015-01-to-2013-06.xsl - ERROR - MapAnnotation elements cannot be converted to 2011-06 Schema, they are not supported.</xsl:message>
  </xsl:template>

  <!-- strip child nodes from ImagingEnvironment and warn about Map -->
  <xsl:template match="OME:ImagingEnvironment">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:call-template name="attribute-units-conversion"/>
    </xsl:element>
    <xsl:for-each select="* [(local-name() = 'Map')]">
      <xsl:comment>ImagingEnvironment:Map elements cannot be converted to 2013-06 Schema, they are not supported.</xsl:comment>
    </xsl:for-each>
  </xsl:template>

  <!-- Rewriting all namespaces -->

  <xsl:template match="OME:OME">
    <OME:OME xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2013-06"
             xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2013-06"
             xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2013-06"
             xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2013-06"
             xmlns:ROI="http://www.openmicroscopy.org/Schemas/ROI/2013-06"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2013-06
                                 http://www.openmicroscopy.org/Schemas/OME/2013-06/ome.xsd">
      <xsl:apply-templates select="@UUID|@Creator|node()"/> <!-- copy UUID and Creator attributes and nodes -->
    </OME:OME>
  </xsl:template>

  <xsl:template match="OME:*">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:call-template name="attribute-units-conversion"/>
      <xsl:apply-templates select="node()"/>
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

  <xsl:template name="attribute-units-conversion">
    <xsl:variable name="unit-attributes" select="@* [substring(name(), string-length(name()) - string-length('Unit') +1) = 'Unit']"/>
    <xsl:variable name="unit-attributes-count" select="count($unit-attributes)"/>
    <xsl:for-each select="@*">
      <xsl:choose>
        <xsl:when test="substring(name(), string-length(name()) - string-length('Unit') +1) = 'Unit'">
          <!-- Units attribute so do not copy -->
        </xsl:when>
        <xsl:when test="$unit-attributes-count != 0">
          <xsl:variable name="match-name"><xsl:value-of select="name()"/>Unit</xsl:variable>
          <xsl:choose>
            <xsl:when test="count($unit-attributes[name() = $match-name]) = 1">
              <!-- This attribute has units specified so convert -->
              <xsl:variable name="theUnitName"><xsl:value-of select="local-name()"/>Unit</xsl:variable>
              <xsl:variable name="theConvertedValue">
                <xsl:call-template name="ConvertValueToDefault">
                  <xsl:with-param name="theValue"><xsl:value-of select="."/></xsl:with-param>
                  <xsl:with-param name="theCurrentUnit"><xsl:value-of select="../@*[name() = $theUnitName]"/></xsl:with-param>
                  <xsl:with-param name="theAttributeName"><xsl:value-of select="local-name(.)"/></xsl:with-param>
                  <xsl:with-param name="theElementName"><xsl:value-of select="local-name(parent::node())"/></xsl:with-param>
                </xsl:call-template>
              </xsl:variable>
              <xsl:attribute name="{local-name(.)}">
                <xsl:value-of select="$theConvertedValue"/>
              </xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
              <!-- Units used but this attribute has no units specified -->
              <xsl:apply-templates select="."/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <!-- No units being used -->
          <xsl:apply-templates select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
  </xsl:template>
  <!-- Default processing -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
