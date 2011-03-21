<?xml version="1.0"?>
<!--
	Support for SchemaDoc of the XML Schema recommendation.
	
	
	$Id: esdXSD.xsl 4 2003-12-02 19:59:11Z siah $
	
-->

<s:stylesheet  xmlns:s="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns="http://www.w3.org/2000/10/XMLSchema" 
	xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	xmlns:t="http://www.tibco.com/namespaces/2002/temporary"
	xmlns:xalan="http://xml.apache.org/xalan"
	exclude-result-prefixes="xsd t xalan" >

<s:import href="xsd_common.xsl" />
<s:import href="schemadoc_common.xsl" />
<s:import href="images.xsl" />

<s:variable name="xsdNamespace" select="'http://www.w3.org/2001/XMLSchema'" />
<s:variable name="schemaFlavor" select="'XML Schema'" />

<!-- IDENTICAL to XSD-CR stylesheet from here to comment at end reading END IDENTICAL -->
<!-- It would seem weird that we would have to duplicate so much for the stylesheets
	however, the namespaces are different, but the information that we are extracting
	is the same, so the stylesheets are pretty much entirely the same.
	
	Where namespaces do not matter, and templates have been written that way, the templates
	can be put into xsd_common.xsl, otherwise, keep them here and assume that they are the
	same.
	-->
	
<s:param name="imageDir" select="'sdimages'"/>
<s:param name="schemaName" select="''"/>
<s:param name="doImages" select="'true'"/>
<s:param name="doSource" select="'true'"/>

<s:variable name="root" select="/*"/>
<s:variable name="elements" select="$root//xsd:element"/>
<s:variable name="complexTypes" select="$root//xsd:complexType"/>
<s:variable name="specRoot" select="'http://www.w3.org/TR/xmlschema-'" />

<s:variable name="subctypes" select="$root//*[./@base]"/>

<s:variable name="targetNamespace" select="string(/xsd:schema/@targetNamespace)" />

<s:key name="gid-key"
	match="xsd:element | xsd:group | xsd:complexType | xsd:simpleType | xsd:attributeGroup | xsd:attribute"
	use="generate-id()" />

<s:output method="html" indent="yes"/>
<s:strip-space elements="*"/>

<!-- ========================================================================
========================================================================== -->		
<s:template match="xsd:schema">

	<s:variable name="elementRefsTree"><s:call-template name="buildElementRefs" /></s:variable>
	<s:variable name="elementRefs" select="xalan:nodeset($elementRefsTree)" />
	
	<table width="90%" border="0" cellspacing="4" cellpadding="2" align="center">
		<tr>
			<td bgcolor="#003366"  colspan="2" >
				<font face="Arial,Helvetica" size="5" color="#FFFFFF">
				 
					<s:value-of select="$schemaFlavor" />
					<s:if test="$schemaName">
						<s:value-of select="concat(' : ', $schemaName)"/>
					</s:if>
				
				</font>
			</td>
		</tr>
		
		<!-- show the target namespace only if there is one -->
		<s:if test="@targetNamespace">
			<tr>
				<td width="20%" class="headers" align="right" valign="top" wrap="true">Target namespace:</td>
				<td class="values" bgcolor="#eeeeee" align="left" valign="top">
					<s:value-of select="@targetNamespace" />
				</td>
			</tr>
		</s:if>
		
		<tr>
			<td width="20%" class="headers" align="right" valign="top" wrap="true">
				Schema Comments:
			</td>
			<td class="descriptions" align="left" bgcolor="#EEEEEE" fgcolor="#FFAA00" valign="top">
				<font color="#990000">
				<s:for-each select="./xsd:annotation ">
					<s:apply-templates mode="schemacomments" select=".">
						<s:with-param name="otherComments" select="/comment()"/>
					</s:apply-templates>
					
					<s:if test="position()!=last()"><br/> - - - - - <br/></s:if>						
				</s:for-each>
				<s:apply-templates select="/comment()"/>
				</font>
			</td>
		</tr>
		<s:if test="$root//xsd:include">
			<tr>
				<td width="20%" class="headers" align="right" valign="top" wrap="true">
					Modules Used
				</td>
				<td class="values" bgcolor="#eeeeee" align="left" valign="top">
				
					<s:for-each select="$root//xsd:include">
						<A>
							<s:attribute name="href"><s:value-of select="./@schemaLocation"/> 
							</s:attribute>
							<s:value-of select="./@schemaLocation"/> 
						</A><br/>
					
					</s:for-each>
					 
				</td>
			</tr>
		</s:if>
		<s:if test="$root//xsd:import">
			<tr>
				<td width="20%" class="headers" align="right" valign="top" wrap="true">
					Imports
				</td>
				<td class="values" bgcolor="#eeeeee" align="left" valign="top">
				
					<s:apply-templates mode="doImports" select="$root//xsd:import"/>
				</td>
			</tr>
		</s:if>
		<tr>
			<td width="20%" class="headers" align="right" valign="top" wrap="true">
				Processing Instructions
			</td>
			<td class="values" bgcolor="#eeeeee" align="left" valign="top">
				<s:apply-templates select ="$root//processing-instruction()" /> 
			</td>
		</tr>
		     
		<tr>
			<td width="20%" class="headers" align="right" valign="top" wrap="true">
				Schema has:
			</td>
			<td class="values" bgcolor="#eeeeee" align="left" valign="top">
				<s:number value="count(xsd:element) + count(xsd:complexType)" />
																		
				<a href="#TARGET_ELEMENT_LIST">element definitions</a>,&#160; 
				<s:if test="count(xsd:group)> 0" >
					<s:number value="count(xsd:group) "/>
					<a href="#TARGET_GROUP_DEFS"> group definitions</a>,&#160;
				</s:if>
				
				<s:number value="count(xsd:attribute) + count(xsd:attributeGroup)"/>
				<a href="#TARGET_GLOBAL_ATTS"> global attribute definitions</a>,&#160; 
				<s:number value="count(.//xsd:complexType//xsd:attribute)"/>
				element attribute definitions,&#160;
				<s:number value=" count(.//xsd:simpleType/@name)"/>
				<a href="#TARGET_DATATYPE_DEFS"> datatype definitions</a>.
			</td>
		</tr>

		<tr>
			<td width="20%" class="headers" align="right" valign="top" wrap="true">
				Possible root elements:
			</td>

			<td class="values" bgcolor="#eeeeee" align="left" valign="top">
				<s:call-template name="dumpPossibleRoots">
					<s:with-param name="elementRefs" select="$elementRefs" />
				</s:call-template>					
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
					<A>
						<s:attribute name="name">TARGET_ELEMENT_LIST</s:attribute>
						Element list
					</A>
				</font>
			</td>
		</tr>			
	</table>
	<p/>

	<s:apply-templates mode="elementDescriptions" select=".//xsd:element[@name]">
		<s:sort select="@name"/>
		<s:with-param name="refsList" select="$elementRefs/t:elementRefs/t:ref" />
	</s:apply-templates>

	<s:if test="xsd:complexType">
		<table width="90%" border="0" cellspacing="0" cellpadding="2" align="center">
			<tr  >
					<td bgcolor="#003366"  >
						<font face="Arial,Helvetica" size="5" color="#FFFFFF">
							
							<A>
								<s:attribute name="name">TARGET_TYPES_LIST</s:attribute>
								Complex Types
							</A>
						</font>
				</td>
			</tr>			
		</table>
		<p/>
		
		<s:apply-templates mode="elementDescriptions" select="xsd:complexType ">
			<s:sort select="@name"/>
		<s:with-param name="refsList" select="$elementRefs/t:typeRefs/t:ref" />
		</s:apply-templates>

	</s:if>

	<s:if test="./xsd:group">
		<table width="90%" border="0" cellspacing="0" cellpadding="2" align="center">
			<tr>
			<td bgcolor="#003366"  >
				<font face="Arial,Helvetica" size="5" color="#FFFFFF">
							<A>
								<s:attribute name="name">TARGET_GROUP_DEFS</s:attribute>
								Element groups
							</A>
				</font>
			</td>
			</tr>			
		</table>
		<p/>
		<s:apply-templates mode="elementGroupsMode" select="./xsd:group">
			<s:sort select="@name"/>
			<s:with-param name="refsList" select="$elementRefs/t:groupRefs/t:ref" />
		</s:apply-templates>
	</s:if>
	<P/>
	<P/>
	<s:if test="./xsd:attributeGroup | ./xsd:attribute">
		<table width="90%" border="0" cellspacing="0" cellpadding="2" align="center">
			<tr>
			<td bgcolor="#8888ff"  >
				<font face="Arial,Helvetica" size="5" color="#FFFFFF">
							<A>
								<s:attribute name="name">TARGET_GLOBAL_ATTS</s:attribute>
								Global Attributes
							</A>
							
				</font>
			</td>
			</tr>			
		</table>
		
		<s:apply-templates mode="globalAttributeMode" select="./xsd:attributeGroup | ./xsd:attribute">
			<s:sort select="@name"/> 
		</s:apply-templates>
	</s:if>

	<P/>
	<P/>
	<p/>
	<table width="90%" border="0" cellspacing="0" cellpadding="2" align="center">
		<tr  >
				<td bgcolor="#003366"  >
					<font face="Arial,Helvetica" size="5" color="#FFFFFF">
						<A>
							<s:attribute name="name">TARGET_DATATYPE_DEFS</s:attribute>
							DataType definitions
						</A>
					</font>
			</td>
		</tr>			
	</table>
	<p/>
	<s:apply-templates mode="datatypeDefs" select=".//xsd:simpleType">
		<s:sort select="@name"/> 
	</s:apply-templates>
	<p/>		<p/>		
	<P/>
	<s:if test="$doSource='true' or $doSource='TRUE'">
		<table width="90%" border="0" cellspacing="0" cellpadding="2" align="center">
			<tr>
			<td bgcolor="#bbbbbb"  >
				<font face="Arial,Helvetica" size="5" color="#FFFFFF">
						<s:if test="@targetNamespace">
								<s:value-of select="@targetNamespace"/>
						</s:if>
				</font>
				
			Original Schema Source

			</td>
			</tr>			
		</table>	
	</s:if> 
                      
