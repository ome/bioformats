/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffParser;
import loci.formats.tiff.TiffRational;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.NonNegativeInteger;

import ome.units.quantity.Time;
import ome.units.UNITS;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * VectraReader is the reader for PerkinElmer Vectra QPTIFF data
 */
public class VectraReader extends BaseTiffReader {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(VectraReader.class);

  /** TIFF image description prefix for PerkinElmer Vectra/QPTIFF files. */
  private static final String SOFTWARE_CHECK = "PerkinElmer-QPI";

  private static final String ANNOTATION_SUFFIX = "_annotations.xml";
  private static final List<String> EXTRA_FILES = Arrays.asList(
    "CoverslipMask.tif", "FocusMap.tif", "Label.tif",
    "OverviewBF.tif", "OverviewFL.tif", "SampleMask.tif"
  );

  // -- Fields --

  private int pyramidDepth = 1;
  private String profileXML;
  private List<String> allFiles = new ArrayList<String>();

  // -- Constructor --

  /** Constructs a new Vectra reader. */
  public VectraReader() {
    super("PerkinElmer Vectra/QPTIFF", new String[] {"tiff", "tif", "qptiff"});
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN, FormatTools.LM_DOMAIN};
    noSubresolutions = true;
    suffixSufficient = false;
    canSeparateSeries = false;
    hasCompanionFiles = true;
  }

  // -- IFormatReader API methods --

  /* (non-Javadoc)
   * @see loci.formats.FormatReader#isThisType(java.lang.String, boolean)
   */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (!open) {
      return checkSuffix(name, "qptiff");
    }
    try (RandomAccessInputStream stream = new RandomAccessInputStream(name)) {
      TiffParser tiffParser = new TiffParser(stream);
      tiffParser.setDoCaching(false);
      if (!tiffParser.isValidHeader()) {
        return false;
      }
      IFD ifd = tiffParser.getFirstIFD();
      if (ifd == null) {
        return false;
      }
      tiffParser.fillInIFD(ifd);
      String software = ifd.getIFDTextValue(IFD.SOFTWARE);
      return software != null && software.startsWith(SOFTWARE_CHECK);
    }
    catch (IOException e) {
      LOGGER.debug("I/O exception during isThisType() evaluation.", e);
      return false;
    }
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    if (allFiles.size() == 1) {
      return super.getSeriesUsedFiles(noPixels);
    }
    if (noPixels) {
      return allFiles.subList(
        1, allFiles.size()).toArray(new String[allFiles.size() - 1]);
    }
    return allFiles.toArray(new String[allFiles.size()]);
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
    int ifd = getIFDIndex(getCoreIndex(), no);
    tiffParser.getSamples(ifds.get(ifd), buf, x, y, w, h);
    return buf;
  }

  /* @see loci.formats.IFormatReader#openThumbBytes(int) */
  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    if (getCoreIndex() < pyramidDepth - 1) {
      int currentIndex = getCoreIndex();
      setCoreIndex(pyramidDepth - 1);
      byte[] thumb = openThumbBytes(no);
      setCoreIndex(currentIndex);
      return thumb;
    }
    return super.openThumbBytes(no);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pyramidDepth = 1;
      profileXML = null;
      allFiles.clear();
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    try {
      int ifd = getIFDIndex(getCoreIndex(), 0);
      return (int) ifds.get(ifd).getTileWidth();
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
      int ifd = getIFDIndex(getCoreIndex(), 0);
      return (int) ifds.get(ifd).getTileLength();
    }
    catch (FormatException e) {
      LOGGER.debug("", e);
    }
    return super.getOptimalTileHeight();
  }

  // -- Internal BaseTiffReader API methods --

  /* @see loci.formats.BaseTiffReader#initStandardMetadata() */
  @Override
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    // look for companion files

    Location currentFile = new Location(currentId).getAbsoluteFile();
    allFiles.add(currentFile.getAbsolutePath());
    Location parent = currentFile.getParentFile();
    String[] list = parent.list(true);
    Arrays.sort(list);
    for (String f : list) {
      if (f.endsWith(ANNOTATION_SUFFIX) || EXTRA_FILES.contains(f)) {
        allFiles.add(new Location(parent, f).getAbsolutePath());
      }
    }

    // set up IFDs

    ifds = tiffParser.getMainIFDs();
    thumbnailIFDs = null;

    for (IFD ifd : ifds) {
      tiffParser.fillInIFD(ifd);
    }

    // count number of channels

    CoreMetadata m = core.get(0, 0);
    m.sizeC = 1;

    if (ifds.get(0).getSamplesPerPixel() == 1) {
      long width = ifds.get(0).getImageWidth();
      long height = ifds.get(0).getImageLength();
      int ifd = 1;
      while (ifds.get(ifd).getImageWidth() == width &&
        ifds.get(ifd).getImageLength() == height) {
        m.sizeC++;
        ifd++;
      }
    }

    // count number of pyramid resolutions

    for (int start = m.sizeC + 1; start < ifds.size(); start += m.sizeC) {
      IFD ifd = ifds.get(start);
      if (ifd.getIFDIntValue(IFD.NEW_SUBFILE_TYPE) == 1) {
        pyramidDepth++;
      } else break;
    }

    int coreSize = ifds.size() - (pyramidDepth * (m.sizeC - 1));

    // repopulate core metadata

    core.clear();
    for (int s = 0; s < coreSize; s++) {
      CoreMetadata ms = new CoreMetadata(m);
      if (s == 0) {
        ms.resolutionCount = pyramidDepth;
      }
      if (s > 0 && s < pyramidDepth) {
        core.add(0, ms);
      }
      else {
        core.add(ms);
      }
    }

    for (int s = 0; s < core.size(); s++) {
      for (int r = 0; r < core.size(s); r++) {
        CoreMetadata ms = core.get(s, r);
        int index = getIFDIndex(core.flattenedIndex(s, r), 0);
        IFD ifd = ifds.get(index);
        PhotoInterp p = ifd.getPhotometricInterpretation();
        int samples = ifd.getSamplesPerPixel();
        ms.rgb = samples > 1 || p == PhotoInterp.RGB;

        ms.sizeX = (int) ifd.getImageWidth();
        ms.sizeY = (int) ifd.getImageLength();
        ms.sizeZ = 1;
        ms.sizeT = 1;
        if (ms.rgb) {
          ms.sizeC = samples;
        }
        ms.littleEndian = ifd.isLittleEndian();
        ms.indexed = p == PhotoInterp.RGB_PALETTE &&
          (get8BitLookupTable() != null || get16BitLookupTable() != null);
        ms.imageCount = ms.sizeC / samples;
        ms.pixelType = ifd.getPixelType();
        ms.metadataComplete = true;
        ms.interleaved = false;
        ms.falseColor = false;
        ms.dimensionOrder = "XYCZT";
        ms.thumbnail = s != 0 || r > 0;
      }
    }
  }

  /* @see loci.formats.BaseTiffReader#initMetadataStore() */
  @Override
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();

    MetadataStore store = makeFilterMetadata();

    for (int i=0; i<getSeriesCount(); i++) {
      setSeries(i);
      int coreIndex = seriesToCoreIndex(i);
      store.setImageName(getImageName(coreIndex), i);
      store.setImageDescription("", i);

      int ifdIndex = getIFDIndex(coreIndex, 0);
      IFD ifd = ifds.get(ifdIndex);
      double x = ifd.getXResolution();
      double y = ifd.getYResolution();

      store.setPixelsPhysicalSizeX(FormatTools.getPhysicalSizeX(x), i);
      store.setPixelsPhysicalSizeY(FormatTools.getPhysicalSizeY(y), i);

      TiffRational xPos = ifd.getIFDRationalValue(IFD.X_POSITION);
      TiffRational yPos = ifd.getIFDRationalValue(IFD.Y_POSITION);
      int unitMultiplier = ifd.getResolutionMultiplier();

      for (int c=0; c<getEffectiveSizeC(); c++) {
        store.setPlaneTheZ(new NonNegativeInteger(0), i, c);
        store.setPlaneTheT(new NonNegativeInteger(0), i, c);
        store.setPlaneTheC(new NonNegativeInteger(c), i, c);

        if (xPos != null) {
          double position = xPos.doubleValue() * unitMultiplier;
          store.setPlanePositionX(FormatTools.getPhysicalSizeX(position), i, c);
        }
        if (yPos != null) {
          double position = yPos.doubleValue() * unitMultiplier;
          store.setPlanePositionY(FormatTools.getPhysicalSizeY(position), i, c);
        }
      }
    }
    setSeries(0);

    // each high-resolution IFD has an XML description that needs to be parsed

    for (int c=0; c<getEffectiveSizeC(); c++) {
      String xml = getIFDComment(c);
      try {
        Element root = XMLTools.parseDOM(xml).getDocumentElement();
        NodeList children = root.getChildNodes();
        for (int i=0; i<children.getLength(); i++) {
          if (!(children.item(i) instanceof Element)) {
            continue;
          }
          Element e = (Element) children.item(i);

          String name = e.getNodeName();
          String value = e.getTextContent();
          if (name.equals("ScanProfile")) {
            try {
              Document profileRoot = XMLTools.createDocument();
              Node tmp = profileRoot.importNode(e, true);
              profileRoot.appendChild(tmp);
              profileXML = XMLTools.getXML(profileRoot);

              // scan profile XML is usually too long to be saved
              // when original metadata filtering is enabled, but there
              // is an API method below to retrieve it
              addGlobalMeta(name, profileXML);
            }
            catch (Exception ex) {
              LOGGER.debug("Could not preserve scan profile metadata", ex);
            }
          }
          else {
            addGlobalMetaList(name, value);
          }

          if (name.equals("Name")) {
            if (hasFlattenedResolutions()) {
              for (int series=0; series<pyramidDepth; series++) {
                store.setChannelName(value, series, c);
              }
            }
            else {
              store.setChannelName(value, 0, c);
            }
          }
          else if (name.equals("Color")) {
            String[] components = value.split(",");
            Color color = new Color(Integer.parseInt(components[0]),
              Integer.parseInt(components[1]), Integer.parseInt(components[2]), 255);
            if (hasFlattenedResolutions()) {
              for (int series=0; series<pyramidDepth; series++) {
                store.setChannelColor(color, series, c);
              }
            }
            else {
              store.setChannelColor(color, 0, c);
            }
          }
          else if (name.equals("Objective") && c == 0) {
            String instrument = MetadataTools.createLSID("Instrument", 0);
            String objective = MetadataTools.createLSID("Objective", 0, 0);

            store.setInstrumentID(instrument, 0);
            store.setObjectiveID(objective, 0, 0);
            store.setObjectiveModel(value, 0, 0);

            // objective model value is usually something like "20x",
            // so attempt to parse a valid magnification from it

            try {
              String mag = value.toLowerCase().replace("x", "");
              Double magFactor = DataTools.parseDouble(mag);
              store.setObjectiveNominalMagnification(magFactor, 0, 0);
            }
            catch (NumberFormatException ex) {
              LOGGER.info("Could not determine magnification: {}", value);
            }

            for (int series=0; series<getSeriesCount(); series++) {
              store.setImageInstrumentRef(instrument, series);
              store.setObjectiveSettingsID(objective, series);
            }
          }
          else if (name.equals("ExposureTime")) {
            Time exposure = new Time(DataTools.parseDouble(value), UNITS.MICROSECOND);
            store.setPlaneExposureTime(exposure, 0, c);
          }
        }
      }
      catch (ParserConfigurationException|SAXException|IOException e) {
        LOGGER.warn("Could not parse XML for channel {}", c);
        LOGGER.debug("", e);
      }
    }
  }

  // -- VectraReader API methods --

  /**
   * Returns an XML string corresponding to the ScanProfile node.
   */
  public String getScanProfileXML() {
    FormatTools.assertId(currentId, true, 1);
    return profileXML;
  }

  private String getImageName(int coreIndex) {
    if (coreIndex < pyramidDepth) {
      return "resolution #" + (coreIndex + 1);
    }
    if (coreIndex == pyramidDepth) {
      return "thumbnail";
    }
    String name = getImageType(getIFDIndex(coreIndex, 0));
    if (name != null) {
      return name;
    }
    return core.flattenedSize() == ifds.size() - 1 ? "label" : "macro";
  }

  private String getIFDComment(int ifdIndex) {
    String xml = ifds.get(ifdIndex).getComment().trim();
    // encoding is incorrectly specified as UTF-16
    // all files encountered so far are actually UTF-8
    xml = xml.replace("utf-16", "utf-8");
    return xml;
  }

  private String getImageType(int ifdIndex) {
    String xml = getIFDComment(ifdIndex);
    try {
      Element root = XMLTools.parseDOM(xml).getDocumentElement();
      NodeList types = root.getElementsByTagName("ImageType");
      if (types.getLength() > 0) {
        return types.item(0).getTextContent();
      }
    }
    catch (ParserConfigurationException|SAXException|IOException e) {
      LOGGER.debug("Could not determine image type for IFD #" + ifdIndex, e);
    }
    return null;
  }

  /**
   * Returns the index of the IFD to be used for the given
   * core index and image number.
   *
   * The IFD order in general is:
   *
   *  - IFD #0 to n-1: full resolution images (1 RGB for BF data, n grayscale for FL)
   *  - IFD #n: RGB thumbnail
   *  - IFD #n+1 to (n*2)-1: 50% resolution images (optional)
   *  - IFD #n*2 to (n*3)-1: 25% resolution images (optional)
   *  ...
   *  - macro/overview image (optional)
   *  - label image (optional)
   */
  private int getIFDIndex(int coreIndex, int no) {
    if (coreIndex == 0) {
      return no;
    }
    if (coreIndex < pyramidDepth) {
      return getImageCount() * coreIndex + 1 + no;
    }
    if (coreIndex == pyramidDepth) {
      // this is always the RGB thumbnail, which is stored between the
      // largest resolution and the rest of the pyramid
      return core.get(0, 0).imageCount;

    }
    // optional extra macro or label image at the end of the IFD list
    return ifds.size() - (core.flattenedSize() - coreIndex);
  }

}
