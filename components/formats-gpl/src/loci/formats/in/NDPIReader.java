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

import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.common.services.ServiceException;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.services.JPEGTurboService;
import loci.formats.services.JPEGTurboServiceImpl;
import loci.formats.tiff.IFD;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffIFDEntry;
import loci.formats.tiff.TiffParser;

import ome.xml.model.primitives.Timestamp;
import ome.units.quantity.Length;

/**
 * NDPIReader is the file format reader for Hamamatsu .ndpi files.
 */
public class NDPIReader extends BaseTiffReader {

  // -- Constants --

  private static final int MAX_SIZE = 2048;
  private static final int SOURCE_LENS = 65421;
  private static final int MARKER_TAG = 65426;
  private static final int THUMB_TAG_2 = 65439;
  private static final int METADATA_TAG = 65449;

  // -- Fields --

  private int initializedSeries = -1;
  private int initializedPlane = -1;

  private int sizeZ = 1;
  private int pyramidHeight = 1;

  private JPEGTurboService service = new JPEGTurboServiceImpl();

  private Double magnification;
  private String serialNumber;
  private String instrumentModel;

  // -- Constructor --

  /** Constructs a new NDPI reader. */
  public NDPIReader() {
    super("Hamamatsu NDPI", new String[] {"ndpi"});
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* (non-Javadoc)
   * @see loci.formats.FormatReader#isThisType(java.lang.String, boolean)
   */
  @Override
  public boolean isThisType(String name, boolean open) {
    boolean isThisType = super.isThisType(name, open);
    if (isThisType && open) {
      RandomAccessInputStream stream = null;
      TiffParser parser = null;
      try {
        stream = new RandomAccessInputStream(name);
        parser = new TiffParser(stream);
        parser.setDoCaching(false);
        parser.setUse64BitOffsets(stream.length() >= Math.pow(2, 32));
        if (!parser.isValidHeader()) {
          return false;
        }
        IFD ifd = parser.getFirstIFD();
        if (ifd == null) {
          return false;
        }
        return ifd.containsKey(MARKER_TAG);
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
          if (parser != null) {
            parser.getStream().close();
          }
        }
        catch (IOException e) {
          LOGGER.debug("I/O exception during stream closure.", e);
        }
      }
    }
    return isThisType;
  }

