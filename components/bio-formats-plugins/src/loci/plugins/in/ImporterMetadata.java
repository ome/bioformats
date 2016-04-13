/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2016 Open Microscopy Environment:
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

package loci.plugins.in;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;

import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.MetadataTools;

/**
 * Helper class for storing original metadata key/value pairs.
 */
public class ImporterMetadata extends HashMap<String, Object> {

  // -- Constructor --

  public ImporterMetadata(IFormatReader r, ImportProcess process,
    boolean usePrefix)
  {
    // merge global metadata
    putAll(r.getGlobalMetadata());

    // merge location path
    put("Location", process.getCurrentFile());

    final ImporterOptions options = process.getOptions();
    final int oldSeries = r.getSeries();
    final int seriesCount = r.getSeriesCount();
    final int digits = digits(seriesCount);
    for (int i=0; i<seriesCount; i++) {
      if (!options.isSeriesOn(i)) continue;
      r.setSeries(i);

      // build prefix from image name and/or series number
      String s = "";
      if (usePrefix) {
        s = process.getOMEMetadata().getImageName(i);
        if ((s == null || s.trim().length() == 0) && seriesCount > 1) {
          StringBuffer sb = new StringBuffer();
          sb.append("Series ");
          int zeroes = digits - digits(i + 1);
          for (int j=0; j<zeroes; j++) sb.append(0);
          sb.append(i + 1);
          sb.append(" ");
          s = sb.toString();
        }
        else s += " ";
      }

      // merge series metadata
      Hashtable<String, Object> seriesMeta = r.getSeriesMetadata();
      MetadataTools.merge(seriesMeta, this, s);

      // merge core values
      final String pad = " "; // puts core values first when alphabetizing
      put(pad + s + "SizeX", new Integer(r.getSizeX()));
      put(pad + s + "SizeY", new Integer(r.getSizeY()));
      put(pad + s + "SizeZ", new Integer(r.getSizeZ()));
      put(pad + s + "SizeT", new Integer(r.getSizeT()));
      put(pad + s + "SizeC", new Integer(r.getSizeC()));
      put(pad + s + "IsRGB", new Boolean(r.isRGB()));
      put(pad + s + "PixelType",
        FormatTools.getPixelTypeString(r.getPixelType()));
      put(pad + s + "LittleEndian", new Boolean(r.isLittleEndian()));
      put(pad + s + "DimensionOrder", r.getDimensionOrder());
      put(pad + s + "IsInterleaved", new Boolean(r.isInterleaved()));
      put(pad + s + "BitsPerPixel", new Integer(r.getBitsPerPixel()));

      String seriesName = process.getOMEMetadata().getImageName(i);
      put(pad + "Series " + i + " Name", seriesName);
    }
    r.setSeries(oldSeries);
  }

  /** Returns a string with each key/value pair on its own line. */
  public String getMetadataString(String separator) {
    ArrayList<String> keys = new ArrayList<String>(keySet());
    Collections.sort(keys);

    StringBuffer sb = new StringBuffer();
    for (String key : keys) {
      sb.append(key);
      sb.append(separator);
      sb.append(get(key));
      sb.append("\n");
    }
    return sb.toString();
  }

  // -- Object API methods --

  @Override
  public String toString() {
    return getMetadataString(" = ");
  }

  // -- Helper methods --

  /** Computes the given value's number of digits. */
  private static int digits(int value) {
    int digits = 0;
    while (value > 0) {
      value /= 10;
      digits++;
    }
    return digits;
  }

}