</s:template>
<!--END xsd:schema***********************************************************-->

<!-- ========================================================================
-->
<s:template name="dumpPossibleRoots">
	<s:param name="elementRefs" />
	
	<!-- find all of the potential roots where there is not a reference to a node
		of that name -->
	<s:variable name="potentialRoots" select="/xsd:schema/xsd:element[not(@name=$elementRefs/t:elementRefs/t:ref/@toName)]" />
	<s:call-template name="listElements">
		<s:with-param name="elementList" select="$potentialRoots" />
		<s:with-param name="emptyMessage" select="'No obvious root elements'" />
	</s:call-template>
	
</s:template>

<!-- ========================================================================
	Assumes the current node has a @ref attribute, builds a reference node for
	what it is targeting.
-->
<s:template name="buildSingleRef">

	<s:variable name="prefix" select="substring-before(@ref, ':')" />
	<s:variable name="ns" select="string(namespace::*[name()=$prefix])" />
	
	<s:if test="$ns=$targetNamespace">
		<s:variable name="tempName" select="concat(@ref, ':', @ref, ':')" />
		<s:variable name="localPart" select="substring-before(substring-after($tempName, ':'), ':')" />
		
		<!-- get the first named ancestor -->
		<s:variable name="firstAncestor" select="ancestor::*[@name][1]" />
		<s:variable name="immediateAncestorId" select="generate-id($firstAncestor)" />
		
		<t:ref fromId="{$immediateAncestorId}" toName="{$localPart}"/>
	</s:if>
	
</s:template>

<!-- ========================================================================
	Build a temporary result of references to elements.  We use this later to
	figure out which items can be potential roots, and which items are referring
	to a particular element.
	
	The only really tricky part to this is that references could be prefixed or
	not, and we need to unravel the prefix to verify that it is for the namespace
	that we think it is for.
-->
<s:template name="buildElementRefs" >

	<t:elementRefs>
		<s:for-each select="//xsd:element[@ref]">
			<s:call-template name="buildSingleRef" />
		</s:for-each>
	</t:elementRefs>

	<!-- build the references to all the model groups -->
	<t:groupRefs>
		<s:for-each select="//xsd:group[@ref]">
			<s:call-template name="buildSingleRef" />
		</s:for-each>
	</t:groupRefs>
	
	<!-- build the references to all the attribute groups -->
	<t:attributeGroupRefs>
		<s:for-each select="//xsd:attributeGroup[@ref]">
			<s:call-template name="buildSingleRef" />
		</s:for-each>
	</t:attributeGroupRefs>
	
</s:template>

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
				
				<a class="element-link" href="{concat('#', generate-id())}">
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
========================================================================== -->		
<s:template mode="elementName" match="xsd:complexType">
	Complex Type  
</s:template>
	
<!-- ========================================================================
========================================================================== -->		
<s:template mode="elementName" match="xsd:element">
	Element 
</s:template>
	
<!-- ========================================================================
========================================================================== -->		
<s:template mode="elementName" match="xsd:group">
	Model Group 
</s:template>
	
<!-- ========================================================================
========================================================================== -->		
<s:template mode="linkPrefix" match="xsd:element | xsd:complexType | xsd:group">
	<s:if test="local-name()='complexType'">CTYPE_</s:if>
	<s:if test="local-name()='element'">ELEMENT_</s:if>
	<s:if test="local-name()='group'">GROUP_</s:if>
</s:template>
	
