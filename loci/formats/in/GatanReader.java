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
import java.util.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

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

  // -- Constants --

  /** Tag types. */
  private static final int GROUP = 20;
  private static final int VALUE = 21;

  /** Data types. */
  private static final int ARRAY = 15;
  private static final int SHORT = 2;
  private static final int USHORT = 4;
  private static final int INT = 3;
  private static final int UINT = 5;
  private static final int FLOAT = 6;
  private static final int DOUBLE = 7;
  private static final int BYTE = 8;
  private static final int UBYTE = 9;
  private static final int CHAR = 10;

  // -- Fields --

  /** Offset to pixel data. */
  private long pixelOffset;

  /** List of pixel sizes. */
  private Vector pixelSizes;

  private int bytesPerPixel;

  private int pixelDataNum = 0;
  private int numPixelBytes;

  private boolean signed;
  private long timestamp;
  private float gamma, mag, voltage;
  private String info;

  // -- Constructor --

  /** Constructs a new Gatan reader. */
  public GatanReader() { super("Gatan Digital Micrograph", "dm3"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    if (block == null || block.length < 4) return false;
    return DataTools.bytesToInt(block, false) == 3;
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

    int bpp = FormatTools.getBytesPerPixel(core.pixelType[0]);
    in.seek(pixelOffset + y * core.sizeX[0] * bpp);

    for (int row=0; row<h; row++) {
      in.skipBytes(x * bpp);
      in.read(buf, row * w * bpp, w * bpp);
      in.skipBytes(bpp * (core.sizeX[0] - w - x));
    }

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    pixelOffset = 0;
    bytesPerPixel = pixelDataNum = numPixelBytes = 0;
    pixelSizes = null;
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
    in.order(!core.littleEndian[0]);

    // TagGroup instance

    in.skipBytes(2);
    parseTags(in.readInt(), "initFile");

    status("Populating metadata");

    core.littleEndian[0] = true;

    int bytes = numPixelBytes / (core.sizeX[0] * core.sizeY[0]);

    switch (bytes) {
      case 1:
        core.pixelType[0] = signed ? FormatTools.INT8 : FormatTools.UINT8;
        break;
      case 2:
        core.pixelType[0] = signed ? FormatTools.INT16 : FormatTools.UINT16;
        break;
      case 4:
        core.pixelType[0] = signed ? FormatTools.INT32 : FormatTools.UINT32;
        break;
      default:
        throw new FormatException("Unsupported pixel type");
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
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);
    store.setImageCreationDate(
      DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), 0);
    MetadataTools.populatePixels(store, this);

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

    store.setDimensionsPhysicalSizeX(pixX, 0, 0);
    store.setDimensionsPhysicalSizeY(pixY, 0, 0);
    store.setDimensionsPhysicalSizeZ(pixZ, 0, 0);

    for (int i=0; i<core.sizeC[0]; i++) {
      // CTR CHECK
//      store.setDisplayChannel(new Integer(i), null, null,
//        new Float(gamma), null);
    }

    // CTR CHECK
    //store.setObjectiveCalibratedMagnification(new Float(mag), 0, 0);
    //store.setDetectorVoltage(new Float(voltage), 0, 0);

    if (info == null) info = "";
    StringTokenizer scopeInfo = new StringTokenizer(info, "(");
    while (scopeInfo.hasMoreTokens()) {
      String token = scopeInfo.nextToken().trim();
      if (token.startsWith("Microscope")) {
        //token = token.substring(0, token.indexOf(" ")).trim();
        //store.setMicroscopeManufacturer(
        //  token.substring(token.indexOf(" ")).trim(), 0, 0);
        //store.setMicroscopeModel(token, 0, 0);
      }
      else if (token.startsWith("Mode")) {
        token = token.substring(token.indexOf(" ")).trim();
        String mode = token.substring(0, token.indexOf(" ")).trim();
        if (mode.equals("TEM")) mode = "Other";
        store.setLogicalChannelMode(mode, 0, 0);
      }
    }
  }

  // -- Helper methods --

  /**
   * Parses Gatan DM3 tags.
   * Information on the DM3 structure found at:
   * http://rsb.info.nih.gov/ij/plugins/DM3Format.gj.html and
   * http://www-hrem.msm.cam.ac.uk/~cbb/info/dmformat/
   *
   * The basic structure is this: the file is comprised of a list of tags.
   * Each tag is either a data tag or a group tag.  Group tags are simply
   * containers for more group and data tags, where data tags contain actual
   * metadata.  Each data tag is comprised of a type (byte, short, etc.),
   * a label, and a value.
   */
  private void parseTags(int numTags, String parent) throws IOException {
    for (int i=0; i<numTags; i++) {
      byte type = in.readByte();  // can be 21 (data) or 20 (tag group)
      int length = in.readShort();
      String labelString = in.readString(length);

      // image data is in tag with type 21 and label 'Data'
      // image dimensions are in type 20 tag with 2 type 15 tags
      // bytes per pixel is in type 21 tag with label 'PixelDepth'

      String value = null;

      if (type == VALUE) {
        in.skipBytes(4);  // equal to '%%%%'
        int n = in.readInt();
        int dataType = in.readInt();
        if (n == 1) {
          if (parent.equals("Dimensions") && labelString.length() == 0) {
            in.order(!in.isLittleEndian());
            if (i == 0) core.sizeX[0] = in.readInt();
            else if (i == 1) core.sizeY[0] = in.readInt();
            in.order(!in.isLittleEndian());
          }
          else value = String.valueOf(readValue(dataType));
        }
        else if (n == 2) {
          if (dataType == 18) { // this should always be true
            length = in.readInt();
          }
          value = in.readString(length);
        }
        else if (n == 3 && !labelString.equals("Data")) {
          if (dataType == GROUP) { // this should always be true
            dataType = in.readInt();
            length = in.readInt();
            if (dataType == 10) {
              in.skipBytes(length);
            }
            else {
              value = DataTools.stripString(in.readString(length * 2));
            }
          }
        }
        else if (n == 3 && labelString.equals("Data")) {
          if (dataType == GROUP) {  // this should always be true
            dataType = in.readInt();
            length = in.readInt();
            pixelOffset = in.getFilePointer();
            in.skipBytes(getNumBytes(dataType) * length);
            numPixelBytes = (int) (in.getFilePointer() - pixelOffset);
          }
        }
        else {
          // this is a normal struct of simple types
          if (dataType == ARRAY) {
            in.skipBytes(4);
            int numFields = in.readInt();
            StringBuffer s = new StringBuffer();
            in.skipBytes(4);
            for (int j=0; j<numFields; j++) {
              dataType = in.readInt();
              s.append(readValue(dataType));
              if (j < numFields - 1) s.append(", ");
            }
            value = s.toString();
            byte b = in.readByte();
            while (b != GROUP && b != VALUE &&
              in.getFilePointer() < in.length() - 1)
            {
              b = in.readByte();
            }
            in.seek(in.getFilePointer() - 1);
          }
          else if (dataType == GROUP) {
            // this is an array of structs
            dataType = in.readInt();
            if (dataType == ARRAY) { // should always be true
              in.skipBytes(4);
              int numFields = in.readInt();
              int[] datatypes = new int[numFields];
              for (int j=0; j<numFields; j++) {
                in.skipBytes(4);
                datatypes[j] = in.readInt();
              }
              int len = in.readInt();

              double[][] values = new double[numFields][len];

              for (int k=0; k<len; k++) {
                for (int q=0; q<numFields; q++) {
                  values[q][k] = readValue(datatypes[q]);
                }
              }
            }
          }
        }
      }
      else if (type == GROUP) {
        in.skipBytes(2);
        parseTags(in.readInt(), labelString);
      }

      if (value != null) {
        addMeta(labelString, value);

        if (labelString.equals("Scale")) {
          if (value.indexOf(",") == -1) pixelSizes.add(value);
          else {
            float start =
              Float.parseFloat(value.substring(0, value.indexOf(",")));
            float end =
              Float.parseFloat(value.substring(value.indexOf(",") + 2));
            pixelSizes.add(String.valueOf(end - start));
          }
        }
        else if (labelString.equals("LowLimit")) {
          signed = Float.parseFloat(value) < 0;
        }
        else if (labelString.equals("Acquisition Start Time (epoch)")) {
          timestamp = (long) Double.parseDouble(value);
        }
        else if (labelString.equals("Voltage")) {
          voltage = Float.parseFloat(value);
        }
        else if (labelString.equals("Microscope Info")) info = value;
        else if (labelString.equals("Indicated Magnification")) {
          mag = Float.parseFloat(value);
        }
        else if (labelString.equals("Gamma")) gamma = Float.parseFloat(value);

        value = null;
      }
    }
  }

  private double readValue(int type) throws IOException {
    switch (type) {
      case SHORT:
      case USHORT:
        return in.readShort();
      case INT:
      case UINT:
        return in.readInt();
      case FLOAT:
        in.order(!in.isLittleEndian());
        float f = in.readFloat();
        in.order(!in.isLittleEndian());
        return f;
      case DOUBLE:
        in.order(!in.isLittleEndian());
        double dbl = in.readDouble();
        in.order(!in.isLittleEndian());
        return dbl;
      case BYTE:
      case UBYTE:
      case CHAR:
        return in.readByte();
    }
    return 0;
  }

  private int getNumBytes(int type) {
    switch (type) {
      case SHORT:
      case USHORT:
        return 2;
      case INT:
      case UINT:
      case FLOAT:
        return 4;
      case DOUBLE:
        return 8;
      case BYTE:
      case UBYTE:
      case CHAR:
        return 1;
    }
    return 0;
  }

}
