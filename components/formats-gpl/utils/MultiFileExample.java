/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

import java.io.IOException;
import loci.formats.FormatException;
import loci.formats.ImageReader;

/**
 * Simple example of how to open multiple files simultaneously.
 */
public class MultiFileExample {
  public static void main(String[] args) throws FormatException, IOException {
    if (args.length < 2) {
      System.out.println("You must specify two files.");
      System.exit(1);
    }
    ImageReader[] readers = new ImageReader[args.length];
    for (int i=0; i<readers.length; i++) {
      readers[i] = new ImageReader();
      readers[i].setId(args[i]);
    }

    // read plane #0 from file #0
    readers[0].openBytes(0);

    // read plane #0 from file #1
    readers[1].openBytes(0);

    // the other option is to use a single reader for all of the files
    // this will use a little less memory, but is substantially slower
    // unless you read all of the planes from one file before moving on
    // to the next file
    //
    // if you want one reader total, uncomment the following:

    /*
    ImageReader reader = new ImageReader();
    //read plane #0 from file #0
    reader.setId(args[0]);
    reader.openBytes(0);
    // read plane #0 from file #1
    reader.setId(args[1]);
    reader.openBytes(0);
    */

  }
}
