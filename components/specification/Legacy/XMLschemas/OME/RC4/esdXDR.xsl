<?xml version="1.0"?>
<!-- 
SchemaDOC Stylesheet for XDR Schemas.
Copyright TIBCO Extensibility - 2000-2001

$Id: esdXDR.xsl 457 2008-06-30 15:08:37Z andrew $
-->

<s:stylesheet
	xmlns:s="http://www.w3.org/1999/XSL/Transform"
	version="1.0"
	xmlns="http://www.w3.org/TR/REC-html40" xmlns:xdr="urn:schemas-microsoft-com:xml-data"
	xmlns:dt="urn:schemas-microsoft-com:datatypes"
	xmlns:local="#local-functions">

<s:import href="images.xsl" />
<s:import href="schemadoc_common.xsl" />

<s:param name="imageDir" select="'sdimages'"/>
<s:param name="schemaName" select="''"/>
<s:param name="doImages" select="'true'"/>
<s:param name="doSource" select="'true'"/>

<s:output method="html" indent="no"/>
<s:strip-space elements="*"/>

<s:key name="elementRefs" match="xdr:element" use="@type" />

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
<s:template match="xdr:Schema">
		<table width="90%" border="0" cellspacing="4" cellpadding="2" align="center">
			<tr  >
					<td bgcolor="#003366"  colspan="2" >
						<font face="Arial,Helvetica" size="5" color="#FFFFFF">
					 
						XDR Schema : <s:value-of select="$schemaName"/>
					
					</font>
				</td>
			</tr>
		    <s:if test="xdr:description">
			<tr >
				<td width="20%" class="headers" align="right" valign="top" wrap="true">
					Schema Comments:
				</td>
				<td class="descriptions" align="left" bgcolor="#EEEEEE" fgcolor="#FFAA00" valign="top">
					<font color="#990000">
					<s:apply-templates mode="schemacomments" select="./xdr:description"/></font>
				</td>
			</tr>
		     </s:if>
			<tr>
				<td width="20%" class="headers" align="right" valign="top" wrap="true">
					Schema has:
				</td>
				<td class="values" bgcolor="eeeeee" align="left" valign="top">
					<s:number value="count(xdr:ElementType)"/>
									element definitions, 
									<s:number value="count(xdr:AttributeType)"/>
									global attribute definitions.
									<s:number value="count(xdr:ElementType/xdr:AttributeType)"/>
									element attribute definitions.
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
		<p/>		<p/>
		<table width="90%" border="0" cellspacing="0" cellpadding="2" align="center">
	    <tr  >
					<td bgcolor="#003366"  >
						<font face="Arial,Helvetica" size="5" color="#FFFFFF">
							Element list
						</font>
				</td>
			</tr>			
		</table>
		<p/>
		<s:apply-templates mode="anElement" select="xdr:ElementType">
			<s:sort select="@name" />
		</s:apply-templates>


		<table width="90%" border="0" cellspacing="0" cellpadding="2" align="center">
			<tr>
			<td bgcolor="#8888ff"  >
				<font face="Arial,Helvetica" size="5" color="#FFFFFF">
							Global Attributes
				</font>
			</td>
			</tr>			
		</table>
			<table width="90%" cellspacing="0" cellpadding="0" border="0"  bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF" align="center">
				<tr>
					<td width="3%" bgcolor="#8888ff" align="left" valign="top"></td>
					<td width="97%" align="left" valign="top">
						<table width="100%" cellspacing="3" cellpadding="1" border="0"  align="left" valign="top" bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF">
							<tr>
								<th width="15%" class="headers" align="right" valign="top">
									Attribute
								</th>
								<th width="25%" class="headerslg">
									Datatype
								</th>
								<th width="35%" class="headerslg">
									Required
								</th>
								<th width="25%" class="headerslg">
									Values
								</th>
								<th width="25%" class="headerslg">
									Default
								</th>
							</tr>
							<s:apply-templates mode="detailAttrib" select="xdr:AttributeType"/>
						</table>
					</td>
				</tr>
			</table>
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
<!--END xdr:schema***********************************************************-->

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
			<!-- spit out a note that none were found -->
			<span class="note"><s:value-of select="$emptyMessage" /></span>
		</s:otherwise>
	</s:choose>

</s:template>

<!-- ========================================================================
	Dump the possible root elements.
-->
<s:template name="dumpPossibleRoots" >

	<s:variable name="elementList" select="xdr:ElementType" />
	<s:variable name="possibleRoots" select="$elementList[ not( key('elementRefs', @name) ) ]" />
	
	<s:call-template name="listElements">
		<s:with-param name="elementList" select="$possibleRoots" />
		<s:with-param name="emptyMessage" select="'No obvious root elements' " />
	</s:call-template>

