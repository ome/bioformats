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

import java.io.IOException;
import java.util.Vector;
import loci.formats.*;

/**
 * GatanReader is the file format reader for Gatan files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/GatanReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/GatanReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class GatanReader extends FormatReader {

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
    if (block == null || block.length < 4) return false;
    return DataTools.bytesToInt(block, false) == 3;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    in.seek(pixelOffset);
    in.read(buf);
    return buf;
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

    in.order(false);

    // only support version 3
    if (in.readInt() != 3) {
      throw new FormatException("invalid header");
    }

    status("Reading tags");

    in.skipBytes(4);
    core.littleEndian[0] = in.readInt() == 1;

    // TagGroup instance

    in.skipBytes(2);
    in.order(!core.littleEndian[0]);
    parseTags(in.readInt(), "initFile");

    status("Populating metadata");

    switch (datatype) {
      case 1:
      case 10:
        core.pixelType[0] = FormatTools.UINT16;
        break;
      case 2:
      case 3:
      case 5:
      case 12:
      case 13:
        core.pixelType[0] = FormatTools.FLOAT;
        break;
      case 7:
      case 8:
      case 11:
      case 23:
        core.pixelType[0] = FormatTools.UINT32;
        break;
      default:
        core.pixelType[0] = FormatTools.UINT8;
    }

    core.sizeZ[0] = 1;
    core.sizeC[0] = 1;
    core.sizeT[0] = 1;
    core.currentOrder[0] = "XYZTC";
    core.imageCount[0] = 1;
    core.rgb[0] = false;
    core.interleaved[0] = false;
    core.metadataComplete[0] = true;
    core.indexed[0] = false;
    core.falseColor[0] = false;

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();
    store.setImage(currentId, null, null, null);

    FormatTools.populatePixels(store, this);

    Float pixX = new Float(1);
    Float pixY = new Float(1);
    Float pixZ = new Float(1);

    if (pixelSizes.size() > 0) {
      pixX = new Float((String) pixelSizes.get(0));
    }

    if (pixelSizes.size() > 1) {
      pixY = new Float((String) pixelSizes.get(1));
    }

    if (pixelSizes.size() > 2) {
      pixZ = new Float((String) pixelSizes.get(2));
    }

    store.setDimensions(pixX, pixY, pixZ, null, null, null);

    String gamma = (String) getMeta("Gamma");
    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null);
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
    for (int i=0; i<numTags; i++) {
      byte type = in.readByte();  // can be 21 (data) or 20 (tag group)
      int length = in.readShort();
      String labelString = in.readString(length);

      // image data is in tag with type 21 and label 'Data'
      // image dimensions are in type 20 tag with 2 type 15 tags
      // bytes per pixel is in type 21 tag with label 'PixelDepth'

      if (type == 21) {
        in.skipBytes(4);  // equal to '%%%%'
        int n = in.readInt();
        int dataType = 0;
        if (n == 1) {
          dataType = in.readInt();
          String data;
          in.order(core.littleEndian[0]);
          switch (dataType) {
            case 2:
            case 4:
              data = "" + in.readShort();
              break;
            case 3:
            case 5:
              data = "" + in.readInt();
              break;
            case 6:
              data = "" + in.readFloat();
              break;
            case 7:
              data = "" + in.readFloat();
              in.skipBytes(4);
              break;
            case 8:
            case 9:
            case 10:
              data = "" + in.read();
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
          in.order(!core.littleEndian[0]);
        }
        else if (n == 2) {
          in.order(core.littleEndian[0]);
          dataType = in.readInt();
          if (dataType == 18) { // this should always be true
            length = in.readInt();
          }
          addMeta(labelString, in.readString(length));
          in.order(!core.littleEndian[0]);
        }
        else if (n == 3) {
          dataType = in.readInt();
          if (dataType == 20) { // this should always be true
            dataType = in.readInt();
            length = in.readInt();

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
              in.seek((long) (pos + bpp * length));
            }
            else {
              int[] data = new int[length];
              for (int j=0; j<length; j++) {
                if (dataType == 2 || dataType == 4) {
                  data[j] = in.readShort();
                }
                else if (dataType == 7) in.skipBytes(8);
                else if (dataType == 8 || dataType == 9) in.skipBytes(1);
                else {
                  data[j] = in.readInt();
                }
              }
            }
          }
        }
        else {
          dataType = in.readInt();
          // this is a normal struct of simple types
          if (dataType == 15) {
            int skip = in.readInt();
            int numFields = in.readInt();
            for (int j=0; j<numFields; j++) {
              skip += in.readInt();
              dataType = in.readInt();

              switch (dataType) {
                case 2:
                case 4:
                  skip += 2;
                  break;
                case 3:
                case 5:
                case 6:
                  skip += 4;
                  break;
                case 7:
                  skip += 8;
                  break;
                case 8:
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
            dataType = in.readInt();
            if (dataType == 15) { // should always be true
              skip += in.readInt();
              int numFields = in.readInt();
              for (int j=0; j<numFields; j++) {
                skip += in.readInt();
                dataType = in.readInt();

                switch (dataType) {
                  case 2:
                  case 4:
                    skip += 2;
                    break;
                  case 3:
                  case 5:
                  case 6:
                    skip += 4;
                    break;
                  case 7:
                    skip += 8;
                    break;
                  case 8:
                  case 9:
                    skip += 1;
                    break;
                }
              }
            }
            skip *= in.readInt();
            in.skipBytes(skip);
          }
        }
      }
      else if (type == 20) {
        in.skipBytes(2);
        parseTags(in.readInt(), labelString);
      }
    }
  }

}
