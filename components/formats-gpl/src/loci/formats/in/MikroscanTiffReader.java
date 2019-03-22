/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loci.formats.in;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
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

class MikroscanTiffCoreMetadata extends CoreMetadata {
  int[] ifdIndex;
  String comment;
  Length pixelSize;

  MikroscanTiffCoreMetadata() {
    super();
  }
}

/**
 * MikroscanTiffReader is the file format reader for Mikroscan TIFF files.
 */
public class MikroscanTiffReader extends BaseTiffReader {
  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(MikroscanTiffReader.class);

  /** TIFF image description prefix for Mikroscan TIF files. */
  private static final String MIKROSCAN_IMAGE_DESCRIPTION_PREFIX = "Mikroscan Image";

  private static final String DATE_FORMAT = "MM/dd/yy HH:mm:ss";

  // -- Fields --
  private String[] comments;
  private Double magnification;
  private String date, time;

  // -- Constructor --

  /** Constructs a new TIF reader. */
  public MikroscanTiffReader() {
    super("Mikroscan TIFF", new String[] {"tif", "tiff"});
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN, FormatTools.LM_DOMAIN};
    suffixNecessary = false;
    noSubresolutions = true;
    canSeparateSeries = false;
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
        Object description = ifd.get(IFD.IMAGE_DESCRIPTION);
        if (description != null) {
          String imageDescription = null;

          if (description instanceof TiffIFDEntry) {
            Object value = tiffParser.getIFDValue((TiffIFDEntry) description);
            if (value != null) {
              imageDescription = value.toString();
            }
          }
          else if (description instanceof String) {
            imageDescription = (String) description;
          }
          if (imageDescription != null
              && imageDescription.startsWith(MIKROSCAN_IMAGE_DESCRIPTION_PREFIX)) {
            return true;
          }
        }
        return false;
      }
      catch (IOException e) {
        LOGGER.debug("I/O exception during isThisType() evaluation.", e);
        return false;
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
    if (core.size() == 1) {
      return super.openBytes(no, buf, x, y, w, h);
    }
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    if (tiffParser == null) {
      initTiffParser();
    }
    int ifd = ((MikroscanTiffCoreMetadata) getCurrentCore()).ifdIndex[no];
    tiffParser.getSamples(ifds.get(ifd), buf, x, y, w, h);
    return buf;
  }

  /* @see loci.formats.IFormatReader#openThumbBytes(int) */
  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    if (core.size() == 1 || getSeries() >= getSeriesCount() - 2) {
      return super.openThumbBytes(no);
    }

    int smallestSeries = getSeriesCount() - 3;
    if (smallestSeries >= 0) {
      int thisSeries = getSeries();
      int resolution = getResolution();
      setSeries(smallestSeries);
      if (!hasFlattenedResolutions()) {
        setResolution(1);
      }
      byte[] thumb = FormatTools.openThumbBytes(this, no);
      setSeries(thisSeries);
      setResolution(resolution);
      return thumb;
    }
    return super.openThumbBytes(no);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      comments = null;
      magnification = null;
      date = null;
      time = null;
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    try {
      int ifd = ((MikroscanTiffCoreMetadata) getCurrentCore()).ifdIndex[0];
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
      int ifd = ((MikroscanTiffCoreMetadata) getCurrentCore()).ifdIndex[0];
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
      core.add(new MikroscanTiffCoreMetadata());
    }

    comments = new String[seriesCount];

    HashSet<Double> uniqueZ = new HashSet<Double>();

    for (int i=0; i<seriesCount; i++) {
      setSeries(i);
      int index = getIFDIndex(i, 0);
      tiffParser.fillInIFD(ifds.get(index));

      String comment = ifds.get(index).getComment();
      if (comment == null) {
        continue;
      }
      String[] lines = comment.split("\n");
      String[] tokens;
      String key, value;
      for (String line : lines) {
        tokens = line.split("[|]");
        for (String t : tokens) {
          if (t.indexOf('=') >= 0) {
            key = t.substring(0, t.indexOf('=')).trim();
            value = t.substring(t.indexOf('=') + 1).trim();
          }
        }
      }
    }
    setSeries(0);

    // repopulate core metadata

    // remove any invalid pyramid resolutions
    IFD firstIFD = ifds.get(getIFDIndex(0, 0));
    for (int s=1; s<getSeriesCount() - 2; s++) {
      int index = getIFDIndex(s, 0);
      IFD ifd = ifds.get(index);
      tiffParser.fillInIFD(ifd);
      if (ifd.getPixelType() != firstIFD.getPixelType()) {
        ifds.set(index, null);
      }
    }
    for (int s=0; s<ifds.size(); ) {
      if (ifds.get(s) != null) {
        s++;
      }
      else {
        ifds.remove(s);
      }
    }
    if (uniqueZ.size() == 0) {
      uniqueZ.add(0d);
    }
    seriesCount = ((ifds.size() - 2) / uniqueZ.size()) + 2;

    core.clear();
    if (seriesCount > 2) {
      core.add();
      for (int r=0; r < seriesCount - 2; r++) {
        core.add(0, new MikroscanTiffCoreMetadata());
      }
      core.add(new MikroscanTiffCoreMetadata());
      core.add(new MikroscanTiffCoreMetadata());
    }
    else {
      // Should never happen unless the TIF is corrupt?
      for (int s=0; s<seriesCount; s++) {
        core.add(new MikroscanTiffCoreMetadata());
      }
    }

    for (int s=0; s<seriesCount; s++) {
      int[] pos = core.flattenedIndexes(s);
      setCoreIndex(s);

      MikroscanTiffCoreMetadata ms = (MikroscanTiffCoreMetadata) core.get(pos[0], pos[1]);

      if (s == 0 && seriesCount > 2) {
        ms.resolutionCount = seriesCount - 2;
      }

      IFD ifd = ifds.get(getIFDIndex(s, 0));
      tiffParser.fillInIFD(ifd);
      PhotoInterp p = ifd.getPhotometricInterpretation();
      int samples = ifd.getSamplesPerPixel();
      ms.rgb = samples > 1 || p == PhotoInterp.RGB;

      ms.sizeX = (int) ifd.getImageWidth();
      ms.sizeY = (int) ifd.getImageLength();
      if (s < seriesCount - 2) {
        ms.sizeZ = uniqueZ.size();
      }
      else {
        ms.sizeZ = 1;
      }
      ms.ifdIndex = new int[ms.sizeZ];
      for (int z=0; z<ms.sizeZ; z++) {
        ms.ifdIndex[z] = getIFDIndex(s, z);
      }
      ms.sizeT = 1;
      ms.sizeC = ms.rgb ? samples : 1;
      ms.littleEndian = ifd.isLittleEndian();
      ms.indexed = p == PhotoInterp.RGB_PALETTE &&
        (get8BitLookupTable() != null || get16BitLookupTable() != null);
      ms.imageCount = ms.sizeZ * ms.sizeT;
      ms.pixelType = ifd.getPixelType();
      ms.metadataComplete = true;
      ms.interleaved = false;
      ms.falseColor = false;
      ms.dimensionOrder = "XYCZT";
      ms.thumbnail = s != 0;

      if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
        String comment = ifds.get(ms.ifdIndex[0]).getComment();
        if (comment == null) {
          continue;
        }
        String[] lines = comment.split("\n");
        String[] tokens;
        String key, value;
        for (String line : lines) {
          tokens = line.split("[|]");
          for (String t : tokens) {
            if (t.indexOf('=') == -1) {
              addGlobalMeta("Comment", t);
              ((MikroscanTiffCoreMetadata) getCurrentCore()).comment = t;
            }
            else {
              key = t.substring(0, t.indexOf('=')).trim();
              value = t.substring(t.indexOf('=') + 1).trim();
              addSeriesMeta(key, value);
              switch (key) {
                case "MPP":
                  ((MikroscanTiffCoreMetadata) getCurrentCore()).pixelSize =
                    FormatTools.getPhysicalSizeX(DataTools.parseDouble(value));
                  break;
                case "Date":
                  date = value;
                  break;
                case "Time":
                  time = value;
                  break;
                case "AppMag":
                  magnification = DataTools.parseDouble(value);
                  break;
              }
            }
          }
        }
      }
    }
    setSeries(0);

    core.reorder();
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

    int lastImage = core.size() - 1;
    for (int i=0; i<getSeriesCount(); i++) {
      setSeries(i);

      store.setImageInstrumentRef(instrument, i);
      store.setObjectiveSettingsID(objective, i);

      if (hasFlattenedResolutions() || i > 2) {
        store.setImageName("Series " + (i + 1), i);
      }
      else {
        switch (i) {
          case 0:
            store.setImageName("", i);
            break;
          case 1:
            // if there are only two images, assume that there is no label
            if (lastImage == 1) {
              store.setImageName("macro image", i);
            }
            else {
              store.setImageName("label image", i);
            }
            break;
          case 2:
            store.setImageName("macro image", i);
            break;
        }
      }
      String comment = ((MikroscanTiffCoreMetadata) getCurrentCore()).comment;
      store.setImageDescription(comment, i);

      if (getDatestamp() != null) {
        store.setImageAcquisitionDate(getDatestamp(), i);
      }

      Length pixelSize = ((MikroscanTiffCoreMetadata) getCurrentCore()).pixelSize;
      if (pixelSize != null && pixelSize.value(UNITS.MICROMETER).doubleValue() - Constants.EPSILON > 0) {
        store.setPixelsPhysicalSizeX(pixelSize, i);
        store.setPixelsPhysicalSizeY(pixelSize, i);
      }
    }
    setSeries(0);
  }

  private int getIFDIndex(int coreIndex, int no) {
    int index = coreIndex;
    int coreCount = core.flattenedSize() - 2;
    if (coreIndex > 0 && coreIndex < coreCount) {
      if (core.get(0, 0).imageCount > 1) {
        index++;
      }
      else {
        index = coreCount - coreIndex;
      }
    }
    if ((coreIndex > 0 && coreIndex < coreCount) || no > 0) {
      for (int i=0; i<no; i++) {
        index += coreCount;
      }
      if (coreIndex == 0) {
        index++;
      }
    }
    else if (coreIndex >= coreCount && core.get(0, 0).imageCount > 1) {
      for (int i=0; i<coreCount; i++) {
        index += core.get(0, i).imageCount;
      }
      index -= (coreCount - 1);
    }
    return index;
  }

  protected Timestamp getDatestamp() {
    if (date != null && time != null) {
      try {
        return new Timestamp(
          DateTools.formatDate(date + " " + time, DATE_FORMAT));
      }
      catch (Exception e) {
        LOGGER.debug("Failed to parse '" + date + " " + time + "'", e);
      }
    }
    return null;
  }

  protected Length[] getPhysicalSizes() {
    Length psizes[] = new Length[getSeriesCount()];
    for(int i = 0; i < getSeriesCount(); i++) {
      int[] pos = core.flattenedIndexes(i);
      MikroscanTiffCoreMetadata c = (MikroscanTiffCoreMetadata) core.get(pos[0], pos[1]);
      psizes[i] = c.pixelSize;
    }
    return psizes;
  }

  protected double getMagnification() {
    return magnification;
  } 
  
}
