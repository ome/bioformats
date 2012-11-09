/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in;

import java.io.IOException;

import java.util.Stack;
import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ome.xml.model.enums.IlluminationType;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import loci.common.RandomAccessInputStream;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.MetadataTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffParser;

/**
 * LeicaSCNReader is the file format reader for Leica SCN TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/LeicaSCNReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/LeicaSCNReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */

public class LeicaSCNReader extends BaseTiffReader {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(LeicaSCNReader.class);


  // -- Fields --
  LeicaSCNHandler handler;

  // -- Constructor --

  /** Constructs a new LeicaSCN reader. */
  public LeicaSCNReader() {
    super("Leica SCN", new String[] {"scn"});
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN};
    suffixNecessary = false;
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --

  /* (non-Javadoc)
   * @see loci.formats.FormatReader#isThisType(java.lang.String, boolean)
   */
  @Override
  public boolean isThisType(String name, boolean open) {
    boolean isThisType = super.isThisType(name, open);
    if (isThisType && open) {
      isThisType = false;
      RandomAccessInputStream stream = null;
      try {
        stream = new RandomAccessInputStream(name);
        TiffParser tiffParser = new TiffParser(stream);
        if (!tiffParser.isValidHeader()) {
          isThisType = false;
        } else {
          String imageDescription = tiffParser.getComment();
          if (imageDescription != null) {
            try {
              // Test if XML is valid SCN metadata
              LeicaSCNHandler handler = new LeicaSCNHandler();
              XMLTools.parseXML(imageDescription, handler);
              isThisType = true;
            }
            catch (Exception se) {
              isThisType = false;
            }
          }
        }
      }
      catch (IOException e) {
        LOGGER.debug("I/O exception during isThisType() evaluation.", e);
        isThisType = false;
      }
      finally {
        try {
          if (stream != null) {
            stream.close();
          }
        }
        catch (IOException e) {
          LOGGER.debug("I/O exception during stream closure.", e);
        }
      }
    } else {
      isThisType = false;
    }
    return isThisType;
  }

  private int imageIFD(int no) {
    int s = getCoreIndex();
    LeicaSCNHandler.Image i = handler.imageMap.get(s);

    int[] dims = FormatTools.getZCTCoords(core[s].dimensionOrder, core[s].sizeZ, core[s].imageCount/(core[s].sizeZ * core[s].sizeT), core[s].sizeT, core[s].imageCount, no);
    int dz = dims[0];
    int dc = dims[1];
    int dr = getCoreIndex() - i.imageNumStart;
    int ifd = i.pixels.dimIFD[dz][dc][dr];

    return ifd;
  }


  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
      throws FormatException, IOException
      {
    int ifd = imageIFD(no);

    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    tiffParser.getSamples(ifds.get(ifd), buf, x, y, w, h);
    return buf;
      }

  /* @see loci.formats.IFormatReader#openThumbBytes(int) */
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    int s = getCoreIndex();
    LeicaSCNHandler.Image i = handler.imageMap.get(s);

    int thumbseries = i.imageNumStart + i.imageThumbnail;
    int thisSeries = getCoreIndex();
    setSeries(thumbseries);
    byte[] thumb = FormatTools.openThumbBytes(this, no);
    setSeries(thisSeries);

    return thumb;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    handler = null;
    if (!fileOnly) {
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    try {
      return (int) ifds.get(imageIFD(0)).getTileWidth();
    }
    catch (FormatException e) {
      LOGGER.debug("", e);
    }
    return super.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    try {
      return (int) ifds.get(imageIFD(0)).getTileLength();
    }
    catch (FormatException e) {
      LOGGER.debug("", e);
    }
    return super.getOptimalTileHeight();
  }

  // -- Internal BaseTiffReader API methods --