<!-- ========================================================================
========================================================================== -->		
<s:template mode="elementDescriptions" match="xsd:complexType | xsd:element" >
	<s:param name="refsList" />
	
	<s:variable name="pathParts" select="ancestor::*[@name] | ." />
	<s:variable name="componentPath">
		<s:apply-templates select="$pathParts" mode="computePath" />
	</s:variable>
	
	<center>
		<table width="90%" cellspacing="0" cellpadding="2" border="0" >
			<tr bgcolor="#003366">
				<td>
				
					<font face="Arial,Helvetica" color="#FFFFFF">
						<s:apply-templates mode="elementName" select="."/>
						<A>
							<s:attribute name="name">
								<s:apply-templates mode="linkPrefix" select="." />
								<s:value-of select="@name"/>
							</s:attribute>
							<b>
								<s:value-of select="@name"/>
							</b>
						</A>
						<a name="{generate-id()}" />
					
					<s:if test="@abstract='true'">
					<i> (abstract) </i>
					</s:if>
					<s:if test="@substitutionGroup">
					(substitutionGroup 
					
					<A class="element-link">
						<s:attribute name="HREF">
												#ELEMENT_<s:value-of select="@substitutionGroup"/>
						</s:attribute>
						<s:value-of select="@substitutionGroup"/>
					</A> )
					</s:if>
					<s:if test="./*/xsd:extension">
						<i> derived by extension from  </i> 
						
						<A class="element-link">
												<s:attribute name="HREF">
													#CTYPE_<s:value-of select="./*/xsd:extension/@base"/>
												</s:attribute>
												<s:value-of select="./*/xsd:extension/@base"/>
						</A> 	
					</s:if>
					<s:if test="./*/xsd:restriction">
						<i> derived by restriction from  </i> 
						
						<A class="element-link">
												<s:attribute name="HREF">
													#CTYPE_<s:value-of select="./*/xsd:restriction/@base"/>
												</s:attribute>
												<s:value-of select="./*/xsd:restriction/@base"/>
						</A> 
					</s:if>
					</font>
							 
				</td>
				<s:if test="@id">
					<td align="left">
					<font face="Arial,Helvetica" color="#FFFFFF">
							id: <s:value-of select="@id"/>
					 </font> 
					</td>	   
				</s:if>
			</tr>
		</table>
	</center>
	<table width="90%" cellspacing="0" cellpadding="0" border="0" bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF" align="center">
		<tr>
			<td width="3%" bgcolor="#4444ff" align="left" valign="middle">&#160;</td>
			<td width="97%" align="left" valign="top">
				<table width="100%" cellspacing="3" cellpadding="1" border="0" bordercolor="#FFFFFF" align="left">
				
					<!-- insert the diagram row, if appropriate -->
					<s:call-template name="insertImageRow">
						<s:with-param name="doImages" select="$doImages" />
						<s:with-param name="componentPath" select="$componentPath" />
					</s:call-template>
					
					<!-- if this is not a root element, then display the full element path -->
					<s:if test="not(parent::xsd:schema)">
						<tr>
							<td class="tdnames">path</td>
							<td width="85%" class="values" bgcolor="#eeeeeeee">
								<s:apply-templates mode="generateComponentPath" select="." />
							</td>
						</tr>
					</s:if>
					
					<tr>
						<td class="tdnames">description</td>
						<td width="85%" class="descriptions"  align="left" bgcolor="#eeeeeeee" wrap="true">
							<font color="#990000">
							<s:apply-templates mode="schemacomments" select="./xsd:annotation | ./xsd:complexType/xsd:annotation | ./xsd:simpleType/xsd:annotation">
								<s:with-param name="otherComments" select="comment()"/>
							</s:apply-templates>
							</font>
						</td>
					</tr>

					<s:call-template name="generateUsesRows" />
					
				</table>
			</td>
		</tr>
	</table>
	<table width="90%" cellspacing="0" cellpadding="0" border="0" bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF" align="center">
		<tr>
			<td width="3%" bgcolor="#6666ff" align="center" valign="top">&#160;</td>
			<td width="97%" align="center" valign="left">
				<table width="100%" cellspacing="3" cellpadding="1" border="0" align="left" valign="top" bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF">
					<!-- USED BY rows -->
					<s:call-template name="dumpElementOrComplexTypeUsedBy">
						<s:with-param name="refsList" select="$refsList" />
					</s:call-template>
					
					<s:if	test=".//xsd:extension | .//xsd:restriction">
						<TR>
							<td class="tdnames">substitution hierarchy</td>
							<TD class="values" bgcolor="#eeeeee" align="left" valign="top">
								<s:apply-templates mode="inheritance" select="." >
									<s:with-param name="elementName" select=".//xsd:extension/@base | .//xsd:restriction/@base " />
								</s:apply-templates>  
	
							</TD>
						</TR>
					</s:if>
					
					<s:variable name="ourName" select="@name" />

					<s:choose>
					<s:when test="@abstract='true'">
						<s:apply-templates mode="doSubTypes" select="." />
					</s:when>
					<s:when test="$subctypes[./@base=$ourName]">
						<s:apply-templates mode="doSubTypes" select="." />
					</s:when>
					
					</s:choose>
					<tr>
						<td class="tdnames">content</td>
						<td width="85%" class="values" bgcolor="#eeeeeeee">
								<s:apply-templates mode="contentModel" select="."/>
						</td>
					</tr>
	
					<s:if test="@type">
						<tr>
							<td width="15%" bgcolor="#CCCCCC" align="right" valign="top">
								<span class="names">type</span>
							</td>
							<td width="85%"  class="values" bgcolor="#eeeeee" >
							
								<s:variable name="typePrefix" select="substring-before(@type , ':')" />
								<s:variable name="typeNs" select="string(namespace::*[name()=$typePrefix])"/>
								
								<s:variable name="tempLocal" select="concat(@type, ':', @type, ':')" />
								<s:variable name="local" select="substring-before(substring-after($tempLocal, ':'), ':')" />
							
							 <s:choose> 
								<s:when test="$typeNs = $xsdNamespace">
									<A><s:attribute name="href"><s:value-of select="$specRoot" />2/#<s:value-of select="$local"/></s:attribute>
										<s:value-of select="@type"/></A>
								</s:when>
								<s:when test="$typeNs = $targetNamespace">
									<s:variable name="matchType" select="/xsd:schema/xsd:complexType[@name=$local] | /xsd:schema/xsd:simpleType[@name=$local]" />
									<s:choose>
										<s:when test="$matchType">
											<a href="{concat('#', generate-id($matchType))}">
												<s:value-of select="@type"/>
											</a>
										</s:when>
										<s:otherwise>
											<s:value-of select="@type"/>
										</s:otherwise>
									</s:choose>
								</s:when>
								<s:otherwise>
									<s:value-of select="@type"/>
								</s:otherwise>
							 </s:choose> 
							 
							</td>
						</tr>
					</s:if>
										 
				</table>
			</td>
		</tr>
	</table>
	<s:if test=".//xsd:attribute">
		<table width="90%" cellspacing="0" cellpadding="0" border="0"  bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF" align="center">
			<tr>
				<td width="3%" bgcolor="#8888ff" align="left" valign="top">&#160;</td>
				<td width="97%" align="left" valign="top">
					<table width="100%" cellspacing="3" cellpadding="1" border="0"  align="left" valign="top" bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF">
						<s:call-template name="detailAttributeHeader" />
						<s:apply-templates mode="detailAttrib" select=".//xsd:attribute"/>
					</table>
					</td>
				</tr>
			</table>
	</s:if>
	<s:if test="./xsd:key">
		<table width="90%" cellspacing="0" cellpadding="0" border="0"  bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF" align="center">
			<tr>
				<td width="3%" bgcolor="#8888ff" align="left" valign="top">&#160;</td>
				<td width="97%" align="left" valign="top">
					<table width="100%" cellspacing="3" cellpadding="1" border="0"  align="left" valign="top" bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF">
						<tr>
							<th width="15%" class="headers" align="right" valign="top">
								Identity Constraints
							</th>

							<th width="10%" class="headers" align="left" >
								Name
							</th>
							<th width="10%" class="headers" align="left" >
								Selector
							</th>
							<th width="25%" class="headers" align="left" >
								Fields
							</th>
							<th width="40%" class="headers" align="left" >
								&#160;
							</th>
							
						</tr>
						<s:apply-templates mode="detailConstraints" select="./xsd:key | ./xsd:keyref | ./xsd:unique"/>
					</table>
					</td>
				</tr>
			</table>
	</s:if>
							
	<s:if test="$doSource='true' or $doSource='TRUE'">
		<table width="90%" cellspacing="0" cellpadding="0" border="0"  bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF" align="center">
			<tr>
				<td width="3%" bgcolor="#bbbbbb" align="left" valign="top">&#160;</td>
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
	Block the contents of annotations from showing up in the uses list.
