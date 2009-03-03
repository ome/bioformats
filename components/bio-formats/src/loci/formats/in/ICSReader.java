//
// ICSReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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

import java.io.*;
import java.util.*;
import java.util.zip.*;
import loci.common.*;
import loci.formats.*;
import loci.formats.codec.ByteVector;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * ICSReader is the file format reader for ICS (Image Cytometry Standard)
 * files. More information on ICS can be found at http://libics.sourceforge.net
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/ICSReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/ICSReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class ICSReader extends FormatReader {

  // -- Constants --

  /** Metadata field categories. */
  private static final String[] CATEGORIES = new String[] {
    "ics_version", "filename", "source", "layout", "representation",
    "parameter", "sensor", "history", "document", "view", "end"
  };

  /** Metadata field subcategories. */
  private static final String[] SUB_CATEGORIES = new String[] {
    "file", "offset", "parameters", "order", "sizes", "coordinates",
    "significant_bits", "format", "sign", "compression", "byte_order",
    "origin", "scale", "units", "labels", "SCIL_TYPE", "type", "model",
    "s_params", "laser", "gain*", "dwell", "shutter*", "pinhole", "laser*",
    "version", "objective", "PassCount", "step*", "view",
    "view*", "date", "GMTdate", "label", "software", "author", "length",
    "Z (background)", "dimensions", "rep period", "image form",
    "extents", "offsets", "region", "expon. order", "a*", "tau*",
    "noiseval", "excitationfwhm", "created on",
    "text", "other text", "mode", "CFD limit low", "CFD limit high",
    "CFD zc level", "CFD holdoff", "SYNC zc level", "SYNC freq div",
    "SYNC holdoff", "TAC range", "TAC gain", "TAC offset", "TAC limit low",
    "ADC resolution", "Ext latch delay", "collection time", "repeat time",
    "stop on time", "stop on O'flow", "dither range", "count increment",
    "memory bank", "sync threshold", "dead time comp", "polarity",
    "line compressio", "scan flyback", "scan borders", "pixel time",
    "pixel clock", "trigger", "scan pixels x", "scan pixels y",
    "routing chan x", "routing chan y", "detector type", "channel*",
    "filter*", "wavelength*", "black level*", "gain*", "ht*",
    "scan resolution", "scan speed", "scan zoom", "scan pattern",
    "scan pos x", "scan pos y", "transmission", "x amplitude", "y amplitude",
    "x offset", "y offset", "x delay", "y delay", "beam zoom", "mirror *",
    "direct turret", "desc exc turret", "desc emm turret", "cube",
    "stage_xyzum", "cube descriptio", "camera", "exposure", "bits/pixel",
    "black level", "binning", "left", "top", "cols", "rows", "gain",
    "significant_channels", "allowedlinemodes", "real_significant_bits",
    "sample_width", "range", "ch", "lower_limit", "higher_limit", "passcount",
    "detector", "dateGMT", "RefrInxMedium", "RefrInxLensMedium"
  };

  /** Metadata field sub-subcategories. */
  private static final String[] SUB_SUB_CATEGORIES = new String[] {
    "Channels", "PinholeRadius", "LambdaEx", "LambdaEm", "ExPhotonCnt",
    "RefInxMedium", "NumAperture", "RefInxLensMedium", "PinholeSpacing",
    "power", "wavelength", "name", "Type", "Magnification", "NA",
    "WorkingDistance", "Immersion", "Pinhole", "Channel *", "Gain *",
    "Shutter *", "Position", "Size", "Port", "Cursor", "Color", "BlackLevel",
    "Saturation", "Gamma", "IntZoom", "Live", "Synchronize", "ShowIndex",
    "AutoResize", "UseUnits", "Zoom", "IgnoreAspect", "ShowCursor", "ShowAll",
    "Axis", "Order", "Tile", "scale", "DimViewOption"
  };

  // -- Fields --

  /** Current filename. */
  private String currentIcsId;
  private String currentIdsId;

  /** Current ICS file. */
  private Location icsIn;

  /** Number of bits per pixel. */
  private int bitsPerPixel;

  /** Flag indicating whether current file is v2.0. */
  private boolean versionTwo;

  /** Image data. */
  private byte[] data;

  /** Emission and excitation wavelength. */
  private String em, ex;

  private long offset;
  private boolean gzip;

  private boolean invertY;

  private String imageName, date, magnification, lastName, description;
  private String objectiveModel, immersion, lensNA, workingDistance;
  private Hashtable gains, wavelengths, pinholes, channelNames;
  private String[] stagePos;

  // -- Constructor --

  /** Constructs a new ICSReader. */
  public ICSReader() {
    super("Image Cytometry Standard", new String[] {"ics", "ids"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    return false;
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
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

    int bpp = bitsPerPixel / 8;
    int len = getSizeX() * getSizeY() * bpp * getRGBChannelCount();
    int pixel = bpp * getRGBChannelCount();
    int rowLen = w * pixel;

    in.seek(offset + no * len);

    if (!isRGB() && getSizeC() > 4) {
      // channels are stored interleaved, but because there are more than we
      // can display as RGB, we need to separate them
      if (!gzip && data == null) {
        data = new byte[len * getSizeC()];
        in.read(data);
      }

      for (int row=y; row<h + y; row++) {
        for (int col=x; col<w + x; col++) {
          System.arraycopy(data, bpp * (no + getSizeC() *
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
          System.arraycopy(data, len * no + row * getSizeX() * pixel +
            x * pixel, buf, row * rowLen, rowLen);
        }
      }
    }
    else {
      readPlane(in, x, y, w, h, buf);
    }

    if (invertY) {
      byte[] row = new byte[rowLen];
      for (int r=0; r<h/2; r++) {
        System.arraycopy(buf, r*rowLen, row, 0, rowLen);
        System.arraycopy(buf, (h - r - 1)*rowLen, buf, r*rowLen, rowLen);
        System.arraycopy(row, 0, buf, (h - r - 1)*rowLen, rowLen);
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    if (versionTwo) {
      return new String[] {currentId};
    }
    return new String[] {currentIdsId, currentIcsId};
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    icsIn = null;
    currentIcsId = null;
    currentIdsId = null;
    data = null;
    bitsPerPixel = 0;
    versionTwo = false;
    gzip = false;
    invertY = false;
    imageName = date = objectiveModel = immersion = lensNA = null;
    workingDistance = magnification = lastName = description = null;
    gains = wavelengths = pinholes = channelNames = null;
    stagePos = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("ICSReader.initFile(" + id + ")");
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
    RandomAccessStream f = new RandomAccessStream(icsId);
    if (f.readString(17).trim().equals("ics_version\t2.0")) {
      in = new RandomAccessStream(icsId);
      versionTwo = true;
    }
    else {
      if (idsId == null) throw new FormatException("No IDS file found.");
      Location idsFile = new Location(idsId);
      if (!idsFile.exists()) throw new FormatException("IDS file not found.");
      currentIdsId = idsId;
      in = new RandomAccessStream(idsId);
    }
    f.close();

    currentIcsId = icsId;

    icsIn = icsFile;

    status("Reading metadata");

    String layoutSizes = null, layoutOrder = null, byteOrder = null;
    String rFormat = null, compression = null, scale = null;

    // parse key/value pairs from beginning of ICS file

    RandomAccessStream reader = new RandomAccessStream(icsIn.getAbsolutePath());
    reader.seek(0);
    String token;
    String line = reader.readLine();
    line = reader.readLine();
    boolean signed = false;

    StringBuffer textBlock = new StringBuffer();
    String[] sizes = null, labels = null, lengths= null;

    while (line != null && !line.trim().equals("end")) {
      String[] tokens = null;
      if (line.indexOf("\t") != -1) tokens = line.split("\t");
      else tokens = line.split(" ");
      StringBuffer key = new StringBuffer();
      for (int q=0; q<tokens.length; q++) {
        tokens[q] = tokens[q].trim();
        if (tokens[q].length() == 0) continue;

        boolean foundValue = true;
        for (int i=0; i<CATEGORIES.length; i++) {
          if (matches(CATEGORIES[i], tokens[q])) {
            foundValue = false;
            break;
          }
        }
        if (foundValue) {
          for (int i=0; i<SUB_CATEGORIES.length; i++) {
            if (matches(SUB_CATEGORIES[i], tokens[q])) {
              foundValue = false;
              break;
            }
          }
        }
        if (foundValue) {
          for (int i=0; i<SUB_SUB_CATEGORIES.length; i++) {
            if (matches(SUB_SUB_CATEGORIES[i], tokens[q])) {
              foundValue = false;
              break;
            }
          }
        }

        if (foundValue) {
          StringBuffer value = new StringBuffer();
          value.append(tokens[q++]);
          for (; q<tokens.length; q++) {
            value.append(" ");
            value.append(tokens[q].trim());
          }
          String k = key.toString().trim();
          String v = value.toString().trim();
          addMeta(k, v);

          k = k.replaceAll("\t", " ");

          if (k.equalsIgnoreCase("layout sizes")) layoutSizes = v;
          else if (k.equalsIgnoreCase("layout order")) layoutOrder = v;
          else if (k.equalsIgnoreCase("representation byte_order")) {
            byteOrder = v;
          }
          else if (k.equalsIgnoreCase("representation format")) rFormat = v;
          else if (k.equalsIgnoreCase("representation compression")) {
            compression = v;
          }
          else if (k.equalsIgnoreCase("parameter scale")) scale = v;
          else if (k.equalsIgnoreCase("representation sign")) {
            signed = v.equals("signed");
          }
          else if (k.equalsIgnoreCase("sensor s_params LambdaEm")) em = v;
          else if (k.equalsIgnoreCase("sensor s_params LambdaEx")) ex = v;
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
            String[] formats = new String[] {"EEEE, MMMM dd, yyyy HH:mm:ss",
              "EEE dd MMMM yyyy HH:mm:ss", "EEE MMM dd HH:mm:ss yyyy",
              "EE dd MMM yyyy HH:mm:ss z"};

            boolean success = false;

            for (int n=0; n<formats.length; n++) {
              try {
                date = DataTools.formatDate(date, formats[n]);
                success = true;
              }
              catch (NullPointerException e) { }
            }

            if (!success) date = null;
          }
          else if (k.startsWith("history gain")) {
            int n = 0;
            try {
              n = Integer.parseInt(k.substring(12).trim());
            }
            catch (NumberFormatException e) { }
            if (gains == null) gains = new Hashtable();
            gains.put(new Integer(n), v);
          }
          else if (k.startsWith("history laser") && k.endsWith("wavelength")) {
            int laser = Integer.parseInt(k.substring(13, k.indexOf(" ", 13)));
            v = v.replaceAll("nm", "").trim();
            if (wavelengths == null) wavelengths = new Hashtable();
            wavelengths.put(new Integer(laser), v);
          }
          else if (k.equalsIgnoreCase("history objective type")) {
            objectiveModel = v;
          }
          else if (k.equalsIgnoreCase("history objective immersion")) {
            immersion = v;
          }
          else if (k.equalsIgnoreCase("history objective NA")) {
            lensNA = v;
          }
          else if (k.equalsIgnoreCase("history objective WorkingDistance")) {
            workingDistance = v;
          }
          else if (k.equalsIgnoreCase("history objective magnification")) {
            magnification = v;
          }
          else if (k.equalsIgnoreCase("sensor s_params PinholeRadius")) {
            String[] pins = v.split(" ");
            int channel = 0;
            if (pinholes == null) pinholes = new Hashtable();
            for (int n=0; n<pins.length; n++) {
              if (pins[n].trim().equals("")) continue;
              pinholes.put(new Integer(channel++), pins[n]);
            }
          }
          else if (k.equalsIgnoreCase("history author")) lastName = v;
          else if (k.equalsIgnoreCase("history extents")) {
            sizes = v.split(" ");
          }
          else if (k.equalsIgnoreCase("history lengths")) {
            lengths = v.split(" ");
          }
          else if (k.equalsIgnoreCase("history labels")) {
            labels = v.split(" ");
          }
          else if (k.equalsIgnoreCase("history stage_xyzum")) {
            stagePos = v.split(" ");
          }
          else if (k.equalsIgnoreCase("history other text")) {
            description = v;
          }
          else if (k.startsWith("history step") && k.endsWith("name")) {
            Integer n = new Integer(k.substring(12, k.indexOf(" ", 12)));
            if (channelNames == null) channelNames = new Hashtable();
            channelNames.put(n, v);
          }
          else if (k.equalsIgnoreCase("parameter ch")) {
            String[] names = v.split(" ");
            if (channelNames == null) channelNames = new Hashtable();
            for (int n=0; n<names.length; n++) {
              channelNames.put(new Integer(n), names[n].trim());
            }
          }
        }
        else {
          key.append(tokens[q]);
          key.append(" ");
        }
      }
      line = reader.readLine();
      if (line.trim().equals("")) line = null;
    }
    reader.close();

    addMeta("history text", textBlock.toString());

    status("Populating metadata");

    if (invertY) signed = false;

    layoutOrder = layoutOrder.trim();
    StringTokenizer t1 = new StringTokenizer(layoutSizes);
    StringTokenizer t2 = new StringTokenizer(layoutOrder);

    core[0].rgb = layoutOrder.indexOf("ch") >= 0 &&
      layoutOrder.indexOf("ch") < layoutOrder.indexOf("x");
    core[0].dimensionOrder = "XY";

    // find axis sizes

    String imageToken;
    String orderToken;
    while (t1.hasMoreTokens() && t2.hasMoreTokens()) {
      imageToken = t1.nextToken().trim();
      orderToken = t2.nextToken().trim();
      if (orderToken.equals("bits")) {
        bitsPerPixel = Integer.parseInt(imageToken);
      }
      else if (orderToken.equals("x")) {
        core[0].sizeX = Integer.parseInt(imageToken);
      }
      else if (orderToken.equals("y")) {
        core[0].sizeY = Integer.parseInt(imageToken);
      }
      else if (orderToken.equals("z")) {
        core[0].sizeZ = Integer.parseInt(imageToken);
        core[0].dimensionOrder += "Z";
      }
      else if (orderToken.equals("ch")) {
        core[0].sizeC = Integer.parseInt(imageToken);
        if (getSizeC() > 4) core[0].rgb = false;
        core[0].dimensionOrder += "C";
      }
      else {
        core[0].sizeT = Integer.parseInt(imageToken);
        core[0].dimensionOrder += "T";
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

    if (getImageCount() == 0) core[0].imageCount = 1;
    core[0].rgb = isRGB() && getSizeC() > 1;
    core[0].interleaved = isRGB();
    core[0].imageCount = getSizeZ() * getSizeT();
    if (!isRGB()) core[0].imageCount *= getSizeC();
    core[0].indexed = false;
    core[0].falseColor = false;
    core[0].metadataComplete = true;

    String endian = byteOrder;
    core[0].littleEndian = true;

    if (endian != null) {
      StringTokenizer endianness = new StringTokenizer(endian);
      String firstByte = endianness.nextToken();
      int first = Integer.parseInt(firstByte);
      core[0].littleEndian = rFormat.equals("real") ? first == 1 : first != 1;
    }

    String test = compression;
    gzip = (test == null) ? false : test.equals("gzip");

    if (versionTwo) {
      String s = in.readLine();
      while(!s.trim().equals("end")) s = in.readLine();
    }

    offset = in.getFilePointer();

    // extra check is because some of our datasets are labeled as 'gzip', and
    // have a valid GZIP header, but are actually uncompressed
    if (gzip && (((in.length() - in.getFilePointer()) / (getImageCount()) <
      (getSizeX() * getSizeY() * bitsPerPixel / 8))))
    {
      data = new byte[(int) (in.length() - in.getFilePointer())];
      status("Decompressing pixel data");
      in.read(data);
      byte[] buf = new byte[8192];
      ByteVector v = new ByteVector();
      try {
        GZIPInputStream decompressor =
          new GZIPInputStream(new ByteArrayInputStream(data));
        int r = decompressor.read(buf, 0, buf.length);
        while (r > 0) {
          v.add(buf, 0, r);
          r = decompressor.read(buf, 0, buf.length);
        }
        data = v.toByteArray();
      }
      catch (IOException dfe) {
        throw new FormatException("Error uncompressing gzip'ed data", dfe);
      }
    }
    else gzip = false;

    status("Populating metadata");

    // populate Pixels element

    String fmt = rFormat;

    if (bitsPerPixel < 32) core[0].littleEndian = !isLittleEndian();

    if (fmt.equals("real") && bitsPerPixel == 32) {
      core[0].pixelType = FormatTools.FLOAT;
    }
    else if (fmt.equals("real") && bitsPerPixel == 64) {
      core[0].pixelType = FormatTools.DOUBLE;
    }
    else if (fmt.equals("integer")) {
      while (bitsPerPixel % 8 != 0) bitsPerPixel++;
      if (bitsPerPixel == 24 || bitsPerPixel == 48) bitsPerPixel /= 3;

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
      throw new RuntimeException("Unknown pixel format: " + fmt);
    }

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    MetadataTools.populatePixels(store, this);

    // populate Image data

    store.setImageName(imageName, 0);
    if (date != null) {
      store.setImageCreationDate(date, 0);
    }
    else MetadataTools.setDefaultCreationDate(store, id, 0);

    store.setImageDescription(description, 0);

    // link Instrument and Image
    store.setInstrumentID("Instrument:0", 0);
    store.setImageInstrumentRef("Instrument:0", 0);

    // populate Dimensions data

    String pixelSizes = scale;
    String o = layoutOrder;
    if (pixelSizes != null) {
      StringTokenizer pixelSizeTokens = new StringTokenizer(pixelSizes);
      StringTokenizer axisTokens = new StringTokenizer(o);

      Float pixX = null, pixY = null, pixZ = null, pixT = null;
      Integer pixC = null;

      while (pixelSizeTokens.hasMoreTokens()) {
        String axis = axisTokens.nextToken().trim().toLowerCase();
        String size = pixelSizeTokens.nextToken().trim();
        if (axis.equals("x")) pixX = new Float(size);
        else if (axis.equals("y")) pixY = new Float(size);
        else if (axis.equals("z")) pixZ = new Float(size);
        else if (axis.equals("t")) pixT = new Float(size);
        else if (axis.equals("ch")) {
          pixC = new Integer(new Float(size).intValue());
        }
      }
      store.setDimensionsPhysicalSizeX(pixX, 0, 0);
      store.setDimensionsPhysicalSizeY(pixY, 0, 0);
      store.setDimensionsPhysicalSizeZ(pixZ, 0, 0);
      store.setDimensionsTimeIncrement(pixT, 0, 0);
      if (pixC != null && pixC.intValue() > 0) {
        store.setDimensionsWaveIncrement(pixC, 0, 0);
      }
    }
    else if (sizes != null) {
      for (int i=0; i<sizes.length; i++) {
        float size = Float.parseFloat(sizes[i].trim());
        if (i == 0) {
          size /= getSizeX();
          store.setDimensionsPhysicalSizeX(new Float(size), 0, 0);
        }
        else if (i == 1) {
          size /= getSizeY();
          store.setDimensionsPhysicalSizeY(new Float(size), 0, 0);
        }
      }
    }

    // populate LogicalChannel data

    if (em != null) {
      String[] emTokens = em.split(" ");
      int channel = 0;
      for (int i=0; i<emTokens.length; i++) {
        if (emTokens[i].trim().equals("")) continue;
        store.setLogicalChannelEmWave(
          new Integer((int) Float.parseFloat(emTokens[i])), 0, channel++);
      }
    }
    if (ex != null) {
      String[] exTokens = ex.split(" ");
      int channel = 0;
      for (int i=0; i<exTokens.length; i++) {
        if (exTokens[i].trim().equals("")) continue;
        store.setLogicalChannelExWave(
          new Integer((int) Float.parseFloat(exTokens[i])), 0, channel++);
      }
    }

    for (int i=0; i<getSizeC(); i++) {
      Integer channel = new Integer(i);
      if (channelNames != null) {
        String name = (String) channelNames.get(channel);
        store.setLogicalChannelName(name, 0, i);
      }
      if (pinholes != null) {
        String pinhole = (String) pinholes.get(channel);
        store.setLogicalChannelPinholeSize(new Float(pinhole), 0, i);
      }
    }

    // populate Laser data

    if (wavelengths != null) {
      for (int i=1; i<=wavelengths.size(); i++) {
        String wavelength = (String) wavelengths.get(new Integer(i));
        if (wavelength != null) {
          store.setLaserWavelength(new Integer(wavelength), 0, i - 1);
        }
      }
    }

    // populate Objective data

    if (objectiveModel != null) store.setObjectiveModel(objectiveModel, 0, 0);
    if (immersion != null) store.setObjectiveImmersion(immersion, 0, 0);
    else store.setObjectiveImmersion("Unknown", 0, 0);
    if (lensNA != null) store.setObjectiveLensNA(new Float(lensNA), 0, 0);
    if (workingDistance != null) {
      store.setObjectiveWorkingDistance(new Float(workingDistance), 0, 0);
    }
    if (magnification != null) {
      store.setObjectiveCalibratedMagnification(new Float(magnification), 0, 0);
    }
    store.setObjectiveCorrection("Unknown", 0, 0);

    // link Objective to Image
    store.setObjectiveID("Objective:0", 0, 0);
    store.setObjectiveSettingsObjective("Objective:0", 0);

    if (gains != null) {
      for (int i=0; i<gains.size(); i++) {
        if (gains.containsKey(new Integer(i + 1))) {
          store.setDetectorSettingsGain(
            new Float((String) gains.get(new Integer(i + 1))), 0, i);
          store.setDetectorType("Unknown", 0, i);
          store.setDetectorID("Detector:" + i, 0, i);
          store.setDetectorSettingsDetector("Detector:0", 0, i);
        }
      }
    }

    // populate Experimenter data

    store.setExperimenterLastName(lastName, 0);

    // populate StagePosition data

    if (stagePos != null) {
      for (int i=0; i<getImageCount(); i++) {
        if (stagePos.length > 0) {
          store.setStagePositionPositionX(new Float(stagePos[0]), 0, 0, i);
        }
        if (stagePos.length > 1) {
          store.setStagePositionPositionY(new Float(stagePos[1]), 0, 0, i);
        }
        if (stagePos.length > 2) {
          store.setStagePositionPositionZ(new Float(stagePos[2]), 0, 0, i);
        }
      }
    }
  }

  // -- Helper functions --

  private boolean matches(String pattern, String text) {
    if (text == null || pattern == null) return false;
    if (text.equalsIgnoreCase(pattern)) return true;
    if (!pattern.endsWith("*")) return false;

    return text.startsWith(pattern.substring(0, pattern.length() - 1));
  }

}
