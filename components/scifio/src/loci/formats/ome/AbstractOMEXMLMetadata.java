//
// AbstractOMEXMLMetadata.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.ome;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import ome.xml.DOMUtil;
import ome.xml.model.OME;
import ome.xml.model.OMEModelObject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A utility class for constructing and manipulating OME-XML DOMs.
 * It is the superclass for all versions of OME-XML. It requires the
 * ome.xml package to compile (part of ome-xml.jar).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/ome/AbstractOMEXMLMetadata.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/ome/AbstractOMEXMLMetadata.java;hb=HEAD">Gitweb</a></dd></dl>
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
    //"http://www.openmicroscopy.org/Schemas/OME/2012-06/ome.xsd";
    "https://raw.github.com/openmicroscopy/openmicroscopy/schema-2012-06/components/specification/InProgress/ome.xsd";

  // -- Fields --

  /** The root element of OME-XML. */
  protected OMEModelObject root;

  /** DOM element that backs the first Image's CustomAttributes node. */
  private Element imageCA;

  private DocumentBuilder builder;

  // -- Constructors --

  /** Creates a new OME-XML metadata object. */
  public AbstractOMEXMLMetadata() {
    try {
      builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }
    catch (ParserConfigurationException e) { }
  }

  // -- OMEXMLMetadata API methods --

  /**
   * Dumps the given OME-XML DOM tree to a string.
   * @return OME-XML as a string.
   */
  public String dumpXML() {
    if (root == null) {
      root = (OMEModelObject) getRoot();
      if (root == null) return null;
    }
    try {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      Document doc = builder.newDocument();
      Element r = root.asXMLElement(doc);
      r.setAttribute("xmlns:xsi", XSI_NS);
      r.setAttribute("xsi:schemaLocation", OME.NAMESPACE + " " + SCHEMA);
      doc.appendChild(r);
      DOMUtil.writeXML(os, doc);
      return os.toString();
    }
    catch (TransformerException exc) {
    }
    return null;
  }

  // -- MetadataRetrieve API methods --

  /* @see loci.formats.meta.MetadataRetrieve#getUUID() */
  public String getUUID() {
    Element ome = root.asXMLElement(builder.newDocument());
    return DOMUtil.getAttribute("UUID", ome);
  }

  // -- MetadataStore API methods --

  /* @see loci.formats.meta.MetadataStore#setRoot(Object) */
  public void setRoot(Object root) {
  }

  /* @see loci.formats.meta.MetadataStore#getRoot() */
  public Object getRoot() {
    return root;
  }

  /* @see loci.formats.meta.MetadataRetrieve#setUUID(String) */
  public void setUUID(String uuid) {
    Element ome = root.asXMLElement(builder.newDocument());
    DOMUtil.setAttribute("UUID", uuid, ome);
  }

  // -- Type conversion methods --

  /**
   * Converts Boolean value to Integer. Used to convert
   * from 2003-FC Laser FrequencyDoubled Boolean value
   * to Laser FrequencyMultiplication Integer value.
   */
  protected Integer booleanToInteger(Boolean value) {
    return value == null ? null : new Integer(value.booleanValue() ? 2 : 1);
  }

  /**
   * Converts Integer value to Boolean. Used to convert
   * from Laser FrequencyMultiplication Integer value
   * to 2003-FC Laser FrequencyDoubled Boolean value.
   */
  protected Boolean integerToBoolean(Integer value) {
    return value == null ? null : new Boolean(value.intValue() == 2);
  }

  /**
   * Converts Double value to Integer. Used to convert
   * from 2008-02 LogicalChannel PinholeSize Integer value
   * to LogicalChannel PinholeSize Double value.
   */
  protected Integer doubleToInteger(Double value) {
    return value == null ? null : new Integer(value.intValue());
  }

  /**
   * Converts Integer value to Double. Used to convert
   * from LogicalChannel PinholeSize Double value
   * to 2008-02 LogicalChannel PinholeSize Integer value.
   */
  protected Double integerToDouble(Integer value) {
    return value == null ? null : new Double(value.doubleValue());
  }

}
