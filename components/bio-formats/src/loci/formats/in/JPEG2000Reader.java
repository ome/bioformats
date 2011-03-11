//
// JPEG2000Reader.java
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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEG2000BoxType;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.codec.JPEG2000SegmentMarker;
import loci.formats.meta.MetadataStore;

/**
 * JPEG2000Reader is the file format reader for JPEG-2000 images.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/JPEG2000Reader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/JPEG2000Reader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class JPEG2000Reader extends FormatReader {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(JPEG2000Reader.class);

  // -- Fields --

  /** The number of JPEG 2000 resolution levels the file has. */
  private Integer resolutionLevels;

  // -- Constructor --

  /** Constructs a new JPEG2000Reader. */
  public JPEG2000Reader() {
    super("JPEG-2000", new String[] {"jp2", "j2k"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    boolean validStart = (stream.readShort() & 0xffff) == 0xff4f;
    if (!validStart) {
      stream.skipBytes(2);
      validStart = stream.readInt() == JPEG2000BoxType.SIGNATURE.getCode();
    }
    stream.seek(stream.length() - 2);
    boolean validEnd = (stream.readShort() & 0xffff) == 0xffd9;
    return validStart && validEnd;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    CodecOptions options = new CodecOptions();
    options.interleaved = isInterleaved();
    options.littleEndian = isLittleEndian();

    in.seek(0);
    byte[] plane = new JPEG2000Codec().decompress(in, options);
    RandomAccessInputStream s = new RandomAccessInputStream(plane);
    readPlane(s, x, y, w, h, buf);
    s.close();
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    in = new RandomAccessInputStream(id);

    parseBoxes();

    core[0].sizeZ = 1;
    core[0].sizeT = 1;
    core[0].imageCount = 1;
    core[0].dimensionOrder = "XYCZT";
    core[0].rgb = getSizeC() > 1;
    core[0].interleaved = true;
    core[0].littleEndian = false;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);
    MetadataTools.setDefaultCreationDate(store, currentId, 0);
  }

  // -- Helper methods --

  /**
   * Parses the JPEG 2000 JP2 metadata boxes.
   * @throws IOException Thrown if there is an error reading from the file.
   */
  private void parseBoxes() throws IOException {
    long pos = in.getFilePointer(), nextPos = 0;
    LOGGER.info("Parsing JPEG 2000 boxes at {}", pos);
    int length = 0, boxCode;
    JPEG2000BoxType boxType;

    while (pos < in.length()) {
      pos = in.getFilePointer();
      length = in.readInt();
      nextPos = pos + length;
      boxCode = in.readInt();
      boxType = JPEG2000BoxType.get(boxCode);
      length -= 8;
      if (boxType == null) {
        LOGGER.warn("Unknown JPEG 2000 box {} at {}", boxCode, pos);
        if (pos == 0) {
          in.seek(0);
          if (JPEG2000SegmentMarker.get(in.readUnsignedShort()) != null) {
            LOGGER.info("File is a raw codestream not a JP2.");
            in.seek(0);
            parseContiguousCodestream(in.length(), true);
          }
        }
      }
      else {
        LOGGER.debug("Found JPEG 2000 '{}' box at {}", boxType.getName(), pos);
        switch (boxType) {
          case CONTIGUOUS_CODESTREAM: {
            try {
              parseContiguousCodestream(length, false);
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
              core[0].sizeY = in.readInt();
              core[0].sizeX = in.readInt();
              core[0].sizeC = in.readShort();
              int type = in.readInt();
              core[0].pixelType = convertPixelType(type);
            }
            parseBoxes();
            break;
          }
          default: {
            if ((length + 8) == 0xff4fff51) {
              core[0].sizeX = in.readInt();
              core[0].sizeY = in.readInt();
              in.skipBytes(24);
              core[0].sizeC = in.readShort();
              int type = in.readInt();
              core[0].pixelType = convertPixelType(type);
            }
          }
        }
      }
      // Exit or seek to the next metadata box
      if (nextPos < 0 || nextPos >= in.length() || length == 0) {
        LOGGER.debug("Exiting box parser loop.");
        break;
      }
      LOGGER.debug("Seeking to next box at {}", nextPos);
      in.seek(nextPos);
    }
  }

  /**
   * Parses the JPEG 2000 codestream metadata.
   * @param length Total length of the codestream block.
   * @param overrideSize Whether or not to override existing dimensions set
   * potentially by JP2 metadata boxes.
   * @throws IOException Thrown if there is an error reading from the file.
   */
  private void parseContiguousCodestream(long length, boolean overrideSize)
    throws IOException {
    JPEG2000SegmentMarker segmentMarker;
    int segmentMarkerCode = 0, segmentLength = 0;
    long pos = in.getFilePointer(), nextPos = 0;
    LOGGER.info("Parsing JPEG 2000 contiguous codestream at {}", pos);
    boolean terminate = false;
    while (pos < length && !terminate) {
      segmentMarkerCode = in.readUnsignedShort();
      segmentMarker = JPEG2000SegmentMarker.get(segmentMarkerCode);
      pos = in.getFilePointer();
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
      nextPos = pos + segmentLength;
      if (segmentMarker == null) {
        LOGGER.warn("Unknown JPEG 2000 segment marker {} at {}",
            segmentMarkerCode, pos);
      }
      else {
        LOGGER.debug("Found JPEG 2000 segment marker '{}' at {}",
            segmentMarker.getName(), pos);
        switch (segmentMarker) {
          case EOC: {
            terminate = true;
            break;
          }
          case SIZ: {
            if (!overrideSize) {
              break;
            }
            // Skipping:
            //  * Capability (uint16)
            in.skipBytes(2);
            core[0].sizeX = in.readInt();
            LOGGER.debug("Read reference grid width {} at {}", core[0].sizeX,
                in.getFilePointer());
            core[0].sizeY = in.readInt();
            LOGGER.debug("Read reference grid height {} at {}", core[0].sizeY,
                in.getFilePointer());
            // Skipping:
            //  * Horizontal image offset (uint32)
            //  * Vertical image offset (uint32)
            //  * Tile width (uint32)
            //  * Tile height (uint32)
            //  * Horizontal tile offset (uint32)
            //  * Vertical tile offset (uint32)
            in.skipBytes(24);
            core[0].sizeC = in.readShort();
            LOGGER.debug("Read total components {} at {}",
                core[0].sizeC, in.getFilePointer());
            int type = in.readInt();
            core[0].pixelType = convertPixelType(type);
            LOGGER.debug("Read codestream pixel type {} at {}",
                core[0].pixelType, in.getFilePointer());
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
            LOGGER.debug("Found number of resolution levels {} at {} ", 
                resolutionLevels, in.getFilePointer());
            terminate = true;
            break;
          }
        }
      }
      // Exit or seek to the next metadata box
      if (nextPos < 0 || nextPos >= length || terminate) {
        LOGGER.debug("Exiting segment marker parse loop.");
        break;
      }
      LOGGER.debug("Seeking to next segment marker at {}", nextPos);
      in.seek(nextPos);
    }
  }

  private int convertPixelType(int type) {
    if (type == 0xf070100 || type == 0xf070000) {
      return FormatTools.UINT16;
    }
    return FormatTools.UINT8;
  }

}
