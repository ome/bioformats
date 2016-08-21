/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

package loci.formats.in;

import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatTools;
import loci.formats.codec.JPEG2000BoxType;
import loci.formats.codec.JPEG2000SegmentMarker;

/**
 * A parser for JPEG 2000 metadata.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/JPEG2000MetadataParser.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/JPEG2000MetadataParser.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class JPEG2000MetadataParser {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(JPEG2000MetadataParser.class);

  // -- Fields --

  /** Stream that we're parsing metadata from. */
  private RandomAccessInputStream in;

  /** Offset to first contiguous codestream. */
  private long codestreamOffset;

  /** Maximum read offset within in the stream. */
  private long maximumReadOffset;

  /** Width of the image as specified in the header. */
  private Integer headerSizeX;

  /** Height of the image as specified in the header. */
  private Integer headerSizeY;

  /** Number of channels the image has as specified in the header. */
  private Short headerSizeC;

  /** Pixel type as specified in the header. */
  private Integer headerPixelType;

  /** Width of the image as specified in the JPEG 2000 codestream. */
  private Integer codestreamSizeX;

  /** Height of the image as specified in the JPEG 2000 codestream. */
  private Integer codestreamSizeY;

  /** Number of channels the image as specified in the JPEG 2000 codestream. */
  private Short codestreamSizeC;

  /** Pixel type as specified in the JPEG 2000 codestream.. */
  private Integer codestreamPixelType;

  /** Whether or not the codestream is raw and not JP2 boxed. */
  private boolean isRawCodestream = false;

  /** Number of JPEG 2000 resolution levels the file has. */
  private Integer resolutionLevels;

  /** Color lookup table stored in the file. */
  private int[][] lut;

  /** List of comments stored in the file.*/
  private ArrayList<String> comments;

  // -- Constructor --

  /**
   * Constructs a new JPEG2000MetadataParser.
   * @param in Stream to parse JPEG 2000 metadata from.
   * @throws IOException Thrown if there is an error reading from the file.
   */
  public JPEG2000MetadataParser(RandomAccessInputStream in)
    throws IOException {
    this.in = in;
    this.maximumReadOffset = in.length();
    comments = new ArrayList<String>();
    parseBoxes();
  }

  /**
   * Constructs a new JPEG2000MetadataParser.
   * @param in Stream to parse JPEG 2000 metadata from.
   * @param maximumReadOffset Maximum read offset within the stream when
   * parsing.
   * @throws IOException Thrown if there is an error reading from the file.
   */
  public JPEG2000MetadataParser(RandomAccessInputStream in,
                                long maximumReadOffset)
    throws IOException {
    this.in = in;
    this.maximumReadOffset = maximumReadOffset;
    comments = new ArrayList<String>();
    boolean isLittleEndian = in.isLittleEndian();
    try {
      // Parse boxes may need to change the endianness of the input stream so
      // we're going to reset it when we're done.
      parseBoxes();
    }
    finally {
      in.order(isLittleEndian);
    }
  }

  /** Retrieves the offset to the first contiguous codestream. */
  public long getCodestreamOffset() {
    return codestreamOffset;
  }

  /** Retrieves the list of comments stored in the file. */
  public ArrayList<String> getComments() {
    return comments;
  }

  /**
   * Parses the JPEG 2000 JP2 metadata boxes.
   * @throws IOException Thrown if there is an error reading from the file.
   */
  private void parseBoxes() throws IOException {
    long originalPos = in.getFilePointer(), nextPos = 0;
    long pos = originalPos;
    LOGGER.trace("Parsing JPEG 2000 boxes at {}", pos);
    int length = 0, boxCode;
    JPEG2000BoxType boxType;

    while (pos < maximumReadOffset) {
      pos = in.getFilePointer();
      length = in.readInt();
      boxCode = in.readInt();
      boxType = JPEG2000BoxType.get(boxCode);
      if (boxType == JPEG2000BoxType.SIGNATURE_WRONG_ENDIANNESS) {
        LOGGER.trace("Swapping endianness during box parsing.");
        in.order(!in.isLittleEndian());
        length = DataTools.swap(length);
      }
      nextPos = pos + length;
      if (length >= 8) {
        length -= 8;
      }
      if (boxType == null) {
        LOGGER.warn("Unknown JPEG 2000 box 0x{} at {}",
            Integer.toHexString(boxCode), pos);
        if (pos == originalPos) {
          in.seek(originalPos);
          if (JPEG2000SegmentMarker.get(in.readUnsignedShort()) != null) {
            LOGGER.info("File is a raw codestream not a JP2.");
            isRawCodestream = true;
            in.seek(originalPos);
            parseContiguousCodestream(in.length());
          }
        }
      }
      else {
        LOGGER.trace("Found JPEG 2000 '{}' box at {}", boxType.getName(), pos);
        switch (boxType) {
          case CONTIGUOUS_CODESTREAM: {
            try {
              parseContiguousCodestream(length == 0 ? in.length() : length);
            }
            catch (Exception e) {
              LOGGER.warn("Could not parse contiguous codestream.", e);
            }
            break;
          }
          case HEADER: {
            in.skipBytes(4);
            String s = in.readString(4);
            if (s.equals("ihdr")) {
              headerSizeY = in.readInt();
              headerSizeX = in.readInt();
              headerSizeC = in.readShort();
              int type = in.read();
              in.skipBytes(3);
              headerPixelType = convertPixelType(type);
            }
            parseBoxes();
            break;
          }
          case PALETTE:
            int nEntries = in.readShort();
            int nColumns = in.read();
            int[] bitDepths = new int[nColumns];
            for (int i=0; i<bitDepths.length; i++) {
              bitDepths[i] = in.read() & 0x7f;
              while ((bitDepths[i] % 8) != 0) {
                bitDepths[i]++;
              }
            }
            lut = new int[nColumns][nEntries];

            for (int i=0; i<nColumns; i++) {
              for (int j=0; j<lut[i].length; j++) {
                if (bitDepths[i] == 8) {
                  lut[i][j] = in.read();
                }
                else if (bitDepths[i] == 16) {
                  lut[i][j] = in.readShort();
                }
              }
            }

            break;
        }
      }
      // Exit or seek to the next metadata box
      if (nextPos < 0 || nextPos >= maximumReadOffset || length == 0) {
        LOGGER.trace("Exiting box parser loop.");
        break;
      }
      LOGGER.trace("Seeking to next box at {}", nextPos);
      in.seek(nextPos);
    }
  }

  /**
   * Parses the JPEG 2000 codestream metadata.
   * @param length Total length of the codestream block.
   * @throws IOException Thrown if there is an error reading from the file.
   */
  private void parseContiguousCodestream(long length) throws IOException {
    if (codestreamOffset == 0) {
      codestreamOffset = in.getFilePointer();
    }

    JPEG2000SegmentMarker segmentMarker;
    int segmentMarkerCode = 0, segmentLength = 0;
    long pos = in.getFilePointer(), nextPos = 0;
    LOGGER.trace("Parsing JPEG 2000 contiguous codestream of length {} at {}",
        length, pos);
    long maximumReadOffset = pos + length;
    boolean terminate = false;
    while (pos < maximumReadOffset && !terminate) {
      pos = in.getFilePointer();
      segmentMarkerCode = in.readUnsignedShort();
      segmentMarker = JPEG2000SegmentMarker.get(segmentMarkerCode);
      if (segmentMarker == JPEG2000SegmentMarker.SOC_WRONG_ENDIANNESS) {
        LOGGER.trace("Swapping endianness during segment marker parsing.");
        in.order(!in.isLittleEndian());
        segmentMarkerCode = JPEG2000SegmentMarker.SOC.getCode();
        segmentMarker = JPEG2000SegmentMarker.SOC;
      }
      if (segmentMarker == JPEG2000SegmentMarker.SOC
          || segmentMarker == JPEG2000SegmentMarker.SOD
          || segmentMarker == JPEG2000SegmentMarker.EPH
          || segmentMarker == JPEG2000SegmentMarker.EOC
          || (segmentMarkerCode >= JPEG2000SegmentMarker.RESERVED_DELIMITER_MARKER_MIN.getCode()
              && segmentMarkerCode <= JPEG2000SegmentMarker.RESERVED_DELIMITER_MARKER_MAX.getCode())) {
        // Delimiter marker; no segment.
        segmentLength = 0;
      }
      else {
        segmentLength = in.readUnsignedShort();
      }
      nextPos = pos + segmentLength + 2;
      if (segmentMarker == null) {
        LOGGER.warn("Unknown JPEG 2000 segment marker 0x{} at {}",
            Integer.toHexString(segmentMarkerCode), pos);
      }
      else {
        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace(String.format(
              "Found JPEG 2000 segment marker '%s' of length %d at %d",
              segmentMarker.getName(), segmentLength, pos));
        }
        switch (segmentMarker) {
          case SOT:
          case SOD:
          case EOC:
            terminate = true;
            break;
          case SIZ: {
            // Skipping:
            //  * Capability (uint16)
            in.skipBytes(2);
            codestreamSizeX = in.readInt();
            LOGGER.trace("Read reference grid width {} at {}", codestreamSizeX,
                in.getFilePointer());
            codestreamSizeY = in.readInt();
            LOGGER.trace("Read reference grid height {} at {}", codestreamSizeY,
                in.getFilePointer());
            // Skipping:
            //  * Horizontal image offset (uint32)
            //  * Vertical image offset (uint32)
            //  * Tile width (uint32)
            //  * Tile height (uint32)
            //  * Horizontal tile offset (uint32)
            //  * Vertical tile offset (uint32)
            in.skipBytes(24);
            codestreamSizeC = in.readShort();
            LOGGER.trace("Read total components {} at {}",
                codestreamSizeC, in.getFilePointer());
            int type = in.read();
            in.skipBytes(3);
            codestreamPixelType = convertPixelType(type);
            LOGGER.trace("Read codestream pixel type {} at {}",
                codestreamPixelType, in.getFilePointer());
            break;
          }
          case COD: {
            // Skipping:
            //  * Segment coding style (uint8)
            //  * Progression order (uint8)
            //  * Total quality layers (uint16)
            //  * Multiple component transform (uint8)
            in.skipBytes(5);
            resolutionLevels = in.readUnsignedByte();
            LOGGER.trace("Found number of resolution levels {} at {} ",
                resolutionLevels, in.getFilePointer());
            break;
          }
          case COM:
            in.skipBytes(2);
            String comment = in.readString(segmentLength - 4);
            comments.add(comment);
            break;
        }
      }
      // Exit or seek to the next metadata box
      if (nextPos < 0 || nextPos >= maximumReadOffset || terminate) {
        LOGGER.trace("Exiting segment marker parse loop.");
        break;
      }
      LOGGER.trace("Seeking to next segment marker at {}", nextPos);
      in.seek(nextPos);
    }
  }

  /**
   * Whether or not the codestream is raw and not JP2 boxed.
   * @return <code>true</code> if the codestream is raw and <code>false</code>
   * otherwise.
   */
  public boolean isRawCodestream() {
    return isRawCodestream;
  }

  /**
   * Returns the number of resolution levels the file JPEG 2000 data has.
   * @return The number of resolution levels or <code>null</code> if the
   * number cannot be parsed.
   */
  public Integer getResolutionLevels() {
    return resolutionLevels;
  }

  /**
   * Returns the width of the image as specified in the header.
   * @return See above.
   */
  public Integer getHeaderSizeX() {
    return headerSizeX;
  }

  /**
   * Returns the height of the image as specified in the header.
   * @return See above.
   */
  public Integer getHeaderSizeY() {
    return headerSizeY;
  }

  /**
   * Returns the number of channels the image has as specified in the header.
   * @return See above.
   */
  public Short getHeaderSizeC() {
    return headerSizeC;
  }

  /**
   * Returns the pixel type as specified in the header.
   * @return See above.
   */
  public Integer getHeaderPixelType() {
    return headerPixelType;
  }

  /**
   * Returns the width of the image as specified in the header.
   * @return See above.
   */
  public Integer getCodestreamSizeX() {
    return codestreamSizeX;
  }

  /**
   * Returns the height of the image as specified in the header.
   * @return See above.
   */
  public Integer getCodestreamSizeY() {
    return codestreamSizeY;
  }

  /**
   * Returns the number of channels the image has as specified in the header.
   * @return See above.
   */
  public Short getCodestreamSizeC() {
    return codestreamSizeC;
  }

  /**
   * Returns the pixel type as specified in the header.
   * @return See above.
   */
  public Integer getCodestreamPixelType() {
    return codestreamPixelType;
  }

  /**
   * Returns the color lookup table stored in the file, or null if a lookup
   * table is not present.
   * @return See above.
   */
  public int[][] getLookupTable() {
    return lut;
  }

  private int convertPixelType(int type) {
    int bits = (type & 0x7f) + 1;
    boolean isSigned = ((type & 0x80) >> 7) == 1;

    if (bits <= 8) {
      return isSigned ? FormatTools.INT8 : FormatTools.UINT8;
    }
    else if (bits <= 16) {
      return isSigned ? FormatTools.INT16 : FormatTools.UINT16;
    }
    else if (bits <= 32) {
      return isSigned ? FormatTools.INT32 : FormatTools.UINT32;
    }
    return FormatTools.UINT8;
  }

}
