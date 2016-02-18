<?xml version = "1.0" encoding = "UTF-8"?>
<!--
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#
# Copyright (C) 2009 - 2015 Open Microscopy Environment
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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:OME="http://www.openmicroscopy.org/Schemas/OME/2016-DEV0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xml="http://www.w3.org/XML/1998/namespace"
    exclude-result-prefixes="OME"
    xmlns:exsl="http://exslt.org/common"
    extension-element-prefixes="exsl" version="1.0">

    <xsl:variable name="newOMENS">http://www.openmicroscopy.org/Schemas/OME/2015-01</xsl:variable>
    <xsl:variable name="newOMESL">http://www.openmicroscopy.org/Schemas/OME/2015-01 http://www.openmicroscopy.org/Schemas/OME/2015-01/ome.xsd</xsl:variable>
    <xsl:variable name="newSPWNS">http://www.openmicroscopy.org/Schemas/SPW/2015-01</xsl:variable>
    <xsl:variable name="newBINNS">http://www.openmicroscopy.org/Schemas/BinaryFile/2015-01</xsl:variable>
    <xsl:variable name="newROINS">http://www.openmicroscopy.org/Schemas/ROI/2015-01</xsl:variable>
    <xsl:variable name="newSANS">http://www.openmicroscopy.org/Schemas/SA/2015-01</xsl:variable>
    <xsl:variable name="newXSINS">http://www.w3.org/2001/XMLSchema-instance</xsl:variable>

    <xsl:output method="xml" indent="yes"/>
    <xsl:preserve-space elements="*"/>

    <!-- Dummy elements to register namespace prefixes -->
    <xsl:variable name="dummyOME">
        <xsl:element name="OME:OME" namespace="{$newOMENS}"/>
    </xsl:variable>
    <xsl:variable name="dummySPW">
        <xsl:element name="SPW:Screen" namespace="{$newSPWNS}"/>
    </xsl:variable>
    <xsl:variable name="dummyBIN">
        <xsl:element name="Bin:BinData" namespace="{$newBINNS}"/>
    </xsl:variable>
    <xsl:variable name="dummyROI">
        <xsl:element name="ROI:ROI" namespace="{$newROINS}"/>
    </xsl:variable>
    <xsl:variable name="dummySA">
        <xsl:element name="SA:StructuredAnnotations" namespace="{$newSANS}"/>
    </xsl:variable>
    <xsl:variable name="dummyXSI">
        <xsl:element name="xsi:schemaLocation" namespace="{$newXSINS}"/>
    </xsl:variable>

    <!-- Rewrite all namespaces -->

    <xsl:template match="OME:OME">
        <xsl:element name="OME" namespace="{$newOMENS}">
            <xsl:copy-of select="exsl:node-set($dummyOME)/*/namespace::*[.=$newOMENS]"/>
            <xsl:copy-of select="exsl:node-set($dummySPW)/*/namespace::*[.=$newSPWNS]"/>
            <xsl:copy-of select="exsl:node-set($dummyBIN)/*/namespace::*[.=$newBINNS]"/>
            <xsl:copy-of select="exsl:node-set($dummyROI)/*/namespace::*[.=$newROINS]"/>
            <xsl:copy-of select="exsl:node-set($dummySA)/*/namespace::*[.=$newSANS]"/>
            <xsl:copy-of select="exsl:node-set($dummyXSI)/*/namespace::*[.=$newXSINS]"/>
	    <xsl:attribute name="xsi:schemaLocation"><xsl:value-of select="$newOMESL"/></xsl:attribute>
	    <xsl:apply-templates select="@UUID|@Creator|node()"/> <!-- copy UUID and Creator attributes and nodes -->
        </xsl:element>
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

    <xsl:template match="OME:ROI | OME:Union | OME:ROI/OME:Description | OME:Shape | OME:Transform | OME:Rectangle | OME:Mask | OME:Point | OME:Ellipse | OME:Line | OME:Polyline | OME:Polygon | OME:Label | OME:ROIRef"
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

    <!-- Default processing -->

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
