/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2018 Open Microscopy Environment:
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import loci.formats.*;
import loci.formats.ome.OMEXMLMetadataImpl;

import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;

/**
 * Demonstrates the minimum amount of metadata
 * necessary to write out an image pyramid.
 * Resolutions are supplied via IFormatWriter.setResolutions(...),
 * not an IPyramidStore.
 */
public class AlternativeMinimumPyramidWriter {

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println("Please specify an output file name.");
      System.exit(1);
    }
    String id = args[0];

    // create a blank pyramid

    int w = 4096, h = 4096, c = 1;
    int resolutions = 6;
    int pixelType = FormatTools.UINT16;
    int bpp = FormatTools.getBytesPerPixel(pixelType);
    byte[] img = new byte[w * h * c * bpp];

    // fill with random data
    for (int i=0; i<img.length; i++) img[i] = (byte) (256 * Math.random());

    // create metadata object with minimum required metadata fields
    System.out.println("Populating metadata...");

    // for now, don't use the factory to ensure that we don't get an OMEPyramidStore
    OMEXMLMetadataImpl meta = new OMEXMLMetadataImpl();

    MetadataTools.populateMetadata(meta, 0, null, false, "XYZCT",
      FormatTools.getPixelTypeString(pixelType), w, h, 1, c, 1, c);

    List<Resolution> resolutionData = new ArrayList<Resolution>();
    for (int i=1; i<resolutions; i++) {
      resolutionData.add(new Resolution(i, w, h, 2));
    }

    // write image plane to disk
    System.out.println("Writing image to '" + id + "'...");
    IFormatWriter writer = new ImageWriter();
    writer.setMetadataRetrieve(meta);
    writer.setResolutions(resolutionData);
    writer.setId(id);
    writer.saveBytes(0, img);
    for (int i=1; i<resolutions; i++) {
      writer.setResolution(i);
      int x = resolutionData.get(i - 1).sizeX.getValue();
      int y = resolutionData.get(i - 1).sizeY.getValue();
      byte[] downsample = new byte[x * y * bpp * c];
      // don't use random data, so it's obvious that the correct resolution is read
      Arrays.fill(downsample, (byte) i);
      writer.saveBytes(0, downsample);
    }
    writer.close();

    System.out.println("Done.");
  }

}
