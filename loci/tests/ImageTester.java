//
// ImageTester.java
//

/*
LOCI software manual test suite. Copyright (C) 2007-@year@
Curtis Rueden and Melissa Linkert. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the UW-Madison LOCI nor the names of its
    contributors may be used to endorse or promote products derived from
    this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE UW-MADISON LOCI ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package loci.tests;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import javax.swing.*;
import loci.formats.ImageTools;
import loci.formats.LogTools;

/**
 * A class for testing the {@link loci.formats.ImageTools#makeImage} methods.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/test/ImageTester.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/test/ImageTester.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ImageTester extends JPanel implements WindowListener {

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
    LogTools.println("Using images of size " + w + " x " + h);
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
      LogTools.println("Building c=" + c + " images");
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
      bimg1[q] = ImageTools.makeImage(bdata1, w, h);
      bimg2[q] = ImageTools.makeImage(bdata2, w, h, c, true);
      bimg3[q] = ImageTools.makeImage(bdata3, w, h, c, false);
      simg1[q] = ImageTools.makeImage(sdata1, w, h);
      simg2[q] = ImageTools.makeImage(sdata2, w, h, c, true);
      simg3[q] = ImageTools.makeImage(sdata3, w, h, c, false);
//      iimg1[q] = ImageTools.makeImage(idata1, w, h);
//      iimg2[q] = ImageTools.makeImage(idata2, w, h, c, true);
//      iimg3[q] = ImageTools.makeImage(idata3, w, h, c, false);
    }

    LogTools.println("Rows are: byte[][], byte[] (interleaved), " +
      "byte[] (sequential)");
    LogTools.println("  short[][], short[] (interleaved), " +
      "short[] (sequential)");
//    LogTools.println("  int[][], int[] (interleaved), int[] (sequential)");
    LogTools.print("Columns are:");
    for (int q=0; q<chan.length; q++) {
      if (q > 0) LogTools.print(",");
      LogTools.print(" c=" + chan[q]);
    }
    LogTools.println();

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
