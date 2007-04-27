//
// FormatTools.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

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

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.StringTokenizer;

/** A utility class for format reader and writer implementations. */
public final class FormatTools {

  // -- Constants --

  /** Identifies the <i>INT8</i> data type used to store pixel values. */
  public static final int INT8 = 0;

  /** Identifies the <i>UINT8</i> data type used to store pixel values. */
  public static final int UINT8 = 1;

  /** Identifies the <i>INT16</i> data type used to store pixel values. */
  public static final int INT16 = 2;

  /** Identifies the <i>UINT16</i> data type used to store pixel values. */
  public static final int UINT16 = 3;

  /** Identifies the <i>INT32</i> data type used to store pixel values. */
  public static final int INT32 = 4;

  /** Identifies the <i>UINT32</i> data type used to store pixel values. */
  public static final int UINT32 = 5;

  /** Identifies the <i>FLOAT</i> data type used to store pixel values. */
  public static final int FLOAT = 6;

  /** Identifies the <i>DOUBLE</i> data type used to store pixel values. */
  public static final int DOUBLE = 7;

  /** Human readable pixel type. */
  private static String[] pixelTypes;
  static {
    pixelTypes = new String[8];
    pixelTypes[INT8] = "int8";
    pixelTypes[UINT8] = "uint8";
    pixelTypes[INT16] = "int16";
    pixelTypes[UINT16] = "uint16";
    pixelTypes[INT32] = "int32";
    pixelTypes[UINT32] = "uint32";
    pixelTypes[FLOAT] = "float";
    pixelTypes[DOUBLE] = "double";
  }

  /**
   * Identifies the <i>Channel</i> dimensional type,
   * representing a generic channel dimension.
   */
  public static final String CHANNEL = "Channel";

  /**
   * Identifies the <i>Spectra</i> dimensional type,
   * representing a dimension consisting of spectral channels.
   */
  public static final String SPECTRA = "Spectra";

  /**
   * Identifies the <i>Lifetime</i> dimensional type,
   * representing a dimension consisting of a lifetime histogram.
   */
  public static final String LIFETIME = "Lifetime";

  /**
   * Identifies the <i>Polarization</i> dimensional type,
   * representing a dimension consisting of polarization states.
   */
  public static final String POLARIZATION = "Polarization";

  // -- Constructor --

  private FormatTools() { }

  // -- Utility methods - testing --

