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

import loci.common.ByteArrayHandle;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.Codec;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.PackbitsCodec;
import loci.formats.codec.ZlibCodec;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffIFDEntry;
import loci.formats.tiff.TiffParser;

/**
 * PhotoshopTiffReader is the file format reader for
 * Adobe Photoshop TIFF files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class PhotoshopTiffReader extends BaseTiffReader {

  // -- Constants --

  public static final int IMAGE_SOURCE_DATA = 37724;

  public static final int PACKBITS = 1;
  public static final int ZIP = 3;

  // -- Fields --

  private transient RandomAccessInputStream tag;
  private long[] layerOffset;
  private int[] compression;
  private int[][] channelOrder;
  private String[] layerNames;

  // -- Constructor --

  /** Constructs a new Photoshop TIFF reader. */
  public PhotoshopTiffReader() {
    super("Adobe Photoshop TIFF", new String[] {"tif", "tiff"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    tp.setDoCaching(false);
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;
    return ifd.containsKey(IMAGE_SOURCE_DATA);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (getSeries() == 0) return super.openBytes(no, buf, x, y, w, h);
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int offsetIndex = 0;
    for (int i=1; i<getSeries(); i++) {
      offsetIndex += core.get(i).sizeC;
    }

    tag.seek(layerOffset[offsetIndex]);

    int bpp = FormatTools.getBytesPerPixel(getPixelType());

    if (compression[getSeries() - 1] == PACKBITS ||
      compression[getSeries() - 1] == ZIP)
    {
      Codec codec = compression[getSeries() - 1] == ZIP ? new ZlibCodec() :
        new PackbitsCodec();
      CodecOptions options = new CodecOptions();
      options.maxBytes = FormatTools.getPlaneSize(this) / getSizeC();
      ByteArrayHandle pix = new ByteArrayHandle();
      for (int c=0; c<getSizeC(); c++) {
        int index = channelOrder[getSeries() - 1][c];
        tag.seek(layerOffset[offsetIndex + index]);
        pix.write(codec.decompress(tag, options));
      }
      RandomAccessInputStream plane = new RandomAccessInputStream(pix);
      plane.seek(0);
      readPlane(plane, x, y, w, h, buf);
      plane.close();
      pix = null;
    }
    else readPlane(tag, x, y, w, h, buf);

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (tag != null) tag.close();
      tag = null;
      layerOffset = null;
      compression = null;
      layerNames = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    Object sourceData = ifds.get(0).getIFDValue(IMAGE_SOURCE_DATA);
    byte[] b = null;
    if (sourceData instanceof byte[]) {
      b = (byte[]) sourceData;
    }
    else if (sourceData instanceof TiffIFDEntry) {
      b = (byte[]) tiffParser.getIFDValue((TiffIFDEntry) sourceData);
    }
    if (b == null) return;

    tag = new RandomAccessInputStream(b);
    tag.order(isLittleEndian());

    String checkString = tag.readCString();

    String signature, type;
    int length;

    while (tag.getFilePointer() < tag.length() - 12 && tag.getFilePointer() > 0)
    {
      signature = tag.readString(4);
      type = tag.readString(4);
      length = tag.readInt();
      int skip = length % 4;
      if (skip != 0) skip = 4 - skip;

      if (type.equals("ryaL")) {
        int nLayers = (int) Math.abs(tag.readShort());

        compression = new int[nLayers];
        layerNames = new String[nLayers];

        channelOrder = new int[nLayers][];

        int[][] dataSize = new int[nLayers][];
        int offsetCount = 0;
        for (int layer=0; layer<nLayers; layer++) {
          int top = tag.readInt();
          int left = tag.readInt();
          int bottom = tag.readInt();
          int right = tag.readInt();

          CoreMetadata layerCore = new CoreMetadata();
          layerCore = new CoreMetadata();
          layerCore.sizeX = right - left;
          layerCore.sizeY = bottom - top;
          layerCore.pixelType = getPixelType();
          layerCore.sizeC = tag.readShort();
          layerCore.sizeZ = 1;
          layerCore.sizeT = 1;
          layerCore.imageCount = 1;
          layerCore.rgb = isRGB();
          layerCore.interleaved = isInterleaved();
          layerCore.littleEndian = isLittleEndian();
          layerCore.dimensionOrder = getDimensionOrder();

          if (layerCore.sizeX == 0 || layerCore.sizeY == 0 ||
            (layerCore.sizeC > 1 && !isRGB()))
          {
            // Set size to 1
            CoreMetadata ms0 = core.get(0);
            core.clear();
            core.add(ms0);
            break;
          }

          offsetCount += layerCore.sizeC;

          channelOrder[layer] = new int[layerCore.sizeC];
          dataSize[layer] = new int[layerCore.sizeC];
          for (int c=0; c<layerCore.sizeC; c++) {
            int channelID = tag.readShort();
            if (channelID < 0) channelID = layerCore.sizeC - 1;
            channelOrder[layer][channelID] = c;
            dataSize[layer][c] = tag.readInt();
          }

          tag.skipBytes(12);

          int len = tag.readInt();
          long fp = tag.getFilePointer();

          int mask = tag.readInt();
          if (mask != 0) tag.skipBytes(mask);
          int blending = tag.readInt();
          tag.skipBytes(blending);

          int nameLength = tag.read();
          int pad = nameLength % 4;
          if (pad != 0) pad = 4 - pad;
          layerNames[layer] = tag.readString(nameLength + pad);
          layerNames[layer] =
            layerNames[layer].replaceAll("[^\\p{ASCII}]", "").trim();
          if (layerNames[layer].length() == nameLength + pad &&
            !layerNames[layer].equalsIgnoreCase("Layer " + layer + "M"))
          {
            addGlobalMetaList("Layer name", layerNames[layer]);
            core.add(layerCore);
          }
          tag.skipBytes((int) (fp + len - tag.getFilePointer()));
        }

        nLayers = core.size() - 1;

        layerOffset = new long[offsetCount];

        int nextOffset = 0;
        for (int layer=0; layer<nLayers; layer++) {
          for (int c=0; c<core.get(layer + 1).sizeC; c++) {
            long startFP = tag.getFilePointer();
            compression[layer] = tag.readShort();
            layerOffset[nextOffset] = tag.getFilePointer();
            if (compression[layer] == ZIP) {
              layerOffset[nextOffset] = tag.getFilePointer();
              ZlibCodec codec = new ZlibCodec();
              codec.decompress(tag, null);
            }
            else if (compression[layer] == PACKBITS) {
              if (layer == 0) {
                tag.skipBytes(256 * 6 + 36);
              }
              else tag.skipBytes(192);
              layerOffset[nextOffset] = tag.getFilePointer();
              PackbitsCodec codec = new PackbitsCodec();
              CodecOptions options = new CodecOptions();
              options.maxBytes = core.get(layer + 1).sizeX * core.get(layer + 1).sizeY;
              codec.decompress(tag, options);
            }
            tag.seek(startFP + dataSize[layer][c]);
            nextOffset++;
          }
        }
      }
      else tag.skipBytes(length + skip);
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
    store.setImageName("Merged", 0);
    if (layerNames != null) {
      int end = (int) Math.min(getSeriesCount() - 1, layerNames.length);
      for (int layer=0; layer<end; layer++) {
        store.setImageName(layerNames[layer], layer + 1);
      }
    }
  }

}
