/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2021 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.out;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.CompressionType;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.codec.JPEG2000CodecOptions;
import loci.formats.codec.JPEGCodec;
import loci.formats.in.DicomTag;
import loci.formats.meta.MetadataRetrieve;

import static loci.formats.in.DicomAttribute.*;
import static loci.formats.in.DicomVR.*;

/**
 * DicomWriter is the file format writer for DICOM files.
 */
public class DicomWriter extends FormatWriter {

  // -- Constants --

  // -- Fields --

  private long pixelDataLengthPointer = 0;
  private int pixelDataSize = 0;

  // -- Constructor --

  public DicomWriter() {
    super("DICOM", "dcm");
    compressionTypes = new String[] {
      CompressionType.UNCOMPRESSED.getCompression(),
      CompressionType.JPEG.getCompression(),
      CompressionType.J2K.getCompression()
    };
  }

  // -- IFormatWriter API methods --

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  @Override
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    checkParams(no, buf, x, y, w, h);

    // TODO: non-sequential writing == TILED_SPARSE,
    // will require placeholder metadata for plane positions
    if (!sequential) {
      throw new FormatException("Non-sequential writing not yet supported");
    }

    MetadataRetrieve retrieve = getMetadataRetrieve();
    int bytesPerPixel = FormatTools.getBytesPerPixel(
      FormatTools.pixelTypeFromString(
      retrieve.getPixelsType(series).toString()));

    out.seek(out.length());
    long start = out.getFilePointer();

    if (compression == null || compression.equals(CompressionType.UNCOMPRESSED.getCompression())) {
      out.write(buf);
    }
    else if (compression.equals(CompressionType.JPEG.getCompression())) {
      JPEGCodec codec = new JPEGCodec();
      CodecOptions options = new CodecOptions();
      options.width = w;
      options.height = h;
      options.channels = getSamplesPerPixel();
      out.write(codec.compress(buf, options));
    }
    else if (compression.equals(CompressionType.J2K.getCompression())) {
      JPEG2000CodecOptions options = new JPEG2000CodecOptions();
      options.width = w;
      options.height = h;
      options.channels = getSamplesPerPixel();
      options.bitsPerSample = bytesPerPixel * 8;
      options.littleEndian = out.isLittleEndian();
      options.interleaved = interleaved;

      JPEG2000Codec codec = new JPEG2000Codec();
      out.write(codec.compress(buf, options));
    }

