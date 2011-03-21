<?xml version="1.0"?>
<!-- 
SchemaDOC Stylesheet for SOX Schemas.
Copyright TIBCO Extensibility - 2000-2001

$Id: esdSOX.xsl 457 2008-06-30 15:08:37Z andrew $

-->
<s:stylesheet xmlns:s="http://www.w3.org/1999/XSL/Transform" version="1.0" 
	xmlns="http://www.w3.org/TR/REC-html40" >

<s:import href="images.xsl" />
<s:import href="schemadoc_common.xsl" />

<s:param name="imageDir" select="'sdimages'"/>

<s:param name="schemaName" select="''"/>

<s:param name="doImages" select="'true'"/>

<s:param name="doSource" select="'true'"/>

<s:output method="html" indent="no"/>

<s:strip-space elements="*"/>

<s:key name="elementRefs" match="element" use="@type" />

<!-- ========================================================================
========================================================================== -->		
<s:template match="*|/"><s:apply-templates/></s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template match="text()|@*"><s:value-of select="."/></s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template match="/">
	<html>
		<head>
			<title>SchemaDOC - <s:value-of select="$schemaName"/> </title>
			<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>

			<link rel="stylesheet" title="Schemadoc Stylesheet" HREF="../schemadoc.css"/>				
			<script language="javascript" src='../sourceutils.js'>&#160;</script>
			<s:if test="$doImages='true' or $doImages='TRUE'">			
				<script language="javascript" src='../svgcheck.js'>&#160;</script>
				<script language="VBScript" src='../svgcheck.vbs'>&#160;</script>
			</s:if>
		</head>
		<body bgcolor="#FFFFFF" marginheight="0" marginwidth="0" topmargin="0" leftmargin="0">
			<s:if test="$doImages='true' or $doImages='TRUE'">
				<!-- removed SVN check -->	 
			</s:if> 	
			<s:apply-templates/>
			<center>
				<s:if test="$doSource='true' or $doSource='TRUE'">
					<table border="0" WIDTH="90%" BGCOLOR="#eeeeee" >
					<tr>
						<td> 
							<s:apply-templates mode="copy"/>
						</td>
					</tr>
					</table>
				</s:if>
			</center>
				
		</body>
	</html>