  /**
   * A utility method for test reading a file from the command line,
   * and displaying the results in a simple display.
   */
  public static boolean testRead(IFormatReader reader, String[] args)
    throws FormatException, IOException
  {
    String id = null;
    boolean pixels = true;
    boolean doMeta = true;
    boolean thumbs = false;
    boolean minmax = false;
    boolean merge = false;
    boolean stitch = false;
    boolean separate = false;
    boolean omexml = false;
    boolean normalize = false;
    boolean fastBlit = false;
    int start = 0;
    int end = Integer.MAX_VALUE;
    int series = 0;
    String map = null;
    if (args != null) {
      for (int i=0; i<args.length; i++) {
        if (args[i].startsWith("-") && args.length > 1) {
          if (args[i].equals("-nopix")) pixels = false;
          else if (args[i].equals("-nometa")) doMeta = false;
          else if (args[i].equals("-thumbs")) thumbs = true;
          else if (args[i].equals("-minmax")) minmax = true;
          else if (args[i].equals("-merge")) merge = true;
          else if (args[i].equals("-stitch")) stitch = true;
          else if (args[i].equals("-separate")) separate = true;
          else if (args[i].equals("-omexml")) omexml = true;
          else if (args[i].equals("-normalize")) normalize = true;
          else if (args[i].equals("-fast")) fastBlit = true;
          else if (args[i].equals("-debug")) FormatHandler.setDebug(true);
          else if (args[i].equals("-level")) {
            try {
              FormatHandler.setDebugLevel(Integer.parseInt(args[++i]));
            }
            catch (NumberFormatException exc) { }
          }
          else if (args[i].equals("-range")) {
            try {
              start = Integer.parseInt(args[++i]);
              end = Integer.parseInt(args[++i]);
            }
            catch (NumberFormatException exc) { }
          }
          else if (args[i].equals("-series")) {
            try {
              series = Integer.parseInt(args[++i]);
            }
            catch (NumberFormatException exc) { }
          }
          else if (args[i].equals("-map")) map = args[++i];
          else System.out.println("Ignoring unknown command flag: " + args[i]);
        }
        else {
          if (id == null) id = args[i];
          else System.out.println("Ignoring unknown argument: " + args[i]);
        }
      }
    }
    if (FormatHandler.debug) {
      System.out.println("Debugging at level " + FormatHandler.debugLevel);
    }
    if (id == null) {
      String className = reader.getClass().getName();
      String format = reader.getFormat();
      String[] s = {
        "To test read a file in " + format + " format, run:",
        "  java " + className + " [-nopix] [-nometa] [-thumbs] [-minmax]",
        "    [-merge] [-stitch] [-separate] [-omexml] [-normalize]",
        "    [-fast] [-debug] [-range start end] [-series num] [-map id] file",
        "",
        "      file: the image file to read",
        "    -nopix: read metadata only, not pixels",
        "   -nometa: output only core metadata",
        "   -thumbs: read thumbnails instead of normal pixels",
        "   -minmax: compute min/max statistics",
        "    -merge: combine separate channels into RGB image",
        "   -stitch: stitch files with similar names",
        " -separate: split RGB image into separate channels",
        "   -omexml: populate OME-XML metadata",
        "-normalize: normalize floating point images*",
        "     -fast: paint RGB images as quickly as possible*",
        "    -debug: turn on debugging output",
        "    -range: specify range of planes to read (inclusive)",
        "   -series: specify which image series to read",
        "      -map: specify file on disk to which name should be mapped",
        "",
        "* = may result in loss of precision",
        ""
      };
      for (int i=0; i<s.length; i++) System.out.println(s[i]);
      return false;
    }
    if (map != null) Location.mapId(id, map);
    if (omexml) {
      try {
        // NB: avoid dependencies on optional loci.formats.ome package
        Class c = Class.forName("loci.formats.ome.OMEXMLMetadataStore");
        MetadataStore ms = (MetadataStore) c.newInstance();
        reader.setMetadataStore(ms);
      }
      catch (Throwable t) {
        // NB: error messages for missing OME-Java are printed later
      }
    }

    // check file format
    if (reader instanceof ImageReader) {
      // determine format
      ImageReader ir = (ImageReader) reader;
      System.out.print("Checking file format ");
      System.out.println("[" + ir.getFormat(id) + "]");
    }
    else {
      // verify format
      System.out.print("Checking " + reader.getFormat() + " format ");
      System.out.println(reader.isThisType(id) ? "[yes]" : "[no]");
    }

    System.out.println("Initializing reader");
    if (stitch) {
      reader = new FileStitcher(reader, true);
      String pat = FilePattern.findPattern(new Location(id));
      if (pat != null) id = pat;
    }
    if (separate) reader = new ChannelSeparator(reader);
    if (merge) reader = new ChannelMerger(reader);
    MinMaxCalculator minMaxCalc = null;
    if (minmax) reader = minMaxCalc = new MinMaxCalculator(reader);

    StatusEchoer status = new StatusEchoer();
    reader.addStatusListener(status);

    reader.close();
    reader.setNormalized(normalize);
    reader.setMetadataFiltered(true);
    reader.setMetadataCollected(doMeta);
    long s1 = System.currentTimeMillis();
    reader.setId(id);
    long e1 = System.currentTimeMillis();
    float sec1 = (e1 - s1) / 1000f;
    System.out.println("Initialization took " + sec1 + "s");

    if (!normalize && reader.getPixelType() == FLOAT) {
      throw new FormatException("Sorry, unnormalized floating point " +
        "data is not supported. Please use the '-normalize' option.");
    }

    // read basic metadata
    System.out.println();
    System.out.println("Reading core metadata");
    System.out.println(stitch ?
      "File pattern = " + id : "Filename = " + reader.getCurrentFile());
    if (map != null) System.out.println("Mapped filename = " + map);
    String[] used = reader.getUsedFiles();
    boolean usedValid = used != null && used.length > 0;
    if (usedValid) {
      for (int u=0; u<used.length; u++) {
        if (used[u] == null) {
          usedValid = false;
          break;
        }
      }
    }
    if (!usedValid) {
      System.out.println(
        "************ Warning: invalid used files list ************");
    }
    if (used == null) {
      System.out.println("Used files = null");
    }
    else if (used.length == 0) {
      System.out.println("Used files = []");
    }
    else if (used.length > 1) {
      System.out.println("Used files:");
      for (int u=0; u<used.length; u++) System.out.println("\t" + used[u]);
    }
    else if (!id.equals(used[0])) {
      System.out.println("Used files = [" + used[0] + "]");
    }
    int seriesCount = reader.getSeriesCount();
    System.out.println("Series count = " + seriesCount);
    for (int j=0; j<seriesCount; j++) {
      reader.setSeries(j);

      // read basic metadata for series #i
      int imageCount = reader.getImageCount();
      boolean rgb = reader.isRGB();
      int sizeX = reader.getSizeX();
      int sizeY = reader.getSizeY();
      int sizeZ = reader.getSizeZ();
      int sizeC = reader.getSizeC();
      int sizeT = reader.getSizeT();
      int pixelType = reader.getPixelType();
      int effSizeC = reader.getEffectiveSizeC();
      int rgbChanCount = reader.getRGBChannelCount();
      int[] cLengths = reader.getChannelDimLengths();
      String[] cTypes = reader.getChannelDimTypes();
      int thumbSizeX = reader.getThumbSizeX();
      int thumbSizeY = reader.getThumbSizeY();
      boolean little = reader.isLittleEndian();
      String dimOrder = reader.getDimensionOrder();
      boolean orderCertain = reader.isOrderCertain();
      boolean interleaved = reader.isInterleaved();

      // output basic metadata for series #i
      System.out.println("Series #" + j + ":");
      System.out.println("\tImage count = " + imageCount);
      System.out.print("\tRGB = " + rgb + " (" + rgbChanCount + ")");
      if (merge) System.out.print(" (merged)");
      else if (separate) System.out.print(" (separated)");
      if (rgb != (rgbChanCount != 1)) {
        System.out.println("\t************ Warning: RGB mismatch ************");
      }
      System.out.println();
      System.out.println("\tInterleaved = " + interleaved);
      System.out.println("\tWidth = " + sizeX);
      System.out.println("\tHeight = " + sizeY);
      System.out.println("\tSizeZ = " + sizeZ);
      System.out.print("\tSizeC = " + sizeC);
      if (sizeC != effSizeC) {
        System.out.print(" (effectively " + effSizeC + ")");
      }
      System.out.println();
      for (int i=0; i<cLengths.length; i++) {
        System.out.println("\t\t#" + (i + 1) + " = " +
          cTypes[i] + " (" + cLengths[i] + ")");
      }
      System.out.println("\tSizeT = " + sizeT);
      if (imageCount != sizeZ * effSizeC * sizeT) {
        System.out.println("\t************ Warning: ZCT mismatch ************");
      }
      System.out.println("\tThumbnail size = " +
        thumbSizeX + " x " + thumbSizeY);
      System.out.println("\tEndianness = " +
        (little ? "intel (little)" : "motorola (big)"));
      System.out.println("\tDimension order = " + dimOrder +
        (orderCertain ? " (certain)" : " (uncertain)"));
      System.out.println("\tPixel type = " + getPixelTypeString(pixelType));
      if (doMeta) {
        System.out.println("\t-----");
        int[] indices;
        if (imageCount > 6) {
          int q = imageCount / 2;
          indices = new int[] {
            0, q - 2, q - 1, q, q + 1, q + 2, imageCount - 1
          };
        }
        else if (imageCount > 2) {
          indices = new int[] {0, imageCount / 2, imageCount - 1};
        }
        else if (imageCount > 1) indices = new int[] {0, 1};
        else indices = new int[] {0};
        int[][] zct = new int[indices.length][];
        int[] indices2 = new int[indices.length];
        for (int i=0; i<indices.length; i++) {
          zct[i] = reader.getZCTCoords(indices[i]);
          indices2[i] = reader.getIndex(zct[i][0], zct[i][1], zct[i][2]);
          System.out.print("\tPlane #" + indices[i] + " <=> Z " + zct[i][0] +
            ", C " + zct[i][1] + ", T " + zct[i][2]);
          if (indices[i] != indices2[i]) {
            System.out.println(" [mismatch: " + indices2[i] + "]");
          }
          else System.out.println();
        }
      }
    }
    reader.setSeries(series);
    String s = seriesCount > 1 ? (" series #" + series) : "";
    int pixelType = reader.getPixelType();
    int sizeC = reader.getSizeC();

    // get a priori min/max values
    Double[] preGlobalMin = null, preGlobalMax = null;
    Double[] preKnownMin = null, preKnownMax = null;
    Double[] prePlaneMin = null, prePlaneMax = null;
    boolean preIsMinMaxPop = false;
    if (minmax) {
      preGlobalMin = new Double[sizeC];
      preGlobalMax = new Double[sizeC];
      preKnownMin = new Double[sizeC];
      preKnownMax = new Double[sizeC];
      for (int c=0; c<sizeC; c++) {
        preGlobalMin[c] = minMaxCalc.getChannelGlobalMinimum(c);
        preGlobalMax[c] = minMaxCalc.getChannelGlobalMaximum(c);
        preKnownMin[c] = minMaxCalc.getChannelKnownMinimum(c);
        preKnownMax[c] = minMaxCalc.getChannelKnownMaximum(c);
      }
      prePlaneMin = minMaxCalc.getPlaneMinimum(0);
      prePlaneMax = minMaxCalc.getPlaneMaximum(0);
      preIsMinMaxPop = minMaxCalc.isMinMaxPopulated();
    }

    // read pixels
    if (pixels) {
      System.out.println();
      System.out.print("Reading" + s + " pixel data ");
      status.setVerbose(false);
      int num = reader.getImageCount();
      if (start < 0) start = 0;
      if (start >= num) start = num - 1;
      if (end < 0) end = 0;
      if (end >= num) end = num - 1;
      if (end < start) end = start;

      System.out.print("(" + start + "-" + end + ") ");
      BufferedImage[] images = new BufferedImage[end - start + 1];
      long s2 = System.currentTimeMillis();
      boolean mismatch = false;
      for (int i=start; i<=end; i++) {
        status.setEchoNext(true);
        if (!fastBlit) {
          images[i - start] = thumbs ?
            reader.openThumbImage(i) : reader.openImage(i);
        }
        else {
          int x = reader.getSizeX();
          int y = reader.getSizeY();
          byte[] b = thumbs ? reader.openThumbBytes(i) :
            reader.openBytes(i);
          Object pix = DataTools.makeDataArray(b,
            getBytesPerPixel(reader.getPixelType()),
            reader.getPixelType() == FLOAT,
            reader.isLittleEndian());
          images[i - start] =
            ImageTools.makeImage(ImageTools.make24Bits(pix, x, y,
              false, false), x, y);
        }

        // check for pixel type mismatch
        int pixType = ImageTools.getPixelType(images[i - start]);
        if (pixType != pixelType && !fastBlit) {
          if (!mismatch) {
            System.out.println();
            mismatch = true;
          }
          System.out.println("\tPlane #" + i + ": pixel type mismatch: " +
            getPixelTypeString(pixType) + "/" + getPixelTypeString(pixelType));
        }
        else {
          mismatch = false;
          System.out.print(".");
        }
      }
      long e2 = System.currentTimeMillis();
      if (!mismatch) System.out.print(" ");
      System.out.println("[done]");

      // output timing results
      float sec2 = (e2 - s2) / 1000f;
      float avg = (float) (e2 - s2) / images.length;
      System.out.println(sec2 + "s elapsed (" + avg + "ms per image)");

      if (minmax) {
        // get computed min/max values
        Double[] globalMin = new Double[sizeC];
        Double[] globalMax = new Double[sizeC];
        Double[] knownMin = new Double[sizeC];
        Double[] knownMax = new Double[sizeC];
        for (int c=0; c<sizeC; c++) {
          globalMin[c] = minMaxCalc.getChannelGlobalMinimum(c);
          globalMax[c] = minMaxCalc.getChannelGlobalMaximum(c);
          knownMin[c] = minMaxCalc.getChannelKnownMinimum(c);
          knownMax[c] = minMaxCalc.getChannelKnownMaximum(c);
        }
        Double[] planeMin = minMaxCalc.getPlaneMinimum(0);
        Double[] planeMax = minMaxCalc.getPlaneMaximum(0);
        boolean isMinMaxPop = minMaxCalc.isMinMaxPopulated();

        // output min/max results
        System.out.println();
        System.out.println("Min/max values:");
        for (int c=0; c<sizeC; c++) {
          System.out.println("\tChannel " + c + ":");
          System.out.println("\t\tGlobal minimum = " +
            globalMin[c] + " (initially " + preGlobalMin[c] + ")");
          System.out.println("\t\tGlobal maximum = " +
            globalMax[c] + " (initially " + preGlobalMax[c] + ")");
          System.out.println("\t\tKnown minimum = " +
            knownMin[c] + " (initially " + preKnownMin[c] + ")");
          System.out.println("\t\tKnown maximum = " +
            knownMax[c] + " (initially " + preKnownMax[c] + ")");
        }
        System.out.print("\tFirst plane minimum(s) =");
        if (planeMin == null) System.out.print(" none");
        else {
          for (int subC=0; subC<planeMin.length; subC++) {
            System.out.print(" " + planeMin[subC]);
          }
        }
        System.out.print(" (initially");
        if (prePlaneMin == null) System.out.print(" none");
        else {
          for (int subC=0; subC<prePlaneMin.length; subC++) {
            System.out.print(" " + prePlaneMin[subC]);
          }
        }
        System.out.println(")");
        System.out.print("\tFirst plane maximum(s) =");
        if (planeMax == null) System.out.print(" none");
        else {
          for (int subC=0; subC<planeMax.length; subC++) {
            System.out.print(" " + planeMax[subC]);
          }
        }
        System.out.print(" (initially");
        if (prePlaneMax == null) System.out.print(" none");
        else {
          for (int subC=0; subC<prePlaneMax.length; subC++) {
            System.out.print(" " + prePlaneMax[subC]);
          }
        }
        System.out.println(")");
        System.out.println("\tMin/max populated = " +
          isMinMaxPop + " (initially " + preIsMinMaxPop + ")");
      }

      // display pixels in image viewer
      // NB: avoid dependencies on optional loci.formats.gui package
      ReflectedUniverse r = new ReflectedUniverse();
      try {
        r.exec("import loci.formats.gui.ImageViewer");
        r.exec("viewer = new ImageViewer()");
        r.setVar("reader", reader);
        r.setVar("images", images);
        r.setVar("true", true);
        r.exec("viewer.setImages(reader, images)");
        r.exec("viewer.setVisible(true)");
      }
      catch (ReflectException exc) {
        throw new FormatException(exc);
      }
    }

    // read format-specific metadata table
    if (doMeta) {
      System.out.println();
      System.out.println("Reading" + s + " metadata");
      Hashtable meta = reader.getMetadata();
      String[] keys = (String[]) meta.keySet().toArray(new String[0]);
      Arrays.sort(keys);
      for (int i=0; i<keys.length; i++) {
        System.out.print(keys[i] + ": ");
        System.out.println(reader.getMetadataValue(keys[i]));
      }
    }

    // output OME-XML
    if (omexml) {
      System.out.println();
      System.out.println("Generating OME-XML");
      MetadataStore ms = reader.getMetadataStore();

      // NB: avoid dependencies on optional loci.formats.ome package
      if (ms.getClass().getName().equals(
        "loci.formats.ome.OMEXMLMetadataStore"))
      {
        try {
          Method m = ms.getClass().getMethod("dumpXML", (Class[]) null);
          String xml = (String) m.invoke(ms, (Object[]) null);
          System.out.println(indentXML(xml));
        }
        catch (Throwable t) {
          System.out.println("Error generating OME-XML:");
          t.printStackTrace();
        }
      }
      else {
        System.out.println("OME-Java library not found; no OME-XML available");
      }
    }

    return true;
  }

