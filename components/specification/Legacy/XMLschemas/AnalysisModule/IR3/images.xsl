<?xml version="1.0" ?>

<!-- utility functions to help with images in schema doc -->

<s:stylesheet  xmlns:s="http://www.w3.org/1999/XSL/Transform" version="1.0">

<s:variable name="imgIdRefsDoc" select="document('id-map.xml')" />
<s:variable name="imgIdRefs" select="$imgIdRefsDoc/id-map" />

<s:variable name="imageScale" select="'1.4'" />

<!-- ========================================================================
	Inserts the table row for the diagram entry.
-->
<s:template name="insertImageRow">
	<s:param name="doImages" />
	<s:param name="componentPath" />

	<s:if test="$doImages='true' or $doImages='TRUE'">
	
		<s:variable name="detailNode" select="$imgIdRefs/entry[@path=$componentPath]" />
	
		<s:if test="$detailNode">
			<tr>
				<td class="tdnames">
					diagram
				</td>
				<td align="left" valign="top" bgcolor="#eeeeeeee" >
					<script language="JavaScript">
					<s:text>emitSVG('src="</s:text>
					<s:value-of select="concat($detailNode/@id, '.svg')" />
					<s:text>" name="SVGEmbed" height="</s:text>
					<s:value-of select="$detailNode/@height * $imageScale" />
					<s:text>" width="</s:text>
					<s:value-of select="$detailNode/@width * $imageScale" />
					<s:text>" type="image/svg-xml"');</s:text>
					</script>
				</td>
			</tr>
		</s:if>
	</s:if>
</s:template>

</s:stylesheet>


