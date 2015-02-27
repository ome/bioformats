/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.tests;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import loci.formats.gui.AWTImageTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class for testing the {@link loci.formats.gui.AWTImageTools#makeImage}
 * methods.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/test-suite/src/loci/tests/ImageTester.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/test-suite/src/loci/tests/ImageTester.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ImageTester extends JPanel implements WindowListener {

  // -- Constants --

  private static final Logger LOGGER =
    LoggerFactory.getLogger(ImageTester.class);

  // -- Fields --

  private BufferedImage[][] img;

  // -- Constructor --

  public ImageTester(BufferedImage[][] img) { this.img = img; }

  // -- Component API methods --

  public void paint(Graphics g) {
    Dimension size = getSize();
    g.setColor(Color.black);
    g.fillRect(0, 0, size.width, size.height);
    int xoff = 0, yoff = 0;
    for (int y=0; y<img.length; y++) {
      int maxh = 0;
      for (int x=0; x<img[y].length; x++) {
        int w = img[y][x].getWidth(), h = img[y][x].getHeight();
        g.drawImage(img[y][x], xoff, yoff, this);
        xoff += w;
        if (h > maxh) maxh = h;
      }
      xoff = 0;
      yoff += maxh;
    }
  }

  public Dimension getPreferredSize() {
    int width = 0, height = 0;
    for (int y=0; y<img.length; y++) {
      int wsum = 0;
      int maxh = 0;
      for (int x=0; x<img[y].length; x++) {
        int w = img[y][x].getWidth(), h = img[y][x].getHeight();
        wsum += w;
        if (h > maxh) maxh = h;
      }
      if (wsum > width) width = wsum;
      height += maxh;
    }
    return new Dimension(width, height);
  }

  // -- WindowListener API methods --

  public void windowActivated(WindowEvent e) { }
  public void windowClosed(WindowEvent e) { }
  public void windowClosing(WindowEvent e) { System.exit(0); }
  public void windowDeactivated(WindowEvent e) { }
  public void windowDeiconified(WindowEvent e) { }
  public void windowIconified(WindowEvent e) { }
  public void windowOpened(WindowEvent e) { }

  // -- Main method --

  public static void main(String[] args) {
    int[] chan = {1, 3, 4};
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    int wpad = 50, hpad = 100;
    int w = (screen.width - wpad) / chan.length;
    int h = (screen.height - hpad) / 6; //9;
    LOGGER.info("Using images of size {} x {}", w, h);
    int size = w * h;
    BufferedImage[] bimg1 = new BufferedImage[chan.length];
    BufferedImage[] bimg2 = new BufferedImage[chan.length];
    BufferedImage[] bimg3 = new BufferedImage[chan.length];
    BufferedImage[] simg1 = new BufferedImage[chan.length];
    BufferedImage[] simg2 = new BufferedImage[chan.length];
    BufferedImage[] simg3 = new BufferedImage[chan.length];
    // NB: comment out integer-based images for now;
    // these images can be created, but take a long time
    // to paint, and do not appear as expected
//    BufferedImage[] iimg1 = new BufferedImage[chan.length];
//    BufferedImage[] iimg2 = new BufferedImage[chan.length];
//    BufferedImage[] iimg3 = new BufferedImage[chan.length];
    for (int q=0; q<chan.length; q++) {
      int c = chan[q];
      LOGGER.info("Building c={} images", c);
      byte[][] bdata1 = new byte[c][size];
      byte[] bdata2 = new byte[c * size];
      byte[] bdata3 = new byte[c * size];
      short[][] sdata1 = new short[c][size];
      short[] sdata2 = new short[c * size];
      short[] sdata3 = new short[c * size];
//      int[][] idata1 = new int[c][size];
//      int[] idata2 = new int[c * size];
//      int[] idata3 = new int[c * size];
      for (int i=0; i<c; i++) {
        for (int y=0; y<h; y++) {
          for (int x=0; x<w; x++) {
            int ndx1 = w * y + x;
            int ndx2 = c * ndx1 + i;
            int ndx3 = i * size + ndx1;
            float val;
            if (i == 0) val = (float) (x + y) / (w + h);
            else if (i == 1) val = (float) (x - y + h - 1) / (w + h);
            else if (i == 2) val = (float) (y - x + w - 1) / (w + h);
            else if (i == 3) val = (float) (x * y) / size;
            else val = (float) Math.random();
            int bval = (int) (256 * val);
            int sval = (int) (65536 * val);
//            long ival = (long) (4294967296L * val - 2147483648L);
            bdata1[i][ndx1] = (byte) bval;
            bdata2[ndx2] = (byte) bval;
            bdata3[ndx3] = (byte) bval;
            sdata1[i][ndx1] = (short) sval;
            sdata2[ndx2] = (short) sval;
            sdata3[ndx3] = (short) sval;
//            idata1[i][ndx1] = (int) ival;
//            idata2[ndx2] = (int) ival;
//            idata3[ndx3] = (int) ival;
          }
        }
      }
      boolean signedBytes = false;
      bimg1[q] = AWTImageTools.makeImage(bdata1, w, h, signedBytes);
      bimg2[q] = AWTImageTools.makeImage(bdata2, w, h, c, true, signedBytes);
      bimg3[q] = AWTImageTools.makeImage(bdata3, w, h, c, false, signedBytes);
      boolean signedShorts = false;
      simg1[q] = AWTImageTools.makeImage(sdata1, w, h, signedShorts);
      simg2[q] = AWTImageTools.makeImage(sdata2, w, h, c, true, signedShorts);
      simg3[q] = AWTImageTools.makeImage(sdata3, w, h, c, false, signedShorts);
//      boolean signedInts = true;
//      iimg1[q] = AWTImageTools.makeImage(idata1, w, h, signedInts);
//      iimg2[q] = AWTImageTools.makeImage(idata2, w, h, c, true, signedInts);
//      iimg3[q] = AWTImageTools.makeImage(idata3, w, h, c, false, signedInts);
    }

    LOGGER.info(
      "Rows are: byte[][], byte[] (interleaved), byte[] (sequential)");
    LOGGER.info("  short[][], short[] (interleaved), short[] (sequential)");
//    LOGGER.info("  int[][], int[] (interleaved), int[] (sequential)\n");

    // TODO: This should use a second LOGGER2 instance if a different
    // layout is needed.
    LOGGER.info("Columns are:");
    for (int q=0; q<chan.length; q++) {
      if (q > 0) LOGGER.info(",");
      LOGGER.info(" c={}", chan[q]);
    }
    // END USE OF LOGGER2
    LOGGER.info("");

    JFrame frame = new JFrame("ImageTester");
    BufferedImage[][] img = {
      bimg1, bimg2, bimg3,
      simg1, simg2, simg3,
//      iimg1, iimg2, iimg3
    };
    ImageTester pane = new ImageTester(img);
    frame.addWindowListener(pane);
    frame.setContentPane(pane);
    frame.pack();
    frame.setVisible(true);
  }

}
