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

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.ServiceFactory;
import loci.formats.FilePattern;
import loci.formats.in.TiffReader;
import loci.formats.meta.IMetadata;
import loci.formats.out.TiffWriter;
import loci.formats.services.OMEXMLService;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;

/**
 * Stitches the first plane from a collection of TIFFs into a single file.
 */
public class SewTiffs {

  private static final int DOTS = 50;

  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      System.out.println(
        "Usage: java SewTiffs base_name channel_num [time_count]");
      System.exit(1);
    }
    String base = args[0];
    int c = Integer.parseInt(args[1]);
    int num;
    if (args.length < 3) {
      FilePattern fp = new FilePattern(
        new Location(base + "_C" + c + "_TP1.tiff"));
      int[] count = fp.getCount();
      num = count[count.length - 1];
    }
    else num = Integer.parseInt(args[2]);
    System.out.println("Fixing " + base + "_C" + c + "_TP<1-" + num + ">.tiff");
    TiffReader in = new TiffReader();
    TiffWriter out = new TiffWriter();
    String outId = base + "_C" + c + ".tiff";
    System.out.println("Writing " + outId);
    out.setId(outId);
    System.out.print("   ");
    boolean comment = false;

    for (int t=0; t<num; t++) {
      String inId = base + "_C" + c + "_TP" + (t + 1) + ".tiff";
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      IMetadata meta = service.createOMEXMLMetadata();
      in.setMetadataStore(meta);
      in.setId(inId);
      out.setMetadataRetrieve(meta);

      // read first image plane
      byte[] image = in.openBytes(0);
      in.close();

      if (t == 0) {
        // read first IFD
        RandomAccessInputStream ras = new RandomAccessInputStream(inId);
        TiffParser parser = new TiffParser(ras);
        IFD ifd = parser.getFirstIFD();
        ras.close();

        // preserve TIFF comment
        String desc = ifd.getComment();

        if (desc != null) {
          ifd = new IFD();
          ifd.putIFDValue(IFD.IMAGE_DESCRIPTION, desc);
          comment = true;
          out.saveBytes(t, image, ifd);
          System.out.print(".");
          continue;
        }
      }

      // write image plane
      out.saveBytes(t, image);

      // update status
      System.out.print(".");
      if (t % DOTS == DOTS - 1) {
        System.out.println(" " + (t + 1));
        System.out.print("   ");
      }
    }
    System.out.println();
    if (comment) System.out.println("OME-TIFF comment saved.");
    else System.out.println("No OME-TIFF comment found.");
  }

}
