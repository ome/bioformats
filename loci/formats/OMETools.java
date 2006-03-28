//
// OMETools.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import org.w3c.dom.Element;

/**
 * A utility class for constructing and manipulating OME-XML DOMs. It uses
 * reflection to access the loci.ome.xml package so that the class compiles
 * if the loci.ome.xml package is unavailable.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public abstract class OMETools {

  // -- Constants --

  private static final ReflectedUniverse R = createUniverse();

  private static ReflectedUniverse createUniverse() {
    ReflectedUniverse r = new ReflectedUniverse();
    try {
      r.exec("import loci.ome.xml.CustomAttributesNode");
      r.exec("import loci.ome.xml.DOMUtil");
      r.exec("import loci.ome.xml.ImageNode");
      r.exec("import loci.ome.xml.OMENode");
      r.exec("import loci.ome.xml.OMEXMLNode");
      r.setVar("FALSE", false);
    }
    catch (Throwable t) { r = null; }
    return r;
  }


  // -- OMETools API methods - general purpose --

  /** Constructs a new OME-XML root node. */
  public static Object createRoot() { return createRoot(null); }

  /** Constructs a new OME-XML root node with the given XML block. */
  public static Object createRoot(String xml) {
    if (R == null) return null;
    try {
      if (xml == null) return R.exec("new OMENode()");
      else {
        R.setVar("xml", xml);
        return R.exec("new OMENode(xml)");
      }
    }
    catch (ReflectException exc) { }
    return null;
  }

  /** Dumps the given OME-XML DOM tree to a string. */
  public static String dumpXML(Object root) {
    if (root == null) return null;
    R.setVar("ome", root);
    try {
      Object s = R.exec("ome.writeOME(FALSE)");
      if (s instanceof String) return (String) s;
    }
    catch (ReflectException exc) { }
    return null;
  }


  // -- OMETools API methods - node (element) assignment --

  /** Populates the first OME/Image element. */
  public static Object setImage(Object root, String name,
    String creationDate, String description)
  {
    Object image = null;
    try {
      R.setVar("ome", root);
      R.setVar("name", name);
      R.setVar("creationDate", creationDate);
      R.setVar("description", description);

      image = getChild(root, "Image");
      R.setVar("image", image);

      R.exec("image.setName(name)");
      R.exec("image.setCreated(creationDate)");
      R.exec("image.setDescription(description)");
    }
    catch (ReflectException exc) { exc.printStackTrace(); }
    return image;
  }

  /** Populates the first OME/CA/Experimenter element. */
  public static Object setExperimenter(Object root, String firstName,
    String lastName, String email, String institution, String dataDirectory,
    Object group)
  {
    Object experimenter = null;
    try {
      R.setVar("ome", root);
      R.setVar("firstName", firstName);
      R.setVar("lastName", lastName);
      R.setVar("email", email);
      R.setVar("institution", institution);
      R.setVar("dataDirectory", dataDirectory);
      R.setVar("group", group);

      Object ca = getChild(root, "CustomAttributes");
      R.setVar("ca", ca);

      R.exec("import loci.ome.xml.st.ExperimenterNode");
      experimenter = getChild(ca, "Experimenter");
      R.setVar("experimenter", experimenter);

      R.exec("experimenter.setFirstName(firstName)");
      R.exec("experimenter.setLastName(lastName)");
      R.exec("experimenter.setEmail(email)");
      R.exec("experimenter.setInstitution(institution)");
      R.exec("experimenter.setDataDirectory(dataDirectory)");
      R.exec("experimenter.setGroup(group)");
    }
    catch (ReflectException exc) { exc.printStackTrace(); }
    return experimenter;
  }

  /** Populates the first OME/CA/Group element. */
  public static Object setGroup(Object root, String name,
    Object leader, Object contact)
  {
    Object group = null;
    try {
      R.setVar("ome", root);
      R.setVar("name", name);
      R.setVar("leader", leader);
      R.setVar("contact", contact);

      Object ca = getChild(root, "CustomAttributes");
      R.setVar("ca", ca);

      R.exec("import loci.ome.xml.st.GroupNode");
      group = getChild(ca, "Group");
      R.setVar("group", group);

      R.exec("group.setName(name)");
      R.exec("group.setLeader(leader)");
      R.exec("group.setContact(contact)");
    }
    catch (ReflectException exc) { exc.printStackTrace(); }
    return group;
  }

  /** Populates the first OME/CA/Instrument element. */
  public static Object setInstrument(Object root, String manufacturer,
    String model, String serialNumber, String type)
  {
    Object instrument = null;
    try {
      R.setVar("ome", root);
      R.setVar("manufacturer", manufacturer);
      R.setVar("model", model);
      R.setVar("serialNumber", serialNumber);
      R.setVar("type", type);

      Object ca = getChild(root, "CustomAttributes");
      R.setVar("ca", ca);

      R.exec("import loci.ome.xml.st.InstrumentNode");
      instrument = getChild(ca, "Instrument");
      R.setVar("instrument", instrument);

      R.exec("instrument.setManufacturer(manufacturer)");
      R.exec("instrument.setModel(model)");
      R.exec("instrument.setSerialNumber(serialNumber)");
      R.exec("instrument.setType(type)");
    }
    catch (ReflectException exc) { exc.printStackTrace(); }
    return instrument;
  }

  /** Populates the first OME/Image/CA/Dimensions element. */
  public static Object setDimensions(Object root, Float pixelSizeX,
    Float pixelSizeY, Float pixelSizeZ, Float pixelSizeC, Float pixelSizeT)
  {
    Object dimensions = null;
    try {
      R.setVar("ome", root);
      R.setVar("pixelSizeX", pixelSizeX);
      R.setVar("pixelSizeY", pixelSizeY);
      R.setVar("pixelSizeZ", pixelSizeZ);
      R.setVar("pixelSizeC", pixelSizeC);
      R.setVar("pixelSizeT", pixelSizeT);

      Object image = getChild(root, "Image");
      R.setVar("image", image);
      Object ca = getChild(image, "CustomAttributes");
      R.setVar("ca", ca);

      R.exec("import loci.ome.xml.st.DimensionsNode");
      dimensions = getChild(ca, "Dimensions");
      R.setVar("dimensions", dimensions);

      R.exec("dimensions.setPixelSizeX(pixelSizeX)");
      R.exec("dimensions.setPixelSizeY(pixelSizeY)");
      R.exec("dimensions.setPixelSizeZ(pixelSizeZ)");
      R.exec("dimensions.setPixelSizeC(pixelSizeC)");
      R.exec("dimensions.setPixelSizeT(pixelSizeT)");
    }
    catch (ReflectException exc) { exc.printStackTrace(); }
    return dimensions;
  }

  /** Populates the first OME/Image/CA/DisplayROI element. */
  public static Object setDisplayROI(Object root, Integer x0, Integer y0,
    Integer z0, Integer x1, Integer y1, Integer z1, Integer t0, Integer t1,
    Object displayOptions)
  {
    Object displayROI = null;
    try {
      R.setVar("ome", root);
      R.setVar("x0", x0);
      R.setVar("y0", y0);
      R.setVar("z0", z0);
      R.setVar("x0", x1);
      R.setVar("y0", y1);
      R.setVar("z0", z1);
      R.setVar("t0", t0);
      R.setVar("t1", t1);
      R.setVar("displayOptions", displayOptions);

      Object image = getChild(root, "Image");
      R.setVar("image", image);
      Object ca = getChild(image, "CustomAttributes");
      R.setVar("ca", ca);

      R.exec("import loci.ome.xml.st.DisplayROINode");
      displayROI = getChild(ca, "DisplayROI");
      R.setVar("displayROI", displayROI);

      R.exec("displayROI.setX0(x0)");
      R.exec("displayROI.setY0(y0)");
      R.exec("displayROI.setZ0(z0)");
      R.exec("displayROI.setX1(x1)");
      R.exec("displayROI.setY1(y1)");
      R.exec("displayROI.setZ1(z1)");
      R.exec("displayROI.setT0(t0)");
      R.exec("displayROI.setT1(t1)");
      R.exec("displayROI.setDisplayOptions(displayOptions)");
    }
    catch (ReflectException exc) { exc.printStackTrace(); }
    return displayROI;
  }

  /** Populates the first OME/Image/CA/Pixels element. */
  public static Object setPixels(Object root, Integer sizeX, Integer sizeY,
    Integer sizeZ, Integer sizeC, Integer sizeT, String pixelType,
    Boolean bigEndian, String dimensionOrder)
  {
    Object pixels = null;
    try {
      R.setVar("ome", root);
      R.setVar("sizeX", sizeX);
      R.setVar("sizeY", sizeY);
      R.setVar("sizeZ", sizeZ);
      R.setVar("sizeC", sizeC);
      R.setVar("sizeT", sizeT);
      R.setVar("pixelType", pixelType);
      R.setVar("bigEndian", bigEndian);
      R.setVar("dimensionOrder", dimensionOrder);

      Object image = getChild(root, "Image");
      R.setVar("image", image);
      Object ca = getChild(image, "CustomAttributes");
      R.setVar("ca", ca);

      R.exec("import loci.ome.xml.st.PixelsNode");
      pixels = getChild(ca, "Pixels");
      R.setVar("pixels", pixels);

      R.exec("pixels.setSizeX(sizeX)");
      R.exec("pixels.setSizeY(sizeY)");
      R.exec("pixels.setSizeZ(sizeZ)");
      R.exec("pixels.setSizeC(sizeC)");
      R.exec("pixels.setSizeT(sizeT)");
      R.exec("pixels.setPixelType(pixelType)");
      R.exec("pixels.setBigEndian(bigEndian)");
      R.exec("pixels.setDimensionOrder(dimensionOrder)");
    }
    catch (ReflectException exc) { exc.printStackTrace(); }
    return pixels;
  }

  /** Populates the first OME/Image/CA/StageLabel element. */
  public static Object setStageLabel(Object root, String name,
    Float x, Float y, Float z)
  {
    Object stageLabel = null;
    try {
      R.setVar("ome", root);
      R.setVar("name", name);
      R.setVar("x", x);
      R.setVar("y", y);
      R.setVar("z", z);

      Object image = getChild(root, "Image");
      R.setVar("image", image);
      Object ca = getChild(image, "CustomAttributes");
      R.setVar("ca", ca);

      R.exec("import loci.ome.xml.st.StageLabelNode");
      stageLabel = getChild(ca, "StageLabel");
      R.setVar("stageLabel", stageLabel);

      R.exec("stageLabel.setName(name)");
      R.exec("stageLabel.setX(x)");
      R.exec("stageLabel.setY(y)");
      R.exec("stageLabel.setZ(z)");
    }
    catch (ReflectException exc) { exc.printStackTrace(); }
    return stageLabel;
  }


  // -- OMETools API methods - individual attribute assignment --

  /** Populates the first OME/Image element's Name attribute. */
  public static Object setImageName(Object root, String name) {
    return setImage(root, name, null, null);
  }

  /** Populates the first OME/Image element's CreationDate attribute. */
  public static Object setCreationDate(Object root, String creationDate) {
    return setImage(root, null, creationDate, null);
  }

  /** Populates the first OME/Image element's Description attribute. */
  public static Object setDescription(Object root, String description) {
    return setImage(root, null, null, description);
  }

  /**
   * Populates the first OME/Image/CA/Dimensions
   * element's PixelSizeX attribute.
   */
  public static Object setPixelSizeX(Object root, float pixelSizeX) {
    return setDimensions(root, new Float(pixelSizeX), null, null, null, null);
  }

  /**
   * Populates the first OME/Image/CA/Dimensions
   * element's PixelSizeY attribute.
   */
  public static Object setPixelSizeY(Object root, float pixelSizeY) {
    return setDimensions(root, null, new Float(pixelSizeY), null, null, null);
  }

  /**
   * Populates the first OME/Image/CA/Dimensions
   * element's PixelSizeZ attribute.
   */
  public static Object setPixelSizeZ(Object root, float pixelSizeZ) {
    return setDimensions(root, null, null, new Float(pixelSizeZ), null, null);
  }

  /**
   * Populates the first OME/Image/CA/Dimensions
   * element's PixelSizeC attribute.
   */
  public static Object setPixelSizeC(Object root, float pixelSizeC) {
    return setDimensions(root, null, null, null, new Float(pixelSizeC), null);
  }

  /**
   * Populates the first OME/Image/CA/Dimensions
   * element's PixelSizeT attribute.
   */
  public static Object setPixelSizeT(Object root, float pixelSizeT) {
    return setDimensions(root, null, null, null, null, new Float(pixelSizeT));
  }

  /** Populates the first OME/Image/CA/Pixels element's SizeX attribute. */
  public static Object setSizeX(Object root, int sizeX) {
    return setPixels(root, new Integer(sizeX), null, null, null, null,
      null, null, null);
  }

  /** Populates the first OME/Image/CA/Pixels element's SizeY attribute. */
  public static Object setSizeY(Object root, int sizeY) {
    return setPixels(root, null, new Integer(sizeY), null, null, null,
      null, null, null);
  }

  /** Populates the first OME/Image/CA/Pixels element's SizeZ attribute. */
  public static Object setSizeZ(Object root, int sizeZ) {
    return setPixels(root, null, null, new Integer(sizeZ), null, null,
      null, null, null);
  }

  /** Populates the first OME/Image/CA/Pixels element's SizeC attribute. */
  public static Object setSizeC(Object root, int sizeC) {
    return setPixels(root, null, null, null, new Integer(sizeC), null,
      null, null, null);
  }

  /** Populates the first OME/Image/CA/Pixels element's SizeT attribute. */
  public static Object setSizeT(Object root, int sizeT) {
    return setPixels(root, null, null, null, null, new Integer(sizeT),
      null, null, null);
  }

  /** Populates the first OME/Image/CA/Pixels element's PixelType attribute. */
  public static Object setPixelType(Object root, String pixelType) {
    return setPixels(root, null, null, null, null, null,
      pixelType, null, null);
  }

  /** Populates the first OME/Image/CA/Pixels element's BigEndian attribute. */
  public static Object setBigEndian(Object root, boolean bigEndian) {
    return setPixels(root, null, null, null, null, null,
      null, new Boolean(bigEndian), null);
  }

  /**
   * Populates the first OME/Image/CA/Pixels
   * element's DimensionOrder attribute.
   */
  public static Object setDimensionOrder(Object root, String dimensionOrder) {
    return setPixels(root, null, null, null, null, null,
      null, null, dimensionOrder);
  }

  /** Populates the first OME/Image/CA/StageLabel element's Name attribute. */
  public static Object setStageName(Object root, String name) {
    return setStageLabel(root, name, null, null, null);
  }

  /** Populates the first OME/Image/CA/StageLabel element's X attribute. */
  public static Object setStageX(Object root, float x) {
    return setStageLabel(root, null, new Float(x), null, null);
  }

  /** Populates the first OME/Image/CA/StageLabel element's Y attribute. */
  public static Object setStageY(Object root, float y) {
    return setStageLabel(root, null, null, new Float(y), null);
  }

  /** Populates the first OME/Image/CA/StageLabel element's Z attribute. */
  public static Object setStageZ(Object root, float z) {
    return setStageLabel(root, null, null, null, new Float(z));
  }


  // -- OMETools API methods - individual attribute retrieval --


  /** Gets the first OME/Image element's Name attribute. */
  public static String getImageName(Object root) {
    return getAttribute(root, "Image", "Name");
  }

  /** Gets the first OME/Image element's CreationDate attribute. */
  public static String getCreationDate(Object root) {
    return getAttribute(root, "Image", "CreationDate");
  }

  /** Gets the first OME/Image element's Description attribute. */
  public static String getDescription(Object root, String description) {
    return getAttribute(root, "Image", "Description");
  }

  /** Gets the first OME/Image/CA/Dimensions element's PixelSizeX attribute. */
  public static Float getPixelSizeX(Object root) {
    String pixelSizeX = getAttribute(root, "Dimensions", "PixelSizeX");
    return pixelSizeX == null ? null : new Float(pixelSizeX);
  }

  /** Gets the first OME/Image/CA/Dimensions element's PixelSizeY attribute. */
  public static Float getPixelSizeY(Object root) {
    String pixelSizeY = getAttribute(root, "Dimensions", "PixelSizeY");
    return pixelSizeY == null ? null : new Float(pixelSizeY);
  }

  /** Gets the first OME/Image/CA/Dimensions element's PixelSizeZ attribute. */
  public static Float getPixelSizeZ(Object root) {
    String pixelSizeZ = getAttribute(root, "Dimensions", "PixelSizeZ");
    return pixelSizeZ == null ? null : new Float(pixelSizeZ);
  }

  /** Gets the first OME/Image/CA/Dimensions element's PixelSizeC attribute. */
  public static Float getPixelSizeC(Object root) {
    String pixelSizeC = getAttribute(root, "Dimensions", "PixelSizeC");
    return pixelSizeC == null ? null : new Float(pixelSizeC);
  }

  /** Gets the first OME/Image/CA/Dimensions element's PixelSizeT attribute. */
  public static Float getPixelSizeT(Object root) {
    String pixelSizeT = getAttribute(root, "Dimensions", "PixelSizeT");
    return pixelSizeT == null ? null : new Float(pixelSizeT);
  }

  /** Gets the first OME/Image/CA/Pixels element's SizeX attribute. */
  public static Integer getSizeX(Object root) {
    String sizeX = getAttribute(root, "Pixels", "SizeX");
    return sizeX == null ? null : new Integer(sizeX);
  }

  /** Gets the first OME/Image/CA/Pixels element's SizeY attribute. */
  public static Integer getSizeY(Object root) {
    String sizeY = getAttribute(root, "Pixels", "SizeY");
    return sizeY == null ? null : new Integer(sizeY);
  }

  /** Gets the first OME/Image/CA/Pixels element's SizeZ attribute. */
  public static Integer getSizeZ(Object root) {
    String sizeZ = getAttribute(root, "Pixels", "SizeZ");
    return sizeZ == null ? null : new Integer(sizeZ);
  }

  /** Gets the first OME/Image/CA/Pixels element's SizeC attribute. */
  public static Integer getSizeC(Object root) {
    String sizeC = getAttribute(root, "Pixels", "SizeC");
    return sizeC == null ? null : new Integer(sizeC);
  }

  /** Gets the first OME/Image/CA/Pixels element's SizeT attribute. */
  public static Integer getSizeT(Object root) {
    String sizeT = getAttribute(root, "Pixels", "SizeT");
    return sizeT == null ? null : new Integer(sizeT);
  }

  /** Gets the first OME/Image/CA/Pixels element's PixelType attribute. */
  public static String getPixelType(Object root) {
    return getAttribute(root, "Pixels", "PixelType");
  }

  /** Gets the first OME/Image/CA/Pixels element's BigEndian attribute. */
  public static Boolean getBigEndian(Object root) {
    String bigEndian = getAttribute(root, "Pixels", "BigEndian");
    return bigEndian == null ? null :
      new Boolean(bigEndian.equalsIgnoreCase("true"));
  }

  /**
   * Gets the first OME/Image/CA/Pixels element's DimensionOrder attribute.
   */
  public static String getDimensionOrder(Object root) {
    return getAttribute(root, "Pixels", "DimensionOrder");
  }

  /** Gets the first OME/Image/CA/StageLabel element's Name attribute. */
  public static String getStageName(Object root, String name) {
    return getAttribute(root, "StageLabel", "Name");
  }

  /** Gets the first OME/Image/CA/StageLabel element's X attribute. */
  public static Float getStageX(Object root) {
    String stageX = getAttribute(root, "StageLabel", "X");
    return stageX == null ? null : new Float(stageX);
  }

  /** Gets the first OME/Image/CA/StageLabel element's Y attribute. */
  public static Float getStageY(Object root) {
    String stageY = getAttribute(root, "StageLabel", "Y");
    return stageY == null ? null : new Float(stageY);
  }

  /** Gets the first OME/Image/CA/StageLabel element's Z attribute. */
  public static Float getStageZ(Object root) {
    String stageZ = getAttribute(root, "StageLabel", "Z");
    return stageZ == null ? null : new Float(stageZ);
  }


  // -- Helper methods --

  /**
   * Retrieves the first node associated with the given DOM element name,
   * creating it if it does not already exist.
   */
  private static Object getChild(Object base, String name)
    throws ReflectException
  {
    R.setVar("base", base);
    R.setVar("name", name);
    Object child = R.exec("base.getChild(name)");
    if (child == null) child = R.exec("new " + name + "Node(base)");
    return child;
  }

  /**
   * Gets the value of the given attribute in the first occurence of the
   * specified node.
   * @return the value of the attribute.
   */
  private static String getAttribute(Object root, String nodeName, String name)
  {
    if (R == null || root == null) return null;
    R.setVar("root", root);
    try {
      // get the node
      Object node = findNode(root, nodeName);
      if (node == null) return null;

      // get the attribute
      R.setVar("node", node);
      R.setVar("name", name);
      R.exec("attr = node.getAttribute(name)");
      String attr = ((String) R.getVar("attr"));
      return attr;
    }
    catch (ReflectException exc) { exc.printStackTrace(); }
    return null;
  }

  /** Retrieves the first node associated with the given DOM element name. */
  private static Object findNode(Object root, String name)
    throws ReflectException
  {
    if (R == null || root == null || name == null) return null;
    R.setVar("root", root);
    R.setVar("name", name);
    Element rel = (Element) R.exec("root.getDOMElement()");
    R.setVar("doc", rel.getOwnerDocument());
    R.exec("el = DOMUtil.findElement(name, doc)");
    return R.exec("OMEXMLNode.createNode(el)");
  }

}