-->
<s:template match="xsd:annotation" mode="usesList">
</s:template>

<!-- ========================================================================
	Dump contents of a reference
-->
<s:template match="@*" mode="dumpUsesRef">
	<s:param name="type" />

	<s:variable name="prefix" select="substring-before(. , ':')" />
	<s:variable name="ns" select="string(namespace::*[name()=$prefix])"/>
	
	<s:variable name="tempLocal" select="concat(., ':', ., ':')" />
	<s:variable name="local" select="substring-before(substring-after($tempLocal, ':'), ':')" />

	<!-- due to weirdnesses with namespaces and the way that the xalan nodeset
	  function works, the prefixes of the use element must be defined when the
		element is defined - otherwise they end up in the wrong namespace -->	
	<s:choose>
		<s:when test="$ns=$targetNamespace">
			<t:use type="{$type}" name="{$local}" local="true">
				<s:value-of select="concat($type, ':', $local, ':', $ns)" />
			</t:use>
		</s:when>
		<s:otherwise>
			<t:use type="{$type}" name="{$local}" prefix="{$prefix}">
				<s:value-of select="concat($type, ':', $local, ':', $ns)" />
			</t:use>
		</s:otherwise>
	</s:choose>
	
</s:template>

<!-- ========================================================================
	Dump out the use of a model group.
-->
<s:template match="xsd:group" mode="usesList">
	<s:choose>
		<s:when test="@ref">
			<s:apply-templates select="@ref" mode="dumpUsesRef" >
				<s:with-param name="type" select="'mg'" />
			</s:apply-templates>
		</s:when>
		<s:otherwise>
			<!-- recurse over the contents of the group -->
			<s:apply-templates select="*" mode="usesList" />
		</s:otherwise>
	</s:choose>
</s:template>

<!-- ========================================================================
	Add a reference to an attribute.
-->
<s:template match="xsd:attribute" mode="usesList">
	<s:choose>
		<s:when test="@ref">
			<s:apply-templates select="@ref" mode="dumpUsesRef">
				<s:with-param name="type" select="'a'" />
			</s:apply-templates>
		</s:when>
		<s:otherwise>
			<t:use type="a" name="{@name}" id="{generate-id()}">la:<s:value-of select="@name" /></t:use>
		</s:otherwise>
	</s:choose>
</s:template>

<!-- ========================================================================
	Add a reference to an attribute group.
-->
<s:template match="xsd:attributeGroup" mode="usesList">
	
	<!-- since attribute groups cannot be anonymous or local, when we encounter
		one while building a uses list, it must be reference -->
	<s:apply-templates select="@ref" mode="dumpUsesRef">
		<s:with-param name="type" select="'ag'" />
	</s:apply-templates>

</s:template>

<!-- ========================================================================
	For all the cases besides the ones that we want to capture explicitly.
-->
<s:template match="xsd:*" mode="usesList">
	<s:apply-templates select="xsd:*" mode="usesList" />
</s:template>

<!-- ========================================================================
	Dump out a "use" of the form <use type='e' name='lclName'>e:prefix:lclName:ns</use> or
	<use type='e' name='lclName'>le:lclName</use>
	
	Note that the latter form is used for local elements, which can only be local
	within their parent.  This takes care of the case of repeated local elements.
-->
<s:template match="xsd:element" mode="usesList">

	<s:choose>
		<s:when test="@ref">
			<s:apply-templates select="@ref" mode="dumpUsesRef">
				<s:with-param name="type" select="'e'" />
			</s:apply-templates>
		</s:when>
		<s:otherwise>
			<t:use type="e" name="{@name}" id="{generate-id()}">le:<s:value-of select="@name" /></t:use>
		</s:otherwise>
	</s:choose>
</s:template>

<!-- ========================================================================
	Generate the uses list
-->
<s:template name="generateUsesList" >
	<t:useList>
		<s:apply-templates mode="usesList" select="*" />
	</t:useList>
	
</s:template>

<!-- ========================================================================
-->
<s:template name="outGlobalRefFromNode">
	<s:param name="node" />
	
	<s:choose>
		<s:when test="$node">
			<a href="{concat('#', generate-id($node))}">
				<s:value-of select="@name" />
			</a>
		</s:when>
		<s:otherwise>
			<s:value-of select="@name" />
		</s:otherwise>
	</s:choose>
	
</s:template>

<!-- ========================================================================
-->
<s:template mode="outGlobalRef" match="t:use" >
	<s:param name="schema" />

	<s:variable name="name" select="@name" />
	
	<s:choose>
		<s:when test="starts-with(., 'e:')" >
	
			<s:call-template name="outGlobalRefFromNode">
				<s:with-param name="node" select="$schema/xsd:element[@name=$name]" />
			</s:call-template>
		</s:when>
		<s:when test="starts-with(., 'ag:')" >
			<s:call-template name="outGlobalRefFromNode">
				<s:with-param name="node" select="$schema/xsd:attributeGroup[@name=$name]" />
			</s:call-template>
		</s:when>
		<s:when test="starts-with(., 'mg:')" >
			<s:call-template name="outGlobalRefFromNode">
				<s:with-param name="node" select="$schema/xsd:group[@name=$name]" />
			</s:call-template>
		</s:when>
		<s:when test="starts-with(., 'a:')" >
			<s:call-template name="outGlobalRefFromNode">
				<s:with-param name="node" select="$schema/xsd:attribute[@name=$name]" />
			</s:call-template>
		</s:when>
	</s:choose>
			
</s:template>

<!-- ========================================================================
	Generate a particular uses row
-->
<s:template name="generateParticularUsesRow">

	<s:param name="rowName" />
	<s:param name="usesList" />
	<s:variable name="schema" select="/xsd:schema" />
	
	<s:if test="$usesList">
		<tr>
			<td class="tdnames"><s:value-of select="$rowName" /></td>
			<td width="85%" class="values" bgcolor="#eeeeee">
				<s:for-each select="$usesList">
					<s:sort select="@name" />
					
					<s:variable name="id" select="@id" />
					<s:variable name="name" select="@name" />
					<s:variable name="local" select="@local" />
					
					<s:choose>
						<s:when test="$id">
							<!-- this weirdness appears to be due to the "key" function being
								bound to the document of the context node.  To change the
								context node, simply execute a 'for-each' on the schema node -->
							<s:for-each select="$schema">
								<a href="{concat('#', $id)}">
									<s:apply-templates select="key('gid-key', $id)" mode="generateComponentPath"/>
								</a>
							</s:for-each>
						</s:when>
						<s:when test="@local">
							<s:apply-templates select="." mode="outGlobalRef">
								<s:with-param name="schema" select="$schema" />
							</s:apply-templates>
						</s:when>
						<s:otherwise>
							<s:value-of select="concat(@prefix, ':', @name)" />
						</s:otherwise>
					</s:choose>
					
					<s:if test="position() != last()" >
						<s:text>, </s:text>
					</s:if>
				</s:for-each>
			</td>
		</tr>
	</s:if>
</s:template>

<!-- ========================================================================
	Generate the "uses" rows
