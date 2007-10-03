//
// MakeTestOmeTiff.java
//

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import loci.formats.gui.ImageViewer;//TEMP

/** Creates a sample OME-TIFF dataset according to the given parameters. */
public class MakeTestOmeTiff {

  public static void main(String[] args) {
    int paramCount = 5;
    if (args == null || args.length == 0 || args.length % paramCount != 0) {
      System.out.println("Usage: java MakeTestOmeTiff");
      System.out.println("\tseries1_SizeX series1_SizeY series1_SizeZ " +
        "series1_SizeC series1_SizeT");
      System.out.println("\t[series2_SizeX series2_SizeY series2_SizeZ " +
        "series2_SizeC series2_SizeT]");
      System.out.println("\t[...]");
      System.exit(1);
    }
    int numSeries = args.length / paramCount;
    int[] sizeX = new int[numSeries];
    int[] sizeY = new int[numSeries];
    int[] sizeZ = new int[numSeries];
    int[] sizeC = new int[numSeries];
    int[] sizeT = new int[numSeries];
    int totalPlanes = 0;
    for (int i=0; i<numSeries; i++) {
      sizeX[i] = Integer.parseInt(args[paramCount * i]);
      sizeY[i] = Integer.parseInt(args[paramCount * i + 1]);
      sizeZ[i] = Integer.parseInt(args[paramCount * i + 2]);
      sizeC[i] = Integer.parseInt(args[paramCount * i + 3]);
      sizeT[i] = Integer.parseInt(args[paramCount * i + 4]);
      totalPlanes += sizeZ[i] * sizeC[i] * sizeT[i];
    }
    BufferedImage[] images = new BufferedImage[totalPlanes];
    int ndx = 0;
    for (int s=0; s<numSeries; s++) {
      for (int t=0; t<sizeT[s]; t++) {
        for (int c=0; c<sizeC[s]; c++) {
          for (int z=0; z<sizeZ[s]; z++) {
            images[ndx] = new BufferedImage(
              sizeX[s], sizeY[s], BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g = images[ndx].createGraphics();
            // draw gradient
            for (int y=0; y<sizeY[s]; y++) {
              int v = 96 * y / sizeY[s];
              g.setColor(new Color(v, v, v));
              g.drawLine(0, y, sizeX[s], y);
            }
            // draw planar information as text
            g.setColor(Color.white);
            Font font = g.getFont();
            FontRenderContext frc = g.getFontRenderContext();
            String[] lines = {
              numSeries > 1 ? "Series #" + (s + 1) + "/" + numSeries : "",
              "Resolution = " + sizeX[s] + " x " + sizeY[s],
              "Focal plane = " + (z + 1) + "/" + sizeZ[s],
              "Channel = " + (c + 1) + "/" + sizeC[s],
              "Time point = " + (t + 1) + "/" + sizeT[s]
            };
            int xoff = 5, yoff = 5; // starting offset for lines of text
            for (int i=0; i<lines.length; i++) {
              Rectangle2D r = font.getStringBounds(lines[i], frc);
              yoff += r.getHeight();
              g.drawString(lines[i], xoff, yoff);
            }
            g.dispose();
            ndx++;
          }
        }
      }
    }
    ImageViewer view = new ImageViewer();
    view.setImages(images);
    view.setVisible(true);
  }

}
