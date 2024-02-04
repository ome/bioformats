/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2023 Open Microscopy Environment:
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

import loci.formats.codec.Codec;
import loci.formats.codec.CodecOptions;
import loci.formats.in.SVSReader;

/**
 * Example of working with precompressed tiles.
 * Input is a SVS file.
 */
public class PrecompressedExample {

  public static void main(String[] args) throws Exception {
    long compressedTime = 0;
    long uncompressedTime = 0;
    long tiles = 0;

    // initialize the provided SVS file, preserving the pyramid structure
    try (SVSReader reader = new SVSReader()) {
      reader.setFlattenedResolutions(false);
      reader.setId(args[0]);

      // iterate over every tile in every series, resolution, and plane
      for (int series=0; series<reader.getSeriesCount(); series++) {
        reader.setSeries(series);
        for (int resolution=0; resolution<reader.getResolutionCount(); resolution++) {
          reader.setResolution(resolution);

          int width = reader.getOptimalTileWidth();
          int height = reader.getOptimalTileHeight();

          for (int plane=0; plane<reader.getImageCount(); plane++) {
            Codec tileCodec = reader.getTileCodec(plane);

            for (int y=0; y<reader.getTileRows(plane); y++) {
              for (int x=0; x<reader.getTileColumns(plane); x++) {
                // check that we can open both the compressed and uncompressed tiles
                // directly from the reader
                long t0 = System.currentTimeMillis();
                byte[] tile = reader.openCompressedBytes(plane, x, y);
                long t1 = System.currentTimeMillis();
                byte[] rawTile = reader.openBytes(plane, x * width, y * height, (int) Math.min(width, reader.getSizeX() - x * width), (int) Math.min(height, reader.getSizeY() - y * height));
                long t2 = System.currentTimeMillis();

                compressedTime += (t1 - t0);
                uncompressedTime += (t2 - t1);
                tiles++;

                // now test that we can decompress the compressed tile
                // using metadata provided by the reader

                CodecOptions tileOptions = reader.getTileCodecOptions(plane, x, y);
                byte[] decompressedTile = tileCodec.decompress(tile, tileOptions);
                assert rawTile.length == decompressedTile.length;
              }
            }
          }
        }
      }
    }

    System.out.println("total compressed tile read time: " + compressedTime + " ms (" + ((double) compressedTime / tiles) + " ms/tile)");
    System.out.println("total uncompressed tile read time: " + uncompressedTime + " ms (" + ((double) uncompressedTime / tiles) + " ms/tile)");
  }

}
