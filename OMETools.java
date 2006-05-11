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
import java.util.Vector;

/**
 * A utility class for constructing and manipulating OME-XML DOMs. It uses
 * reflection to access the org.openmicroscopy.xml package so that the class
 * compiles if the org.openmicroscopy.xml package is unavailable.
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
      r.exec("import org.openmicroscopy.xml.CustomAttributesNode");
      r.exec("import org.openmicroscopy.xml.DOMUtil");
      r.exec("import org.openmicroscopy.xml.ImageNode");
      r.exec("import org.openmicroscopy.xml.OMENode");
      r.exec("import org.openmicroscopy.xml.OMEXMLNode");
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
    return setImage(root, name, creationDate, description, 0);
  }

  /** Populates the nth OME/Image element. */
  public static Object setImage(Object root, String name,
    String creationDate, String description, int n)
  {
    if (root == null) return null;
    Object image = null;
    try {
      R.setVar("ome", root);
      R.setVar("name", name);
      R.setVar("creationDate", creationDate);
      R.setVar("description", description);

      image = getChild(root, "Image", n);
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
    return setExperimenter(root, firstName, lastName, email, institution,
      dataDirectory, group, 0);
  }

  /** Populates the nth OME/CA/Experimenter element. */
  public static Object setExperimenter(Object root, String firstName,
    String lastName, String email, String institution, String dataDirectory,
    Object group, int n)
  {
    if (root == null) return null;
    Object experimenter = null;
    try {
      R.setVar("ome", root);
      R.setVar("firstName", firstName);
      R.setVar("lastName", lastName);
      R.setVar("email", email);
      R.setVar("institution", institution);
      R.setVar("dataDirectory", dataDirectory);
      R.setVar("group", group);

      Object ca = getChild(root, "CustomAttributes", n);
      R.setVar("ca", ca);

      R.exec("import org.openmicroscopy.xml.st.ExperimenterNode");
      experimenter = getChild(ca, "Experimenter", n);
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
    return setGroup(root, name, leader, contact, 0);
  }

  /** Populates the nth OME/CA/Group element. */
  public static Object setGroup(Object root, String name,
    Object leader, Object contact, int n)
  {
    if (root == null) return null;
    Object group = null;
    try {
      R.setVar("ome", root);
      R.setVar("name", name);
      R.setVar("leader", leader);
      R.setVar("contact", contact);

      Object ca = getChild(root, "CustomAttributes", n);
      R.setVar("ca", ca);

      R.exec("import org.openmicroscopy.xml.st.GroupNode");
      group = getChild(ca, "Group", n);
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
    return setInstrument(root, manufacturer, model, serialNumber, type, 0);
  }

  /** Populates the nth OME/CA/Instrument element. */
  public static Object setInstrument(Object root, String manufacturer,
    String model, String serialNumber, String type, int n)
  {
    if (root == null) return null;
    Object instrument = null;
    try {
      R.setVar("ome", root);
      R.setVar("manufacturer", manufacturer);
      R.setVar("model", model);
      R.setVar("serialNumber", serialNumber);
      R.setVar("type", type);

      Object ca = getChild(root, "CustomAttributes", n);
      R.setVar("ca", ca);

      R.exec("import org.openmicroscopy.xml.st.InstrumentNode");
      instrument = getChild(ca, "Instrument", n);
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
    return setDimensions(root, pixelSizeX, pixelSizeY, pixelSizeZ, pixelSizeC,
      pixelSizeT, 0);
  }

  /** Populates the nth OME/Image/CA/Dimensions element. */
  public static Object setDimensions(Object root, Float pixelSizeX,
    Float pixelSizeY, Float pixelSizeZ, Float pixelSizeC,
    Float pixelSizeT, int n)
  {
    if (root == null) return null;
    Object dimensions = null;
    try {
      R.setVar("ome", root);
      R.setVar("pixelSizeX", pixelSizeX);
      R.setVar("pixelSizeY", pixelSizeY);
      R.setVar("pixelSizeZ", pixelSizeZ);
      R.setVar("pixelSizeC", pixelSizeC);
      R.setVar("pixelSizeT", pixelSizeT);

      Object image = getChild(root, "Image", n);
      R.setVar("image", image);
      Object ca = getChild(image, "CustomAttributes", n);
      R.setVar("ca", ca);

      R.exec("import org.openmicroscopy.xml.st.DimensionsNode");
      dimensions = getChild(ca, "Dimensions", n);
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
    return setDisplayROI(root, x0, y0, z0, x1, y1, z1, t0, t1,
      displayOptions, 0);
  }

  /** Populates the nth OME/Image/CA/DisplayROI element. */
  public static Object setDisplayROI(Object root, Integer x0, Integer y0,
    Integer z0, Integer x1, Integer y1, Integer z1, Integer t0, Integer t1,
    Object displayOptions, int n)
  {
    if (root == null) return null;
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

      Object image = getChild(root, "Image", n);
      R.setVar("image", image);
      Object ca = getChild(image, "CustomAttributes", n);
      R.setVar("ca", ca);

      R.exec("import org.openmicroscopy.xml.st.DisplayROINode");
      displayROI = getChild(ca, "DisplayROI", n);
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
    return setPixels(root, sizeX, sizeY, sizeZ, sizeC, sizeT, pixelType,
      bigEndian, dimensionOrder, 0);
  }

  /** Populates the nth OME/Image/CA/Pixels element. */
  public static Object setPixels(Object root, Integer sizeX, Integer sizeY,
    Integer sizeZ, Integer sizeC, Integer sizeT, String pixelType,
    Boolean bigEndian, String dimensionOrder, int n)
  {
    if (root == null) return null;
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

      Object image = getChild(root, "Image", n);
      R.setVar("image", image);
      Object ca = getChild(image, "CustomAttributes", n);
      R.setVar("ca", ca);

      R.exec("import org.openmicroscopy.xml.st.PixelsNode");
      pixels = getChild(ca, "Pixels", n);
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
    return setStageLabel(root, name, x, y, z, 0);
  }

  /** Populates the nth OME/Image/CA/StageLabel element. */
  public static Object setStageLabel(Object root, String name,
    Float x, Float y, Float z, int n)
  {
    if (root == null) return null;
    Object stageLabel = null;
    try {
      R.setVar("ome", root);
      R.setVar("name", name);
      R.setVar("x", x);
      R.setVar("y", y);
      R.setVar("z", z);

      Object image = getChild(root, "Image", n);
      R.setVar("image", image);
      Object ca = getChild(image, "CustomAttributes", n);
      R.setVar("ca", ca);

      R.exec("import org.openmicroscopy.xml.st.StageLabelNode");
      stageLabel = getChild(ca, "StageLabel", n);
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

  /** Populates the nth OME/Image element's Name attribute. */
  public static Object setImageName(Object root, String name, int n) {
    return setImage(root, name, null, null, n);
  }

  /** Populates the first OME/Image element's CreationDate attribute. */
  public static Object setCreationDate(Object root, String creationDate) {
    return setImage(root, null, creationDate, null);
  }

  /** Populates the nth OME/Image element's CreationDate attribute. */
  public static Object setCreationDate(Object root, String creationDate, int n)
  {
    return setImage(root, null, creationDate, null, n);
  }

  /** Populates the first OME/Image element's Description attribute. */
  public static Object setDescription(Object root, String description) {
    return setImage(root, null, null, description);
  }

  /** Populates the nth OME/Image element's Description attribute. */
  public static Object setDescription(Object root, String description, int n) {
    return setImage(root, null, null, description, n);
  }

  /**
   * Populates the first OME/Image/CA/Dimensions
   * element's PixelSizeX attribute.
   */
  public static Object setPixelSizeX(Object root, float pixelSizeX) {
    return setDimensions(root, new Float(pixelSizeX), null, null, null, null);
  }

  /**
   * Populates the nth OME/Image/CA/Dimensions
   * element's PixelSizeX attribute.
   */
  public static Object setPixelSizeX(Object root, float pixelSizeX, int n) {
    return setDimensions(root, new Float(pixelSizeX), null, null, null,
      null, n);
  }

  /**
   * Populates the first OME/Image/CA/Dimensions
   * element's PixelSizeY attribute.
   */
  public static Object setPixelSizeY(Object root, float pixelSizeY) {
    return setDimensions(root, null, new Float(pixelSizeY), null, null, null);
  }

  /**
   * Populates the nth OME/Image/CA/Dimensions
   * element's PixelSizeY attribute.
   */
  public static Object setPixelSizeY(Object root, float pixelSizeY, int n) {
    return setDimensions(root, null, new Float(pixelSizeY), null,
      null, null, n);
  }

  /**
   * Populates the first OME/Image/CA/Dimensions
   * element's PixelSizeZ attribute.
   */
  public static Object setPixelSizeZ(Object root, float pixelSizeZ) {
    return setDimensions(root, null, null, new Float(pixelSizeZ), null, null);
  }

  /**
   * Populates the nth OME/Image/CA/Dimensions
   * element's PixelSizeZ attribute.
   */
  public static Object setPixelSizeZ(Object root, float pixelSizeZ, int n) {
    return setDimensions(root, null, null, new Float(pixelSizeZ),
      null, null, n);
  }

  /**
   * Populates the first OME/Image/CA/Dimensions
   * element's PixelSizeC attribute.
   */
  public static Object setPixelSizeC(Object root, float pixelSizeC) {
    return setDimensions(root, null, null, null, new Float(pixelSizeC), null);
  }

  /**
   * Populates the nth OME/Image/CA/Dimensions
   * element's PixelSizeC attribute.
   */
  public static Object setPixelSizeC(Object root, float pixelSizeC, int n) {
    return setDimensions(root, null, null, null,
      new Float(pixelSizeC), null, n);
  }

  /**
   * Populates the first OME/Image/CA/Dimensions
   * element's PixelSizeT attribute.
   */
  public static Object setPixelSizeT(Object root, float pixelSizeT) {
    return setDimensions(root, null, null, null, null, new Float(pixelSizeT));
  }

  /**
   * Populates the nth OME/Image/CA/Dimensions
   * element's PixelSizeT attribute.
   */
  public static Object setPixelSizeT(Object root, float pixelSizeT, int n) {
    return setDimensions(root, null, null, null, null,
      new Float(pixelSizeT), n);
  }

  /** Populates the first OME/Image/CA/Pixels element's SizeX attribute. */
  public static Object setSizeX(Object root, int sizeX) {
    return setPixels(root, new Integer(sizeX), null, null, null, null,
      null, null, null);
  }

  /** Populates the nth OME/Image/CA/Pixels element's SizeX attribute. */
  public static Object setSizeX(Object root, int sizeX, int n) {
    return setPixels(root, new Integer(sizeX), null, null, null, null,
      null, null, null, n);
  }

  /** Populates the first OME/Image/CA/Pixels element's SizeY attribute. */
  public static Object setSizeY(Object root, int sizeY) {
    return setPixels(root, null, new Integer(sizeY), null, null, null,
      null, null, null);
  }

  /** Populates the nth OME/Image/CA/Pixels element's SizeY attribute. */
  public static Object setSizeY(Object root, int sizeY, int n) {
    return setPixels(root, null, new Integer(sizeY), null, null, null,
      null, null, null, n);
  }

  /** Populates the first OME/Image/CA/Pixels element's SizeZ attribute. */
  public static Object setSizeZ(Object root, int sizeZ) {
    return setPixels(root, null, null, new Integer(sizeZ), null, null,
      null, null, null);
  }

  /** Populates the nth OME/Image/CA/Pixels element's SizeZ attribute. */
  public static Object setSizeZ(Object root, int sizeZ, int n) {
    return setPixels(root, null, null, new Integer(sizeZ), null, null,
      null, null, null, n);
  }

  /** Populates the first OME/Image/CA/Pixels element's SizeC attribute. */
  public static Object setSizeC(Object root, int sizeC) {
    return setPixels(root, null, null, null, new Integer(sizeC), null,
      null, null, null);
  }

  /** Populates the nth OME/Image/CA/Pixels element's SizeC attribute. */
  public static Object setSizeC(Object root, int sizeC, int n) {
    return setPixels(root, null, null, null, new Integer(sizeC), null,
      null, null, null, n);
  }

  /** Populates the first OME/Image/CA/Pixels element's SizeT attribute. */
  public static Object setSizeT(Object root, int sizeT) {
    return setPixels(root, null, null, null, null, new Integer(sizeT),
      null, null, null);
  }

  /** Populates the nth OME/Image/CA/Pixels element's SizeT attribute. */
  public static Object setSizeT(Object root, int sizeT, int n) {
    return setPixels(root, null, null, null, null, new Integer(sizeT),
      null, null, null, n);
  }

  /** Populates the first OME/Image/CA/Pixels element's PixelType attribute. */
  public static Object setPixelType(Object root, String pixelType) {
    return setPixels(root, null, null, null, null, null,
      pixelType, null, null);
  }

  /** Populates the nth OME/Image/CA/Pixels element's PixelType attribute. */
  public static Object setPixelType(Object root, String pixelType, int n) {
    return setPixels(root, null, null, null, null, null,
      pixelType, null, null, n);
  }

  /** Populates the first OME/Image/CA/Pixels element's BigEndian attribute. */
  public static Object setBigEndian(Object root, boolean bigEndian) {
    return setPixels(root, null, null, null, null, null,
      null, new Boolean(bigEndian), null);
  }

  /** Populates the nth OME/Image/CA/Pixels element's BigEndian attribute. */
  public static Object setBigEndian(Object root, boolean bigEndian, int n) {
    return setPixels(root, null, null, null, null, null,
      null, new Boolean(bigEndian), null, n);
  }

  /**
   * Populates the first OME/Image/CA/Pixels
   * element's DimensionOrder attribute.
   */
  public static Object setDimensionOrder(Object root, String dimensionOrder) {
    return setPixels(root, null, null, null, null, null,
      null, null, dimensionOrder);
  }

  /**
   * Populates the nth OME/Image/CA/Pixels
   * element's DimensionOrder attribute.
   */
  public static Object setDimensionOrder(Object root, String dimensionOrder,
    int n)
  {
    return setPixels(root, null, null, null, null, null,
      null, null, dimensionOrder, n);
  }

  /** Populates the first OME/Image/CA/StageLabel element's Name attribute. */
  public static Object setStageName(Object root, String name) {
    return setStageLabel(root, name, null, null, null);
  }

  /** Populates the nth OME/Image/CA/StageLabel element's Name attribute. */
  public static Object setStageName(Object root, String name, int n) {
    return setStageLabel(root, name, null, null, null, n);
  }

  /** Populates the first OME/Image/CA/StageLabel element's X attribute. */
  public static Object setStageX(Object root, float x) {
    return setStageLabel(root, null, new Float(x), null, null);
  }

  /** Populates the nth OME/Image/CA/StageLabel element's X attribute. */
  public static Object setStageX(Object root, float x, int n) {
    return setStageLabel(root, null, new Float(x), null, null, n);
  }

  /** Populates the first OME/Image/CA/StageLabel element's Y attribute. */
  public static Object setStageY(Object root, float y) {
    return setStageLabel(root, null, null, new Float(y), null);
  }

  /** Populates the nth OME/Image/CA/StageLabel element's Y attribute. */
  public static Object setStageY(Object root, float y, int n) {
    return setStageLabel(root, null, null, new Float(y), null, n);
  }

  /** Populates the first OME/Image/CA/StageLabel element's Z attribute. */
  public static Object setStageZ(Object root, float z) {
    return setStageLabel(root, null, null, null, new Float(z));
  }

  /** Populates the nth OME/Image/CA/StageLabel element's Z attribute. */
  public static Object setStageZ(Object root, float z, int n) {
    return setStageLabel(root, null, null, null, new Float(z), n);
  }


  // -- OMETools API methods - individual attribute retrieval --


  /** Gets the first OME/Image element's Name attribute. */
  public static String getImageName(Object root) {
    return getImageName(root, 0);
  }

  /** Gets the nth OME/Image element's Name attribute. */
  public static String getImageName(Object root, int n) {
    return getAttribute(root, "Image", "Name", n);
  }

  /** Gets the first OME/Image element's CreationDate attribute. */
  public static String getCreationDate(Object root) {
    return getCreationDate(root, 0);
  }

  /** Gets the nth OME/Image element's CreationDate attribute. */
  public static String getCreationDate(Object root, int n) {
    return getAttribute(root, "Image", "CreationDate", n);
  }

  /** Gets the first OME/Image element's Description attribute. */
  public static String getDescription(Object root, String description) {
    return getDescription(root, description, 0);
  }

  /** Gets the nth OME/Image element's Description attribute. */
  public static String getDescription(Object root, String description, int n) {
    return getAttribute(root, "Image", "Description", n);
  }

  /** Gets the first OME/Image/CA/Dimensions element's PixelSizeX attribute. */
  public static Float getPixelSizeX(Object root) {
    return getPixelSizeX(root, 0);
  }

  /** Gets the nth OME/Image/CA/Dimensions element's PixelSizeX attribute. */
  public static Float getPixelSizeX(Object root, int n) {
    String pixelSizeX = getAttribute(root, "Dimensions", "PixelSizeX", n);
    return pixelSizeX == null ? null : new Float(pixelSizeX);
  }

  /** Gets the first OME/Image/CA/Dimensions element's PixelSizeY attribute. */
  public static Float getPixelSizeY(Object root) {
    return getPixelSizeY(root, 0);
  }

  /** Gets the nth OME/Image/CA/Dimensions element's PixelSizeY attribute. */
  public static Float getPixelSizeY(Object root, int n) {
    String pixelSizeY = getAttribute(root, "Dimensions", "PixelSizeY", n);
    return pixelSizeY == null ? null : new Float(pixelSizeY);
  }

  /** Gets the first OME/Image/CA/Dimensions element's PixelSizeZ attribute. */
  public static Float getPixelSizeZ(Object root) {
    return getPixelSizeZ(root, 0);
  }

  /** Gets the nth OME/Image/CA/Dimensions element's PixelSizeZ attribute. */
  public static Float getPixelSizeZ(Object root, int n) {
    String pixelSizeZ = getAttribute(root, "Dimensions", "PixelSizeZ", n);
    return pixelSizeZ == null ? null : new Float(pixelSizeZ);
  }

  /** Gets the first OME/Image/CA/Dimensions element's PixelSizeC attribute. */
  public static Float getPixelSizeC(Object root) {
    return getPixelSizeC(root, 0);
  }

  /** Gets the nth OME/Image/CA/Dimensions element's PixelSizeC attribute. */
  public static Float getPixelSizeC(Object root, int n) {
    String pixelSizeC = getAttribute(root, "Dimensions", "PixelSizeC", n);
    return pixelSizeC == null ? null : new Float(pixelSizeC);
  }

  /** Gets the first OME/Image/CA/Dimensions element's PixelSizeT attribute. */
  public static Float getPixelSizeT(Object root) {
    return getPixelSizeT(root, 0);
  }

  /** Gets the nth OME/Image/CA/Dimensions element's PixelSizeT attribute. */
  public static Float getPixelSizeT(Object root, int n) {
    String pixelSizeT = getAttribute(root, "Dimensions", "PixelSizeT", n);
    return pixelSizeT == null ? null : new Float(pixelSizeT);
  }

  /** Gets the first OME/Image/CA/Pixels element's SizeX attribute. */
  public static Integer getSizeX(Object root) {
    return getSizeX(root, 0);
  }

  /** Gets the nth OME/Image/CA/Pixels element's SizeX attribute. */
  public static Integer getSizeX(Object root, int n) {
    String sizeX = getAttribute(root, "Pixels", "SizeX", n);
    return sizeX == null ? null : new Integer(sizeX);
  }

  /** Gets the first OME/Image/CA/Pixels element's SizeY attribute. */
  public static Integer getSizeY(Object root) {
    return getSizeY(root, 0);
  }

  /** Gets the nth OME/Image/CA/Pixels element's SizeY attribute. */
  public static Integer getSizeY(Object root, int n) {
    String sizeY = getAttribute(root, "Pixels", "SizeY", n);
    return sizeY == null ? null : new Integer(sizeY);
  }

  /** Gets the first OME/Image/CA/Pixels element's SizeZ attribute. */
  public static Integer getSizeZ(Object root) {
    return getSizeZ(root, 0);
  }

  /** Gets the nth OME/Image/CA/Pixels element's SizeZ attribute. */
  public static Integer getSizeZ(Object root, int n) {
    String sizeZ = getAttribute(root, "Pixels", "SizeZ", n);
    return sizeZ == null ? null : new Integer(sizeZ);
  }

  /** Gets the first OME/Image/CA/Pixels element's SizeC attribute. */
  public static Integer getSizeC(Object root) {
    return getSizeC(root, 0);
  }

  /** Gets the nth OME/Image/CA/Pixels element's SizeC attribute. */
  public static Integer getSizeC(Object root, int n) {
    String sizeC = getAttribute(root, "Pixels", "SizeC", n);
    return sizeC == null ? null : new Integer(sizeC);
  }

  /** Gets the first OME/Image/CA/Pixels element's SizeT attribute. */
  public static Integer getSizeT(Object root) {
    return getSizeT(root, 0);
  }

  /** Gets the nth OME/Image/CA/Pixels element's SizeT attribute. */
  public static Integer getSizeT(Object root, int n) {
    String sizeT = getAttribute(root, "Pixels", "SizeT", n);
    return sizeT == null ? null : new Integer(sizeT);
  }

  /** Gets the first OME/Image/CA/Pixels element's PixelType attribute. */
  public static String getPixelType(Object root) {
    return getPixelType(root, 0);
  }

  /** Gets the nth OME/Image/CA/Pixels element's PixelType attribute. */
  public static String getPixelType(Object root, int n) {
    return getAttribute(root, "Pixels", "PixelType", n);
  }

  /** Gets the first OME/Image/CA/Pixels element's BigEndian attribute. */
  public static Boolean getBigEndian(Object root) {
    return getBigEndian(root, 0);
  }

  /** Gets the nth OME/Image/CA/Pixels element's BigEndian attribute. */
  public static Boolean getBigEndian(Object root, int n) {
    String bigEndian = getAttribute(root, "Pixels", "BigEndian", n);
    return bigEndian == null ? null :
      new Boolean(bigEndian.equalsIgnoreCase("true"));
  }

  /**
   * Gets the first OME/Image/CA/Pixels element's DimensionOrder attribute.
   */
  public static String getDimensionOrder(Object root) {
    return getDimensionOrder(root, 0);
  }

  /**
   * Gets the nth OME/Image/CA/Pixels element's DimensionOrder attribute.
   */
  public static String getDimensionOrder(Object root, int n) {
    return getAttribute(root, "Pixels", "DimensionOrder", n);
  }

  /** Gets the first OME/Image/CA/StageLabel element's Name attribute. */
  public static String getStageName(Object root, String name) {
    return getStageName(root, name, 0);
  }

  /** Gets the nth OME/Image/CA/StageLabel element's Name attribute. */
  public static String getStageName(Object root, String name, int n) {
    return getAttribute(root, "StageLabel", "Name", n);
  }

  /** Gets the first OME/Image/CA/StageLabel element's X attribute. */
  public static Float getStageX(Object root) {
    return getStageX(root, 0);
  }

  /** Gets the nth OME/Image/CA/StageLabel element's X attribute. */
  public static Float getStageX(Object root, int n) {
    String stageX = getAttribute(root, "StageLabel", "X", n);
    return stageX == null ? null : new Float(stageX);
  }

  /** Gets the first OME/Image/CA/StageLabel element's Y attribute. */
  public static Float getStageY(Object root) {
    return getStageY(root, 0);
  }

  /** Gets the nth OME/Image/CA/StageLabel element's Y attribute. */
  public static Float getStageY(Object root, int n) {
    String stageY = getAttribute(root, "StageLabel", "Y", n);
    return stageY == null ? null : new Float(stageY);
  }

  /** Gets the first OME/Image/CA/StageLabel element's Z attribute. */
  public static Float getStageZ(Object root) {
    return getStageZ(root, 0);
  }

  /** Gets the nth OME/Image/CA/StageLabel element's Z attribute. */
  public static Float getStageZ(Object root, int n) {
    String stageZ = getAttribute(root, "StageLabel", "Z", n);
    return stageZ == null ? null : new Float(stageZ);
  }


  // -- Helper methods --

  /**
   * Retrieves the specified node associated with the given DOM element name,
   * creating it if it does not already exist.
   */
  private static Object getChild(Object base, String name, int n)
    throws ReflectException
  {
    R.setVar("base", base);
    R.setVar("name", name);
    Vector children = (Vector) R.exec("base.getChildren(name)");
    if (n < children.size()) {
      if (children.get(n) != null) return children.get(n);
    }
    Object child = R.exec("new " + name + "Node(base)");
    return child;
  }

  /**
   * Gets the value of the given attribute in the nth occurence of the
   * specified node.
   * @return the value of the attribute.
   */
  private static String getAttribute(Object root, String nodeName,
    String name, int n)
  {
    if (R == null || root == null) return null;
    R.setVar("root", root);
    try {
      // get the node
      Object node = findNode(root, nodeName, n);
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

  /**
   * Retrieves the specified node associated
   * with the given DOM element name.
   */
  private static Object findNode(Object root, String name, int n)
    throws ReflectException
  {
    if (R == null || root == null || name == null) return null;
    R.setVar("root", root);
    R.setVar("name", name);
    Element rel = (Element) R.exec("root.getDOMElement()");
    R.setVar("doc", rel.getOwnerDocument());
    R.exec("el = DOMUtil.findElementList(name, doc)");
    Vector elements = (Vector) R.getVar("el");
    Object element = elements.get(n);
    R.setVar("el", element);
    return R.exec("OMEXMLNode.createNode(el)");
  }

}