  /** A utility method for converting a file from the command line. */
  public static boolean testConvert(IFormatWriter writer, String[] args)
    throws FormatException, IOException
  {
    String in = null, out = null;
    if (args != null) {
      for (int i=0; i<args.length; i++) {
        if (args[i].startsWith("-") && args.length > 1) {
          if (args[i].equals("-debug")) FormatHandler.setDebug(true);
          else System.out.println("Ignoring unknown command flag: " + args[i]);
        }
        else {
          if (in == null) in = args[i];
          else if (out == null) out = args[i];
          else System.out.println("Ignoring unknown argument: " + args[i]);
        }
      }
    }
    if (FormatHandler.debug) {
      System.out.println("Debugging at level " + FormatHandler.debugLevel);
    }
    String className = writer.getClass().getName();
    if (in == null || out == null) {
      System.out.println("To convert a file to " + writer.getFormat() +
        " format, run:");
      System.out.println("  java " + className + " [-debug] in_file out_file");
      return false;
    }


    long start = System.currentTimeMillis();
    System.out.print(in + " ");
    ImageReader reader = new ImageReader();
    reader.setId(in);
    System.out.print("[" + reader.getFormat() + "] -> " + out + " ");
    writer.setId(out);
    System.out.print("[" + writer.getFormat() + "] ");
    long mid = System.currentTimeMillis();

    int num = writer.canDoStacks() ? reader.getImageCount() : 1;
    long read = 0, write = 0;
    for (int i=0; i<num; i++) {
      long s = System.currentTimeMillis();
      Image image = reader.openImage(i);
      long m = System.currentTimeMillis();
      writer.saveImage(image, i == num - 1);
      long e = System.currentTimeMillis();
      System.out.print(".");
      read += m - s;
      write += e - m;
    }
    long end = System.currentTimeMillis();
    System.out.println(" [done]");

    // output timing results
    float sec = (end - start) / 1000f;
    long initial = mid - start;
    float readAvg = (float) read / num;
    float writeAvg = (float) write / num;
    System.out.println(sec + "s elapsed (" +
      readAvg + "+" + writeAvg + "ms per image, " + initial + "ms overhead)");

    return true;
  }

