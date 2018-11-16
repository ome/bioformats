/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2018 Open Microscopy Environment:
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
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffIFDEntry;
import loci.formats.tiff.TiffParser;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Length;
import ome.units.UNITS;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * VentanaReader is the file format reader for Ventana .bif files.
 */
public class VentanaReader extends BaseTiffReader {

  // -- Constants --

  public static final String SPLIT_TILES_KEY = "ventana.split_tiles";
  public static final boolean SPLIT_TILES_DEFAULT = false;

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(VentanaReader.class);

  // primary metadata, not present on subresolution images
  private static final int XML_TAG = 700;

  // Photoshop image resources, see
  // https://www.adobe.com/devnet-apps/photoshop/fileformatashtml/#50577413_pgfId-1039502
  private static final int EXTRA_TAG_1 = 34377;

  // Microsoft ICM color profile; only expected on full resolution image
  private static final int EXTRA_TAG_2 = 34677;

  // -- Fields --

  private Double magnification = null;
  private List<AreaOfInterest> areas = new ArrayList<AreaOfInterest>();
  private int tileWidth, tileHeight;
  private TIFFTile[] tiles;
  private int resolutions = 0;
  private Double physicalPixelSize = null;

  // -- Constructor --

  /** Constructs a new Ventana reader. */
  public VentanaReader() {
    super("Ventana .bif", new String[] {"bif"});
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN};
    suffixNecessary = true;
    noSubresolutions = true;
  }

  // -- VentanaReader API methods --

  /**
   * @return true if MetadataOptions are set so that each tile
   *  in the full resolution image will be returned as a separate series
   */
  public boolean splitTiles() {
    MetadataOptions options = getMetadataOptions();
    if (options instanceof DynamicMetadataOptions) {
      return ((DynamicMetadataOptions) options).getBoolean(
        SPLIT_TILES_KEY, SPLIT_TILES_DEFAULT);
    }
    return SPLIT_TILES_DEFAULT;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* (non-Javadoc)
   * @see loci.formats.FormatReader#isThisType(java.lang.String, boolean)
   */
  @Override
  public boolean isThisType(String name, boolean open) {
    boolean isThisType = super.isThisType(name, open);
    if (!isThisType && open) {
      RandomAccessInputStream stream = null;
      try {
        stream = new RandomAccessInputStream(name);
        TiffParser tiffParser = new TiffParser(stream);
        tiffParser.setDoCaching(false);
        if (!tiffParser.isValidHeader()) {
          return false;
        }
        IFD ifd = tiffParser.getFirstIFD();
        if (ifd == null) {
          return false;
        }
        return ifd.get(XML_TAG) != null;
      }
      catch (IOException e) {
        LOGGER.debug("I/O exception during isThisType() evaluation.", e);
        return false;
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
    return isThisType;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    lastPlane = getIFDIndex(getCoreIndex(), 0);
    return super.get8BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    lastPlane = getIFDIndex(getCoreIndex(), 0);
    return super.get16BitLookupTable();
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
    Arrays.fill(buf, (byte) 0);
    IFD ifd = ifds.get(getIFDIndex(getCoreIndex(), no));

    if (splitTiles()) {
      TIFFTile tile = tiles[getCoreIndex()];
      tiffParser.getSamples(ifd, buf, tile.baseX + x, tile.baseY + y, w, h);
      return buf;
    }

    if (getCoreIndex() >= core.get(0).resolutionCount) {
      tiffParser.getSamples(ifd, buf, x, y, w, h);
      return buf;
    }

    Region imageBox = new Region(x, y, w, h);

    boolean interleaved = isInterleaved();
    int count = interleaved ? 1 : getRGBChannelCount();
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int pixel = bpp * getRGBChannelCount();
    int tilePixel = bpp * (getRGBChannelCount() / count);
    int outputRowLen = w * tilePixel;

    int scale = getScale(getCoreIndex());
    int thisTileWidth = tileWidth / scale;
    int thisTileHeight = tileHeight / scale;

    byte[] subResTile = null;
    int subResX = -1, subResY = -1;

    byte[] tilePixels = new byte[thisTileWidth * thisTileHeight * pixel];
    for (TIFFTile tile : tiles) {
      Region tileBox = new Region(tile.realX / scale, tile.realY / scale, thisTileWidth, thisTileHeight);

      if (tileBox.intersects(imageBox)) {
        if (scale == 1) {
          tiffParser.getSamples(ifd, tilePixels, tile.baseX, tile.baseY, thisTileWidth, thisTileHeight);
        }
        else {
          // load a whole tile from the subresolution IFD for reuse
          // it's less complicated to just call tiffParser.setSamples(...)
          // each time, but also an order of magnitude slower
          int resX = tile.baseX / scale;
          int offsetX = resX % tileWidth;
          resX -= offsetX;
          int resY = tile.baseY / scale;
          int offsetY = resY % tileHeight;
          resY -= offsetY;
          if (resX != subResX || resY != subResY || subResTile == null) {
            if (subResTile == null) {
              subResTile = new byte[tileWidth * tileHeight * pixel];
            }
            tiffParser.getSamples(ifd, subResTile, resX, resY, tileWidth, tileHeight);
            subResX = resX;
            subResY = resY;
          }
          int inRow = tileWidth * tilePixel;
          int outRow = thisTileWidth * tilePixel;
          int input = 0;
          int output = 0;
          for (int c=0; c<count; c++) {
            input = (c * tileHeight * inRow) + (offsetY * inRow) + offsetX * tilePixel;
            output = c * thisTileHeight * outRow;
            for (int row=0; row<thisTileHeight; row++) {
              System.arraycopy(subResTile, input, tilePixels, output, outRow);
              input += inRow;
              output += outRow;
            }
          }
        }

        Region intersection = tileBox.intersection(imageBox);
        int intersectionX = 0;

        if (tileBox.x < imageBox.x) {
          intersectionX = imageBox.x - tileBox.x;
        }

        int rowLen = (int) Math.min(intersection.width, thisTileWidth) * tilePixel;

        for (int c=0; c<count; c++) {
          for (int row=0; row<intersection.height; row++) {
            int realRow = row + intersection.y - tileBox.y;
            int inputOffset = tilePixel * (realRow * thisTileWidth + intersection.x - tileBox.x);
            if (!interleaved) {
              inputOffset += (c * thisTileWidth * thisTileHeight * tilePixel);
            }
            int outputOffset = tilePixel * (intersection.x - x) + outputRowLen * (row + intersection.y - y);
            if (!interleaved) {
              outputOffset += (c * w * h * tilePixel);
            }
            int copy = (int) Math.min(rowLen, buf.length - outputOffset);
            System.arraycopy(tilePixels, inputOffset, buf, outputOffset, copy);
          }
        }
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#openThumbBytes(int) */
  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    if (getCoreIndex() < core.get(0).resolutionCount) {
      int currentCore = getCoreIndex();
      setCoreIndex(core.get(0).resolutionCount - 1);
      byte[] thumb = super.openThumbBytes(no);
      setCoreIndex(currentCore);
      return thumb;
    }
    return super.openThumbBytes(no);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      magnification = null;
      areas.clear();
      tileWidth = 0;
      tileHeight = 0;
      tiles = null;
      resolutions = 0;
      physicalPixelSize = null;
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

    ifds = tiffParser.getMainIFDs();

    int seriesCount = ifds.size();

    core.clear();
    for (int i=0; i<seriesCount; i++) {
      core.add(new CoreMetadata());
    }

    int resolutionCount = 0;
    for (int i=0; i<seriesCount; i++) {
      setSeries(i);
      int index = getIFDIndex(i, 0);
      tiffParser.fillInIFD(ifds.get(index));

      String comment = ifds.get(index).getComment();
      if (comment == null) {
        continue;
      }
      String[] tokens = comment.split(" ");
      for (String token : tokens) {
        String[] v = token.split("=");
        if (v.length == 2) {
          addSeriesMeta(v[0], v[1]);

          if (v[0].equals("level")) {
            resolutionCount++;
          }
          else if (v[0].equals("mag")) {
            magnification = DataTools.parseDouble(v[1]);
          }
        }
      }
      String xml = ifds.get(index).getIFDTextValue(XML_TAG);
      LOGGER.debug("XMP tag for series #{} = {}", i, xml);
      if (xml != null && resolutionCount == 1) {
        parseXML(xml);
      }
    }
    setSeries(0);
    resolutions = resolutionCount;

    for (int s=0; s<seriesCount; s++) {
      CoreMetadata ms = core.get(s);
      if (s == 0 && resolutions > 0) {
        ms.resolutionCount = resolutions;
      }
      ms.sizeZ = 1;
      ms.sizeT = 1;
      ms.imageCount = ms.sizeZ * ms.sizeT;

      int ifdIndex = getIFDIndex(s, 0);
      IFD ifd = ifds.get(ifdIndex);
      PhotoInterp p = ifd.getPhotometricInterpretation();
      int samples = ifd.getSamplesPerPixel();
      ms.rgb = samples > 1 || p == PhotoInterp.RGB;

      ms.sizeX = (int) ifd.getImageWidth();
      ms.sizeY = (int) ifd.getImageLength();
      ms.sizeC = ms.rgb ? samples : 1;
      ms.littleEndian = ifd.isLittleEndian();
      ms.indexed = p == PhotoInterp.RGB_PALETTE &&
        (get8BitLookupTable() != null || get16BitLookupTable() != null);
      ms.pixelType = ifd.getPixelType();
      ms.metadataComplete = true;
      ms.interleaved = false;
      ms.falseColor = false;
      ms.dimensionOrder = "XYCZT";
      ms.thumbnail = s != 0;

      if (s == 0) {
        tileWidth = (int) ifd.getTileWidth();
        tileHeight = (int) ifd.getTileLength();
        long[] offsets = ifd.getStripOffsets();
        int x = 0, y = 0;
        tiles = new TIFFTile[offsets.length];
        for (int i=0; i<offsets.length; i++) {
          tiles[i] = new TIFFTile();
          tiles[i].offset = offsets[i];
          tiles[i].ifd = ifdIndex;
          tiles[i].realX = -1 * tileWidth;
          tiles[i].realY = -1 * tileHeight;
          tiles[i].baseX = tileWidth * (x++);
          tiles[i].baseY = tileHeight * y;
          if (x * tileWidth >= ms.sizeX) {
            y++;
            x = 0;
          }
        }
      }
    }

    if (splitTiles()) {
      CoreMetadata first = core.get(0);
      core.clear();
      for (TIFFTile tile : tiles) {
        CoreMetadata m = new CoreMetadata(first);
        m.sizeX = tileWidth;
        m.sizeY = tileHeight;
        m.thumbnail = false;
        core.add(m);
      }
      return;
    }

    // now process TIFF tiles and overlap data to get the real coordinates for each tile
    // largest tile index is in upper right corner
    // smallest tile index is in lower left corner (snake ordering)
    int tileCols = core.get(0).sizeX / tileWidth;
    for (AreaOfInterest area : areas) {
      int tileRow = area.yOrigin / tileHeight;
      int tileCol = area.xOrigin / tileWidth;

      for (int row=0; row<area.tileRows; row++) {
        for (int col=0; col<area.tileColumns; col++) {
          int index = (tileRow + row) * tileCols + (tileCol + col);
          tiles[index].realX = tiles[index].baseX;
          tiles[index].realY = tiles[index].baseY;
        }
      }

      // list of overlaps is not authoritative
      // some values will be wrong, and some overlaps will be missing
      // average the RIGHT values and apply the average to all tiles
      // then average the UP values and apply to all tiles

      HashMap<Integer, Integer> columnYAdjust = new HashMap<Integer, Integer>();
      double rightSum = 0.0;
      double upSum = 0.0;
      int rightCount = 0;
      int upCount = 0;
      for (Overlap overlap : area.overlaps) {
        if (overlap.confidence < 98) {
          continue;
        }
        if (overlap.direction.equals("RIGHT")) {
          rightSum += overlap.x;
          rightCount++;
          columnYAdjust.put(getTileColumn(overlap.a, area.tileRows, area.tileColumns), overlap.y);
        }
        else if (overlap.direction.equals("UP")) {
          upSum += overlap.y;
          upCount++;
        }
        else {
          throw new FormatException("Unsupported overlap direction: " + overlap.direction);
        }
      }
      if (rightCount > 0) {
        rightSum /= rightCount;
      }
      if (upCount > 0) {
        upSum /= upCount;
      }

      for (int row=0; row<area.tileRows; row++) {
        for (int col=0; col<area.tileColumns; col++) {
          int index = (tileRow + row) * tileCols + (tileCol + col);
          tiles[index].realX -= (rightSum * col);
          tiles[index].realY -= (upSum * row);
          if (columnYAdjust.containsKey(col) && columnYAdjust.get(col) > 0) {
            tiles[index].realY += columnYAdjust.get(col);
          }
        }
      }
    }
  }

  private int getScale(int coreIndex) {
    return (int) Math.round((double) core.get(0).sizeX / core.get(coreIndex).sizeX);
  }

  private int getTileRow(int index, int rows, int cols) {
    int row = (int) Math.floor((double) index / cols);
    return rows - row - 1;
  }

  private int getTileColumn(int index, int rows, int cols) {
    int row = (int) Math.floor((double) index / cols);
    // even numbered rows increase from left to right
    // odd numbered rows decrease from left to right
    int col = index - (row * cols);
    if (row % 2 == 1) {
      return cols - col - 1;
    }
    return col;
  }

  /* @see loci.formats.BaseTiffReader#initMetadataStore() */
  @Override
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, splitTiles() || getImageCount() > 1);

    String instrument = MetadataTools.createLSID("Instrument", 0);
    String objective = MetadataTools.createLSID("Objective", 0, 0);
    store.setInstrumentID(instrument, 0);
    store.setObjectiveID(objective, 0, 0);
    store.setObjectiveNominalMagnification(magnification, 0, 0);

    for (int i=0; i<getSeriesCount(); i++) {
      setSeries(i);
      store.setImageInstrumentRef(instrument, i);
      store.setObjectiveSettingsID(objective, i);

      if (physicalPixelSize != null) {
        store.setPixelsPhysicalSizeX(new Length(physicalPixelSize, UNITS.MICROM), i);
        store.setPixelsPhysicalSizeY(new Length(physicalPixelSize, UNITS.MICROM), i);
      }
      if (splitTiles()) {
        for (int p=0; p<getImageCount(); p++) {
          store.setPlanePositionX(new Length(tiles[i].baseX, UNITS.REFERENCEFRAME), i, p);
          store.setPlanePositionY(new Length(tiles[i].baseY, UNITS.REFERENCEFRAME), i, p);
        }
      }
    }
    setSeries(0);
  }

  private int getIFDIndex(int coreIndex, int no) {
    if (splitTiles() && coreIndex > 0 && resolutions > 0) {
      return getIFDIndex(0, no);
    }
    int extra = ifds.size() - (resolutions * core.get(0).imageCount);
    if (coreIndex < ifds.size() - extra) {
      return extra + (coreIndex * core.get(0).imageCount) + no;
    }
    return coreIndex - (ifds.size() - extra);
  }

  private void parseXML(String xml) throws IOException {
    Element root = null;
    try {
      root = XMLTools.parseDOM(xml).getDocumentElement();
    }
    catch (ParserConfigurationException | SAXException e) {
      throw new IOException(e);
    }

    Element iScan = (Element) root.getElementsByTagName("iScan").item(0);
    String physicalSize = iScan.getAttribute("ScanRes");
    if (physicalSize != null) {
      physicalPixelSize = DataTools.parseDouble(physicalSize);
    }

    Element slideStitchInfo = (Element) root.getElementsByTagName("SlideStitchInfo").item(0);
    Element aoiOrigins = (Element) root.getElementsByTagName("AoiOrigin").item(0);

    NodeList imageInfos = slideStitchInfo.getElementsByTagName("ImageInfo");
    for (int i=0; i<imageInfos.getLength(); i++) {
      Element imageInfo = (Element) imageInfos.item(i);

      if (imageInfo.getAttribute("AOIScanned").equals("0")) {
        continue;
      }
      AreaOfInterest aoi = new AreaOfInterest();
      aoi.index = Integer.parseInt(imageInfo.getAttribute("AOIIndex"));
      aoi.tileRows = Integer.parseInt(imageInfo.getAttribute("NumRows"));
      aoi.tileColumns = Integer.parseInt(imageInfo.getAttribute("NumCols"));

      NodeList joints = imageInfo.getElementsByTagName("TileJointInfo");
      for (int j=0; j<joints.getLength(); j++) {
        Element joint = (Element) joints.item(j);
        if (joint.getAttribute("FlagJoined").equals("1")) {
          Overlap overlap = new Overlap();
          overlap.a = Integer.parseInt(joint.getAttribute("Tile1")) - 1;
          overlap.b = Integer.parseInt(joint.getAttribute("Tile2")) - 1;
          overlap.x = DataTools.parseDouble(joint.getAttribute("OverlapX")).intValue();
          overlap.y = DataTools.parseDouble(joint.getAttribute("OverlapY")).intValue();
          overlap.direction = joint.getAttribute("Direction");
          overlap.confidence = Integer.parseInt(joint.getAttribute("Confidence"));
          aoi.overlaps.add(overlap);
        }
      }
      areas.add(aoi);
    }

    NodeList aois = aoiOrigins.getChildNodes();
    for (int i=0; i<aois.getLength(); i++) {
      String name = aois.item(i).getNodeName();
      if (!name.startsWith("AOI")) {
        continue;
      }
      Element aoi = (Element) aois.item(i);
      int thisIndex = Integer.parseInt(name.replace("AOI", ""));
      for (AreaOfInterest a : areas) {
        if (a.index == thisIndex) {
          a.xOrigin = Integer.parseInt(aoi.getAttribute("OriginX"));
          a.yOrigin = Integer.parseInt(aoi.getAttribute("OriginY"));
        }
      }
    }
  }


  class AreaOfInterest {
    // origin in pixels
    public int xOrigin;
    public int yOrigin;
    public int index;
    public int tileRows;
    public int tileColumns;
    public List<Overlap> overlaps = new ArrayList<Overlap>();

    @Override
    public String toString() {
      return "AOI #" + index + ", X=" + xOrigin + ", Y=" + yOrigin +
        ", rows=" + tileRows + ", cols=" + tileColumns +
        ", overlaps=" + overlaps.size();
    }
  }

  class Overlap implements Comparable<Overlap> {
    public int a;
    public int b;
    public int x;
    public int y;
    public int confidence;
    public String direction;

    public int compareTo(Overlap o) {
      if (a != o.a) {
        return a - o.a;
      }
      return b - o.b;
    }

    @Override
    public String toString() {
      return a + " => " + b + ", overlap = {" + x + ", " + y +
        "}, direction = " + direction;
    }
  }

  class TIFFTile {
    public int ifd;
    public long offset;
    // these are the XY coordinates if we were placing the tile
    // as in standard TIFF, with no overlaps
    public int baseX;
    public int baseY;
    // these are the XY coordinates after overlap calculation
    public int realX;
    public int realY;
  }

}
