

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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import loci.common.DataTools;

import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.enums.NamingConvention;
import ome.units.quantity.Time;
import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.Image;
import ome.xml.model.Plate;
import ome.xml.model.StructuredAnnotations;
import ome.xml.model.Well;
import ome.xml.model.WellSample;
import ome.xml.model.XMLAnnotation;

/**
 * Example class that shows how to export raw pixel data to OME-TIFF as a Plate using
 * Bio-Formats version 5.0.3 or later.
 */
public class FileWriteSPW {
  
  private final int pixelType = FormatTools.UINT16;
  private int rows;
  private int cols;
  private int width;
  private int height;
  private int sizet;
  boolean initializationSuccess = false;
  
  private ArrayList<String> delays = null;
  
  private double[] exposureTimes = null;
  
  /** The file writer. */
  private ImageWriter writer = null;

  /** The name of the current output file. */
  private final String outputFile;
  
   /** Description of the plate. */
  private final String plateDescription;
  
  /** OME metadata **/
  private IMetadata omexml = null;
  
  /** OMEXML service **/
  private OMEXMLService service = null;
  
  /** expected Images array. No of planes that have been written to each Image **/
  private int[] expectedImages;
  
  
 

  /**
   * Construct a new FileWriteSPW that will save to the specified file.
   *
   * @param outputFile the file to which we will export
   * @param plateDescription
   */
  public FileWriteSPW(String outputFile, String plateDescription) {
    this.outputFile = outputFile;    
    this.plateDescription = plateDescription;
   
    File file = new File(outputFile);
 
    // delete file if it exists
    // NB deleting old files seems to be critical 
    if (file.exists())  {
      file.delete();
    }
    this.sizet = 1; // Non-FLIM by default
  }
  
  // Initialisation method for FLIM including  exposure times.
  public boolean init( int[][] nFov, int sizeX, int  sizeY, ArrayList<String> delays, double[] exposureTimes )  {
    
    this.exposureTimes = exposureTimes;
    
    initializationSuccess = init(nFov, sizeX, sizeY, delays);
    
    return initializationSuccess;
  }
  
  // Initialisation method for FLIM without   exposure times.
  public boolean init( int[][] nFov, int sizeX, int  sizeY, ArrayList<String> delays )  {
    
    this.sizet = delays.size();
    setupModulo(delays);
    
    initializationSuccess = init(nFov, sizeX, sizeY);
    
    return initializationSuccess;
    
  }
  
  // Initialisation method for non-FLIM data.
  public boolean init( int[][] nFov, int sizeX, int  sizeY )  {
    this.rows = nFov.length;
    this.cols = nFov[0].length;
    
    width = sizeX;
    height = sizeY;
    
    omexml = initializeMetadata(nFov);
    
    initializationSuccess = initializeWriter(omexml);
    
    return initializationSuccess;
    
  }

  

  /** Save a single Short plane of data.
   * @param plane  data
   * @param series  image no in plate
   * @param index t plane within image
   * @param imageDescription*/
  public void export(short[] plane, int series, int index, String imageDescription) {

    byte[] planeb;
    planeb = DataTools.shortsToBytes(plane, false);
    export(planeb, series, index, imageDescription);

  }
  
  
  /** Save a single byte plane of data.
   * @param plane  data
   * @param series  image no in plate
   * @param index t plane within image*/
  private void export(byte[] plane, int series, int index, String imageDescription) {

    Exception exception = null;

    if (initializationSuccess) {
      if (series != writer.getSeries()) {
        try {
          writer.setSeries(series);
        } catch (FormatException e) {
          exception = e;
        }
      }
      try {
        writer.saveBytes(index, plane);
        if (index == 0) {
          OMEXMLMetadataRoot root = (OMEXMLMetadataRoot) omexml.getRoot();
          Plate plate = root.getPlate(0);
          Image im = root.getImage(series);
          im.setDescription(imageDescription);
        }
        expectedImages[series]++;
      } catch (FormatException  | IOException e) {
        exception = e;
      }
    }
    if (exception != null) {
      System.err.println("Failed to write data!");
    }
  }
  
