//
// MakeTestOmeTiff.java
//

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Calendar;
import loci.formats.*;
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
    int[] offsets = new int[numSeries];
    for (int s=0; s<numSeries; s++) {
      sizeX[s] = Integer.parseInt(args[leadParams + paramCount * s]);
      sizeY[s] = Integer.parseInt(args[leadParams + paramCount * s + 1]);
      sizeZ[s] = Integer.parseInt(args[leadParams + paramCount * s + 2]);
      sizeC[s] = Integer.parseInt(args[leadParams + paramCount * s + 3]);
      sizeT[s] = Integer.parseInt(args[leadParams + paramCount * s + 4]);
      int len = sizeZ[s] * sizeC[s] * sizeT[s];
      dimOrder[s] = args[leadParams + paramCount * s + 5].toUpperCase();
      images[s] = new BufferedImage[len];
      if (s < numSeries - 1) offsets[s + 1] = offsets[s] + len;
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
    Hashtable firstZ = new Hashtable();
    Hashtable firstC = new Hashtable();
    Hashtable firstT = new Hashtable();
    StringBuffer sb = new StringBuffer();
    for (int s=0; s<numSeries; s++) {
      int len = images[s].length;
      filenames[s] = new String[len];
      last[s] = new boolean[len];
      for (int p=0; p<len; p++) {
        sb.append(name);
        if (!allS && numSeries > 1) sb.append("_series" + (s + 1));
        int[] zct = FormatTools.getZCTCoords(dimOrder[s],
          sizeZ[s], sizeC[s], sizeT[s], len, p);
        if (!allZ && sizeZ[s] > 1) sb.append("_Z" + (zct[0] + 1));
        if (!allC && sizeC[s] > 1) sb.append("_C" + (zct[1] + 1));
        if (!allT && sizeT[s] > 1) sb.append("_T" + (zct[2] + 1));
        sb.append(".ome.tif");
        filenames[s][p] = sb.toString();
        sb.setLength(0);
        last[s][p] = true;

        // update last flag for this filename
        String key = filenames[s][p];
        ImageIndex index = (ImageIndex) lastHash.get(key);
        if (index != null) last[index.series][index.plane] = false;
        lastHash.put(key, new ImageIndex(s, p));

        // update FirstZ, FirstC and FirstT values for this filename
        if (!allZ && sizeZ[s] > 1) {
          firstZ.put(filenames[s][p], new Integer(zct[0]));
        }
        if (!allC && sizeC[s] > 1) {
          firstC.put(filenames[s][p], new Integer(zct[1]));
        }
        if (!allT && sizeT[s] > 1) {
          firstT.put(filenames[s][p], new Integer(zct[2]));
        }
      }
    }

    // build OME-XML block

    // CreationDate is required; initialize a default value (current time)
    // use ISO 8601 dateTime format (e.g., 1988-04-07T18:39:09)
    sb.setLength(0);
    Calendar now = Calendar.getInstance();
    int year = now.get(Calendar.YEAR);
    int month = now.get(Calendar.MONTH);
    int day = now.get(Calendar.DAY_OF_MONTH);
    int hour = now.get(Calendar.HOUR_OF_DAY);
    int min = now.get(Calendar.MINUTE);
    int sec = now.get(Calendar.SECOND);
    sb.append(year);
    sb.append("-");
    if (month < 9) sb.append("0");
    sb.append(month + 1);
    sb.append("-");
    if (day < 10) sb.append("0");
    sb.append(day);
    sb.append("T");
    if (hour < 10) sb.append("0");
    sb.append(hour);
    sb.append(":");
    if (min < 10) sb.append("0");
    sb.append(min);
    sb.append(":");
    if (sec < 10) sb.append("0");
    sb.append(sec);
    String creationDate = sb.toString();

    sb.setLength(0);
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
      "<!-- Warning: this comment is an OME-XML metadata block, which " +
      "contains crucial dimensional parameters and other important metadata. " +
      "Please edit cautiously (if at all), and back up the original data " +
      "before doing so. For more information, see the OME-TIFF web site: " +
      "http://loci.wisc.edu/ome/ome-tiff.html. --><OME " +
      "xmlns=\"http://www.openmicroscopy.org/XMLschemas/OME/FC/ome.xsd\" " +
      "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
      "xsi:schemaLocation=\"" +
      "http://www.openmicroscopy.org/XMLschemas/OME/FC/ome.xsd\">");
    for (int s=0; s<numSeries; s++) {
      sb.append("<Image " +
        "ID=\"org.openmicroscopy:Image:" + (s + 1) + "\" " +
        "Name=\"" + name + "\" " +
        "DefaultPixels=\"org.openmicroscopy:Pixels:" + (s + 1) + "\">" +
        "<CreationDate>" + creationDate + "</CreationDate>" +
        "<Pixels ID=\"Pixels:1\" " +
        "DimensionOrder=\"" + dimOrder[s] + "\" " +
        "PixelType=\"uint8\" " +
        "BigEndian=\"true\" " +
        "SizeX=\"" + sizeX[s] + "\" " +
        "SizeY=\"" + sizeY[s] + "\" " +
        "SizeZ=\"" + sizeZ[s] + "\" " +
        "SizeC=\"" + sizeC[s] + "\" " +
        "SizeT=\"" + sizeT[s] + "\">" +
        "TIFF_DATA_SERIES_" + s + // placeholder
        "</Pixels></Image>");
    }
    sb.append("</OME>");
    String xmlTemplate = sb.toString();

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
        // write comment stub, to be overwritten later
        Hashtable ifd = new Hashtable();
        TiffTools.putIFDValue(ifd, TiffTools.IMAGE_DESCRIPTION, "");
        out.saveImage(images[s][p], ifd, last[s][p]);
        if (last[s][p]) {
          // append OME-XML block
          String xml = xmlTemplate;
          for (int ss=0; ss<numSeries; ss++) {
            String pattern = "TIFF_DATA_SERIES_" + ss;
            if (allS) {
              xml = xml.replaceFirst(pattern,
                "<TiffData IFD=\"" + offsets[ss] + "\" " +
                "NumPlanes=\"" + images[ss].length + "\"/>");
            }
            else if (s == ss) {
              Integer fz = (Integer) firstZ.get(filenames[s][p]);
              Integer fc = (Integer) firstC.get(filenames[s][p]);
              Integer ft = (Integer) firstT.get(filenames[s][p]);
              sb.setLength(0);
              sb.append("<TiffData");
              if (fz != null) sb.append(" FirstZ=\"" + fz + "\"");
              if (fc != null) sb.append(" FirstC=\"" + fc + "\"");
              if (ft != null) sb.append(" FirstT=\"" + ft + "\"");
              sb.append("/>");
              xml = xml.replaceFirst(pattern, sb.toString());
            }
            else {
              xml = xml.replaceFirst(pattern, "<TiffData NumPlanes=\"0\"/>");
            }
          }
          TiffTools.overwriteComment(filenames[s][p], xml);
        }
      }
    }
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
