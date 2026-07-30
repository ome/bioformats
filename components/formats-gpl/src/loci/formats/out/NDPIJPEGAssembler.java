/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2026 Open Microscopy Environment:
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

package loci.formats.out;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEGCodec;

/**
 * Streams one continuous NDPI-style restart-marked JPEG assembled from
 * independently compressed MCU-row regions.
 *
 * Input must be interleaved, unsigned 8-bit RGB rows supplied in
 * top-to-bottom order; no other layout is accepted. Pixel buffering is
 * limited to one eight-pixel-high MCU row.
 */
final class NDPIJPEGAssembler {

  // -- Constants --

  private static final int CHANNELS = 3;
  private static final int MCU_SIZE = 8;
  private static final int DRI = 0xffdd;
  private static final int EOI = 0xffd9;
  private static final int SOS = 0xffda;

  // -- Fields --

  private final RandomAccessOutputStream out;
  private final int width;
  private final int height;
  private final int restartMCUs;
  private final boolean indexed;
  private final double quality;
  private final int regionWidth;
  private final byte[] rowBuffer;
  private final byte[] imageBuffer;
  private final long[] mcuStarts;
  private final int[] captureRegions;
  private final byte[][] capturedEntropy;

  private ParsedJPEG template;
  private int bufferedRows;
  private int writtenRows;
  private int regions;
  private long jpegOffset = -1;
  private boolean finished;

  // -- Constructor --

  /**
   * Construct an assembler at the destination's current file position.
   *
   * @param out destination stream
   * @param width complete image width
   * @param height complete image height
   * @param restartMCUs number of 8-by-8 MCUs in each restart interval;
   *   ignored when not indexed
   * @param indexed whether to emit a restart-marked, MCU-indexed JPEG rather
   *   than one ordinary baseline JPEG buffered in full
   * @param quality JPEG quality between 0.25 and 1
   * @param captureRegions restart regions whose entropy data is retained for
   *   AuthCode calculation, or null
   */
  NDPIJPEGAssembler(RandomAccessOutputStream out, int width, int height,
    int restartMCUs, boolean indexed, double quality, int[] captureRegions)
    throws FormatException
  {
    if (out == null) {
      throw new IllegalArgumentException("Destination stream cannot be null");
    }
    if (width <= 0 || height <= 0) {
      throw new FormatException("JPEG dimensions must be positive");
    }
    if (indexed && (restartMCUs <= 0 || restartMCUs > 0xffff)) {
      throw new FormatException("JPEG restart interval must be 1..65535 MCUs");
    }
    if (!Double.isFinite(quality) || quality < 0.25 || quality > 1) {
      throw new FormatException("JPEG quality must be between 0.25 and 1");
    }
    long mcuColumns = ((long) width + MCU_SIZE - 1) / MCU_SIZE;
    if (indexed && mcuColumns % restartMCUs != 0) {
      throw new FormatException("Padded JPEG width must contain a whole " +
        "number of restart intervals");
    }
    long rowBytes = (long) width * CHANNELS;
    long bufferBytes = rowBytes * MCU_SIZE;
    if (bufferBytes > Integer.MAX_VALUE) {
      throw new FormatException("One JPEG MCU row is too large to buffer");
    }
    this.out = out;
    this.width = width;
    this.height = height;
    this.restartMCUs = restartMCUs;
    this.indexed = indexed;
    this.quality = quality;
    this.regionWidth = indexed ? restartMCUs * MCU_SIZE : 0;
    this.rowBuffer = indexed ? new byte[(int) bufferBytes] : null;
    long imageBytes = rowBytes * height;
    if (!indexed && imageBytes > Integer.MAX_VALUE) {
      throw new FormatException(
        "Non-indexed JPEG is too large to buffer as one image");
    }
    this.imageBuffer = indexed ? null : new byte[(int) imageBytes];
    long regionRows = ((long) height + MCU_SIZE - 1) / MCU_SIZE;
    long regionColumns = indexed ?
      ((long) width + regionWidth - 1) / regionWidth : 0;
    long regionCount = regionRows * regionColumns;
    if (regionCount > Integer.MAX_VALUE) {
      throw new FormatException("JPEG contains too many restart regions");
    }
    this.mcuStarts = indexed ?
      new long[(int) regionCount] : new long[0];
    this.captureRegions =
      captureRegions == null ? new int[0] : captureRegions.clone();
    if (!indexed && this.captureRegions.length > 0) {
      throw new FormatException(
        "Non-indexed JPEG cannot capture restart regions");
    }
    this.capturedEntropy = new byte[this.captureRegions.length][];
    for (int region : this.captureRegions) {
      if (region < 0 || region >= regionCount) {
        throw new FormatException("Captured JPEG region is out of range");
      }
    }
  }

