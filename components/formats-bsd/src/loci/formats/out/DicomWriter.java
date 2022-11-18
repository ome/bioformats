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

import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.rmi.dgc.VMID;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Arrays;
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
import loci.formats.dicom.DicomTag;
import loci.formats.in.DynamicMetadataOptions;
import loci.formats.in.MetadataOptions;
import loci.formats.meta.IPyramidStore;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.tiff.IFD;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffCompression;
import loci.formats.tiff.TiffConstants;
import loci.formats.tiff.TiffRational;
import loci.formats.tiff.TiffSaver;

import ome.xml.model.enums.DimensionOrder;
import ome.units.UNITS;
import ome.units.quantity.Length;

import static loci.formats.dicom.DicomAttribute.*;
import static loci.formats.dicom.DicomVR.*;

/**
 * DicomWriter is the file format writer for DICOM files.
 * This is designed for whole slide images, and may not produce
 * schema-compliant files for other modalities.
 */
public class DicomWriter extends FormatWriter {

  // -- Constants --

  /** Option for setting the organization root for UIDs. */
  public static final String UID_ROOT_KEY = "dicom.uid_root";
  public static final String UID_DEFAULT_ROOT = "1";

  // see http://dicom.nema.org/medical/dicom/current/output/chtml/part06/chapter_A.html
  private static final String SOP_CLASS_UID_VALUE = "1.2.840.10008.5.1.4.1.1.77.1.6";

  // -- Fields --

  private long[] pixelDataLengthPointer;
  private int[] pixelDataSize;
  private long[] transferSyntaxPointer;
  private long[] compressionMethodPointer;
  private long[] nextIFDPointer;
  private IFD[][] ifds;
  private long fileMetaLengthPointer;
  private int baseTileWidth = 0;
  private int baseTileHeight = 0;
  private int[] tileWidth;
  private int[] tileHeight;
  private PlaneOffset[][] planeOffsets;
  private Integer currentPlane = null;
  private UIDCreator uids;

  private String instanceUIDValue;
  private String implementationUID;

  private boolean bigTiff = false;
  private TiffSaver tiffSaver;

  // -- Constructor --

  public DicomWriter() {
    super("DICOM", "dcm");
    compressionTypes = new String[] {
      CompressionType.UNCOMPRESSED.getCompression(),
      CompressionType.JPEG.getCompression(),
      CompressionType.J2K.getCompression()
    };
  }

