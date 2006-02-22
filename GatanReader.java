//
// GatanReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import java.awt.Image;
import java.io.*;

/**
 * GatanReader is the file format reader for Gatan files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class GatanReader extends FormatReader {

  // -- Constants --

  private static final byte[] GATAN_MAGIC_BLOCK_1 = {0, 0, 0, 3};


  // -- Fields --

  /** Current file. */
  protected RandomAccessFile in;

  /** Flag indicating whether current file is little endian. */
  protected boolean littleEndian;

  /** Array of pixel bytes. */
  protected byte[] pixelData;

  /** Dimensions -- width, height, bytes per pixel */
  protected int[] dims = new int[3];

  protected int pixelDataNum = 0;


  // -- Constructor --

  /** Constructs a new Gatan reader. */
  public GatanReader() { super("Gatan Digital Micrograph", "dm3"); }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a Gatan file. */
  public boolean isThisType(byte[] block) {
    if (block == null) return false;
    if (block.length != GATAN_MAGIC_BLOCK_1.length) return false;
    for (int i=0; i<block.length; i++) {
      if (block[i] != GATAN_MAGIC_BLOCK_1[i]) return false;
    }
    return true;
  }

  /** Determines the number of images in the given Gatan file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    // every Gatan file has only one image
    return 1;
  }

  /** Obtains the specified image from the given Gatan file. */
  public Image open(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    int width = dims[0];
    int height = dims[1];
    int channels = 1;

    int numSamples = width * height;

    // supporting 8, 16, and 32 bit data

    if (dims[2] == 1) {
      return ImageTools.makeImage(pixelData, width, height, channels, false);
    }
    else if (dims[2] == 2) {
      short[] data = new short[numSamples];
      for (int i=0; i<pixelData.length; i+=2) {
        data[i/2] = DataTools.bytesToShort(pixelData, i, littleEndian);
      }
      return ImageTools.makeImage(data, width, height, channels, false);
    }
    else if (dims[2] == 4) {
      // this could be broken, since we don't have any 32 bit samples
      // however, I'm pretty sure it will work
      int[] data = new int[numSamples];
      for (int i=0; i<pixelData.length; i+=4) {
        data[i/4] = DataTools.bytesToInt(pixelData, i, 4, littleEndian);
      }
      return ImageTools.makeImage(data, width, height, channels, false);
    }
    else {
      throw new FormatException("Sorry, " + dims[2] +
        " bytes per pixel is unsupported.");
    }
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given Gatan file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessFile(id, "r");

    littleEndian = false;

    byte[] temp = new byte[4];
    in.read(temp);
    // only support version 3
    if(!isThisType(temp)) {
      throw new FormatException("invalid header");
    }

    in.read(temp);
    int numBytes = DataTools.bytesToInt(temp, littleEndian);
    in.read(temp);
    if (DataTools.bytesToInt(temp, littleEndian) == 1) littleEndian = true;

    // TagGroup instance

    in.skipBytes(2);
    in.read(temp);
    parseTags(DataTools.bytesToInt(temp, !littleEndian), "initFile");

    if (ome != null) {
      int datatype = ((Integer) metadata.get("DataType")).intValue();

      String type = "int8";
      switch (datatype) {
        case 1: type = "int16"; break;
        case 2: type = "float"; break;
        case 3: type = "float"; break;
        // there is no case 4
        case 5: type = "float"; break;
        case 6: type = "Uint8"; break;
        case 7: type = "int32"; break;
        case 8: type = "Uint32"; break;
        case 9: type = "int8"; break;
        case 10: type = "Uint16"; break;
        case 11: type = "Uint32"; break;
        case 12: type = "float"; break;
        case 13: type = "float"; break;
        case 14: type = "Uint8"; break;
        case 23: type = "int32"; break;
      }

      OMETools.setAttribute(ome, "Pixels", "PixelType", type);
      OMETools.setAttribute(ome, "Pixels", "BigEndian",
        littleEndian ? "false" : "true");
      OMETools.setAttribute(ome, "Pixels", "SizeX", "" + dims[0]);
      OMETools.setAttribute(ome, "Pixels", "SizeY", "" + dims[1]);
      OMETools.setAttribute(ome, "Pixels", "SizeC", "1");
      OMETools.setAttribute(ome, "Pixels", "SizeZ", "1");
      OMETools.setAttribute(ome, "Pixels", "SizeT", "1");
      OMETools.setAttribute(ome, "Pixels", "DimensionOrder", "XYZTC");
    }
  }


  // -- Helper method --

  /**
   * Parses Gatan DM3 tags.
   * Information on the DM3 structure found at:
   * http://rsb.info.nih.gov/ij/plugins/DM3Format.gj.html and
   * http://www-hrem.msm.cam.ac.uk/~cbb/info/dmformat/
   */
  public void parseTags(int numTags, String parent) throws IOException {
    byte[] temp = new byte[4];
    for (int i=0; i<numTags; i++) {
      byte type = in.readByte();  // can be 21 (data) or 20 (tag group)
      byte[] twobytes = new byte[2];
      in.read(twobytes);
      int length = DataTools.bytesToInt(twobytes, !littleEndian);
      byte[] label = new byte[length];
      in.read(label);
      String labelString = new String(label);

      // image data is in tag with type 21 and label 'Data'
      // image dimensions are in type 20 tag with 2 type 15 tags
      // bytes per pixel is in type 21 tag with label 'PixelDepth'

      if (type == 21) {
        in.skipBytes(4);  // equal to '%%%%'
        in.read(temp);
        int n = DataTools.bytesToInt(temp, !littleEndian);
        int dataType = 0;
        if (n == 1) {
          in.read(temp);
          dataType = DataTools.bytesToInt(temp, !littleEndian);
          int data;
          switch (dataType) {
            case 2: data = DataTools.read2SignedBytes(in, littleEndian); break;
            case 3: data = DataTools.read4SignedBytes(in, littleEndian); break;
            case 4: data = DataTools.read2UnsignedBytes(in, littleEndian);
              break;
            case 5:
              data = (int) DataTools.read4UnsignedBytes(in, littleEndian);
              break;
            case 6: data = (int) DataTools.readFloat(in, littleEndian); break;
            case 7: data = (int) DataTools.readFloat(in, littleEndian);
              in.skipBytes(4);
              break;
            case 8: data = DataTools.readSignedByte(in); break;
            case 9: data = DataTools.readSignedByte(in); break;
            case 10: data = DataTools.readSignedByte(in); break;
            default: data = 0;
          }
          if (parent.equals("Dimensions")) {
            if (i == 0) dims[0] = data;
            else if (i == 1) dims[1] = data;
          }
          if ("PixelDepth".equals(labelString)) dims[2] = data;
          metadata.put(labelString, new Integer(data));
        }
        else if (n == 2) {
          in.read(temp);
          dataType = DataTools.bytesToInt(temp, littleEndian);
          if (dataType == 18) { // this should always be true
            in.read(temp);
            length = DataTools.bytesToInt(temp, littleEndian);
          }
          byte[] data = new byte[length];
          in.read(data);
          metadata.put(labelString, new String(label));
        }
        else if (n == 3) {
          in.read(temp);
          dataType = DataTools.bytesToInt(temp, !littleEndian);
          if (dataType == 20) { // this should always be true
            in.read(temp);
            dataType = DataTools.bytesToInt(temp, !littleEndian);
            in.read(temp);
            length = DataTools.bytesToInt(temp, !littleEndian);

            if ("Data".equals(labelString)) pixelDataNum++;

            if ("Data".equals(labelString) && pixelDataNum == 2) {
              // we're given the number of pixels, but the tag containing
              // bytes per pixel doesn't occur until after the image data
              //
              // this is a messy way to read pixel data, which uses the fact
              // that the first byte after the pixel data is either 20 or 21

              byte check = 0;
              double bpp = 0.5;
              int fp = (int) in.getFilePointer();
              while (check != 20 && check != 21) {
                bpp *= 2;
                in.seek(fp);
                pixelData = new byte[(int) bpp * length];
                in.read(pixelData);
                check = in.readByte();
              }
              in.seek((long) (fp + bpp * length));
            }
            else {
              int[] data = new int[length];
              for (int j=0; j<length; j++) {
                if (dataType == 2 || dataType == 4) {
                  byte[] two = new byte[2];
                  in.read(two);
                  data[j] = (int) DataTools.bytesToShort(two, !littleEndian);
                }
                else if (dataType == 7) in.skipBytes(8);
                else if (dataType == 8 || dataType == 9) in.skipBytes(1);
                else {
                  in.read(temp);
                  data[j] = DataTools.bytesToInt(temp, !littleEndian);
                }
              }
            }
          }
        }
        else {
          in.read(temp);
          dataType = DataTools.bytesToInt(temp, !littleEndian);
          // this is a normal struct of simple types
          if (dataType == 15) {
            int skip = 0;
            in.read(temp);
            skip += DataTools.bytesToInt(temp, !littleEndian);
            in.read(temp);
            int numFields = DataTools.bytesToInt(temp, !littleEndian);
            for (int j=0; j<numFields; j++) {
              in.read(temp);
              skip += DataTools.bytesToInt(temp, !littleEndian);
              in.read(temp);
              dataType = DataTools.bytesToInt(temp, !littleEndian);

              switch (dataType) {
                case 2: skip += 2; break;
                case 3: skip += 4; break;
                case 4: skip += 2; break;
                case 5: skip += 4; break;
                case 6: skip += 4; break;
                case 7: skip += 8; break;
                case 8: skip += 1; break;
                case 9: skip += 1; break;
              }
            }
            in.skipBytes(skip);
          }
          else if (dataType == 20) {
            // this is an array of structs
            int skip = 0;
            in.read(temp);
            dataType = DataTools.bytesToInt(temp, !littleEndian);
            if (dataType == 15) { // should always be true
              in.read(temp);
              skip += DataTools.bytesToInt(temp, !littleEndian);
              in.read(temp);
              int numFields = DataTools.bytesToInt(temp, !littleEndian);
              for (int j=0; j<numFields; j++) {
                in.read(temp);
                skip += DataTools.bytesToInt(temp, !littleEndian);
                in.read(temp);
                dataType = DataTools.bytesToInt(temp, !littleEndian);

                switch (dataType) {
                  case 2: skip += 2; break;
                  case 3: skip += 4; break;
                  case 4: skip += 2; break;
                  case 5: skip += 4; break;
                  case 6: skip += 4; break;
                  case 7: skip += 8; break;
                  case 8: skip += 1; break;
                  case 9: skip += 1; break;
                }
              }
            }
            in.read(temp);
            skip *= DataTools.bytesToInt(temp, !littleEndian);
            in.skipBytes(skip);
          }
        }
      }
      else if (type == 20) {
        in.skipBytes(2);
        in.read(temp);
        parseTags(DataTools.bytesToInt(temp, !littleEndian), labelString);
      }
    }
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new GatanReader().testRead(args);
  }

}
