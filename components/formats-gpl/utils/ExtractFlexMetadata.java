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

import java.io.File;
import java.io.FileWriter;

import loci.common.RandomAccessInputStream;
import loci.formats.in.FlexReader;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;

/**
 * Convenience method to extract the metadata from
 * all the Flex files present in a directory.
 */
public class ExtractFlexMetadata {

  public static void main(String[] args) throws Exception {
    File dir;
    if (args.length != 1 || !(dir = new File(args[0])).canRead()) {
      System.out.println("Usage: java ExtractFlexMetadata dir");
      return;
    }
    for (File file:dir.listFiles()) {
      if (file.getName().endsWith(".flex")) {
        String id = file.getPath();
        int dot = id.lastIndexOf(".");
        String outId = (dot >= 0 ? id.substring(0, dot) : id) + ".xml";
        RandomAccessInputStream in = new RandomAccessInputStream(id);
        TiffParser parser = new TiffParser(in);
        IFD firstIFD = parser.getIFDs().get(0);
        String xml = firstIFD.getIFDTextValue(FlexReader.FLEX);
        in.close();
        FileWriter writer = new FileWriter(new File(outId));
        writer.write(xml);
        writer.close();
        System.out.println("Writing header of: " + id);
      }
    }
    System.out.println("Done");
  }
}
