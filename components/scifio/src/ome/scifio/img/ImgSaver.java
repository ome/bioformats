/*
ImgLib I/O logic using Bio-Formats.

Copyright (c) 2009, Stephan Preibisch & Stephan Saalfeld.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the Fiji project developers nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package ome.scifio.img;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;

import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.basictypeaccess.PlanarAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.meta.AxisType;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.StatusEvent;
import loci.common.StatusListener;
import loci.common.StatusReporter;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.AxisGuesser;
import loci.formats.FilePattern;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatWriter;
import loci.formats.ImageWriter;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.services.OMEXMLService;

/**
 * Writes out an {@link ImgPlus} using Bio-Formats
 *
 *
 * @author Mark Hiner
 * @author Curtis Rueden
 */

public class ImgSaver implements StatusReporter {

  //TODO update "source" metadata field if save as...
  
  //TODO convert ImgIOExceptions with IncompatibleTypeExceptions where applicable

  // -- Constants --

  // -- Fields --

  private final List<StatusListener> listeners =
    new ArrayList<StatusListener>();

  private OMEXMLService omexmlService;

  // -- Constructor --

  public ImgSaver() {
    omexmlService = createOMEXMLService();
  }

  // -- ImgSaver methods --

  /**
   * id and Img provided.
   * 
   * @param <T>
   * @param id
   * @param img
   * @throws ImgIOException
   */
  public <T extends RealType<T> & NativeType<T>> void saveImg(final String id,
    final Img<T> img) throws ImgIOException
  {
    saveImg(id, ImgPlus.wrap(img));
  }

  /**
   * id provided.
   * ImgPlus provided or wrapped Img.
   * 
   * @param <T>
   * @param id
   * @param img
   * @throws ImgIOException
   */
  public <T extends RealType<T> & NativeType<T>> void saveImg(final String id,
    final ImgPlus<T> img) throws ImgIOException
  {
    saveImg(initializeWriter(id, img), img, false);
  }

  /**
   * Writer and Img provided
   * 
   * @param <T>
   * @param w
   * @param img
   * @throws ImgIOException
   */
  public <T extends RealType<T> & NativeType<T>> void saveImg(
    final IFormatWriter w, final Img<T> img) throws ImgIOException
  {
    saveImg(w, ImgPlus.wrap(img));
  }

  /**
   * Writer provided.
   * ImgPlus provided, or wrapped provided Img.
   * 
   * @param <T>
   * @param w
   * @param img
   * @throws ImgIOException 
   */
  public <T extends RealType<T> & NativeType<T>> void saveImg(
    final IFormatWriter w, final ImgPlus<T> img) throws ImgIOException
  {
    saveImg(w, img, true);
  }
  
  /* Entry point for writePlanes method, the actual workhorse to save pixels to disk */
  private <T extends RealType<T> & NativeType<T>> void saveImg(
    final IFormatWriter w, final ImgPlus<T> img, final boolean initializeWriter) throws ImgIOException
    {
    
    // use the ImgPlus to calculate necessary metadata if 
    if(initializeWriter) {
      populateMeta(w, img);
    }

    final long startTime = System.currentTimeMillis();
    final String id = img.getName();
    final int planeCount = img.numDimensions();

    // write pixels
    writePlanes(w, img);

    final long endTime = System.currentTimeMillis();
    final float time = (endTime - startTime) / 1000f;
    notifyListeners(new StatusEvent(planeCount, planeCount, id + ": wrote " +
      planeCount + " planes in " + time + " s"));
    }

  // -- StatusReporter methods --

  /** Adds a listener to those informed when progress occurs. */
  public void addStatusListener(final StatusListener l) {
    synchronized (listeners) {
      listeners.add(l);
    }
  }

  /** Removes a listener from those informed when progress occurs. */
  public void removeStatusListener(final StatusListener l) {
    synchronized (listeners) {
      listeners.remove(l);
    }
  }

  /** Notifies registered listeners of progress. */
  public void notifyListeners(final StatusEvent e) {
    synchronized (listeners) {
      for (final StatusListener l : listeners)
        l.statusUpdated(e);
    }
  }

  // -- Helper Methods --

