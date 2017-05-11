/*
 * #%L
 * Bio-Formats autogen package for programmatically generating source code.
 * %%
 * Copyright (C) 2017 Open Microscopy Environment:
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import loci.common.DataTools;
import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;

public class MetadataRatings {

  // -- Constants --

  private static final String METADATA = "meta-support.txt";
  private static final String FORMATS = "format-pages.txt";
  private static final String[] RATINGS =
   {"poor", "fair", "good", "very good", "outstanding"};

  // -- Fields --

  private IniList metadata;
  private IniList formats;
  private String outputFile;

  // -- Constructors --

  /** Constructs an entity list. */
  public MetadataRatings(String outputFile) throws IOException {
    this.outputFile = outputFile;
    IniParser parser = new IniParser();
    metadata = parser.parseINI(METADATA, MetadataRatings.class);
    formats = parser.parseINI(FORMATS, MetadataRatings.class);
  }

  // -- API methods --

  public void updateRatings() throws IOException {
    for (IniTable table : formats) {
      String readerName = table.get("reader");
      String opennessRating = table.get("opennessRating");
      String pixelsRating = table.get("pixelsRating");
      String pyramid = table.get("pyramid");

      int opennessIndex = DataTools.indexOf(RATINGS, opennessRating.toLowerCase());
      int pixelsIndex = DataTools.indexOf(RATINGS, pixelsRating.toLowerCase());
      int metadataIndex = (int) Math.min(opennessIndex, pixelsIndex);

      for (IniTable metadataTable : metadata) {
        if (metadataTable.get(IniTable.HEADER_KEY).equals(readerName)) {
          String instrument = metadataTable.get("Instrument.ID");
          String instrumentRef = metadataTable.get("Image.InstrumentRef");
          String emission = metadataTable.get("Channel.EmissionWavelength");
          String excitation = metadataTable.get("Channel.ExcitationWavelength");

          if ((instrument != null && instrument.equalsIgnoreCase("yes") &&
            instrumentRef != null && instrumentRef.equalsIgnoreCase("yes")) ||
            (emission != null && emission.equalsIgnoreCase("yes")) ||
            (excitation != null && excitation.equalsIgnoreCase("yes")))
          {
            metadataIndex++;
          }

          if (pyramid == null || !pyramid.equalsIgnoreCase("yes")) {
            // look for SPW metadata
            for (String key : metadataTable.keySet()) {
              if (key.startsWith("Plate.") || key.startsWith("Screen.") ||
                key.startsWith("Well."))
              {
                String value = metadataTable.get(key);
                if (value != null && value.equalsIgnoreCase("yes")) {
                  metadataIndex++;
                }
              }
            }
          }
          else {
            metadataIndex++;
          }
        }
      }

      if (metadataIndex >= RATINGS.length) {
        metadataIndex = RATINGS.length - 1;
      }

      String originalRating = table.get("metadataRating").toLowerCase();
      if (!RATINGS[metadataIndex].equals(originalRating)) {
        table.put("metadataRating", RATINGS[metadataIndex]);
      }
    }
    // replace metadata ratings as needed, without altering the key/value pair order
    BufferedReader reader = IniParser.openTextResource(FORMATS, MetadataRatings.class);
    ArrayList<String> lines = new ArrayList<String>();
    int ratingIndex = -1;
    while (true) {
      String line = reader.readLine();
      if (line == null) {
        break;
      }
      lines.add(line);
      line = line.trim();
      if (line.startsWith("reader =")) {
        String name = line.substring(line.indexOf("=") + 1).trim();
        String rating = null;
        for (IniTable table : formats) {
          if (name.equals(table.get("reader"))) {
            rating = table.get("metadataRating");
            break;
          }
        }
        if (ratingIndex >= 0) {
          lines.set(ratingIndex, "metadataRating = " + rating);
        }
      }
      else if (line.startsWith("metadataRating =")) {
        ratingIndex = lines.size() - 1;
      }
    }
    reader.close();
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
    for (String line : lines) {
      out.write(line);
      out.write("\n");
    }
    out.close();
  }

  public static void main(String[] args) throws IOException {
    MetadataRatings ratings = new MetadataRatings(args[0]);
    ratings.updateRatings();
  }


}
