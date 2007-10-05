//
// MakeTestOmeTiff.java
//

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.*;
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

  /** Fisher-Yates shuffle, stolen from Wikipedia. */
  public static void shuffle(int[] array) {
    Random r = new Random();
    int n = array.length;
    while (--n > 0) {
      int k = r.nextInt(n + 1);  // 0 <= k <= n (!)
      int temp = array[n];
      array[n] = array[k];
      array[k] = temp;
    }
  }

  /**
   * Constructs a TiffData element matching the given parameters.
   * @param ifd Value to use for IFD attribute; 0 is default/none.
   * @param num Value to use for NumPlanes attribute, or -1 for none.
   * @param firstZ Value to use for FirstZ attribute; 0 is default/none.
   * @param firstC Value to use for FirstC attribute; 0 is default/none.
   * @param firstT Value to use for FirstT attribute; 0 is default/none.
   * @param order Dimension order; only used when scrambling.
   * @param sizeZ Number of focal planes; only used when scrambling.
   * @param sizeC Number of channels; only used when scrambling.
   * @param sizeT Number of time points; only used when scrambling.
   * @param total Total number of IFDs in the file;
   *   if null, no scrambling will be performed.
   */
  private static String tiffData(int ifd, int num,
    int firstZ, int firstC, int firstT,
    String order, int sizeZ, int sizeC, int sizeT, Integer total)
  {
    StringBuffer sb = new StringBuffer();
    if (total == null) {
      sb.append("<TiffData");
      if (ifd > 0) sb.append(" IFD=\"" + ifd + "\"");
      if (num >= 0) sb.append(" NumPlanes=\"" + num + "\"");
      if (firstZ > 0) sb.append(" FirstZ=\"" + firstZ + "\"");
      if (firstC > 0) sb.append(" FirstC=\"" + firstC + "\"");
      if (firstT > 0) sb.append(" FirstT=\"" + firstT + "\"");
      sb.append("/>");
    }
    else {
      // scramble planes
      if (ifd < 0) ifd = 0;
      if (num < 0) num = total.intValue();
      int len = sizeZ * sizeC * sizeT;
      int index = FormatTools.getIndex(order,
        sizeZ, sizeC, sizeT, len, firstZ, firstC, firstT);
      int[] planes = new int[num];
      for (int i=0; i<num; i++) planes[i] = index + i;
      shuffle(planes);
      for (int i=0; i<num; i++) {
        int[] zct = FormatTools.getZCTCoords(order,
          sizeZ, sizeC, sizeT, len, planes[i]);
        sb.append("<TiffData IFD=\"" + (i + ifd) + "\"" +
          " NumPlanes=\"1\" FirstZ=\"" + zct[0] + "\"" +
          " FirstC=\"" + zct[1] + "\" FirstT=\"" + zct[2] + "\"/>");
      }
    }
    return sb.toString();
  }

  public static void main(String[] args) throws FormatException, IOException {
    boolean usage = false;
    
    // parse command line arguments
    String name = null;
    String dist = null;
    boolean scramble = false;
    int numImages = 0;
    int[] numPixels = null;
    int[][] sizeX = null, sizeY = null;
    int[][] sizeZ = null, sizeC = null, sizeT = null;
    String[][] dimOrder = null;

    if (args == null || args.length < 2) usage = true;
    else {
      name = args[0];
      dist = args[1].toLowerCase();
      scramble = args.length >= 2 && args[2].equalsIgnoreCase("-scramble");

      int startIndex = scramble ? 3 : 2;

      // count number of images
      int ndx = startIndex;
      while (ndx < args.length) {
        int numPix = Integer.parseInt(args[ndx]);
        ndx += 1 + 6 * numPix;
        numImages++;
      }

      // parse pixels's dimensional information
      if (ndx > args.length) usage = true;
      else {
        numPixels = new int[numImages];
        sizeX = new int[numImages][];
        sizeY = new int[numImages][];
        sizeZ = new int[numImages][];
        sizeC = new int[numImages][];
        sizeT = new int[numImages][];
        dimOrder = new String[numImages][];

        ndx = startIndex;
        for (int i=0; i<numImages; i++) {
          numPixels[i] = Integer.parseInt(args[ndx++]);
          sizeX[i] = new int[numPixels[i]];
          sizeY[i] = new int[numPixels[i]];
          sizeZ[i] = new int[numPixels[i]];
          sizeC[i] = new int[numPixels[i]];
          sizeT[i] = new int[numPixels[i]];
          dimOrder[i] = new String[numPixels[i]];
          for (int p=0; p<numPixels[i]; p++) {
            sizeX[i][p] = Integer.parseInt(args[ndx++]);
            sizeY[i][p] = Integer.parseInt(args[ndx++]);
            sizeZ[i][p] = Integer.parseInt(args[ndx++]);
            sizeC[i][p] = Integer.parseInt(args[ndx++]);
            sizeT[i][p] = Integer.parseInt(args[ndx++]);
            dimOrder[i][p] = args[ndx++].toUpperCase();
          }
        }
      }
    }

    if (usage) {
      System.out.println(
        "Usage: java MakeTestOmeTiff name dist [-scramble]");
      System.out.println("         image1_NumPixels");
      System.out.println("           image1_pixels1_SizeX " +
        "image1_pixels1_SizeY image1_pixels1_SizeZ");
      System.out.println("             image1_pixels1_SizeC " +
        "image1_pixels1_SizeT image1_pixels1_DimOrder");
      System.out.println("           image1_pixels2_SizeX " +
        "image1_pixels2_SizeY image1_pixels2_SizeZ");
      System.out.println("             image1_pixels2_SizeC " +
        "image1_pixels2_SizeT image1_pixels2_DimOrder");
      System.out.println("           [...]");
      System.out.println("         image2_NumPixels");
      System.out.println("           image2_pixels1_SizeX " +
        "image2_pixels1_SizeY image1_pixels1_SizeZ");
      System.out.println("             image2_pixels1_SizeC " +
        "image2_pixels1_SizeT image1_pixels1_DimOrder");
      System.out.println("           image2_pixels2_SizeX " +
        "image2_pixels2_SizeY image1_pixels2_SizeZ");
      System.out.println("             image2_pixels2_SizeC " +
        "image2_pixels2_SizeT image1_pixels2_DimOrder");
      System.out.println("           [...]");
      System.out.println("         [...]");
      System.out.println();
      System.out.println("  name: prefix for filenames");
      System.out.println("  dist: code for how to distribute across files:");
      System.out.println("    ipzct = all Images + Pixels in one file");
      System.out.println("    pzct = each Image in its own file");
      System.out.println("    zct = each Pixels in its own file");
      System.out.println("    zc = all Z + C positions per file");
      System.out.println("    zt = all Z + T positions per file");
      System.out.println("    ct = all C + T positions per file");
      System.out.println("    z = all Z positions per file");
      System.out.println("    c = all C positions per file");
      System.out.println("    t = all T positions per file");
      System.out.println("    x = single plane per file");
      System.out.println("  -scramble: randomizes IFD ordering");
      System.out.println("  image*_pixels*_SizeX: width of image planes");
      System.out.println("  image*_pixels*_SizeY: height of image planes");
      System.out.println("  image*_pixels*_SizeZ: number of focal planes");
      System.out.println("  image*_pixels*_SizeC: number of channels");
      System.out.println("  image*_pixels*_SizeT: number of time points");
      System.out.println("  image*_pixels*_DimOrder: planar ordering:");
      System.out.println("    XYZCT, XYZTC, XYCZT, XYCTZ, XYTZC, or XYTCZ");
      System.out.println();
      System.out.println("Example:");
      System.out.println("  java MakeTestOmeTiff test ipzct \\");
      System.out.println("    2 431 555 1 2 7 XYTCZ \\");
      System.out.println("      348 461 2 1 6 XYZTC \\");
      System.out.println("    1 517 239 5 3 4 XYCZT");
      System.exit(1);
    }

    if (!dist.equals("ipzct") && !dist.equals("pzct") && !dist.equals("zct") &&
      !dist.equals("zc") && !dist.equals("zt") && !dist.equals("ct") &&
      !dist.equals("z") && !dist.equals("c") && !dist.equals("t") &&
      !dist.equals("x"))
    {
      System.out.println("Invalid dist value: " + dist);
      System.exit(2);
    }
    boolean allI = dist.indexOf("i") >= 0;
    boolean allP = dist.indexOf("p") >= 0;
    boolean allZ = dist.indexOf("z") >= 0;
    boolean allC = dist.indexOf("c") >= 0;
    boolean allT = dist.indexOf("t") >= 0;

    BufferedImage[][][] images = new BufferedImage[numImages][][];
    int[][] globalOffsets = new int[numImages][]; // IFD offsets across Images
    int[][] localOffsets = new int[numImages][]; // IFD offsets per Image
    int globalOffset = 0, localOffset = 0;
    for (int i=0; i<numImages; i++) {
      images[i] = new BufferedImage[numPixels[i]][];
      globalOffsets[i] = new int[numPixels[i]];
      localOffsets[i] = new int[numPixels[i]];
      localOffset = 0;
      for (int p=0; p<numPixels[i]; p++) {
        int len = sizeZ[i][p] * sizeC[i][p] * sizeT[i][p];
        images[i][p] = new BufferedImage[len];
        globalOffsets[i][p] = globalOffset;
        globalOffset += len;
        localOffsets[i][p] = localOffset;
        localOffset += len;
      }
    }

    System.out.println("Generating image planes");
    for (int i=0, ipNum=0; i<numImages; i++) {
      System.out.println("  Image #" + (i + 1) + ":");
      for (int p=0; p<numPixels[i]; p++, ipNum++) {
        System.out.print("    Pixels #" + (p + 1) + " - " +
          sizeX[i][p] + " x " + sizeY[i][p] + ", " +
          sizeZ[i][p] + "Z " + sizeC[i][p] + "C " + sizeT[i][p] + "T");
        int len = images[i][p].length;
        for (int j=0; j<len; j++) {
          int[] zct = FormatTools.getZCTCoords(dimOrder[i][p],
            sizeZ[i][p], sizeC[i][p], sizeT[i][p], len, j);

          System.out.print(".");
          images[i][p][j] = new BufferedImage(
            sizeX[i][p], sizeY[i][p], BufferedImage.TYPE_BYTE_GRAY);
          Graphics2D g = images[i][p][j].createGraphics();
          // draw gradient
          boolean even = ipNum % 2 == 0;
          int type = ipNum / 2;
          if (even) {
            // draw vertical gradient for even-numbered pixelses
            for (int y=0; y<sizeY[i][p]; y++) {
              int v = gradient(type, y, sizeY[i][p]);
              g.setColor(new Color(v, v, v));
              g.drawLine(0, y, sizeX[i][p], y);
            }
          }
          else {
            // draw horizontal gradient for odd-numbered pixelses
            for (int x=0; x<sizeX[i][p]; x++) {
              int v = gradient(type, x, sizeX[i][p]);
              g.setColor(new Color(v, v, v));
              g.drawLine(x, 0, x, sizeY[i][p]);
            }
          }

          // build list of text lines from planar information
          Vector lines = new Vector();
          Font font = g.getFont();
          lines.add(new TextLine(name, font.deriveFont(32f), 5, -5));
          lines.add(new TextLine(sizeX[i][p] + " x " + sizeY[i][p],
            font.deriveFont(Font.ITALIC, 16f), 20, 10));
          lines.add(new TextLine(dimOrder[i][p],
            font.deriveFont(Font.ITALIC, 14f), 30, 5));
          int space = 5;
          if (numImages > 1) {
            lines.add(new TextLine("Image #" + (i + 1) + "/" + numImages,
              font, 20, space));
            space = 2;
          }
          if (numPixels[i] > 1) {
            lines.add(new TextLine("Pixels #" + (p + 1) + "/" + numPixels[i],
              font, 20, space));
            space = 2;
          }
          if (sizeZ[i][p] > 1) {
            lines.add(new TextLine("Focal plane = " +
              (zct[0] + 1) + "/" + sizeZ[i][p], font, 20, space));
            space = 2;
          }
          if (sizeC[i][p] > 1) {
            lines.add(new TextLine("Channel = " +
              (zct[1] + 1) + "/" + sizeC[i][p], font, 20, space));
            space = 2;
          }
          if (sizeT[i][p] > 1) {
            lines.add(new TextLine("Time point = " +
              (zct[2] + 1) + "/" + sizeT[i][p], font, 20, space));
            space = 2;
          }

          // draw text lines to image
          g.setColor(Color.white);
          int yoff = 0;
          for (int l=0; l<lines.size(); l++) {
            TextLine text = (TextLine) lines.get(l);
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
    }

    System.out.println("Writing output files");

    // determine filename for each image plane
    String[][][] filenames = new String[numImages][][];
    Hashtable lastHash = new Hashtable();
    boolean[][][] last = new boolean[numImages][][];
    Hashtable ifdTotal = new Hashtable();
    Hashtable firstZ = new Hashtable();
    Hashtable firstC = new Hashtable();
    Hashtable firstT = new Hashtable();
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<numImages; i++) {
      filenames[i] = new String[numPixels[i]][];
      last[i] = new boolean[numPixels[i]][];
      for (int p=0; p<numPixels[i]; p++) {
        int len = images[i][p].length;
        filenames[i][p] = new String[len];
        last[i][p] = new boolean[len];
        for (int j=0; j<len; j++) {
          sb.append(name);
          if (!allI && numImages > 1) sb.append("_image" + (i + 1));
          if (!allP && numPixels[i] > 1) sb.append("_pixels" + (p + 1));
          int[] zct = FormatTools.getZCTCoords(dimOrder[i][p],
            sizeZ[i][p], sizeC[i][p], sizeT[i][p], len, j);
          if (!allZ && sizeZ[i][p] > 1) sb.append("_Z" + (zct[0] + 1));
          if (!allC && sizeC[i][p] > 1) sb.append("_C" + (zct[1] + 1));
          if (!allT && sizeT[i][p] > 1) sb.append("_T" + (zct[2] + 1));
          sb.append(".ome.tif");
          filenames[i][p][j] = sb.toString();
          sb.setLength(0);
          last[i][p][j] = true;

          // update last flag for this filename
          String key = filenames[i][p][j];
          ImageIndex index = (ImageIndex) lastHash.get(key);
          if (index != null) {
            last[index.image][index.pixels][index.plane] = false;
          }
          lastHash.put(key, new ImageIndex(i, p, j));

          // update IFD count for this filename
          Integer total = (Integer) ifdTotal.get(key);
          if (total == null) total = new Integer(1);
          else total = new Integer(total.intValue() + 1);
          ifdTotal.put(key, total);

          // update FirstZ, FirstC and FirstT values for this filename
          if (!allZ && sizeZ[i][p] > 1) {
            firstZ.put(filenames[i][p][j], new Integer(zct[0]));
          }
          if (!allC && sizeC[i][p] > 1) {
            firstC.put(filenames[i][p][j], new Integer(zct[1]));
          }
          if (!allT && sizeT[i][p] > 1) {
            firstT.put(filenames[i][p][j], new Integer(zct[2]));
          }
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
    for (int i=0; i<numImages; i++) {
      sb.append("<Image " +
        "ID=\"org.openmicroscopy:Image:" + (i + 1) + "\" " +
        "Name=\"" + name + "\" " +
        "DefaultPixels=\"org.openmicroscopy:Pixels:" + (i + 1) + "-1\">" +
        "<CreationDate>" + creationDate + "</CreationDate>");
      for (int p=0; p<numPixels[i]; p++) {
        sb.append("<Pixels " +
          "ID=\"org.openmicroscopy.Pixels:" + (i + 1) + "-" + (p + 1) + "\" " +
          "DimensionOrder=\"" + dimOrder[i][p] + "\" " +
          "PixelType=\"uint8\" " +
          "BigEndian=\"true\" " +
          "SizeX=\"" + sizeX[i][p] + "\" " +
          "SizeY=\"" + sizeY[i][p] + "\" " +
          "SizeZ=\"" + sizeZ[i][p] + "\" " +
          "SizeC=\"" + sizeC[i][p] + "\" " +
          "SizeT=\"" + sizeT[i][p] + "\">" +
          "TIFF_DATA_IMAGE_" + i + "_PIXELS_" + p + // placeholder
          "</Pixels>");
      }
      sb.append("</Image>");
    }
    sb.append("</OME>");
    String xmlTemplate = sb.toString();

    TiffWriter out = new TiffWriter();
    for (int i=0; i<numImages; i++) {
        System.out.println("  Image #" + (i + 1) + ":");
      for (int p=0; p<numPixels[i]; p++) {
        System.out.println("    Pixels #" + (p + 1) + " - " +
          sizeX[i][p] + " x " + sizeY[i][p] + ", " +
          sizeZ[i][p] + "Z " + sizeC[i][p] + "C " + sizeT[i][p] + "T:");
        int len = images[i][p].length;
        for (int j=0; j<len; j++) {
          int[] zct = FormatTools.getZCTCoords(dimOrder[i][p],
            sizeZ[i][p], sizeC[i][p], sizeT[i][p], len, j);
          System.out.println("      " +
            "Z" + zct[0] + " C" + zct[1] + " T" + zct[2] +
            " -> " + filenames[i][p][j] + (last[i][p][j] ? "*" : ""));
          out.setId(filenames[i][p][j]);
          // write comment stub, to be overwritten later
          Hashtable ifdHash = new Hashtable();
          TiffTools.putIFDValue(ifdHash, TiffTools.IMAGE_DESCRIPTION, "");
          out.saveImage(images[i][p][j], ifdHash, last[i][p][j]);
          if (last[i][p][j]) {
            // inject OME-XML block
            String xml = xmlTemplate;
            String key = filenames[i][p][j];
            Integer fzObj = (Integer) firstZ.get(key);
            Integer fcObj = (Integer) firstC.get(key);
            Integer ftObj = (Integer) firstT.get(key);
            int fz = fzObj == null ? 0 : fzObj.intValue();
            int fc = fcObj == null ? 0 : fcObj.intValue();
            int ft = ftObj == null ? 0 : ftObj.intValue();
            Integer total = (Integer) ifdTotal.get(key);
            if (!scramble) total = null;
            for (int ii=0; ii<numImages; ii++) {
              for (int pp=0; pp<numPixels[ii]; pp++) {
                String pattern = "TIFF_DATA_IMAGE_" + ii + "_PIXELS_" + pp;
                if (!allI && ii != i || !allP && pp != p) {
                  // current Pixels is not part of this file
                  xml = xml.replaceFirst(pattern,
                    tiffData(0, 0, 0, 0, 0, dimOrder[ii][pp],
                    sizeZ[ii][pp], sizeC[ii][pp], sizeT[ii][pp], total));
                  continue;
                }
                if (allP) {
                  int ifd;
                  if (allI) {
                    // all Images in one file; need to use global offset
                    ifd = globalOffsets[ii][pp];
                  }
                  else { // ii == i
                    // one Image per file; use local offset
                    ifd = localOffsets[ii][pp];
                  }
                  int num = images[ii][pp].length;
                  if ((!allI || numImages == 1) && numPixels[i] == 1) {
                    // only one Pixels in this file; don't need IFD/NumPlanes
                    ifd = 0;
                    num = -1;
                  }
                  xml = xml.replaceFirst(pattern,
                    tiffData(ifd, num, 0, 0, 0, dimOrder[ii][pp],
                    sizeZ[ii][pp], sizeC[ii][pp], sizeT[ii][pp], total));
                }
                else { // pp == p
                  xml = xml.replaceFirst(pattern,
                    tiffData(0, -1, fz, fc, ft, dimOrder[ii][pp],
                    sizeZ[ii][pp], sizeC[ii][pp], sizeT[ii][pp], total));
                }
              }
            }
            TiffTools.overwriteComment(filenames[i][p][j], xml);
          }
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
    private int image;
    private int pixels;
    private int plane;
    private ImageIndex(int i, int p, int j) {
      this.image = i;
      this.pixels = p;
      this.plane = j;
    }
  }

}
