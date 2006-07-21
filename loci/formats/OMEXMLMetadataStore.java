//
// OMEXMLMetadataStore.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.openmicroscopy.xml.*;
import org.openmicroscopy.xml.st.*;
import org.w3c.dom.Element;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 * A utility class for constructing and manipulating OME-XML DOMs. It requires
 * the org.openmicroscopy.xml package to compile (part of ome-java.jar).
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class OMEXMLMetadataStore implements MetadataStore {

  // -- Static fields --

  /** Logger for this class. */
  //private static Log log = LogFactory.getLog(OMEXMLMetadataStore.class);

  // -- Fields --

  /** The root element of OME-XML. */
  private OMENode root;

  /** Each channel's global minimum. */
  private double[] channelMinimum;

  /** Each channel's global maximum. */
  private double[] channelMaximum;

  // -- Constructor --

  /** Creates a new instance. */
  public OMEXMLMetadataStore() throws UnsupportedMetadataStoreException { }

  // -- OMEXMLMetadataStore methods --

  /** Constructs a new OME-XML root node with the given XML block. */
  public void createRoot(String xml) {
    try { root = xml == null ? new OMENode() : new OMENode(xml); }
    catch (TransformerException exc) { exc.printStackTrace(); }
    catch (SAXException exc) { exc.printStackTrace(); }
    catch (ParserConfigurationException exc) { exc.printStackTrace(); }
    catch (IOException exc) { exc.printStackTrace(); }
  }

  /**
   * Dumps the given OME-XML DOM tree to a string.
   * @return OME-XML as a string.
   */
  public String dumpXML() {
    try { return root == null ? null : root.writeOME(false); }
    catch (TransformerException exc) { exc.printStackTrace(); }
    catch (SAXException exc) { exc.printStackTrace(); }
    catch (ParserConfigurationException exc) { exc.printStackTrace(); }
    catch (IOException exc) { exc.printStackTrace(); }
    return null;
  }

  /**
   * Gets the nth OME/Image/CA/Pixels element's SizeX attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Integer getSizeX(Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    String sizeX = getAttribute("Pixels", "SizeX", ndx);
    return sizeX == null ? null : new Integer(sizeX);
  }

  /**
   * Gets the nth OME/Image/CA/Pixels element's SizeY attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Integer getSizeY(Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    String sizeY = getAttribute("Pixels", "SizeY", ndx);
    return sizeY == null ? null : new Integer(sizeY);
  }

  /**
   * Gets the nth OME/Image/CA/Pixels element's SizeZ attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Integer getSizeZ(Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    String sizeZ = getAttribute("Pixels", "SizeZ", ndx);
    return sizeZ == null ? null : new Integer(sizeZ);
  }

  /**
   * Gets the nth OME/Image/CA/Pixels element's SizeC attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Integer getSizeC(Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    String sizeC = getAttribute("Pixels", "SizeC", ndx);
    return sizeC == null ? null : new Integer(sizeC);
  }

  /**
   * Gets the nth OME/Image/CA/Pixels element's SizeT attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Integer getSizeT(Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    String sizeT = getAttribute("Pixels", "SizeT", ndx);
    return sizeT == null ? null : new Integer(sizeT);
  }

  /** Gets the nth OME/Image/CA/Pixels element's PixelType attribute. */
  public String getPixelType(Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    return getAttribute("Pixels", "PixelType", ndx);
  }

  /**
   * Gets the nth OME/Image/CA/Pixels element's BigEndian attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Boolean getBigEndian(Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    String bigEndian = getAttribute("Pixels", "BigEndian", ndx);
    return bigEndian == null ? null :
      new Boolean(bigEndian.equalsIgnoreCase("true"));
  }

  /**
   * Gets the nth OME/Image/CA/Pixels element's DimensionOrder attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public String getDimensionOrder(Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    return getAttribute("Pixels", "DimensionOrder", ndx);
  }

  // -- MetadataStore methods --

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#createRoot()
   */
  public void createRoot() { createRoot(null); }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setRoot(java.lang.Object)
   */
  public void setRoot(Object root) throws IllegalArgumentException {
    if (!(root instanceof OMENode)) {
      throw new IllegalArgumentException(
        "This metadata store accepts root objects of type 'OMENode'.");
    }
    this.root = (OMENode) root;
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#getRoot()
   */
  public Object getRoot() { return root; }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setImage(java.lang.String,
   *   java.lang.String, java.lang.String, java.lang.Integer)
   */
  public void setImage(String name,
    String creationDate, String description, Integer i)
  {
    int ndx = i == null ? 0 : i.intValue();
    ImageNode image = (ImageNode) getChild(root, "Image", ndx);
    image.setName(name);
    image.setCreated(creationDate);
    image.setDescription(description);
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setExperimenter(java.lang.String,
   *   java.lang.String, java.lang.String, java.lang.String, java.lang.String,
   *   java.lang.Object, java.lang.Integer)
   */
  public void setExperimenter(String firstName, String lastName, String email,
    String institution, String dataDirectory, Object group, Integer i)
  {
    int ndx = i == null ? 0 : i.intValue();
    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(root, "CustomAttributes", 0);
    ExperimenterNode experimenter =
      (ExperimenterNode) getChild(ca, "Experimenter", ndx);
    experimenter.setFirstName(firstName);
    experimenter.setLastName(lastName);
    experimenter.setEmail(email);
    experimenter.setInstitution(institution);
    experimenter.setDataDirectory(dataDirectory);
    experimenter.setGroup((GroupNode) group);
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setGroup(java.lang.String,
   *   java.lang.Object, java.lang.Object, java.lang.Integer)
   */
  public void setGroup(String name, Object leader, Object contact, Integer i) {
    int ndx = i == null ? 0 : i.intValue();
    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(root, "CustomAttributes", 0);
    GroupNode group = (GroupNode) getChild(ca, "Group", ndx);
    group.setName(name);
    group.setLeader((ExperimenterNode) leader);
    group.setContact((ExperimenterNode) contact);
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setInstrument(java.lang.String,
   *   java.lang.String, java.lang.String, java.lang.String, java.lang.Integer)
   */
  public void setInstrument(String manufacturer,
    String model, String serialNumber, String type, Integer i)
  {
    int ndx = i == null ? 0 : i.intValue();
    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(root, "CustomAttributes", 0);
    InstrumentNode instrument =
      (InstrumentNode) getChild(ca, "Instrument", ndx);
    instrument.setManufacturer(manufacturer);
    instrument.setModel(model);
    instrument.setSerialNumber(serialNumber);
    instrument.setType(type);
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setDimensions(java.lang.Float,
   *   java.lang.Float, java.lang.Float, java.lang.Float, java.lang.Float,
   *   java.lang.Integer)
   */
  public void setDimensions(Float pixelSizeX, Float pixelSizeY,
    Float pixelSizeZ, Float pixelSizeC, Float pixelSizeT, Integer i)
  {
    int ndx = i == null ? 0 : i.intValue();
    // TODO find a better way to specify which Dimensions we want
    ImageNode image = (ImageNode) getChild(root, "Image", 0);
    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(image, "CustomAttributes", 0);
    DimensionsNode dimensions =
      (DimensionsNode) getChild(ca, "Dimensions", ndx);
    dimensions.setPixelSizeX(pixelSizeX);
    dimensions.setPixelSizeY(pixelSizeY);
    dimensions.setPixelSizeZ(pixelSizeZ);
    dimensions.setPixelSizeC(pixelSizeC);
    dimensions.setPixelSizeT(pixelSizeT);
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setDisplayROI(java.lang.Integer,
   *   java.lang.Integer, java.lang.Integer, java.lang.Integer,
   *   java.lang.Integer, java.lang.Integer, java.lang.Integer,
   *   java.lang.Integer, java.lang.Object, java.lang.Integer)
   */
  public void setDisplayROI(Integer x0, Integer y0, Integer z0,
    Integer x1, Integer y1, Integer z1, Integer t0, Integer t1,
    Object displayOptions, Integer i)
  {
    int ndx = i == null ? 0 : i.intValue();
    // TODO find a better way to specify which DisplayROI we want
    ImageNode image = (ImageNode) getChild(root, "Image", 0);
    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(image, "CustomAttributes", 0);
    DisplayROINode displayROI = (DisplayROINode) getChild(ca, "DisplayROI", 0);
    displayROI.setX0(x0);
    displayROI.setY0(y0);
    displayROI.setZ0(z0);
    displayROI.setX1(x1);
    displayROI.setY1(y1);
    displayROI.setZ1(z1);
    displayROI.setT0(t0);
    displayROI.setT1(t1);
    displayROI.setDisplayOptions((DisplayOptionsNode) displayOptions);
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setPixels(java.lang.Integer,
   *   java.lang.Integer, java.lang.Integer, java.lang.Integer,
   *   java.lang.Integer, java.lang.String, java.lang.Boolean,
   *   java.lang.String, java.lang.Integer)
   */
  public void setPixels(Integer sizeX, Integer sizeY, Integer sizeZ,
    Integer sizeC, Integer sizeT, String pixelType, Boolean bigEndian,
    String dimensionOrder, Integer i)
  {
    int ndx = i == null ? 0 : i.intValue();
    // TODO find a better way to specify which Pixels we want
    ImageNode image = (ImageNode) getChild(root, "Image", 0);
    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(image, "CustomAttributes", 0);
    PixelsNode pixels = (PixelsNode) getChild(ca, "Pixels", ndx);
    pixels.setSizeX(sizeX);
    pixels.setSizeY(sizeY);
    pixels.setSizeZ(sizeZ);
    pixels.setSizeC(sizeC);
    pixels.setSizeT(sizeT);
    pixels.setPixelType(pixelType);
    pixels.setBigEndian(bigEndian);
    pixels.setDimensionOrder(dimensionOrder);
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setStageLabel(java.lang.String,
   *   java.lang.Float, java.lang.Float, java.lang.Float, java.lang.Integer)
   */
  public void setStageLabel(String name, Float x, Float y, Float z, Integer i) {
    int ndx = i == null ? 0 : i.intValue();
    // TODO find a better way to specify which StageLabel we want
    ImageNode image = (ImageNode) getChild(root, "Image", 0);
    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(root, "CustomAttributes", 0);
    StageLabelNode stageLabel =
      (StageLabelNode) getChild(ca, "StageLabel", ndx);
    stageLabel.setName(name);
    stageLabel.setX(x);
    stageLabel.setY(y);
    stageLabel.setZ(z);
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setLogicalChannel(int, java.lang.String,
   *   java.lang.Float, java.lang.Integer, java.lang.Integer, java.lang.String,
   *   java.lang.String, java.lang.Integer)
   */
  public void setLogicalChannel(int channelIdx, String name, Float ndFilter,
    Integer emWave, Integer exWave, String photometricInterpretation,
    String mode, Integer i)
  {
    int ndx = i == null ? 0 : i.intValue();
    // TODO find a better way to specify which LogicalChannel we want
    ImageNode image = (ImageNode) getChild(root, "Image", 0);
    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(image, "CustomAttributes", 0);
    LogicalChannelNode logicalChannel =
      (LogicalChannelNode) getChild(ca, "LogicalChannel", ndx);
    logicalChannel.setName(name);
    logicalChannel.setNDFilter(ndFilter);
    logicalChannel.setEmissionWavelength(emWave);
    logicalChannel.setExcitationWavelength(exWave);
    logicalChannel.setPhotometricInterpretation(photometricInterpretation);
    logicalChannel.setMode(mode);

    // Now populate the channel component
    /* CTR TODO FIX THIS
    r.exec("import org.openmicroscopy.xml.st.PixelChannelComponentNode");
    channel = getChild(logicalChannel, "ChannelComponent", i);
    r.setVar("channel", channel);

    // It's just waaaaay too complicated to think of dealing with multiple
    // sets of pixels per image with the API as it stands so we're using just
    // the first pixels set.
    PixelsNode pixels = (PixelsNode) getChild(image, "Pixels", 0);
    Object pixels = getChild(image, "Pixels", null);

    r.setVar("channelIdx", channelIdx);
    r.setVar("pixels", pixels);
    r.exec("channel.setIndex(channelIdx)");
    r.exec("channel.setPixels(pixels)");

    // For colour domains, we're using "R" for index 0, "G" for index 1 and
    // "B" for index 2. All other indexes are "null".
    String[] colorDomains = {"R", "G", "B"};
    channel.setColorDomain(colorDomain < colorDomains.length ?
      colorDomains[colorDomain] : null);
    */
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setChannelGlobalMinMax(int,
   *   java.lang.Double, java.lang.Double, java.lang.Integer)
   */
  public void setChannelGlobalMinMax(int channel,
    Double globalMin, Double globalMax, Integer i)
  {
    int ndx = i == null ? 0 : i.intValue();
    /* CTR TODO FIX THIS
    // Since we will need this information for default display options creation
    // but don't have a place really to store this in OME-XML we're just going
    // to store it in instance variables.
    if (channelMinimum == null) {
      Float sizeC = getPixelSizeC(i);
      if (sizeC == null) {
        //log.warn("Out of order access or missing metadata 'sizeC'.");
        return;
      }
      channelMinimum = new double[sizeC.intValue()];
    }

    if (channelMaximum == null) {
      Float sizeC = getPixelSizeC(i);
      if (sizeC == null) {
        //log.warn("Out of order access or missing metadata 'sizeC'.");
        return;
      }
      channelMaximum = new double[sizeC.intValue()];
    }

    // Now that the array initialization hocus-pocus has been completed
    // let's do the work.
    channelMinimum[channel] = globalMin.doubleValue();
    channelMaximum[channel] = globalMax.doubleValue();
    */
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setPlaneInfo(int, int, int,
   *   java.lang.Float, java.lang.Float, java.lang.Integer)
   */
  public void setPlaneInfo(int theZ, int theC, int theT, Float timestamp,
    Float exposureTime, Integer i)
  {
    int ndx = i == null ? 0 : i.intValue();
    // TODO: No-op with regards to OME-XML until the PlaneInfo type has been
    // put into the OME-XML schema.
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setDefaultDisplaySettings(
   *   java.lang.Integer)
   */
  public void setDefaultDisplaySettings(Integer i) {
    int ndx = i == null ? 0 : i.intValue();
    /* CTR TODO FIX THIS
    if (i == null) i = new Integer(0);

    Float sizeCAsFloat = getPixelSizeC(i);
    if (sizeCAsFloat == null) {
      //log.warn("Out of order access or missing metadata 'sizeC'.");
      return;
    }
    int sizeC = sizeCAsFloat.intValue();

    // Sanity check
    if (sizeC < 1) {
      //log.warn("Ignoring request for default display options: sizeC < 1.");
      return;
    }

    Object displayOptions = null;
    try {
      r.setVar("ome", root);

      Object image = getChild(root, "Image", i);
      r.setVar("image", image);

      r.exec("import org.openmicroscopy.xml.st.DisplayOptionsNode");
      displayOptions = getChild(image, "DisplayOptions", i);
      r.setVar("displayOptions", displayOptions);

      // The strategy here is to use the already populated (hopefully :))
      // global minimum and global maximum for the black and white levels.

      Object[] displayChannels =
        createRGBDisplayChannels(sizeC, displayOptions);
      Object greyscaleChannel =
        createGreyscaleDisplayChannel(displayOptions, new Integer(sizeC + 1));

      // Populate the RGB channels
      if (displayChannels.length > 0) {  // Red only
        r.setVar("channel", displayChannels[0]);
        r.exec("displayOptions.setRedChannel(channel)");
        r.exec("displayOptions.setRedChannelOn(Boolean.TRUE)");
      }
      if (displayChannels.length > 1) {  // Red and green
        r.setVar("channel", displayChannels[1]);
        r.exec("displayOptions.setGreenChannel(channel)");
        r.exec("displayOptions.setGreenChannelOn(Boolean.TRUE)");
      }
      if (displayChannels.length > 2) {  // Red, green and blue
        r.setVar("channel", displayChannels[2]);
        r.exec("displayOptions.setBlueChannel(channel)");
        r.exec("displayOptions.setBlueChannelOn(Boolean.TRUE)");
      }

      // Populate the greyscale channel
      r.setVar("channel", greyscaleChannel);
      r.exec("displayOptions.setGreyChannel(channel)");
      r.exec("displayOptions.setGreyChannelOn(Boolean.TRUE)");
    }
    catch (ReflectException e) {
      // We want potential errors in the reflected universe to be exposed to a
      // developer.
      //log.warn(e);
    }
    */
  }

  // -- Helper methods --

  /**
   * Creates default DisplayChannel nodes for use with DisplayOptions.
   * @param sizeC the number of channels.
   * @param displayOptions the display options that these display channels will
   * be used with.
   * @return an array of display channels.
   * @throws ReflectException if there is a syntax problem during execution.
   */
  private Object[] createRGBDisplayChannels(int sizeC, Object displayOptions)
    throws ReflectException
  {
    Object[] displayChannels = new Object[sizeC > 3 ? 3 : sizeC];
    /* CTR TODO FIX THIS
    r.exec("import org.openmicroscopy.xml.st.DisplayChannelNode");

    // Create each channel's display settings
    for (int i = 0; i < displayChannels.length; i++)
    {
      Object displayChannel =
        getChild(displayOptions, "DisplayChannel", new Integer(i));
      r.setVar("displayChannel", displayChannel);
      r.setVar("min", channelMinimum[i]);
      r.setVar("max", channelMaximum[i]);
      r.exec("displayChannel.setBlackLevel(min)");
      r.exec("displayChannel.setWhiteLevel(max)");
      displayChannels[i] = displayChannel;
    }
    */

    return displayChannels;
  }

  /**
   * Creates default greyscale DisplayChannel node for use with DisplayOptions.
   * @param displayOptions the display options that the display channel will be
   * used with.
   * @param i the index of display channels. Should be sizeC + 1 for the
   * greyscale DisplayChannel.
   * @return a default greyscale DisplayChannel.
   * @throws ReflectException if there is a syntax problem during execution.
   */
  private Object createGreyscaleDisplayChannel(Object displayOptions,
    Integer i) throws ReflectException
  {
    /* CTR TODO FIX THIS
    r.exec("import org.openmicroscopy.xml.st.DisplayChannelNode");

    // Create each channel's display settings
    Object displayChannel = getChild(displayOptions, "DisplayChannel", i);
    r.setVar("displayChannel", displayChannel);
    r.setVar("min", channelMinimum[0]);
    r.setVar("max", channelMaximum[0]);

    return displayChannel;
    */ return null;
  }

  /**
   * Retrieves the specified node associated with the given DOM element name,
   * creating it if it does not already exist.
   * @param base the node to retrieve the child from
   * @param name the type of node to retrieve
   * @param i the index of the node in the child list
   * @return child node object
   */
  private OMEXMLNode getChild(OMEXMLNode base, String name, int i) {
    Vector children = base.getChildren(name);
    if (i < children.size()) {
      if (children.get(i) != null) return (OMEXMLNode) children.get(i);
    }
    // CTR TODO fix this, it is crap
    try {
      Class c = Class.forName("org.openmicroscopy.xml.st." + name + "Node");
      Constructor con = c.getConstructor(new Class[] {OMEXMLNode.class});
      return (OMEXMLNode) con.newInstance(new Object[] {base});
    }
    catch (Exception exc) { exc.printStackTrace(); }
    return null;
  }

  /**
   * Gets the value of the given attribute in the nth occurence of the
   * specified node.
   * @param nodeName the name of the node to retrieve the value from
   * @param name the name of the attribute to retrieve the value from
   * @param i the index of the node in the DOM tree
   * @return the value of the attribute
   */
  private String getAttribute(String nodeName, String name, int i) {
    OMEXMLNode node = findNode(nodeName, i);
    if (node == null) return null;
    return node.getAttribute(name);
  }

  /**
   * Retrieves the specified node associated with the given DOM element name.
   * @param name the DOM element name
   * @param i the index of the DOM element
   */
  private OMEXMLNode findNode(String name, int i) {
    Element rel = root.getDOMElement();
    Vector elements = DOMUtil.findElementList(name, rel.getOwnerDocument());
    Element el = (Element) elements.get(i);
    return OMEXMLNode.createNode(el);
  }

}
