/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.xml.model.enums.IlluminationType;
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
 */

public class LeicaSCNReader extends BaseTiffReader {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(LeicaSCNReader.class);

  private static final String SCHEMA_2010_03 =
    "http://www.leica-microsystems.com/scn/2010/03/10";
  private static final String SCHEMA_2010_10 =
    "http://www.leica-microsystems.com/scn/2010/10/01";

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
    if (super.isThisType(name, open) && open) {
      RandomAccessInputStream stream = null;
      try {
        stream = new RandomAccessInputStream(name);
        TiffParser tiffParser = new TiffParser(stream);
        if (!tiffParser.isValidHeader()) {
          return false;
        }

        String imageDescription = tiffParser.getComment();
        if (imageDescription != null) {
          try {
            // Test if XML is valid SCN metadata
            LeicaSCNHandler handler = new LeicaSCNHandler();
            XMLTools.parseXML(imageDescription, handler);
            return true;
          }
          catch (Exception se) {
            LOGGER.debug("XML parsing failed", se);
          }
        }
      }
      catch (IOException e) {
        LOGGER.debug("I/O exception during isThisType() evaluation.", e);
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
    }
    return false;
  }

  private int imageIFD(int no) {
    int s = getCoreIndex();
    Image i = handler.imageMap.get(s);

    int[] dims = getZCTCoords(no);
    int dz = dims[0];
    int dc = dims[1];
    int dr = s - getParent(s);

    return i.pixels.lookupDimension(dz, dc, dr).ifd;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
      throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    if (tiffParser == null) {
      initTiffParser();
    }
    int ifd = imageIFD(no);
    tiffParser.getSamples(ifds.get(ifd), buf, x, y, w, h);
    return buf;
  }

  /* @see loci.formats.IFormatReader#openThumbBytes(int) */
  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    int originalIndex = getCoreIndex();
    Image i = handler.imageMap.get(getCoreIndex());

    setCoreIndex(getParent(originalIndex) + i.imageThumbnail);
    byte[] thumb = FormatTools.openThumbBytes(this, no);
    setCoreIndex(originalIndex);

