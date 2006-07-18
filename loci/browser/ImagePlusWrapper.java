//
// ImagePlusWrapper.java
//

package loci.browser;

import ij.*;
import ij.process.*;
import java.awt.image.*;
import loci.formats.*;

public class ImagePlusWrapper {

  protected ImagePlus imp;
  protected int sizeX, sizeY, sizeZ, sizeT, sizeC;
  protected String dim;
  private int numTotal; // total number of images (across all stitched files)
  private int numSingle;

  /**
   * Constructor.
   * @param name file name, or any one
   *   of the file names if you use file stitching
   * @param virtual false if use file stitching
   */
  public ImagePlusWrapper(String name, boolean virtual) throws
    java.io.IOException, FormatException
  {
    loci.formats.ImageReader reader = new ImageReader();
    loci.formats.FormatReader r = reader.getReader(name);
    ChannelMerger cm = null;
    if (virtual) cm = new ChannelMerger(new FileStitcher(r));
    else  cm = new ChannelMerger(r);

    // get # images in all matching files
    numTotal = reader.getTotalImageCount(name); // gives wrong number!

    dim = reader.getDimensionOrder(name);
    sizeX = reader.getSizeX(name);
    sizeY = reader.getSizeY(name);
    sizeZ = reader.getSizeZ(name);
    sizeT = reader.getSizeT(name);
    sizeC = reader.getSizeC(name);
    numSingle = reader.getImageCount(name);
    System.err.println("numSingle = "+numSingle);
    System.err.println("numTotal = "+numTotal);
    if (!virtual) {
      int num = reader.getImageCount(name);
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
        BufferedImage img = cm.openStitchedImage(name, i);
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
              if (virtual) stackB = new VirtualStack(w,h);
              else stackB = new ImageStack(w,h);
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
              if (virtual) stackS = new VirtualStack(w, h);
              else stackS = new ImageStack(w, h);
              stackS.addSlice(name + ":" + (i + 1), ip);
            }
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
              if (virtual) stackF = new VirtualStack(w, h);
              stackF = new ImageStack(w, h);
            }
            stackF.addSlice(name + ":" + (i + 1), ip);
          }
        }
        if (ip == null) {
          ip = new ImagePlus(null, img).getProcessor(); // slow
          if (stackO == null) {
            if (virtual) stackO = new VirtualStack(w,h);
            else stackO = new ImageStack(w, h);
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

  public int getNumSingle() { return numSingle; }

}