  protected void initCoreMetadata(int s) throws FormatException, IOException {
    LeicaSCNHandler.ImageCollection c = handler.collectionMap.get(s);
    LeicaSCNHandler.Image i = handler.imageMap.get(s);

    if (c == null || i == null || !(i.imageNumStart <= s && i.imageNumEnd >= s))
      throw new FormatException("Error setting core metadata for image number " + s);

    // repopulate core metadata
    IFD ifd = ifds.get(handler.IFDMap.get(s));
    PhotoInterp pi = ifd.getPhotometricInterpretation();
    int samples = ifd.getSamplesPerPixel();
    int r = s-i.imageNumStart; // subresolution

    if (s == i.imageNumStart && !hasFlattenedResolutions()) {
      core[s].resolutionCount = i.pixels.sizeR;
    }

    core[s].rgb = samples > 1 || pi == PhotoInterp.RGB;
    core[s].sizeX = (int) i.pixels.dimSizeX[0][0][r];
    core[s].sizeY = (int) i.pixels.dimSizeY[0][0][r];
    core[s].sizeZ = (int) i.pixels.sizeZ;
    core[s].sizeT = 1;
    core[s].sizeC = core[s].rgb ? samples : i.pixels.sizeC;

    if ((ifd.getImageWidth() != core[s].sizeX) ||
        (ifd.getImageLength() != core[s].sizeY))
      throw new FormatException("IFD dimensions do not match XML dimensions for image number " + s + ": x=" + ifd.getImageWidth() + ", " + core[s].sizeX + ", y=" + ifd.getImageLength() + ", " + core[s].sizeY);

    core[s].orderCertain = true;
    core[s].littleEndian = ifd.isLittleEndian();
    core[s].indexed = (pi == PhotoInterp.RGB_PALETTE &&
        (get8BitLookupTable() != null || get16BitLookupTable() != null));
    core[s].imageCount = i.pixels.sizeZ * i.pixels.sizeC;
    core[s].pixelType = ifd.getPixelType();
    core[s].metadataComplete = true;
    core[s].interleaved = false;
    core[s].falseColor = false;
    core[s].dimensionOrder = i.pixels.dataOrder;
    core[s].thumbnail = (i.imageThumbnail == r);
  }

  /* @see loci.formats.BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    tiffParser.setDoCaching(true); // Otherwise getComment() doesn't return the comment.

    String imageDescription = tiffParser.getComment();
    handler = new LeicaSCNHandler();
    if (imageDescription != null) {
      try {
        // Test if XML is valid SCN metadata
        XMLTools.parseXML(imageDescription, handler);
      }
      catch (Exception se) {
        throw new FormatException("Failed to parse XML", se);
      }
    }

    int count = handler.count();


    core = new CoreMetadata[count];
    ifds = tiffParser.getIFDs();

    for (int i=0; i<core.length; i++) {
      core[i] = new CoreMetadata();
      tiffParser.fillInIFD(ifds.get(handler.IFDMap.get(i)));
      initCoreMetadata(i);
    }
    setSeries(0);
  }

  /* @see loci.formats.BaseTiffReader#initMetadataStore() */
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();

    MetadataStore store = makeFilterMetadata();

    HashMap<String,String> instruments = new HashMap<String,String>();
    HashMap<String,Integer> instrumentIDs = new HashMap<String,Integer>();
    int instrumentidno = 0;
    HashMap<String,String> objectives = new HashMap<String,String>();
    HashMap<String,Integer> objectiveIDs = new HashMap<String,Integer>();
    int objectiveidno = 0;

