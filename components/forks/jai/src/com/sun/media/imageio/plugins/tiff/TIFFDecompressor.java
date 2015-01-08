/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2014 Open Microscopy Environment:
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

/*
 * $RCSfile: TIFFDecompressor.java,v $
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
 * $Revision: 1.3 $
 * $Date: 2007/03/09 20:14:40 $
 * $State: Exp $
 */
package com.sun.media.imageio.plugins.tiff;

import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import com.sun.media.imageioimpl.common.BogusColorSpace;
import com.sun.media.imageioimpl.common.ImageUtil;
import com.sun.media.imageioimpl.common.SimpleCMYKColorSpace;

/**
 * A class defining a pluggable TIFF decompressor.
 *
 * <p> The mapping between source and destination Y coordinates is
 * given by the equations:
 *
 * <pre>
 * dx = (sx - sourceXOffset)/subsampleX + dstXOffset;
 * dy = (sy - sourceYOffset)/subsampleY + dstYOffset;
 * </pre>
 *
 * Note that the mapping from source coordinates to destination
 * coordinates is not one-to-one if subsampling is being used, since
 * only certain source pixels are to be copied to the
 * destination. However, * the inverse mapping is always one-to-one:
 *
 * <pre>
 * sx = (dx - dstXOffset)*subsampleX + sourceXOffset;
 * sy = (dy - dstYOffset)*subsampleY + sourceYOffset;
 * </pre>
 *
 * <p> Decompressors may be written with various levels of complexity.
 * The most complex decompressors will override the
 * <code>decode</code> method, and will perform all the work of
 * decoding, subsampling, offsetting, clipping, and format conversion.
 * This approach may be the most efficient, since it is possible to
 * avoid the use of extra image buffers, and it may be possible to
 * avoid decoding portions of the image that will not be copied into
 * the destination.
 *
 * <p> Less ambitious decompressors may override the
 * <code>decodeRaw</code> method, which is responsible for
 * decompressing the entire tile or strip into a byte array (or other
 * appropriate datatype).  The default implementation of
 * <code>decode</code> will perform all necessary setup of buffers,
 * call <code>decodeRaw</code> to perform the actual decoding, perform
 * subsampling, and copy the results into the final destination image.
 * Where possible, it will pass the real image buffer to
 * <code>decodeRaw</code> in order to avoid making an extra copy.
 *
 * <p> Slightly more ambitious decompressors may override
 * <code>decodeRaw</code>, but avoid writing pixels that will be
 * discarded in the subsampling phase.
 */
public abstract class TIFFDecompressor {

    private static final boolean DEBUG = false; // XXX false for release!

    /**
     * The <code>ImageReader</code> calling this
     * <code>TIFFDecompressor</code>.
     */
    protected ImageReader reader;

    /**
     * The <code>IIOMetadata</code> object containing metadata for the
     * current image.
     */
    protected IIOMetadata metadata;

    /**
     * The value of the <code>PhotometricInterpretation</code> tag.
     * Legal values are {@link
     * BaselineTIFFTagSet#PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO },
     * {@link
     * BaselineTIFFTagSet#PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO},
     * {@link BaselineTIFFTagSet#PHOTOMETRIC_INTERPRETATION_RGB},
     * {@link
     * BaselineTIFFTagSet#PHOTOMETRIC_INTERPRETATION_PALETTE_COLOR},
     * {@link
     * BaselineTIFFTagSet#PHOTOMETRIC_INTERPRETATION_TRANSPARENCY_MASK},
     * {@link BaselineTIFFTagSet#PHOTOMETRIC_INTERPRETATION_Y_CB_CR},
     * {@link BaselineTIFFTagSet#PHOTOMETRIC_INTERPRETATION_CIELAB},
     * {@link BaselineTIFFTagSet#PHOTOMETRIC_INTERPRETATION_ICCLAB},
     * or other value defined by a TIFF extension.
     */
    protected int photometricInterpretation;

    /**
     * The value of the <code>Compression</code> tag. Legal values are
     * {@link BaselineTIFFTagSet#COMPRESSION_NONE}, {@link
     * BaselineTIFFTagSet#COMPRESSION_CCITT_RLE}, {@link
     * BaselineTIFFTagSet#COMPRESSION_CCITT_T_4}, {@link
     * BaselineTIFFTagSet#COMPRESSION_CCITT_T_6}, {@link
     * BaselineTIFFTagSet#COMPRESSION_LZW}, {@link
     * BaselineTIFFTagSet#COMPRESSION_OLD_JPEG}, {@link
     * BaselineTIFFTagSet#COMPRESSION_JPEG}, {@link
     * BaselineTIFFTagSet#COMPRESSION_ZLIB}, {@link
     * BaselineTIFFTagSet#COMPRESSION_PACKBITS}, {@link
     * BaselineTIFFTagSet#COMPRESSION_DEFLATE}, or other value
     * defined by a TIFF extension.
     */
    protected int compression;

    /**
     * <code>true</code> if the image is encoded using separate planes.
     */
    protected boolean planar;

    /**
     * The value of the <code>SamplesPerPixel</code> tag.
     */
    protected int samplesPerPixel;

    /**
     * The value of the <code>BitsPerSample</code> tag.
     *
     */
    protected int[] bitsPerSample;

    /**
     * The value of the <code>SampleFormat</code> tag.  Legal values
     * are {@link BaselineTIFFTagSet#SAMPLE_FORMAT_UNSIGNED_INTEGER},
     * {@link BaselineTIFFTagSet#SAMPLE_FORMAT_SIGNED_INTEGER}, {@link
     * BaselineTIFFTagSet#SAMPLE_FORMAT_FLOATING_POINT}, {@link
     * BaselineTIFFTagSet#SAMPLE_FORMAT_UNDEFINED}, or other value
     * defined by a TIFF extension.
     */
    protected int[] sampleFormat =
        new int[] {BaselineTIFFTagSet.SAMPLE_FORMAT_UNSIGNED_INTEGER};

    /**
     * The value of the <code>ExtraSamples</code> tag.  Legal values
     * are {@link BaselineTIFFTagSet#EXTRA_SAMPLES_UNSPECIFIED},
     * {@link BaselineTIFFTagSet#EXTRA_SAMPLES_ASSOCIATED_ALPHA},
     * {@link BaselineTIFFTagSet#EXTRA_SAMPLES_UNASSOCIATED_ALPHA},
     * or other value defined by a TIFF extension.
     */
    protected int[] extraSamples;

    /**
     * The value of the <code>ColorMap</code> tag.
     *
     */
    protected char[] colorMap;        

    // Region of input stream containing the data

    /**
     * The <code>ImageInputStream</code> containing the TIFF source
     * data.
     */
    protected ImageInputStream stream;

    /**
     * The offset in the source <code>ImageInputStream</code> of the
     * start of the data to be decompressed.
     */
    protected long offset;

    /**
     * The number of bytes of data from the source
     * <code>ImageInputStream</code> to be decompressed.
     */
    protected int byteCount;

    // Region of the file image represented in the stream
    // This is unaffected by subsampling

    /**
     * The X coordinate of the upper-left pixel of the source region
     * being decoded from the source stream.  This value is not affected
     * by source subsampling.
     */
    protected int srcMinX;

    /**
     * The Y coordinate of the upper-left pixel of the source region
     * being decoded from the source stream.  This value is not affected
     * by source subsampling.
     */
    protected int srcMinY;

    /** 
     * The width of the source region being decoded from the source
     * stream.  This value is not affected by source subsampling.
     */
    protected int srcWidth;

    /**
     * The height of the source region being decoded from the source
     * stream.  This value is not affected by source subsampling.
     */
    protected int srcHeight;

    // Subsampling to be performed

    /**
     * The source X offset used, along with <code>dstXOffset</code>
     * and <code>subsampleX</code>, to map between horizontal source
     * and destination pixel coordinates.
     */
    protected int sourceXOffset;

    /**
     * The horizontal destination offset used, along with
     * <code>sourceXOffset</code> and <code>subsampleX</code>, to map
     * between horizontal source and destination pixel coordinates.
     * See the comment for {@link #sourceXOffset
     * <code>sourceXOffset</code>} for the mapping equations.
     */
    protected int dstXOffset;

    /**
     * The source Y offset used, along with <code>dstYOffset</code>
     * and <code>subsampleY</code>, to map between vertical source and
     * destination pixel coordinates.
     */
    protected int sourceYOffset;

    /**
     * The vertical destination offset used, along with
     * <code>sourceYOffset</code> and <code>subsampleY</code>, to map
     * between horizontal source and destination pixel coordinates.
     * See the comment for {@link #sourceYOffset
     * <code>sourceYOffset</code>} for the mapping equations.
     */
    protected int dstYOffset;
    
    /**
     * The horizontal subsampling factor.  A factor of 1 means that
     * every column is copied to the destination; a factor of 2 means
     * that every second column is copied, etc.
     */
    protected int subsampleX;

    /**
     * The vertical subsampling factor.  A factor of 1 means that
     * every row is copied to the destination; a factor of 2 means
     * that every second row is copied, etc.
     */
    protected int subsampleY;

    // Band subsetting/rearrangement

    /**
     * The sequence of source bands that are to be copied into the
     * destination.
     */
    protected int[] sourceBands;

    /**
     * The sequence of destination bands to receive the source data.
     */
    protected int[] destinationBands;

    // Destination for decodeRaw

    /**
     * A <code>BufferedImage</code> for the <code>decodeRaw</code>
     * method to write into.
     */
    protected BufferedImage rawImage;

    // Destination

    /**
     * The final destination image.
     */
    protected BufferedImage image;

    /**
     * The X coordinate of the upper left pixel to be written in the
     * destination image.
     */
    protected int dstMinX;

    /**
     * The Y coordinate of the upper left pixel to be written in the
     * destination image.
     */
    protected int dstMinY;

    /**
     * The width of the region of the destination image to be written.
     */
    protected int dstWidth;

    /**
     * The height of the region of the destination image to be written.
     */
    protected int dstHeight;

    // Region of source contributing to the destination

    /**
     * The X coordinate of the upper-left source pixel that will
     * actually be copied into the destination image, taking into
     * account all subsampling, offsetting, and clipping.  That is,
     * the pixel at (<code>activeSrcMinX</code>,
     * <code>activeSrcMinY</code>) is to be copied into the
     * destination pixel at (<code>dstMinX</code>,
     * <code>dstMinY</code>).
     *
     * <p> The pixels in the source region to be copied are
     * those with X coordinates of the form <code>activeSrcMinX +
     * k*subsampleX</code>, where <code>k</code> is an integer such
     * that <code>0 <= k < dstWidth</code>.
     */
    protected int activeSrcMinX;

    /**
     * The Y coordinate of the upper-left source pixel that will
     * actually be copied into the destination image, taking into account
     * all subsampling, offsetting, and clipping.
     *
     * <p> The pixels in the source region to be copied are
     * those with Y coordinates of the form <code>activeSrcMinY +
     * k*subsampleY</code>, where <code>k</code> is an integer such
     * that <code>0 <= k < dstHeight</code>.
     */
    protected int activeSrcMinY;

    /**
     * The width of the source region that will actually be copied
     * into the destination image, taking into account all
     * susbampling, offsetting, and clipping.
     *
     * <p> The active source width will always be equal to
     * <code>(dstWidth - 1)*subsampleX + 1</code>.
     */
    protected int activeSrcWidth;

    /**
     * The height of the source region that will actually be copied
     * into the destination image, taking into account all
     * susbampling, offsetting, and clipping.
     *
     * <p> The active source height will always be equal to
     * <code>(dstHeight - 1)*subsampleY + 1</code>.
     */
    protected int activeSrcHeight;

