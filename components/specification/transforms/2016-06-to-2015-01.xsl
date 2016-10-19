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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2016-06"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:xml="http://www.w3.org/XML/1998/namespace"
                exclude-result-prefixes="OME"
                xmlns:exsl="http://exslt.org/common"
                extension-element-prefixes="exsl" version="1.0">

  <xsl:variable name="newOMENS">http://www.openmicroscopy.org/Schemas/OME/2015-01</xsl:variable>
  <xsl:variable name="newSPWNS">http://www.openmicroscopy.org/Schemas/SPW/2015-01</xsl:variable>
  <xsl:variable name="newBINNS">http://www.openmicroscopy.org/Schemas/BinaryFile/2015-01</xsl:variable>
  <xsl:variable name="newROINS">http://www.openmicroscopy.org/Schemas/ROI/2015-01</xsl:variable>
  <xsl:variable name="newSANS">http://www.openmicroscopy.org/Schemas/SA/2015-01</xsl:variable>

  <xsl:output method="xml" indent="yes"/>
  <xsl:preserve-space elements="*"/>

  <!-- Rewrite abstract elements for Shape and LightSource -->
  <xsl:template match="OME:Line | OME:Rectangle | OME:Mask | OME:Ellipse | OME:Point | OME:Polyline | OME:Polygon | OME:Label">
    <xsl:element name="ROI:Shape"  namespace="{$newROINS}">
      <xsl:apply-templates select="@FillRule | @FillColor | @StrokeColor | @StrokeWidth | @StrokeWidthUnit | @StrokeDashArray | @LineCap |@Text |  @FontFamily | @FontSize | @FontSizeUnit | @FontStyle | @Locked | @ID | @TheZ | @TheT | @TheC"/>
      <xsl:apply-templates select="node()[local-name() = 'Transform' or local-name() = 'AnnotationRef']"/>
      <xsl:element name="ROI:{name()}"  namespace="{$newROINS}">
        <xsl:apply-templates select="@*[not(name() = 'FillRule' or name() = 'FillColor' or name() = 'StrokeColor' or name() = 'StrokeWidth' or name() = 'StrokeWidthUnit' or name() = 'StrokeDashArray' or name() = 'LineCap' or name() = 'Text' or  name() = 'FontFamily' or name() = 'FontSize' or name() = 'FontSizeUnit' or name() = 'FontStyle' or name() = 'Locked' or name() = 'ID' or name() = 'TheZ' or name() = 'TheT' or name() = 'TheC')]"/>
        <xsl:apply-templates select="node()[not(local-name() = 'Transform' or local-name() = 'AnnotationRef')]"/>
      </xsl:element>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:Laser | OME:Arc | OME:Filament | OME:LightEmittingDiode | OME:GenericExcitationSource">
    <xsl:element name="OME:LightSource"  namespace="{$newOMENS}">
      <xsl:apply-templates select="@ID | @Power | @PowerUnit |@Type"/>
      <xsl:apply-templates select="node()[local-name() = 'AnnotationRef']"/>
      <xsl:element name="OME:{name()}"  namespace="{$newOMENS}">
        <xsl:apply-templates select="@*[not(name() = 'ID' or name() = 'Power' or name() = 'PowerUnit' or name() = 'Type')]"/>
        <xsl:apply-templates select="node()[not(local-name() = 'AnnotationRef')]"/>
      </xsl:element>
    </xsl:element>
  </xsl:template>

  <!-- Rewrite all namespaces -->

  <xsl:template match="OME:OME">
    <OME:OME xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2015-01"
             xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2015-01"
             xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2015-01"
             xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2015-01"
             xmlns:ROI="http://www.openmicroscopy.org/Schemas/ROI/2015-01"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2015-01
                                 http://www.openmicroscopy.org/Schemas/OME/2015-01/ome.xsd">
      <xsl:apply-templates select="@UUID|@Creator|node()"/> <!-- copy UUID and Creator attributes and nodes -->
    </OME:OME>
  </xsl:template>

  <!-- Move all BinaryFile, SA, SPW and ROI elements back into their separate namespaces -->

  <xsl:template match="OME:External | OME:BinData | OME:BinaryFile">
    <xsl:element name="Bin:{local-name()}" namespace="{$newBINNS}"
                 xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2015-01">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:StructuredAnnotations | OME:AnnotationRef | OME:Annotation | OME:FileAnnotation | OME:XMLAnnotation | OME:ListAnnotation | OME:CommentAnnotation | OME:LongAnnotation | OME:DoubleAnnotation | OME:BooleanAnnotation | OME:TimestampAnnotation | OME:TagAnnotation | OME:TermAnnotation | OME:MapAnnotation"
                xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2015-01">
    <xsl:element name="SA:{local-name()}" namespace="{$newSANS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:Plate | OME:Plate/OME:Description | OME:Reagent | OME:Reagent/OME:Description | OME:ReagentRef | OME:Screen | OME:Screen/OME:Description | OME:PlateAcquisition | OME:PlateAcquisition/OME:Description | OME:Well | OME:WellSample | OME:WellSampleRef"
                xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2015-01">
    <xsl:element name="SPW:{local-name()}" namespace="{$newSPWNS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:ROI | OME:Union | OME:ROI/OME:Description | OME:Shape | OME:Transform | OME:ROIRef"
                xmlns:ROI="http://www.openmicroscopy.org/Schemas/ROI/2015-01">
    <xsl:element name="ROI:{local-name()}" namespace="{$newROINS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:*">
    <xsl:element name="{name()}" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <!-- Remove Folder elements -->

  <xsl:template match="OME:Folder"/>
  <xsl:template match="OME:FolderRef"/>

  <!-- Remove new enumeration values -->
  <xsl:template match="OME:Channel/@AcquisitionMode[. = 'BrightField' or
                                                    . = 'SweptFieldConfocal' or
                                                    . = 'SPIM']">
    <xsl:attribute name="AcquisitionMode">
      <xsl:text>Other</xsl:text>
    </xsl:attribute>
  </xsl:template>

  <!-- Default processing -->

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
