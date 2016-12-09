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

import java.io.IOException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.ImageReader;
import loci.formats.meta.IMetadata;
import loci.formats.out.OMETiffWriter;
import loci.formats.services.OMEXMLService;

/**
 * Example class for reading a full image and use an OME Tiff writer to 
 * automatically write out the image in a tiled format.
 *
 * @author David Gault dgault at dundee.ac.uk
 */
public class SimpleTiledWriter {

  /** The file format reader. */
  private ImageReader reader;

  /** The file format writer. */
  private OMETiffWriter writer;

  /** The file to be read. */
  private String inputFile;

  /** The file to be written. */
  private String outputFile;

  /** The tile width to be used. */
  private int tileSizeX;

  /** The tile height to be used. */
  private int tileSizeY;

  /**
   * Construct a new SimpleTiledWriter to read the specified input file 
   * and write the given output file using the tile sizes provided.
   *
   * @param inputFile the file to be read
   * @param outputFile the file to be written
   * @param tileSizeX the width of tile to attempt to use
   * @param tileSizeY the height of tile to attempt to use
   */
  public SimpleTiledWriter(String inputFile, String outputFile, int tileSizeX, int tileSizeY) {
    this.inputFile = inputFile;
    this.outputFile = outputFile;
    this.tileSizeX = tileSizeX;
    this.tileSizeY = tileSizeY;
  }

  /**
   * Set up the file reader and writer, ensuring that the input file is
   * associated with the reader and the output file is associated with the
   * writer. The input reader will read a full plane which will then be passed
   * to the OME Tiff Writer. The writer will then automatically write the
   * image in a tiled format based on the tile size values provided.
   *
   */
  public void readWriteTiles() {
    try {
      // construct the object that stores OME-XML metadata
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      IMetadata omexml = service.createOMEXMLMetadata();

      // set up the reader and associate it with the input file
      reader = new ImageReader();
      reader.setMetadataStore(omexml);
      reader.setId(inputFile);

      /* initialize-tiling-writer-example-start */
      // set up the writer and associate it with the output file
      writer = new OMETiffWriter();
      writer.setMetadataRetrieve(omexml);
      writer.setInterleaved(reader.isInterleaved());

      // set the tile size height and width for writing
      writer.setTileSizeX(tileSizeX);
      writer.setTileSizeY(tileSizeY);

      writer.setId(outputFile);

      /* initialize-tiling-writer-example-end */
      /* tiling-writer-example-start */

      byte[] buf = new byte[FormatTools.getPlaneSize(reader)];

      for (int series=0; series<reader.getSeriesCount(); series++) {
        reader.setSeries(series);
        writer.setSeries(series);

        // convert each image in the current series
        for (int image=0; image<reader.getImageCount(); image++) {
          // Read tiles from the input file and write them to the output OME Tiff
          // The OME Tiff Writer will automatically write the images in a tiled format
          buf = reader.openBytes(image);
          writer.saveBytes(image, buf);
        }
      }
      /* tiling-writer-example-end */
    }
    catch (Exception e) {
      System.err.println("Failed to read and write tiled files.");
      e.printStackTrace();
    }
  }

  /** Close the file reader and writer. */
  private void cleanup() {
    try {
      reader.close();
    }
    catch (IOException e) {
      System.err.println("Failed to close reader.");
      e.printStackTrace();
    }
    try {
      writer.close();
    }
    catch (IOException e) {
      System.err.println("Failed to close writer.");
      e.printStackTrace();
    }
  }

  /**
   * To read an image file and write out an OME Tiff tiled image on the command line:
   *
   * $ java SimpleTiledWriter input-file.oib output-file.ome.tiff 256 256
   * @throws IOException
   * @throws FormatException
   */
  public static void main(String[] args) throws FormatException, IOException {
    int tileSizeX = Integer.parseInt(args[2]);
    int tileSizeY = Integer.parseInt(args[3]);
    SimpleTiledWriter tiledWriter = new SimpleTiledWriter(args[0], args[1], tileSizeX, tileSizeY);

    // Read in images from the input and write them out automatically using tiling
    tiledWriter.readWriteTiles();

    // close the files
    tiledWriter.cleanup();
  }

}
