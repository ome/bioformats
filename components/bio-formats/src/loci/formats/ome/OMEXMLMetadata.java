//
// OMEXMLMetadata.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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
import javax.xml.transform.TransformerException;
import loci.common.LogTools;
import loci.formats.meta.IMetadata;
import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import org.w3c.dom.*;

/**
 * A utility class for constructing and manipulating OME-XML DOMs.
 * It is the superclass for all versions of OME-XML. It requires the
 * ome.xml package to compile (part of ome-xml.jar).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/ome/OMEXMLMetadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/ome/OMEXMLMetadata.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public abstract class OMEXMLMetadata implements IMetadata {

  // -- Constants --

  /** Custom attribute for storing original metadata key/value pairs. */
  private static final String ORIGINAL_METADATA = "OriginalMetadata";

  // -- Fields --

  /** The root element of OME-XML. */
  protected OMEXMLNode root;

  /** DOM element that backs the first Image's CustomAttributes node. */
  private Element imageCA;

  /** Whether OriginalMetadata semantic type definition has been created. */
  private boolean omCreated;

  // -- Constructors --

  /** Creates a new OME-XML metadata object. */
  public OMEXMLMetadata() { }

  // -- OMEXMLMetadata API methods --

  /**
   * Dumps the given OME-XML DOM tree to a string.
   * @return OME-XML as a string.
   */
  public String dumpXML() {
    if (root == null) return null;
    try {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      DOMUtil.writeXML(os, root.getDOMElement().getOwnerDocument());
      return os.toString();
    }
    catch (TransformerException exc) {
      LogTools.trace(exc);
    }
    return null;
  }

  /** Adds the key/value pair as a new OriginalMetadata node. */
  public void setOriginalMetadata(String key, String value) {
    if (imageCA == null) {
      Element ome = root.getDOMElement();
      Element image = DOMUtil.getChildElement("Image", ome);
      if (image == null) {
        setImageName("", 0); // HACK - force creation of Image element
        image = DOMUtil.getChildElement("Image", ome);
      }
      imageCA = DOMUtil.getChildElement("CustomAttributes", image);
      if (imageCA == null) {
        imageCA = DOMUtil.createChild(image, "CustomAttributes", true);
      }
    }
    if (!omCreated) {
      Element std = DOMUtil.createChild(root.getDOMElement(),
        "SemanticTypeDefinitions");
      DOMUtil.setAttribute("xmlns",
        "http://www.openmicroscopy.org/XMLschemas/STD/RC2/STD.xsd", std);
      Element st = DOMUtil.createChild(std, "SemanticType");
      DOMUtil.setAttribute("Name", ORIGINAL_METADATA, st);
      DOMUtil.setAttribute("AppliesTo", "I", st);

      Element nameElement = DOMUtil.createChild(st, "Element");
      DOMUtil.setAttribute("Name", "Name", nameElement);
      DOMUtil.setAttribute("DBLocation", "ORIGINAL_METADATA.NAME", nameElement);
      DOMUtil.setAttribute("DataType", "string", nameElement);

      Element valueElement = DOMUtil.createChild(st, "Element");
      DOMUtil.setAttribute("Name", "Value", valueElement);
      DOMUtil.setAttribute("DBLocation",
        "ORIGINAL_METADATA.VALUE", valueElement);
      DOMUtil.setAttribute("DataType", "string", valueElement);
      omCreated = true;
    }
    Element om = DOMUtil.createChild(imageCA, ORIGINAL_METADATA);
    DOMUtil.setAttribute("ID", root.makeID(ORIGINAL_METADATA), om);
    DOMUtil.setAttribute("Name", key, om);
    DOMUtil.setAttribute("Value", value, om);
  }

  /** Gets the OriginalMetadata value corresponding to the given key. */
  public String getOriginalMetadataValue(String key) {
    if (imageCA == null) {
      Element ome = root.getDOMElement();
      Element image = DOMUtil.getChildElement("Image", ome);
      if (image == null) return null;
      imageCA = DOMUtil.getChildElement("CustomAttributes", image);
      if (imageCA == null) return null;
    }
    NodeList list = imageCA.getChildNodes();
    int size = list.getLength();
    for (int i=0; i<size; i++) {
      Node node = list.item(i);
      if (!(node instanceof Element)) continue;
      String nodeName = node.getNodeName();
      if (!nodeName.equals(ORIGINAL_METADATA)) {
        // not an OriginalMetadata element
        continue;
      }
      NamedNodeMap attrs = node.getAttributes();
      int len = attrs.getLength();
      String value = null;
      for (int j=0; j<len; j++) {
        Attr attr = (Attr) attrs.item(i);
        if (attr == null) continue;
        String attrName = attr.getName();
        if ("Name".equals(attrName)) {
          String name = attr.getValue();
          if (!key.equals(name)) {
            // wrong OriginalMetadata element
            value = null;
            break;
          }
        }
        else if ("Value".equals(attrName)) {
          value = attr.getValue();
        }
      }
      if (value != null) return value;
    }
    return null;
  }

  // -- MetadataRetrieve API methods --

  /* @see loci.formats.meta.MetadataRetrieve#getUUID() */
  public String getUUID() {
    Element ome = root.getDOMElement();
    return DOMUtil.getAttribute("UUID", ome);
  }

  // -- MetadataStore API methods --

  /* @see loci.formats.meta.MetadataStore#getRoot() */
  public Object getRoot() {
    return root;
  }

  /* @see loci.formats.meta.MetadataRetrieve#setUUID(String) */
  public void setUUID(String uuid) {
    Element ome = root.getDOMElement();
    DOMUtil.setAttribute("UUID", uuid, ome);
  }

  // -- Type conversion methods --

  /**
   * Converts Boolean value to Integer. Used to convert
   * from 2003-FC Laser element's FrequencyDoubled Boolean value
   * to Laser entity's FrequencyMultiplication Integer value.
   */
  protected Integer booleanToInteger(Boolean value) {
    return value == null ? null : new Integer(value.booleanValue() ? 2 : 1);
  }

  /**
   * Converts Integer value to Boolean. Used to convert
   * from Laser entity's FrequencyMultiplication Integer value
   * to 2003-FC Laser element's FrequencyDoubled Boolean value.
   */
  protected Boolean integerToBoolean(Integer value) {
    return value == null ? null : new Boolean(value.intValue() == 2);
  }

}