  // -- Utility methods - dimensional positions --

  /**
   * Gets the rasterized index corresponding
   * to the given Z, C and T coordinates.
   */
  public static int getIndex(IFormatReader reader, int z, int c, int t)
    throws FormatException
  {
    String order = reader.getDimensionOrder();
    int zSize = reader.getSizeZ();
    int cSize = reader.getEffectiveSizeC();
    int tSize = reader.getSizeT();
    int num = reader.getImageCount();
    return getIndex(order, zSize, cSize, tSize, num, z, c, t);
  }

  /**
   * Gets the rasterized index corresponding
   * to the given Z, C and T coordinates.
   */
  public static int getIndex(String order, int zSize, int cSize, int tSize,
    int num, int z, int c, int t) throws FormatException
  {
    // check DimensionOrder
    if (order == null) throw new FormatException("Dimension order is null");
    if (!order.startsWith("XY")) {
      throw new FormatException("Invalid dimension order: " + order);
    }
    int iz = order.indexOf("Z") - 2;
    int ic = order.indexOf("C") - 2;
    int it = order.indexOf("T") - 2;
    if (iz < 0 || iz > 2 || ic < 0 || ic > 2 || it < 0 || it > 2) {
      throw new FormatException("Invalid dimension order: " + order);
    }

    // check SizeZ
    if (zSize <= 0) throw new FormatException("Invalid Z size: " + zSize);
    if (z < 0 || z >= zSize) {
      throw new FormatException("Invalid Z index: " + z + "/" + zSize);
    }

    // check SizeC
    if (cSize <= 0) throw new FormatException("Invalid C size: " + cSize);
    if (c < 0 || c >= cSize) {
      throw new FormatException("Invalid C index: " + c + "/" + cSize);
    }

    // check SizeT
    if (tSize <= 0) throw new FormatException("Invalid T size: " + tSize);
    if (t < 0 || t >= tSize) {
      throw new FormatException("Invalid T index: " + t + "/" + tSize);
    }

    // check image count
    if (num <= 0) throw new FormatException("Invalid image count: " + num);
    if (num != zSize * cSize * tSize) {
      // if this happens, there is probably a bug in metadata population --
      // either one of the ZCT sizes, or the total number of images --
      // or else the input file is invalid
      throw new FormatException("ZCT size vs image count mismatch (sizeZ=" +
        zSize + ", sizeC=" + cSize + ", sizeT=" + tSize + ", total=" + num +
        ")");
    }

    // assign rasterization order
    int v0 = iz == 0 ? z : (ic == 0 ? c : t);
    int v1 = iz == 1 ? z : (ic == 1 ? c : t);
    int v2 = iz == 2 ? z : (ic == 2 ? c : t);
    int len0 = iz == 0 ? zSize : (ic == 0 ? cSize : tSize);
    int len1 = iz == 1 ? zSize : (ic == 1 ? cSize : tSize);
    int len2 = iz == 2 ? zSize : (ic == 2 ? cSize : tSize);

    return v0 + v1 * len0 + v2 * len0 * len1;
  }

