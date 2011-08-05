//
// ZeissCZIReader.java
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
import java.util.ArrayList;

import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.LZWCodec;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;

/**
 * ZeissCZIReader is the file format reader for Zeiss .czi files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/ZeissCZIReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/ZeissCZIReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class ZeissCZIReader extends FormatReader {

  // -- Constants --

  private static final int ALIGNMENT = 32;
  private static final int HEADER_SIZE = 32;
  private static final String CZI_MAGIC_STRING = "ZISRAWFILE";

  /** Compression constants. */
  private static final int UNCOMPRESSED = 0;
  private static final int JPEG = 1;
  private static final int LZW = 2;

  /** Pixel type constants. */
  private static final int GRAY8 = 0;
  private static final int GRAY16 = 1;
  private static final int GRAY_FLOAT = 2;
  private static final int BGR_24 = 3;
  private static final int BGR_48 = 4;
  private static final int BGR_FLOAT = 8;
  private static final int BGRA_8 = 9;
  private static final int COMPLEX = 10;
  private static final int COMPLEX_FLOAT = 11;
  private static final int GRAY32 = 12;
  private static final int GRAY_DOUBLE = 13;

  // -- Fields --

  private ArrayList<SubBlock> planes;
  private int rotations = 1;
  private int positions = 1;
  private int illuminations = 1;
  private int acquisitions = 1;
  private int mosaics = 1;
  private int phases = 1;

  // -- Constructor --

  /** Constructs a new Zeiss .czi reader. */
  public ZeissCZIReader() {
    super("Zeiss CZI", "czi");
    domains = new String[] {FormatTools.LM_DOMAIN};
    suffixSufficient = true;
    suffixNecessary = false;
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream)
   */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 10;
    if (!FormatTools.validStream(stream, blockLen, true)) return false;
    String check = stream.readString(blockLen);
    return check.equals(CZI_MAGIC_STRING);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    for (SubBlock plane : planes) {
      if (plane.seriesIndex == getSeries() && plane.planeIndex == no) {
        byte[] rawData = plane.readPixelData();
        RandomAccessInputStream s = new RandomAccessInputStream(rawData);
        readPlane(s, x, y, w, h, buf);
        s.close();
        break;
      }
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      planes = null;
      rotations = 1;
      positions = 1;
      illuminations = 1;
      acquisitions = 1;
      mosaics = 1;
      phases = 1;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    core[0].littleEndian = true;
    in.order(isLittleEndian());

    ArrayList<Segment> segments = new ArrayList<Segment>();
    planes = new ArrayList<SubBlock>();

    while (in.getFilePointer() < in.length()) {
      Segment segment = readSegment();
      segments.add(segment);

      if (segment instanceof SubBlock) {
        planes.add((SubBlock) segment);
      }
    }

    calculateDimensions();

    // finish populating the core metadata

    int seriesCount = rotations * positions * illuminations * acquisitions *
      mosaics * phases;

    core[0].imageCount = planes.size() / seriesCount;
    convertPixelType(planes.get(0).directoryEntry.pixelType);

    if (seriesCount > 1) {
      CoreMetadata firstSeries = core[0];

      core = new CoreMetadata[seriesCount];

      for (int i=0; i<seriesCount; i++) {
        core[i] = firstSeries;
      }
    }

    core[0].dimensionOrder = "XYCZT";

    assignPlaneIndices();

    // populate the OME metadata

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

  // -- Helper methods --

  private void calculateDimensions() {
    // calculate the dimensions

    for (SubBlock plane : planes) {
      for (DimensionEntry dimension : plane.directoryEntry.dimensionEntries) {
        switch (dimension.dimension.charAt(0)) {
          case 'X':
            core[0].sizeX = dimension.size;
            break;
          case 'Y':
            core[0].sizeY = dimension.size;
            break;
          case 'C':
            if (dimension.start >= getSizeC()) {
              core[0].sizeC = dimension.start + 1;
            }
            break;
          case 'Z':
            if (dimension.start >= getSizeZ()) {
              core[0].sizeZ = dimension.start + 1;
            }
            break;
          case 'T':
            if (dimension.start >= getSizeT()) {
              core[0].sizeT = dimension.start + 1;
            }
            break;
          case 'R':
            if (dimension.start >= rotations) {
              rotations = dimension.start + 1;
            }
            break;
          case 'S':
            if (dimension.start >= positions) {
              positions = dimension.start + 1;
            }
            break;
          case 'I':
            if (dimension.start >= illuminations) {
              illuminations = dimension.start + 1;
            }
            break;
          case 'B':
            if (dimension.start >= acquisitions) {
              acquisitions = dimension.start + 1;
            }
            break;
          case 'M':
            if (dimension.start >= mosaics) {
              mosaics = dimension.start + 1;
            }
            break;
          case 'H':
            if (dimension.start >= phases) {
              phases = dimension.start + 1;
            }
            break;
        }
      }
    }
  }

  private void assignPlaneIndices() {
    // assign plane and series indices to each SubBlock
    int[] extraLengths =
      {rotations, positions, illuminations, acquisitions, mosaics, phases};
    for (SubBlock plane : planes) {
      int z = 0;
      int c = 0;
      int t = 0;
      int[] extra = new int[6];

      for (DimensionEntry dimension : plane.directoryEntry.dimensionEntries) {
        switch (dimension.dimension.charAt(0)) {
          case 'C':
            c = dimension.start;
            break;
          case 'Z':
            z = dimension.start;
            break;
          case 'T':
            t = dimension.start;
            break;
          case 'R':
            extra[0] = dimension.start;
            break;
          case 'S':
            extra[1] = dimension.start;
            break;
          case 'I':
            extra[2] = dimension.start;
            break;
          case 'B':
            extra[3] = dimension.start;
            break;
          case 'M':
            extra[4] = dimension.start;
            break;
          case 'H':
            extra[5] = dimension.start;
            break;
        }
      }

      plane.planeIndex = getIndex(z, c, t);
      plane.seriesIndex = FormatTools.positionToRaster(extraLengths, extra);
    }
  }

  private Segment readSegment() throws IOException {
    // align the stream to a multiple of 32 bytes
    int skip =
      (ALIGNMENT - (int) (in.getFilePointer() % ALIGNMENT)) % ALIGNMENT;
    in.skipBytes(skip);
    long startingPosition = in.getFilePointer();

    // instantiate a Segment subclass based upon the segment ID
    String segmentID = in.readString(16).trim();
    Segment segment = null;

    if (segmentID.equals("ZISRAWFILE")) {
      segment = new FileHeader();
    }
    else if (segmentID.equals("ZISRAWMETADATA")) {
      segment = new Metadata();
    }
    else if (segmentID.equals("ZISRAWSUBBLOCK")) {
      segment = new SubBlock();
    }
    else if (segmentID.equals("ZISRAWATTACH")) {
      segment = new Attachment();
    }
    else {
      LOGGER.info("Unknown segment type: " + segmentID);
      segment = new Segment();
    }
    segment.startingPosition = startingPosition;
    segment.id = segmentID;

    segment.fillInData();
    in.seek(segment.startingPosition + segment.allocatedSize + HEADER_SIZE);
    return segment;
  }

  private void convertPixelType(int pixelType) throws FormatException {
    switch (pixelType) {
      case GRAY8:
        core[0].pixelType = FormatTools.UINT8;
        break;
      case GRAY16:
        core[0].pixelType = FormatTools.UINT16;
        break;
      case GRAY32:
        core[0].pixelType = FormatTools.UINT32;
        break;
      case GRAY_FLOAT:
        core[0].pixelType = FormatTools.FLOAT;
        break;
      case GRAY_DOUBLE:
        core[0].pixelType = FormatTools.DOUBLE;
        break;
      case BGR_24:
        core[0].pixelType = FormatTools.UINT8;
        core[0].sizeC *= 3;
        core[0].rgb = true;
        break;
      case BGR_48:
        core[0].pixelType = FormatTools.UINT16;
        core[0].sizeC *= 3;
        core[0].rgb = true;
        break;
      case BGRA_8:
        core[0].pixelType = FormatTools.UINT8;
        core[0].sizeC *= 4;
        core[0].rgb = true;
        break;
      case BGR_FLOAT:
        core[0].pixelType = FormatTools.FLOAT;
        core[0].sizeC *= 3;
        core[0].rgb = true;
        break;
      case COMPLEX:
      case COMPLEX_FLOAT:
        throw new FormatException("Sorry, complex pixel data not supported.");
      default:
        throw new FormatException("Unknown pixel type: " + pixelType);
    }
  }

  // -- Helper classes --

  /** Top-level class that implements logic common to all types of Segment. */
  class Segment {
    public long startingPosition;
    public String id;
    public long allocatedSize;
    public long usedSize;

    public void fillInData() throws IOException {
      // read the segment header
      allocatedSize = in.readLong();
      usedSize = in.readLong();

      if (usedSize == 0) {
        usedSize = allocatedSize;
      }
    }
  }

  /** Segment with ID "ZISRAWFILE". */
  class FileHeader extends Segment {
    public int majorVersion;
    public int minorVersion;
    public long primaryFileGUID;
    public long fileGUID;
    public int filePart;
    public long directoryPosition;
    public long metadataPosition;
    public boolean updatePending;
    public long attachmentDirectoryPosition;

    public void fillInData() throws IOException {
      super.fillInData();

      majorVersion = in.readInt();
      minorVersion = in.readInt();
      in.skipBytes(4); // reserved 1
      in.skipBytes(4); // reserved 2
      primaryFileGUID = in.readLong();
      fileGUID = in.readLong();
      filePart = in.readInt();
      directoryPosition = in.readLong();
      metadataPosition = in.readLong();
      updatePending = in.readInt() != 0;
      attachmentDirectoryPosition = in.readLong();
    }
  }

  /** Segment with ID "ZISRAWMETADATA". */
  class Metadata extends Segment {
    public String xml;
    public byte[] attachment;

    public void fillInData() throws IOException {
      super.fillInData();

      int xmlSize = in.readInt();
      int attachmentSize = in.readInt();

      in.seek(in.getFilePointer() + allocatedSize - 8);

      xml = in.readString(xmlSize);
      attachment = new byte[attachmentSize];
      in.read(attachment);
    }
  }

  /** Segment with ID "ZISRAWSUBBLOCK". */
  class SubBlock extends Segment {
    public int metadataSize;
    public int attachmentSize;
    public long dataSize;
    public DirectoryEntry directoryEntry;
    public String metadata;

    public int seriesIndex;
    public int planeIndex;

    private long dataOffset;

    public void fillInData() throws IOException {
      super.fillInData();

      long fp = in.getFilePointer();
      metadataSize = in.readInt();
      attachmentSize = in.readInt();
      dataSize = in.readLong();
      directoryEntry = new DirectoryEntry();
      in.skipBytes((int) Math.max(256 - (in.getFilePointer() - fp), 0));

      metadata = in.readString(metadataSize);
      dataOffset = in.getFilePointer();
      in.seek(in.getFilePointer() + dataSize + attachmentSize);
    }

    // -- SubBlock API methods --

    public byte[] readPixelData() throws FormatException, IOException {
      in.seek(dataOffset);
      byte[] data = new byte[(int) dataSize];
      in.read(data);

      CodecOptions options = new CodecOptions();
      options.interleaved = isInterleaved();
      options.littleEndian = isLittleEndian();
      options.maxBytes = getSizeX() * getSizeY() * getRGBChannelCount() *
        FormatTools.getBytesPerPixel(getPixelType());

      switch (directoryEntry.compression) {
        case JPEG:
          data = new JPEGCodec().decompress(data, options);
          break;
        case LZW:
          data = new LZWCodec().decompress(data, options);
          break;
      }

      return data;
    }
  }

  /** Segment with ID "ZISRAWATTACH". */
  class Attachment extends Segment {
    public int dataSize;
    public AttachmentEntry attachment;
    public byte[] attachmentData;

    public void fillInData() throws IOException {
      super.fillInData();

      dataSize = in.readInt();
      in.skipBytes(12); // reserved
      attachment = new AttachmentEntry();
      in.skipBytes(112); // reserved
      attachmentData = new byte[dataSize];
      in.read(attachmentData);
    }

  }

  class DirectoryEntry {
    public String schemaType;
    public int pixelType;
    public long filePosition;
    public int filePart;
    public int compression;
    public byte pyramidType;
    public int dimensionCount;
    public DimensionEntry[] dimensionEntries;

    public DirectoryEntry() throws IOException {
      schemaType = in.readString(2);
      pixelType = in.readInt();
      filePosition = in.readLong();
      filePart = in.readInt();
      compression = in.readInt();
      pyramidType = in.readByte();
      in.skipBytes(1); // reserved
      in.skipBytes(4); // reserved
      dimensionCount = in.readInt();

      dimensionEntries = new DimensionEntry[dimensionCount];
      for (int i=0; i<dimensionEntries.length; i++) {
        dimensionEntries[i] = new DimensionEntry();
      }
    }
  }

  class DimensionEntry {
    public String dimension;
    public int start;
    public int size;
    public float startCoordinate;
    public int storedSize;

    public DimensionEntry() throws IOException {
      dimension = in.readString(4).trim();
      start = in.readInt();
      size = in.readInt();
      startCoordinate = in.readFloat();
      storedSize = in.readInt();
    }
  }

  class AttachmentEntry {
    public String schemaType;
    public long filePosition;
    public int filePart;
    public String contentGUID;
    public String contentFileType;
    public String name;

    public AttachmentEntry() throws IOException {
      schemaType = in.readString(2);
      in.skipBytes(10); // reserved
      filePosition = in.readLong();
      filePart = in.readInt();
      contentGUID = in.readString(16);
      contentFileType = in.readString(8);
      name = in.readString(80);
    }
  }

}