  // -- NDPIJPEGAssembler API methods --

  /**
   * Append one or more complete image rows.
   */
  void writeRows(byte[] pixels, int offset, int rows)
    throws FormatException, IOException
  {
    ensureOpen();
    if (pixels == null) {
      throw new IllegalArgumentException("Pixel buffer cannot be null");
    }
    if (rows <= 0 || writtenRows + rows > height) {
      throw new FormatException("Rows must be written once, top to bottom");
    }
    int rowBytes = width * CHANNELS;
    long required = (long) rows * rowBytes;
    if (offset < 0 || required > pixels.length - (long) offset) {
      throw new FormatException("Pixel buffer does not contain " + rows +
        " complete RGB rows");
    }
    if (!indexed) {
      System.arraycopy(pixels, offset, imageBuffer, writtenRows * rowBytes,
        (int) required);
      writtenRows += rows;
      return;
    }

    int source = offset;
    int remaining = rows;
    while (remaining > 0) {
      int count = Math.min(remaining, MCU_SIZE - bufferedRows);
      System.arraycopy(pixels, source, rowBuffer, bufferedRows * rowBytes,
        count * rowBytes);
      source += count * rowBytes;
      bufferedRows += count;
      writtenRows += count;
      remaining -= count;
      if (bufferedRows == MCU_SIZE) {
        writeMCURow(bufferedRows);
        bufferedRows = 0;
      }
    }
  }

  /**
   * Finish the JPEG and return its location and restart-segment starts.
   */
  Result finish() throws FormatException, IOException {
    ensureOpen();
    if (writtenRows != height) {
      throw new FormatException("Expected " + height + " rows but received " +
        writtenRows);
    }
    if (!indexed) {
      byte[] compressed = new JPEGCodec().compress(imageBuffer,
        options(width, height, quality));
      ParsedJPEG parsed = ParsedJPEG.parse(compressed);
      parsed.assert444Sampling();
      parsed.assertNoRestartMarkers();
      jpegOffset = out.getFilePointer();
      out.write(compressed);
      finished = true;
      return new Result(jpegOffset, compressed.length, new long[0],
        new byte[0][]);
    }
    if (bufferedRows > 0) {
      writeMCURow(bufferedRows);
      bufferedRows = 0;
    }
    writeMarker(EOI);
    finished = true;
    return new Result(jpegOffset, out.getFilePointer() - jpegOffset,
      mcuStarts.clone(), capturedEntropy.clone());
  }

  // -- Helper methods --

  private void writeMCURow(int rows) throws FormatException, IOException {
    for (int x = 0; x < width; x += regionWidth) {
      int pieceWidth = Math.min(regionWidth, width - x);
      byte[] piece = extractPiece(x, pieceWidth, rows);
      byte[] compressed = new JPEGCodec().compress(piece,
        options(pieceWidth, rows, quality));
      ParsedJPEG parsed = ParsedJPEG.parse(compressed);

      if (template == null) {
        template = parsed;
        template.assert444Sampling();
        jpegOffset = out.getFilePointer();
        byte[] header = Arrays.copyOf(template.header, template.header.length);
        patchDimensions(header, template.sofOffset, width, height);
        out.write(header, 0, template.sosOffset);
        writeMarker(DRI);
        out.write(0);
        out.write(4);
        out.write(restartMCUs >>> 8);
        out.write(restartMCUs);
        out.write(header, template.sosOffset,
          header.length - template.sosOffset);
      }
      else {
        template.assertCompatible(parsed);
        writeMarker(0xffd0 + ((regions - 1) & 7));
      }
      mcuStarts[regions] = out.getFilePointer() - jpegOffset;
      for (int i = 0; i < captureRegions.length; i++) {
        if (captureRegions[i] == regions) {
          capturedEntropy[i] = parsed.entropy;
        }
      }
      out.write(parsed.entropy);
      regions++;
    }
  }

  private byte[] extractPiece(int x, int pieceWidth, int rows) {
    int sourceRowBytes = width * CHANNELS;
    int pieceRowBytes = pieceWidth * CHANNELS;
    byte[] piece = new byte[pieceRowBytes * rows];
    for (int row = 0; row < rows; row++) {
      System.arraycopy(rowBuffer, row * sourceRowBytes + x * CHANNELS, piece,
        row * pieceRowBytes, pieceRowBytes);
    }
    return piece;
  }

  private void writeMarker(int marker) throws IOException {
    out.write(marker >>> 8);
    out.write(marker);
  }

