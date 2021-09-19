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
import java.util.ArrayList;
import java.util.Comparator;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.codec.Codec;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.CompressionType;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.codec.JPEG2000CodecOptions;
import loci.formats.codec.JPEGCodec;
import loci.formats.in.DicomTag;
import loci.formats.meta.IPyramidStore;
import loci.formats.meta.MetadataRetrieve;

import ome.xml.model.enums.DimensionOrder;
import ome.units.UNITS;
import ome.units.quantity.Length;

import static loci.formats.in.DicomAttribute.*;
import static loci.formats.in.DicomVR.*;

/**
 * DicomWriter is the file format writer for DICOM files.
 */
public class DicomWriter extends FormatWriter {

  // -- Constants --

  // -- Fields --

  private long[] pixelDataLengthPointer;
  private int[] pixelDataSize;
  private long[] transferSyntaxPointer;
  private int tileWidth = 0;
  private int tileHeight = 0;
  private PlaneOffset[][] planeOffsets;
  private Integer currentPlane = null;

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

  @Override
  public void setSeries(int s) throws FormatException {
    super.setSeries(s);
    try {
      openFile(series, resolution);
    }
    catch (IOException e) {
      LOGGER.error("Could not open file for series #" + s, e);
    }
  }

  @Override
  public void setResolution(int r) {
    super.setResolution(r);
    try {
      openFile(series, resolution);
    }
    catch (IOException e) {
      LOGGER.error("Could not open file for series #" + series + ", resolution #" + r, e);
    }
  }

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  @Override
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    checkParams(no, buf, x, y, w, h);

    boolean first = x == 0 && y == 0;
    boolean last = x + w == getSizeX() && y + h == getSizeY();
    int resolutionIndex = getIndex(series, resolution);

    if (first) {
      out.seek(transferSyntaxPointer[resolutionIndex]);
      out.writeBytes(getTransferSyntax());
    }

    if (!isReallySequential()) {
      for (int p=0; p<planeOffsets[resolutionIndex].length; p++) {
        if (!planeOffsets[resolutionIndex][p].written) {
          PlaneOffset offset = planeOffsets[resolutionIndex][p];
          offset.written = true;

          int[] zct = getZCTCoords(no);

          out.seek(offset.cOffset);
          out.writeBytes(padString(String.valueOf(zct[1])));
          out.seek(offset.xOffset);
          out.writeInt(x + 1);
          out.seek(offset.yOffset);
          out.writeInt(y + 1);
          out.seek(offset.zOffset);
          out.writeBytes(padString(String.valueOf(zct[0])));
          break;
        }
      }
    }

    MetadataRetrieve retrieve = getMetadataRetrieve();
    int bytesPerPixel = FormatTools.getBytesPerPixel(
      FormatTools.pixelTypeFromString(
      retrieve.getPixelsType(series).toString()));

    out.seek(out.length());
    long start = out.getFilePointer();

    byte[] paddedBuf = null;

    // pad the last row and column of tiles to match specified tile size
    if ((x + w == getSizeX() && w < tileWidth) ||
      (y + h == getSizeY() && h < tileHeight))
    {
      int srcRowLen = w * bytesPerPixel * getSamplesPerPixel();
      int destRowLen = tileWidth * bytesPerPixel * getSamplesPerPixel();
      paddedBuf = new byte[tileHeight * destRowLen];

      for (int row=0; row<h; row++) {
        System.arraycopy(buf, row * srcRowLen, paddedBuf, row * destRowLen, srcRowLen);
      }
    }
    else {
      paddedBuf = buf;
    }