  /**
   * Gets the Z, C and T coordinates corresponding
   * to the given rasterized index value.
   */
  public static int[] getZCTCoords(IFormatReader reader, int index)
    throws FormatException
  {
    String order = reader.getDimensionOrder();
    int zSize = reader.getSizeZ();
    int cSize = reader.getEffectiveSizeC();
    int tSize = reader.getSizeT();
    int num = reader.getImageCount();
    return getZCTCoords(order, zSize, cSize, tSize, num, index);
  }

  /**
   * Gets the Z, C and T coordinates corresponding to the given rasterized
   * index value.
   */
  public static int[] getZCTCoords(String order,
    int zSize, int cSize, int tSize, int num, int index) throws FormatException
  {
    // check DimensionOrder
    if (order == null) throw new FormatException("Dimension order is null");
    if (!order.startsWith("XY")) {
      throw new FormatException("Invalid dimension order: " + order);
    }
    int iz = order.indexOf("Z") - 2;
    int ic = order.indexOf("C") - 2;
    int it = order.indexOf("T") - 2;
    if (iz < 0 || iz > 2 || ic < 0 || ic > 2 || it < 0 || it > 2) {
      throw new FormatException("Invalid dimension order: " + order);
    }

    // check SizeZ
    if (zSize <= 0) throw new FormatException("Invalid Z size: " + zSize);

    // check SizeC
    if (cSize <= 0) throw new FormatException("Invalid C size: " + cSize);

    // check SizeT
    if (tSize <= 0) throw new FormatException("Invalid T size: " + tSize);

    // check image count
    if (num <= 0) throw new FormatException("Invalid image count: " + num);
    if (num != zSize * cSize * tSize) {
      // if this happens, there is probably a bug in metadata population --
      // either one of the ZCT sizes, or the total number of images --
      // or else the input file is invalid
      throw new FormatException("ZCT size vs image count mismatch (sizeZ=" +
        zSize + ", sizeC=" + cSize + ", sizeT=" + tSize + ", total=" + num +
        ")");
    }
    if (index < 0 || index >= num) {
      throw new FormatException("Invalid image index: " + index + "/" + num);
    }

    // assign rasterization order
    int len0 = iz == 0 ? zSize : (ic == 0 ? cSize : tSize);
    int len1 = iz == 1 ? zSize : (ic == 1 ? cSize : tSize);
    //int len2 = iz == 2 ? sizeZ : (ic == 2 ? sizeC : sizeT);
    int v0 = index % len0;
    int v1 = index / len0 % len1;
    int v2 = index / len0 / len1;
    int z = iz == 0 ? v0 : (iz == 1 ? v1 : v2);
    int c = ic == 0 ? v0 : (ic == 1 ? v1 : v2);
    int t = it == 0 ? v0 : (it == 1 ? v1 : v2);

    return new int[] {z, c, t};
  }

