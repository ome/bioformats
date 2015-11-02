/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2014 Open Microscopy Environment:
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

import java.io.IOException;
import java.lang.reflect.Array;

import loci.common.DataTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.ImageTools;
import loci.formats.MetadataTools;
import loci.formats.gui.AWTImageTools;
import loci.formats.in.TiffReader;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataConverter;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffCompression;
import loci.formats.tiff.TiffParser;
import loci.formats.tiff.TiffRational;
import loci.formats.tiff.TiffSaver;

import ome.xml.model.primitives.PositiveInteger;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/out/TiffWriter.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/out/TiffWriter.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class OMEPyramidWriter extends TiffWriter {

  // -- Constants --

  /** Current TIFF image comment for OMERO pixels pyramid TIFFs. */
  public static final String IMAGE_DESCRIPTION = "OmeroPixelsPyramid v1.0.0";

  /** TIFF tag we're using to store the Bio-Formats series. */
  public static final int IFD_TAG_SERIES = 65000;

  /** TIFF tag we're using to store the Bio-Formats plane number. */
  public static final int IFD_TAG_PLANE_NUMBER = 65001;

  // -- Fields --

  private int[] factors = new int[] {0};

  // -- IFormatWriter API methods --

  /* @see loci.formats.out.TiffWriter#close() */
  public void close() throws IOException {
    LOGGER.debug("close({})", currentId);

    try {
      if (currentId != null) {
        postProcess();
      }
    }
    catch (FormatException e) {
      LOGGER.error("Error during post-processing", e);
      throw new IOException(e);
    }
    finally {
      super.close();
    }
  }

  /* @see loci.formats.out.IFormatWriter#setId(String) */
  public void setId(String id) throws FormatException, IOException {
    // make sure that the full resolution image is stored first
    if (factors[0] != 0) {
      int[] oldFactors = factors;
      factors = new int[oldFactors.length + 1];
      factors[0] = 0;
      System.arraycopy(oldFactors, 0, factors, 1, oldFactors.length);
    }

    if (factors.length > 1) {
      // set up metadata for any additional defined resolution levels
      // the caller is only expected to call setResolutionLevels; each resolution
      // is not expected to be pre-defined in the MetadataRetrieve

      int width = getMetadataRetrieve().getPixelsSizeX(0).getValue();
      int height = getMetadataRetrieve().getPixelsSizeY(0).getValue();

      if (!(getMetadataRetrieve() instanceof MetadataStore)) {
        IMetadata newMeta = MetadataTools.createOMEXMLMetadata();
        MetadataConverter.convertMetadata(getMetadataRetrieve(), newMeta);
        metadataRetrieve = newMeta;
      }

      IMetadata meta = (IMetadata) getMetadataRetrieve();

      meta.setImageDescription(IMAGE_DESCRIPTION, 0);

      for (int i=1; i<factors.length; i++) {
        int factor = (int) Math.pow(2, factors[i]);

        int newWidth = width / factor;
        int newHeight = height / factor;

        meta.setImageID("Image:" + i, i);
        meta.setImageDescription(IMAGE_DESCRIPTION, i);
        meta.setPixelsID("Pixels: " + i, i);
        meta.setPixelsBigEndian(meta.getPixelsBigEndian(0), i);
        meta.setPixelsDimensionOrder(meta.getPixelsDimensionOrder(0), i);
        meta.setPixelsType(meta.getPixelsType(0), i);
        meta.setPixelsSizeX(new PositiveInteger(newWidth), i);
        meta.setPixelsSizeY(new PositiveInteger(newHeight), i);
        meta.setPixelsSizeZ(new PositiveInteger(1), i);
        meta.setPixelsSizeC(new PositiveInteger(1), i);
        int totalPlanes = meta.getPixelsSizeZ(0).getValue() *
          meta.getPixelsSizeC(0).getValue() * meta.getPixelsSizeT(0).getValue();
        meta.setPixelsSizeT(new PositiveInteger(totalPlanes), i);
        meta.setChannelID("Channel:" + i, i, 0);
        meta.setChannelSamplesPerPixel(new PositiveInteger(1), i, 0);

        LOGGER.debug(String.format("Added series %d %dx%dx%d",
          i, newWidth, newHeight, totalPlanes));
      }
    }

    super.setId(id);
  }

  // -- OMEPyramidWriter API methods --

  /**
   * Sets the resolution levels to be used.
   * Each resolution level will be computed by downsampling the full resolution
   * image; the width and height of the full resolution image will be divided
   * by 2**factors[i] for each new resolution.
   */
  public void setResolutionLevels(int[] factors) {
    this.factors = factors;
  }

  /**
   * Performs re-compression post processing on the pixel pyramid.
   * @throws IOException
   * @throws FormatException
   */
  protected void postProcess() throws FormatException, IOException {
    TiffReader reader = new TiffReader();
    try {
      reader.setId(currentId);

      // First we want to re-compress resolution level 0 (the source series,
      // with resolution levels exposed, are in reverse order).
      recompressSeries(reader, 1);
      // Second we want to re-compress resolution level 1 (the source series,
      // with resolution levels exposed, are in reverse order).
      recompressSeries(reader, 2);
    }
    finally {
      reader.close();
    }
  }

  /**
   * Re-compresses a source series, ensuring that resolution levels are
   * recorded correctly.
   * @param source Reader created of ourselves.
   * @param series Target series for the re-compressed data which is the
   * inverse of the source resolution level.
   * @throws FormatException
   * @throws IOException
   */
  protected void recompressSeries(TiffReader source, int series)
    throws FormatException, IOException
  {
    int sourceSeries = source.getSeriesCount() - series;
    boolean createResolution = false;

    if (sourceSeries > 0 && sourceSeries < source.getSeriesCount()) {
      // this will only happen for JPEG-2000 data or the highest
      // resolution in the pyramid
      source.setSeries(sourceSeries);
    }
    else {
      // we're not using a compression scheme that internally stores
      // resolution data, so we have to create the resolutions ourselves

      source.setSeries(0);
      createResolution = true;
      if (series >= factors.length) {
        throw new FormatException("Cannot create resolution #" + series +
          "; only " + factors.length + " resolutions defined");
      }
    }

    int pixelType = source.getPixelType();
    int bpp = source.getRGBChannelCount() * FormatTools.getBytesPerPixel(pixelType);
    boolean fp = FormatTools.isFloatingPoint(pixelType);

    int imageCount = source.getImageCount();
    setSeries(series);
    for (int i = 0; i < imageCount; i++) {
      byte[] plane = null;
      int height = source.getSizeY();
      if (createResolution) {
        // open and downsample each tile

        int tileWidth = source.getOptimalTileWidth();
        int tileLength = source.getOptimalTileHeight();
        int width = source.getSizeX();

        long factor = (long) Math.pow(2, factors[series]);

        long newTileWidth = Math.round((double) tileWidth / factor);
        newTileWidth = newTileWidth < 1? 1 : newTileWidth;
        long newTileLength = Math.round((double) tileLength / factor);
        newTileLength = newTileLength < 1? 1: newTileLength;
        long evenTilesPerRow = width / tileWidth;
        long evenTilesPerColumn = height / tileLength;
        double remainingWidth =
          ((double) (width - (evenTilesPerRow * tileWidth))) / factor;
        remainingWidth = remainingWidth < 1 ? Math.ceil(remainingWidth) :
            Math.round(remainingWidth);
        double remainingLength =
          ((double) height - (evenTilesPerColumn * tileLength)) / factor;
        remainingLength = remainingLength < 1 ? Math.ceil(remainingLength) :
            Math.round(remainingLength);

        int newWidth = getMetadataRetrieve().getPixelsSizeX(series).getValue();
        int newLength = getMetadataRetrieve().getPixelsSizeY(series).getValue();
        height = newLength;

        plane = new byte[newWidth * newLength * bpp];

        // read small tiles, each of which is averaged to become a single pixel
        // probably this could be replaced by smoother interpolation
        byte[] tile = new byte[(int) (factor * factor * bpp)];
        int nextPixel = 0;
        for (int y=0; y<height; y+=factor) {
          for (int x=0; x<width; x+=factor) {
            int scaleTileWidth = (int) Math.min(factor, width - x);
            int scaleTileHeight = (int) Math.min(factor, height - y);
            source.openBytes(i, tile, x, y, scaleTileWidth, scaleTileHeight);

            Object pix =
              DataTools.makeDataArray(tile, bpp, fp, source.isLittleEndian());
            double sum = 0;
            for (int q=0; q<Array.getLength(pix); q++) {
              sum += Array.getDouble(pix, q);
            }
            sum /= Array.getLength(pix);
            DataTools.unpackBytes(Double.doubleToLongBits(sum), plane, nextPixel,
              bpp, source.isLittleEndian());
            nextPixel += bpp;
          }
        }
      }
      else {
        plane = source.openBytes(i);
      }
      IFD ifd = new IFD();
      // Ensure that we're compressing all rows of the image in a single
      // JPEG 2000 block.
      ifd.put(IFD.ROWS_PER_STRIP, new long[] { height });
      // Set the TIFF image description so that we are able to
      // differentiate ourselves from basic TIFFs.
      ifd.put(IFD.IMAGE_DESCRIPTION, IMAGE_DESCRIPTION);
      // First re-usable TIFF IFD (series)
      ifd.put(IFD_TAG_SERIES, sourceSeries - 1);
      // Second re-usable TIFF IFD (plane number)
      ifd.put(IFD_TAG_PLANE_NUMBER, i);
      saveBytes(i, plane, ifd);
    }
  }

}
