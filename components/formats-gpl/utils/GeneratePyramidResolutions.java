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

import java.util.Arrays;

import loci.common.image.IImageScaler;
import loci.common.image.SimpleImageScaler;
import loci.common.services.ServiceFactory;
import loci.formats.*;
import loci.formats.ome.OMEPyramidStore;
import loci.formats.services.OMEXMLService;

import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;

/**
 * Demonstrates writing an image pyramid the source dataset
 * only contains the full resolution image.
 */
public class GeneratePyramidResolutions {

  public static void main(String[] args) throws Exception {
    if (args.length < 4) {
      System.out.println("GeneratePyramidResolutions input-file scale-factor resolution-count output-file");
      System.exit(1);
    }
    String in = args[0];
    String out = args[3];
    int scale = Integer.parseInt(args[1]);
    int resolutions = Integer.parseInt(args[2]);

    ImageReader reader = new ImageReader();
    ServiceFactory factory = new ServiceFactory();
    OMEXMLService service = factory.getInstance(OMEXMLService.class);
    OMEPyramidStore meta = (OMEPyramidStore) service.createOMEXMLMetadata();
    reader.setMetadataStore(meta);

    reader.setId(in);

    for (int i=1; i<resolutions; i++) {
      int divScale = (int) Math.pow(scale, i);
      meta.setResolutionSizeX(new PositiveInteger(reader.getSizeX() / divScale), 0, i);
      meta.setResolutionSizeY(new PositiveInteger(reader.getSizeY() / divScale), 0, i);
    }

    IImageScaler scaler = new SimpleImageScaler();
    byte[] img = reader.openBytes(0);

    // write image plane to disk
    System.out.println("Writing image to '" + out + "'...");
    IFormatWriter writer = new ImageWriter();
    writer.setMetadataRetrieve(meta);
    writer.setId(out);
    writer.saveBytes(0, img);
    int type = reader.getPixelType();
    for (int i=1; i<resolutions; i++) {
      writer.setResolution(i);
      int x = meta.getResolutionSizeX(0, i).getValue();
      int y = meta.getResolutionSizeY(0, i).getValue();
      byte[] downsample = scaler.downsample(img, reader.getSizeX(),
        reader.getSizeY(), Math.pow(scale, i),
        FormatTools.getBytesPerPixel(type), reader.isLittleEndian(),
        FormatTools.isFloatingPoint(type), reader.getRGBChannelCount(),
        reader.isInterleaved());
      writer.saveBytes(0, downsample);
    }
    writer.close();
    reader.close();

    System.out.println("Done.");
  }

}
