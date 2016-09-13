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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import loci.common.DateTools;
import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.ImageTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;

import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Time;
import ome.units.quantity.Length;
import ome.units.UNITS;

/**
 * SimplePCITiffReader is the file format reader for TIFF files produced by
 * SimplePCI software.
 */
public class SimplePCITiffReader extends BaseTiffReader {

  // -- Constants --

  private static final String MAGIC_STRING = "Created by Hamamatsu Inc.";
  private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

  private static final int CUSTOM_BITS = 65531;

  // -- Fields --

  private MinimalTiffReader delegate;
  private String date;
  private Double magnification;
  private String immersion;
  private String cameraType;
  private String cameraName;
  private String binning;
  private ArrayList<Double> exposureTimes;
  private double scaling;

  // -- Constructor --

  public SimplePCITiffReader() {
    super("SimplePCI TIFF", new String[] {"tif", "tiff"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    String comment = tp.getComment();
    if (comment == null) return false;
    return comment.trim().startsWith(MAGIC_STRING);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    if (getSizeC() == 1) {
      return super.openBytes(no, buf, x, y, w, h);
    }

    byte[] b = delegate.openBytes(no / getSizeC(), x, y, w, h);
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int c = getZCTCoords(no)[1];
    ImageTools.splitChannels(
      b, buf, c, getSizeC(), bpp, false, isInterleaved(), w * h * bpp);
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (delegate != null) delegate.close(fileOnly);
    if (!fileOnly) {
      delegate = null;
      date = null;
      magnification = null;
      immersion = null;
      cameraType = null;
      cameraName = null;
      binning = null;
      exposureTimes = null;
      scaling = 0d;
    }
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  @Override
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    delegate = new MinimalTiffReader();
    delegate.setId(currentId);

    exposureTimes = new ArrayList<Double>();

    String data = ifds.get(0).getComment();

    // remove magic string
    data = data.substring(data.indexOf("\n") + 1);

    date = data.substring(0, data.indexOf("\n"));
    data = data.substring(data.indexOf("\n") + 1);
    data = data.replaceAll("ReadFromDoc", "");

    IniParser parser = new IniParser();
    parser.setCommentDelimiter(";");

    IniList ini = parser.parseINI(new BufferedReader(new StringReader(data)));

    IniTable microscopeTable = ini.getTable(" MICROSCOPE ");
    if (microscopeTable != null) {
      String objective = microscopeTable.get("Objective");
      int space = objective.indexOf(' ');
      if (space != -1) {
        magnification = new Double(objective.substring(0, space - 1));
        immersion = objective.substring(space + 1);
      }
    }

    CoreMetadata m = core.get(0);

    IniTable cameraTable = ini.getTable(" CAPTURE DEVICE ");
    binning = cameraTable.get("Binning") + "x" + cameraTable.get("Binning");
    cameraType = cameraTable.get("Camera Type");
    cameraName = cameraTable.get("Camera Name");
    String displayDepth = cameraTable.get("Display Depth");
    if (displayDepth != null) {
      m.bitsPerPixel = Integer.parseInt(displayDepth);
    } else {
      String bitDepth = cameraTable.get("Bit Depth");
      if (bitDepth != null && bitDepth.length() > "-bit".length()) {
        bitDepth = bitDepth.substring(0, bitDepth.length() - "-bit".length());
        m.bitsPerPixel = Integer.parseInt(bitDepth);
      } else {
        throw new FormatException("Could not find bits per pixels");
      }
    }

    IniTable captureTable = ini.getTable(" CAPTURE ");
    if (captureTable != null) {
      int index = 1;
      for (int i=0; i<getSizeC(); i++) {
        if (captureTable.get("c_Filter" + index) != null) {
          exposureTimes.add(new Double(captureTable.get("c_Expos" + index)));
        }
        index++;
      }
    }

    IniTable calibrationTable = ini.getTable(" CALIBRATION ");
    String units = calibrationTable.get("units");
    scaling = Double.parseDouble(calibrationTable.get("factor"));

    m.imageCount *= getSizeC();
    m.rgb = false;

    if (ifds.get(0).containsKey(CUSTOM_BITS)) {
      m.bitsPerPixel = ifds.get(0).getIFDIntValue(CUSTOM_BITS);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      HashMap<String, String> iniMap = ini.flattenIntoHashMap();
      metadata.putAll(iniMap);
    }
  }

  /* @see BaseTiffReader#initMetadataStore() */
  @Override
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    if (date != null) {
      date = DateTools.formatDate(date, DATE_FORMAT);
      if (date != null) {
        store.setImageAcquisitionDate(new Timestamp(date), 0);
      }
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setImageDescription(MAGIC_STRING, 0);

      Length sizeX = FormatTools.getPhysicalSizeX(scaling);
      Length sizeY = FormatTools.getPhysicalSizeY(scaling);
      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }

      String instrument = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrument, 0);
      store.setImageInstrumentRef(instrument, 0);

      store.setObjectiveID(MetadataTools.createLSID("Objective", 0, 0), 0, 0);
      store.setObjectiveNominalMagnification(magnification, 0, 0);
      store.setObjectiveImmersion(getImmersion(immersion), 0, 0);

      String detector = MetadataTools.createLSID("Detector", 0, 0);
      store.setDetectorID(detector, 0, 0);
      store.setDetectorModel(cameraType + " " + cameraName, 0, 0);
      store.setDetectorType(getDetectorType("CCD"), 0, 0);

      for (int i=0; i<getSizeC(); i++) {
        store.setDetectorSettingsID(detector, 0, i);
        store.setDetectorSettingsBinning(getBinning(binning), 0, i);
      }

      for (int i=0; i<getImageCount(); i++) {
        int[] zct = getZCTCoords(i);
        if (zct[1] < exposureTimes.size() && exposureTimes.get(zct[1]) != null) {
          store.setPlaneExposureTime(new Time(exposureTimes.get(zct[1]) / 1000000, UNITS.SECOND), 0, i);
        }
      }
    }
  }

}
