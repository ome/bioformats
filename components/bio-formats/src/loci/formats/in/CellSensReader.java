//
// CellSensReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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

package loci.formats.in;

import java.io.IOException;
import java.util.ArrayList;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.Codec;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffParser;

/**
 * CellSensReader is the file format reader for cellSens .vsi files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/CellSensReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/CellSensReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class CellSensReader extends FormatReader {

  // -- Constants --

  private static final int TILE_SIZE = 512;

  // -- Fields --

  private String[] usedFiles;

  private TiffParser parser;
  private IFDList ifds;

  private Long[][] tileOffsets;
  private boolean jpeg = false;

  // -- Constructor --

  /** Constructs a new cellSens reader. */
  public CellSensReader() {
    super("CellSens VSI", "vsi");
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN};
    suffixSufficient = true;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    return usedFiles;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (getSeries() < getSeriesCount() - ifds.size()) {
      int tilesWide = getSizeX() / TILE_SIZE;
      int tilesHigh = getSizeY() / TILE_SIZE;
      if (tilesWide * TILE_SIZE < getSizeX()) {
        tilesWide++;
      }
      if (tilesHigh * TILE_SIZE < getSizeY()) {
        tilesHigh++;
      }

      Region imageBounds = new Region(x, y, w, h);

      for (int ty=0; ty<tilesHigh; ty++) {
        for (int tx=0; tx<tilesWide; tx++) {
          int tile = ty * tilesWide + tx;

          Region tileBounds =
            new Region(tx * TILE_SIZE, ty * TILE_SIZE, TILE_SIZE, TILE_SIZE);

          if (!imageBounds.intersects(tileBounds)) {
            continue;
          }

          Region intersection = imageBounds.intersection(tileBounds);
          byte[] b = decodeTile(tile);

          int pixel =
            getRGBChannelCount() * FormatTools.getBytesPerPixel(getPixelType());
          int row = (int) Math.min(TILE_SIZE, intersection.width) * pixel;

          if (b != null) {
            for (int yy=0; yy<intersection.height; yy++) {
              int offset = (ty * TILE_SIZE + yy) * w * pixel + tx * row;
              System.arraycopy(b, yy * row, buf, offset, row);
            }
          }
        }
      }

      return buf;
    }
    else {
      int ifdIndex = getSeries() - (usedFiles.length - 1);
      return parser.getSamples(ifds.get(ifdIndex), buf, x, y, w, h);
    }
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      parser = null;
      ifds = null;
      usedFiles = null;
      tileOffsets = null;
      jpeg = false;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    parser = new TiffParser(id);
    ifds = parser.getIFDs();

    RandomAccessInputStream vsi = new RandomAccessInputStream(id);
    vsi.order(parser.getStream().isLittleEndian());
    vsi.seek(parser.getStream().getFilePointer());

    vsi.skipBytes(273);

    ArrayList<String> files = new ArrayList<String>();
    Location file = new Location(id).getAbsoluteFile();

    Location dir = file.getParentFile();

    String name = file.getName();
    name = name.substring(0, name.lastIndexOf("."));

    Location pixelsDir = new Location(dir, "_" + name + "_");
    String[] stackDirs = pixelsDir.list(true);
    for (String f : stackDirs) {
      Location stackDir = new Location(pixelsDir, f);
      String[] pixelsFiles = stackDir.list(true);
      if (pixelsFiles != null) {
        for (String pixelsFile : pixelsFiles) {
          if (checkSuffix(pixelsFile, "ets")) {
            files.add(new Location(stackDir, pixelsFile).getAbsolutePath());
          }
        }
      }
    }
    files.add(file.getAbsolutePath());
    usedFiles = files.toArray(new String[files.size()]);

    core = new CoreMetadata[files.size() - 1 + ifds.size()];

    tileOffsets = new Long[files.size() - 1][];

    IFDList exifs = parser.getExifIFDs();

    for (int s=0; s<core.length; s++) {
      core[s] = new CoreMetadata();

      if (s < files.size() - 1) {
        RandomAccessInputStream etsFile =
          new RandomAccessInputStream(files.get(s));

        ArrayList<Long> offsets = new ArrayList<Long>();

        byte[] buf = new byte[8192];
        etsFile.read(buf);

        while (etsFile.getFilePointer() < etsFile.length()) {
          for (int i=0; i<buf.length-3; i++) {
            if (buf[i] == (byte) 0xff && buf[i + 1] == 0x4f &&
              buf[i + 2] == (byte) 0xff && buf[i + 3] == 0x51)
            {
              long fp = etsFile.getFilePointer();
              etsFile.seek(fp - buf.length + i + 8);

              if (etsFile.readInt() == TILE_SIZE && etsFile.readInt() == TILE_SIZE) {
                offsets.add(fp - buf.length + i);
              }

              etsFile.seek(fp);
            }
            else if (buf[i] == (byte) 0xff && buf[i + 1] == (byte) 0xd8) {
              offsets.add(etsFile.getFilePointer() - buf.length + i);
              jpeg = true;
            }
          }
          buf[0] = buf[buf.length - 3];
          buf[1] = buf[buf.length - 2];
          buf[2] = buf[buf.length - 1];
          etsFile.read(buf, 3, buf.length - 3);
        }

        tileOffsets[s] = offsets.toArray(new Long[offsets.size()]);
        byte[] b = decodeTile(0);

        int diff = 0;
        if (b != null) {
          diff = b.length / (TILE_SIZE * TILE_SIZE);
        }

        switch (diff) {
          case 0:
          case 1:
            core[s].pixelType = FormatTools.UINT8;
            core[s].sizeC = 1;
            break;
          case 2:
            core[s].pixelType = FormatTools.UINT16;
            core[s].sizeC = 1;
            break;
          case 3:
            core[s].pixelType = FormatTools.UINT8;
            core[s].sizeC = 3;
            break;
        }

        etsFile.close();

        if (s < exifs.size() && exifs.get(s).containsKey(IFD.PIXEL_X_DIMENSION)) {
          core[s].sizeX = exifs.get(s).getIFDIntValue(IFD.PIXEL_X_DIMENSION);
          core[s].sizeY = exifs.get(s).getIFDIntValue(IFD.PIXEL_Y_DIMENSION);
        }
        else {
          core[s].sizeX = vsi.readInt();
          core[s].sizeY = vsi.readInt();
        }

        core[s].sizeZ = 1;
        core[s].sizeT = 1;
        core[s].imageCount = 1;
        core[s].littleEndian = false;
        core[s].rgb = core[s].sizeC > 1;
        core[s].interleaved = core[s].rgb;
      }
      else {
        IFD ifd = ifds.get(s - files.size() + 1);
        PhotoInterp p = ifd.getPhotometricInterpretation();
        int samples = ifd.getSamplesPerPixel();
        core[s].rgb = samples > 1 || p == PhotoInterp.RGB;
        core[s].sizeX = (int) ifd.getImageWidth();
        core[s].sizeY = (int) ifd.getImageLength();
        core[s].sizeZ = 1;
        core[s].sizeT = 1;
        core[s].sizeC = core[s].rgb ? samples : 1;
        core[s].littleEndian = ifd.isLittleEndian();
        core[s].indexed = p == PhotoInterp.RGB_PALETTE &&
          (get8BitLookupTable() != null || get16BitLookupTable() != null);
        core[s].imageCount = 1;
        core[s].pixelType = ifd.getPixelType();
        core[s].interleaved = false;
        core[s].falseColor = false;
        core[s].thumbnail = s != 0;
      }
      core[s].dimensionOrder = "XYCZT";
      core[s].metadataComplete = true;
    }
    vsi.close();

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

  // -- Helper methods --

  private int getTileSize() {
    int channels = getRGBChannelCount();
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    return bpp * channels * TILE_SIZE * TILE_SIZE;
  }

  private byte[] decodeTile(int index) throws FormatException, IOException {
    if (index >= tileOffsets[getSeries()].length) {
      return null;
    }
    Long offset = tileOffsets[getSeries()][index];
    RandomAccessInputStream ets =
      new RandomAccessInputStream(usedFiles[getSeries()]);
    ets.seek(offset);

    Codec codec = null;
    CodecOptions options = new CodecOptions();
    options.interleaved = isInterleaved();
    options.littleEndian = isLittleEndian();
    int tileSize = getTileSize();
    if (tileSize == 0) {
      tileSize = TILE_SIZE * TILE_SIZE * 10;
    }
    options.maxBytes = (int) (offset + tileSize);

    if (jpeg) {
      codec = new JPEGCodec();
    }
    else {
      codec = new JPEG2000Codec();
    }
    byte[] buf = codec.decompress(ets, options);

    ets.close();
    return buf;
  }

}
