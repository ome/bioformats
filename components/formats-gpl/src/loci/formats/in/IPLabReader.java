/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in;

import java.io.IOException;

import loci.common.Constants;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.xml.model.primitives.PositiveFloat;

import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * IPLabReader is the file format reader for IPLab (.IPL) files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class IPLabReader extends FormatReader {

  // -- Fields --

  private Double pixelSize, timeIncrement;

  // -- Constructor --

  /** Constructs a new IPLab reader. */
  public IPLabReader() {
    super("IPLab", "ipl");
    suffixNecessary = false; // allow extensionless IPLab files
    suffixSufficient = false;
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 12;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    String s = stream.readString(4);
    boolean little = s.equals("iiii");
    boolean big = s.equals("mmmm");
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
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int numPixels = FormatTools.getPlaneSize(this);
    in.seek(numPixels * no + 44);

    readPlane(in, x, y, w, h, buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelSize = timeIncrement = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    LOGGER.info("Populating metadata");

    CoreMetadata m = core.get(0);

    m.littleEndian = in.readString(4).equals("iiii");

    in.order(isLittleEndian());

    in.skipBytes(12);

    // read axis sizes from header

    int dataSize = in.readInt() - 28;
    m.sizeX = in.readInt();
    m.sizeY = in.readInt();
    m.sizeC = in.readInt();
    m.sizeZ = in.readInt();
    m.sizeT = in.readInt();
    int filePixelType = in.readInt();

    m.imageCount = getSizeZ() * getSizeT();

    String ptype;
    switch (filePixelType) {
      case 0:
        ptype = "8 bit unsigned";
        m.pixelType = FormatTools.UINT8;
        break;
      case 1:
        ptype = "16 bit signed short";
        m.pixelType = FormatTools.INT16;
        break;
      case 2:
        ptype = "16 bit unsigned short";
        m.pixelType = FormatTools.UINT16;
        break;
      case 3:
        ptype = "32 bit signed long";
        m.pixelType = FormatTools.INT32;
        break;
      case 4:
        ptype = "32 bit single-precision float";
        m.pixelType = FormatTools.FLOAT;
        break;
      case 5:
        ptype = "Color24";
        m.pixelType = FormatTools.UINT32;
        break;
      case 6:
        ptype = "Color48";
        m.pixelType = FormatTools.UINT16;
        break;
      case 10:
        ptype = "64 bit double-precision float";
        m.pixelType = FormatTools.DOUBLE;
        break;
      default:
        ptype = "reserved"; // for values 7-9
    }

    m.dimensionOrder = "XY";
    if (getSizeC() > 1) m.dimensionOrder += "CZT";
    else m.dimensionOrder += "ZTC";

    m.rgb = getSizeC() > 1;
    m.interleaved = false;
    m.indexed = false;
    m.falseColor = false;
    m.metadataComplete = true;

    addGlobalMeta("PixelType", ptype);
    addGlobalMeta("Width", getSizeX());
    addGlobalMeta("Height", getSizeY());
    addGlobalMeta("Channels", getSizeC());
    addGlobalMeta("ZDepth", getSizeZ());
    addGlobalMeta("TDepth", getSizeT());

    // The metadata store we're working with.
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      in.skipBytes(dataSize);
      parseTags(store);

      Length sizeX = FormatTools.getPhysicalSizeX(pixelSize);
      Length sizeY = FormatTools.getPhysicalSizeY(pixelSize);
      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }
      if (timeIncrement != null) {
        store.setPixelsTimeIncrement(new Time(timeIncrement, UNITS.SECOND), 0);
      }
    }
  }

  // -- Helper methods --

  private void parseTags(MetadataStore store)
    throws FormatException, IOException
  {
    LOGGER.info("Reading tags");

    byte[] tagBytes = new byte[4];
    in.read(tagBytes);
    String tag = new String(tagBytes, Constants.ENCODING);
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
          addGlobalMeta("LUT type", clutType);
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
          addGlobalMetaList("NormalizationSource", sourceType);

          double min = in.readDouble();
          double max = in.readDouble();
          double gamma = in.readDouble();
          double black = in.readDouble();
          double white = in.readDouble();

          addGlobalMetaList("NormalizationMin", min);
          addGlobalMetaList("NormalizationMax", max);
          addGlobalMetaList("NormalizationGamma", gamma);
          addGlobalMetaList("NormalizationBlack", black);
          addGlobalMetaList("NormalizationWhite", white);
        }
      }
      else if (tag.equals("head")) {
        // read in header labels

        for (int i=0; i<size / 22; i++) {
          int num = in.readShort();
          addGlobalMeta("Header" + num, in.readString(20));
        }
      }
      else if (tag.equals("mmrc")) {
        in.skipBytes(size);
      }
      else if (tag.equals("roi ") &&
        getMetadataOptions().getMetadataLevel() != MetadataLevel.NO_OVERLAYS)
      {
        // read in ROI information

        in.skipBytes(4);
        int roiLeft = in.readInt();
        int roiTop = in.readInt();
        int roiRight = in.readInt();
        int roiBottom = in.readInt();
        int numRoiPts = in.readInt();

        store.setRectangleID(MetadataTools.createLSID("Shape", 0, 0), 0, 0);
        store.setRectangleX(new Double(roiLeft), 0, 0);
        store.setRectangleY(new Double(roiTop), 0, 0);
        store.setRectangleWidth(new Double(roiRight - roiLeft), 0, 0);
        store.setRectangleHeight(new Double(roiBottom - roiTop), 0, 0);
        String roiID = MetadataTools.createLSID("ROI", 0, 0);
        store.setROIID(roiID, 0);
        store.setImageROIRef(roiID, 0, 0);

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

          addGlobalMetaList("ResolutionStyle", xResStyle);
          addGlobalMetaList("UnitsPerPixel", unitsPerPixel);

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

          if (i == 0) pixelSize = new Double(unitsPerPixel);

          addGlobalMetaList("UnitName", xUnitName);
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
        addGlobalMeta("Descriptor", descriptor);
        addGlobalMeta("Notes", notes);

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

          addGlobalMetaList("Timestamp", timepoint);

          for (int c=0; c<getSizeC(); c++) {
            for (int z=0; z<getSizeZ(); z++) {
              int plane = getIndex(z, c, i);
              store.setPlaneDeltaT(new Time(new Double(timepoint), UNITS.SECOND), 0, plane);
            }
          }
          if (i == 1) {
            timeIncrement = new Double(timepoint);
          }
        }
      }
      else in.skipBytes(size);

      if (in.getFilePointer() + 4 <= in.length()) {
        in.read(tagBytes);
        tag = new String(tagBytes, Constants.ENCODING);
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
