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
  xmlns:exsl="http://exslt.org/common" 
  xmlns:fn="http://www.w3.org/2005/xpath-functions"
  extension-element-prefixes="exsl"
  version="1.0">
 
  <xsl:variable name="newOMENS">http://www.openmicroscopy.org/Schemas/OME/2009-09</xsl:variable>
  <xsl:variable name="newSPWNS">http://www.openmicroscopy.org/Schemas/SPW/2009-09</xsl:variable>
  <xsl:output method="xml" indent="yes"/>
  <xsl:preserve-space elements="*"/>

  
  <!-- Actual schema changes -->

 <!-- data management -->
 <!-- Remove the Locked attribute -->
   <xsl:template match="OME:Dataset">
    <xsl:element name="Dataset" namespace="{$newOMENS}">
      <xsl:copy-of select="@* [not(name() = 'Locked')]"/>  
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>
  
<!-- 
Convert element into Attribute except GroupRef
Rename attribute OMEName into UserName
-->
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
 
 <!-- Rename ObjectiveRef to ObjectiveSettings -->
  <xsl:template match="OME:ObjectiveRef">
    <xsl:element name="ObjectiveSettings" namespace="{$newOMENS}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

 <!-- Rename LightSourceRef to LightSettings -->
  <xsl:template match="OME:LightSourceRef">
    <xsl:element name="LightSettings" namespace="{$newOMENS}">
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

<!-- Convert element into Attribute -->
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

<!-- 
Convert the attributes EmFilterRef, ExFilterRef and DichroicRef into 
elements EmissionFilterRef, ExcitationFilterRef and DichroicRef.
Copy all the other attributes.
-->  
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
 </xsl:element>
</xsl:template>

<!-- Rename PixelType attribute to Type -->   
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

<!-- 
Copy the MicrobeamManipulation node from Image corresponding to the MicrobeamManipulationRef.
-->
<xsl:template match="OME:Experiment">
<xsl:variable name="images">
  <xsl:copy-of select="following-sibling::OME:Image"/>
</xsl:variable>
 <xsl:element name="Experiment" namespace="{$newOMENS}">
   <xsl:for-each select="*">
    <xsl:choose>
      <xsl:when test="local-name(.) = 'MicrobeamManipulationRef'">
        <xsl:variable name="id" select="@ID"/>
        <xsl:for-each select="exsl:node-set($images)/*">
          <xsl:for-each select="* [name()='MicrobeamManipulation']">
            <xsl:if test="@ID=$id">
              <xsl:element name="{local-name(.)}" namespace="{$newOMENS}">
                <xsl:apply-templates select="@*|node()"/>
              </xsl:element>
            </xsl:if>
           </xsl:for-each>
         </xsl:for-each>
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
    <xsl:variable name="ac" select="current()/@AcquiredPixels"/>
    <xsl:apply-templates select="@* [not(name() = 'DefaultPixels' or name() = 'AcquiredPixels')]"/>
    <xsl:for-each select="* [not(local-name(.) = 'Thumbnail' or local-name(.) = 'DisplayOptions' or local-name(.) = 'Region' or local-name(.) = 'CustomAttributes' or local-name(.) = 'ROI' or local-name(.) = 'LogicalChannel')]">
        <xsl:choose>
         <xsl:when test="local-name(.) = 'CreationDate'">
          <xsl:element name="AcquiredDate" namespace="{$newOMENS}">
            <xsl:value-of select="."/>
          </xsl:element>
         </xsl:when>
         <xsl:when test="local-name(.) = 'Pixels'">
            <xsl:if test="@ID=$ac"> <!-- add controls to make sure we only copy one. -->
             <xsl:element name="{local-name(.)}" namespace="{$newOMENS}">
             <xsl:apply-templates select="@*|node()"/>
             <!-- copy channel to Pixels -->
                <!-- logical channel start -->
                <!--
                <xsl:when test="local-name(.) = 'LogicalChannel'">
                <xsl:variable name="lc">
                    <xsl:copy-of select="current()"/>
                </xsl:variable>
                -->
                <xsl:variable name="logicalChannels">
                <xsl:copy-of select="preceding-sibling::OME:LogicalChannel"/>
                </xsl:variable>
                <xsl:for-each select="exsl:node-set($logicalChannels)/*">
                <xsl:variable name="lc">
                    <xsl:copy-of select="current()"/>
                </xsl:variable>
                
                <xsl:for-each select="*  [local-name(.) = 'ChannelComponent']">
                    <xsl:element name="Channel" namespace="{$newOMENS}">
                        <xsl:attribute name="Color">
                        <!-- convert value of @ColorDomain-->
                         <xsl:call-template name="convertColorDomain">
                         <xsl:with-param name="cc" select="@ColorDomain"/>
                        </xsl:call-template>
                        </xsl:attribute>
                        <xsl:for-each select="exsl:node-set($lc)/*">
                            <xsl:apply-templates select="@*"/>
                            <xsl:for-each select="* [not(local-name(.) = 'ChannelComponent')]">
                                <xsl:element name="{local-name(.)}" namespace="{$newOMENS}">
                                    <xsl:apply-templates select="@*|node()"/>
                                </xsl:element>
                            </xsl:for-each>
                        </xsl:for-each>
                    </xsl:element>
                </xsl:for-each>
                </xsl:for-each>
                <!--
                </xsl:when>
                -->
            <!-- logical channel end -->
             
             
             </xsl:element>
            </xsl:if>
         </xsl:when>
         <!-- replace MicrobeamManipulation by MicrobeamManipulationRef -->
         <xsl:when test="local-name(.) = 'MicrobeamManipulation'">
            <xsl:variable name="id" select="@ID"/>
            <xsl:element name="MicrobeamManipulationRef" namespace="{$newOMENS}">
             <xsl:apply-templates select="@* [name() = 'ID']"/>
             <xsl:value-of select="."/>
            </xsl:element>
         </xsl:when>
      
         
         <xsl:otherwise>
            <xsl:element name="{local-name(.)}" namespace="{$newOMENS}">
             <xsl:apply-templates select="@*|node()"/>
             <xsl:value-of select="."/>
            </xsl:element>
         </xsl:otherwise>
       </xsl:choose>
    </xsl:for-each>
 </xsl:element>