-->
<s:template name="generateUsesRows">

	<s:variable name="usesListWithDupsTree"><s:call-template name="generateUsesList" /></s:variable>
	<s:variable name="usesListWithDups" select="xalan:nodeset($usesListWithDupsTree)" />
	
	<!-- get the list of distinct uses -->
	<s:variable name="usesNoDups" select="xalan:distinct($usesListWithDups/t:useList/t:use)" />
	
	<s:call-template name="generateParticularUsesRow">
		<s:with-param name="rowName" select="'uses elements'" />
		<s:with-param name="usesList" select="$usesNoDups[@type='e']" />
	</s:call-template>
	
	<s:call-template name="generateParticularUsesRow">
		<s:with-param name="rowName" select="'uses attributes'" />
		<s:with-param name="usesList" select="$usesNoDups[@type='a']" />
	</s:call-template>
	
	<s:call-template name="generateParticularUsesRow">
		<s:with-param name="rowName" select="'uses model groups'" />
		<s:with-param name="usesList" select="$usesNoDups[@type='mg']" />
	</s:call-template>
	
	<s:call-template name="generateParticularUsesRow">
		<s:with-param name="rowName" select="'uses attribute groups'" />
		<s:with-param name="usesList" select="$usesNoDups[@type='ag']" />
	</s:call-template>
	
</s:template>

<!-- ========================================================================
	Generate the item path
-->
<s:template mode="generateComponentPath"
	match="xsd:simpleType | xsd:complexType | xsd:group | xsd:attributeGroup" >

	<s:value-of select="@name" />
</s:template>

<!-- ========================================================================
	Generate the node path
-->
<s:template mode="generateComponentPath" match="xsd:element | xsd:attribute" >

	<!-- apply-templates will traverse the ancestors in document order, which
		is what we want -->
	<s:apply-templates mode="buildLocalPath" select="ancestor::*[@name] | ."/>
</s:template>

<!-- ========================================================================
	Dump the "used by" list for an element
-->
<s:template name="dumpElementOrComplexTypeUsedBy">
	<s:param name="refsList" />
	
	<s:choose>
		<!-- is this a global element or complex type? -->
		<s:when test="parent::xsd:schema">
		
			<!-- yes - look for its references -->
			<s:call-template name="dumpComponentUsedBy">
				<s:with-param name="refsList" select="$refsList" />
			</s:call-template>
		</s:when>
		
		<s:otherwise>
			<!-- local element - used by its parent -->
			<s:variable name="parent" select="ancestor::*[@name][1]" />
			<s:call-template name="dumpUsedByFromNodes">
				<s:with-param name="usingNodes" select="$parent" />
			</s:call-template>
		</s:otherwise>
	</s:choose>
</s:template>

<!-- ========================================================================
	Dump the "used by" list for an element
-->
<s:template name="dumpUsedByFromNodes">
	<s:param name="usingNodes" />
	
	<s:call-template name="dumpUsedByRow">
		<s:with-param name="title" select="'used by elements'" />
		<s:with-param name="nodes" select="$usingNodes/self::xsd:element" />
	</s:call-template>
	
	<s:call-template name="dumpUsedByRow">
		<s:with-param name="title" select="'used by model groups'" />
		<s:with-param name="nodes" select="$usingNodes/self::xsd:group" />
	</s:call-template>
	
	<s:call-template name="dumpUsedByRow">
		<s:with-param name="title" select="'used by type'" />
		<s:with-param name="nodes" select="$usingNodes/self::xsd:complexType" />
	</s:call-template>
	
</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template mode="elementGroupsMode" match="xsd:group" >

	<s:param name="refsList" />
				
	<center>
		<table width="90%" cellspacing="0" cellpadding="2" border="0" >
			<tr bgcolor="#4444FF">
				<td>
					<font face="Arial,Helvetica" color="#FFFFFF">
						<s:apply-templates mode="elementName" select="."/>
						<a name="{concat('#', generate-id())}" />
						<A>
							<s:attribute name="name">
								<s:apply-templates mode="linkPrefix" select="." />
								<s:value-of select="@name"/>
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
			<td width="3%" bgcolor="#4444FF" align="left" valign="middle">&#160;</td>
			<td width="97%" align="left" valign="top">
				<table width="100%" cellspacing="3" cellpadding="1" border="0" bordercolor="#FFFFFF" align="left">

					<s:call-template name="insertImageRow">
						<s:with-param name="doImages" select="$doImages" />
						<s:with-param name="componentPath" select="concat('mg:', @name)" />
					</s:call-template>
					<s:if test=".//xsd:annotation ">
						<tr>
							<td class="tdnames">description</td>
							<td width="85%" class="descriptions"  align="left" bgcolor="#eeeeeeee" wrap="true">
								<font color="#990000">
								<s:apply-templates mode="schemacomments" select=".//xsd:annotation "/></font>
							</td>
						</tr>
					</s:if>
					
					<s:call-template name="generateUsesRows" />
						<tr>
							<td class="tdnames">content model</td>
							<td width="85%"  class="values" bgcolor="#eeeeeeee">
								
								<s:apply-templates mode="contentModel" select="."/>
							</td>
						</tr>
						
						<!-- dump the list of used-by rows. -->
						<s:call-template name="dumpComponentUsedBy">
							<s:with-param name="refsList" select="$refsList" />
						</s:call-template>
					</table>
				</td>
			</tr>
		</table>
		<s:if test="$doSource = 'true'">
			<table width="90%" cellspacing="0" cellpadding="0" border="0"  bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF" align="center">
				<tr>
					<td width="3%" bgcolor="#bbbbbb" align="left" valign="top">&#160;</td>
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
========================================================================== -->		
<s:template mode="detailConstraints" match="xsd:key | xsd:keyref | xsd:unique">
	<tr>
		<td width="15%" class="headers" align="right" valign="top">
			<b>
				<s:value-of select="name(.)"/>
			</b>
		</td>
		<td width="10%" class="values" bgcolor="#eeeeee" align="left" valign="top">
		
				<s:value-of select="./@name"/>
					
		</td>
		<td width="10%" class="values" bgcolor="#eeeeee" align="left" valign="top">
			 <s:if test="./xsd:selector">
				<s:value-of select="./xsd:selector"/>
					 </s:if>
		</td>
		<td width="25%" class="values" bgcolor="#eeeeee" align="left" valign="top">
				<s:if test="./xsd:field">
					<s:for-each select="./xsd:field">
					<s:value-of select="."/>
				</s:for-each>
						</s:if>
		</td>
		<td width="40%" class="values" bgcolor="#eeeeee" align="left" valign="top">
		&#160; </td>		
	</tr>	
</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template match="*" mode="constructProperLink">
	<s:variable name="prefix" select="substring-before(./@type,':')"/>
	<s:variable name="ns-uri" select="string(namespace::*[name()=$prefix])"/>
	{
	<A>
		<s:attribute name="href">http://www.w3.org/TR/xmlschema-2/#<s:value-of select="./@type"/></s:attribute>
		<s:value-of select="./@type"/>
	</A>
	}											 