  /**
   * Computes a unique 1-D index corresponding to the multidimensional
   * position given in the pos array, using the specified lengths array
   * as the maximum value at each positional dimension.
   */
  public static int positionToRaster(int[] lengths, int[] pos) {
    int[] offsets = new int[lengths.length];
    if (offsets.length > 0) offsets[0] = 1;
    for (int i=1; i<offsets.length; i++) {
      offsets[i] = offsets[i - 1] * lengths[i - 1];
    }
    int raster = 0;
    for (int i=0; i<pos.length; i++) raster += offsets[i] * pos[i];
    return raster;
  }

  /**
   * Computes a unique 3-D position corresponding to the given raster
   * value, using the specified lengths array as the maximum value at
   * each positional dimension.
   */
  public static int[] rasterToPosition(int[] lengths, int raster) {
    int[] offsets = new int[lengths.length];
    if (offsets.length > 0) offsets[0] = 1;
    for (int i=1; i<offsets.length; i++) {
      offsets[i] = offsets[i - 1] * lengths[i - 1];
    }
    int[] pos = new int[lengths.length];
    for (int i=0; i<pos.length; i++) {
      int q = i < pos.length - 1 ? raster % offsets[i + 1] : raster;
      pos[i] = q / offsets[i];
      raster -= q;
    }
    return pos;
  }

