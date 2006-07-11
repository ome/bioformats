package loci.browser;

import java.awt.image.*;
import ij.ImageStack;
import ij.IJ;
import ij.process.*;
import ij.ImagePlus;
import loci.formats.*;



public class ImagePlusWrapper {
    ImagePlus imp;
    public ImagePlusWrapper(String name) throws Exception {
      loci.formats.ImageReader reader = new ImageReader();
      loci.formats.FormatReader r = reader.getReader(name);
      ChannelMerger cm = new ChannelMerger(r);
      int num = cm.getTotalImageCount(name);
      ImageStack stackB = null, stackS = null,
        stackF = null, stackO = null;
      long start = System.currentTimeMillis();
      long time = start;
      int channels = cm.getSizeC(name);

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
          if (stackB == null) stackB = new ImageStack(w,h);
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
          if (stackS == null) stackS = new ImageStack(w, h);
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
          if (stackF == null) stackF = new ImageStack(w, h);
          stackF.addSlice(name + ":" + (i + 1), ip);
        }
      }
      if (ip == null) {
        ip = new ImagePlus(null, img).getProcessor(); // slow
        if (stackO == null) stackO = new ImageStack(w, h);
        stackO.addSlice(name + ":" + (i + 1), ip);
      }
    }
    IJ.showStatus("Creating image");
    IJ.showProgress(1);
    if (stackB != null) imp = new ImagePlus(name, stackB);
    if (stackS != null) imp = new ImagePlus(name, stackS);
    if (stackF != null) imp = new ImagePlus(name, stackF);
    if (stackO != null) imp = new ImagePlus(name, stackO);
    long end = System.currentTimeMillis();
    double elapsed = (end - start) / 1000.0;
    if (num == 1) IJ.showStatus(elapsed + " seconds");
    else {
      long average = (end - start) / num;
      IJ.showStatus("LOCI Bio-Formats : " + elapsed + " seconds (" +
        average + " ms per plane)");
    }
  }

  public ImagePlus getImagePlus() { return imp; }

}
