//
// OMEXMLMetadata.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.ome;

import loci.formats.*;
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

  // -- Helper methods --

  /** Issues the given message as a warning. */
  protected void warn(String msg) {
    LogTools.println(msg);
  }

  /** Converts the given Integer into a primitive int. */
  protected int i2i(Integer i) { return i == null ? 0 : i.intValue(); }

  // -- Type conversion methods --

  /** Converts Dimensions.PixelSizeC Float value to Integer. */
  protected Integer dimensionsPixelSizeCToInteger(Float value) {
    return value == null ? null : new Integer(value.intValue());
  }

  /** Converts Integer value to Dimensions.PixelSizeC Float. */
  protected Float dimensionsPixelSizeCFromInteger(Integer value) {
    return value == null ? null : new Float(value.floatValue());
  }

  /** Converts Laser.FrequencyDoubled Boolean value to Integer. */
  protected Integer laserFrequencyDoubledToInteger(Boolean value) {
    return value == null ? null : new Integer(value.booleanValue() ? 2 : 1);
  }

  /** Converts Integer value to Laser.FrequencyDoubled Boolean. */
  protected Boolean laserFrequencyDoubledFromInteger(Integer value) {
    return value == null ? null : value.intValue() == 2 ?
      Boolean.TRUE : Boolean.FALSE;
  }

}