  /**
   * Set up the file writer.
   *
   * @param omexml the IMetadata object that is to be associated with the writer
   * @return true if the file writer was successfully initialized; false if an
   *   error occurred
   */
  private boolean initializeWriter(IMetadata omexml) {
    // create the file writer and associate the OME-XML metadata with it
    writer = new ImageWriter();
    writer.setMetadataRetrieve(omexml);

    Exception exception = null;
    try {
      writer.setId(outputFile);
    }
    catch (FormatException | IOException e) {
      exception = e;
    }
    if (exception != null) {
      System.err.println("Failed to initialize file writer.");
    }
    return exception == null;
  }

  /**
   * Populate the minimum amount of metadata required to export a Plate.
   *
   */
  private IMetadata initializeMetadata(int[][] nFovs) {
    Exception exception = null;
    try {
      // create the OME-XML metadata storage object
      ServiceFactory factory = new ServiceFactory();
      service = factory.getInstance(OMEXMLService.class);
      OMEXMLMetadata meta = service.createOMEXMLMetadata();
      //IMetadata meta = service.createOMEXMLMetadata();
      meta.createRoot();
    
      int plateIndex = 0;
      int series = 0;     // count of images
      int well = 0;
  
      meta.setPlateDescription(plateDescription,0); 
 
      meta.setPlateID(MetadataTools.createLSID("Plate", 0), 0);
      
      meta.setPlateRowNamingConvention(NamingConvention.LETTER, 0);
      meta.setPlateColumnNamingConvention(NamingConvention.NUMBER, 0);
      meta.setPlateRows(new PositiveInteger(rows), 0);
      meta.setPlateColumns(new PositiveInteger(cols), 0);
      meta.setPlateName("First test Plate", 0);
      
      PositiveInteger pwidth = new PositiveInteger(width);
      PositiveInteger pheight = new PositiveInteger(height);
        
      char rowChar = 'A';
      for (int row = 0; row  < rows; row++) {
        for (int column = 0; column < cols; column++) {
          
          // set up well
          String wellID = MetadataTools.createLSID("Well", well);
          meta.setWellID(wellID, plateIndex, well);
          meta.setWellRow(new NonNegativeInteger(row), plateIndex, well);
          meta.setWellColumn(new NonNegativeInteger(column), plateIndex, well); 
          
          int nFOV= nFovs[row][column];
          
          for(int fov = 0; fov < nFOV ; fov++)  {
            
            // Create Image NB numberng in the Name goes from 1->n not 0-> n-1
            String imageName = rowChar + ":" + Integer.toString(column + 1) + ":FOV:" + Integer.toString(fov + 1);
            String imageID = MetadataTools.createLSID("Image", well, fov);
            meta.setImageID(imageID, series);
            meta.setImageName(imageName, series);
            
            String pixelsID = MetadataTools.createLSID("Pixels", well, fov);
            meta.setPixelsID(pixelsID, series);
            
            // specify that the pixel data is stored in big-endian format
            // change 'TRUE' to 'FALSE' to specify little-endian format
            meta.setPixelsBigEndian(Boolean.TRUE, series);

            // specify that the image is stored in ZCT order
            meta.setPixelsDimensionOrder(DimensionOrder.XYZCT, series);

            // specify the pixel type of the image
            meta.setPixelsType(PixelType.fromString(FormatTools.getPixelTypeString(pixelType)), series);

            // specify the dimensions of the image
            meta.setPixelsSizeX(pwidth, series);
            meta.setPixelsSizeY(pheight, series);
            meta.setPixelsSizeZ(new PositiveInteger(1), series);
            meta.setPixelsSizeC(new PositiveInteger(1), series);
            meta.setPixelsSizeT(new PositiveInteger(sizet), series);

            // define each channel and specify the number of samples in the channel
            // the number of samples is 3 for RGB images and 1 otherwise
            String channelID = MetadataTools.createLSID("Channel",well, fov);
            meta.setChannelID(channelID, series,0 );
            meta.setChannelSamplesPerPixel(new PositiveInteger(1), series, 0);
           
            // set sample
            String wellSampleID = MetadataTools.createLSID("WellSample",well, fov);
            meta.setWellSampleID(wellSampleID,0,well,fov);
            // NB sampleIndex here == series ie the image No
            meta.setWellSampleIndex(new NonNegativeInteger(series), 0, well, fov);
            meta.setWellSampleImageRef(imageID, 0, well, fov);
             
            if (exposureTimes != null && exposureTimes.length == sizet)  {
              for (int t = 0; t < sizet; t++)  {
                meta.setPlaneTheT(new NonNegativeInteger(t), series, t);
                meta.setPlaneTheC(new NonNegativeInteger(0), series, t);
                meta.setPlaneTheZ(new NonNegativeInteger(0), series, t);
                meta.setPlaneExposureTime(new Time(exposureTimes[t],ome.units.UNITS.SECOND), series, t);
              } 
            }
            
            // add FLIM ModuloAlongT annotation if required 
            if (delays != null)  {
              CoreMetadata modlo = createModuloAnn(meta);
              service.addModuloAlong(meta, modlo, series);
            }
            
            series++;
          }  //end of samples  
          well++;
        }
        rowChar++;
      }
      
      expectedImages = new int[series];
      
      //String dump = meta.dumpXML();
      //System.out.println("dump = ");
      //System.out.println(dump);
      return meta;
    }
    
    catch (DependencyException | ServiceException | EnumerationException e) {
      exception = e;
    }

    System.err.println("Failed to populate OME-XML metadata object.");
    return null;    
      
  }
  
  
  /**
   * Setup delays.
   */
  private boolean setupModulo(ArrayList<String> delays) {
    
    boolean success = false;
    if (delays.size() == sizet)  {
      this.delays = delays;
      success = true;
    }
    return success;
  
  }

  
   /**
   * Add ModuloAlong annotation.
   */
  private CoreMetadata createModuloAnn(IMetadata meta) {

    CoreMetadata modlo = new CoreMetadata();

    modlo.moduloT.type = loci.formats.FormatTools.LIFETIME;
    modlo.moduloT.unit = "ps";
    modlo.moduloT.typeDescription = "Gated";

    modlo.moduloT.labels = new String[sizet];

    for (int i = 0; i < sizet; i++) {
      //System.out.println(delays.get(i));
      modlo.moduloT.labels[i] = delays.get(i);
      
    }

    return modlo;
  }

  
  