  /**
   * Iterates through the planes of the provided {@link ImgPlus}, converting each to a 
   * byte[] if necessary (the Bio-Formats writer requires a byte[]) and saving the
   * plane.
   * 
   * Currently only {@link PlanarImg} is supported
   */
  @SuppressWarnings("unchecked")
  private <T extends RealType<T> & NativeType<T>> void writePlanes(
    final IFormatWriter w, final ImgPlus<T> img) throws ImgIOException
  {
    final PlanarAccess<?> planarAccess = ImgIOUtils.getPlanarAccess(img);
    if (planarAccess == null) {
      throw new ImgIOException("Only Planar images supported at this time.");
    }

    final PlanarImg<T, ?> planarImg = (PlanarImg<T, ?>) planarAccess;
    final int planeCount = planarImg.numSlices();

    if (img.numDimensions() > 0) {
      final Class<?> arrayType = planarImg.getPlane(0).getCurrentStorageArray().getClass();

      byte[] plane = null;

      // iterate over each plane
      for (int planeIndex = 0; planeIndex < planeCount; planeIndex++) {
        notifyListeners(new StatusEvent(
          planeIndex, planeCount, "Saving plane " + (planeIndex + 1) + "/" +
            planeCount));

        Object curPlane = planarImg.getPlane(planeIndex).getCurrentStorageArray();

        // Convert current plane if necessary
        if (arrayType == int[].class) {
          plane = DataTools.intsToBytes((int[]) curPlane, false);
        }
        else if (arrayType == byte[].class) {
          plane = (byte[]) curPlane;
        }
        else if (arrayType == short[].class) {
          plane = DataTools.shortsToBytes((short[]) curPlane, false);
        }
        else if (arrayType == long[].class) {
          plane = DataTools.longsToBytes((long[]) curPlane, false);
        }
        else if (arrayType == double[].class) {
          plane = DataTools.doublesToBytes((double[]) curPlane, false);
        }
        else if (arrayType == float[].class) {
          plane = DataTools.floatsToBytes((float[]) curPlane, false);
        }
        else {
          throw new ImgIOException(
            "PlanarImgs of type boolean or char not supported.");
        }

        // save bytes
        try {
          w.saveBytes(planeIndex, plane);
        }
        catch (FormatException e) {
          throw new ImgIOException(e);
        }
        catch (IOException e) {
          throw new ImgIOException(e);
        }
      }

    }

    try {
      w.close();
    }
    catch (IOException e) {
      throw new ImgIOException(e);
    }
  }

  /**
   *  Creates a new {@link IFormatWriter} with an unpopulated MetadataStore
   *  and sets its id to the provided String. 
   */
  private <T extends RealType<T> & NativeType<T>> IFormatWriter initializeWriter(
    final String id, final ImgPlus<T> img) throws ImgIOException
  {
    final IFormatWriter writer = new ImageWriter();
    final IMetadata store = MetadataTools.createOMEXMLMetadata();
    store.createRoot();
    writer.setMetadataRetrieve(store);
    
    populateMeta(writer, img);
    
    try {
      writer.setId(id);
    }
    catch (FormatException e) {
      throw new ImgIOException(e);
    }
    catch (IOException e) {
      throw new ImgIOException(e);
    }

    return writer;
  }

  /**
   * Uses the provided {@link ImgPlus} to populate the minimum metadata
   *  fields necessary for writing.
   * 
   */
  private <T extends RealType<T> & NativeType<T>> void populateMeta(
    final IFormatWriter w, final ImgPlus<T> img) throws ImgIOException
  {
    //TODO image numbers? or are all imgPluses just a single image?

    notifyListeners(new StatusEvent("Initializing " + img.getName()));

    final MetadataRetrieve retrieve = w.getMetadataRetrieve();

    if (omexmlService == null)
      throw new ImgIOException(
        "No OMEXMLService found. Invoke ImgSaver constructor first.");

    // make sure we can store information in the writer's MetadataObject
    if (omexmlService.asStore(retrieve) != null) {
      final IMetadata meta = (IMetadata) retrieve;

      // set required metadata

      final int pixelType = ImgIOUtils.makeType(img.firstElement());

      final AxisType[] axes = new AxisType[img.numDimensions()];
      img.axes(axes);

      String dimOrder = "";

      int sizeX = 0, sizeY = 0, sizeZ = -1, sizeC = -1, sizeT = -1;
      
      // parse dimension order and lengths
      for (int i = 0; i < axes.length; i++) {
        switch (axes[i].getLabel().toLowerCase().charAt(0)) {
          case 'x':
            sizeX = new Long(img.dimension(i)).intValue();
            dimOrder += "X";
            break;
          case 'y':
            sizeY = new Long(img.dimension(i)).intValue();
            dimOrder += "Y";
            break;
          case 'z':
            sizeZ = new Long(img.dimension(i)).intValue();
            dimOrder += "Z";
            break;
          case 'c':
            sizeC = new Long(img.dimension(i)).intValue();
            dimOrder += "C";
            break;
          case 't':
            sizeT = new Long(img.dimension(i)).intValue();
            dimOrder += "T";
            break;
        }
      }
      
      //TODO if size C, Z, T and dimension order are populated we won't overwrite them.
      /*
     if(meta.getPixelsSizeZ(0) == null) sizeZ = meta.getPixelsSizeZ(0).getValue();
     if(meta.getPixelsSizeC(0) == null) sizeC = meta.getPixelsSizeC(0).getValue();
     if(meta.getPixelsSizeT(0) == null) sizeT = meta.getPixelsSizeT(0).getValue();
      */
      
      //TODO guess at dimension order..
      if(sizeZ == -1) {
        dimOrder += "Z"; 
        sizeZ = 1;
      }
      if(sizeT == -1) {
        dimOrder += "T"; 
        sizeT = 1;
      }
      if(sizeC == -1) {
        dimOrder += "C"; 
        sizeC = 1;
      }
      
      MetadataTools.populateMetadata(meta, 0, img.getName(), false, dimOrder, FormatTools.getPixelTypeString(pixelType), sizeX, sizeY, sizeZ, sizeC, sizeT, img.getCompositeChannelCount());
    }
  }

  private OMEXMLService createOMEXMLService() {
    try {
      return new ServiceFactory().getInstance(OMEXMLService.class);
    }
    catch (DependencyException exc) {
      return null;
    }
  }
}