    for (int s=0; s<getSeriesCount(); s++) {
      LeicaSCNHandler.ImageCollection c = handler.collectionMap.get(s);
      LeicaSCNHandler.Image i = handler.imageMap.get(s);
      int r = s-i.imageNumStart; // subresolution

      // Leica units are nanometres; convert to µm
      double sizeX = ((double) i.vSizeX) / 1000;
      double sizeY = ((double) i.vSizeY) / 1000;
      double offsetX = ((double) i.vOffsetX) / 1000;
      double offsetY = ((double) i.vOffsetY) / 1000;
      double sizeZ = (double) i.vSpacingZ / 1000;

      store.setPixelsPhysicalSizeX(new PositiveFloat(sizeX/i.pixels.dimSizeX[0][0][r]), s);
      store.setPixelsPhysicalSizeY(new PositiveFloat(sizeY/i.pixels.dimSizeY[0][0][r]), s);
      if (sizeZ > 0) // awful hack to cope with PositiveFloat
        store.setPixelsPhysicalSizeZ(new PositiveFloat(sizeZ), s);

      if (instruments.get(i.devModel) == null) {
        String instrumentID = MetadataTools.createLSID("Instrument", instrumentidno);
        instruments.put(i.devModel, instrumentID);
        instrumentIDs.put(i.devModel, new Integer(instrumentidno));
        store.setInstrumentID(instrumentID, instrumentidno);
        instrumentidno++;
      }

      if (objectives.get(i.devModel+":"+i.objMag) == null) {
        int inst = instrumentIDs.get(i.devModel);
        String objectiveID = MetadataTools.createLSID("Objective", inst, objectiveidno);
        objectives.put(i.devModel+":"+i.objMag, objectiveID);
        objectiveIDs.put(i.devModel+":"+i.objMag, new Integer(objectiveidno));
        store.setObjectiveID(objectiveID, inst, objectiveidno);

        // TODO: Current OME model only allows nominal magnification to be specified as an integer.
        Double mag = Double.parseDouble(i.objMag);
        store.setObjectiveNominalMagnification(new PositiveInteger((int) Math.round(mag)), inst, objectiveidno);
        store.setObjectiveCalibratedMagnification(mag, inst, objectiveidno);
        store.setObjectiveLensNA(new Double(i.illumNA), inst, objectiveidno);
        objectiveidno++;
      }

      store.setImageInstrumentRef(instruments.get(i.devModel), s);
      store.setObjectiveSettingsID(objectives.get(i.devModel+":"+i.objMag), s);
      String channelID = MetadataTools.createLSID("Channel", s);
      store.setChannelID(channelID, s, 0);
      // TODO: Only "brightfield has been seen in example files
      if (i.illumSource.equals("brightfield")) {
        store.setChannelIlluminationType(IlluminationType.TRANSMITTED, s, 0);
      } else {
        store.setChannelIlluminationType(IlluminationType.OTHER, s, 0);
        System.out.println("Unknown illumination source " + i.illumSource + "; please report this");
      }

      for (int q=0; q<core[s].imageCount; q++) {
        int[] dims = FormatTools.getZCTCoords(core[s].dimensionOrder, core[s].sizeZ, core[s].imageCount/(core[s].sizeZ * core[s].sizeT), core[s].sizeT, core[s].imageCount, q);

        store.setPlaneTheZ(new NonNegativeInteger(dims[0]), s, q);
        store.setPlaneTheC(new NonNegativeInteger(dims[1]), s, q);
        store.setPlaneTheT(new NonNegativeInteger(dims[2]), s, q);
        store.setPlanePositionX(offsetX, s, q);
        store.setPlanePositionY(offsetY, s, q);
      }

      store.setImageName(i.name + " (R" + (s-i.imageNumStart) + ")", s);
      store.setImageDescription("Collection " + c.name, s);

      store.setImageAcquisitionDate(new Timestamp(i.creationDate), s);

      // Original metadata...
      addGlobalMeta("collection.name", c.name);
      addGlobalMeta("collection.uuid", c.uuid);
      addGlobalMeta("collection.barcode", c.barcode);
      addGlobalMeta("collection.ocr", c.ocr);
      addGlobalMeta("creationDate", i.creationDate);

      addGlobalMeta("device.model for image #" + s, i.devModel);
      addGlobalMeta("device.version for image #" + s, i.devVersion);
      addGlobalMeta("view.sizeX for image #" + s, i.vSizeX);
      addGlobalMeta("view.sizeY for image #" + s, i.vSizeY);
      addGlobalMeta("view.offsetX for image #" + s, i.vOffsetX);
      addGlobalMeta("view.offsetY for image #" + s, i.vOffsetY);
      addGlobalMeta("view.spacingZ for image #" + s, i.vSpacingZ);
      addGlobalMeta("scanSettings.objectiveSettings.objective for image #" + s, i.objMag);
      addGlobalMeta("scanSettings.illuminationSettings.numericalAperture for image #" + s, i.illumNA);
      addGlobalMeta("scanSettings.illuminationSettings.illuminationSource for image #" + s, i.illumSource);
    }
  }

}

/**
 * SAX handler for parsing XML in Zeiss TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/ZeissTIFFHandler.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/ZeissTIFFHandler.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Roger Leigh <r.leigh at dundee.ac.uk>
 */
class LeicaSCNHandler extends DefaultHandler {

  // -- Fields --
  boolean valid = false;

  public ArrayList<LeicaSCNHandler.ImageCollection> collections;
  public LeicaSCNHandler.ImageCollection currentCollection;
  public LeicaSCNHandler.Image currentImage;
  public int seriesIndex;
  public ArrayList<Integer> IFDMap = new ArrayList<Integer>();
  public ArrayList<ImageCollection> collectionMap = new ArrayList<ImageCollection>();
  public ArrayList<Image> imageMap = new ArrayList<Image>();

  // Stack of XML elements to keep track of placement in the tree.
  public Stack<String> nameStack = new Stack<String>();
  // CDATA text stored while parsing.  Note that this is limited to a
  // single span between two tags, and CDATA with embedded elements is
  // not supported (and not present in the Zeiss TIFF format).
  public String cdata = new String();


  //public ArrayList<Plane> planes = new ArrayList<Plane>();

  // -- ZeissTIFFHandler API methods --

  LeicaSCNHandler() {
  }

  public String toString()
  {
    String s = new String("TIFF-XML parsing\n");

    return s;
  }