  private void ensureOpen() throws FormatException {
    if (finished) {
      throw new FormatException("JPEG assembly is already complete");
    }
  }

  private static CodecOptions options(int width, int height, double quality) {
    CodecOptions options = CodecOptions.getDefaultOptions();
    options.width = width;
    options.height = height;
    options.channels = CHANNELS;
    options.bitsPerSample = 8;
    options.interleaved = true;
    options.lossless = false;
    options.quality = quality;
    options.disableChromaSubsampling = true;
    return options;
  }

  private static void patchDimensions(byte[] header, int sofOffset, int width,
    int height)
  {
    int jpegWidth = width > 0xffff ? 0 : width;
    int jpegHeight = height > 0xffff ? 0 : height;
    header[sofOffset + 5] = (byte) (jpegHeight >>> 8);
    header[sofOffset + 6] = (byte) jpegHeight;
    header[sofOffset + 7] = (byte) (jpegWidth >>> 8);
    header[sofOffset + 8] = (byte) jpegWidth;
  }

  /** Completed JPEG location information. */
  static final class Result {

    private final long jpegOffset;
    private final long jpegLength;
    private final long[] mcuStarts;
    private final byte[][] capturedEntropy;

    private Result(long jpegOffset, long jpegLength, long[] mcuStarts,
      byte[][] capturedEntropy)
    {
      this.jpegOffset = jpegOffset;
      this.jpegLength = jpegLength;
      this.mcuStarts = mcuStarts;
      this.capturedEntropy = capturedEntropy;
    }

    long getJPEGOffset() {
      return jpegOffset;
    }

    long getJPEGLength() {
      return jpegLength;
    }

    long[] getMCUStarts() {
      return mcuStarts.clone();
    }

    byte[] getCapturedEntropy(int index) {
      return capturedEntropy[index].clone();
    }
  }

  /** A parsed baseline JPEG. */
  static final class ParsedJPEG {

    private final byte[] header;
    private final byte[] entropy;
    private final List<byte[]> quantizationTables;
    private final List<byte[]> huffmanTables;
    private final byte[] frameComponents;
    private final byte[] scanComponents;
    private final int sofOffset;
    private final int sosOffset;
    private final boolean hasDRI;

    private ParsedJPEG(byte[] header, byte[] entropy,
      List<byte[]> quantizationTables, List<byte[]> huffmanTables,
      byte[] frameComponents, byte[] scanComponents, int sofOffset,
      int sosOffset, boolean hasDRI)
    {
      this.header = header;
      this.entropy = entropy;
      this.quantizationTables = quantizationTables;
      this.huffmanTables = huffmanTables;
      this.frameComponents = frameComponents;
      this.scanComponents = scanComponents;
      this.sofOffset = sofOffset;
      this.sosOffset = sosOffset;
      this.hasDRI = hasDRI;
    }

    static ParsedJPEG parse(byte[] jpeg) throws FormatException {
      if (jpeg.length < 4 || marker(jpeg, 0) != 0xffd8) {
        throw new FormatException("JPEG piece does not begin with SOI");
      }
      List<byte[]> dqt = new ArrayList<byte[]>();
      List<byte[]> dht = new ArrayList<byte[]>();
      byte[] frame = null;
      byte[] scan = null;
      int sof = -1;
      int sos = -1;
      int entropyStart = -1;
      int entropyEnd = -1;
      boolean hasDRI = false;
      int offset = 2;
      while (offset + 1 < jpeg.length) {
        if ((jpeg[offset] & 0xff) != 0xff) {
          throw new FormatException("Expected JPEG marker at " + offset);
        }
        int code = marker(jpeg, offset);
        if (code == EOI) break;
        if (offset + 3 >= jpeg.length) {
          throw new FormatException("Truncated JPEG marker");
        }
        int length = u16(jpeg, offset + 2);
        if (length < 2 || offset + 2 + length > jpeg.length) {
          throw new FormatException("Invalid JPEG marker length");
        }
        if (code == 0xffdb) {
          dqt.add(slice(jpeg, offset + 4, offset + 2 + length));
        }
        else if (code == 0xffc4) {
          dht.add(slice(jpeg, offset + 4, offset + 2 + length));
        }
        else if (code == 0xffc0) {
          sof = offset;
          int components = jpeg[offset + 9] & 0xff;
          frame = slice(jpeg, offset + 9, offset + 10 + 3 * components);
        }
        else if (code == DRI) {
          hasDRI = true;
        }
        else if (isSOF(code)) {
          throw new FormatException("Only baseline sequential JPEG is " +
            "supported, found SOF marker 0x" + Integer.toHexString(code));
        }
        else if (code == SOS) {
          sos = offset;
          int components = jpeg[offset + 4] & 0xff;
          scan = slice(jpeg, offset + 4, offset + 5 + 2 * components);
          entropyStart = offset + 2 + length;
          entropyEnd = findEntropyEnd(jpeg, entropyStart);
          break;
        }
        offset += 2 + length;
      }
      if (sof < 0 || sos < 0 || entropyStart < 0 || entropyEnd < 0) {
        throw new FormatException("Incomplete JPEG marker structure");
      }
      return new ParsedJPEG(slice(jpeg, 0, entropyStart),
        slice(jpeg, entropyStart, entropyEnd), dqt, dht, frame, scan, sof, sos,
        hasDRI);
    }

