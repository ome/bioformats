/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2012 Board of Regents of the University of
 * Wisconsin-Madison, Glencoe Software, Inc., and University of Dundee.
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

package loci.formats.itk;

import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
//import loci.formats.FormatTools;
import loci.formats.ImageReader;
import loci.formats.FormatTools;
import loci.formats.FormatException;

/**
 * Java bridge connecting Bio-Formats to ITK via JNI.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/itk/ITKBridgeJNI.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/itk/ITKBridgeJNI.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Mark Hiner hiner at wisc.edu
 */
public class ITKBridgeJNI {
  private static ImageReader reader;
  public ITKBridgeJNI() {
    reader = new ImageReader();
  }

  public static boolean canReadFile(String id) {
    boolean h = false;
    try {
      h = reader.isThisType(id);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return h;
  }

  public static double[] readImageInfo(String id) {
    try {
      reader.setId(id);
    } catch (FormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // ORDERING: 0 little, 1 seriesCount, 2 pixelType, 3 bpp, 4 itkComponentType, 5 sizeX,
    //           6 sizeY, 7 sizeZ, 8 sizeT, 9 sizeC, 10 effSizeC, 11 rgbChannelCount, 12 imageCount
    //           13 physX, 14 physY, 15 physZ, 16 timeIncrement
    double[] returnValues = new double[17];

    // return this and use SetByteOrderToLittleEndian or SetByteOrderToBigEndian in C++ land
    boolean little = reader.isLittleEndian();



    if(little) {
      returnValues[0] = 1;
    }
    else {
      returnValues[0] = 0;
    }

    returnValues[1] = reader.getSeriesCount();

    // return bpp and set an IOComponent based on it
    int pixelType = reader.getPixelType();
    returnValues[2]  = (double)pixelType;
    returnValues[3] = (double)FormatTools.getBytesPerPixel((int)returnValues[2]);

    // 0 UCHAR, 1 CHAR, 2 USHORT, 3 SHORT, 4 UINT, 5 INT, 6 FLOAT, 7 DOUBLE, 8 UNKNOWN
    if (pixelType == FormatTools.UINT8)
      returnValues[4] = (double)0;
    else if (pixelType == FormatTools.INT8)
      returnValues[4] = (double)1;
    else if (pixelType == FormatTools.UINT16)
      returnValues[4] = (double)2;
    else if (pixelType == FormatTools.INT16)
      returnValues[4] = (double)3;
    else if (pixelType == FormatTools.UINT32)
      returnValues[4] = (double)4;
    else if (pixelType == FormatTools.INT32)
      returnValues[4] = (double)5;
    else if (pixelType == FormatTools.FLOAT)
      returnValues[4] = (double)6;
    else if (pixelType == FormatTools.DOUBLE)
      returnValues[4] = (double)7;
    else
      returnValues[4] = (double)8;

    // return these
    returnValues[5] = (double)reader.getSizeX();
    returnValues[6] = (double)reader.getSizeY();
    returnValues[7] = (double)reader.getSizeZ();
    returnValues[8] = (double)reader.getSizeT();
    returnValues[9] = (double)reader.getSizeC();
    returnValues[10] = (double)reader.getEffectiveSizeC();
    returnValues[11] = (double)reader.getRGBChannelCount();
    returnValues[12] = (double)reader.getImageCount();
    
    MetadataRetrieve retrieve = MetadataTools.asRetrieve(reader.getMetadataStore());
    Double d = retrieve.getPixelsPhysicalSizeX(0).getValue();
    double d2 = d == null ? 1.0 : d.doubleValue();
    returnValues[13] = d2;
    d = retrieve.getPixelsPhysicalSizeY(0).getValue();
    d2 = d == null ? 1.0 : d.doubleValue();
    returnValues[14] = d2;
    d = retrieve.getPixelsPhysicalSizeZ(0).getValue();
    d2 = d == null ? 1.0 : d.doubleValue();
    returnValues[15] = d2;
    d = retrieve.getPixelsTimeIncrement(0);
    d2 = d == null ? 1.0 : d.doubleValue();
    returnValues[16] = d2;
       
    return returnValues;
  }

  public static void readPlane(int z, int c, int t, byte[] buf, int xStart, int yStart, int xCount, int yCount) {
	  int no = reader.getIndex(z, c, t);
	  
	  try {
		  reader.openBytes(no, buf, xStart, yStart, xCount, yCount);
	  } catch (FormatException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	  } catch (IOException e) {
	      e.printStackTrace();
	  }
  }
  
  public static void close() {
	  try {
		  reader.close();
	  } catch (IOException e) {
		  e.printStackTrace();
	  }
  }
  
  public static boolean getIsInterleaved() {
	  return reader.isInterleaved();
  }
  
  public static int getImageCount() {
	  return reader.getImageCount();
  }
  
  public static int getBytesPerPixel() {
	  return FormatTools.getBytesPerPixel(reader.getPixelType());
  }

  public void quit() {
    System.exit(0);
  }

}