    return thumb;
  }

  @Override
  public int getThumbSizeX() {
    int originalIndex = getCoreIndex();
    Image i = handler.imageMap.get(getCoreIndex());

    setCoreIndex(getParent(originalIndex) + i.imageThumbnail);
    int size = super.getThumbSizeX();
    setCoreIndex(originalIndex);

    return size;
  }

  @Override
  public int getThumbSizeY() {
    int originalIndex = getCoreIndex();
    Image i = handler.imageMap.get(getCoreIndex());

    setCoreIndex(getParent(originalIndex) + i.imageThumbnail);
    int size = super.getThumbSizeY();
    setCoreIndex(originalIndex);

    return size;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    handler = null;
    if (!fileOnly) {
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
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
  @Override
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

  protected void initCoreMetadata(int s, int resolution) throws FormatException, IOException {
    ImageCollection c = handler.collection;
    Image i = handler.imageMap.get(s);

    if (c == null || i == null) {
      throw new FormatException("Error setting core metadata for image number " + s);
    }

    CoreMetadata ms = core.get(s);

    // repopulate core metadata

    if (resolution == 0) {
      ms.resolutionCount = i.pixels.sizeR;
    }

    Dimension dimension = i.pixels.lookupDimension(0, 0, resolution);
    if (dimension == null) {
      throw new FormatException(
        "No dimension information for subresolution=" + resolution);
    }

    IFD ifd = ifds.get(dimension.ifd);
    PhotoInterp pi = ifd.getPhotometricInterpretation();
    int samples = ifd.getSamplesPerPixel();

    ms.rgb = samples > 1 || pi == PhotoInterp.RGB;
    ms.sizeX = (int) dimension.sizeX;
    ms.sizeY = (int) dimension.sizeY;
    ms.sizeZ = (int) i.pixels.sizeZ;
    ms.sizeT = 1;
    ms.sizeC = ms.rgb ? samples : i.pixels.sizeC;

    if ((ifd.getImageWidth() != ms.sizeX) || (ifd.getImageLength() != ms.sizeY))
    {
      throw new FormatException("IFD dimensions do not match XML dimensions for image " +
        s + ": x=" + ifd.getImageWidth() + ", " + ms.sizeX + ", y=" + ifd.getImageLength() +
        ", " + ms.sizeY);
    }

    ms.orderCertain = true;
    ms.littleEndian = ifd.isLittleEndian();
    ms.indexed = pi == PhotoInterp.RGB_PALETTE &&
      (get8BitLookupTable() != null || get16BitLookupTable() != null);
    ms.imageCount = i.pixels.sizeZ * i.pixels.sizeC;
    ms.pixelType = ifd.getPixelType();
    ms.metadataComplete = true;
    ms.interleaved = false;
    ms.falseColor = false;
    ms.dimensionOrder = "XYCZT";
    ms.thumbnail = i.imageThumbnail == resolution;
  }

  /* @see loci.formats.BaseTiffReader#initStandardMetadata() */
  @Override
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    tiffParser.setDoCaching(true); // Otherwise getComment() doesn't return the comment.

    String imageDescription = tiffParser.getComment();
    handler = new LeicaSCNHandler();
    if (imageDescription != null) {
      try {
        // parse the XML description
        XMLTools.parseXML(imageDescription, handler);
      }
      catch (Exception se) {
        throw new FormatException("Failed to parse XML", se);
      }
    }

    int count = handler.count();

    ifds = tiffParser.getIFDs();

    if (ifds.size() < count) {
      count = ifds.size();
    }

    core.clear();
    int resolution = 0;
    int parent = 0;
    for (int i=0; i<count; i++) {
      if (resolution == 0) {
        parent = i;
      }
      CoreMetadata ms = new CoreMetadata();
      core.add(ms);
      tiffParser.fillInIFD(ifds.get(handler.IFDMap.get(i)));
      initCoreMetadata(i, resolution);
      resolution++;
      if (resolution == core.get(parent).resolutionCount) {
        resolution = 0;
      }
    }
  }

  /* @see loci.formats.BaseTiffReader#initMetadataStore() */
  @Override
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    HashMap<String,Integer> instrumentIDs = new HashMap<String,Integer>();
    int instrumentidno = 0;
    HashMap<String,String> objectives = new HashMap<String,String>();
    int objectiveidno = 0;

    int parent = 0;
    for (int s=0; s<getSeriesCount(); s++) {
      int coreIndex = seriesToCoreIndex(s);
      ImageCollection c = handler.collection;
      Image i = handler.imageMap.get(coreIndex);

      int subresolution = coreIndex - parent;
      if (!hasFlattenedResolutions()) {
        subresolution = 0;
      }

      if (core.get(s).resolutionCount > 1) {
        parent = s;
      }
      else if (core.get(parent).resolutionCount -1 == subresolution) {
        parent = s + 1;
      }

      Dimension dimension = i.pixels.lookupDimension(0, 0, subresolution);
      if (dimension == null) {
        throw new FormatException(
          "No dimension information for subresolution=" + subresolution);
      }

      // Leica units are nanometres; convert to µm
      double sizeX = i.vSizeX / 1000.0;
      double sizeY = i.vSizeY / 1000.0;
      final Length offsetX = new Length(i.vOffsetX, UNITS.REFERENCEFRAME);
      final Length offsetY = new Length(i.vOffsetY, UNITS.REFERENCEFRAME);
      double sizeZ = i.vSpacingZ / 1000.0;

      store.setPixelsPhysicalSizeX(
        FormatTools.getPhysicalSizeX(sizeX / dimension.sizeX), s);
      store.setPixelsPhysicalSizeY(
        FormatTools.getPhysicalSizeY(sizeY / dimension.sizeY), s);
      store.setPixelsPhysicalSizeZ(FormatTools.getPhysicalSizeZ(sizeZ), s);

      if (instrumentIDs.get(i.devModel) == null) {
        String instrumentID = MetadataTools.createLSID("Instrument", instrumentidno);
        instrumentIDs.put(i.devModel, instrumentidno);
        store.setInstrumentID(instrumentID, instrumentidno);
        instrumentidno++;
      }

      int inst = instrumentIDs.get(i.devModel);
      String objectiveName = i.devModel + ":" + i.objMag;
      if (objectives.get(objectiveName) == null) {
        String objectiveID = MetadataTools.createLSID("Objective", inst, objectiveidno);
        objectives.put(objectiveName, objectiveID);
        store.setObjectiveID(objectiveID, inst, objectiveidno);

        Double mag = Double.parseDouble(i.objMag);
        store.setObjectiveNominalMagnification(mag, inst, objectiveidno);
        store.setObjectiveCalibratedMagnification(mag, inst, objectiveidno);
        store.setObjectiveLensNA(new Double(i.illumNA), inst, objectiveidno);
        objectiveidno++;
      }

      store.setImageInstrumentRef(
        MetadataTools.createLSID("Instrument", inst), s);
      store.setObjectiveSettingsID(objectives.get(objectiveName), s);
      // TODO: Only "brightfield" has been seen in example files
      if (i.illumSource.equals("brightfield")) {
        store.setChannelIlluminationType(IlluminationType.TRANSMITTED, s, 0);
      } else {
        store.setChannelIlluminationType(IlluminationType.OTHER, s, 0);
        LOGGER.debug("Unknown illumination source {}", i.illumSource);
      }

      CoreMetadata ms = core.get(s);

      for (int q=0; q<ms.imageCount; q++) {
        store.setPlanePositionX(offsetX, s, q);
        store.setPlanePositionY(offsetY, s, q);
      }

      store.setImageName(i.name + " (R" + subresolution + ")", s);
      store.setImageDescription("Collection " + c.name, s);

      store.setImageAcquisitionDate(new Timestamp(i.creationDate), s);

      // Original metadata...
      addSeriesMeta("collection.name", c.name);
      addSeriesMeta("collection.uuid", c.uuid);
      addSeriesMeta("collection.barcode", c.barcode);
      addSeriesMeta("collection.ocr", c.ocr);
      addSeriesMeta("creationDate", i.creationDate);

      addSeriesMeta("device.model for image", i.devModel);
      addSeriesMeta("device.version for image", i.devVersion);
      addSeriesMeta("view.sizeX for image", i.vSizeX);
      addSeriesMeta("view.sizeY for image", i.vSizeY);
      addSeriesMeta("view.offsetX for image", i.vOffsetX);
      addSeriesMeta("view.offsetY for image", i.vOffsetY);
      addSeriesMeta("view.spacingZ for image", i.vSpacingZ);
      addSeriesMeta("scanSettings.objectiveSettings.objective for image", i.objMag);
      addSeriesMeta("scanSettings.illuminationSettings.numericalAperture for image", i.illumNA);
      addSeriesMeta("scanSettings.illuminationSettings.illuminationSource for image", i.illumSource);
    }
  }

  private int getParent(int coreIndex) {
    for (int parent=0; parent<core.size(); ) {
      int resCount = core.get(parent).resolutionCount;
      if (parent + resCount > coreIndex) {
        return parent;
      }
      parent += resCount;
    }
    return -1;
  }

  /**
   * SAX handler for parsing XML in Leica SCN files.
   *
   * @author Roger Leigh <r.leigh at dundee.ac.uk>
   */
  class LeicaSCNHandler extends DefaultHandler {

    // -- Fields --
    boolean valid = false;

    public ImageCollection collection;
    public Image currentImage;
    public int seriesIndex;
    public ArrayList<Integer> IFDMap = new ArrayList<Integer>();
    public ArrayList<Image> imageMap = new ArrayList<Image>();

    // Stack of XML elements to keep track of placement in the tree.
    public Deque<String> nameStack = new ArrayDeque<String>();
    // CDATA text stored while parsing.  Note that this is limited to a
    // single span between two tags, and CDATA with embedded elements is
    // not supported.
    public String cdata;

    public int resolutionCount = 0;

    // -- DefaultHandler API methods --

    @Override
    public void endElement(String uri, String localName, String qName) {
      if (!nameStack.isEmpty() && nameStack.peek().equals(qName)) {
        nameStack.pop();
      }

      if (qName.equals("image")) {
        currentImage.imageNumStart = seriesIndex;
        seriesIndex += currentImage.pixels.sizeR *
          currentImage.pixels.sizeC * currentImage.pixels.sizeZ;
        currentImage.imageNumEnd = seriesIndex - 1;
        resolutionCount += currentImage.pixels.sizeR;
        currentImage = null;
      }
      else if (qName.equals("creationDate")) {
        currentImage.creationDate = cdata;
      }
      else if (qName.equals("pixels")) {
        // Compute size of C, R and Z
        Pixels p = currentImage.pixels;
        int sizeC = 0;
        int sizeR = 0;
        int sizeZ = 0;

        for (Dimension d : p.dims) {
          if (d.c > sizeC) {
            sizeC = d.c;
          }
          if (d.r > sizeR) {
            sizeR = d.r;
          }
          if (d.z > sizeZ) {
            sizeZ = d.z;
          }
        }
        sizeC++;
        sizeR++;
        sizeZ++;

        // Set up storage for all dimensions.
        p.sizeC = sizeC;
        p.sizeR = sizeR;
        p.sizeZ = sizeZ;

        for (Dimension d : p.dims) {
          if (d.r == 0 || currentImage.thumbSizeX > d.sizeX) {
            currentImage.thumbSizeX = d.sizeX;
            currentImage.imageThumbnail = d.r;
          }
        }

        // Dimension ordering indirection (R=image, then Z, then C)
        for (int cr = 0; cr < sizeR; cr++) {
          imageMap.add(currentImage);
          for (int cc = 0; cc < sizeC; cc++) {
            for (int cz = 0; cz < sizeZ; cz++) {
              IFDMap.add(p.lookupDimension(cz, cc, cr).ifd);
            }
          }
        }
      }
      else if (qName.equals("objective")) {
        currentImage.objMag = cdata;
      }
      else if (qName.equals("numericalAperture")) {
        currentImage.illumNA = cdata;
      }
      else if (qName.equals("illuminationSource")) {
        currentImage.illumSource = cdata;
      }
      cdata = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
      String s = new String(ch, start, length);
      if (cdata == null) {
        cdata = s;
      }
      else {
        cdata += s;
      }
    }

    @Override
    public void startElement(String uri, String localName, String qName,
      Attributes attributes) throws SAXException
    {
      cdata = null;

      if (qName.equals("scn")) {
        String ns = attributes.getValue("xmlns");
        if (ns == null) {
          throw new SAXException("Invalid Leica SCN XML");
        }
        if (!(ns.equals(SCHEMA_2010_03) || ns.equals(SCHEMA_2010_10))) {
          LOGGER.warn("Unknown Leica SCN XML schema: " + ns + "; this file may not be read correctly");
        }

        valid = true;
        seriesIndex = 0;
      }

      if (!valid) {
        throw new SAXException("Invalid Leica SCN XML");
      }

      if (qName.equals("collection")) {
        collection = new ImageCollection(attributes);
      }
      else if (qName.equals("image")) {
        currentImage = new Image(attributes);
        collection.images.add(currentImage);
      }
      else if (qName.equals("device")) {
        currentImage.devModel = attributes.getValue("model");
        currentImage.devVersion = attributes.getValue("version");
      }
      else if (qName.equals("pixels")) {
        if (currentImage.pixels == null) {
          currentImage.pixels = new Pixels(attributes);
        }
        else {
          throw new SAXException("Invalid Leica SCN XML: Multiple pixels elements for single image");
        }
      }
      else if (qName.equals("dimension")) {
        currentImage.pixels.dims.add(new Dimension(attributes));
      }
      else if (qName.equals("view")) {
        currentImage.setView(attributes);
      }

      nameStack.push(qName);
    }

    int count() {
      return resolutionCount;
    }
  }

  class ImageCollection {
    String name;
    String uuid;
    long sizeX;
    long sizeY;
    String barcode;
    String ocr;

    ArrayList<Image> images;

    ImageCollection(Attributes attrs) {
      name = attrs.getValue("name");
      uuid = attrs.getValue("uuid");
      String s = attrs.getValue("sizeX");
      if (s != null) {
        sizeX = Long.parseLong(s);
      }
      s = attrs.getValue("sizeY");
      if (s != null) {
        sizeY = Long.parseLong(s);
      }
      barcode = attrs.getValue("barcode");
      ocr = attrs.getValue("ocr");

      images = new ArrayList<Image>();
    }
  }

  class Image {
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

    void setView(Attributes attrs) {
      String s = attrs.getValue("sizeX");
      if (s != null) {
        vSizeX = Long.parseLong(s);
      }
      s = attrs.getValue("sizeY");
      if (s != null) {
        vSizeY = Long.parseLong(s);
      }
      s = attrs.getValue("offsetX");
      if (s != null) {
        vOffsetX = Long.parseLong(s);
      }
      s = attrs.getValue("offsetY");
      if (s != null) {
        vOffsetY = Long.parseLong(s);
      }
      s = attrs.getValue("spacingZ");
      if (s != null) {
        vSpacingZ = Long.parseLong(s);
      }
    }
  }

  class Pixels {
    // Set up storage for each resolution and each dimension.  Set main resolution.

    // data order (XYCRZ) [unused; force to XYCZT]
    // sizeX, sizeY
    // sizeZ, sizeC, sizeR [unused; compute from dimensions]
    // firstIFD (number) [unused]
    // dimension->IFD mapping (RZC to sizeX, sizeY, IFD)
    //   use 3 arrays of size C*Z*R

    ArrayList<Dimension> dims = new ArrayList<Dimension>();
    long sizeX;
    long sizeY;
    int sizeZ;
    int sizeC;
    int sizeR;
    int lastIFD;

    Pixels(Attributes attrs) {
      // Set main resolution.
      String s = attrs.getValue("sizeX");
      if (s != null) {
        sizeX = Long.parseLong(s);
      }
      s = attrs.getValue("sizeY");
      if (s != null) {
        sizeY = Long.parseLong(s);
      }
    }

    public Dimension lookupDimension(int z, int c, int resolution) {
      for (Dimension d : dims) {
        if (d.z == z && d.c == c && d.r == resolution) {
          return d;
        }
      }
      return null;
    }
  }

  class Dimension {
    // Single image plane for given Z, C, R dimensions
    long sizeX = 0;
    long sizeY = 0;
    int z = 0;
    int c = 0;
    int r = 0;
    int ifd = 0;

    Dimension(Attributes attrs) {
      String s = attrs.getValue("r");
      if (s != null) {
        r = Integer.parseInt(s);
      }
      s = attrs.getValue("z");
      if (s != null) {
        z = Integer.parseInt(s);
      }
      s = attrs.getValue("c");
      if (s != null) {
        c = Integer.parseInt(s);
      }
      s = attrs.getValue("sizeX");
      if (s != null) {
        sizeX = Long.parseLong(s);
      }
      s = attrs.getValue("sizeY");
      if (s != null) {
        sizeY = Long.parseLong(s);
      }
      s = attrs.getValue("ifd");
      if (s != null) {
        ifd = Integer.parseInt(s);
      }
    }
  }
}
