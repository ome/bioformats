/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.SubResolutionFormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.units.quantity.Length;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;


/**
 * Reader for Bitplane Imaris 5.5 (HDF) files.
 */
public class ImarisImsReader extends SubResolutionFormatReader {

  // -- Constants --

  public static final String HDF_MAGIC_STRING = "HDF";

  // -- Fields --

  private int pixelType;
  private double minX, minY, minZ, maxX, maxY, maxZ;
  private int seriesCount;
  private Object reader;

  // cache parameters
  private long maxCacheSize = 1024 * 1024 * 1024;
  private Memory buffer;
  private int[] blockSizeZPerResolution;
  private int[] currentMinZPerResolution;
  private int[] currentMaxZPerResolution;
  private int lastT = 0;
  private int lastRes = 0;

  // channel parameters
  private List<double[]> colors;
  private int lastChannel = 0;

  // -- Constructor --

  /** Constructs a new Imaris ims reader. */
  public ImarisImsReader() throws IOException {
    super("Bitplane Imaris 5.5 (HDF)", "ims");
    suffixSufficient = false;
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};

    // extract bpImarisReader library from jar
    String tmpDir = System.getProperty("jna.tmpdir");
    String libName = "bpImarisReader";
    String resourcePath = "/loci/formats/native/" + System.mapLibraryName(libName);
    if (tmpDir == null) {
      File extractedLib = Native.extractFromResourcePath(resourcePath);
      String jnaTempDir = extractedLib.getParent();
      File lib = new File(jnaTempDir + "/" + System.mapLibraryName(libName));
      boolean wasRenamed = extractedLib.renameTo(lib);
      System.setProperty("jna.library.path", jnaTempDir);
      System.setProperty("jna.tmpdir", jnaTempDir);
    }
    else {
      File lib = new File(tmpDir + "/" + System.mapLibraryName(libName));
      if (!lib.exists()) {
        File extractedLib = Native.extractFromResourcePath(resourcePath);
        boolean wasRenamed = extractedLib.renameTo(lib);
      }
      System.setProperty("jna.library.path", tmpDir);
    }
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    return core.get(series, core.size(series) - 1).sizeX;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    return core.get(series, core.size(series) - 1).sizeY;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).indexOf(HDF_MAGIC_STRING) >= 0;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    if (getPixelType() != FormatTools.UINT8 || !isIndexed()) return null;

    if (lastChannel < 0 || lastChannel >= colors.size()) {
      return null;
    }

    double[] color = colors.get(lastChannel);

    byte[][] lut = new byte[3][256];
    for (int c=0; c<lut.length; c++) {
      double max = color[c] * 255;
      for (int p=0; p<lut[c].length; p++) {
        lut[c][p] = (byte) ((p / 255.0) * max);
      }
    }
    return lut;
  }

  /* @see loci.formats.IFormatReaderget16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    if (getPixelType() != FormatTools.UINT16 || !isIndexed()) return null;

    if (lastChannel < 0 || lastChannel >= colors.size()) {
      return null;
    }

    double[] color = colors.get(lastChannel);

    short[][] lut = new short[3][65536];
    for (int c=0; c<lut.length; c++) {
      double max = color[c] * 65535;
      for (int p=0; p<lut[c].length; p++) {
        lut[c][p] = (short) ((p / 65535.0) * max);
      }
    }
    return lut;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    lastChannel = getZCTCoords(no)[1];

    int[] zct = getZCTCoords(no);
    int resolutionIndex = getCoreIndex();

    switch (pixelType) {
      case FormatTools.UINT8:
        if (w * h * blockSizeZPerResolution[resolutionIndex] * getSizeC() < maxCacheSize) {
          // update cache if needed
          if (zct[0] < currentMinZPerResolution[resolutionIndex] || zct[0] > currentMaxZPerResolution[resolutionIndex] || zct[2] != lastT || resolutionIndex != lastRes || buffer == null) {
            if (resolutionIndex != lastRes) {
              lastRes = resolutionIndex;
              lastT = zct[2];
            }

            if (zct[2] != lastT) {
              lastT = zct[2];
            }

            int blockNumber = zct[0] / blockSizeZPerResolution[resolutionIndex];
            jImarisReader.bp5D vBegin = new jImarisReader.bp5D(x, y, blockNumber * blockSizeZPerResolution[resolutionIndex], 0, zct[2]);
            jImarisReader.bp5D vEnd = new jImarisReader.bp5D(x + w, y + h, (blockNumber + 1) * blockSizeZPerResolution[resolutionIndex], getSizeC(), zct[2] + 1);
            int vNumberOfVoxels = w * h * blockSizeZPerResolution[resolutionIndex] * getSizeC();
            int vBufferSize = vNumberOfVoxels;
            buffer = new Memory(vBufferSize);
            ((jImarisReader.bpImageReaderUInt8) reader).ReadData(vBegin, vEnd, resolutionIndex, buffer);

            currentMinZPerResolution[resolutionIndex] = blockNumber * blockSizeZPerResolution[resolutionIndex];
            currentMaxZPerResolution[resolutionIndex] = currentMinZPerResolution[resolutionIndex] + blockSizeZPerResolution[resolutionIndex] - 1;
          }

          System.arraycopy(buffer.getByteArray(w * h * (zct[0] - currentMinZPerResolution[resolutionIndex]) + w * h * blockSizeZPerResolution[resolutionIndex] * zct[1], w * h), 0, buf, 0, w * h);
        }
        else {
          jImarisReader.bp5D vBegin = new jImarisReader.bp5D(x, y, zct[0], zct[1], zct[2]);
          jImarisReader.bp5D vEnd = new jImarisReader.bp5D(x + w, y + h, zct[0] + 1, zct[1] + 1, zct[2] + 1);
          int vNumberOfVoxels = w * h;
          int vBufferSize = vNumberOfVoxels;
          buffer = new Memory(vBufferSize);
          ((jImarisReader.bpImageReaderUInt8) reader).ReadData(vBegin, vEnd, resolutionIndex, buffer);

          System.arraycopy(buffer.getByteArray(0, vBufferSize), 0, buf, 0, w * h);
        }
        break;

      case FormatTools.UINT16:
        if (2 * w * h * blockSizeZPerResolution[resolutionIndex] * getSizeC() < maxCacheSize) {
          if (zct[0] < currentMinZPerResolution[resolutionIndex] || zct[0] > currentMaxZPerResolution[resolutionIndex] || zct[2] != lastT || resolutionIndex != lastRes || buffer == null) {
            if (resolutionIndex != lastRes) {
              lastRes = resolutionIndex;
              lastT = zct[2];
            }

            if (zct[2] != lastT) {
              lastT = zct[2];
            }

            int blockNumber = zct[0] / blockSizeZPerResolution[resolutionIndex];
            jImarisReader.bp5D vBegin = new jImarisReader.bp5D(x, y, blockNumber * blockSizeZPerResolution[resolutionIndex], 0, zct[2]);
            jImarisReader.bp5D vEnd = new jImarisReader.bp5D(x + w, y + h, (blockNumber + 1) * blockSizeZPerResolution[resolutionIndex], getSizeC(), zct[2] + 1);
            int vNumberOfVoxels = w * h * blockSizeZPerResolution[resolutionIndex] * getSizeC();
            int vBufferSize = vNumberOfVoxels * 2;
            buffer = new Memory(vBufferSize);
            ((jImarisReader.bpImageReaderUInt16) reader).ReadData(vBegin, vEnd, resolutionIndex, buffer);

            currentMinZPerResolution[resolutionIndex] = blockNumber * blockSizeZPerResolution[resolutionIndex];
            currentMaxZPerResolution[resolutionIndex] = currentMinZPerResolution[resolutionIndex] + blockSizeZPerResolution[resolutionIndex] - 1;
          }

          System.arraycopy(buffer.getByteArray(2 * w * h * (zct[0] - currentMinZPerResolution[resolutionIndex]) + 2 * w * h * blockSizeZPerResolution[resolutionIndex] * zct[1], 2 * w * h), 0, buf, 0, 2 * w * h);
        }
        else {
          jImarisReader.bp5D vBegin = new jImarisReader.bp5D(x, y, zct[0], zct[1], zct[2]);
          jImarisReader.bp5D vEnd = new jImarisReader.bp5D(x + w, y + h, zct[0] + 1, zct[1] + 1, zct[2] + 1);
          int vNumberOfVoxels = w * h;
          int vBufferSize = vNumberOfVoxels * 2;
          buffer = new Memory(vBufferSize);
          ((jImarisReader.bpImageReaderUInt16) reader).ReadData(vBegin, vEnd, resolutionIndex, buffer);

          System.arraycopy(buffer.getByteArray(0, vBufferSize), 0, buf, 0, 2 * w * h);
        }
        break;

      case FormatTools.UINT32:
        if (4 * w * h * blockSizeZPerResolution[resolutionIndex] * getSizeC() < maxCacheSize) {
          if (zct[0] < currentMinZPerResolution[resolutionIndex] || zct[0] > currentMaxZPerResolution[resolutionIndex] || zct[2] != lastT || resolutionIndex != lastRes || buffer == null) {
            if (resolutionIndex != lastRes) {
              lastRes = resolutionIndex;
              lastT = zct[2];
            }

            if (zct[2] != lastT) {
              lastT = zct[2];
            }

            int blockNumber = zct[0] / blockSizeZPerResolution[resolutionIndex];
            jImarisReader.bp5D vBegin = new jImarisReader.bp5D(x, y, blockNumber * blockSizeZPerResolution[resolutionIndex], 0, zct[2]);
            jImarisReader.bp5D vEnd = new jImarisReader.bp5D(x + w, y + h, (blockNumber + 1) * blockSizeZPerResolution[resolutionIndex], getSizeC(), zct[2] + 1);
            int vNumberOfVoxels = w * h * blockSizeZPerResolution[resolutionIndex] * getSizeC();
            int vBufferSize = vNumberOfVoxels * 4;
            buffer = new Memory(vBufferSize);
            ((jImarisReader.bpImageReaderUInt32) reader).ReadData(vBegin, vEnd, resolutionIndex, buffer);

            currentMinZPerResolution[resolutionIndex] = blockNumber * blockSizeZPerResolution[resolutionIndex];
            currentMaxZPerResolution[resolutionIndex] = currentMinZPerResolution[resolutionIndex] + blockSizeZPerResolution[resolutionIndex] - 1;
          }

          System.arraycopy(buffer.getByteArray(4 * w * h * (zct[0] - currentMinZPerResolution[resolutionIndex]) + 4 * w * h * blockSizeZPerResolution[resolutionIndex] * zct[1], 4 * w * h), 0, buf, 0, 4 * w * h);
        }
        else {
          jImarisReader.bp5D vBegin = new jImarisReader.bp5D(x, y, zct[0], zct[1], zct[2]);
          jImarisReader.bp5D vEnd = new jImarisReader.bp5D(x + w, y + h, zct[0] + 1, zct[1] + 1, zct[2] + 1);
          int vNumberOfVoxels = w * h;
          int vBufferSize = vNumberOfVoxels * 4;
          buffer = new Memory(vBufferSize);
          ((jImarisReader.bpImageReaderUInt32) reader).ReadData(vBegin, vEnd, resolutionIndex, buffer);

          System.arraycopy(buffer.getByteArray(0, vBufferSize), 0, buf, 0, 4 * w * h);
        }
        break;

      case FormatTools.FLOAT:
        if (4 * w * h * blockSizeZPerResolution[resolutionIndex] * getSizeC() < maxCacheSize) {
          if (zct[0] < currentMinZPerResolution[resolutionIndex] || zct[0] > currentMaxZPerResolution[resolutionIndex] || zct[2] != lastT || resolutionIndex != lastRes || buffer == null) {
            if (resolutionIndex != lastRes) {
              lastRes = resolutionIndex;
              lastT = zct[2];
            }

            if (zct[2] != lastT) {
              lastT = zct[2];
            }

            int blockNumber = zct[0] / blockSizeZPerResolution[resolutionIndex];
            jImarisReader.bp5D vBegin = new jImarisReader.bp5D(x, y, blockNumber * blockSizeZPerResolution[resolutionIndex], 0, zct[2]);
            jImarisReader.bp5D vEnd = new jImarisReader.bp5D(x + w, y + h, (blockNumber + 1) * blockSizeZPerResolution[resolutionIndex], getSizeC(), zct[2] + 1);
            int vNumberOfVoxels = w * h * blockSizeZPerResolution[resolutionIndex] * getSizeC();
            int vBufferSize = vNumberOfVoxels * 4;
            buffer = new Memory(vBufferSize);
            ((jImarisReader.bpImageReaderFloat) reader).ReadData(vBegin, vEnd, resolutionIndex, buffer);

            currentMinZPerResolution[resolutionIndex] = blockNumber * blockSizeZPerResolution[resolutionIndex];
            currentMaxZPerResolution[resolutionIndex] = currentMinZPerResolution[resolutionIndex] + blockSizeZPerResolution[resolutionIndex] - 1;
          }

          System.arraycopy(buffer.getByteArray(4 * w * h * (zct[0] - currentMinZPerResolution[resolutionIndex]) + 4 * w * h * blockSizeZPerResolution[resolutionIndex] * zct[1], 4 * w * h), 0, buf, 0, 4 * w * h);
        }
        else {
          jImarisReader.bp5D vBegin = new jImarisReader.bp5D(x, y, zct[0], zct[1], zct[2]);
          jImarisReader.bp5D vEnd = new jImarisReader.bp5D(x + w, y + h, zct[0] + 1, zct[1] + 1, zct[2] + 1);
          int vNumberOfVoxels = w * h;
          int vBufferSize = vNumberOfVoxels * 4;
          buffer = new Memory(vBufferSize);
          ((jImarisReader.bpImageReaderFloat) reader).ReadData(vBegin, vEnd, resolutionIndex, buffer);

          System.arraycopy(buffer.getByteArray(0, vBufferSize), 0, buf, 0, 4 * w * h);
        }
        break;
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      seriesCount = 0;
      minX = minY = minZ = maxX = maxY = maxZ = 0;
      colors = null;
      buffer = null;
      lastChannel = 0;
      lastT = 0;
      lastRes = 0;
      blockSizeZPerResolution = null;
      currentMinZPerResolution = null;
      currentMaxZPerResolution = null;
      if (pixelType == FormatTools.UINT8) {
        ((jImarisReader.bpImageReaderUInt8) reader).Destroy();
      }
      else if (pixelType == FormatTools.UINT16) {
        ((jImarisReader.bpImageReaderUInt16) reader).Destroy();
      }
      else if (pixelType == FormatTools.UINT32) {
        ((jImarisReader.bpImageReaderUInt32) reader).Destroy();
      }
      else if (pixelType == FormatTools.FLOAT) {
        ((jImarisReader.bpImageReaderFloat) reader).Destroy();
      }
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    pixelType = getImageDataType();
    jImarisReader.bpReaderTypesC_Options options = new jImarisReader.bpReaderTypesC_Options();
    if (pixelType == FormatTools.UINT8) {
      reader = new jImarisReader.bpImageReaderUInt8(getCurrentFile(), 0, options);
    }
    else if (pixelType == FormatTools.UINT16) {
      reader = new jImarisReader.bpImageReaderUInt16(getCurrentFile(), 0, options);
    }
    else if (pixelType == FormatTools.UINT32) {
      reader = new jImarisReader.bpImageReaderUInt32(getCurrentFile(), 0, options);
    }
    else if (pixelType == FormatTools.FLOAT) {
      reader = new jImarisReader.bpImageReaderFloat(getCurrentFile(), 0, options);
    }

    colors = new ArrayList<double[]>();

    seriesCount = 0;

    // read metadata
    CoreMetadata ms0 = core.get(0, 0);
    getMetadata();

    // additional metadata
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    String imageName = new Location(getCurrentFile()).getName();
    for (int s = 0; s < getSeriesCount(); s++) {
      setSeries(s);
      store.setImageName(imageName + " Resolution Level " + s, s);

      double pixelSizeX, pixelSizeY, pixelSizeZ;
      pixelSizeX = (maxX - minX) / getSizeX();
      pixelSizeY = (maxY - minY) / getSizeY();
      pixelSizeZ = (maxZ - minZ) / getSizeZ();

      Length sizeX = FormatTools.getPhysicalSizeX(pixelSizeX);
      Length sizeY = FormatTools.getPhysicalSizeY(pixelSizeY);
      Length sizeZ = FormatTools.getPhysicalSizeZ(pixelSizeZ);
      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, s);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, s);
      }
      if (sizeZ != null) {
        store.setPixelsPhysicalSizeZ(sizeZ, s);
      }
    }

    setSeries(0);
  }

  // -- Helper methods --

  /**
   * Retrieve the image data type.
   */
  private int getImageDataType() 
    throws IOException, FormatException
  {
    int type = -1;

    jImarisReader.bpFileImagesInfo vDataTypesReader = new jImarisReader.bpFileImagesInfo(getCurrentFile(), false);
    ArrayList<Integer> vDataTypes = vDataTypesReader.GetFileImagesInformation(); 
    if (vDataTypes.get(getCoreIndex()) == 0) type = FormatTools.UINT8;
    else if (vDataTypes.get(getCoreIndex()) == 1) type = FormatTools.UINT16;
    else if (vDataTypes.get(getCoreIndex()) == 2) type = FormatTools.UINT32;
    else if (vDataTypes.get(getCoreIndex()) == 3) type = FormatTools.FLOAT;
    else {
      throw new FormatException("Unknown pixel type");
    }

    return type;
  }

  private void getMetadata() {
    if (pixelType == FormatTools.UINT8) {
      jImarisReader.bpMetadata vMetadata = ((jImarisReader.bpImageReaderUInt8) reader).ReadMetadata();
      populateCoreMetadata(vMetadata);
    }
    else if (pixelType == FormatTools.UINT16) {
      jImarisReader.bpMetadata vMetadata = ((jImarisReader.bpImageReaderUInt16) reader).ReadMetadata();
      populateCoreMetadata(vMetadata);
    }
    else if (pixelType == FormatTools.UINT32) {
      jImarisReader.bpMetadata vMetadata = ((jImarisReader.bpImageReaderUInt32) reader).ReadMetadata();
      populateCoreMetadata(vMetadata);
    }
    else if (pixelType == FormatTools.FLOAT) {
      jImarisReader.bpMetadata vMetadata = ((jImarisReader.bpImageReaderFloat) reader).ReadMetadata();
      populateCoreMetadata(vMetadata);
    }
  }

  private void populateCoreMetadata(jImarisReader.bpMetadata aMetadata) {
    CoreMetadata ms0 = core.get(0, 0);
    int vResolutionIndex = getCoreIndex();

    // the original Bioformats ims reader considers different resolutions of the same image as series, instead of supporting multi-image files.
    // we maintain the same logic since it also offers the possibility to open larger than memory datasets (at lower resolution).
    seriesCount = aMetadata.mImageSizePerResolution.size();

    blockSizeZPerResolution = new int[seriesCount];
    currentMinZPerResolution = new int[seriesCount];
    currentMaxZPerResolution = new int[seriesCount];
    for (int res = 0; res < seriesCount; res++) {
      blockSizeZPerResolution[res] = aMetadata.mBlockSizePerResolution.get(res).mZ;
    }

    ms0.sizeX = aMetadata.mImageSizePerResolution.get(vResolutionIndex).mX;
    ms0.sizeY = aMetadata.mImageSizePerResolution.get(vResolutionIndex).mY;
    ms0.sizeZ = aMetadata.mImageSizePerResolution.get(vResolutionIndex).mZ;
    ms0.sizeC = aMetadata.mImageSizePerResolution.get(vResolutionIndex).mC;
    ms0.sizeT = aMetadata.mImageSizePerResolution.get(vResolutionIndex).mT;

    minX = aMetadata.mImageExtents.mExtentMinX;
    minY = aMetadata.mImageExtents.mExtentMinY;
    minZ = aMetadata.mImageExtents.mExtentMinZ;
    maxX = aMetadata.mImageExtents.mExtentMaxX;
    maxY = aMetadata.mImageExtents.mExtentMaxY;
    maxZ = aMetadata.mImageExtents.mExtentMaxZ;

    double[] color = new double[3];
    for (int ch = 0; ch < ms0.sizeC; ch++) {
      if (aMetadata.mColorInfos.get(ch).mIsBaseColorMode) {
        color[0] = (double) aMetadata.mColorInfos.get(ch).mBaseColor.mRed;
        color[1] = (double) aMetadata.mColorInfos.get(ch).mBaseColor.mGreen;
        color[2] = (double) aMetadata.mColorInfos.get(ch).mBaseColor.mBlue;
        colors.add(color.clone());
      }
    }

    ms0.imageCount = getSizeZ() * getSizeC() * getSizeT();
    ms0.thumbnail = false;
    ms0.dimensionOrder = "XYZCT";

    int currentSeries = 0;
    if (seriesCount > 1) {
      for (int i = 1; i < seriesCount; i++) {
        CoreMetadata ms = new CoreMetadata();
        ms.sizeX = aMetadata.mImageSizePerResolution.get(i).mX;
        ms.sizeY = aMetadata.mImageSizePerResolution.get(i).mY;
        ms.sizeZ = aMetadata.mImageSizePerResolution.get(i).mZ;
        ms.sizeC = aMetadata.mImageSizePerResolution.get(i).mC;
        ms.sizeT = aMetadata.mImageSizePerResolution.get(i).mT;
        ms.imageCount = ms.sizeZ * ms.sizeC * ms.sizeT;
        ms.thumbnail = true;

        if (ms.sizeZ == ms0.sizeZ && ms.sizeC == ms0.sizeC &&
            ms.sizeT == ms0.sizeT) {
          core.add(currentSeries, ms);
        }
        else {
          core.add(ms);
          currentSeries++;
        }
      }
    }

    for (int i = 0; i < core.size(); i++) {
      for (int j = 0; j < core.size(i); j++) {
        CoreMetadata ms = core.get(i, j);
        ms.pixelType = pixelType;
        ms.dimensionOrder = "XYZCT";
        ms.rgb = false;
        ms.thumbSizeX = 128;
        ms.thumbSizeY = 128;
        ms.orderCertain = true;
        ms.littleEndian = true;
        ms.interleaved = false;
        ms.indexed = colors.size() >= getSizeC();
      }
    }
  }
}

