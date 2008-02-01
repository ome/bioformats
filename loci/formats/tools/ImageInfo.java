//
// ImageInfo.java
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

package loci.formats.tools;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.StringTokenizer;
import loci.formats.*;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;

/**
 * ImageInfo is a utility class for reading a file
 * and reporting information about it.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/tools/ImageInfo.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/tools/ImageInfo.java">SVN</a></dd></dl>
 */
public final class ImageInfo {

  // -- Constructor --

  private ImageInfo() { }

  // -- Utility methods --

  /**
   * A utility method for reading a file from the command line,
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
    boolean expand = false;
    boolean omexml = false;
    boolean normalize = false;
    boolean fastBlit = false;
    boolean preload = false;
    String omexmlVersion = null;
    int start = 0;
    int end = Integer.MAX_VALUE;
    int series = 0;
    int xCoordinate = 0, yCoordinate = 0, width = 0, height = 0;
    String swapOrder = null;
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
          else if (args[i].equals("-expand")) expand = true;
          else if (args[i].equals("-omexml")) omexml = true;
          else if (args[i].equals("-normalize")) normalize = true;
          else if (args[i].equals("-fast")) fastBlit = true;
          else if (args[i].equals("-debug")) FormatHandler.setDebug(true);
          else if (args[i].equals("-preload")) preload = true;
          else if (args[i].equals("-version")) omexmlVersion = args[++i];
          else if (args[i].equals("-crop")) {
            StringTokenizer st = new StringTokenizer(args[++i], ",");
            xCoordinate = Integer.parseInt(st.nextToken());
            yCoordinate = Integer.parseInt(st.nextToken());
            width = Integer.parseInt(st.nextToken());
            height = Integer.parseInt(st.nextToken());
          }
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
          else if (args[i].equals("-swap")) {
            swapOrder = args[++i].toUpperCase();
          }
          else if (args[i].equals("-map")) map = args[++i];
          else LogTools.println("Ignoring unknown command flag: " + args[i]);
        }
        else {
          if (id == null) id = args[i];
          else LogTools.println("Ignoring unknown argument: " + args[i]);
        }
      }
    }
    if (FormatHandler.debug) {
      LogTools.println("Debugging at level " + FormatHandler.debugLevel);
    }
    if (id == null) {
      String className = reader.getClass().getName();
      String fmt = reader instanceof ImageReader ? "any" : reader.getFormat();
      String[] s = {
        "To test read a file in " + fmt + " format, run:",
        "  showinf [-nopix] [-nometa] [-thumbs] [-minmax] ",
        "    [-merge] [-stitch] [-separate] [-expand] [-omexml]",
        "    [-normalize] [-fast] [-debug] [-range start end] [-series num]",
        "    [-swap order] [-map id] [-preload] [-version v] file",
        "",
        "      file: the image file to read",
        "    -nopix: read metadata only, not pixels",
        "   -nometa: output only core metadata",
        "   -thumbs: read thumbnails instead of normal pixels",
        "   -minmax: compute min/max statistics",
        "    -merge: combine separate channels into RGB image",
        "   -stitch: stitch files with similar names",
        " -separate: split RGB image into separate channels",
        "   -expand: expand indexed color to RGB",
        "   -omexml: populate OME-XML metadata",
        "-normalize: normalize floating point images*",
        "     -fast: paint RGB images as quickly as possible*",
        "    -debug: turn on debugging output",
        "    -range: specify range of planes to read (inclusive)",
        "   -series: specify which image series to read",
        "     -swap: override the default dimension order",
        "      -map: specify file on disk to which name should be mapped",
        "  -preload: pre-read entire file into a buffer; significantly",
        "            reduces the time required to read the images, but",
        "            requires more memory",
        "  -version: specify which OME-XML version should be generated",
        "     -crop: crop images before displaying; argument is 'x,y,w,h'",
        "",
        "* = may result in loss of precision",
        ""
      };
      for (int i=0; i<s.length; i++) LogTools.println(s[i]);
      return false;
    }
    if (map != null) Location.mapId(id, map);
    else if (preload) {
      RandomAccessStream f = new RandomAccessStream(id);
      byte[] b = new byte[(int) f.length()];
      f.read(b);
      f.close();
      RABytes file = new RABytes(b);
      Location.mapFile(id, file);
    }

    if (omexml) {
      if (omexmlVersion == null) omexmlVersion = "2003-FC";
      reader.setOriginalMetadataPopulated(true);
      MetadataStore store =
        MetadataTools.createOMEXMLMetadata(null, omexmlVersion);
      if (store != null) reader.setMetadataStore(store);
    }

    // check file format
    if (reader instanceof ImageReader) {
      // determine format
      ImageReader ir = (ImageReader) reader;
      LogTools.print("Checking file format ");
      LogTools.println("[" + ir.getFormat(id) + "]");
    }
    else {
      // verify format
      LogTools.print("Checking " + reader.getFormat() + " format ");
      LogTools.println(reader.isThisType(id) ? "[yes]" : "[no]");
    }

    LogTools.println("Initializing reader");
    if (stitch) {
      reader = new FileStitcher(reader, true);
      String pat = FilePattern.findPattern(new Location(id));
      if (pat != null) id = pat;
    }
    if (expand) reader = new ChannelFiller(reader);
    if (separate) reader = new ChannelSeparator(reader);
    if (merge) reader = new ChannelMerger(reader);
    MinMaxCalculator minMaxCalc = null;
    if (minmax) reader = minMaxCalc = new MinMaxCalculator(reader);
    DimensionSwapper dimSwapper = null;
    if (swapOrder != null) reader = dimSwapper = new DimensionSwapper(reader);

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
    LogTools.println("Initialization took " + sec1 + "s");
    if (swapOrder != null) dimSwapper.swapDimensions(swapOrder);

    if (!normalize && reader.getPixelType() == FormatTools.FLOAT) {
      LogTools.println("Warning: Java does not support " +
        "display of unnormalized floating point data.");
      LogTools.println("Please use the '-normalize' option " +
        "to avoid receiving a cryptic exception.");
    }

    // read basic metadata
    LogTools.println();
    LogTools.println("Reading core metadata");
    LogTools.println(stitch ?
      "File pattern = " + id : "Filename = " + reader.getCurrentFile());
    if (map != null) LogTools.println("Mapped filename = " + map);
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
      LogTools.println(
        "************ Warning: invalid used files list ************");
    }
    if (used == null) {
      LogTools.println("Used files = null");
    }
    else if (used.length == 0) {
      LogTools.println("Used files = []");
    }
    else if (used.length > 1) {
      LogTools.println("Used files:");
      for (int u=0; u<used.length; u++) LogTools.println("\t" + used[u]);
    }
    else if (!id.equals(used[0])) {
      LogTools.println("Used files = [" + used[0] + "]");
    }
    int seriesCount = reader.getSeriesCount();
    LogTools.println("Series count = " + seriesCount);
    MetadataStore ms = reader.getMetadataStore();
    MetadataRetrieve mr = ms instanceof MetadataRetrieve ?
      (MetadataRetrieve) ms : null;
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
      boolean indexed = reader.isIndexed();
      byte[][] table8 = reader.get8BitLookupTable();
      short[][] table16 = reader.get16BitLookupTable();
      int[] cLengths = reader.getChannelDimLengths();
      String[] cTypes = reader.getChannelDimTypes();
      int thumbSizeX = reader.getThumbSizeX();
      int thumbSizeY = reader.getThumbSizeY();
      boolean little = reader.isLittleEndian();
      String dimOrder = reader.getDimensionOrder();
      boolean orderCertain = reader.isOrderCertain();
      boolean interleaved = reader.isInterleaved();
      boolean metadataComplete = reader.isMetadataComplete();

      // output basic metadata for series #i
      String seriesName = mr == null ? null : mr.getImageName(j);
      LogTools.println("Series #" + j +
        (seriesName == null ? "" : " -- " + seriesName) + ":");
      LogTools.println("\tImage count = " + imageCount);
      LogTools.print("\tRGB = " + rgb + " (" + rgbChanCount + ")");
      if (merge) LogTools.print(" (merged)");
      else if (separate) LogTools.print(" (separated)");
      LogTools.println();
      if (rgb != (rgbChanCount != 1)) {
        LogTools.println("\t************ Warning: RGB mismatch ************");
      }
      LogTools.println("\tInterleaved = " + interleaved);
      LogTools.print("\tIndexed = " + indexed);
      if (table8 != null) {
        int len0 = table8.length;
        int len1 = table8[0].length;
        LogTools.print(" (8-bit LUT: " + table8.length + " x ");
        LogTools.print(table8[0] == null ? "null" : "" + table8[0].length);
        LogTools.print(")");
      }
      if (table16 != null) {
        int len0 = table16.length;
        int len1 = table16[0].length;
        LogTools.print(" (16-bit LUT: " + table16.length + " x ");
        LogTools.print(table16[0] == null ? "null" : "" + table16[0].length);
        LogTools.print(")");
      }
      LogTools.println();
      if (indexed && table8 == null && table16 == null) {
        LogTools.println("\t************ Warning: no LUT ************");
      }
      if (table8 != null && table16 != null) {
        LogTools.println(
          "\t************ Warning: multiple LUTs ************");
      }
      LogTools.println("\tWidth = " + sizeX);
      LogTools.println("\tHeight = " + sizeY);
      LogTools.println("\tSizeZ = " + sizeZ);
      LogTools.println("\tSizeT = " + sizeT);
      LogTools.print("\tSizeC = " + sizeC);
      if (sizeC != effSizeC) {
        LogTools.print(" (effectively " + effSizeC + ")");
      }
      int cProduct = 1;
      if (cLengths.length == 1 && FormatTools.CHANNEL.equals(cTypes[0])) {
        cProduct = cLengths[0];
      }
      else {
        LogTools.print(" (");
        for (int i=0; i<cLengths.length; i++) {
          if (i > 0) LogTools.print(" x ");
          LogTools.print(cLengths[i] + " " + cTypes[i]);
          cProduct *= cLengths[i];
        }
        LogTools.print(")");
      }
      LogTools.println();
      if (cLengths.length == 0 || cProduct != sizeC) {
        LogTools.println(
          "\t************ Warning: C dimension mismatch ************");
      }
      if (imageCount != sizeZ * effSizeC * sizeT) {
        LogTools.println("\t************ Warning: ZCT mismatch ************");
      }
      LogTools.println("\tThumbnail size = " +
        thumbSizeX + " x " + thumbSizeY);
      LogTools.println("\tEndianness = " +
        (little ? "intel (little)" : "motorola (big)"));
      LogTools.println("\tDimension order = " + dimOrder +
        (orderCertain ? " (certain)" : " (uncertain)"));
      LogTools.println("\tPixel type = " +
        FormatTools.getPixelTypeString(pixelType));
      LogTools.println("\tMetadata complete = " + metadataComplete);
      if (doMeta) {
        LogTools.println("\t-----");
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
          LogTools.print("\tPlane #" + indices[i] + " <=> Z " + zct[i][0] +
            ", C " + zct[i][1] + ", T " + zct[i][2]);
          if (indices[i] != indices2[i]) {
            LogTools.println(" [mismatch: " + indices2[i] + "]");
          }
          else LogTools.println();
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
      LogTools.println();
      LogTools.print("Reading" + s + " pixel data ");
      status.setVerbose(false);
      int num = reader.getImageCount();
      if (start < 0) start = 0;
      if (start >= num) start = num - 1;
      if (end < 0) end = 0;
      if (end >= num) end = num - 1;
      if (end < start) end = start;

      if (width == 0) width = reader.getSizeX();
      if (height == 0) height = reader.getSizeY();

      LogTools.print("(" + start + "-" + end + ") ");
      BufferedImage[] images = new BufferedImage[end - start + 1];
      long s2 = System.currentTimeMillis();
      boolean mismatch = false;
      for (int i=start; i<=end; i++) {
        status.setEchoNext(true);
        if (!fastBlit) {
          images[i - start] = thumbs ? reader.openThumbImage(i) :
            reader.openImage(i, xCoordinate, yCoordinate, width, height);
        }
        else {
          int x = reader.getSizeX();
          int y = reader.getSizeY();
          byte[] b = thumbs ? reader.openThumbBytes(i) :
            reader.openBytes(i, xCoordinate, yCoordinate, width, height);
          Object pix = DataTools.makeDataArray(b,
            FormatTools.getBytesPerPixel(reader.getPixelType()),
            reader.getPixelType() == FormatTools.FLOAT,
            reader.isLittleEndian());
          images[i - start] =
            ImageTools.makeImage(ImageTools.make24Bits(pix, x, y,
              false, false), x, y);
        }

        // check for pixel type mismatch
        int pixType = ImageTools.getPixelType(images[i - start]);
        if (pixType != pixelType && pixType != pixelType + 1 && !fastBlit) {
          if (!mismatch) {
            LogTools.println();
            mismatch = true;
          }
          LogTools.println("\tPlane #" + i + ": pixel type mismatch: " +
            FormatTools.getPixelTypeString(pixType) + "/" +
            FormatTools.getPixelTypeString(pixelType));
        }
        else {
          mismatch = false;
          LogTools.print(".");
        }
      }
      long e2 = System.currentTimeMillis();
      if (!mismatch) LogTools.print(" ");
      LogTools.println("[done]");

      // output timing results
      float sec2 = (e2 - s2) / 1000f;
      float avg = (float) (e2 - s2) / images.length;
      LogTools.println(sec2 + "s elapsed (" + avg + "ms per image)");

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
        LogTools.println();
        LogTools.println("Min/max values:");
        for (int c=0; c<sizeC; c++) {
          LogTools.println("\tChannel " + c + ":");
          LogTools.println("\t\tGlobal minimum = " +
            globalMin[c] + " (initially " + preGlobalMin[c] + ")");
          LogTools.println("\t\tGlobal maximum = " +
            globalMax[c] + " (initially " + preGlobalMax[c] + ")");
          LogTools.println("\t\tKnown minimum = " +
            knownMin[c] + " (initially " + preKnownMin[c] + ")");
          LogTools.println("\t\tKnown maximum = " +
            knownMax[c] + " (initially " + preKnownMax[c] + ")");
        }
        LogTools.print("\tFirst plane minimum(s) =");
        if (planeMin == null) LogTools.print(" none");
        else {
          for (int subC=0; subC<planeMin.length; subC++) {
            LogTools.print(" " + planeMin[subC]);
          }
        }
        LogTools.print(" (initially");
        if (prePlaneMin == null) LogTools.print(" none");
        else {
          for (int subC=0; subC<prePlaneMin.length; subC++) {
            LogTools.print(" " + prePlaneMin[subC]);
          }
        }
        LogTools.println(")");
        LogTools.print("\tFirst plane maximum(s) =");
        if (planeMax == null) LogTools.print(" none");
        else {
          for (int subC=0; subC<planeMax.length; subC++) {
            LogTools.print(" " + planeMax[subC]);
          }
        }
        LogTools.print(" (initially");
        if (prePlaneMax == null) LogTools.print(" none");
        else {
          for (int subC=0; subC<prePlaneMax.length; subC++) {
            LogTools.print(" " + prePlaneMax[subC]);
          }
        }
        LogTools.println(")");
        LogTools.println("\tMin/max populated = " +
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
      LogTools.println();
      LogTools.println("Reading" + s + " metadata");
      Hashtable meta = reader.getMetadata();
      String[] keys = (String[]) meta.keySet().toArray(new String[0]);
      Arrays.sort(keys);
      for (int i=0; i<keys.length; i++) {
        LogTools.print(keys[i] + ": ");
        LogTools.println(reader.getMetadataValue(keys[i]));
      }
    }

    // output and validate OME-XML
    if (omexml) {
      LogTools.println();
      String version = MetadataTools.getOMEXMLVersion(ms);
      if (version == null) LogTools.println("Generating OME-XML");
      else {
        LogTools.println("Generating OME-XML (schema version " + version + ")");
      }
      if (MetadataTools.isOMEXMLMetadata(ms)) {
        String xml = MetadataTools.getOMEXML((MetadataRetrieve) ms);
        LogTools.println(XMLTools.indentXML(xml));
        MetadataTools.validateOMEXML(xml);
      }
      else {
        LogTools.println("The metadata could not be converted to OME-XML.");
        if (omexmlVersion == null) {
          LogTools.println("The OME-Java library is probably not available.");
        }
        else {
          LogTools.println(omexmlVersion +
            " is probably not a legal schema version.");
        }
      }
    }

    return true;
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    if (!testRead(new ImageReader(), args)) System.exit(1);
  }

  // -- Helper classes --

  /** Used by testRead to echo status messages to the console. */
  private static class StatusEchoer implements StatusListener {
    private boolean verbose = true;
    private boolean next = true;

    public void setVerbose(boolean value) { verbose = value; }
    public void setEchoNext(boolean value) { next = value; }

    public void statusUpdated(StatusEvent e) {
      if (verbose) LogTools.println("\t" + e.getStatusMessage());
      else if (next) {
        LogTools.print(";");
        next = false;
      }
    }
  }

}