    /**
     * A <code>TIFFColorConverter</code> object describing the color space of
     * the encoded pixel data, or <code>null</code>.
     */
    protected TIFFColorConverter colorConverter;

    boolean isBilevel;
    boolean isContiguous;
    boolean isImageSimple;
    boolean adjustBitDepths;
    int[][] bitDepthScale;

    // source pixel at (sx, sy) should map to dst pixel (dx, dy), where:
    //
    // dx = (sx - sourceXOffset)/subsampleX + dstXOffset;
    // dy = (sy - sourceYOffset)/subsampleY + dstYOffset; 
    //
    // Note that this mapping is many-to-one.  Source pixels such that
    // (sx - sourceXOffset) % subsampleX != 0 should not be copied
    // (and similarly for y).
    // 
    // The backwards mapping from dest to source is one-to-one:
    //
    // sx = (dx - dstXOffset)*subsampleX + sourceXOffset;
    // sy = (dy - dstYOffset)*subsampleY + sourceYOffset;
    //
    // The reader will always hand us the full source region as it
    // exists in the file.  It will take care of clipping the dest region 
    // to exactly those dest pixels that are present in the source region.

    /**
     * Create a <code>PixelInterleavedSampleModel</code> for use in creating
     * an <code>ImageTypeSpecifier</code>.  Its dimensions will be 1x1 and
     * it will have ascending band offsets as {0, 1, 2, ..., numBands}.
     *
     * @param dataType The data type (DataBuffer.TYPE_*).
     * @param numBands The number of bands.
     * @return A <code>PixelInterleavedSampleModel</code>.
     */
    // XXX Maybe don't need to have this as a separate method?
    static SampleModel createInterleavedSM(int dataType,
                                           int numBands) {
        int[] bandOffsets = new int[numBands];
        for(int i = 0; i < numBands; i++) {
            bandOffsets[i] = i;
        }
        return new PixelInterleavedSampleModel(dataType,
                                               1, // width
                                               1, // height
                                               numBands, // pixelStride,
                                               numBands, // scanlineStride
                                               bandOffsets);
    }

    /**
     * Create a <code>ComponentColorModel</code> for use in creating
     * an <code>ImageTypeSpecifier</code>.
     */
    // This code was copied from javax.imageio.ImageTypeSpecifier and
    // modified to support floating point data.
    static ColorModel createComponentCM(ColorSpace colorSpace,
                                        int numBands,
                                        int dataType,
                                        boolean hasAlpha,
                                        boolean isAlphaPremultiplied) {
        int transparency =
            hasAlpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE;

        ColorModel colorModel;
        if(dataType == DataBuffer.TYPE_FLOAT ||
           dataType == DataBuffer.TYPE_DOUBLE) {

            colorModel = new ComponentColorModel(colorSpace,
                                                 hasAlpha,
                                                 isAlphaPremultiplied,
                                                 transparency,
                                                 dataType);
        } else {
            int[] numBits = new int[numBands];
            int bits;
            if (dataType == DataBuffer.TYPE_BYTE) {
                bits = 8;
            } else if (dataType == DataBuffer.TYPE_SHORT ||
                       dataType == DataBuffer.TYPE_USHORT) {
                bits = 16;
            } else if (dataType == DataBuffer.TYPE_INT) {
                bits = 32;
            } else {
                throw new IllegalArgumentException("dataType = " + dataType);
            }
            for (int i = 0; i < numBands; i++) {
                numBits[i] = bits;
            }

            colorModel = new ComponentColorModel(colorSpace,
                                                 numBits,
                                                 hasAlpha,
                                                 isAlphaPremultiplied,
                                                 transparency,
                                                 dataType);
        }

        return colorModel;
    }

    private static int createMask(int[] bitsPerSample, int band) {
        int mask = (1 << bitsPerSample[band]) - 1;
        for (int i = band + 1; i < bitsPerSample.length; i++) {
            mask <<= bitsPerSample[i];
        }

        return mask;
    }

    private static int getDataTypeFromNumBits(int numBits, boolean isSigned) {
        int dataType;

        if (numBits <= 8) {
            dataType = DataBuffer.TYPE_BYTE;
        } else if (numBits <= 16) {
            dataType = isSigned ?
                DataBuffer.TYPE_SHORT : DataBuffer.TYPE_USHORT;
        } else {
            dataType = DataBuffer.TYPE_INT;
        }

        return dataType;
    }

