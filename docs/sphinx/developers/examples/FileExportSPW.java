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

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.io.IOException;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.ImageWriter;
import loci.formats.meta.IMetadata;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;
import loci.formats.MetadataTools;

import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.enums.NamingConvention;

/**
 * Example class that shows how to export raw pixel data to OME-TIFF as a Plate
 * using Bio-Formats version 5.0.3 or later.
 */
public class FileExportSPW {

  private final int sizeT = 3;

  private final int rows = 2;

  private final int cols = 2;

  private final int fovPerWell = 2;

  /** The file writer. */
  private ImageWriter writer;

  /** The name of the output file. */
  private final String outputFile;

  /**
   * Construct a new FileExport that will save to the specified file.
   *
   * @param outputFile
   *          the file to which we will export
   */
  public FileExportSPW(String outputFile) {
    this.outputFile = outputFile;
  }

  /** Save a single uint16 plane of data. */
  public void export() {
    int width = 4, height = 4;
    int pixelType = FormatTools.UINT16;
    Exception exception = null;

    IMetadata omexml = initializeMetadata(width, height, pixelType);

    int series = 0;
    int nSeries = rows * cols * fovPerWell;

    // only save data if the file writer was initialized successfully
    boolean initializationSuccess = initializeWriter(omexml);

    if (initializationSuccess) {
      while (series < nSeries) {
        for (int p = 0; p < sizeT; p++) {
          savePlane(width, height, pixelType, p, series);
        }
        series++;
        if (series < nSeries)  {
          try {
            writer.setSeries(series);
          } catch (FormatException e) {
            System.err.println("Fatal error unable to select correct image in series! " + e.getMessage());
            break;
          }
        }
      } // endwhile
    } // endif
    cleanup();
  }

  /**
   * Set up the file writer.
   *
   * @param omexml
   *          the IMetadata object that is to be associated with the writer
   * @return true if the file writer was successfully initialized; false if an
   *         error occurred
   */
  private boolean initializeWriter(IMetadata omexml) {
    // create the file writer and associate the OME-XML metadata with it
    writer = new ImageWriter();
    writer.setMetadataRetrieve(omexml);

    Exception exception = null;
    try {
      writer.setId(outputFile);
    } catch (FormatException e) {
      exception = e;
    } catch (IOException e) {
      exception = e;
    }
    if (exception != null) {
      System.err.println("Failed to initialize file writer.");
      exception.printStackTrace();
    }
    return exception == null;
  }

  /**
   * Populate the minimum amount of metadata required to export a Plate.
   *
   * @param width
   *          the width (in pixels) of the image
   * @param height
   *          the height (in pixels) of the image
   * @param pixelType
   *          the pixel type of the image; @see loci.formats.FormatTools
   */
  private IMetadata initializeMetadata(int width, int height, int pixelType) {
    Exception exception = null;
    try {
      // create the OME-XML metadata storage object
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      OMEXMLMetadata meta = service.createOMEXMLMetadata();
      meta.createRoot();

      int plateIndex = 0;
      int series = 0; // count of images
      int well = 0;

      // Create Minimal 2x2 Plate
      meta.setPlateID(MetadataTools.createLSID("Plate", 0), 0);

      meta.setPlateRowNamingConvention(NamingConvention.LETTER, 0);
      meta.setPlateColumnNamingConvention(NamingConvention.NUMBER, 0);
      meta.setPlateRows(new PositiveInteger(rows), 0);
      meta.setPlateColumns(new PositiveInteger(cols), 0);
      meta.setPlateName("First test Plate", 0);

      PositiveInteger pwidth = new PositiveInteger(width);
      PositiveInteger pheight = new PositiveInteger(height);

      char rowChar = 'A';
      for (int row = 0; row < rows; row++) {
        for (int column = 0; column < cols; column++) {

          // set up well
          String wellID = MetadataTools.createLSID("Well:", well);
          meta.setWellID(wellID, plateIndex, well);
          meta.setWellRow(new NonNegativeInteger(row), plateIndex, well);
          meta.setWellColumn(new NonNegativeInteger(column), plateIndex, well);

          for (int fov = 0; fov < fovPerWell; fov++) {

            // Create Image 
            String imageName = rowChar + ":" + Integer.toString(column+1)
                + ":FOV:" + Integer.toString(fov+1);
            String imageID = MetadataTools.createLSID("Image", well, fov);
            meta.setImageID(imageID, series);
            meta.setImageName(imageName, series);

            String pixelsID = MetadataTools
                .createLSID("Pixels", row, well, fov);
            meta.setPixelsID(pixelsID, series);

            // specify that the pixel data is stored in big-endian format
            // change 'TRUE' to 'FALSE' to specify little-endian format
            meta.setPixelsBinDataBigEndian(Boolean.TRUE, series, 0);

            // specify that the image is stored in ZCT order
            meta.setPixelsDimensionOrder(DimensionOrder.XYZCT, series);

            // specify the pixel type of the image
            meta.setPixelsType(
                PixelType.fromString(FormatTools.getPixelTypeString(pixelType)),
                series);

            // specify the dimensions of the image
            meta.setPixelsSizeX(pwidth, series);
            meta.setPixelsSizeY(pheight, series);
            meta.setPixelsSizeZ(new PositiveInteger(1), series);
            meta.setPixelsSizeC(new PositiveInteger(1), series);
            meta.setPixelsSizeT(new PositiveInteger(sizeT), series);

            // define each channel and specify the number of samples in the
            // channel the number of samples is 3 for RGB images and 1 otherwise
            String channelID = MetadataTools.createLSID("Channel", well, fov);
            meta.setChannelID(channelID, series, 0);
            meta.setChannelSamplesPerPixel(new PositiveInteger(1), series, 0);

            // set sample
            String wellSampleID = MetadataTools.createLSID("WellSample", well,
                fov);
            meta.setWellSampleID(wellSampleID, 0, well, fov);
            // NB sampleIndex here == series ie the image No
            meta.setWellSampleIndex(new NonNegativeInteger(series), 0, well,
                fov);
            meta.setWellSampleImageRef(imageID, 0, well, fov);

            // add FLIM ModuloAlongT annotation if required
            // CoreMetadata modlo = createModuloAnn();
            // meta.addModuloAlong(meta, modlo, series);

            series++;
          } // end of samples
          well++;
        }
        rowChar++;
      }

      return meta;
    } catch (DependencyException e) {
      exception = e;
    } catch (ServiceException e) {
      exception = e;
    } catch (EnumerationException e) {
      exception = e;
    }

    System.err.println("Failed to populate OME-XML metadata object.");
    exception.printStackTrace();
    return null;
  }

