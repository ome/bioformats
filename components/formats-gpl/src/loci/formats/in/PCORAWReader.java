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

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;

import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * PCORAWReader is the file format reader for PCORAW files.
 */
public class PCORAWReader extends FormatReader {

  // -- Fields --

  private TiffReader reader = new TiffReader();
  private String imageFile;
  private String paramFile;

  // -- Constructor --

  /** Constructs a new PCORAW reader. */
  public PCORAWReader() {
    super("PCO-RAW", new String[] {"pcoraw", "rec"});
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
    hasCompanionFiles = true;
    suffixSufficient = false;
    datasetDescription = "A single .pcoraw file with a " +
      "similarly-named .rec file";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String) */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, "rec") && open) {
      String base = new Location(name).getAbsoluteFile().getAbsolutePath();
      base = base.substring(0, base.lastIndexOf("."));
      String id = base + ".pcoraw";
      return new Location(id).exists();
    }
    if (checkSuffix(name, "pcoraw") && open) {
      return reader.isThisType(name, open);
    }
    return super.isThisType(name, open);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return reader.isThisType(stream);
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    if (noPixels) {
      return paramFile == null ? null : new String[] {paramFile};
    }
    return paramFile == null ? new String[] {imageFile} :
      new String[] {imageFile, paramFile};
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    return reader.openBytes(no, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    reader.close(fileOnly);
    if (!fileOnly) {
      imageFile = null;
      paramFile = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    if (checkSuffix(id, "rec")) {
      paramFile = new Location(id).getAbsolutePath();

      String base = new Location(id).getAbsoluteFile().getAbsolutePath();
      base = base.substring(0, base.lastIndexOf("."));
      id = base + ".pcoraw";
      if (!new Location(id).exists()) {
        throw new FormatException("Could not find image file.");
      }
    }

    super.initFile(id);

    imageFile = new Location(id).getAbsolutePath();

    reader.close();
    reader.setMetadataStore(getMetadataStore());
    reader.setId(id);

    core = reader.getCoreMetadataList();
    metadata = reader.getGlobalMetadata();

    in = new RandomAccessInputStream(id);
    try {
      if (in.length() >= Math.pow(2, 32)) {
        // even though BigTIFF is likely being used, the offsets
        // are still recorded as though only 32 bits are available

        long add = 0;
        long prevOffset = 0;
        for (IFD ifd : reader.ifds) {
          long[] offsets = ifd.getStripOffsets();
          for (int i=0; i<offsets.length; i++) {
            offsets[i] += add;

            if (offsets[i] < prevOffset) {
              add += 0x100000000L;
              offsets[i] += 0x100000000L;
            }
            prevOffset = offsets[i];
          }
          ifd.put(IFD.STRIP_OFFSETS, offsets);
        }
      }
    }
    finally {
      in.close();
    }

    if (paramFile == null) {
      String base = imageFile.substring(0, imageFile.lastIndexOf("."));
      base += ".rec";
      if (new Location(base).exists()) {
        paramFile = base;
      }
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    if (paramFile != null) {
      // parse extra metadata from the parameter file

      store.setInstrumentID(MetadataTools.createLSID("Instrument", 0), 0);
      String detector = MetadataTools.createLSID("Detector", 0, 0);
      store.setDetectorID(detector, 0, 0);

      String[] lines = DataTools.readFile(paramFile).split("\n");
      for (int i=0; i<lines.length; i++) {
        String line = lines[i];
        int sep = line.indexOf(":");
        if (sep < 0) {
          continue;
        }

        String key = line.substring(0, sep).trim();
        String value = line.substring(sep + 1).trim();

        addGlobalMeta(key, value);

        if (key.equals("Exposure / Delay")) {
          // set the exposure time

          String exp = value.substring(0, value.indexOf(" "));
          Double parsedExp = new Double(exp);
          Time exposure = null;
          if (parsedExp != null) {
            exposure = new Time(parsedExp / 1000, UNITS.SECOND);
          }

          for (int plane=0; plane<getImageCount(); plane++) {
            store.setPlaneExposureTime(exposure, 0, plane);
          }
        }
        else if (key.equals("Camera serial number")) {
          // set the serial number

          store.setDetectorSerialNumber(value, 0, 0);
        }
        else if (key.equals("Binning horz./vert.")) {
          store.setDetectorSettingsID(detector, 0, 0);

          value = value.charAt(1) + value;
          value = value.substring(0, 3);

          store.setDetectorSettingsBinning(getBinning(value), 0, 0);
        }
        else if (key.equals("Comment")) {
          StringBuffer description = new StringBuffer();
          for (int j=i + 1; j<lines.length; j++) {
            lines[j] = lines[j].trim();
            if (lines[j].length() > 0) {
              description.append(lines[j]);
              description.append(" ");
            }
          }
          store.setImageDescription(description.toString().trim(), 0);
          break;
        }
      }
    }

  }

}
