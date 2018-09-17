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
import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Length;
import ome.units.UNITS;

class SVSCoreMetadata extends CoreMetadata {
  int ifdIndex;
  String comment;
  Length pixelSize;

  SVSCoreMetadata() {
    super();
  }
}

/**
 * SVSReader is the file format reader for Aperio SVS TIFF files.
 */
public class SVSReader extends BaseTiffReader {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(SVSReader.class);

  /** TIFF image description prefix for Aperio SVS files. */
  private static final String APERIO_IMAGE_DESCRIPTION_PREFIX = "Aperio Image";

  private static final String DATE_FORMAT = "MM/dd/yy HH:mm:ss";

  // -- Fields --

  private Double emissionWavelength, excitationWavelength;
  private Double exposureTime, exposureScale;
  private Double magnification;
  private String date, time;

  // -- Constructor --

  /** Constructs a new SVS reader. */
  public SVSReader() {
    super("Aperio SVS", new String[] {"svs"});
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN};
    suffixNecessary = true;
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
              && imageDescription.startsWith(APERIO_IMAGE_DESCRIPTION_PREFIX)) {
            return true;
          }
        }
        return false;
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
    if (core.size() == 1) {
      return super.openBytes(no, buf, x, y, w, h);
    }
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    if (tiffParser == null) {
      initTiffParser();
    }
    int ifd = ((SVSCoreMetadata) currentCore()).ifdIndex;
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
      emissionWavelength = null;
      excitationWavelength = null;
      exposureTime = null;
      exposureScale = null;
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
      int ifd = ((SVSCoreMetadata) currentCore()).ifdIndex;
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
      int ifd = ((SVSCoreMetadata) currentCore()).ifdIndex;
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
    if (seriesCount > 2) {
      core.add();
      for (int r=0; r < seriesCount - 2; r++) {
        core.add(0, new SVSCoreMetadata());
      }
      core.add(new SVSCoreMetadata());
      core.add(new SVSCoreMetadata());
    }
    else {
      // Should never happen unless the SVS is corrupt?
      for (int s=0; s<seriesCount; s++) {
        core.add(new SVSCoreMetadata());
      }
    }
    for (int s=0; s<seriesCount; s++) {
      int[] pos = core.flattenedIndexes(s);
      setCoreIndex(s);

      SVSCoreMetadata ms = (SVSCoreMetadata) core.get(pos[0], pos[1]);

      ms.ifdIndex = getIFDIndex(s);

      if (s == 0 && seriesCount > 2) {
        ms.resolutionCount = seriesCount - 2;
      }

      IFD ifd = ifds.get(ms.ifdIndex);
      tiffParser.fillInIFD(ifds.get(ms.ifdIndex));

      PhotoInterp p = ifd.getPhotometricInterpretation();
      int samples = ifd.getSamplesPerPixel();
      ms.rgb = samples > 1 || p == PhotoInterp.RGB;

      ms.sizeX = (int) ifd.getImageWidth();
      ms.sizeY = (int) ifd.getImageLength();
      ms.sizeZ = 1;
      ms.sizeT = 1;
      ms.sizeC = ms.rgb ? samples : 1;
      ms.littleEndian = ifd.isLittleEndian();
      ms.indexed = p == PhotoInterp.RGB_PALETTE &&
        (get8BitLookupTable() != null || get16BitLookupTable() != null);
      ms.imageCount = 1;
      ms.pixelType = ifd.getPixelType();
      ms.metadataComplete = true;
      ms.interleaved = false;
      ms.falseColor = false;
      ms.dimensionOrder = "XYCZT";
      ms.thumbnail = s != 0;

      if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
        String comment = ifds.get(ms.ifdIndex).getComment();
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
              ((SVSCoreMetadata) currentCore()).comment = t;
            }
            else {
              key = t.substring(0, t.indexOf('=')).trim();
              value = t.substring(t.indexOf('=') + 1).trim();
              addSeriesMeta(key, value);
              switch (key) {
                case "MPP":
                  ((SVSCoreMetadata) currentCore()).pixelSize =
                    FormatTools.getPhysicalSizeX(DataTools.parseDouble(value));
                  break;
                case "Date":
                  date = value;
                  break;
                case "Time":
                  time = value;
                  break;
                case "Emission Wavelength":
                  emissionWavelength = DataTools.parseDouble(value);
                  break;
                case "Excitation Wavelength":
                  excitationWavelength = DataTools.parseDouble(value);
                  break;
                case "Exposure Time":
                  exposureTime = DataTools.parseDouble(value);
                  break;
                case "Exposure Scale":
                  exposureScale = DataTools.parseDouble(value);
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

    String instrument = MetadataTools.createLSID("Instrument", 0);
    String objective = MetadataTools.createLSID("Objective", 0, 0);
    store.setInstrumentID(instrument, 0);
    store.setObjectiveID(objective, 0, 0);
    store.setObjectiveNominalMagnification(magnification, 0, 0);

    for (int i=0; i<getSeriesCount(); i++) {
      setSeries(i);

      store.setImageInstrumentRef(instrument, i);
      store.setObjectiveSettingsID(objective, i);

      store.setImageName("Series " + (i + 1), i);
      String comment = ((SVSCoreMetadata) currentCore()).comment;
      store.setImageDescription(comment, i);

      if (getDatestamp() != null) {
        store.setImageAcquisitionDate(getDatestamp(), i);
      }

      for (int c=0; c<getEffectiveSizeC(); c++) {
        if (getEmission() != null) {
          store.setChannelEmissionWavelength(getEmission(), i, c);
        }

        if (getExcitation() != null) {
          store.setChannelExcitationWavelength(getExcitation(), i, c);
        }
      }

      Length pixelSize = ((SVSCoreMetadata) currentCore()).pixelSize;
      if (pixelSize != null && pixelSize.value(UNITS.MICROMETER).doubleValue() - Constants.EPSILON > 0) {
        store.setPixelsPhysicalSizeX(pixelSize, i);
        store.setPixelsPhysicalSizeY(pixelSize, i);
      }
    }
  }

  private int getIFDIndex(int coreIndex) {
    int index = coreIndex;
    if (coreIndex > 0 && coreIndex < core.size() - 2) {
      index = core.size() - 2 - coreIndex;
    }
    return index;
  }

  protected Length getEmission() {
    if (emissionWavelength != null && emissionWavelength > 0) {
      return FormatTools.getEmissionWavelength(emissionWavelength);
    }
    return null;
  }

  protected Length getExcitation() {
    if (excitationWavelength != null && excitationWavelength > 0) {
      return FormatTools.getExcitationWavelength(excitationWavelength);
    }
    return null;
  }

  protected Double getExposureTime() {
    return exposureTime;
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
      SVSCoreMetadata c = (SVSCoreMetadata) core.get(pos[0], pos[1]);
      psizes[i] = c.pixelSize;
    }
    return psizes;
  }

  protected double getMagnification() {
    return magnification;
  }

}