</xsl:template>

<xsl:template name="convertColorDomain">
<xsl:param name="cc"/>
<xsl:choose>

<xsl:when test="contains($cc,'red') or contains($cc,'r')">
foo
</xsl:when>
<xsl:when test="contains($cc,'green') or contains($cc,'g')">
foo
</xsl:when>
<xsl:when test="contains($cc,'blue') or contains($cc,'b')">
foo
</xsl:when>
<xsl:otherwise>
0
</xsl:otherwise>
</xsl:choose>


</xsl:template>


 <!-- Screen Plate Well -->
  <!-- 
  Remove or rename attributes in WellSample.
  Remove Index, Rename PosX to PositionX & PosY to PositionY
  -->
  <xsl:template match="SPW:WellSample">
   <xsl:element name="WellSample" namespace="{$newSPWNS}">
      <xsl:for-each select="@* [not(name() = 'Index' or name() = 'PosX' or name() = 'PosY')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:for-each select="@* [name() = 'PosX']">
        <xsl:attribute name="PositionX">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:for-each select="@* [name() = 'PosY']">
        <xsl:attribute name="PositionY">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
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
      <xsl:otherwise>
        <xsl:element name="Description" namespace="{$newOMENS}">
          <xsl:apply-templates select="node()"/>
        </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- 
  Convert the attribute Description in Plate into a child element.
  Copy all the other attributes.
  Copy all child elements.
  -->
  <xsl:template match="SPW:Plate">
   <xsl:element name="Plate" namespace="{$newSPWNS}">
      <xsl:for-each select="@* [not(name() = 'Description')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:for-each select="@* [name() = 'Description']">
        <xsl:element name="Description">
          <xsl:value-of select="."/>
        </xsl:element>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <!-- 
  Convert the attribute Description in Plate into a child element.
  Copy all the other attributes.
  Copy all child elements.
  -->
  <xsl:template match="SPW:Plate">
   <xsl:element name="Plate" namespace="{$newSPWNS}">
      <xsl:for-each select="@* [not(name() = 'Description')]">
        <xsl:attribute name="{local-name(.)}">
          <xsl:value-of select="."/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:for-each select="@* [name() = 'Description']">
        <xsl:element name="Description" namespace="{$newSPWNS}">
          <xsl:value-of select="."/>
        </xsl:element>
      </xsl:for-each>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
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
