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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.units.UNITS;
import ome.units.quantity.Length;

/**
 * VarianFDFReader is the file format reader for Varian FDF files.
 */
public class VarianFDFReader extends FormatReader {

  // -- Fields --

  private final List<String> files = new ArrayList<String>();
  private long[] pixelOffsets;
  private double pixelSizeX;
  private double pixelSizeY;
  private double pixelSizeZ;
  private Length originX;
  private Length originY;
  private Length originZ;
  private String[] units;

  // -- Constructor --

  /** Constructs a new Varian FDF reader. */
  public VarianFDFReader() {
    super("Varian FDF", "fdf");
    domains = new String[] {FormatTools.MEDICAL_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return false;
  }

  /**
   * @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean)
   */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    if (noPixels) return null;
    if (files.isEmpty()) return new String[] {currentId};
    return files.toArray(new String[files.size()]);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (files.size() > 1) {
      in = new RandomAccessInputStream(files.get(no));
      in.order(isLittleEndian());
    }
    in.seek(pixelOffsets[no]);
    readPlane(in, x, getSizeY() - y - h, w, h, buf);

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    byte[] rowBuf = new byte[w * bpp];
    for (int row=0; row<h/2; row++) {
      int src = row * rowBuf.length;
      int dest = (h - row - 1) * rowBuf.length;
      System.arraycopy(buf, src, rowBuf, 0, rowBuf.length);
      System.arraycopy(buf, dest, buf, src, rowBuf.length);
      System.arraycopy(rowBuf, 0, buf, dest, rowBuf.length);
    }
    if (files.size() > 1) {
      in.close();
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelOffsets = null;
      files.clear();
      pixelSizeX = 0d;
      pixelSizeY = 0d;
      pixelSizeZ = 0d;
      originX = null;
      originY = null;
      originZ = null;
      units = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    CoreMetadata m = core.get(0);

    m.sizeZ = 1;
    m.sizeC = 1;
    m.sizeT = 1;

    parseFDF(id);

    m.imageCount = getSizeZ() * getSizeC() * getSizeT();
    m.dimensionOrder = "XYTZC";

    if (files.size() > getImageCount()) {
      int rem = files.size() / getImageCount();
      m.sizeT *= rem;
      m.imageCount = getSizeZ() * getSizeC() * getSizeT();
    }

    pixelOffsets = new long[getImageCount()];
    int planeSize = FormatTools.getPlaneSize(this);
    for (int i=0; i<pixelOffsets.length; i++) {
      if (files.size() > 1) {
        in.close();
        in = new RandomAccessInputStream(files.get(i));
        pixelOffsets[i] = in.length() - planeSize;
      }
      else {
        pixelOffsets[i] = in.length() - (planeSize * (getImageCount() - i));
      }
    }

    boolean minMetadata =
      getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, !minMetadata);

    if (!minMetadata) {
      Length sizeX = FormatTools.getPhysicalSizeX(pixelSizeX);
      Length sizeY = FormatTools.getPhysicalSizeY(pixelSizeY);
      Length sizeZ = FormatTools.getPhysicalSizeZ(pixelSizeZ);
      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }
      if (sizeZ != null) {
        store.setPixelsPhysicalSizeZ(sizeZ, 0);
      }

      for (int i=0; i<getImageCount(); i++) {
        store.setPlanePositionX(originX, 0, i);
        store.setPlanePositionY(originY, 0, i);
        store.setPlanePositionZ(originZ, 0, i);
      }
    }
  }

  // -- Helper methods --

  private void parseFDF(String file) throws FormatException, IOException {
    in = new RandomAccessInputStream(file);
    CoreMetadata m = core.get(0);
    boolean storedFloats = false;
    boolean multifile = false;

    String data = in.readString(Character.toString((char) 0x0c));
    String[] lines = data.split("\n");

    for (String line : lines) {
      line = line.trim();
      if (line.length() == 0) break;
      if (line.startsWith("#")) continue;

      int space = line.indexOf(' ');
      int eq = line.indexOf('=');
      String type = line.substring(0, space).trim();
      String var = line.substring(space, eq).trim();
      String value = line.substring(eq + 1, line.indexOf(';')).trim();

      if (var.equals("*storage")) {
        storedFloats = value.equals("\"float\"");
      }
      if (var.equals("bits")) {
        m.bitsPerPixel = Integer.parseInt(value);
        if (value.equals("8")) {
          m.pixelType = FormatTools.UINT8;
        }
        else if (value.equals("16")) {
          m.pixelType = FormatTools.UINT16;
        }
        else if (value.equals("32")) {
          if (storedFloats) {
            m.pixelType = FormatTools.FLOAT;
          }
          else m.pixelType = FormatTools.UINT32;
        }
        else throw new FormatException("Unsupported bits: " + value);
      }
      else if (var.equals("matrix[]")) {
        String[] values = parseArray(value);
        m.sizeX = (int) Double.parseDouble(values[0]);
        m.sizeY = (int) Double.parseDouble(values[1]);
        if (values.length > 2) {
          m.sizeZ = (int) Double.parseDouble(values[2]);
        }
      }
      else if (var.equals("slices")) {
        m.sizeZ = Integer.parseInt(value);
        multifile = true;
      }
      else if (var.equals("echoes")) {
        m.sizeT = Integer.parseInt(value);
        multifile = true;
      }
      else if (var.equals("span[]")) {
        String[] values = parseArray(value);
        if (values.length > 0) {
          pixelSizeX = computePhysicalSize(getSizeX(), values[0], units[0]);
        }
        if (values.length > 1) {
          pixelSizeY = computePhysicalSize(getSizeY(), values[1], units[1]);
        }
        if (values.length > 2) {
          pixelSizeZ = computePhysicalSize(getSizeZ(), values[2], units[2]);
        }
      }
      else if (var.equals("origin[]")) {
        String[] values = parseArray(value);
        if (values.length > 0) {
          final double size = computePhysicalSize(1, values[0], units[0]);
          originX = new Length(size, UNITS.REFERENCEFRAME);
          addGlobalMeta("X position for position #1", originX);
        }
        if (values.length > 1) {
          final double size = computePhysicalSize(1, values[1], units[1]);
          originY = new Length(size, UNITS.REFERENCEFRAME);
          addGlobalMeta("Y position for position #1", originY);
        }
        if (values.length > 2) {
          final double size = computePhysicalSize(1, values[2], units[2]);
          originZ = new Length(size, UNITS.REFERENCEFRAME);
          addGlobalMeta("Z position for position #1", originZ);
        }
      }
      else if (var.equals("*abscissa[]")) {
        units = parseArray(value);
      }
      else if (var.equals("bigendian")) {
        m.littleEndian = value.equals("0");
        in.order(isLittleEndian());
      }

      addGlobalMeta(var, value);
    }

    if (multifile && files.isEmpty()) {
      Location thisFile = new Location(file).getAbsoluteFile();
      Location parent = thisFile.getParentFile();
      String[] list = parent.list(true);
      Arrays.sort(list);
      for (String f : list) {
        if (checkSuffix(f, "fdf") && f.length() == thisFile.getName().length())
        {
          files.add(new Location(parent, f).getAbsolutePath());
        }
      }
    }
  }

  /** Split a String that represents an array into individual elements.  */
  private String[] parseArray(String value) {
    value = value.replaceAll("[{}]", "");
    String[] values = value.split(",");
    for (int i=0; i<values.length; i++) {
      values[i] = values[i].replaceAll("\"", "");
      values[i] = values[i].trim();
    }
    return values;
  }

  /**
   * Return the physical size of a pixel, in microns, along an arbitrary axis
   * based on the supplied physical and pixel lengths of the axis.
   *
   * @param length the length, in pixels, of the axis
   * @param physicalLength the length, in physical units of the axis
   * @param unit the units for the physical length
   * @return the size, in microns, of a pixel
   */
  private double computePhysicalSize(
    int length, String physicalLength, String unit)
  {
    double size = Double.parseDouble(physicalLength) / length;
    if (unit.equals("cm")) {
      size *= 1000;
    }
    return size;
  }

}
