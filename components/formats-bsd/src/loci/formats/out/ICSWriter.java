/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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
import java.util.ArrayList;
import java.util.List;

import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataRetrieve;

import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * ICSWriter is the file format writer for ICS files.  It writes ICS version 1
 * and 2 files.
 */
public class ICSWriter extends FormatWriter {

  // -- Fields --

  private long dimensionOffset;
  private int dimensionLength;
  private long pixelOffset;
  private int lastPlane = -1;
  private RandomAccessOutputStream pixels;

  private List<String> uniqueFiles = new ArrayList<String>();

  // NB: write in ZTC order by default.  Certain software (e.g. Volocity)
  //     lacks the capacity to import files with any other dimension
  //     ordering.  Technically, this is not our problem, but it is
  //     easy enough to work around and makes life easier for our users.
  private String outputOrder = "XYZTC";

  // -- Constructor --

  public ICSWriter() {
    super("Image Cytometry Standard", new String[] {"ids", "ics"});
  }

  // -- ICSWriter API methods --

  /**
   * Set the order in which dimensions should be written to the file.
   * Valid values are specified in the documentation for
   * {@link loci.formats.IFormatReader#getDimensionOrder()}
   *
   * By default, the ordering is "XYZTC".
   */
  public void setOutputOrder(String outputOrder) {
    this.outputOrder = outputOrder;
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

    if (pixels == null) {
      pixels = new RandomAccessOutputStream(currentId);
    }

    MetadataRetrieve meta = getMetadataRetrieve();

    int rgbChannels = getSamplesPerPixel();

    String order = meta.getPixelsDimensionOrder(series).getValue();
    int sizeZ = meta.getPixelsSizeZ(series).getValue().intValue();
    int sizeC = meta.getChannelCount(series);
    if (rgbChannels <= sizeC) {
      sizeC /= rgbChannels;
    }

    int sizeT = meta.getPixelsSizeT(series).getValue().intValue();
    int planes = sizeZ * sizeC * sizeT;

    int[] coords =
      FormatTools.getZCTCoords(order, sizeZ, sizeC, sizeT, planes, no);
    int realIndex =
      FormatTools.getIndex(outputOrder, sizeZ, sizeC, sizeT, planes,
      coords[0], coords[1], coords[2]);
    if (uniqueFiles.size() > 1) {
      realIndex = no;
    }

    int sizeX = meta.getPixelsSizeX(series).getValue().intValue();
    int sizeY = meta.getPixelsSizeY(series).getValue().intValue();
    int pixelType =
      FormatTools.pixelTypeFromString(meta.getPixelsType(series).toString());
    int bytesPerPixel = FormatTools.getBytesPerPixel(pixelType);
    long planeSize = sizeX * sizeY * rgbChannels * bytesPerPixel;

    if (!initialized[series][realIndex]) {
      initialized[series][realIndex] = true;

      if (!isFullPlane(x, y, w, h)) {
        // write a dummy plane that will be overwritten in sections
        pixels.seek(pixelOffset + (realIndex + 1) * planeSize);
      }
    }

    pixels.seek(pixelOffset + realIndex * planeSize);
    if (isFullPlane(x, y, w, h) && (interleaved || rgbChannels == 1)) {
      pixels.write(buf);
    }
    else {
      pixels.skipBytes(bytesPerPixel * rgbChannels * sizeX * y);
      for (int row=0; row<h; row++) {
        ByteArrayOutputStream strip = new ByteArrayOutputStream();
        for (int col=0; col<w; col++) {
          for (int c=0; c<rgbChannels; c++) {
            int index = interleaved ? rgbChannels * (row * w + col) + c :
              w * (c * h + row) + col;
            strip.write(buf, index * bytesPerPixel, bytesPerPixel);
          }
        }
        pixels.skipBytes(bytesPerPixel * rgbChannels * x);
        pixels.write(strip.toByteArray());
        pixels.skipBytes(bytesPerPixel * rgbChannels * (sizeX - w - x));
      }
    }
    lastPlane = no;
    if (lastPlane != getPlaneCount() - 1 || uniqueFiles.size() > 1) {
      overwriteDimensions(getMetadataRetrieve());
    }

    pixels.close();
    pixels = null;
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
  @Override
  public boolean canDoStacks() { return true; }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  @Override
  public int[] getPixelTypes(String codec) {
    return new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,
      FormatTools.UINT16, FormatTools.INT32, FormatTools.UINT32,
      FormatTools.FLOAT};
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.FormatWriter#setId(String) */
  @Override
  public void setId(String id) throws FormatException, IOException {
    super.setId(id);

    if (!uniqueFiles.contains(id)) {
      uniqueFiles.add(id);
    }

    if (checkSuffix(currentId, "ids")) {
      String metadataFile = currentId.substring(0, currentId.lastIndexOf("."));
      metadataFile += ".ics";
      out.close();
      out = new RandomAccessOutputStream(metadataFile);
    }

    if (out.length() == 0) {
      out.writeBytes("\t\n");
      if (checkSuffix(id, "ids")) {
        out.writeBytes("ics_version\t1.0\n");
      }
      else {
        out.writeBytes("ics_version\t2.0\n");
      }
      out.writeBytes("filename\t" + currentId + "\n");
      out.writeBytes("layout\tparameters\t6\n");

      MetadataRetrieve meta = getMetadataRetrieve();
      MetadataTools.verifyMinimumPopulated(meta, series);

      int pixelType =
        FormatTools.pixelTypeFromString(meta.getPixelsType(series).toString());

      dimensionOffset = out.getFilePointer();
      int[] sizes = overwriteDimensions(meta);
      dimensionLength = (int) (out.getFilePointer() - dimensionOffset);

      if (validBits != 0) {
        out.writeBytes("layout\tsignificant_bits\t" + validBits + "\n");
      }

      boolean signed = FormatTools.isSigned(pixelType);
      boolean littleEndian = false;
      if (meta.getPixelsBigEndian(series) != null) {
        littleEndian = !meta.getPixelsBigEndian(series).booleanValue();
      }
      else if (meta.getPixelsBinDataCount(series) == 0) {
        littleEndian = !meta.getPixelsBinDataBigEndian(series, 0).booleanValue();
      }

      out.writeBytes("representation\tformat\t" +
        (pixelType == FormatTools.FLOAT ? "real\n" : "integer\n"));
      out.writeBytes("representation\tsign\t" +
        (signed ? "signed\n" : "unsigned\n"));
      out.writeBytes("representation\tcompression\tuncompressed\n");
      out.writeBytes("representation\tbyte_order\t");
      for (int i=0; i<sizes[0]/8; i++) {
        if ((littleEndian &&
          (sizes[0] < 32 || pixelType == FormatTools.FLOAT)) ||
          (!littleEndian && sizes[0] >= 32 && pixelType != FormatTools.FLOAT))
        {
          out.writeBytes((i + 1) + "\t");
        }
        else {
          out.writeBytes(((sizes[0] / 8) - i) + "\t");
        }
      }

      out.writeBytes("\nparameter\tscale\t1.000000\t");

      final StringBuilder units = new StringBuilder();
      for (int i=0; i<outputOrder.length(); i++) {
        char dim = outputOrder.charAt(i);
        Number value = 1.0;
        if (dim == 'X') {
          if (meta.getPixelsPhysicalSizeX(0) != null) {
            value = meta.getPixelsPhysicalSizeX(0).value(UNITS.MICROMETER).doubleValue();
          }
          units.append("micrometers\t");
        }
        else if (dim == 'Y') {
          if (meta.getPixelsPhysicalSizeY(0) != null) {
            value = meta.getPixelsPhysicalSizeY(0).value(UNITS.MICROMETER).doubleValue();
          }
          units.append("micrometers\t");
        }
        else if (dim == 'Z') {
          if (meta.getPixelsPhysicalSizeZ(0) != null) {
            value = meta.getPixelsPhysicalSizeZ(0).value(UNITS.MICROMETER).doubleValue();
          }
          units.append("micrometers\t");
        }
        else if (dim == 'T') {
          Time valueTime = meta.getPixelsTimeIncrement(0);
          if (valueTime != null) {
            value = valueTime.value(UNITS.SECOND);
            units.append("seconds\t");
          }
        }
        out.writeBytes(value + "\t");
      }

      out.writeBytes("\nparameter\tunits\tbits\t" + units.toString() + "\n");
      out.writeBytes("\nend\n");
      pixelOffset = out.getFilePointer();
    }
    else if (checkSuffix(currentId, "ics")) {
      RandomAccessInputStream in = new RandomAccessInputStream(currentId);
      in.findString("\nend\n");
      pixelOffset = in.getFilePointer();
      in.close();
    }

    if (checkSuffix(currentId, "ids")) {
      pixelOffset = 0;
    }
  }

  /* @see loci.formats.IFormatHandler#close() */
  @Override
  public void close() throws IOException {
    super.close();
    pixelOffset = 0;
    lastPlane = -1;
    dimensionOffset = 0;
    dimensionLength = 0;
    if (pixels != null) {
      pixels.close();
    }
    pixels = null;
    uniqueFiles.clear();
  }

  // -- Helper methods --

  private int[] overwriteDimensions(MetadataRetrieve meta) throws IOException {
    out.seek(dimensionOffset);
    int sizeX = meta.getPixelsSizeX(series).getValue().intValue();
    int sizeY = meta.getPixelsSizeY(series).getValue().intValue();
    int z = meta.getPixelsSizeZ(series).getValue().intValue();
    int c = meta.getPixelsSizeC(series).getValue().intValue();
    int t = meta.getPixelsSizeT(series).getValue().intValue();
    int pixelType =
      FormatTools.pixelTypeFromString(meta.getPixelsType(series).toString());
    int bytesPerPixel = FormatTools.getBytesPerPixel(pixelType);
    int rgbChannels = getSamplesPerPixel();

    if (lastPlane < 0) lastPlane = z * c * t - 1;
    int[] pos =
      FormatTools.getZCTCoords(outputOrder, z, c, t, z * c * t, lastPlane);

    final StringBuilder dimOrder = new StringBuilder();
    int[] sizes = new int[6];
    int nextSize = 0;
    sizes[nextSize++] = 8 * bytesPerPixel;

    if (rgbChannels > 1) {
      dimOrder.append("ch\t");
      sizes[nextSize++] = pos[1] + 1;
    }

    for (int i=0; i<outputOrder.length(); i++) {
      if (outputOrder.charAt(i) == 'X') sizes[nextSize++] = sizeX;
      else if (outputOrder.charAt(i) == 'Y') sizes[nextSize++] = sizeY;
      else if (outputOrder.charAt(i) == 'Z') sizes[nextSize++] = pos[0] + 1;
      else if (outputOrder.charAt(i) == 'T') sizes[nextSize++] = pos[2] + 1;
      else if (outputOrder.charAt(i) == 'C' && dimOrder.indexOf("ch") == -1) {
        sizes[nextSize++] = pos[1] + 1;
        dimOrder.append("ch");
      }
      if (outputOrder.charAt(i) != 'C') {
        dimOrder.append(String.valueOf(outputOrder.charAt(i)).toLowerCase());
      }
      dimOrder.append("\t");
    }
    out.writeBytes("layout\torder\tbits\t" + dimOrder.toString() + "\n");
    out.writeBytes("layout\tsizes\t");
    for (int i=0; i<sizes.length; i++) {
      out.writeBytes(sizes[i] + "\t");
    }
    while ((out.getFilePointer() - dimensionOffset) < dimensionLength - 1) {
      out.writeBytes(" ");
    }
    out.writeBytes("\n");

    return sizes;
  }

}