</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template match="schema">
	<table width="90%" border="0" cellspacing="4" cellpadding="2" align="center">
		<tr>
			<td bgcolor="#003366"  colspan="2" >
				<font face="Arial,Helvetica" size="5" color="#FFFFFF">
					SOX Schema -
					<s:apply-templates mode="prefix" select="." />
					<s:if test="@uri">
						:
						<s:value-of select="@uri"/> - 
					</s:if>
					
					<s:if test="$schemaName">
						<s:value-of select="concat(' : ', $schemaName)" />
					</s:if>
				
				</font>
			</td>
		</tr>
		<s:if test="intro | comment">
			<tr>
				<td width="20%" class="headers" align="right" valign="top" wrap="true">
					Schema Comments:
				</td>
				<td class="descriptions" align="left" bgcolor="#EEEEEE" fgcolor="#FFAA00" valign="top">
					<font color="#990000">
					<s:for-each select="./intro | ./comment">
						<s:apply-templates mode="comments" select="."/>
						<br/>-----<br/>
						
					</s:for-each>
					<!--<s:apply-templates mode="comments" select="./intro | ./comment"/>-->
					</font>
				</td>
			</tr>
		</s:if>

		<s:if test="namespace">
			<tr>
				<td width="20%" class="headers" align="right" valign="top" wrap="true">
					namespace
				</td>
				<td class="values" align="left" bgcolor="#EEEEEE" fgcolor="#FFAA00" valign="top">
					<s:for-each select="namespace">
						<A><s:attribute name="name">NAMESPACE_<s:value-of select="@prefix"/>
								</s:attribute>
						<s:value-of select="@prefix"/></A>:
						<s:value-of select="@namespace"/>
						
					</s:for-each>
				</td>
			</tr>
		</s:if>

		<s:if test="@version">
			<tr>
				<td width="20%" class="headers" align="right" valign="top" wrap="true">
					version
				</td>
				<td class="descriptions" align="left" bgcolor="#EEEEEE" fgcolor="#FFAA00" valign="top">
					<s:value-of select="@version"/>
				</td>
			</tr>
		</s:if>
		<s:if test="./join">
			<tr>
				<td width="20%" class="headers" align="right" valign="top" wrap="true">
					other schemas used
				</td>
				<td class="descriptions" align="left" bgcolor="#EEEEEE" fgcolor="#FFAA00" valign="top">
					<s:for-each select="./join/@system">
						<s:value-of select="."/>, 
					</s:for-each>
				</td>
			</tr>
		</s:if>
		     
			<tr>
				<td width="20%" class="headers" align="right" valign="top" wrap="true">
					Schema has:
				</td>
				<td class="values" bgcolor="eeeeee" align="left" valign="top">
					<s:number value="count(elementtype)"/>
									element definitions and 									 
									<s:number value="count(attdef)"/>
									element attribute definitions and
									<s:number value=" count(datatype)"/>
									datatype definitions.

				</td>
			</tr>

			<tr>
				<td width="20%" class="headers" align="right" valign="top" wrap="true">
					Possible root elements:
			</td>
			<td class="values" bgcolor="eeeeee" align="left" valign="top">
				<s:call-template name="dumpPossibleRoots" />
			</td>
		</tr>
	</table>
	<p/>
	<p/>
	<p/>
	<table width="90%" border="0" cellspacing="0" cellpadding="2" align="center">
	  <tr>
			<td bgcolor="#003366"  >
				<font face="Arial,Helvetica" size="5" color="#FFFFFF">
					Element list
				</font>
			</td>
		</tr>			
	</table>
	<p/>
	<s:apply-templates mode="anElement" select="elementtype"/>
	<p/>
	<p/>
	<table width="90%" border="0" cellspacing="0" cellpadding="2" align="center">
	  <tr>
			<td bgcolor="#003366"  >
				<font face="Arial,Helvetica" size="5" color="#FFFFFF">
					DataType definitions
				</font>
			</td>
		</tr>			
	</table>
	<p/>
	<s:apply-templates mode="datatypeDefs" select=".//datatype"/>
	<p/>
	<p/>		

	<P/>
	<P/>
	<P/>
	<s:if test="$doSource='true' or $doSource='TRUE'">

		<table width="90%" border="0" cellspacing="0" cellpadding="2" align="center">
			<tr>
				<td bgcolor="#bbbbbb"  >
					<font face="Arial,Helvetica" size="5" color="#FFFFFF">
						<s:if test="@name">
							<s:value-of select="@name"/>
						</s:if>
						Original Schema Source
					</font>
				</td>
			</tr>			
		</table>		
	</s:if>
</s:template>

<!--END s:schema***********************************************************-->

<!-- ========================================================================
	Generates a list of elements.
-->
<s:template name="listElements">
	<s:param name="elementList"/>
	<s:param name="emptyMessage"/>

	<s:choose>
		<s:when test="$elementList" >
			<s:for-each select="$elementList">
				<s:sort select="@name" />
				
				<a class="element-link" href="{concat('#ELEMENT_', @name)}">
					<s:value-of select="@name" />
				</a>
				<s:if test="position() != last()">, </s:if>
			</s:for-each>
		</s:when>
		<s:otherwise>
			<!-- spit out a note that this is not used -->
			<span class="note"><s:value-of select="$emptyMessage" /></span>
		</s:otherwise>
	</s:choose>

</s:template>

<!-- ========================================================================
	Compute the possible root list
-->
<s:template name="dumpPossibleRoots">

	<s:variable name="elementList" select="elementtype" />
	<s:variable name="possibleRoots" select="$elementList[ not( key('elementRefs', @name) ) ]" />
	
	<s:call-template name="listElements">
		<s:with-param name="elementList" select="$possibleRoots" />
		<s:with-param name="emptyMessage" select="'No obvious root elements' " />
	</s:call-template>

