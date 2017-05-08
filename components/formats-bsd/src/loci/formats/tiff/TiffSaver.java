/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.tiff;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import loci.common.ByteArrayHandle;
import loci.common.Constants;
import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.codec.CodecOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses TIFF data from an input source.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Chris Allan callan at blackcat.ca
 */
public class TiffSaver {

  // -- Constructor --

  private static final Logger LOGGER = LoggerFactory.getLogger(TiffSaver.class);

  // -- Fields --

  /** Output stream to use when saving TIFF data. */
  protected RandomAccessOutputStream out;

  /** Output filename. */
  protected String filename;

  /** Output bytes. */
  protected ByteArrayHandle bytes;

  /** Whether or not to write BigTIFF data. */
  private boolean bigTiff = false;
  private boolean sequentialWrite = false;
  
  /** Store tile offsets and original file pointer when writing sequentially. */
  private List<Long> sequentialTileOffsets;
  private Long sequentialTileFilePointer;

  /** The codec options if set. */
  private CodecOptions options;

  // -- Constructors --
  /**
   * Constructs a new TIFF saver from the given filename.
   * @param filename Filename of the output stream that we may use to create
   * extra input or output streams as required.
   */
  public TiffSaver(String filename) throws IOException {
    this(new RandomAccessOutputStream(filename), filename);
  }

  /**
   * Constructs a new TIFF saver from the given output source.
   * @param out Output stream to save TIFF data to.
   * @param filename Filename of the output stream that we may use to create
   * extra input or output streams as required.
   */
  public TiffSaver(RandomAccessOutputStream out, String filename) {
    if (out == null) {
      throw new IllegalArgumentException(
          "Output stream expected to be not-null");
    }
    if (filename == null) {
      throw new IllegalArgumentException(
          "Filename expected to be not null");
    }
    this.out = out;
    this.filename = filename;
  }

  /**
   * Constructs a new TIFF saver from the given output source.
   * @param out Output stream to save TIFF data to.
   * @param bytes In memory byte array handle that we may use to create
   * extra input or output streams as required.
   */
  public TiffSaver(RandomAccessOutputStream out, ByteArrayHandle bytes) {
    if (out == null) {
      throw new IllegalArgumentException(
          "Output stream expected to be not-null");
    }
    if (bytes == null) {
      throw new IllegalArgumentException(
          "Bytes expected to be not null");
    }
    this.out = out;
    this.bytes = bytes;
  }

  // -- TiffSaver methods --

  /**
   * Closes the output stream if not null.
   * @throws IOException Thrown if an error occurred while closing.
   */
  public void close() throws IOException {
    if (out != null) {
      out.close();
    }
  }

  /**
   * Sets whether or not we know that the planes will be written sequentially.
   * If we are writing planes sequentially and set this flag, then performance
   * is slightly improved.
   */
  public void setWritingSequentially(boolean sequential) {
    sequentialWrite = sequential;
  }

  /** Gets the stream from which TIFF data is being saved. */
  public RandomAccessOutputStream getStream() {
    return out;
  }

  /** Sets whether or not little-endian data should be written. */
  public void setLittleEndian(boolean littleEndian) {
    out.order(littleEndian);
  }

  /** Sets whether or not BigTIFF data should be written. */
  public void setBigTiff(boolean bigTiff) {
    this.bigTiff = bigTiff;
  }

  /** Returns whether or not we are writing little-endian data. */
  public boolean isLittleEndian() {
    return out.isLittleEndian();
  }

  /** Returns whether or not we are writing BigTIFF data. */
  public boolean isBigTiff() { return bigTiff; }

  /**
   * Sets the codec options.
   * @param options The value to set.
   */
  public void setCodecOptions(CodecOptions options) {
    this.options = options;
  }

  /** Writes the TIFF file header. */
  public void writeHeader() throws IOException {
    // write endianness indicator
    out.seek(0);
    if (isLittleEndian()) {
      out.writeByte(TiffConstants.LITTLE);
      out.writeByte(TiffConstants.LITTLE);
    }
    else {
      out.writeByte(TiffConstants.BIG);
      out.writeByte(TiffConstants.BIG);
    }
    // write magic number
    if (bigTiff) {
      out.writeShort(TiffConstants.BIG_TIFF_MAGIC_NUMBER);
    }
    else out.writeShort(TiffConstants.MAGIC_NUMBER);

    // write the offset to the first IFD

    // for vanilla TIFFs, 8 is the offset to the first IFD
    // for BigTIFFs, 8 is the number of bytes in an offset
    if (bigTiff) {
      out.writeShort(8);
      out.writeShort(0);

      // write the offset to the first IFD for BigTIFF files
      out.writeLong(16);
    }
    else {
      out.writeInt(8);
    }
  }

