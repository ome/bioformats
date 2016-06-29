<?xml version = "1.0" encoding = "UTF-8"?>
<!--
  #%L
  BSD implementations of Bio-Formats readers and writers
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
# Written by:  Josh Moore, josh at glencoesoftware.com
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2008-09"
                xmlns:AML="http://www.openmicroscopy.org/Schemas/AnalysisModule/2008-09"
                xmlns:CLI="http://www.openmicroscopy.org/Schemas/CLI/2008-09"
                xmlns:MLI="http://www.openmicroscopy.org/Schemas/MLI/2008-09"
                xmlns:STD="http://www.openmicroscopy.org/Schemas/STD/2008-09"
                xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2008-09"
                xmlns:CA="http://www.openmicroscopy.org/Schemas/CA/2008-09"
                xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2008-09"
                xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2008-09"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:xml="http://www.w3.org/XML/1998/namespace"
                exclude-result-prefixes="OME AML CLI MLI STD Bin CA SPW SA"
                xmlns:exsl="http://exslt.org/common"
                extension-element-prefixes="exsl" version="1.0">
  <!-- xmlns="http://www.openmicroscopy.org/Schemas/OME/2009-09"-->
  <xsl:variable name="newOMENS">http://www.openmicroscopy.org/Schemas/OME/2009-09</xsl:variable>
  <xsl:variable name="newSPWNS">http://www.openmicroscopy.org/Schemas/SPW/2009-09</xsl:variable>
  <xsl:variable name="newBINNS">http://www.openmicroscopy.org/Schemas/BinaryFile/2009-09</xsl:variable>
  <xsl:variable name="newROINS">http://www.openmicroscopy.org/Schemas/ROI/2009-09</xsl:variable>
  <xsl:variable name="newCANS">http://www.openmicroscopy.org/Schemas/CA/2009-09</xsl:variable>
  <xsl:variable name="newSANS">http://www.openmicroscopy.org/Schemas/SA/2009-09</xsl:variable>
  <xsl:variable name="newSTDNS">http://www.openmicroscopy.org/Schemas/STD/2009-09</xsl:variable>
  <xsl:variable name="newAMLNS">http://www.openmicroscopy.org/Schemas/AnalysisModule/2009-09</xsl:variable>
  <xsl:variable name="newMLINS">http://www.openmicroscopy.org/Schemas/MLI/2009-09</xsl:variable>
  <xsl:variable name="newCLINS">http://www.openmicroscopy.org/Schemas/CLI/2009-09</xsl:variable>

  <xsl:output method="xml" indent="yes"/>
  <xsl:preserve-space elements="*"/>

  <!-- default value for points attribute for Polygon and PolyLine -->
  <xsl:variable name="pointsDefault" select="'0,0 1,1'"/>

  <!-- default value for non-numerical value when transforming the attribute of concrete shape -->
  <xsl:variable name="numberDefault" select="1"/>

  <!-- The Enumeration terms to be modified. -->
  <xsl:variable name="enumeration-maps">
    <mapping name="DetectorType">
      <map from="EM-CCD" to="EMCCD"/>
    </mapping>
    <mapping name="LaserMedium">
      <map from="Unknown" to="Other"/>
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
        <!--
            The isOptional value is not used in this transform
        -->
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Actual schema changes -->

  <!-- Rewriting all namespaces -->

  <xsl:template match="OME:OME">
    <OME xmlns="http://www.openmicroscopy.org/Schemas/OME/2009-09"
         xmlns:CA="http://www.openmicroscopy.org/Schemas/CA/2009-09"
         xmlns:STD="http://www.openmicroscopy.org/Schemas/STD/2009-09"
         xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2009-09"
         xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2009-09"
         xmlns:AML="http://www.openmicroscopy.org/Schemas/AnalysisModule/2009-09"
         xmlns:CLI="http://www.openmicroscopy.org/Schemas/CLI/2009-09"
         xmlns:MLI="http://www.openmicroscopy.org/Schemas/MLI/2009-09"
         xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2009-09"
         xmlns:ROI="http://www.openmicroscopy.org/Schemas/ROI/2009-09"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2009-09 http://www.openmicroscopy.org/Schemas/OME/2009-09/ome.xsd">
      <xsl:apply-templates select="@UUID|node()"/> <!-- copy UUID attribute and nodes -->
      <xsl:comment>Append all ROI nodes</xsl:comment>
      <xsl:for-each select="exsl:node-set(OME:Image/OME:ROI)">
        <xsl:comment>ROI node from Image</xsl:comment>
        <xsl:element name="ROI:ROI" namespace="{$newROINS}">
          <xsl:apply-templates select="@*|node()"/>
          <xsl:call-template name="possibleSALink">
            <xsl:with-param name="theID">
              <xsl:value-of select="@ID"/>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:element>
      </xsl:for-each>
      <xsl:for-each select="exsl:node-set(OME:Image/OME:DisplayOptions/OME:ROI)">
        <xsl:comment>ROI node from DisplayOptions</xsl:comment>
        <xsl:element name="ROI:ROI" namespace="{$newROINS}">
          <xsl:apply-templates select="@*|node()"/>
          <xsl:call-template name="possibleSALink">
            <xsl:with-param name="theID">
              <xsl:value-of select="@ID"/>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:element>
      </xsl:for-each>

    </OME>
  </xsl:template>

  <xsl:template match="OME:*">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="CA:*">
    <xsl:element name="{name()}" namespace="{$newCANS}">
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

  <xsl:template match="SA:List">
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <xsl:template match="SA:List/SA:Link">
    <xsl:variable name="theLinkValue"><xsl:value-of select="node()"/></xsl:variable>
    <xsl:choose>
      <xsl:when test="//SA:StructuredAnnotations/* [@ID=$theLinkValue]">
        <xsl:element name="SA:AnnotationRef" namespace="{$newSANS}">
          <xsl:attribute name="ID"><xsl:value-of select="$theLinkValue"/></xsl:attribute>
        </xsl:element>
      </xsl:when>
      <xsl:otherwise>
        <xsl:comment>Link to <xsl:value-of select="$theLinkValue"/> removed as only annotation links now supported.</xsl:comment>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="SA:Link">
    <xsl:comment>Link to <xsl:value-of select="node()"/> reversed (if possible) or removed (if not).</xsl:comment>
  </xsl:template>

  <xsl:template match="SPW:*">
    <xsl:element name="{name()}" namespace="{$newSPWNS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="STD:*">
    <xsl:element name="{name()}" namespace="{$newSTDNS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="AML:*">
    <xsl:comment> AnalysisModule Removed </xsl:comment>
  </xsl:template>

  <xsl:template match="MLI:*">
    <xsl:element name="{name()}" namespace="{$newMLINS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="CLI:*">
    <xsl:element name="{name()}" namespace="{$newCLINS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <!-- data management -->
  <!-- Remove the Locked attribute -->
  <xsl:template match="OME:Dataset">
    <xsl:element name="Dataset" namespace="{$newOMENS}">
      <xsl:copy-of select="@* [not(name() = 'Locked')]"/>
      <xsl:apply-templates select="node()"/>
      <xsl:call-template name="possibleSALink">
        <xsl:with-param name="theID">
          <xsl:value-of select="@ID"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:element>
  </xsl:template>

  <!--
      Convert element into Attribute except GroupRef
      Rename attribute OMEName into UserName
  -->
  <xsl:template match="OME:Experimenter">
    <xsl:element name="Experimenter" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*"/>
      <!--
          determine the value of the attribute
          Rule: OMEName, Email, LastName+FirstName
      -->
      <xsl:variable name="displayName">
        <xsl:for-each
            select="* [not(local-name(.) = 'Institution' or local-name(.) = 'GroupRef')]">
          <xsl:choose>
            <xsl:when test="local-name(.) = 'Email'">
              <!-- check if a OMEName exists. -->
              <xsl:variable name="userName">
                <xsl:copy-of select="following-sibling::OME:OMEName"/>
              </xsl:variable>
              <xsl:if test="count(exsl:node-set($userName)/*)=0">
                <xsl:value-of select="."/>
              </xsl:if>
            </xsl:when>
            <xsl:when test="local-name(.) = 'OMEName'">
              <xsl:value-of select="."/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:variable name="userName">
                <xsl:copy-of select="following-sibling::OME:OMEName"/>
              </xsl:variable>
              <xsl:variable name="email">
                <xsl:copy-of select="following-sibling::OME:Email"/>
              </xsl:variable>
              <xsl:if
                  test="(count(exsl:node-set($userName)/*)+count(exsl:node-set($email)/*))=0">
                <xsl:value-of select="."/>
              </xsl:if>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>
      </xsl:variable>

      <!-- insert DisplayName attribute -->
      <xsl:attribute name="DisplayName">
        <xsl:value-of select="$displayName"/>
      </xsl:attribute>

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
      <xsl:for-each select="* [name() = 'GroupRef']">
        <xsl:element name="{local-name(.)}" namespace="{$newOMENS}">
          <xsl:apply-templates select="@*"/>
          <xsl:value-of select="."/>
        </xsl:element>
      </xsl:for-each>
      <xsl:call-template name="possibleSALink">
        <xsl:with-param name="theID">
          <xsl:value-of select="@ID"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:element>
  </xsl:template>

  <!-- Acquisition Settings -->

  <!-- Rename ObjectiveRef to ObjectiveSettings -->
  <xsl:template match="OME:ObjectiveRef">
    <xsl:element name="ObjectiveSettings" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <!-- Rename LightSourceRef to LightSettings -->
  <xsl:template match="OME:LightSourceRef">
    <xsl:element name="LightSourceSettings" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <!-- Rename DetectorRef to DetectorSettings -->
  <xsl:template match="OME:DetectorRef">
    <xsl:element name="DetectorSettings" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>


  <!-- Instrument components -->

  <!--Transform the value of the Transmittance attribute from integer to percentFraction -->
  <xsl:template match="OME:TransmittanceRange">
    <xsl:element name="TransmittanceRange" namespace="{$newOMENS}">
      <xsl:for-each select="@*">
        <xsl:attribute name="{local-name(.)}">
          <xsl:choose>
            <xsl:when test="local-name(.) ='Transmittance'">
              <xsl:call-template name="convertPercentFraction">
                <xsl:with-param name="value">
                  <xsl:value-of select="."/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="."/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- Transform the value of RepetitionRate attribute from boolean to float -->
  <xsl:template match="OME:Laser">
    <xsl:variable name="false" select="0"/>
    <xsl:variable name="true" select="1"/>
    <xsl:element name="Laser" namespace="{$newOMENS}">
      <xsl:for-each select="@*">
        <xsl:attribute name="{local-name(.)}">
          <xsl:choose>
            <xsl:when test="local-name(.) ='RepetitionRate'">
              <xsl:choose>
                <xsl:when test="@RepetitionRate = 'true' or @RepetitionRate = 't'">
                  <xsl:value-of select="$true"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="$false"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:when>
            <xsl:when test="local-name(.) ='Type'">
              <xsl:call-template name="transformEnumerationValue">
                <xsl:with-param name="mappingName" select="'LightSourceType'"/>
                <xsl:with-param name="value">
                  <xsl:value-of select="."/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:when test="local-name(.) ='LaserMedium'">
              <xsl:call-template name="transformEnumerationValue">
                <xsl:with-param name="mappingName" select="'LaserMedium'"/>
                <xsl:with-param name="value">
                  <xsl:value-of select="."/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="."/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <!-- Check the value of the Type attribute -->
  <xsl:template match="OME:Arc">
    <xsl:element name="Arc" namespace="{$newOMENS}">
      <xsl:for-each select="@*">
        <xsl:attribute name="{local-name(.)}">
          <xsl:choose>
            <xsl:when test="local-name(.) ='Type'">
              <xsl:call-template name="transformEnumerationValue">
                <xsl:with-param name="mappingName" select="'LightSourceType'"/>
                <xsl:with-param name="value">
                  <xsl:value-of select="."/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="."/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <!-- Check the value of the Type attribute -->
  <xsl:template match="OME:Filament">
    <xsl:element name="Filament" namespace="{$newOMENS}">
      <xsl:for-each select="@*">
        <xsl:attribute name="{local-name(.)}">
          <xsl:choose>
            <xsl:when test="local-name(.) ='Type'">
              <xsl:call-template name="transformEnumerationValue">
                <xsl:with-param name="mappingName" select="'LightSourceType'"/>
                <xsl:with-param name="value">
                  <xsl:value-of select="."/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="."/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <!-- Check the value of the Type attribute -->
  <xsl:template match="OME:Microscope">
    <xsl:element name="Microscope" namespace="{$newOMENS}">
      <xsl:for-each select="@*">
        <xsl:attribute name="{local-name(.)}">
          <xsl:choose>
            <xsl:when test="local-name(.) ='Type'">
              <xsl:call-template name="transformEnumerationValue">
                <xsl:with-param name="mappingName" select="'MicroscopeType'"/>
                <xsl:with-param name="value">
                  <xsl:value-of select="."/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="."/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <!-- Rename attributes -->
  <xsl:template match="OME:OTF">
    <xsl:element name="OTF" namespace="{$newOMENS}">
      <xsl:for-each select="@*">
        <xsl:choose>
          <xsl:when test="local-name(.)='PixelType'">
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

  <!--
      In Bin:BinData add BigEndian attribute.
      This template is for BinData inside BinaryFile inside OTF
  -->
  <xsl:template match="OME:OTF/Bin:BinaryFile/Bin:BinData">
    <xsl:element name="Bin:BinData" namespace="{$newBINNS}">
      <xsl:apply-templates select="@*"/>
      <xsl:attribute name="BigEndian">false</xsl:attribute>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <!--
      In Bin:BinData add BigEndian attribute.
      This template is for BinData inside BinaryFile inside AML:InstallationFile
  -->
  <xsl:template match="AML:InstallationFile/Bin:BinaryFile/Bin:BinData">
    <xsl:element name="Bin:BinData" namespace="{$newBINNS}">
      <xsl:apply-templates select="@*"/>
      <xsl:attribute name="BigEndian">false</xsl:attribute>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>


  <!-- Check the value of the type attribute -->
  <xsl:template match="OME:Detector">
    <xsl:element name="Detector" namespace="{$newOMENS}">
      <xsl:for-each select="@*">
        <xsl:attribute name="{local-name(.)}">
          <xsl:choose>
            <xsl:when test="local-name(.)='Type'">
              <xsl:call-template name="transformEnumerationValue">
                <xsl:with-param name="mappingName" select="'DetectorType'"/>
                <xsl:with-param name="value">
                  <xsl:value-of select="."/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="."/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>

  </xsl:template>

  <!-- Convert Correction and Immersion elements into Attributes -->
  <xsl:template match="OME:Objective">
    <xsl:element name="Objective" namespace="{$newOMENS}">
      <xsl:for-each select="*">
        <xsl:attribute name="{local-name(.)}">
          <xsl:choose>
            <xsl:when test="local-name(.)='Correction' or local-name(.)='Immersion'">
              <xsl:call-template name="transformEnumerationValue">
                <xsl:with-param name="mappingName" select="'ObjectiveCorrectionOrImmersion'"/>
                <xsl:with-param name="value">
                  <xsl:value-of select="."/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="."/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="@*"/>
    </xsl:element>
  </xsl:template>

  <!--
      Convert the attributes EmFilterRef, ExFilterRef and DichroicRef into
      elements EmissionFilterRef, ExcitationFilterRef and DichroicRef.
      Copy all the other attributes.
  -->
  <xsl:template match="OME:FilterSet">
    <xsl:element name="FilterSet" namespace="{$newOMENS}">
      <xsl:for-each
          select="@* [not(name() = 'EmFilterRef' or name() = 'ExFilterRef' or name() = 'DichroicRef')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:for-each
          select="@* [name() = 'EmFilterRef' or name() = 'ExFilterRef' or name() = 'DichroicRef']">
        <xsl:choose>
          <xsl:when test="local-name(.) = 'EmFilterRef'">
            <xsl:element name="EmissionFilterRef" namespace="{$newOMENS}">
              <xsl:attribute name="ID">
                <xsl:value-of select="."/>
              </xsl:attribute>
            </xsl:element>
          </xsl:when>
          <xsl:when test="local-name(.) = 'ExFilterRef'">
            <xsl:element name="ExcitationFilterRef" namespace="{$newOMENS}">
              <xsl:attribute name="ID">
                <xsl:value-of select="."/>
              </xsl:attribute>
            </xsl:element>
          </xsl:when>
          <xsl:otherwise>
            <xsl:element name="{local-name(.)}" namespace="{$newOMENS}">
              <xsl:attribute name="ID">
                <xsl:value-of select="."/>
              </xsl:attribute>
            </xsl:element>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- Image and Pixels -->

  <!--
      Convert the attibutes of  all the elements except element HashSHA1 into attributes of Plane.
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
      <xsl:call-template name="possibleSALink">
        <xsl:with-param name="theID">
          <xsl:value-of select="@ID"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:element>
  </xsl:template>

  <!--
      Convert the Pixels element
      Rename PixelType attribute to Type
      Remove BigEndian attribute from Pixels and move it to Bin:BinData
      Add channel elements.
      Remove WaveStart and WaveIncrement
      Due to the sequence constraints, we have to transform it that way.
  -->
  <xsl:template name="convertPixels">
    <xsl:param name="pixels"/>
    <xsl:param name="logicalChannels"/>

    <!-- transform attribute -->
    <xsl:variable name="bg" select="$pixels/@BigEndian"/>
    <xsl:for-each
        select="@* [not((local-name(.) ='BigEndian') or (local-name(.) ='WaveStart') or (local-name(.) ='WaveIncrement'))]">
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

    <!-- Convert the logical Channels -->
    <xsl:variable name="pixelsID" select="$pixels/@ID"/>
    <xsl:for-each select="exsl:node-set($logicalChannels)/*">
      <xsl:variable name="lc">
        <xsl:copy-of select="current()"/>
      </xsl:variable>
      <xsl:for-each select="*  [local-name(.) = 'ChannelComponent']">
        <xsl:variable name="positionCount"><xsl:number value="position()"/></xsl:variable>
        <xsl:if test="$pixelsID = @Pixels">
          <xsl:element name="Channel" namespace="{$newOMENS}">
            <!-- convert value of @ColorDomain-->
            <xsl:attribute name="Color">
              <xsl:call-template name="convertColorDomain">
                <xsl:with-param name="cc" select="@ColorDomain"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:for-each select="exsl:node-set($lc)/*">
              <!-- convert attribute of logicalChannel -->
              <xsl:for-each select="@* [not((name(.) = 'PhotometricInterpretation') or (name(.) = 'SamplesPerPixel'))]">
                <xsl:choose>
                  <xsl:when test="name() = 'Mode'">
                    <xsl:attribute name="AcquisitionMode">
                      <xsl:value-of select="."/>
                    </xsl:attribute>
                  </xsl:when>
                  <xsl:when test="name() = 'ExWave'">
                    <xsl:attribute name="ExcitationWavelength">
                      <xsl:value-of select="."/>
                    </xsl:attribute>
                  </xsl:when>
                  <xsl:when test="name() = 'EmWave'">
                    <xsl:attribute name="EmissionWavelength">
                      <xsl:value-of select="."/>
                    </xsl:attribute>
                  </xsl:when>
                  <xsl:when test="name() = 'NdFilter'">
                    <xsl:attribute name="NDFilter">
                      <xsl:value-of select="."/>
                    </xsl:attribute>
                  </xsl:when>
                  <xsl:when test="name() = 'ID'">
                    <xsl:variable name="idLc">
                      <xsl:value-of select="."/>
                    </xsl:variable>
                    <xsl:attribute name="{local-name(.)}">
                      <xsl:call-template name="replace-string-id">
                        <xsl:with-param name="text" select="$idLc"/>
                        <xsl:with-param name="replace"
                                        select="'LogicalChannel'"/>
                        <xsl:with-param name="replacement"
                                        select="'Channel'"/>
                        </xsl:call-template>:<xsl:value-of select="$positionCount"/>
                    </xsl:attribute>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:attribute name="{local-name(.)}">
                      <xsl:value-of select="."/>
                    </xsl:attribute>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:for-each>
              <!-- end of Channel's attributes -->
              <!-- convert the nodes -->
              <xsl:for-each select="* [not(local-name(.) = 'ChannelComponent')]">
                <xsl:choose>
                  <xsl:when
                      test="local-name(.)='DetectorRef' or local-name(.)='LightSourceRef'">
                    <xsl:apply-templates select="current()"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:element name="{local-name(.)}" namespace="{$newOMENS}">
                      <xsl:apply-templates select="@*|node()"/>
                    </xsl:element>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:for-each>
              <!-- end of Channel's nodes -->
            </xsl:for-each>
            <!-- End of lc -->
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </xsl:for-each>

    <!-- Transform the elements -->
    <xsl:for-each select="*">
      <xsl:call-template name="convertPixelsData">
        <xsl:with-param name="bg" select="$bg"/>
        <xsl:with-param name="node" select="current()"/>
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>

  <!-- Rename attribute NumPlanes into PlateCount -->
  <xsl:template match="OME:TiffData">
    <xsl:element name="TiffData" namespace="{$newOMENS}">
      <xsl:for-each select="@*">
        <xsl:choose>
          <xsl:when test="local-name(.) = 'NumPlanes'">
            <xsl:attribute name="PlaneCount">
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

  <!--
      Copy the MicrobeamManipulation node from Image corresponding to the MicrobeamManipulationRef.
  -->
  <xsl:template match="OME:Experiment">
    <xsl:variable name="images">
      <xsl:copy-of select="following-sibling::OME:Image"/>
    </xsl:variable>
    <xsl:element name="Experiment" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*"/>
      <xsl:for-each select="*">
        <xsl:choose>
          <xsl:when test="local-name(.) = 'MicrobeamManipulationRef'">
            <xsl:variable name="id" select="@ID"/>
            <xsl:for-each select="exsl:node-set($images)/*">
              <xsl:for-each select="* [name()='MicrobeamManipulation']">
                <xsl:variable name="rois">
                  <xsl:copy-of select="preceding-sibling::OME:ROI"/>
                </xsl:variable>
                <xsl:if test="@ID=$id">
                  <xsl:element name="{local-name(.)}" namespace="{$newOMENS}">
                    <xsl:apply-templates select="@*"/>
                    <xsl:for-each select="*">
                      <xsl:choose>
                        <xsl:when test="local-name(.) = 'ROIRef'">
                          <xsl:apply-templates select="current()"/>
                        </xsl:when>
                        <xsl:when test="local-name(.) = 'LightSourceRef'">
                          <xsl:apply-templates select="current()"/>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:element name="{local-name(.)}"
                                       namespace="{$newOMENS}">
                            <xsl:apply-templates select="@*|node()"/>
                          </xsl:element>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:for-each>
                  </xsl:element>
                </xsl:if>
              </xsl:for-each>
            </xsl:for-each>
          </xsl:when>
          <xsl:when test="local-name(.) = 'Description'">
            <xsl:apply-templates select="current()"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:element name="{local-name(.)}" namespace="{$newOMENS}">
              <xsl:apply-templates select="@*|node()"/>
            </xsl:element>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- Transform the CustomAttributes into XMLAnnotation -->
  <xsl:template match="CA:CustomAttributes">
    <xsl:if test="count(@*|node()) &gt; 0">
      <xsl:element name="StructuredAnnotations" namespace="{$newSANS}">
        <xsl:element name="XMLAnnotation" namespace="{$newSANS}">
          <xsl:attribute name="ID">Annotation:1</xsl:attribute>
          <xsl:element name="Value" namespace="{$newSANS}">
            <xsl:apply-templates select="@*|node()"/>
          </xsl:element>
        </xsl:element>
      </xsl:element>
    </xsl:if>
  </xsl:template>

  <!--
      Remove AcquiredPixels and DefaultPixels attributes.
      Remove elements Thumbnail, DisplayOptions, Region and CustomAttributes
      MicrobeamManipulation node is moved to Experiment see Experiment template.
      LogicalChannel and ChannelComponent are merged: new name is Channel
      If a logical channel has n ChannelComponent nodes, n Channel nodes are created.
      The Channel nodes are then linked to Pixels and no longer to Image.
  -->
  <xsl:template match="OME:Image">
    <xsl:element name="Image" namespace="{$newOMENS}">
      <xsl:variable name="requiredPixels">
        <xsl:choose>
          <xsl:when test="@AcquiredPixels">
            <xsl:value-of select="current()/@AcquiredPixels"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="current()/@DefaultPixels"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:apply-templates
          select="@* [not(name() = 'DefaultPixels' or name() = 'AcquiredPixels')]"/>
      <xsl:for-each
          select="* [not(local-name(.) = 'Thumbnail' or local-name(.) = 'DisplayOptions' or local-name(.) = 'Region' or local-name(.) = 'CustomAttributes' or local-name(.) = 'LogicalChannel')]">
        <xsl:choose>
          <xsl:when test="local-name(.) ='Description'">
            <xsl:apply-templates select="current()"/>
          </xsl:when>
          <xsl:when test="local-name(.) = 'CreationDate'">
            <xsl:element name="AcquiredDate" namespace="{$newOMENS}">
              <xsl:value-of select="."/>
            </xsl:element>
          </xsl:when>

          <xsl:when test="local-name(.) = 'Pixels'">
            <!-- Also has to handle the invalid case where both the pixels values are missing -->
            <xsl:if test="@ID=$requiredPixels or $requiredPixels=''">
              <!-- add controls to make sure we only copy one. -->
              <xsl:element name="{local-name(.)}" namespace="{$newOMENS}">
                <xsl:call-template name="convertPixels">
                  <xsl:with-param name="pixels" select="."/>
                  <xsl:with-param name="logicalChannels">
                    <xsl:copy-of select="preceding-sibling::OME:LogicalChannel"
                                 />
                  </xsl:with-param>

                </xsl:call-template>
                <xsl:call-template name="possibleSALink">
                  <xsl:with-param name="theID">
                    <xsl:value-of select="@ID"/>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:element>
            </xsl:if>
          </xsl:when>
          <xsl:when test="local-name(.) = 'ObjectiveRef'">
            <xsl:apply-templates select="current()"/>
          </xsl:when>
          <!-- replace MicrobeamManipulation by MicrobeamManipulationRef -->
          <xsl:when test="local-name(.) = 'MicrobeamManipulation'">
            <xsl:variable name="id" select="@ID"/>
            <xsl:element name="MicrobeamManipulationRef" namespace="{$newOMENS}">
              <xsl:attribute name="ID">
                <xsl:value-of select="$id"/>
              </xsl:attribute>
            </xsl:element>
          </xsl:when>
          <xsl:when test="local-name(.) = 'ROI'">
            <xsl:apply-templates select="current()"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:element name="{local-name(.)}" namespace="{$newOMENS}">
              <xsl:apply-templates select="@*|node()"/>
            </xsl:element>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
      <xsl:call-template name="possibleSALink">
        <xsl:with-param name="theID">
          <xsl:value-of select="@ID"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:element>
  </xsl:template>

  <!-- Transform the LogicalChannel Ref into ChannelRef -->
  <xsl:template match="OME:LogicalChannelRef">
    <xsl:element name="ChannelRef" namespace="{$newOMENS}">
      <xsl:for-each select="@*">
        <xsl:choose>
          <xsl:when test="name()='ID'">
            <xsl:variable name="id">
              <xsl:value-of select="current()"/>
            </xsl:variable>
            <xsl:attribute name="{local-name(.)}">
              <xsl:call-template name="replace-string-id">
                <xsl:with-param name="text" select="$id"/>
                <xsl:with-param name="replace" select="'LogicalChannel'"/>
                <xsl:with-param name="replacement" select="'Channel'"/>
              </xsl:call-template>
            </xsl:attribute>
          </xsl:when>
          <xsl:otherwise> </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- ROI -->
  <!-- Rename all the attributes -->
  <xsl:template match="OME:Ellipse">
    <xsl:element name="ROI:Ellipse" namespace="{$newROINS}">
      <xsl:for-each select="@*">
        <xsl:variable name="converted">
          <xsl:call-template name="formatNumber">
            <xsl:with-param name="value">
              <xsl:value-of select="."/>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="name()='cx'">
            <xsl:attribute name="X">
              <xsl:value-of select="$converted"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="name()='cy'">
            <xsl:attribute name="Y">
              <xsl:value-of select="$converted"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="name()='rx'">
            <xsl:attribute name="RadiusX">
              <xsl:value-of select="$converted"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="name()='ry'">
            <xsl:attribute name="RadiusY">
              <xsl:value-of select="$converted"/>
            </xsl:attribute>
          </xsl:when>
        </xsl:choose>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- Rename all the attributes -->
  <xsl:template match="OME:Rect">
    <xsl:element name="ROI:Rectangle" namespace="{$newROINS}">
      <xsl:for-each select="@* [not(name() ='transform')]">
        <xsl:variable name="converted">
          <xsl:call-template name="formatNumber">
            <xsl:with-param name="value">
              <xsl:value-of select="."/>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="name()='x'">
            <xsl:attribute name="X">
              <xsl:value-of select="$converted"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="name()='y'">
            <xsl:attribute name="Y">
              <xsl:value-of select="$converted"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="name()='width'">
            <xsl:attribute name="Width">
              <xsl:value-of select="$converted"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="name()='height'">
            <xsl:attribute name="Height">
              <xsl:value-of select="$converted"/>
            </xsl:attribute>
          </xsl:when>
        </xsl:choose>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- Rename attributes cx and cy -->
  <xsl:template match="OME:Point">
    <xsl:element name="ROI:Point" namespace="{$newROINS}">
      <xsl:for-each select="@* [not(name() ='transform' or name() ='r')]">
        <xsl:variable name="converted">
          <xsl:call-template name="formatNumber">
            <xsl:with-param name="value">
              <xsl:value-of select="."/>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="name()='cx'">
            <xsl:attribute name="X">
              <xsl:value-of select="$converted"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="name()='cy'">
            <xsl:attribute name="Y">
              <xsl:value-of select="$converted"/>
            </xsl:attribute>
          </xsl:when>
        </xsl:choose>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- Rename attributes cx and cy -->
  <xsl:template match="OME:Line">
    <xsl:element name="ROI:Line" namespace="{$newROINS}">
      <xsl:for-each select="@* [not(name() ='transform')]">
        <xsl:variable name="converted">
          <xsl:call-template name="formatNumber">
            <xsl:with-param name="value">
              <xsl:value-of select="."/>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="name()='x1'">
            <xsl:attribute name="X1">
              <xsl:value-of select="$converted"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="name()='x2'">
            <xsl:attribute name="X2">
              <xsl:value-of select="$converted"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="name()='y1'">
            <xsl:attribute name="Y1">
              <xsl:value-of select="$converted"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="name()='y2'">
            <xsl:attribute name="Y2">
              <xsl:value-of select="$converted"/>
            </xsl:attribute>
          </xsl:when>
        </xsl:choose>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- Rename attributes points -->
  <xsl:template match="OME:Polyline">
    <xsl:variable name="default" select="'false'"/>
    <xsl:element name="ROI:Polyline" namespace="{$newROINS}">
      <xsl:for-each select="@* [not(name() ='transform')]">
        <xsl:choose>
          <xsl:when test="name()='points'">
            <xsl:attribute name="Points">
              <xsl:call-template name="setPoints">
                <xsl:with-param name="value">
                  <xsl:value-of select="."/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:attribute>
          </xsl:when>
        </xsl:choose>
      </xsl:for-each>
      <xsl:attribute name="Closed">
        <xsl:value-of select="$default"/>
      </xsl:attribute>
    </xsl:element>
  </xsl:template>

  <!-- Rename attributes points -->
  <xsl:template match="OME:Polygon">
    <xsl:variable name="default" select="'true'"/>
    <xsl:element name="ROI:Polyline" namespace="{$newROINS}">
      <xsl:for-each select="@* [not(name() ='transform')]">
        <xsl:choose>
          <xsl:when test="name()='points'">
            <xsl:attribute name="Points">
              <xsl:call-template name="setPoints">
                <xsl:with-param name="value">
                  <xsl:value-of select="."/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:attribute>
          </xsl:when>
        </xsl:choose>
      </xsl:for-each>
      <xsl:attribute name="Closed">
        <xsl:value-of select="$default"/>
      </xsl:attribute>

    </xsl:element>
  </xsl:template>

  <!-- Sets the value of the points attribute for Polygon and Polyline -->
  <xsl:template name="setPoints">
    <xsl:param name="value"/>
    <xsl:choose>
      <xsl:when test="string-length($value) > 0">
        <xsl:value-of select="$value"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$pointsDefault"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Transform a Circle into an Ellipse -->
  <xsl:template match="OME:Circle">
    <xsl:element name="ROI:Ellipse" namespace="{$newROINS}">
      <xsl:for-each select="@* [not(name() ='transform')]">
        <xsl:variable name="converted">
          <xsl:call-template name="formatNumber">
            <xsl:with-param name="value">
              <xsl:value-of select="."/>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="name()='cx'">
            <xsl:attribute name="X">
              <xsl:value-of select="$converted"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="name()='cy'">
            <xsl:attribute name="Y">
              <xsl:value-of select="$converted"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="name()='r'">
            <xsl:attribute name="RadiusX">
              <xsl:value-of select="$converted"/>
            </xsl:attribute>
            <xsl:attribute name="RadiusY">
              <xsl:value-of select="$converted"/>
            </xsl:attribute>
          </xsl:when>
        </xsl:choose>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- Move the ROI to its new name space -->
  <xsl:template match="OME:ROI">
    <xsl:element name="ROI:ROIRef" namespace="{$newROINS}">
      <xsl:apply-templates select="@*"/>
    </xsl:element>
  </xsl:template>

  <!-- Move the ROIRef to its new name space -->
  <xsl:template match="OME:ROIRef">
    <xsl:element name="ROI:ROIRef" namespace="{$newROINS}">
      <xsl:apply-templates select="@*"/>
    </xsl:element>
  </xsl:template>



  <!-- Move the ROI to its new name space -->
  <xsl:template match="OME:Union">
    <xsl:element name="ROI:Union" namespace="{$newROINS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <!-- Transform attributes and move the transform attribute from a "real" shape to Shape -->
  <xsl:template match="OME:Shape">
    <xsl:element name="ROI:Shape" namespace="{$newROINS}">
      <xsl:variable name="shape" select="'Shape:'"/>
      <xsl:variable name="id" select="@ID"/>
      <xsl:variable name="convertedID">
        <xsl:choose>
          <xsl:when test="contains($id, 'Shape')">
            <xsl:value-of select="$id"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$shape"/>
            <xsl:value-of select="$id"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:for-each select="@*">
        <xsl:choose>
          <xsl:when test="name()='theZ'">
            <xsl:attribute name="TheZ">
              <xsl:value-of select="."/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="name()='theT'">
            <xsl:attribute name="TheT">
              <xsl:value-of select="."/>
            </xsl:attribute>
          </xsl:when>
          <!-- control ID due b/c bug in previous version -->
          <xsl:when test="name()='ID'">
            <xsl:attribute name="{local-name()}">
              <xsl:value-of select="$convertedID"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
            <xsl:attribute name="{local-name()}">
              <xsl:value-of select="."/>
            </xsl:attribute>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>

      <!-- retrieve the value of the transform attribute -->
      <xsl:variable name="trans">
        <xsl:for-each select="* [not(local-name(.) = 'Channels')]">
          <xsl:value-of select="@transform"/>
        </xsl:for-each>
      </xsl:variable>

      <xsl:attribute name="Transform">
        <xsl:value-of select="$trans"/>
      </xsl:attribute>
      <xsl:for-each select="*">
        <xsl:choose>
          <xsl:when test="name()='Channels'">
            <xsl:apply-templates select="@*|node()"/>
          </xsl:when>
          <xsl:when test="name()='Mask'">
            <xsl:call-template name="maskTansformation">
              <xsl:with-param name="mask" select="current()"/>
              <xsl:with-param name="id" select="$convertedID"/>
            </xsl:call-template>
            <!-- <xsl:apply-templates select="@*|node()"/>-->
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates select="current()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- Rename attributes and link to Pixels -->
  <xsl:template name="maskTansformation">
    <xsl:param name="mask"/>
    <xsl:param name="id"/>
    <xsl:element name="ROI:Mask" namespace="{$newROINS}">
      <xsl:for-each
          select="$mask/@* [not(name() ='transform' or name() ='width' or name() ='height')]">
        <xsl:choose>
          <xsl:when test="name()='x'">
            <xsl:attribute name="X">
              <xsl:value-of select="."/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="name()='y'">
            <xsl:attribute name="Y">
              <xsl:value-of select="."/>
            </xsl:attribute>
          </xsl:when>
        </xsl:choose>
      </xsl:for-each>
      <!-- transform MaskPixels -->
      <xsl:for-each select="($mask)/*">
        <xsl:choose>
          <xsl:when test="local-name(.)='MaskPixels'">
            <xsl:variable name="bg" select="current()/@BigEndian"/>
            <xsl:for-each select="current()/*">
              <xsl:call-template name="convertPixelsData">
                <xsl:with-param name="bg" select="$bg"/>
                <xsl:with-param name="node" select="current()"/>
              </xsl:call-template>
            </xsl:for-each>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates select="node()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <!-- template to transform the possibile data source related to Pixels -->
  <xsl:template name="convertPixelsData">
    <xsl:param name="bg"/>
    <xsl:param name="node"/>
    <xsl:choose>
      <xsl:when test="local-name($node) = 'BinData'">
        <xsl:element name="{local-name($node)}" namespace="{$newBINNS}">
          <xsl:attribute name="BigEndian">
            <xsl:value-of select="$bg"/>
          </xsl:attribute>
          <xsl:apply-templates select="@*|node()"/>
        </xsl:element>
      </xsl:when>
      <xsl:when test="local-name($node)='Plane' or local-name($node)='TiffData'">
        <xsl:apply-templates select="$node"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:element name="{local-name($node)}" namespace="{$newOMENS}">
          <xsl:apply-templates select="@*|node()"/>
        </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Screen Plate Well -->

  <!-- Add any SA links to Screen -->
  <xsl:template match="SA:Screen">
    <xsl:element name="{name()}" namespace="{$newSANS}">
      <xsl:apply-templates select="@*|node()"/>
      <xsl:call-template name="possibleSALink">
        <xsl:with-param name="theID">
          <xsl:value-of select="@ID"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:element>
  </xsl:template>

  <!-- Screen Plate Well -->
  <xsl:template match="SA:ScreenAcquisition">
    <xsl:element name="{name()}" namespace="{$newSANS}">
      <xsl:apply-templates select="@*|node()"/>
      <xsl:call-template name="possibleSALink">
        <xsl:with-param name="theID">
          <xsl:value-of select="@ID"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:element>
  </xsl:template>

  <!--
      Remove or rename attributes in WellSample.
      Remove Index, Rename PosX to PositionX & PosY to PositionY
  -->
  <xsl:template match="SPW:WellSample">
    <xsl:element name="WellSample" namespace="{$newSPWNS}">
      <xsl:for-each select="@* [not(name() = 'Index')]">
        <xsl:choose>
          <xsl:when test="name() = 'PosX'">
            <xsl:attribute name="PositionX">
              <xsl:value-of select="."/>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="name() = 'PosY'">
            <xsl:attribute name="PositionY">
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
      <xsl:call-template name="possibleSALink">
        <xsl:with-param name="theID">
          <xsl:value-of select="@ID"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:element>
  </xsl:template>

  <!--  Transform the Row and Column attribute from Integer to nonNegativeInteger -->
  <xsl:template match="SPW:Well">
    <xsl:element name="Well" namespace="{$newSPWNS}">
      <xsl:for-each select="@*">
        <xsl:choose>
          <xsl:when test="name() = 'Row' or name() = 'Column'">
            <xsl:attribute name="{local-name(.)}">
              <xsl:call-template name="isValueValid">
                <xsl:with-param name="value">
                  <xsl:value-of select="."/>
                </xsl:with-param>
                <xsl:with-param name="control" select="0"/>
                <xsl:with-param name="type" select="'less'"/>
              </xsl:call-template>
            </xsl:attribute>
          </xsl:when>
          <xsl:when test="name() = 'Type'">
            <xsl:attribute name="Status">
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
      <xsl:call-template name="possibleSALink">
        <xsl:with-param name="theID">
          <xsl:value-of select="@ID"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:element>
  </xsl:template>

  <!--
      Convert the attribute Description in Plate into a child element.
      Copy all the other attributes.
      Copy all child elements.
  -->
  <xsl:template match="SPW:Plate">
    <xsl:element name="SPW:Plate" namespace="{$newSPWNS}">
      <xsl:for-each select="@* [not(name() = 'Description')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:variable name="des" select="@Description"/>
      <xsl:element name="Description" namespace="{$newSPWNS}">
        <xsl:value-of select="$des"/>
      </xsl:element>
      <xsl:apply-templates select="node()"/>
      <xsl:call-template name="possibleSALink">
        <xsl:with-param name="theID">
          <xsl:value-of select="@ID"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:element>
  </xsl:template>

  <!--
      Convert the attribute Description in Plate into a child element.
      Copy all the other attributes.
      Copy all child elements.
  -->
  <xsl:template match="SPW:Reagent">
    <xsl:element name="SPW:Reagent" namespace="{$newSPWNS}">
      <xsl:for-each select="@* [not(name() = 'Description')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:variable name="des" select="@Description"/>
      <xsl:element name="Description" namespace="{$newSPWNS}">
        <xsl:value-of select="$des"/>
      </xsl:element>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
    <xsl:call-template name="possibleSALink">
      <xsl:with-param name="theID">
        <xsl:value-of select="@ID"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- General -->
  <!-- Fix the various Description Elements and Attributes -->
  <!--
      Move all Description Elements into same namespace as their
      parent and strip any lang attributes.
  -->
  <xsl:template match="OME:Description">
    <xsl:choose>
      <xsl:when test="local-name(..) = 'Screen'">
        <xsl:element name="Description" namespace="{$newSPWNS}">
          <xsl:apply-templates select="node()"/>
        </xsl:element>
      </xsl:when>
      <xsl:when test="local-name(..) = 'AnalysisModule'">
        <xsl:element name="Description" namespace="{$newAMLNS}">
          <xsl:apply-templates select="node()"/>
        </xsl:element>
      </xsl:when>
      <xsl:when test="local-name(..) = 'Entry'">
        <xsl:element name="Description" namespace="{$newAMLNS}">
          <xsl:apply-templates select="node()"/>
        </xsl:element>
      </xsl:when>
      <xsl:when test="local-name(..) = 'FormalInput'">
        <xsl:element name="Description" namespace="{$newAMLNS}">
          <xsl:apply-templates select="node()"/>
        </xsl:element>
      </xsl:when>
      <xsl:when test="local-name(..) = 'FormalOutput'">
        <xsl:element name="Description" namespace="{$newAMLNS}">
          <xsl:apply-templates select="node()"/>
        </xsl:element>
      </xsl:when>
      <xsl:when test="local-name(..) = 'LookupTable'">
        <xsl:element name="Description" namespace="{$newAMLNS}">
          <xsl:apply-templates select="node()"/>
        </xsl:element>
      </xsl:when>
      <xsl:when test="local-name(..) = 'Category'">
        <xsl:element name="Description" namespace="{$newAMLNS}">
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


  <!-- SA processing -->

  <!-- Rename XmlAnnotation to XMLAnnotation -->
  <xsl:template match="SA:XmlAnnotation">
    <xsl:element name="XMLAnnotation" namespace="{$newSANS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <!-- Locate the SA link  -->
  <xsl:template name="possibleSALink">
    <xsl:param name="theID"/>
    <!-- <xsl:comment>Possible Link: <xsl:value-of select="$theID"/></xsl:comment>-->

    <xsl:for-each select="/OME:OME/SA:StructuredAnnotations/*/SA:Link">
      <!-- <xsl:comment>Found Node</xsl:comment> -->
      <xsl:if test=".=$theID">
        <xsl:element name="SA:AnnotationRef" namespace="{$newSANS}">
          <xsl:attribute name="ID"><xsl:value-of select="../@ID"/></xsl:attribute>
        </xsl:element>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <!-- Default processing -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <!-- Follow useful list of functions -->
  <!--
      convert the value of the color domain attribute of ChannelComponent.
      A limited number of strings is supported.
  -->
  <xsl:template name="convertColorDomain">
    <xsl:param name="cc"/>
    <xsl:choose>
      <xsl:when test="contains($cc,'r') or contains($cc,'R')">4278190335</xsl:when>
      <xsl:when test="contains($cc,'g') or contains($cc,'G')">16711935</xsl:when>
      <xsl:when test="contains($cc,'b') or contains($cc,'B')">65535</xsl:when>
      <xsl:otherwise>4294967295</xsl:otherwise>
    </xsl:choose>
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

  <!-- Control if a value is in the specified interval -->
  <xsl:template name="valueInInterval">
    <xsl:param name="value"/>
    <xsl:param name="min"/>
    <xsl:param name="max"/>
    <xsl:choose>
      <xsl:when test="$value &lt; $min">
        <xsl:value-of select="$min"/>
      </xsl:when>
      <xsl:when test="$value &gt; $max">
        <xsl:value-of select="$max"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$value"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--Convert value to PercentFration -->
  <xsl:template name="convertPercentFraction">
    <xsl:param name="value"/>
    <xsl:variable name="min" select="0"/>
    <xsl:variable name="max" select="1"/>
    <xsl:choose>
      <xsl:when test="$value &lt; $min">
        <xsl:value-of select="$min"/>
      </xsl:when>
      <xsl:when test="$value &gt; $max">
        <xsl:call-template name="convertPercentFraction">
          <xsl:with-param name="value">
            <xsl:value-of select="$value div 100"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$value"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--
      Controls if a value is greater than or less than depending on the type.
      The types are greater or less.
  -->
  <xsl:template name="isValueValid">
    <xsl:param name="value"/>
    <xsl:param name="control"/>
    <xsl:param name="type"/>
    <xsl:choose>
      <xsl:when test="$type = 'less'">
        <xsl:choose>
          <xsl:when test="$value &lt; $control">
            <xsl:value-of select="$control"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$value"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$value &gt; $control">
            <xsl:value-of select="$control"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$value"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Check if the passed value is a number, if not extract number if any -->
  <xsl:template name="formatNumber">
    <xsl:param name="value"/>
    <xsl:choose>
      <!-- number already -->
      <xsl:when test="number($value)=number($value)">
        <xsl:value-of select="$value"/>
      </xsl:when>
      <xsl:otherwise>
        <!-- try to find a number -->
        <xsl:value-of select="$numberDefault"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
