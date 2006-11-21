//
// ImagePlusWrapper.java
//

/*
LOCI 4D Data Browser plugin for quick browsing of 4D datasets in ImageJ.
Copyright (C) 2005-@year@ Christopher Peterson, Francis Wong, Curtis Rueden
and Melissa Linkert.

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

package loci.plugins.browser;

import ij.*;
import ij.process.*;
import java.awt.image.*;
import java.io.IOException;
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
  public ImagePlusWrapper(String name, IFormatReader r, FileStitcher fs, boolean stitch)
    throws IOException, FormatException
  {
    store = new OMEXMLMetadataStore();
    synchronized (r) {
      try {
        store.createRoot();
        r.setMetadataStore(store);
      }
      catch (Exception e) { e.printStackTrace(); }

      // get # images in all matching files
      
      try {
        numTotal = fs.getImageCount(name);
        
        dim = fs.getDimensionOrder(name);
        sizeX = fs.getSizeX(name);
        sizeY = fs.getSizeY(name);
        sizeZ = fs.getSizeZ(name);
        sizeT = fs.getSizeT(name);
        sizeC = fs.getSizeC(name);
        if (LociDataBrowser.DEBUG) {
          System.err.println("numTotal = "+numTotal);
        }
      }
      catch(Exception exc) { exc.printStackTrace();}

      int num = r.getImageCount(name);
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
        IJ.showProgress((double) i/num);
        BufferedImage img = r.openImage(name, i);

        // scale image if it is the wrong size
        int w = img.getWidth(), h = img.getHeight();
        if (w != sizeX || h != sizeY) {
          System.out.println("Warning: image size " + w + "x" + h +
            " does not match expected size of " + sizeX + "x" + sizeY + ".");
          img = ImageTools.scale(img, sizeX, sizeY, false, false);
          w = sizeX;
          h = sizeY;
        }

        ImageProcessor ip = null;
        WritableRaster raster = img.getRaster();
        int c = raster.getNumBands();
        int tt = raster.getTransferType();
        if (c == 1) {
          if (tt == DataBuffer.TYPE_BYTE) {
            byte[] b = ImageTools.getBytes(img)[0];
            if (b.length > w*h) {
              byte[] tmp = b;
              b = new byte[w*h];
              System.arraycopy(tmp, 0, b, 0, b.length);
            }
            ip = new ByteProcessor(w, h, b, null);
            if (stackB == null) {
              stackB = new ImageStack(w, h);
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
    }
  }

  public ImagePlus getImagePlus() { return imp; }

  public int getNumTotal() { return numTotal; }

  public static ImageProcessor getImageProcessor(
    String name, IFormatReader read, int index)
  {
    BufferedImage img = null;
    String dim = null;
    int sizeX = 0, sizeY = 0, sizeZ = 0, sizeT = 0, sizeC = 0;
    synchronized (read) {
      Exception problem = null;
      try {
        dim = read.getDimensionOrder(name);
        sizeX = read.getSizeX(name);
        sizeY = read.getSizeY(name);
        sizeZ = read.getSizeZ(name);
        sizeT = read.getSizeT(name);
        sizeC = read.getSizeC(name);
      }
      catch (FormatException exc) { problem = exc; }
      catch (IOException exc) { problem = exc; }
      if (problem != null) {
        problem.printStackTrace();
        return null;
      }

      // read image from disk
      try {
        img = read.openImage(name, index);
      }
      catch (FormatException exc) { exc.printStackTrace(); }
      catch (IOException exc) { exc.printStackTrace(); }
    }

    if (img == null) {
      System.out.println("Sorry, but there was a problem reading image '" +
        name + "' at index " + index + " (sizeX=" + sizeX + ", sizeY=" +
        sizeY + ", sizeZ=" + sizeZ + ", sizeT=" + sizeT + ", sizeC=" + sizeC +
        ", dim=" + dim + ").");
      return null;
    }

    // scale image if it is the wrong size
    int w = img.getWidth(), h = img.getHeight();
    if (w != sizeX || h != sizeY) {
      System.out.println("Warning: image size " + w + "x" + h +
        " does not match expected size of " + sizeX + "x" + sizeY + ".");
      img = ImageTools.scale(img, sizeX, sizeY, false, false);
      w = sizeX;
      h = sizeY;
    }

    // convert BufferedImage to ImageProcessor
    ImageProcessor ip = null;
    WritableRaster raster = img.getRaster();
    int c = raster.getNumBands();
    int tt = raster.getTransferType();
    if (c == 1) {
      if (tt == DataBuffer.TYPE_BYTE) {
        byte[] b = ImageTools.getBytes(img)[0];
        if (b.length > w*h) {
          byte[] tmp = b;
          b = new byte[w*h];
          System.arraycopy(tmp, 0, b, 0, b.length);
        }
        ip = new ByteProcessor(w, h, b, null);
      }
      else if (tt == DataBuffer.TYPE_USHORT) {
        short[] s = ImageTools.getShorts(img)[0];
        if (s.length > w*h) {
          short[] tmp = s;
          s = new short[w*h];
          System.arraycopy(tmp, 0, s, 0, s.length);
        }
        ip = new ShortProcessor(w, h, s, null);
      }
      else if (tt == DataBuffer.TYPE_FLOAT) {
        float[] f = ImageTools.getFloats(img)[0];
        if (f.length > w*h) {
          float[] tmp = f;
          f = new float[w*h];
          System.arraycopy(tmp, 0, f, 0, f.length);
        }
        ip = new FloatProcessor(w, h, f, null);
      }
    }
    if (ip == null) {
      ip = new ImagePlus(name, img).getProcessor(); // slow
    }

    return ip;
  }

}
