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

import java.io.*;
import loci.formats.in.SDTReader;

/**
 * Reads SDT files and creates text files containing histogram data.
 */
public class ExtractSDTData {

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println("Usage: java ExtractSDTData file.sdt");
      System.exit(1);
    }
    String id = args[0];
    String tid = id.substring(0, id.indexOf("."));
    SDTReader r = new SDTReader();
    r.setId(id);
    int bins = r.getTimeBinCount();
    int chan = r.getChannelCount();
    int w = r.getSizeX();
    int h = r.getSizeY();
    System.out.println("Data is " + w + " x " + h +
      ", bins=" + bins + ", channels=" + chan);
    byte[][] data = new byte[chan][h * w * bins * 2];
    for (int c=0; c<chan; c++) {
      System.out.println("Reading channel #" + c + "...");
      r.openBytes(c, data[c]);
      int i = 0;
      for (int y=0; y<h; y++) {
        for (int x=0; x<w; x++) {
          String oid = tid + "-c" + c + "-row" + y + "-col" + x;
          System.out.println(oid);
          PrintWriter out = new PrintWriter(new FileWriter(oid));
          for (int b=0; b<bins; b++) {
            i += 2;
            int v0 = data[c][i];
            int v1 = data[c][i + 1];
            int v = (v0 << 8) & v1;
            out.println(b + " " + v);
          }
          out.close();
        }
      }
    }
  }

}
