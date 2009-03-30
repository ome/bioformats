//
// Util.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

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

package loci.plugins;

import ij.*;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import ij.process.*;
import ij.text.TextWindow;
import ij.util.Tools;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import loci.common.*;
import loci.formats.*;
import loci.formats.codec.LuraWaveCodec;
import loci.formats.in.*;
import loci.formats.meta.MetadataRetrieve;

/**
 * Miscellaneous generally useful utility methods.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/Util.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/Util.java">SVN</a></dd></dl>
 */
public final class Util {

  // -- Constants --

  /** Message to be displayed if we try to use CompositeImage with IJ < 1.39l */
  public static final String COMPOSITE_MSG =
    "ImageJ 1.39l or later is required to merge >8 bit or >3 channel data";

  public static final String PREF_READER_ENABLED = "bioformats.enabled";
  public static final String PREF_READER_WINDOWLESS = "bioformats.windowless";

  public static final String PREF_ND2_NIKON = "bioformats.nd2.nikon";
  public static final String PREF_PICT_QTJAVA = "bioformats.pict.qtjava";
  public static final String PREF_QT_QTJAVA = "bioformats.qt.qtjava";
  public static final String PREF_SDT_INTENSITY = "bioformats.sdt.intensity";

  public static final String VERSION = "4.0.0";

  public static final String REGISTRY = "http://upgrade.openmicroscopy.org.uk";

  public static final String[] REGISTRY_PROPERTIES = new String[] {
    "version", "os.name", "os.version", "os.arch",
    "java.runtime.version", "java.vm.vendor"
  };

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
  public static ImageProcessor[] openProcessors(IFormatReader r, int no)
    throws FormatException, IOException
  {
    return openProcessors(r, no, null);
  }