  /**
   * Sets whether or not BigTIFF files should be written.
   * This flag is not reset when close() is called.
   */
  public void setBigTiff(boolean bigTiff) {
    FormatTools.assertId(currentId, false, 1);
    this.bigTiff = bigTiff;
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

    MetadataRetrieve r = getMetadataRetrieve();
    if ((!(r instanceof IPyramidStore) ||
      ((IPyramidStore) r).getResolutionCount(series) == 1) &&
      !isFullPlane(x, y, w, h))
    {
      throw new FormatException("DicomWriter does not allow tiles for non-pyramid images");
    }

    boolean first = x == 0 && y == 0;
    boolean last = x + w == getSizeX() && y + h == getSizeY();
    int resolutionIndex = getIndex(series, resolution);

    // the compression type isn't supplied to the writer until
    // after setId is called, so metadata that indicates or
    // depends on the compression type needs to be set in
    // the first call to saveBytes for each file
    if (first) {
      out.seek(transferSyntaxPointer[resolutionIndex]);
      out.writeBytes(getTransferSyntax());

      out.seek(compressionMethodPointer[resolutionIndex]);
      out.writeBytes(getCompressionMethod());
    }

    // TILED_SPARSE, so the tile coordinates must be written
    if (!isReallySequential()) {
      Length physicalX = r.getPixelsPhysicalSizeX(series);
      Length physicalY = r.getPixelsPhysicalSizeY(series);

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

          out.seek(offset.dimensionIndex);
          out.writeInt(x + 1);
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
    if ((x + w == getSizeX() && w < tileWidth[resolutionIndex]) ||
      (y + h == getSizeY() && h < tileHeight[resolutionIndex]))
    {
      if (interleaved || getSamplesPerPixel() == 1) {
        int srcRowLen = w * bytesPerPixel * getSamplesPerPixel();
        int destRowLen = tileWidth[resolutionIndex] * bytesPerPixel * getSamplesPerPixel();
        paddedBuf = new byte[tileHeight[resolutionIndex] * destRowLen];

        for (int row=0; row<h; row++) {
          System.arraycopy(buf, row * srcRowLen, paddedBuf, row * destRowLen, srcRowLen);
        }
      }
      else {
        int srcRowLen = w * bytesPerPixel;
        int destRowLen = tileWidth[resolutionIndex] * bytesPerPixel;
        paddedBuf = new byte[tileHeight[resolutionIndex] * destRowLen * getSamplesPerPixel()];

        for (int c=0; c<getSamplesPerPixel(); c++) {
          for (int row=0; row<h; row++) {
            int src = srcRowLen * ((c * h) + row);
            int dest = destRowLen * ((c * tileHeight[resolutionIndex]) + row);
            System.arraycopy(buf, src, paddedBuf, dest, srcRowLen);
          }
        }
      }
    }
    else {
      paddedBuf = buf;
    }

    // now we actually compress and write the pixel data

    // we need to know the tile index to write save the tile offset
    // in the IFD
    // this tries to calculate the index without assuming sequential tile
    // writing, but maybe there is a better way to calculate this?
    int xTiles = (int) Math.ceil((double) getSizeX() / tileWidth[resolutionIndex]);
    int xTile = x / tileWidth[resolutionIndex];
    int yTile = y / tileHeight[resolutionIndex];
    int tileIndex = (yTile * xTiles) + xTile;
    long[] tileByteCounts = (long[]) ifds[resolutionIndex][no].getIFDValue(IFD.TILE_BYTE_COUNTS);
    long[] tileOffsets = (long[]) ifds[resolutionIndex][no].getIFDValue(IFD.TILE_OFFSETS);

    if (compression == null || compression.equals(CompressionType.UNCOMPRESSED.getCompression())) {
      long tileOffset = out.getFilePointer();
      out.write(paddedBuf);
      if (paddedBuf.length % 2 == 1) {
        out.writeByte(0);
      }

      long length = out.getFilePointer() - start;
      pixelDataSize[resolutionIndex] += (int) length;

      out.seek(pixelDataLengthPointer[resolutionIndex]);
      out.writeInt(pixelDataSize[resolutionIndex]);

      tileByteCounts[tileIndex] = length;
      tileOffsets[tileIndex] = tileOffset;
    }
    else {
      Codec codec = getCodec();
      CodecOptions options = new CodecOptions();
      options.width = tileWidth[resolutionIndex];
      options.height = tileHeight[resolutionIndex];
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

      tileByteCounts[tileIndex] = compressed.length;

      DicomTag item = new DicomTag(ITEM, IMPLICIT);
      item.elementLength = compressed.length;
      if (pad) {
        item.elementLength++;
      }
      item.value = compressed;
      writeTag(item);
      tileOffsets[tileIndex] = out.getFilePointer() - compressed.length;
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

    uids = new UIDCreator();

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
    compressionMethodPointer = new long[totalFiles];
    nextIFDPointer = new long[totalFiles];
    ifds = new IFD[totalFiles][];

    planeOffsets = new PlaneOffset[totalFiles][];
    tileWidth = new int[totalFiles];
    tileHeight = new int[totalFiles];

    // create UIDs that must be consistent across all files in the dataset
    String specimenUIDValue = uids.getUID();
    implementationUID = uids.getUID();
    String seriesInstanceUID = uids.getUID();
    String studyInstanceUID = uids.getUID();

    for (int pyramid=0; pyramid<r.getImageCount(); pyramid++) {
      series = pyramid;
      int resolutionCount = 1;
      if (hasPyramid) {
        resolutionCount = ((IPyramidStore) r).getResolutionCount(pyramid);
      }
      for (int res=0; res<resolutionCount; res++) {
        instanceUIDValue = uids.getUID();

        resolution = res;
        int resolutionIndex = getIndex(series, resolution);

        ifds[resolutionIndex] = new IFD[getPlaneCount(pyramid)];

        ArrayList<DicomTag> tags = new ArrayList<DicomTag>();

        DicomTag imageType = new DicomTag(IMAGE_TYPE, CS);
        String pyramidName = r.getImageName(pyramid);
        String type = getImageType(pyramidName, res, hasPyramid, resolutionCount);
        imageType.value = padString(type);
        tags.add(imageType);

        // pyramid resolutions can be supplied tile-wise,
        // but extra images (label, overview, etc.) must be stored as
        // whole images in order to conform to schema
        boolean fullImage = type.indexOf("VOLUME") < 0;

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
        int sizeC = r.getPixelsSizeC(pyramid).getValue().intValue();
        int sizeT = r.getPixelsSizeT(pyramid).getValue().intValue();

        // check the number of uncompressed pixel bytes in this resolution
        // if we suspect that there will be more than 4 GB written (including tags/IFDs),
        // automatically switch to BigTIFF for this and all subsequent resolutions
        // writing BigTIFF even when not necessary is generally safer than
        // trying to write plain TIFF for larger datasets
        long rawPixelBytes = (long) width * height * bytesPerPixel * sizeZ * sizeC * sizeT;
        if (rawPixelBytes >= TiffConstants.BIG_TIFF_CUTOFF) {
          bigTiff = true;
        }

        openFile(series, resolution);

        tileWidth[resolutionIndex] = getTileSizeX();
        if (fullImage || tileWidth[resolutionIndex] <= 0) {
          tileWidth[resolutionIndex] = width;
        }
        tileHeight[resolutionIndex] = getTileSizeY();
        if (fullImage || tileHeight[resolutionIndex] <= 0) {
          tileHeight[resolutionIndex] = height;
        }

        // not valid to store planar configuration for single-channel images
        if (nChannels > 1) {
          DicomTag planarConfig = new DicomTag(PLANAR_CONFIGURATION, US);
          planarConfig.value = new short[] {(short) 0};
          tags.add(planarConfig);
        }

        DicomTag rows = new DicomTag(ROWS, US);
        rows.value = new short[] {(short) tileHeight[resolutionIndex]};
        tags.add(rows);

        DicomTag columns = new DicomTag(COLUMNS, US);
        columns.value = new short[] {(short) tileWidth[resolutionIndex]};
        tags.add(columns);

        DicomTag matrixRows = new DicomTag(TOTAL_PIXEL_MATRIX_ROWS, UL);
        matrixRows.value = new long[] {height};
        tags.add(matrixRows);

        DicomTag matrixColumns = new DicomTag(TOTAL_PIXEL_MATRIX_COLUMNS, UL);
        matrixColumns.value = new long[] {width};
        tags.add(matrixColumns);

        int tileCountX = (int) Math.ceil((double) width / tileWidth[resolutionIndex]);
        int tileCountY = (int) Math.ceil((double) height / tileHeight[resolutionIndex]);
        DicomTag numberOfFrames = new DicomTag(NUMBER_OF_FRAMES, IS);
        numberOfFrames.value = padString(String.valueOf(
          tileCountX * tileCountY * sizeZ * r.getChannelCount(pyramid)));
        tags.add(numberOfFrames);

        DicomTag matrixFrames = new DicomTag(TOTAL_PIXEL_MATRIX_FOCAL_PLANES, UL);
        matrixFrames.value = new long[] {sizeZ};
        tags.add(matrixFrames);

        DicomTag edf = new DicomTag(EXTENDED_DEPTH_OF_FIELD, CS);
        edf.value = padString("NO");
        tags.add(edf);

        DicomTag bits = new DicomTag(BITS_ALLOCATED, US);
        bits.value = new short[] {(short) (bytesPerPixel * 8)};
        tags.add(bits);

        DicomTag bitsStored = new DicomTag(BITS_STORED, US);
        bitsStored.value = bits.value;
        tags.add(bitsStored);

        DicomTag highBit = new DicomTag(HIGH_BIT, US);
        highBit.value = new short[] {(short) (out.isLittleEndian() ? (bytesPerPixel * 8) - 1 : 0)};
        tags.add(highBit);

        DicomTag pixelRepresentation = new DicomTag(PIXEL_REPRESENTATION, US);
        int pixelTypeCode = FormatTools.pixelTypeFromString(pixelType);
        boolean isSigned = FormatTools.isSigned(pixelTypeCode);
        pixelRepresentation.value = new short[] {(short) (isSigned ? 1 : 0)};
        tags.add(pixelRepresentation);

        DicomTag samplesPerPixel = new DicomTag(SAMPLES_PER_PIXEL, US);
        samplesPerPixel.value = new short[] {(short) nChannels};
        tags.add(samplesPerPixel);

        // the writer has no way to know if the source images were lossy-compressed
        // so assuming lossy compression was applied at some point is safest
        DicomTag lossy = new DicomTag(LOSSY_IMAGE_COMPRESSION, CS);
        lossy.value = "01";
        tags.add(lossy);

        // we don't know this in advance, so 1 (no reduction in size) is safest?
        DicomTag lossyRatio = new DicomTag(LOSSY_IMAGE_COMPRESSION_RATIO, DS);
        lossyRatio.value = padString("1");
        tags.add(lossyRatio);

        // this is a placeholder since the compression type won't be set until after setId
        DicomTag lossyMethod = new DicomTag(LOSSY_IMAGE_COMPRESSION_METHOD, CS);
        lossyMethod.elementLength = 12;
        tags.add(lossyMethod);

        DicomTag dimensionOrganization = new DicomTag(DIMENSION_ORGANIZATION_TYPE, CS);
        dimensionOrganization.value = padString(isReallySequential() ? "TILED_FULL" : "TILED_SPARSE");
        tags.add(dimensionOrganization);

        DicomTag dimensionOrganizationSequence = new DicomTag(DIMENSION_ORGANIZATION_SEQUENCE, SQ);
        DicomTag xDimensionUID = new DicomTag(DIMENSION_ORGANIZATION_UID, UI);
        xDimensionUID.value = padUID(uids.getUID());
        dimensionOrganizationSequence.children.add(xDimensionUID);
        DicomTag yDimensionUID = new DicomTag(DIMENSION_ORGANIZATION_UID, UI);
        yDimensionUID.value = padUID(uids.getUID());
        dimensionOrganizationSequence.children.add(yDimensionUID);
        tags.add(dimensionOrganizationSequence);

        if (!isReallySequential()) {
          DicomTag dimensionIndexSequence = new DicomTag(DIMENSION_INDEX_SEQUENCE, SQ);
          dimensionIndexSequence.children.add(makeItem());

          DicomTag xuid = new DicomTag(DIMENSION_ORGANIZATION_UID, UI);
          xuid.value = xDimensionUID.value;
          dimensionIndexSequence.children.add(xuid);

          DicomTag xPointer = new DicomTag(DIMENSION_INDEX_POINTER, AT);
          xPointer.value = makeShortArray(COLUMN_POSITION_IN_MATRIX.getTag());
          dimensionIndexSequence.children.add(xPointer);

          DicomTag groupPointer = new DicomTag(FUNCTIONAL_GROUP_POINTER, AT);
          groupPointer.value = makeShortArray(PLANE_POSITION_SLIDE_SEQUENCE.getTag());
          dimensionIndexSequence.children.add(groupPointer);

          dimensionIndexSequence.children.add(makeItemDelimitation());

          dimensionIndexSequence.children.add(makeItem());

          DicomTag yuid = new DicomTag(DIMENSION_ORGANIZATION_UID, UI);
          yuid.value = yDimensionUID.value;
          dimensionIndexSequence.children.add(yuid);

          DicomTag yPointer = new DicomTag(DIMENSION_INDEX_POINTER, AT);
          yPointer.value = makeShortArray(ROW_POSITION_IN_MATRIX.getTag());
          dimensionIndexSequence.children.add(yPointer);

          dimensionIndexSequence.children.add(groupPointer);

          dimensionIndexSequence.children.add(makeItemDelimitation());

          tags.add(dimensionIndexSequence);
        }

        DicomTag modality = new DicomTag(MODALITY, CS);
        modality.value = padString("SM");
        tags.add(modality);

        DicomTag seriesNumber = new DicomTag(SERIES_NUMBER, IS);
        seriesNumber.value = padString("1");
        tags.add(seriesNumber);
        DicomTag instanceNumber = new DicomTag(INSTANCE_NUMBER, IS);
        instanceNumber.value = padString(String.valueOf(resolutionIndex + 1));
        tags.add(instanceNumber);
        DicomTag instanceUID = new DicomTag(SOP_INSTANCE_UID, UI);
        instanceUID.value = padUID(instanceUIDValue);
        tags.add(instanceUID);
        DicomTag sopClassUID = new DicomTag(SOP_CLASS_UID, UI);
        sopClassUID.value = padUID(SOP_CLASS_UID_VALUE);
        tags.add(sopClassUID);
        DicomTag studyUID = new DicomTag(STUDY_INSTANCE_UID, UI);
        studyUID.value = padUID(studyInstanceUID);
        tags.add(studyUID);

        DicomTag seriesInstance = new DicomTag(SERIES_INSTANCE_UID, UI);
        seriesInstance.value = padUID(seriesInstanceUID);
        tags.add(seriesInstance);

        DicomTag photoInterp = new DicomTag(PHOTOMETRIC_INTERPRETATION, CS);
        photoInterp.value = padString(nChannels == 3 ? "RGB" : "MONOCHROME2");
        tags.add(photoInterp);

        if (nChannels != 3) {
          // when MONOCHROME2 is set, need to add more metadata
          // for pixel value interpretation

          DicomTag rescaleSlope = new DicomTag(RESCALE_SLOPE, DS);
          rescaleSlope.value = padString("1");
          tags.add(rescaleSlope);

          DicomTag rescaleIntercept = new DicomTag(RESCALE_INTERCEPT, DS);
          rescaleIntercept.value = padString("0");
          tags.add(rescaleIntercept);

          DicomTag presentationLutShape = new DicomTag(PRESENTATION_LUT_SHAPE, CS);
          presentationLutShape.value = padString("IDENTITY");
          tags.add(presentationLutShape);
        }

        long timestamp = System.currentTimeMillis();
        DicomTag date = new DicomTag(ACQUISITION_DATE, DA);
        date.value = DateTools.convertDate(timestamp, DateTools.UNIX, "yyyyMMdd");
        tags.add(date);
        DicomTag time = new DicomTag(ACQUISITION_TIME, TM);
        time.value = DateTools.convertDate(timestamp, DateTools.UNIX, "HHmmss");
        tags.add(time);

        DicomTag contentDate = new DicomTag(CONTENT_DATE, DA);
        contentDate.value = date.value;
        tags.add(contentDate);

        DicomTag contentTime = new DicomTag(CONTENT_TIME, TM);
        contentTime.value = time.value;
        tags.add(contentTime);

        DicomTag dateTime = new DicomTag(ACQUISITION_TIMESTAMP, DT);
        dateTime.value = padString(DateTools.convertDate(timestamp, DateTools.UNIX, "yyyyMMddHHmmss"));
        tags.add(dateTime);

        DicomTag specimenLabelInImage = new DicomTag(SPECIMEN_LABEL_IN_IMAGE, CS);
        boolean specimenLabel = type.indexOf("OVERVIEW") > 0 || type.indexOf("LABEL") > 0;
        specimenLabelInImage.value = padString(specimenLabel ? "YES" : "NO");
        tags.add(specimenLabelInImage);

        DicomTag volumetricProps = new DicomTag(VOLUMETRIC_PROPERTIES, CS);
        volumetricProps.value = padString("VOLUME");
        tags.add(volumetricProps);

        // define optical paths (OME Channels)

        DicomTag opticalPathCount = new DicomTag(NUMBER_OPTICAL_PATHS, UL);
        opticalPathCount.value = new long[] {r.getChannelCount(pyramid)};
        tags.add(opticalPathCount);

        DicomTag opticalSequence = new DicomTag(OPTICAL_PATH_SEQUENCE, SQ);
        for (int c=0; c<r.getChannelCount(pyramid); c++) {
          boolean rgb = r.getChannelSamplesPerPixel(pyramid, c).getValue() > 1;

          opticalSequence.children.add(makeItem());

          DicomTag illuminationTypeCodes = new DicomTag(ILLUMINATION_TYPE_CODE_SEQUENCE, SQ);
          illuminationTypeCodes.children.add(makeItem());

          // see http://dicom.nema.org/medical/dicom/current/output/chtml/part16/chapter_D.html
          // for definitions of the code values/meanings
          DicomTag illuminationTypeCodeValue = new DicomTag(CODE_VALUE, SH);
          illuminationTypeCodeValue.value = padString(rgb ? "111744" : "111743");
          illuminationTypeCodes.children.add(illuminationTypeCodeValue);

          DicomTag illuminationTypeCodingScheme = new DicomTag(CODING_SCHEME_DESIGNATOR, SH);
          illuminationTypeCodingScheme.value = padString("DCM");
          illuminationTypeCodes.children.add(illuminationTypeCodingScheme);

          DicomTag illuminationTypeCodeMeaning = new DicomTag(CODE_MEANING, LO);
          illuminationTypeCodeMeaning.value = padString(rgb ? "Brightfield illumination" : "Epifluorescence illumination");
          illuminationTypeCodes.children.add(illuminationTypeCodeMeaning);

          illuminationTypeCodes.children.add(makeItemDelimitation());
          opticalSequence.children.add(illuminationTypeCodes);

          DicomTag wavelength = new DicomTag(ILLUMINATION_WAVELENGTH, FL);
          Length wave = r.getChannelEmissionWavelength(pyramid, c);
          wavelength.value = new float[] {wave == null ? 1f : wave.value(UNITS.NM).floatValue()};
          opticalSequence.children.add(wavelength);

          if (rgb) {
            DicomTag iccProfile = new DicomTag(ICC_PROFILE, OB);
            iccProfile.value = ICC_Profile.getInstance(ColorSpace.CS_sRGB).getData();
            opticalSequence.children.add(iccProfile);
          }

          DicomTag opticalID = new DicomTag(OPTICAL_PATH_ID, SH);
          opticalID.value = padString(String.valueOf(c));
          opticalSequence.children.add(opticalID);

          DicomTag opticalDescription = new DicomTag(OPTICAL_PATH_DESCRIPTION, ST);
          opticalDescription.value = padString(r.getChannelName(pyramid, c));
          opticalSequence.children.add(opticalDescription);

          opticalSequence.children.add(makeItemDelimitation());
        }
        tags.add(opticalSequence);

        // define physical pixels sizes and origins
        DicomTag sharedGroupsSequence = new DicomTag(SHARED_FUNCTIONAL_GROUPS_SEQUENCE, SQ);
        sharedGroupsSequence.children.add(makeItem());

        DicomTag pixelMeasuresSequence = new DicomTag(PIXEL_MEASURES_SEQUENCE, SQ);
        pixelMeasuresSequence.children.add(makeItem());

        DicomTag sliceThickness = new DicomTag(SLICE_THICKNESS, DS);
        DicomTag sliceSpace = new DicomTag(SLICE_SPACING, DS);
        Length physicalZ = r.getPixelsPhysicalSizeZ(pyramid);
        if (physicalZ != null) {
          sliceThickness.value = padString(String.valueOf(physicalZ.value(UNITS.MM)));
        }
        else {
          // a value of 0 is not allowed, but we don't know the actual thickness or slice spacing
          sliceThickness.value = padString("1");
        }
        sliceSpace.value = sliceThickness.value;
        pixelMeasuresSequence.children.add(sliceThickness);
        pixelMeasuresSequence.children.add(sliceSpace);

        DicomTag pixelSpacing = new DicomTag(PIXEL_SPACING, DS);
        Length physicalX = r.getPixelsPhysicalSizeX(pyramid);
        Length physicalY = r.getPixelsPhysicalSizeY(pyramid);
        String px = physicalX == null ? "1" : String.valueOf(physicalX.value(UNITS.MM));
        String py = physicalY == null ? "1" : String.valueOf(physicalY.value(UNITS.MM));
        pixelSpacing.value = padString(px + "\\" + py);
        pixelMeasuresSequence.children.add(pixelSpacing);

        pixelMeasuresSequence.children.add(makeItemDelimitation());

        sharedGroupsSequence.children.add(pixelMeasuresSequence);

        DicomTag wsiFrameType = new DicomTag(WHOLE_SLIDE_FRAME_TYPE_SEQUENCE, SQ);
        DicomTag frameType = new DicomTag(FRAME_TYPE, CS);
        frameType.value = imageType.value;
        wsiFrameType.children.add(frameType);

        sharedGroupsSequence.children.add(wsiFrameType);

        sharedGroupsSequence.children.add(makeItemDelimitation());
        tags.add(sharedGroupsSequence);

        DicomTag volumeWidth = new DicomTag(IMAGED_VOLUME_WIDTH, FL);
        volumeWidth.value = new float[] {physicalX == null ? 1f : physicalX.value(UNITS.MM).floatValue() * width};
        tags.add(volumeWidth);

        DicomTag volumeHeight = new DicomTag(IMAGED_VOLUME_HEIGHT, FL);
        volumeHeight.value = new float[] {physicalY == null ? 1f : physicalY.value(UNITS.MM).floatValue() * height};
        tags.add(volumeHeight);

        // as with slice thickness above, when the physical Z size is missing
        // we don't know the volume depth, and setting it to 0 is invalid
        DicomTag volumeDepth = new DicomTag(IMAGED_VOLUME_DEPTH, FL);
        volumeDepth.value = new float[] {physicalZ == null ? 1f : physicalZ.value(UNITS.MM).floatValue() * sizeZ};
        tags.add(volumeDepth);

        DicomTag originSequence = new DicomTag(TOTAL_PIXEL_MATRIX_ORIGIN_SEQUENCE, SQ);
        originSequence.children.add(makeItem());

        DicomTag xOffset = new DicomTag(X_OFFSET_IN_SLIDE, DS);
        xOffset.value = padString("0");
        originSequence.children.add(xOffset);
        DicomTag yOffset = new DicomTag(Y_OFFSET_IN_SLIDE, DS);
        yOffset.value = padString("0");
        originSequence.children.add(yOffset);

        originSequence.children.add(makeItemDelimitation());
        tags.add(originSequence);

        DicomTag imageOrientation = new DicomTag(SLIDE_IMAGE_ORIENTATION, DS);
        imageOrientation.value = padString("1\\0\\0\\0\\1\\0");
        tags.add(imageOrientation);

        // placeholder tile positions for TILED_SPARSE
        if (!isReallySequential()) {
          planeOffsets[resolutionIndex] = new PlaneOffset[getPlaneCount(pyramid) * tileCountX * tileCountY];
          DicomTag perFrameSequence = new DicomTag(PER_FRAME_FUNCTIONAL_GROUPS_SEQUENCE, SQ);
          for (int p=0; p<planeOffsets[resolutionIndex].length; p++) {
            perFrameSequence.children.add(makeItem());

            DicomTag frameContentSequence = new DicomTag(FRAME_CONTENT_SEQUENCE, SQ);
            frameContentSequence.children.add(makeItem());

            DicomTag dimensionIndexValues = new DicomTag(DIMENSION_INDEX_VALUES, UL);
            dimensionIndexValues.value = new long[] {1, 1};
            frameContentSequence.children.add(dimensionIndexValues);

            frameContentSequence.children.add(makeItemDelimitation());
            perFrameSequence.children.add(frameContentSequence);

            // the values here don't matter, they will be overwritten when saveBytes is called
            DicomTag opticalPath = new DicomTag(OPTICAL_PATH_ID_SEQUENCE, SQ);
            DicomTag opticalPathID = new DicomTag(OPTICAL_PATH_ID, SH);
            opticalPathID.value = padString("0");
            opticalPath.children.add(opticalPathID);
            perFrameSequence.children.add(opticalPath);

            DicomTag plane = new DicomTag(PLANE_POSITION_SLIDE_SEQUENCE, SQ);
            plane.children.add(makeItem());

            DicomTag offsetX = new DicomTag(X_OFFSET_IN_SLIDE, DS);
            offsetX.value = padString(physicalX == null ? "0" : padString(String.valueOf(physicalX.value(UNITS.MM).floatValue() * width)));
            plane.children.add(offsetX);

            DicomTag offsetY = new DicomTag(Y_OFFSET_IN_SLIDE, DS);
            offsetY.value = padString(physicalY == null ? "0" : padString(String.valueOf(physicalY.value(UNITS.MM).floatValue() * height)));
            plane.children.add(offsetY);

            DicomTag positionZ = new DicomTag(Z_OFFSET_IN_SLIDE, DS);
            positionZ.value = padString("0");
            plane.children.add(positionZ);

            DicomTag positionX = new DicomTag(COLUMN_POSITION_IN_MATRIX, SL);
            positionX.value = new int[] {1};
            plane.children.add(positionX);
            DicomTag positionY = new DicomTag(ROW_POSITION_IN_MATRIX, SL);
            positionY.value = new int[] {1};
            plane.children.add(positionY);

            plane.children.add(makeItemDelimitation());

            perFrameSequence.children.add(plane);
            perFrameSequence.children.add(makeItemDelimitation());
          }

          tags.add(perFrameSequence);
        }

        // tags that are required, but for which we don't have a reliable value
        // explicitly setting values to empty strings isn't necessary,
        // but is done for clarity
        // in cases where an empty value is not allowed, the intent is to
        // pick the safest default value (e.g. assuming a burned-in annotation)
        DicomTag patientName = new DicomTag(PATIENT_NAME, PN);
        patientName.value = padString("");
        tags.add(patientName);

        DicomTag patientID = new DicomTag(PATIENT_ID, LO);
        patientID.value = padString("");
        tags.add(patientID);

        DicomTag patientBirthDate = new DicomTag(PATIENT_BIRTH_DATE, DA);
        patientBirthDate.value = padString("");
        tags.add(patientBirthDate);

        DicomTag patientSex = new DicomTag(PATIENT_SEX, CS);
        patientSex.value = padString("");
        tags.add(patientSex);

        DicomTag studyID = new DicomTag(STUDY_ID, SH);
        studyID.value = padString("");
        tags.add(studyID);

        DicomTag studyDate = new DicomTag(STUDY_DATE, DA);
        studyDate.value = padString("");
        tags.add(studyDate);

        DicomTag studyTime = new DicomTag(STUDY_TIME, TM);
        studyTime.value = padString("");
        tags.add(studyTime);

        DicomTag accessionNumber = new DicomTag(ACCESSION_NUMBER, SH);
        accessionNumber.value = padString("");
        tags.add(accessionNumber);

        DicomTag manufacturer = new DicomTag(MANUFACTURER, LO);
        manufacturer.value = padString("UNKNOWN");
        tags.add(manufacturer);

        DicomTag manufacturerModelName = new DicomTag(MANUFACTURER_MODEL_NAME, LO);
        manufacturerModelName.value = padString("UNKNOWN");
        tags.add(manufacturerModelName);

        DicomTag deviceSerialNumber = new DicomTag(DEVICE_SERIAL_NUMBER, LO);
        deviceSerialNumber.value = padString("UNKNOWN");
        tags.add(deviceSerialNumber);

        DicomTag softwareVersion = new DicomTag(SOFTWARE_VERSION, LO);
        softwareVersion.value = padString("UNKNOWN");
        tags.add(softwareVersion);

        DicomTag focus = new DicomTag(FOCUS_METHOD, CS);
        focus.value = padString("MANUAL");
        tags.add(focus);

        DicomTag burnedAnnotation = new DicomTag(BURNED_IN_ANNOTATION, CS);
        burnedAnnotation.value = padString("YES");
        tags.add(burnedAnnotation);

        DicomTag referringPhysician = new DicomTag(REFERRING_PHYSICIAN_NAME, PN);
        referringPhysician.value = padString("");
        tags.add(referringPhysician);

        DicomTag containerID = new DicomTag(CONTAINER_ID, LO);
        containerID.value = padString("UNKNOWN");
        tags.add(containerID);

        DicomTag containerIDSequence = new DicomTag(CONTAINER_ID_ISSUER_SEQUENCE, SQ);
        tags.add(containerIDSequence);

        DicomTag containerTypeCodeSequence = new DicomTag(CONTAINER_TYPE_CODE_SEQUENCE, SQ);
        tags.add(containerTypeCodeSequence);

        DicomTag specimenDescription = new DicomTag(SPECIMEN_DESCRIPTION_SEQUENCE, SQ);
        specimenDescription.children.add(makeItem());

        DicomTag specimenID = new DicomTag(SPECIMEN_ID, LO);
        specimenID.value = containerID.value;
        specimenDescription.children.add(specimenID);

        DicomTag specimenUID = new DicomTag(SPECIMEN_UID, UI);
        specimenUID.value = padUID(specimenUIDValue);
        specimenDescription.children.add(specimenUID);

        DicomTag specimenIDIssuer = new DicomTag(SPECIMEN_ID_ISSUER_SEQUENCE, SQ);
        specimenDescription.children.add(specimenIDIssuer);

        DicomTag specimenPrep = new DicomTag(SPECIMEN_PREP_SEQUENCE, SQ);
        specimenDescription.children.add(specimenPrep);

        specimenDescription.children.add(makeItemDelimitation());
        tags.add(specimenDescription);

        DicomTag acquisitionContext = new DicomTag(ACQUISITION_CONTEXT_SEQUENCE, SQ);
        tags.add(acquisitionContext);

        if (type.indexOf("LABEL") > 0) {
          DicomTag barcode = new DicomTag(BARCODE_VALUE, LT);
          barcode.value = padString("");
          tags.add(barcode);

          DicomTag labelText = new DicomTag(LABEL_TEXT, UT);
          labelText.value = padString("");
          tags.add(labelText);
        }

        // sort tags into ascending order, then write

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

        // construct one IFD per plane
        // saveBytes will fill in the tile offsets and byte counts
        // close will write the IFDs to the file(s)
        for (int plane=0; plane<ifds[resolutionIndex].length; plane++) {
          int c = getZCTCoords(plane)[1];
          boolean rgb = nChannels > 1;

          IFD ifd = new IFD();
          ifd.put(IFD.LITTLE_ENDIAN, out.isLittleEndian());
          ifd.put(IFD.IMAGE_WIDTH, (long) width);
          ifd.put(IFD.IMAGE_LENGTH, (long) height);
          ifd.put(IFD.TILE_WIDTH, tileWidth[resolutionIndex]);
          ifd.put(IFD.TILE_LENGTH, tileHeight[resolutionIndex]);
          ifd.put(IFD.COMPRESSION, getTIFFCompression().getCode());
          ifd.put(IFD.PLANAR_CONFIGURATION, rgb ? 2 : 1);

          int sampleFormat = 1;
          if (FormatTools.isFloatingPoint(pixelTypeCode)) {
            sampleFormat = 3;
          }
          else if (FormatTools.isSigned(pixelTypeCode)) {
            sampleFormat = 2;
          }

          ifd.put(IFD.SAMPLE_FORMAT, sampleFormat);

          int[] bps = new int[rgb ? nChannels : 1];
          Arrays.fill(bps, FormatTools.getBytesPerPixel(pixelTypeCode) * 8);
          ifd.put(IFD.BITS_PER_SAMPLE, bps);

          ifd.put(IFD.PHOTOMETRIC_INTERPRETATION,
            rgb ? PhotoInterp.RGB.getCode() : PhotoInterp.BLACK_IS_ZERO.getCode());
          ifd.put(IFD.SAMPLES_PER_PIXEL, bps.length);

          ifd.put(IFD.SOFTWARE, FormatTools.CREATOR);

          int tileCount = tileCountX * tileCountY * bps.length;
          ifd.put(IFD.TILE_BYTE_COUNTS, new long[tileCount]);
          ifd.put(IFD.TILE_OFFSETS, new long[tileCount]);

          ifd.put(IFD.RESOLUTION_UNIT, 3);
          ifd.put(IFD.X_RESOLUTION, getPhysicalSize(physicalX));
          ifd.put(IFD.Y_RESOLUTION, getPhysicalSize(physicalY));

          ifds[resolutionIndex][plane] = ifd;
        }

      }
    }
    setSeries(0);
  }

  /* @see loci.formats.FormatWriter#close() */
  @Override
  public void close() throws IOException {
    // write IFDs to the end of each file

    MetadataRetrieve r = getMetadataRetrieve();
    for (int pyramid=0; pyramid<r.getImageCount(); pyramid++) {
      int resolutionCount = 1;
      if (r instanceof IPyramidStore) {
        resolutionCount = ((IPyramidStore) r).getResolutionCount(pyramid);
      }
      for (int res=0; res<resolutionCount; res++) {
        resolution = res;
        openFile(pyramid, resolution);
        int resolutionIndex = getIndex(pyramid, resolution);

        out.seek(out.length());

        // write the extra DICOM tag?
        DicomTag trailingPadding = new DicomTag(TRAILING_PADDING, OB);
        trailingPadding.elementLength = (int) 0xffffffff;
        writeTag(trailingPadding);

        out.seek(out.length());
        long fp = out.getFilePointer();
        writeIFDs(resolutionIndex);
        long length = out.getFilePointer() - fp;
        out.seek(fp - 4);
        out.writeInt((int) length);
      }
    }

    super.close();

    uids = null;
    pixelDataSize = null;
    pixelDataLengthPointer = null;
    transferSyntaxPointer = null;
    compressionMethodPointer = null;
    fileMetaLengthPointer = 0;
    nextIFDPointer = null;
    ifds = null;
    tiffSaver = null;

    // intentionally don't reset tile dimensions
  }

  @Override
  public int setTileSizeX(int tileSize) throws FormatException {
    baseTileWidth = tileSize;
    return baseTileWidth;
  }

  @Override
  public int getTileSizeX() {
    return baseTileWidth;
  }

  @Override
  public int setTileSizeY(int tileSize) throws FormatException {
    baseTileHeight = tileSize;
    return baseTileHeight;
  }

  @Override
  public int getTileSizeY() {
    return baseTileHeight;
  }

  // -- DicomWriter-specific methods --

  public String getUIDRoot() {
    MetadataOptions options = getMetadataOptions();
    if (options instanceof DynamicMetadataOptions) {
      String root = ((DynamicMetadataOptions) options).get(UID_ROOT_KEY, UID_DEFAULT_ROOT);
    }
    return UID_DEFAULT_ROOT;
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
      else if (tag.attribute == LOSSY_IMAGE_COMPRESSION_METHOD) {
        compressionMethodPointer[getIndex(series, resolution)] = out.getFilePointer();
      }
      else if (tag.attribute == FILE_META_INFO_GROUP_LENGTH) {
        fileMetaLengthPointer = out.getFilePointer();
      }

      // sequences with no items still need to write a SequenceDelimitationItem below
      if (tag.children.size() == 0 && tag.value == null && tag.vr != SQ) {
        if (tag.attribute != PIXEL_DATA) {
          out.skipBytes(tag.elementLength);
        }
        return;
      }
    }

    if (currentPlane != null && tag.attribute == DIMENSION_INDEX_VALUES) {
      currentPlane++;
    }

    if (tag.children.size() > 0 || (tag.value == null && tag.vr == SQ)) {
      if (tag.attribute == PER_FRAME_FUNCTIONAL_GROUPS_SEQUENCE) {
        currentPlane = -1;
      }

      if (tag.children.size() > 0 && tag.children.get(0).attribute == ITEM) {
        // items are already defined in the list of children
        for (DicomTag child : tag.children) {
          writeTag(child);
        }
      }
      else {
        // items are not yet defined and need to be added for each child tag
        for (DicomTag child : tag.children) {
          if (tag.vr == SQ) {
            writeTag(makeItem());
          }

          writeTag(child);
          if (tag.vr == SQ) {
            writeTag(makeItemDelimitation());
          }
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
          case DIMENSION_INDEX_VALUES:
            planeOffsets[resolutionIndex][currentPlane].dimensionIndex = out.getFilePointer();
            break;
          case X_OFFSET_IN_SLIDE:
            planeOffsets[resolutionIndex][currentPlane].xOffsetReal = out.getFilePointer();
            planeOffsets[resolutionIndex][currentPlane].xOffsetSize = tag.elementLength;
            break;
          case Y_OFFSET_IN_SLIDE:
            planeOffsets[resolutionIndex][currentPlane].yOffsetReal = out.getFilePointer();
            planeOffsets[resolutionIndex][currentPlane].yOffsetSize = tag.elementLength;
            break;
          case Z_OFFSET_IN_SLIDE:
            planeOffsets[resolutionIndex][currentPlane].zOffset = out.getFilePointer();
            planeOffsets[resolutionIndex][currentPlane].zOffsetSize = tag.elementLength;
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
   * This should not be used for UID values.
   *
   * @param value original string
   * @return padded string whose length is a multiple of 2
   */
  private String padString(String value) {
    return padString(value, " ");
  }

  /**
   * Pad the given string so that the length is a multiple of 2.
   * If the input is null, an empty string is returned.
   * Otherwise, a null character is appended if necessary.
   * This should not be used for non-UID values.
   *
   * @param value original string
   * @return padded string whose length is a multiple of 2
   */
  private String padUID(String value) {
    return padString(value, "\0");
  }

  private String padString(String value, String append) {
    if (value == null) {
      return "";
    }
    if (value.length() % 2 == 0) {
      return value;
    }
    return value + append;
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
   * @return compression method corresponding to the current compression type
   */
  private String getCompressionMethod() {
    if (compression != null) {
      if (compression.equals(CompressionType.J2K.getCompression())) {
        return padString("ISO_15444_1");
      }
      else if (compression.equals(CompressionType.JPEG.getCompression())) {
        return padString("ISO_10918_1");
      }
    }
    // this isn't a valid value, but I think the only other alternative is
    // setting the lossy flag to 0 (which may be incorrect)
    return padString("NOT_DEFINED");
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
    String filename = getFilename(pyramid, res);
    out = new RandomAccessOutputStream(filename);
    tiffSaver = new TiffSaver(out, filename);
    tiffSaver.setBigTiff(bigTiff);

    MetadataRetrieve r = getMetadataRetrieve();
    boolean littleEndian = false;
    if (r.getPixelsBigEndian(pyramid) != null) {
      littleEndian = !r.getPixelsBigEndian(pyramid).booleanValue();
    }
    else if (r.getPixelsBinDataCount(pyramid) == 0) {
      littleEndian = !r.getPixelsBinDataBigEndian(pyramid, 0).booleanValue();
    }

    out.order(littleEndian);
    if (out.length() == 0) {
      writeHeader();
    }
  }

  /**
   * Write the preamble, prefix, and file meta elements.
   * See http://dicom.nema.org/medical/dicom/current/output/html/part10.html#sect_7.1
   */
  private void writeHeader() throws IOException {
    // write a TIFF header in the preamble
    boolean littleEndian = out.isLittleEndian();
    if (littleEndian) {
      out.writeByte(TiffConstants.LITTLE);
      out.writeByte(TiffConstants.LITTLE);
    }
    else {
      out.writeByte(TiffConstants.BIG);
      out.writeByte(TiffConstants.BIG);
    }
    if (bigTiff) {
      out.writeShort(TiffConstants.BIG_TIFF_MAGIC_NUMBER);
      out.writeShort(8); // number of bytes in an offset
      out.writeShort(0); // reserved

      nextIFDPointer[getIndex(series, resolution)] = out.getFilePointer();
      out.writeLong(-1); // placeholder to first IFD
    }
    else {
      out.writeShort(TiffConstants.MAGIC_NUMBER);
      nextIFDPointer[getIndex(series, resolution)] = out.getFilePointer();
      out.writeInt(-1); // placeholder to first IFD
    }

    // seek to end of preamble, then write DICOM header
    out.seek(128);
    out.order(true);
    out.writeBytes("DICM");

    DicomTag fileMetaLength = new DicomTag(FILE_META_INFO_GROUP_LENGTH, UL);
    // placeholder value, overwritten at the end of this method
    fileMetaLength.value = new long[] {0};
    writeTag(fileMetaLength);

    DicomTag fileMetaVersion = new DicomTag(FILE_META_INFO_VERSION, OB);
    fileMetaVersion.value = new byte[] {0, 1};
    writeTag(fileMetaVersion);

    DicomTag mediaStorageClassUID = new DicomTag(MEDIA_SOP_CLASS_UID, UI);
    mediaStorageClassUID.value = padUID(SOP_CLASS_UID_VALUE);
    writeTag(mediaStorageClassUID);

    DicomTag mediaStorageInstanceUID = new DicomTag(MEDIA_SOP_INSTANCE_UID, UI);
    mediaStorageInstanceUID.value = padUID(instanceUIDValue);
    writeTag(mediaStorageInstanceUID);

    // placeholder, will be overwritten on the first call to saveBytes
    DicomTag transferSyntaxUID = new DicomTag(TRANSFER_SYNTAX_UID, UI);
    transferSyntaxUID.elementLength = 22;
    writeTag(transferSyntaxUID);

    DicomTag implementationClassUID = new DicomTag(IMPLEMENTATION_UID, UI);
    implementationClassUID.value = padUID(implementationUID);
    writeTag(implementationClassUID);

    DicomTag implementationVersionName = new DicomTag(IMPLEMENTATION_VERSION, SH);
    implementationVersionName.value = padString(FormatTools.VERSION);
    writeTag(implementationVersionName);

    // count all bytes after the file meta length value
    int fileMetaBytes = (int) (out.getFilePointer() - fileMetaLengthPointer - 4);
    out.seek(fileMetaLengthPointer);
    out.writeInt(fileMetaBytes);
    fileMetaLengthPointer = 0;
    out.skipBytes(fileMetaBytes);

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
    int sizeC = retrieve.getChannelCount(series);
    int sizeT = retrieve.getPixelsSizeT(series).getValue();
    int sizeZ = retrieve.getPixelsSizeZ(series).getValue();

    // single plane and single resolution implies sequential doesn't matter
    if (retrieve instanceof IPyramidStore &&
      ((IPyramidStore) retrieve).getResolutionCount(series) == 1)
    {
      return sizeC * sizeZ * sizeT == 1;
    }

    DimensionOrder order = retrieve.getPixelsDimensionOrder(series);
    return sequential && (sizeC == 1 || sizeZ == 1 ||
      order == DimensionOrder.XYZCT ||
      order == DimensionOrder.XYZTC ||
      order == DimensionOrder.XYZTC ||
      order == DimensionOrder.XYTZC);
  }

  private String getImageType(String pyramidName, int res, boolean hasPyramid, int resolutionCount) {
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

    return type;
  }

  private void writeIFDs(int resIndex) throws IOException {
    long ifdStart = out.getFilePointer();
    out.seek(nextIFDPointer[resIndex]);
    if (bigTiff) {
      out.writeLong(ifdStart);
    }
    else {
      out.writeInt((int) ifdStart);
    }
    out.seek(ifdStart);

    for (int no=0; no<ifds[resIndex].length; no++) {
      try {
        tiffSaver.writeIFD(ifds[resIndex][no], 0, no < ifds[resIndex].length - 1);
      }
      catch (FormatException e) {
        throw new IOException("Failed to write IFD for coreIndex=" + resIndex + ", plane=" + no, e);
      }
    }
  }

  private TiffCompression getTIFFCompression() {
    if (CompressionType.J2K.getCompression().equals(compression)) {
      return TiffCompression.JPEG_2000;
    }
    else if (CompressionType.JPEG.getCompression().equals(compression)) {
      return TiffCompression.JPEG;
    }
    return TiffCompression.UNCOMPRESSED;
  }

  /**
   * @return item tag with an undefined length
   */
  private DicomTag makeItem() {
    DicomTag item = new DicomTag(ITEM, IMPLICIT);
    item.elementLength = (int) 0xffffffff;
    return item;
  }

  /**
   * @return item delimitation tag with 0 length
   */
  private DicomTag makeItemDelimitation() {
    DicomTag item = new DicomTag(ITEM_DELIMITATION_ITEM, IMPLICIT);
    item.elementLength = 0;
    return item;
  }

  private short[] makeShortArray(int v) {
    short[] s = new short[2];
    s[0] = (short) ((v >> 16) & 0xffff);
    s[1] = (short) (v & 0xffff);
    return s;
  }

  private TiffRational getPhysicalSize(Length size) {
    if (size == null || size.value(UNITS.MICROMETER) == null) {
      return new TiffRational(0, 1000);
    }
    Double physicalSize = size.value(UNITS.MICROMETER).doubleValue();
    if (physicalSize.doubleValue() != 0) {
      physicalSize = 1d / physicalSize;
    }

    return new TiffRational((long) (physicalSize * 1000 * 10000), 1000);
  }

  class PlaneOffset {
    public long xOffset;
    public long yOffset;
    public long zOffset;
    public long cOffset;
    public long xOffsetReal;
    public long yOffsetReal;
    public long dimensionIndex;

    public int xOffsetSize;
    public int yOffsetSize;
    public int zOffsetSize;

    public boolean written = false;
  }

  /**
   * Helper class for creating UIDs.
   */
  class UIDCreator {
    private static final int MAX_LEN = 64;
    private String vmid = String.valueOf(new VMID().hashCode() & 0xffffffffL);

    public String getUID() {
      UID uid = new UID();

      // now we have a unique root (vmid) and unique ID to combine

      String[] tokens = uid.toString().split(":");
      StringBuffer thisUID = new StringBuffer(getUIDRoot());
      thisUID.append(".");
      thisUID.append(vmid);

      for (String token : tokens) {
        thisUID.append(".");
        long v = Long.parseLong(token, 16) & 0xffffffffL;
        String s = String.valueOf(v);
        while (thisUID.length() + s.length() > MAX_LEN) {
          v /= 8;
          s = String.valueOf(v);
        }
        thisUID.append(s);
      }
      return thisUID.toString();
    }
  }

}
