//
// ICSWriter.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.out;

import java.awt.Image;
import java.awt.image.*;
import java.io.*;
import loci.formats.*;
import loci.formats.meta.MetadataRetrieve;

/**
 * ICSWriter is the file format writer for ICS files.  It writes ICS version 2
 * files only.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/out/ICSWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/out/ICSWriter.java">SVN</a></dd></dl>
 */
public class ICSWriter extends FormatWriter {

  // -- Fields --

  private RandomAccessFile out;
  private boolean initialized = false;

  // -- Constructor --

  public ICSWriter() { super("Image Cytometry Standard", "ics"); }

  // -- IFormatWriter API methods --

  /* @see loci.formats.IFormatWriter#saveImage(Image, boolean) */
  public void saveImage(Image image, boolean last)
    throws FormatException, IOException
  {
    saveImage(image, 0, last, last);
  }

  /* @see loci.formats.IFormatWriter#saveImage(Image, int, boolean, boolean) */
  public void saveImage(Image image, int series, boolean lastInSeries,
    boolean last) throws FormatException, IOException
  {
    if (image == null) {
      throw new FormatException("Image is null");
    }
    BufferedImage img = null;
    if (cm != null) img = ImageTools.makeBuffered(image, cm);
    else img = ImageTools.makeBuffered(image);
    byte[][] byteData = ImageTools.getPixelBytes(img, false);
    int bytesPerPixel =
      FormatTools.getBytesPerPixel(ImageTools.getPixelType(img));

    if (!initialized) {
      initialized = true;
      out = new RandomAccessFile(currentId, "rw");
      out.writeBytes("\t\n");
      out.writeBytes("ics_version\t2.0\n");
      out.writeBytes("filename\t" + currentId + "\n");
      out.writeBytes("layout\tparameters\t6\n");

      MetadataRetrieve meta = getMetadataRetrieve();
      if (meta == null) {
        throw new FormatException("MetadataRetrieve is null.  " +
          "Call setMetadataRetrieve(MetadataRetrieve) before calling " +
          "saveImage(Image, boolean).");
      }

      String order = meta.getPixelsDimensionOrder(series, 0);
      int x = meta.getPixelsSizeX(series, 0).intValue();
      int y = meta.getPixelsSizeY(series, 0).intValue();
      int z = meta.getPixelsSizeZ(series, 0).intValue();
      int c = meta.getPixelsSizeC(series, 0).intValue();
      int t = meta.getPixelsSizeT(series, 0).intValue();
      int pixelType =
        FormatTools.pixelTypeFromString(meta.getPixelsPixelType(series, 0));

      StringBuffer dimOrder = new StringBuffer();
      int[] sizes = new int[6];
      int nextSize = 0;
      sizes[nextSize++] = 8 * FormatTools.getBytesPerPixel(pixelType);

      if (byteData.length > 1) {
        dimOrder.append("ch\t");
        sizes[nextSize++] = c;
      }


      for (int i=0; i<order.length(); i++) {
        if (order.charAt(i) == 'C' && byteData.length == 1) {
          dimOrder.append("ch");
          sizes[nextSize++] = c;
        }
        else {
          if (order.charAt(i) == 'X') sizes[nextSize++] = x;
          else if (order.charAt(i) == 'Y') sizes[nextSize++] = y;
          else if (order.charAt(i) == 'Z') sizes[nextSize++] = z;
          else if (order.charAt(i) == 'T') sizes[nextSize++] = t;
          dimOrder.append(String.valueOf(order.charAt(i)).toLowerCase());
        }
        dimOrder.append("\t");
      }
      out.writeBytes("layout\torder\tbits\t" + dimOrder.toString() + "\n");
      out.writeBytes("layout\tsizes\t");
      for (int i=0; i<sizes.length; i++) {
        out.writeBytes(sizes[i] + "\t");
        if (i == sizes.length - 1) out.writeBytes("\n");
      }

      boolean signed = pixelType == FormatTools.INT8 ||
        pixelType == FormatTools.INT16 || pixelType == FormatTools.INT32;

      out.writeBytes("representation\tformat\t" +
        (pixelType == FormatTools.FLOAT ? "real\n" : "integer\n"));
      out.writeBytes("representation\tsign\t" +
        (signed ? "signed\n" : "unsigned\n"));
      out.writeBytes("representation\tcompression\tuncompressed\n");
      out.writeBytes("representation\tbyte_order\t");
      for (int i=0; i<sizes[0]/8; i++) {
        out.writeBytes(((sizes[0] / 8) - i) + "\t");
      }
      out.writeBytes("\nend\n");
    }

    if (byteData.length == 1) out.write(byteData[0]);
    else {
      for (int pixel=0; pixel<byteData[0].length/bytesPerPixel; pixel++) {
        for (int channel=0; channel<byteData.length; channel++) {
          out.write(byteData[channel], pixel*bytesPerPixel, bytesPerPixel);
        }
      }
    }

    if (last) close();
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
  public boolean canDoStacks() { return true; }

  /* @see loci.formats.IFormatWriter#getPixelTypes() */
  public int[] getPixelTypes() {
    return new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,
      FormatTools.UINT16, FormatTools.INT32, FormatTools.UINT32,
      FormatTools.FLOAT};
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    if (out != null) out.close();
    out = null;
    currentId = null;
    initialized = false;
  }

}