</s:template>

<!-- ========================================================================
========================================================================== -->		
	<s:template mode="anElement" match="xdr:ElementType">
		<s:variable name="componentPath" select="concat('e:',@name)" />
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
						
						<s:if test="xdr:description">
							<tr>
								<td class="tdnames">description</td>
								<td width="85%" class="descriptions"  align="left" bgcolor="#eeeeeeee" wrap="true">
									<font color="#990000">
									<s:apply-templates mode="schemacomments" select="./xdr:description"/>
									</font>
								</td>
							</tr>
						</s:if>
						<s:if test="xdr:description/@usage">
							<tr>
								<td class="tdnames">usage notes</td>
								<td width="85%" class="descriptions"  align="left" bgcolor="#eeeeeeee" wrap="true">
									<font color="#990000">
									
									<s:apply-templates mode="usagecomments" select="./xdr:description"/></font>
								</td>
							</tr>
						</s:if>
						<tr>
							<td class="tdnames">attributes</td>
							<td width="85%" class="values" bgcolor="#eeeeeeee">
								<s:apply-templates mode="glanceAttrib" select="xdr:attribute">
									<s:sort select="@type" />
								</s:apply-templates>
							</td>
						</tr>
				 		<tr>
							<td class="tdnames">uses</td>
							<td width="85%" class="values" bgcolor="#eeeeeeee">
								<s:call-template name="computeUsesElements" />
							</td>
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
							<td class="tdnames" >used by</td>
							<td class="values" bgcolor="#eeeeee" align="left" valign="top">
								<s:call-template name="dumpUsedByList" />
							</td>
						</tr>
						<tr>
							<td class="tdnames" >content</td>
							<td width="85%" class="values" bgcolor="#eeeeeeee">
      <s:choose>
        <s:when test="@dt:type">
          <A ><s:attribute name="href">http://apps.xmlschema.com/schemaDoc/xdrref.htm#datatype_<s:value-of select="@dt:type"></s:value-of></s:attribute>
            <s:value-of select="@dt:type"></s:value-of></A> datatype
        </s:when>
        <s:when test="@content">
          <A ><s:attribute name="href">http://apps.xmlschema.com/schemaDoc/xdrref.htm#content_<s:value-of select="@content"></s:value-of></s:attribute>
            <s:value-of select="@content"></s:value-of></A>
        </s:when>
        <s:otherwise>
          <A href="#content_mixed">mixed</A> (default)
        </s:otherwise>
      </s:choose>
							</td>
						</tr>

						<tr>
							<td width="15%" bgcolor="#CCCCCC" align="right" valign="top">
								<span class="names">order</span>
							</td>
							<td width="85%"  class="values" bgcolor="#eeeeee" >

      <s:choose>
        <s:when test="@dt:type">
          <SPAN class="note">Ignored when a datatype is specified.</SPAN>
        </s:when>
        <s:when test="@order">
          <A><s:attribute name="href">http://apps.xmlschema.com/schemaDoc/xdrref.htm#order_<s:value-of select="@order"/></s:attribute>
            <s:value-of select="@order"/></A>
        </s:when>
        <s:otherwise>
          <A href="http://apps.xmlschema.com/schemaDoc/xdrref.htm#order_many">many</A> (default)
        </s:otherwise>
      </s:choose>					
							</td>
						</tr>

						<tr>
							<td width="15%" bgcolor="#CCCCCC" align="right" valign="top">
								<span class="names">model</span>
							</td>
							<td width="85%"  class="values" bgcolor="#eeeeee" >

      <s:choose>
        <s:when test="@dt:type">
          Treated as <A class="reference-link" href="#model_closed">closed</A> when a datatype is specified.
        </s:when>
        <s:when test="@model">
          <A ><s:attribute name="href">http://apps.xmlschema.com/schemaDoc/xdrref.htm#model_<s:value-of select="@model"></s:value-of></s:attribute>
            <s:value-of select="@model"></s:value-of></A>
        </s:when>
        <s:otherwise>
          <A href="http://apps.xmlschema.com/schemaDoc/xdrref.htm#model_open">open</A> (default)
        </s:otherwise>
      </s:choose>					
							</td>
						</tr>

					</table>
				</td>
			</tr>
		</table>
		<s:if test="xdr:AttributeType">
			<table width="90%" cellspacing="0" cellpadding="0" border="0"  bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF" align="center">
				<tr>
					<td width="3%" bgcolor="#8888ff" align="left" valign="top"></td>
					<td width="97%" align="left" valign="top">
						<table width="100%" cellspacing="3" cellpadding="1" border="0"  align="left" valign="top" bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF">
							<s:call-template name="attribDetailHeader"/>
							<s:apply-templates mode="detailAttrib" select="xdr:AttributeType"/>
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
	Note that the complexity here is due both to avoiding duplicates, and also
	insuring that we can list items that do not occur in the the document we
	are processing.
