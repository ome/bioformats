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
# Written by:  Andrew Patterson: ajpatterson at lifesci.dundee.ac.uk,
#              Josh Moore, Jean-Marie Burel, Donald McDonald, Chris Allan
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2010-06"
                xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2010-06"
                xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2010-06"
                xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2010-06"
                xmlns:ROI="http://www.openmicroscopy.org/Schemas/ROI/2010-06"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:xml="http://www.w3.org/XML/1998/namespace"
                exclude-result-prefixes="OME Bin SPW SA ROI"
                xmlns:exsl="http://exslt.org/common"
                extension-element-prefixes="exsl" version="1.0">

  <xsl:variable name="newOMENS"
                >http://www.openmicroscopy.org/Schemas/OME/2008-02</xsl:variable>
  <xsl:variable name="newSPWNS"
                >http://www.openmicroscopy.org/Schemas/SPW/2008-02</xsl:variable>
  <xsl:variable name="newBINNS"
                >http://www.openmicroscopy.org/Schemas/BinaryFile/2008-02</xsl:variable>
  <xsl:variable name="newSANS"
                >http://www.openmicroscopy.org/Schemas/SA/2008-02</xsl:variable>

  <xsl:output method="xml" indent="yes"/>
  <xsl:preserve-space elements="*"/>

  <!-- default value for non-numerical value when transforming the attribute of concrete shape -->
  <xsl:variable name="numberDefault" select="1"/>

  <!-- The Enumeration terms to be modified. -->
  <xsl:variable name="enumeration-maps">
    <mapping name="LaserLaserMedium">
      <map from="Other" to="Ag"/>
    </mapping>
    <mapping name="LaserType">
      <map from="Other" to="Dye"/>
    </mapping>
    <mapping name="DetectorSettingsBinning">
      <map from="Other" to="1x1"/>
    </mapping>
    <mapping name="DetectorType">
      <map from="EBCCD" to="CCD"/>
      <map from="EMCCD" to="EM-CCD"/>
    </mapping>
    <mapping name="FilterType">
      <map from="Dichroic" to="BandPass"/>
      <map from="NeutralDensity" to="BandPass"/>
      <map from="Other" to="BandPass"/>
    </mapping>
    <mapping name="ObjectiveCorrection">
      <map from="Achromat" to="UV"/>
      <map from="PlanNeofluar" to="UV"/>
      <map from="Other" to="UV"/>
    </mapping>
    <mapping name="LogicalChannelIlluminationType">
      <map from="Other" to="Transmitted"/>
    </mapping>
    <mapping name="LogicalChannelContrastMethod">
      <map from="Other" to="Brightfield"/>
    </mapping>
    <mapping name="LogicalChannelMode">
      <map from="PALM" to="Other"/>
      <map from="STORM" to="Other"/>
      <map from="STED" to="Other"/>
      <map from="TIRF" to="Other"/>
      <map from="FSM" to="Other"/>
      <map from="LCM" to="Other"/>
      <map from="LaserScanningConfocalMicroscopy" to="LaserScanningConfocal"/>
    </mapping>
    <mapping name="ObjectiveRefMedium">
      <map from="Other" to="Air"/>
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

  <!--
      convert the value of the Color attribute of Channel to a ColorDomain
      attribute on ChannelComponent.
      A limited number of colours are supported, others map to w for (white).
  -->
  <xsl:template name="convertToColorDomain">
    <xsl:param name="cc"/>
    <xsl:choose>
      <xsl:when test="contains($cc,'-16776961')">r</xsl:when>
      <xsl:when test="contains($cc,'16711935')">g</xsl:when>
      <xsl:when test="contains($cc,'65535')">b</xsl:when>
      <xsl:otherwise>w</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Actual schema changes -->

  <xsl:template match="OME:AcquiredDate">
    <xsl:element name="CreationDate" namespace="{$newOMENS}">
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:Image">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name(.) = 'ID')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:for-each select="@* [name(.) = 'ID']">
        <xsl:attribute name="ID"><xsl:value-of select="."/></xsl:attribute>
      </xsl:for-each>
      <xsl:attribute name="DefaultPixels">
        <xsl:variable name="firstPixels"><xsl:for-each select="* [local-name(.) = 'Pixels'][1]">
          <xsl:value-of select="@ID"/>
        </xsl:for-each>
        </xsl:variable>
        <xsl:value-of select="$firstPixels"/>
      </xsl:attribute>

      <xsl:choose>
        <xsl:when test="local-name(*[1])='AcquiredDate'">
          <xsl:apply-templates select="* [name(.) = 'AcquiredDate']"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:comment>Creation Date inserted by XSLT downgrade (set to 1st January 1970)</xsl:comment>
          <xsl:element name="CreationDate" namespace="{$newOMENS}">1970-01-01T00:00:00.0Z</xsl:element>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:apply-templates select="* [local-name(.) = 'ExperimenterRef']"/>
      <xsl:apply-templates select="* [local-name(.) = 'Description']"/>
      <xsl:apply-templates select="* [local-name(.) = 'ExperimentRef']"/>
      <xsl:apply-templates select="* [local-name(.) = 'GroupRef']"/>
      <xsl:apply-templates select="* [local-name(.) = 'DatasetRef']"/>
      <xsl:apply-templates select="* [local-name(.) = 'InstrumentRef']"/>
      <xsl:apply-templates select="* [local-name(.) = 'ObjectiveSettings']"/>
      <xsl:apply-templates select="* [local-name(.) = 'ImagingEnvironment']"/>
      <xsl:for-each select=" descendant::OME:Channel">
        <xsl:element name="LogicalChannel" namespace="{$newOMENS}">
          <xsl:for-each select="@* [not(name(.) = 'Color' or name(.) = 'PinholeSize')]">
            <xsl:choose>
              <xsl:when test="local-name(.)='ID'">
                <xsl:attribute name="ID">LogicalChannel:XSLT:<xsl:value-of
                select="."/></xsl:attribute>
              </xsl:when>

              <xsl:when test="local-name(.)='AcquisitionMode'">
                <xsl:attribute name="Mode">
                  <xsl:call-template name="transformEnumerationValue">
                    <xsl:with-param name="mappingName"
                                    select="'LogicalChannelMode'"/>
                    <xsl:with-param name="value">
                      <xsl:value-of select="."/>
                    </xsl:with-param>
                  </xsl:call-template>
                </xsl:attribute>
              </xsl:when>

              <xsl:when test="local-name(.)='ExcitationWavelength'">
                <xsl:attribute name="ExWave">
                  <xsl:value-of select="."/>
                </xsl:attribute>
              </xsl:when>
              <xsl:when test="local-name(.)='EmissionWavelength'">
                <xsl:attribute name="EmWave">
                  <xsl:value-of select="."/>
                </xsl:attribute>
              </xsl:when>
              <xsl:when test="local-name(.)='NDFilter'">
                <xsl:attribute name="NdFilter">
                  <xsl:value-of select="."/>
                </xsl:attribute>
              </xsl:when>
              <xsl:when test="local-name(.)='IlluminationType'">
                <xsl:attribute name="{local-name(.)}">
                  <xsl:call-template name="transformEnumerationValue">
                    <xsl:with-param name="mappingName"
                                    select="'LogicalChannelIlluminationType'"/>
                    <xsl:with-param name="value">
                      <xsl:value-of select="."/>
                    </xsl:with-param>
                  </xsl:call-template>
                </xsl:attribute>
              </xsl:when>
              <xsl:when test="local-name(.)='ContrastMethod'">
                <xsl:attribute name="{local-name(.)}">
                  <xsl:call-template name="transformEnumerationValue">
                    <xsl:with-param name="mappingName"
                                    select="'LogicalChannelContrastMethod'"/>
                    <xsl:with-param name="value">
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
          <xsl:apply-templates select="* [local-name(.) = 'LightSourceSettings']"/>
          <xsl:apply-templates select="* [local-name(.) = 'OTFRef']"/>
          <xsl:apply-templates select="* [local-name(.) = 'DetectorSettings']"/>
          <xsl:apply-templates select="* [local-name(.) = 'FilterSetRef']"/>

          <xsl:apply-templates select="* [local-name(.) = 'LightPath']"/>

          <xsl:element name="ChannelComponent" namespace="{$newOMENS}">
            <xsl:attribute name="Pixels">
              <xsl:for-each select=" parent::node()">
                <xsl:value-of select="@ID"/>
              </xsl:for-each>
            </xsl:attribute>
            <xsl:attribute name="ColorDomain">
              <xsl:call-template name="convertToColorDomain">
                <xsl:with-param name="cc" select="@Color"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:attribute name="Index">
              <xsl:value-of select="position()"/>
            </xsl:attribute>
          </xsl:element>
        </xsl:element>
      </xsl:for-each>

      <xsl:apply-templates select="* [local-name(.) = 'StageLabel']"/>
      <xsl:apply-templates select="* [local-name(.) = 'Pixels']"/>
      <xsl:apply-templates select="* [local-name(.) = 'ROIRef']"/>
      <xsl:apply-templates select="* [local-name(.) = 'MicrobeamManipulationRef']" mode="justROI"/>
      <xsl:apply-templates select="* [local-name(.) = 'MicrobeamManipulationRef']"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:LightPath">
    <xsl:comment>LightPath is not supported in 2008-02 schema.</xsl:comment>
  </xsl:template>

  <xsl:template match="OME:DetectorSettings">
    <xsl:element name="DetectorRef" namespace="{$newOMENS}">
      <xsl:for-each select="@*">
        <xsl:choose>
          <xsl:when test="local-name(.)='Binning'">
            <xsl:attribute name="{local-name(.)}">
              <xsl:call-template name="transformEnumerationValue">
                <xsl:with-param name="mappingName"
                                select="'DetectorSettingsBinning'"/>
                <xsl:with-param name="value">
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
      <xsl:apply-templates select="*"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:Pixels">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@* [name(.) = 'ID']">
        <xsl:attribute name="ID"><xsl:value-of select="."/></xsl:attribute>
      </xsl:for-each>
      <xsl:for-each select="@* [name(.) = 'Type']">
        <xsl:attribute name="PixelType">
          <xsl:call-template name="transformEnumerationValue">
            <xsl:with-param name="mappingName" select="'PixelsPixelType'"/>
            <xsl:with-param name="value"><xsl:value-of select="."/></xsl:with-param>
          </xsl:call-template>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:attribute name="BigEndian">
        <xsl:variable name="valueBigEndian">
          <xsl:for-each select="* [local-name(.) = 'BinData'][1]">
            <xsl:value-of select="@BigEndian"/>
          </xsl:for-each>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="string-length($valueBigEndian) = 0">
            <xsl:value-of select="'false'"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$valueBigEndian"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>

      <xsl:for-each select="@* [name(.) = 'DimensionOrder' or
                            name(.) = 'SizeC' or name(.) = 'SizeT' or name(.) = 'SizeX' or
                            name(.) = 'SizeY' or name(.) = 'SizeZ']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="* [local-name(.) = 'BinData']"/>
      <xsl:apply-templates select="* [local-name(.) = 'TiffData']"/>
      <xsl:apply-templates select="* [local-name(.) = 'MetadataOnly']"/>
      <xsl:apply-templates select="* [local-name(.) = 'Plane']"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:Plane">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@* [(name(.) = 'TheZ' or name(.) = 'TheC' or name(.) = 'TheT')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <!-- check if planetiming can be created -->
      <xsl:choose>
        <xsl:when test="./@DeltaT">
          <xsl:choose>
            <xsl:when test="./@ExposureTime">
              <xsl:element name="PlaneTiming" namespace="{$newOMENS}">
                <xsl:attribute name="DeltaT">
                  <xsl:value-of select="./@DeltaT"/>
                </xsl:attribute>
                <xsl:attribute name="ExposureTime">
                  <xsl:value-of select="./@ExposureTime"/>
                </xsl:attribute>
              </xsl:element>
            </xsl:when>
          </xsl:choose>
        </xsl:when>
      </xsl:choose>
      <!-- check if StageLabel can be created -->
      <xsl:choose>
        <xsl:when test="./@PositionX">
          <xsl:choose>
            <xsl:when test="./@PositionY">
              <xsl:choose>
                <xsl:when test="./@PositionZ">
                  <xsl:element name="StagePosition" namespace="{$newOMENS}">
                    <xsl:attribute name="PositionX">
                      <xsl:value-of select="./@PositionX"/>
                    </xsl:attribute>
                    <xsl:attribute name="PositionY">
                      <xsl:value-of select="./@PositionY"/>
                    </xsl:attribute>
                    <xsl:attribute name="PositionZ">
                      <xsl:value-of select="./@PositionZ"/>
                    </xsl:attribute>
                  </xsl:element>
                </xsl:when>
              </xsl:choose>
            </xsl:when>
          </xsl:choose>
        </xsl:when>
      </xsl:choose>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:TiffData">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name(.) = 'PlaneCount')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:for-each select="@* [name(.) = 'PlaneCount']">
        <xsl:attribute name="NumPlanes">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:MetadataOnly">
    <xsl:comment>MetadataOnly Files cannot be suported in version 2008-02 schema.</xsl:comment>
    <xsl:comment>Begin Dummy BinData</xsl:comment>
    <xsl:element name="BIN:BinData" namespace="{$newBINNS}">
      <xsl:attribute name="Length">0</xsl:attribute>
    </xsl:element>
    <xsl:comment>End Dummy BinData</xsl:comment>
  </xsl:template>

  <xsl:template match="OME:MicrobeamManipulationRef">
    <xsl:variable name="theMicrobeamManipulationID">
      <xsl:value-of select="@ID"/>
    </xsl:variable>
    <xsl:for-each select="//OME:MicrobeamManipulation[@ID=$theMicrobeamManipulationID]">
      <xsl:element name="MicrobeamManipulation" namespace="{$newOMENS}">
        <xsl:for-each select="@* [name(.) = 'ID']">
          <xsl:attribute name="{local-name(.)}">
            <xsl:value-of select="."/>
          </xsl:attribute>
        </xsl:for-each>
        <xsl:for-each select="@* [not(name(.) = 'Type')]">
          <xsl:attribute name="{local-name(.)}">
            <xsl:value-of select="."/>
          </xsl:attribute>
        </xsl:for-each>
        <xsl:for-each select="@* [name(.) = 'Type']">
          <xsl:attribute name="Type">
            <xsl:call-template name="replace-string-id">
              <xsl:with-param name="text"><xsl:call-template name="replace-string-id">
                <xsl:with-param name="text" select="."/>
                <xsl:with-param name="replace" select="'FLIP'"/>
                <xsl:with-param name="replacement" select="'Other'"/>
              </xsl:call-template></xsl:with-param>
              <xsl:with-param name="replace" select="'InverseFRAP'"/>
              <xsl:with-param name="replacement" select="'Other'"/>
            </xsl:call-template>
          </xsl:attribute>
        </xsl:for-each>
        <xsl:for-each select="* [local-name(.) = 'ROIRef']">
          <xsl:apply-templates select="." mode="inMicrobeam"/>
        </xsl:for-each>
        <xsl:apply-templates select="* [local-name(.) = 'ExperimenterRef']"/>
        <xsl:apply-templates select="* [local-name(.) = 'LightSourceSettings']"/>
      </xsl:element>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="OME:MicrobeamManipulationRef" mode="justROI">
    <xsl:comment>Add the roi used by MicrobeamManipulation</xsl:comment>
    <xsl:variable name="theMicrobeamManipulationID">
      <xsl:value-of select="@ID"/>
    </xsl:variable>
    <xsl:for-each select="//OME:MicrobeamManipulation[@ID=$theMicrobeamManipulationID]">
      <xsl:for-each select="* [local-name(.) = 'ROIRef']">
        <xsl:apply-templates select="."/>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="OME:MicrobeamManipulation">
    <xsl:for-each select="* [name(.) = 'ExperimenterRef']">
      <xsl:element name="{name()}" namespace="{$newOMENS}">
        <xsl:for-each select="@* [name(.) = 'ID']">
          <xsl:attribute name="{local-name(.)}">
            <xsl:value-of select="."/>
          </xsl:attribute>
        </xsl:for-each>
      </xsl:element>
    </xsl:for-each>
    <xsl:element name="MicrobeamManipulationRef" namespace="{$newOMENS}">
      <xsl:for-each select="@* [name(.) = 'ID']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="* [local-name(.) = 'MicrobeamManipulationRef']"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:Microscope">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name(.) = 'LotNumber')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="*"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:FilterSet">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name(.) = 'SerialNumber')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:Filter">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name(.) = 'SerialNumber')]">
        <xsl:choose>
          <xsl:when test="local-name(.)='Type'">
            <xsl:attribute name="{local-name(.)}">
              <xsl:call-template name="transformEnumerationValue">
                <xsl:with-param name="mappingName"
                                select="'FilterType'"/>
                <xsl:with-param name="value">
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
      <xsl:apply-templates select="*"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:Dichroic">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name(.) = 'SerialNumber')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:OTF">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@*">
        <xsl:choose>
          <xsl:when test="local-name(.)='Type'">
            <xsl:attribute name="PixelType">
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
      <xsl:apply-templates select="*"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:TransmittanceRange">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@*">
        <xsl:choose>
          <xsl:when test="local-name(.)='Transmittance'">
            <xsl:attribute name="{local-name(.)}">
              <xsl:value-of select="round(. * 100)"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
            <xsl:attribute name="{local-name(.)}">
              <xsl:value-of select="."/>
            </xsl:attribute>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
      <xsl:apply-templates select="*"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:Detector">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name(.) = 'LotNumber')]">
        <xsl:choose>
          <xsl:when test="local-name(.)='Type'">
            <xsl:attribute name="{local-name(.)}">
              <xsl:call-template name="transformEnumerationValue">
                <xsl:with-param name="mappingName"
                                select="'DetectorType'"/>
                <xsl:with-param name="value">
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
      <xsl:apply-templates select="*"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:Objective">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@* [name(.) = 'ID']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:for-each select="@* [name(.) = 'Model']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:for-each select="@* [name(.) = 'Manufacturer']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:for-each select="@* [name(.) = 'SerialNumber']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:for-each select="@* [name(.) = 'Correction']">
        <xsl:element name="Correction" namespace="{$newOMENS}">
          <xsl:call-template name="transformEnumerationValue">
            <xsl:with-param name="mappingName"
                            select="'ObjectiveCorrection'"/>
            <xsl:with-param name="value">
              <xsl:value-of select="."/>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:element>
      </xsl:for-each>
      <xsl:for-each select="@* [name(.) = 'Immersion']">
        <xsl:element name="Immersion" namespace="{$newOMENS}">
          <xsl:value-of select="."/>
        </xsl:element>
      </xsl:for-each>
      <xsl:for-each select="@* [name(.) = 'LensNA']">
        <xsl:element name="LensNA" namespace="{$newOMENS}">
          <xsl:value-of select="."/>
        </xsl:element>
      </xsl:for-each>
      <xsl:for-each select="@* [name(.) = 'NominalMagnification']">
        <xsl:element name="NominalMagnification" namespace="{$newOMENS}">
          <xsl:value-of select="."/>
        </xsl:element>
      </xsl:for-each>
      <xsl:for-each select="@* [name(.) = 'CalibratedMagnification']">
        <xsl:element name="CalibratedMagnification" namespace="{$newOMENS}">
          <xsl:value-of select="."/>
        </xsl:element>
      </xsl:for-each>
      <xsl:for-each select="@* [name(.) = 'WorkingDistance']">
        <xsl:element name="WorkingDistance" namespace="{$newOMENS}">
          <xsl:value-of select="."/>
        </xsl:element>
      </xsl:for-each>
      <xsl:apply-templates select="*"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:ObjectiveSettings">
    <xsl:element name="ObjectiveRef" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name(.) = 'RepetitionRate')]">
        <xsl:choose>
          <xsl:when test="local-name(.)='Medium'">
            <xsl:attribute name="{local-name(.)}">
              <xsl:call-template name="transformEnumerationValue">
                <xsl:with-param name="mappingName"
                                select="'ObjectiveRefMedium'"/>
                <xsl:with-param name="value">
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
      <xsl:apply-templates select="*"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:LightSource">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name(.) = 'LotNumber')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="*"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:LightSourceSettings">
    <xsl:element name="LightSourceRef" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*|*"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:Laser">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@* [not(name(.) = 'RepetitionRate')]">
        <xsl:choose>
          <xsl:when test="local-name(.)='Type'">
            <xsl:attribute name="{local-name(.)}">
              <xsl:call-template name="transformEnumerationValue">
                <xsl:with-param name="mappingName"
                                select="'LaserType'"/>
                <xsl:with-param name="value">
                  <xsl:value-of select="."/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="local-name(.)='LaserMedium'">
            <xsl:attribute name="{local-name(.)}">
              <xsl:call-template name="transformEnumerationValue">
                <xsl:with-param name="mappingName"
                                select="'LaserLaserMedium'"/>
                <xsl:with-param name="value">
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
      <xsl:apply-templates select="*"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:LightEmittingDiode">
    <xsl:comment>Was LightEmittingDiode, now converted to Arc as LED not available in legacy schema.</xsl:comment>
    <xsl:element name="Arc" namespace="{$newOMENS}">
      <xsl:attribute name="Type">
        <xsl:value-of select="'Hg'"/>
      </xsl:attribute>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:Group">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="* [local-name(.) = 'Leader']"/>
      <xsl:apply-templates select="* [local-name(.) = 'Contact']"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:Experiment">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="* [local-name(.) = 'Description']"/>
      <xsl:apply-templates select="* [local-name(.) = 'MicrobeamManipulation']"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:Experimenter">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@* [name(.) = 'ID']">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:for-each select="@* [name(.) = 'FirstName']">
        <xsl:element name="FirstName" namespace="{$newOMENS}">
          <xsl:value-of select="."/>
        </xsl:element>
      </xsl:for-each>
      <xsl:for-each select="@* [name(.) = 'LastName']">
        <xsl:element name="LastName" namespace="{$newOMENS}">
          <xsl:value-of select="."/>
        </xsl:element>
      </xsl:for-each>
      <xsl:for-each select="@* [name(.) = 'Email']">
        <xsl:element name="Email" namespace="{$newOMENS}">
          <xsl:value-of select="."/>
        </xsl:element>
      </xsl:for-each>
      <xsl:for-each select="@* [name(.) = 'Institution']">
        <xsl:element name="Institution" namespace="{$newOMENS}">
          <xsl:value-of select="."/>
        </xsl:element>
      </xsl:for-each>
      <xsl:for-each select="@* [name(.) = 'UserName' or name(.) = 'DisplayName']">
        <xsl:element name="OMEName" namespace="{$newOMENS}">
          <xsl:value-of select="."/>
        </xsl:element>
      </xsl:for-each>
      <xsl:apply-templates select="* [local-name(.) = 'GroupRef']"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:LogicalChannel">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:for-each select="@*">
        <xsl:choose>
          <xsl:when test="local-name(.)='Mode'">
            <xsl:attribute name="{local-name(.)}">
              <xsl:call-template name="transformEnumerationValue">
                <xsl:with-param name="mappingName"
                                select="'LogicalChannelMode'"/>
                <xsl:with-param name="value">
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
      <xsl:apply-templates select="*"/>
    </xsl:element>
  </xsl:template>

  <!--
      In Bin:BinData remove BigEndian attribute.
  -->
  <xsl:template match="Bin:BinData">
    <xsl:element name="{name()}" namespace="{$newBINNS}">
      <xsl:for-each select="@* [not(name(.) = 'BigEndian')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="Bin:BinaryFile">
    <xsl:element name="{name()}" namespace="{$newBINNS}">
      <xsl:for-each select="@* [not(name(.) = 'MIMEType')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="SA:*"/>
  <xsl:template match="SPW:*"/>

  <xsl:template match="SPW:Description"/>

  <xsl:template match="SPW:Reagent">
    <xsl:element name="{name()}" namespace="{$newSPWNS}">
      <xsl:apply-templates select="@*"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="SPW:Screen">
    <xsl:variable name="screenID">
      <xsl:value-of select="@* [name() = 'ID']"/>
    </xsl:variable>
    <xsl:element name="{name()}" namespace="{$newSPWNS}">
      <xsl:apply-templates select="@*"/>
      <xsl:for-each select="* [local-name(.)='Description']">
        <xsl:element name="Description" namespace="{$newOMENS}">
          <xsl:value-of select="."/>
        </xsl:element>
      </xsl:for-each>
      <xsl:apply-templates select="* [not(local-name(.)='Description')]"/>

      <!-- begin creating ScreenAcquisition -->
      <xsl:for-each select="* [local-name(.)='PlateRef']">
        <xsl:variable name="thePlateID"><xsl:value-of select="@ID"/></xsl:variable>
        <xsl:for-each select="//SPW:PlateAcquisition [ancestor::node()/@ID=$thePlateID]">
          <xsl:element name="SPW:ScreenAcquisition" namespace="{$newSPWNS}">
            <xsl:attribute name="ID">ScreenAcquisition:<xsl:value-of select="$screenID"
            />:<xsl:value-of select="@* [name() = 'ID']"/></xsl:attribute>
            <xsl:for-each select="@* [name() = 'StartTime' or name() = 'EndTime']">
              <xsl:attribute name="{local-name(.)}">
                <xsl:value-of select="."/>
              </xsl:attribute>
            </xsl:for-each>
            <xsl:apply-templates select="*"/>
          </xsl:element>
        </xsl:for-each>
      </xsl:for-each>
      <!-- end creating ScreenAcquisition -->

    </xsl:element>
  </xsl:template>

  <!-- Create PlateAcquisition, Remove DefaultSample -->
  <xsl:template match="SPW:Plate">
    <xsl:variable name="plateID">
      <xsl:value-of select="@* [name() = 'ID']"/>
    </xsl:variable>
    <xsl:element name="{name()}" namespace="{$newSPWNS}">
      <!-- copy all attributes except DefaultSample -->
      <xsl:attribute name="Name">
        <xsl:value-of select="@Name"/>
      </xsl:attribute>
      <xsl:for-each select="* [local-name(.)='Description']">
        <xsl:attribute name="Description">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>

      <xsl:for-each select="@* [not(name(.) = 'DefaultSample' or
                            name(.) = 'Name' or name(.) = 'ColumnNamingConvention' or
                            name(.) = 'RowNamingConvention' or name(.) = 'WellOriginX' or
                            name(.) = 'WellOriginY' or name(.) = 'Rows' or name(.) = 'Columns')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>

      <!-- Copy unchanged children -->
      <xsl:apply-templates select="* [local-name(.)='Description']"/>
      <xsl:apply-templates select="* [local-name(.)='ScreenRef']"/>

      <!-- begin copying Well (and adding WellSampleIndex) -->
      <xsl:variable name="wellCount">
        <xsl:value-of select="count(* [local-name(.)='Well'])"/>
      </xsl:variable>
      <xsl:for-each select="* [local-name(.)='Well']">
        <xsl:variable name="wellNumber">
          <xsl:number value="position()"/>
        </xsl:variable>
        <xsl:call-template name="convertWell">
          <xsl:with-param name="wellNode" select="."/>
          <xsl:with-param name="wellCount" select="$wellCount"/>
          <xsl:with-param name="wellNumber" select="$wellNumber"/>
        </xsl:call-template>
      </xsl:for-each>
      <!-- end copying Well -->

      <!-- Copy unchanged children -->
      <xsl:apply-templates select="* [local-name(.)='AnnotationRef']"/>

    </xsl:element>
  </xsl:template>

  <!-- SPW:Well - passing values through to well sample template -->
  <xsl:template name="convertWell">
    <xsl:param name="wellNode"/>
    <xsl:param name="wellCount"/>
    <xsl:param name="wellNumber"/>
    <xsl:element name="SPW:Well" namespace="{$newSPWNS}">
      <xsl:for-each select="@* [not(name() = 'Status' or name() = 'Color')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:for-each select="* [local-name(.)='WellSample']">
        <xsl:call-template name="convertWellSample">
          <xsl:with-param name="wellSampleNode" select="."/>
          <xsl:with-param name="wellCount" select="$wellCount"/>
          <xsl:with-param name="wellNumber" select="$wellNumber"/>
        </xsl:call-template>
      </xsl:for-each>
      <xsl:apply-templates select="* [local-name(.)='ReagentRef']"/>
      <xsl:apply-templates select="* [local-name(.)='AnnotationRef']"/>
    </xsl:element>
  </xsl:template>

  <!-- SPW:WellSample - adding index to well sample -->
  <xsl:template name="convertWellSample">
    <xsl:param name="wellSampleNode"/>
    <xsl:param name="wellCount"/>
    <xsl:param name="wellNumber"/>
    <xsl:element name="SPW:WellSample" namespace="{$newSPWNS}">
      <xsl:for-each select="@* [not(name() = 'Timepoint')]">
        <xsl:choose>
          <xsl:when test="name() = 'PositionX'">
            <xsl:attribute name="PosX">
              <xsl:value-of select="."/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="name() = 'PositionY'">
            <xsl:attribute name="PosY">
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
      <xsl:attribute name="Index">
        <xsl:value-of select="(($wellNumber) + ($wellCount * (position() - 1)))"/>
      </xsl:attribute>
      <xsl:apply-templates select="*"/>
    </xsl:element>
  </xsl:template>

  <!-- Rewriting all namespaces -->

  <xsl:template match="OME:OME">
    <OME xmlns="http://www.openmicroscopy.org/Schemas/OME/2008-02"
         xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2008-02"
         xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2008-02"
         xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2008-02"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2008-02
                             http://www.openmicroscopy.org/Schemas/OME/2008-02/ome.xsd">
      <xsl:apply-templates select="@UUID"/> <!-- copy UUID attribute -->
      <xsl:apply-templates select="* [local-name(.) = 'Project']"/>
      <xsl:apply-templates select="* [local-name(.) = 'Dataset']"/>
      <xsl:apply-templates select="* [local-name(.) = 'Experiment']"/>
      <xsl:apply-templates select="* [local-name(.) = 'Plate']"/>
      <xsl:apply-templates select="* [local-name(.) = 'Screen']"/>
      <xsl:apply-templates select="* [local-name(.) = 'Experimenter']"/>
      <xsl:apply-templates select="* [local-name(.) = 'Group']"/>
      <xsl:apply-templates select="* [local-name(.) = 'Instrument']"/>
      <xsl:apply-templates select="* [local-name(.) = 'Image']"/>
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

  <xsl:template match="SPW:*">
    <xsl:element name="{name()}" namespace="{$newSPWNS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="ROI:ROIRef">
    <xsl:comment>Only 2D Rectangle ROI supported in 2008-02: <xsl:value-of select="@ID"/></xsl:comment>
    <xsl:variable name="roiID" select="@ID"/>
    <xsl:for-each select="exsl:node-set(//OME:OME/ROI:ROI[@ID=$roiID])">
      <xsl:for-each select="exsl:node-set(ROI:Union/ROI:Shape/ROI:Rectangle)">
        <xsl:element name="ROI" namespace="{$newOMENS}">
          <xsl:attribute name="ID">
            <xsl:value-of select="$roiID"/>
          </xsl:attribute>
          <xsl:attribute name="X0">
            <xsl:value-of select="round(@X)"/>
          </xsl:attribute>
          <xsl:attribute name="Y0">
            <xsl:value-of select="round(@Y)"/>
          </xsl:attribute>
          <xsl:attribute name="X1">
            <xsl:value-of select="round(@X + @Width)"/>
          </xsl:attribute>
          <xsl:attribute name="Y1">
            <xsl:value-of select="round(@Y + @Height)"/>
          </xsl:attribute>
        </xsl:element>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="ROI:ROIRef" mode="inMicrobeam">
    <xsl:comment>Only 2D Rectangle ROI supported in 2008-02: <xsl:value-of select="@ID"/></xsl:comment>
    <xsl:variable name="roiID" select="@ID"/>
    <xsl:for-each select="exsl:node-set(//OME:OME/ROI:ROI[@ID=$roiID])">
      <xsl:for-each select="exsl:node-set(ROI:Union/ROI:Shape/ROI:Rectangle)">
        <xsl:element name="ROIRef" namespace="{$newOMENS}">
          <xsl:attribute name="ID">
            <xsl:value-of select="$roiID"/>
          </xsl:attribute>
        </xsl:element>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>

  <!-- Default processing -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <!-- Replace string -->
  <xsl:template name="replace-string-id">
    <xsl:param name="text"/>
    <xsl:param name="replace"/>
    <xsl:param name="replacement"/>
    <xsl:choose>
      <xsl:when test="contains($text, $replace)">
        <xsl:value-of select="substring-before($text, $replace)"/>
        <xsl:value-of select="$replacement"/>
        <xsl:value-of select="substring-after($text, $replace)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
