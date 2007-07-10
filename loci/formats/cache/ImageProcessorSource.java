//
// ImageProcessorSource.java
//

package loci.formats.cache;

import java.awt.image.BufferedImage;
import java.io.IOException;
import loci.formats.*;

/** Retrieves ImageJ image processors from a file, using Bio-Formats. */
public class ImageProcessorSource extends CacheSource {

  // -- Constants -- 
 
  private static final String NO_IJ_MSG =
    "ImageJ is required to use ImageProcessorSource. Please obtain ij.jar" +
    " from http://rsb.info.nih.gov/ij/download.html";

  // -- Static fields --

  private static boolean noIJ = false;
  private static ReflectedUniverse r = createReflectedUniverse();

  private static ReflectedUniverse createReflectedUniverse() {
    r = null;
    try {
      r = new ReflectedUniverse();
      r.exec("import ij.process.ByteProcessor");
      r.exec("import ij.process.ColorProcessor");
      r.exec("import ij.process.FloatProcessor");
      r.exec("import ij.process.ImageProcessor");
      r.exec("import ij.process.ShortProcessor");
    }
    catch (Throwable t) {
      noIJ = true; 
      LogTools.trace(t); 
    }
    return r;
  }

  // -- Fields --

  private IFormatReader reader;

  // -- Constructor --

  public ImageProcessorSource(Object o) throws CacheException {
    super(o); 
    try {
      reader = new FileStitcher();
      reader.setId(o.toString()); 
    }
    catch (FormatException exc) { throw new CacheException(exc); }
    catch (IOException exc) { throw new CacheException(exc); }
  }

  // -- ICacheSource API methods --

  /* @see loci.formats.cache.ICacheSource#getObjectCount() */
  public int getObjectCount() {
    return reader.getImageCount();
  }

  /* @see loci.formats.cache.ICacheSource#getObject(int[], int[]) */
  public Object getObject(int[] len, int[] pos) throws CacheException {
    if (noIJ) throw new CacheException(NO_IJ_MSG);

    int ndx = FormatTools.positionToRaster(len, pos);
  
    try {
      int w = reader.getSizeX();
      int h = reader.getSizeY();
      int c = reader.getRGBChannelCount();
      int type = reader.getPixelType();
      int bpp = FormatTools.getBytesPerPixel(type);

      byte[] b = reader.openBytes(ndx);

      if (b.length != w * h * c * bpp && b.length != w * h * bpp) {
        // HACK - byte array dimensions are incorrect - image is probably
        // a different size, but we have no way of knowing what size;
        // so open this plane as a BufferedImage to find out
        BufferedImage bi = reader.openImage(ndx);
        b = ImageTools.padImage(b, reader.isInterleaved(), c, 
          bi.getWidth() * bpp, w, h);
      }

      // convert byte array to appropriate primitive array type
      Object pixels = DataTools.makeDataArray(b, bpp,
        type == FormatTools.FLOAT || type == FormatTools.DOUBLE,
        reader.isLittleEndian());

      // construct image processor 
      r.setVar("ip", null);
      r.setVar("n", null);
      r.setVar("w", w); 
      r.setVar("h", h); 
    
     if (pixels instanceof byte[]) {
        byte[] q = (byte[]) pixels;
        if (q.length > w * h) {
          byte[] tmp = q;
          q = new byte[w * h];
          System.arraycopy(tmp, 0, q, 0, q.length);
        }
        r.setVar("q", q); 
        r.exec("ip = new ByteProcessor(w, h, q, n)"); 
      }
      else if (pixels instanceof short[]) {
        short[] q = (short[]) pixels;
        if (q.length > w * h) {
          short[] tmp = q;
          q = new short[w * h];
          System.arraycopy(tmp, 0, q, 0, q.length);
        }
        r.setVar("q", q); 
        r.exec("ip = new ShortProcessor(w, h, q, n)"); 
      }
      else if (pixels instanceof int[]) {
        int[] q = (int[]) pixels;
        if (q.length > w * h) {
          int[] tmp = q;
          q = new int[w * h];
          System.arraycopy(tmp, 0, q, 0, q.length);
        }
        r.setVar("q", q); 
        r.exec("ip = new FloatProcessor(w, h, q)"); 
      }
      else if (pixels instanceof float[]) {
        float[] q = (float[]) pixels;
        if (q.length > w * h) {
          float[] tmp = q;
          q = new float[w * h];
          System.arraycopy(tmp, 0, q, 0, q.length);
        }
        if (c == 1) {
          // single channel -- use normal float processor
          r.setVar("q", q); 
          r.exec("ip = new ByteProcessor(w, h, q, n)"); 
        }
        else {
          // multiple channels -- convert floats to color processor
          float[][] pix = new float[c][w * h];
          if (!reader.isInterleaved()) {
            for (int i=0; i<q.length; i+=c) {
              for (int j=0; j<c; j++) pix[j][i / c] = q[i + j];
            }
          }
          else {
            for (int i=0; i<c; i++) {
              System.arraycopy(q, i * pix[i].length, pix[i], 0, pix[i].length);
            }
          }
          byte[][] bytes = new byte[c][w * h];
          for (int i=0; i<c; i++) {
            r.setVar("pix", pix[i]);
            r.setVar("t", true);
            r.exec("ip = new FloatProcessor(w, h, pix, n)");
            r.exec("ip = ip.convertToByte(t)");
            r.exec("bytes = ip.getPixels()");
            bytes[i] = (byte[]) r.getVar("bytes");
          }
          r.exec("ip = new ColorProcessor(w, h)");
          r.setVar("red", bytes[0]); 
          r.setVar("green", bytes[1]); 
          r.setVar("blue", pix.length >= 3 ? bytes[2] : new byte[w * h]); 
          r.exec("ip.setRGB(red, green, blue)"); 
        }
      }
      else if (pixels instanceof double[]) {
        double[] q = (double[]) pixels;
        if (q.length > w * h) {
          double[] tmp = q;
          q = new double[w * h];
          System.arraycopy(tmp, 0, q, 0, q.length);
        }
        r.setVar("q", q); 
        r.exec("ip = new FloatProcessor(w, h, q)"); 
      } 
      return r.getVar("ip"); 
    }
    catch (Exception e) { throw new CacheException(e); }
  }

}
