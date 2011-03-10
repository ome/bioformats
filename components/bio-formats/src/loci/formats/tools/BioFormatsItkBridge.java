//
// BioFormatsItkBridge.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.tools;

import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.ImageReader;
import loci.formats.FormatTools;
import loci.formats.FormatException;

/**
 * Java bridge connecting Bio-Formats to ITK via JNI.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/tools/BioFormatsItkBridge.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/tools/BioFormatsItkBridge.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Mark Hiner hiner at wisc.edu
 */
public class BioFormatsItkBridge {
  private static ImageReader reader;
  public BioFormatsItkBridge() {
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

  public static int[] readImageInfo(String id) {
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
    int[] returnValues = new int[13];

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
    returnValues[2]  = pixelType;
    returnValues[3] = FormatTools.getBytesPerPixel(returnValues[2]);

    // 0 UCHAR, 1 CHAR, 2 USHORT, 3 SHORT, 4 UINT, 5 INT, 6 FLOAT, 7 DOUBLE, 8 UNKNOWN
    if (pixelType == FormatTools.UINT8)
      returnValues[4] = 0;
    else if (pixelType == FormatTools.INT8)
      returnValues[4] = 1;
    else if (pixelType == FormatTools.UINT16)
      returnValues[4] = 2;
    else if (pixelType == FormatTools.INT16)
      returnValues[4] = 3;
    else if (pixelType == FormatTools.UINT32)
      returnValues[4] = 4;
    else if (pixelType == FormatTools.INT32)
      returnValues[4] = 5;
    else if (pixelType == FormatTools.FLOAT)
      returnValues[4] = 6;
    else if (pixelType == FormatTools.DOUBLE)
      returnValues[4] = 7;
    else
      returnValues[4] = 8;

    // return these
    returnValues[5] = reader.getSizeX();
    returnValues[6] = reader.getSizeY();
    returnValues[7] = reader.getSizeZ();
    returnValues[8] = reader.getSizeT();
    returnValues[9] = reader.getSizeC();
    returnValues[10] = reader.getEffectiveSizeC();
    returnValues[11] = reader.getRGBChannelCount();
    returnValues[12] = reader.getImageCount();

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