    long length = out.getFilePointer() - start;
    pixelDataSize += (int) length;
    out.seek(pixelDataLengthPointer);
    out.writeInt(pixelDataSize);
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
  @Override
  public boolean canDoStacks() { return true; }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  @Override
  public int[] getPixelTypes(String codec) {
    // TODO : take codec value into consideration
    return new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,
      FormatTools.UINT16};
  }

  // -- FormatWriter API methods --

  /* @see loci.formats.FormatWriter#setId(String) */
  @Override
  public void setId(String id) throws FormatException, IOException {
    super.setId(id);

    // TODO: only writes one file, no resolution support yet

    MetadataRetrieve r = getMetadataRetrieve();
    int width = r.getPixelsSizeX(series).getValue().intValue();
    int height = r.getPixelsSizeY(series).getValue().intValue();
    int sizeZ = r.getPixelsSizeZ(series).getValue().intValue();
    String pixelType = r.getPixelsType(series).toString();
    int bytesPerPixel = FormatTools.getBytesPerPixel(pixelType);
    int nChannels = getSamplesPerPixel();

    boolean littleEndian = false;
    if (r.getPixelsBigEndian(series) != null) {
      littleEndian = !r.getPixelsBigEndian(series).booleanValue();
    }
    else if (r.getPixelsBinDataCount(series) == 0) {
      littleEndian = !r.getPixelsBinDataBigEndian(series, 0).booleanValue();
    }

    out.order(littleEndian);

    int tileWidth = getTileSizeX();
    if (tileWidth <= 0) {
      tileWidth = width;
    }
    int tileHeight = getTileSizeY();
    if (tileHeight <= 0) {
      tileHeight = height;
    }

    // TODO: sort tags before writing

    // see http://dicom.nema.org/dicom/2013/output/chtml/part05/sect_A.4.html
    DicomTag transferSyntax = new DicomTag(TRANSFER_SYNTAX_UID, UI);
    if (compression == null || compression.equals(CompressionType.UNCOMPRESSED.getCompression())) {
      if (littleEndian) {
        transferSyntax.value = "1.2.840.10008.1.2.1 ";
      }
      else {
        transferSyntax.value = "1.2.840.10008.1.2.2 ";
      }
    }
    else if (compression.equals(CompressionType.J2K.getCompression())) {
      transferSyntax.value = "1.2.840.10008.1.2.4.91";
    }
    else if (compression.equals(CompressionType.JPEG.getCompression())) {
      transferSyntax.value = "1.2.840.10008.1.2.4.50";
    }
    writeTag(transferSyntax);

    DicomTag planarConfig = new DicomTag(PLANAR_CONFIGURATION, US);
    planarConfig.value = new short[] {(short) (interleaved ? 1 : 0)};
    writeTag(planarConfig);

    DicomTag rows = new DicomTag(ROWS, US);
    rows.value = new short[] {(short) tileWidth};
    writeTag(rows);

    DicomTag columns = new DicomTag(COLUMNS, US);
    columns.value = new short[] {(short) tileHeight};
    writeTag(columns);

    DicomTag matrixRows = new DicomTag(TOTAL_PIXEL_MATRIX_ROWS, UL);
    matrixRows.value = new long[] {width};
    writeTag(matrixRows);

    DicomTag matrixColumns = new DicomTag(TOTAL_PIXEL_MATRIX_COLUMNS, UL);
    matrixColumns.value = new long[] {height};
    writeTag(matrixColumns);

    int tileCountX = (int) Math.ceil((double) width / tileWidth);
    int tileCountY = (int) Math.ceil((double) height / tileHeight);
    DicomTag numberOfFrames = new DicomTag(NUMBER_OF_FRAMES, SL);
    numberOfFrames.value = new int[] {tileCountX * tileCountY * sizeZ};
    writeTag(numberOfFrames);

    DicomTag matrixFrames = new DicomTag(TOTAL_PIXEL_MATRIX_FOCAL_PLANES, SL);
    matrixFrames.value = new int[] {sizeZ};
    writeTag(matrixFrames);

    DicomTag bits = new DicomTag(BITS_ALLOCATED, US);
    bits.value = new short[] {(short) (bytesPerPixel * 8)};
    writeTag(bits);

    DicomTag signed = new DicomTag(PIXEL_SIGN, US);
    boolean isSigned = FormatTools.isSigned(FormatTools.pixelTypeFromString(pixelType));
    signed.value = new short[] {(short) (isSigned ? 1 : 0)};
    writeTag(signed);

    DicomTag samplesPerPixel = new DicomTag(SAMPLES_PER_PIXEL, US);
    samplesPerPixel.value = new short[] {(short) nChannels};
    writeTag(samplesPerPixel);

    DicomTag dimensionOrganization = new DicomTag(DIMENSION_ORGANIZATION_TYPE, CS);
    dimensionOrganization.value = sequential ? "TILED_FULL" : "TILED_SPARSE";
    writeTag(dimensionOrganization);

    DicomTag seriesNumber = new DicomTag(SERIES_NUMBER, IS);
    seriesNumber.value = "1 ";
    DicomTag instanceNumber = new DicomTag(INSTANCE_NUMBER, IS);
    instanceNumber.value = "1 ";
    writeTag(seriesNumber);

    DicomTag photoInterp = new DicomTag(PHOTOMETRIC_INTERPRETATION, CS);
    photoInterp.value = nChannels == 3 ? "RGB " : "MONOCHROME1 ";
    writeTag(photoInterp);

    DicomTag pixelData = new DicomTag(PIXEL_DATA, OB);
    writeTag(pixelData);
    pixelDataLengthPointer = out.getFilePointer() - 4;
  }

  /* @see loci.formats.FormatWriter#close() */
  @Override
  public void close() throws IOException {
    super.close();

    pixelDataSize = 0;
    pixelDataLengthPointer = 0;
  }

  // -- Helper methods --

  private int getStoredLength(DicomTag tag) {
    if (tag.value != null) {
      if (tag.value instanceof String) {
        /* debug */ System.out.println("using string length for " + tag.value);
        return ((String) tag.value).length();
      }
      /* debug */ System.out.println(tag.vr + " width = " + tag.vr.getWidth());
      return tag.vr.getWidth() * Array.getLength(tag.value);
    }
    int length = 0;
    for (DicomTag child : tag.children) {
      length += getStoredLength(child);
    }
    return length;
  }

  private void writeTag(DicomTag tag) throws IOException {
    if (tag.children.size() == 0 && tag.value == null && tag.attribute != PIXEL_DATA) {
      return;
    }
    int tagCode = tag.attribute.getTag();

    out.writeShort((short) ((tagCode & 0xffff0000) >> 16));
    out.writeShort((short) (tagCode & 0xffff));

    boolean order = out.isLittleEndian();
    out.order(false);
    out.writeShort(tag.vr.getCode());
    out.order(order);

    if (tag.vr == OB || tag.vr == OW || tag.vr == SQ ||
      tag.vr == UN || tag.vr == UT || tag.vr == UC)
    {
      out.writeShort((short) 0);
      out.writeInt(getStoredLength(tag));
    }
    else {
      out.writeShort((short) getStoredLength(tag));
    }

    if (tag.children.size() > 0) {
      for (DicomTag child : tag.children) {
        writeTag(child);
      }
    }
    else if (tag.value != null) {
      switch (tag.vr) {
        case AE:
        case AS:
        case CS:
        case DA:
        case DS:
        case DT:
        case IS:
        case LO:
        case LT:
        case PN:
        case SH:
        case ST:
        case TM:
        case UC:
        case UI:
        case UR:
        case UT:
          out.writeBytes(tag.value.toString());
          break;
        case AT:
          for (short s : (short[]) tag.value) {
            out.writeShort(s);
          }
          break;
        case FL:
          for (float f : (float[]) tag.value) {
            out.writeFloat(f);
          }
          break;
        case FD:
          for (double d : (double[]) tag.value) {
            out.writeDouble(d);
          }
          break;
        case OB:
          out.write((byte[]) tag.value);
          break;
        case SL:
          for (int v : (int[]) tag.value) {
            out.writeInt(v);
          }
          break;
        case SS:
          for (short s : (short[]) tag.value) {
            out.writeShort(s);
          }
          break;
        case SV:
          for (long v : (long[]) tag.value) {
            out.writeLong(v);
          }
          break;
        case UL:
          for (long v : (long[]) tag.value) {
            out.writeInt((int) (v & 0xffffffff));
          }
          break;
        case US:
          for (short s : (short[]) tag.value) {
            out.writeShort(s);
          }
          break;
        default:
          throw new IllegalArgumentException(String.valueOf(tag.vr.getCode()));
      }
    }
  }

}