-->
<s:template name="computeUsesElements">

	<s:variable name="references" select=".//xdr:element" />
	
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
	Invoked by outputUsesList to dump a link to a particular element.
-->
<s:template name="elementLinkByName">
	<s:param name="elementName"/>
	
	<s:if test="/xdr:Schema/xdr:ElementType[@name=$elementName]">
		<s:value-of select="concat('#ELEMENT_', $elementName)" />
	</s:if>
	
</s:template>

<!-- ========================================================================
	Dumps the list of used by elements
-->
<s:template name="dumpUsedByList">
	
	<s:variable name="name" select="@name" />
	
	<s:variable name="referenceNodes" select="key('elementRefs', @name)" />
	<s:variable name="referenceElements" select="$referenceNodes/ancestor::xdr:ElementType" />
	
	<s:call-template name="listElements">
		<s:with-param name="elementList" select="$referenceElements" />
		<s:with-param name="emptyMessage" select="'Not used by other elements.  Possible root element.'" />
	</s:call-template>

</s:template>

<!-- ========================================================================
-->
<s:template mode="glanceAttrib" match="xdr:attribute">
	<s:value-of select="@type"/>
	<s:if test="position() != last()">
		<s:text>, </s:text>
	</s:if>
</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template name="attribDetailHeader" >

	<tr>
		<th width="15%" class="headers" align="right" valign="top">
			Attribute
		</th>
		<th width="20%" class="headers" align="left" >
			Datatype
		</th>
		<th width="10%" class="headers" align="left" >
			Required
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
	<s:template mode="detailAttrib" match="xdr:AttributeType">
		<tr>
			<td width="15%" class="headers" align="right" valign="top">
				<b>
					<s:value-of select="@name"/>
				</b>
			</td>
			<td width="20%" class="values" bgcolor="#eeeeee" align="left" valign="top">
				<A ><s:attribute name="href">http://apps.xmlschema.com/schemaDoc/xdrref.htm#datatype_<s:value-of select="@dt:type"/></s:attribute>
        				<s:value-of select="@dt:type"/></A>
			</td>
			<td width="10%" class="values" bgcolor="#eeeeee" align="left" valign="top">
      <s:choose>
        <s:when test="@required">
          <s:value-of select="@required"/>
        </s:when>
        <s:otherwise>
          no (default)
        </s:otherwise>
      </s:choose>
			</td>
			<td width="25%" class="values" bgcolor="#eeeeee" align="left" valign="top">

<s:value-of  select="@dt:values"/>
<!--<s:for-each select="@dt:values">
	<s:value-of disable-output-escaping="yes" select="local:writeEnumeration(.)" />
</s:for-each>
	-->		</td>
		
			<td width="10%" class="values" bgcolor="#eeeeee" align="left" valign="top">

					<s:value-of select="@default"/>
			</td>


			<td width="20%" class="descriptions" bgcolor="#eeeeee" align="left" valign="top">
					<font color="#990000" size="-1">
						<s:if test="./xdr:description">
							<s:apply-templates mode="schemacomments" select="./xdr:description"/>
						</s:if>
						<s:if test="./xdr:description/@usage">
							<br/>Usage Notes:<br/><s:apply-templates mode="usagecomments" select="./xdr:description"/>
						</s:if>
					&#160;
					 </font> 				
			</td>
		
<!--	<td width="85%" bgcolor="#eeeeeeee" align="left" valign="top">
				<span class="Paragraph">
					<s:apply-templates mode="copy" select="."/>
				</span>
			</td>
			-->
		</tr>
		
	</s:template>


	<!-- END detailattributetype TYPE -->

<!-- ========================================================================
========================================================================== -->		
	<s:template match="xdr:description" mode="schemacomments">
		 
		<s:choose> 
		<s:when test="not(@usage='USAGE')">
     			<s:value-of select="*"/>
		</s:when> 
		</s:choose>   
			<!--<s:apply-templates mode="comments" />-->
	</s:template>
	
<!-- ========================================================================
========================================================================== -->		
	<s:template match="xdr:description" mode="usagecomments">
		<s:choose> 
		<s:when test="@usage='USAGE'">
     			<s:value-of select="*"/>
		</s:when> 
		</s:choose>   
	</s:template>

<!--
	$Log$
	Revision 1.1  2003/12/02 19:57:06  siah
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
