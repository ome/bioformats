//
// OMEXMLMetadataStore.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
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
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.openmicroscopy.xml.*;
import org.openmicroscopy.xml.st.*;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * A utility class for constructing and manipulating OME-XML DOMs. It requires
 * the org.openmicroscopy.xml package to compile (part of ome-java.jar).
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
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
  public OMEXMLMetadataStore() { }

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

  // -- OMEXMLMetadataStore methods - individual attribute retrieval --

  /**
   * Gets the nth OME/Image element's Name attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public String getImageName(Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    return getAttribute("Image", "Name", ndx);
  }

  /**
   * Gets the nth OME/Image element's CreationDate attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public String getCreationDate(Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    return getAttribute("Image", "CreationDate", ndx);
  }

  /**
   * Gets the nth OME/Image element's Description attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public String getDescription(String description, Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    return getAttribute("Image", "Description", ndx);
  }

  /**
   * Gets the nth OME/Image/CA/Dimensions element's PixelSizeX attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Float getPixelSizeX(Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    String pixelSizeX = getAttribute("Dimensions", "PixelSizeX", ndx);
    return pixelSizeX == null ? null : new Float(pixelSizeX);
  }

  /**
   * Gets the nth OME/Image/CA/Dimensions element's PixelSizeY attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Float getPixelSizeY(Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    String pixelSizeY = getAttribute("Dimensions", "PixelSizeY", ndx);
    return pixelSizeY == null ? null : new Float(pixelSizeY);
  }

  /**
   * Gets the nth OME/Image/CA/Dimensions element's PixelSizeZ attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Float getPixelSizeZ(Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    String pixelSizeZ = getAttribute("Dimensions", "PixelSizeZ", ndx);
    return pixelSizeZ == null ? null : new Float(pixelSizeZ);
  }

  /**
   * Gets the nth OME/Image/CA/Dimensions element's PixelSizeC attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Float getPixelSizeC(Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    String pixelSizeC = getAttribute("Dimensions", "PixelSizeC", ndx);
    return pixelSizeC == null ? null : new Float(pixelSizeC);
  }

  /**
   * Gets the nth OME/Image/CA/Dimensions element's PixelSizeT attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Float getPixelSizeT(Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    String pixelSizeT = getAttribute("Dimensions", "PixelSizeT", ndx);
    return pixelSizeT == null ? null : new Float(pixelSizeT);
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

  /**
   * Gets the nth OME/Image/CA/StageLabel element's Name attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public String getStageName(String name, Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    return getAttribute("StageLabel", "Name", ndx);
  }

  /**
   * Gets the nth OME/Image/CA/StageLabel element's X attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Float getStageX(Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    String stageX = getAttribute("StageLabel", "X", ndx);
    return stageX == null ? null : new Float(stageX);
  }

  /**
   * Gets the nth OME/Image/CA/StageLabel element's Y attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Float getStageY(Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    String stageY = getAttribute("StageLabel", "Y", ndx);
    return stageY == null ? null : new Float(stageY);
  }

  /**
   * Gets the nth OME/Image/CA/StageLabel element's Z attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Float getStageZ(Integer n) {
    int ndx = n == null ? 0 : n.intValue();
    String stageZ = getAttribute("StageLabel", "Z", ndx);
    return stageZ == null ? null : new Float(stageZ);
  }

  // -- MetadataStore methods --

  /* @see MetadataStore#createRoot() */
  public void createRoot() { createRoot(null); }

  /* @see MetadataStore#setRoot(Object) */
  public void setRoot(Object root) {
    if (!(root instanceof OMENode)) {
      throw new IllegalArgumentException(
        "This metadata store accepts root objects of type 'OMENode'.");
    }
    this.root = (OMENode) root;
  }

  /* @see MetadataStore#getRoot() */
  public Object getRoot() { return root; }

  /* @see MetadataStore#setImage(String, String, String, Integer) */
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
   * @see MetadataStore#setExperimenter(String, String,
   *   String, String, String, Object, Integer)
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

  /* @see MetadataStore#setGroup(String, Object, Object, Integer) */
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
   * @see MetadataStore#setInstrument(String, String, String, String, Integer)
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
   * @see MetadataStore#setDimensions(Float,
   *   Float, Float, Float, Float, Integer)
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
   * @see MetadataStore#setDisplayROI(Integer, Integer, Integer,
   *   Integer, Integer, Integer, Integer, Integer, Object, Integer)
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
    DisplayROINode displayROI = (DisplayROINode)
      getChild(ca, "DisplayROI", ndx);
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
   * @see MetadataStore#setPixels(Integer, Integer, Integer,
   *   Integer, Integer, String, Boolean, String, Integer)
   */
  public void setPixels(Integer sizeX, Integer sizeY, Integer sizeZ,
    Integer sizeC, Integer sizeT, Integer pixelType, Boolean bigEndian,
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
    pixels.setPixelType(pixelTypeAsString(pixelType));
    pixels.setBigEndian(bigEndian);
    pixels.setDimensionOrder(dimensionOrder);
    if (ndx == 0) {
      // assign Pixels as default for the Image
      image.setDefaultPixels(pixels);
    }
  }

  /* @see MetadataStore#setStageLabel(String, Float, Float, Float, Integer) */
  public void setStageLabel(String name, Float x, Float y, Float z, Integer i) {
    int ndx = i == null ? 0 : i.intValue();
    // TODO find a better way to specify which StageLabel we want
    ImageNode image = (ImageNode) getChild(root, "Image", 0);
    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(image, "CustomAttributes", 0);
    StageLabelNode stageLabel =
      (StageLabelNode) getChild(ca, "StageLabel", ndx);
    stageLabel.setName(name);
    stageLabel.setX(x);
    stageLabel.setY(y);
    stageLabel.setZ(z);
  }

  /*
   * @see MetadataStore#setLogicalChannel(int, String,
   *   Float, Integer, Integer, String, String, Integer)
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
    PixelChannelComponentNode channel = (PixelChannelComponentNode)
      getChild(ca, "PixelChannelComponent", channelIdx);
    channel.setLogicalChannel(logicalChannel);
    channel.setIndex(new Integer(channelIdx));

    // It's just waaaaay too complicated to think of dealing with multiple
    // sets of pixels per image with the API as it stands so we're using just
    // the first pixels set.
    PixelsNode pixels = (PixelsNode) getChild(ca, "Pixels", 0);
    channel.setPixels(pixels);

    // For colour domains, we're using "R" for index 0, "G" for index 1 and
    // "B" for index 2. All other indexes are "null".
    String[] colorDomains = {"R", "G", "B"};
    channel.setColorDomain(channelIdx < colorDomains.length ?
      colorDomains[channelIdx] : null);
  }

  /* @see MetadataStore#setChannelGlobalMinMax(int, Double, Double, Integer) */
  public void setChannelGlobalMinMax(int channel,
    Double globalMin, Double globalMax, Integer i)
  {
    int ndx = i == null ? 0 : i.intValue();
    // Since we will need this information for default display options creation
    // but don't have a place really to store this in OME-XML we're just going
    // to store it in instance variables.
    if (channelMinimum == null) {
      Integer sizeC = getSizeC(i);
      if (sizeC == null) {
        warn("Out of order access or missing metadata 'sizeC'.");
        return;
      }
      channelMinimum = new double[sizeC.intValue()];
    }

    if (channelMaximum == null) {
      Integer sizeC = getSizeC(i);
      if (sizeC == null) {
        warn("Out of order access or missing metadata 'sizeC'.");
        return;
      }
      channelMaximum = new double[sizeC.intValue()];
    }

    // Now that the array initialization hocus-pocus has been completed
    // let's do the work.
    channelMinimum[channel] = globalMin.doubleValue();
    channelMaximum[channel] = globalMax.doubleValue();
  }

  /* @see MetadataStore#setPlaneInfo(int, int, int, Float, Float, Integer) */
  public void setPlaneInfo(int theZ, int theC, int theT, Float timestamp,
    Float exposureTime, Integer i)
  {
    int ndx = i == null ? 0 : i.intValue();
    // TODO: No-op with regards to OME-XML until the PlaneInfo type has been
    // put into the OME-XML schema.
  }

  /* @see MetadataStore#setDefaultDisplaySettings(Integer) */
  public void setDefaultDisplaySettings(Integer i) {
    int ndx = i == null ? 0 : i.intValue();

    Integer sizeCAsInteger = getSizeC(i);
    if (sizeCAsInteger == null) {
      warn("Out of order access or missing metadata 'sizeC'.");
      return;
    }
    int sizeC = sizeCAsInteger.intValue();

    // Sanity check
    if (sizeC < 1) {
      warn("Ignoring request for default display options: sizeC < 1.");
      return;
    }

    ImageNode image = (ImageNode) getChild(root, "Image", 0);
    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(image, "CustomAttributes", 0);
    DisplayOptionsNode displayOptions = (DisplayOptionsNode)
      getChild(ca, "DisplayOptions", ndx);

    // The strategy here is to use the already populated (hopefully :))
    // global minimum and global maximum for the black and white levels.

    if (channelMinimum == null) {
      warn("Out of order access or missing metadata 'channelMinimum'.");
      return;
    }
    if (channelMaximum == null) {
      warn("Out of order access or missing metadata 'channelMinimum'.");
      return;
    }

    DisplayChannelNode[] displayChannels = createRGBDisplayChannels(sizeC, ca);
    DisplayChannelNode greyscaleChannel =
      createGreyscaleDisplayChannel(ca, sizeC + 1);

    // Populate the RGB channels
    if (displayChannels.length > 0) {  // Red only
      displayOptions.setRedChannel(displayChannels[0]);
      displayOptions.setRedChannelOn(Boolean.TRUE);
    }
    if (displayChannels.length > 1) {  // Red and green
      displayOptions.setGreenChannel(displayChannels[1]);
      displayOptions.setGreenChannelOn(Boolean.TRUE);
    }
    if (displayChannels.length > 2) {  // Red, green and blue
      displayOptions.setBlueChannel(displayChannels[2]);
      displayOptions.setBlueChannelOn(Boolean.TRUE);
    }

    // Populate the greyscale channel
    displayOptions.setGreyChannel(greyscaleChannel);
  }

  // -- Helper methods --

  /**
   * Gets the OME pixel type string from the Bio-Formats enumeration.
   * @param pixelType the <i>pixel type</i> as an enumeration.
   * @return the <i>pixel type</i> as a string.
   */
  private String pixelTypeAsString(Integer pixelType) {
    if (pixelType == null) return null;

    switch (pixelType.intValue()) {
      case FormatReader.INT8:
        return "int8";
      case FormatReader.UINT8:
        return "Uint8";
      case FormatReader.INT16:
        return "int16";
      case FormatReader.UINT16:
        return "Uint16";
      case FormatReader.INT32:
        return "int32";
      case FormatReader.UINT32:
        return "Uint32";
      case FormatReader.FLOAT:
        return "float";
      case FormatReader.DOUBLE:
        return "double";
    }
    throw new RuntimeException("Unknown pixel type: " + pixelType);
  }

  /**
   * Creates default DisplayChannel nodes for use with DisplayOptions.
   * @param sizeC the number of channels.
   * @return an array of display channels.
   */
  private DisplayChannelNode[] createRGBDisplayChannels(int sizeC,
    CustomAttributesNode parent)
  {
    DisplayChannelNode[] displayChannels =
      new DisplayChannelNode[sizeC > 3 ? 3 : sizeC];

    // Create each channel's display settings
    for (int i = 0; i < displayChannels.length; i++) {
      DisplayChannelNode displayChannel = (DisplayChannelNode)
        getChild(parent, "DisplayChannel", i);
      displayChannel.setChannelNumber(new Integer(i));
      displayChannel.setBlackLevel(new Double(channelMinimum[i]));
      displayChannel.setWhiteLevel(new Double(channelMaximum[i]));
      displayChannels[i] = displayChannel;
    }

    return displayChannels;
  }

  /**
   * Creates default greyscale DisplayChannel node for use with DisplayOptions.
   * @param displayOptions the display options that the display channel will be
   * used with.
   * @param i the index of display channels. Should be sizeC + 1 for the
   * greyscale DisplayChannel.
   * @return a default greyscale DisplayChannel.
   */
  private DisplayChannelNode createGreyscaleDisplayChannel(
    CustomAttributesNode parent, int i)
  {
    // Create each channel's display settings
    DisplayChannelNode displayChannel = (DisplayChannelNode)
      getChild(parent, "DisplayChannel", i);
    displayChannel.setBlackLevel(new Double(channelMinimum[0]));
    displayChannel.setWhiteLevel(new Double(channelMaximum[0]));
    return displayChannel;
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
    String className;
    Class param;
    if (name.equals("CustomAttributes") || name.equals("Dataset") ||
      name.equals("Feature") || name.equals("Image") || name.equals("OME") ||
      name.equals("Project"))
    {
      className = "org.openmicroscopy.xml." + name + "Node";
      param = base.getClass();
    }
    else {
      className = "org.openmicroscopy.xml.st." + name + "Node";
      param = CustomAttributesNode.class;
    }
    try {
      Class c = Class.forName(className);
      Constructor con = c.getConstructor(new Class[] {param});
      return (OMEXMLNode) con.newInstance(new Object[] {base});
    }
    catch (Exception exc) { exc.printStackTrace(); }
    return null;
  }

  /**
   * Gets the value of the given attribute in the nth occurrence of the
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
    if (elements == null || i >= elements.size()) return null;
    Element el = (Element) elements.get(i);
    return OMEXMLNode.createNode(el);
  }

  /** Issues the given message as a warning. */
  private void warn(String msg) {
    //log.warn(msg);
    System.err.println(msg);
  }

}
