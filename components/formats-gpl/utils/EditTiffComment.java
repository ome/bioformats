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

import java.io.*;
import java.util.Hashtable;
import loci.common.RandomAccessInputStream;
import loci.formats.tiff.*;

/**
 * Allows raw user TIFF comment editing for the given TIFF files.
 */
public class EditTiffComment {

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println("Usage: java EditTiffComment file1 file2 ...");
      return;
    }
    BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
    for (int i=0; i<args.length; i++) {
      String f = args[i];
      // read comment
      System.out.println("Reading " + f + " ");
      String comment = new TiffParser(f).getComment();
      // or if you already have the file open for random access, you can use:
      // RandomAccessInputStream fin = new RandomAccessInputStream(f);
      // TiffParser tiffParser = new TiffParser(fin);
      // String comment = tiffParser.getComment();
      // fin.close();
      System.out.println("[done]");
      // display comment, and prompt for changes
      System.out.println("Comment =");
      System.out.println(comment);
      System.out.println("Enter new comment (no line breaks):");
      String xml = cin.readLine();
      System.out.print("Saving " + f);
      // save results back to the TIFF file
      TiffSaver saver = new TiffSaver(f);
      RandomAccessInputStream in = new RandomAccessInputStream(f);
      saver.overwriteComment(in, xml);
      in.close();
      saver.close();
      System.out.println(" [done]");
    }
  }

}