  /**
   * Returns an array of ImageProcessors that represent the given slice.
   * There is one ImageProcessor per RGB channel, i.e.
   * length of returned array == r.getRGBChannelCount().
   *
   * @param r Format reader to use for reading the data.
   * @param no Position of image plane.
   * @param crop Image cropping specifications, or null if no cropping
   *   is to be done.
   */
  public static ImageProcessor[] openProcessors(IFormatReader r, int no,
    Rectangle crop) throws FormatException, IOException
  {
    // read byte array
    byte[] b = null;
    boolean first = true;
    if (crop == null) crop = new Rectangle(0, 0, r.getSizeX(), r.getSizeY());
    while (true) {
      // read LuraWave license code, if available
      String code = Prefs.get(LuraWaveCodec.LICENSE_PROPERTY, null);
      if (code != null) {
        System.setProperty(LuraWaveCodec.LICENSE_PROPERTY, code);
      }
      try {
        b = r.openBytes(no, crop.x, crop.y, crop.width, crop.height);
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

    int w = crop.width;
    int h = crop.height;
    int c = r.getRGBChannelCount();
    int type = r.getPixelType();
    int bpp = FormatTools.getBytesPerPixel(type);
    boolean interleave = r.isInterleaved();

    if (b.length != w * h * c * bpp && b.length != w * h * bpp) {
      // HACK - byte array dimensions are incorrect - image is probably
      // a different size, but we have no way of knowing what size;
      // so open this plane as a BufferedImage to find out
      BufferedImage bi =
        r.openImage(no, crop.x, crop.y, crop.width, crop.height);
      b = ImageTools.padImage(b, r.isInterleaved(), c,
        bi.getWidth() * bpp, w, h);
    }

    // convert byte array to appropriate primitive array type
    boolean isFloat = type == FormatTools.FLOAT || type == FormatTools.DOUBLE;
    boolean isLittle = r.isLittleEndian();
    boolean isSigned = type == FormatTools.INT8 || type == FormatTools.INT16 ||
      type == FormatTools.INT32;

    IndexColorModel cm = null;
    Index16ColorModel model = null;
    if (r.isIndexed()) {
      byte[][] byteTable = r.get8BitLookupTable();
      if (byteTable != null) {
        cm = new IndexColorModel(8, byteTable[0].length, byteTable[0],
          byteTable[1], byteTable[2]);
      }
    }

    // construct image processors
    ImageProcessor[] ip = new ImageProcessor[c];
    for (int i=0; i<c; i++) {
      byte[] channel =
        ImageTools.splitChannels(b, i, c, bpp, false, interleave);
      Object pixels = DataTools.makeDataArray(channel, bpp, isFloat, isLittle);
      if (pixels instanceof byte[]) {
        byte[] q = (byte[]) pixels;
        if (q.length > w * h) {
          byte[] tmp = q;
          q = new byte[w * h];
          System.arraycopy(tmp, 0, q, 0, q.length);
        }
        if (isSigned) q = DataTools.makeSigned(q);

        ip[i] = new ByteProcessor(w, h, q, null);
        if (cm != null) ip[i].setColorModel(cm);
      }
      else if (pixels instanceof short[]) {
        short[] q = (short[]) pixels;
        if (q.length > w * h) {
          short[] tmp = q;
          q = new short[w * h];
          System.arraycopy(tmp, 0, q, 0, q.length);
        }
        if (isSigned) q = DataTools.makeSigned(q);

        ip[i] = new ShortProcessor(w, h, q, model);
      }
      else if (pixels instanceof int[]) {
        int[] q = (int[]) pixels;
        if (q.length > w * h) {
          int[] tmp = q;
          q = new int[w * h];
          System.arraycopy(tmp, 0, q, 0, q.length);
        }

        ip[i] = new FloatProcessor(w, h, q);
      }
      else if (pixels instanceof float[]) {
        float[] q = (float[]) pixels;
        if (q.length > w * h) {
          float[] tmp = q;
          q = new float[w * h];
          System.arraycopy(tmp, 0, q, 0, q.length);
        }
        ip[i] = new FloatProcessor(w, h, q, null);
      }
      else if (pixels instanceof double[]) {
        double[] q = (double[]) pixels;
        if (q.length > w * h) {
          double[] tmp = q;
          q = new double[w * h];
          System.arraycopy(tmp, 0, q, 0, q.length);
        }
        ip[i] = new FloatProcessor(w, h, q);
      }
    }

    return ip;
  }

  /**
   * Converts the given array of ImageProcessors into a single-slice
   * RGB ImagePlus.
   */
  public static ImagePlus makeRGB(ImageProcessor[] p) {
    return makeRGB("", p);
  }

  /**
   * Converts the given array of ImageProcessors into a single-slice
   * RGB ImagePlus.
   */
  public static ImagePlus makeRGB(String title, ImageProcessor[] p) {
    if (p.length == 1) return new ImagePlus(title, p[0]);

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
      imp = new ImagePlus(title, cp);
    }
    else if (p.length <= 7 && Util.checkVersion("1.39l", COMPOSITE_MSG)) {
      ImageStack tmpStack = new ImageStack(width, height);
      for (int i=0; i<p.length; i++) {
        tmpStack.addSlice("", p[i]);
      }
      try {
        ReflectedUniverse r = new ReflectedUniverse();
        r.exec("import ij.CompositeImage");
        ImagePlus ii = new ImagePlus(title, tmpStack);
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
    double xcal = Double.NaN, ycal = Double.NaN;
    double zcal = Double.NaN, tcal = Double.NaN;

    Float xf = retrieve.getDimensionsPhysicalSizeX(series, 0);
    if (xf != null) xcal = xf.floatValue();
    Float yf = retrieve.getDimensionsPhysicalSizeY(series, 0);
    if (yf != null) ycal = yf.floatValue();
    Float zf = retrieve.getDimensionsPhysicalSizeZ(series, 0);
    if (zf != null) zcal = zf.floatValue();
    Float tf = retrieve.getDimensionsTimeIncrement(series, 0);
    if (tf != null) tcal = tf.floatValue();

    if (xcal == xcal || ycal == ycal || zcal == zcal || tcal == tcal) {
      // if the physical width or physical height are missing, assume that
      // the width and height are equal
      if (xcal != xcal) xcal = ycal;
      if (ycal != ycal) ycal = xcal;

      Calibration cal = new Calibration();
      cal.setUnit("micron");
      cal.pixelWidth = xcal;
      cal.pixelHeight = ycal;
      cal.pixelDepth = zcal;
      cal.frameInterval = tcal;
      imp.setCalibration(cal);
    }

    String type = retrieve.getPixelsPixelType(series, 0);
    int pixelType = FormatTools.pixelTypeFromString(type);

    boolean signed = pixelType == FormatTools.INT8 ||
      pixelType == FormatTools.INT16 || pixelType == FormatTools.INT32;

    // set calibration function, so that both signed and unsigned pixel
    // values are shown
    if (signed) {
      int bitsPerPixel = FormatTools.getBytesPerPixel(pixelType) * 8;
      double min = -1 * Math.pow(2, bitsPerPixel - 1);
      imp.getLocalCalibration().setFunction(Calibration.STRAIGHT_LINE,
        new double[] {min, 1.0}, "gray value");
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

  /** Reorder the given ImagePlus's stack. */
  public static ImagePlus reorder(ImagePlus imp, String origOrder,
    String newOrder)
  {
    ImageStack s = imp.getStack();
    ImageStack newStack = new ImageStack(s.getWidth(), s.getHeight());

    int z = imp.getNSlices();
    int c = imp.getNChannels();
    int t = imp.getNFrames();

    int stackSize = s.getSize();
    for (int i=0; i<stackSize; i++) {
      /*
      int[] target =
        FormatTools.getZCTCoords(newOrder, z, c, t, stackSize, i);
      int ndx = FormatTools.getIndex(origOrder, z, c, t, stackSize,
        target[0], target[1], target[2]);
      */
      int ndx = FormatTools.getReorderedIndex(
        origOrder, newOrder, z, c, t, stackSize, i);
      newStack.addSlice(s.getSliceLabel(ndx + 1), s.getProcessor(ndx + 1));
    }
    ImagePlus p = new ImagePlus(imp.getTitle(), newStack);
    p.setDimensions(c, z, t);
    p.setCalibration(imp.getCalibration());
    p.setFileInfo(imp.getOriginalFileInfo());
    return p;
  }

  /**
   * Creates an image reader according to the current configuration settings,
   * including which format readers are currently enabled, as well as
   * format-specific configuration settings.
   */
  public static ImageReader makeImageReader() {
    // include only enabled classes
    Class[] c = null;
    try {
      ClassList defaultClasses =
        new ClassList("readers.txt", IFormatReader.class);
      c = defaultClasses.getClasses();
    }
    catch (IOException exc) {
      return new ImageReader();
    }
    ClassList enabledClasses = new ClassList(IFormatReader.class);
    for (int i=0; i<c.length; i++) {
      boolean on = getPref(PREF_READER_ENABLED, c[i], true);
      if (on) {
        try {
          enabledClasses.addClass(c[i]);
        }
        catch (FormatException exc) {
          exc.printStackTrace();
        }
      }
    }
    ImageReader reader = new ImageReader(enabledClasses);

    // toggle reader-specific options
    boolean nd2Nikon = Prefs.get(PREF_ND2_NIKON, false);
    boolean pictQTJava = Prefs.get(PREF_PICT_QTJAVA, false);
    boolean qtQTJava = Prefs.get(PREF_QT_QTJAVA, false);
    boolean sdtIntensity = Prefs.get(PREF_SDT_INTENSITY, false);
    IFormatReader[] r = reader.getReaders();
    for (int i=0; i<r.length; i++) {
      if (r[i] instanceof ND2Reader) {
        ND2Reader nd2 = (ND2Reader) r[i];
        nd2.setLegacy(nd2Nikon);
      }
      else if (r[i] instanceof PictReader) {
        PictReader pict = (PictReader) r[i];
        pict.setLegacy(pictQTJava);
      }
      else if (r[i] instanceof QTReader) {
        QTReader qt = (QTReader) r[i];
        qt.setLegacy(qtQTJava);
      }
      else if (r[i] instanceof SDTReader) {
        SDTReader sdt = (SDTReader) r[i];
        sdt.setIntensity(sdtIntensity);
      }
    }

    return reader;
  }

  /**
   * Gets whether windowless mode should be used when opening
   * the given image reader's currently initialized dataset.
   */
  public static boolean isWindowless(IFormatReader reader) {
    return getPref(PREF_READER_WINDOWLESS, reader.getClass(), false);
  }

  /**
   * Places the given window at a nice location on screen, either centered
   * below the ImageJ window if there is one, or else centered on screen.
   */
  public static void placeWindow(Window w) {
    Dimension size = w.getSize();

    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

    ImageJ ij = IJ.getInstance();

    Point p = new Point();

    if (ij == null) {
      // center config window on screen
      p.x = (screen.width - size.width) / 2;
      p.y = (screen.height - size.height) / 2;
    }
    else {
      // place config window below ImageJ window
      Rectangle ijBounds = ij.getBounds();
      p.x = ijBounds.x + (ijBounds.width - size.width) / 2;
      p.y = ijBounds.y + ijBounds.height + 5;
    }

    // nudge config window away from screen edges
    final int pad = 10;
    if (p.x < pad) p.x = pad;
    else if (p.x + size.width + pad > screen.width) {
      p.x = screen.width - size.width - pad;
    }
    if (p.y < pad) p.y = pad;
    else if (p.y + size.height + pad > screen.height) {
      p.y = screen.height - size.height - pad;
    }

    w.setLocation(p);
  }

  /** Reports an exception in an ImageJ text window. */
  public static void reportException(Throwable t) {
    // stolen from IJ.Executor.run()
    IJ.showStatus("");
    IJ.showProgress(1.0);
    CharArrayWriter caw = new CharArrayWriter();
    PrintWriter pw = new PrintWriter(caw);
    t.printStackTrace(pw);
    String s = caw.toString();
    if (IJ.isMacintosh()) {
      if (s.indexOf("ThreadDeath") > 0) return;
      s = Tools.fixNewLines(s);
    }
    if (IJ.getInstance() != null) new TextWindow("Exception", s, 350, 250);
    else IJ.log(s);
  }

  /** Check if a new stable version is available. */
  public static boolean newVersionAvailable() {
    // connect to the registry

    StringBuffer query = new StringBuffer(REGISTRY);
    for (int i=0; i<REGISTRY_PROPERTIES.length; i++) {
      if (i == 0) query.append("?");
      else query.append(";");
      query.append(REGISTRY_PROPERTIES[i]);
      query.append("=");
      if (i == 0) query.append(VERSION);
      else {
        try {
          query.append(URLEncoder.encode(
            System.getProperty(REGISTRY_PROPERTIES[i]), "UTF-8"));
        }
        catch (UnsupportedEncodingException e) { }
      }
    }

    try {
      URLConnection conn = new URL(query.toString()).openConnection();
      conn.setUseCaches(false);
      conn.addRequestProperty("User-Agent", "OMERO.imagej");
      conn.connect();

      // retrieve latest version number from the registry

      InputStream in = conn.getInputStream();
      StringBuffer latestVersion = new StringBuffer();
      while (true) {
        int data = in.read();
        if (data == -1) break;
        latestVersion.append((char) data);
      }
      in.close();

      // check to see if version reported by registry is greater than
      // the current version - version number should be in "x.x.x" format

      String[] version = latestVersion.toString().split("\\.");
      String[] thisVersion = VERSION.split("\\.");
      for (int i=0; i<thisVersion.length; i++) {
        int subVersion = Integer.parseInt(thisVersion[i]);
        try {
          int registrySubVersion = Integer.parseInt(version[i]);
          if (registrySubVersion > subVersion) return true;
          if (registrySubVersion < subVersion) return false;
        }
        catch (NumberFormatException e) {
          return false;
        }
      }
    }
    catch (IOException e) { }
    return false;
  }

  // -- Helper methods --

  private static boolean getPref(String pref, Class c, boolean defaultValue) {
    String n = c.getName();
    String readerName = n.substring(n.lastIndexOf(".") + 1, n.length() - 6);
    String key = pref + "." + readerName;
    return Prefs.get(key, defaultValue);
  }

}
