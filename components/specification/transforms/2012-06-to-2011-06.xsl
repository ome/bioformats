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
# Written by:  Andrew Patterson: ajpatterson at lifesci.dundee.ac.uk
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2012-06"
                xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2012-06"
                xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2012-06"
                xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2012-06"
                xmlns:ROI="http://www.openmicroscopy.org/Schemas/ROI/2012-06"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:xml="http://www.w3.org/XML/1998/namespace"
                exclude-result-prefixes="OME Bin SPW SA ROI"
                xmlns:exsl="http://exslt.org/common"
                extension-element-prefixes="exsl" version="1.0">

  <xsl:variable name="newOMENS">http://www.openmicroscopy.org/Schemas/OME/2011-06</xsl:variable>
  <xsl:variable name="newSPWNS">http://www.openmicroscopy.org/Schemas/SPW/2011-06</xsl:variable>
  <xsl:variable name="newBINNS"
                >http://www.openmicroscopy.org/Schemas/BinaryFile/2011-06</xsl:variable>
  <xsl:variable name="newROINS">http://www.openmicroscopy.org/Schemas/ROI/2011-06</xsl:variable>
  <xsl:variable name="newSANS">http://www.openmicroscopy.org/Schemas/SA/2011-06</xsl:variable>

  <xsl:output method="xml" indent="yes"/>
  <xsl:preserve-space elements="*"/>

  <!-- default value for non-numerical value when transforming the attribute of concrete shape -->
  <xsl:variable name="numberDefault" select="1"/>


  <!-- Actual schema changes -->

  <xsl:template match="OME:Dataset">
    <xsl:element name="OME:Dataset" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="* [not (local-name(.) = 'ImageRef' or local-name(.) = 'AnnotationRef')]"/>
      <xsl:comment>Insert ProjectRef elements</xsl:comment>
      <!-- Insert ProjectRef elements -->
      <xsl:variable name="datasetID" select="@ID"/>
      <xsl:for-each select="exsl:node-set(//OME:Project/OME:DatasetRef[@ID=$datasetID])">
        <xsl:element name="OME:ProjectRef" namespace="{$newOMENS}">
          <xsl:attribute name="ID"><xsl:for-each select=" parent::node()">
            <xsl:value-of select="@ID"/>
          </xsl:for-each></xsl:attribute>
        </xsl:element>
      </xsl:for-each>
      <xsl:apply-templates select="* [local-name(.) = 'AnnotationRef']"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:Project">
    <xsl:element name="OME:Project" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="* [not (local-name(.) = 'DatasetRef')]"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:Image">
    <xsl:element name="OME:Image" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="* [local-name(.) = 'AcquiredDate' or local-name(.) = 'ExperimenterRef' or local-name(.) = 'Description' or local-name(.) = 'ExperimentRef' or local-name(.) = 'GroupRef']"/>
      <xsl:comment>Insert DatasetRef elements</xsl:comment>
      <!-- Insert DatasetRef elements -->
      <xsl:variable name="imageID" select="@ID"/>
      <xsl:for-each select="exsl:node-set(//OME:Dataset/OME:ImageRef[@ID=$imageID])">
        <xsl:element name="OME:DatasetRef" namespace="{$newOMENS}">
          <xsl:attribute name="ID"><xsl:for-each select=" parent::node()">
            <xsl:value-of select="@ID"/>
          </xsl:for-each></xsl:attribute>
        </xsl:element>
      </xsl:for-each>
      <xsl:apply-templates select="* [local-name(.) = 'InstrumentRef' or local-name(.) = 'ObjectiveSettings' or local-name(.) = 'ImagingEnvironment' or local-name(.) = 'StageLabel' or local-name(.) = 'Pixels' or local-name(.) = 'ROIRef' or local-name(.) = 'MicrobeamManipulationRef' or local-name(.) = 'AnnotationRef']"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:AcquisitionDate">
    <xsl:element name="OME:AcquiredDate" namespace="{$newOMENS}">
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:Experimenter">
    <xsl:element name="OME:Experimenter" namespace="{$newOMENS}">
      <!-- Calculate DisplayName -->
      <xsl:attribute name="DisplayName"><xsl:value-of select="@FirstName"/>/<xsl:value-of select="@MiddleName"/>/<xsl:value-of select="@LastName"/>(<xsl:value-of select="@UserName"/>)[<xsl:value-of select="@Email"/>]</xsl:attribute>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="node()"/>
      <xsl:variable name="experimenterID" select="@ID"/>
      <xsl:for-each select="exsl:node-set(//OME:ExperimenterGroup/OME:ExperimenterRef[@ID=$experimenterID])">
        <xsl:element name="OME:GroupRef" namespace="{$newOMENS}">
          <xsl:attribute name="ID">Group:<xsl:for-each select=" parent::node()">
          <xsl:value-of select="@ID"/>
          </xsl:for-each></xsl:attribute>
        </xsl:element>
      </xsl:for-each>
      <xsl:for-each select="exsl:node-set(//OME:ExperimenterGroup/OME:Leader[@ID=$experimenterID])">
        <xsl:element name="OME:GroupRef" namespace="{$newOMENS}">
          <xsl:attribute name="ID">Group:<xsl:for-each select=" parent::node()">
          <xsl:value-of select="@ID"/>
          </xsl:for-each></xsl:attribute>
        </xsl:element>
      </xsl:for-each>

    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:ExperimenterGroup">
    <xsl:element name="OME:Group" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*[not(local-name(.)='ID')]"/>
      <xsl:for-each select="@* [name() = 'ID']">
        <xsl:attribute name="ID">Group:<xsl:value-of select="."/></xsl:attribute>
      </xsl:for-each>

      <xsl:apply-templates select="* [local-name(.) = 'Description']"/>
      <xsl:for-each select="* [local-name(.) = 'Leader'][1]">
        <xsl:apply-templates select="."/>
      </xsl:for-each>
      <xsl:for-each select="* [local-name(.) = 'Leader'][2]">
        <xsl:apply-templates select="." mode="ToContact"/>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:Leader" mode="ToContact">
    <xsl:element name="OME:Contact" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:ExperimenterGroupRef">
    <xsl:element name="OME:GroupRef" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*[not(local-name(.)='ID')]"/>
      <xsl:for-each select="@* [name() = 'ID']">
        <xsl:attribute name="ID">Group:<xsl:value-of select="."/></xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="OME:ImageRef">
    <xsl:element name="SPW:ImageRef" namespace="{$newSPWNS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="SPW:Well">
    <xsl:element name="SPW:Well" namespace="{$newSPWNS}">
      <xsl:for-each select="@* [not(name() = 'Type')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:for-each select="@* [name() = 'Type']">
        <xsl:attribute name="Status">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="SPW:Plate">
    <xsl:element name="SPW:Plate" namespace="{$newSPWNS}">
      <xsl:for-each select="@* [not(name() = 'FieldIndex')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="ROI:Shape">
    <xsl:element name="ROI:Shape" namespace="{$newROINS}">
      <xsl:for-each
          select="@* [not(name() = 'FillColor' or name() = 'StrokeColor' or name() = 'Text' or name() =  'Visible' or name() =  'Locked')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:for-each select="@* [name() = 'FillColor']">
        <xsl:attribute name="Fill">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:for-each select="@* [name() = 'StrokeColor']">
        <xsl:attribute name="Stroke">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:for-each select="@* [name() = 'Text']">
        <xsl:attribute name="Label">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>

      <xsl:for-each select="* [local-name(.) = 'Line' or local-name(.) = 'Polyline']">
        <xsl:for-each select="@* [name(.) = 'MarkerStart']">
          <xsl:attribute name="MarkerStart">
            <xsl:value-of select="."/>
          </xsl:attribute>
        </xsl:for-each>
        <xsl:for-each select="@* [name(.) = 'MarkerEnd']">
          <xsl:attribute name="MarkerEnd">
            <xsl:value-of select="."/>
          </xsl:attribute>
        </xsl:for-each>
      </xsl:for-each>

      <xsl:for-each select="* [local-name(.) = 'Transform']">
        <xsl:attribute name="Transform"><xsl:value-of select="@A00"/>, <xsl:value-of
        select="@A10"/>, <xsl:value-of select="@A01"/>, <xsl:value-of select="@A11"
        />, <xsl:value-of select="@A02"/>, <xsl:value-of select="@A12"/></xsl:attribute>
      </xsl:for-each>
      <!-- end of attributes -->
      <xsl:for-each select="* [not(local-name(.) = 'Transform')]">
        <xsl:apply-templates select="."/>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <xsl:template match="ROI:Label">
    <xsl:element name="ROI:Text" namespace="{$newROINS}">
      <xsl:apply-templates select="@*|node()"/>
      <xsl:element name="ROI:Value" namespace="{$newROINS}"><xsl:for-each select=" parent::node()">
        <xsl:value-of select="@Text"/>
      </xsl:for-each></xsl:element>
    </xsl:element>
  </xsl:template>

  <xsl:template match="ROI:Polygon">
    <xsl:element name="ROI:Polyline" namespace="{$newROINS}">
      <xsl:apply-templates select="@* [not(name(.) = 'MarkerStart' or name(.) = 'MarkerEnd')]"/>
      <xsl:attribute name="Closed">true</xsl:attribute>
    </xsl:element>
  </xsl:template>

  <xsl:template match="ROI:Polyline">
    <xsl:element name="ROI:Polyline" namespace="{$newROINS}">
      <xsl:apply-templates select="@* [not(name(.) = 'MarkerStart' or name(.) = 'MarkerEnd')]"/>
      <xsl:attribute name="Closed">false</xsl:attribute>
    </xsl:element>
  </xsl:template>

  <xsl:template match="ROI:Line">
    <xsl:element name="ROI:Line" namespace="{$newROINS}">
      <xsl:apply-templates select="@* [not(name(.) = 'MarkerStart' or name(.) = 'MarkerEnd')]"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="SA:BasicAnnotation">
    <xsl:comment>BasicAnnotation elements cannot be converted to 2011-06 Schema, they are not supported.</xsl:comment>
    <xsl:message terminate="yes">OME-XSLT: 2012-06-to-2011-06.xsl - ERROR - BasicAnnotation elements cannot be converted to 2011-06 Schema, they are not supported.</xsl:message>
  </xsl:template>
  <xsl:template match="SA:NumericAnnotation">
    <xsl:comment>NumericAnnotation elements cannot be converted to 2011-06 Schema, they are not supported.</xsl:comment>
    <xsl:message terminate="yes">OME-XSLT: 2012-06-to-2011-06.xsl - ERROR - NumericAnnotation elements cannot be converted to 2011-06 Schema, they are not supported.</xsl:message>
  </xsl:template>

  <xsl:template match="SA:TextAnnotation">
    <xsl:comment>TextAnnotation elements cannot be converted to 2011-06 Schema, they are not supported.</xsl:comment>
    <xsl:message terminate="yes">OME-XSLT: 2012-06-to-2011-06.xsl - ERROR - TextAnnotation elements cannot be converted to 2011-06 Schema, they are not supported.</xsl:message>
  </xsl:template>

  <xsl:template match="SA:TypeAnnotation">
    <xsl:comment>TypeAnnotation elements cannot be converted to 2011-06 Schema, they are not supported.</xsl:comment>
    <xsl:message terminate="yes">OME-XSLT: 2012-06-to-2011-06.xsl - ERROR - TypeAnnotation elements cannot be converted to 2011-06 Schema, they are not supported.</xsl:message>
  </xsl:template>

  <!-- Rewriting all namespaces -->

  <xsl:template match="OME:OME">
    <OME:OME xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2011-06"
             xmlns:Bin="http://www.openmicroscopy.org/Schemas/BinaryFile/2011-06"
             xmlns:SPW="http://www.openmicroscopy.org/Schemas/SPW/2011-06"
             xmlns:SA="http://www.openmicroscopy.org/Schemas/SA/2011-06"
             xmlns:ROI="http://www.openmicroscopy.org/Schemas/ROI/2011-06"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://www.openmicroscopy.org/Schemas/OME/2011-06
                                 http://www.openmicroscopy.org/Schemas/OME/2011-06/ome.xsd">
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
