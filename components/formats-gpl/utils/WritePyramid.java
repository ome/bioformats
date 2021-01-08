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

import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.meta.DummyMetadata;
import loci.formats.meta.IMetadata;
import loci.formats.meta.IPyramidStore;
import loci.formats.out.PyramidOMETiffWriter;
import loci.formats.tiff.IFD;

import ome.xml.model.primitives.PositiveInteger;

/**
 * Reads a set of pyramid resolutions (one per file) and converts to a single
 * pyramid OME-TIFF.
 */
public class WritePyramid {

  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      System.out.println("Specify one input file per resolution in " +
        "descending order and one output file");
      return;
    }
    loci.common.DebugTools.enableLogging("INFO");

    // read each input file's metadata to build an IPyramidStore
    // that represents the full image pyramid
    IMetadata meta = MetadataTools.createOMEXMLMetadata();
    if (!(meta instanceof IPyramidStore)) {
      System.out.println("MetadataStore is not an IPyramidStore; " +
        "cannot write pyramid");
      return;
    }

    ImageReader reader = new ImageReader();
    reader.setMetadataStore(meta);
    reader.setId(args[0]);
    reader.close();
    reader.setMetadataStore(new DummyMetadata());
    for (int i=1; i<args.length-1; i++) {
      reader.setId(args[i]);
      int x = reader.getSizeX();
      int y = reader.getSizeY();
      reader.close();
      ((IPyramidStore) meta).setResolutionSizeX(new PositiveInteger(x), 0, i);
      ((IPyramidStore) meta).setResolutionSizeY(new PositiveInteger(y), 0, i);
    }

    // pass metadata to the writer so that a single file will be
    // written containing the whole image pyramid
    String output = args[args.length - 1];
    PyramidOMETiffWriter writer = new PyramidOMETiffWriter();
    writer.setWriteSequentially(true);
    writer.setMetadataRetrieve(meta);
    writer.setId(output);

    // save image tiles with dimensions 256x256
    // the largest resolution in a pyramid may be very large,
    // so working with whole planes at once doesn't make sense
    int tileSize = 256;
    for (int i=0; i<args.length-1; i++) {
      writer.setResolution(i);
      reader.setId(args[i]);
      writer.setInterleaved(reader.isInterleaved());

      for (int plane=0; plane<reader.getImageCount(); plane++) {
        IFD ifd = new IFD();
        ifd.put(IFD.TILE_WIDTH, tileSize);
        ifd.put(IFD.TILE_LENGTH, tileSize);
        for (int yy=0; yy<reader.getSizeY(); yy+=tileSize) {
          for (int xx=0; xx<reader.getSizeX(); xx+=tileSize) {
            int realWidth = (int) Math.min(tileSize, reader.getSizeX() - xx);
            int realHeight = (int) Math.min(tileSize, reader.getSizeY() - yy);
            byte[] tile = reader.openBytes(plane, xx, yy, realWidth, realHeight);
            writer.saveBytes(plane, tile, ifd, xx, yy, realWidth, realHeight);
          }
        }
      }
      reader.close();
    }
    writer.close();
  }

}