    if (compression == null || compression.equals(CompressionType.UNCOMPRESSED.getCompression())) {
      out.write(paddedBuf);
      if (paddedBuf.length % 2 == 1) {
        out.writeByte(0);
      }

      long length = out.getFilePointer() - start;
      pixelDataSize[resolutionIndex] += (int) length;

      out.seek(pixelDataLengthPointer[resolutionIndex]);
      out.writeInt(pixelDataSize[resolutionIndex]);
    }
    else {
      Codec codec = getCodec();
      CodecOptions options = new CodecOptions();
      options.width = tileWidth;
      options.height = tileHeight;
      options.channels = getSamplesPerPixel();
      options.bitsPerSample = bytesPerPixel * 8;
      options.littleEndian = out.isLittleEndian();
      options.interleaved = options.channels > 1 && interleaved;
      byte[] compressed = codec.compress(paddedBuf, options);
      boolean pad = compressed.length % 2 == 1;

      if (first) {
        DicomTag bot = new DicomTag(ITEM, IMPLICIT);
        bot.elementLength = 0;
        writeTag(bot);
      }

      DicomTag item = new DicomTag(ITEM, IMPLICIT);
      item.elementLength = compressed.length;
      if (pad) {
        item.elementLength++;
      }
      item.value = compressed;
      writeTag(item);
      if (pad) {
        out.writeByte(0);
      }

      if (last) {
        DicomTag end = new DicomTag(SEQUENCE_DELIMITATION_ITEM, IMPLICIT);
        end.elementLength = 0;
        writeTag(end);
      }
    }
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
  @Override
  public boolean canDoStacks() { return true; }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  @Override
  public int[] getPixelTypes(String codec) {
    if (compression != null && !compression.equals(CompressionType.UNCOMPRESSED.getCompression())) {
      return new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,
        FormatTools.UINT16};
    }
    return new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,
      FormatTools.UINT16, FormatTools.INT32, FormatTools.UINT32};
  }

  // -- FormatWriter API methods --

  /* @see loci.formats.FormatWriter#setId(String) */
  @Override
  public void setId(String id) throws FormatException, IOException {
    if (id.equals(currentId)) return;
    currentId = id;
    if (out != null) {
      if (out.length() != 0) {
        return;
      }
      out.close();
    }

    MetadataRetrieve r = getMetadataRetrieve();
    resolution = 0;

    boolean hasPyramid = r instanceof IPyramidStore;

    int totalFiles = 0;
    for (int pyramid=0; pyramid<r.getImageCount(); pyramid++) {
      if (r.getPixelsSizeT(pyramid).getValue().intValue() > 1) {
        throw new FormatException("Multiple timepoints not supported");
      }
      if (hasPyramid) {
        totalFiles += ((IPyramidStore) r).getResolutionCount(pyramid);
      }
      else {
        totalFiles++;
      }
    }
    pixelDataLengthPointer = new long[totalFiles];
    pixelDataSize = new int[totalFiles];
    transferSyntaxPointer = new long[totalFiles];

    planeOffsets = new PlaneOffset[totalFiles][];

    for (int pyramid=0; pyramid<r.getImageCount(); pyramid++) {
      series = pyramid;
      int resolutionCount = 1;
      if (hasPyramid) {
        resolutionCount = ((IPyramidStore) r).getResolutionCount(pyramid);
      }
      for (int res=0; res<resolutionCount; res++) {
        resolution = res;
        openFile(series, resolution);
        int resolutionIndex = getIndex(series, resolution);

        int width = 0;
        int height = 0;
        if (hasPyramid && resolution > 0) {
          width = ((IPyramidStore) r).getResolutionSizeX(pyramid, resolution).getValue().intValue();
          height = ((IPyramidStore) r).getResolutionSizeY(pyramid, resolution).getValue().intValue();
        }
        else {
          width = r.getPixelsSizeX(pyramid).getValue().intValue();
          height = r.getPixelsSizeY(pyramid).getValue().intValue();
        }

        int sizeZ = r.getPixelsSizeZ(pyramid).getValue().intValue();
        String pixelType = r.getPixelsType(pyramid).toString();
        int bytesPerPixel = FormatTools.getBytesPerPixel(pixelType);
        int nChannels = getSamplesPerPixel();

        tileWidth = getTileSizeX();
        if (tileWidth <= 0) {
          tileWidth = width;
        }
        tileHeight = getTileSizeY();
        if (tileHeight <= 0) {
          tileHeight = height;
        }

        ArrayList<DicomTag> tags = new ArrayList<DicomTag>();

        // see http://dicom.nema.org/dicom/2013/output/chtml/part05/sect_A.4.html
        DicomTag transferSyntax = new DicomTag(TRANSFER_SYNTAX_UID, UI);
        transferSyntax.elementLength = 22;
        tags.add(transferSyntax);

        DicomTag planarConfig = new DicomTag(PLANAR_CONFIGURATION, US);
        planarConfig.value = new short[] {(short) (interleaved ? 0 : 1)};
        tags.add(planarConfig);

        DicomTag rows = new DicomTag(ROWS, US);
        rows.value = new short[] {(short) tileHeight};
        tags.add(rows);

        DicomTag columns = new DicomTag(COLUMNS, US);
        columns.value = new short[] {(short) tileWidth};
        tags.add(columns);

        DicomTag matrixRows = new DicomTag(TOTAL_PIXEL_MATRIX_ROWS, UL);
        matrixRows.value = new long[] {height};
        tags.add(matrixRows);

        DicomTag matrixColumns = new DicomTag(TOTAL_PIXEL_MATRIX_COLUMNS, UL);
        matrixColumns.value = new long[] {width};
        tags.add(matrixColumns);

        int tileCountX = (int) Math.ceil((double) width / tileWidth);
        int tileCountY = (int) Math.ceil((double) height / tileHeight);
        DicomTag numberOfFrames = new DicomTag(NUMBER_OF_FRAMES, IS);
        numberOfFrames.value = padString(String.valueOf(
          tileCountX * tileCountY * sizeZ * r.getChannelCount(pyramid)));
        tags.add(numberOfFrames);

        DicomTag matrixFrames = new DicomTag(TOTAL_PIXEL_MATRIX_FOCAL_PLANES, UL);
        matrixFrames.value = new long[] {sizeZ};
        tags.add(matrixFrames);

        DicomTag bits = new DicomTag(BITS_ALLOCATED, US);
        bits.value = new short[] {(short) (bytesPerPixel * 8)};
        tags.add(bits);

        DicomTag signed = new DicomTag(PIXEL_SIGN, SS);
        boolean isSigned = FormatTools.isSigned(FormatTools.pixelTypeFromString(pixelType));
        signed.value = new short[] {(short) (isSigned ? 1 : 0)};
        tags.add(signed);

        DicomTag samplesPerPixel = new DicomTag(SAMPLES_PER_PIXEL, US);
        samplesPerPixel.value = new short[] {(short) nChannels};
        tags.add(samplesPerPixel);

        DicomTag dimensionOrganization = new DicomTag(DIMENSION_ORGANIZATION_TYPE, CS);
        dimensionOrganization.value = padString(isReallySequential() ? "TILED_FULL" : "TILED_SPARSE");
        tags.add(dimensionOrganization);

        DicomTag seriesNumber = new DicomTag(SERIES_NUMBER, IS);
        seriesNumber.value = padString("1");
        tags.add(seriesNumber);
        DicomTag instanceNumber = new DicomTag(INSTANCE_NUMBER, IS);
        instanceNumber.value = padString(String.valueOf(resolutionIndex + 1));
        tags.add(instanceNumber);
        DicomTag instanceUID = new DicomTag(SOP_INSTANCE_UID, UI);
        instanceUID.value = padString(" ");
        tags.add(instanceUID);

        DicomTag photoInterp = new DicomTag(PHOTOMETRIC_INTERPRETATION, CS);
        photoInterp.value = padString(nChannels == 3 ? "RGB" : "MONOCHROME2");
        tags.add(photoInterp);

        long timestamp = System.currentTimeMillis();
        DicomTag date = new DicomTag(ACQUISITION_DATE, DA);
        date.value = DateTools.convertDate(timestamp, DateTools.UNIX, "yyyyMMdd");
        tags.add(date);
        DicomTag time = new DicomTag(ACQUISITION_TIME, TM);
        time.value = DateTools.convertDate(timestamp, DateTools.UNIX, "HHmmss");
        tags.add(time);

        DicomTag imageType = new DicomTag(IMAGE_TYPE, CS);

        String pyramidName = r.getImageName(pyramid);

        String type = "DERIVED\\PRIMARY\\";
        if (!hasPyramid || resolutionCount > 1) {
          type += "VOLUME\\";
        }
        else if (pyramidName != null) {
          pyramidName = pyramidName.toLowerCase();
          if (pyramidName.indexOf("label") >= 0) {
            type += "LABEL\\";
          }
          else if (pyramidName.indexOf("macro") >= 0 ||
            pyramidName.indexOf("overview") >= 0)
          {
            type += "OVERVIEW\\";
          }
          else {
            // TODO: not sure if there is a better way to handle this case (and below)
            type += "OVERVIEW\\";
          }
        }
        else {
          // TODO: see a few lines up, does falling back to DERIVED\PRIMARY\OVERVIEW\*
          // always make sense?
          type += "OVERVIEW\\";
        }

        if (res > 0) {
          type += "RESAMPLED";
        }
        else {
          type += "NONE";
        }
        imageType.value = padString(type);
        tags.add(imageType);

        // define optical paths (OME Channels)
        DicomTag opticalSequence = new DicomTag(OPTICAL_PATH_SEQUENCE, SQ);
        for (int c=0; c<r.getChannelCount(pyramid); c++) {
          DicomTag opticalID = new DicomTag(OPTICAL_PATH_ID, SH);
          opticalID.value = padString(String.valueOf(c));
          opticalSequence.children.add(opticalID);

          DicomTag opticalDescription = new DicomTag(OPTICAL_PATH_DESCRIPTION, ST);
          opticalDescription.value = padString(r.getChannelName(pyramid, c));
          opticalSequence.children.add(opticalDescription);
        }
        tags.add(opticalSequence);

        // define physical pixels sizes
        DicomTag sharedGroupsSequence = new DicomTag(SHARED_FUNCTIONAL_GROUPS_SEQUENCE, SQ);
        DicomTag pixelMeasuresSequence = new DicomTag(PIXEL_MEASURES_SEQUENCE, SQ);

        DicomTag sliceThickness = new DicomTag(SLICE_THICKNESS, DS);
        Length physicalZ = r.getPixelsPhysicalSizeZ(pyramid);
        if (physicalZ != null) {
          sliceThickness.value = padString(String.valueOf(physicalZ.value(UNITS.MM)));
        }
        else {
          sliceThickness.value = padString("0");
        }
        pixelMeasuresSequence.children.add(sliceThickness);

        DicomTag pixelSpacing = new DicomTag(PIXEL_SPACING, DS);
        Length physicalX = r.getPixelsPhysicalSizeX(pyramid);
        Length physicalY = r.getPixelsPhysicalSizeY(pyramid);
        String px = physicalX == null ? "0" : String.valueOf(physicalX.value(UNITS.MM));
        String py = physicalY == null ? "0" : String.valueOf(physicalY.value(UNITS.MM));
        pixelSpacing.value = padString(px + "\\" + py);
        pixelMeasuresSequence.children.add(pixelSpacing);

        sharedGroupsSequence.children.add(pixelMeasuresSequence);
        tags.add(sharedGroupsSequence);

        // placeholder tile positions for TILED_SPARSE
        if (!isReallySequential()) {
          planeOffsets[resolutionIndex] = new PlaneOffset[getPlaneCount(pyramid) * tileCountX * tileCountY];
          DicomTag perFrameSequence = new DicomTag(PER_FRAME_FUNCTIONAL_GROUPS_SEQUENCE, SQ);
          for (int p=0; p<planeOffsets[resolutionIndex].length; p++) {
            // the values here don't matter, they will be overwritten when saveBytes is called
            DicomTag opticalPath = new DicomTag(OPTICAL_PATH_ID_SEQUENCE, SQ);
            DicomTag opticalPathID = new DicomTag(OPTICAL_PATH_ID, SH);
            opticalPathID.value = padString("0");
            opticalPath.children.add(opticalPathID);
            perFrameSequence.children.add(opticalPath);

            DicomTag plane = new DicomTag(PLANE_POSITION_SLIDE_SEQUENCE, SQ);

            DicomTag positionX = new DicomTag(COLUMN_POSITION_IN_MATRIX, SL);
            positionX.value = new int[] {1};
            plane.children.add(positionX);
            DicomTag positionY = new DicomTag(ROW_POSITION_IN_MATRIX, SL);
            positionY.value = new int[] {1};
            plane.children.add(positionY);
            DicomTag positionZ = new DicomTag(Z_OFFSET_IN_SLIDE, DS);
            positionZ.value = padString("0");
            plane.children.add(positionZ);

            perFrameSequence.children.add(plane);
          }

          tags.add(perFrameSequence);
        }

        tags.sort(new Comparator<DicomTag>() {
          public int compare(DicomTag a, DicomTag b) {
            return a.attribute.getTag() - b.attribute.getTag();
          }
        });
        for (DicomTag tag : tags) {
          writeTag(tag);
        }

        DicomTag pixelData = new DicomTag(PIXEL_DATA, OB);
        pixelData.elementLength = (int) 0xffffffff;
        writeTag(pixelData);
        pixelDataLengthPointer[resolutionIndex] = out.getFilePointer() - 4;
      }
    }
    setSeries(0);
  }

  /* @see loci.formats.FormatWriter#close() */
  @Override
  public void close() throws IOException {
    super.close();

    pixelDataSize = null;
    pixelDataLengthPointer = null;
    transferSyntaxPointer = null;

    // intentionally don't reset tile dimensions
  }

  @Override
  public int setTileSizeX(int tileSize) throws FormatException {
    tileWidth = tileSize;
    return tileWidth;
  }

  @Override
  public int getTileSizeX() {
    return tileWidth;
  }

  @Override
  public int setTileSizeY(int tileSize) throws FormatException {
    tileHeight = tileSize;
    return tileHeight;
  }

  @Override
  public int getTileSizeY() {
    return tileHeight;
  }

  // -- Helper methods --

  private int getStoredLength(DicomTag tag) {
    if (tag.vr == SQ) {
      return (int) 0xffffffff;
    }
    if (tag.elementLength != 0) {
      return tag.elementLength;
    }
    if (tag.value != null) {
      if (tag.value instanceof String) {
        return ((String) tag.value).length();
      }
      return tag.vr.getWidth() * Array.getLength(tag.value);
    }
    int length = 0;
    for (DicomTag child : tag.children) {
      length += getStoredLength(child);
    }
    return length;
  }

  private void writeTag(DicomTag tag) throws IOException {
    int tagCode = tag.attribute.getTag();

    out.writeShort((short) ((tagCode & 0xffff0000) >> 16));
    out.writeShort((short) (tagCode & 0xffff));

    if (tag.vr == IMPLICIT) {
      out.writeInt(getStoredLength(tag));
    }
    else {
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

      if (tag.attribute == TRANSFER_SYNTAX_UID) {
        transferSyntaxPointer[getIndex(series, resolution)] = out.getFilePointer();
      }
      if (tag.children.size() == 0 && tag.value == null) {
        if (tag.attribute != PIXEL_DATA) {
          out.skipBytes(tag.elementLength);
        }
        return;
      }
    }

    if (currentPlane != null && tag.attribute == OPTICAL_PATH_ID) {
      currentPlane++;
    }

    if (tag.children.size() > 0) {
      if (tag.attribute == PER_FRAME_FUNCTIONAL_GROUPS_SEQUENCE) {
        currentPlane = -1;
      }
      for (DicomTag child : tag.children) {
        if (tag.vr == SQ) {
          DicomTag item = new DicomTag(ITEM, IMPLICIT);
          item.elementLength = (int) 0xffffffff;
          writeTag(item);
        }
        writeTag(child);
        if (tag.vr == SQ) {
          DicomTag item = new DicomTag(ITEM_DELIMITATION_ITEM, IMPLICIT);
          item.elementLength = 0;
          writeTag(item);
        }
      }
      if (tag.vr == SQ) {
        DicomTag finalItem = new DicomTag(SEQUENCE_DELIMITATION_ITEM, IMPLICIT);
        finalItem.elementLength = 0;
        writeTag(finalItem);
      }
      if (tag.attribute == PER_FRAME_FUNCTIONAL_GROUPS_SEQUENCE) {
        currentPlane = null;
      }
    }
    else if (tag.value != null) {
      if (currentPlane != null && currentPlane >= 0) {
        int resolutionIndex = getIndex(series, resolution);
        if (planeOffsets[resolutionIndex][currentPlane] == null) {
          planeOffsets[resolutionIndex][currentPlane] = new PlaneOffset();
        }
        switch (tag.attribute) {
          case OPTICAL_PATH_ID:
            planeOffsets[resolutionIndex][currentPlane].cOffset = out.getFilePointer();
            break;
          case ROW_POSITION_IN_MATRIX:
            planeOffsets[resolutionIndex][currentPlane].yOffset = out.getFilePointer();
            break;
          case COLUMN_POSITION_IN_MATRIX:
            planeOffsets[resolutionIndex][currentPlane].xOffset = out.getFilePointer();
            break;
          case Z_OFFSET_IN_SLIDE:
            planeOffsets[resolutionIndex][currentPlane].zOffset = out.getFilePointer();
            break;
        }
      }
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
        case IMPLICIT:
          out.write((byte[]) tag.value);
          break;
        default:
          throw new IllegalArgumentException(String.valueOf(tag.vr.getCode()));
      }
    }
  }

  /**
   * Pad the given string so that the length is a multiple of 2.
   * If the input is null, an empty string is returned.
   * Otherwise, a space is appended if necessary.
   *
   * @param value original string
   * @return padded string whose length is a multiple of 2
   */
  private String padString(String value) {
    if (value == null) {
      return "";
    }
    if (value.length() % 2 == 0) {
      return value;
    }
    return value + " ";
  }

  /**
   * @return transfer syntax UID corresponding to the current compression type
   */
  private String getTransferSyntax() {
    String transferSyntax = null;
    if (compression == null || compression.equals(CompressionType.UNCOMPRESSED.getCompression())) {
      if (out.isLittleEndian()) {
        transferSyntax = "1.2.840.10008.1.2.1";
      }
      else {
        transferSyntax = "1.2.840.10008.1.2.2";
      }
    }
    else if (compression.equals(CompressionType.J2K.getCompression())) {
      transferSyntax = "1.2.840.10008.1.2.4.91";
    }
    else if (compression.equals(CompressionType.JPEG.getCompression())) {
      transferSyntax = "1.2.840.10008.1.2.4.50";
    }
    return padString(transferSyntax);
  }

  /**
   * @return Codec instance corresponding to current compression type
   */
  private Codec getCodec() {
    if (compression.equals(CompressionType.JPEG.getCompression())) {
      return new JPEGCodec();
    }
    else if (compression.equals(CompressionType.J2K.getCompression())) {
      return new JPEG2000Codec();
    }
    return null;
  }

  private void openFile(int pyramid, int res) throws IOException {
    if (pixelDataLengthPointer == null) {
      // not fully initialized, can't reliably determine
      // filename for this series/resolution
      return;
    }
    if (out != null) {
      out.close();
    }
    out = new RandomAccessOutputStream(getFilename(pyramid, res));

    MetadataRetrieve r = getMetadataRetrieve();
    boolean littleEndian = false;
    if (r.getPixelsBigEndian(pyramid) != null) {
      littleEndian = !r.getPixelsBigEndian(pyramid).booleanValue();
    }
    else if (r.getPixelsBinDataCount(pyramid) == 0) {
      littleEndian = !r.getPixelsBinDataBigEndian(pyramid, 0).booleanValue();
    }

    out.order(littleEndian);
  }

  private String getFilename(int pyramid, int res) {
    if (pixelDataLengthPointer.length == 1) {
      return currentId;
    }
    String base = new Location(currentId).getAbsolutePath();
    base = base.substring(0, base.lastIndexOf("."));
    // TODO: this could be changed, or an option
    return String.format("%s_%d_%d.dcm", base, pyramid, res);
  }

  private int getIndex(int pyramid, int res) {
    MetadataRetrieve r = getMetadataRetrieve();
    if (r instanceof IPyramidStore) {
      int index = 0;
      for (int i=0; i<r.getImageCount(); i++) {
        int resCount = ((IPyramidStore) r).getResolutionCount(i);
        if (i < pyramid) {
          index += resCount;
        }
        else {
          return index + res;
        }
      }
    }
    return pyramid;
  }

  private int[] getZCTCoords(int no) {
    MetadataRetrieve retrieve = getMetadataRetrieve();
    DimensionOrder order = retrieve.getPixelsDimensionOrder(series);
    int sizeC = retrieve.getChannelCount(series);
    int sizeT = retrieve.getPixelsSizeT(series).getValue();
    int sizeZ = retrieve.getPixelsSizeZ(series).getValue();
    return FormatTools.getZCTCoords(order.getValue(), sizeZ, sizeC, sizeT, sizeZ*sizeC*sizeT, no);
  }

  private boolean isReallySequential() {
    MetadataRetrieve retrieve = getMetadataRetrieve();
    DimensionOrder order = retrieve.getPixelsDimensionOrder(series);
    int sizeC = retrieve.getChannelCount(series);
    int sizeT = retrieve.getPixelsSizeT(series).getValue();
    int sizeZ = retrieve.getPixelsSizeZ(series).getValue();
    return sequential && (sizeC == 1 || sizeZ == 1 ||
      order == DimensionOrder.XYZCT ||
      order == DimensionOrder.XYZTC ||
      order == DimensionOrder.XYZTC ||
      order == DimensionOrder.XYTZC);
  }

  class PlaneOffset {
    public long xOffset;
    public long yOffset;
    public long zOffset;
    public long cOffset;
    public boolean written = false;
  }

}