</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template match="elementtype" mode="anElement">
	<s:variable name="componentPath" select="concat('e:', @name)" />
	
	<center>
			<table width="90%" cellspacing="0" cellpadding="2" border="0" >
				<tr bgcolor="#003366">
					<td>
						<font face="Arial,Helvetica" color="#FFFFFF">
							Element
							<A>
								<s:attribute name="name">ELEMENT_<s:value-of select="@name"/>
								</s:attribute>
								<b>
									<s:value-of select="@name"/>
								</b>
							</A>
						</font>
					</td>
				</tr>
			</table>
		</center>
		<table width="90%" cellspacing="0" cellpadding="0" border="0" bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF" align="center">
			<tr>
				<td width="3%" bgcolor="#4444FF" align="left" valign="middle"></td>
				<td width="97%" align="left" valign="top">
					<table width="100%" cellspacing="3" cellpadding="1" border="0" bordercolor="#FFFFFF" align="left">
						
						<!-- insert the diagram row, if appropriate -->
						<s:call-template name="insertImageRow">
							<s:with-param name="doImages" select="$doImages" />
							<s:with-param name="componentPath" select="$componentPath" />
						</s:call-template>
						
						<s:if test="explain">
							<tr>
								<td class="tdnames">description</td>
								<td width="85%" class="descriptions"  align="left" bgcolor="#eeeeeeee" wrap="true">
									<font color="#990000">
									<s:apply-templates mode="schemacomments" select="./explain"/></font>
								</td>
							</tr>
						</s:if>
		<s:if test="extends">
						<tr>
							<td class="tdnames">extends</td>
							<td width="85%" class="values" bgcolor="#eeeeeeee">
								<s:apply-templates mode="extends" select="extends"/>
							</td>
						</tr>
		</s:if>	
			
						<tr>
							<td class="tdnames">attributes</td>
							<td width="85%" class="values" bgcolor="#eeeeeeee">
		<s:choose>			
			<s:when test=".//attdef">
								<s:apply-templates mode="glanceAttrib" select=".//attdef"/>
			</s:when>
			<s:otherwise>
			 none
			</s:otherwise>
		</s:choose>	</td>
						</tr>
			
				 		<tr>
							<td class="tdnames">uses</td>
								<s:call-template name="dumpUsesList" />
						</tr>
					</table>
				</td>
			</tr>
		</table>
		<table width="90%" cellspacing="0" cellpadding="0" border="0" bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF" align="center">
			<tr>
				<td width="3%" bgcolor="#6666FF" align="center" valign="top"></td>
				<td width="97%" align="center" valign="left">
					<table width="100%" cellspacing="3" cellpadding="1" border="0" align="left" valign="top" bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF">
						<!-- Find the parents -->
						<tr>
							<td class="tdnames">used by</td>
							<td class="values" bgcolor="#eeeeee" align="left" valign="top">
								<s:call-template name="dumpUsedByList" />
							</td>
						</tr>
						
						<tr>
							<td width="15%" bgcolor="#CCCCCC" align="right" valign="top">
								<span class="names">content model</span>
							</td>
							<td width="85%"  class="values" bgcolor="#eeeeee" >
										<s:choose>
											<s:when test="empty">
												empty 
											</s:when>
											<s:when test="extends">
												inherits from  <s:value-of select="extends/@type"/>
											</s:when>
											<s:when test="model">
												<s:apply-templates mode="content" select="model"/>&#160;		
											</s:when>
											<s:otherwise>
												string
											</s:otherwise>
										</s:choose>
												
							</td>
						</tr>

					</table>
				</td>
			</tr>
		</table>
		<s:if test=".//attdef">
			<table width="90%" cellspacing="0" cellpadding="0" border="0"  bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF" align="center">
				<tr>
					<td width="3%" bgcolor="#8888FF" align="left" valign="top"></td>
					<td width="97%" align="left" valign="top">
						<table width="100%" cellspacing="3" cellpadding="1" border="0"  align="left" valign="top" bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF">
							<s:call-template name="attribDetailHeader"/>
							<s:apply-templates mode="detailAttrib" select=".//attdef"/>
						</table>
						</td>
					</tr>
				</table>
		</s:if>
		<s:if test="$doSource='true' or $doSource='TRUE'">
		<table width="90%" cellspacing="0" cellpadding="0" border="0"  bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF" align="center">
				<tr>
					<td width="3%" bgcolor="#bbbbbb" align="left" valign="top"></td>
					<td width="97%" align="left" valign="top">
						<table width="100%" cellspacing="3" cellpadding="1" border="0"  align="left" valign="top" bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF">

						<tr>
							<td class="tdnames">source</td>
							<td width="85%" class="source" bgcolor="#eeeeeeee" align="left" valign="top">
									<s:apply-templates mode="copy" select="."/>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
		</s:if>
		<p/>
	</s:template>

<!-- ========================================================================
	Invoked by outputUsesList to dump a link to a particular element.
-->
<s:template name="elementLinkByName">
	<s:param name="elementName"/>
	
	<s:if test="/schema/elementtype[@name=$elementName]">
		<s:value-of select="concat('#ELEMENT_', $elementName)" />
	</s:if>
	