  /** @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return MUST_GROUP;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (x == 0 && y == 0 && w == 1 && h == 1) {
      return buf;
    }
    else if (getSizeX() <= MAX_SIZE || getSizeY() <= MAX_SIZE) {
      int ifdIndex = getIFDIndex(getCoreIndex(), no);
      in = new RandomAccessInputStream(currentId);
      tiffParser = new TiffParser(in);
      tiffParser.setUse64BitOffsets(true);
      tiffParser.setYCbCrCorrection(false);
      byte[] b = tiffParser.getSamples(ifds.get(ifdIndex), buf, x, y, w, h);
      in.close();
      tiffParser.getStream().close();
      return b;
    }

    if (initializedSeries != getCoreIndex() || initializedPlane != no) {
      IFD ifd = ifds.get(getIFDIndex(getCoreIndex(), no));

      long offset = ifd.getStripOffsets()[0];
      long byteCount = ifd.getStripByteCounts()[0];
      if (in != null) {
        in.close();
      }
      in = new RandomAccessInputStream(currentId);
      in.seek(offset);
      in.setLength(offset + byteCount);

      try {
        service.close();
        long[] markers = ifd.getIFDLongArray(MARKER_TAG);
        if (!use64Bit) {
          for (int i=0; i<markers.length; i++) {
            markers[i] = markers[i] & 0xffffffffL;
          }
        }
        if (markers != null) {
          service.setRestartMarkers(markers);
        }
        service.initialize(in, getSizeX(), getSizeY());
      }
      catch (ServiceException e) {
        throw new FormatException(e);
      }

      initializedSeries = getCoreIndex();
      initializedPlane = no;
    }
    service.getTile(buf, x, y, w, h);
    return buf;
  }

  /* @see loci.formats.IFormatReader#openThumbBytes(int) */
  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);

    int currentSeries = getSeries();
    if (getCoreIndex() >= pyramidHeight) {
      return super.openThumbBytes(no);
    }

    int thumbX = getThumbSizeX();
    int thumbY = getThumbSizeY();
    int rgbCount = getRGBChannelCount();

    if (hasFlattenedResolutions()) {
      setSeries(pyramidHeight - 1);
    }
    else {
      setResolution(pyramidHeight - 1);
    }

    byte[] thumb = null;

    if (thumbX == getThumbSizeX() && thumbY == getThumbSizeY() &&
      rgbCount == getRGBChannelCount())
    {
      thumb = FormatTools.openThumbBytes(this, no);
      setSeries(currentSeries);
      setResolution(0);
    }
    else {
      // find the smallest series with the same aspect ratio
      for (int s=getSeriesCount()-1; s>=0; s--) {
        setSeries(s);
        if (thumbX == getThumbSizeX() && thumbY == getThumbSizeY() &&
          s != currentSeries && rgbCount == getRGBChannelCount())
        {
          thumb = FormatTools.openThumbBytes(this, no);
          break;
        }
      }
      setSeries(currentSeries);
      if (thumb == null) {
        thumb = FormatTools.openThumbBytes(this, no);
      }
    }
    return thumb;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    if (!fileOnly) {
      service.close();
      initializedSeries = -1;
      initializedPlane = -1;
      sizeZ = 1;
      pyramidHeight = 1;
      magnification = null;
      serialNumber = null;
      instrumentModel = null;
      if (tiffParser != null) {
        tiffParser.getStream().close();
      }
    }
    super.close(fileOnly);
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    return 1024;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return 1024;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    RandomAccessInputStream s = new RandomAccessInputStream(id);
    use64Bit = s.length() >= Math.pow(2, 32);
    s.close();
    super.initFile(id);
  }

  // -- Internal BaseTiffReader API methods --

  /* @see loci.formats.BaseTiffReader#initStandardMetadata() */
  @Override
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    ifds = tiffParser.getMainIFDs();

    // fix the offsets for > 4 GB files
    RandomAccessInputStream stream = new RandomAccessInputStream(currentId);
    for (int i=0; i<ifds.size(); i++) {
      IFD ifd = ifds.get(i);
      long[] stripOffsets = ifd.getStripOffsets();

      boolean neededAdjustment = false;
      for (int j=0; j<stripOffsets.length; j++) {
        long prevOffset = i == 0 ? 0 : ifds.get(i - 1).getStripOffsets()[0];
        long prevByteCount =
          i == 0 ? 0 : ifds.get(i - 1).getStripByteCounts()[0];

        long currentOffset = (int) stripOffsets[j];

        while (currentOffset < prevOffset || currentOffset < prevOffset + prevByteCount) {
          long newOffset = currentOffset + 0x100000000L;
          if (newOffset < stream.length() && ((j > 0 &&
            (currentOffset < stripOffsets[j - 1])) ||
            (i > 0 && currentOffset < prevOffset + prevByteCount)))
          {
            stripOffsets[j] = newOffset;
            currentOffset = stripOffsets[j];
            neededAdjustment = true;
          }
        }
      }
      if (neededAdjustment) {
        ifd.putIFDValue(IFD.STRIP_OFFSETS, stripOffsets);
      }

      neededAdjustment = false;

      long[] stripByteCounts = ifd.getStripByteCounts();
      for (int j=0; j<stripByteCounts.length; j++) {
        long currentCount = (int) stripByteCounts[j];
        long newByteCount = currentCount + 0x100000000L;
        if (currentCount < 0 || neededAdjustment ||
          newByteCount + stripOffsets[j] < in.length())
        {
          if (newByteCount < ifd.getImageWidth() * ifd.getImageLength()) {
            stripByteCounts[j] = newByteCount;
            neededAdjustment = true;
          }
        }
      }

      if (neededAdjustment) {
        ifd.putIFDValue(IFD.STRIP_BYTE_COUNTS, stripByteCounts);
      }
    }
    stream.close();

    for (int i=1; i<ifds.size(); i++) {
      IFD ifd = ifds.get(i);

      if (ifd.getImageWidth() == ifds.get(0).getImageWidth() &&
        ifd.getImageLength() == ifds.get(0).getImageLength())
      {
        sizeZ++;
      } else if (sizeZ == 1)
      {
        boolean isPyramid;
        Object source_lens_value = ifd.getIFDValue(SOURCE_LENS);
        if (source_lens_value != null)
        {
          float source_lens = (Float) source_lens_value;
          // A value of -1 correspond to the macro image and a value of -2
          // correspond to the map image
          isPyramid = (source_lens != -1 && source_lens != -2);
        } else {
          // Assume the last IFD is the macro image
          isPyramid = i < ifds.size() - 1;
        }

        if (isPyramid) pyramidHeight++;
      }
    }

    // repopulate core metadata

    int seriesCount = pyramidHeight + (ifds.size() - pyramidHeight * sizeZ);

    for (int i=0; i<ifds.size(); i++) {
      IFD ifd = ifds.get(i);
      ifd.remove(THUMB_TAG_2);
      ifds.set(i, ifd);

      TiffIFDEntry markerTag = (TiffIFDEntry) ifd.get(MARKER_TAG);

      if (markerTag != null) {
        if (markerTag.getValueOffset() > in.length()) {
          // can't rely upon the MARKER_TAG to be detected correctly
          ifds.get(i).remove(MARKER_TAG);
        }
        else {
          Object value = tiffParser.getIFDValue(markerTag);
          ifds.get(i).putIFDValue(MARKER_TAG, value);
        }
      }

      tiffParser.fillInIFD(ifds.get(i));

      int[] bpp = ifds.get(i).getBitsPerSample();
      for (int q=0; q<bpp.length; q++) {
        if (bpp[q] < 8) {
          bpp[q] = 8;
        }
      }
      ifds.get(i).putIFDValue(IFD.BITS_PER_SAMPLE, bpp);
    }

    core.clear();
    for (int s=0; s<seriesCount; s++) {
      CoreMetadata ms = new CoreMetadata();
      if (s == 0) {
        ms.resolutionCount = pyramidHeight;
        core.add(ms);
      }
      else if (s < pyramidHeight) {
        core.add(0, ms);
      }
      else {
        core.add(ms);
      }
    }

    for (int s=0; s<core.size(); s++) {
      for (int r = 0; r < core.size(s); r++) {
        int index = core.flattenedIndex(s, r);
        IFD ifd = ifds.get(getIFDIndex(index, 0));
        PhotoInterp p = ifd.getPhotometricInterpretation();
        int samples = ifd.getSamplesPerPixel();
        CoreMetadata ms = core.get(s, r);
        ms.rgb = samples > 1 || p == PhotoInterp.RGB;

        ms.sizeX = (int) ifd.getImageWidth();
        ms.sizeY = (int) ifd.getImageLength();
        ms.sizeZ = index < pyramidHeight ? sizeZ : 1;
        ms.sizeT = 1;
        ms.sizeC = ms.rgb ? samples : 1;
        ms.littleEndian = ifd.isLittleEndian();
        ms.indexed = p == PhotoInterp.RGB_PALETTE &&
          (get8BitLookupTable() != null || get16BitLookupTable() != null);
        ms.imageCount = ms.sizeZ * ms.sizeT;
        ms.pixelType = ifd.getPixelType();
        ms.metadataComplete = true;
        ms.interleaved =
          ms.sizeX > MAX_SIZE && ms.sizeY > MAX_SIZE;
        ms.falseColor = false;
        ms.dimensionOrder = "XYCZT";
        ms.thumbnail = index != 0;
      }
    }

    String metadataTag = ifds.get(0).getIFDStringValue(METADATA_TAG);
    if (metadataTag != null) {
      String[] entries = metadataTag.split("\n");
      for (String entry : entries) {
        int eq = entry.indexOf('=');
        if (eq < 0) {
          continue;
        }
        String key = entry.substring(0, eq).trim();
        String value = entry.substring(eq + 1).trim();

        addGlobalMeta(key, value);

        if (key.equals("Objective.Lens.Magnificant")) { // not a typo
          magnification = new Double(value);
        }
        else if (key.equals("NDP.S/N")) {
          serialNumber = value;
        }
        else if (key.equals("Product")) {
          instrumentModel = value;
        }
      }
    }
  }

  /* @see loci.formats.BaseTiffReader#initMetadataStore() */
  @Override
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();

    MetadataStore store = makeFilterMetadata();

    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    String objectiveID = MetadataTools.createLSID("Objective", 0, 0);

    store.setInstrumentID(instrumentID, 0);
    store.setObjectiveID(objectiveID, 0, 0);

    if (instrumentModel != null) {
      store.setMicroscopeModel(instrumentModel, 0);
    }

    if (magnification != null) {
      store.setObjectiveNominalMagnification(magnification, 0, 0);
    }

    for (int i=0; i<getSeriesCount(); i++) {
      if (hasFlattenedResolutions() || i > 2) {
        store.setImageName("Series " + (i + 1), i);
      }
      else {
        switch (i) {
          case 0:
            store.setImageName("", i);
            break;
          case 1:
            store.setImageName("macro image", i);
            break;
          case 2:
            store.setImageName("macro mask image", i);
            break;
        }
      }

      store.setImageInstrumentRef(instrumentID, i);
      store.setObjectiveSettingsID(objectiveID, i);

      if (i > 0) {
        int ifdIndex = getIFDIndex(i, 0);
        String creationDate = ifds.get(ifdIndex).getIFDTextValue(IFD.DATE_TIME);
        creationDate = DateTools.formatDate(creationDate, DATE_FORMATS, ".");
        if (creationDate != null) {
          store.setImageAcquisitionDate(new Timestamp(creationDate), i);
        }

        double xResolution = ifds.get(ifdIndex).getXResolution();
        double yResolution = ifds.get(ifdIndex).getYResolution();

        Length sizeX = FormatTools.getPhysicalSizeX(xResolution);
        Length sizeY = FormatTools.getPhysicalSizeY(yResolution);

        if (sizeX != null) {
          store.setPixelsPhysicalSizeX(sizeX, i);
        }
        if (sizeY != null) {
          store.setPixelsPhysicalSizeY(sizeY, i);
        }
      }
      else {
        store.setImageDescription(serialNumber, i);
      }
    }
  }

  // -- Helper methods --

  private int getIFDIndex(int seriesIndex, int zIndex) {
    if (seriesIndex < pyramidHeight) {
      return zIndex * pyramidHeight + seriesIndex;
    }

    return sizeZ * pyramidHeight + (seriesIndex - pyramidHeight);
  }

}
