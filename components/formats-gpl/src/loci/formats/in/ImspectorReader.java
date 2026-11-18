/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.quantity.Time;

/**
 * ImspectorReader is the file format reader for Imspector .msr files.
 *
 * An MSR file is an MFC (Microsoft Foundation Classes) serialized binary container
 * containing acquisition metadata records and one or more uncompressed 16-bit image stacks.
 * Modern Imspector Pro files (v7.x+) begin with an MFC-serialized {@code CProperty} root block,
 * followed by {@code CDataStack} and/or {@code CImageStack} objects. Older versions (&le; 3.7)
 * begin directly with {@code CDataStack}.
 *
 * @author: Saleh at Altahini dot com
 */
public class ImspectorReader extends FormatReader {

  // -- Constants --

  /** Magic signatures for format identification. */
  private static final String NEW_MAGIC = "CProperty";
  private static final String OLD_MAGIC = "CDataStack";

  /** Regex to identify modern device tags: e.g. "ResonantPMT:0:0:0", "GalvoPMT:0:1". */
  private static final Pattern DEV_PATTERN =
    Pattern.compile("([A-Za-z0-9_-]+:\\d+(?::\\d+)*)");

  /** Regex fallback for legacy device tags: e.g. "PMT", "TCSPC", "ResonantPMT". */
  private static final Pattern LEGACY_DEV_PATTERN =
    Pattern.compile("((?:PMT|TCSPC|ResonantPMT)[A-Za-z0-9_-]*)");

  // -- Helper Classes --

  /** Internal structure holding parsed metadata and file offset for a single stack/series. */
  protected static class StackInfo {
    public String deviceName;
    public String seriesName;
    public long pixelOffset;
    public long pixelBytes;

    public int sizeX;
    public int sizeY;
    public int sizeZ;
    public int sizeC;
    public int sizeT;
    public String dimensionOrder;

    public double physicalSizeX;
    public double physicalSizeY;
    public double physicalSizeZ;
    public double timeStep;

    /** Stage coordinates (relative to origin/set zero). */
    public double stageX;
    public double stageY;
    public double stageZ;

    /** Stage coordinates (raw hardware encoder position). */
    public Double stageXRaw;
    public Double stageYRaw;
    public Double stageZRaw;

    public String axisLabel0;
    public String axisLabel1;
    public String axisLabel2;
    public String axisLabel3;

    public float axisLen0;
    public float axisLen1;
    public float axisLen2;
    public float axisLen3;
  }

  // -- Fields --

  /** List of all image stacks / series discovered in the file. */
  protected List<StackInfo> stacks = new ArrayList<StackInfo>();

  /** Hardware table zero offsets (if present). */
  private Float zeroOffsetX = null;
  private Float zeroOffsetY = null;
  private Float zeroOffsetZ = null;

  /** Hardware table raw measurement positions (if present). */
  private Double rawMeasX = null;
  private Double rawMeasY = null;
  private Double rawMeasZ = null;

  // -- Constructor --