  // -- DefaultHandler API methods --

  public void endElement(String uri,
    String localName,
    String qName) {
    if (!nameStack.empty() && nameStack.peek().equals(qName))
      nameStack.pop();

    if (qName.equals("scn")) {
      // Finalise data.
    }
    else if (qName.equals("collection")) {
      currentCollection = null;
    }
    else if (qName.equals("image")) {
      currentImage.imageNumStart = seriesIndex;
      seriesIndex += currentImage.pixels.sizeR;
      currentImage.imageNumEnd = seriesIndex - 1;
      currentImage = null;
    }
    else if (qName.equals("creationDate")) {
      currentImage.creationDate = cdata;
    }
    else if (qName.equals("device")) {
    }
    else if (qName.equals("pixels")) {
    }
    else if (qName.equals("dimension")) {
    }
    else if (qName.equals("view")) {
    }
    else if (qName.equals("scanSettings")) {
    }
    else if (qName.equals("objectiveSettings")) {
    }
    else if (qName.equals("objective")) {
      currentImage.objMag = cdata;
    }
    else if (qName.equals("illuminationSettings")) {
    }
    else if (qName.equals("numericalAperture")) {
      currentImage.illumNA = cdata;
    }
    else if (qName.equals("illuminationSource")) {
      currentImage.illumSource = cdata;
    } else {
      // Other or unknown tag; will be handled by endElement.
      System.out.println("Unknown tag: " + qName);
    }
    cdata = null;

  }

  public void characters(char[] ch,
    int start,
    int length)
  {
    String s = new String(ch, start, length);
    if (cdata == null)
      cdata = s;
    else
      cdata += s;
  }

  public void startElement(String uri, String localName, String qName,
    Attributes attributes) throws SAXException
    {
    cdata = null;

    if (qName.equals("scn")) {
      String ns = attributes.getValue("xmlns");
      if (ns == null || !ns.equals("http://www.leica-microsystems.com/scn/2010/03/10"))
      {
        throw new SAXException("Invalid Leica SCN XML");
      }
      valid = true;
      collections = new ArrayList<LeicaSCNHandler.ImageCollection>();
      seriesIndex = 0;
    }

    if (valid == false)
    {
      throw new SAXException("Invalid Leica SCN XML");
    }

    if (qName.equals("collection")) {
      ImageCollection c = new LeicaSCNHandler.ImageCollection(attributes);
      collections.add(c);
      currentCollection = c;
      if (collections.size() != 1)
        throw new SAXException("Invalid Leica SCN XML: Only a single collection is permitted");
    }
    else if (qName.equals("image")) {
      Image i = new LeicaSCNHandler.Image(attributes);
      currentCollection.images.add(i);
      currentImage = i;
    }
    else if (qName.equals("creationDate")) {
    }
    else if (qName.equals("device")) {
      currentImage.devModel = attributes.getValue("model");
      currentImage.devVersion = attributes.getValue("version");
    }
    else if (qName.equals("pixels")) {
      if (currentImage.pixels == null)
        currentImage.pixels = new LeicaSCNHandler.Pixels(attributes);
      else
      {
        throw new SAXException("Invalid Leica SCN XML: Multiple pixels elements for single image");
      }
    }
    else if (qName.equals("dimension")) {
      String s;
      int r = 0;
      int z = 0;
      int c = 0;
      long sizeX = 0;
      long sizeY = 0;
      int ifd = 0;
      s = attributes.getValue("r");
      if (s != null)
        r = Integer.parseInt(s);
      s = attributes.getValue("z");
      if (s != null)
        z = Integer.parseInt(s);
      s = attributes.getValue("c");
      if (s != null)
        c = Integer.parseInt(s);
      s = attributes.getValue("sizeX");
      if (s != null)
        sizeX = Long.parseLong(s);
      s = attributes.getValue("sizeY");
      if (s != null)
        sizeY = Long.parseLong(s);
      s = attributes.getValue("ifd");
      if (s != null)
        ifd = Integer.parseInt(s);
      currentImage.pixels.dimSizeX[z][c][r] = sizeX;
      currentImage.pixels.dimSizeY[z][c][r] = sizeY;
      currentImage.pixels.dimIFD[z][c][r] = ifd;
      if (r == 0 || currentImage.thumbSizeX > sizeX) {
        currentImage.thumbSizeX = sizeX;
        currentImage.imageThumbnail = r;
      }
      IFDMap.add(ifd);
      collectionMap.add(currentCollection);
      imageMap.add(currentImage);
    }
    else if (qName.equals("view")) {
      currentImage.setView(attributes);
    }
    else if (qName.equals("scanSettings")) {
    }
    else if (qName.equals("objectiveSettings")) {
    }
    else if (qName.equals("objective")) {
    }
    else if (qName.equals("illuminationSettings")) {
    }
    else if (qName.equals("numericalAperture")) {
    }
    else if (qName.equals("illuminationSource")) {
    } else {
      // Other or unknown tag; will be handled by endElement.
    }

    nameStack.push(qName);
    }