  /**
   */
  public void writeImage(byte[][] buf, IFDList ifds, int pixelType)
    throws FormatException, IOException
  {
    if (ifds == null) {
      throw new FormatException("IFD cannot be null");
    }
    if (buf == null) {
      throw new FormatException("Image data cannot be null");
    }
    for (int i=0; i<ifds.size(); i++) {
      if (i < buf.length) {
        writeImage(buf[i], ifds.get(i), i, pixelType, i == ifds.size() - 1);
      }
    }
  }

  /**
   */
  public void writeImage(byte[] buf, IFD ifd, int no, int pixelType,
    boolean last)
    throws FormatException, IOException
  {
    if (ifd == null) {
      throw new FormatException("IFD cannot be null");
    }
    int w = (int) ifd.getImageWidth();
    int h = (int) ifd.getImageLength();
    writeImage(buf, ifd, no, pixelType, 0, 0, w, h, last);
  }

  /**
   * Writes to any rectangle from the passed block.
   *
   * @param buf The block that is to be written.
   * @param ifd The Image File Directories. Mustn't be <code>null</code>.
   * @param no  The image index within the current file, starting from 0.
   * @param pixelType The type of pixels.
   * @param x   The X-coordinate of the top-left corner.
   * @param y   The Y-coordinate of the top-left corner.
   * @param w   The width of the rectangle.
   * @param h   The height of the rectangle.
   * @param last Pass <code>true</code> if it is the last image,
   *             <code>false</code> otherwise.
   * @throws FormatException
   * @throws IOException
   */
  public void writeImage(byte[] buf, IFD ifd, int no, int pixelType, int x,
      int y, int w, int h, boolean last)
  throws FormatException, IOException
  {
    writeImage(buf, ifd, no, pixelType, x, y, w, h, last, null, false);
  }

