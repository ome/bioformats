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
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;

import ome.units.quantity.Length;
import ome.units.UNITS;

import ome.xml.model.primitives.Timestamp;

/**
 * LEOReader is the file format reader for LEO EM files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class LEOReader extends BaseTiffReader {

  // -- Constants --

  public static final int LEO_TAG = 34118;
  private static final String[] DATE_FORMATS = new String[] {
      "HH:mm dd-MMM-yyyy", "HH:mm:ss dd MMM yyyy"};

  // -- Fields --

  private Length xSize;
  private String date;
  private String time;
  private Length workingDistance;

  // -- Constructor --

  /** Constructs a new LEO reader. */
  public LEOReader() {
    super("LEO", new String[] {"sxm", "tif", "tiff"});
    domains = new String[] {FormatTools.EM_DOMAIN};
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser parser = new TiffParser(stream);
    parser.setDoCaching(false);
    IFD ifd = parser.getFirstIFD();
    if (ifd == null) return false;
    return ifd.containsKey(LEO_TAG);
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  @Override
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    String tag = ifds.get(0).getIFDTextValue(LEO_TAG);
    String[] lines = tag.split("\n");
    date = "";
    String value;
    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      for (int line=36; line<lines.length; line++) {
        if (lines[line].equals("AP_IMAGE_PIXEL_SIZE")) {
          value = parseKeyValue(lines[++line], "\\s+=\\s+");
          xSize = FormatTools.parseLength(value, "mm");
        }
        else if (lines[line].equals("AP_WD")) {
          value = parseKeyValue(lines[++line], "\\s+=\\s+");
          workingDistance = FormatTools.parseLength(value, "mm");
        }
        else if (lines[line].equals("AP_ACTUALCURRENT")) {
          parseKeyValue(lines[++line], "\\s+=\\s+");
        }
        else if (lines[line].equals("AP_ACTUALKV")) {
          parseKeyValue(lines[++line], "\\s+=\\s+");
        }
        else if (lines[line].equals("AP_TIME")) {
          time = parseKeyValue(lines[++line], "\\s+:");
        }
        else if (lines[line].equals("AP_DATE")) {
          date = parseKeyValue(lines[++line], "\\s+:");
        }
      }

      if (xSize == null) {
        // Legacy physical size parsing if API_IMAGE_PIXEL_SIZE is not found
        xSize = FormatTools.getPhysicalSizeY(
          Double.parseDouble(lines[3]) * 1000000);
      }
      addGlobalMeta("Acquisition date", date);
    }
  }

  /* @see BaseTiffReader#initMetadataStore() */
  @Override
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();

    MetadataStore store = makeFilterMetadata();

    date = DateTools.formatDate(time + " " + date, DATE_FORMATS);
    if (date != null) {
      store.setImageAcquisitionDate(new Timestamp(date), 0);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      if (xSize != null) {
        store.setPixelsPhysicalSizeX(xSize, 0);
        store.setPixelsPhysicalSizeY(xSize, 0);
      }

      String instrument = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrument, 0);
      store.setImageInstrumentRef(instrument, 0);

      store.setObjectiveID(MetadataTools.createLSID("Objective", 0, 0), 0, 0);
      if (workingDistance != null) {
        store.setObjectiveWorkingDistance(workingDistance, 0, 0);
      }
      store.setObjectiveImmersion(MetadataTools.getImmersion("Other"), 0, 0);
      store.setObjectiveCorrection(MetadataTools.getCorrection("Other"), 0, 0);
    }
  }

  private String parseKeyValue(String string, String separator) {
    String[] key_value = string.split(separator);
    addGlobalMeta(key_value[0], key_value[1]);
    return key_value[1];
  }
}
