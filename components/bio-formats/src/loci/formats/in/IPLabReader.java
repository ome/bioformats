//
// IPLabReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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
import loci.common.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * IPLabReader is the file format reader for IPLab (.IPL) files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/IPLabReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/IPLabReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class IPLabReader extends FormatReader {

  // -- Fields --

  /** Bytes per pixel. */
  private int bps;

  /** Total number of pixel bytes. */
  private int dataSize;

  // -- Constructor --

  /** Constructs a new IPLab reader. */
  public IPLabReader() {
    super("IPLab", "ipl");
    blockCheckLen = 12;
    suffixNecessary = false; // allow extensionless IPLab files
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    if (!FormatTools.validStream(stream, blockCheckLen, false)) return false;
    String s = stream.readString(4);
    boolean big = s.equals("iiii");
    boolean little = s.equals("mmmm");
    if (!big && !little) return false;
    stream.order(little);
    int size = stream.readInt();
    if (size != 4) return false; // first block size should be 4
    int version = stream.readInt();
    return version >= 0x100e;
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

    int numPixels = getSizeX() * getSizeY() * getSizeC() * bps;
    in.seek(numPixels * (no / getSizeC()) + 44);

    readPlane(in, x, y, w, h, buf);
    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    bps = dataSize = 0;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("IPLabReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Populating metadata");

    core[0].littleEndian = in.readString(4).equals("iiii");

    in.order(isLittleEndian());

    in.skipBytes(12);

    // read axis sizes from header

    dataSize = in.readInt() - 28;
    core[0].sizeX = in.readInt();
    core[0].sizeY = in.readInt();
    core[0].sizeC = in.readInt();
    core[0].sizeZ = in.readInt();
    core[0].sizeT = in.readInt();
    int filePixelType = in.readInt();

    core[0].imageCount = getSizeZ() * getSizeT();

    addMeta("Width", getSizeX());
    addMeta("Height", getSizeY());
    addMeta("Channels", getSizeC());
    addMeta("ZDepth", getSizeZ());
    addMeta("TDepth", getSizeT());

    String ptype;
    switch (filePixelType) {
      case 0:
        ptype = "8 bit unsigned";
        core[0].pixelType = FormatTools.UINT8;
        break;
      case 1:
        ptype = "16 bit signed short";
        core[0].pixelType = FormatTools.INT16;
        break;
      case 2:
        ptype = "16 bit unsigned short";
        core[0].pixelType = FormatTools.UINT16;
        break;
      case 3:
        ptype = "32 bit signed long";
        core[0].pixelType = FormatTools.INT32;
        break;
      case 4:
        ptype = "32 bit single-precision float";
        core[0].pixelType = FormatTools.FLOAT;
        break;
      case 5:
        ptype = "Color24";
        core[0].pixelType = FormatTools.UINT32;
        break;
      case 6:
        ptype = "Color48";
        core[0].pixelType = FormatTools.UINT16;
        break;
      case 10:
        ptype = "64 bit double-precision float";
        core[0].pixelType = FormatTools.DOUBLE;
        break;
      default:
        ptype = "reserved"; // for values 7-9
    }

    bps = FormatTools.getBytesPerPixel(getPixelType());

    addMeta("PixelType", ptype);
    in.skipBytes(dataSize);

    core[0].dimensionOrder = "XY";
    if (getSizeC() > 1) core[0].dimensionOrder += "CZT";
    else core[0].dimensionOrder += "ZTC";

    core[0].rgb = getSizeC() > 1;
    core[0].interleaved = false;
    core[0].indexed = false;
    core[0].falseColor = false;
    core[0].metadataComplete = true;

    // The metadata store we're working with.
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);
    MetadataTools.setDefaultCreationDate(store, id, 0);
    MetadataTools.populatePixels(store, this, true);

    status("Reading tags");

    byte[] tagBytes = new byte[4];
    in.read(tagBytes);
    String tag = new String(tagBytes);
    while (!tag.equals("fini") && in.getFilePointer() < in.length() - 4) {
      int size = in.readInt();
      if (tag.equals("clut")) {
        // read in Color Lookup Table
        if (size == 8) {
          // indexed lookup table
          in.skipBytes(4);
          int type = in.readInt();

          String[] types = new String[] {
            "monochrome", "reverse monochrome", "BGR", "classify", "rainbow",
            "red", "green", "blue", "cyan", "magenta", "yellow",
            "saturated pixels"
          };
          String clutType = (type >= 0 && type < types.length) ? types[type] :
            "unknown";
          addMeta("LUT type", clutType);
        }
        else {
          // explicitly defined lookup table
          // length is 772
          in.skipBytes(772);
        }
      }
      else if (tag.equals("norm")) {
        // read in normalization information

        if (size != (44 * getSizeC())) {
          throw new FormatException("Bad normalization settings");
        }

        String[] types = new String[] {
          "user", "plane", "sequence", "saturated plane",
          "saturated sequence", "ROI"
        };

        for (int i=0; i<getSizeC(); i++) {
          int source = in.readInt();

          String sourceType = (source >= 0 && source < types.length) ?
            types[source] : "user";
          addMeta("NormalizationSource" + i, sourceType);

          double min = in.readDouble();
          double max = in.readDouble();
          double gamma = in.readDouble();
          double black = in.readDouble();
          double white = in.readDouble();

          addMeta("NormalizationMin" + i, min);
          addMeta("NormalizationMax" + i, max);
          addMeta("NormalizationGamma" + i, gamma);
          addMeta("NormalizationBlack" + i, black);
          addMeta("NormalizationWhite" + i, white);

          // CTR CHECK
          //store.setDisplayChannel(new Integer(core[0].sizeC),
          //  new Double(black), new Double(white), new Float(gamma), null);
        }
      }
      else if (tag.equals("head")) {
        // read in header labels

        for (int i=0; i<size / 22; i++) {
          int num = in.readShort();
          addMeta("Header" + num, in.readString(20));
        }
      }
      else if (tag.equals("mmrc")) {
        in.skipBytes(size);
      }
      else if (tag.equals("roi ")) {
        // read in ROI information

        in.skipBytes(4);
        int roiLeft = in.readInt();
        int roiTop = in.readInt();
        int roiRight = in.readInt();
        int roiBottom = in.readInt();
        int numRoiPts = in.readInt();

        Integer x0 = new Integer(roiLeft);
        Integer x1 = new Integer(roiRight);
        Integer y0 = new Integer(roiBottom);
        Integer y1 = new Integer(roiTop);
        // TODO
        //store.setDisplayROIX0(x0, 0, 0);
        //store.setDisplayROIY0(y0, 0, 0);
        //store.setDisplayROIX1(x1, 0, 0);
        //store.setDisplayROIY1(y1, 0, 0);

        in.skipBytes(8 * numRoiPts);
      }
      else if (tag.equals("mask")) {
        // read in Segmentation Mask
        in.skipBytes(size);
      }
      else if (tag.equals("unit")) {
        // read in units

        for (int i=0; i<4; i++) {
          int xResStyle = in.readInt();
          float unitsPerPixel = in.readFloat();
          int xUnitName = in.readInt();

          addMeta("ResolutionStyle" + i, xResStyle);
          addMeta("UnitsPerPixel" + i, unitsPerPixel);

          switch (xUnitName) {
            case 2: // mm
              unitsPerPixel *= 1000;
              break;
            case 3: // cm
              unitsPerPixel *= 10000;
              break;
            case 4: // m
              unitsPerPixel *= 1000000;
              break;
            case 5: // inch
              unitsPerPixel *= 3937;
              break;
            case 6: //ft
              unitsPerPixel *= 47244;
              break;
          }

          if (i == 0) {
            Float pixelSize = new Float(unitsPerPixel);
            store.setDimensionsPhysicalSizeX(pixelSize, 0, 0);
            store.setDimensionsPhysicalSizeY(pixelSize, 0, 0);
          }

          addMeta("UnitName" + i, xUnitName);
        }
      }
      else if (tag.equals("view")) {
        // read in view
        in.skipBytes(size);
      }
      else if (tag.equals("plot")) {
        // read in plot
        // skipping this field for the moment
        in.skipBytes(size);
      }
      else if (tag.equals("note")) {
        // read in notes (image info)
        String descriptor = in.readString(64);
        String notes = in.readString(512);
        addMeta("Descriptor", descriptor);
        addMeta("Notes", notes);

        store.setImageDescription(notes, 0);
      }
      else if (tagBytes[0] == 0x1a && tagBytes[1] == (byte) 0xd9 &&
        tagBytes[2] == (byte) 0x8b && tagBytes[3] == (byte) 0xef)
      {
        int units = in.readInt();

        for (int i=0; i<getSizeT(); i++) {
          float timepoint = in.readFloat();
          // normalize to seconds
          switch (units) {
            case 0:
              // time stored in milliseconds
              timepoint /= 1000;
              break;
            case 2:
              // time stored in minutes
              timepoint *= 60;
              break;
            case 3:
              // time stored in hours
              timepoint *= 60 * 60;
              break;
          }

          addMeta("Timestamp " + i, timepoint);

          for (int c=0; c<getSizeC(); c++) {
            for (int z=0; z<getSizeZ(); z++) {
              int plane = getIndex(z, c, i);
              store.setPlaneTimingDeltaT(new Float(timepoint), 0, 0, plane);
            }
          }
        }
      }
      else in.skipBytes(size);

      if (in.getFilePointer() + 4 <= in.length()) {
        in.read(tagBytes);
        tag = new String(tagBytes);
      }
      else {
        tag = "fini";
      }
      if (in.getFilePointer() >= in.length() && !tag.equals("fini")) {
        tag = "fini";
      }
    }
  }

}
