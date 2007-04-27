//
// GatanReader.java
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

package loci.formats.in;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;
import loci.formats.*;

/**
 * GatanReader is the file format reader for Gatan files.
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class GatanReader extends FormatReader {

  // -- Constants --

  private static final byte[] GATAN_MAGIC_BLOCK_1 = {0, 0, 0, 3};

  // -- Fields --

  /** Offset to pixel data. */
  private long pixelOffset;

  /** List of pixel sizes. */
  private Vector pixelSizes;

  private int bytesPerPixel;

  protected int pixelDataNum = 0;
  protected int datatype;

  // -- Constructor --

  /** Constructs a new Gatan reader. */
  public GatanReader() { super("Gatan Digital Micrograph", "dm3"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    if (block == null) return false;
    if (block.length != GATAN_MAGIC_BLOCK_1.length) return false;
    for (int i=0; i<block.length; i++) {
      if (block[i] != GATAN_MAGIC_BLOCK_1[i]) return false;
    }
    return true;
  }

  /* @see loci.formats.IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    byte[] buf = new byte[core.sizeX[0] * core.sizeY[0] * bytesPerPixel];
    return openBytes(no, buf);
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    if (no != 0) {
      throw new FormatException("Invalid image number: " + no);
    }
    if (buf.length < core.sizeX[0] * core.sizeY[0] * bytesPerPixel) {
      throw new FormatException("Buffer too small.");
    }

    in.seek(pixelOffset);
    in.read(buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (no < 0 || no >= getImageCount()) {
      throw new FormatException("Invalid image number: " + no);
    }

    return ImageTools.makeImage(openBytes(no), core.sizeX[0], core.sizeY[0],
      1, false, bytesPerPixel, core.littleEndian[0]);
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("GatanReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);
    pixelOffset = 0;

    status("Verifying Gatan format");

    core.littleEndian[0] = false;
    pixelSizes = new Vector();

    byte[] tmp = new byte[4];
    in.read(tmp);
    // only support version 3
    if (!isThisType(tmp)) {
      throw new FormatException("invalid header");
    }

    status("Reading tags");

    in.skipBytes(4);
    in.read(tmp);
    core.littleEndian[0] = DataTools.bytesToInt(tmp, core.littleEndian[0]) == 1;

    // TagGroup instance

    in.skipBytes(2);
    in.read(tmp);
    parseTags(DataTools.bytesToInt(tmp, !core.littleEndian[0]), "initFile");

    status("Populating metadata");

    switch (datatype) {
      case 1:
        core.pixelType[0] = FormatTools.UINT16;
        break;
      case 2:
        core.pixelType[0] = FormatTools.FLOAT;
        break;
      case 3:
        core.pixelType[0] = FormatTools.FLOAT;
        break;
      // there is no case 4
      case 5:
        core.pixelType[0] = FormatTools.FLOAT;
        break;
      case 6:
        core.pixelType[0] = FormatTools.UINT8;
        break;
      case 7:
        core.pixelType[0] = FormatTools.INT32;
        break;
      case 8:
        core.pixelType[0] = FormatTools.UINT32;
        break;
      case 9:
        core.pixelType[0] = FormatTools.INT8;
        break;
      case 10:
        core.pixelType[0] = FormatTools.UINT16;
        break;
      case 11:
        core.pixelType[0] = FormatTools.UINT32;
        break;
      case 12:
        core.pixelType[0] = FormatTools.FLOAT;
        break;
      case 13:
        core.pixelType[0] = FormatTools.FLOAT;
        break;
      case 14:
        core.pixelType[0] = FormatTools.UINT8;
        break;
      case 23:
        core.pixelType[0] = FormatTools.INT32;
        break;
      default:
        core.pixelType[0] = FormatTools.INT8;
    }

    core.sizeZ[0] = 1;
    core.sizeC[0] = 1;
    core.sizeT[0] = 1;
    core.currentOrder[0] = "XYZTC";
    core.imageCount[0] = 1;
    core.rgb[0] = false;
    core.interleaved[0] = false;

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();

    store.setPixels(
      new Integer(core.sizeX[0]), // SizeX
      new Integer(core.sizeY[0]), // SizeY
      new Integer(core.sizeZ[0]), // SizeZ
      new Integer(core.sizeC[0]), // SizeC
      new Integer(core.sizeT[0]), // SizeT
      new Integer(core.pixelType[0]), // PixelType
      new Boolean(!core.littleEndian[0]), // BigEndian
      core.currentOrder[0], // DimensionOrder
      null, // Use image index 0
      null); // Use pixels index 0

    Float pixX = null;
    Float pixY = null;
    Float pixZ = null;

    if (pixelSizes.size() > 0) {
      pixX = new Float((String) pixelSizes.get(0));
    }
    else pixX = new Float(1);

    if (pixelSizes.size() > 1) {
      pixY = new Float((String) pixelSizes.get(1));
    }
    else pixY = new Float(1);

    if (pixelSizes.size() > 2) {
      pixZ = new Float((String) pixelSizes.get(2));
    }
    else pixZ = new Float(1);

    store.setDimensions(pixX, pixY, pixZ, null, null, null);

    String gamma = (String) getMeta("Gamma");
    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null);
      store.setDisplayChannel(new Integer(i), null, null,
        gamma == null ? null : new Float(gamma), null);
    }

    String mag = (String) getMeta("Indicated Magnification");
    store.setObjective(null, null, null, null,
      mag == null ? null : new Float(mag), null, null);
  }

  // -- Helper methods --

  /**
   * Parses Gatan DM3 tags.
   * Information on the DM3 structure found at:
   * http://rsb.info.nih.gov/ij/plugins/DM3Format.gj.html and
   * http://www-hrem.msm.cam.ac.uk/~cbb/info/dmformat/
   */
  private void parseTags(int numTags, String parent) throws IOException {
    byte[] temp = new byte[4];
    for (int i=0; i<numTags; i++) {
      byte type = in.readByte();  // can be 21 (data) or 20 (tag group)
      byte[] twobytes = new byte[2];
      in.read(twobytes);
      int length = DataTools.bytesToInt(twobytes, !core.littleEndian[0]);
      byte[] label = new byte[length];
      in.read(label);
      String labelString = new String(label);

      // image data is in tag with type 21 and label 'Data'
      // image dimensions are in type 20 tag with 2 type 15 tags
      // bytes per pixel is in type 21 tag with label 'PixelDepth'

      if (type == 21) {
        in.skipBytes(4);  // equal to '%%%%'
        in.read(temp);
        int n = DataTools.bytesToInt(temp, !core.littleEndian[0]);
        int dataType = 0;
        if (n == 1) {
          in.read(temp);
          dataType = DataTools.bytesToInt(temp, !core.littleEndian[0]);
          String data;
          switch (dataType) {
            case 2:
              data = "" + DataTools.read2SignedBytes(in, core.littleEndian[0]);
              break;
            case 3:
              data = "" + DataTools.read4SignedBytes(in, core.littleEndian[0]);
              break;
            case 4:
              data =
                "" + DataTools.read2UnsignedBytes(in, core.littleEndian[0]);
              break;
            case 5:
              data =
                "" + DataTools.read4UnsignedBytes(in, core.littleEndian[0]);
              break;
            case 6:
              data = "" + DataTools.readFloat(in, core.littleEndian[0]);
              break;
            case 7:
              data = "" + DataTools.readFloat(in, core.littleEndian[0]);
              in.skipBytes(4);
              break;
            case 8:
              data = "" + DataTools.readSignedByte(in);
              break;
            case 9:
              data = "" + DataTools.readSignedByte(in);
              break;
            case 10:
              data = "" + DataTools.readSignedByte(in);
              break;
            default:
              data = "0";
          }
          if (parent.equals("Dimensions")) {
            if (i == 0) core.sizeX[0] = Integer.parseInt(data);
            else if (i == 1) core.sizeY[0] = Integer.parseInt(data);
          }
          if (labelString.equals("PixelDepth")) {
            bytesPerPixel = Integer.parseInt(data);
          }
          else if (labelString.equals("Scale") && !data.equals("1.0") &&
            !data.equals("0.0"))
          {
            pixelSizes.add(data);
          }
          addMeta(labelString, data);
          if (labelString.equals("DataType")) datatype = Integer.parseInt(data);
        }
        else if (n == 2) {
          in.read(temp);
          dataType = DataTools.bytesToInt(temp, core.littleEndian[0]);
          if (dataType == 18) { // this should always be true
            in.read(temp);
            length = DataTools.bytesToInt(temp, core.littleEndian[0]);
          }
          byte[] data = new byte[length];
          in.read(data);
          addMeta(labelString, new String(label));
        }
        else if (n == 3) {
          in.read(temp);
          dataType = DataTools.bytesToInt(temp, !core.littleEndian[0]);
          if (dataType == 20) { // this should always be true
            in.read(temp);
            dataType = DataTools.bytesToInt(temp, !core.littleEndian[0]);
            in.read(temp);
            length = DataTools.bytesToInt(temp, !core.littleEndian[0]);

            if ("Data".equals(labelString)) pixelDataNum++;

            if ("Data".equals(labelString) /*&& pixelDataNum == 2*/) {
              // we're given the number of pixels, but the tag containing
              // bytes per pixel doesn't occur until after the image data
              //
              // this is a messy way to read pixel data, which uses the fact
              // that the first byte after the pixel data is either 20 or 21

              byte check = 0;
              double bpp = 0.5;
              long pos = in.getFilePointer();
              while (check != 20 && check != 21) {
                bpp *= 2;
                in.seek(pos);
                pixelOffset = pos;
                in.skipBytes((int) (bpp * length));
                check = in.readByte();
              }
              in.seek((int) (pos + bpp * length));
            }
            else {
              int[] data = new int[length];
              for (int j=0; j<length; j++) {
                if (dataType == 2 || dataType == 4) {
                  byte[] two = new byte[2];
                  in.read(two);
                  data[j] = (int) DataTools.bytesToShort(two,
                    !core.littleEndian[0]);
                }
                else if (dataType == 7) in.skipBytes(8);
                else if (dataType == 8 || dataType == 9) in.skipBytes(1);
                else {
                  in.read(temp);
                  data[j] = DataTools.bytesToInt(temp, !core.littleEndian[0]);
                }
              }
            }
          }
        }
        else {
          in.read(temp);
          dataType = DataTools.bytesToInt(temp, !core.littleEndian[0]);
          // this is a normal struct of simple types
          if (dataType == 15) {
            int skip = 0;
            in.read(temp);
            skip += DataTools.bytesToInt(temp, !core.littleEndian[0]);
            in.read(temp);
            int numFields = DataTools.bytesToInt(temp, !core.littleEndian[0]);
            for (int j=0; j<numFields; j++) {
              in.read(temp);
              skip += DataTools.bytesToInt(temp, !core.littleEndian[0]);
              in.read(temp);
              dataType = DataTools.bytesToInt(temp, !core.littleEndian[0]);

              switch (dataType) {
                case 2:
                  skip += 2;
                  break;
                case 3:
                  skip += 4;
                  break;
                case 4:
                  skip += 2;
                  break;
                case 5:
                  skip += 4;
                  break;
                case 6:
                  skip += 4;
                  break;
                case 7:
                  skip += 8;
                  break;
                case 8:
                  skip += 1;
                  break;
                case 9:
                  skip += 1;
                  break;
              }
            }
            in.skipBytes(skip);
          }
          else if (dataType == 20) {
            // this is an array of structs
            int skip = 0;
            in.read(temp);
            dataType = DataTools.bytesToInt(temp, !core.littleEndian[0]);
            if (dataType == 15) { // should always be true
              in.read(temp);
              skip += DataTools.bytesToInt(temp, !core.littleEndian[0]);
              in.read(temp);
              int numFields = DataTools.bytesToInt(temp, !core.littleEndian[0]);
              for (int j=0; j<numFields; j++) {
                in.read(temp);
                skip += DataTools.bytesToInt(temp, !core.littleEndian[0]);
                in.read(temp);
                dataType = DataTools.bytesToInt(temp, !core.littleEndian[0]);

                switch (dataType) {
                  case 2:
                    skip += 2;
                    break;
                  case 3:
                    skip += 4;
                    break;
                  case 4:
                    skip += 2;
                    break;
                  case 5:
                    skip += 4;
                    break;
                  case 6:
                    skip += 4;
                    break;
                  case 7:
                    skip += 8;
                    break;
                  case 8:
                    skip += 1;
                    break;
                  case 9:
                    skip += 1;
                    break;
                }
              }
            }
            in.read(temp);
            skip *= DataTools.bytesToInt(temp, !core.littleEndian[0]);
            in.skipBytes(skip);
          }
        }
      }
      else if (type == 20) {
        in.skipBytes(2);
        in.read(temp);
        parseTags(DataTools.bytesToInt(temp, !core.littleEndian[0]),
          labelString);
      }
    }
  }

}