</s:template>

<!-- ========================================================================
	Note that the complexity here is due both to avoiding duplicates, and also
	insuring that we can list items that do not occur in the the document we
	are processing.
-->
<s:template name="computeUsesElements">
	<s:param name="references" />

	<s:if test="$references">
		<!-- build a sorted list -->
		<s:variable name="refList">
			<s:for-each select="$references">
				<s:sort select="@type" />
				<s:value-of select="concat(@type, ' ')"	/>
			</s:for-each>
		</s:variable>
		
		<s:call-template name="outputUsesList">
			<s:with-param name="list" select="$refList" />
		</s:call-template>
	</s:if>
	
</s:template>

<!-- ========================================================================
	Dump the list of items that we are using.
-->
<s:template name="dumpUsesList">
	<td width="85%" class="values" bgcolor="#eeeeeeee">
		<s:choose>			
			<s:when test="model">
				<s:call-template name="computeUsesElements">
					<s:with-param name="references" select=".//element" />
				</s:call-template>
			</s:when>	
			<s:when test="extends//element">
				<s:call-template name="computeUsesElements">
					<s:with-param name="references" select="extends//element" />
				</s:call-template>
				&#160;	and posible others from the supertype  <s:apply-templates mode="extends" select="extends"/>
			</s:when>	
			<s:when test="extends">
				<s:apply-templates mode="uses" select="extends//element" />
				see <s:apply-templates mode="extends" select="extends"/>
			</s:when>	
			<s:when test="any">any mix of string content and know elements is permited</s:when>
			
			<s:when test="empty">content is not permited</s:when>				
		</s:choose>						
	</td>
</s:template>

<!-- ========================================================================
-->
<s:template name="dumpUsedByList">
	
	<s:variable name="referenceNodes" select="key('elementRefs', @name)" />
	<s:variable name="referenceElements" select="$referenceNodes/ancestor::elementtype" />
	
	<s:call-template name="listElements">
		<s:with-param name="elementList" select="$referenceElements" />
		<s:with-param name="emptyMessage" select="'Not used by other elements.  Possible root element.'" />
	</s:call-template>
	
</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template match="attdef" mode="glanceAttrib">
		<s:value-of select="@name"/>&#160;
	</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template name="attribDetailHeader">

	<tr>
		<th width="15%" class="headers" align="right" valign="top">
			Attribute
		</th>
		<th width="20%" class="headers" align="left" >
			Datatype
		</th>
		<th width="10%" class="headers" align="left" >
			Presence
		</th>
		<th width="25%" class="headers" align="left"  >
			Values
		</th>
		<th width="10%" class="headers" align="left" >
			Default
		</th>
		<th width="20%" class="headers" align="left" >
			Comments
		</th>

	</tr>

</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template match="attdef" mode="detailAttrib">
		<tr>
			<td width="15%" class="headers" align="right" valign="top">
				<b>
					<s:value-of select="@name"/>
				</b>
			</td>
			<td width="20%" class="values" bgcolor="#eeeeee" align="left" valign="top">
				<A><s:attribute name="href">http://apps.xmlschema.com/schemaDoc/soxref.htm#datatype_<s:value-of select="@datatype"/></s:attribute>
       <s:choose>
        <s:when test="@datatype">
          <s:value-of select="@datatype"/>
        </s:when>
        <s:when test="enumeration">
        		enumeration (<s:value-of select="./enumeration/@datatype"/>)
        </s:when>
        <s:otherwise>
        	 string(default)
        </s:otherwise>
      </s:choose>
       </A>
			</td>
			<td width="10%" class="values" bgcolor="#eeeeee" align="left" valign="top">
      <s:choose>
        <s:when test="required">
          required
        </s:when>
        <s:when test="implied">
          implied
        </s:when>
        <s:when test="default">
          default
        </s:when>
        <s:when test="fixed">
          fixed
        </s:when>
        <s:otherwise>
           implied <i> default </i>
        </s:otherwise>
      </s:choose>
			</td>
			<td width="25%" class="values" bgcolor="#eeeeee" align="left" valign="top">

<s:for-each select=".//option">
	<s:value-of select="." />&#160;
</s:for-each>
			</td>
		
			<td width="10%" class="values" bgcolor="#eeeeee" align="left" valign="top">

					<s:value-of select="default"/>
			</td>

			<td width="20%" class="descriptions" bgcolor="#eeeeee" align="left" valign="top">
					<font color="#990000" size="-1">
						<s:if test="explain" >
							<s:apply-templates mode="schemacomments" select="./explain"/>
						</s:if>
					&#160;
					 </font> 				
			</td>		</tr>
	</s:template>

