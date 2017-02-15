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

import loci.common.RandomAccessInputStream;
import loci.formats.tiff.TiffParser;
import loci.formats.tiff.TiffSaver;

/**
 * Performs "surgery" on a TIFF ImageDescription comment, particularly the
 * OME-XML comment found in OME-TIFF files. Note that this code must be
 * tailored to a specific need by editing the commented out code below to
 * make desired alterations to the comment.
 */
public class CommentSurgery {
  public static void main(String[] args) throws Exception {
    // the -test flag will print proposed changes to stdout
    // rather than actually changing the comment
    boolean test = args[0].equals("-test");

    for (int i=0; i<args.length; i++) {
      String id = args[i];
      if (!test) System.out.println(id + ": ");
      String xml = new TiffParser(id).getComment();
      if (xml == null) {
        System.out.println("ERROR: No OME-XML comment.");
        return;
      }
      int len = xml.length();
      // do something to the comment; e.g.:
      //xml = xml.replaceAll("LogicalChannel:OWS", "LogicalChannel:OWS347-");

      if (test) System.out.println(xml);
      else {
        System.out.println(len + " -> " + xml.length());
        TiffSaver saver = new TiffSaver(id);
        RandomAccessInputStream in = new RandomAccessInputStream(id);
        saver.overwriteComment(in, xml);
        in.close();
        saver.close();
      }
    }
  }
}
