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

import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.basictypeaccess.PlanarAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import loci.common.StatusEvent;
import loci.common.StatusListener;
import loci.common.StatusReporter;
import loci.formats.FormatException;
import loci.formats.IFormatWriter;
import loci.formats.ImageWriter;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;

/**
 * Writes out an {@link ImgPlus} using Bio-Formats
 *
 *
 * @author Mark Hiner
 * @author Curtis Rueden
 */

public class ImgSaver implements StatusReporter {

  //TODO Status messages
  
  //TODO determine if working with planar images
  
  // -- Fields --
  
  private final List<StatusListener> listeners =
    new ArrayList<StatusListener>();
  
  // -- ImgSaver methods --
  
  /**
   * @throws ImgIOException 
   * @throws IOException 
   * @throws FormatException 
   * 
   */
  public <T extends RealType<T> & NativeType<T>> void saveImg(
    final String id, final ImgPlus<T> img) throws ImgIOException  {
    saveImg(initializeWriter(id), img);
  }
  
  /**
   * 
   * @param <T>
   * @param img
   * @throws ImgIOException 
   * @throws IOException 
   * @throws FormatException 
   */
  public <T extends RealType<T> & NativeType<T>> void saveImg(
    final String id, final Img<T> img) throws ImgIOException  {
    saveImg(id, ImgPlus.wrap(img));
  }
  
  /**
   * 
   * @param <T>
   * @param w
   * @param img
   * @throws ImgIOException 
   */
  public <T extends RealType<T> & NativeType<T>> void saveImg(final IFormatWriter w, 
    final ImgPlus<T> img) throws ImgIOException {
    
    // write pixels
   final long startTime = System.currentTimeMillis();
   final String id = img.getName();
   final int planeCount = img.numDimensions();

   writePlanes(w, img);

   final long endTime = System.currentTimeMillis();
   final float time = (endTime - startTime) / 1000f;
   notifyListeners(new StatusEvent(planeCount, planeCount, id + 
     ": wrote " + planeCount + " planes in " + time + " s"));
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
  
  private <T extends RealType<T>> void writePlanes(IFormatWriter w, ImgPlus<T> img) throws ImgIOException {
    final PlanarAccess<?> planarAccess = ImgIOUtils.getPlanarAccess(img);
    if(planarAccess == null) {
      throw new ImgIOException(new FormatException(), "Only Planar images supported at this time.");
    }
    
    
    try {
      w.close();
    }
    catch (IOException e) {
      throw new ImgIOException(e);
    }
  }
  
  private IFormatWriter initializeWriter(String id) throws ImgIOException {
    IFormatWriter writer = new ImageWriter();
    final IMetadata store = MetadataTools.createOMEXMLMetadata();
    writer.setMetadataRetrieve(store);
    
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
}
