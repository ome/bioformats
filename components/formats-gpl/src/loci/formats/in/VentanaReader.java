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

  // -- Constructor --

  /** Constructs a new Ventana reader. */
  public VentanaReader() {
    super("Ventana .bif", new String[] {"bif"});
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN};
    suffixNecessary = true;
    noSubresolutions = true;
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
    if (getCoreIndex() >= core.get(0).resolutionCount) {
      tiffParser.getSamples(ifd, buf, x, y, w, h);
      return buf;
    }

    Region imageBox = new Region(x, y, w, h);

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int pixel = bpp * getRGBChannelCount();
    int outputRowLen = w * bpp;
    boolean interleaved = isInterleaved();
    if (interleaved) {
      outputRowLen *= getRGBChannelCount();
    }
    byte[] tilePixels = new byte[tileWidth * tileHeight * pixel];
    for (TIFFTile tile : tiles) {
      Region tileBox = new Region(tile.realX, tile.realY, tileWidth, tileHeight);

      if (tileBox.intersects(imageBox)) {
        tiffParser.getSamples(ifd, tilePixels, tile.baseX, tile.baseY, tileWidth, tileHeight);

        Region intersection = tileBox.intersection(imageBox);
        int intersectionX = 0;

        if (tileBox.x < imageBox.x) {
          intersectionX = imageBox.x - tileBox.x;
        }

        int rowLen = (int) Math.min(intersection.width, tileWidth) * bpp;
        if (interleaved) {
          rowLen *= getRGBChannelCount();
        }

        int count = interleaved ? 1 : getRGBChannelCount();
        for (int c=0; c<count; c++) {
          for (int row=0; row<intersection.height; row++) {
            int realRow = row + intersection.y - tileBox.y;
            int inputOffset = bpp * (realRow * tileWidth + intersection.x - tileBox.x);
            if (interleaved) {
              inputOffset *= getRGBChannelCount();
            }
            else {
              inputOffset += (c * tileWidth * tileHeight * bpp);
            }
            int outputOffset = bpp * (intersection.x - x) + outputRowLen * (row + intersection.y - y);
            if (interleaved) {
              outputOffset *= getRGBChannelCount();
            }
            else {
              outputOffset += (c * w * h * bpp);
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

    for (int s=0; s<seriesCount; s++) {
      CoreMetadata ms = core.get(s);
      if (s == 0) {
        ms.resolutionCount = resolutionCount;
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

    // now process TIFF tiles and overlap data to get the real coordinates for each tile
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

      // largest tile index is in upper right corner
      // smallest tile index is in lower left corner (snake ordering)

      for (Overlap overlap : area.overlaps) {
        int thisRow = getTileRow(overlap.a, area.tileRows, area.tileColumns);
        int thisCol = getTileColumn(overlap.a, area.tileRows, area.tileColumns);
        if (overlap.direction.equals("RIGHT")) {
          for (int col=0; col<=thisCol; col++) {
            int currentIndex = (tileRow + thisRow) * tileCols + (tileCol + col);
            tiles[currentIndex].realX += overlap.x;
          }
          int currentIndex = (tileRow + thisRow) * tileCols + (tileCol + thisCol);
          tiles[currentIndex].realY += overlap.y;
        }
        else if (overlap.direction.equals("UP")) {
          for (int row=thisRow; row<area.tileRows; row++) {
            int currentIndex = (tileRow + row) * tileCols + (tileCol + thisCol);
            tiles[currentIndex].realY -= overlap.y;
          }
        }
        else {
          throw new FormatException("Unsupported overlap direction: " + overlap.direction);
        }
      }
    }
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
    MetadataTools.populatePixels(store, this, getImageCount() > 1);

    String instrument = MetadataTools.createLSID("Instrument", 0);
    String objective = MetadataTools.createLSID("Objective", 0, 0);
    store.setInstrumentID(instrument, 0);
    store.setObjectiveID(objective, 0, 0);
    store.setObjectiveNominalMagnification(magnification, 0, 0);

    for (int i=0; i<getSeriesCount(); i++) {
      setSeries(i);
      store.setImageInstrumentRef(instrument, i);
      store.setObjectiveSettingsID(objective, i);
    }
    setSeries(0);
  }

  private int getIFDIndex(int coreIndex, int no) {
    int extra = ifds.size() - (core.get(0).resolutionCount * core.get(0).imageCount);
    if (coreIndex < core.size() - extra) {
      return extra + (coreIndex * core.get(0).imageCount) + no;
    }
    return coreIndex - (core.size() - extra);
  }

  private void parseXML(String xml) throws IOException {
    Element root = null;
    try {
      root = XMLTools.parseDOM(xml).getDocumentElement();
    }
    catch (ParserConfigurationException | SAXException e) {
      throw new IOException(e);
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
