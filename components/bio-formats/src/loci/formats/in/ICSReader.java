//
// ICSReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.ByteVector;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * ICSReader is the file format reader for ICS (Image Cytometry Standard)
 * files. More information on ICS can be found at http://libics.sourceforge.net
 *
 * TODO : remove sub-C logic once N-dimensional support is in place
 *        see https://skyking.microscopy.wisc.edu/trac/java/ticket/398
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/ICSReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/ICSReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class ICSReader extends FormatReader {

  // -- Constants --

  /** Newline characters. */
  public static final String NL = "\r\n";

  public static final String[] DATE_FORMATS = {
    "EEEE, MMMM dd, yyyy HH:mm:ss",
    "EEE dd MMMM yyyy HH:mm:ss",
    "EEE MMM dd HH:mm:ss yyyy",
    "EE dd MMM yyyy HH:mm:ss z"
  };

  /** Metadata field categories. */
  private static final String[] CATEGORIES = new String[] {
    "ics_version", "filename", "source", "layout", "representation",
    "parameter", "sensor", "history", "document", "view.*", "end", "file",
    "offset", "parameters", "order", "sizes", "coordinates", "significant_bits",
    "format", "sign", "compression", "byte_order", "origin", "scale", "units",
    "labels", "SCIL_TYPE", "type", "model", "s_params", "gain.*", "dwell",
    "shutter.*", "pinhole", "laser.*", "version", "objective", "PassCount",
    "step.*", "date", "GMTdate", "label", "software", "author", "length",
    "Z (background)", "dimensions", "rep period", "image form", "extents",
    "offsets", "region", "expon. order", "a.*", "tau.*", "noiseval",
    "excitationfwhm", "created on", "text", "other text", "mode",
    "CFD limit low", "CFD limit high", "CFD zc level", "CFD holdoff",
    "SYNC zc level", "SYNC freq div", "SYNC holdoff", "TAC range", "TAC gain",
    "TAC offset", "TAC limit low", "ADC resolution", "Ext latch delay",
    "collection time", "repeat time", "stop on time", "stop on O'flow",
    "dither range", "count increment", "memory bank", "sync threshold",
    "dead time comp", "polarity", "line compressio", "scan flyback",
    "scan borders", "pixel time", "pixel clock", "trigger", "scan pixels x",
    "scan pixels y", "routing chan x", "routing chan y", "detector type",
    "channel.*", "filter.*", "wavelength.*", "black level.*", "ht.*",
    "scan resolution", "scan speed", "scan zoom", "scan pattern", "scan pos x",
    "scan pos y", "transmission", "x amplitude", "y amplitude", "x offset",
    "y offset", "x delay", "y delay", "beam zoom", "mirror .*", "direct turret",
    "desc exc turret", "desc emm turret", "cube", "stage_xyzum",
    "cube descriptio", "camera", "exposure", "bits/pixel", "binning", "left",
    "top", "cols", "rows", "significant_channels", "allowedlinemodes",
    "real_significant_bits", "sample_width", "range", "ch", "lower_limit",
    "higher_limit", "passcount", "detector", "dateGMT", "RefrInxMedium",
    "RefrInxLensMedium", "Channels", "PinholeRadius", "LambdaEx", "LambdaEm",
    "ExPhotonCnt", "RefInxMedium", "NumAperture", "RefInxLensMedium",
    "PinholeSpacing", "power", "name", "Type", "Magnification", "NA",
    "WorkingDistance", "Immersion", "Pinhole", "Channel .*", "Gain .*",
    "Shutter .*", "Position", "Size", "Port", "Cursor", "Color", "BlackLevel",
    "Saturation", "Gamma", "IntZoom", "Live", "Synchronize", "ShowIndex",
    "AutoResize", "UseUnits", "Zoom", "IgnoreAspect", "ShowCursor", "ShowAll",
    "Axis", "Order", "Tile", "DimViewOption", "channels", "pinholeradius",
    "refrinxmedium", "numaperture", "refrinxlensmedium"
  };

  // -- Fields --

  /** Current filename. */
  private String currentIcsId;
  private String currentIdsId;

  /** Flag indicating whether current file is v2.0. */
  private boolean versionTwo;

  /** Image data. */
  private byte[] data;

  /** Offset to pixel data. */
  private long offset;

  /** Whether or not the pixels are GZIP-compressed. */
  private boolean gzip;

  /** Whether or not the image is inverted along the Y axis. */
  private boolean invertY;

  /** Whether or not the channels represent lifetime histogram bins. */
  private boolean lifetime;

  /** The length of each channel axis. */
  private Vector<Integer> channelLengths;

  /** The type of each channel axis. */
  private Vector<String> channelTypes;

  private int prevImage;

  // -- Constructor --

  /** Constructs a new ICSReader. */
  public ICSReader() {
    super("Image Cytometry Standard", new String[] {"ics", "ids"});
    domains = new String[] {FormatTools.LM_DOMAIN, FormatTools.FLIM_DOMAIN,
      FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    // check if we have a v2 ICS file - means there is no companion IDS file
    RandomAccessInputStream f = new RandomAccessInputStream(id);
    boolean singleFile = f.readString(17).trim().equals("ics_version\t2.0");
    f.close();
    return singleFile;
  }

  /* @see loci.formats.IFormatReader#getChannelDimLengths() */
  public int[] getChannelDimLengths() {
    FormatTools.assertId(currentId, true, 1);
    int[] len = new int[channelLengths.size()];
    for (int i=0; i<len.length; i++) {
      len[i] = channelLengths.get(i).intValue();
    }
    return len;
  }

  /* @see loci.formats.IFormatReader#getChannelDimTypes() */
  public String[] getChannelDimTypes() {
    FormatTools.assertId(currentId, true, 1);
    return channelTypes.toArray(new String[channelTypes.size()]);
  }

  /* @see loci.formats.IFormatReader#isInterleaved(int) */
  public boolean isInterleaved(int subC) {
    FormatTools.assertId(currentId, true, 1);
    return subC == 0 && isRGB();
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int len = FormatTools.getPlaneSize(this);
    int pixel = bpp * getRGBChannelCount();
    int rowLen = FormatTools.getPlaneSize(this, w, 1);

    int[] coordinates = getZCTCoords(no);
    int[] prevCoordinates = getZCTCoords(prevImage);

    in.seek(offset + no * len);

    int sizeC = lifetime ? 1 : getSizeC();

    if (!isRGB() && sizeC > 4 && channelLengths.size() == 1) {
      // channels are stored interleaved, but because there are more than we
      // can display as RGB, we need to separate them
      in.seek(offset + len * getIndex(coordinates[0], 0, coordinates[2]));
      if (!gzip && data == null) {
        data = new byte[len * getSizeC()];
        in.read(data);
      }
      else if (!gzip && (coordinates[0] != prevCoordinates[0] ||
        coordinates[2] != prevCoordinates[2]))
      {
        in.read(data);
      }

      for (int row=y; row<h + y; row++) {
        for (int col=x; col<w + x; col++) {
          System.arraycopy(data, bpp * ((no % getSizeC()) + sizeC *
            (row * getSizeX() + col)), buf, bpp * (row * w + col), bpp);
        }
      }
    }
    else if (gzip) {
      if (x == 0 && getSizeX() == w) {
        System.arraycopy(data, len * no + y * rowLen, buf, 0, h * rowLen);
      }
      else {
        for (int row=y; row<h + y; row++) {
          System.arraycopy(data, len * no + pixel * (row * getSizeX() + x),
            buf, row * rowLen, rowLen);
        }
      }
    }
    else {
      readPlane(in, x, y, w, h, buf);
    }

    if (invertY) {
      byte[] row = new byte[rowLen];
      for (int r=0; r<h/2; r++) {
        int topOffset = r * rowLen;
        int bottomOffset = (h - r - 1) * rowLen;
        System.arraycopy(buf, topOffset, row, 0, rowLen);
        System.arraycopy(buf, bottomOffset, buf, topOffset, rowLen);
        System.arraycopy(row, 0, buf, bottomOffset, rowLen);
      }
    }

    prevImage = no;

    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (versionTwo) {
      return noPixels ? null : new String[] {currentIcsId};
    }
    return noPixels ? new String[] {currentIcsId} :
      new String[] {currentIcsId, currentIdsId};
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      currentIcsId = null;
      currentIdsId = null;
      data = null;
      versionTwo = false;
      gzip = false;
      invertY = false;
      lifetime = false;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("ICSReader.initFile(" + id + ")");
    super.initFile(id);

    status("Finding companion file");

    String icsId = id, idsId = id;
    int dot = id.lastIndexOf(".");
    String ext = dot < 0 ? "" : id.substring(dot + 1).toLowerCase();
    if (ext.equals("ics")) {
      // convert C to D regardless of case
      char[] c = idsId.toCharArray();
      c[c.length - 2]++;
      idsId = new String(c);
    }
    else if (ext.equals("ids")) {
      // convert D to C regardless of case
      char[] c = icsId.toCharArray();
      c[c.length - 2]--;
      icsId = new String(c);
    }

    if (icsId == null) throw new FormatException("No ICS file found.");
    Location icsFile = new Location(icsId);
    if (!icsFile.exists()) throw new FormatException("ICS file not found.");

    status("Checking file version");

    // check if we have a v2 ICS file - means there is no companion IDS file
    RandomAccessInputStream f = new RandomAccessInputStream(icsId);
    if (f.readString(17).trim().equals("ics_version\t2.0")) {
      in = new RandomAccessInputStream(icsId);
      versionTwo = true;
    }
    else {
      if (idsId == null) throw new FormatException("No IDS file found.");
      Location idsFile = new Location(idsId);
      if (!idsFile.exists()) throw new FormatException("IDS file not found.");
      currentIdsId = idsId;
      in = new RandomAccessInputStream(currentIdsId);
    }
    f.close();

    currentIcsId = icsId;

    status("Reading metadata");

    Float[] pixelSizes = null;
    String[] axes = null;
    int[] axisLengths = null;
    String byteOrder = null, rFormat = null, compression = null;

    // parse key/value pairs from beginning of ICS file

    RandomAccessInputStream reader = new RandomAccessInputStream(icsId);
    reader.seek(0);
    reader.readString(NL);
    String line = reader.readString(NL);
    boolean signed = false;

    StringBuffer textBlock = new StringBuffer();
    float[] sizes = null;

    Integer[] emWaves = null, exWaves = null;
    Float[] stagePos = null;
    String imageName = null, date = null, description = null;
    Float magnification = null, lensNA = null, workingDistance = null;
    String objectiveModel = null, immersion = null, lastName = null;
    Hashtable<Integer, Float> gains = new Hashtable<Integer, Float>();
    Hashtable<Integer, Float> pinholes = new Hashtable<Integer, Float>();
    Hashtable<Integer, Integer> wavelengths = new Hashtable<Integer, Integer>();
    Hashtable<Integer, String> channelNames = new Hashtable<Integer, String>();

    while (line != null && !line.trim().equals("end") &&
      reader.getFilePointer() < reader.length() - 1)
    {
      String[] tokens = null;
      if (line.indexOf("\t") != -1) tokens = line.split("\t");
      else tokens = line.split(" ");
      StringBuffer key = new StringBuffer();
      for (int q=0; q<tokens.length; q++) {
        tokens[q] = tokens[q].trim();
        if (tokens[q].length() == 0) continue;

        boolean foundValue = true;
        for (int i=0; i<CATEGORIES.length; i++) {
          if (tokens[q].matches(CATEGORIES[i])) {
            foundValue = false;
            break;
          }
        }
        if (!foundValue) {
          key.append(tokens[q]);
          key.append(" ");
          continue;
        }

        StringBuffer value = new StringBuffer(tokens[q++]);
        for (; q<tokens.length; q++) {
          value.append(" ");
          value.append(tokens[q].trim());
        }
        String k = key.toString().trim().replaceAll("\t", " ");
        String v = value.toString().trim();
        addGlobalMeta(k, v);

        Float floatValue = null;
        try {
          floatValue = new Float(v);
        }
        catch (NumberFormatException e) {
          traceDebug(e);
        }

        if (k.equalsIgnoreCase("layout sizes")) {
          StringTokenizer t = new StringTokenizer(v);
          axisLengths = new int[t.countTokens()];
          for (int n=0; n<axisLengths.length; n++) {
            try {
              axisLengths[n] = Integer.parseInt(t.nextToken().trim());
            }
            catch (NumberFormatException e) {
              traceDebug(e);
            }
          }
        }
        else if (k.equalsIgnoreCase("layout order")) {
          StringTokenizer t = new StringTokenizer(v);
          axes = new String[t.countTokens()];
          for (int n=0; n<axes.length; n++) {
            axes[n] = t.nextToken().trim();
          }
        }
        else if (k.equalsIgnoreCase("representation byte_order")) {
          byteOrder = v;
        }
        else if (k.equalsIgnoreCase("representation format")) rFormat = v;
        else if (k.equalsIgnoreCase("representation compression")) {
          compression = v;
        }
        else if (k.equalsIgnoreCase("parameter scale")) {
          StringTokenizer t = new StringTokenizer(v);
          pixelSizes = new Float[t.countTokens()];
          for (int n=0; n<pixelSizes.length; n++) {
            try {
              pixelSizes[n] = new Float(t.nextToken().trim());
            }
            catch (NumberFormatException e) {
              traceDebug(e);
            }
          }
        }
        else if (k.equalsIgnoreCase("representation sign")) {
          signed = v.equals("signed");
        }
        else if (k.equalsIgnoreCase("sensor s_params LambdaEm")) {
          String[] waves = v.split(" ");
          emWaves = new Integer[waves.length];
          for (int n=0; n<emWaves.length; n++) {
            try {
              emWaves[n] = new Integer((int) Float.parseFloat(waves[n]));
            }
            catch (NumberFormatException e) {
              traceDebug(e);
            }
          }
        }
        else if (k.equalsIgnoreCase("sensor s_params LambdaEx")) {
          String[] waves = v.split(" ");
          exWaves = new Integer[waves.length];
          for (int n=0; n<exWaves.length; n++) {
            try {
              exWaves[n] = new Integer((int) Float.parseFloat(waves[n]));
            }
            catch (NumberFormatException e) {
              traceDebug(e);
            }
          }
        }
        else if (k.equalsIgnoreCase("history software") &&
          v.indexOf("SVI") != -1)
        {
          // ICS files written by SVI Huygens are inverted on the Y axis
          invertY = true;
        }
        else if (k.equalsIgnoreCase("history") ||
          k.equalsIgnoreCase("history text"))
        {
          textBlock.append(v);
          textBlock.append("\n");
          metadata.remove(k);
        }
        else if (k.equalsIgnoreCase("filename")) imageName = v;
        else if (k.equalsIgnoreCase("history date") ||
          k.equalsIgnoreCase("history created on"))
        {
          if (v.indexOf(" ") == -1) continue;
          date = v.substring(0, v.lastIndexOf(" "));
          date = DateTools.formatDate(date, DATE_FORMATS);
        }
        else if (k.startsWith("history gain")) {
          Integer n = new Integer(0);
          try {
            n = new Integer(k.substring(12).trim());
            n = new Integer(n.intValue() - 1);
          }
          catch (NumberFormatException e) { }
          if (floatValue != null) gains.put(n, floatValue);
        }
        else if (k.startsWith("history laser") && k.endsWith("wavelength")) {
          int laser = Integer.parseInt(k.substring(13, k.indexOf(" ", 13))) - 1;
          v = v.replaceAll("nm", "").trim();
          try {
            wavelengths.put(new Integer(laser), new Integer(v));
          }
          catch (NumberFormatException e) {
            traceDebug(e);
          }
        }
        else if (k.equalsIgnoreCase("history objective type")) {
          objectiveModel = v;
        }
        else if (k.equalsIgnoreCase("history objective immersion")) {
          immersion = v;
        }
        else if (k.equalsIgnoreCase("history objective NA")) {
          lensNA = floatValue;
        }
        else if (k.equalsIgnoreCase("history objective WorkingDistance")) {
          workingDistance = floatValue;
        }
        else if (k.equalsIgnoreCase("history objective magnification")) {
          magnification = floatValue;
        }
        else if (k.equalsIgnoreCase("sensor s_params PinholeRadius")) {
          String[] pins = v.split(" ");
          int channel = 0;
          for (int n=0; n<pins.length; n++) {
            if (pins[n].trim().equals("")) continue;
            try {
              pinholes.put(new Integer(channel++), new Float(pins[n]));
            }
            catch (NumberFormatException e) {
              traceDebug(e);
            }
          }
        }
        else if (k.equalsIgnoreCase("history author")) lastName = v;
        else if (k.equalsIgnoreCase("history extents")) {
          String[] lengths = v.split(" ");
          sizes = new float[lengths.length];
          for (int n=0; n<sizes.length; n++) {
            try {
              sizes[n] = Float.parseFloat(lengths[n].trim());
            }
            catch (NumberFormatException e) {
              traceDebug(e);
            }
          }
        }
        else if (k.equalsIgnoreCase("history stage_xyzum")) {
          String[] positions = v.split(" ");
          stagePos = new Float[positions.length];
          for (int n=0; n<stagePos.length; n++) {
            try {
              stagePos[n] = new Float(positions[n]);
            }
            catch (NumberFormatException e) {
              traceDebug(e);
            }
          }
        }
        else if (k.equalsIgnoreCase("history other text")) {
          description = v;
        }
        else if (k.startsWith("history step") && k.endsWith("name")) {
          Integer n = new Integer(k.substring(12, k.indexOf(" ", 12)));
          channelNames.put(n, v);
        }
        else if (k.equalsIgnoreCase("history type")) {
          // HACK - support for Gray Institute at Oxford's ICS lifetime data
          if (v.equalsIgnoreCase("time resolved")) lifetime = true;
        }
        else if (k.equalsIgnoreCase("parameter ch")) {
          String[] names = v.split(" ");
          for (int n=0; n<names.length; n++) {
            channelNames.put(new Integer(n), names[n].trim());
          }
        }
      }
      line = reader.readString(NL);
    }
    reader.close();

    addGlobalMeta("history text", textBlock.toString());

    status("Populating core metadata");

    core[0].rgb = false;
    core[0].dimensionOrder = "XY";

    // find axis sizes

    channelLengths = new Vector<Integer>();
    channelTypes = new Vector<String>();

    int bitsPerPixel = 0;
    for (int i=0; i<axes.length; i++) {
      if (i >= axisLengths.length) break;
      if (axes[i].equals("bits")) {
        bitsPerPixel = axisLengths[i];
        while (bitsPerPixel % 8 != 0) bitsPerPixel++;
        if (bitsPerPixel == 24 || bitsPerPixel == 48) bitsPerPixel /= 3;
      }
      else if (axes[i].equals("x")) {
        core[0].sizeX = axisLengths[i];
      }
      else if (axes[i].equals("y")) {
        core[0].sizeY = axisLengths[i];
      }
      else if (axes[i].equals("z")) {
        core[0].sizeZ = axisLengths[i];
        if (getDimensionOrder().indexOf("Z") == -1) {
          core[0].dimensionOrder += "Z";
        }
      }
      else if (axes[i].equals("t")) {
        if (getSizeT() == 0) core[0].sizeT = axisLengths[i];
        else core[0].sizeT *= axisLengths[i];
        if (getDimensionOrder().indexOf("T") == -1) {
          core[0].dimensionOrder += "T";
        }
      }
      else {
        if (core[0].sizeC == 0) core[0].sizeC = axisLengths[i];
        else core[0].sizeC *= axisLengths[i];
        channelLengths.add(new Integer(axisLengths[i]));
        core[0].rgb = getSizeX() == 0 && getSizeC() <= 4 && getSizeC() > 1;
        if (getDimensionOrder().indexOf("C") == -1) {
          core[0].dimensionOrder += "C";
        }

        if (axes[i].startsWith("c")) {
          channelTypes.add(FormatTools.CHANNEL);
        }
        else if (axes[i].equals("p")) {
          channelTypes.add(FormatTools.PHASE);
        }
        else if (axes[i].equals("f")) {
          channelTypes.add(FormatTools.FREQUENCY);
        }
        else channelTypes.add("");
      }
    }

    if (getDimensionOrder().indexOf("Z") == -1) {
      core[0].dimensionOrder += "Z";
    }
    if (getDimensionOrder().indexOf("T") == -1) {
      core[0].dimensionOrder += "T";
    }
    if (getDimensionOrder().indexOf("C") == -1) {
      core[0].dimensionOrder += "C";
    }

    if (getSizeZ() == 0) core[0].sizeZ = 1;
    if (getSizeC() == 0) core[0].sizeC = 1;
    if (getSizeT() == 0) core[0].sizeT = 1;

    core[0].interleaved = isRGB();
    core[0].imageCount = getSizeZ() * getSizeT();
    if (!isRGB()) core[0].imageCount *= getSizeC();
    core[0].indexed = false;
    core[0].falseColor = false;
    core[0].metadataComplete = true;
    core[0].littleEndian = true;

    // HACK - support for Gray Institute at Oxford's ICS lifetime data
    if (lifetime) {
      int binCount = core[0].sizeZ;
      core[0].sizeZ = core[0].sizeC;
      core[0].sizeC = binCount;
      core[0].cLengths = new int[] {binCount};
      core[0].cTypes = new String[] {FormatTools.LIFETIME};
      String newOrder = core[0].dimensionOrder.replace("Z", "x");
      newOrder = newOrder.replace("C", "Z");
      newOrder = newOrder.replace("x", "C");
      core[0].dimensionOrder = newOrder;
    }

    if (byteOrder != null) {
      String firstByte = byteOrder.split(" ")[0];
      int first = Integer.parseInt(firstByte);
      core[0].littleEndian = rFormat.equals("real") ? first == 1 : first != 1;
    }

    gzip = (compression == null) ? false : compression.equals("gzip");

    if (versionTwo) {
      String s = in.readString(NL);
      while (!s.trim().equals("end")) s = in.readString(NL);
    }

    offset = in.getFilePointer();

    // extra check is because some of our datasets are labeled as 'gzip', and
    // have a valid GZIP header, but are actually uncompressed
    long pixelDataSize = getSizeX() * getSizeY() * (bitsPerPixel / 8) *
      getImageCount() * getRGBChannelCount();
    if (gzip && ((in.length() - in.getFilePointer()) < pixelDataSize)) {
      data = new byte[(int) (in.length() - in.getFilePointer())];
      status("Decompressing pixel data");
      in.read(data);
      byte[] buf = new byte[8192];
      ByteVector v = new ByteVector();
      try {
        GZIPInputStream r = new GZIPInputStream(new ByteArrayInputStream(data));
        int n = r.read(buf, 0, buf.length);
        while (n > 0) {
          v.add(buf, 0, n);
          n = r.read(buf, 0, buf.length);
        }
        r.close();
        data = v.toByteArray();
        Location.mapFile("data.gz", null);
      }
      catch (IOException dfe) {
        throw new FormatException("Error uncompressing gzip'ed data", dfe);
      }
    }
    else gzip = false;

    if (bitsPerPixel < 32) core[0].littleEndian = !isLittleEndian();

    if (rFormat.equals("real")) {
      if (bitsPerPixel == 32) core[0].pixelType = FormatTools.FLOAT;
      else if (bitsPerPixel == 64) core[0].pixelType = FormatTools.DOUBLE;
    }
    else if (rFormat.equals("integer")) {
      switch (bitsPerPixel) {
        case 8:
          core[0].pixelType = signed ? FormatTools.INT8 : FormatTools.UINT8;
          break;
        case 16:
          core[0].pixelType = signed ? FormatTools.INT16 : FormatTools.UINT16;
          break;
        case 32:
          core[0].pixelType = signed ? FormatTools.INT32 : FormatTools.UINT32;
          break;
      }
    }
    else {
      throw new RuntimeException("Unknown pixel format: " + rFormat);
    }

    status("Populating OME metadata");

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    MetadataTools.populatePixels(store, this);

    // populate Image data

    store.setImageName(imageName, 0);

    if (date != null) store.setImageCreationDate(date, 0);
    else MetadataTools.setDefaultCreationDate(store, id, 0);

    store.setImageDescription(description, 0);

    // link Instrument and Image
    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrumentID, 0);
    store.setImageInstrumentRef(instrumentID, 0);

    // populate Dimensions data

    if (pixelSizes != null) {
      for (int i=0; i<pixelSizes.length; i++) {
        if (axes[i].equals("x")) {
          store.setDimensionsPhysicalSizeX(pixelSizes[i], 0, 0);
        }
        else if (axes[i].equals("y")) {
          store.setDimensionsPhysicalSizeY(pixelSizes[i], 0, 0);
        }
        else if (axes[i].equals("z")) {
          store.setDimensionsPhysicalSizeZ(pixelSizes[i], 0, 0);
        }
        else if (axes[i].equals("t")) {
          store.setDimensionsTimeIncrement(pixelSizes[i], 0, 0);
        }
        else if (axes[i].equals("ch")) {
          int c = pixelSizes[i].intValue();
          if (c > 0) store.setDimensionsWaveIncrement(new Integer(c), 0, 0);
        }
      }
    }
    else if (sizes != null) {
      if (sizes.length > 0) {
        store.setDimensionsPhysicalSizeX(new Float(sizes[0]), 0, 0);
      }
      if (sizes.length > 1) {
        sizes[1] /= getSizeY();
        store.setDimensionsPhysicalSizeY(new Float(sizes[1]), 0, 0);
      }
    }

    // populate LogicalChannel data

    for (int i=0; i<getEffectiveSizeC(); i++) {
      Integer channel = new Integer(i);
      if (channelNames.containsKey(channel)) {
        store.setLogicalChannelName(channelNames.get(channel), 0, i);
      }
      if (pinholes.containsKey(channel)) {
        store.setLogicalChannelPinholeSize(pinholes.get(channel), 0, i);
      }
      if (emWaves != null && i < emWaves.length) {
        store.setLogicalChannelEmWave(emWaves[i], 0, i);
      }
      if (exWaves != null && i < exWaves.length) {
        store.setLogicalChannelExWave(exWaves[i], 0, i);
      }
    }

    // populate Laser data

    Integer[] lasers = wavelengths.keySet().toArray(new Integer[0]);
    for (int i=0; i<lasers.length; i++) {
      store.setLaserWavelength(wavelengths.get(lasers[i]), 0,
        lasers[i].intValue());
    }

    // populate Objective data

    if (objectiveModel != null) store.setObjectiveModel(objectiveModel, 0, 0);
    if (immersion != null) store.setObjectiveImmersion(immersion, 0, 0);
    else store.setObjectiveImmersion("Unknown", 0, 0);
    if (lensNA != null) store.setObjectiveLensNA(lensNA, 0, 0);
    if (workingDistance != null) {
      store.setObjectiveWorkingDistance(workingDistance, 0, 0);
    }
    if (magnification != null) {
      store.setObjectiveCalibratedMagnification(magnification, 0, 0);
    }
    store.setObjectiveCorrection("Unknown", 0, 0);

    // link Objective to Image
    String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
    store.setObjectiveID(objectiveID, 0, 0);
    store.setObjectiveSettingsObjective(objectiveID, 0);

    for (Integer key : gains.keySet()) {
      int index = key.intValue();
      if (index < getEffectiveSizeC()) {
        store.setDetectorSettingsGain(gains.get(key), 0, index);
        store.setDetectorType("Unknown", 0, index);
        String detectorID = MetadataTools.createLSID("Detector", 0, index);
        store.setDetectorID(detectorID, 0, index);
        store.setDetectorSettingsDetector(detectorID, 0, index);
      }
    }

    // populate Experimenter data

    if (lastName != null) store.setExperimenterLastName(lastName, 0);

    // populate StagePosition data

    if (stagePos != null) {
      for (int i=0; i<getImageCount(); i++) {
        if (stagePos.length > 0) {
          store.setStagePositionPositionX(stagePos[0], 0, 0, i);
        }
        if (stagePos.length > 1) {
          store.setStagePositionPositionY(stagePos[1], 0, 0, i);
        }
        if (stagePos.length > 2) {
          store.setStagePositionPositionZ(stagePos[2], 0, 0, i);
        }
      }
    }
  }

}
