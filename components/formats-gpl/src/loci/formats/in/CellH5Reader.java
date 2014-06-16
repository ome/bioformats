/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
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
import java.util.Vector;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
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
import loci.formats.services.NetCDFServiceImpl;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.PositiveFloat;
import ucar.ma2.Array;
import java.util.Arrays;

/**
 * Reader for CellH5 (HDF) files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/CellH5Reader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/CellH5Reader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class CellH5Reader extends FormatReader {

  // -- Constants --

  public static final String HDF_MAGIC_STRING = "HDF";

  private static final String[] DELIMITERS = {" ", "-", "."};

  // -- Fields --

  private double pixelSizeX, pixelSizeY, pixelSizeZ;
  private double minX, minY, minZ, maxX, maxY, maxZ;
  private int seriesCount;
  private NetCDFService netcdf;

  // channel parameters
  private Vector<String> emWave, exWave, channelMin, channelMax;
  private Vector<String> gain, pinhole, channelName, microscopyMode;
  private Vector<double[]> colors;
  private int lastChannel = 0;
  
  
  private String pathToImageData = "/" ;
  private String pathToImageSegmentation = "/" ;

  // -- Constructor --

  /** Constructs a new Imaris HDF reader. */
  public CellH5Reader() {
    super("CellH5 (HDF)", "ch5");
    suffixSufficient = true;
    domains = new String[] {FormatTools.HCS_DOMAIN};
    colors = new Vector<double[]>();
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return getSizeY();
  }
  
  public boolean isThisType(String name, boolean open) {
	  String[] tokens = name.split("\\.(?=[^\\.]+$)");
	  if (tokens[1].equals("ch5")) return true;
	  return false;
	  
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
	  final int blockLen = 8;
      if (!FormatTools.validStream(stream, blockLen, false)) return false;
      return stream.readString(blockLen).indexOf(HDF_MAGIC_STRING) >= 0;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    if (getPixelType() != FormatTools.UINT8 || !isIndexed()) return null;
        
    if (lastChannel < 0) {
        return null;
      }

      byte[][] lut = new byte[3][256];
      for (int i=0; i<256; i++) {
        switch (lastChannel) {
          case 0:
            // red
            lut[0][i] = (byte) (i & 0xff);
            break;
          case 1:
            // green
            lut[1][i] = (byte) (i & 0xff);
            break;
          case 2:
            // blue
            lut[2][i] = (byte) (i & 0xff);
            break;
          case 3:
            // cyan
            lut[1][i] = (byte) (i & 0xff);
            lut[2][i] = (byte) (i & 0xff);
            break;
          case 4:
            // magenta
            lut[0][i] = (byte) (i & 0xff);
            lut[2][i] = (byte) (i & 0xff);
            break;
          case 5:
            // yellow
            lut[0][i] = (byte) (i & 0xff);
            lut[1][i] = (byte) (i & 0xff);
            break;
          default:
            // gray
            lut[0][i] = (byte) (i & 0xff);
            lut[1][i] = (byte) (i & 0xff);
            lut[2][i] = (byte) (i & 0xff);
        }
      }
      return lut;
  }

  /* @see loci.formats.IFormatReaderget16BitLookupTable() */
  public short[][] get16BitLookupTable() {
	    FormatTools.assertId(currentId, true, 1);
	    if (getPixelType() != FormatTools.UINT16 || !isIndexed()) return null;

	    if (lastChannel < 0 || lastChannel >= 9) {
	      return null;
	    }

	    short[][] lut = new short[3][65536];
	    for (int i=0; i<65536; i++) {
	      switch (lastChannel) {
	        case 0:
	          // red
	          lut[0][i] = (short) (i & 0xff);
	          break;
	        case 1:
	          // green
	          lut[1][i] = (short) (i & 0xff);
	          break;
	        case 2:
	          // blue
	          lut[2][i] = (short) (i & 0xff);
	          break;
	        case 3:
	          // cyan
	          lut[1][i] = (short) (i & 0xff);
	          lut[2][i] = (short) (i & 0xff);
	          break;
	        case 4:
	          // magenta
	          lut[0][i] = (short) (i & 0xff);
	          lut[2][i] = (short) (i & 0xff);
	          break;
	        case 5:
	          // yellow
	          lut[0][i] = (short) (i & 0xff);
	          lut[1][i] = (short) (i & 0xff);
	          break;
	        default:
	          // gray
	          lut[0][i] = (short) (i & 0xff);
	          lut[1][i] = (short) (i & 0xff);
	          lut[2][i] = (short) (i & 0xff);
	      }
	    }
	    return lut;
	  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    lastChannel = getZCTCoords(no)[1];

    // pixel data is stored in XYZ blocks

    Object image = getImageData(no, y, h);

    boolean big = !isLittleEndian();
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    for (int row=0; row<h; row++) {
      int base = row * w * bpp;
      if (image instanceof byte[][]) {
        byte[][] data = (byte[][]) image;
        byte[] rowData = data[row];
        System.arraycopy(rowData, x, buf, row*w, w);
      }
      else if (image instanceof short[][]) {
        short[][] data = (short[][]) image;
        short[] rowData = data[row];
        for (int i=0; i<w; i++) {
          DataTools.unpackBytes(rowData[i + x], buf, base + 2*i, 2, big);
        }
      }
      else if (image instanceof int[][]) {
        int[][] data = (int[][]) image;
        int[] rowData = data[row];
        for (int i=0; i<w; i++) {
          DataTools.unpackBytes(rowData[i + x], buf, base + i*4, 4, big);
        }
      }
      else if (image instanceof float[][]) {
        float[][] data = (float[][]) image;
        float[] rowData = data[row];
        for (int i=0; i<w; i++) {
          int v = Float.floatToIntBits(rowData[i + x]);
          DataTools.unpackBytes(v, buf, base + i*4, 4, big);
        }
      }
      else if (image instanceof double[][]) {
        double[][] data = (double[][]) image;
        double[] rowData = data[row];
        for (int i=0; i<w; i++) {
          long v = Double.doubleToLongBits(rowData[i + x]);
          DataTools.unpackBytes(v, buf, base + i * 8, 8, big);
        }
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      seriesCount = 0;
      pixelSizeX = pixelSizeY = pixelSizeZ = 0;

      if (netcdf != null) netcdf.close();
      netcdf = null;

      lastChannel = 0;
    }
  }

  // -- Internal FormatReader API methods --

  protected void initFile(String id) throws FormatException, IOException {
	    super.initFile(id);

	    try {
	      ServiceFactory factory = new ServiceFactory();
	      netcdf = factory.getInstance(NetCDFService.class);
	      netcdf.setFile(id);
	    }
	    catch (DependencyException e) {
	      throw new MissingLibraryException(NetCDFServiceImpl.NO_NETCDF_MSG, e);
	    }

	    pixelSizeX = pixelSizeY = pixelSizeZ = 1;

	    // read experiment structure
	    CoreMetadata ms0 = core.get(0);


	    parseStructure();
	    setSeries(0);
	  }

  // -- Helper methods --

  private Object getImageData(int no, int y, int height)
    throws FormatException
  {
    int[] zct = getZCTCoords(no);
    int zslice = zct[0];
    int channel = zct[1];
    int time = zct[2];
    
    String path = pathToImageData;
    if (series == 1) {
    	path = pathToImageSegmentation;
    }
    Object image = null;

    // the width and height cannot be 1, because then netCDF will give us a
    // singleton instead of an array
    if (height == 1) {
      height++;
    }

    int[] dimensions = new int[] {1, 1, 1, height, getSizeX()};
    int[] indices = new int[] {channel, time, zslice, y, 0};
    try {
      image = netcdf.getArray(path, indices, dimensions);
    }
    catch (ServiceException e) {
      throw new FormatException(e);
    }
    return image;
  }
  
  private String getFirstSubGroup(String pathInFile) {
	 String result = netcdf.getSubGroups(pathInFile).get(0);
//	 if (result.startsWith("N") && result.substring(1).matches("\\d*")) {
//		 result = result.substring(1);
//	 }
	 return result;
  }

  private void parseStructure() throws FormatException {
	 seriesCount = 1;
	 pathToImageData = "/sample/";
	 
	 String sample = getFirstSubGroup(pathToImageData).toString();
	 pathToImageData += sample + "/plate/";
	 
	 String plate = getFirstSubGroup(pathToImageData).toString();
	 pathToImageData += plate + "/experiment/";
	 
	 String well = getFirstSubGroup(pathToImageData).toString();
	 pathToImageData += well + "/position/";
	 
	 String position = getFirstSubGroup(pathToImageData).toString();
	 pathToImageData += position + "/image/";
	 pathToImageSegmentation = pathToImageData + "region";
	 pathToImageData += "channel";
	 
	 
	 LOGGER.info(String.format("Found sample '%s', plate: '%s', well: '%s' and position: '%s' in path: '%s'", sample, plate, well, position, pathToImageData));
	 LOGGER.info(String.format("Found segmentation '%s', plate: '%s', well: '%s' and position: '%s' in path: '%s'", sample, plate, well, position, pathToImageSegmentation));
	 
	 int[] ctzyx = netcdf.getShape(pathToImageData);
	 
	 for (int k=0;k<1;k++) {
		 core.get(k).sizeC = ctzyx[0];
		 core.get(k).sizeT = ctzyx[1];
		 core.get(k).sizeZ = ctzyx[2];
		 core.get(k).sizeY = ctzyx[3];
		 core.get(k).sizeX = ctzyx[4];
		 
		 core.get(k).resolutionCount = 1;
		 core.get(k).thumbnail = false;
		 core.get(k).imageCount = getSizeC() * getSizeT() * getSizeZ();
		 core.get(k).dimensionOrder = "XYZCT";
	
	     core.get(k).rgb = false;
	     core.get(k).thumbSizeX = 128;
	     core.get(k).thumbSizeY = 128;
	     core.get(k).orderCertain = false;
	     core.get(k).littleEndian = true;
	     core.get(k).interleaved = false;
	     core.get(k).indexed = true;
	 }
     int type = -1;
     Object pix = getImageData(0, 0, 1);
     if (pix instanceof byte[][]) type = FormatTools.UINT8;
     else if (pix instanceof short[][]) type = FormatTools.UINT16;
     else if (pix instanceof int[][]) type = FormatTools.UINT32;
     else if (pix instanceof float[][]) type = FormatTools.FLOAT;
     else if (pix instanceof double[][]) type = FormatTools.DOUBLE;
     else {
       throw new FormatException("Unknown pixel type: " + pix);
     }
     core.get(0).pixelType = type;
     /*core[1].pixelType = FormatTools.UINT16;*/
     
	 
	 MetadataStore store = makeFilterMetadata();
     MetadataTools.populatePixels(store, this);

     String imageName = new Location(getCurrentFile()).getName();
     store.setImageName(imageName, 0);
    
  }
  
//  public static void main (String[] args) throws FormatException, IOException {
//	    System.out.println("start!");
//		CellH5Reader c = new CellH5Reader(); 
//		c.setId("C:/Users/sommerc/data/cellh5_demo/0013.ch5");
//		byte[] b0 = new byte[1392 * 1040 * 8];
//		byte[] b = c.openBytes(0, b0, 0, 0, 1392, 1040);
//		System.out.println("Asdf");
//		c.close();
//		System.out.println("done!");
//  }

}
