/*
 * $RCSfile: CLibImageWriter.java,v $
 *
 * 
 * Copyright (c) 2005 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 
 * 
 * - Redistribution of source code must retain the above copyright 
 *   notice, this  list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in 
 *   the documentation and/or other materials provided with the
 *   distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of 
 * contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any 
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND 
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL 
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF 
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR 
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES. 
 * 
 * You acknowledge that this software is not designed or intended for 
 * use in the design, construction, operation or maintenance of any 
 * nuclear facility. 
 *
 * $Revision: 1.6 $
 * $Date: 2007/02/06 22:14:59 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.clib;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import com.sun.medialib.codec.jiio.Constants;
import com.sun.medialib.codec.jiio.mediaLibImage;

public abstract class CLibImageWriter extends ImageWriter {
    /**
     * Returns the data array from the <code>DataBuffer</code>.
     */
    private static final Object getDataBufferData(DataBuffer db) {
        Object data;

        int dType = db.getDataType();
        switch (dType) {
        case DataBuffer.TYPE_BYTE:
            data = ((DataBufferByte)db).getData();
            break;
        case DataBuffer.TYPE_USHORT:
            data = ((DataBufferUShort)db).getData();
            break;
        default:
            throw new IllegalArgumentException
                (I18N.getString("Generic0")+" "+dType);
        }

        return data;
    }

    /**
     * Returns the mediaLib type enum given the Java2D type enum.
     */
    private static final int getMediaLibDataType(int dataType) {
        int mlibType;

        switch (dataType) {
        case DataBuffer.TYPE_BYTE:
            mlibType = mediaLibImage.MLIB_BYTE;
            break;
        case DataBuffer.TYPE_USHORT:
            mlibType = mediaLibImage.MLIB_USHORT;
            break;
        default:
            throw new IllegalArgumentException
                (I18N.getString("Generic0")+" "+dataType);
        }

        return mlibType;
    }

    /**
     * Returns the mediaLib format enum given the <code>SampleModel</code>
     * and <code>ColorModel</code> of an image. If the format cannot be
     * determined to be anything more specific, the value
     * <code>Constants.MLIB_FORMAT_UNKNOWN</code> will be returned.
     *
     * @param sampleModel The <code>SampleModel</code> describing the
     * layout of the <code>DataBuffer</code>; may be <code>null</code>.
     * @param colorModel The <code>ColorModel</code> describing the
     * mapping of the samples in a pixel to a color.
     *
     * @throws IllegalArgumentExcaption if <code>sampleModel</code> is
     * <code>null</code>.
     *
     * @return One of the <code>Constants.MLIB_FORMAT</code> constants.
     */
    private static final int getMediaLibFormat(SampleModel sampleModel,
                                               ColorModel colorModel) {
        if(sampleModel == null) {
            throw new IllegalArgumentException("sampleModel == null!");
        }

        int mlibFormat = Constants.MLIB_FORMAT_UNKNOWN;

        if(sampleModel instanceof SinglePixelPackedSampleModel &&
           sampleModel.getNumBands() == 4 &&
           colorModel != null &&
           colorModel.hasAlpha()) {
            int[] masks =
                ((SinglePixelPackedSampleModel)sampleModel).getBitMasks();
            if(masks[3] == 0xff000000) {
                if(masks[0] == 0xff &&
                   masks[1] == 0xff00 &&
                   masks[2] == 0xff0000) {
                    mlibFormat = Constants.MLIB_FORMAT_PACKED_ABGR;
                } else if(masks[0] == 0xff0000 &&
                          masks[1] == 0xff00 &&
                          masks[2] == 0xff) {
                    mlibFormat = Constants.MLIB_FORMAT_PACKED_ARGB;
                }
            }
        } else if(sampleModel instanceof ComponentSampleModel) {
            ComponentSampleModel csm = (ComponentSampleModel)sampleModel;
            int bandOffsets[] = csm.getBandOffsets();
            int pixelStride = csm.getPixelStride();

            if (pixelStride == bandOffsets.length) {
                int numBands = pixelStride; // for clarity

                boolean hasOneBank = true;
                int bankIndices[] = csm.getBankIndices();
                for (int i = 1; i < bankIndices.length; i++) {
                    if(bankIndices[i] != bankIndices[0]) {
                        hasOneBank = false;
                    }
                }

                if(hasOneBank) {
                    if(colorModel instanceof IndexColorModel) {
                        mlibFormat = Constants.MLIB_FORMAT_INDEXED;
                    } else if(numBands == 1) {
                        mlibFormat = Constants.MLIB_FORMAT_GRAYSCALE;
                    } else if(numBands == 2 &&
                              bandOffsets[0] == 0 &&
                              bandOffsets[1] == 1) {
                        mlibFormat = Constants.MLIB_FORMAT_GRAYSCALE_ALPHA;
                    } else if(numBands == 3) {
                        int csType = colorModel != null ?
                            colorModel.getColorSpace().getType() :
                            ColorSpace.TYPE_RGB;
                        if(csType == ColorSpace.TYPE_RGB) {
                            if(bandOffsets[0] == 2 &&
                               bandOffsets[1] == 1 &&
                               bandOffsets[2] == 0) {
                                mlibFormat = Constants.MLIB_FORMAT_BGR;
                            } else if(bandOffsets[0] == 0 &&
                                      bandOffsets[1] == 1 &&
                                      bandOffsets[2] == 2) {
                                mlibFormat = Constants.MLIB_FORMAT_RGB;
                            }
                        } else if(csType == ColorSpace.TYPE_Yxy &&
                                  bandOffsets[0] == 0 &&
                                  bandOffsets[1] == 1 &&
                                  bandOffsets[2] == 2) {
                            mlibFormat = Constants.MLIB_FORMAT_YCC;
                        }
                    } else if(numBands == 4) {
                        int csType = colorModel != null ?
                            colorModel.getColorSpace().getType() :
                            ColorSpace.TYPE_RGB;
                        if(csType == ColorSpace.TYPE_RGB) {
                            if(bandOffsets[3] == 0) {
                                if(bandOffsets[0] == 3 &&
                                   bandOffsets[1] == 2 &&
                                   bandOffsets[2] == 1) {
                                    mlibFormat = Constants.MLIB_FORMAT_ABGR;
                                } else if(bandOffsets[0] == 1 &&
                                          bandOffsets[1] == 2 &&
                                          bandOffsets[2] == 3) {
                                    mlibFormat = Constants.MLIB_FORMAT_ARGB;
                                }
                            } else if(bandOffsets[3] == 3) {
                                if(bandOffsets[0] == 0 &&
                                   bandOffsets[1] == 1 &&
                                   bandOffsets[2] == 2) {
                                    mlibFormat = Constants.MLIB_FORMAT_RGBA;
                                } else if(bandOffsets[0] == 2 &&
                                          bandOffsets[1] == 1 &&
                                          bandOffsets[2] == 0) {
                                    mlibFormat = Constants.MLIB_FORMAT_BGRA;
                                }
                            }
                        } else if(csType == ColorSpace.TYPE_CMYK &&
                                  bandOffsets[0] == 0 &&
                                  bandOffsets[1] == 1 &&
                                  bandOffsets[2] == 2 &&
                                  bandOffsets[3] == 3) {
                            mlibFormat = Constants.MLIB_FORMAT_CMYK;
                        } else if(csType == ColorSpace.TYPE_Yxy &&
                                  bandOffsets[0] == 0 &&
                                  bandOffsets[1] == 1 &&
                                  bandOffsets[2] == 2 &&
                                  bandOffsets[3] == 3) {
                            if(colorModel != null &&
                               colorModel.hasAlpha()) {
                                mlibFormat = Constants.MLIB_FORMAT_YCCA;
                            } else {
                                mlibFormat = Constants.MLIB_FORMAT_YCCK;
                            }
                        }
                    }
                }
            }
        }

        return mlibFormat;
    }

    /**
     * Returns a contiguous <code>Raster</code> of data over the specified
     * <code>Rectangle</code>. If the region is a sub-region of a single
     * tile, then a child of that tile will be returned. If the region
     * overlaps more than one tile and has 8 bits per sample, then a
     * pixel interleaved Raster having band offsets 0,1,... will be returned.
     * Otherwise the Raster returned by <code>im.copyData(null)</code> will
     * be returned.
     */
    private static final Raster getContiguousData(RenderedImage im,
                                                  Rectangle region) {
        if(im == null) {
            throw new IllegalArgumentException("im == null");
        } else if(region == null) {
            throw new IllegalArgumentException("region == null");
        }

        Raster raster;
        if(im.getNumXTiles() == 1 && im.getNumYTiles() == 1) {
            // Image is not tiled so just get a reference to the tile.
            raster = im.getTile(im.getMinTileX(), im.getMinTileY());

            // Ensure result has requested coverage.
            Rectangle bounds = raster.getBounds();
            if (!bounds.equals(region)) {
                raster = raster.createChild(region.x, region.y,
                                            region.width, region.height,
                                            region.x, region.y,
                                            null);
            }
        } else {
            // Image is tiled.

            // Create an interleaved raster for copying for 8-bit case.
            // This ensures that for RGB data the band offsets are {0,1,2}.
            SampleModel sampleModel = im.getSampleModel();
            WritableRaster target = sampleModel.getSampleSize(0) == 8 ?
                Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
                                               im.getWidth(),
                                               im.getHeight(),
                                               sampleModel.getNumBands(),
                                               new Point(im.getMinX(),
                                                         im.getMinY())) :
                null;

            // Copy the data.
            raster = im.copyData(target);
        }

        return raster;
    }

    /**
     * Subsamples and sub-bands the input <code>Raster</code> over a
     * sub-region and stores the result in a <code>WritableRaster</code>.
     *
     * @param src The source <code>Raster</code>
     * @param sourceBands The source bands to use; may be <code>null</code>
     * @param subsampleX The subsampling factor along the horizontal axis.
     * @param subsampleY The subsampling factor along the vertical axis.
     * in which case all bands will be used.
     * @param dst The destination <code>WritableRaster</code>.
     * @throws IllegalArgumentException if <code>source</code> is
     * <code>null</code> or empty, <code>dst</code> is <code>null</code>,
     * <code>sourceBands.length</code> exceeds the number of bands in
     * <code>source</code>, or <code>sourcBands</code> contains an element
     * which is negative or greater than or equal to the number of bands
     * in <code>source</code>.
     */
    private static void reformat(Raster source,
                                 int[] sourceBands,
                                 int subsampleX,
                                 int subsampleY,
                                 WritableRaster dst) {
        // Check for nulls.
        if(source == null) {
            throw new IllegalArgumentException("source == null!");
        } else if(dst == null) {
            throw new IllegalArgumentException("dst == null!");
        }

        // Validate the source bounds. XXX is this needed?
        Rectangle sourceBounds = source.getBounds();
        if(sourceBounds.isEmpty()) {
            throw new IllegalArgumentException
                ("source.getBounds().isEmpty()!");
        }

        // Check sub-banding.
        boolean isSubBanding = false;
        int numSourceBands = source.getSampleModel().getNumBands();
        if(sourceBands != null) {
            if(sourceBands.length > numSourceBands) {
                throw new IllegalArgumentException
                    ("sourceBands.length > numSourceBands!");
            }

            boolean isRamp = sourceBands.length == numSourceBands;
            for(int i = 0; i < sourceBands.length; i++) {
                if(sourceBands[i] < 0 || sourceBands[i] >= numSourceBands) {
                    throw new IllegalArgumentException
                        ("sourceBands[i] < 0 || sourceBands[i] >= numSourceBands!");
                } else if(sourceBands[i] != i) {
                    isRamp = false;
                }
            }

            isSubBanding = !isRamp;
        }

        // Allocate buffer for a single source row.
        int sourceWidth = sourceBounds.width;
        int[] pixels = new int[sourceWidth*numSourceBands];

        // Initialize variables used in loop.
        int sourceX = sourceBounds.x;
        int sourceY = sourceBounds.y;
        int numBands = sourceBands != null ?
            sourceBands.length : numSourceBands;
        int dstWidth = dst.getWidth();
        int dstYMax = dst.getHeight() - 1;
        int copyFromIncrement = numSourceBands*subsampleX;

        // Loop over source rows, subsample each, and store in destination.
        for(int dstY = 0; dstY <= dstYMax; dstY++) {
            // Read one row.
            source.getPixels(sourceX, sourceY, sourceWidth, 1, pixels);

            // Copy within the same buffer by left shifting.
            if(isSubBanding) {
                int copyFrom = 0;
                int copyTo = 0;
                for(int i = 0; i < dstWidth; i++) {
                    for(int j = 0; j < numBands; j++) {
                        pixels[copyTo++] = pixels[copyFrom + sourceBands[j]];
                    }
                    copyFrom += copyFromIncrement;
                }
            } else {
                int copyFrom = copyFromIncrement;
                int copyTo = numSourceBands;
                // Start from index 1 as no need to copy the first pixel.
                for(int i = 1; i < dstWidth; i++) {
                    int k = copyFrom;
                    for(int j = 0; j < numSourceBands; j++) {
                        pixels[copyTo++] = pixels[k++];
                    }
                    copyFrom += copyFromIncrement;
                }
            }

            // Set the destionation row.
            dst.setPixels(0, dstY, dstWidth, 1, pixels);

            // Increment the source row.
            sourceY += subsampleY;
        }
    }

    protected CLibImageWriter(ImageWriterSpi originatingProvider) {
        super(originatingProvider);
    }

    public IIOMetadata convertImageMetadata(IIOMetadata inData,
                                            ImageTypeSpecifier imageType,
                                            ImageWriteParam param) {
        return null;
    }

    public IIOMetadata convertStreamMetadata(IIOMetadata inData,
                                             ImageWriteParam param) {
        return null;
    }

    public IIOMetadata
        getDefaultImageMetadata(ImageTypeSpecifier imageType,
                                ImageWriteParam param) {
        return null;
    }

    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
        return null;
    }

    /* XXX
    protected int getSignificantBits(RenderedImage image) {
        SampleModel sampleModel = image.getSampleModel();
        int numBands = sampleModel.getNumBands();
        int[] sampleSize = sampleModel.getSampleSize();
        int significantBits = sampleSize[0];
        for(int i = 1; i < numBands; i++) {
            significantBits = Math.max(significantBits, sampleSize[i]);
        }

        return significantBits;
    }
    */

    // Code copied from ImageReader.java with ImageReadParam replaced
    // by ImageWriteParam.
    private static final Rectangle getSourceRegion(ImageWriteParam param,
                                                   int sourceMinX,
                                                   int sourceMinY,
                                                   int srcWidth,
                                                   int srcHeight) {
        Rectangle sourceRegion =
            new Rectangle(sourceMinX, sourceMinY, srcWidth, srcHeight);
        if (param != null) {
            Rectangle region = param.getSourceRegion();
            if (region != null) {
                sourceRegion = sourceRegion.intersection(region);
            }

            int subsampleXOffset = param.getSubsamplingXOffset();
            int subsampleYOffset = param.getSubsamplingYOffset();
            sourceRegion.x += subsampleXOffset;
            sourceRegion.y += subsampleYOffset;
            sourceRegion.width -= subsampleXOffset;
            sourceRegion.height -= subsampleYOffset;
        }

        return sourceRegion;
    }

    /**
     * Returns a <code>mediaLibImage</code> for a specific encoder to use
     * to encode <code>image</code>.
     *
     * @param image The image to encode.
     * @param param The write parameters.
     * @param allowBilevel Whether bilevel images are allowed. A bilevel
     * image must have one 1-bit sample per pixel, have data type
     * <code>DataBuffer.TYE_BYTE</code>, and have a
     * <code>MultiPixelPackedSampleModel</code>.
     * @param supportedFormats An array containing constan values from
     * the set of <code>mediaLibImage.MLIB_FORMAT</code> enums.
     *
     * @throws IllegalArgumentException if <code>supportedFormats</code>
     * is <code>null</code>.
     *
     * @return A <code>mediaLibImage in a format capable of being written
     * by the encoder.
     */
    protected mediaLibImage getMediaLibImage(RenderedImage image,
                                             ImageWriteParam param,
                                             boolean allowBilevel,
                                             int[] supportedFormats) {
        if(supportedFormats == null) {
            throw new IllegalArgumentException("supportedFormats == null!");
        }

        // Determine the source region.
        Rectangle sourceRegion = getSourceRegion(param,
                                                 image.getMinX(),
                                                 image.getMinY(),
                                                 image.getWidth(),
                                                 image.getHeight());

        if(sourceRegion.isEmpty()) {
            throw new IllegalArgumentException("sourceRegion.isEmpty()");
        }

        // Check whether reformatting is necessary to conform to mediaLib
        // image format (packed bilevel if allowed or ((G|I)|(RGB))[A]).

        // Flag indicating need to reformat data.
        boolean reformatData = false;

        // Flag indicating bilevel data.
        boolean isBilevel = false;

        // Value indicating the mediaLib image format.
        int mediaLibFormat = Constants.MLIB_FORMAT_UNKNOWN;

        // Get the SampleModel.
        SampleModel sampleModel = image.getSampleModel();

        // Get the number of bands.
        int numSourceBands = sampleModel.getNumBands();

        // Get the source sub-banding array.
        int[] sourceBands = param != null ? param.getSourceBands() : null;

        // Check for non-nominal sub-banding.
        int numBands;
        if(sourceBands != null) {
            numBands = sourceBands.length;
            if(numBands != numSourceBands) {
                // The number of bands must be the same.
                reformatData = true;
            } else {
                // The band order must not change.
                for(int i = 0; i < numSourceBands; i++) {
                    if(sourceBands[i] != i) {
                        reformatData = true;
                        break;
                    }
                }
            }
        } else {
            numBands = numSourceBands;
        }

        // If sub-banding does not dictate reformatting, check subsampling..
        if(!reformatData && param != null &&
           (param.getSourceXSubsampling() != 1 ||
            param.getSourceXSubsampling() != 1)) {
            reformatData = true;
        }

        // If sub-banding does not dictate reformatting check SampleModel.
        if(!reformatData) {
            if(allowBilevel &&
               sampleModel.getNumBands() == 1 &&
               sampleModel.getSampleSize(0) == 1 &&
               sampleModel instanceof MultiPixelPackedSampleModel &&
               sampleModel.getDataType() == DataBuffer.TYPE_BYTE) {
                // Need continguous packed bits.
                MultiPixelPackedSampleModel mppsm =
                    (MultiPixelPackedSampleModel)sampleModel;
                if(mppsm.getPixelBitStride() == 1) {
                    isBilevel = true;
                } else {
                    reformatData = true;
                }
            } else {
                // Set the mediaLib format flag.
                mediaLibFormat = getMediaLibFormat(sampleModel,
                                                   image.getColorModel());

                // Set the data reformatting flag.
                reformatData = true;
                int len = supportedFormats.length;
                for(int i = 0; i < len; i++) {
                    if(mediaLibFormat == supportedFormats[i]) {
                        reformatData = false;
                        break;
                    }
                }
            }
        }

        // Variable for the eventual destination data.
        Raster raster = null;

        if(reformatData) {
            // Determine the maximum bit depth.
            int[] sampleSize = sampleModel.getSampleSize();
            int bitDepthMax = sampleSize[0];
            for(int i = 1; i < numSourceBands; i++) {
                bitDepthMax = Math.max(bitDepthMax, sampleSize[i]);
            }

            // Set the data type as a function of bit depth.
            int dataType;
            if(bitDepthMax <= 8) {
                dataType = DataBuffer.TYPE_BYTE;
            } else if(bitDepthMax <= 16) {
                dataType = DataBuffer.TYPE_USHORT;
            } else {
                throw new UnsupportedOperationException
                    (I18N.getString("CLibImageWriter0")+" "+bitDepthMax);
            }

            // Determine the width and height.
            int width;
            int height;
            if(param != null) {
                int subsampleX = param.getSourceXSubsampling();
                int subsampleY = param.getSourceYSubsampling();
                width = (sourceRegion.width + subsampleX - 1)/subsampleX;
                height = (sourceRegion.height + subsampleY - 1)/subsampleY;
            } else {
                width = sourceRegion.width;
                height = sourceRegion.height;
            }

            // Load a ramp for band offsets.
            int[] newBandOffsets = new int[numBands];
            for(int i = 0; i < numBands; i++) {
                newBandOffsets[i] = i;
            }

            // Create a new SampleModel.
            SampleModel newSampleModel;
            if(allowBilevel &&
               sampleModel.getNumBands() == 1 &&
               bitDepthMax == 1) {
                // Bilevel image.
                newSampleModel =
                    new MultiPixelPackedSampleModel(dataType,
                                                    width,
                                                    height,
                                                    1);
                isBilevel = true;
            } else {
                // Pixel interleaved image.
                newSampleModel =
                    new PixelInterleavedSampleModel(dataType,
                                                    width,
                                                    height,
                                                    newBandOffsets.length,
                                                    width*numSourceBands,
                                                    newBandOffsets);
            }

            // Create a new Raster at (0,0).
            WritableRaster newRaster =
                Raster.createWritableRaster(newSampleModel, null);

            // Populate the new Raster.
            if(param != null &&
               (param.getSourceXSubsampling() != 1 ||
                param.getSourceXSubsampling() != 1)) {
                // Subsampling, possibly with sub-banding.
                reformat(getContiguousData(image, sourceRegion),
                         sourceBands,
                         param.getSourceXSubsampling(),
                         param.getSourceYSubsampling(),
                         newRaster);
            } else if(sourceBands == null &&
                      image.getSampleModel().getClass().isInstance
                      (newSampleModel) &&
                      newSampleModel.getTransferType() ==
                      image.getSampleModel().getTransferType()) {
                // Neither subsampling nor sub-banding.
                WritableRaster translatedChild =
                    newRaster.createWritableTranslatedChild(sourceRegion.x,
                                                            sourceRegion.y);
                // Use copyData() to avoid potentially cobbling the entire
                // source region into an extra Raster via getData().
                image.copyData(translatedChild);
            } else {
                // Cannot use copyData() so use getData() to retrieve and
                // possibly sub-band the source data and use setRect().
                WritableRaster translatedChild =
                    newRaster.createWritableTranslatedChild(sourceRegion.x,
                                                            sourceRegion.y);
                Raster sourceRaster = getContiguousData(image, sourceRegion);
                if(sourceBands != null) {
                    // Copy only the requested bands.
                    sourceRaster =
                        sourceRaster.createChild(sourceRegion.x,
                                                 sourceRegion.y,
                                                 sourceRegion.width,
                                                 sourceRegion.height,
                                                 sourceRegion.x,
                                                 sourceRegion.y,
                                                 sourceBands);
                }

                // Get the region from the image and set it into the Raster.
                translatedChild.setRect(sourceRaster);
            }

            // Replace Raster and SampleModel.
            raster = newRaster;
            sampleModel = newRaster.getSampleModel();
        } else { // !reformatData
            // No reformatting needed.
            raster = getContiguousData(image, sourceRegion).createTranslatedChild(0, 0);
            sampleModel = raster.getSampleModel();

            // Update mediaLibFormat indicator in case getContiguousData()
            // has changed the layout of the data.
            mediaLibFormat = getMediaLibFormat(sampleModel, image.getColorModel());
        }

        // The mediaLib image.
        mediaLibImage mlibImage = null;

        // Create a mediaLibImage with reference to the Raster data.
        if(isBilevel) {
            // Bilevel image: either is was already bilevel or was
            // formatted to bilevel.

            MultiPixelPackedSampleModel mppsm =
                ((MultiPixelPackedSampleModel)sampleModel);

            // Get the line stride.
            int stride = mppsm.getScanlineStride();

            // Determine the offset to the start of the data.
            int offset =
                raster.getDataBuffer().getOffset() -
                raster.getSampleModelTranslateY()*stride -
                raster.getSampleModelTranslateX()/8 +
                mppsm.getOffset(0, 0);

            // Get a reference to the internal data array.
            Object bitData = getDataBufferData(raster.getDataBuffer());

            mlibImage = new mediaLibImage(mediaLibImage.MLIB_BIT,
                                          1,
                                          raster.getWidth(),
                                          raster.getHeight(),
                                          stride,
                                          offset,
                                          (byte)mppsm.getBitOffset(0),
                                          bitData);
        } else {
            // If the image is not bilevel then it has to be component.
            ComponentSampleModel csm = (ComponentSampleModel)sampleModel;

            // Set the mediaLib data type
            int mlibDataType = getMediaLibDataType(sampleModel.getDataType());

            // Get a reference to the internal data array.
            Object data = getDataBufferData(raster.getDataBuffer());

            // Get the line stride.
            int stride = csm.getScanlineStride();

            // Determine the offset of the first sample from the offset
            // indicated by the (x,y) coordinates. This offset is the
            // minimum valued offset, not the offset of, e.g., red (index 0)
            // as the Raster is by now in a contiguous format that
            // the encoder is guaranteed to handle regardless of whether
            // the smallest offset is to the, e.g., red band.
            int[] bandOffsets = csm.getBandOffsets();
            int minBandOffset = bandOffsets[0];
            for(int i = 1; i < bandOffsets.length; i++) {
                if(bandOffsets[i] < minBandOffset) {
                    minBandOffset = bandOffsets[i];
                }
            }

            // Determine the offset to the start of the data. The
            // sampleModelTranslate parameters are the translations from
            // Raster to SampleModel coordinates and must be subtracted
            // from the Raster coordinates.
            int offset =
                (raster.getMinY() -
                 raster.getSampleModelTranslateY())*stride +
                (raster.getMinX() -
                 raster.getSampleModelTranslateX())*numSourceBands +
                minBandOffset;

            // Create the image.
            mlibImage =
                !reformatData &&
                mediaLibFormat != Constants.MLIB_FORMAT_UNKNOWN ?
                new mediaLibImage(mlibDataType,
                                  numSourceBands,
                                  raster.getWidth(),
                                  raster.getHeight(),
                                  stride,
                                  offset,
                                  mediaLibFormat,
                                  data) :
                new mediaLibImage(mlibDataType,
                                  numSourceBands,
                                  raster.getWidth(),
                                  raster.getHeight(),
                                  stride,
                                  offset,
                                  data);
        }

        return mlibImage;
    }
}