<!-- END detailattributetype TYPE -->

<!-- ========================================================================
========================================================================== -->		
<s:template match="element" mode="anElement">
		<A><s:attribute name="HREF">#ELEMENT_<s:value-of select="@type"/></s:attribute><s:value-of select="@type"/>
		</A>  
	</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template match="explain" mode="schemacomments">
		<s:apply-templates mode="comments" />
	</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template match="explain" mode="usagecomments">
		<s:choose> 
		<s:when test="@usage='USAGE'">
	     			
			<s:copy>
				<s:value-of select="text()"/>
			</s:copy>
		</s:when> 
		</s:choose>   
	</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template match="*" mode="prefix">   
	   <s:if test="@prefix">
			<font color="#00ff00"> <s:value-of select="@prefix"/>:</font>
		</s:if>
	</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template match="element" mode="uses">

		<s:apply-templates mode="prefix" select="." />
		
		<A><s:attribute name="HREF">#ELEMENT_<s:value-of select="@type"/>
			</s:attribute>
			<s:value-of select="@type"/>
		</A> 
		<s:if test="@occurs">
		 (<s:value-of select="@occurs"/>)
		</s:if>&#160;

		<s:if test="@name">
			[<s:value-of select="@name"/>]
		</s:if>
		<s:if test="position()!=last()">, </s:if>
	</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template match="model" mode="content">

	<s:if test="string"> 
		<s:choose> 
			<s:when test="./string/@datatype">
				a string of the datatype <s:value-of select="./string/@datatype" />
			</s:when> 
			<s:otherwise>
				content that may be specialized by datatype (string)
			</s:otherwise>
		</s:choose>
		
	</s:if>
	<s:if test="element">uses one element (element)</s:if>
	<s:if test="mixed">any mix of string content and named elements (mixed)</s:if>
	<s:if test="choice">one of the subelements (choice) </s:if>
	<s:if test="sequence">two or more subelements(sequence)</s:if>
	<s:if test="paramref">parameter reference(paramref)</s:if>
</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template match="explain" mode="comments">
	<s:copy>
		<s:apply-templates mode="comments" />
	</s:copy>
</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template match="extends" mode="extends">
	<s:choose>

		<s:when  test="@prefix">
			<A>
				<s:attribute name="HREF">#NAMESPACE_<s:value-of select="@prefix"/>
				</s:attribute>
				<s:value-of select="@prefix"/>:<s:value-of select="@type"/>	
			</A> 			
		</s:when>
		<s:otherwise>
			<A>
				<s:attribute name="HREF">#ELEMENT_<s:value-of select="@type"/>
				</s:attribute>
				<s:value-of select="@type"/>
			</A>
		</s:otherwise>
		
	</s:choose>
	&#160;
</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template match="datatype" mode="datatypeDefs">
	<center>
		<table width="90%" cellspacing="0" cellpadding="2" border="0" >
			<tr bgcolor="#AAAAFF">
				<td>
					<font face="Arial,Helvetica" color="#FFFFFF">
						Datatype
						<A>
							<s:attribute name="name">DATATYPE_<s:value-of select="@name"/>
							</s:attribute>
							<b>
								<s:value-of select="@name"/>
							</b>
						</A>
					</font>
				</td>					  
			</tr>
		</table>
			<table width="90%" cellspacing="0" cellpadding="0" border="0" bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF" align="center">
	<tr>
		<td width="3%" bgcolor="#AAAAFF" align="left" valign="middle"></td>
		<td width="97%" align="left" valign="top">
	
						 <table width="100%" cellspacing="3" cellpadding="1" border="0" bordercolor="#FFFFFF" align="left">


				<s:if test="/explain">
					<tr>
						<td class="tdnames">description</td>
						<td width="85%" class="descriptions"  align="left" bgcolor="#eeeeeeee" wrap="true">
							<font color="#990000">
							<s:apply-templates mode="schemacomments" select=".//explain"/></font>
						</td>
					</tr>
				</s:if>

	<s:if test="enumeration">
		<s:apply-templates mode="enumdt" select="enumeration" />
	</s:if>
	<s:if test="scalar">
		<s:apply-templates mode="scalardt" select="scalar" />
	</s:if>
	<s:if test="varchar">
		<s:apply-templates mode="varchardt" select="varchar" />
	</s:if>
	
					</table>
				</td>
			</tr>
		</table>

	</center>		
	<p/>
