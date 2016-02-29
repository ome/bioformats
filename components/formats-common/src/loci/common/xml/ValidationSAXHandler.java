/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.common.xml;

import java.util.StringTokenizer;

import org.xml.sax.Attributes;


/**
 * Used by validateXML to parse the XML block's schema path using SAX.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Chris Allan callan at blackcat.ca
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
/**  */
class ValidationSAXHandler extends BaseHandler {
  private String schemaPath;
  private boolean first;
  public String getSchemaPath() { return schemaPath; }
  @Override
  public void startDocument() {
    schemaPath = null;
    first = true;
  }
  @Override
  public void startElement(String uri,
    String localName, String qName, Attributes attributes)
  {
    if (!first) return;
    first = false;

    String namespace_attribute = "xmlns" ;
    final int colon = qName.indexOf( ':' ) ;
    if (colon > 0)
    {
        final String namespace_prefix = qName.substring( 0, colon ) ;
        namespace_attribute += ':' + namespace_prefix ;
    }
    int len = attributes.getLength();
    String xmlns = null, xsiSchemaLocation = null;
    for (int i=0; i<len; i++) {
      String name = attributes.getQName(i);
      if (name.equals(namespace_attribute)) xmlns = attributes.getValue(i);
      else if (name.equals("schemaLocation") ||
        name.endsWith(":schemaLocation"))
      {
        xsiSchemaLocation = attributes.getValue(i);
      }
    }
    if (xmlns == null || xsiSchemaLocation == null) return; // not found

    StringTokenizer st = new StringTokenizer(xsiSchemaLocation);
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (xmlns.equals(token)) {
        // next token is the actual schema path
        if (st.hasMoreTokens()) schemaPath = st.nextToken();
        break;
      }
    }
  }
}
