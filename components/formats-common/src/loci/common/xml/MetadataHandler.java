/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

import java.util.Hashtable;

import org.xml.sax.Attributes;

/**
 * Used to retrieve key/value pairs from XML.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Chris Allan callan at blackcat.ca
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
class MetadataHandler extends BaseHandler {
  private String currentQName;
  private Hashtable<String, String> metadata =
    new Hashtable<String, String>();

  // -- MetadataHandler API methods --

  public Hashtable<String, String> getMetadata() {
    return metadata;
  }

  // -- DefaultHandler API methods --

  @Override
  public void characters(char[] data, int start, int len) {
    metadata.put(currentQName, new String(data, start, len));
  }

  @Override
  public void startElement(String uri, String localName, String qName,
    Attributes attributes)
  {
    if (attributes.getLength() == 0) currentQName += " - " + qName;
    else currentQName = qName;
    for (int i=0; i<attributes.getLength(); i++) {
      metadata.put(qName + " - " + attributes.getQName(i),
        attributes.getValue(i));
    }
  }
}