</s:template>

<!--datatypes -->

<!-- ========================================================================
========================================================================== -->		
<s:template match="enumeration" mode="enumdt">

	<s:if test="@datatype">
		<tr>
			<td class="tdnames">base type</td>
			<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
				 <s:value-of select="@datatype" />
			</td>
		</tr>
	</s:if>
	
	<tr>
		<td class="tdnames">values</td>
		<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
			 <s:for-each select="./option"> 
					<s:value-of select="." />
					<s:if test="position()!=last()"> | </s:if>
			 </s:for-each> 
				
		</td>
	</tr>
	
</s:template>

<!--enumeration dt -->

<!-- ========================================================================
========================================================================== -->		
<s:template match="scalar" mode="scalardt">
							<tr>
								<td class="tdnames">base type</td>
								<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
								  <s:choose> 
								 <s:when test="@datatype">

								   <s:value-of select="@datatype" />
								   </s:when>
								   <s:otherwise>
								   		number <i> (default) </i>
								   </s:otherwise>
								  </s:choose> 
								</td>
							</tr>
						
						<s:if test="@prefix">
							<tr>
								<td class="tdnames" >prefix</td>
								<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
								    <s:value-of select="@prefix" />
								</td>
							</tr>
						</s:if>
						<s:if test="@digits">
							<tr>
								<td class="tdnames">digits</td>
								<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
								    <s:value-of select="@digits" />
								</td>
							</tr>
						</s:if>
						<s:if test="@decimals">
							<tr>
								<td class="tdnames" >decimals</td>
								<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
								    <s:value-of select="@decimals" />
								</td>
							</tr>
						</s:if>
						<s:if test="@minexclusive | @maxexclusive | @minvalue | maxvalue">
							<tr>
								<td class="tdnames" >range</td>
								<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
								   <s:choose>
								   		<s:when test="@minexclusive='true'">
								   			(
								   		</s:when>
								   		<s:otherwise>
								   			[
								   		</s:otherwise>
								   </s:choose>
								   <s:choose>
								   		<s:when test="@minvalue">
								   			<s:value-of select="@minvalue" />,
								   		</s:when>
								   		<s:otherwise>
								   			*,
								   		</s:otherwise>
								   </s:choose>
								   <s:choose>
								   		<s:when test="@maxvalue">
								   			<s:value-of select="@maxvalue" />
								   		</s:when>
								   		<s:otherwise>
								   			*
								   		</s:otherwise>
								   </s:choose>
								   <s:choose>
								   		<s:when test="@maxexclusive='true'">
								   			)
								   		</s:when>
								   		<s:otherwise>
								   			]
								   		</s:otherwise>
								   </s:choose>
								      
								</td>
							</tr>
						</s:if>
						

	   </s:template>

<!--scalar dt -->

<!-- ========================================================================
========================================================================== -->		
<s:template match="varchar" mode="varchardt">
							<tr>
								<td class="tdnames">base type</td>
								<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
				<s:choose>
						<s:when test="@datatype">
								   <s:value-of select="@datatype" />
						</s:when>
						<s:otherwise>
							string <i> (default) </i>
						</s:otherwise>		
				</s:choose>	
								</td>
							</tr>
							<tr>
								<td class="tdnames">maxlength</td>
								<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
								    <s:value-of select="@maxlength" />
								</td>
							</tr>

	   </s:template>

<!--varchar dt -->

<!--
	$Log$
	Revision 1.1  2003/12/02 19:45:47  siah
	first commit

	Revision 1.6  2002/05/15 13:38:09  Eric
	Patches for Xalan issues with XPath.
	
	Revision 1.5  2002/02/06 22:34:10  Eric
	Reenabled schemadoc options, minor improvements...
	
	Revision 1.4  2002/01/22 14:48:23  wraymond
	General: Canon v1.2
	Specific: SchemaDoc Changes
	
	Revision 1.3  2002/01/17 20:40:39  Eric
	Another round of fixes for schemadoc.
	
	Revision 1.2  2002/01/16 19:20:33  Eric
	Moved shared code, fixed uses in XSD, XSD-CR now in sync.
	
	Revision 1.1  2002/01/15 18:37:42  Eric
	Schemadoc test framework and files.
	
-->

</s:stylesheet>