  /** Constructs a new LaVision Imspector reader. */
  public ImspectorReader() {
    super("Lavision Imspector", "msr");
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN, FormatTools.FLIM_DOMAIN};
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 64;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    String content = stream.readString(blockLen);
    return content.contains(NEW_MAGIC) || content.contains(OLD_MAGIC);
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int) */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int seriesIndex = getSeries();
    if (seriesIndex < 0 || seriesIndex >= stacks.size()) {
      throw new FormatException("Invalid series index: " + seriesIndex);
    }

    StackInfo stack = stacks.get(seriesIndex);
    long bytesPerPixel = FormatTools.getBytesPerPixel(getPixelType());
    long planeSize = (long) getSizeX() * getSizeY() * bytesPerPixel;
    long planeOffset = stack.pixelOffset + ((long) no * planeSize);

    in.seek(planeOffset);
    readPlane(in, x, y, w, h, buf);

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      stacks.clear();
      zeroOffsetX = null;
      zeroOffsetY = null;
      zeroOffsetZ = null;
      rawMeasX = null;
      rawMeasY = null;
      rawMeasZ = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    in.order(true); // Little-endian unsigned 16-bit
    in.setEncoding("Cp1252");

    stacks.clear();
    core.clear();
    zeroOffsetX = null;
    zeroOffsetY = null;
    zeroOffsetZ = null;
    rawMeasX = null;
    rawMeasY = null;
    rawMeasZ = null;

    long fileSize = in.length();
    LOGGER.info("Parsing Imspector file: {} ({} bytes)", id, fileSize);

    // Buffer used to scan for stack headers
    byte[] searchBuffer = new byte[131072]; // 128 KB search window
    long searchPos = 0;

    // Discover all stacks by hopping past pixel data
    while (searchPos < fileSize) {
      in.seek(searchPos);
      int bytesRead = in.read(searchBuffer);
      if (bytesRead <= 0) break;

      int matchOffset = findDevicePattern(searchBuffer, bytesRead);
      if (matchOffset == -1) {
        // If not found in current window, advance window with an overlap
        if (bytesRead < searchBuffer.length) {
          break; // Reached end of file
        }
        searchPos += (bytesRead - 1024);

        // Terminate scan if we have searched over 2MB past the last discovered stack
        if (!stacks.isEmpty()) {
          StackInfo last = stacks.get(stacks.size() - 1);
          if (searchPos > last.pixelOffset + last.pixelBytes + 2097152) {
            break;
          }
        }
        continue;
      }

      long absDevPos = searchPos + matchOffset;

      // Extract device tag Pascal string length
      in.seek(absDevPos - 1);
      int devLen = in.read() & 0xff;
      String devStr = in.readString(devLen);

      // Skip 6 unknown/padding bytes after device tag
      in.skipBytes(6);

      // Read dimensions (4 signed 32-bit ints)
      int dx = in.readInt();
      int dy = in.readInt();
      int dz = in.readInt();
      int dt = in.readInt();

      // Read physical spans (4 32-bit floats)
      float len0 = in.readFloat();
      float len1 = in.readFloat();
      float len2 = in.readFloat();
      float len3 = in.readFloat();

      // Read 4 Pascal strings for axis labels
      String lbl0 = readPascalString(in);
      String lbl1 = readPascalString(in);
      String lbl2 = readPascalString(in);
      String lbl3 = readPascalString(in);

      // Stage position coordinates (stored as 4 floats 16 bytes before devLen byte)
      double stageX = 0, stageY = 0, stageZ = 0;
      if (absDevPos >= 17) {
        long savedPos = in.getFilePointer();
        in.seek(absDevPos - 1 - 16);
        stageX = in.readFloat();
        stageY = in.readFloat();
        stageZ = in.readFloat();
        in.seek(savedPos);
      }

      // Extract series name from preceding header metadata
      String seriesName = findSeriesName(searchBuffer, matchOffset);
      if (seriesName == null || seriesName.trim().isEmpty()) {
        seriesName = "Series " + stacks.size() + " (" + devStr + ")";
      }

      long pixelOffset = in.getFilePointer();
      long planeCount = (long) dz * dt;
      long pixelBytes = planeCount * ((long) dx * dy * 2);

      StackInfo stack = new StackInfo();
      stack.deviceName = devStr;
      stack.seriesName = seriesName;
      stack.pixelOffset = pixelOffset;
      stack.pixelBytes = pixelBytes;
      stack.stageX = stageX;
      stack.stageY = stageY;
      stack.stageZ = stageZ;

      stack.axisLabel0 = lbl0;
      stack.axisLabel1 = lbl1;
      stack.axisLabel2 = lbl2;
      stack.axisLabel3 = lbl3;

      stack.axisLen0 = len0;
      stack.axisLen1 = len1;
      stack.axisLen2 = len2;
      stack.axisLen3 = len3;

      // Determine dimensions and physical calibration
      mapStackDimensions(stack, dx, dy, dz, dt);

      stacks.add(stack);
      LOGGER.debug("Discovered stack #{}: {} ({}x{}, z={}, c={}, t={}) @ {}",
        stacks.size() - 1, stack.seriesName, stack.sizeX, stack.sizeY,
        stack.sizeZ, stack.sizeC, stack.sizeT, stack.pixelOffset);

      // Hop past this stack's pixel data to search for the next stack
      searchPos = pixelOffset + pixelBytes;
    }

    if (stacks.isEmpty()) {
      throw new FormatException("No valid image stacks found in Imspector file: " + id);
    }

    LOGGER.info("Total stacks discovered: {}", stacks.size());

    // Populate CoreMetadata for each series
    for (int i = 0; i < stacks.size(); i++) {
      CoreMetadata ms = new CoreMetadata();
      core.add(ms);

      StackInfo s = stacks.get(i);
      ms.sizeX = s.sizeX;
      ms.sizeY = s.sizeY;
      ms.sizeZ = s.sizeZ;
      ms.sizeC = s.sizeC;
      ms.sizeT = s.sizeT;
      ms.imageCount = s.sizeZ * s.sizeT * s.sizeC;
      ms.dimensionOrder = s.dimensionOrder;
      ms.pixelType = FormatTools.UINT16;
      ms.littleEndian = true;
      ms.rgb = false;
      ms.interleaved = false;
      ms.indexed = false;
      ms.falseColor = false;
      ms.metadataComplete = true;
    }

    // Read top header metadata (Creation Date, Instrument Mode, Objective, etc.)
    readTopMetadata();

    // Read footer metadata (Laser Power, PMT Gains, stage positions)
    readFooterMetadata();

    // Compute raw vs relative stage positions for all series
    computeStageCoordinates();

    // Populate OME-XML Metadata Store
    populateMetadataStore();
  }

  // -- Helper Methods --

  /**
   * Searches a byte buffer for the device tag pattern preceding a valid stack header.
   */
  private int findDevicePattern(byte[] buf, int length) {
    String s = new String(buf, 0, length, StandardCharsets.ISO_8859_1);

    // Primary search: device tags with colons (e.g. ResonantPMT:0:0:0)
    Matcher m = DEV_PATTERN.matcher(s);
    while (m.find()) {
      int start = m.start(1);
      if (isValidHeaderAt(buf, length, start, m.group(1).length())) {
        return start;
      }
    }

    // Fallback search: legacy device tags (e.g. PMT, TCSPC)
    Matcher lm = LEGACY_DEV_PATTERN.matcher(s);
    while (lm.find()) {
      int start = lm.start(1);
      if (isValidHeaderAt(buf, length, start, lm.group(1).length())) {
        return start;
      }
    }

    return -1;
  }

  /**
   * Verifies that the string at candidateOffset is preceded by its Pascal length byte
   * and followed by 6 bytes and valid dx, dy, dz, dt image dimensions.
   */
  private boolean isValidHeaderAt(byte[] buf, int length, int candidateOffset, int strLen) {
    if (candidateOffset <= 0) return false;
    int lenByte = buf[candidateOffset - 1] & 0xff;
    if (lenByte != strLen) return false;

    int afterDev = candidateOffset + strLen + 6;
    if (afterDev + 16 > length) return false;

    int dx = readIntLittleEndian(buf, afterDev);
    int dy = readIntLittleEndian(buf, afterDev + 4);
    int dz = readIntLittleEndian(buf, afterDev + 8);
    int dt = readIntLittleEndian(buf, afterDev + 12);

    return dx > 0 && dx <= 32768 && dy > 0 && dy <= 32768 && dz > 0 && dt > 0;
  }

  /** Reads a little-endian 32-bit int from a byte array. */
  private int readIntLittleEndian(byte[] buf, int offset) {
    return (buf[offset] & 0xff) |
           ((buf[offset + 1] & 0xff) << 8) |
           ((buf[offset + 2] & 0xff) << 16) |
           ((buf[offset + 3] & 0xff) << 24);
  }

  /** Reads a 1-byte length prefixed Pascal string from the stream. */
  private String readPascalString(RandomAccessInputStream stream) throws IOException {
    int len = stream.read() & 0xff;
    if (len == 0) return "";
    return stream.readString(len);
  }

  /**
   * Extracts the human-readable series / image name from the MFC header preceding a stack.
   * Pattern: \x00\x00 followed by Pascal string (length byte + ASCII chars) followed by \xff.
   */
  private String findSeriesName(byte[] buf, int matchOffset) {
    int searchStart = Math.max(0, matchOffset - 2048);
    for (int i = matchOffset - 4; i >= searchStart; i--) {
      if (buf[i] == (byte) 0xff) {
        for (int j = i - 1; j >= Math.max(searchStart, i - 150); j--) {
          if (j >= 2 && buf[j - 2] == 0 && buf[j - 1] == 0) {
            int len = buf[j] & 0xff;
            if (len > 0 && j + 1 + len == i) {
              return new String(buf, j + 1, len, StandardCharsets.ISO_8859_1).trim();
            }
          }
        }
      }
    }
    return null;
  }

  /**
   * Maps raw stack dimensions (dx, dy, dz, dt) and axis labels into OME SizeX, SizeY,
   * SizeZ, SizeC, SizeT, dimensionOrder, and physical calibration.
   */
  private void mapStackDimensions(StackInfo s, int dx, int dy, int dz, int dt) {
    s.sizeX = dx;
    s.sizeY = dy;

    // Physical pixel size in X and Y
    s.physicalSizeX = (s.axisLen0 != 0 && dx > 0) ? Math.abs((double) s.axisLen0 / dx) : 0;
    s.physicalSizeY = (s.axisLen1 != 0 && dy > 0) ? Math.abs((double) s.axisLen1 / dy) : 0;

    String lbl2 = s.axisLabel2 != null ? s.axisLabel2.trim() : "";
    String lbl3 = s.axisLabel3 != null ? s.axisLabel3.trim() : "";

    boolean axis2IsZ = containsIgnoreCase(lbl2, "Z");
    boolean axis2IsTime = containsIgnoreCase(lbl2, "Time") || lbl2.equalsIgnoreCase("T");

    boolean axis3IsZ = containsIgnoreCase(lbl3, "Z");
    boolean axis3IsTime = containsIgnoreCase(lbl3, "Time") || lbl3.equalsIgnoreCase("T");
    boolean axis3IsChannel = containsIgnoreCase(lbl3, "WL") ||
      containsIgnoreCase(lbl3, "Wavelength") || containsIgnoreCase(lbl3, "Laser") ||
      containsIgnoreCase(lbl3, "Channel");

    if (axis2IsZ && axis3IsTime) {
      // Z varies fastest, then Time
      s.sizeZ = dz;
      s.sizeT = dt;
      s.sizeC = 1;
      s.dimensionOrder = "XYZCT";
      s.physicalSizeZ = Math.abs((double) s.axisLen2 / dz);
      s.timeStep = (dt > 1) ? Math.abs((double) s.axisLen3 / dt) : 0;
    }
    else if (axis2IsTime && axis3IsZ) {
      // Time varies fastest, then Z
      s.sizeZ = dt;
      s.sizeT = dz;
      s.sizeC = 1;
      s.dimensionOrder = "XYTCZ";
      s.physicalSizeZ = Math.abs((double) s.axisLen3 / dt);
      s.timeStep = (dz > 1) ? Math.abs((double) s.axisLen2 / dz) : 0;
    }
    else if (axis2IsTime && axis3IsChannel) {
      // Time varies fastest, then Channel / Wavelength
      s.sizeZ = 1;
      s.sizeT = dz;
      s.sizeC = dt;
      s.dimensionOrder = "XYTCZ";
      s.timeStep = (dz > 1) ? Math.abs((double) s.axisLen2 / dz) : 0;
    }
    else if (axis2IsZ) {
      // Z stack (single timepoint or secondary axis)
      s.sizeZ = dz;
      s.sizeT = (dt > 1) ? dt : 1;
      s.sizeC = 1;
      s.dimensionOrder = "XYZCT";
      s.physicalSizeZ = Math.abs((double) s.axisLen2 / dz);
    }
    else if (axis2IsTime) {
      // Time series
      s.sizeZ = (dt > 1) ? dt : 1;
      s.sizeT = dz;
      s.sizeC = 1;
      s.dimensionOrder = "XYZTC";
      s.timeStep = (dz > 1) ? Math.abs((double) s.axisLen2 / dz) : 0;
    }
    else {
      // Default fallback
      s.sizeZ = dz > 0 ? dz : 1;
      s.sizeT = dt > 0 ? dt : 1;
      s.sizeC = 1;
      s.dimensionOrder = "XYZCT";
    }
  }

  private boolean containsIgnoreCase(String str, String search) {
    if (str == null || search == null) return false;
    return str.toLowerCase().contains(search.toLowerCase());
  }

  /**
   * Reads top-level header metadata containing acquisition settings.
   */
  private void readTopMetadata() {
    try {
      in.seek(0);
      int searchLimit = (int) Math.min(in.length(), 65536);
      byte[] buf = new byte[searchLimit];
      in.read(buf);

      String s = new String(buf, StandardCharsets.ISO_8859_1);
      int idx = s.indexOf("First Name::");
      if (idx >= 0) {
        int end = s.indexOf("\0", idx);
        if (end < 0) end = Math.min(s.length(), idx + 1024);
        String metaStr = s.substring(idx, end);
        String[] tokens = metaStr.split("::");
        for (int i = 0; i + 1 < tokens.length; i += 2) {
          String key = tokens[i].trim();
          String val = tokens[i + 1].trim();
          if (!key.isEmpty()) {
            addGlobalMeta(key, val);
          }
        }
      }
    }
    catch (Exception e) {
      LOGGER.debug("Could not read top metadata", e);
    }
  }

  /**
   * Searches a byte array in memory for an ASCII / Latin-1 needle string.
   */
  private int findInBuffer(byte[] buf, String needle) {
    byte[] n = needle.getBytes(StandardCharsets.ISO_8859_1);
    for (int i = 0; i <= buf.length - n.length; i++) {
      int j = 0;
      while (j < n.length && buf[i + j] == n[j]) {
        j++;
      }
      if (j == n.length) return i;
    }
    return -1;
  }

  /** Extracts a 32-bit float value from buffer located 4 bytes after the needle. */
  private Float findBufferFloat(byte[] buf, String needle) {
    int idx = findInBuffer(buf, needle);
    if (idx < 0) return null;
    int pos = idx + needle.length() + 4;
    if (pos + 4 > buf.length) return null;
    int asInt = readIntLittleEndian(buf, pos);
    return Float.intBitsToFloat(asInt);
  }

  /** Extracts a 64-bit double value from buffer located 4 bytes after the needle. */
  private Double findBufferDouble(byte[] buf, String needle) {
    int idx = findInBuffer(buf, needle);
    if (idx < 0) return null;
    int pos = idx + needle.length() + 4;
    if (pos + 8 > buf.length) return null;
    long l = (buf[pos] & 0xffL) |
             ((buf[pos + 1] & 0xffL) << 8) |
             ((buf[pos + 2] & 0xffL) << 16) |
             ((buf[pos + 3] & 0xffL) << 24) |
             ((buf[pos + 4] & 0xffL) << 32) |
             ((buf[pos + 5] & 0xffL) << 40) |
             ((buf[pos + 6] & 0xffL) << 48) |
             ((buf[pos + 7] & 0xffL) << 56);
    return Double.longBitsToDouble(l);
  }

  /** Extracts a 32-bit unsigned int from buffer located 4 bytes after the needle. */
  private Long findBufferUnsignedInt(byte[] buf, String needle) {
    int idx = findInBuffer(buf, needle);
    if (idx < 0) return null;
    int pos = idx + needle.length() + 4;
    if (pos + 4 > buf.length) return null;
    return readIntLittleEndian(buf, pos) & 0xffffffffL;
  }

  /** Extracts a boolean value from buffer located 4 bytes after the needle. */
  private Boolean findBufferBoolean(byte[] buf, String needle) {
    int idx = findInBuffer(buf, needle);
    if (idx < 0) return null;
    int pos = idx + needle.length() + 4;
    if (pos >= buf.length) return null;
    return buf[pos] != 0;
  }

  /**
   * Reads acquisition footer metadata (power, PMT gains, table positions) if present.
   */
  private void readFooterMetadata() {
    if (stacks.isEmpty()) return;
    try {
      StackInfo last = stacks.get(stacks.size() - 1);
      long lastPixelEnd = last.pixelOffset + last.pixelBytes;
      if (in.length() <= lastPixelEnd + 100) {
        return; // No footer present
      }

      long footerLen = in.length() - lastPixelEnd;
      int readLen = (int) Math.min(footerLen, 2097152L);
      in.seek(lastPixelEnd);
      byte[] footerBuf = new byte[readLen];
      in.readFully(footerBuf);

      // 1. Laser Power
      Float pwr = findBufferFloat(footerBuf, "TriMScope Actual Power");
      if (pwr != null) addGlobalMeta("Ti:Sa Power", pwr);

      // 2. PMT Gains & Status
      Long pmt1Gain = findBufferUnsignedInt(footerBuf, "ResonantPMT Photom. Gain Ch.0");
      if (pmt1Gain != null) addGlobalMeta("PMT1 Gain", pmt1Gain);
      Long pmt2Gain = findBufferUnsignedInt(footerBuf, "ResonantPMT Photom. Gain Ch.1");
      if (pmt2Gain != null) addGlobalMeta("PMT2 Gain", pmt2Gain);

      Boolean pmt1Act = findBufferBoolean(footerBuf, "ResonantPMT Use Channel 0");
      if (pmt1Act != null) addGlobalMeta("PMT1 Active", pmt1Act);
      Boolean pmt2Act = findBufferBoolean(footerBuf, "ResonantPMT Use Channel 1");
      if (pmt2Act != null) addGlobalMeta("PMT2 Active", pmt2Act);

      // Galvo PMT fallbacks
      Long galvoPmt1 = findBufferUnsignedInt(footerBuf, "GalvoPMT Photom. Gain Ch.0");
      if (galvoPmt1 != null) addGlobalMeta("Galvo PMT1 Gain", galvoPmt1);
      Long galvoPmt2 = findBufferUnsignedInt(footerBuf, "GalvoPMT Photom. Gain Ch.1");
      if (galvoPmt2 != null) addGlobalMeta("Galvo PMT2 Gain", galvoPmt2);

      // 3. Z-Stage Range & Steps
      Float zStart = findBufferFloat(footerBuf, "xyz-Table Z Start position");
      if (zStart != null) addGlobalMeta("xyz-Table Z Start position", zStart);
      Float zStop = findBufferFloat(footerBuf, "xyz-Table Z Stop position");
      if (zStop != null) addGlobalMeta("xyz-Table Z Stop position", zStop);
      Float zLen = findBufferFloat(footerBuf, "xyz-Table Z Length");
      if (zLen != null) {
        addGlobalMeta("xyz-Table Z Length", zLen);
        if (stacks.get(0).sizeZ > 0) {
          addGlobalMeta("xyz-Table Z Step size", zLen / stacks.get(0).sizeZ);
        }
      }

      // 4. XYZ Zero Offsets
      zeroOffsetX = findBufferFloat(footerBuf, "xyz-Table X set as zero offset");
      if (zeroOffsetX != null) addGlobalMeta("xyz-Table X set as zero offset", zeroOffsetX);
      zeroOffsetY = findBufferFloat(footerBuf, "xyz-Table Y set as zero offset");
      if (zeroOffsetY != null) addGlobalMeta("xyz-Table Y set as zero offset", zeroOffsetY);
      zeroOffsetZ = findBufferFloat(footerBuf, "xyz-Table Z set as zero offset");
      if (zeroOffsetZ != null) addGlobalMeta("xyz-Table Z set as zero offset", zeroOffsetZ);

      // 5. XYZ Raw Hardware Measurement Positions
      rawMeasX = findBufferDouble(footerBuf, "xyz-Table X measurement position");
      if (rawMeasX != null) addGlobalMeta("xyz-Table X measurement position", rawMeasX);
      rawMeasY = findBufferDouble(footerBuf, "xyz-Table Y measurement position");
      if (rawMeasY != null) addGlobalMeta("xyz-Table Y measurement position", rawMeasY);
      rawMeasZ = findBufferDouble(footerBuf, "xyz-Table Z measurement position");
      if (rawMeasZ != null) addGlobalMeta("xyz-Table Z measurement position", rawMeasZ);

      // 6. Record Raw and Relative Global Positions
      if (rawMeasX != null) {
        addGlobalMeta("xyz-Table X Raw Position", rawMeasX);
        double relX = (zeroOffsetX != null) ? rawMeasX + zeroOffsetX : rawMeasX;
        addGlobalMeta("xyz-Table X Relative Position", relX);
      }
      if (rawMeasY != null) {
        addGlobalMeta("xyz-Table Y Raw Position", rawMeasY);
        double relY = (zeroOffsetY != null) ? rawMeasY + zeroOffsetY : rawMeasY;
        addGlobalMeta("xyz-Table Y Relative Position", relY);
      }
      if (rawMeasZ != null) {
        addGlobalMeta("xyz-Table Z Raw Position", rawMeasZ);
        double relZ = (zeroOffsetZ != null) ? rawMeasZ + zeroOffsetZ : rawMeasZ;
        addGlobalMeta("xyz-Table Z Relative Position", relZ);
      }
    }
    catch (Exception e) {
      LOGGER.debug("Could not read footer metadata", e);
    }
  }

  /**
   * Computes both raw and relative stage coordinates for each discovered series.
   */
  private void computeStageCoordinates() {
    for (int i = 0; i < stacks.size(); i++) {
      StackInfo info = stacks.get(i);

      // In MSR files, the stage position (s0, s1, s2) in the stack header is the relative stage coordinate.
      // If zero offsets are available:
      // Relative = Raw + ZeroOffset  =>  Raw = Relative - ZeroOffset
      if (zeroOffsetX != null) {
        info.stageXRaw = info.stageX - zeroOffsetX;
      }
      else if (rawMeasX != null) {
        info.stageXRaw = rawMeasX;
      }

      if (zeroOffsetY != null) {
        info.stageYRaw = info.stageY - zeroOffsetY;
      }
      else if (rawMeasY != null) {
        info.stageYRaw = rawMeasY;
      }

      if (zeroOffsetZ != null) {
        info.stageZRaw = info.stageZ - zeroOffsetZ;
      }
      else if (rawMeasZ != null) {
        info.stageZRaw = rawMeasZ;
      }

      // Store in series metadata
      setSeries(i);
      addSeriesMeta("Stage Position X (Relative)", info.stageX);
      addSeriesMeta("Stage Position Y (Relative)", info.stageY);
      addSeriesMeta("Stage Position Z (Relative)", info.stageZ);

      if (info.stageXRaw != null) {
        addSeriesMeta("Stage Position X (Raw)", info.stageXRaw);
      }
      if (info.stageYRaw != null) {
        addSeriesMeta("Stage Position Y (Raw)", info.stageYRaw);
      }
      if (info.stageZRaw != null) {
        addSeriesMeta("Stage Position Z (Raw)", info.stageZRaw);
      }
      if (zeroOffsetX != null) {
        addSeriesMeta("xyz-Table X set as zero offset", zeroOffsetX);
      }
      if (zeroOffsetY != null) {
        addSeriesMeta("xyz-Table Y set as zero offset", zeroOffsetY);
      }
      if (zeroOffsetZ != null) {
        addSeriesMeta("xyz-Table Z set as zero offset", zeroOffsetZ);
      }
    }
    setSeries(0);
  }

  /**
   * Populates the OME-XML MetadataStore with image names, dimensions, physical
   * calibration, and stage coordinates for all series.
   */
  private void populateMetadataStore() throws FormatException {
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    for (int s = 0; s < stacks.size(); s++) {
      StackInfo info = stacks.get(s);

      // Set series / image name
      if (info.seriesName != null && !info.seriesName.trim().isEmpty()) {
        store.setImageName(info.seriesName.trim(), s);
      }

      // Physical pixel sizes in micrometers
      if (info.physicalSizeX > 0) {
        Length px = FormatTools.getPhysicalSizeX(info.physicalSizeX, "um");
        if (px != null) store.setPixelsPhysicalSizeX(px, s);
      }
      if (info.physicalSizeY > 0) {
        Length py = FormatTools.getPhysicalSizeY(info.physicalSizeY, "um");
        if (py != null) store.setPixelsPhysicalSizeY(py, s);
      }
      if (info.physicalSizeZ > 0) {
        Length pz = FormatTools.getPhysicalSizeZ(info.physicalSizeZ, "um");
        if (pz != null) store.setPixelsPhysicalSizeZ(pz, s);
      }

      // Time increment in seconds
      if (info.timeStep > 0) {
        store.setPixelsTimeIncrement(new Time(info.timeStep, UNITS.SECOND), s);
      }

      // Stage position coordinates (for multi-position and mosaic tiles)
      if (info.stageX != 0 || info.stageY != 0 || info.stageZ != 0) {
        Length stX = new Length(info.stageX, UNITS.MICROMETER);
        Length stY = new Length(info.stageY, UNITS.MICROMETER);
        Length stZ = new Length(info.stageZ, UNITS.MICROMETER);

        store.setPlanePositionX(stX, s, 0);
        store.setPlanePositionY(stY, s, 0);
        store.setPlanePositionZ(stZ, s, 0);

        store.setStageLabelName(info.seriesName, s);
        store.setStageLabelX(stX, s);
        store.setStageLabelY(stY, s);
        store.setStageLabelZ(stZ, s);
      }
    }
  }

}