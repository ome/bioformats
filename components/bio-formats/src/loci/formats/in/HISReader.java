//
// HISReader.java
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

import ome.xml.model.primitives.Timestamp;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.BitBuffer;
import loci.formats.meta.MetadataStore;

/**
 * HISReader is the file format reader for Hamamatsu .his files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/HISReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/HISReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class HISReader extends FormatReader {

  // -- Constants --

  public static final String HIS_MAGIC_STRING = "IM";

  // -- Fields --

  /** Offsets to pixel data for each series. */
  private long[] pixelOffset;

  // -- Constructor --

  /** Constructs a new Hamamatsu .his reader. */
  public HISReader() {
    super("Hamamatsu HIS", "his");
    domains = new String[] {FormatTools.SEM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 2;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return (stream.readString(blockLen)).indexOf(HIS_MAGIC_STRING) >= 0;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(pixelOffset[getSeries()]);
    if ((getBitsPerPixel() % 8) == 0) {
      readPlane(in, x, y, w, h, buf);
    }
    else {
      int bits = getBitsPerPixel();
      int bpp = FormatTools.getBytesPerPixel(getPixelType());
      byte[] b = new byte[(getSizeX() * getSizeY() * getSizeC() * bits) / 8];
      in.read(b);
      BitBuffer bb = new BitBuffer(b);

      bb.skipBits(y * getSizeX() * getSizeC() * bits);
      for (int row=0; row<h; row++) {
        int rowOffset = row * getSizeX() * getSizeC() * bpp;
        bb.skipBits(x * getSizeC() * bits);
        for (int col=0; col<w; col++) {
          int colOffset = col * getSizeC() * bpp;
          for (int c=0; c<getSizeC(); c++) {
            int sample = bb.getBits(bits);
            DataTools.unpackBytes(sample, buf, rowOffset + colOffset + c * bpp,
              bpp, isLittleEndian());
          }
        }
        bb.skipBits(getSizeC() * bits * (getSizeX() - w - x));
      }
    }
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    in.order(true);

    in.skipBytes(14);
    int nSeries = in.readShort();
    pixelOffset = new long[nSeries];
    core = new CoreMetadata[nSeries];

    String[] date = new String[nSeries];
    String[] binning = new String[nSeries];
    double[] offset = new double[nSeries];
    double[] exposureTime = new double[nSeries];
    boolean adjustedBitDepth = false;

    in.seek(0);
    for (int i=0; i<nSeries; i++) {
      core[i] = new CoreMetadata();

      String checkString = in.readString(2);
      if (!checkString.equals("IM") && i > 0) {
        if (getBitsPerPixel() == 12) {
          core[i - 1].bitsPerPixel = 16;

          int prevSkip = (getSizeX() * getSizeY() * getSizeC() * 12) / 8;
          int totalBytes = FormatTools.getPlaneSize(this);
          in.skipBytes(totalBytes - prevSkip);
          adjustedBitDepth = true;
        }
      }

      setSeries(i);

      int commentBytes = in.readShort();
      core[i].sizeX = in.readShort();
      core[i].sizeY = in.readShort();
      in.skipBytes(4);

      int dataType = in.readShort();

      switch (dataType) {
        case 1:
          core[i].pixelType = FormatTools.UINT8;
          break;
        case 2:
          core[i].pixelType = FormatTools.UINT16;
          break;
        case 6:
          core[i].pixelType = FormatTools.UINT16;
          core[i].bitsPerPixel = adjustedBitDepth ? 16 : 12;
          break;
        case 11:
          core[i].pixelType = FormatTools.UINT8;
          core[i].sizeC = 3;
          break;
        case 12:
          core[i].pixelType = FormatTools.UINT16;
          core[i].sizeC = 3;
          break;
        case 14:
          core[i].pixelType = FormatTools.UINT16;
          core[i].sizeC = 3;
          core[i].bitsPerPixel = adjustedBitDepth ? 16 : 12;
          break;
      }

      in.skipBytes(50);
      String comment = in.readString(commentBytes);
      if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
        String[] data = comment.split(";");
        for (String token : data) {
          int eq = token.indexOf("=");
          if (eq != -1) {
            String key = token.substring(0, eq);
            String value = token.substring(eq + 1);

            addSeriesMeta(key, value);

            if (key.equals("vDate")) {
              date[i] = value;
            }
            else if (key.equals("vTime")) {
              date[i] += " " + value;
              date[i] = DateTools.formatDate(date[i], "yyyy/MM/dd HH:mm:ss");
            }
            else if (key.equals("vOffset")) {
              offset[i] = Double.parseDouble(value);
            }
            else if (key.equals("vBinX")) {
              binning[i] = value;
            }
            else if (key.equals("vBinY")) {
              binning[i] += "x" + value;
            }
            else if (key.equals("vExpTim1")) {
              exposureTime[i] = Double.parseDouble(value) * 100;
            }
          }
        }
      }

      pixelOffset[i] = in.getFilePointer();

      core[i].littleEndian = true;
      if (core[i].sizeC == 0) core[i].sizeC = 1;
      core[i].sizeT = 1;
      core[i].sizeZ = 1;
      core[i].imageCount = 1;
      core[i].rgb = core[i].sizeC > 1;
      core[i].interleaved = isRGB();
      core[i].dimensionOrder = "XYCZT";

      in.skipBytes(
        (getSizeX() * getSizeY() * getSizeC() * getBitsPerPixel()) / 8);
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrumentID, 0);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      for (int i=0; i<nSeries; i++) {
        store.setImageInstrumentRef(instrumentID, i);
        store.setImageAcquisitionDate(new Timestamp(date[i]), i);

        store.setPlaneExposureTime(exposureTime[i], i, 0);

        String detectorID = MetadataTools.createLSID("Detector", 0, i);
        store.setDetectorID(detectorID, 0, i);
        store.setDetectorOffset(offset[i], 0, i);
        store.setDetectorType(getDetectorType("Other"), 0, i);
        store.setDetectorSettingsID(detectorID, i, 0);
        store.setDetectorSettingsBinning(getBinning(binning[i]), i, 0);
      }
    }
  }

}
