//
// OMEXMLMetadataStore.java
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

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import loci.formats.*;
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

  /** DOM element that backs the first Image's CustomAttributes node. */
  private Element firstImageCA;

  // -- Constructor --

  /** Creates a new instance. */
  public OMEXMLMetadataStore() { createRoot(); }

  // -- OMEXMLMetadataStore API methods --

  /** Constructs a new OME-XML root node with the given XML block. */
  public void createRoot(String xml) {
    try { root = xml == null ? new OMENode() : new OMENode(xml); }
    catch (TransformerException exc) { LogTools.trace(exc); }
    catch (SAXException exc) { LogTools.trace(exc); }
    catch (ParserConfigurationException exc) { LogTools.trace(exc); }
    catch (IOException exc) { LogTools.trace(exc); }
  }

  /**
   * Dumps the given OME-XML DOM tree to a string.
   * @return OME-XML as a string.
   */
  public String dumpXML() {
    try { return root == null ? null : root.writeOME(false); }
    catch (TransformerException exc) { LogTools.trace(exc); }
    catch (SAXException exc) { LogTools.trace(exc); }
    catch (ParserConfigurationException exc) { LogTools.trace(exc); }
    catch (IOException exc) { LogTools.trace(exc); }
    return null;
  }

  /** 
   * Add each of the key/value pairs in the hashtable as a new
   * OriginalMetadata node.
   */
  public void populateOriginalMetadata(Hashtable h) {
    ImageNode image = (ImageNode) getChild(root, "Image", 0);
    CustomAttributesNode ca = 
      (CustomAttributesNode) getChild(image, "CustomAttributes", 0);
    firstImageCA = ca.getDOMElement();

    Vector original = DOMUtil.getChildElements("OriginalMetadata", 
      ca.getDOMElement());
    if (original.size() == 0) {
      Element el = DOMUtil.createChild(root.getDOMElement(),
        "SemanticTypeDefinitions");
      OMEXMLNode node = OMEXMLNode.createNode(el);
      node.setAttribute("xmlns",
        "http://www.openmicroscopy.org/XMLschemas/STD/RC2/STD.xsd");
      el = DOMUtil.createChild(el, "SemanticType");
      node = OMEXMLNode.createNode(el);
      node.setAttribute("Name", "OriginalMetadata");
      node.setAttribute("AppliesTo", "I");

      Element nameElement = DOMUtil.createChild(el, "Element");
      OMEXMLNode nameNode = OMEXMLNode.createNode(nameElement);
      nameNode.setAttribute("Name", "name");
      nameNode.setAttribute("DBLocation", "ORIGINAL_METADATA.NAME");
      nameNode.setAttribute("DataType", "string");

      Element valueElement = DOMUtil.createChild(el, "Element");
      OMEXMLNode valueNode = OMEXMLNode.createNode(valueElement);
      valueElement.setAttribute("Name", "value");
      valueElement.setAttribute("DBLocation", "ORIGINAL_METADATA.VALUE");
      valueElement.setAttribute("DataType", "string");
    }

    Object[] keys = h.keySet().toArray();
    for (int i=0; i<h.size(); i++) {
      Element el = DOMUtil.createChild(firstImageCA, "OriginalMetadata");
      OMEXMLNode node = new AttributeNode(el);
      node.setAttribute("name", keys[i].toString());
      node.setAttribute("value", h.get(keys[i]).toString());
    } 
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
  public String getDescription(Integer n) {
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

  // -- MetadataStore API methods --

  /* @see loci.formats.MetadataStore#createRoot() */
  public void createRoot() { createRoot(null); }

  /* @see loci.formats.MetadataStore#setRoot(Object) */
  public void setRoot(Object root) {
    if (!(root instanceof OMENode)) {
      throw new IllegalArgumentException(
        "This metadata store accepts root objects of type 'OMENode'.");
    }
    this.root = (OMENode) root;
  }

  /* @see loci.formats.MetadataStore#getRoot() */
  public Object getRoot() { return root; }

  /*
   * @see loci.formats.MetadataStore#setImage(String,
   *   String, String, Integer)
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
   * @see loci.formats.MetadataStore#setExperimenter(String, String,
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

  /*
   * @see loci.formats.MetadataStore#setGroup(String, Object, Object, Integer)
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
   * @see loci.formats.MetadataStore#setInstrument(String,
   *   String, String, String, Integer)
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
   * @see loci.formats.MetadataStore#setDimensions(Float,
   *   Float, Float, Float, Float, Integer)
   */
  public void setDimensions(Float pixelSizeX, Float pixelSizeY,
    Float pixelSizeZ, Float pixelSizeC, Float pixelSizeT, Integer i)
  {
    int ndx = i == null ? 0 : i.intValue();
    // TODO find a better way to specify which Dimensions we want
    ImageNode image = (ImageNode) getChild(root, "Image", ndx);
    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(image, "CustomAttributes", 0);
    DimensionsNode dimensions =
      (DimensionsNode) getChild(ca, "Dimensions", 0);

    if (pixelSizeX == null && pixelSizeY != null) pixelSizeX = pixelSizeY;
    if (pixelSizeY == null && pixelSizeX != null) pixelSizeY = pixelSizeX;

    dimensions.setPixelSizeX(pixelSizeX);
    dimensions.setPixelSizeY(pixelSizeY);
    dimensions.setPixelSizeZ(pixelSizeZ);
    dimensions.setPixelSizeC(pixelSizeC);
    dimensions.setPixelSizeT(pixelSizeT);
  }

  /*
   * @see loci.formats.MetadataStore#setDisplayROI(Integer, Integer, Integer,
   *   Integer, Integer, Integer, Integer, Integer, Object, Integer)
   */
  public void setDisplayROI(Integer x0, Integer y0, Integer z0,
    Integer x1, Integer y1, Integer z1, Integer t0, Integer t1,
    Object displayOptions, Integer i)
  {
    int ndx = i == null ? 0 : i.intValue();
    // TODO find a better way to specify which DisplayROI we want
    ImageNode image = (ImageNode) getChild(root, "Image", ndx);
    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(image, "CustomAttributes", 0);
    DisplayROINode displayROI = (DisplayROINode)
      getChild(ca, "DisplayROI", 0);
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
   * @see loci.formats.MetadataStore#setPixels(Integer, Integer, Integer,
   *   Integer, Integer, String, Boolean, String, Integer, Integer)
   */
  public void setPixels(Integer sizeX, Integer sizeY, Integer sizeZ,
    Integer sizeC, Integer sizeT, Integer pixelType, Boolean bigEndian,
    String dimensionOrder, Integer imageNo, Integer pixelsNo)
  {
    int img = imageNo == null ? 0 : imageNo.intValue();
    int pix = pixelsNo == null ? 0 : pixelsNo.intValue();

    ImageNode image = (ImageNode) getChild(root, "Image", img);
    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(image, "CustomAttributes", 0);
    PixelsNode pixels = (PixelsNode) getChild(ca, "Pixels", pix);
    pixels.setSizeX(sizeX);
    pixels.setSizeY(sizeY);
    pixels.setSizeZ(sizeZ);
    pixels.setSizeC(sizeC);
    pixels.setSizeT(sizeT);
    pixels.setPixelType(pixelTypeAsString(pixelType));
    pixels.setBigEndian(bigEndian);
    pixels.setDimensionOrder(dimensionOrder);

    // assign Pixels as default for the Image
    image.setDefaultPixels(pixels);
  }

  /*
   * @see loci.formats.MetadataStore#setStageLabel(String,
   *   Float, Float, Float, Integer)
   */
  public void setStageLabel(String name, Float x, Float y, Float z, Integer i) {
    int ndx = i == null ? 0 : i.intValue();
    // TODO find a better way to specify which StageLabel we want
    ImageNode image = (ImageNode) getChild(root, "Image", ndx);
    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(image, "CustomAttributes", 0);
    StageLabelNode stageLabel =
      (StageLabelNode) getChild(ca, "StageLabel", 0);
    stageLabel.setName(name);
    stageLabel.setX(x);
    stageLabel.setY(y);
    stageLabel.setZ(z);
  }

  /*
   * @see loci.formats.MetadataStore#setLogicalChannel(int, String,
   *   Float, Integer, Integer, String, String, Integer)
   */
  public void setLogicalChannel(int channelIdx, String name, Float ndFilter,
    Integer emWave, Integer exWave, String photometricInterpretation,
    String mode, Integer i)
  {
    int ndx = i == null ? 0 : i.intValue();
    // TODO find a better way to specify which LogicalChannel we want
    ImageNode image = (ImageNode) getChild(root, "Image", ndx);
    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(image, "CustomAttributes", 0);
    LogicalChannelNode logicalChannel =
      (LogicalChannelNode) getChild(ca, "LogicalChannel", 0);
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

  /*
   * @see loci.formats.MetadataStore#setChannelGlobalMinMax(int,
   *   Double, Double, Integer)
   */
  public void setChannelGlobalMinMax(int channel,
    Double globalMin, Double globalMax, Integer i)
  {
    int ndx = i == null ? 0 : i.intValue();
    // Since we will need this information for default display options creation
    // but don't have a place really to store this in OME-XML we're just going
    // to store it in instance variables.
    if (channelMinimum == null || channelMinimum.length <= channel) {
      // expand channel minimum list
      double[] min = new double[channel + 1];
      Arrays.fill(min, Double.NaN);
      if (channelMinimum != null) {
        System.arraycopy(channelMinimum, 0, min, 0, channelMinimum.length);
      }
      channelMinimum = min;
    }

    if (channelMaximum == null || channelMaximum.length <= channel) {
      // expand channel maximum list
      double[] max = new double[channel + 1];
      Arrays.fill(max, Double.NaN);
      if (channelMaximum != null) {
        System.arraycopy(channelMaximum, 0, max, 0, channelMaximum.length);
      }
      channelMaximum = max;
    }

    // Now that the array initialization hocus-pocus has been completed
    // let's do the work.
    channelMinimum[channel] = globalMin.doubleValue();
    channelMaximum[channel] = globalMax.doubleValue();
  }

  /*
   * @see loci.formats.MetadataStore#setPlaneInfo(int,
   *   int, int, Float, Float, Integer)
   */
  public void setPlaneInfo(int theZ, int theC, int theT, Float timestamp,
    Float exposureTime, Integer i)
  {
    int ndx = i == null ? 0 : i.intValue();
    // TODO: No-op with regards to OME-XML until the PlaneInfo type has been
    // put into the OME-XML schema.
  }

  /* @see loci.formats.MetadataStore#setDefaultDisplaySettings(Integer) */
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
      warn("Out of order access or missing metadata 'channelMaximum'.");
      return;
    }
    for (int c=0; c<sizeC; c++) {
      if (c > channelMinimum.length || channelMinimum[c] != channelMinimum[c]) {
        warn("Out of order access or missing metadata " +
          "'channelMinimum[" + c + "]'.");
        return;
      }
      if (c > channelMaximum.length || channelMaximum[c] != channelMaximum[c]) {
        warn("Out of order access or missing metadata " +
          "'channelMaximum[" + c + "]'.");
        return;
      }
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

  /*
   * @see loci.formats.MetadataStore#setImagingEnvironment(Float,
   *   Float, Float, Float, Integer)
   */
  public void setImagingEnvironment(Float temperature, Float airPressure,
    Float humidity, Float co2Percent, Integer i)
  {
    int ndx = i == null ? 0 : i.intValue();
    ImageNode image = (ImageNode) getChild(root, "Image", ndx);
    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(image, "CustomAttributes", 0);
    ImagingEnvironmentNode env =
      (ImagingEnvironmentNode) getChild(ca, "ImagingEnvironment", 0);
    env.setTemperature(temperature);
    env.setAirPressure(airPressure);
    env.setHumidity(humidity);
    env.setCO2Percent(co2Percent);
  }

  /*
   * @see loci.formats.MetadataStore#setDisplayChannel(Integer,
   *   Double, Double, Float, Integer)
   */
  public void setDisplayChannel(Integer channelNumber, Double blackLevel,
    Double whiteLevel, Float gamma, Integer i)
  {
    int ndx = i == null ? 0 : i.intValue();
    ImageNode image = (ImageNode) getChild(root, "Image", ndx);
    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(image, "CustomAttributes", 0);
    DisplayChannelNode display = (DisplayChannelNode)
      getChild(ca, "DisplayChannel", channelNumber.intValue());
    display.setChannelNumber(channelNumber);
    display.setBlackLevel(blackLevel);
    display.setWhiteLevel(whiteLevel);
    display.setGamma(gamma);
  }

  /*
   * @see loci.formats.MetadataStore#setDisplayOptions(Float, Boolean,
   *   Boolean, Boolean, Boolean, String, Integer, Integer, Integer,
   *   Integer, Integer, Integer, Integer, Integer, Integer, Integer)
   */
  public void setDisplayOptions(Float zoom, Boolean redChannelOn,
    Boolean greenChannelOn, Boolean blueChannelOn, Boolean displayRGB,
    String colorMap, Integer zstart, Integer zstop, Integer tstart,
    Integer tstop, Integer imageNdx, Integer pixelsNdx, Integer redChannel,
    Integer greenChannel, Integer blueChannel, Integer grayChannel)
  {
    int ndx = imageNdx == null ? 0 : imageNdx.intValue();
    int pixNdx = pixelsNdx == null ? 0 : pixelsNdx.intValue();

    ImageNode image = (ImageNode) getChild(root, "Image", ndx);
    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(image, "CustomAttributes", 0);
    PixelsNode pix = (PixelsNode) getChild(ca, "Pixels", pixNdx);
    DisplayChannelNode red = redChannel == null ? null : (DisplayChannelNode)
      getChild(ca, "DisplayChannel", redChannel.intValue());
    DisplayChannelNode green = greenChannel == null ? null :
      (DisplayChannelNode)
      getChild(ca, "DisplayChannel", greenChannel.intValue());
    DisplayChannelNode blue = blueChannel == null ? null : (DisplayChannelNode)
      getChild(ca, "DisplayChannel", blueChannel.intValue());
    DisplayChannelNode gray = grayChannel == null ? null : (DisplayChannelNode)
      getChild(ca, "DisplayChannel", grayChannel.intValue());

    DisplayOptionsNode display = (DisplayOptionsNode)
      getChild(ca, "DisplayOptions", 0);
    display.setPixels(pix);
    display.setZoom(zoom);
    if (red != null) display.setRedChannel(red);
    display.setRedChannelOn(new Boolean(red != null));
    if (green != null) display.setGreenChannel(green);
    display.setGreenChannelOn(new Boolean(green != null));
    if (blue != null) display.setBlueChannel(blue);
    display.setBlueChannelOn(new Boolean(blue != null));
    display.setDisplayRGB(displayRGB);
    if (gray != null) display.setGreyChannel(gray);
    display.setColorMap(colorMap);
    display.setZStart(zstart);
    display.setZStop(zstop);
    display.setTStart(tstart);
    display.setTStop(tstop);
  }

  /*
   * @see loci.formats.MetadataStore#setLightSource(String,
   *   String, String, Integer, Integer)
   */
  public void setLightSource(String manufacturer, String model,
    String serialNumber, Integer instrumentIndex, Integer lightIndex)
  {
    int ndx = instrumentIndex == null ? 0 : instrumentIndex.intValue();
    int lightNdx = lightIndex == null ? 0 : lightIndex.intValue();
    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(root, "CustomAttributes", 0);
    InstrumentNode inst = (InstrumentNode) getChild(ca, "Instrument", ndx);
    LightSourceNode light =
      (LightSourceNode) getChild(ca, "LightSource", lightNdx);
    light.setManufacturer(manufacturer);
    light.setModel(model);
    light.setSerialNumber(serialNumber);
    light.setInstrument(inst);
  }

  /*
   * @see loci.formats.MetadataStore#setLaser(String, String, Integer,
   *   Boolean, Boolean, String, Float, Integer, Integer, Integer, Integer)
   */
  public void setLaser(String type, String medium, Integer wavelength,
    Boolean frequencyDoubled, Boolean tunable, String pulse, Float power,
    Integer instrumentNdx, Integer lightNdx, Integer pumpNdx, Integer num)
  {
    int ndx = instrumentNdx == null ? 0 : instrumentNdx.intValue();
    int light = lightNdx == null ? 0 : lightNdx.intValue();
    int pump = pumpNdx == null ? 0 : pumpNdx.intValue();
    int laserNdx = num == null ? 0 : num.intValue();

    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(root, "CustomAttributes", 0);
    LightSourceNode lightNode =
      (LightSourceNode) getChild(ca, "LightSource", light);
    LightSourceNode pumpNode =
      (LightSourceNode) getChild(ca, "LightSource", pump);
    LaserNode laser = (LaserNode) getChild(ca, "Laser", laserNdx);

    laser.setType(type);
    laser.setMedium(medium);
    laser.setWavelength(wavelength);
    laser.setFrequencyDoubled(frequencyDoubled);
    laser.setTunable(tunable);
    laser.setPulse(pulse);
    laser.setPower(power);
    laser.setLightSource(lightNode);
    laser.setPump(pumpNode);
  }

  /*
   * @see loci.formats.MetadataStore#setFilament(String,
   *   Float, Integer, Integer)
   */
  public void setFilament(String type, Float power, Integer lightSource,
    Integer num)
  {
    int lightNdx = lightSource == null ? 0 : lightSource.intValue();
    int n = num == null ? 0 : num.intValue();

    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(root, "CustomAttributes", 0);
    LightSourceNode light =
      (LightSourceNode) getChild(ca, "LightSource", lightNdx);

    FilamentNode filament = (FilamentNode) getChild(ca, "Filament", n);
    filament.setType(type);
    filament.setPower(power);
    filament.setLightSource(light);
  }

  /* @see loci.formats.MetadataStore#setArc(String, Float, Integer, Integer) */
  public void setArc(String type, Float power, Integer lightSource,
    Integer num)
  {
    int lightNdx = lightSource == null ? 0 : lightSource.intValue();
    int n = num == null ? 0 : num.intValue();

    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(root, "CustomAttributes", 0);
    LightSourceNode light =
      (LightSourceNode) getChild(ca, "LightSource", lightNdx);
    ArcNode arc = (ArcNode) getChild(ca, "Arc", n);
    arc.setType(type);
    arc.setPower(power);
    arc.setLightSource(light);
  }

  /*
   * @see loci.formats.MetadataStore#setDetector(String, String,
   *   String, String, Float, Float, Float, Integer, Integer)
   */
  public void setDetector(String manufacturer, String model,
    String serialNumber, String type, Float gain, Float voltage, Float offset,
    Integer instrumentNdx, Integer detectorNdx)
  {
    int ndx = instrumentNdx == null ? 0 : instrumentNdx.intValue();
    int dNdx = detectorNdx == null ? 0 : detectorNdx.intValue();

    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(root, "CustomAttributes", 0);
    InstrumentNode inst = (InstrumentNode) getChild(ca, "Instrument", ndx);
    DetectorNode detector = (DetectorNode) getChild(ca, "Detector", dNdx);
    detector.setManufacturer(manufacturer);
    detector.setModel(model);
    detector.setSerialNumber(serialNumber);
    detector.setType(type);
    detector.setGain(gain);
    detector.setVoltage(voltage);
    detector.setOffset(offset);
    detector.setInstrument(inst);
  }

  /*
   * @see loci.formats.MetadataStore#setObjective(String,
   *   String, String, Float, Float, Integer, Integer)
   */
  public void setObjective(String manufacturer, String model,
    String serialNumber, Float lensNA,
    Float magnification, Integer instrument, Integer objective)
  {
    int iNdx = instrument == null ? 0 : instrument.intValue();
    int oNdx = objective == null ? 0 : objective.intValue();

    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(root, "CustomAttributes", 0);
    InstrumentNode inst = (InstrumentNode) getChild(ca, "Instrument", iNdx);
    ObjectiveNode obj = (ObjectiveNode) getChild(ca, "Objective", oNdx);
    obj.setManufacturer(manufacturer);
    obj.setModel(model);
    obj.setSerialNumber(serialNumber);
    obj.setLensNA(lensNA);
    obj.setMagnification(magnification);
    obj.setInstrument(inst);
  }

  /*
   * @see loci.formats.MetadataStore#setExcitationFilter(String,
   *   String, String, String, Integer)
   */
  public void setExcitationFilter(String manufacturer, String model,
    String lotNumber, String type, Integer filter)
  {
    int fNdx = filter == null ? 0 : filter.intValue();

    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(root, "CustomAttributes", 0);
    ExcitationFilterNode f =
      (ExcitationFilterNode) getChild(ca, "ExcitationFilter", fNdx);
    f.setManufacturer(manufacturer);
    f.setModel(model);
    f.setLotNumber(lotNumber);
    f.setType(type);
  }

  /*
   * @see loci.formats.MetadataStore#setDichroic(String,
   *   String, String, Integer)
   */
  public void setDichroic(String manufacturer, String model, String lotNumber,
    Integer dichroic)
  {
    int dNdx = dichroic == null ? 0 : dichroic.intValue();

    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(root, "CustomAttributes", 0);
    DichroicNode d = (DichroicNode) getChild(ca, "Dichroic", dNdx);
    d.setManufacturer(manufacturer);
    d.setModel(model);
    d.setLotNumber(lotNumber);
  }

  /*
   * @see loci.formats.MetadataStore#setEmissionFilter(String,
   *   String, String, String, Integer)
   */
  public void setEmissionFilter(String manufacturer, String model,
    String lotNumber, String type, Integer filter)
  {
    int fNdx = filter == null ? 0 : filter.intValue();

    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(root, "CustomAttributes", 0);
    EmissionFilterNode f = (EmissionFilterNode)
      getChild(ca, "EmissionFilter", fNdx);
    f.setManufacturer(manufacturer);
    f.setModel(model);
    f.setLotNumber(lotNumber);
    f.setType(type);
  }

  /*
   * @see loci.formats.MetadataStore#setFilterSet(String,
   *   String, String, Integer, Integer)
   */
  public void setFilterSet(String manufacturer, String model, String lotNumber,
    Integer filterSet, Integer filter)
  {
    int fsNdx = filterSet == null ? 0 : filterSet.intValue();
    int fNdx = filter == null ? 0 : filter.intValue();

    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(root, "CustomAttributes", 0);
    FilterNode filterNode = (FilterNode) getChild(ca, "Filter", fNdx);
    FilterSetNode fsNode = (FilterSetNode) getChild(ca, "FilterSet", fsNdx);
    fsNode.setManufacturer(manufacturer);
    fsNode.setModel(model);
    fsNode.setLotNumber(lotNumber);
    fsNode.setFilter(filterNode);
  }

  /*
   * @see loci.formats.MetadataStore#setOTF(Integer, Integer,
   *   String, String, Boolean, Integer, Integer, Integer, Integer)
   */
  public void setOTF(Integer sizeX, Integer sizeY, String pixelType,
    String path, Boolean opticalAxisAverage, Integer instrument, Integer otf,
    Integer filter, Integer objective)
  {
    int iNdx = instrument == null ? 0 : instrument.intValue();
    int fNdx = filter == null ? 0 : filter.intValue();
    int otfNdx = otf == null ? 0 : otf.intValue();
    int oNdx = objective == null ? 0 : objective.intValue();

    CustomAttributesNode ca = (CustomAttributesNode)
      getChild(root, "CustomAttributes", 0);
    InstrumentNode inst = (InstrumentNode) getChild(ca, "Instrument", iNdx);
    ObjectiveNode obj = (ObjectiveNode) getChild(ca, "Objective", oNdx);
    FilterNode f = (FilterNode) getChild(ca, "Filter", fNdx);

    OTFNode otfNode = (OTFNode) getChild(ca, "OTF", otfNdx);
    otfNode.setObjective(obj);
    otfNode.setFilter(f);
    otfNode.setSizeX(sizeX);
    otfNode.setSizeY(sizeY);
    otfNode.setPixelType(pixelType);
    otfNode.setPath(path);
    otfNode.setOpticalAxisAverage(opticalAxisAverage);
    otfNode.setInstrument(inst);
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
      case FormatTools.INT8:
        return "int8";
      case FormatTools.UINT8:
        return "Uint8";
      case FormatTools.INT16:
        return "int16";
      case FormatTools.UINT16:
        return "Uint16";
      case FormatTools.INT32:
        return "int32";
      case FormatTools.UINT32:
        return "Uint32";
      case FormatTools.FLOAT:
        return "float";
      case FormatTools.DOUBLE:
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
    Vector children = base.getChildNodes(name);
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
    catch (Exception exc) { LogTools.trace(exc); }
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
    LogTools.println(msg);
  }

}
