//
// Util.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
4D Data Browser, Bio-Formats Importer, Bio-Formats Exporter and OME plugins.
Copyright (C) 2005-@year@ Melissa Linkert, Christopher Peterson,
Curtis Rueden, Philip Huettl and Francis Wong.

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

package loci.plugins;

import ij.*;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import ij.process.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import loci.formats.*;
import loci.formats.codec.LuraWaveCodec;
import loci.formats.meta.MetadataRetrieve;

/**
 * Miscellaneous generally useful utility methods.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/Util.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/Util.java">SVN</a></dd></dl>
 */
public final class Util {

  // -- Constants --

  /** Message to be displayed if we try to use CompositeImage with IJ < 1.39l */
  public static final String COMPOSITE_MSG =
    "ImageJ 1.39l or later is required to merge >8 bit or >3 channel data";


  // -- Constructor --

  private Util() { }

  // -- Utility methods --

  /**
   * Creates an ImageJ image processor object using the given format reader
   * for the image plane at the given position.
   *
   * @param r Format reader to use for reading the data.
   * @param no Position of image plane.
   */
  public static ImageProcessor openProcessor(IFormatReader r, int no)
    throws FormatException, IOException
  {
    return openProcessor(r, no, null);
  }

  /**
   * Creates an ImageJ image processor object using the given format reader
   * for the image plane at the given position.
   *
   * @param r Format reader to use for reading the data.
   * @param no Position of image plane.
   * @param crop Image cropping specifications, or null if no cropping
   *   is to be done.
   */
  public static ImageProcessor openProcessor(IFormatReader r, int no,
    Rectangle crop) throws FormatException, IOException
  {
    // read byte array
    byte[] b = null;
    boolean first = true;
    boolean doCrop = crop != null;
    while (true) {
      // read LuraWave license code, if available
      String code = Prefs.get(LuraWaveCodec.LICENSE_PROPERTY, null);
      if (code != null) {
        System.setProperty(LuraWaveCodec.LICENSE_PROPERTY, code);
      }
      try {
        if (doCrop) {
          b = r.openBytes(no, crop.x, crop.y, crop.width, crop.height);
        }
        else b = r.openBytes(no);
        break;
      }
      catch (FormatException exc) {
        String msg = exc.getMessage();
        if (msg != null && (msg.equals(LuraWaveCodec.NO_LICENSE_MSG) ||
          msg.startsWith(LuraWaveCodec.INVALID_LICENSE_MSG)))
        {
          // prompt user for LuraWave license code
          GenericDialog gd = new GenericDialog("LuraWave License Code");
          if (first) first = false;
          else gd.addMessage("Invalid license code; try again.");
          gd.addStringField("LuraWave_License Code: ", code, 16);
          gd.showDialog();
          if (gd.wasCanceled()) return null;
          code = gd.getNextString();
          if (code != null) Prefs.set(LuraWaveCodec.LICENSE_PROPERTY, code);
        }
        else throw exc;
      }
    }

    int w = doCrop ? crop.width : r.getSizeX();
    int h = doCrop ? crop.height : r.getSizeY();
    int c = r.getRGBChannelCount();
    int type = r.getPixelType();
    int bpp = FormatTools.getBytesPerPixel(type);

    boolean isSigned = type == FormatTools.INT8 ||
      type == FormatTools.INT16 || type == FormatTools.INT32;

    if (b.length != w * h * c * bpp && b.length != w * h * bpp) {
      // HACK - byte array dimensions are incorrect - image is probably
      // a different size, but we have no way of knowing what size;
      // so open this plane as a BufferedImage to find out
      BufferedImage bi = doCrop ?
        r.openImage(no, crop.x, crop.y, crop.width, crop.height) :
        r.openImage(no);
      b = ImageTools.padImage(b, r.isInterleaved(), c,
        bi.getWidth() * bpp, w, h);
    }

    // convert byte array to appropriate primitive array type
    boolean isFloat = type == FormatTools.FLOAT || type == FormatTools.DOUBLE;
    boolean isLittle = r.isLittleEndian();
    Object pixels = DataTools.makeDataArray(b, bpp, isFloat, isLittle);

    IndexColorModel cm = null;
    IndexedColorModel model = null;
    if (r.isIndexed()) {
      byte[][] byteTable = r.get8BitLookupTable();
      if (byteTable != null) {
        cm = new IndexColorModel(8, byteTable[0].length, byteTable[0],
          byteTable[1], byteTable[2]);
      }
      else {
        short[][] shortTable = r.get16BitLookupTable();
        model = new IndexedColorModel(16, shortTable[0].length, shortTable);
      }
    }

    // construct image processor
    ImageProcessor ip = null;
    if (pixels instanceof byte[]) {
      byte[] q = (byte[]) pixels;
      if (q.length > w * h) {
        byte[] tmp = q;
        q = new byte[w * h];
        System.arraycopy(tmp, 0, q, 0, q.length);
      }

      if (isSigned) q = DataTools.makeSigned(q);

      ip = new ByteProcessor(w, h, q, null);
      if (cm != null) ip.setColorModel(cm);
    }
    else if (pixels instanceof short[]) {
      short[] q = (short[]) pixels;
      if (q.length > w * h) {
        short[] tmp = q;
        q = new short[w * h];
        System.arraycopy(tmp, 0, q, 0, q.length);
      }

      if (isSigned) q = DataTools.makeSigned(q);

      ip = new ShortProcessor(w, h, q, model);
    }
    else if (pixels instanceof int[]) {
      int[] q = (int[]) pixels;
      if (q.length > w * h) {
        int[] tmp = q;
        q = new int[w * h];
        System.arraycopy(tmp, 0, q, 0, q.length);
      }

      if (isSigned) q = DataTools.makeSigned(q);

      ip = new FloatProcessor(w, h, q);
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
        ip = new FloatProcessor(w, h, q, null);
      }
      else {
        // multiple channels -- convert floats to color processor
        float[][] pix = new float[c][w * h];
        if (!r.isInterleaved()) {
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
          ip = new FloatProcessor(w, h, pix[i], null);
          ip = ip.convertToByte(true);
          bytes[i] = (byte[]) ip.getPixels();
        }
        ip = new ColorProcessor(w, h);
        ((ColorProcessor) ip).setRGB(bytes[0], bytes[1],
          pix.length >= 3 ? bytes[2] : new byte[w * h]);
      }
    }
    else if (pixels instanceof double[]) {
      double[] q = (double[]) pixels;
      if (q.length > w * h) {
        double[] tmp = q;
        q = new double[w * h];
        System.arraycopy(tmp, 0, q, 0, q.length);
      }
      ip = new FloatProcessor(w, h, q);
    }

