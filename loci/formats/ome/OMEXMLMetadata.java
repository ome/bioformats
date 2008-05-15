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

import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import org.w3c.dom.Element;

/**
 * A utility class for constructing and manipulating OME-XML DOMs.
 * It is the superclass for all versions of OME-XML. It requires the
 * ome.xml package to compile (part of ome-java.jar).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/ome/OMEXMLMetadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/ome/OMEXMLMetadata.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public abstract class OMEXMLMetadata
  implements MetadataStore, MetadataRetrieve
{

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
  public abstract String dumpXML();

  /** Sets the UUID for this OME-XML block. */
  public void setUUID(String uuid) {
    Element ome = root.getDOMElement();
    DOMUtil.setAttribute("UUID", uuid, ome);
  }

  /** Gets the UUID for this OME-XML block. */
  public String getUUID() {
    Element ome = root.getDOMElement();
    return DOMUtil.getAttribute("UUID", ome);
  }

  /** Adds the key/value pair as a new OriginalMetadata node. */
  public void populateOriginalMetadata(String key, String value) {
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
    final String originalMetadata = "OriginalMetadata";
    if (!omCreated) {
      Element std = DOMUtil.createChild(root.getDOMElement(),
        "SemanticTypeDefinitions");
      DOMUtil.setAttribute("xmlns",
        "http://www.openmicroscopy.org/XMLschemas/STD/RC2/STD.xsd", std);
      Element st = DOMUtil.createChild(std, "SemanticType");
      DOMUtil.setAttribute("Name", originalMetadata, st);
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
    Element om = DOMUtil.createChild(imageCA, originalMetadata);
    DOMUtil.setAttribute("ID", root.makeID(originalMetadata), om);
    DOMUtil.setAttribute("Name", key, om);
    DOMUtil.setAttribute("Value", value, om);
  }

  // -- MetadataStore API methods --

  /* @see loci.formats.MetadataStore#getRoot() */
  public Object getRoot() {
    return root;
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