</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template mode="detailAttrib" match="xsd:attribute">
	<tr>
		<td width="15%" class="headers" align="right" valign="top">
			<b>
				<s:value-of select="@name"/>
			</b>
		</td>			

		<td width="10%" class="values" bgcolor="#eeeeee" align="left" valign="top">
			
			<s:if test="@type">
				<A ><s:attribute name="href">http://www.w3.org/TR/xmlschema-2/#<s:value-of select="@type"/></s:attribute>
					<s:value-of select="@type"/>
				</A>
			</s:if>
			
			<s:if test="./xsd:simpleType">
				<s:choose>
					<s:when test="contains(@type,'xsd:')">
						{<A><s:attribute name="HREF">http://www.w3.org/TR/xmlschema-2/#<s:value-of select="@type"/></s:attribute><s:value-of select="@type"/></A>}
					</s:when>
					<s:otherwise>
						{<A><s:attribute name="HREF">#<s:value-of select="@type"/></s:attribute><s:value-of select="@type"/></A>}
					</s:otherwise>
				</s:choose>        	         
			</s:if>
		</td>
		<td width="15%" class="values" bgcolor="#eeeeee" align="left" valign="top">
		<s:choose>
			<s:when test="@use">
				<s:value-of select="@use"/>
			</s:when>
			<s:otherwise>
				optional (default)
			</s:otherwise>
		</s:choose>
		</td>
		<td width="20%" class="values" bgcolor="#eeeeee" align="left" valign="top">
			<s:apply-templates mode="doShortDTFacets" select="."/>
		</td>
	
		<td width="20%" class="values" bgcolor="#eeeeee" align="left" valign="top">

				<s:value-of select="@value"/><s:value-of select="@default"/> 
		</td>
		<td width="20%" class="values" bgcolor="#eeeeee" align="left" valign="top">

				<s:apply-templates mode="schemacomments" select="./xsd:annotation"/>
		</td>


	</tr>
</s:template>
	
<!-- ========================================================================
========================================================================== -->		
<s:template mode="globalAttributeMode" match="xsd:attributeGroup | xsd:attribute">
<p/>
	<center>
		<table width="90%" cellspacing="0" cellpadding="2" border="0" >
			<tr bgcolor="#8888ff">
				<s:if test="name(.)='attributeGroup'">
					<td>
						<font face="Arial,Helvetica" color="#FFFFFF">
							Attribute Group: 
							<A>
								<s:attribute name="name">ATTRIBUTEGROUP_<s:value-of select="@name"/>
								</s:attribute>
								<b>
									<s:value-of select="@name"/>
								</b>
							</A>
						</font>
					</td>
				</s:if>
				<s:if test="name(.)='attribute'">
					<td>
						<font face="Arial,Helvetica" color="#FFFFFF">
							Attribute: 
							<A>
								<s:attribute name="name">ATTRIBUTE_<s:value-of select="@name"/>
								</s:attribute>
								<b>
									<s:value-of select="@name"/>
								</b>
							</A>
						</font>
					</td>
				</s:if>
			</tr>
		</table>
	</center>


	<table width="90%" cellspacing="0" cellpadding="0" border="0"  bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF" align="center">
			<tr>
				<td width="3%" bgcolor="#8888ff" align="left" valign="top">&#160;</td>
				<td width="97%" align="left" valign="top">
					<table width="100%" cellspacing="3" cellpadding="1" border="0"  align="left" valign="top" bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF">
						<s:call-template name="detailAttributeHeader" />
						<s:apply-templates mode="detailAttrib" select=".//xsd:attribute | ."/>
					</table>
				</td>
			</tr>
		</table>
</s:template>
	<!-- END detailattribute TYPE -->	
	
<!-- ========================================================================
========================================================================== -->		
<s:template mode="datatypeDefs" match="xsd:simpleType">
	 <s:if test="@name">	
	 <p/>
	<center>
		<table width="90%" cellspacing="0" cellpadding="2" border="0" >
			<tr bgcolor="#AAAAff">
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
						<a name="{generate-id()}" />
					</font>
					<s:if test="@abstract='true'">
					<font face="Arial,Helvetica" color="#FFFFFF">
						&#160;&#160; <i> abstract  </i>&#160;&#160;
					 </font>   
					</s:if>
					&#160;
					<!-- describe definition type -->
					<s:if test="./xsd:restriction">
						defined by restriction
					</s:if>
					<s:if test="./xsd:union">
						defined by union
					</s:if>
					<s:if test="./xsd:list">
						defined by list
					</s:if>
				</td> 
				<s:if test="@id">
					<td align="left">
					<font face="Arial,Helvetica" color="#FFFFFF">
							id: <s:value-of select="@id"/>
					 </font> 
					</td>	   
				</s:if>
				
					
			</tr>
		</table>
	<table width="90%" cellspacing="0" cellpadding="0" border="0" bordercolor="#FFFFFF" bordercolordark="#FFFFFF" bordercolorlight="#FFFFFF" align="center">
		<tr>
			<td width="3%" bgcolor="#AAAAFF" align="left" valign="middle">&#160;</td>
			<td width="97%" align="left" valign="top">
		
							 <table width="100%" cellspacing="3" cellpadding="1" border="0" bordercolor="#FFFFFF" align="left">

					<s:if test="./xsd:annotation">
						<tr>
							<td class="tdnames">description</td>
							<td width="85%" class="descriptions"  align="left" bgcolor="#eeeeeeee" wrap="true">
								<font color="#990000">
								<s:apply-templates mode="schemacomments" select="./xsd:annotation"/></font>
							</td>
						</tr>
					</s:if>
					<s:if test=".//xsd:restriction/@base">
						<tr>
							<td class="tdnames">dt base</td>
							<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
									<s:value-of select=".//xsd:restriction/@base" />
							</td>
						</tr>
					</s:if>
					<s:if test=".//xsd:minExclusive | .//xsd:minInclusive | .//xsd:maxExclusive | .//xsd:maxInclusive ">
						<tr>
							<td class="tdnames">range</td>
							<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
									<s:apply-templates mode="doRange" select="."/>
							</td>
						</tr>
					</s:if>
					<s:if test=".//xsd:precision">
						<tr>
							<td class="tdnames">precision</td>
							<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
									<s:value-of select=".//xsd:precision/@value" />
							</td>
						</tr>
					</s:if>

					
					<s:if test=".//xsd:scale">
						<tr>
							<td class="tdnames">scale</td>
							<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
									<s:value-of select=".//xsd:scale/@value" />
							</td>
						</tr>
					</s:if>
					<s:if test=".//xsd:encoding">
						<tr>
							<td class="tdnames">encoding</td>
							<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
									<s:value-of select=".//xsd:encoding/@value" />
							</td>
						</tr>
					</s:if>
					<s:if test=".//xsd:length">
						<tr>
							<td class="tdnames">length</td>
							<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
									<s:value-of select=".//xsd:length/@value" />
							</td>
						</tr>
					</s:if>
					<s:if test=".//xsd:duration">
						<tr>
							<td class="tdnames">duration</td>
							<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
									<s:value-of select=".//xsd:duration/@value" />
							</td>
						</tr>
					</s:if>
					<s:if test=".//xsd:period">
						<tr>
							<td class="tdnames">period</td>
							<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
									<s:value-of select=".//xsd:period/@value" />
							</td>
						</tr>
					</s:if>
					<s:if test=".//xsd:pattern">
						<tr>
							<td class="tdnames">pattern</td>
							<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
									<s:value-of select=".//xsd:pattern/@value" />
							</td>
						</tr>
					</s:if>
					<s:if test=".//xsd:enumeration">
						<tr>
							<td class="tdnames">possible values</td>
							<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
								 <s:for-each select=".//xsd:enumeration" >
									<s:value-of select="@value" /> 
									 <s:if test="position()!=last()"> |  </s:if>  
								 </s:for-each> 
							</td>
						</tr>
					</s:if>

					<s:if test=".//xsd:has-facet">
						<tr>
							<td class="tdnames">facets</td>
							<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
								<s:for-each select=".//xsd:has-facet">
										
										<A class="element-link">
											<s:attribute name="HREF"><s:value-of select="$specRoot"/>-2/#dc-<s:value-of select="@name"/>
											</s:attribute>
											<s:value-of select="@name"/>
											
										</A>
									<s:if test="position()!=last()">, </s:if>
								</s:for-each>
								 
							</td>
						</tr>
					</s:if>

					<s:if test=".//xsd:has-property">
						<tr>
							<td class="tdnames">properties</td>
							<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
								<s:for-each select=".//xsd:has-property">
									<A class="element-link">
											<s:attribute name="HREF"><s:value-of select="$specRoot"/>-2/#dt-<s:value-of select="@name"/>
											</s:attribute>
											<s:value-of select="@name"/>
									</A>=<s:value-of select="./@value" />
									<s:if test="position()!=last()">;</s:if>
								</s:for-each>
								 
							</td>
						</tr>
					</s:if>
					<s:if test="./xsd:list/@itemType">  <!-- list by item type attribute -->
						
						<tr>
							<td class="tdnames">item type</td>
							<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
								 <s:value-of select="./xsd:list/@itemType"/>
							</td>
						</tr>
						
					</s:if> 
					<s:if test="./xsd:list/simpleType">  <!-- list by item type -->
						
						<tr>
							<td class="tdnames">list of</td>
							<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
								 <s:value-of select="./xsd:list/xsd:simpleType/@name"/>
							</td>
						</tr>
						
					</s:if> 
					<s:if test="./xsd:union/@memberTypes">  <!-- list by item type -->
						
						<tr>
							<td class="tdnames">member of type</td>
							<td width="85%" class="values"  align="left" bgcolor="#eeeeeeee" wrap="true">
										<s:value-of select="./xsd:union/@memberTypes"/>
							</td>
						</tr>
						
					</s:if> 
					<!-- list by item type -->
					
						</table>
					</td>
				</tr>
			</table>


		</center>
	</s:if>	
</s:template>
	
<!-- ========================================================================
========================================================================== -->		
<s:template match="*" mode="doShortDTFacets">

	<s:if test=".//xsd:minExclusive | .//xsd:minInclusive | .//xsd:maxExclusive | .//xsd:maxInclusive ">							
				range:
					<s:apply-templates mode="doRange" select="."/>
	</s:if>
	<s:if test=".//xsd:precision">
		precision:
					<s:value-of select=".//xsd:precision/@value" />
	</s:if>
	<s:if test=".//xsd:scale">
		scale:<s:value-of select=".//xsd:scale/@value" />	
	</s:if>
	<s:if test=".//xsd:encoding">
		encoding:<s:value-of select=".//xsd:encoding/@value" />
			
	</s:if>
	<s:if test=".//xsd:length">
		length:<s:value-of select=".//xsd:length/@value" />
		
	</s:if>
	<s:if test=".//xsd:duration">
		duration:<s:value-of select=".//xsd:duration/@value" />
			
	</s:if>
	<s:if test=".//xsd:period">
		period:<s:value-of select=".//xsd:period/@value" />
			
	</s:if>
	<s:if test=".//xsd:pattern">
		pattern:<s:value-of select=".//xsd:pattern/@value" />
			
	</s:if>
	<s:if test=".//xsd:enumeration">
		
		possible values:
			 <s:for-each select=".//xsd:enumeration" >
					<s:value-of select="@value" /> 
					 <s:if test="position()!=last()"> |  </s:if>  
				 </s:for-each> 
			
	</s:if>

</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template mode="contentModel" match="xsd:element |xsd:complexType | xsd:group">
	<s:choose> 
	<s:when test=".//xsd:sequence | ./xsd:sequence">
			sequence
	</s:when> 
	<s:when test=".//xsd:choice">
			choice
			
	</s:when> 
	<s:when test=".//xsd:all">
			all
	</s:when> 
	<s:when test=".//xsd:group">
			group
	</s:when> 
	<!-- if we are here we have a complexContent with none of the above. -->
	<s:when test="./xsd:complexContent">
			empty <i> (default for a complex content) </i>
	</s:when> 
	<!-- .-->
	<s:when test="./xsd:simpleContent">
			simple content
	</s:when> 

	<s:otherwise>
		sequence <i> (default) </i>
	</s:otherwise>
	</s:choose>

</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template mode="contentModel" match="xsd:type">
	<s:value-of select="@content"/>
</s:template>
		
<!-- ========================================================================
========================================================================== -->		
<s:template mode="order" match="xsd:group">
	<s:value-of select="@order"/>
</s:template>
	
<!-- ========================================================================
========================================================================== -->		
<s:template match="*" mode="inheritance">
	<s:param name="spaces" />
	<s:param name="elementName"/>		

	<s:variable name="newSpaces">
		<s:value-of select="$spaces" />&#160;&#160;&#160;&#160;&#160; 
	</s:variable>
<!--		we have:<s:value-of select="name(.)"/>::: <s:value-of select="./@name"/> ::::<s:value-of select="$elementName"/> <p/>
-->	<!-- look for a either a restriction or an extension and calll recursively, otherwise type out name-->
	<s:choose>
		<!-- only check at the top levels for the exzistatnce of a parent -->
		<s:when test="./xsd:complexType/xsd:complexContent/xsd:extension/@base | ./xsd:complexContent/xsd:extension/@base">

			<!-- get parent name -->
			<s:variable name="parentName"  select="./xsd:complexType/xsd:complexContent/xsd:extension/@base  | ./xsd:complexContent/xsd:extension/@base" />
	
			<s:choose>
				<s:when test="$complexTypes[./@name=$parentName]">
						
					<s:variable name="theParent" select="$complexTypes[./@name=$parentName]"/>
	
					<s:apply-templates mode="inheritance" select="$theParent">
						<s:with-param name="elementName" select="$parentName" />
						<s:with-param name="spaces" select="$newSpaces" />
					</s:apply-templates>  -->
				</s:when>
				<s:otherwise>
					<A class="element-link">
							<s:attribute name="HREF">
									#CTYPE_<s:value-of select="$parentName"/>
							</s:attribute>
								<s:value-of select="$parentName"/>
					</A>-->&#160;
				</s:otherwise>
			</s:choose>
		</s:when>	
		<!-- same as above , now for restriction -->
		<s:when test="./xsd:complexType/xsd:complexContent/xsd:restriction/@base  | ./xsd:complexContent/xsd:restriction/@base">
			<s:variable name="parentName2" select="./xsd:complexType/xsd:complexContent/xsd:restriction/@base  | ./xsd:complexContent/xsd:restriction/@base" />				
			<s:variable name="theParent2" select="$complexTypes[./@name=$parentName2]"/>
			<s:choose>
				<s:when test="$complexTypes[./@name=$parentName2]">							
					<s:variable name="theParent3" select="$complexTypes[./@name=$parentName2]"/>
					
					<s:apply-templates mode="inheritance" select="$theParent3">
						<s:with-param name="elementName" select="$parentName2" />
						<s:with-param name="spaces" select="$newSpaces" />
					</s:apply-templates> --> 
				</s:when>
				<s:otherwise>
					<A class="element-link">
							<s:attribute name="HREF">
									#CTYPE_<s:value-of select="$parentName2"/>
							</s:attribute>
							<s:value-of select="$parentName2"/>
							<br/> <s:value-of select="$spaces" /> -->
					</A> 
				</s:otherwise>
			</s:choose>
		</s:when>	
	</s:choose>
	<!--	<s:value-of select="$elementName"/> -->
	

	<A class="element-link">
			<s:attribute name="HREF">
					#CTYPE_<s:value-of select="./@name"/>
			</s:attribute>
			<s:value-of select="./@name"/>
	</A> 
</s:template>
	
<!-- ========================================================================
========================================================================== -->		
<s:template match="*" mode="doSubTypes">
					
	<s:variable name="ourName" select="@name" />
	<TR>
		<td class="tdnames">
				<s:if test="name(.)='complexType'">extended by</s:if>
				<s:if test="name(.)='element'">substituded by</s:if>
		</td>
		<TD class="values" bgcolor="#eeeeee" align="left" valign="top">
			
			
			<s:choose>
				<s:when           test="$elements[@substitutionGroup =  $ourName]  | $complexTypes[@substitutionGroup =  $ourName ]  ">	 
						<s:for-each select="$elements[@substitutionGroup =  $ourName ]  | $complexTypes[@substitutionGroup =  $ourName]  ">	 
						<s:sort select="@name"/>
						<A class="element-link">
							<s:attribute name="HREF">#<s:apply-templates mode="linkPrefix" select="." /><s:value-of select="@name"/>
							</s:attribute>
							<s:value-of select="@name"/>
						</A>
						&#160;
						<s:if test="position()!=last()">,</s:if>
					</s:for-each>
				</s:when>
				<s:when           test=    "$complexTypes[.//xsd:restriction/@base = $ourName] | $complexTypes[.//xsd:extension/@base = $ourName] | $elements[.//xsd:restriction/@base = $ourName] | $elements[.//xsd:extension/@base = $ourName]">	 											
						<s:for-each select="$complexTypes[.//xsd:restriction/@base = $ourName] | $complexTypes[.//xsd:extension/@base = $ourName] | $elements[.//xsd:restriction/@base = $ourName] | $elements[.//xsd:extension/@base = $ourName]">	 
					
						
						<A class="element-link">
							<s:attribute name="HREF">#<s:apply-templates mode="linkPrefix" select="." /><s:value-of select="@name"/>
							</s:attribute>
							<s:value-of select="@name"/>
						</A>
						&#160;
						<s:if test="position()!=last()">,</s:if>  
					</s:for-each>  
				</s:when>
		
				<s:otherwise>
					None
					<s:choose>
						<s:when test=".//xsd:element">
							<SPAN class="note">Not implemented.</SPAN>
						</s:when>
						<s:otherwise>
							<SPAN class="note">Not implemented.</SPAN>
						</s:otherwise>
					</s:choose> 
				</s:otherwise>
			</s:choose>
		</TD>
	</TR>
</s:template>
	
<!-- ========================================================================
========================================================================== -->		
<s:template match="*" mode="doAnnotation">
	<s:choose>
		<s:when test="./@minOccurs='0' and not(./@maxOccurs)" >
			?
		</s:when>
		<s:when test="./@minOccurs='1' and ./@maxOccurs='unbounded'" >
			+
		</s:when>
		<s:when test="./@minOccurs='0' and ./@maxOccurs='unbounded'" >
			*
		</s:when>
		<s:when test="./@minOccurs | ./@maxOccurs">
			(<s:choose>
				<s:when test="./@minOccurs">
					<s:value-of select="@minOccurs"/>
				</s:when>
				<s:otherwise>0</s:otherwise>
			</s:choose>,
			<s:choose>
				<s:when test="./@maxOccurs">
					<s:value-of select="@maxOccurs"/>
				</s:when>
				<s:otherwise>unbounded</s:otherwise>
			</s:choose>)
		</s:when>
	</s:choose>
</s:template>
		 
<!-- ========================================================================
========================================================================== -->		
<s:template match="*" mode="doRange">
	<s:if test=".//xsd:minExclusive | .//xsd:minInclusive | .//xsd:maxExclusive | .//xsd:maxInclusive">
		 <s:choose>
			<s:when test=".//xsd:minInclusive">
				[<s:value-of select=".//xsd:minInclusive/@value"/>
			</s:when>
			<s:when test=".//xsd:minExclusive">
				(<s:value-of select=".//xsd:minExclusive/@value"/>
			</s:when>
			<s:otherwise>[0</s:otherwise>
		 </s:choose>,
		 <s:choose>
			<s:when test=".//xsd:maxInclusive">
				<s:value-of select=".//xsd:maxInclusive/@value"/>]
			</s:when>
			<s:when test=".//xsd:maxExclusive">
				<s:value-of select=".//xsd:maxExclusive/@value"/>)
			</s:when>
			<s:otherwise>UNBOUNDED]</s:otherwise>
		 </s:choose>
	</s:if>

</s:template>
	
<!-- ========================================================================
========================================================================== -->		
<s:template match="xsd:import" mode="doImports">
	<s:if test="@namespace">
		namespace:<s:value-of select="@namespace"/>
	</s:if>
	<s:if test="@schemaLocation">
		schemaLocation: <s:value-of select="@schemaLocation"/>
	</s:if>
</s:template>
       
<!-- ========================================================================
========================================================================== -->		
<s:template match="xsd:description | xsd:appInfo" mode="schemacomments">
	<s:choose> 
	<s:when test="@usage='USAGE'">
			<s:value-of select="text()"/>
	</s:when> 
	</s:choose>   
</s:template>	
	
<!-- ========================================================================
========================================================================== -->		
<s:template match="xsd:appInfo" mode="schemacomments">
	 
	<s:choose> 
	<s:when test="not(@usage='USAGE')">
				<s:value-of select="text()"/>
	</s:when> 
	</s:choose>   
		<!--<s:apply-templates mode="comments" />-->
</s:template>

	
<!-- ========================================================================
========================================================================== -->		
<s:template match="xsd:appInfo " mode="schemacomments">
		<s:param name="otherComments" select="''" />
		<s:for-each select=". | .//* ">
					<s:value-of select="text()"/>&#160;
	 </s:for-each>
	 <s:value-of select="$otherComments"/>
</s:template>

<!-- ========================================================================
========================================================================== -->		
<s:template match="xsd:annotation | xsd:documentation | xsd:appinfo" mode="schemacomments">
		<s:param name="otherComments" select="''" />
			<s:for-each select=".//xsd:documentation | .//xsd:appinfo ">  
				<s:if test="@source">
					See <A class="element-link">
						<s:attribute name="HREF"><s:value-of select="@source"/>	</s:attribute>
						<s:value-of select="@source"/>					
					</A>
			<br/>
				</s:if> 	
					<s:value-of select="."/> 
					<s:if test="position()!=last()"> <br/> - - - - -<br/> </s:if>
				
			</s:for-each>
	 <s:value-of select="$otherComments"/>
		
</s:template>
	
<!-- END IDENTICAL ################################################################# -->

<!--
	$Log$
	Revision 1.1  2003/12/02 19:37:51  siah
	first commit

	Revision 1.8  2002/05/15 13:38:09  Eric
	Patches for Xalan issues with XPath.
	
	Revision 1.7  2002/03/12 18:20:14  Eric
	Fixed duplicate comments on attributes and simple types.
	
	Revision 1.6  2002/02/28 20:20:32  Eric
	Improvements to schemadoc to show model groups in content model diagram.
	
	Revision 1.5  2002/02/06 22:34:10  Eric
	Reenabled schemadoc options, minor improvements...
	
	Revision 1.4  2002/01/17 20:40:39  Eric
	Another round of fixes for schemadoc.
	
	Revision 1.3  2002/01/16 19:20:33  Eric
	Moved shared code, fixed uses in XSD, XSD-CR now in sync.
	
	Revision 1.2  2002/01/15 19:37:49  Eric
	Eliminated spurious message.
	
	Revision 1.1  2002/01/15 18:37:42  Eric
	Schemadoc test framework and files.
	
-->

</s:stylesheet>
