<?xml version="1.0"?>

<!--
	Shared schemadoc stylesheet items
	
	$Id: schemadoc_common.xsl 4 2003-12-02 19:59:11Z siah $
-->

<s:stylesheet  xmlns:s="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- ========================================================================
	OK, this is so complicated because the things that we want to output may
	or may not be in this document.  This in this document should be linked to
	and things in other documents should be show as unlinked items.  If we
	simply intersected with the elements that are in this document, we would
	not show that we are using all the items that are in the content model.
	
	Invokes: elementLinkByName
-->
<s:template name="outputUsesList" >
	<s:param name="list" />
	<s:param name="previous" />
	
	<s:variable name="current" select="substring-before($list, ' ')" />
	<s:variable name="remainder" select="substring-after($list, ' ')" />
	
	<s:if test="$previous!=$current">
	
		<!-- output something to separate this from previous entries. -->
		<s:if test="string-length($previous) > 0">
			<s:text>, </s:text>
		</s:if>
		<!-- OK, this is an element not previously encountered. -->
		<s:variable name="linkName">
			<s:call-template name="elementLinkByName">
				<s:with-param name="elementName" select="$current" />
			</s:call-template>
		</s:variable>
		
		<!-- if we got the name of a link, then write a link, else just name -->
		<s:choose>
			<s:when test="string-length($linkName) > 0">
				<a class="element-link" href="{$linkName}">
					<s:value-of select="$current" />
				</a>
			</s:when>
			<s:otherwise><s:value-of select="$current" /></s:otherwise>
		</s:choose>				
	</s:if>
		
	<!-- invoke for the remaining list -->
	<s:if test="string-length($remainder) > 0">
		<s:call-template name="outputUsesList">
			<s:with-param name="list" select="$remainder" />
			<s:with-param name="previous" select="$current" />
		</s:call-template>
	</s:if>
	
</s:template>

<!-- **********************begin dupplicate stuff in all files*****************-->
<!-- ***********************SOURCE part...copied from lotus*****************-->
	
<!-- Templates for each node type follows.  The output of each template has a similar structure
  to enable script to walk the result tree easily for handling user interaction. -->
  
<!-- Template for pis not handled elsewhere -->
<!-- ========================================================================
========================================================================== -->		
<s:template mode="copy" match="processing-instruction()">
  <DIV class="e">
  <SPAN class="b">&#160;</SPAN>
  <SPAN class="m">&lt;?</SPAN><SPAN class="pi"><s:value-of select="name(.)"/> <s:value-of select="."/></SPAN><SPAN class="m">?&gt;</SPAN>
  </DIV>
</s:template>

<!-- Template for attributes not handled elsewhere -->
<!-- ========================================================================
========================================================================== -->		
<s:template mode="copy" match="@*"><SPAN class=".xdra"><s:text> </s:text><s:value-of select="name(.)"/></SPAN><SPAN class="m">="</SPAN><B><s:value-of select="."/></B><SPAN class="m">"</SPAN></s:template>

<!-- ========================================================================
========================================================================== -->		
<!-- Template for text nodes -->
<s:template mode="copy" match="text()">
  <s:choose><s:when test="name(.) = '#cdata-section'"><s:call-template name="cdata"/></s:when>
  <s:otherwise><DIV class="e">
  <SPAN class="b">&#160;</SPAN>
  <SPAN class="tx"><s:value-of select="."/></SPAN>
  </DIV></s:otherwise></s:choose>
</s:template>
  
<!-- ========================================================================
========================================================================== -->		
<!-- Template for comment nodes -->
<s:template match="comment()" mode="copy">
  <DIV class="k">
  <SPAN><A class="b" onclick="return false" onfocus="h()" STYLE="visibility:hidden">-</A> <SPAN class="m">&lt;!--</SPAN></SPAN>
  <SPAN id="clean" class="ci"><PRE><s:value-of select="."/></PRE></SPAN>
  <SPAN class="b">&#160;</SPAN> <SPAN class="m">--&gt;</SPAN>
  <SCRIPT>f(clean);</SCRIPT></DIV>
</s:template>

<!-- ========================================================================
========================================================================== -->		
<!-- Template for cdata nodes -->
<s:template name="cdata">
  <DIV class="k">
  <SPAN><A class="b" onclick="return false" onfocus="h()" STYLE="visibility:hidden">-</A> <SPAN class="m">&lt;![CDATA[</SPAN></SPAN>
  <SPAN id="clean" class="di"><PRE><s:value-of select="."/></PRE></SPAN>
  <SPAN class="b">&#160;</SPAN> <SPAN class="m">]]&gt;</SPAN>
  <SCRIPT>f(clean);</SCRIPT></DIV>
</s:template>

<!-- ========================================================================
========================================================================== -->		
<!-- Template for elements not handled elsewhere (leaf nodes) -->
<s:template mode="copy" match="*">
  <DIV class="e"><DIV STYLE="margin-left:1em;text-indent:-2em">
  <SPAN class="b">&#160;</SPAN>
  <SPAN class="m">&lt;</SPAN><SPAN class="t"><s:value-of select="name(.)"/></SPAN> <s:apply-templates mode="copy" select="@*"/><SPAN class="m"> /&gt;</SPAN>
  </DIV></DIV>
</s:template>

<!-- ========================================================================
========================================================================== -->		
<!-- Template for elements with only text children -->
<s:template mode="copy" match="*[text() and not(comment() or processing-instruction() or *)]">
  <DIV class="e"><DIV STYLE="margin-left:1em;text-indent:-2em">
  <SPAN class="b">&#160;</SPAN> <SPAN class="m">&lt;</SPAN><SPAN class="t"><s:value-of select="name(.)"/></SPAN><s:apply-templates  mode="copy" select="@*"/>
  <SPAN class="m">&gt;</SPAN><SPAN class="tx"><s:value-of select="."/></SPAN><SPAN class="m">&lt;/</SPAN><SPAN class="t"><s:value-of select="name(.)"/></SPAN><SPAN class="m">&gt;</SPAN>
  </DIV></DIV>
</s:template>

<!-- ========================================================================
========================================================================== -->		
<!-- Template for elementstypes -->
<s:template mode="copy" match="*[*]">
  <DIV class="e">
  <DIV class="c" STYLE="margin-left:1em;text-indent:-2em"><A href="#" onclick="return false" onfocus="h()" class="b">-</A> <SPAN class="m">&lt;</SPAN><SPAN class="et"><s:value-of select="name(.)"/></SPAN><s:apply-templates  mode="copy" select="@*"/><SPAN class="m">&gt;</SPAN></DIV>
  <DIV><s:apply-templates mode="copy" />
  <DIV><SPAN class="b">&#160;</SPAN><SPAN class="m">&lt;/</SPAN><SPAN class="et"><s:value-of select="name(.)"/></SPAN><SPAN class="m">&gt;</SPAN></DIV>
  </DIV></DIV>
</s:template>

<!--
	$Log$
	Revision 1.1  2003/12/02 19:57:06  siah
	first commit

	Revision 1.2  2002/01/16 19:20:33  Eric
	Moved shared code, fixed uses in XSD, XSD-CR now in sync.
	
	Revision 1.1  2002/01/15 18:37:42  Eric
	Schemadoc test framework and files.
	
-->

</s:stylesheet>