  public void writeImage(byte[] buf, IFD ifd, int no, int pixelType, int x,
      int y, int w, int h, boolean last, Integer nChannels,
      boolean copyDirectly)
  throws FormatException, IOException
  {
    LOGGER.debug("Attempting to write image.");
    //b/c method is public should check parameters again
    if (buf == null) {
      throw new FormatException("Image data cannot be null");
    }

    if (ifd == null) {
      throw new FormatException("IFD cannot be null");
    }

    // These operations are synchronized
    TiffCompression compression;
    int tileWidth, tileHeight, nStrips;
    boolean interleaved;
    ByteArrayOutputStream[] stripBuf;
    synchronized (this) {
      int bytesPerPixel = FormatTools.getBytesPerPixel(pixelType);
      int blockSize = w * h * bytesPerPixel;
      if (nChannels == null) {
        nChannels = buf.length / (w * h * bytesPerPixel);
      }
      interleaved = ifd.getPlanarConfiguration() == 1;

      makeValidIFD(ifd, pixelType, nChannels);

      // create pixel output buffers

      compression = ifd.getCompression();
      tileWidth = (int) ifd.getTileWidth();
      tileHeight = (int) ifd.getTileLength();
      int tilesPerRow = (int) ifd.getTilesPerRow();
      int rowsPerStrip = (int) ifd.getRowsPerStrip()[0];
      int stripSize = rowsPerStrip * tileWidth * bytesPerPixel;
      nStrips =
        ((w + tileWidth - 1) / tileWidth) * ((h + tileHeight - 1) / tileHeight);

      if (interleaved) stripSize *= nChannels;
      else nStrips *= nChannels;

      stripBuf = new ByteArrayOutputStream[nStrips];
      DataOutputStream[] stripOut = new DataOutputStream[nStrips];
      for (int strip=0; strip<nStrips; strip++) {
        stripBuf[strip] = new ByteArrayOutputStream(stripSize);
        stripOut[strip] = new DataOutputStream(stripBuf[strip]);
      }
      int[] bps = ifd.getBitsPerSample();
      int off;

      // write pixel strips to output buffers
      int effectiveStrips = !interleaved ? nStrips / nChannels : nStrips;
      if (effectiveStrips == 1 && copyDirectly) {
        stripOut[0].write(buf);
      }
      else {
        for (int strip = 0; strip < effectiveStrips; strip++) {
          int xOffset = (strip % tilesPerRow) * tileWidth;
          int yOffset = (strip / tilesPerRow) * tileHeight;
          for (int row=0; row<tileHeight; row++) {
            for (int col=0; col<tileWidth; col++) {
              int ndx = ((row+yOffset) * w + col + xOffset) * bytesPerPixel;
              for (int c=0; c<nChannels; c++) {
                for (int n=0; n<bps[c]/8; n++) {
                  if (interleaved) {
                    off = ndx * nChannels + c * bytesPerPixel + n;
                    if (row >= h || col >= w) {
                      stripOut[strip].writeByte(0);
                    } else if (off < buf.length) {
                      stripOut[strip].writeByte(buf[off]);
                    }
                    else {
                      stripOut[strip].writeByte(0);
                    }
                  }
                  else {
                    off = c * blockSize + ndx + n;
                    int realStrip = (c * (nStrips / nChannels)) + strip;
                    if (row >= h || col >= w) {
                      stripOut[realStrip].writeByte(0);
                    } else if (off < buf.length) {
                      stripOut[realStrip].writeByte(buf[off]);
                    }
                    else {
                      stripOut[realStrip].writeByte(0);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // Compress strips according to given differencing and compression schemes,
    // this operation is NOT synchronized and is the ONLY portion of the
    // TiffWriter.saveBytes() --> TiffSaver.writeImage() stack that is NOT
    // synchronized.
    byte[][] strips = new byte[nStrips][];
    for (int strip=0; strip<nStrips; strip++) {
      strips[strip] = stripBuf[strip].toByteArray();
      TiffCompression.difference(strips[strip], ifd);
      CodecOptions codecOptions = compression.getCompressionCodecOptions(
          ifd, options);
      codecOptions.height = tileHeight;
      codecOptions.width = tileWidth;
      codecOptions.channels = interleaved ? nChannels : 1;

      strips[strip] = compression.compress(strips[strip], codecOptions);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(String.format("Compressed strip %d/%d length %d",
            strip + 1, nStrips, strips[strip].length));
      }
    }

    // This operation is synchronized
    synchronized (this) {
      writeImageIFD(ifd, no, strips, nChannels, last, x ,y);
    }
  }

  /**
   * Performs the actual work of dealing with IFD data and writing it to the
   * TIFF for a given image or sub-image.
   * @param ifd The Image File Directories. Mustn't be <code>null</code>.
   * @param no The image index within the current file, starting from 0.
   * @param strips The strips to write to the file.
   * @param last Pass <code>true</code> if it is the last image,
   * <code>false</code> otherwise.
   * @param x The initial X offset of the strips/tiles to write.
   * @param y The initial Y offset of the strips/tiles to write.
   * @throws FormatException
   * @throws IOException
   */
  private void writeImageIFD(IFD ifd, int no, byte[][] strips,
      int nChannels, boolean last, int x, int y)
  throws FormatException, IOException {
    LOGGER.debug("Attempting to write image IFD.");
    boolean isTiled = ifd.isTiled();
    long defaultByteCount = 0L;

    RandomAccessInputStream in = null;
    try {
      if (!sequentialWrite) {   
        if (filename != null) {
          in = new RandomAccessInputStream(filename);
        }
        else if (bytes != null) {
          in = new RandomAccessInputStream(bytes);
        }
        else {
          throw new IllegalArgumentException(
              "Filename and bytes are null, cannot create new input stream!");
        }
        TiffParser parser = new TiffParser(in);
        long[] ifdOffsets = parser.getIFDOffsets();
        LOGGER.debug("IFD offsets: {}", Arrays.toString(ifdOffsets));
        if (no < ifdOffsets.length) {
          out.seek(ifdOffsets[no]);
          LOGGER.debug("Reading IFD from {} in non-sequential write.",
              ifdOffsets[no]);
          ifd = parser.getIFD(ifdOffsets[no]);
        }
      }
      else if (isTiled) {
        defaultByteCount = strips[0].length;
      }
      writeIFDStrips(ifd, no, strips, nChannels, last, x, y, defaultByteCount);
    }
    finally {
      if (in != null) {
        in.close();
      }
    }
  }

  public void writeIFD(IFD ifd, long nextOffset)
    throws FormatException, IOException
  {
    TreeSet<Integer> keys = new TreeSet<Integer>(ifd.keySet());
    int keyCount = keys.size();

    if (ifd.containsKey(new Integer(IFD.LITTLE_ENDIAN))) keyCount--;
    if (ifd.containsKey(new Integer(IFD.BIG_TIFF))) keyCount--;
    if (ifd.containsKey(new Integer(IFD.REUSE))) keyCount--;

    long fp = out.getFilePointer();
    int bytesPerEntry = bigTiff ? TiffConstants.BIG_TIFF_BYTES_PER_ENTRY :
      TiffConstants.BYTES_PER_ENTRY;
    int ifdBytes = (bigTiff ? 16 : 6) + bytesPerEntry * keyCount;

    if (bigTiff) out.writeLong(keyCount);
    else out.writeShort(keyCount);

    ByteArrayHandle extra = new ByteArrayHandle();
    RandomAccessOutputStream extraStream = new RandomAccessOutputStream(extra);

    for (Integer key : keys) {
      if (key.equals(IFD.LITTLE_ENDIAN) || key.equals(IFD.BIG_TIFF) ||
          key.equals(IFD.REUSE)) continue;

      Object value = ifd.get(key);
      writeIFDValue(extraStream, ifdBytes + fp, key.intValue(), value);
    }
    if (bigTiff) out.seek(out.getFilePointer());
    writeIntValue(out, nextOffset);
    out.write(extra.getBytes(), 0, (int) extra.length());
    extraStream.close();
  }

  /**
   * Writes the given IFD value to the given output object.
   * @param extraOut buffer to which "extra" IFD information should be written
   * @param offset global offset to use for IFD offset values
   * @param tag IFD tag to write
   * @param value IFD value to write
   */
  public void writeIFDValue(RandomAccessOutputStream extraOut, long offset,
    int tag, Object value)
    throws FormatException, IOException
  {
    extraOut.order(isLittleEndian());

    // convert singleton objects into arrays, for simplicity
    if (value instanceof Short) {
      value = new short[] {((Short) value).shortValue()};
    }
    else if (value instanceof Integer) {
      value = new int[] {((Integer) value).intValue()};
    }
    else if (value instanceof Long) {
      value = new long[] {((Long) value).longValue()};
    }
    else if (value instanceof TiffRational) {
      value = new TiffRational[] {(TiffRational) value};
    }
    else if (value instanceof Float) {
      value = new float[] {((Float) value).floatValue()};
    }
    else if (value instanceof Double) {
      value = new double[] {((Double) value).doubleValue()};
    }

    int dataLength = bigTiff ? 8 : 4;

    // write directory entry to output buffers
    out.writeShort(tag); // tag
    if (value instanceof short[]) {
      short[] q = (short[]) value;
      out.writeShort(IFDType.BYTE.getCode());
      writeIntValue(out, q.length);
      if (q.length <= dataLength) {
        for (int i=0; i<q.length; i++) out.writeByte(q[i]);
        for (int i=q.length; i<dataLength; i++) out.writeByte(0);
      }
      else {
        writeIntValue(out, offset + extraOut.length());
        for (int i=0; i<q.length; i++) extraOut.writeByte(q[i]);
      }
    }
    else if (value instanceof String) { // ASCII
      byte[] q = ((String) value).getBytes(Charset.forName(Constants.ENCODING));
      out.writeShort(IFDType.ASCII.getCode()); // type
      writeIntValue(out, q.length + 1);
      if (q.length < dataLength) {
        for (int i=0; i<q.length; i++) out.writeByte(q[i]); // value(s)
        for (int i=q.length; i<dataLength; i++) out.writeByte(0); // padding
      }
      else {
        writeIntValue(out, offset + extraOut.length());
        for (int i=0; i<q.length; i++) extraOut.writeByte(q[i]); // values
        extraOut.writeByte(0); // concluding NULL byte
      }
    }
    else if (value instanceof int[]) { // SHORT
      int[] q = (int[]) value;
      out.writeShort(IFDType.SHORT.getCode()); // type
      writeIntValue(out, q.length);
      if (q.length <= dataLength / 2) {
        for (int i=0; i<q.length; i++) {
          out.writeShort(q[i]); // value(s)
        }
        for (int i=q.length; i<dataLength / 2; i++) {
          out.writeShort(0); // padding
        }
      }
      else {
        writeIntValue(out, offset + extraOut.length());
        for (int i=0; i<q.length; i++) {
          extraOut.writeShort(q[i]); // values
        }
      }
    }
    else if (value instanceof long[]) { // LONG
      long[] q = (long[]) value;

      int type = bigTiff ? IFDType.LONG8.getCode() : IFDType.LONG.getCode();
      out.writeShort(type);
      writeIntValue(out, q.length);

      int div = bigTiff ? 8 : 4;

      if (q.length <= dataLength / div) {
        for (int i=0; i<q.length; i++) {
          writeIntValue(out, q[0]);
        }
        for (int i=q.length; i<dataLength / div; i++) {
          writeIntValue(out, 0);
        }
      }
      else {
        writeIntValue(out, offset + extraOut.length());
        for (int i=0; i<q.length; i++) {
          writeIntValue(extraOut, q[i]);
        }
      }
    }
    else if (value instanceof TiffRational[]) { // RATIONAL
      TiffRational[] q = (TiffRational[]) value;
      out.writeShort(IFDType.RATIONAL.getCode()); // type
      writeIntValue(out, q.length);
      if (bigTiff && q.length == 1) {
        out.writeInt((int) q[0].getNumerator());
        out.writeInt((int) q[0].getDenominator());
      }
      else {
        writeIntValue(out, offset + extraOut.length());
        for (int i=0; i<q.length; i++) {
          extraOut.writeInt((int) q[i].getNumerator());
          extraOut.writeInt((int) q[i].getDenominator());
        }
      }
    }
    else if (value instanceof float[]) { // FLOAT
      float[] q = (float[]) value;
      out.writeShort(IFDType.FLOAT.getCode()); // type
      writeIntValue(out, q.length);
      if (q.length <= dataLength / 4) {
        for (int i=0; i<q.length; i++) {
          out.writeFloat(q[0]); // value
        }
        for (int i=q.length; i<dataLength / 4; i++) {
          out.writeInt(0); // padding
        }
      }
      else {
        writeIntValue(out, offset + extraOut.length());
        for (int i=0; i<q.length; i++) {
          extraOut.writeFloat(q[i]); // values
        }
      }
    }
    else if (value instanceof double[]) { // DOUBLE
      double[] q = (double[]) value;
      out.writeShort(IFDType.DOUBLE.getCode()); // type
      writeIntValue(out, q.length);
      writeIntValue(out, offset + extraOut.length());
      for (int i=0; i<q.length; i++) {
        extraOut.writeDouble(q[i]); // values
      }
    }
    else {
      throw new FormatException("Unknown IFD value type (" +
        value.getClass().getName() + "): " + value);
    }
  }

  public void overwriteLastIFDOffset(RandomAccessInputStream raf)
    throws FormatException, IOException
  {
    if (raf == null)
      throw new FormatException("Output cannot be null");
    TiffParser parser = new TiffParser(raf);
    long[] offsets = parser.getIFDOffsets();
    out.seek(raf.getFilePointer() - (bigTiff ? 8 : 4));
    writeIntValue(out, 0);
  }

  /**
   * Surgically overwrites an existing IFD value with the given one. This
   * method requires that the IFD directory entry already exist. It
   * intelligently updates the count field of the entry to match the new
   * length. If the new length is longer than the old length, it appends the
   * new data to the end of the file and updates the offset field; if not, or
   * if the old data is already at the end of the file, it overwrites the old
   * data in place.
   */
  public void overwriteIFDValue(RandomAccessInputStream raf,
    int ifd, int tag, Object value) throws FormatException, IOException
  {
    if (raf == null)
      throw new FormatException("Output cannot be null");
    LOGGER.debug("overwriteIFDValue (ifd={}; tag={}; value={})",
      new Object[] {ifd, tag, value});

    raf.seek(0);
    TiffParser parser = new TiffParser(raf);
    Boolean valid = parser.checkHeader();
    if (valid == null) {
      throw new FormatException("Invalid TIFF header");
    }

    boolean little = valid.booleanValue();
    boolean bigTiff = parser.isBigTiff();

    setLittleEndian(little);
    setBigTiff(bigTiff);

    long offset = bigTiff ? 8 : 4; // offset to the IFD

    int bytesPerEntry = bigTiff ?
      TiffConstants.BIG_TIFF_BYTES_PER_ENTRY : TiffConstants.BYTES_PER_ENTRY;

    raf.seek(offset);

    // skip to the correct IFD
    long[] offsets = parser.getIFDOffsets();
    if (ifd >= offsets.length) {
      throw new FormatException(
        "No such IFD (" + ifd + " of " + offsets.length + ")");
    }
    raf.seek(offsets[ifd]);

    // get the number of directory entries
    long num = bigTiff ? raf.readLong() : raf.readUnsignedShort();

    // search directory entries for proper tag
    for (int i=0; i<num; i++) {
      raf.seek(offsets[ifd] + (bigTiff ? 8 : 2) + bytesPerEntry * i);

      TiffIFDEntry entry = parser.readTiffIFDEntry();
      if (entry.getTag() == tag) {
        // write new value to buffers
        ByteArrayHandle ifdBuf = new ByteArrayHandle(bytesPerEntry);
        RandomAccessOutputStream ifdOut = new RandomAccessOutputStream(ifdBuf);
        ByteArrayHandle extraBuf = new ByteArrayHandle();
        RandomAccessOutputStream extraOut =
          new RandomAccessOutputStream(extraBuf);
        extraOut.order(little);
        TiffSaver saver = new TiffSaver(ifdOut, ifdBuf);
        saver.setLittleEndian(isLittleEndian());
        saver.writeIFDValue(extraOut, entry.getValueOffset(), tag, value);
        ifdOut.close();
        saver.close();
        extraOut.close();

        ifdBuf.seek(0);
        extraBuf.seek(0);

        // extract new directory entry parameters
        int newTag = ifdBuf.readShort();
        int newType = ifdBuf.readShort();
        int newCount;
        long newOffset;
        if (bigTiff) {
          newCount = ifdBuf.readInt();
          newOffset = ifdBuf.readLong();
        }
        else {
          newCount = ifdBuf.readInt();
          newOffset = ifdBuf.readInt();
        }
        LOGGER.debug("overwriteIFDValue:");
        LOGGER.debug("\told ({});", entry);
        LOGGER.debug("\tnew: (tag={}; type={}; count={}; offset={})",
          new Object[] {newTag, newType, newCount, newOffset});

        // determine the best way to overwrite the old entry
        if (extraBuf.length() == 0) {
          // new entry is inline; if old entry wasn't, old data is orphaned
          // do not override new offset value since data is inline
          LOGGER.debug("overwriteIFDValue: new entry is inline");
        }
        else if (entry.getValueOffset() + entry.getValueCount() *
          entry.getType().getBytesPerElement() == raf.length())
        {
          // old entry was already at EOF; overwrite it
          newOffset = entry.getValueOffset();
          LOGGER.debug("overwriteIFDValue: old entry is at EOF");
        }
        else if (newCount <= entry.getValueCount()) {
          // new entry is as small or smaller than old entry; overwrite it
          newOffset = entry.getValueOffset();
          LOGGER.debug("overwriteIFDValue: new entry is <= old entry");
        }
        else {
          // old entry was elsewhere; append to EOF, orphaning old entry
          newOffset = raf.length();
          LOGGER.debug("overwriteIFDValue: old entry will be orphaned");
        }

        // overwrite old entry
        out.seek(offsets[ifd] + (bigTiff ? 8 : 2) + bytesPerEntry * i + 2);
        out.writeShort(newType);
        writeIntValue(out, newCount);
        writeIntValue(out, newOffset);
        if (extraBuf.length() > 0) {
          out.seek(newOffset);
          out.write(extraBuf.getByteBuffer(), 0, newCount);
        }
        return;
      }
    }

    throw new FormatException("Tag not found (" + IFD.getIFDTagName(tag) + ")");
  }

  /** Convenience method for overwriting a file's first ImageDescription. */
  public void overwriteComment(RandomAccessInputStream in, Object value)
    throws FormatException, IOException
  {
    overwriteIFDValue(in, 0, IFD.IMAGE_DESCRIPTION, value);
  }

  // -- Helper methods --

  /**
   * Coverts a list to a primitive array.
   * @param l The list of <code>Long</code> to convert.
   * @return A primitive array of type <code>long[]</code> with the values from
   * </code>l</code>.
   */
  private long[] toPrimitiveArray(List<Long> l) {
    long[] toReturn = new long[l.size()];
    for (int i = 0; i < l.size(); i++) {
      toReturn[i] = l.get(i);
    }
    return toReturn;
  }

  /**
   * Write the given value to the given RandomAccessOutputStream.
   * If the 'bigTiff' flag is set, then the value will be written as an 8 byte
   * long; otherwise, it will be written as a 4 byte integer.
   */
  private void writeIntValue(RandomAccessOutputStream out, long offset)
    throws IOException
  {
    if (bigTiff) {
      out.writeLong(offset);
    }
    else {
      out.writeInt((int) offset);
    }
  }

  /**
   * Makes a valid IFD.
   *
   * @param ifd The IFD to handle.
   * @param pixelType The pixel type.
   * @param nChannels The number of channels.
   */
  private void makeValidIFD(IFD ifd, int pixelType, int nChannels) {
    int bytesPerPixel = FormatTools.getBytesPerPixel(pixelType);
    int bps = 8 * bytesPerPixel;
    int[] bpsArray = new int[nChannels];
    Arrays.fill(bpsArray, bps);
    ifd.putIFDValue(IFD.BITS_PER_SAMPLE, bpsArray);

    if (FormatTools.isFloatingPoint(pixelType)) {
      ifd.putIFDValue(IFD.SAMPLE_FORMAT, 3);
    }
    if (ifd.getIFDValue(IFD.COMPRESSION) == null) {
      ifd.putIFDValue(IFD.COMPRESSION, TiffCompression.UNCOMPRESSED.getCode());
    }

    boolean indexed = nChannels == 1 && ifd.getIFDValue(IFD.COLOR_MAP) != null;
    PhotoInterp pi = indexed ? PhotoInterp.RGB_PALETTE :
      nChannels == 1 ? PhotoInterp.BLACK_IS_ZERO : PhotoInterp.RGB;
    ifd.putIFDValue(IFD.PHOTOMETRIC_INTERPRETATION, pi.getCode());

    ifd.putIFDValue(IFD.SAMPLES_PER_PIXEL, nChannels);

    if (ifd.get(IFD.X_RESOLUTION) == null) {
      ifd.putIFDValue(IFD.X_RESOLUTION, new TiffRational(1, 1));
    }
    if (ifd.get(IFD.Y_RESOLUTION) == null) {
      ifd.putIFDValue(IFD.Y_RESOLUTION, new TiffRational(1, 1));
    }
    if (ifd.get(IFD.SOFTWARE) == null) {
      ifd.putIFDValue(IFD.SOFTWARE, FormatTools.CREATOR);
    }
    if (ifd.get(IFD.ROWS_PER_STRIP) == null &&
      ifd.get(IFD.TILE_WIDTH) == null && ifd.get(IFD.TILE_LENGTH) == null)
    {
      ifd.putIFDValue(IFD.ROWS_PER_STRIP, new long[] {1});
    }
    if (ifd.get(IFD.IMAGE_DESCRIPTION) == null) {
      ifd.putIFDValue(IFD.IMAGE_DESCRIPTION, "");
    }
  }

  private void writeIFDStrips(IFD ifd, int no, byte[][] strips,
      int nChannels, boolean last, int x, int y, long defaultByteCount) throws FormatException, IOException {
    int tilesPerRow = (int) ifd.getTilesPerRow();
    int tilesPerColumn = (int) ifd.getTilesPerColumn();
    boolean interleaved = ifd.getPlanarConfiguration() == 1;
    boolean isTiled = ifd.isTiled();

    // record strip byte counts and offsets
    List<Long> byteCounts = new ArrayList<Long>();
    List<Long> offsets = new ArrayList<Long>();
    long totalTiles = tilesPerRow * tilesPerColumn;

    if (!interleaved) {
      totalTiles *= nChannels;
    }

    if (ifd.containsKey(IFD.STRIP_BYTE_COUNTS) ||
      ifd.containsKey(IFD.TILE_BYTE_COUNTS))
    {
      long[] ifdByteCounts = isTiled ?
        ifd.getIFDLongArray(IFD.TILE_BYTE_COUNTS) : ifd.getStripByteCounts();
      for (long stripByteCount : ifdByteCounts) {
        byteCounts.add(stripByteCount);
      }
    }
    else {
      while (byteCounts.size() < totalTiles) {
        byteCounts.add(defaultByteCount);
      }
    }
    int tileOrStripOffsetX = x / (int) ifd.getTileWidth();
    int tileOrStripOffsetY = y / (int) ifd.getTileLength();
    int firstOffset = (tileOrStripOffsetY * tilesPerRow) + tileOrStripOffsetX;
    if (ifd.containsKey(IFD.STRIP_OFFSETS)
        || ifd.containsKey(IFD.TILE_OFFSETS)) {
      long[] ifdOffsets = isTiled ?
        ifd.getIFDLongArray(IFD.TILE_OFFSETS) : ifd.getStripOffsets();
      for (int i = 0; i < ifdOffsets.length; i++) {
        offsets.add(ifdOffsets[i]);
      }
    }
    else {
      while (offsets.size() < totalTiles) {
        offsets.add(0L);
      }
      if (isTiled && tileOrStripOffsetX == 0 && tileOrStripOffsetY == 0) {
        sequentialTileOffsets = offsets;
      }
      else if (isTiled) {
        offsets = sequentialTileOffsets;
      }
    }

    if (isTiled) {
      ifd.putIFDValue(IFD.TILE_BYTE_COUNTS, toPrimitiveArray(byteCounts));
      ifd.putIFDValue(IFD.TILE_OFFSETS, toPrimitiveArray(offsets));
    }
    else {
      ifd.putIFDValue(IFD.STRIP_BYTE_COUNTS, toPrimitiveArray(byteCounts));
      ifd.putIFDValue(IFD.STRIP_OFFSETS, toPrimitiveArray(offsets));
    }

    long fp = out.getFilePointer();
    if (isTiled && tileOrStripOffsetX == 0 && tileOrStripOffsetY == 0) {
      sequentialTileFilePointer = fp;
    }
    else if (isTiled) {
      fp = sequentialTileFilePointer;
    }
    writeIFD(ifd, 0);

    // strips.length is the total number of strips being written during
    // this method call, which is no more than the total number of
    // strips in the image
    //
    // for single-channel or interleaved image data, the strips are written
    // in order
    // for multi-channel non-interleaved image data, the strip indexing has
    // to correct for the fact that each strip represents a single channel
    //
    // for example, in a 3 channel non-interleaved image with 2 calls to
    // writeImageIFD each of which writes half of the image:
    //  - we expect 6 strips to be written in total; the first call writes
    //    {0, 2, 4}, the second writes {1, 3, 5}
    //  - in each call to writeImageIFD:
    //      * strips.length is 3
    //      * interleaved is false
    //      * nChannels is 3
    //      * tileCount is 2

    int tileCount = isTiled ? tilesPerRow * tilesPerColumn : 1;
    for (int i=0; i<strips.length; i++) {
      out.seek(out.length());
      int index = interleaved ? i : (i / nChannels) * nChannels;
      int c = interleaved ? 0 : i % nChannels;
      int thisOffset = firstOffset + index + (c * tileCount);
      offsets.set(thisOffset, out.getFilePointer());
      byteCounts.set(thisOffset, new Long(strips[i].length));
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(String.format(
            "Writing tile/strip %d/%d size: %d offset: %d",
            thisOffset + 1, totalTiles, byteCounts.get(thisOffset),
            offsets.get(thisOffset)));
      }
      out.write(strips[i]);
    }
    if (isTiled) {
      ifd.putIFDValue(IFD.TILE_BYTE_COUNTS, toPrimitiveArray(byteCounts));
      ifd.putIFDValue(IFD.TILE_OFFSETS, toPrimitiveArray(offsets));
    }
    else {
      ifd.putIFDValue(IFD.STRIP_BYTE_COUNTS, toPrimitiveArray(byteCounts));
      ifd.putIFDValue(IFD.STRIP_OFFSETS, toPrimitiveArray(offsets));
    }
    long endFP = out.getFilePointer();
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Offset before IFD write: {} Seeking to: {}",
          out.getFilePointer(), fp);
    }
    out.seek(fp);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Writing tile/strip offsets: {}",
          Arrays.toString(toPrimitiveArray(offsets)));
      LOGGER.debug("Writing tile/strip byte counts: {}",
          Arrays.toString(toPrimitiveArray(byteCounts)));
    }
    writeIFD(ifd, last ? 0 : endFP);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Offset after IFD write: {}", out.getFilePointer());
    }
  }
}