  // -- Helper classes and functions --

  int count() {
    return seriesIndex;
  }

  public class ImageCollection
  {
    String name;
    String uuid;
    long sizeX;
    long sizeY;
    String barcode;
    String ocr;

    ArrayList<Image> images;

    ImageCollection(Attributes attrs) {
      String s;
      name = attrs.getValue("name");
      uuid = attrs.getValue("uuid");
      s = attrs.getValue("sizeX");
      if (s != null)
        sizeX = Long.parseLong(s);
      s = attrs.getValue("sizeY");
      if (s != null)
        sizeY = Long.parseLong(s);
      barcode = attrs.getValue("barcode");
      ocr = attrs.getValue("ocr");

      images = new ArrayList<Image>();
    }
  }

  public class Image
  {
    int imageNumStart; // first image number
    int imageNumEnd;   // last image number (subresolutions)
    int imageThumbnail; // image for thumbnailing
    long thumbSizeX;

    String name;
    String uuid;
    String creationDate;
    String devModel; // device model
    String devVersion; // device version
    Pixels pixels; // pixel metadata for each subresolution
    long vSizeX; // view size x (nm)
    long vSizeY; // view size y (nm)
    long vOffsetX; // view offset x (nm?)
    long vOffsetY; // view offset y (nm?)
    long vSpacingZ; // view spacing z (nm?)
    String objMag; // objective magnification
    String illumNA; // illumination NA (why not objective NA?)
    String illumSource; // illumination source

    Image(Attributes attrs) {
      name = attrs.getValue("name");
      uuid = attrs.getValue("uuid");
    }

    void
    setView(Attributes attrs) {
      String s;
      s = attrs.getValue("sizeX");
      if (s != null)
        vSizeX = Long.parseLong(s);
      s = attrs.getValue("sizeY");
      if (s != null)
        vSizeY = Long.parseLong(s);
      s = attrs.getValue("offsetX");
      if (s != null)
        vOffsetX = Long.parseLong(s);
      s = attrs.getValue("offsetY");
      if (s != null)
        vOffsetY = Long.parseLong(s);
      s = attrs.getValue("spacingZ");
      if (s != null)
        vSpacingZ = Long.parseLong(s);
    }
  }

  public class Pixels
  {
    // Set up storage for each resolution and each dimension.  Set main resolution.


    // data order (XYCRZ)
    // sizes for XYRZC
    // firstIFD (number)
    // dimension->IFD mapping (RZC to sizeX, sizeY, IFD)
    //   use 3 arrays of size C*Z*R

    String dataOrder; // Strip subresolutions and add T
    long sizeX;
    long sizeY;
    int sizeZ;
    int sizeC;
    int sizeR;
    int firstIFD;
    int lastIFD;
    long dimSizeX[][][]; // X size for [ZCR]
    long dimSizeY[][][]; // Y size for [ZCR]
    int dimIFD[][][];   // Corresponding IFD for [ZCR]

    Pixels(Attributes attrs) {
      String s;

      dataOrder = attrs.getValue("dataOrder");
      dataOrder = dataOrder.replace("R", "");
      dataOrder += "T";

      // Set main resolution.
      s = attrs.getValue("sizeX");
      if (s != null)
        sizeX = Long.parseLong(s);
      s = attrs.getValue("sizeY");
      if (s != null)
        sizeY = Long.parseLong(s);

      // Set dimensions.
      s = attrs.getValue("sizeZ");
      if (s != null)
        sizeZ = Integer.parseInt(s);
      s = attrs.getValue("sizeC");
      if (s != null)
        sizeC = Integer.parseInt(s);
      s = attrs.getValue("sizeR");
      if (s != null)
        sizeR = Integer.parseInt(s);

      s = attrs.getValue("firstIFD");
      if (s != null)
        firstIFD = Integer.parseInt(s);
      // Set up storage all dimensions.
      dimSizeX = new long[sizeZ][sizeC][sizeR];
      dimSizeY = new long[sizeZ][sizeC][sizeR];
      dimIFD = new int[sizeZ][sizeC][sizeR];
    }
  }

}

