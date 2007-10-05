//
// MakeTestOmeTiff.java
//

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.out.TiffWriter;

/** Creates a sample OME-TIFF dataset according to the given parameters. */
public class MakeTestOmeTiff {

  private static int gradient(int type, int num, int total) {
    final int max = 96;
    int split = type / 2 + 1;
    boolean reverse = type % 2 == 0;
    int v = max;
    total /= split;
    for (int i=1; i<=split+1; i++) {
      if (num < i * total) {
        if (i % 2 == 0) v = max * (num % total) / total;
        else v = max * (total - num % total) / total;
        break;
      }
    }
    if (reverse) v = max - v;
    return v;
  }

  public static void main(String[] args) throws FormatException, IOException {
    int leadParams = 2, paramCount = 6;
    boolean usage = false, scramble = false;
    if (args == null || args.length < leadParams + paramCount) usage = true;
    else {
      int parity = (args.length - leadParams) % paramCount;
      scramble = args[2].equalsIgnoreCase("-scramble");
      if (parity != (scramble ? 1 : 0)) usage = true;
    }
    if (usage) {
      System.out.println("Usage: java MakeTestOmeTiff name dist [-scramble]");
      System.out.println("\tseries1_SizeX series1_SizeY series1_SizeZ");
      System.out.println("\tseries1_SizeC series1_SizeT series1_DimOrder");
      System.out.println("\t[series2_SizeX series2_SizeY series2_SizeZ");
      System.out.println("\tseries2_SizeC series2_SizeT series2_DimOrder]");
      System.out.println("\t[...]");
      System.out.println();
      System.out.println("  name: prefix for filenames");
      System.out.println("  dist: code for how to distribute across files:");
      System.out.println("    szct = all series + planes in one master file");
      System.out.println("    zct = each series in its own file");
      System.out.println("    zc = all Z + C positions per file");
      System.out.println("    zt = all Z + T positions per file");
      System.out.println("    ct = all C + T positions per file");
      System.out.println("    z = all Z positions per file");
      System.out.println("    c = all C positions per file");
      System.out.println("    t = all T positions per file");
      System.out.println("    x = single plane per file");
      System.out.println("  -scramble: randomizes IFD ordering");
      System.out.println("  series*_SizeX: width of image planes");
      System.out.println("  series*_SizeY: height of image planes");
      System.out.println("  series*_SizeZ: number of focal planes");
      System.out.println("  series*_SizeC: number of channels");
      System.out.println("  series*_SizeT: number of time points");
      System.out.println("  series*_DimOrder: planar ordering: " +
        "xyzct, xyztc, xyczt, xyctz, xytzc, xytcz");
      System.out.println();
      System.out.println("Example:");
      System.out.println("\tjava MakeTestOmeTiff test-image zct \\");
      System.out.println("\t517 239 5 3 4 xyzct 431 555 1 2 7 xytcz");
      System.exit(1);
    }
    String name = args[0];
    String dist = args[1].toLowerCase();
    int numSeries = (args.length - leadParams) / paramCount;
    int[] sizeX = new int[numSeries];
    int[] sizeY = new int[numSeries];
    int[] sizeZ = new int[numSeries];
    int[] sizeC = new int[numSeries];
    int[] sizeT = new int[numSeries];
    String[] dimOrder = new String[numSeries];
    BufferedImage[][] images = new BufferedImage[numSeries][];
    for (int s=0; s<numSeries; s++) {
      sizeX[s] = Integer.parseInt(args[leadParams + paramCount * s]);
      sizeY[s] = Integer.parseInt(args[leadParams + paramCount * s + 1]);
      sizeZ[s] = Integer.parseInt(args[leadParams + paramCount * s + 2]);
      sizeC[s] = Integer.parseInt(args[leadParams + paramCount * s + 3]);
      sizeT[s] = Integer.parseInt(args[leadParams + paramCount * s + 4]);
      dimOrder[s] = args[leadParams + paramCount * s + 5].toUpperCase();
      images[s] = new BufferedImage[sizeZ[s] * sizeC[s] * sizeT[s]];
    }
    int ndx = 0;

    System.out.println("Generating image planes");
    for (int s=0; s<numSeries; s++) {
      int len = images[s].length;
      System.out.print("\tSeries #" + (s + 1) + " - " +
        sizeX[s] + " x " + sizeY[s] + ", " +
        sizeZ[s] + "Z " + sizeC[s] + "C " + sizeT[s] + "T");
      for (int p=0; p<len; p++) {
        int[] zct = FormatTools.getZCTCoords(dimOrder[s],
          sizeZ[s], sizeC[s], sizeT[s], len, p);

        System.out.print(".");
        images[s][p] = new BufferedImage(
          sizeX[s], sizeY[s], BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = images[s][p].createGraphics();
        // draw gradient
        boolean even = s % 2 == 0;
        int type = s / 2;
        if (even) {
          // draw vertical gradient for even-numbered series
          for (int y=0; y<sizeY[s]; y++) {
            int v = gradient(type, y, sizeY[s]);
            g.setColor(new Color(v, v, v));
            g.drawLine(0, y, sizeX[s], y);
          }
        }
        else {
          // draw horizontal gradient for odd-numbered series
          for (int x=0; x<sizeX[s]; x++) {
            int v = gradient(type, x, sizeX[s]);
            g.setColor(new Color(v, v, v));
            g.drawLine(x, 0, x, sizeY[s]);
          }
        }

        // build list of text lines from planar information
        Vector lines = new Vector();
        Font font = g.getFont();
        lines.add(new TextLine(name, font.deriveFont(32f), 5, -5));
        lines.add(new TextLine(sizeX[s] + " x " + sizeY[s],
          font.deriveFont(Font.ITALIC, 16f), 20, 10));
        lines.add(new TextLine(dimOrder[s],
          font.deriveFont(Font.ITALIC, 14f), 30, 5));
        if (numSeries > 1) {
          lines.add(new TextLine("Series #" + (s + 1) + "/" + numSeries,
            font, 20, 5));
        }
        if (sizeZ[s] > 1) {
          lines.add(new TextLine(
            "Focal plane = " + (zct[0] + 1) + "/" + sizeZ[s], font, 20, 2));
        }
        if (sizeC[s] > 1) {
          lines.add(new TextLine(
            "Channel = " + (zct[1] + 1) + "/" + sizeC[s], font, 20, 2));
        }
        if (sizeT[s] > 1) {
          lines.add(new TextLine(
            "Time point = " + (zct[2] + 1) + "/" + sizeT[s], font, 20, 2));
        }

        // draw text lines to image
        g.setColor(Color.white);
        int yoff = 0;
        for (int i=0; i<lines.size(); i++) {
          TextLine text = (TextLine) lines.get(i);
          g.setFont(text.font);
          Rectangle2D r = g.getFont().getStringBounds(
            text.line, g.getFontRenderContext());
          yoff += r.getHeight() + text.ypad;
          g.drawString(text.line, text.xoff, yoff);
        }
        g.dispose();
      }
      System.out.println();
    }

    System.out.println("Writing output files");
    boolean allS = dist.indexOf("s") >= 0;
    boolean allZ = dist.indexOf("z") >= 0;
    boolean allC = dist.indexOf("c") >= 0;
    boolean allT = dist.indexOf("t") >= 0;

    // determine filename for each image plane
    String[][] filenames = new String[numSeries][];
    Hashtable lastHash = new Hashtable();
    boolean[][] last = new boolean[numSeries][];
    StringBuffer sb = new StringBuffer();
    for (int s=0; s<numSeries; s++) {
      int len = images[s].length;
      filenames[s] = new String[len];
      last[s] = new boolean[len];
      for (int p=0; p<len; p++) {
        sb.append(name);
        if (!allS) sb.append("_series" + s);
        int[] zct = FormatTools.getZCTCoords(dimOrder[s],
          sizeZ[s], sizeC[s], sizeT[s], len, p);
        if (!allZ) sb.append("_Z" + zct[0]);
        if (!allC) sb.append("_C" + zct[1]);
        if (!allT) sb.append("_T" + zct[2]);
        sb.append(".ome.tif");
        filenames[s][p] = sb.toString();
        sb.setLength(0);
        last[s][p] = true;

        // update last flag for this filename
        String key = filenames[s][p];
        ImageIndex index = (ImageIndex) lastHash.get(key);
        if (index != null) last[index.series][index.plane] = false;
        lastHash.put(key, new ImageIndex(s, p));
      }
    }

    TiffWriter out = new TiffWriter();
    for (int s=0; s<numSeries; s++) {
      int len = images[s].length;
      System.out.println("\tSeries #" + (s + 1) + " - " +
        sizeX[s] + " x " + sizeY[s] + ", " +
        sizeZ[s] + "Z " + sizeC[s] + "C " + sizeT[s] + "T:");
      for (int p=0; p<len; p++) {
        int[] zct = FormatTools.getZCTCoords(dimOrder[s],
          sizeZ[s], sizeC[s], sizeT[s], len, p);
        System.out.println("\t\tZ" + zct[0] + " C" + zct[1] +
          " T" + zct[2] + " -> " + filenames[s][p] + (last[s][p] ? "*" : ""));
        out.setId(filenames[s][p]);
        out.saveImage(images[s][p], last[s][p]);
      }
    }
//    loci.formats.gui.ImageViewer view = new loci.formats.gui.ImageViewer();
//    view.setImages(images[0]);
//    view.setVisible(true);
  }

  private static class TextLine {
    private String line;
    private Font font;
    private int xoff;
    private int ypad;
    private TextLine(String line, Font font, int xoff, int ypad) {
      this.line = line;
      this.font = font;
      this.xoff = xoff;
      this.ypad = ypad;
    }
  }

  private static class ImageIndex {
    private int series;
    private int plane;
    private ImageIndex(int series, int plane) {
      this.series = series;
      this.plane = plane;
    }
  }

}