  /**
   * Add ModuloAlong annotation.
   *
   * @param meta
   *          OMEXMLMetadata Object to which Modulo need to be added
   */
  private CoreMetadata createModuloAnn() {
    CoreMetadata modlo = new CoreMetadata();

    modlo.moduloT.type = loci.formats.FormatTools.LIFETIME;
    modlo.moduloT.unit = "ps";
    modlo.moduloT.typeDescription = "Gated";

    modlo.moduloT.labels = new String[sizeT];

    for (int i = 0; i < sizeT; i++) {
      modlo.moduloT.labels[i] = Integer.toString(i * 1000);
    }

    return modlo;

  }

  /**
   * Generate a plane of pixel data and save it to the output file.
   *
   * @param width
   *          the width of the image in pixels
   * @param height
   *          the height of the image in pixels
   * @param pixelType
   *          the pixel type of the image; @see loci.formats.FormatTools
   */
  private void savePlane(int width, int height, int pixelType, int index,
      int series) {
    byte[] plane = createImage(width, height, pixelType, index, series);

    Exception exception = null;
    try {
      writer.saveBytes(index, plane);
    } catch (FormatException e) {
      exception = e;
    } catch (IOException e) {
      exception = e;
    }
    if (exception != null) {
      System.err.println("Failed to save plane.");
      exception.printStackTrace();
    }
  }

  /**
   * Generate a plane of pixel data.
   *
   * @param width
   *          the width of the image in pixels
   * @param height
   *          the height of the image in pixels
   * @param pixelType
   *          the pixel type of the image; @see loci.formats.FormatTools
   */
  private byte[] createImage(int width, int height, int pixelType, int index,
      int series) {
    // create a blank image of the specified size
    int bpp = FormatTools.getBytesPerPixel(pixelType);
    byte[] img = new byte[width * height * bpp];

    ByteBuffer bb = ByteBuffer.wrap(img);
    bb.order(ByteOrder.BIG_ENDIAN);

    // fill it with background
    for (int i = 0; i < img.length; i += bpp) {
      bb.putShort(i, (short) 200);
    }

    // then set 1 pixel to non-zero. Different values in each plane
    switch (index) {
      case 0:
        bb.putShort(series * bpp, (short) 1000);
        break;
      case 1:
        bb.putShort(series * bpp, (short) 700);
        break;
      case 2:
        bb.putShort(series * bpp, (short) 300);
        break;
    }

    return img;
  }

  /** Close the file writer. */
  private void cleanup() {
    try {
      writer.close();
    } catch (IOException e) {
      System.err.println("Failed to close file writer. " + e.getMessage());
    }
  }

  /**
   * To export a file to OME-TIFF:
   *
   * $ java FileExportSPW output-file.ome.tiff
   *
   * @param args
   * @throws java.lang.Exception
   */
  public static void main(String[] args) throws Exception {
    
    String fileName  = args[0];
    
    File file = new File(fileName);
    Exception exception = null;
 
    // delete file if it exists
    // NB deleting old files seems to be critical 
    if (file.exists())  {
      file.delete();
    }

    FileExportSPW exporter = new FileExportSPW(fileName);
    exporter.export();
  }
}
