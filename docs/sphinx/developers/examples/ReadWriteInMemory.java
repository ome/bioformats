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
import loci.common.ByteArrayHandle;
import loci.common.Location;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.*;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

/**
 * Tests the Bio-Formats I/O logic to and from byte arrays in memory.
 */
public class ReadWriteInMemory {

  public static void main(String[] args)
    throws DependencyException, FormatException, IOException, ServiceException
  {
    if (args.length < 1) {
      System.out.println("Please specify a (small) image file.");
      System.exit(1);
    }
    String path = args[0];

    /* file-read-start */
    // read in entire file
    System.out.println("Reading file into memory from disk...");
    File inputFile = new File(path);
    int fileSize = (int) inputFile.length();
    DataInputStream in = new DataInputStream(new FileInputStream(inputFile));
    byte[] inBytes = new byte[fileSize];
    in.readFully(inBytes);
    System.out.println(fileSize + " bytes read.");
    /* file-read-end */

    /* mapping-start */
    // determine input file suffix
    String fileName = inputFile.getName();
    int dot = fileName.lastIndexOf(".");
    String suffix = dot < 0 ? "" : fileName.substring(dot);

    // map input id string to input byte array
    String inId = "inBytes" + suffix;
    Location.mapFile(inId, new ByteArrayHandle(inBytes));
    /* mapping-end */

    // read data from byte array using ImageReader
    System.out.println();
    System.out.println("Reading image data from memory...");

    /* read-start */
    ServiceFactory factory = new ServiceFactory();
    OMEXMLService service = factory.getInstance(OMEXMLService.class);
    IMetadata omeMeta = service.createOMEXMLMetadata();

    ImageReader reader = new ImageReader();
    reader.setMetadataStore(omeMeta);
    reader.setId(inId);
    /* read-end */

    int seriesCount = reader.getSeriesCount();
    int imageCount = reader.getImageCount();
    int sizeX = reader.getSizeX();
    int sizeY = reader.getSizeY();
    int sizeZ = reader.getSizeZ();
    int sizeC = reader.getSizeC();
    int sizeT = reader.getSizeT();

    // output some details
    System.out.println("Series count: " + seriesCount);
    System.out.println("First series:");
    System.out.println("\tImage count = " + imageCount);
    System.out.println("\tSizeX = " + sizeX);
    System.out.println("\tSizeY = " + sizeY);
    System.out.println("\tSizeZ = " + sizeZ);
    System.out.println("\tSizeC = " + sizeC);
    System.out.println("\tSizeT = " + sizeT);

    /* out—mapping-start */
    // map output id string to output byte array
    String outId = fileName + ".ome.tif";
    ByteArrayHandle outputFile = new ByteArrayHandle();
    Location.mapFile(outId, outputFile);
    /* out—mapping-end */

    /* write—init-start */
    // write data to byte array using ImageWriter
    System.out.println();
    System.out.print("Writing planes to destination in memory: ");
    ImageWriter writer = new ImageWriter();
    writer.setMetadataRetrieve(omeMeta);
    writer.setId(outId);
    /* write—init-end */

    /* write-start */
    byte[] plane = null;
    for (int i=0; i<imageCount; i++) {
      if (plane == null) {
        // allow reader to allocate a new byte array
        plane = reader.openBytes(i);
      }
      else {
        // reuse previously allocated byte array
        reader.openBytes(i, plane);
      }
      writer.saveBytes(i, plane);
      System.out.print(".");
    }
    reader.close();
    writer.close();
    System.out.println();

    byte[] outBytes = outputFile.getBytes();
    outputFile.close();
    /* write-end */

    /* flush-start */
    // flush output byte array to disk
    System.out.println();
    System.out.println("Flushing image data to disk...");
    File outFile = new File(fileName + ".ome.tif");
    DataOutputStream out = new DataOutputStream(new FileOutputStream(outFile));
    out.write(outBytes);
    out.close();
    /* flush-end */
  }

}