  /**
   * Computes the number of raster values for a positional array
   * with the given lengths.
   */
  public static int getRasterLength(int[] lengths) {
    int len = 1;
    for (int i=0; i<lengths.length; i++) len *= lengths[i];
    return len;
  }

  // -- Utility methods - pixel types --

  /**
   * Takes a string value and maps it to one of the pixel type enumerations.
   * @param pixelTypeAsString the pixel type as a string.
   * @return type enumeration value for use with class constants.
   */
  public static int pixelTypeFromString(String pixelTypeAsString) {
    String lowercaseTypeAsString = pixelTypeAsString.toLowerCase();
    for (int i = 0; i < pixelTypes.length; i++) {
      if (pixelTypes[i].equals(lowercaseTypeAsString)) return i;
    }
    throw new RuntimeException("Unknown type: '" + pixelTypeAsString + "'");
  }

  /**
   * Takes a pixel type value and gets a corresponding string representation.
   * @param pixelType the pixel type.
   * @return string value for human-readable output.
   */
  public static String getPixelTypeString(int pixelType) {
    return pixelType < 0 || pixelType >= pixelTypes.length ?
      "unknown (" + pixelType + ")" : pixelTypes[pixelType];
  }

  /**
   * Retrieves how many bytes per pixel the current plane or section has.
   * @param type the pixel type as retrieved from
   *   {@link IFormatReader#getPixelType(String)}.
   * @return the number of bytes per pixel.
   * @see IFormatReader#getPixelType(String)
   */
  public static int getBytesPerPixel(int type) {
    switch (type) {
      case INT8:
      case UINT8:
        return 1;
      case INT16:
      case UINT16:
        return 2;
      case INT32:
      case UINT32:
      case FLOAT:
        return 4;
      case DOUBLE:
        return 8;
    }
    throw new RuntimeException("Unknown type with id: '" + type + "'");
  }