  /** Close the file writer. */
  public void cleanup() {
    
    int validPlanes = 1;  // No of planes expected for each image = 1 if not FLIM
    if (delays != null)  {
      validPlanes = sizet; 
    }
    
    OMEXMLMetadataRoot root = (OMEXMLMetadataRoot) omexml.getRoot();
    
     Plate plate = root.getPlate(0);
     StructuredAnnotations anns = root.getStructuredAnnotations();
     
     ArrayList<Image> invalidImages = new ArrayList<>();
    
    // Check that all expected Images have received the correct no of timepoints.
    // if not record those images as being invalid
    for(int i = 0; i < expectedImages.length; i++)  {
      if (expectedImages[i] < validPlanes)  {
        Image im = root.getImage(i);
        invalidImages.add(im);
        // remove modulo Annotation if FLIM
        if (delays != null)  {
          XMLAnnotation ann = (XMLAnnotation) im.getLinkedAnnotation(0);
          anns.removeXMLAnnotation(ann);
        }
      }      
    }
    
  
    // Now remove all limked wellSnmples and then invalid images 
    for(int i = 0; i < invalidImages.size(); i++)  {
      Image im = invalidImages.get(i);
      List<WellSample> list = im.copyLinkedWellSampleList();
      if (!list.isEmpty())  {
        WellSample wellSample = im.getLinkedWellSample(0);
        Well well = wellSample.getWell();
        well.removeWellSample(wellSample);
      }
      root.removeImage(im);
    }
    
 
  
    if (writer != null)  {
      try {
        writer.close();
      }
      catch (IOException e) {
        System.err.println("Failed to close file writer.");
      }
    }
  }
  
}
