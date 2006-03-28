//
// ImageTester.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * A class for testing the ImageTools.makeImage methods.
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


  // -- WindowListener methods --

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
    int h = (screen.height - hpad) / 6;//9;
    System.out.println("Using images of size " + w + " x " + h);
    int size = w * h;
    BufferedImage[] bimg1 = new BufferedImage[chan.length];
    BufferedImage[] bimg2 = new BufferedImage[chan.length];
    BufferedImage[] bimg3 = new BufferedImage[chan.length];
    BufferedImage[] simg1 = new BufferedImage[chan.length];
    BufferedImage[] simg2 = new BufferedImage[chan.length];
    BufferedImage[] simg3 = new BufferedImage[chan.length];
//    BufferedImage[] iimg1 = new BufferedImage[chan.length];
//    BufferedImage[] iimg2 = new BufferedImage[chan.length];
//    BufferedImage[] iimg3 = new BufferedImage[chan.length];
    for (int q=0; q<chan.length; q++) {
      int c = chan[q];
      System.out.println("Building c=" + c + " images");
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

    System.out.println("Rows are: byte[][], byte[] (interleaved), " +
      "byte[] (sequential)");
    System.out.println("  short[][], short[] (interleaved), " +
      "short[] (sequential)");
//    System.out.println("  int[][], int[] (interleaved), int[] (sequential)");
    System.out.print("Columns are:");
    for (int q=0; q<chan.length; q++) {
      if (q > 0) System.out.print(",");
      System.out.print(" c=" + chan[q]);
    }
    System.out.println();

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
    frame.show();
  }

}
