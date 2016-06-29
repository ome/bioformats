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

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;

import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.ImageReader;
import loci.formats.ImageWriter;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

/**
 * Example class for converting a file from one format to another, using
 * Bio-Formats version 4.2 or later.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class FileConvert {

  /** The file format reader. */
  private ImageReader reader;

  /** The file format writer. */
  private ImageWriter writer;

  /** The file to be read. */
  private String inputFile;

  /** The file to be written. */
  private String outputFile;

  /**
   * Construct a new FileConvert to convert the specified input file.
   *
   * @param inputFile the file to be read
   * @param outputFile the file to be written
   */
  public FileConvert(String inputFile, String outputFile) {
    this.inputFile = inputFile;
    this.outputFile = outputFile;
  }

  /** Do the actual work of converting the input file to the output file. */
  public void convert() {
    // initialize the files
    boolean initializationSuccess = initialize();

    // if we could not initialize one of the files,
    // then it does not make sense to convert the planes
    if (initializationSuccess) {
      convertPlanes();
    }

    // close the files
    cleanup();
  }

  /**
   * Set up the file reader and writer, ensuring that the input file is
   * associated with the reader and the output file is associated with the
   * writer.
   *
   * @return true if the reader and writer were successfully set up, or false
   *   if an error occurred
   */
  private boolean initialize() {
    Exception exception = null;
    try {
      // construct the object that stores OME-XML metadata
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      IMetadata omexml = service.createOMEXMLMetadata();

      // set up the reader and associate it with the input file
      reader = new ImageReader();
      reader.setMetadataStore(omexml);
      reader.setId(inputFile);

      // set up the writer and associate it with the output file
      writer = new ImageWriter();
      writer.setMetadataRetrieve(omexml);
      writer.setInterleaved(reader.isInterleaved());
      writer.setId(outputFile);
    }
    catch (FormatException e) {
      exception = e;
    }
    catch (IOException e) {
      exception = e;
    }
    catch (DependencyException e) {
      exception = e;
    }
    catch (ServiceException e) {
      exception = e;
    }
    if (exception != null) {
      System.err.println("Failed to initialize files.");
      exception.printStackTrace();
    }
    return exception == null;
  }

  /** Save every plane in the input file to the output file. */
  private void convertPlanes() {
    for (int series=0; series<reader.getSeriesCount(); series++) {
      // tell the reader and writer which series to work with
      // in FV1000 OIB/OIF, there are at most two series - one
      // is the actual data, and one is the preview image
      reader.setSeries(series);
      try {
        writer.setSeries(series);
      }
      catch (FormatException e) {
        System.err.println("Failed to set writer's series #" + series);
        e.printStackTrace();
        break;
      }

      // construct a buffer to hold one image's pixels
      byte[] plane = new byte[FormatTools.getPlaneSize(reader)];

      // convert each image in the current series
      for (int image=0; image<reader.getImageCount(); image++) {
        try {
          reader.openBytes(image, plane);
          writer.saveBytes(image, plane);
        }
        catch (IOException e) {
          System.err.println("Failed to convert image #" + image +
            " in series #" + series);
          e.printStackTrace();
        }
        catch (FormatException e) {
          System.err.println("Failed to convert image #" + image +
            " in series #" + series);
          e.printStackTrace();
        }
      }
    }
  }

  /** Close the file reader and writer. */
  private void cleanup() {
    try {
      reader.close();
      writer.close();
    }
    catch (IOException e) {
      System.err.println("Failed to cleanup reader and writer.");
      e.printStackTrace();
    }
  }

  /**
   * To convert a file on the command line:
   *
   * $ java FileConvert input-file.oib output-file.ome.tiff
   */
  public static void main(String[] args) {
    FileConvert converter = new FileConvert(args[0], args[1]);
    converter.convert();
  }

}