/**
 * Java interface for bpImarisReader open source library.
 */
class jImarisReader {

  // --- C types ---
  public static class bpReaderTypesC_Options extends Structure {
    public boolean mSWMR;

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("mSWMR");
    }
  }

  public static class bpReaderTypesC_DataTypesVector extends Structure {
    public Pointer mDataTypes;
    public int mDataTypesSize;

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("mDataTypes", "mDataTypesSize");
    }
  }

  public static class bpReaderTypesC_5D extends Structure {
    public int mValueX;
    public int mValueY;
    public int mValueZ;
    public int mValueC;
    public int mValueT;

    public bpReaderTypesC_5D(Pointer pointer) {
      super(pointer);
      read();
    }

    public bpReaderTypesC_5D() {
      super();
    }

    public bpReaderTypesC_5D(int x, int y, int z, int c, int t) {
      this.mValueX = x;
      this.mValueY = y;
      this.mValueZ = z;
      this.mValueC = c;
      this.mValueT = t;
    }

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("mValueX", "mValueY", "mValueZ", "mValueC", "mValueT");
    }
  }

  public static class bpReaderTypesC_5DVector extends Structure {
    public Pointer mSizePerResolution;
    public int mSizePerResolutionSize;

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("mSizePerResolution", "mSizePerResolutionSize");
    }
  }

  public static class bpReaderTypesC_ImageExtent extends Structure {
    public float mExtentMinX;
    public float mExtentMinY;
    public float mExtentMinZ;
    public float mExtentMaxX;
    public float mExtentMaxY;
    public float mExtentMaxZ;

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("mExtentMinX", "mExtentMinY",  "mExtentMinZ",  "mExtentMaxX",  "mExtentMaxY",  "mExtentMaxZ");
    }
  }

  public static class bpReaderTypesC_TimeInfo extends Structure {
    public int mJulianDay;
    public long mNanosecondsOfDay;

    public bpReaderTypesC_TimeInfo(Pointer pointer) {
      super(pointer);
      read();
    }

    public bpReaderTypesC_TimeInfo() {
      super();
    }

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("mJulianDay", "mNanosecondsOfDay");
    }
  }

  public static class bpReaderTypesC_TimeInfos extends Structure {
    public Pointer mValues;
    public int mValuesSize;

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("mValues", "mValuesSize");
    }
  }

  public static class bpReaderTypesC_Color extends Structure {
    public float mRed;
    public float mGreen;
    public float mBlue;
    public float mAlpha;

    public bpReaderTypesC_Color(Pointer pointer) {
      super(pointer);
      read();
    }

    public bpReaderTypesC_Color() {
      super();
    }

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("mRed", "mGreen", "mBlue", "mAlpha");
    }        
  }

  public static class bpReaderTypesC_ColorInfo extends Structure {
    public boolean mIsBaseColorMode;
    public bpReaderTypesC_Color mBaseColor = new bpReaderTypesC_Color();
    public Pointer mColorTable;
    public int mColorTableSize;
    public float mOpacity;
    public float mRangeMin;
    public float mRangeMax;
    public float mGammaCorrection;

    public bpReaderTypesC_ColorInfo(Pointer pointer) {
      super(pointer);
      read();
    }

    public bpReaderTypesC_ColorInfo() {
      super();
    }

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("mIsBaseColorMode", "mBaseColor", "mColorTable", "mColorTableSize", "mOpacity", "mRangeMin", "mRangeMax", "mGammaCorrection");
    }        
  }

  public static class bpReaderTypesC_ColorInfos extends Structure {
    public Pointer mValues;
    public int mValuesSize;

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("mValues", "mValuesSize");
    }
  }

  public static class bpReaderTypesC_Parameter extends Structure {
    public String mName;
    public String mValue;

    public bpReaderTypesC_Parameter(Pointer pointer) {
      super(pointer);
      read();
    }

    public bpReaderTypesC_Parameter() {
      super();
    }

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("mName", "mValue");
    }
  }

  public static class bpReaderTypesC_ParameterSection extends Structure {
    public String mName;
    public Pointer mValues;
    public int mValuesCount;

    public bpReaderTypesC_ParameterSection(Pointer pointer) {
      super(pointer);
      read();
    }

    public bpReaderTypesC_ParameterSection() {
      super();
    }

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("mName", "mValues", "mValuesCount");
    }
  }

  public static class bpReaderTypesC_Parameters extends Structure {
    public Pointer mValues;
    public int mValuesCount;

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("mValues", "mValuesCount");
    }
  }

  public static class bpReaderTypesC_IndexTCR extends Structure {
    public int mT;
    public int mC;
    public int mR;

    public bpReaderTypesC_IndexTCR(int t, int ch, int res) {
      this.mT = t;
      this.mC = ch;
      this.mR = res;
    }

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("mT", "mC", "mR");
    }
  }

  public static class bpReaderTypesC_Histogram extends Structure {
    public int mMin;
    public int mMax;
    public Pointer mBins;
    public int mBinsSize;

    public bpReaderTypesC_Histogram(Pointer pointer) {
      super(pointer);
      read();
    }

    public bpReaderTypesC_Histogram() {
      super();
    }

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("mMin", "mMax", "mBins", "mBinsSize");
    }
  }

  public static class bpReaderTypesC_Thumbnail extends Structure {
    public int mSizeX;
    public int mSizeY;
    public Pointer mInterleavedRGBA;
    public int mInterleavedRGBASize;

    public bpReaderTypesC_Thumbnail(Pointer pointer) {
      super(pointer);
      read();
    }

    public bpReaderTypesC_Thumbnail() {
      super();
    }

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("mSizeX", "mSizeY", "mInterleavedRGBA", "mInterleavedRGBASize");
    }
  }
  // --- End C types ---

  // --- Java types ---
  public static class bp5D {
    public int mX;
    public int mY;
    public int mZ;
    public int mC;
    public int mT;

    public bp5D() {
    }

    public bp5D(int x, int y, int z, int c, int t) {
      this.mX = x;
      this.mY = y;
      this.mZ = z;
      this.mC = c;
      this.mT = t;
    }
  }

  public static class bpImageExtents {
    public float mExtentMinX;
    public float mExtentMinY;
    public float mExtentMinZ;
    public float mExtentMaxX;
    public float mExtentMaxY;
    public float mExtentMaxZ;
  }

  public static class bpTimeInfo {
    public int mJulianDay;
    public long mNanosecondsOfDay;
  }

  public static class bpColor {
    public float mRed;
    public float mGreen;
    public float mBlue;
    public float mAlpha;
  }

  public static class bpColorInfo {
    public boolean mIsBaseColorMode;
    public bpColor mBaseColor = new bpColor();
    public ArrayList<bpColor> mColorTable = new ArrayList<>();
    public int mColorTableSize;
    public float mOpacity;
    public float mRangeMin;
    public float mRangeMax;
    public float mGammaCorrection;
  }

  public static class bpMetadata {
    public ArrayList<bp5D> mImageSizePerResolution = new ArrayList<>();
    public ArrayList<bp5D> mBlockSizePerResolution = new ArrayList<>();
    public bpImageExtents mImageExtents;
    public ArrayList<bpTimeInfo> mTimeInfos = new ArrayList<>();
    public ArrayList<bpColorInfo> mColorInfos = new ArrayList<>();
    public int mCompression;
  }

  public static class bpParameters {
    public HashMap<String, HashMap<String, String>> mSections = new HashMap<>();
  }
  // --- End Java types ---

  // --- Interface ---
  public interface javaReader extends Library {
    javaReader INSTANCE = (javaReader)
    Native.load("bpImarisReader", javaReader.class);

    bpReaderTypesC_DataTypesVector bpImageReaderC_GetFileImagesInformation(String aInputFile, boolean aSWMR);
    void bpImageReaderC_FreeDataTypes(bpReaderTypesC_DataTypesVector aDataTypes);

    Pointer bpImageReaderC_CreateUInt8(String aInputFile, int aImageIndex, bpReaderTypesC_Options aOptions);
    void bpImageReaderC_DestroyUInt8(Pointer aImageReaderC);
    void bpImageReaderC_ReadDataUInt8(Pointer aImageReaderC, bpReaderTypesC_5D aBegin, bpReaderTypesC_5D aEnd, int aResolutionIndex, Memory aData);
    void bpImageReaderC_ReadMetadataUInt8(Pointer aImageReaderC, bpReaderTypesC_5DVector aImageSizePerResolution,
                                            bpReaderTypesC_5DVector aBlockSizePerResolution, bpReaderTypesC_ImageExtent aImageExtent,
                                            bpReaderTypesC_TimeInfos aTimeInfoPerTimePoint, bpReaderTypesC_ColorInfos aColorInfoPerChannel,
                                            IntByReference aCompressionAlgorithmType);
    void bpImageReaderC_ReadParametersUInt8(Pointer aImageReaderC, bpReaderTypesC_Parameters aParams);
    bpReaderTypesC_Histogram bpImageReaderC_ReadHistogramUInt8(Pointer aImageReaderC, bpReaderTypesC_IndexTCR aIndex);
    bpReaderTypesC_Thumbnail bpImageReaderC_ReadThumbnailUInt8(Pointer aImageReaderC);

    Pointer bpImageReaderC_CreateUInt16(String aInputFile, int aImageIndex, bpReaderTypesC_Options aOptions);
    void bpImageReaderC_DestroyUInt16(Pointer aImageReaderC);
    void bpImageReaderC_ReadDataUInt16(Pointer aImageReaderC, bpReaderTypesC_5D aBegin, bpReaderTypesC_5D aEnd, int aResolutionIndex, Memory aData);
    void bpImageReaderC_ReadMetadataUInt16(Pointer aImageReaderC, bpReaderTypesC_5DVector aImageSizePerResolution,
                                            bpReaderTypesC_5DVector aBlockSizePerResolution, bpReaderTypesC_ImageExtent aImageExtent,
                                            bpReaderTypesC_TimeInfos aTimeInfoPerTimePoint, bpReaderTypesC_ColorInfos aColorInfoPerChannel,
                                            IntByReference aCompressionAlgorithmType);
    void bpImageReaderC_ReadParametersUInt16(Pointer aImageReaderC, bpReaderTypesC_Parameters aParams);
    bpReaderTypesC_Histogram bpImageReaderC_ReadHistogramUInt16(Pointer aImageReaderC, bpReaderTypesC_IndexTCR aIndex);
    bpReaderTypesC_Thumbnail bpImageReaderC_ReadThumbnailUInt16(Pointer aImageReaderC);

    Pointer bpImageReaderC_CreateUInt32(String aInputFile, int aImageIndex, bpReaderTypesC_Options aOptions);
    void bpImageReaderC_DestroyUInt32(Pointer aImageReaderC);
    void bpImageReaderC_ReadDataUInt32(Pointer aImageReaderC, bpReaderTypesC_5D aBegin, bpReaderTypesC_5D aEnd, int aResolutionIndex, Memory aData);
    void bpImageReaderC_ReadMetadataUInt32(Pointer aImageReaderC, bpReaderTypesC_5DVector aImageSizePerResolution,
                                            bpReaderTypesC_5DVector aBlockSizePerResolution, bpReaderTypesC_ImageExtent aImageExtent,
                                            bpReaderTypesC_TimeInfos aTimeInfoPerTimePoint, bpReaderTypesC_ColorInfos aColorInfoPerChannel,
                                            IntByReference aCompressionAlgorithmType);
    void bpImageReaderC_ReadParametersUInt32(Pointer aImageReaderC, bpReaderTypesC_Parameters aParams);
    bpReaderTypesC_Histogram bpImageReaderC_ReadHistogramUInt32(Pointer aImageReaderC, bpReaderTypesC_IndexTCR aIndex);
    bpReaderTypesC_Thumbnail bpImageReaderC_ReadThumbnailUInt32(Pointer aImageReaderC);

    Pointer bpImageReaderC_CreateFloat(String aInputFile, int aImageIndex, bpReaderTypesC_Options aOptions);
    void bpImageReaderC_DestroyFloat(Pointer aImageReaderC);
    void bpImageReaderC_ReadDataFloat(Pointer aImageReaderC, bpReaderTypesC_5D aBegin, bpReaderTypesC_5D aEnd, int aResolutionIndex, Memory aData);
    void bpImageReaderC_ReadMetadataFloat(Pointer aImageReaderC, bpReaderTypesC_5DVector aImageSizePerResolution,
                                            bpReaderTypesC_5DVector aBlockSizePerResolution, bpReaderTypesC_ImageExtent aImageExtent,
                                            bpReaderTypesC_TimeInfos aTimeInfoPerTimePoint, bpReaderTypesC_ColorInfos aColorInfoPerChannel,
                                            IntByReference aCompressionAlgorithmType);
    void bpImageReaderC_ReadParametersFloat(Pointer aImageReaderC, bpReaderTypesC_Parameters aParams);
    bpReaderTypesC_Histogram bpImageReaderC_ReadHistogramFloat(Pointer aImageReaderC, bpReaderTypesC_IndexTCR aIndex);
    bpReaderTypesC_Thumbnail bpImageReaderC_ReadThumbnailFloat(Pointer aImageReaderC);

    void bpImageReaderC_FreeMetadata(bpReaderTypesC_5DVector aImageSizePerResolution, bpReaderTypesC_5DVector aBlockSizePerResolution,
                                    bpReaderTypesC_TimeInfos aTimeInfoPerTimePoint, bpReaderTypesC_ColorInfos aColorInfoPerChannel);
    void bpImageReaderC_FreeParameters(bpReaderTypesC_Parameters aParams);
  }
  // --- End interface ---

  // --- Helper class to get images data type ---
  public static class bpFileImagesInfo {
    public String mInputFile;
    public boolean mSWMR;

    public bpFileImagesInfo(String aFileName, boolean aSWMR) {
      this.mInputFile = aFileName;
      this.mSWMR = aSWMR;
    }

    public ArrayList<Integer> GetFileImagesInformation() {
      bpReaderTypesC_DataTypesVector vDataTypesC = javaReader.INSTANCE.bpImageReaderC_GetFileImagesInformation(this.mInputFile, this.mSWMR);
      ArrayList<Integer> vDataTypes = new ArrayList<>();
      for (int vI = 0; vI < vDataTypesC.mDataTypesSize; vI++) {
        vDataTypes.add(vDataTypesC.mDataTypes.getInt(vI * 4));
      }
      javaReader.INSTANCE.bpImageReaderC_FreeDataTypes(vDataTypesC);
      return vDataTypes;
    }
  }

  // --- Reader classes (UInt8, UInt16, UInt32, Float) ---
  public static class bpImageReaderUInt8 {
    public String mInputFile; 
    public int mImageIndex; 
    bpReaderTypesC_Options mOptions;
    Pointer mImageReaderPtr;

    public bpImageReaderUInt8(String aFileName, int aIndex, bpReaderTypesC_Options aOptions) {
      this.mInputFile = aFileName;
      this.mImageIndex = aIndex;
      this.mOptions = aOptions;
      this.mImageReaderPtr = javaReader.INSTANCE.bpImageReaderC_CreateUInt8(mInputFile, mImageIndex, mOptions);
    }

    public void Destroy() {
      javaReader.INSTANCE.bpImageReaderC_DestroyUInt8(mImageReaderPtr);
    }

    public void ReadData(bp5D aBegin, bp5D aEnd, int aResolutionIndex, Memory aBuffer) {
      bpReaderTypesC_5D vBegin = new bpReaderTypesC_5D(aBegin.mX, aBegin.mY, aBegin.mZ, aBegin.mC, aBegin.mT);
      bpReaderTypesC_5D vEnd = new bpReaderTypesC_5D(aEnd.mX, aEnd.mY, aEnd.mZ, aEnd.mC, aEnd.mT);
      javaReader.INSTANCE.bpImageReaderC_ReadDataUInt8(mImageReaderPtr, vBegin, vEnd, aResolutionIndex, aBuffer);
    }

    public bpMetadata ReadMetadata() {
      bpReaderTypesC_5DVector imageSizes = new bpReaderTypesC_5DVector();
      bpReaderTypesC_5DVector blockSizes = new bpReaderTypesC_5DVector();
      bpReaderTypesC_ImageExtent imageExtents = new bpReaderTypesC_ImageExtent();
      bpReaderTypesC_TimeInfos timeInfos = new bpReaderTypesC_TimeInfos();
      bpReaderTypesC_ColorInfos colorInfos = new bpReaderTypesC_ColorInfos();
      IntByReference compression = new IntByReference(0);
      javaReader.INSTANCE.bpImageReaderC_ReadMetadataUInt8(mImageReaderPtr, imageSizes, blockSizes, imageExtents, timeInfos, colorInfos, compression);

      bpMetadata vMetadata = new bpMetadata();

      // image and block sizes
      bpReaderTypesC_5D fiveDType = new bpReaderTypesC_5D();
      int vFiveDTypeSize = fiveDType.size();
      for (int vR = 0; vR < imageSizes.mSizePerResolutionSize; vR++) {
        Pointer imSizePtr = imageSizes.mSizePerResolution.share(vR * vFiveDTypeSize);
        bpReaderTypesC_5D imSize = new bpReaderTypesC_5D(imSizePtr);
        bp5D vResolutionSize = new bp5D();
        vResolutionSize.mX = imSize.mValueX;
        vResolutionSize.mY = imSize.mValueY;
        vResolutionSize.mZ = imSize.mValueZ;
        vResolutionSize.mC = imSize.mValueC;
        vResolutionSize.mT = imSize.mValueT;
        vMetadata.mImageSizePerResolution.add(vResolutionSize);
      }
      for (int vR = 0; vR < blockSizes.mSizePerResolutionSize; vR++) {
        Pointer blockSizePtr = blockSizes.mSizePerResolution.share(vR * vFiveDTypeSize);
        bpReaderTypesC_5D blockSize = new bpReaderTypesC_5D(blockSizePtr);
        bp5D vResolutionSize = new bp5D();
        vResolutionSize.mX = blockSize.mValueX;
        vResolutionSize.mY = blockSize.mValueY;
        vResolutionSize.mZ = blockSize.mValueZ;
        vResolutionSize.mC = blockSize.mValueC;
        vResolutionSize.mT = blockSize.mValueT;
        vMetadata.mBlockSizePerResolution.add(vResolutionSize);
      }

      // image extents
      bpImageExtents vExtents = new bpImageExtents();
      vExtents.mExtentMinX = imageExtents.mExtentMinX;
      vExtents.mExtentMinY = imageExtents.mExtentMinY;
      vExtents.mExtentMinZ = imageExtents.mExtentMinZ;
      vExtents.mExtentMaxX = imageExtents.mExtentMaxX;
      vExtents.mExtentMaxY = imageExtents.mExtentMaxY;
      vExtents.mExtentMaxZ = imageExtents.mExtentMaxZ;
      vMetadata.mImageExtents = vExtents;

      // time infos
      bpReaderTypesC_TimeInfo timeType = new bpReaderTypesC_TimeInfo();
      int timeTypeSize = timeType.size();
      for (int vT = 0; vT < timeInfos.mValuesSize; vT++) {
        Pointer timePointPtr = timeInfos.mValues.share(vT * timeTypeSize);
        bpReaderTypesC_TimeInfo timePoint = new bpReaderTypesC_TimeInfo(timePointPtr);
        bpTimeInfo vTimeInfo = new bpTimeInfo();
        vTimeInfo.mJulianDay = timePoint.mJulianDay;
        vTimeInfo.mNanosecondsOfDay = timePoint.mNanosecondsOfDay;
        vMetadata.mTimeInfos.add(vTimeInfo);
      }

      // color infos
      bpReaderTypesC_ColorInfo colorInfoType = new bpReaderTypesC_ColorInfo();
      int colorInfoTypeSize = colorInfoType.size();
      for (int vC = 0; vC < colorInfos.mValuesSize; vC++) {
        Pointer channelColorPtr = colorInfos.mValues.share(vC * colorInfoTypeSize);
        bpReaderTypesC_ColorInfo channelColor = new bpReaderTypesC_ColorInfo(channelColorPtr);
        bpColorInfo vColorInfo = new bpColorInfo();
        if (channelColor.mColorTableSize == 0) {
          vColorInfo.mIsBaseColorMode = true;
          vColorInfo.mBaseColor.mRed = channelColor.mBaseColor.mRed;
          vColorInfo.mBaseColor.mGreen = channelColor.mBaseColor.mGreen;
          vColorInfo.mBaseColor.mBlue = channelColor.mBaseColor.mBlue;
          vColorInfo.mBaseColor.mAlpha = channelColor.mBaseColor.mAlpha;
        }
        else {
          vColorInfo.mIsBaseColorMode = false;
          bpReaderTypesC_Color colorType = new bpReaderTypesC_Color();
          int colorTypeSize = colorType.size();
          for (int vI = 0; vI < channelColor.mColorTableSize; vI++) {
            Pointer colorPtr = channelColor.mColorTable.share(vI * colorTypeSize);
            bpReaderTypesC_Color color = new bpReaderTypesC_Color(colorPtr);
            bpColor vColor = new bpColor();
            vColor.mRed = color.mRed;
            vColor.mGreen = color.mGreen;
            vColor.mBlue = color.mBlue;
            vColor.mAlpha = color.mAlpha;
            vColorInfo.mColorTable.add(vColor);
          }
        }
        vColorInfo.mOpacity = channelColor.mOpacity;
        vColorInfo.mRangeMin = channelColor.mRangeMin;
        vColorInfo.mRangeMax = channelColor.mRangeMax;
        vColorInfo.mGammaCorrection = channelColor.mGammaCorrection;
        vMetadata.mColorInfos.add(vColorInfo);
      }

      // compression algorithm
      vMetadata.mCompression = compression.getValue();

      javaReader.INSTANCE.bpImageReaderC_FreeMetadata(imageSizes, blockSizes, timeInfos, colorInfos);
      return vMetadata;
    }

    public bpParameters ReadParameters() {
      bpReaderTypesC_Parameters params = new bpReaderTypesC_Parameters();
      javaReader.INSTANCE.bpImageReaderC_ReadParametersUInt8(mImageReaderPtr, params);
      bpReaderTypesC_ParameterSection parameterSection = new bpReaderTypesC_ParameterSection();
      int parameterSectionSize = parameterSection.size();
      bpParameters vParameters = new bpParameters();
      for (int vS = 0; vS < params.mValuesCount; vS++) {
        Pointer sectionPtr = params.mValues.share(vS * parameterSectionSize);
        bpReaderTypesC_ParameterSection section = new bpReaderTypesC_ParameterSection(sectionPtr);
        bpReaderTypesC_Parameter parameter = new bpReaderTypesC_Parameter();
        int parameterSize = parameter.size();
        HashMap<String, String> vSection = new HashMap<>();
        for (int vP = 0; vP < section.mValuesCount; vP++) {
          Pointer paramsPtr = section.mValues.share(vP * parameterSize);
          bpReaderTypesC_Parameter par = new bpReaderTypesC_Parameter(paramsPtr);
          vSection.put(par.mName, par.mValue);
        }
        vParameters.mSections.put(section.mName, vSection);
      }
      javaReader.INSTANCE.bpImageReaderC_FreeParameters(params);
      return vParameters;
    }

    public bpReaderTypesC_Histogram ReadHistogram(bpReaderTypesC_IndexTCR aIndex) {
      Pointer vHistogramPtr = javaReader.INSTANCE.bpImageReaderC_ReadHistogramUInt8(mImageReaderPtr, aIndex).getPointer().share(0);
      bpReaderTypesC_Histogram vHistogram = new bpReaderTypesC_Histogram(vHistogramPtr);
      return vHistogram;
    }

    public bpReaderTypesC_Thumbnail ReadThumbnail() {
      Pointer vThumbnailPtr = javaReader.INSTANCE.bpImageReaderC_ReadThumbnailUInt8(mImageReaderPtr).getPointer().share(0);
      bpReaderTypesC_Thumbnail vThumbnail = new bpReaderTypesC_Thumbnail(vThumbnailPtr);
      return vThumbnail;
    }
  }

  public static class bpImageReaderUInt16 {
    public String mInputFile; 
    public int mImageIndex; 
    bpReaderTypesC_Options mOptions;
    Pointer mImageReaderPtr;

    public bpImageReaderUInt16(String aFileName, int aIndex, bpReaderTypesC_Options aOptions) {
      this.mInputFile = aFileName;
      this.mImageIndex = aIndex;
      this.mOptions = aOptions;
      this.mImageReaderPtr = javaReader.INSTANCE.bpImageReaderC_CreateUInt16(mInputFile, mImageIndex, mOptions);
    }

    public void Destroy() {
      javaReader.INSTANCE.bpImageReaderC_DestroyUInt16(mImageReaderPtr);
    }

    public void ReadData(bp5D aBegin, bp5D aEnd, int aResolutionIndex, Memory aBuffer) {
      bpReaderTypesC_5D vBegin = new bpReaderTypesC_5D(aBegin.mX, aBegin.mY, aBegin.mZ, aBegin.mC, aBegin.mT);
      bpReaderTypesC_5D vEnd = new bpReaderTypesC_5D(aEnd.mX, aEnd.mY, aEnd.mZ, aEnd.mC, aEnd.mT);
      javaReader.INSTANCE.bpImageReaderC_ReadDataUInt16(mImageReaderPtr, vBegin, vEnd, aResolutionIndex, aBuffer);
    }

    public bpMetadata ReadMetadata() {
      bpReaderTypesC_5DVector imageSizes = new bpReaderTypesC_5DVector();
      bpReaderTypesC_5DVector blockSizes = new bpReaderTypesC_5DVector();
      bpReaderTypesC_ImageExtent imageExtents = new bpReaderTypesC_ImageExtent();
      bpReaderTypesC_TimeInfos timeInfos = new bpReaderTypesC_TimeInfos();
      bpReaderTypesC_ColorInfos colorInfos = new bpReaderTypesC_ColorInfos();
      IntByReference compression = new IntByReference(0);
      javaReader.INSTANCE.bpImageReaderC_ReadMetadataUInt16(mImageReaderPtr, imageSizes, blockSizes, imageExtents, timeInfos, colorInfos, compression);

      bpMetadata vMetadata = new bpMetadata();

      // image and block sizes
      bpReaderTypesC_5D fiveDType = new bpReaderTypesC_5D();
      int vFiveDTypeSize = fiveDType.size();
      for (int vR = 0; vR < imageSizes.mSizePerResolutionSize; vR++) {
        Pointer imSizePtr = imageSizes.mSizePerResolution.share(vR * vFiveDTypeSize);
        bpReaderTypesC_5D imSize = new bpReaderTypesC_5D(imSizePtr);
        bp5D vResolutionSize = new bp5D();
        vResolutionSize.mX = imSize.mValueX;
        vResolutionSize.mY = imSize.mValueY;
        vResolutionSize.mZ = imSize.mValueZ;
        vResolutionSize.mC = imSize.mValueC;
        vResolutionSize.mT = imSize.mValueT;
        vMetadata.mImageSizePerResolution.add(vResolutionSize);
      }
      for (int vR = 0; vR < blockSizes.mSizePerResolutionSize; vR++) {
        Pointer blockSizePtr = blockSizes.mSizePerResolution.share(vR * vFiveDTypeSize);
        bpReaderTypesC_5D blockSize = new bpReaderTypesC_5D(blockSizePtr);
        bp5D vResolutionSize = new bp5D();
        vResolutionSize.mX = blockSize.mValueX;
        vResolutionSize.mY = blockSize.mValueY;
        vResolutionSize.mZ = blockSize.mValueZ;
        vResolutionSize.mC = blockSize.mValueC;
        vResolutionSize.mT = blockSize.mValueT;
        vMetadata.mBlockSizePerResolution.add(vResolutionSize);
      }

      // image extents
      bpImageExtents vExtents = new bpImageExtents();
      vExtents.mExtentMinX = imageExtents.mExtentMinX;
      vExtents.mExtentMinY = imageExtents.mExtentMinY;
      vExtents.mExtentMinZ = imageExtents.mExtentMinZ;
      vExtents.mExtentMaxX = imageExtents.mExtentMaxX;
      vExtents.mExtentMaxY = imageExtents.mExtentMaxY;
      vExtents.mExtentMaxZ = imageExtents.mExtentMaxZ;
      vMetadata.mImageExtents = vExtents;

      // time infos
      bpReaderTypesC_TimeInfo timeType = new bpReaderTypesC_TimeInfo();
      int timeTypeSize = timeType.size();
      for (int vT = 0; vT < timeInfos.mValuesSize; vT++) {
        Pointer timePointPtr = timeInfos.mValues.share(vT * timeTypeSize);
        bpReaderTypesC_TimeInfo timePoint = new bpReaderTypesC_TimeInfo(timePointPtr);
        bpTimeInfo vTimeInfo = new bpTimeInfo();
        vTimeInfo.mJulianDay = timePoint.mJulianDay;
        vTimeInfo.mNanosecondsOfDay = timePoint.mNanosecondsOfDay;
        vMetadata.mTimeInfos.add(vTimeInfo);
      }

      // color infos
      bpReaderTypesC_ColorInfo colorInfoType = new bpReaderTypesC_ColorInfo();
      int colorInfoTypeSize = colorInfoType.size();
      for (int vC = 0; vC < colorInfos.mValuesSize; vC++) {
        Pointer channelColorPtr = colorInfos.mValues.share(vC * colorInfoTypeSize);
        bpReaderTypesC_ColorInfo channelColor = new bpReaderTypesC_ColorInfo(channelColorPtr);
        bpColorInfo vColorInfo = new bpColorInfo();
        if (channelColor.mColorTableSize == 0) {
          vColorInfo.mIsBaseColorMode = true;
          vColorInfo.mBaseColor.mRed = channelColor.mBaseColor.mRed;
          vColorInfo.mBaseColor.mGreen = channelColor.mBaseColor.mGreen;
          vColorInfo.mBaseColor.mBlue = channelColor.mBaseColor.mBlue;
          vColorInfo.mBaseColor.mAlpha = channelColor.mBaseColor.mAlpha;
        }
        else {
          vColorInfo.mIsBaseColorMode = false;
          bpReaderTypesC_Color colorType = new bpReaderTypesC_Color();
          int colorTypeSize = colorType.size();
          for (int vI = 0; vI < channelColor.mColorTableSize; vI++) {
            Pointer colorPtr = channelColor.mColorTable.share(vI * colorTypeSize);
            bpReaderTypesC_Color color = new bpReaderTypesC_Color(colorPtr);
            bpColor vColor = new bpColor();
            vColor.mRed = color.mRed;
            vColor.mGreen = color.mGreen;
            vColor.mBlue = color.mBlue;
            vColor.mAlpha = color.mAlpha;
            vColorInfo.mColorTable.add(vColor);
          }
        }
        vColorInfo.mOpacity = channelColor.mOpacity;
        vColorInfo.mRangeMin = channelColor.mRangeMin;
        vColorInfo.mRangeMax = channelColor.mRangeMax;
        vColorInfo.mGammaCorrection = channelColor.mGammaCorrection;
        vMetadata.mColorInfos.add(vColorInfo);
      }

      // compression algorithm
      vMetadata.mCompression = compression.getValue();

      javaReader.INSTANCE.bpImageReaderC_FreeMetadata(imageSizes, blockSizes, timeInfos, colorInfos);
      return vMetadata;
    }

    public bpParameters ReadParameters() {
      bpReaderTypesC_Parameters params = new bpReaderTypesC_Parameters();
      javaReader.INSTANCE.bpImageReaderC_ReadParametersUInt16(mImageReaderPtr, params);
      bpReaderTypesC_ParameterSection parameterSection = new bpReaderTypesC_ParameterSection();
      int parameterSectionSize = parameterSection.size();
      bpParameters vParameters = new bpParameters();
      for (int vS = 0; vS < params.mValuesCount; vS++) {
        Pointer sectionPtr = params.mValues.share(vS * parameterSectionSize);
        bpReaderTypesC_ParameterSection section = new bpReaderTypesC_ParameterSection(sectionPtr);
        bpReaderTypesC_Parameter parameter = new bpReaderTypesC_Parameter();
        int parameterSize = parameter.size();
        HashMap<String, String> vSection = new HashMap<>();
        for (int vP = 0; vP < section.mValuesCount; vP++) {
          Pointer paramsPtr = section.mValues.share(vP * parameterSize);
          bpReaderTypesC_Parameter par = new bpReaderTypesC_Parameter(paramsPtr);
          vSection.put(par.mName, par.mValue);
        }
        vParameters.mSections.put(section.mName, vSection);
      }
      javaReader.INSTANCE.bpImageReaderC_FreeParameters(params);
      return vParameters;
    }

    public bpReaderTypesC_Histogram ReadHistogram(bpReaderTypesC_IndexTCR aIndex) {
      Pointer vHistogramPtr = javaReader.INSTANCE.bpImageReaderC_ReadHistogramUInt16(mImageReaderPtr, aIndex).getPointer().share(0);
      bpReaderTypesC_Histogram vHistogram = new bpReaderTypesC_Histogram(vHistogramPtr);
      return vHistogram;
    }

    public bpReaderTypesC_Thumbnail ReadThumbnail() {
      Pointer vThumbnailPtr = javaReader.INSTANCE.bpImageReaderC_ReadThumbnailUInt16(mImageReaderPtr).getPointer().share(0);
      bpReaderTypesC_Thumbnail vThumbnail = new bpReaderTypesC_Thumbnail(vThumbnailPtr);
      return vThumbnail;
    }
  }

  public static class bpImageReaderUInt32 {
    public String mInputFile; 
    public int mImageIndex; 
    bpReaderTypesC_Options mOptions;
    Pointer mImageReaderPtr;

    public bpImageReaderUInt32(String aFileName, int aIndex, bpReaderTypesC_Options aOptions) {
      this.mInputFile = aFileName;
      this.mImageIndex = aIndex;
      this.mOptions = aOptions;
      this.mImageReaderPtr = javaReader.INSTANCE.bpImageReaderC_CreateUInt32(mInputFile, mImageIndex, mOptions);
    }

    public void Destroy() {
      javaReader.INSTANCE.bpImageReaderC_DestroyUInt32(mImageReaderPtr);
    }

    public void ReadData(bp5D aBegin, bp5D aEnd, int aResolutionIndex, Memory aBuffer) {
      bpReaderTypesC_5D vBegin = new bpReaderTypesC_5D(aBegin.mX, aBegin.mY, aBegin.mZ, aBegin.mC, aBegin.mT);
      bpReaderTypesC_5D vEnd = new bpReaderTypesC_5D(aEnd.mX, aEnd.mY, aEnd.mZ, aEnd.mC, aEnd.mT);
      javaReader.INSTANCE.bpImageReaderC_ReadDataUInt32(mImageReaderPtr, vBegin, vEnd, aResolutionIndex, aBuffer);
    }

    public bpMetadata ReadMetadata() {
      bpReaderTypesC_5DVector imageSizes = new bpReaderTypesC_5DVector();
      bpReaderTypesC_5DVector blockSizes = new bpReaderTypesC_5DVector();
      bpReaderTypesC_ImageExtent imageExtents = new bpReaderTypesC_ImageExtent();
      bpReaderTypesC_TimeInfos timeInfos = new bpReaderTypesC_TimeInfos();
      bpReaderTypesC_ColorInfos colorInfos = new bpReaderTypesC_ColorInfos();
      IntByReference compression = new IntByReference(0);
      javaReader.INSTANCE.bpImageReaderC_ReadMetadataUInt32(mImageReaderPtr, imageSizes, blockSizes, imageExtents, timeInfos, colorInfos, compression);

      bpMetadata vMetadata = new bpMetadata();

      // image and block sizes
      bpReaderTypesC_5D fiveDType = new bpReaderTypesC_5D();
      int vFiveDTypeSize = fiveDType.size();
      for (int vR = 0; vR < imageSizes.mSizePerResolutionSize; vR++) {
        Pointer imSizePtr = imageSizes.mSizePerResolution.share(vR * vFiveDTypeSize);
        bpReaderTypesC_5D imSize = new bpReaderTypesC_5D(imSizePtr);
        bp5D vResolutionSize = new bp5D();
        vResolutionSize.mX = imSize.mValueX;
        vResolutionSize.mY = imSize.mValueY;
        vResolutionSize.mZ = imSize.mValueZ;
        vResolutionSize.mC = imSize.mValueC;
        vResolutionSize.mT = imSize.mValueT;
        vMetadata.mImageSizePerResolution.add(vResolutionSize);
      }
      for (int vR = 0; vR < blockSizes.mSizePerResolutionSize; vR++) {
        Pointer blockSizePtr = blockSizes.mSizePerResolution.share(vR * vFiveDTypeSize);
        bpReaderTypesC_5D blockSize = new bpReaderTypesC_5D(blockSizePtr);
        bp5D vResolutionSize = new bp5D();
        vResolutionSize.mX = blockSize.mValueX;
        vResolutionSize.mY = blockSize.mValueY;
        vResolutionSize.mZ = blockSize.mValueZ;
        vResolutionSize.mC = blockSize.mValueC;
        vResolutionSize.mT = blockSize.mValueT;
        vMetadata.mBlockSizePerResolution.add(vResolutionSize);
      }

      // image extents
      bpImageExtents vExtents = new bpImageExtents();
      vExtents.mExtentMinX = imageExtents.mExtentMinX;
      vExtents.mExtentMinY = imageExtents.mExtentMinY;
      vExtents.mExtentMinZ = imageExtents.mExtentMinZ;
      vExtents.mExtentMaxX = imageExtents.mExtentMaxX;
      vExtents.mExtentMaxY = imageExtents.mExtentMaxY;
      vExtents.mExtentMaxZ = imageExtents.mExtentMaxZ;
      vMetadata.mImageExtents = vExtents;

      // time infos
      bpReaderTypesC_TimeInfo timeType = new bpReaderTypesC_TimeInfo();
      int timeTypeSize = timeType.size();
      for (int vT = 0; vT < timeInfos.mValuesSize; vT++) {
        Pointer timePointPtr = timeInfos.mValues.share(vT * timeTypeSize);
        bpReaderTypesC_TimeInfo timePoint = new bpReaderTypesC_TimeInfo(timePointPtr);
        bpTimeInfo vTimeInfo = new bpTimeInfo();
        vTimeInfo.mJulianDay = timePoint.mJulianDay;
        vTimeInfo.mNanosecondsOfDay = timePoint.mNanosecondsOfDay;
        vMetadata.mTimeInfos.add(vTimeInfo);
      }

      // color infos
      bpReaderTypesC_ColorInfo colorInfoType = new bpReaderTypesC_ColorInfo();
      int colorInfoTypeSize = colorInfoType.size();
      for (int vC = 0; vC < colorInfos.mValuesSize; vC++) {
        Pointer channelColorPtr = colorInfos.mValues.share(vC * colorInfoTypeSize);
        bpReaderTypesC_ColorInfo channelColor = new bpReaderTypesC_ColorInfo(channelColorPtr);
        bpColorInfo vColorInfo = new bpColorInfo();
        if (channelColor.mColorTableSize == 0) {
          vColorInfo.mIsBaseColorMode = true;
          vColorInfo.mBaseColor.mRed = channelColor.mBaseColor.mRed;
          vColorInfo.mBaseColor.mGreen = channelColor.mBaseColor.mGreen;
          vColorInfo.mBaseColor.mBlue = channelColor.mBaseColor.mBlue;
          vColorInfo.mBaseColor.mAlpha = channelColor.mBaseColor.mAlpha;
        }
        else {
          vColorInfo.mIsBaseColorMode = false;
          bpReaderTypesC_Color colorType = new bpReaderTypesC_Color();
          int colorTypeSize = colorType.size();
          for (int vI = 0; vI < channelColor.mColorTableSize; vI++) {
            Pointer colorPtr = channelColor.mColorTable.share(vI * colorTypeSize);
            bpReaderTypesC_Color color = new bpReaderTypesC_Color(colorPtr);
            bpColor vColor = new bpColor();
            vColor.mRed = color.mRed;
            vColor.mGreen = color.mGreen;
            vColor.mBlue = color.mBlue;
            vColor.mAlpha = color.mAlpha;
            vColorInfo.mColorTable.add(vColor);
          }
        }
        vColorInfo.mOpacity = channelColor.mOpacity;
        vColorInfo.mRangeMin = channelColor.mRangeMin;
        vColorInfo.mRangeMax = channelColor.mRangeMax;
        vColorInfo.mGammaCorrection = channelColor.mGammaCorrection;
        vMetadata.mColorInfos.add(vColorInfo);
      }

      // compression algorithm
      vMetadata.mCompression = compression.getValue();

      javaReader.INSTANCE.bpImageReaderC_FreeMetadata(imageSizes, blockSizes, timeInfos, colorInfos);
      return vMetadata;
    }

    public bpParameters ReadParameters() {
      bpReaderTypesC_Parameters params = new bpReaderTypesC_Parameters();
      javaReader.INSTANCE.bpImageReaderC_ReadParametersUInt32(mImageReaderPtr, params);
      bpReaderTypesC_ParameterSection parameterSection = new bpReaderTypesC_ParameterSection();
      int parameterSectionSize = parameterSection.size();
      bpParameters vParameters = new bpParameters();
      for (int vS = 0; vS < params.mValuesCount; vS++) {
        Pointer sectionPtr = params.mValues.share(vS * parameterSectionSize);
        bpReaderTypesC_ParameterSection section = new bpReaderTypesC_ParameterSection(sectionPtr);
        bpReaderTypesC_Parameter parameter = new bpReaderTypesC_Parameter();
        int parameterSize = parameter.size();
        HashMap<String, String> vSection = new HashMap<>();
        for (int vP = 0; vP < section.mValuesCount; vP++) {
          Pointer paramsPtr = section.mValues.share(vP * parameterSize);
          bpReaderTypesC_Parameter par = new bpReaderTypesC_Parameter(paramsPtr);
          vSection.put(par.mName, par.mValue);
        }
        vParameters.mSections.put(section.mName, vSection);
      }
      javaReader.INSTANCE.bpImageReaderC_FreeParameters(params);
      return vParameters;
    }

    public bpReaderTypesC_Histogram ReadHistogram(bpReaderTypesC_IndexTCR aIndex) {
      Pointer vHistogramPtr = javaReader.INSTANCE.bpImageReaderC_ReadHistogramUInt32(mImageReaderPtr, aIndex).getPointer().share(0);
      bpReaderTypesC_Histogram vHistogram = new bpReaderTypesC_Histogram(vHistogramPtr);
      return vHistogram;
    }

    public bpReaderTypesC_Thumbnail ReadThumbnail() {
      Pointer vThumbnailPtr = javaReader.INSTANCE.bpImageReaderC_ReadThumbnailUInt32(mImageReaderPtr).getPointer().share(0);
      bpReaderTypesC_Thumbnail vThumbnail = new bpReaderTypesC_Thumbnail(vThumbnailPtr);
      return vThumbnail;
    }
  }

  public static class bpImageReaderFloat {
    public String mInputFile; 
    public int mImageIndex; 
    bpReaderTypesC_Options mOptions;
    Pointer mImageReaderPtr;

    public bpImageReaderFloat(String aFileName, int aIndex, bpReaderTypesC_Options aOptions) {
      this.mInputFile = aFileName;
      this.mImageIndex = aIndex;
      this.mOptions = aOptions;
      this.mImageReaderPtr = javaReader.INSTANCE.bpImageReaderC_CreateFloat(mInputFile, mImageIndex, mOptions);
    }

    public void Destroy() {
      javaReader.INSTANCE.bpImageReaderC_DestroyFloat(mImageReaderPtr);
    }

    public void ReadData(bp5D aBegin, bp5D aEnd, int aResolutionIndex, Memory aBuffer) {
      bpReaderTypesC_5D vBegin = new bpReaderTypesC_5D(aBegin.mX, aBegin.mY, aBegin.mZ, aBegin.mC, aBegin.mT);
      bpReaderTypesC_5D vEnd = new bpReaderTypesC_5D(aEnd.mX, aEnd.mY, aEnd.mZ, aEnd.mC, aEnd.mT);
      javaReader.INSTANCE.bpImageReaderC_ReadDataFloat(mImageReaderPtr, vBegin, vEnd, aResolutionIndex, aBuffer);
    }

    public bpMetadata ReadMetadata() {
      bpReaderTypesC_5DVector imageSizes = new bpReaderTypesC_5DVector();
      bpReaderTypesC_5DVector blockSizes = new bpReaderTypesC_5DVector();
      bpReaderTypesC_ImageExtent imageExtents = new bpReaderTypesC_ImageExtent();
      bpReaderTypesC_TimeInfos timeInfos = new bpReaderTypesC_TimeInfos();
      bpReaderTypesC_ColorInfos colorInfos = new bpReaderTypesC_ColorInfos();
      IntByReference compression = new IntByReference(0);
      javaReader.INSTANCE.bpImageReaderC_ReadMetadataFloat(mImageReaderPtr, imageSizes, blockSizes, imageExtents, timeInfos, colorInfos, compression);

      bpMetadata vMetadata = new bpMetadata();

      // image and block sizes
      bpReaderTypesC_5D fiveDType = new bpReaderTypesC_5D();
      int vFiveDTypeSize = fiveDType.size();
      for (int vR = 0; vR < imageSizes.mSizePerResolutionSize; vR++) {
        Pointer imSizePtr = imageSizes.mSizePerResolution.share(vR * vFiveDTypeSize);
        bpReaderTypesC_5D imSize = new bpReaderTypesC_5D(imSizePtr);
        bp5D vResolutionSize = new bp5D();
        vResolutionSize.mX = imSize.mValueX;
        vResolutionSize.mY = imSize.mValueY;
        vResolutionSize.mZ = imSize.mValueZ;
        vResolutionSize.mC = imSize.mValueC;
        vResolutionSize.mT = imSize.mValueT;
        vMetadata.mImageSizePerResolution.add(vResolutionSize);
      }
      for (int vR = 0; vR < blockSizes.mSizePerResolutionSize; vR++) {
        Pointer blockSizePtr = blockSizes.mSizePerResolution.share(vR * vFiveDTypeSize);
        bpReaderTypesC_5D blockSize = new bpReaderTypesC_5D(blockSizePtr);
        bp5D vResolutionSize = new bp5D();
        vResolutionSize.mX = blockSize.mValueX;
        vResolutionSize.mY = blockSize.mValueY;
        vResolutionSize.mZ = blockSize.mValueZ;
        vResolutionSize.mC = blockSize.mValueC;
        vResolutionSize.mT = blockSize.mValueT;
        vMetadata.mBlockSizePerResolution.add(vResolutionSize);
      }

      // image extents
      bpImageExtents vExtents = new bpImageExtents();
      vExtents.mExtentMinX = imageExtents.mExtentMinX;
      vExtents.mExtentMinY = imageExtents.mExtentMinY;
      vExtents.mExtentMinZ = imageExtents.mExtentMinZ;
      vExtents.mExtentMaxX = imageExtents.mExtentMaxX;
      vExtents.mExtentMaxY = imageExtents.mExtentMaxY;
      vExtents.mExtentMaxZ = imageExtents.mExtentMaxZ;
      vMetadata.mImageExtents = vExtents;

      // time infos
      bpReaderTypesC_TimeInfo timeType = new bpReaderTypesC_TimeInfo();
      int timeTypeSize = timeType.size();
      for (int vT = 0; vT < timeInfos.mValuesSize; vT++) {
        Pointer timePointPtr = timeInfos.mValues.share(vT * timeTypeSize);
        bpReaderTypesC_TimeInfo timePoint = new bpReaderTypesC_TimeInfo(timePointPtr);
        bpTimeInfo vTimeInfo = new bpTimeInfo();
        vTimeInfo.mJulianDay = timePoint.mJulianDay;
        vTimeInfo.mNanosecondsOfDay = timePoint.mNanosecondsOfDay;
        vMetadata.mTimeInfos.add(vTimeInfo);
      }

      // color infos
      bpReaderTypesC_ColorInfo colorInfoType = new bpReaderTypesC_ColorInfo();
      int colorInfoTypeSize = colorInfoType.size();
      for (int vC = 0; vC < colorInfos.mValuesSize; vC++) {
        Pointer channelColorPtr = colorInfos.mValues.share(vC * colorInfoTypeSize);
        bpReaderTypesC_ColorInfo channelColor = new bpReaderTypesC_ColorInfo(channelColorPtr);
        bpColorInfo vColorInfo = new bpColorInfo();
        if (channelColor.mColorTableSize == 0) {
          vColorInfo.mIsBaseColorMode = true;
          vColorInfo.mBaseColor.mRed = channelColor.mBaseColor.mRed;
          vColorInfo.mBaseColor.mGreen = channelColor.mBaseColor.mGreen;
          vColorInfo.mBaseColor.mBlue = channelColor.mBaseColor.mBlue;
          vColorInfo.mBaseColor.mAlpha = channelColor.mBaseColor.mAlpha;
        }
        else {
          vColorInfo.mIsBaseColorMode = false;
          bpReaderTypesC_Color colorType = new bpReaderTypesC_Color();
          int colorTypeSize = colorType.size();
          for (int vI = 0; vI < channelColor.mColorTableSize; vI++) {
            Pointer colorPtr = channelColor.mColorTable.share(vI * colorTypeSize);
            bpReaderTypesC_Color color = new bpReaderTypesC_Color(colorPtr);
            bpColor vColor = new bpColor();
            vColor.mRed = color.mRed;
            vColor.mGreen = color.mGreen;
            vColor.mBlue = color.mBlue;
            vColor.mAlpha = color.mAlpha;
            vColorInfo.mColorTable.add(vColor);
          }
        }
        vColorInfo.mOpacity = channelColor.mOpacity;
        vColorInfo.mRangeMin = channelColor.mRangeMin;
        vColorInfo.mRangeMax = channelColor.mRangeMax;
        vColorInfo.mGammaCorrection = channelColor.mGammaCorrection;
        vMetadata.mColorInfos.add(vColorInfo);
      }

      // compression algorithm
      vMetadata.mCompression = compression.getValue();

      javaReader.INSTANCE.bpImageReaderC_FreeMetadata(imageSizes, blockSizes, timeInfos, colorInfos);
      return vMetadata;
    }

    public bpParameters ReadParameters() {
      bpReaderTypesC_Parameters params = new bpReaderTypesC_Parameters();
      javaReader.INSTANCE.bpImageReaderC_ReadParametersFloat(mImageReaderPtr, params);
      bpReaderTypesC_ParameterSection parameterSection = new bpReaderTypesC_ParameterSection();
      int parameterSectionSize = parameterSection.size();
      bpParameters vParameters = new bpParameters();
      for (int vS = 0; vS < params.mValuesCount; vS++) {
        Pointer sectionPtr = params.mValues.share(vS * parameterSectionSize);
        bpReaderTypesC_ParameterSection section = new bpReaderTypesC_ParameterSection(sectionPtr);
        bpReaderTypesC_Parameter parameter = new bpReaderTypesC_Parameter();
        int parameterSize = parameter.size();
        HashMap<String, String> vSection = new HashMap<>();
        for (int vP = 0; vP < section.mValuesCount; vP++) {
          Pointer paramsPtr = section.mValues.share(vP * parameterSize);
          bpReaderTypesC_Parameter par = new bpReaderTypesC_Parameter(paramsPtr);
          vSection.put(par.mName, par.mValue);
        }
        vParameters.mSections.put(section.mName, vSection);
      }
      javaReader.INSTANCE.bpImageReaderC_FreeParameters(params);
      return vParameters;
    }

    public bpReaderTypesC_Histogram ReadHistogram(bpReaderTypesC_IndexTCR aIndex) {
      Pointer vHistogramPtr = javaReader.INSTANCE.bpImageReaderC_ReadHistogramFloat(mImageReaderPtr, aIndex).getPointer().share(0);
      bpReaderTypesC_Histogram vHistogram = new bpReaderTypesC_Histogram(vHistogramPtr);
      return vHistogram;
    }

    public bpReaderTypesC_Thumbnail ReadThumbnail() {
      Pointer vThumbnailPtr = javaReader.INSTANCE.bpImageReaderC_ReadThumbnailFloat(mImageReaderPtr).getPointer().share(0);
      bpReaderTypesC_Thumbnail vThumbnail = new bpReaderTypesC_Thumbnail(vThumbnailPtr);
      return vThumbnail;
    }
  }
  // --- End Reader classes ---
}