  // -- Utility methods - XML --

  /** Indents XML to be more readable. */
  public static String indentXML(String xml) { return indentXML(xml, 3); }

  /** Indents XML by the given spacing to be more readable. */
  public static String indentXML(String xml, int spacing) {
    int indent = 0;
    StringBuffer sb = new StringBuffer();
    StringTokenizer st = new StringTokenizer(xml, "<>", true);
    boolean element = false;
    while (st.hasMoreTokens()) {
      String token = st.nextToken().trim();
      if (token.equals("")) continue;
      if (token.equals("<")) {
        element = true;
        continue;
      }
      if (element && token.equals(">")) {
        element = false;
        continue;
      }
      if (element && token.startsWith("/")) indent -= spacing;
      for (int j=0; j<indent; j++) sb.append(" ");
      if (element) sb.append("<");
      sb.append(token);
      if (element) sb.append(">");
      sb.append("\n");
      if (element && !token.startsWith("?") &&
        !token.startsWith("/") && !token.endsWith("/"))
      {
        indent += spacing;
      }
    }
    return sb.toString();
  }

  // -- Utility methods - sanity checking

  /**
   * Asserts that the current file is either null, nor not, according to the
   * given flag. If the assertion fails, an IllegalStateException is thrown.
   * @param currentId File name to test.
   * @param notNull True iff id should be non-null.
   * @param depth How far back in the stack the calling method is; this name
   *   is reported as part of the exception message, if available. Use zero
   *   to suppress output of the calling method name.
   */
  public static void assertId(String currentId, boolean notNull, int depth) {
    String msg = null;
    if (currentId == null && notNull) {
      msg = "Current file should not be null; call setId(String) first";
    }
    else if (currentId != null && !notNull) {
      msg = "Current file should be null, but is '" +
        currentId + "'; call close() first";
    }
    if (msg == null) return;

    StackTraceElement[] ste = new Exception().getStackTrace();
    String header;
    if (depth > 0 && ste.length > depth) {
      String c = ste[depth].getClassName();
      if (c.startsWith("loci.formats.")) {
        c = c.substring(c.lastIndexOf(".") + 1);
      }
      header = c + "." + ste[depth].getMethodName() + ": ";
    }
    else header = "";
    throw new IllegalStateException(header + msg);
  }

  // -- Helper classes --

  /** Used by testRead to echo status messages to the console. */
  private static class StatusEchoer implements StatusListener {
    private boolean verbose = true;
    private boolean next = true;

    public void setVerbose(boolean value) { verbose = value; }
    public void setEchoNext(boolean value) { next = value; }

    public void statusUpdated(StatusEvent e) {
      if (verbose) System.out.println("\t" + e.getStatusMessage());
      else if (next) {
        System.out.print(";");
        next = false;
      }
    }
  }

}
