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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import ome.xml.model.primitives.Timestamp;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
import loci.formats.meta.MetadataStore;
import loci.formats.services.POIService;
import loci.formats.tiff.IFD;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffParser;

/**
 * IPWReader is the file format reader for Image-Pro Workspace (IPW) files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class IPWReader extends FormatReader {

  // -- Constants --

  public static final int IPW_MAGIC_BYTES = 0xd0cf11e0;

  // -- Fields --

  /** List of embedded image file names (one per image plane). */
  private Map<Integer, String> imageFiles;

  /** Helper reader - parses embedded files from the OLE document. */
  private transient POIService poi;

  // -- Constructor --

  /** Constructs a new IPW reader. */
  public IPWReader() {
    super("Image-Pro Workspace", "ipw");
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 4;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readInt() == IPW_MAGIC_BYTES;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    try (RandomAccessInputStream stream = poi.getDocumentStream(imageFiles.get(0))) {
      TiffParser tp = new TiffParser(stream);
      IFD firstIFD = tp.getFirstIFD();
      int[] bits = firstIFD.getBitsPerSample();
      if (bits[0] <= 8) {
        int[] colorMap = tp.getColorMap(firstIFD);
        if (colorMap == null) {
          return null;
        }

        byte[][] table = new byte[3][colorMap.length / 3];
        int next = 0;
        for (int j=0; j<table.length; j++) {
          for (int i=0; i<table[0].length; i++) {
            table[j][i] = (byte) (colorMap[next++] >> 8);
          }
        }

          return table;
        }
    }
    return null;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    try (RandomAccessInputStream stream = poi.getDocumentStream(imageFiles.get(0))) {
      TiffParser tp = new TiffParser(stream);
      IFD ifd = tp.getFirstIFD();
      return (int) ifd.getTileWidth();
    }
    catch (FormatException e) {
      LOGGER.debug("Could not retrieve tile width", e);
    }
    catch (IOException e) {
      LOGGER.debug("Could not retrieve tile height", e);
    }
    return super.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    try (RandomAccessInputStream stream = poi.getDocumentStream(imageFiles.get(0))) {
      TiffParser tp = new TiffParser(stream);
      IFD ifd = tp.getFirstIFD();
      stream.close();
      return (int) ifd.getTileLength();
    }
    catch (FormatException e) {
      LOGGER.debug("Could not retrieve tile height", e);
    }
    catch (IOException e) {
      LOGGER.debug("Could not retrieve tile length", e);
    }
    return super.getOptimalTileHeight();
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (poi == null) {
      initPOIService();
    }

    try (RandomAccessInputStream stream = poi.getDocumentStream(imageFiles.get(no))) {
      TiffParser tp = new TiffParser(stream);
      IFD ifd = tp.getFirstIFD();
      tp.getSamples(ifd, buf, x, y, w, h);
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (poi != null) poi.close();
      poi = null;
      imageFiles = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    in = new RandomAccessInputStream(id);
    initPOIService();

    imageFiles = new HashMap<Integer, String>();

    Vector<String> fileList = poi.getDocumentList();

    String description = null, creationDate = null;

    CoreMetadata m = core.get(0);

    for (String name : fileList) {
      String relativePath =
        name.substring(name.lastIndexOf(File.separator) + 1);

      if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
        if (relativePath.equals("CONTENTS")) {
          addGlobalMeta("Version", new String(
            poi.getDocumentBytes(name), Constants.ENCODING).trim());
        }
        else if (relativePath.equals("FrameRate")) {
          byte[] b = poi.getDocumentBytes(name, 4);
          addGlobalMeta("Frame Rate", DataTools.bytesToInt(b, true));
        }
        else if (relativePath.equals("FrameInfo")) {
          RandomAccessInputStream s = poi.getDocumentStream(name);
          s.order(true);
          for (int q=0; q<s.length()/2; q++) {
            addGlobalMetaList("FrameInfo", s.readShort());
          }
          s.close();
        }
      }

      if (relativePath.equals("ImageInfo")) {
        description =
          new String(poi.getDocumentBytes(name), Constants.ENCODING).trim();
        addGlobalMeta("Image Description", description);

        String timestamp = null;

        // parse the description to get channels/slices/times where applicable
        // basically the same as in SEQReader
        if (description != null) {
          String[] tokens = description.split("\n");
          for (String token : tokens) {
            String label = "Timestamp";
            String data = token.trim();
            if (token.indexOf('=') != -1) {
              label = token.substring(0, token.indexOf('=')).trim();
              data = token.substring(token.indexOf('=') + 1).trim();
            }
            addGlobalMeta(label, data);
            if (label.equals("frames")) m.sizeT = Integer.parseInt(data);
            else if (label.equals("slices")) {
              m.sizeZ = Integer.parseInt(data);
            }
            else if (label.equals("channels")) {
              m.sizeC = Integer.parseInt(data);
            }
            else if (label.equals("Timestamp")) timestamp = data;
          }
        }

        if (timestamp != null) {
          if (timestamp.length() > 26) {
            timestamp = timestamp.substring(timestamp.length() - 26);
          }
          creationDate =
            DateTools.formatDate(timestamp, "MM/dd/yyyy HH:mm:ss aa", ".");
        }
      }
      else if (relativePath.equals("ImageTIFF")) {
        // pixel data
        String idx = "0";
        if (!name.substring(0,
          name.lastIndexOf(File.separator)).equals("Root Entry"))
        {
          idx = name.substring(21, name.indexOf(File.separator, 22));
        }

        imageFiles.put(new Integer(idx), name);
      }
    }

    LOGGER.info("Populating metadata");

    m.imageCount = imageFiles.size();
    IFD firstIFD = null;
    try (RandomAccessInputStream stream = poi.getDocumentStream(imageFiles.get(0))) {
      TiffParser tp = new TiffParser(stream);
      firstIFD = tp.getFirstIFD();
    }

    m.rgb = firstIFD.getSamplesPerPixel() > 1;

    if (!isRGB()) {
      m.indexed =
        firstIFD.getPhotometricInterpretation() == PhotoInterp.RGB_PALETTE;
    }
    if (isIndexed()) {
      m.sizeC = 1;
      m.rgb = false;
    }

    m.littleEndian = firstIFD.isLittleEndian();

    // retrieve axis sizes

    addGlobalMeta("slices", "1");
    addGlobalMeta("channels", "1");
    addGlobalMeta("frames", getImageCount());

    m.sizeX = (int) firstIFD.getImageWidth();
    m.sizeY = (int) firstIFD.getImageLength();
    m.dimensionOrder = isRGB() ? "XYCZT" : "XYZCT";

    if (getSizeZ() == 0) m.sizeZ = 1;
    if (getSizeC() == 0) m.sizeC = 1;
    if (getSizeT() == 0) m.sizeT = 1;

    if (getSizeZ() * getSizeC() * getSizeT() == 1 && getImageCount() != 1) {
      m.sizeZ = getImageCount();
    }

    if (isRGB()) m.sizeC *= 3;

    int bitsPerSample = firstIFD.getBitsPerSample()[0];
    m.pixelType = firstIFD.getPixelType();

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
    store.setImageDescription(description, 0);
    if (creationDate != null) {
      store.setImageAcquisitionDate(new Timestamp(creationDate), 0);
    }
  }

  private void initPOIService() throws FormatException, IOException {
    try {
      ServiceFactory factory = new ServiceFactory();
      poi = factory.getInstance(POIService.class);
    }
    catch (DependencyException de) {
      throw new MissingLibraryException("POI library not found", de);
    }

    poi.initialize(Location.getMappedId(getCurrentFile()));
  }

}
