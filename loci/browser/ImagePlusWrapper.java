//
// ImagePlusWrapper.java
//

/*
LOCI 4D Data Browser package for quick browsing of 4D datasets in ImageJ.
Copyright (C) 2005-2006 Francis Wong, Curtis Rueden and Melissa Linkert.

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

package loci.browser;

import ij.*;
import ij.process.*;
import java.awt.image.*;
import loci.formats.*;

public class ImagePlusWrapper {

  protected ImagePlus imp;
  protected int sizeX, sizeY, sizeZ, sizeT, sizeC;
  protected String dim;
  protected MetadataStore store;
  private int numTotal; // total number of images (across all stitched files)

  /**
   * Constructor.
   * @param name file name, or any one
   *   of the file names if you use file stitching
   * @param stitch true if use file stitching
   */
  public ImagePlusWrapper(String name, boolean stitch) throws
    java.io.IOException, FormatException
  {
    FormatReader r = (FormatReader) LociDataBrowser.reader.getReader(name);
    try {
      OMEXMLMetadataStore s = new OMEXMLMetadataStore();
      s.createRoot();
      r.setMetadataStore(s);
    }
    catch (Exception e) { }
    ChannelMerger cm = null;
    if (stitch) cm = new ChannelMerger(new FileStitcher(r));
    else cm = new ChannelMerger(r);

    // get # images in all matching files
    numTotal = cm.getImageCount(name);

    dim = cm.getDimensionOrder(name);
    sizeX = cm.getSizeX(name);
    sizeY = cm.getSizeY(name);
    sizeZ = cm.getSizeZ(name);
    sizeT = cm.getSizeT(name);
    sizeC = cm.getSizeC(name);
    if (LociDataBrowser.DEBUG) {
      System.err.println("numTotal = "+numTotal);
    }

    int num = cm.getImageCount(name);
    ImageStack stackB = null, stackS = null,
      stackF = null, stackO = null;
    long start = System.currentTimeMillis();
    long time = start;
    for (int i=0; i<num; i++) {
      long clock = System.currentTimeMillis();
      if (clock - time >= 50) {
        IJ.showStatus("Reading plane "+(i+1)+"/"+num);
        time = clock;
      }
      IJ.showProgress((double)i/num);
      BufferedImage img = cm.openImage(name, i);
      if (img.getWidth() != sizeX || img.getHeight() != sizeY) {
        try {
          img = ImageTools.scale(img, sizeX, sizeY);
        }
        catch (Exception e) {
        }
      }

      ImageProcessor ip = null;
      WritableRaster raster = img.getRaster();
      int c = raster.getNumBands();
      int tt = raster.getTransferType();
      int w = img.getWidth(), h = img.getHeight();
      if (c == 1) {
        if (tt == DataBuffer.TYPE_BYTE) {
          byte[] b = ImageTools.getBytes(img)[0];
          if (b.length > w*h) {
            byte[] tmp = b;
            b = new byte[w*h];
            System.arraycopy(tmp,0,b,0,b.length);
          }
          ip = new ByteProcessor (w,h,b,null);
          if (stackB == null) {
            stackB = new ImageStack(w,h);
          }
          stackB.addSlice(name + ":" + (i + 1), ip);
        }
        else if (tt == DataBuffer.TYPE_USHORT) {
          short[] s = ImageTools.getShorts(img)[0];
          if (s.length > w*h) {
            short[] tmp = s;
            s = new short[w*h];
            System.arraycopy(tmp, 0, s, 0, s.length);
          }
          ip = new ShortProcessor(w, h, s, null);
          if (stackS == null) {
            stackS = new ImageStack(w, h);
          }
          stackS.addSlice(name + ":" + (i + 1), ip);
        }
        else if (tt == DataBuffer.TYPE_FLOAT) {
          float[] f = ImageTools.getFloats(img)[0];
          if (f.length > w*h) {
            float[] tmp = f;
            f = new float[w*h];
            System.arraycopy(tmp, 0, f, 0, f.length);
          }
          ip = new FloatProcessor(w, h, f, null);
          if (stackF == null) {
            stackF = new ImageStack(w, h);
          }
          stackF.addSlice(name + ":" + (i + 1), ip);
        }
      }
      if (ip == null) {
        ip = new ImagePlus(name, img).getProcessor(); // slow
        if (stackO == null) {
          stackO = new ImageStack(w, h);
        }
        stackO.addSlice(name + ":" + (i + 1), ip);
      }
    }
    IJ.showStatus("Creating image");
    IJ.showProgress(1);
    if (stackB != null) {
      imp = new ImagePlus(name, stackB);
    }
    if (stackS != null) {
      imp = new ImagePlus(name, stackS);
    }
    if (stackF != null) {
      imp = new ImagePlus(name, stackF);
    }
    if (stackO != null) {
      imp = new ImagePlus(name, stackO);
    }
    long end = System.currentTimeMillis();
    double elapsed = (end - start) / 1000.0;
    if (num == 1) IJ.showStatus(elapsed + " seconds");
    else {
      long average = (end - start) / num;
      IJ.showStatus("LOCI Bio-Formats : " + elapsed + " seconds (" +
        average + " ms per plane)");
    }

    store = cm.getMetadataStore(name);
  }

  public ImagePlus getImagePlus() { return imp; }

  public int getNumTotal() { return numTotal; }

}

