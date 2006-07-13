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
public class OMEXMLMetadataStore implements MetadataStore {

  // -- Constants --

  /**
   * The reflected universe that is used to avoid direct compile-time
   * dependency on the org.openmicroscopy.xml package.
   */
  private ReflectedUniverse r;

  /** The root element of OME-XML (org.openmicroscopy.xml.OMENode) */
  private Object root;

  /** Each channel's global minimum */
  private double[] channelMinimum;

  private double[] channelMaximum;

  /** Logger for this class. */
  //private static Log log = LogFactory.getLog(OMEXMLMetadataStore.class);

  /**
   * Creates a new instance.
   * @throws UnsupportedMetadataStoreException if the constructor is unable to
   * import pre-requisites from the org.openmicroscopy.org.xml.
   */
  public OMEXMLMetadataStore() throws UnsupportedMetadataStoreException {
    r = new ReflectedUniverse();
    try {
      r.exec("import org.openmicroscopy.xml.CustomAttributesNode");
      r.exec("import org.openmicroscopy.xml.DOMUtil");
      r.exec("import org.openmicroscopy.xml.ImageNode");
      r.exec("import org.openmicroscopy.xml.OMENode");
      r.exec("import org.openmicroscopy.xml.OMEXMLNode");
      r.setVar("FALSE", false);
    }
    catch (Throwable t) {
      throw new UnsupportedMetadataStoreException(t);
    }
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#createRoot()
   */
  public void createRoot() { createRoot(null); }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#getRoot()
   */
  public Object getRoot() { return root; }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setRoot(java.lang.Object)
   */
  public void setRoot(Object root) throws IllegalArgumentException {
    // FIXME: For the moment, due to the reflection semantics it is extremely
    // difficult to do the following so I'm commenting it out.
    // if (!OMENode.isInstance(root))
    //   throw new IllegalArgumentException(
    //     "This metadata store accepts root objects of type 'OMENode'.");
    this.root = root;
  }

  /** Constructs a new OME-XML root node with the given XML block. */
  public void createRoot(String xml) {
    try {
      if (xml == null) root = r.exec("new OMENode()");
      else {
        r.setVar("xml", xml);
        root = r.exec("new OMENode(xml)");
      }
    }
    catch (ReflectException e) {
      // We want potential errors in the reflected universe to be exposed to a
      // developer.
      //log.warn(e);
    }
  }

  /**
   * Dumps the given OME-XML DOM tree to a string.
   * @return OME-XML as a string.
   */
  public String dumpXML() {
    if (root == null) return null;
    r.setVar("ome", root);
    try {
      Object s = r.exec("ome.writeOME(FALSE)");
      if (s instanceof String)
        return (String) s;
      else
        throw new RuntimeException("Object: '" + s + "' not of type String.");
    }
    catch (ReflectException e) {
      // We want potential errors in the reflected universe to be exposed to a
      // developer.
      //log.warn(e);
    }
    return null;
  }

  // -- OMETools API methods - node (element) assignment --

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setImage(java.lang.String,
   *   java.lang.String, java.lang.String, java.lang.Integer)
   */
  public void setImage(String name, String creationDate,
                       String description, Integer i)
  {
    if (i == null) i = new Integer(0);
    Object image = null;
    try {
      r.setVar("ome", root);
      r.setVar("name", name);
      r.setVar("creationDate", creationDate);
      r.setVar("description", description);

      image = getChild(root, "Image", i);
      r.setVar("image", image);

      r.exec("image.setName(name)");
      r.exec("image.setCreated(creationDate)");
      r.exec("image.setDescription(description)");
    }
    catch (ReflectException e) {
      // We want potential errors in the reflected universe to be exposed to a
      // developer.
      //log.warn(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setExperimenter(java.lang.String,
   *   java.lang.String, java.lang.String, java.lang.String, java.lang.String,
   *   java.lang.Object, java.lang.Integer)
   */
  public void setExperimenter(String firstName, String lastName, String email,
                              String institution, String dataDirectory,
                              Object group, Integer i)
  {
    if (i == null) i = new Integer(0);
    Object experimenter = null;
    try {
      r.setVar("ome", root);
      r.setVar("firstName", firstName);
      r.setVar("lastName", lastName);
      r.setVar("email", email);
      r.setVar("institution", institution);
      r.setVar("dataDirectory", dataDirectory);
      r.setVar("group", group);

      Object ca = getChild(root, "CustomAttributes", i);
      r.setVar("ca", ca);

      r.exec("import org.openmicroscopy.xml.st.ExperimenterNode");
      experimenter = getChild(ca, "Experimenter", i);
      r.setVar("experimenter", experimenter);

      r.exec("experimenter.setFirstName(firstName)");
      r.exec("experimenter.setLastName(lastName)");
      r.exec("experimenter.setEmail(email)");
      r.exec("experimenter.setInstitution(institution)");
      r.exec("experimenter.setDataDirectory(dataDirectory)");
      r.exec("experimenter.setGroup(group)");
    }
    catch (ReflectException e) {
      // We want potential errors in the reflected universe to be exposed to a
      // developer.
      //log.warn(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setGroup(java.lang.String,
   *   java.lang.Object, java.lang.Object, java.lang.Integer)
   */
  public void setGroup(String name, Object leader, Object contact, Integer i)
  {
    if (i == null) i = new Integer(0);
    Object group = null;
    try {
      r.setVar("ome", root);
      r.setVar("name", name);
      r.setVar("leader", leader);
      r.setVar("contact", contact);

      Object ca = getChild(root, "CustomAttributes", i);
      r.setVar("ca", ca);

      r.exec("import org.openmicroscopy.xml.st.GroupNode");
      group = getChild(ca, "Group", i);
      r.setVar("group", group);

      r.exec("group.setName(name)");
      r.exec("group.setLeader(leader)");
      r.exec("group.setContact(contact)");
    }
    catch (ReflectException e) {
      // We want potential errors in the reflected universe to be exposed to a
      // developer.
      //log.warn(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setInstrument(java.lang.String,
   *   java.lang.String, java.lang.String, java.lang.String, java.lang.Integer)
   */
  public void setInstrument(String manufacturer, String model,
                            String serialNumber, String type, Integer i)
  {
    if (i == null) i = new Integer(0);
    Object instrument = null;
    try {
      r.setVar("ome", root);
      r.setVar("manufacturer", manufacturer);
      r.setVar("model", model);
      r.setVar("serialNumber", serialNumber);
      r.setVar("type", type);

      Object ca = getChild(root, "CustomAttributes", i);
      r.setVar("ca", ca);

      r.exec("import org.openmicroscopy.xml.st.InstrumentNode");
      instrument = getChild(ca, "Instrument", i);
      r.setVar("instrument", instrument);

      r.exec("instrument.setManufacturer(manufacturer)");
      r.exec("instrument.setModel(model)");
      r.exec("instrument.setSerialNumber(serialNumber)");
      r.exec("instrument.setType(type)");
    }
    catch (ReflectException e) {
      // We want potential errors in the reflected universe to be exposed to a
      // developer.
      //log.warn(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setDimensions(java.lang.Float,
   *   java.lang.Float, java.lang.Float, java.lang.Float, java.lang.Float,
   *   java.lang.Integer)
   */
  public void setDimensions(Float pixelSizeX, Float pixelSizeY,
                            Float pixelSizeZ, Float pixelSizeC,
                            Float pixelSizeT, Integer i)
  {
    if (i == null) i = new Integer(0);
    Object dimensions = null;
    try {
      r.setVar("ome", root);
      r.setVar("pixelSizeX", pixelSizeX);
      r.setVar("pixelSizeY", pixelSizeY);
      r.setVar("pixelSizeZ", pixelSizeZ);
      r.setVar("pixelSizeC", pixelSizeC);
      r.setVar("pixelSizeT", pixelSizeT);

      Object image = getChild(root, "Image", i);
      r.setVar("image", image);
      Object ca = getChild(image, "CustomAttributes", i);
      r.setVar("ca", ca);

      r.exec("import org.openmicroscopy.xml.st.DimensionsNode");
      dimensions = getChild(ca, "Dimensions", i);
      r.setVar("dimensions", dimensions);

      r.exec("dimensions.setPixelSizeX(pixelSizeX)");
      r.exec("dimensions.setPixelSizeY(pixelSizeY)");
      r.exec("dimensions.setPixelSizeZ(pixelSizeZ)");
      r.exec("dimensions.setPixelSizeC(pixelSizeC)");
      r.exec("dimensions.setPixelSizeT(pixelSizeT)");
    }
    catch (ReflectException e) {
      // We want potential errors in the reflected universe to be exposed to a
      // developer.
      //log.warn(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setDisplayROI(java.lang.Integer,
   *   java.lang.Integer, java.lang.Integer, java.lang.Integer,
   *   java.lang.Integer, java.lang.Integer, java.lang.Integer,
   *   java.lang.Integer, java.lang.Object, java.lang.Integer)
   */
  public void setDisplayROI(Integer x0, Integer y0, Integer z0,
                            Integer x1, Integer y1, Integer z1,
                            Integer t0, Integer t1, Object displayOptions,
                            Integer i)
  {
    if (i == null) i = new Integer(0);
    Object displayROI = null;
    try {
      r.setVar("ome", root);
      r.setVar("x0", x0);
      r.setVar("y0", y0);
      r.setVar("z0", z0);
      r.setVar("x0", x1);
      r.setVar("y0", y1);
      r.setVar("z0", z1);
      r.setVar("t0", t0);
      r.setVar("t1", t1);
      r.setVar("displayOptions", displayOptions);

      Object image = getChild(root, "Image", i);
      r.setVar("image", image);
      Object ca = getChild(image, "CustomAttributes", i);
      r.setVar("ca", ca);

      r.exec("import org.openmicroscopy.xml.st.DisplayROINode");
      displayROI = getChild(ca, "DisplayROI", i);
      r.setVar("displayROI", displayROI);

      r.exec("displayROI.setX0(x0)");
      r.exec("displayROI.setY0(y0)");
      r.exec("displayROI.setZ0(z0)");
      r.exec("displayROI.setX1(x1)");
      r.exec("displayROI.setY1(y1)");
      r.exec("displayROI.setZ1(z1)");
      r.exec("displayROI.setT0(t0)");
      r.exec("displayROI.setT1(t1)");
      r.exec("displayROI.setDisplayOptions(displayOptions)");
    }
    catch (ReflectException e) {
      // We want potential errors in the reflected universe to be exposed to a
      // developer.
      //log.warn(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setPixels(java.lang.Integer,
   *   java.lang.Integer, java.lang.Integer, java.lang.Integer,
   *   java.lang.Integer, java.lang.String, java.lang.Boolean,
   *   java.lang.String, java.lang.Integer)
   */
  public void setPixels(Integer sizeX, Integer sizeY, Integer sizeZ,
                        Integer sizeC, Integer sizeT, String pixelType,
                        Boolean bigEndian, String dimensionOrder, Integer i)
  {
    if (i == null) i = new Integer(0);
    Object pixels = null;
    try {
      r.setVar("ome", root);
      r.setVar("sizeX", sizeX);
      r.setVar("sizeY", sizeY);
      r.setVar("sizeZ", sizeZ);
      r.setVar("sizeC", sizeC);
      r.setVar("sizeT", sizeT);
      r.setVar("pixelType", pixelType);
      r.setVar("bigEndian", bigEndian);
      r.setVar("dimensionOrder", dimensionOrder);

      Object image = getChild(root, "Image", i);
      r.setVar("image", image);
      Object ca = getChild(image, "CustomAttributes", i);
      r.setVar("ca", ca);

      r.exec("import org.openmicroscopy.xml.st.PixelsNode");
      pixels = getChild(ca, "Pixels", i);
      r.setVar("pixels", pixels);

      r.exec("pixels.setSizeX(sizeX)");
      r.exec("pixels.setSizeY(sizeY)");
      r.exec("pixels.setSizeZ(sizeZ)");
      r.exec("pixels.setSizeC(sizeC)");
      r.exec("pixels.setSizeT(sizeT)");
      r.exec("pixels.setPixelType(pixelType)");
      r.exec("pixels.setBigEndian(bigEndian)");
      r.exec("pixels.setDimensionOrder(dimensionOrder)");
    }
    catch (ReflectException e) {
      // We want potential errors in the reflected universe to be exposed to a
      // developer.
      //log.warn(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setStageLabel(java.lang.String,
   *   java.lang.Float, java.lang.Float, java.lang.Float, java.lang.Integer)
   */
  public void setStageLabel(String name, Float x, Float y, Float z, Integer i)
  {
    if (i == null) i = new Integer(0);
    Object stageLabel = null;
    try {
      r.setVar("ome", root);
      r.setVar("name", name);
      r.setVar("x", x);
      r.setVar("y", y);
      r.setVar("z", z);

      Object image = getChild(root, "Image", i);
      r.setVar("image", image);
      Object ca = getChild(image, "CustomAttributes", i);
      r.setVar("ca", ca);

      r.exec("import org.openmicroscopy.xml.st.StageLabelNode");
      stageLabel = getChild(ca, "StageLabel", i);
      r.setVar("stageLabel", stageLabel);

      r.exec("stageLabel.setName(name)");
      r.exec("stageLabel.setX(x)");
      r.exec("stageLabel.setY(y)");
      r.exec("stageLabel.setZ(z)");
    }
    catch (ReflectException e) {
      // We want potential errors in the reflected universe to be exposed to a
      // developer.
      //log.warn(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setLogicalChannel(int, java.lang.String,
   *   float, int, int, java.lang.String, java.lang.String, java.lang.Integer)
   */
  public void setLogicalChannel(int channelIdx, String name, Float ndFilter,
                                Integer emWave, Integer exWave,
                                String photometricInterpretation, String mode,
                                Integer i) {
    if (i == null) i = new Integer(0);
    Object logicalChannel = null;
    Object channel = null;
    try {
      r.setVar("ome", root);
      r.setVar("name", name);
      r.setVar("ndFilter", ndFilter);
      r.setVar("emWave", emWave);
      r.setVar("exWave", exWave);
      r.setVar("pi", photometricInterpretation);
      r.setVar("mode", mode);

      Object image = getChild(root, "Image", i);
      r.setVar("image", image);

      // Populate the logical channel
      r.exec("import org.openmicroscopy.xml.st.LogicalChannelNode");
      logicalChannel = getChild(image, "ChannelInfo", i);
      r.setVar("logicalChannel", logicalChannel);

      r.exec("logicalChannel.setName(name)");
      r.exec("logicalChannel.setNdFilter(ndFilter)");
      r.exec("logicalChannel.setEmWave(emWave)");
      r.exec("logicalChannel.setExWave(exWave)");
      r.exec("logicalChannel.setPhotometricInterpretation(pi)");
      r.exec("logicalChannel.setMode(mode)");

      // Now populate the channel component
      r.exec("import org.openmicroscopy.xml.st.PixelChannelComponentNode");
      channel = getChild(logicalChannel, "ChannelComponent", i);
      r.setVar("channel", channel);

      // It's just waaaaay too complicated to think of dealing with multiple
      // sets of pixels per image with the API as it stands so we're using just
      // the first pixels set.
      Object pixels = getChild(image, "Pixels", null);

      r.setVar("channelIdx", channelIdx);
      r.setVar("pixels", pixels);
      r.exec("channel.setIndex(channelIdx)");
      r.exec("channel.setPixels(pixels)");

      // For colour domains, we're using "R" for index 0, "G" for index 1 and
      // "B" for index 2. All other indexes are "null".
      switch (channelIdx) {
      case 0:
        r.setVar("colorDomain", "R");
        break;
      case 1:
        r.setVar("colorDomain", "G");
        break;
      case 2:
        r.setVar("colorDomain", "B");
        break;
      default:
        r.setVar("colorDomain", null);
      }
      r.exec("channel.setColorDomain(colorDomain)");
    }
    catch (ReflectException e) {
      // We want potential errors in the reflected universe to be exposed to a
      // developer.
      //log.warn(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setChannelGlobalMinMax(int,
   *   java.lang.Double, java.lang.Double, java.lang.Integer)
   */
  public void setChannelGlobalMinMax(int channel, Double globalMin,
                                     Double globalMax, Integer i) {
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

    // Now that the array initialization hocus-pocus has been completed lets do
    // the work.
    channelMinimum[channel] = globalMin.doubleValue();
    channelMaximum[channel] = globalMax.doubleValue();
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setPlaneInfo(int, int, int,
   *   java.lang.Float, java.lang.Float, java.lang.Integer)
   */
  public void setPlaneInfo(int theZ, int theC, int theT, Float timestamp,
                           Float exposureTime, Integer i) {
    // TODO: No-op with regards to OME-XML until the PlaneInfo type has been
    // put into the OME-XML schema.
  }

  /*
   * (non-Javadoc)
   * @see loci.formats.MetadataStore#setDefaultDisplaySettings(
   *   java.lang.Integer)
   */
  public void setDefaultDisplaySettings(Integer i) {
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
  }

  /**
   * Creates default DisplayChannel nodes for use with DisplayOptions.
   * @param sizeC the number of channels.
   * @param displayOptions the display options that these display channels will
   * be used with.
   * @return an array of display channels.
   * @throws ReflectException if there is a syntax problem during execution.
   */
  private Object[]
  createRGBDisplayChannels(int sizeC, Object displayOptions)
    throws ReflectException {
    Object[] displayChannels = new Object[sizeC > 3? 3 : sizeC];
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
  private Object createGreyscaleDisplayChannel(Object displayOptions, Integer i)
    throws ReflectException {
    r.exec("import org.openmicroscopy.xml.st.DisplayChannelNode");

    // Create each channel's display settings
    Object displayChannel = getChild(displayOptions, "DisplayChannel", i);
    r.setVar("displayChannel", displayChannel);
    r.setVar("min", channelMinimum[0]);
    r.setVar("max", channelMaximum[0]);

    return displayChannel;
  }

  // -- OMEXMLMetadataStore API methods - individual attribute retrieval --

  /**
   * Gets the nth OME/Image element's Name attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public String getImageName(Integer n) {
    return getAttribute("Image", "Name", n);
  }

  /**
   * Gets the nth OME/Image element's CreationDate attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public String getCreationDate(Integer n) {
    return getAttribute("Image", "CreationDate", n);
  }

  /**
   * Gets the nth OME/Image element's Description attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public String getDescription(String description, Integer n) {
    return getAttribute("Image", "Description", n);
  }

  /**
   * Gets the nth OME/Image/CA/Dimensions element's PixelSizeX attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Float getPixelSizeX(Integer n) {
    String pixelSizeX = getAttribute("Dimensions", "PixelSizeX", n);
    return pixelSizeX == null ? null : new Float(pixelSizeX);
  }

  /**
   * Gets the nth OME/Image/CA/Dimensions element's PixelSizeY attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Float getPixelSizeY(Integer n) {
    String pixelSizeY = getAttribute("Dimensions", "PixelSizeY", n);
    return pixelSizeY == null ? null : new Float(pixelSizeY);
  }

  /**
   * Gets the nth OME/Image/CA/Dimensions element's PixelSizeZ attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Float getPixelSizeZ(Integer n) {
    String pixelSizeZ = getAttribute("Dimensions", "PixelSizeZ", n);
    return pixelSizeZ == null ? null : new Float(pixelSizeZ);
  }

  /**
   * Gets the nth OME/Image/CA/Dimensions element's PixelSizeC attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Float getPixelSizeC(Integer n) {
    String pixelSizeC = getAttribute("Dimensions", "PixelSizeC", n);
    return pixelSizeC == null ? null : new Float(pixelSizeC);
  }

  /**
   * Gets the nth OME/Image/CA/Dimensions element's PixelSizeT attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Float getPixelSizeT(Integer n) {
    String pixelSizeT = getAttribute("Dimensions", "PixelSizeT", n);
    return pixelSizeT == null ? null : new Float(pixelSizeT);
  }

  /**
   * Gets the nth OME/Image/CA/Pixels element's SizeX attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Integer getSizeX(Integer n) {
    String sizeX = getAttribute("Pixels", "SizeX", n);
    return sizeX == null ? null : new Integer(sizeX);
  }

  /**
   * Gets the nth OME/Image/CA/Pixels element's SizeY attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Integer getSizeY(Integer n) {
    String sizeY = getAttribute("Pixels", "SizeY", n);
    return sizeY == null ? null : new Integer(sizeY);
  }

  /**
   * Gets the nth OME/Image/CA/Pixels element's SizeZ attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Integer getSizeZ(Integer n) {
    String sizeZ = getAttribute("Pixels", "SizeZ", n);
    return sizeZ == null ? null : new Integer(sizeZ);
  }

  /**
   * Gets the nth OME/Image/CA/Pixels element's SizeC attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Integer getSizeC(Integer n) {
    String sizeC = getAttribute("Pixels", "SizeC", n);
    return sizeC == null ? null : new Integer(sizeC);
  }

  /**
   * Gets the nth OME/Image/CA/Pixels element's SizeT attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Integer getSizeT(Integer n) {
    String sizeT = getAttribute("Pixels", "SizeT", n);
    return sizeT == null ? null : new Integer(sizeT);
  }

  /** Gets the nth OME/Image/CA/Pixels element's PixelType attribute. */
  public String getPixelType(Integer n) {
    return getAttribute("Pixels", "PixelType", n);
  }

  /**
   * Gets the nth OME/Image/CA/Pixels element's BigEndian attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Boolean getBigEndian(Integer n) {
    String bigEndian = getAttribute("Pixels", "BigEndian", n);
    return bigEndian == null ? null :
      new Boolean(bigEndian.equalsIgnoreCase("true"));
  }

  /**
   * Gets the nth OME/Image/CA/Pixels element's DimensionOrder attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public String getDimensionOrder(Integer n) {
    return getAttribute("Pixels", "DimensionOrder", n);
  }

  /** Gets the nth OME/Image/CA/StageLabel element's Name attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public String getStageName(String name, Integer n) {
    return getAttribute("StageLabel", "Name", n);
  }

  /**
   * Gets the nth OME/Image/CA/StageLabel element's X attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Float getStageX(Integer n) {
    String stageX = getAttribute("StageLabel", "X", n);
    return stageX == null ? null : new Float(stageX);
  }

  /**
   * Gets the nth OME/Image/CA/StageLabel element's Y attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Float getStageY(Integer n) {
    String stageY = getAttribute("StageLabel", "Y", n);
    return stageY == null ? null : new Float(stageY);
  }

  /**
   * Gets the nth OME/Image/CA/StageLabel element's Z attribute.
   * @param n the index of the element. If <code>null</code> the default index
   * of 0 will be used.
   */
  public Float getStageZ(Integer n) {
    String stageZ = getAttribute("StageLabel", "Z", n);
    return stageZ == null ? null : new Float(stageZ);
  }


  // -- Helper methods --

  /**
   * Retrieves the specified node associated with the given DOM element name,
   * creating it if it does not already exist.
   * @param base the node to retrieve the child from.
   * @param name the type of node to retrieve.
   * @param i the index of the node in the child list. If <code>null</code>
   * the default index of 0 will be used.
   * @return child node object.
   */
  private Object getChild(Object base, String name, Integer i)
    throws ReflectException
  {
    int index = i == null? 0 : i.intValue();
    r.setVar("base", base);
    r.setVar("name", name);
    Vector children = (Vector) r.exec("base.getChildren(name)");
    if (index < children.size()) {
      if (children.get(index) != null) return children.get(index);
    }
    Object child = r.exec("new " + name + "Node(base)");
    return child;
  }

  /**
   * Gets the value of the given attribute in the nth occurence of the
   * specified node.
   * @param nodeName the name of the node to retrieve the value from.
   * @param name the name of the attribute to retrieve the value from.
   * @param i the index of the node in the DOM tree. If <code>null</code>
   * the default index of 0 will be used.
   * @return the value of the attribute.
   */
  private String getAttribute(String nodeName, String name, Integer i)
  {
    r.setVar("root", root);
    try {
      // get the node
      Object node = findNode(nodeName, i);
      if (node == null) return null;

      // get the attribute
      r.setVar("node", node);
      r.setVar("name", name);
      r.exec("attr = node.getAttribute(name)");
      String attr = ((String) r.getVar("attr"));
      return attr;
    }
    catch (ReflectException e) {
      // We want potential errors in the reflected universe to be exposed to a
      // developer.
      //log.warn(e);
    }
    return null;
  }

  /**
   * Retrieves the specified node associated with the given DOM element name.
   * @param name the DOM element name.
   * @param i the index of the DOM element. If <code>null</code> the default
   * index of 0 will be used.
   */
  private Object findNode(String name, Integer i) throws ReflectException
  {
    int index = i == null? 0 : i.intValue();
    r.setVar("root", root);
    r.setVar("name", name);
    Element rel = (Element) r.exec("root.getDOMElement()");
    r.setVar("doc", rel.getOwnerDocument());
    r.exec("el = DOMUtil.findElementList(name, doc)");
    Vector elements = (Vector) r.getVar("el");
    Object element = elements.get(index);
    r.setVar("el", element);
    return r.exec("OMEXMLNode.createNode(el)");
  }

}
