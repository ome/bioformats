//
// Util.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
4D Data Browser, Bio-Formats Importer, Bio-Formats Exporter and OME plugins.
Copyright (C) 2006-@year@ Melissa Linkert, Christopher Peterson,
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
import ij.process.*;
import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import loci.formats.*;
import loci.formats.codec.LuraWaveCodec;

/**
 * Miscellaneous generally useful utility methods.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/Util.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/Util.java">SVN</a></dd></dl>
 */
public final class Util {

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
    // read byte array
    boolean first = true;
    byte[] b = null;
    while (true) {
      // read LuraWave license code, if available
      String code = Prefs.get(LuraWaveCodec.LICENSE_PROPERTY, null);
      if (code != null) {
        System.setProperty(LuraWaveCodec.LICENSE_PROPERTY, code);
      }
      try {
        b = r.openBytes(no);
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

    int w = r.getSizeX();
    int h = r.getSizeY();
    int c = r.getRGBChannelCount();
    int type = r.getPixelType();
    int bpp = FormatTools.getBytesPerPixel(type);

    boolean signed = type == FormatTools.INT8 || type == FormatTools.INT16 ||
      type == FormatTools.INT32;

    if (b.length != w * h * c * bpp && b.length != w * h * bpp) {
      // HACK - byte array dimensions are incorrect - image is probably
      // a different size, but we have no way of knowing what size;
      // so open this plane as a BufferedImage to find out
      BufferedImage bi = r.openImage(no);
      b = ImageTools.padImage(b, r.isInterleaved(), c,
        bi.getWidth() * bpp, w, h);
    }

    // convert byte array to appropriate primitive array type
    Object pixels = DataTools.makeDataArray(b, bpp,
      type == FormatTools.FLOAT || type == FormatTools.DOUBLE,
      r.isLittleEndian());

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

      if (signed) {
        for (int i=0; i<q.length; i++) {
          q[i] = (byte) (q[i] - 128);
        }
      }

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

      if (signed) {
        for (int i=0; i<q.length; i++) {
          q[i] = (short) (q[i] - 32768);
        }
      }

      ip = new ShortProcessor(w, h, q, model);
    }
    else if (pixels instanceof int[]) {
      int[] q = (int[]) pixels;
      if (q.length > w * h) {
        int[] tmp = q;
        q = new int[w * h];
        System.arraycopy(tmp, 0, q, 0, q.length);
      }

      if (signed) {
        for (int i=0; i<q.length; i++) {
          q[i] = (int) (q[i] - 2147483648L);
        }
      }

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

}