    private static boolean areIntArraysEqual(int[] a, int[] b) {
        if(a == null || b == null) {
            if(a == null && b == null) {
                return true;
            } else { // one is null and one is not
                return false;
            }
        }

        if(a.length != b.length) {
            return false;
        }

        int length = a.length;
        for(int i = 0; i < length; i++) {
            if(a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Return the number of bits occupied by <code>dataType</code>
     * which must be one of the <code>DataBuffer</code> <code>TYPE</code>s.
     */
    private static int getDataTypeSize(int dataType) throws IIOException {
        int dataTypeSize = 0;
        switch(dataType) {
        case DataBuffer.TYPE_BYTE:
            dataTypeSize = 8;
            break;
        case DataBuffer.TYPE_SHORT:
        case DataBuffer.TYPE_USHORT:
            dataTypeSize = 16;
            break;
        case DataBuffer.TYPE_INT:
        case DataBuffer.TYPE_FLOAT:
            dataTypeSize = 32;
            break;
        case DataBuffer.TYPE_DOUBLE:
            dataTypeSize = 64;
            break;
        default:
            throw new IIOException("Unknown data type "+dataType);
        }

        return dataTypeSize;
    }

    /**
     * Returns the number of bits per pixel.
     */
    private static int getBitsPerPixel(SampleModel sm) {
        int bitsPerPixel = 0;
        int[] sampleSize = sm.getSampleSize();
        int numBands = sampleSize.length;
        for(int i = 0; i < numBands; i++) {
            bitsPerPixel += sampleSize[i];
        }
        return bitsPerPixel;
    }

    /**
     * Returns whether all samples have the same number of bits.
     */
    private static boolean areSampleSizesEqual(SampleModel sm) {
        boolean allSameSize = true;
        int[] sampleSize = sm.getSampleSize();
        int sampleSize0 = sampleSize[0];
        int numBands = sampleSize.length;

        for(int i = 1; i < numBands; i++) {
            if(sampleSize[i] != sampleSize0) {
                allSameSize = false;
                break;
            }
        }

        return allSameSize;
    }

    /**
     * Determines whether the <code>DataBuffer</code> is filled without
     * any interspersed padding bits.
     */
    private static boolean isDataBufferBitContiguous(SampleModel sm)
        throws IIOException {
        int dataTypeSize = getDataTypeSize(sm.getDataType());

        if(sm instanceof ComponentSampleModel) {
            int numBands = sm.getNumBands();
            for(int i = 0; i < numBands; i++) {
                if(sm.getSampleSize(i) != dataTypeSize) {
                    // Sample does not fill data element.
                    return false;
                }
            }
        } else if(sm instanceof MultiPixelPackedSampleModel) {
            MultiPixelPackedSampleModel mppsm =
                (MultiPixelPackedSampleModel)sm;
            if(dataTypeSize % mppsm.getPixelBitStride() != 0) {
                // Pixels do not fill the data element.
                return false;
            }
        } else if(sm instanceof SinglePixelPackedSampleModel) {
            SinglePixelPackedSampleModel sppsm =
                (SinglePixelPackedSampleModel)sm;
            int numBands = sm.getNumBands();
            int numBits = 0;
            for(int i = 0; i < numBands; i++) {
                numBits += sm.getSampleSize(i);
            }
            if(numBits != dataTypeSize) {
                // Pixel does not fill the data element.
                return false;
            }
        } else {
            // Unknown SampleModel class.
            return false;
        }

        return true;
    }

    /**
     * Reformats data read as bytes into a short or int buffer.
     */
    private static void reformatData(byte[] buf,
                                     int bytesPerRow,
                                     int numRows,
                                     short[] shortData,
                                     int[] intData,
                                     int outOffset,
                                     int outStride)
        throws IIOException {

        if(shortData != null) {
            if(DEBUG) {
                System.out.println("Reformatting data to short");
            }
            int inOffset = 0;
            int shortsPerRow = bytesPerRow/2;
            int numExtraBytes = bytesPerRow % 2;
            for(int j = 0; j < numRows; j++) {
                int k = outOffset;
                for(int i = 0; i < shortsPerRow; i++) {
                    shortData[k++] =
                        (short)(((buf[inOffset++]&0xff) << 8) |
                                (buf[inOffset++]&0xff));
                }
                if(numExtraBytes != 0) {
                    shortData[k++] = (short)((buf[inOffset++]&0xff) << 8);
                }
                outOffset += outStride;
            }
        } else if(intData != null) {
            if(DEBUG) {
                System.out.println("Reformatting data to int");
            }
            int inOffset = 0;
            int intsPerRow = bytesPerRow/4;
            int numExtraBytes = bytesPerRow % 4;
            for(int j = 0; j < numRows; j++) {
                int k = outOffset;
                for(int i = 0; i < intsPerRow; i++) {
                    intData[k++] =
                        ((buf[inOffset++]&0xff) << 24) |
                        ((buf[inOffset++]&0xff) << 16) |
                        ((buf[inOffset++]&0xff) << 8) |
                        (buf[inOffset++]&0xff);
                }
                if(numExtraBytes != 0) {
                    int shift = 24;
                    int ival = 0;
                    for(int b = 0; b < numExtraBytes; b++) {
                        ival |= (buf[inOffset++]&0xff) << shift;
                        shift -= 8;
                    }
                    intData[k++] = ival;
                }
                outOffset += outStride;
            }
        } else {
            throw new IIOException("shortData == null && intData == null!");
        }
    }

    /**
     * Reformats bit-discontiguous data into the <code>DataBuffer</code>
     * of the supplied <code>WritableRaster</code>.
     */
    private static void reformatDiscontiguousData(byte[] buf,
                                                  int stride,
                                                  int w,
                                                  int h,
                                                  WritableRaster raster)
        throws IOException {

        if(DEBUG) {
            System.out.println("Reformatting discontiguous data");
        }

        // Get SampleModel info.
        SampleModel sm = raster.getSampleModel();
        int numBands = sm.getNumBands();
        int[] sampleSize = sm.getSampleSize();

        // Initialize input stream.
        ByteArrayInputStream is = new ByteArrayInputStream(buf);
        ImageInputStream iis = new MemoryCacheImageInputStream(is);

        // Reformat.
        long iisPosition = 0L;
        int y = raster.getMinY();
        for(int j = 0; j < h; j++, y++) {
            iis.seek(iisPosition);
            int x = raster.getMinX();
            for(int i = 0; i < w; i++, x++) {
                for(int b = 0; b < numBands; b++) {
                    long bits = iis.readBits(sampleSize[b]);
                    raster.setSample(x, y, b, (int)bits);
                }
            }
            iisPosition += stride;
        }
    }

    /**
     * A utility method that returns an
     * <code>ImageTypeSpecifier</code> suitable for decoding an image
     * with the given parameters.
     *
     * @param photometricInterpretation the value of the
     * <code>PhotometricInterpretation</code> field.
     * @param compression the value of the <code>Compression</code> field.
     * @param samplesPerPixel the value of the
     * <code>SamplesPerPixel</code> field.
     * @param bitsPerSample the value of the <code>BitsPerSample</code> field.
     * @param sampleFormat the value of the <code>SampleFormat</code> field.
     * @param extraSamples the value of the <code>ExtraSamples</code> field.
     * @param colorMap the value of the <code>ColorMap</code> field.
     *
     * @return a suitable <code>ImageTypeSpecifier</code>, or
     * <code>null</code> if it is not possible to create one.
     */
    public static ImageTypeSpecifier
        getRawImageTypeSpecifier(int photometricInterpretation,
                                 int compression,
                                 int samplesPerPixel,
                                 int[] bitsPerSample,
                                 int[] sampleFormat,
                                 int[] extraSamples,
                                 char[] colorMap) {
        // XXX BEGIN
        /* XXX
        System.out.println("samplesPerPixel: "+samplesPerPixel);
        System.out.print("bitsPerSample:");
        for(int i = 0; i < bitsPerSample.length; i++) {
            System.out.print(" "+bitsPerSample[i]);
        }
        System.out.println("");
        System.out.print("sampleFormat:");
        for(int i = 0; i < sampleFormat.length; i++) {
            System.out.print(" "+sampleFormat[i]);
        }
        System.out.println("");
        if(extraSamples != null) {
            System.out.print("extraSamples:");
            for(int i = 0; i < extraSamples.length; i++) {
                System.out.print(" "+extraSamples[i]);
            }
            System.out.println("");
        }
        */
        // XXX END

        //
        // Types to support:
        //
        // 1, 2, 4, 8, or 16 bit grayscale or indexed
        // 8,8-bit gray+alpha
        // 16,16-bit gray+alpha
        // 8,8,8-bit RGB
        // 8,8,8,8-bit CMYK
        // 8,8,8,8-bit RGB+alpha
        // 16,16,16-bit RGB
        // 16,16,16,16-bit RGB+alpha
        // 1,1,1,1 or 2,2,2,2 or 4,4,4,4 CMYK
        // R+G+B = 8-bit RGB
        // R+G+B+A = 8-bit RGB
        // R+G+B = 16-bit RGB
        // R+G+B+A = 16-bit RGB
        // 8X-bits/sample, arbitrary numBands.
        // Arbitrary non-indexed, non-float layouts (discontiguous).
        //

        // Band-sequential

        if(DEBUG) {
            System.out.println("\n ---- samplesPerPixel = "+samplesPerPixel+
                               "\n ---- bitsPerSample[0] = "+bitsPerSample[0]+
                               "\n ---- sampleFormat[0] = "+sampleFormat[0]);
        }

        // 1, 2, 4, 8, or 16 bit grayscale or indexed images
        if (samplesPerPixel == 1 &&
            (bitsPerSample[0] == 1 ||
             bitsPerSample[0] == 2 ||
             bitsPerSample[0] == 4 ||
             bitsPerSample[0] == 8 ||
             bitsPerSample[0] == 16)) {

            // 2 and 16 bits images are not in the baseline
            // specification, but we will allow them anyway
            // since they fit well into Java2D
            //
            // this raises the issue of how to write such images...
            
            if (colorMap == null) {
                // Grayscale
                boolean isSigned = (sampleFormat[0] ==
                              BaselineTIFFTagSet.SAMPLE_FORMAT_SIGNED_INTEGER);
                int dataType;
                if (bitsPerSample[0] <= 8) {
                    dataType = DataBuffer.TYPE_BYTE;
                } else {
                    dataType = sampleFormat[0] == 
                        BaselineTIFFTagSet.SAMPLE_FORMAT_SIGNED_INTEGER ?
                        DataBuffer.TYPE_SHORT :
                        DataBuffer.TYPE_USHORT;
                }

                return ImageTypeSpecifier.createGrayscale(bitsPerSample[0],
                                                          dataType,
                                                          isSigned);
            } else {
                // Indexed
                int mapSize = 1 << bitsPerSample[0];
                byte[] redLut = new byte[mapSize];
                byte[] greenLut = new byte[mapSize];
                byte[] blueLut = new byte[mapSize];
                byte[] alphaLut = null;

                int idx = 0;
                for (int i = 0; i < mapSize; i++) {
                    redLut[i] = (byte)((colorMap[i]*255)/65535);
                    greenLut[i] = (byte)((colorMap[mapSize + i]*255)/65535);
                    blueLut[i] = (byte)((colorMap[2*mapSize + i]*255)/65535);
                }

                int dataType = bitsPerSample[0] == 8 ?
                    DataBuffer.TYPE_BYTE : DataBuffer.TYPE_USHORT;
                return ImageTypeSpecifier.createIndexed(redLut,
                                                        greenLut,
                                                        blueLut,
                                                        alphaLut,
                                                        bitsPerSample[0],
                                                        dataType);
            }
        }

        // 8-bit gray-alpha
        if (samplesPerPixel == 2 &&
            bitsPerSample[0] == 8 &&
            bitsPerSample[1] == 8) {
            int dataType = DataBuffer.TYPE_BYTE;
            boolean alphaPremultiplied = false;
            if (extraSamples != null &&
                extraSamples[0] ==
                BaselineTIFFTagSet.EXTRA_SAMPLES_ASSOCIATED_ALPHA) {
                alphaPremultiplied = true;
            }
            //System.out.println("alphaPremultiplied = "+alphaPremultiplied);//XXX
            return ImageTypeSpecifier.createGrayscale(8,
                                                      dataType,
                                                      false,
                                                      alphaPremultiplied);
        }

        // 16-bit gray-alpha
        if (samplesPerPixel == 2 &&
            bitsPerSample[0] == 16 &&
            bitsPerSample[1] == 16) {
            int dataType = sampleFormat[0] == 
                BaselineTIFFTagSet.SAMPLE_FORMAT_SIGNED_INTEGER ?
                DataBuffer.TYPE_SHORT :
                DataBuffer.TYPE_USHORT;
            boolean alphaPremultiplied = false;
            if (extraSamples != null &&
                extraSamples[0] ==
                BaselineTIFFTagSet.EXTRA_SAMPLES_ASSOCIATED_ALPHA) {
                alphaPremultiplied = true;
            }
            //System.out.println("alphaPremultiplied = "+alphaPremultiplied);//XXX
            boolean isSigned = dataType == DataBuffer.TYPE_SHORT;
            return ImageTypeSpecifier.createGrayscale(16,
                                                      dataType,
                                                      isSigned,
                                                      alphaPremultiplied);
        }

        ColorSpace rgb = ColorSpace.getInstance(ColorSpace.CS_sRGB);

        // 8-bit RGB
        if (samplesPerPixel == 3 &&
            bitsPerSample[0] == 8 &&
            bitsPerSample[1] == 8 &&
            bitsPerSample[2] == 8) {
            int[] bandOffsets = new int[3];
            bandOffsets[0] = 0;
            bandOffsets[1] = 1;
            bandOffsets[2] = 2;
            int dataType = DataBuffer.TYPE_BYTE;
            ColorSpace theColorSpace;
            if((photometricInterpretation ==
                BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_Y_CB_CR &&
                compression != BaselineTIFFTagSet.COMPRESSION_JPEG &&
                compression != BaselineTIFFTagSet.COMPRESSION_OLD_JPEG) ||
               photometricInterpretation ==
               BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_CIELAB) {
                theColorSpace =
                    ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
            } else {
                theColorSpace = rgb;
            }
            return ImageTypeSpecifier.createInterleaved(theColorSpace,
                                                        bandOffsets,
                                                        dataType,
                                                        false,
                                                        false);
        }

        // 8-bit RGBA
        if (samplesPerPixel == 4 &&
            bitsPerSample[0] == 8 &&
            bitsPerSample[1] == 8 &&
            bitsPerSample[2] == 8 &&
            bitsPerSample[3] == 8) {
            int[] bandOffsets = new int[4];
            bandOffsets[0] = 0;
            bandOffsets[1] = 1;
            bandOffsets[2] = 2;
            bandOffsets[3] = 3;
            int dataType = DataBuffer.TYPE_BYTE;

            ColorSpace theColorSpace;
            boolean hasAlpha;
            boolean alphaPremultiplied = false;
            if(photometricInterpretation ==
               BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_CMYK) {
                theColorSpace = SimpleCMYKColorSpace.getInstance();
                hasAlpha = false;
            } else {
                theColorSpace = rgb;
                hasAlpha = true;
                if (extraSamples != null &&
                    extraSamples[0] ==
                    BaselineTIFFTagSet.EXTRA_SAMPLES_ASSOCIATED_ALPHA) {
                    alphaPremultiplied = true;
                }
            }

            return ImageTypeSpecifier.createInterleaved(theColorSpace,
                                                        bandOffsets,
                                                        dataType,
                                                        hasAlpha,
                                                        alphaPremultiplied);
        }

        // 16-bit RGB
        if (samplesPerPixel == 3 &&
            bitsPerSample[0] == 16 &&
            bitsPerSample[1] == 16 &&
            bitsPerSample[2] == 16) {
            int[] bandOffsets = new int[3];
            bandOffsets[0] = 0;
            bandOffsets[1] = 1;
            bandOffsets[2] = 2;
            int dataType = sampleFormat[0] == 
                BaselineTIFFTagSet.SAMPLE_FORMAT_SIGNED_INTEGER ?
                DataBuffer.TYPE_SHORT :
                DataBuffer.TYPE_USHORT;
            return ImageTypeSpecifier.createInterleaved(rgb,
                                                        bandOffsets,
                                                        dataType,
                                                        false,
                                                        false);
        }

        // 16-bit RGBA
        if (samplesPerPixel == 4 &&
            bitsPerSample[0] == 16 &&
            bitsPerSample[1] == 16 &&
            bitsPerSample[2] == 16 &&
            bitsPerSample[3] == 16) {
            int[] bandOffsets = new int[4];
            bandOffsets[0] = 0;
            bandOffsets[1] = 1;
            bandOffsets[2] = 2;
            bandOffsets[3] = 3;
            int dataType = sampleFormat[0] == 
                BaselineTIFFTagSet.SAMPLE_FORMAT_SIGNED_INTEGER ?
                DataBuffer.TYPE_SHORT :
                DataBuffer.TYPE_USHORT;

            boolean alphaPremultiplied = false;
            if (extraSamples != null &&
                extraSamples[0] ==
                BaselineTIFFTagSet.EXTRA_SAMPLES_ASSOCIATED_ALPHA) {
                alphaPremultiplied = true;
            }
            return ImageTypeSpecifier.createInterleaved(rgb,
                                                        bandOffsets,
                                                        dataType,
                                                        true,
                                                        alphaPremultiplied);
        }
        
        // Support for Tiff files containing half-tone data
        // in more than 1 channel
        if((photometricInterpretation ==
            BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_CMYK) &&
           (bitsPerSample[0] == 1 || bitsPerSample[0] == 2 ||
            bitsPerSample[0] == 4)) {
            ColorSpace cs = null;
            if(samplesPerPixel == 4)
                cs = SimpleCMYKColorSpace.getInstance();
            else
                cs = new BogusColorSpace(samplesPerPixel);
            // By specifying the bits per sample the color values
            // will scale on display
            ColorModel cm =
                new ComponentColorModel(cs, bitsPerSample, false, false,
                                        Transparency.OPAQUE,
                                        DataBuffer.TYPE_BYTE);            
            return new ImageTypeSpecifier(cm,
                                          cm.createCompatibleSampleModel(1, 1));
        }

        // Compute bits per pixel.
        int totalBits = 0;
        for (int i = 0; i < bitsPerSample.length; i++) {
            totalBits += bitsPerSample[i];
        }

        // Packed: 3- or 4-band, 8- or 16-bit.
        if ((samplesPerPixel == 3 || samplesPerPixel == 4) &&
            (totalBits == 8 || totalBits == 16)) {
            int redMask = createMask(bitsPerSample, 0);
            int greenMask = createMask(bitsPerSample, 1);
            int blueMask = createMask(bitsPerSample, 2);
            int alphaMask = (samplesPerPixel == 4) ?
                createMask(bitsPerSample, 3) : 0;
            int transferType = totalBits == 8 ?
                DataBuffer.TYPE_BYTE : DataBuffer.TYPE_USHORT;
            boolean alphaPremultiplied = false;
            if (extraSamples != null &&
                extraSamples[0] ==
                BaselineTIFFTagSet.EXTRA_SAMPLES_ASSOCIATED_ALPHA) {
                alphaPremultiplied = true;
            }
            return ImageTypeSpecifier.createPacked(rgb,
                                                   redMask,
                                                   greenMask,
                                                   blueMask,
                                                   alphaMask,
                                                   transferType,
                                                   alphaPremultiplied);
        }

        // Generic components with 8X bits per sample.
        if(bitsPerSample[0] % 8 == 0) {
            // Check whether all bands have same bit depth.
            boolean allSameBitDepth = true;
            for(int i = 1; i < bitsPerSample.length; i++) {
                if(bitsPerSample[i] != bitsPerSample[i-1]) {
                    allSameBitDepth = false;
                    break;
                }
            }

            // Proceed if all bands have same bit depth.
            if(allSameBitDepth) {
                // Determine the data type.
                int dataType = -1;
                boolean isDataTypeSet = false;
                switch(bitsPerSample[0]) {
                case 8:
                    if(sampleFormat[0] !=
                       BaselineTIFFTagSet.SAMPLE_FORMAT_FLOATING_POINT) {
                        // Ignore whether signed or unsigned:
                        // treat all as unsigned.
                        dataType = DataBuffer.TYPE_BYTE;
                        isDataTypeSet = true;
                    }
                    break;
                case 16:
                    if(sampleFormat[0] !=
                       BaselineTIFFTagSet.SAMPLE_FORMAT_FLOATING_POINT) {
                        if(sampleFormat[0] ==
                           BaselineTIFFTagSet.SAMPLE_FORMAT_SIGNED_INTEGER) {
                            dataType = DataBuffer.TYPE_SHORT;
                        } else {
                            dataType = DataBuffer.TYPE_USHORT;
                        }
                        isDataTypeSet = true;
                    }
                    break;
                case 32:
                    if(sampleFormat[0] ==
                       BaselineTIFFTagSet.SAMPLE_FORMAT_FLOATING_POINT) {
                        dataType = DataBuffer.TYPE_FLOAT;
                    } else {
                        dataType = DataBuffer.TYPE_INT;
                    }
                    isDataTypeSet = true;
                    break;
                }

                if(isDataTypeSet) {
                    // Create the SampleModel.
                    SampleModel sm = createInterleavedSM(dataType,
                                                         samplesPerPixel);

                    // Create the ColorModel.
                    ColorModel cm;
                    if(samplesPerPixel >= 1 && samplesPerPixel <= 4 &&
                       (dataType == DataBuffer.TYPE_INT ||
                        dataType == DataBuffer.TYPE_FLOAT)) {
                        // Handle the 32-bit cases for 1-4 bands.
                        ColorSpace cs = samplesPerPixel <= 2 ?
                            ColorSpace.getInstance(ColorSpace.CS_GRAY) : rgb;
                        boolean hasAlpha = ((samplesPerPixel % 2) == 0);
                        boolean alphaPremultiplied = false;
                        if(hasAlpha && extraSamples != null &&
                           extraSamples[0] ==
                           BaselineTIFFTagSet.EXTRA_SAMPLES_ASSOCIATED_ALPHA) {
                            alphaPremultiplied = true;
                        }

                        cm = createComponentCM(cs,
                                               samplesPerPixel,
                                               dataType,
                                               hasAlpha,
                                               alphaPremultiplied);
                    } else {
                        ColorSpace cs = new BogusColorSpace(samplesPerPixel);
                        cm = createComponentCM(cs,
                                               samplesPerPixel,
                                               dataType,
                                               false, // hasAlpha
                                               false); // alphaPremultiplied
                    }
                    //System.out.println(cm); // XXX
                    return new ImageTypeSpecifier(cm, sm);
                }
            }
        }

        // Other more bizarre cases including discontiguous DataBuffers
        // such as for the image in bug 4918959.

        if(colorMap == null &&
           sampleFormat[0] !=
           BaselineTIFFTagSet.SAMPLE_FORMAT_FLOATING_POINT) {

            // Determine size of largest sample.
            int maxBitsPerSample = 0;
            for(int i = 0; i < bitsPerSample.length; i++) {
                if(bitsPerSample[i] > maxBitsPerSample) {
                    maxBitsPerSample = bitsPerSample[i];
                }
            }

            // Determine whether data are signed.
            boolean isSigned =
                (sampleFormat[0] ==
                 BaselineTIFFTagSet.SAMPLE_FORMAT_SIGNED_INTEGER);

            // Grayscale
            if(samplesPerPixel == 1) {
                int dataType =
                    getDataTypeFromNumBits(maxBitsPerSample, isSigned);

                return ImageTypeSpecifier.createGrayscale(maxBitsPerSample,
                                                          dataType,
                                                          isSigned);
            }

            // Gray-alpha
            if (samplesPerPixel == 2) {
                boolean alphaPremultiplied = false;
                if (extraSamples != null &&
                    extraSamples[0] ==
                    BaselineTIFFTagSet.EXTRA_SAMPLES_ASSOCIATED_ALPHA) {
                    alphaPremultiplied = true;
                }

                int dataType =
                    getDataTypeFromNumBits(maxBitsPerSample, isSigned);

                return ImageTypeSpecifier.createGrayscale(maxBitsPerSample,
                                                          dataType,
                                                          false,
                                                          alphaPremultiplied);
            }

            if (samplesPerPixel == 3 || samplesPerPixel == 4) {
                if(totalBits <= 32 && !isSigned) {
                    // Packed RGB or RGBA
                    int redMask = createMask(bitsPerSample, 0);
                    int greenMask = createMask(bitsPerSample, 1);
                    int blueMask = createMask(bitsPerSample, 2);
                    int alphaMask = (samplesPerPixel == 4) ?
                        createMask(bitsPerSample, 3) : 0;
                    int transferType =
                        getDataTypeFromNumBits(totalBits, false);
                    boolean alphaPremultiplied = false;
                    if (extraSamples != null &&
                        extraSamples[0] ==
                        BaselineTIFFTagSet.EXTRA_SAMPLES_ASSOCIATED_ALPHA) {
                        alphaPremultiplied = true;
                    }
                    return ImageTypeSpecifier.createPacked(rgb,
                                                           redMask,
                                                           greenMask,
                                                           blueMask,
                                                           alphaMask,
                                                           transferType,
                                                           alphaPremultiplied);
                } else if(samplesPerPixel == 3) {
                    // Interleaved RGB
                    int[] bandOffsets = new int[] {0, 1, 2};
                    int dataType =
                        getDataTypeFromNumBits(maxBitsPerSample, isSigned);
                    return ImageTypeSpecifier.createInterleaved(rgb,
                                                                bandOffsets,
                                                                dataType,
                                                                false,
                                                                false);
                } else if(samplesPerPixel == 4) {
                    // Interleaved RGBA
                    int[] bandOffsets = new int[] {0, 1, 2, 3};
                    int dataType =
                        getDataTypeFromNumBits(maxBitsPerSample, isSigned);
                    boolean alphaPremultiplied = false;
                    if (extraSamples != null &&
                        extraSamples[0] ==
                        BaselineTIFFTagSet.EXTRA_SAMPLES_ASSOCIATED_ALPHA) {
                        alphaPremultiplied = true;
                    }
                    return ImageTypeSpecifier.createInterleaved(rgb,
                                                                bandOffsets,
                                                                dataType,
                                                                true,
                                                                alphaPremultiplied);
                }
            } else {
                // Arbitrary Interleaved.
                int dataType =
                    getDataTypeFromNumBits(maxBitsPerSample, isSigned);
                SampleModel sm = createInterleavedSM(dataType,
                                                     samplesPerPixel);
                ColorSpace cs = new BogusColorSpace(samplesPerPixel);
                ColorModel cm = createComponentCM(cs,
                                                  samplesPerPixel,
                                                  dataType,
                                                  false, // hasAlpha
                                                  false); // alphaPremultiplied
                return new ImageTypeSpecifier(cm, sm);
            }
        }

        if(DEBUG) {
            System.out.println("\nNo raw ITS available:");

            System.out.println("photometricInterpretation = " +
                               photometricInterpretation);
            System.out.println("compression = " + compression);
            System.out.println("samplesPerPixel = " + samplesPerPixel);
            if (bitsPerSample != null) {
                for (int i = 0; i < bitsPerSample.length; i++) {
                    System.out.println("bitsPerSample[" + i + "] = " +
                                       (int)bitsPerSample[i]);
                }
            }
            if (sampleFormat != null) {
                for (int i = 0; i < sampleFormat.length; i++) {
                    System.out.println("sampleFormat[" + i + "] = " +
                                       (int)sampleFormat[i]);
                }
            }
            if (extraSamples != null) {
                for (int i = 0; i < extraSamples.length; i++) {
                    System.out.println("extraSamples[" + i + "] = " +
                                       (int)extraSamples[i]);
                }
            }
            System.out.println("colorMap = " + colorMap);
            if (colorMap != null) {
                System.out.println("colorMap.length = " + colorMap.length);
            }

            throw new RuntimeException("Unable to create an ImageTypeSpecifier");
        }

        return null;
    }

    /**
     * Sets the value of the <code>reader</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param reader the current <code>ImageReader</code>.
     */
    public void setReader(ImageReader reader) {
        this.reader = reader;
    }

    /**
     * Sets the value of the <code>metadata</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param metadata the <code>IIOMetadata</code> object for the
     * image being read.
     */
    public void setMetadata(IIOMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * Sets the value of the <code>photometricInterpretation</code>
     * field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param photometricInterpretation the photometric interpretation
     * value.
     */
    public void setPhotometricInterpretation(int photometricInterpretation) {
        this.photometricInterpretation = photometricInterpretation;
    }

    /**
     * Sets the value of the <code>compression</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param compression the compression type. 
     */
    public void setCompression(int compression) {
        this.compression = compression;
    }

    /**
     * Sets the value of the <code>planar</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param planar <code>true</code> if the image to be decoded is
     * stored in planar format.
     */
    public void setPlanar(boolean planar) {
        this.planar = planar;
    }

    /**
     * Sets the value of the <code>samplesPerPixel</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param samplesPerPixel the number of samples in each source
     * pixel.
     */
    public void setSamplesPerPixel(int samplesPerPixel) {
        this.samplesPerPixel = samplesPerPixel;
    }

    /**
     * Sets the value of the <code>bitsPerSample</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param bitsPerSample the number of bits for each source image
     * sample.
     */
    public void setBitsPerSample(int[] bitsPerSample) {
        this.bitsPerSample = bitsPerSample == null ?
            null : (int[])bitsPerSample.clone();
    }

    /**
     * Sets the value of the <code>sampleFormat</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param sampleFormat the format of the source image data,
     * for example unsigned integer or floating-point.
     */
    public void setSampleFormat(int[] sampleFormat) {
        this.sampleFormat = sampleFormat == null ?
            new int[] {BaselineTIFFTagSet.SAMPLE_FORMAT_UNSIGNED_INTEGER} :
            (int[])sampleFormat.clone();
    }

    /**
     * Sets the value of the <code>extraSamples</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param extraSamples the interpretation of any samples in the
     * source file beyond those used for basic color or grayscale
     * information.
     */
    public void setExtraSamples(int[] extraSamples) {
        this.extraSamples = extraSamples == null ?
            null : (int[])extraSamples.clone();
    }
    
    /**
     * Sets the value of the <code>colorMap</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param colorMap the color map to apply to the source data,
     * as an array of <code>char</code>s.
     */
    public void setColorMap(char[] colorMap) {
        this.colorMap = colorMap == null ?
            null : (char[])colorMap.clone();
    }

    /**
     * Sets the value of the <code>stream</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param stream the <code>ImageInputStream</code> to be read.
     */
    public void setStream(ImageInputStream stream) {
        this.stream = stream;
    }

    /**
     * Sets the value of the <code>offset</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param offset the offset of the beginning of the compressed
     * data.
     */
    public void setOffset(long offset) {
        this.offset = offset;
    }

    /**
     * Sets the value of the <code>byteCount</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param byteCount the number of bytes of compressed data.
     */
    public void setByteCount(int byteCount) {
        this.byteCount = byteCount;
    }

    // Region of the file image represented in the stream

    /**
     * Sets the value of the <code>srcMinX</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param srcMinX the minimum X coordinate of the source region
     * being decoded, irrespective of how it will be copied into the
     * destination.
     */
    public void setSrcMinX(int srcMinX) {
        this.srcMinX = srcMinX;
    }
    
    /**
     * Sets the value of the <code>srcMinY</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param srcMinY the minimum Y coordinate of the source region
     * being decoded, irrespective of how it will be copied into the
     * destination.
     */
    public void setSrcMinY(int srcMinY) {
        this.srcMinY = srcMinY;
    }
    
    /**
     * Sets the value of the <code>srcWidth</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param srcWidth the width of the source region being decoded,
     * irrespective of how it will be copied into the destination.
     */
    public void setSrcWidth(int srcWidth) {
        this.srcWidth = srcWidth;
    }
    
    /**
     * Sets the value of the <code>srcHeight</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param srcHeight the height of the source region being decoded,
     * irrespective of how it will be copied into the destination.
     */
    public void setSrcHeight(int srcHeight) {
        this.srcHeight = srcHeight;
    }

    // First source pixel to be read

    /**
     * Sets the value of the <code>sourceXOffset</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param sourceXOffset the horizontal source offset to be used when
     * mapping between source and destination coordinates.
     */
    public void setSourceXOffset(int sourceXOffset) {
        this.sourceXOffset = sourceXOffset;
    }

    /**
     * Sets the value of the <code>dstXOffset</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param dstXOffset the horizontal destination offset to be
     * used when mapping between source and destination coordinates.
     */
    public void setDstXOffset(int dstXOffset) {
        this.dstXOffset = dstXOffset;
    }

    /**
     * Sets the value of the <code>sourceYOffset</code>.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param sourceYOffset the vertical source offset to be used when
     * mapping between source and destination coordinates.
     */
    public void setSourceYOffset(int sourceYOffset) {
        this.sourceYOffset = sourceYOffset;
    }

    /**
     * Sets the value of the <code>dstYOffset</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param dstYOffset the vertical destination offset to be
     * used when mapping between source and destination coordinates.
     */
    public void setDstYOffset(int dstYOffset) {
        this.dstYOffset = dstYOffset;
    }

    // Subsampling to be performed

    /**
     * Sets the value of the <code>subsampleX</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param subsampleX the horizontal subsampling factor.
     *
     * @throws IllegalArgumentException if <code>subsampleX</code> is
     * less than or equal to 0.
     */
    public void setSubsampleX(int subsampleX) {
        if (subsampleX <= 0) {
            throw new IllegalArgumentException("subsampleX <= 0!");
        }
        this.subsampleX = subsampleX;
    }
    
    /**
     * Sets the value of the <code>subsampleY</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param subsampleY the vertical subsampling factor.
     *
     * @throws IllegalArgumentException if <code>subsampleY</code> is
     * less than or equal to 0.
     */
    public void setSubsampleY(int subsampleY) {
        if (subsampleY <= 0) {
            throw new IllegalArgumentException("subsampleY <= 0!");
        }
        this.subsampleY = subsampleY;
    }
    
    // Band subsetting/rearrangement

    /**
     * Sets the value of the <code>sourceBands</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param sourceBands an array of <code>int</code>s
     * specifying the source bands to be read.
     */
    public void setSourceBands(int[] sourceBands) {
        this.sourceBands = sourceBands == null ?
            null : (int[])sourceBands.clone();
    }

    /**
     * Sets the value of the <code>destinationBands</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param destinationBands an array of <code>int</code>s
     * specifying the destination bands to be written.
     */
    public void setDestinationBands(int[] destinationBands) {
        this.destinationBands = destinationBands == null ?
            null : (int[])destinationBands.clone();
    }

    // Destination image and region

    /**
     * Sets the value of the <code>image</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param image the destination <code>BufferedImage</code>.
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }
    
    /**
     * Sets the value of the <code>dstMinX</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param dstMinX the minimum X coordinate of the destination
     * region. 
     */
    public void setDstMinX(int dstMinX) {
        this.dstMinX = dstMinX;
    }
    
    /**
     * Sets the value of the <code>dstMinY</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param dstMinY the minimum Y coordinate of the destination
     * region.
     */
    public void setDstMinY(int dstMinY) {
        this.dstMinY = dstMinY;
    }
    
    /**
     * Sets the value of the <code>dstWidth</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param dstWidth the width of the destination region.
     */
    public void setDstWidth(int dstWidth) {
        this.dstWidth = dstWidth;
    }

    /**
     * Sets the value of the <code>dstHeight</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param dstHeight the height of the destination region.
     */
    public void setDstHeight(int dstHeight) {
        this.dstHeight = dstHeight;
    }

    // Active source region

    /**
     * Sets the value of the <code>activeSrcMinX</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param activeSrcMinX the minimum X coordinate of the active
     * source region.
     */
    public void setActiveSrcMinX(int activeSrcMinX) {
        this.activeSrcMinX = activeSrcMinX;
    }
    
    /**
     * Sets the value of the <code>activeSrcMinY</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param activeSrcMinY the minimum Y coordinate of the active
     * source region.
     */
    public void setActiveSrcMinY(int activeSrcMinY) {
        this.activeSrcMinY = activeSrcMinY;
    }
    
    /**
     * Sets the value of the <code>activeSrcWidth</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param activeSrcWidth the width of the active source region.
     */
    public void setActiveSrcWidth(int activeSrcWidth) {
        this.activeSrcWidth = activeSrcWidth;
    }
    
    /**
     * Sets the value of the <code>activeSrcHeight</code> field.
     *
     * <p> If this method is called, the <code>beginDecoding</code>
     * method must be called prior to calling any of the decode
     * methods.
     *
     * @param activeSrcHeight the height of the active source region.
     */
    public void setActiveSrcHeight(int activeSrcHeight) {
        this.activeSrcHeight = activeSrcHeight;
    }

    /**
     * Sets the <code>TIFFColorConverter</code> object describing the color
     * space of the encoded data in the input stream.  If no
     * <code>TIFFColorConverter</code> is set, no conversion will be performed.
     *
     * @param colorConverter a <code>TIFFColorConverter</code> object, or
     * <code>null</code>.
     */
    public void setColorConverter(TIFFColorConverter colorConverter) {
        this.colorConverter = colorConverter;
    }

    /**
     * Returns an <code>ImageTypeSpecifier</code> describing an image
     * whose underlying data array has the same format as the raw
     * source pixel data.
     *
     * @return an <code>ImageTypeSpecifier</code>.
     */
    public ImageTypeSpecifier getRawImageType() {
        ImageTypeSpecifier its =
            getRawImageTypeSpecifier(photometricInterpretation,
                                     compression,
                                     samplesPerPixel,
                                     bitsPerSample,
                                     sampleFormat,
                                     extraSamples,
                                     colorMap);
        return its;
    }

    /**
     * Creates a <code>BufferedImage</code> whose underlying data
     * array will be suitable for holding the raw decoded output of
     * the <code>decodeRaw</code> method.
     *
     * <p> The default implementation calls
     * <code>getRawImageType</code>, and calls the resulting
     * <code>ImageTypeSpecifier</code>'s
     * <code>createBufferedImage</code> method.
     *
     * @return a <code>BufferedImage</code> whose underlying data
     * array has the same format as the raw source pixel data, or
     * <code>null</code> if it is not possible to create such an
     * image.
     */
    public BufferedImage createRawImage() { 
        if (planar) {
            // Create a single-banded image of the appropriate data type.

            // Get the number of bits per sample.
            int bps = bitsPerSample[sourceBands[0]];

            // Determine the data type.
            int dataType;
            if(sampleFormat[0] ==
               BaselineTIFFTagSet.SAMPLE_FORMAT_FLOATING_POINT) {
                dataType = DataBuffer.TYPE_FLOAT;
            } else if(bps <= 8) {
                dataType = DataBuffer.TYPE_BYTE;
            } else if(bps <= 16) {
                if(sampleFormat[0] ==
                   BaselineTIFFTagSet.SAMPLE_FORMAT_SIGNED_INTEGER) {
                    dataType = DataBuffer.TYPE_SHORT;
                } else {
                    dataType = DataBuffer.TYPE_USHORT;
                }
            } else {
                dataType = DataBuffer.TYPE_INT;
            }

            ColorSpace csGray = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            
            ImageTypeSpecifier its = null;
            // For planar images with 1, 2 or 4 bits per sample, we need to
            // use a MultiPixelPackedSampleModel so that when the TIFF
            // decoder properly decodes the data per pixel, we know how to
            // extract it back out into individual pixels.  This is how the
            // pixels are actually stored in the planar bands.
            if(bps == 1 || bps == 2 || bps == 4) {
                int bits = bps;
                int size = 1 << bits;
                byte[] r = new byte[size];
                byte[] g = new byte[size];
                byte[] b = new byte[size];
                for(int j=0; j<r.length; j++) {
                    r[j] = 0;
                    g[j] = 0;
                    b[j] = 0;
                }
                ColorModel cmGray= new IndexColorModel(bits,size,r,g,b);
                SampleModel smGray = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE, 1, 1, bits);
                its = new ImageTypeSpecifier(cmGray, smGray);
            }
            else {
                its = ImageTypeSpecifier.createInterleaved(csGray,
                                                     new int[] {0},
                                                     dataType,
                                                     false,
                                                     false);
            }

            return its.createBufferedImage(srcWidth, srcHeight);

            /* XXX Not necessarily byte for planar
            return new BufferedImage(srcWidth, srcHeight,
                                     BufferedImage.TYPE_BYTE_GRAY);
            */
        } else {
            ImageTypeSpecifier its = getRawImageType();
            if (its == null) {
                return null;
            }
            
            BufferedImage bi = its.createBufferedImage(srcWidth, srcHeight);
            return bi;
        }
    }

    /**
     * Decodes the source data into the provided <code>byte</code>
     * array <code>b</code>, starting at the offset given by
     * <code>dstOffset</code>.  Each pixel occupies
     * <code>bitsPerPixel</code> bits, with no padding between pixels.
     * Scanlines are separated by <code>scanlineStride</code>
     * <code>byte</code>s.
     *
     * @param b a <code>byte</code> array to be written.
     * @param dstOffset the starting offset in <code>b</code> to be
     * written.
     * @param bitsPerPixel the number of bits for each pixel.
     * @param scanlineStride the number of <code>byte</code>s to
     * advance between that starting pixels of each scanline.
     *
     * @throws IOException if an error occurs reading from the source
     * <code>ImageInputStream</code>.
     */
    public abstract void decodeRaw(byte[] b,
                                   int dstOffset,
                                   int bitsPerPixel,
                                   int scanlineStride) throws IOException;

    /**
     * Decodes the source data into the provided <code>short</code>
     * array <code>s</code>, starting at the offset given by
     * <code>dstOffset</code>.  Each pixel occupies
     * <code>bitsPerPixel</code> bits, with no padding between pixels.
     * Scanlines are separated by <code>scanlineStride</code>
     * <code>short</code>s
     *
     * <p> The default implementation calls <code>decodeRaw(byte[] b,
     * ...)</code> and copies the resulting data into <code>s</code>.
     *
     * @param s a <code>short</code> array to be written.
     * @param dstOffset the starting offset in <code>s</code> to be
     * written.
     * @param bitsPerPixel the number of bits for each pixel.
     * @param scanlineStride the number of <code>short</code>s to
     * advance between that starting pixels of each scanline.
     *
     * @throws IOException if an error occurs reading from the source
     * <code>ImageInputStream</code>.
     */
    public void decodeRaw(short[] s,
                          int dstOffset,
                          int bitsPerPixel,
                          int scanlineStride) throws IOException {
        int bytesPerRow = (srcWidth*bitsPerPixel + 7)/8;
        int shortsPerRow = bytesPerRow/2;

        byte[] b = new byte[bytesPerRow*srcHeight];
        decodeRaw(b, 0, bitsPerPixel, bytesPerRow);

        int bOffset = 0;
        if(stream.getByteOrder() == ByteOrder.BIG_ENDIAN) {
            for (int j = 0; j < srcHeight; j++) {
                for (int i = 0; i < shortsPerRow; i++) {
                    short hiVal = b[bOffset++];
                    short loVal = b[bOffset++];
                    short sval = (short)((hiVal << 8) | (loVal & 0xff));
                    s[dstOffset + i] = sval;
                }

                dstOffset += scanlineStride;
            }
        } else { // ByteOrder.LITLE_ENDIAN
            for (int j = 0; j < srcHeight; j++) {
                for (int i = 0; i < shortsPerRow; i++) {
                    short loVal = b[bOffset++];
                    short hiVal = b[bOffset++];
                    short sval = (short)((hiVal << 8) | (loVal & 0xff));
                    s[dstOffset + i] = sval;
                }

                dstOffset += scanlineStride;
            }
        }
    }

    /**
     * Decodes the source data into the provided <code>int</code>
     * array <code>i</code>, starting at the offset given by
     * <code>dstOffset</code>.  Each pixel occupies
     * <code>bitsPerPixel</code> bits, with no padding between pixels.
     * Scanlines are separated by <code>scanlineStride</code>
     * <code>int</code>s.
     *
     * <p> The default implementation calls <code>decodeRaw(byte[] b,
     * ...)</code> and copies the resulting data into <code>i</code>.
     *
     * @param i an <code>int</code> array to be written.
     * @param dstOffset the starting offset in <code>i</code> to be
     * written.
     * @param bitsPerPixel the number of bits for each pixel.
     * @param scanlineStride the number of <code>int</code>s to
     * advance between that starting pixels of each scanline.
     *
     * @throws IOException if an error occurs reading from the source
     * <code>ImageInputStream</code>.
     */
    public void decodeRaw(int[] i,
                          int dstOffset,
                          int bitsPerPixel,
                          int scanlineStride) throws IOException {
        int numBands = bitsPerPixel/32;
        int intsPerRow = srcWidth*numBands;
        int bytesPerRow = intsPerRow*4;

        byte[] b = new byte[bytesPerRow*srcHeight];
        decodeRaw(b, 0, bitsPerPixel, bytesPerRow);

        int bOffset = 0;
        if(stream.getByteOrder() == ByteOrder.BIG_ENDIAN) {
            for (int j = 0; j < srcHeight; j++) {
                for (int k = 0; k < intsPerRow; k++) {
                    int v0 = b[bOffset++] & 0xff;
                    int v1 = b[bOffset++] & 0xff; 
                    int v2 = b[bOffset++] & 0xff;
                    int v3 = b[bOffset++] & 0xff;
                    int ival = (v0 << 24) | (v1 << 16) | (v2 << 8) | v3;
                    i[dstOffset + k] = ival;
                }

                dstOffset += scanlineStride;
            }
        } else { // ByteOrder.LITLE_ENDIAN
            for (int j = 0; j < srcHeight; j++) {
                for (int k = 0; k < intsPerRow; k++) {
                    int v3 = b[bOffset++] & 0xff;
                    int v2 = b[bOffset++] & 0xff; 
                    int v1 = b[bOffset++] & 0xff;
                    int v0 = b[bOffset++] & 0xff;
                    int ival = (v0 << 24) | (v1 << 16) | (v2 << 8) | v3;
                    i[dstOffset + k] = ival;
                }

                dstOffset += scanlineStride;
            }
        }
    }

    /**
     * Decodes the source data into the provided <code>float</code>
     * array <code>f</code>, starting at the offset given by
     * <code>dstOffset</code>.  Each pixel occupies
     * <code>bitsPerPixel</code> bits, with no padding between pixels.
     * Scanlines are separated by <code>scanlineStride</code>
     * <code>float</code>s.
     *
     * <p> The default implementation calls <code>decodeRaw(byte[] b,
     * ...)</code> and copies the resulting data into <code>f</code>.
     *
     * @param f a <code>float</code> array to be written.
     * @param dstOffset the starting offset in <code>f</code> to be
     * written.
     * @param bitsPerPixel the number of bits for each pixel.
     * @param scanlineStride the number of <code>float</code>s to
     * advance between that starting pixels of each scanline.
     *
     * @throws IOException if an error occurs reading from the source
     * <code>ImageInputStream</code>.
     */
    public void decodeRaw(float[] f,
                          int dstOffset,
                          int bitsPerPixel,
                          int scanlineStride) throws IOException {
        int numBands = bitsPerPixel/32;
        int floatsPerRow = srcWidth*numBands;
        int bytesPerRow = floatsPerRow*4;

        byte[] b = new byte[bytesPerRow*srcHeight];
        decodeRaw(b, 0, bitsPerPixel, bytesPerRow);

        int bOffset = 0;
        if(stream.getByteOrder() == ByteOrder.BIG_ENDIAN) {
            for (int j = 0; j < srcHeight; j++) {
                for (int i = 0; i < floatsPerRow; i++) {
                    int v0 = b[bOffset++] & 0xff;
                    int v1 = b[bOffset++] & 0xff; 
                    int v2 = b[bOffset++] & 0xff;
                    int v3 = b[bOffset++] & 0xff;
                    int ival = (v0 << 24) | (v1 << 16) | (v2 << 8) | v3;
                    float fval = Float.intBitsToFloat(ival);
                    f[dstOffset + i] = fval;
                }

                dstOffset += scanlineStride;
            }
        } else { // ByteOrder.LITLE_ENDIAN
            for (int j = 0; j < srcHeight; j++) {
                for (int i = 0; i < floatsPerRow; i++) {
                    int v3 = b[bOffset++] & 0xff;
                    int v2 = b[bOffset++] & 0xff; 
                    int v1 = b[bOffset++] & 0xff;
                    int v0 = b[bOffset++] & 0xff;
                    int ival = (v0 << 24) | (v1 << 16) | (v2 << 8) | v3;
                    float fval = Float.intBitsToFloat(ival);
                    f[dstOffset + i] = fval;
                }

                dstOffset += scanlineStride;
            }
        }
    }

    //
    // Values used to prevent unneeded recalculation of bit adjustment table.
    //
    private boolean isFirstBitDepthTable = true;
    private boolean planarCache = false;
    private int[] destBitsPerSampleCache = null;
    private int[] sourceBandsCache = null;
    private int[] bitsPerSampleCache = null;
    private int[] destinationBandsCache = null;

    /**
     * This routine is called prior to a sequence of calls to the
     * <code>decode</code> method, in order to allow any necessary
     * tables or other structures to be initialized based on metadata
     * values.  This routine is guaranteed to be called any time the
     * metadata values have changed.
     *
     * <p> The default implementation computes tables used by the
     * <code>decode</code> method to rescale components to different
     * bit depths.  Thus, if this method is overridden, it is
     * important for the subclass method to call <code>super()</code>,
     * unless it overrides <code>decode</code> as well.
     */
    public void beginDecoding() {
        // Note: This method assumes that sourceBands, destinationBands,
        // and bitsPerSample are all non-null which is true as they are
        // set up that way in TIFFImageReader. Also the lengths and content
        // of sourceBands and destinationBands are checked in TIFFImageReader
        // before the present method is invoked.

        // Determine if all of the relevant output bands have the
        // same bit depth as the source data
        this.adjustBitDepths = false;
        int numBands = destinationBands.length;
        int[] destBitsPerSample = null;
        if(planar) {
            int totalNumBands = bitsPerSample.length;
            destBitsPerSample = new int[totalNumBands];
            int dbps = image.getSampleModel().getSampleSize(0);
            for(int b = 0; b < totalNumBands; b++) {
                destBitsPerSample[b] = dbps;
            }
        } else {
            destBitsPerSample = image.getSampleModel().getSampleSize();
        }

        // Make sure that the image is not CMYK (separated) or does not have
        // bits per sample of 1, 2, or 4 before trying adjust.
        if(photometricInterpretation !=
           BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_CMYK || 
           bitsPerSample[0] != 1 && bitsPerSample[0] != 2 &&
           bitsPerSample[0] != 4) {
            for (int b = 0; b < numBands; b++) {
                if (destBitsPerSample[destinationBands[b]] !=
                    bitsPerSample[sourceBands[b]]) {
                    adjustBitDepths = true;
                    break;
                }
            }
        }

        // If the bit depths differ, create a lookup table
        // per band to perform the conversion
        if(adjustBitDepths) {
            // Compute the table only if this is the first time one is
            // being computed or if any of the variables on which the
            // table is based have changed.
            if(this.isFirstBitDepthTable ||
               planar != planarCache ||
               !areIntArraysEqual(destBitsPerSample,
                                  destBitsPerSampleCache) ||
               !areIntArraysEqual(sourceBands,
                                  sourceBandsCache) ||
               !areIntArraysEqual(bitsPerSample,
                                  bitsPerSampleCache) ||
               !areIntArraysEqual(destinationBands,
                                  destinationBandsCache)) {

                this.isFirstBitDepthTable = false;

                // Cache some variables.
                this.planarCache = planar;
                this.destBitsPerSampleCache =
                    (int[])destBitsPerSample.clone(); // never null ...
                this.sourceBandsCache = sourceBands == null ?
                    null : (int[])sourceBands.clone();
                this.bitsPerSampleCache = bitsPerSample == null ?
                    null : (int[])bitsPerSample.clone();
                this.destinationBandsCache = destinationBands == null ?
                    null : (int[])destinationBands.clone();

                // Allocate and fill the table.
                bitDepthScale = new int[numBands][];
                for (int b = 0; b < numBands; b++) {
                    int maxInSample = (1 << bitsPerSample[sourceBands[b]]) - 1;
                    int halfMaxInSample = maxInSample/2;

                    int maxOutSample =
                        (1 << destBitsPerSample[destinationBands[b]]) - 1;

                    bitDepthScale[b] = new int[maxInSample + 1];
                    for (int s = 0; s <= maxInSample; s++) {
                        bitDepthScale[b][s] =
                            (s*maxOutSample + halfMaxInSample)/
                            maxInSample;
                    }
                }
            }
        } else { // !adjustBitDepths
            // Clear any prior table.
            this.bitDepthScale = null;
        }

        // Determine whether source and destination band lists are ramps.
        // Note that these conditions will be true for planar images if
        // and only if samplesPerPixel == 1, sourceBands[0] == 0, and
        // destinationBands[0] == 0. For the purposes of this method, the
        // only difference between such a planar image and a chunky image
        // is the setting of the PlanarConfiguration field.
        boolean sourceBandsNormal = false;
        boolean destinationBandsNormal = false;
        if (numBands == samplesPerPixel) {
            sourceBandsNormal = true;
            destinationBandsNormal = true;
            for (int i = 0; i < numBands; i++) {
                if (sourceBands[i] != i) {
                    sourceBandsNormal = false;
                }
                if (destinationBands[i] != i) {
                    destinationBandsNormal = false;
                }
            }
        }

        // Determine whether the image is bilevel and/or contiguous.
        // Note that a planar image could be bilevel but it will not
        // be contiguous unless it has a single component band stored
        // in a single bank.
        this.isBilevel =
            ImageUtil.isBinary(this.image.getRaster().getSampleModel());
        this.isContiguous = this.isBilevel ?
            true : ImageUtil.imageIsContiguous(this.image);

        // Analyze destination image to see if we can copy into it
        // directly

        this.isImageSimple =
            (colorConverter == null) &&
            (subsampleX == 1) && (subsampleY == 1) &&
            (srcWidth == dstWidth) && (srcHeight == dstHeight) &&
            ((dstMinX + dstWidth) <= image.getWidth()) &&
            ((dstMinY + dstHeight) <= image.getHeight()) &&
            sourceBandsNormal && destinationBandsNormal &&
            !adjustBitDepths;
    }

    /**
     * Decodes the input bit stream (located in the
     * <code>ImageInputStream</code> <code>stream</code>, at offset
     * <code>offset</code>, and continuing for <code>byteCount</code>
     * bytes) into the output <code>BufferedImage</code>
     * <code>image</code>.
     *
     * <p> The default implementation analyzes the destination image
     * to determine if it is suitable as the destination for the
     * <code>decodeRaw</code> method.  If not, a suitable image is
     * created.  Next, <code>decodeRaw</code> is called to perform the
     * actual decoding, and the results are copied into the
     * destination image if necessary.  Subsampling and offsetting are
     * performed automatically.
     * 
     * <p> The precise responsibilities of this routine are as
     * follows.  The input bit stream is defined by the instance
     * variables <code>stream</code>, <code>offset</code>, and
     * <code>byteCount</code>.  These bits contain the data for the
     * region of the source image defined by <code>srcMinX</code>,
     * <code>srcMinY</code>, <code>srcWidth</code>, and
     * <code>srcHeight</code>.
     *
     * <p> The source data is required to be subsampling, starting at
     * the <code>sourceXOffset</code>th column and including
     * every <code>subsampleX</code>th pixel thereafter (and similarly
     * for <code>sourceYOffset</code> and
     * <code>subsampleY</code>).
     *
     * <p> Pixels are copied into the destination with an addition shift of 
     * (<code>dstXOffset</code>, <code>dstYOffset</code>).  The complete
     * set of formulas relating the source and destination coordinate spaces
     * are:
     *
     * <pre>
     * dx = (sx - sourceXOffset)/subsampleX + dstXOffset;
     * dy = (sy - sourceYOffset)/subsampleY + dstYOffset; 
     * </pre>
     *
     * Only source pixels such that <code>(sx - sourceXOffset) %
     * subsampleX == 0</code> and <code>(sy - sourceYOffset) %
     * subsampleY == 0</code> are copied.
     *
     * <p> The inverse mapping, from destination to source coordinates, 
     * is one-to-one:
     *
     * <pre>
     * sx = (dx - dstXOffset)*subsampleX + sourceXOffset;
     * sy = (dy - dstYOffset)*subsampleY + sourceYOffset;
     * </pre>
     *
     * <p> The region of the destination image to be updated is given
     * by the instance variables <code>dstMinX</code>,
     * <code>dstMinY</code>, <code>dstWidth</code>, and
     * <code>dstHeight</code>.
     *
     * <p> It is possible that not all of the source data being read
     * will contribute to the destination image.  For example, the
     * destination offsets could be set such that some of the source
     * pixels land outside of the bounds of the image.  As a
     * convenience, the bounds of the active source region (that is,
     * the region of the strip or tile being read that actually
     * contributes to the destination image, taking clipping into
     * account) are available as <code>activeSrcMinX</code>,
     * <code>activeSrcMinY</code>, <code>activeSrcWidth</code> and
     * <code>activeSrcHeight</code>.  Thus, the source pixel at
     * (<code>activeSrcMinX</code>, <code>activeSrcMinY</code>) will
     * map to the destination pixel (<code>dstMinX</code>,
     * <code>dstMinY</code>).
     *
     * <p> The sequence of source bands given by
     * <code>sourceBands</code> are to be copied into the sequence of
     * bands in the destination given by
     * <code>destinationBands</code>.
     *
     * <p> Some standard tag information is provided the instance
     * variables <code>photometricInterpretation</code>,
     * <code>compression</code>, <code>samplesPerPixel</code>,
     * <code>bitsPerSample</code>, <code>sampleFormat</code>,
     * <code>extraSamples</code>, and <code>colorMap</code>.
     *
     * <p> In practice, unless there is a significant performance
     * advantage to be gained by overriding this routine, most users
     * will prefer to use the default implementation of this routine,
     * and instead override the <code>decodeRaw</code> and/or
     * <code>getRawImageType</code> methods.
     *
     * @exception IOException if an error occurs in
     * <code>decodeRaw</code>.
     */
    public void decode() throws IOException {
        byte[] byteData = null;
        short[] shortData = null;
        int[] intData = null;
        float[] floatData = null;

        int dstOffset = 0;
        int pixelBitStride = 1;
        int scanlineStride = 0;

        // Analyze raw image

        this.rawImage = null;
        if(isImageSimple) {
            if(isBilevel) {
                rawImage = this.image;
            } else if (isContiguous) {
                rawImage =
                    image.getSubimage(dstMinX, dstMinY, dstWidth, dstHeight);
            }
        }

        boolean isDirectCopy = rawImage != null;

        if(rawImage == null) {
            rawImage = createRawImage();
            if (rawImage == null) {
                throw new IIOException("Couldn't create image buffer!");
            }
        }

        WritableRaster ras = rawImage.getRaster();

        if(isBilevel) {
            Rectangle rect = isImageSimple ?
                new Rectangle(dstMinX, dstMinY, dstWidth, dstHeight) :
                ras.getBounds();
            byteData = ImageUtil.getPackedBinaryData(ras, rect);
            dstOffset = 0;
            pixelBitStride = 1;
            scanlineStride = (rect.width + 7)/8;
        } else {
            SampleModel sm = ras.getSampleModel();
            DataBuffer db = ras.getDataBuffer();

            boolean isSupportedType = false;

            if (sm instanceof ComponentSampleModel) {
                ComponentSampleModel csm = (ComponentSampleModel)sm;
                dstOffset = csm.getOffset(-ras.getSampleModelTranslateX(),
                                          -ras.getSampleModelTranslateY());
                scanlineStride = csm.getScanlineStride();
                if(db instanceof DataBufferByte) {
                    DataBufferByte dbb = (DataBufferByte)db;

                    byteData = dbb.getData();
                    pixelBitStride = csm.getPixelStride()*8;
                    isSupportedType = true;
                } else if(db instanceof DataBufferUShort) {
                    DataBufferUShort dbus = (DataBufferUShort)db;

                    shortData = dbus.getData();
                    pixelBitStride = csm.getPixelStride()*16;
                    isSupportedType = true;
                } else if(db instanceof DataBufferShort) {
                    DataBufferShort dbs = (DataBufferShort)db;

                    shortData = dbs.getData();
                    pixelBitStride = csm.getPixelStride()*16;
                    isSupportedType = true;
                } else if(db instanceof DataBufferInt) {
                    DataBufferInt dbi = (DataBufferInt)db;

                    intData = dbi.getData();
                    pixelBitStride = csm.getPixelStride()*32;
                    isSupportedType = true;
                } else if(db instanceof DataBufferFloat) {
                    DataBufferFloat dbf = (DataBufferFloat)db;

                    floatData = dbf.getData();
                    pixelBitStride = csm.getPixelStride()*32;
                    isSupportedType = true;
                }
            } else if (sm instanceof MultiPixelPackedSampleModel) {
                MultiPixelPackedSampleModel mppsm =
                    (MultiPixelPackedSampleModel)sm;
                dstOffset =
                    mppsm.getOffset(-ras.getSampleModelTranslateX(),
                                    -ras.getSampleModelTranslateY());
                pixelBitStride = mppsm.getPixelBitStride();
                scanlineStride = mppsm.getScanlineStride();
                if(db instanceof DataBufferByte) {
                    DataBufferByte dbb = (DataBufferByte)db;

                    byteData = dbb.getData();
                    isSupportedType = true;
                } else if(db instanceof DataBufferUShort) {
                    DataBufferUShort dbus = (DataBufferUShort)db;

                    shortData = dbus.getData();
                    isSupportedType = true;
                } else if(db instanceof DataBufferInt) {
                    DataBufferInt dbi = (DataBufferInt)db;

                    intData = dbi.getData();
                    isSupportedType = true;
                }
            } else if (sm instanceof SinglePixelPackedSampleModel) {
                SinglePixelPackedSampleModel sppsm =
                    (SinglePixelPackedSampleModel)sm;
                dstOffset =
                    sppsm.getOffset(-ras.getSampleModelTranslateX(),
                                    -ras.getSampleModelTranslateY());
                scanlineStride = sppsm.getScanlineStride();
                if(db instanceof DataBufferByte) {
                    DataBufferByte dbb = (DataBufferByte)db;

                    byteData = dbb.getData();
                    pixelBitStride = 8;
                    isSupportedType = true;
                } else if(db instanceof DataBufferUShort) {
                    DataBufferUShort dbus = (DataBufferUShort)db;

                    shortData = dbus.getData();
                    pixelBitStride = 16;
                    isSupportedType = true;
                } else if(db instanceof DataBufferInt) {
                    DataBufferInt dbi = (DataBufferInt)db;

                    intData = dbi.getData();
                    pixelBitStride = 32;
                    isSupportedType = true;
                }
            }

            if(!isSupportedType) {
                throw new IIOException
                    ("Unsupported raw image type: SampleModel = "+sm+
                     "; DataBuffer = "+db);
            }
        }

        if(isBilevel) {
            // Bilevel data are always in a contiguous byte buffer.
            decodeRaw(byteData, dstOffset, pixelBitStride, scanlineStride);
        } else {
            SampleModel sm = ras.getSampleModel();

            // Branch based on whether data are bit-contiguous, i.e.,
            // data are packaed as tightly as possible leaving no unused
            // bits except at the end of a row.
            if(isDataBufferBitContiguous(sm)) {
                // Use byte or float data directly.
                if (byteData != null) {
                    if(DEBUG) {
                        System.out.println("Decoding bytes directly");
                    }
                    decodeRaw(byteData, dstOffset,
                              pixelBitStride, scanlineStride);
                } else if (floatData != null) {
                    if(DEBUG) {
                        System.out.println("Decoding floats directly");
                    }
                    decodeRaw(floatData, dstOffset,
                              pixelBitStride, scanlineStride);
                } else {
                    if (shortData != null) {
                        if(areSampleSizesEqual(sm) &&
                           sm.getSampleSize(0) == 16) {
                            if(DEBUG) {
                                System.out.println("Decoding shorts directly");
                            }
                            // Decode directly into short data.
                            decodeRaw(shortData, dstOffset,
                                      pixelBitStride, scanlineStride);
                        } else {
                            if(DEBUG) {
                                System.out.println("Decoding bytes->shorts");
                            }
                            // Decode into bytes and reformat into shorts.
                            int bpp = getBitsPerPixel(sm);
                            int bytesPerRow = (bpp*srcWidth + 7)/8;
                            byte[] buf = new byte[bytesPerRow*srcHeight];
                            decodeRaw(buf, 0, bpp, bytesPerRow);
                            reformatData(buf, bytesPerRow, srcHeight,
                                         shortData, null,
                                         dstOffset, scanlineStride);
                        }
                    } else if (intData != null) {
                        if(areSampleSizesEqual(sm) &&
                           sm.getSampleSize(0) == 32) {
                            if(DEBUG) {
                                System.out.println("Decoding ints directly");
                            }
                            // Decode directly into int data.
                            decodeRaw(intData, dstOffset,
                                      pixelBitStride, scanlineStride);
                        } else {
                            if(DEBUG) {
                                System.out.println("Decoding bytes->ints");
                            }
                            // Decode into bytes and reformat into ints.
                            int bpp = getBitsPerPixel(sm);
                            int bytesPerRow = (bpp*srcWidth + 7)/8;
                            byte[] buf = new byte[bytesPerRow*srcHeight];
                            decodeRaw(buf, 0, bpp, bytesPerRow);
                            reformatData(buf, bytesPerRow, srcHeight,
                                         null, intData,
                                         dstOffset, scanlineStride);
                        }
                    }
                }
            } else {
                if(DEBUG) {
                    System.out.println("Decoding discontiguous data");
                }
                // Read discontiguous data into bytes and set the samples
                // into the Raster.
                int bpp = getBitsPerPixel(sm);
                int bytesPerRow = (bpp*srcWidth + 7)/8;
                byte[] buf = new byte[bytesPerRow*srcHeight];
                decodeRaw(buf, 0, bpp, bytesPerRow);
                reformatDiscontiguousData(buf, bytesPerRow,
                                          srcWidth, srcHeight,
                                          ras);
            }
        }

        //         System.out.println("colorConverter = " + colorConverter);

        if (colorConverter != null) {
            float[] rgb = new float[3];

            if(byteData != null) {
                for (int j = 0; j < dstHeight; j++) {
                    int idx = dstOffset;
                    for (int i = 0; i < dstWidth; i++) {
                        float x0 = (float)(byteData[idx] & 0xff);
                        float x1 = (float)(byteData[idx + 1] & 0xff);
                        float x2 = (float)(byteData[idx + 2] & 0xff);

                        colorConverter.toRGB(x0, x1, x2, rgb);

                        byteData[idx] = (byte)(rgb[0]);
                        byteData[idx + 1] = (byte)(rgb[1]);
                        byteData[idx + 2] = (byte)(rgb[2]);

                        idx += 3;
                    }

                    dstOffset += scanlineStride;
                }
            } else if(shortData != null) {
                if(sampleFormat[0] ==
                   BaselineTIFFTagSet.SAMPLE_FORMAT_SIGNED_INTEGER) {
                    for (int j = 0; j < dstHeight; j++) {
                        int idx = dstOffset;
                        for (int i = 0; i < dstWidth; i++) {
                            float x0 = (float)shortData[idx];
                            float x1 = (float)shortData[idx + 1];
                            float x2 = (float)shortData[idx + 2];

                            colorConverter.toRGB(x0, x1, x2, rgb);

                            shortData[idx] = (short)(rgb[0]);
                            shortData[idx + 1] = (short)(rgb[1]);
                            shortData[idx + 2] = (short)(rgb[2]);

                            idx += 3;
                        }

                        dstOffset += scanlineStride;
                    }
                } else {
                    for (int j = 0; j < dstHeight; j++) {
                        int idx = dstOffset;
                        for (int i = 0; i < dstWidth; i++) {
                            float x0 = (float)(shortData[idx] & 0xffff);
                            float x1 = (float)(shortData[idx + 1] & 0xffff);
                            float x2 = (float)(shortData[idx + 2] & 0xffff);

                            colorConverter.toRGB(x0, x1, x2, rgb);

                            shortData[idx] = (short)(rgb[0]);
                            shortData[idx + 1] = (short)(rgb[1]);
                            shortData[idx + 2] = (short)(rgb[2]);

                            idx += 3;
                        }

                        dstOffset += scanlineStride;
                    }
                }
            } else if(intData != null) {
                for (int j = 0; j < dstHeight; j++) {
                    int idx = dstOffset;
                    for (int i = 0; i < dstWidth; i++) {
                        float x0 = (float)intData[idx];
                        float x1 = (float)intData[idx + 1];
                        float x2 = (float)intData[idx + 2];

                        colorConverter.toRGB(x0, x1, x2, rgb);

                        intData[idx] = (int)(rgb[0]);
                        intData[idx + 1] = (int)(rgb[1]);
                        intData[idx + 2] = (int)(rgb[2]);

                        idx += 3;
                    }

                    dstOffset += scanlineStride;
                }
            } else if(floatData != null) {
                for (int j = 0; j < dstHeight; j++) {
                    int idx = dstOffset;
                    for (int i = 0; i < dstWidth; i++) {
                        float x0 = floatData[idx];
                        float x1 = floatData[idx + 1];
                        float x2 = floatData[idx + 2];

                        colorConverter.toRGB(x0, x1, x2, rgb);

                        floatData[idx] = rgb[0];
                        floatData[idx + 1] = rgb[1];
                        floatData[idx + 2] = rgb[2];

                        idx += 3;
                    }

                    dstOffset += scanlineStride;
                }
            }

//             int[] p = new int[3];
//             ras.getPixel(0, 0, p);
//             System.out.println("p00 = " + 
//                                p[0] + " " + p[1] + " " + p[2]);
//             ras.getPixel(1, 0, p);
//             System.out.println("p10 = " + 
//                                p[0] + " " + p[1] + " " + p[2]);
//             ras.getPixel(2, 0, p);
//             System.out.println("p20 = " + 
//                                p[0] + " " + p[1] + " " + p[2]);
//             ras.getPixel(3, 0, p);
//             System.out.println("p30 = " + 
//                                p[0] + " " + p[1] + " " + p[2]);

//             ColorSpace rgb = ColorSpace.getInstance(ColorSpace.CS_sRGB);
//             ColorConvertOp op = new ColorConvertOp(colorSpace, rgb, null);
//             WritableRaster dest = op.createCompatibleDestRaster(ras);
//             op.filter(ras, dest);
//             ras = dest;
        }
        
        if (photometricInterpretation ==
            BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO) {
            if(byteData != null) {
                int bytesPerRow = (srcWidth*pixelBitStride + 7)/8;
                for (int y = 0; y < srcHeight; y++) {
                    int offset = dstOffset + y*scanlineStride;
                    for (int i = 0; i < bytesPerRow; i++) {
                        byteData[offset + i] ^= 0xff;
                    }
                }
            } else if(shortData != null) {
                int shortsPerRow = (srcWidth*pixelBitStride + 15)/16;
                if(sampleFormat[0] ==
                   BaselineTIFFTagSet.SAMPLE_FORMAT_SIGNED_INTEGER) {
                    for (int y = 0; y < srcHeight; y++) {
                        int offset = dstOffset + y*scanlineStride;
                        for (int i = 0; i < shortsPerRow; i++) {
                            int shortOffset = offset + i;
                            // XXX Does this make any sense?
                            shortData[shortOffset] =
                                (short)(Short.MAX_VALUE -
                                        shortData[shortOffset]);
                        }
                    }
                } else {
                    for (int y = 0; y < srcHeight; y++) {
                        int offset = dstOffset + y*scanlineStride;
                        for (int i = 0; i < shortsPerRow; i++) {
                            shortData[offset + i] ^= 0xffff;
                        }
                    }
                }
            } else if(intData != null) {
                int intsPerRow = (srcWidth*pixelBitStride + 15)/16;
                for (int y = 0; y < srcHeight; y++) {
                    int offset = dstOffset + y*scanlineStride;
                    for (int i = 0; i < intsPerRow; i++) {
                        int intOffset = offset + i;
                        // XXX Does this make any sense?
                        intData[intOffset] =
                            Integer.MAX_VALUE - intData[intOffset];
                    }
                }
            } else if(floatData != null) {
                int floatsPerRow = (srcWidth*pixelBitStride + 15)/16;
                for (int y = 0; y < srcHeight; y++) {
                    int offset = dstOffset + y*scanlineStride;
                    for (int i = 0; i < floatsPerRow; i++) {
                        int floatOffset = offset + i;
                        // XXX Does this make any sense?
                        floatData[floatOffset] =
                            1.0F - floatData[floatOffset];
                    }
                }
            }
        }

        if(isBilevel) {
            Rectangle rect = isImageSimple ?
                new Rectangle(dstMinX, dstMinY, dstWidth, dstHeight) :
                ras.getBounds();
            ImageUtil.setPackedBinaryData(byteData, ras, rect);
        }

        // XXX A better test might be if the rawImage raster either
        // equals the raster of 'image' or is a child thereof.
        if (isDirectCopy) { // rawImage == image) {
            return;
        }

        // Copy the raw image data into the true destination image
        Raster src = rawImage.getRaster();

        // Create band child of source
        Raster srcChild = src.createChild(0, 0,
                                          srcWidth, srcHeight,
                                          srcMinX, srcMinY,
                                          planar ? null : sourceBands);

        WritableRaster dst = image.getRaster();

        // Create dst child covering area and bands to be written
        WritableRaster dstChild = dst.createWritableChild(dstMinX, dstMinY,
                                                          dstWidth, dstHeight,
                                                          dstMinX, dstMinY,
                                                          destinationBands);

        if (subsampleX == 1 && subsampleY == 1 && !adjustBitDepths) {
            srcChild = srcChild.createChild(activeSrcMinX,
                                            activeSrcMinY,
                                            activeSrcWidth, activeSrcHeight,
                                            dstMinX, dstMinY,
                                            null);

            dstChild.setRect(srcChild);
        } else if (subsampleX == 1 && !adjustBitDepths) {
            int sy = activeSrcMinY;
            int dy = dstMinY;
            while (sy < srcMinY + srcHeight) {
                Raster srcRow = srcChild.createChild(activeSrcMinX, sy,
                                                     activeSrcWidth, 1,
                                                     dstMinX, dy,
                                                     null);
                dstChild.setRect(srcRow);
                
                sy += subsampleY;
                ++dy;
            }
        } else {
            int[] p = srcChild.getPixel(srcMinX, srcMinY, (int[])null);
            int numBands = p.length;

            int sy = activeSrcMinY;
            int dy = dstMinY;

            while (sy < activeSrcMinY + activeSrcHeight) {
                int sx = activeSrcMinX;
                int dx = dstMinX;

                while (sx < activeSrcMinX + activeSrcWidth) {
                    srcChild.getPixel(sx, sy, p);
                    if (adjustBitDepths) {
                        for (int band = 0; band < numBands; band++) {
                            p[band] = bitDepthScale[band][p[band]];
                        }
                    }
                    dstChild.setPixel(dx, dy, p);
                    
                    sx += subsampleX;
                    ++dx;
                }

                sy += subsampleY;
                ++dy;
            }
        }
    }
}
