//
// AmiraReader.java
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
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.InflaterInputStream;

import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.tools.AmiraParameters;

/**
 * This is a file format reader for AmiraMesh data.
 * @author Gregory Jefferis jefferis at gmail.com
 * @author Johannes Schindelin johannes.schindelin at gmx.de
 */
public class AmiraReader extends FormatReader {

  AmiraParameters parameters;
  long offsetOfFirstStream;

  // for non-raw plane formats
  PlaneReader planeReader;

  // for labels
  byte[][] lut;

  public AmiraReader() {
    super("Amira", new String[] {"am", "amiramesh", "grey", "hx", "labels"});
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  /* (non-Javadoc)
   * @see loci.formats.FormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int width,
      int height) throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, width, height);

    int planeSize = FormatTools.getPlaneSize(this);
    if (planeReader != null) {
      if (x == 0 && y == 0 &&
          width == parameters.width && height == parameters.height)
        return planeReader.read(no, buf);

      // plane readers can only read whole planes, so we need to blit
      int bytesPerPixel = FormatTools.getBytesPerPixel(core[series].pixelType);
      byte[] planeBuf = new byte[planeSize];
      planeReader.read(no, planeBuf);
      for (int j = y; j < y + height; j++)
        System.arraycopy(planeBuf, (x + j * parameters.width) * bytesPerPixel,
          buf, (j - y) * width * bytesPerPixel, width * bytesPerPixel);
      return buf;
    }

    in.seek(offsetOfFirstStream + no * planeSize);
    readPlane(in, x, y, width, height, buf);

    return buf;
  }

  /* (non-Javadoc)
   * @see loci.formats.FormatReader#getUsedFiles()
   */
  public String[] getUsedFiles() {
    return super.getUsedFiles();
  }

  /* (non-Javadoc)
   * @see loci.formats.FormatReader#initFile(java.lang.String)
   */
  protected void initFile(String id) throws FormatException, IOException {
    debug("AmiraReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    parameters = new AmiraParameters(in);
    offsetOfFirstStream = in.getFilePointer();

    status("Populating metadata hashtable");

    addGlobalMeta("Image width", parameters.width);
    addGlobalMeta("Image height", parameters.height);
    addGlobalMeta("Number of planes", parameters.depth);
    addGlobalMeta("Bits per pixel", 8);

    status("Populating core metadata");

    int channelIndex = 1;
    while (parameters.getStreams().get("@" + channelIndex) != null) {
      channelIndex++;
    }

    core[0].sizeX = parameters.width;
    core[0].sizeY = parameters.height;
    core[0].sizeZ = parameters.depth;
    core[0].sizeT = 1;
    core[0].sizeC = channelIndex - 1;
    core[0].imageCount = getSizeZ() * getSizeC();
    core[0].littleEndian = parameters.littleEndian;
    core[0].dimensionOrder = "XYZCT";

    if (parameters.streamTypes[0].toLowerCase().equals("byte")) {
      core[0].pixelType = FormatTools.UINT8;
    }
    else if (parameters.streamTypes[0].toLowerCase().equals("short")) {
      core[0].pixelType = FormatTools.INT16;
      addGlobalMeta("Bits per pixel", 16);
    }
    else if (parameters.streamTypes[0].toLowerCase().equals("ushort")) {
      core[0].pixelType = FormatTools.UINT16;
      addGlobalMeta("Bits per pixel", 16);
    }
    else if (parameters.streamTypes[0].toLowerCase().equals("int")) {
      core[0].pixelType = FormatTools.INT32;
      addGlobalMeta("Bits per pixel", 32);
    }
    else if (parameters.streamTypes[0].toLowerCase().equals("float")) {
      core[0].pixelType = FormatTools.FLOAT;
      addGlobalMeta("Bits per pixel", 32);
    }
    else {
      System.err.println("Assuming data type is byte");
      core[0].pixelType = FormatTools.UINT8;
    }
    status("Populating metadata store");

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);
    MetadataTools.setDefaultCreationDate(store, id, 0);

    // Note that Amira specifies a bounding box, not pixel sizes.
    // The bounding box is the range of the centre of the voxels
    float pixelWidth = (float) (parameters.x1 - parameters.x0) /
      (parameters.width - 1);
    float pixelHeight = (float) (parameters.y1 - parameters.y0) /
      (parameters.height - 1);
    // TODO - what is correct setting if single slice?
    float pixelDepth = (float) (parameters.z1 - parameters.z0) /
      (parameters.depth - 1);

    // Amira does not have a standard form for encoding units, so we just
    // have to assume microns for microscopy data
    addGlobalMeta("Pixels per meter (X)", 1e6 / pixelWidth);
    addGlobalMeta("Pixels per meter (Y)", 1e6 / pixelHeight);
    addGlobalMeta("Pixels per meter (Z)", 1e6 / pixelDepth);

    // NB these methods expects pixels sizes in microns
    store.setDimensionsPhysicalSizeX(new Float(pixelWidth), 0, 0);
    store.setDimensionsPhysicalSizeY(new Float(pixelHeight), 0, 0);
    store.setDimensionsPhysicalSizeZ(new Float(pixelDepth), 0, 0);

    if (parameters.ascii)
      planeReader = new ASCII(core[0].pixelType,
        parameters.width * parameters.height);

    int compressionType = 0;
    ArrayList streamData = (ArrayList) parameters.getStreams().get("@1");
    if (streamData.size() > 2) {
      String compression = (String) streamData.get(2);
      if (compression.startsWith("HxZip,")) {
        compressionType = 1;
        long size = Long.parseLong(compression.substring("HxZip,".length()));
        planeReader = new HxZip(size);
      }
      else if (compression.startsWith("HxByteRLE,")) {
        compressionType = 2;
        long size =
          Long.parseLong(compression.substring("HxByteRLE,".length()));
        planeReader = new HxRLE(parameters.depth, size);
      }

    }
    addGlobalMeta("Compression", compressionType);

    Map params = (Map) parameters.getMap().get("Parameters");
    if (params != null) {
      Map materials = (Map) params.get("Materials");
      if (materials != null) {
        lut = getLookupTable(materials);
        core[0].indexed = true;
      }
    }
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    if (!FormatTools.validStream(stream, 50, false)) return false;
    String c=stream.readLine().toString();

    Matcher amiraMeshDef = Pattern.compile("#\\s+AmiraMesh.*?" +
        "(BINARY|ASCII)(-LITTLE-ENDIAN)*").matcher(c);
    return amiraMeshDef.find();
  }

  /* @see IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() {
    return lut;
  }

  byte[][] getLookupTable(Map materials) throws FormatException {
    byte[][] result = new byte[3][256];
    int i = -1;
    for (Object label : materials.keySet()) {
      i++;
      Object object = materials.get(label);
      if (!(object instanceof Map))
        throw new FormatException("Invalid material: " + label);
      Map material = (Map) object;
      object = material.get("Color");
      if (object == null)
        continue; // black
      if (!(object instanceof Number[]))
        throw new FormatException("Invalid material: " + label);
      Number[] color = (Number[]) object;
      if (color.length != 3)
        throw new FormatException("Invalid color: " +
          color.length + " channels");
      for (int j = 0; j < 3; j++)
        result[j][i] = (byte) (int) (255 * color[j].floatValue());
    }
    return result;
  }

  /**
   * This is the common interface for all formats, compressed or not.
   */
  interface PlaneReader {
    byte[] read(int no, byte[] buf) throws FormatException, IOException;
  }

  /**
   * This class provides a not-quite-efficient, but simple reader for
   * planes stored in ASCII-encoded numbers.
   *
   * Should efficiency ever become a concern, we'll need to have
   * specializations for every pixel type.
   */
  class ASCII implements PlaneReader {
    int pixelType, bytesPerPixel, pixelsPerPlane;
    long[] offsets;
    byte[] numberBuffer = new byte[32];

    ASCII(int pixelType, int pixelsPerPlane) {
      this.pixelType = pixelType;
      this.pixelsPerPlane = pixelsPerPlane;
      bytesPerPixel = FormatTools.getBytesPerPixel(pixelType);
      offsets = new long[parameters.depth + 1];
      offsets[0] = offsetOfFirstStream;
    }

    public byte[] read(int no, byte[] buf) throws FormatException, IOException {
      if (offsets[no] == 0) {
        int i = no - 1;
        while (offsets[i] == 0)
          i--;
        in.seek(offsets[i]);
        while (i < no) {
          for (int j = 0; j < pixelsPerPlane; j++)
            readNumberString();
          i++;
          offsets[i] = in.getFilePointer();
        }
      }
      else
        in.seek(offsets[no]);
      for (int j = 0; j < pixelsPerPlane; j++) {
        int offset = j * bytesPerPixel;
        double number = readNumberString();
        long value = pixelType == FormatTools.DOUBLE ?
          Double.doubleToLongBits(number) :
          pixelType == FormatTools.FLOAT ?
          Float.floatToIntBits((float) number) :
          (long) number;
        DataTools.unpackBytes(value, buf, offset,
            bytesPerPixel, false);
      }
      offsets[no + 1] = in.getFilePointer();
      return buf;
    }

    double readNumberString() throws IOException {
      numberBuffer[0] = skipWhiteSpace();
      for (int i = 1;; i++) {
        byte c = in.readByte();
        if (!(c >= '0' && c <= '9') && c != '.')
          return Double.parseDouble(new String(numberBuffer, 0, i));
        numberBuffer[i] = c;
      }
    }

    byte skipWhiteSpace() throws IOException {
      for (;;) {
        byte c = in.readByte();
        if (c != ' ' && c != '\t' && c != '\n')
          return c;
      }
    }
  }

  /**
   * This is the reader for GZip-compressed AmiraMeshes.
   *
   * As such files contain a single GZipped stream for the complete stack,
   * we cannot really access the slices randomly, but have to decompress
   * instead of seeking.
   */
  class HxZip implements PlaneReader {
    long offsetOfStream, compressedSize;
    int currentNo, planeSize;
    InflaterInputStream decompressor;

    HxZip(long compressedSize) {
      this.compressedSize = compressedSize;
      planeSize = FormatTools.getPlaneSize(AmiraReader.this);
      offsetOfStream = offsetOfFirstStream;
      currentNo = Integer.MAX_VALUE;
    }

    void initDecompressor() throws IOException {
      currentNo = 0;
      in.seek(offsetOfStream);
      decompressor = new InflaterInputStream(in);
    }

    public byte[] read(int no, byte[] buf) throws FormatException, IOException {
      if (no < currentNo)
        initDecompressor();
      for (; currentNo <= no; currentNo++) {
        int offset = 0, len = planeSize;
        while (len > 0) {
          int count = decompressor.read(buf, offset, len);
          if (count <= 0)
            return null;
          offset += count;
          len -= count;
        }
      }
      return buf;
    }
  }

  /**
   * This is the reader for RLE-compressed AmiraMeshes.
   *
   * Amira expects the RLE-compressed stream to be aligned with the slices.
   * In other words, the RLE stream must restart at each slice start.
   */
  class HxRLE implements PlaneReader {
    long compressedSize;
    long[] offsets;
    int currentNo, maxOffsetIndex, planeSize;

    HxRLE(int sliceCount, long compressedSize) {
      this.compressedSize = compressedSize;
      offsets = new long[sliceCount + 1];
      offsets[0] = offsetOfFirstStream;
      planeSize = FormatTools.getPlaneSize(AmiraReader.this);
      maxOffsetIndex = currentNo = 0;
    }

    void read(byte[] buf, int len) throws FormatException, IOException {
      int off = 0;
      while (len > 0) {
        int insn = in.readByte();
        if (insn < 0) {
          insn = (insn & 0x7f);
          if (insn > len)
            throw new FormatException("Slice " + currentNo + " is unaligned!");
          while (insn > 0) {
            int count = in.read(buf, off, insn);
            if (count < 0)
              throw new IOException("End of file!");
            insn -= count;
            len -= count;
            off += count;
          }
        }
        else {
          if (insn > len)
            throw new FormatException("Slice " + currentNo + " is unaligned!");
          Arrays.fill(buf, off, off + insn, in.readByte());
          len -= insn;
          off += insn;
        }
      }
    }

    public byte[] read(int no, byte[] buf) throws FormatException, IOException {
      if (maxOffsetIndex < no) {
        if (currentNo != maxOffsetIndex)
          in.seek(offsets[maxOffsetIndex]);
        while (maxOffsetIndex < no) {
          read(buf, planeSize);
          offsets[++maxOffsetIndex] = in.getFilePointer();
        }
      }
      else {
        if (currentNo != no)
          in.seek(offsets[no]);
        read(buf, planeSize);
        if (maxOffsetIndex == no)
          offsets[++maxOffsetIndex] = in.getFilePointer();
      }
      currentNo = no + 1;
      return buf;
    }
  }
}
