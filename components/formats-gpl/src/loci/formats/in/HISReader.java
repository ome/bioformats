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
import loci.formats.meta.MetadataStore;

import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * HISReader is the file format reader for Hamamatsu .his files.
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
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 2;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return (stream.readString(blockLen)).indexOf(HIS_MAGIC_STRING) >= 0;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
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

      in.skipBits(y * getSizeX() * getSizeC() * bits);
      for (int row=0; row<h; row++) {
        int rowOffset = row * w * getSizeC() * bpp;
        in.skipBits(x * getSizeC() * bits);
        for (int col=0; col<w; col++) {
          int colOffset = col * getSizeC() * bpp;
          for (int c=0; c<getSizeC(); c++) {
            int sample = in.readBits(bits);
            DataTools.unpackBytes(sample, buf, rowOffset + colOffset + c * bpp,
              bpp, isLittleEndian());
          }
        }
        in.skipBits(getSizeC() * bits * (getSizeX() - w - x));
      }
    }
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    in.order(true);

    in.skipBytes(14);
    int nSeries = in.readShort();
    pixelOffset = new long[nSeries];

    String[] date = new String[nSeries];
    String[] binning = new String[nSeries];
    double[] offset = new double[nSeries];
    double[] exposureTime = new double[nSeries];
    boolean adjustedBitDepth = false;

    in.seek(0);

    core.clear();
    for (int i=0; i<nSeries; i++) {
      CoreMetadata ms = new CoreMetadata();
      core.add(ms);

      String checkString = in.readString(2);
      if (!checkString.equals("IM") && i > 0) {
        if (getBitsPerPixel() == 12) {
          core.get(i - 1).bitsPerPixel = 16;

          long prevSkip = ((long) getSizeX() * getSizeY() * getSizeC() * 12) / 8;
          long totalBytes = FormatTools.getPlaneSize(this);
          in.skipBytes(totalBytes - prevSkip);
          adjustedBitDepth = true;
        }
      }

      setSeries(i);

      int commentBytes = in.readShort();
      ms.sizeX = in.readShort();
      ms.sizeY = in.readShort();
      in.skipBytes(4);

      int dataType = in.readShort();

      switch (dataType) {
        case 1:
          ms.pixelType = FormatTools.UINT8;
          break;
        case 2:
          ms.pixelType = FormatTools.UINT16;
          break;
        case 6:
          ms.pixelType = FormatTools.UINT16;
          ms.bitsPerPixel = adjustedBitDepth ? 16 : 12;
          break;
        case 11:
          ms.pixelType = FormatTools.UINT8;
          ms.sizeC = 3;
          break;
        case 12:
          ms.pixelType = FormatTools.UINT16;
          ms.sizeC = 3;
          break;
        case 14:
          ms.pixelType = FormatTools.UINT16;
          ms.sizeC = 3;
          ms.bitsPerPixel = adjustedBitDepth ? 16 : 12;
          break;
      }

      in.skipBytes(50);
      String comment = in.readString(commentBytes);
      if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
        String[] data = comment.split(";");
        for (String token : data) {
          int eq = token.indexOf('=');
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

      ms.littleEndian = true;
      if (ms.sizeC == 0) ms.sizeC = 1;
      ms.sizeT = 1;
      ms.sizeZ = 1;
      ms.imageCount = 1;
      ms.rgb = ms.sizeC > 1;
      ms.interleaved = isRGB();
      ms.dimensionOrder = "XYCZT";

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
        if (date[i] != null) {
          store.setImageAcquisitionDate(new Timestamp(date[i]), i);
        }
        store.setPlaneExposureTime(new Time(exposureTime[i], UNITS.SECOND), i, 0);

        String detectorID = MetadataTools.createLSID("Detector", 0, i);
        store.setDetectorID(detectorID, 0, i);
        store.setDetectorOffset(offset[i], 0, i);
        store.setDetectorType(MetadataTools.getDetectorType("Other"), 0, i);
        store.setDetectorSettingsID(detectorID, i, 0);
        store.setDetectorSettingsBinning(MetadataTools.getBinning(binning[i]), i, 0);
      }
    }
  }

}
