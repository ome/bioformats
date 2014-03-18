/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package loci.formats.out;

import java.io.IOException;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.meta.MetadataRetrieve;
import static loci.common.DataTools.unpackBytes;

/**
  */
public class V3DrawWriter extends FormatWriter {

  // -- Fields --


  private long pixelOffset = 43;
  private long lastPlane = -1;
  private RandomAccessOutputStream pixels;

  private String outputOrder = "XYZCT";

  // -- Constructor --

  public V3DrawWriter() {
    super("Vaa3d", new String[] {"v3draw"});
  
  }

  // -- V3DRAWWriter API methods --

  /**
   * Set the order in which dimensions should be written to the file.
   * Valid values are specified in the documentation for
   * {@link loci.formats.IFormatReader#getDimensionOrder()}
   *
   * By default, the ordering is "XYZCT".
   */
  public void setOutputOrder(String outputOrder) {
    this.outputOrder = outputOrder;
  }

  // -- IFormatWriter API methods --

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {    
        final String formatkey = "raw_image_stack_by_hpeng"; // for header  
        byte[] v2 = new byte[2];
        byte[] v4 = new byte[4];

        int[] sz = new int[4];  // new variable for clarity: vaa3d is in xyzct format
        boolean little = true;
  
          checkParams(no, buf, x, y, w, h);
      // .v3draw format doesn't know anything about x and y offsets, so they are ignored, asssuming
      // each byte [] buf is a full width-height plane.
     
      
      
    if (pixels == null) {
      pixels = new RandomAccessOutputStream(currentId);
    }

    MetadataRetrieve meta = getMetadataRetrieve();

    int rgbChannels = getSamplesPerPixel();

    String order = meta.getPixelsDimensionOrder(series).getValue();
    int sizeZ = meta.getPixelsSizeZ(series).getValue().intValue();
    int sizeC = meta.getChannelCount(series);
    if (rgbChannels <= sizeC) {
      sizeC /= rgbChannels;
    }
    int sizeT = meta.getPixelsSizeT(series).getValue().intValue();
    int planes = sizeZ * sizeC * sizeT;

    int[] coords =
      FormatTools.getZCTCoords(order, sizeZ, sizeC, sizeT, planes, no);
    int realIndex =
      FormatTools.getIndex(outputOrder, sizeZ, sizeC, sizeT, planes,
      coords[0], coords[1], coords[2]);

    int sizeX = meta.getPixelsSizeX(series).getValue().intValue();
    int sizeY = meta.getPixelsSizeY(series).getValue().intValue();
    int pixelType =
      FormatTools.pixelTypeFromString(meta.getPixelsType(series).toString());
    int bytesPerPixel = FormatTools.getBytesPerPixel(pixelType);
    long planeSize = sizeX * sizeY *  bytesPerPixel* rgbChannels;

//FormatTools.get
  
    
    			sz[0] = sizeX;
			sz[1] = sizeY;
			sz[2] = sizeZ*sizeT;// temporary aggregate for layer
			sz[3] = sizeC*rgbChannels; // temp aggregate for color
    if (!initialized[series][realIndex]) {
        initialized[series][realIndex] = true;      
    }

//write the header if it's the first time through   

    
    if (lastPlane == -1){
    pixels.write(formatkey.getBytes());             // write 	format key 
    pixels.write("L".getBytes());                   //       	endianness.
    unpackBytes(bytesPerPixel, v2, 0, 2, little);		
    pixels.write(v2);                                //       	unitSize 
for (int d:sz){ 
    unpackBytes(d, v4, 0, 4, little);		
    pixels.write(v4); }        // and    	image dimensions into header 
    pixels.write(buf);
    
    System.out.println("*********   V3DrawWriter.java internal variables  *********");

    System.out.println("bytesPerPixel = "+bytesPerPixel);
    System.out.println("pixelType = "+pixelType);
    System.out.println("rgbChannels =" +rgbChannels);
    System.out.println("sizeC = "+sizeC);
    System.out.println("sizeZ = "+sizeZ);
    System.out.println("sizeT = "+sizeT);
    
    
    
    }
    else{
    /*
    System.out.println("inside main loop");
    System.out.println(planeSize);
    System.out.println(realIndex);
    System.out.println(pixelOffset);
    System.out.println(planeSize*realIndex + pixelOffset);
    */
    pixels.seek(planeSize*realIndex + pixelOffset);
    //write the rest of the plane 
    pixels.write(buf);
    } 
    lastPlane = realIndex;
    pixels.close();
    pixels = null;
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
  public boolean canDoStacks() { return true; }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  public int[] getPixelTypes(String codec) {
    return new int[] { FormatTools.UINT8, 
      FormatTools.UINT16,
      FormatTools.FLOAT};
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#setId(String) */
  public void setId(String id) throws FormatException, IOException {
    super.setId(id);
  }

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    pixelOffset = 0;
    lastPlane = -1;
    if (pixels != null) {
      pixels.close();
    }
    pixels = null;
  }

  // -- Helper methods --

}