    return ip;
  }

  /**
   * Converts the given array of ImageProcessors into a single-slice
   * RGB ImagePlus.
   */
  public static ImagePlus makeRGB(ImageProcessor[] p) {
    if (p.length == 1) return new ImagePlus("", p[0]);

    // check that all processors are of the same type and size
    boolean sameType = true;
    int width = p[0].getWidth();
    int height = p[0].getHeight();
    boolean byteProc = p[0] instanceof ByteProcessor;
    boolean shortProc = p[0] instanceof ShortProcessor;
    boolean floatProc = p[0] instanceof FloatProcessor;
    for (int i=1; i<p.length; i++) {
      int w = p[i].getWidth();
      int h = p[i].getHeight();
      boolean b = p[i] instanceof ByteProcessor;
      boolean s = p[i] instanceof ShortProcessor;
      boolean f = p[i] instanceof FloatProcessor;
      if (w != width || h != height || b != byteProc || s != shortProc ||
        f != floatProc)
      {
        sameType = false;
        break;
      }
    }

    if (!sameType || p.length > 4 || p[0] instanceof ColorProcessor) {
      return null;
    }

    ImagePlus imp = null;

    if (p.length < 4 && byteProc) {
      ColorProcessor cp = new ColorProcessor(width, height);
      byte[][] bytes = new byte[p.length][];
      for (int i=0; i<p.length; i++) {
        bytes[i] = (byte[]) p[i].getPixels();
      }
      cp.setRGB(bytes[0], bytes[1], bytes.length == 3 ? bytes[2] :
        new byte[width * height]);
      imp = new ImagePlus("", cp);
    }
    else if (p.length <= 7 && Util.checkVersion("1.39l", COMPOSITE_MSG)) {
      ImageStack tmpStack = new ImageStack(width, height);
      for (int i=0; i<p.length; i++) {
        tmpStack.addSlice("", p[i]);
      }
      try {
        ReflectedUniverse r = new ReflectedUniverse();
        r.exec("import ij.CompositeImage");
        ImagePlus ii = new ImagePlus("", tmpStack);
        r.setVar("ii", ii);
        r.exec("imp = new CompositeImage(ii, CompositeImage.COMPOSITE)");
        imp = (ImagePlus) r.getVar("imp");
      }
      catch (ReflectException e) {
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(s));
        IJ.error(s.toString());
      }
    }

    return imp;
  }

  /** Applies spatial calibrations to an image stack. */
  public static void applyCalibration(MetadataRetrieve retrieve,
    ImagePlus imp, int series)
  {
    double xcal = Double.NaN, ycal = Double.NaN, zcal = Double.NaN;

    Float xf = retrieve.getDimensionsPhysicalSizeX(0, 0);
    if (xf != null) xcal = xf.floatValue();
    Float yf = retrieve.getDimensionsPhysicalSizeY(0, 0);
    if (yf != null) ycal = yf.floatValue();
    Float zf = retrieve.getDimensionsPhysicalSizeZ(0, 0);
    if (zf != null) zcal = zf.floatValue();

    if (xcal == xcal || ycal == ycal || zcal == zcal) {
      Calibration cal = new Calibration();
      cal.setUnit("micron");
      cal.pixelWidth = xcal;
      cal.pixelHeight = ycal;
      cal.pixelDepth = zcal;
      imp.setCalibration(cal);
    }
  }

  /** Adds AWT scroll bars to the given container. */
  public static void addScrollBars(Container pane) {
    GridBagLayout layout = (GridBagLayout) pane.getLayout();

    // extract components
    int count = pane.getComponentCount();
    Component[] c = new Component[count];
    GridBagConstraints[] gbc = new GridBagConstraints[count];
    for (int i=0; i<count; i++) {
      c[i] = pane.getComponent(i);
      gbc[i] = layout.getConstraints(c[i]);
    }

    // clear components
    pane.removeAll();
    layout.invalidateLayout(pane);

    // create new container panel
    Panel newPane = new Panel();
    GridBagLayout newLayout = new GridBagLayout();
    newPane.setLayout(newLayout);
    for (int i=0; i<count; i++) {
      newLayout.setConstraints(c[i], gbc[i]);
      newPane.add(c[i]);
    }

    // HACK - get preferred size for container panel
    // NB: don't know a better way:
    // - newPane.getPreferredSize() doesn't work
    // - newLayout.preferredLayoutSize(newPane) doesn't work
    Frame f = new Frame();
    f.setLayout(new BorderLayout());
    f.add(newPane, BorderLayout.CENTER);
    f.pack();
    final Dimension size = newPane.getSize();
    f.remove(newPane);
    f.dispose();

    // compute best size for scrollable viewport
    size.width += 15;
    size.height += 15;
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    int maxWidth = 3 * screen.width / 4;
    int maxHeight = 3 * screen.height / 4;
    if (size.width > maxWidth) size.width = maxWidth;
    if (size.height > maxHeight) size.height = maxHeight;

    // create scroll pane
    ScrollPane scroll = new ScrollPane() {
      public Dimension getPreferredSize() {
        return size;
      }
    };
    scroll.add(newPane);

    // add scroll pane to original container
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.BOTH;
    constraints.weightx = 1.0;
    constraints.weighty = 1.0;
    layout.setConstraints(scroll, constraints);
    pane.add(scroll);
  }

  /**
   * Adjusts the color range of the given image stack
   * to match its global minimum and maximum.
   */
  public static void adjustColorRange(ImagePlus imp) {
    ImageStack s = imp.getStack();
    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;
    for (int i=0; i<s.getSize(); i++) {
      ImageProcessor p = s.getProcessor(i + 1);
      p.resetMinAndMax();
      if (p.getMin() < min) min = p.getMin();
      if (p.getMax() > max) max = p.getMax();
    }

    ImageProcessor p = imp.getProcessor();
    if (p instanceof ColorProcessor) {
      ((ColorProcessor) p).setMinAndMax(min, max, 3);
    }
    else p.setMinAndMax(min, max);
    imp.setProcessor(imp.getTitle(), p);
  }

  /**
   * Returns true the current ImageJ version is greater than or equal to the
   * specified version.  Displays the given warning if the current version is
   * less than the specified version.
   */
  public static boolean checkVersion(String target, String msg) {
    String current = IJ.getVersion();
    if (current.compareTo(target) < 0) {
      IJ.showMessage(msg);
      return false;
    }
    return true;
  }

  /** Reorder the given ImagePlus' stack. */
  public static ImagePlus reorder(ImagePlus imp, String oldOrder,
    String newOrder)
  {
    ImageStack s = imp.getStack();
    ImageStack newStack = new ImageStack(s.getWidth(), s.getHeight());

    int z = imp.getNSlices();
    int c = imp.getNChannels();
    int t = imp.getNFrames();

    for (int i=0; i<s.getSize(); i++) {
      int[] target =
        FormatTools.getZCTCoords(newOrder, z, c, t, s.getSize(), i);
      int ndx = FormatTools.getIndex(oldOrder, z, c, t, s.getSize(),
        target[0], target[1], target[2]);
      newStack.addSlice(s.getSliceLabel(ndx + 1), s.getProcessor(ndx + 1));
    }
    ImagePlus p = new ImagePlus(imp.getTitle(), newStack);
    p.setDimensions(c, z, t);
    p.setCalibration(imp.getCalibration());
    p.setFileInfo(imp.getOriginalFileInfo());
    return p;
  }

}