    void assertCompatible(ParsedJPEG other) throws FormatException {
      if (!byteListsEqual(quantizationTables, other.quantizationTables)) {
        throw new FormatException("JPEG quantization tables differ between " +
          "restart regions");
      }
      if (!byteListsEqual(huffmanTables, other.huffmanTables)) {
        throw new FormatException("JPEG Huffman tables differ between " +
          "restart regions");
      }
      if (!Arrays.equals(frameComponents, other.frameComponents)) {
        throw new FormatException("JPEG frame components or sampling factors " +
          "differ between restart regions");
      }
      if (!Arrays.equals(scanComponents, other.scanComponents)) {
        throw new FormatException("JPEG scan components differ between " +
          "restart regions");
      }
    }

    void assert444Sampling() throws FormatException {
      // parse() rejects any stream without an SOF0, and sets the frame
      // components alongside it, so frameComponents is never null here.
      if ((frameComponents[0] & 0xff) != CHANNELS) {
        throw new FormatException("NDPI JPEG requires three components");
      }
      for (int i = 0; i < CHANNELS; i++) {
        if ((frameComponents[2 + 3 * i] & 0xff) != 0x11) {
          throw new FormatException("NDPI JPEG requires 4:4:4 sampling");
        }
      }
    }

    void assertNoRestartMarkers() throws FormatException {
      if (hasDRI) {
        throw new FormatException("Non-indexed JPEG must not contain DRI");
      }
      for (int i = 0; i + 1 < entropy.length; i++) {
        if ((entropy[i] & 0xff) == 0xff) {
          int next = entropy[i + 1] & 0xff;
          if (next >= 0xd0 && next <= 0xd7) {
            throw new FormatException(
              "Non-indexed JPEG must not contain restart markers");
          }
        }
      }
    }

    private static int findEntropyEnd(byte[] jpeg, int start)
      throws FormatException
    {
      int offset = start;
      while (offset + 1 < jpeg.length) {
        if ((jpeg[offset] & 0xff) != 0xff) {
          offset++;
          continue;
        }
        int next = jpeg[offset + 1] & 0xff;
        if (next == 0) {
          offset += 2;
          continue;
        }
        int markerOffset = offset;
        while (next == 0xff) {
          offset++;
          if (offset + 1 >= jpeg.length) {
            throw new FormatException("Truncated JPEG fill bytes");
          }
          next = jpeg[offset + 1] & 0xff;
        }
        if (next == 0xd9) return markerOffset;
        if (next == 0xda) {
          throw new FormatException("Multiple JPEG scans are not supported");
        }
        if (next == 0) {
          throw new FormatException("Invalid fill before stuffed entropy byte");
        }
        if (next >= 0xd0 && next <= 0xd7) {
          offset += 2;
          continue;
        }
        throw new FormatException("Unexpected marker in JPEG entropy data");
      }
      throw new FormatException("JPEG entropy data has no EOI");
    }

    private static boolean byteListsEqual(List<byte[]> first,
      List<byte[]> second)
    {
      if (first.size() != second.size()) return false;
      for (int i = 0; i < first.size(); i++) {
        if (!Arrays.equals(first.get(i), second.get(i))) return false;
      }
      return true;
    }

    private static boolean isSOF(int code) {
      int low = code & 0xff;
      return code >= 0xffc0 && code <= 0xffcf &&
        low != 0xc4 && low != 0xc8 && low != 0xcc;
    }

    private static byte[] slice(byte[] bytes, int start, int end) {
      return Arrays.copyOfRange(bytes, start, end);
    }

    private static int marker(byte[] bytes, int offset) {
      return (bytes[offset] & 0xff) << 8 | bytes[offset + 1] & 0xff;
    }

    private static int u16(byte[] bytes, int offset) {
      return (bytes[offset] & 0xff) << 8 | bytes[offset + 1] & 0xff;
    }
  }
}
