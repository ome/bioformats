/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
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

package ome.xml.meta;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import loci.common.Constants;
import loci.common.xml.XMLTools;

import ome.xml.model.OME;
import ome.xml.model.OMEModelObject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class for constructing and manipulating OME-XML DOMs.
 * It is the superclass for all versions of OME-XML. It requires the
 * ome.xml package to compile (part of ome-xml.jar).
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public abstract class AbstractOMEXMLMetadata implements OMEXMLMetadata {

  // -- Constants --

  /** XSI namespace. */
  public static final String XSI_NS =
    "http://www.w3.org/2001/XMLSchema-instance";

  /** OME-XML schema location. */
  public static final String SCHEMA =
    "http://www.openmicroscopy.org/Schemas/OME/2015-01/ome.xsd";

  protected static final Logger LOGGER =
    LoggerFactory.getLogger(AbstractOMEXMLMetadata.class);

  // -- Fields --

  /** The root element of OME-XML. */
  protected OMEModelObject root;

  /** DOM element that backs the first Image's CustomAttributes node. */
  private Element imageCA;

  private DocumentBuilder builder;

  // -- Constructors --

  /** Creates a new OME-XML metadata object. */
  public AbstractOMEXMLMetadata() {
  }

  // -- OMEXMLMetadata API methods --

  /**
   * Dumps the given OME-XML DOM tree to a string.
   * @return OME-XML as a string.
   */
  @Override
  public String dumpXML() {
    if (root == null) {
      root = (OMEModelObject) getRoot();
      if (root == null) return null;
    }
    Document doc = createNewDocument();
    Element r = root.asXMLElement(doc);
    String schemaLocation = OME.NAMESPACE + " " + SCHEMA;
    return XMLTools.dumpXML(schemaLocation, doc, r);
  }


  // -- Helper methods --

  public Document createNewDocument() {
    if (builder == null) {
      builder = XMLTools.createBuilder();
    }
    return builder.newDocument();
  }

}
