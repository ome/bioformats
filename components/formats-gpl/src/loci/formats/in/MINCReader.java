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
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
import loci.formats.meta.MetadataStore;
import loci.formats.services.NetCDFService;

import ome.units.quantity.Length;

/**
 * MINCReader is the file format reader for MINC MRI files.
 */
public class MINCReader extends FormatReader {

  // -- Fields --

  private NetCDFService netcdf;
  private byte[][][] pixelData;
  private boolean isMINC2 = false;

  // -- Constructor --

  /** Constructs a new MINC reader. */
  public MINCReader() {
    super("MINC MRI", "mnc");
    domains = new String[] {FormatTools.MEDICAL_DOMAIN};
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int bpp = FormatTools.getBytesPerPixel(getPixelType());

    if (no < pixelData.length) {
      for (int row=0; row<h; row++) {
        int srcRow = getSizeY() - (row + y) - 1;
        if (srcRow < pixelData[no].length &&
          x + w <= pixelData[no][srcRow].length)
        {
          System.arraycopy(pixelData[no][srcRow], x * bpp, buf,
            row * w * bpp, w * bpp);
        }
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (netcdf != null) netcdf.close();
      pixelData = null;
      isMINC2 = false;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    try {
      ServiceFactory factory = new ServiceFactory();
      netcdf = factory.getInstance(NetCDFService.class);
      netcdf.setFile(id);
    }
    catch (DependencyException e) {
      throw new MissingLibraryException(e);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      Vector<String> variableList = netcdf.getVariableList();

      for (String variable : variableList) {
        Hashtable<String, Object> attributes =
          netcdf.getVariableAttributes(variable);
        String[] keys = attributes.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        for (String key : keys) {
          if (attributes.get(key) instanceof Object[]) {
            final StringBuilder sb = new StringBuilder();
            Object[] o = (Object[]) attributes.get(key);
            for (Object q : o) {
              sb.append(q.toString());
            }
            addGlobalMeta(variable + " " + key, sb.toString());
          }
          else {
            addGlobalMeta(variable + " " + key, attributes.get(key));
          }
        }
      }
    }

    CoreMetadata m = core.get(0);

    try {
      Object pixels = netcdf.getVariableValue("/image");
      if (pixels == null) {
        pixels = netcdf.getVariableValue("/minc-2.0/image/0/image");
        isMINC2 = true;
      }
      m.littleEndian = isMINC2;
      
      if (pixels instanceof byte[][][]) {
        m.pixelType = FormatTools.UINT8;
        pixelData = (byte[][][]) pixels;
      }
      else if (pixels instanceof byte[][][][]) {
        byte[][][][] actualPixels = (byte[][][][]) pixels;
        m.pixelType = FormatTools.UINT8;

        pixelData = new byte[actualPixels.length * actualPixels[0].length][][];
        int nextPlane = 0;
        for (int t=0; t<actualPixels.length; t++) {
          for (int z=0; z<actualPixels[t].length; z++) {
            pixelData[nextPlane++] = actualPixels[t][z];
          }
        }
      }
      else if (pixels instanceof short[][][]) {
        m.pixelType = FormatTools.UINT16;

        short[][][] s = (short[][][]) pixels;
        pixelData = new byte[s.length][][];
        for (int i=0; i<s.length; i++) {
          pixelData[i] = new byte[s[i].length][];
          for (int j=0; j<s[i].length; j++) {
            pixelData[i][j] =
              DataTools.shortsToBytes(s[i][j], isLittleEndian());
          }
        }
      }
      else if (pixels instanceof int[][][]) {
        m.pixelType = FormatTools.UINT32;

        int[][][] s = (int[][][]) pixels;
        pixelData = new byte[s.length][][];
        for (int i=0; i<s.length; i++) {
          pixelData[i] = new byte[s[i].length][];
          for (int j=0; j<s[i].length; j++) {
            pixelData[i][j] = DataTools.intsToBytes(s[i][j], isLittleEndian());
          }
        }
      }
      else if (pixels instanceof float[][][]) {
        m.pixelType = FormatTools.FLOAT;

        float[][][] s = (float[][][]) pixels;
        pixelData = new byte[s.length][][];
        for (int i=0; i<s.length; i++) {
          pixelData[i] = new byte[s[i].length][];
          for (int j=0; j<s[i].length; j++) {
            pixelData[i][j] =
              DataTools.floatsToBytes(s[i][j], isLittleEndian());
          }
        }
      }
      else if (pixels instanceof double[][][]) {
        m.pixelType = FormatTools.DOUBLE;

        double[][][] s = (double[][][]) pixels;
        pixelData = new byte[s.length][][];
        for (int i=0; i<s.length; i++) {
          pixelData[i] = new byte[s[i].length][];
          for (int j=0; j<s[i].length; j++) {
            pixelData[i][j] =
              DataTools.doublesToBytes(s[i][j], isLittleEndian());
          }
        }
      }
    }
    catch (ServiceException e) {
      throw new FormatException(e);
    }

    Double physicalX = null;
    Double physicalY = null;
    Double physicalZ = null;

    if (isMINC2) {
      Hashtable<String, Object> attrs =
        netcdf.getVariableAttributes("/minc-2.0/dimensions/xspace");
      m.sizeX = Integer.parseInt(attrs.get("length").toString());
      physicalX = getStepSize(attrs);

      attrs = netcdf.getVariableAttributes("/minc-2.0/dimensions/yspace");
      m.sizeY = Integer.parseInt(attrs.get("length").toString());
      physicalY = getStepSize(attrs);

      attrs = netcdf.getVariableAttributes("/minc-2.0/dimensions/zspace");
      m.sizeZ = Integer.parseInt(attrs.get("length").toString());
      physicalZ = getStepSize(attrs);
    }
    else {
      m.sizeX = netcdf.getDimension("/xspace");
      m.sizeY = netcdf.getDimension("/yspace");
      m.sizeZ = netcdf.getDimension("/zspace");

      physicalX = getStepSize(netcdf.getVariableAttributes("/xspace"));
      physicalY = getStepSize(netcdf.getVariableAttributes("/yspace"));
      physicalZ = getStepSize(netcdf.getVariableAttributes("/zspace"));
    }

    try {
      m.sizeT = netcdf.getDimension("/time");
    }
    catch (NullPointerException e) {
      m.sizeT = 1;
    }
    m.sizeC = 1;
    m.imageCount = getSizeZ() * getSizeT() * getSizeC();
    m.rgb = false;
    m.indexed = false;
    m.dimensionOrder = "XYZCT";

    String history = null;
    if (isMINC2) {
      history = netcdf.getAttributeValue("/minc-2.0/ident");
    }
    else {
      history = netcdf.getAttributeValue("/history");
    }

    addGlobalMeta("Comment", history);

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setImageDescription(history, 0);

      Length sizeX = FormatTools.getPhysicalSizeX(physicalX);
      Length sizeY = FormatTools.getPhysicalSizeY(physicalY);
      Length sizeZ = FormatTools.getPhysicalSizeZ(physicalZ);
      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }
      if (sizeZ != null) {
        store.setPixelsPhysicalSizeZ(sizeZ, 0);
      }
    }
  }

  private Double getStepSize(Hashtable<String, Object> attrs) {
    Double stepSize = Double.parseDouble(attrs.get("step").toString());
    String units = attrs.get("units").toString();
    if (units.equals("mm")) {
      stepSize *= 1000.0;
    }
    return stepSize;
  }

}
