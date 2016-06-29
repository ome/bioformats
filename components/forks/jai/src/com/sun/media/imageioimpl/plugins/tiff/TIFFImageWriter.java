/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2016 Open Microscopy Environment:
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
 * $RCSfile: TIFFImageWriter.java,v $
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
 * $Revision: 1.24 $
 * $Date: 2007/09/01 00:27:20 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.Node;
import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.EXIFParentTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.EXIFTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFColorConverter;
import com.sun.media.imageio.plugins.tiff.TIFFCompressor;
import com.sun.media.imageio.plugins.tiff.TIFFField;
import com.sun.media.imageio.plugins.tiff.TIFFImageWriteParam;
import com.sun.media.imageio.plugins.tiff.TIFFTag;
import com.sun.media.imageio.plugins.tiff.TIFFTagSet;
import com.sun.media.imageioimpl.common.ImageUtil;
import com.sun.media.imageioimpl.common.PackageUtil;
import com.sun.media.imageioimpl.common.SimpleRenderedImage;
import com.sun.media.imageioimpl.common.SingleTileRenderedImage;

public class TIFFImageWriter extends ImageWriter {

    private static final boolean DEBUG = false; // XXX false for release!

    static final String EXIF_JPEG_COMPRESSION_TYPE = "EXIF JPEG";

    public static final int DEFAULT_BYTES_PER_STRIP = 8192;

    /**
     * Supported TIFF compression types.
     */
    public static final String[] TIFFCompressionTypes = {
        "CCITT RLE",
        "CCITT T.4",
        "CCITT T.6",
        "LZW",
        // "Old JPEG",
        "JPEG",
        "ZLib",
        "PackBits",
        "Deflate",
        EXIF_JPEG_COMPRESSION_TYPE
    };

    //
    // !!! The lengths of the arrays 'compressionTypes',
    // !!! 'isCompressionLossless', and 'compressionNumbers'
    // !!! must be equal.
    //

    /**
     * Known TIFF compression types.
     */
    public static final String[] compressionTypes = {
        "CCITT RLE",
        "CCITT T.4",
        "CCITT T.6",
        "LZW",
        "Old JPEG",
        "JPEG",
        "ZLib",
        "PackBits",
        "Deflate",
        EXIF_JPEG_COMPRESSION_TYPE
    };

    /**
     * Lossless flag for known compression types.
     */
    public static final boolean[] isCompressionLossless = {
        true,  // RLE
        true,  // T.4
        true,  // T.6
        true,  // LZW
        false, // Old JPEG
        false, // JPEG
        true,  // ZLib
        true,  // PackBits
        true,  // DEFLATE
        false  // EXIF JPEG
    };

    /**
     * Compression tag values for known compression types.
     */
    public static final int[] compressionNumbers = {
        BaselineTIFFTagSet.COMPRESSION_CCITT_RLE,
        BaselineTIFFTagSet.COMPRESSION_CCITT_T_4,
        BaselineTIFFTagSet.COMPRESSION_CCITT_T_6,
        BaselineTIFFTagSet.COMPRESSION_LZW,
        BaselineTIFFTagSet.COMPRESSION_OLD_JPEG,
        BaselineTIFFTagSet.COMPRESSION_JPEG,
        BaselineTIFFTagSet.COMPRESSION_ZLIB,
        BaselineTIFFTagSet.COMPRESSION_PACKBITS,
        BaselineTIFFTagSet.COMPRESSION_DEFLATE,
        BaselineTIFFTagSet.COMPRESSION_OLD_JPEG, // EXIF JPEG
    };

    ImageOutputStream stream;
    long headerPosition;
    RenderedImage image;
    ImageTypeSpecifier imageType;
    ByteOrder byteOrder;
    ImageWriteParam param;
    TIFFCompressor compressor;
    TIFFColorConverter colorConverter;

    TIFFStreamMetadata streamMetadata;
    TIFFImageMetadata imageMetadata;

    int sourceXOffset;
    int sourceYOffset;
    int sourceWidth;
    int sourceHeight;
    int[] sourceBands;
    int periodX;
    int periodY;

    int bitDepth; // bits per channel
    int numBands;
    int tileWidth;
    int tileLength;
    int tilesAcross;
    int tilesDown;

    int[] sampleSize = null; // Input sample size per band, in bits
    int scalingBitDepth = -1; // Output bit depth of the scaling tables
    boolean isRescaling = false; // Whether rescaling is needed.

    boolean isBilevel; // Whether image is bilevel
    boolean isImageSimple; // Whether image can be copied into directly
    boolean isInverted; // Whether photometric inversion is required

    boolean isTiled; // Whether the image is tiled (true) or stipped (false).

    int nativePhotometricInterpretation;
    int photometricInterpretation;

    char[] bitsPerSample; // Output sample size per band
    int sampleFormat =
        BaselineTIFFTagSet.SAMPLE_FORMAT_UNDEFINED; // Output sample format

    // Tables for 1, 2, 4, or 8 bit output
    byte[][] scale = null; // 8 bit table
    byte[] scale0 = null; // equivalent to scale[0]

    // Tables for 16 bit output
    byte[][] scaleh = null; // High bytes of output
    byte[][] scalel = null; // Low bytes of output

    int compression;
    int predictor;

    int totalPixels;
    int pixelsDone;

    long nextIFDPointerPos;

    // Next available space.
    long nextSpace = 0L;

    // Whether a sequence is being written.
    boolean isWritingSequence = false;

    /**
     * Converts a pixel's X coordinate into a horizontal tile index
     * relative to a given tile grid layout specified by its X offset
     * and tile width.
     *
     * <p> If <code>tileWidth < 0</code>, the results of this method
     * are undefined.  If <code>tileWidth == 0</code>, an
     * <code>ArithmeticException</code> will be thrown.
     *
     * @throws ArithmeticException  If <code>tileWidth == 0</code>.
     */
    public static int XToTileX(int x, int tileGridXOffset, int tileWidth) {
        x -= tileGridXOffset;
        if (x < 0) {
            x += 1 - tileWidth;		// force round to -infinity (ceiling)
        }
        return x/tileWidth;
    }

    /**
     * Converts a pixel's Y coordinate into a vertical tile index
     * relative to a given tile grid layout specified by its Y offset
     * and tile height.
     *
     * <p> If <code>tileHeight < 0</code>, the results of this method
     * are undefined.  If <code>tileHeight == 0</code>, an
     * <code>ArithmeticException</code> will be thrown.
     *
     * @throws ArithmeticException  If <code>tileHeight == 0</code>.
     */
    public static int YToTileY(int y, int tileGridYOffset, int tileHeight) {
        y -= tileGridYOffset;
        if (y < 0) {
            y += 1 - tileHeight;	 // force round to -infinity (ceiling)
        }
        return y/tileHeight;
    }

    public TIFFImageWriter(ImageWriterSpi originatingProvider) {
        super(originatingProvider);
    }
    
    public ImageWriteParam getDefaultWriteParam() {
        return new TIFFImageWriteParam(getLocale());
    }

    public void setOutput(Object output) {
        super.setOutput(output);

        if (output != null) {
            if (!(output instanceof ImageOutputStream)) {
                throw new IllegalArgumentException
                    ("output not an ImageOutputStream!");
            }
            this.stream = (ImageOutputStream)output;

            //
            // The output is expected to be positioned at a TIFF header
            // or at some arbitrary location which may or may not be
            // the EOF. In the former case the writer should be able
            // either to overwrite the existing sequence or append to it.
            //

            // Set the position of the header and the next available space.
            try {
                headerPosition = this.stream.getStreamPosition();
                try {
                    // Read byte order and magic number.
                    byte[] b = new byte[4];
                    stream.readFully(b);

                    // Check bytes for TIFF header.
                    if((b[0] == (byte)0x49 && b[1] == (byte)0x49 &&
                        b[2] == (byte)0x2a && b[3] == (byte)0x00) ||
                       (b[0] == (byte)0x4d && b[1] == (byte)0x4d &&
                        b[2] == (byte)0x00 && b[3] == (byte)0x2a)) {
                        // TIFF header.
                        this.nextSpace = stream.length();
                    } else {
                        // Neither TIFF header nor EOF: overwrite.
                        this.nextSpace = headerPosition;
                    }
                } catch(IOException io) { // thrown by readFully()
                    // At EOF or not at a TIFF header.
                    this.nextSpace = headerPosition;
                }
                stream.seek(headerPosition);
            } catch(IOException ioe) { // thrown by getStreamPosition()
                // Assume it's at zero.
                this.nextSpace = headerPosition = 0L;
            }
        } else {
            this.stream = null;
        }
    }

    public IIOMetadata
        getDefaultStreamMetadata(ImageWriteParam param) {
        return new TIFFStreamMetadata();
    }

    public IIOMetadata
        getDefaultImageMetadata(ImageTypeSpecifier imageType,
                                ImageWriteParam param) {
        
        List tagSets = new ArrayList(1);
        tagSets.add(BaselineTIFFTagSet.getInstance());
        // XXX Should add Fax/EXIF/GeoTIFF TagSets?
        TIFFImageMetadata imageMetadata = new TIFFImageMetadata(tagSets);

        if(imageType != null) {
            TIFFImageMetadata im =
                (TIFFImageMetadata)convertImageMetadata(imageMetadata,
                                                        imageType,
                                                        param);
            if(im != null) {
                imageMetadata = im;
            }
        }

        return imageMetadata;
    }

    public IIOMetadata convertStreamMetadata(IIOMetadata inData,
                                             ImageWriteParam param) {
        // Check arguments.
        if(inData == null) {
            throw new IllegalArgumentException("inData == null!");
        }

        // Note: param is irrelevant as it does not contain byte order.

        TIFFStreamMetadata outData = null;
        if(inData instanceof TIFFStreamMetadata) {
            outData = new TIFFStreamMetadata();
            outData.byteOrder = ((TIFFStreamMetadata)inData).byteOrder;
            return outData;
        } else if(Arrays.asList(inData.getMetadataFormatNames()).contains(
                      TIFFStreamMetadata.nativeMetadataFormatName)) {
            outData = new TIFFStreamMetadata();
            String format = TIFFStreamMetadata.nativeMetadataFormatName;
            try {
                outData.mergeTree(format, inData.getAsTree(format));
            } catch(IIOInvalidTreeException e) {
                // XXX Warning
            }
        }

        return outData;
    }

    public IIOMetadata
        convertImageMetadata(IIOMetadata inData,
                             ImageTypeSpecifier imageType,
                             ImageWriteParam param) {
        // Check arguments.
        if(inData == null) {
            throw new IllegalArgumentException("inData == null!");
        }
        if(imageType == null) {
            throw new IllegalArgumentException("imageType == null!");
        }

        TIFFImageMetadata outData = null;

        // Obtain a TIFFImageMetadata object.
        if(inData instanceof TIFFImageMetadata) {
            // Create a new metadata object from a clone of the input IFD.
            TIFFIFD inIFD = ((TIFFImageMetadata)inData).getRootIFD();
            outData = new TIFFImageMetadata(inIFD.getShallowClone());
        } else if(Arrays.asList(inData.getMetadataFormatNames()).contains(
                      TIFFImageMetadata.nativeMetadataFormatName)) {
            // Initialize from the native metadata form of the input tree.
            try {
                outData = convertNativeImageMetadata(inData);
            } catch(IIOInvalidTreeException e) {
                // XXX Warning
            }
        } else if(inData.isStandardMetadataFormatSupported()) {
            // Initialize from the standard metadata form of the input tree.
            try {
                outData = convertStandardImageMetadata(inData);
            } catch(IIOInvalidTreeException e) {
                // XXX Warning
            }
        }

        // Update the metadata per the image type and param.
        if(outData != null) {
            TIFFImageWriter bogusWriter =
                new TIFFImageWriter(this.originatingProvider);
            bogusWriter.imageMetadata = outData;
            bogusWriter.param = param;
            SampleModel sm = imageType.getSampleModel();
            try {
                bogusWriter.setupMetadata(imageType.getColorModel(), sm,
                                          sm.getWidth(), sm.getHeight());
                return bogusWriter.imageMetadata;
            } catch(IIOException e) {
                // XXX Warning
                return null;
            }
        }

        return outData;
    }

    /**
     * Converts a standard <code>javax_imageio_1.0</code> tree to a
     * <code>TIFFImageMetadata</code> object.
     *
     * @param inData The metadata object.
     * @return a <code>TIFFImageMetadata</code> or <code>null</code> if
     * the standard tree derived from the input object is <code>null</code>.
     * @throws IllegalArgumentException if <code>inData</code> is
     * <code>null</code> or does not support the standard metadata format.
     * @throws IIOInvalidTreeException if <code>inData</code> generates an
     * invalid standard metadata tree.
     */
    private TIFFImageMetadata convertStandardImageMetadata(IIOMetadata inData)
        throws IIOInvalidTreeException {

        if(inData == null) {
            throw new IllegalArgumentException("inData == null!");
        } else if(!inData.isStandardMetadataFormatSupported()) {
            throw new IllegalArgumentException
                ("inData does not support standard metadata format!");
        }

        TIFFImageMetadata outData = null;

        String formatName = IIOMetadataFormatImpl.standardMetadataFormatName;
        Node tree = inData.getAsTree(formatName);
        if (tree != null) {
            List tagSets = new ArrayList(1);
            tagSets.add(BaselineTIFFTagSet.getInstance());
            outData = new TIFFImageMetadata(tagSets);
            outData.setFromTree(formatName, tree);
        }

        return outData;
    }

    /**
     * Converts a native
     * <code>com_sun_media_imageio_plugins_tiff_image_1.0</code> tree to a
     * <code>TIFFImageMetadata</code> object.
     *
     * @param inData The metadata object.
     * @return a <code>TIFFImageMetadata</code> or <code>null</code> if
     * the native tree derived from the input object is <code>null</code>.
     * @throws IllegalArgumentException if <code>inData</code> is
     * <code>null</code> or does not support the native metadata format.
     * @throws IIOInvalidTreeException if <code>inData</code> generates an
     * invalid native metadata tree.
     */
    private TIFFImageMetadata convertNativeImageMetadata(IIOMetadata inData)
        throws IIOInvalidTreeException {

        if(inData == null) {
            throw new IllegalArgumentException("inData == null!");
        } else if(!Arrays.asList(inData.getMetadataFormatNames()).contains(
                      TIFFImageMetadata.nativeMetadataFormatName)) {
            throw new IllegalArgumentException
                ("inData does not support native metadata format!");
        }

        TIFFImageMetadata outData = null;

        String formatName = TIFFImageMetadata.nativeMetadataFormatName;
        Node tree = inData.getAsTree(formatName);
        if (tree != null) {
            List tagSets = new ArrayList(1);
            tagSets.add(BaselineTIFFTagSet.getInstance());
            outData = new TIFFImageMetadata(tagSets);
            outData.setFromTree(formatName, tree);
        }

        return outData;
    }

    /**
     * Sets up the output metadata adding, removing, and overriding fields
     * as needed. The destination image dimensions are provided as parameters
     * because these might differ from those of the source due to subsampling.
     * 
     * @param cm The <code>ColorModel</code> of the image being written.
     * @param sm The <code>SampleModel</code> of the image being written.
     * @param destWidth The width of the written image after subsampling.
     * @param destHeight The height of the written image after subsampling.
     */
    void setupMetadata(ColorModel cm, SampleModel sm,
                       int destWidth, int destHeight)
        throws IIOException {
        // Get initial IFD from metadata

        // Always emit these fields:
        //
        // Override values from metadata:
        //
        //  planarConfiguration -> chunky (planar not supported on output)
        //
        // Override values from metadata with image-derived values:
        // 
        //  bitsPerSample (if not bilivel)
        //  colorMap (if palette color)
        //  photometricInterpretation (derive from image)
        //  imageLength
        //  imageWidth
        // 
        //  rowsPerStrip     \      /   tileLength
        //  stripOffsets      | OR |   tileOffsets
        //  stripByteCounts  /     |   tileByteCounts
        //                          \   tileWidth
        //                    
        //
        // Override values from metadata with write param values:
        //
        //  compression

        // Use values from metadata if present for these fields,
        // otherwise use defaults:
        //
        //  resolutionUnit
        //  XResolution (take from metadata if present)
        //  YResolution
        //  rowsPerStrip
        //  sampleFormat

        TIFFIFD rootIFD = imageMetadata.getRootIFD();

        BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();

        // If PlanarConfiguration field present, set value to chunky.

        TIFFField f =
            rootIFD.getTIFFField(BaselineTIFFTagSet.TAG_PLANAR_CONFIGURATION);
        if(f != null &&
           f.getAsInt(0) != BaselineTIFFTagSet.PLANAR_CONFIGURATION_CHUNKY) {
            // XXX processWarningOccurred()
            TIFFField planarConfigurationField =
                new TIFFField(base.getTag(BaselineTIFFTagSet.TAG_PLANAR_CONFIGURATION),
                              BaselineTIFFTagSet.PLANAR_CONFIGURATION_CHUNKY);
            rootIFD.addTIFFField(planarConfigurationField);
        }

        char[] extraSamples = null;

        this.photometricInterpretation = -1;
        boolean forcePhotometricInterpretation = false;

        f =
       rootIFD.getTIFFField(BaselineTIFFTagSet.TAG_PHOTOMETRIC_INTERPRETATION);
        if (f != null) {
            photometricInterpretation = f.getAsInt(0);
            if(photometricInterpretation ==
               BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_PALETTE_COLOR &&
               !(cm instanceof IndexColorModel)) {
                photometricInterpretation = -1;
            } else {
                forcePhotometricInterpretation = true;
            }
        }

//         f = rootIFD.getTIFFField(BaselineTIFFTagSet.TAG_EXTRA_SAMPLES);
//         if (f != null) {
//             extraSamples = f.getAsChars();
//         }

//         f = rootIFD.getTIFFField(BaselineTIFFTagSet.TAG_BITS_PER_SAMPLE);
//         if (f != null) {
//             bitsPerSample = f.getAsChars();
//         }

        int[] sampleSize = sm.getSampleSize();

        int numBands = sm.getNumBands();
        int numExtraSamples = 0;

        // Check that numBands > 1 here because TIFF requires that
        // SamplesPerPixel = numBands + numExtraSamples and numBands
        // cannot be zero.
        if (numBands > 1 && cm != null && cm.hasAlpha()) {
            --numBands;
            numExtraSamples = 1;
            extraSamples = new char[1];
            if (cm.isAlphaPremultiplied()) {
                extraSamples[0] =
                    BaselineTIFFTagSet.EXTRA_SAMPLES_ASSOCIATED_ALPHA;
            } else {
                extraSamples[0] =
                    BaselineTIFFTagSet.EXTRA_SAMPLES_UNASSOCIATED_ALPHA;
            }
        }

        if (numBands == 3) {
            this.nativePhotometricInterpretation =
                BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_RGB;
            if (photometricInterpretation == -1) {
                photometricInterpretation =
                    BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_RGB;
            }
        } else if (sm.getNumBands() == 1 && cm instanceof IndexColorModel) {
            IndexColorModel icm = (IndexColorModel)cm;
            int r0 = icm.getRed(0);
            int r1 = icm.getRed(1);
            if (icm.getMapSize() == 2 &&
                (r0 == icm.getGreen(0)) && (r0 == icm.getBlue(0)) &&
                (r1 == icm.getGreen(1)) && (r1 == icm.getBlue(1)) &&
                (r0 == 0 || r0 == 255) &&
                (r1 == 0 || r1 == 255) &&
                (r0 != r1)) {
                // Black/white image

                if (r0 == 0) {
                    nativePhotometricInterpretation =
                   BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO;
                } else {
                    nativePhotometricInterpretation =
                   BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO;
                }


                // If photometricInterpretation is already set to
                // WhiteIsZero or BlackIsZero, leave it alone
                if (photometricInterpretation !=
                 BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO &&
                    photometricInterpretation !=
                 BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO) {
                    photometricInterpretation =
                        r0 == 0 ?
                  BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO :
                  BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO;
                }
            } else {
                nativePhotometricInterpretation =
                photometricInterpretation =
                   BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_PALETTE_COLOR;
            }
        } else {
            if(cm != null) {
                switch(cm.getColorSpace().getType()) {
                case ColorSpace.TYPE_Lab:
                    nativePhotometricInterpretation =
                        BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_CIELAB;
                    break;
                case ColorSpace.TYPE_YCbCr:
                    nativePhotometricInterpretation =
                        BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_Y_CB_CR;
                    break;
                case ColorSpace.TYPE_CMYK:
                    nativePhotometricInterpretation =
                        BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_CMYK;
                    break;
                default:
                    nativePhotometricInterpretation =
                        BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO;
                }
            } else {
                nativePhotometricInterpretation =
                    BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO;
            }
            if (photometricInterpretation == -1) {
                photometricInterpretation = nativePhotometricInterpretation;
            }
        }

        // Set the compressor and color converter.

        this.compressor = null;
        this.colorConverter = null;
        if (param instanceof TIFFImageWriteParam) {
            TIFFImageWriteParam tparam = (TIFFImageWriteParam)param;
            if(tparam.getCompressionMode() == tparam.MODE_EXPLICIT) {
                compressor = tparam.getTIFFCompressor();
                String compressionType = param.getCompressionType();
                if(compressor != null &&
                   !compressor.getCompressionType().equals(compressionType)) {
                    // Unset the TIFFCompressor if its compression type is
                    // not the one selected.
                    compressor = null;
                }
            } else {
                // Compression mode not MODE_EXPLICIT.
                compressor = null;
            }
            colorConverter = tparam.getColorConverter();
            if (colorConverter != null) {
                photometricInterpretation =
                    tparam.getPhotometricInterpretation();
            }
        }

        // Emit compression tag

        int compressionMode = param instanceof TIFFImageWriteParam ?
            param.getCompressionMode() : ImageWriteParam.MODE_DEFAULT;
        switch(compressionMode) {
        case ImageWriteParam.MODE_EXPLICIT:
            {
                String compressionType = param.getCompressionType();
                if (compressionType == null) {
                    this.compression = BaselineTIFFTagSet.COMPRESSION_NONE;
                } else {
                    // Determine corresponding compression tag value.
                    int len = compressionTypes.length;
                    for (int i = 0; i < len; i++) {
                        if (compressionType.equals(compressionTypes[i])) {
                            this.compression = compressionNumbers[i];
                        }
                    }
                }

                // Ensure the compressor, if any, matches compression setting
                // with the precedence described in TIFFImageWriteParam.
                if(compressor != null &&
                   compressor.getCompressionTagValue() != this.compression) {
                    // Does not match: unset the compressor.
                    compressor = null;
                }
            }
            break;
        case ImageWriteParam.MODE_COPY_FROM_METADATA:
            {
                TIFFField compField =
                    rootIFD.getTIFFField(BaselineTIFFTagSet.TAG_COMPRESSION);
                if(compField != null) {
                    this.compression = compField.getAsInt(0);
                    break;
                }
            }
        case ImageWriteParam.MODE_DEFAULT:
        case ImageWriteParam.MODE_DISABLED:
        default:
            this.compression = BaselineTIFFTagSet.COMPRESSION_NONE;
        }

	TIFFField predictorField =
            rootIFD.getTIFFField(BaselineTIFFTagSet.TAG_PREDICTOR);
	if (predictorField != null) {
	    this.predictor = predictorField.getAsInt(0);
	    
	    // We only support Horizontal Predictor for a bitDepth of 8
	    if (sampleSize[0] != 8 || 
		// Check the value of the tag for validity
		(predictor != BaselineTIFFTagSet.PREDICTOR_NONE && 
		 predictor != 
		 BaselineTIFFTagSet.PREDICTOR_HORIZONTAL_DIFFERENCING)) {
		// XXX processWarningOccured ???
		// Set to default
		predictor = BaselineTIFFTagSet.PREDICTOR_NONE;		

		// Emit this changed predictor value to metadata
		TIFFField newPredictorField =
		   new TIFFField(base.getTag(BaselineTIFFTagSet.TAG_PREDICTOR),
				 predictor);
		rootIFD.addTIFFField(newPredictorField);
	    }

	    // XXX Do we need to ensure that predictor is not passed on if
	    // the compression is not either Deflate or LZW?
	}

        TIFFField compressionField =
            new TIFFField(base.getTag(BaselineTIFFTagSet.TAG_COMPRESSION),
                          compression);
        rootIFD.addTIFFField(compressionField);

        // Set EXIF flag. Note that there is no way to determine definitively
        // when an uncompressed thumbnail is being written as the EXIF IFD
        // pointer field is optional for thumbnails.
        boolean isEXIF = false;
        if(numBands == 3 &&
           sampleSize[0] == 8 && sampleSize[1] == 8 && sampleSize[2] == 8) {
            // Three bands with 8 bits per sample.
            if(rootIFD.getTIFFField(EXIFParentTIFFTagSet.TAG_EXIF_IFD_POINTER)
               != null) {
                // EXIF IFD pointer present.
                if(compression == BaselineTIFFTagSet.COMPRESSION_NONE &&
                   (photometricInterpretation ==
                    BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_RGB ||
                    photometricInterpretation ==
                    BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_Y_CB_CR)) {
                    // Uncompressed RGB or YCbCr.
                    isEXIF = true;
                } else if(compression ==
                          BaselineTIFFTagSet.COMPRESSION_OLD_JPEG) {
                    // Compressed.
                    isEXIF = true;
                }
            } else if(compressionMode == ImageWriteParam.MODE_EXPLICIT &&
                      EXIF_JPEG_COMPRESSION_TYPE.equals
                      (param.getCompressionType())) {
                // EXIF IFD pointer absent but EXIF JPEG compression set.
                isEXIF = true;
            }
        }

        // Initialize JPEG interchange format flag which is used to
        // indicate that the image is stored as a single JPEG stream.
        // This flag is separated from the 'isEXIF' flag in case JPEG
        // interchange format is eventually supported for non-EXIF images.
        boolean isJPEGInterchange =
            isEXIF && compression == BaselineTIFFTagSet.COMPRESSION_OLD_JPEG;

        if (compressor == null) {
            if (compression == BaselineTIFFTagSet.COMPRESSION_CCITT_RLE) {
                if(compressor == null) {
                    compressor = new TIFFRLECompressor();
                    if(DEBUG) {
                        System.out.println("Using Java RLE compressor");
                    }
                }

                if (!forcePhotometricInterpretation) {
                    photometricInterpretation =
                   BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO;
                }
            } else if (compression ==
                       BaselineTIFFTagSet.COMPRESSION_CCITT_T_4) {
                if(compressor == null) {
                    compressor = new TIFFT4Compressor();
                    if(DEBUG) {
                        System.out.println("Using Java T.4 compressor");
                    }
                }

                if (!forcePhotometricInterpretation) {
                    photometricInterpretation =
                   BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO;
                }
            } else if (compression ==
                       BaselineTIFFTagSet.COMPRESSION_CCITT_T_6) {
                if(compressor == null) {
                    compressor = new TIFFT6Compressor();
                    if(DEBUG) {
                        System.out.println("Using Java T.6 compressor");
                    }
                }

                if (!forcePhotometricInterpretation) {
                    photometricInterpretation =
                   BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO;
                }
            } else if (compression ==
                       BaselineTIFFTagSet.COMPRESSION_LZW) {
                compressor = new TIFFLZWCompressor(predictor);
            } else if (compression ==
                       BaselineTIFFTagSet.COMPRESSION_OLD_JPEG) {
                if(isEXIF) {
                    compressor = new TIFFEXIFJPEGCompressor(param);
                } else {
                    throw new IIOException
                        ("Old JPEG compression not supported!");
                }
            } else if (compression ==
                       BaselineTIFFTagSet.COMPRESSION_JPEG) {
                if(numBands == 3 && sampleSize[0] == 8 &&
                   sampleSize[1] == 8 && sampleSize[2] == 8) {
                    photometricInterpretation =
                        BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_Y_CB_CR;
                } else if(numBands == 1 && sampleSize[0] == 8) {
                    photometricInterpretation =
                        BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO;
                } else {
                    throw new IIOException
                        ("JPEG compression supported for 1- and 3-band byte images only!");
                }
                compressor = new TIFFJPEGCompressor(param);
            } else if (compression ==
                       BaselineTIFFTagSet.COMPRESSION_ZLIB) {
                compressor = new TIFFZLibCompressor(param, predictor);
            } else if (compression ==
                       BaselineTIFFTagSet.COMPRESSION_PACKBITS) {
                compressor = new TIFFPackBitsCompressor();
            } else if (compression ==
                       BaselineTIFFTagSet.COMPRESSION_DEFLATE) {
                compressor = new TIFFDeflateCompressor(param, predictor);
            } else {
                // Determine inverse fill setting.
                f = rootIFD.getTIFFField(BaselineTIFFTagSet.TAG_FILL_ORDER);
                boolean inverseFill = (f != null && f.getAsInt(0) == 2);

                if(inverseFill) {
                    compressor = new TIFFLSBCompressor();
                } else {
                    compressor = new TIFFNullCompressor();
                }
            } // compression == ?
        } // compressor == null

        if(DEBUG) {
            if(param != null &&
               param.getCompressionMode() == param.MODE_EXPLICIT) {
                System.out.println("compressionType = "+
                                   param.getCompressionType());
            }
            if(compressor != null) {
                System.out.println("compressor = "+
                                   compressor.getClass().getName());
            }
        }

        if (colorConverter == null) {
            if(cm != null &&
               cm.getColorSpace().getType() == ColorSpace.TYPE_RGB) {
                //
                // Perform color conversion only if image has RGB color space.
                //
                if (photometricInterpretation ==
                    BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_Y_CB_CR &&
                    compression !=
                    BaselineTIFFTagSet.COMPRESSION_JPEG) {
                    //
                    // Convert RGB to YCbCr only if compression type is not
                    // JPEG in which case this is handled implicitly by the
                    // compressor.
                    //
                    colorConverter =
                        new TIFFYCbCrColorConverter(imageMetadata);
                } else if (photometricInterpretation ==
                           BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_CIELAB) {
                    colorConverter = new TIFFCIELabColorConverter();
                }
            }
        }

        //
        // Cannot at this time do YCbCr subsampling so set the
        // YCbCrSubsampling field value to [1, 1] and the YCbCrPositioning
        // field value to "cosited".
        //
        if(photometricInterpretation ==
           BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_Y_CB_CR &&
           compression !=
           BaselineTIFFTagSet.COMPRESSION_JPEG) {
            // Remove old subsampling and positioning fields.
            rootIFD.removeTIFFField
                (BaselineTIFFTagSet.TAG_Y_CB_CR_SUBSAMPLING);
            rootIFD.removeTIFFField
                (BaselineTIFFTagSet.TAG_Y_CB_CR_POSITIONING);

            // Add unity chrominance subsampling factors.
            rootIFD.addTIFFField
                (new TIFFField
                    (base.getTag(BaselineTIFFTagSet.TAG_Y_CB_CR_SUBSAMPLING),
                     TIFFTag.TIFF_SHORT,
                     2,
                     new char[] {(char)1, (char)1}));

            // Add cosited positioning.
            rootIFD.addTIFFField
                (new TIFFField
                    (base.getTag(BaselineTIFFTagSet.TAG_Y_CB_CR_POSITIONING),
                     TIFFTag.TIFF_SHORT,
                     1,
                     new char[] {
                         (char)BaselineTIFFTagSet.Y_CB_CR_POSITIONING_COSITED
                     }));
        }

        TIFFField photometricInterpretationField =
            new TIFFField(
                base.getTag(BaselineTIFFTagSet.TAG_PHOTOMETRIC_INTERPRETATION),
                          photometricInterpretation);
        rootIFD.addTIFFField(photometricInterpretationField);

        this.bitsPerSample = new char[numBands + numExtraSamples];
        this.bitDepth = 0;
        for (int i = 0; i < numBands; i++) {
            this.bitDepth = Math.max(bitDepth, sampleSize[i]);
        }
        if (bitDepth == 3) {
            bitDepth = 4;
        } else if (bitDepth > 4 && bitDepth < 8) {
            bitDepth = 8;
        } else if (bitDepth > 8 && bitDepth < 16) {
            bitDepth = 16;
        } else if (bitDepth > 16) {
            bitDepth = 32;
        }

        for (int i = 0; i < bitsPerSample.length; i++) {
            bitsPerSample[i] = (char)bitDepth;
        }

        // Emit BitsPerSample. If the image is bilevel, emit if and only
        // if already in the metadata and correct (count and value == 1).
        if (bitsPerSample.length != 1 || bitsPerSample[0] != 1) {
            TIFFField bitsPerSampleField =
                new TIFFField(
                           base.getTag(BaselineTIFFTagSet.TAG_BITS_PER_SAMPLE),
                           TIFFTag.TIFF_SHORT,
                           bitsPerSample.length,
                           bitsPerSample);
            rootIFD.addTIFFField(bitsPerSampleField);
        } else { // bitsPerSample.length == 1 && bitsPerSample[0] == 1
            TIFFField bitsPerSampleField =
                rootIFD.getTIFFField(BaselineTIFFTagSet.TAG_BITS_PER_SAMPLE);
            if(bitsPerSampleField != null) {
                int[] bps = bitsPerSampleField.getAsInts();
                if(bps == null || bps.length != 1 || bps[0] != 1) {
                    rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_BITS_PER_SAMPLE);
                }
            }
        }

        // Prepare SampleFormat field.
        f = rootIFD.getTIFFField(BaselineTIFFTagSet.TAG_SAMPLE_FORMAT);
        if(f == null && (bitDepth == 16 || bitDepth == 32)) {
            // Set up default content for 16- and 32-bit cases.
            char sampleFormatValue;
            int dataType = sm.getDataType();
            if(bitDepth == 16 && dataType == DataBuffer.TYPE_USHORT) {
               sampleFormatValue =
                   (char)BaselineTIFFTagSet.SAMPLE_FORMAT_UNSIGNED_INTEGER;
            } else if(bitDepth == 32 && dataType == DataBuffer.TYPE_FLOAT) {
                sampleFormatValue =
                    (char)BaselineTIFFTagSet.SAMPLE_FORMAT_FLOATING_POINT;
            } else {
                sampleFormatValue =
                    BaselineTIFFTagSet.SAMPLE_FORMAT_SIGNED_INTEGER;
            }
            this.sampleFormat = (int)sampleFormatValue;
            char[] sampleFormatArray = new char[bitsPerSample.length];
            Arrays.fill(sampleFormatArray, sampleFormatValue);

            // Update the metadata.
            TIFFTag sampleFormatTag =
                base.getTag(BaselineTIFFTagSet.TAG_SAMPLE_FORMAT);

            TIFFField sampleFormatField =
                new TIFFField(sampleFormatTag, TIFFTag.TIFF_SHORT,
                              sampleFormatArray.length, sampleFormatArray);

            rootIFD.addTIFFField(sampleFormatField);
        } else if(f != null) {
            // Get whatever was provided.
            sampleFormat = f.getAsInt(0);
        } else {
            // Set default value for internal use only.
            sampleFormat = BaselineTIFFTagSet.SAMPLE_FORMAT_UNDEFINED;
        }

        if (extraSamples != null) {
            TIFFField extraSamplesField =
                new TIFFField(
                           base.getTag(BaselineTIFFTagSet.TAG_EXTRA_SAMPLES),
                           TIFFTag.TIFF_SHORT,
                           extraSamples.length,
                           extraSamples);
            rootIFD.addTIFFField(extraSamplesField);
        } else {
            rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_EXTRA_SAMPLES);
        }

        TIFFField samplesPerPixelField =
            new TIFFField(
                         base.getTag(BaselineTIFFTagSet.TAG_SAMPLES_PER_PIXEL),
                         bitsPerSample.length);
        rootIFD.addTIFFField(samplesPerPixelField);

        // Emit ColorMap if image is of palette color type
        if (photometricInterpretation ==
            BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_PALETTE_COLOR &&
            cm instanceof IndexColorModel) {
            char[] colorMap = new char[3*(1 << bitsPerSample[0])];

            IndexColorModel icm = (IndexColorModel)cm;

            // mapSize is determined by BitsPerSample, not by incoming ICM.
            int mapSize = 1 << bitsPerSample[0];
            int indexBound = Math.min(mapSize, icm.getMapSize());
            for (int i = 0; i < indexBound; i++) {
                colorMap[i] = (char)((icm.getRed(i)*65535)/255);
                colorMap[mapSize + i] = (char)((icm.getGreen(i)*65535)/255);
                colorMap[2*mapSize + i] = (char)((icm.getBlue(i)*65535)/255);
            }

            TIFFField colorMapField =
                new TIFFField(
                           base.getTag(BaselineTIFFTagSet.TAG_COLOR_MAP),
                           TIFFTag.TIFF_SHORT,
                           colorMap.length,
                           colorMap);
            rootIFD.addTIFFField(colorMapField);
        } else {
            rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_COLOR_MAP);
        }

        // Emit ICCProfile if there is no ICCProfile field already in the
        // metadata and the ColorSpace is non-standard ICC.
        if(cm != null &&
           rootIFD.getTIFFField(BaselineTIFFTagSet.TAG_ICC_PROFILE) == null &&
           ImageUtil.isNonStandardICCColorSpace(cm.getColorSpace())) {
            ICC_ColorSpace iccColorSpace = (ICC_ColorSpace)cm.getColorSpace();
            byte[] iccProfileData = iccColorSpace.getProfile().getData();
            TIFFField iccProfileField =
                new TIFFField(base.getTag(BaselineTIFFTagSet.TAG_ICC_PROFILE),
                              TIFFTag.TIFF_UNDEFINED,
                              iccProfileData.length,
                              iccProfileData);
            rootIFD.addTIFFField(iccProfileField);
        }

        // Always emit XResolution and YResolution.

        TIFFField XResolutionField =
            rootIFD.getTIFFField(BaselineTIFFTagSet.TAG_X_RESOLUTION);
        TIFFField YResolutionField =
            rootIFD.getTIFFField(BaselineTIFFTagSet.TAG_Y_RESOLUTION);

        if(XResolutionField == null && YResolutionField == null) {
            long[][] resRational = new long[1][2];
            resRational[0] = new long[2];

            TIFFField ResolutionUnitField =
                rootIFD.getTIFFField(BaselineTIFFTagSet.TAG_RESOLUTION_UNIT);

            // Don't force dimensionless if one of the other dimensional
            // quantities is present.
            if(ResolutionUnitField == null &&
               rootIFD.getTIFFField(BaselineTIFFTagSet.TAG_X_POSITION) == null &&
               rootIFD.getTIFFField(BaselineTIFFTagSet.TAG_Y_POSITION) == null) {
                // Set resolution to unit and units to dimensionless.
                resRational[0][0] = 1;
                resRational[0][1] = 1;

                ResolutionUnitField =
                    new TIFFField(rootIFD.getTag
                                  (BaselineTIFFTagSet.TAG_RESOLUTION_UNIT),
                                  BaselineTIFFTagSet.RESOLUTION_UNIT_NONE);
                rootIFD.addTIFFField(ResolutionUnitField);
            } else {
                // Set resolution to a value which would make the maximum
                // image dimension equal to 4 inches as arbitrarily stated
                // in the description of ResolutionUnit in the TIFF 6.0
                // specification. If the ResolutionUnit field specifies
                // "none" then set the resolution to unity (1/1).
                int resolutionUnit = ResolutionUnitField != null ?
                    ResolutionUnitField.getAsInt(0) :
                    BaselineTIFFTagSet.RESOLUTION_UNIT_INCH;
                int maxDimension = Math.max(destWidth, destHeight);
                switch(resolutionUnit) {
                case BaselineTIFFTagSet.RESOLUTION_UNIT_INCH:
                    resRational[0][0] = maxDimension;
                    resRational[0][1] = 4;
                    break;
                case BaselineTIFFTagSet.RESOLUTION_UNIT_CENTIMETER:
                    resRational[0][0] = 100L*maxDimension; // divide out 100
                    resRational[0][1] = 4*254; // 2.54 cm/inch * 100
                    break;
                default:
                    resRational[0][0] = 1;
                    resRational[0][1] = 1;
                }
            }

            XResolutionField =
                new TIFFField(rootIFD.getTag(BaselineTIFFTagSet.TAG_X_RESOLUTION),
                              TIFFTag.TIFF_RATIONAL,
                              1,
                              resRational);
            rootIFD.addTIFFField(XResolutionField);

            YResolutionField =
                new TIFFField(rootIFD.getTag(BaselineTIFFTagSet.TAG_Y_RESOLUTION),
                              TIFFTag.TIFF_RATIONAL,
                              1,
                              resRational);
            rootIFD.addTIFFField(YResolutionField);
        } else if(XResolutionField == null && YResolutionField != null) {
            // Set XResolution to YResolution.
            long[] yResolution =
                (long[])YResolutionField.getAsRational(0).clone();
            XResolutionField =
             new TIFFField(rootIFD.getTag(BaselineTIFFTagSet.TAG_X_RESOLUTION),
                              TIFFTag.TIFF_RATIONAL,
                              1,
                              yResolution);
            rootIFD.addTIFFField(XResolutionField);
        } else if(XResolutionField != null && YResolutionField == null) {
            // Set YResolution to XResolution.
            long[] xResolution =
                (long[])XResolutionField.getAsRational(0).clone();
            YResolutionField =
             new TIFFField(rootIFD.getTag(BaselineTIFFTagSet.TAG_Y_RESOLUTION),
                              TIFFTag.TIFF_RATIONAL,
                              1,
                              xResolution);
            rootIFD.addTIFFField(YResolutionField);
        }

        // Set mandatory fields, overriding metadata passed in

        int width = destWidth;
        TIFFField imageWidthField =
            new TIFFField(base.getTag(BaselineTIFFTagSet.TAG_IMAGE_WIDTH),
                          width);
        rootIFD.addTIFFField(imageWidthField);

        int height = destHeight;
        TIFFField imageLengthField =
            new TIFFField(base.getTag(BaselineTIFFTagSet.TAG_IMAGE_LENGTH),
                          height);
        rootIFD.addTIFFField(imageLengthField);

        // Determine rowsPerStrip

        int rowsPerStrip;

        TIFFField rowsPerStripField =
            rootIFD.getTIFFField(BaselineTIFFTagSet.TAG_ROWS_PER_STRIP);
        if (rowsPerStripField != null) {
            rowsPerStrip = rowsPerStripField.getAsInt(0);
            if(rowsPerStrip < 0) {
                rowsPerStrip = height;
            }
        } else {
            int bitsPerPixel = bitDepth*(numBands + numExtraSamples);
            int bytesPerRow = (bitsPerPixel*width + 7)/8;
            rowsPerStrip =
                Math.max(Math.max(DEFAULT_BYTES_PER_STRIP/bytesPerRow, 1), 8);
        }
        rowsPerStrip = Math.min(rowsPerStrip, height);

        // Tiling flag.
        boolean useTiling = false;

        // Analyze tiling parameters
        int tilingMode = param instanceof TIFFImageWriteParam ?
            param.getTilingMode() : ImageWriteParam.MODE_DEFAULT;
        if (tilingMode == ImageWriteParam.MODE_DISABLED ||
            tilingMode == ImageWriteParam.MODE_DEFAULT) {
            this.tileWidth = width;
            this.tileLength = rowsPerStrip;
            useTiling = false;
        } else if (tilingMode == ImageWriteParam.MODE_EXPLICIT) {
            tileWidth = param.getTileWidth();
            tileLength = param.getTileHeight();
            useTiling = true;
        } else if (tilingMode == ImageWriteParam.MODE_COPY_FROM_METADATA) {
            f = rootIFD.getTIFFField(BaselineTIFFTagSet.TAG_TILE_WIDTH);
            if (f == null) {
                tileWidth = width;
                useTiling = false;
            } else {
                tileWidth = f.getAsInt(0);
                useTiling = true;
            }

            f = rootIFD.getTIFFField(BaselineTIFFTagSet.TAG_TILE_LENGTH);
            if (f == null) {
                tileLength = rowsPerStrip;
            } else {
                tileLength = f.getAsInt(0);
                useTiling = true;
            }
        } else {
            throw new IIOException("Illegal value of tilingMode!");
        }

        if(compression == BaselineTIFFTagSet.COMPRESSION_JPEG) {
            // Reset tile size per TTN2 spec for JPEG compression.
            int subX;
            int subY;
            if(numBands == 1) {
                subX = subY = 1;
            } else {
                subX = subY = TIFFJPEGCompressor.CHROMA_SUBSAMPLING;
            }
            if(useTiling) {
                int MCUMultipleX = 8*subX;
                int MCUMultipleY = 8*subY;
                tileWidth =
                    Math.max(MCUMultipleX*((tileWidth +
                                            MCUMultipleX/2)/MCUMultipleX),
                             MCUMultipleX);
                tileLength =
                    Math.max(MCUMultipleY*((tileLength +
                                            MCUMultipleY/2)/MCUMultipleY),
                             MCUMultipleY);
            } else if(rowsPerStrip < height) {
                int MCUMultiple = 8*Math.max(subX, subY);
                rowsPerStrip = tileLength =
                    Math.max(MCUMultiple*((tileLength +
                                           MCUMultiple/2)/MCUMultiple),
                             MCUMultiple);
            }
        } else if(isJPEGInterchange) {
            // Force tile size to equal image size.
            tileWidth = width;
            tileLength = height;
        } else if(useTiling) {
            // Round tile size to multiple of 16 per TIFF 6.0 specification
            // (see pages 67-68 of version 6.0.1 from Adobe).
            int tileWidthRemainder = tileWidth % 16;
            if(tileWidthRemainder != 0) {
                // Round to nearest multiple of 16 not less than 16.
                tileWidth = Math.max(16*((tileWidth + 8)/16), 16);
                // XXX insert processWarningOccurred(int,String);
            }

            int tileLengthRemainder = tileLength % 16;
            if(tileLengthRemainder != 0) {
                // Round to nearest multiple of 16 not less than 16.
                tileLength = Math.max(16*((tileLength + 8)/16), 16);
                // XXX insert processWarningOccurred(int,String);
            }
        }

        this.tilesAcross = (width + tileWidth - 1)/tileWidth;
        this.tilesDown = (height + tileLength - 1)/tileLength;
 
        if (!useTiling) {
            this.isTiled = false;

            rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_TILE_WIDTH);
            rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_TILE_LENGTH);
            rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_TILE_OFFSETS);
            rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_TILE_BYTE_COUNTS);

            rowsPerStripField =
              new TIFFField(base.getTag(BaselineTIFFTagSet.TAG_ROWS_PER_STRIP),
                            rowsPerStrip);
            rootIFD.addTIFFField(rowsPerStripField);

            TIFFField stripOffsetsField =
                new TIFFField(
                         base.getTag(BaselineTIFFTagSet.TAG_STRIP_OFFSETS),
                         TIFFTag.TIFF_LONG,
                         tilesDown);
            rootIFD.addTIFFField(stripOffsetsField);

            TIFFField stripByteCountsField =
                new TIFFField(
                         base.getTag(BaselineTIFFTagSet.TAG_STRIP_BYTE_COUNTS),
                         TIFFTag.TIFF_LONG,
                         tilesDown);
            rootIFD.addTIFFField(stripByteCountsField);
        } else {
            this.isTiled = true;

            rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_ROWS_PER_STRIP);
            rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_STRIP_OFFSETS);
            rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_STRIP_BYTE_COUNTS);

            TIFFField tileWidthField =
                new TIFFField(base.getTag(BaselineTIFFTagSet.TAG_TILE_WIDTH),
                              tileWidth);
            rootIFD.addTIFFField(tileWidthField);

            TIFFField tileLengthField =
                new TIFFField(base.getTag(BaselineTIFFTagSet.TAG_TILE_LENGTH),
                              tileLength);
            rootIFD.addTIFFField(tileLengthField);

            TIFFField tileOffsetsField =
                new TIFFField(
                         base.getTag(BaselineTIFFTagSet.TAG_TILE_OFFSETS),
                         TIFFTag.TIFF_LONG,
                         tilesDown*tilesAcross);
            rootIFD.addTIFFField(tileOffsetsField);

            TIFFField tileByteCountsField =
                new TIFFField(
                         base.getTag(BaselineTIFFTagSet.TAG_TILE_BYTE_COUNTS),
                         TIFFTag.TIFF_LONG,
                         tilesDown*tilesAcross);
            rootIFD.addTIFFField(tileByteCountsField);
        }

        if(isEXIF) {
            //
            // Ensure presence of mandatory fields and absence of prohibited
            // fields and those that duplicate information in JPEG marker
            // segments per tables 14-18 of the EXIF 2.2 specification.
            //

            // If an empty image is being written or inserted then infer
            // that the primary IFD is being set up.
            boolean isPrimaryIFD = isEncodingEmpty();

            // Handle TIFF fields in order of increasing tag number.
            if(compression == BaselineTIFFTagSet.COMPRESSION_OLD_JPEG) {
                // ImageWidth
                rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_IMAGE_WIDTH);

                // ImageLength
                rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_IMAGE_LENGTH);

                // BitsPerSample
                rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_BITS_PER_SAMPLE);
                // Compression
                if(isPrimaryIFD) {
                    rootIFD.removeTIFFField
                        (BaselineTIFFTagSet.TAG_COMPRESSION);
                }

                // PhotometricInterpretation
                rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_PHOTOMETRIC_INTERPRETATION);

                // StripOffsets
                rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_STRIP_OFFSETS);

                // SamplesPerPixel
                rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_SAMPLES_PER_PIXEL);

                // RowsPerStrip
                rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_ROWS_PER_STRIP);

                // StripByteCounts
                rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_STRIP_BYTE_COUNTS);
                // XResolution and YResolution are handled above for all TIFFs.

                // PlanarConfiguration
                rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_PLANAR_CONFIGURATION);

                // ResolutionUnit
                if(rootIFD.getTIFFField
                   (BaselineTIFFTagSet.TAG_RESOLUTION_UNIT) == null) {
                    f = new TIFFField(base.getTag
                                      (BaselineTIFFTagSet.TAG_RESOLUTION_UNIT),
                                      BaselineTIFFTagSet.RESOLUTION_UNIT_INCH);
                    rootIFD.addTIFFField(f);
                }

                if(isPrimaryIFD) {
                    // JPEGInterchangeFormat
                    rootIFD.removeTIFFField
                        (BaselineTIFFTagSet.TAG_JPEG_INTERCHANGE_FORMAT);

                    // JPEGInterchangeFormatLength
                    rootIFD.removeTIFFField
                        (BaselineTIFFTagSet.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH);

                    // YCbCrSubsampling
                    rootIFD.removeTIFFField
                        (BaselineTIFFTagSet.TAG_Y_CB_CR_SUBSAMPLING);

                    // YCbCrPositioning
                    if(rootIFD.getTIFFField
                       (BaselineTIFFTagSet.TAG_Y_CB_CR_POSITIONING) == null) {
                        f = new TIFFField
                            (base.getTag
                             (BaselineTIFFTagSet.TAG_Y_CB_CR_POSITIONING),
                             TIFFTag.TIFF_SHORT,
                             1,
                             new char[] {
                                 (char)BaselineTIFFTagSet.Y_CB_CR_POSITIONING_CENTERED
                             });
                        rootIFD.addTIFFField(f);
                    }
                } else { // Thumbnail IFD
                    // JPEGInterchangeFormat
                    f = new TIFFField
                        (base.getTag
                         (BaselineTIFFTagSet.TAG_JPEG_INTERCHANGE_FORMAT),
                         TIFFTag.TIFF_LONG,
                         1);
                    rootIFD.addTIFFField(f);

                    // JPEGInterchangeFormatLength
                    f = new TIFFField
                        (base.getTag
                         (BaselineTIFFTagSet.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH),
                         TIFFTag.TIFF_LONG,
                         1);
                    rootIFD.addTIFFField(f);

                    // YCbCrSubsampling
                    rootIFD.removeTIFFField
                        (BaselineTIFFTagSet.TAG_Y_CB_CR_SUBSAMPLING);
                }
            } else { // Uncompressed
                // ImageWidth through PlanarConfiguration are set above.
                // XResolution and YResolution are handled above for all TIFFs.

                // ResolutionUnit
                if(rootIFD.getTIFFField
                   (BaselineTIFFTagSet.TAG_RESOLUTION_UNIT) == null) {
                    f = new TIFFField(base.getTag
                                      (BaselineTIFFTagSet.TAG_RESOLUTION_UNIT),
                                      BaselineTIFFTagSet.RESOLUTION_UNIT_INCH);
                    rootIFD.addTIFFField(f);
                }


                // JPEGInterchangeFormat
                rootIFD.removeTIFFField
                    (BaselineTIFFTagSet.TAG_JPEG_INTERCHANGE_FORMAT);

                // JPEGInterchangeFormatLength
                rootIFD.removeTIFFField
                    (BaselineTIFFTagSet.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH);

                if(photometricInterpretation ==
                   BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_RGB) {
                    // YCbCrCoefficients
                    rootIFD.removeTIFFField
                        (BaselineTIFFTagSet.TAG_Y_CB_CR_COEFFICIENTS);

                    // YCbCrSubsampling
                    rootIFD.removeTIFFField
                        (BaselineTIFFTagSet.TAG_Y_CB_CR_SUBSAMPLING);

                    // YCbCrPositioning
                    rootIFD.removeTIFFField
                        (BaselineTIFFTagSet.TAG_Y_CB_CR_POSITIONING);
                }
            }

            // Get EXIF tags.
            TIFFTagSet exifTags = EXIFTIFFTagSet.getInstance();

            // Retrieve or create the EXIF IFD.
            TIFFIFD exifIFD = null;
            f = rootIFD.getTIFFField
                (EXIFParentTIFFTagSet.TAG_EXIF_IFD_POINTER);
            if(f != null) {
                // Retrieve the EXIF IFD.
                exifIFD = (TIFFIFD)f.getData();
            } else if(isPrimaryIFD) {
                // Create the EXIF IFD.
                List exifTagSets = new ArrayList(1);
                exifTagSets.add(exifTags);
                exifIFD = new TIFFIFD(exifTagSets);

                // Add it to the root IFD.
                TIFFTagSet tagSet = EXIFParentTIFFTagSet.getInstance();
                TIFFTag exifIFDTag =
                    tagSet.getTag(EXIFParentTIFFTagSet.TAG_EXIF_IFD_POINTER);
                rootIFD.addTIFFField(new TIFFField(exifIFDTag,
                                                   TIFFTag.TIFF_LONG,
                                                   1,
                                                   exifIFD));
            }

            if(exifIFD != null) {
                // Handle EXIF private fields in order of increasing
                // tag number.

                // ExifVersion
                if(exifIFD.getTIFFField
                   (EXIFTIFFTagSet.TAG_EXIF_VERSION) == null) {
                    f = new TIFFField
                        (exifTags.getTag(EXIFTIFFTagSet.TAG_EXIF_VERSION),
                         TIFFTag.TIFF_UNDEFINED,
                         4,
                         EXIFTIFFTagSet.EXIF_VERSION_2_2);
                    exifIFD.addTIFFField(f);
                }

                if(compression == BaselineTIFFTagSet.COMPRESSION_OLD_JPEG) {
                    // ComponentsConfiguration
                    if(exifIFD.getTIFFField
                       (EXIFTIFFTagSet.TAG_COMPONENTS_CONFIGURATION) == null) {
                        f = new TIFFField
                            (exifTags.getTag
                             (EXIFTIFFTagSet.TAG_COMPONENTS_CONFIGURATION),
                             TIFFTag.TIFF_UNDEFINED,
                             4,
                             new byte[] {
                                 (byte)EXIFTIFFTagSet.COMPONENTS_CONFIGURATION_Y,
                                 (byte)EXIFTIFFTagSet.COMPONENTS_CONFIGURATION_CB,
                                 (byte)EXIFTIFFTagSet.COMPONENTS_CONFIGURATION_CR,
                                 (byte)0
                             });
                        exifIFD.addTIFFField(f);
                    }
                } else {
                    // ComponentsConfiguration
                    exifIFD.removeTIFFField
                        (EXIFTIFFTagSet.TAG_COMPONENTS_CONFIGURATION);

                    // CompressedBitsPerPixel
                    exifIFD.removeTIFFField
                        (EXIFTIFFTagSet.TAG_COMPRESSED_BITS_PER_PIXEL);
                }

                // FlashpixVersion
                if(exifIFD.getTIFFField
                   (EXIFTIFFTagSet.TAG_FLASHPIX_VERSION) == null) {
                    f = new TIFFField
                        (exifTags.getTag(EXIFTIFFTagSet.TAG_FLASHPIX_VERSION),
                         TIFFTag.TIFF_UNDEFINED,
                         4,
                     new byte[] {(byte)'0', (byte)'1', (byte)'0', (byte)'0'});
                    exifIFD.addTIFFField(f);
                }

                // ColorSpace
                if(exifIFD.getTIFFField
                   (EXIFTIFFTagSet.TAG_COLOR_SPACE) == null) {
                    f = new TIFFField
                        (exifTags.getTag(EXIFTIFFTagSet.TAG_COLOR_SPACE),
                         TIFFTag.TIFF_SHORT,
                         1,
                         new char[] {
                             (char)EXIFTIFFTagSet.COLOR_SPACE_SRGB
                         });
                    exifIFD.addTIFFField(f);
                }

                if(compression == BaselineTIFFTagSet.COMPRESSION_OLD_JPEG) {
                    // PixelXDimension
                    if(exifIFD.getTIFFField
                       (EXIFTIFFTagSet.TAG_PIXEL_X_DIMENSION) == null) {
                        f = new TIFFField
                            (exifTags.getTag(EXIFTIFFTagSet.TAG_PIXEL_X_DIMENSION),
                             width);
                        exifIFD.addTIFFField(f);
                    }

                    // PixelYDimension
                    if(exifIFD.getTIFFField
                       (EXIFTIFFTagSet.TAG_PIXEL_Y_DIMENSION) == null) {
                        f = new TIFFField
                            (exifTags.getTag(EXIFTIFFTagSet.TAG_PIXEL_Y_DIMENSION),
                             height);
                        exifIFD.addTIFFField(f);
                    }
                } else {
                    exifIFD.removeTIFFField
                        (EXIFTIFFTagSet.TAG_INTEROPERABILITY_IFD_POINTER);
                }
            }

        } // if(isEXIF)
    }

    /**
       @param tileRect The area to be written which might be outside the image.
     */
    private int writeTile(Rectangle tileRect, TIFFCompressor compressor)
        throws IOException {
        // Determine the rectangle which will actually be written 
        // and set the padding flag. Padding will occur only when the
        // image is written as a tiled TIFF and the tile bounds are not
        // contained within the image bounds.
        Rectangle activeRect;
        boolean isPadded;
        Rectangle imageBounds =
            new Rectangle(image.getMinX(), image.getMinY(),
                          image.getWidth(), image.getHeight());
        if(!isTiled) {
            // Stripped
            activeRect = tileRect.intersection(imageBounds);
            tileRect = activeRect;
            isPadded = false;
        } else if(imageBounds.contains(tileRect)) {
            // Tiled, tile within image bounds
            activeRect = tileRect;
            isPadded = false;
        } else {
            // Tiled, tile not within image bounds
            activeRect = imageBounds.intersection(tileRect);
            isPadded = true;
        }

        // Shouldn't happen, but return early if empty intersection.
        if(activeRect.isEmpty()) {
            return 0;
        }

        int minX = tileRect.x;
        int minY = tileRect.y;
        int width = tileRect.width;
        int height = tileRect.height;

        if(isImageSimple) {

            SampleModel sm = image.getSampleModel();

            // Read only data from the active rectangle.
            Raster raster = image.getData(activeRect);

            // If padding is required, create a larger Raster and fill
            // it from the active rectangle.
            if(isPadded) {
                WritableRaster wr =
                    raster.createCompatibleWritableRaster(minX, minY,
                                                          width, height);
                wr.setRect(raster);
                raster = wr;
            }

            if(isBilevel) {
                /* XXX
                MultiPixelPackedSampleModel mppsm =
                    (MultiPixelPackedSampleModel)raster.getSampleModel();

                byte[] buf;
                int off;
                int lineStride;
                if(mppsm.getDataBitOffset() == 0 &&
                   raster.getDataBuffer() instanceof DataBufferByte) {
                    buf = ((DataBufferByte)raster.getDataBuffer()).getData();
                    off = mppsm.getOffset(tileRect.x -
                                          raster.getSampleModelTranslateX(),
                                          tileRect.y -
                                          raster.getSampleModelTranslateY());
                    lineStride = mppsm.getScanlineStride();
                } else {
                    buf = ImageUtil.getPackedBinaryData(raster,
                                                        tileRect);
                    off = 0;
                    lineStride = (tileRect.width + 7)/8;
                }
                */
                byte[] buf = ImageUtil.getPackedBinaryData(raster,
                                                           tileRect);

                if(isInverted) {
                    DataBuffer dbb = raster.getDataBuffer();
                    if(dbb instanceof DataBufferByte &&
                       buf == ((DataBufferByte)dbb).getData()) {
                        byte[] bbuf = new byte[buf.length];
                        int len = buf.length;
                        for(int i = 0; i < len; i++) {
                            bbuf[i] = (byte)(buf[i] ^ 0xff);
                        }
                        buf = bbuf;
                    } else {
                        int len = buf.length;
                        for(int i = 0; i < len; i++) {
                            buf[i] ^= 0xff;
                        }
                    }
                }

                if(DEBUG) {
                    System.out.println("Optimized bilevel case");
                }

                return compressor.encode(buf, 0,
                                         width, height, sampleSize,
                                         (tileRect.width + 7)/8);
            } else if(bitDepth == 8 &&
                      sm.getDataType() == DataBuffer.TYPE_BYTE) {
                ComponentSampleModel csm =
                    (ComponentSampleModel)raster.getSampleModel();

                byte[] buf =
                    ((DataBufferByte)raster.getDataBuffer()).getData();

                int off =
                    csm.getOffset(minX -
                                  raster.getSampleModelTranslateX(),
                                  minY -
                                  raster.getSampleModelTranslateY());

                if(DEBUG) {
                    System.out.println("Optimized component case");
                }

                return compressor.encode(buf, off,
                                         width, height, sampleSize,
                                         csm.getScanlineStride());
            }
        }
        
        // Set offsets and skips based on source subsampling factors
        int xOffset = minX;
        int xSkip = periodX;
        int yOffset = minY;
        int ySkip = periodY;

        // Early exit if no data for this pass
        int hpixels = (width + xSkip - 1)/xSkip;
        int vpixels = (height + ySkip - 1)/ySkip;
        if (hpixels == 0 || vpixels == 0) {
            return 0;
        }

        // Convert X offset and skip from pixels to samples
        xOffset *= numBands;
        xSkip *= numBands;

        // Initialize sizes
        int samplesPerByte = 8/bitDepth;
        int numSamples = width*numBands;
        int bytesPerRow = hpixels*numBands;

        // Update number of bytes per row.
        if (bitDepth < 8) {
            bytesPerRow = (bytesPerRow + samplesPerByte - 1)/samplesPerByte;
        } else if (bitDepth == 16) {
            bytesPerRow *= 2;
        } else if (bitDepth == 32) {
            bytesPerRow *= 4;
        }

        // Create row buffers
        int[] samples = null;
        float[] fsamples = null;
        if(sampleFormat == BaselineTIFFTagSet.SAMPLE_FORMAT_FLOATING_POINT) {
            fsamples = new float[numSamples];
        } else {
            samples = new int[numSamples];
        }

        // Create tile buffer
        byte[] currTile = new byte[bytesPerRow*vpixels];

        // Sub-optimal case: shy of "isImageSimple" only by virtue of
        // not being contiguous.
        if(!isInverted &&                  // no inversion
           !isRescaling &&                 // no value rescaling
           sourceBands == null &&          // no subbanding
           periodX == 1 && periodY == 1 && // no subsampling
           colorConverter == null) {

            SampleModel sm = image.getSampleModel();

            if(sm instanceof ComponentSampleModel &&       // component
               bitDepth == 8 &&                            // 8 bits/sample
               sm.getDataType() == DataBuffer.TYPE_BYTE) { // byte type

                if(DEBUG) {
                    System.out.println("Sub-optimal byte component case");
                    System.out.println(sm.getClass().getName());
                }

                // Read only data from the active rectangle.
                Raster raster = image.getData(activeRect);

                // If padding is required, create a larger Raster and fill
                // it from the active rectangle.
                if(isPadded) {
                    WritableRaster wr =
                        raster.createCompatibleWritableRaster(minX, minY,
                                                              width, height);
                    wr.setRect(raster);
                    raster = wr;
                }

                // Get SampleModel info.
                ComponentSampleModel csm =
                    (ComponentSampleModel)raster.getSampleModel();
                int[] bankIndices = csm.getBankIndices();
                byte[][] bankData =
                    ((DataBufferByte)raster.getDataBuffer()).getBankData();
                int lineStride = csm.getScanlineStride();
                int pixelStride = csm.getPixelStride();

                // Copy the data into a contiguous pixel interleaved buffer.
                for(int k = 0; k < numBands; k++) {
                    byte[] bandData = bankData[bankIndices[k]];
                    int lineOffset =
                        csm.getOffset(raster.getMinX() -
                                      raster.getSampleModelTranslateX(),
                                      raster.getMinY() -
                                      raster.getSampleModelTranslateY(), k);
                    int idx = k;
                    for(int j = 0; j < vpixels; j++) {
                        int offset = lineOffset;
                        for(int i = 0; i < hpixels; i++) {
                            currTile[idx] = bandData[offset];
                            idx += numBands;
                            offset += pixelStride;
                        }
                        lineOffset += lineStride;
                    }
                }

                // Compressor and return.
                return compressor.encode(currTile, 0,
                                         width, height, sampleSize,
                                         width*numBands);
            }
        }

        if(DEBUG) {
            System.out.println("Unoptimized case for bit depth "+bitDepth);
            SampleModel sm = image.getSampleModel();
            System.out.println("isRescaling = "+isRescaling);
            System.out.println("sourceBands = "+sourceBands);
            System.out.println("periodX = "+periodX);
            System.out.println("periodY = "+periodY);
            System.out.println(sm.getClass().getName());
            System.out.println(sm.getDataType());
            if(sm instanceof ComponentSampleModel) {
                ComponentSampleModel csm = (ComponentSampleModel)sm;
                System.out.println(csm.getNumBands());
                System.out.println(csm.getPixelStride());
                int[] bankIndices = csm.getBankIndices();
                for(int b = 0; b < numBands; b++) {
                    System.out.print(bankIndices[b]+" ");
                }
                int[] bandOffsets = csm.getBandOffsets();
                for(int b = 0; b < numBands; b++) {
                    System.out.print(bandOffsets[b]+" ");
                }
                System.out.println("");
            }
        }

        int tcount = 0;

        // Save active rectangle variables.
        int activeMinX = activeRect.x;
        int activeMinY = activeRect.y;
        int activeMaxY = activeMinY + activeRect.height - 1;
        int activeWidth = activeRect.width;

        // Set a SampleModel for use in padding.
        SampleModel rowSampleModel = null;
        if(isPadded) {
           rowSampleModel =
               image.getSampleModel().createCompatibleSampleModel(width, 1);
        }

        for (int row = yOffset; row < yOffset + height; row += ySkip) {
            Raster ras = null;
            if(isPadded) {
                // Create a raster for the entire row.
                WritableRaster wr =
                    Raster.createWritableRaster(rowSampleModel,
                                                new Point(minX, row));

                // Populate the raster from the active sub-row, if any.
                if(row >= activeMinY && row <= activeMaxY) {
                    Rectangle rect =
                        new Rectangle(activeMinX, row, activeWidth, 1);
                    ras = image.getData(rect);
                    wr.setRect(ras);
                }

                // Update the raster variable.
                ras = wr;
            } else {
                Rectangle rect = new Rectangle(minX, row, width, 1);
                ras = image.getData(rect);
            }
            if (sourceBands != null) {
                ras = ras.createChild(minX, row, width, 1, minX, row,
                                      sourceBands);
            }

            if(sampleFormat ==
               BaselineTIFFTagSet.SAMPLE_FORMAT_FLOATING_POINT) {
                ras.getPixels(minX, row, width, 1, fsamples);
            } else {
                ras.getPixels(minX, row, width, 1, samples);

                if ((nativePhotometricInterpretation ==
                     BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO &&
                     photometricInterpretation ==
                     BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO) ||
                    (nativePhotometricInterpretation ==
                     BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO &&
                     photometricInterpretation ==
                     BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO)) {
                    int bitMask = (1 << bitDepth) - 1;
                    for (int s = 0; s < numSamples; s++) {
                        samples[s] ^= bitMask;
                    }
                }
            }

            if (colorConverter != null) {
                int idx = 0;
                float[] result = new float[3];

                if(sampleFormat ==
                   BaselineTIFFTagSet.SAMPLE_FORMAT_FLOATING_POINT) {
                    for (int i = 0; i < width; i++) {
                        float r = fsamples[idx];
                        float g = fsamples[idx + 1];
                        float b = fsamples[idx + 2];

                        colorConverter.fromRGB(r, g, b, result);

                        fsamples[idx] = result[0];
                        fsamples[idx + 1] = result[1];
                        fsamples[idx + 2] = result[2];

                        idx += 3;
                    }
                } else {
                    for (int i = 0; i < width; i++) {
                        float r = (float)samples[idx];
                        float g = (float)samples[idx + 1];
                        float b = (float)samples[idx + 2];

                        colorConverter.fromRGB(r, g, b, result);

                        samples[idx] = (int)(result[0]);
                        samples[idx + 1] = (int)(result[1]);
                        samples[idx + 2] = (int)(result[2]);

                        idx += 3;
                    }
                }
            }

            int tmp = 0;
            int pos = 0;
            
            switch (bitDepth) {
            case 1: case 2: case 4:
                // Image can only have a single band
                
                if(isRescaling) {
                    for (int s = 0; s < numSamples; s += xSkip) {
                        byte val = scale0[samples[s]];
                        tmp = (tmp << bitDepth) | val;

                        if (++pos == samplesPerByte) {
                            currTile[tcount++] = (byte)tmp;
                            tmp = 0;
                            pos = 0;
                        }
                    }
                } else {
                    for (int s = 0; s < numSamples; s += xSkip) {
                        byte val = (byte)samples[s];
                        tmp = (tmp << bitDepth) | val;

                        if (++pos == samplesPerByte) {
                            currTile[tcount++] = (byte)tmp;
                            tmp = 0;
                            pos = 0;
                        }
                    }
                }

                // Left shift the last byte
                if (pos != 0) {
                    tmp <<= ((8/bitDepth) - pos)*bitDepth;
                    currTile[tcount++] = (byte)tmp;
                }
                break;

            case 8:
		if (numBands == 1) {
                    if(isRescaling) {
                        for (int s = 0; s < numSamples; s += xSkip) {
                            currTile[tcount++] = scale0[samples[s]];
                        }
                    } else {
                        for (int s = 0; s < numSamples; s += xSkip) {
                            currTile[tcount++] = (byte)samples[s];
                        }
                    }
		} else {
                    if(isRescaling) {
                        for (int s = 0; s < numSamples; s += xSkip) {
                            for (int b = 0; b < numBands; b++) {
                                currTile[tcount++] = scale[b][samples[s + b]];
                            }
                        }
                    } else {
                        for (int s = 0; s < numSamples; s += xSkip) {
                            for (int b = 0; b < numBands; b++) {
                                currTile[tcount++] = (byte)samples[s + b];
                            }
                        }
                    }
		}
                break;

            case 16:
                // XXX Need to verify this rescaling for signed vs. unsigned.
                if(isRescaling) {
                    if(stream.getByteOrder() == ByteOrder.BIG_ENDIAN) {
                        for (int s = 0; s < numSamples; s += xSkip) {
                            for (int b = 0; b < numBands; b++) {
                                int sample = samples[s + b];
                                currTile[tcount++] = scaleh[b][sample];
                                currTile[tcount++] = scalel[b][sample];
                            }
                        }
                    } else { // ByteOrder.LITLE_ENDIAN
                        for (int s = 0; s < numSamples; s += xSkip) {
                            for (int b = 0; b < numBands; b++) {
                                int sample = samples[s + b];
                                currTile[tcount++] = scalel[b][sample];
                                currTile[tcount++] = scaleh[b][sample];
                            }
                        }
                    }
                } else {
                    if(stream.getByteOrder() == ByteOrder.BIG_ENDIAN) {
                        for (int s = 0; s < numSamples; s += xSkip) {
                            for (int b = 0; b < numBands; b++) {
                                int sample = samples[s + b];
                                currTile[tcount++] =
                                    (byte)((sample >>> 8) & 0xff);
                                currTile[tcount++] =
                                    (byte)(sample & 0xff);
                            }
                        }
                    } else { // ByteOrder.LITLE_ENDIAN
                        for (int s = 0; s < numSamples; s += xSkip) {
                            for (int b = 0; b < numBands; b++) {
                                int sample = samples[s + b];
                                currTile[tcount++] =
                                    (byte)(sample & 0xff);
                                currTile[tcount++] =
                                    (byte)((sample >>> 8) & 0xff);
                            }
                        }
                    }
                }
                break;

            case 32:
                if(sampleFormat ==
                   BaselineTIFFTagSet.SAMPLE_FORMAT_FLOATING_POINT) {
                    if(stream.getByteOrder() == ByteOrder.BIG_ENDIAN) {
                        for (int s = 0; s < numSamples; s += xSkip) {
                            for (int b = 0; b < numBands; b++) {
                                float fsample = fsamples[s + b];
                                int isample = Float.floatToIntBits(fsample);
                                currTile[tcount++] =
                                    (byte)((isample & 0xff000000) >> 24);
                                currTile[tcount++] =
                                    (byte)((isample & 0x00ff0000) >> 16);
                                currTile[tcount++] =
                                    (byte)((isample & 0x0000ff00) >> 8);
                                currTile[tcount++] =
                                    (byte)(isample & 0x000000ff);
                            }
                        }
                    } else { // ByteOrder.LITLE_ENDIAN
                        for (int s = 0; s < numSamples; s += xSkip) {
                            for (int b = 0; b < numBands; b++) {
                                float fsample = fsamples[s + b];
                                int isample = Float.floatToIntBits(fsample);
                                currTile[tcount++] =
                                    (byte)(isample & 0x000000ff);
                                currTile[tcount++] =
                                    (byte)((isample & 0x0000ff00) >> 8);
                                currTile[tcount++] =
                                    (byte)((isample & 0x00ff0000) >> 16);
                                currTile[tcount++] =
                                    (byte)((isample & 0xff000000) >> 24);
                            }
                        }
                    }
                } else {
                    if(isRescaling) {
                        // XXX Need to verify this for signed vs. unsigned.
                        // XXX The following gives saturated results when the
                        // original data are in the signed integer range.
                        long[] maxIn = new long[numBands];
                        long[] halfIn = new long[numBands];
                        long maxOut = (1L << (long)bitDepth) - 1L;

                        for (int b = 0; b < numBands; b++) {
                            maxIn[b] = ((1L << (long)sampleSize[b]) - 1L);
                            halfIn[b] = maxIn[b]/2;
                        }

                        if(stream.getByteOrder() == ByteOrder.BIG_ENDIAN) {
                            for (int s = 0; s < numSamples; s += xSkip) {
                                for (int b = 0; b < numBands; b++) {
                                    long sampleOut =
                                        (samples[s + b]*maxOut + halfIn[b])/
                                        maxIn[b];
                                    currTile[tcount++] =
                                        (byte)((sampleOut & 0xff000000) >> 24);
                                    currTile[tcount++] =
                                        (byte)((sampleOut & 0x00ff0000) >> 16);
                                    currTile[tcount++] =
                                        (byte)((sampleOut & 0x0000ff00) >> 8);
                                    currTile[tcount++] =
                                        (byte)(sampleOut & 0x000000ff);
                                }
                            }
                        } else { // ByteOrder.LITLE_ENDIAN
                            for (int s = 0; s < numSamples; s += xSkip) {
                                for (int b = 0; b < numBands; b++) {
                                    long sampleOut =
                                        (samples[s + b]*maxOut + halfIn[b])/
                                        maxIn[b];
                                    currTile[tcount++] =
                                        (byte)(sampleOut & 0x000000ff);
                                    currTile[tcount++] =
                                        (byte)((sampleOut & 0x0000ff00) >> 8);
                                    currTile[tcount++] =
                                        (byte)((sampleOut & 0x00ff0000) >> 16);
                                    currTile[tcount++] =
                                        (byte)((sampleOut & 0xff000000) >> 24);
                                }
                            }
                        }
                    } else {
                        if(stream.getByteOrder() == ByteOrder.BIG_ENDIAN) {
                            for (int s = 0; s < numSamples; s += xSkip) {
                                for (int b = 0; b < numBands; b++) {
                                    int isample = samples[s + b];
                                    currTile[tcount++] =
                                        (byte)((isample & 0xff000000) >> 24);
                                    currTile[tcount++] =
                                        (byte)((isample & 0x00ff0000) >> 16);
                                    currTile[tcount++] =
                                        (byte)((isample & 0x0000ff00) >> 8);
                                    currTile[tcount++] =
                                        (byte)(isample & 0x000000ff);
                                }
                            }
                        } else { // ByteOrder.LITLE_ENDIAN
                            for (int s = 0; s < numSamples; s += xSkip) {
                                for (int b = 0; b < numBands; b++) {
                                    int isample = samples[s + b];
                                    currTile[tcount++] =
                                        (byte)(isample & 0x000000ff);
                                    currTile[tcount++] =
                                        (byte)((isample & 0x0000ff00) >> 8);
                                    currTile[tcount++] =
                                        (byte)((isample & 0x00ff0000) >> 16);
                                    currTile[tcount++] =
                                        (byte)((isample & 0xff000000) >> 24);
                                }
                            }
                        }
                    }
                }
                break;
            }
        }

        int[] bitsPerSample = new int[numBands];
        for (int i = 0; i < bitsPerSample.length; i++) {
            bitsPerSample[i] = bitDepth;
        }

        int byteCount = compressor.encode(currTile, 0,
                                          hpixels, vpixels,
                                          bitsPerSample,
                                          bytesPerRow);
        return byteCount;
    }

    // Check two int arrays for value equality, always returns false
    // if either array is null
    private boolean equals(int[] s0, int[] s1) {
	if (s0 == null || s1 == null) {
	    return false;
	}
	if (s0.length != s1.length) {
	    return false;
	}
	for (int i = 0; i < s0.length; i++) {
	    if (s0[i] != s1[i]) {
		return false;
	    }
	}
	return true;
    }

    // Initialize the scale/scale0 or scaleh/scalel arrays to
    // hold the results of scaling an input value to the desired
    // output bit depth
    // XXX Need to verify this rescaling for signed vs. unsigned.
    private void initializeScaleTables(int[] sampleSize) {
        // Save the sample size in the instance variable.

	// If the existing tables are still valid, just return.
	if (bitDepth == scalingBitDepth &&
            equals(sampleSize, this.sampleSize)) {
            if(DEBUG) {
                System.out.println("Returning from initializeScaleTables()");
            }
	    return;
	}

        // Reset scaling variables.
        isRescaling = false;
        scalingBitDepth = -1;
        scale = scalel = scaleh = null;
        scale0 = null;

        // Set global sample size to parameter.
        this.sampleSize = sampleSize;

        // Check whether rescaling is called for.
        if(bitDepth <= 16) {
            for(int b = 0; b < numBands; b++) {
                if(sampleSize[b] != bitDepth) {
                    isRescaling = true;
                    break;
                }
            }
        }

        if(DEBUG) {
            System.out.println("isRescaling = "+isRescaling);
        }

        // If not rescaling then return after saving the sample size.
        if(!isRescaling) {
            return;
        }
	
	// Compute new tables
	this.scalingBitDepth = bitDepth;
	int maxOutSample = (1 << bitDepth) - 1;
	if (bitDepth <= 8) {
	    scale = new byte[numBands][];
	    for (int b = 0; b < numBands; b++) {
		int maxInSample = (1 << sampleSize[b]) - 1;
		int halfMaxInSample = maxInSample/2;
		scale[b] = new byte[maxInSample + 1];
		for (int s = 0; s <= maxInSample; s++) {
		    scale[b][s] =
			(byte)((s*maxOutSample + halfMaxInSample)/maxInSample);
		}
	    }
	    scale0 = scale[0];
	    scaleh = scalel = null;
	} else if(bitDepth <= 16) {
	    // Divide scaling table into high and low bytes
	    scaleh = new byte[numBands][];
	    scalel = new byte[numBands][];

	    for (int b = 0; b < numBands; b++) {
		int maxInSample = (1 << sampleSize[b]) - 1;
		int halfMaxInSample = maxInSample/2;
		scaleh[b] = new byte[maxInSample + 1];
		scalel[b] = new byte[maxInSample + 1];
		for (int s = 0; s <= maxInSample; s++) {
		    int val = (s*maxOutSample + halfMaxInSample)/maxInSample;
		    scaleh[b][s] = (byte)(val >> 8);
		    scalel[b][s] = (byte)(val & 0xff);
		}
	    }
	    scale = null;
	    scale0 = null;
	}
    }

    public void write(IIOMetadata sm,
                      IIOImage iioimage,
                      ImageWriteParam p) throws IOException {
	write(sm, iioimage, p, true, true);
    }
    
    private void writeHeader() throws IOException {
        if (streamMetadata != null) {
            this.byteOrder = streamMetadata.byteOrder;
        } else {
            this.byteOrder = ByteOrder.BIG_ENDIAN;
        }

	stream.setByteOrder(byteOrder);
	if (byteOrder == ByteOrder.BIG_ENDIAN) {
	    stream.writeShort(0x4d4d);
	} else {
	    stream.writeShort(0x4949);
	}
	
	stream.writeShort(42); // Magic number
        stream.writeInt(0); // Offset of first IFD (0 == none)

        nextSpace = stream.getStreamPosition();
        headerPosition = nextSpace - 8;
    }

    private void write(IIOMetadata sm,
                       IIOImage iioimage,
                       ImageWriteParam p,
                       boolean writeHeader,
                       boolean writeData) throws IOException {
        if (stream == null) {
            throw new IllegalStateException("output == null!");
        }
        if (iioimage == null) {
            throw new IllegalArgumentException("image == null!");
        }
        if(iioimage.hasRaster() && !canWriteRasters()) {
            throw new UnsupportedOperationException
                ("TIFF ImageWriter cannot write Rasters!");
        }

        this.image = iioimage.getRenderedImage();
        SampleModel sampleModel = image.getSampleModel();

        this.sourceXOffset = image.getMinX();
        this.sourceYOffset = image.getMinY();
        this.sourceWidth = image.getWidth();
        this.sourceHeight = image.getHeight();

        Rectangle imageBounds = new Rectangle(sourceXOffset,
                                              sourceYOffset,
                                              sourceWidth,
                                              sourceHeight);

        ColorModel colorModel = null;
        if (p == null) {
            this.param = getDefaultWriteParam();
            this.sourceBands = null;
            this.periodX = 1;
            this.periodY = 1;
            this.numBands = sampleModel.getNumBands();
            colorModel = image.getColorModel();
        } else {
            this.param = p;

            // Get source region and subsampling factors
            Rectangle sourceRegion = param.getSourceRegion();
            if (sourceRegion != null) {
                // Clip to actual image bounds
                sourceRegion = sourceRegion.intersection(imageBounds);

                sourceXOffset = sourceRegion.x;
                sourceYOffset = sourceRegion.y;
                sourceWidth = sourceRegion.width;
                sourceHeight = sourceRegion.height;
            }

            // Adjust for subsampling offsets
            int gridX = param.getSubsamplingXOffset();
            int gridY = param.getSubsamplingYOffset();
            this.sourceXOffset += gridX;
            this.sourceYOffset += gridY;
            this.sourceWidth -= gridX;
            this.sourceHeight -= gridY;
            
            // Get subsampling factors
            this.periodX = param.getSourceXSubsampling();
            this.periodY = param.getSourceYSubsampling();
            
            int[] sBands = param.getSourceBands();
            if (sBands != null) {
                sourceBands = sBands;
                this.numBands = sourceBands.length;
            } else {
                this.numBands = sampleModel.getNumBands();
            }

            ImageTypeSpecifier destType = p.getDestinationType();
            if(destType != null) {
                ColorModel cm = destType.getColorModel();
                if(cm.getNumComponents() == numBands) {
                    colorModel = cm;
                }
            }

            if(colorModel == null) {
                colorModel = image.getColorModel();
            }
        }
            
        this.imageType = new ImageTypeSpecifier(colorModel, sampleModel);

	ImageUtil.canEncodeImage(this, this.imageType);

        // Compute output dimensions
        int destWidth = (sourceWidth + periodX - 1)/periodX;
        int destHeight = (sourceHeight + periodY - 1)/periodY;
        if (destWidth <= 0 || destHeight <= 0) {
            throw new IllegalArgumentException("Empty source region!");
        }

        // this.bitDepth = 8; // XXX fix?

        clearAbortRequest();
        processImageStarted(0);

        // Optionally write the header.
	if (writeHeader) {
            // Clear previous stream metadata.
	    this.streamMetadata = null;

            // Try to convert non-null input stream metadata.
            if (sm != null) {
                this.streamMetadata =
                    (TIFFStreamMetadata)convertStreamMetadata(sm, param);
            }

            // Set to default if not converted.
            if(this.streamMetadata == null) {
                this.streamMetadata =
                    (TIFFStreamMetadata)getDefaultStreamMetadata(param);
            }

            // Write the header.
	    writeHeader();

            // Seek to the position of the IFD pointer in the header.
            stream.seek(headerPosition + 4);

            // Ensure IFD is written on a word boundary
            nextSpace = (nextSpace + 3) & ~0x3;

            // Write the pointer to the first IFD after the header.
            stream.writeInt((int)nextSpace);
	}

        // Write out the IFD and any sub IFDs, followed by a zero

        // Clear previous image metadata.
        this.imageMetadata = null;

        // Initialize the metadata object.
        IIOMetadata im = iioimage.getMetadata();
        if(im != null) {
            if (im instanceof TIFFImageMetadata) {
                // Clone the one passed in.
                this.imageMetadata = ((TIFFImageMetadata)im).getShallowClone();
            } else if(Arrays.asList(im.getMetadataFormatNames()).contains(
                   TIFFImageMetadata.nativeMetadataFormatName)) {
                this.imageMetadata = convertNativeImageMetadata(im);
            } else if(im.isStandardMetadataFormatSupported()) {
                try {
                    // Convert standard metadata.
                    this.imageMetadata = convertStandardImageMetadata(im);
                } catch(IIOInvalidTreeException e) {
                    // XXX Warning
                }
            }
        }

        // Use default metadata if still null.
        if(this.imageMetadata == null) {
            this.imageMetadata =
                (TIFFImageMetadata)getDefaultImageMetadata(this.imageType,
                                                           this.param);
        }

        // Set or overwrite mandatory fields in the root IFD
        setupMetadata(colorModel, sampleModel, destWidth, destHeight);

        // Set compressor fields.
        compressor.setWriter(this);
        // Metadata needs to be set on the compressor before the IFD is
        // written as the compressor could modify the metadata.
        compressor.setMetadata(imageMetadata);
        compressor.setStream(stream);
        
	// Initialize scaling tables for this image
        int[] sampleSize = sampleModel.getSampleSize();
        initializeScaleTables(sampleModel.getSampleSize());

        // Determine whether bilevel.
        this.isBilevel = ImageUtil.isBinary(this.image.getSampleModel());

        // Check for photometric inversion.
        this.isInverted =
            (nativePhotometricInterpretation ==
             BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO &&
             photometricInterpretation ==
             BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO) ||
            (nativePhotometricInterpretation ==
             BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO &&
             photometricInterpretation ==
             BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO);

        // Analyze image data suitability for direct copy.
        this.isImageSimple = 
            (isBilevel ||
             (!isInverted && ImageUtil.imageIsContiguous(this.image))) &&
            !isRescaling &&                 // no value rescaling
            sourceBands == null &&          // no subbanding
            periodX == 1 && periodY == 1 && // no subsampling
            colorConverter == null;

        TIFFIFD rootIFD = imageMetadata.getRootIFD();

        rootIFD.writeToStream(stream);

        this.nextIFDPointerPos = stream.getStreamPosition();
        stream.writeInt(0);

        // Seek to end of IFD data
        long lastIFDPosition = rootIFD.getLastPosition();
        stream.seek(lastIFDPosition);
        if(lastIFDPosition > this.nextSpace) {
            this.nextSpace = lastIFDPosition;
        }

        // If not writing the image data, i.e., if writing or inserting an
        // empty image, return.
        if(!writeData) {
            return;
        }

        // Get positions of fields within the IFD to update as we write
        // each strip or tile
        long stripOrTileByteCountsPosition =
            rootIFD.getStripOrTileByteCountsPosition();
        long stripOrTileOffsetsPosition =
            rootIFD.getStripOrTileOffsetsPosition();

        // Compute total number of pixels for progress notification
        this.totalPixels = tileWidth*tileLength*tilesDown*tilesAcross;
        this.pixelsDone = 0;

        // Write the image, a strip or tile at a time
        for (int tj = 0; tj < tilesDown; tj++) {
            for (int ti = 0; ti < tilesAcross; ti++) {
                long pos = stream.getStreamPosition();

                // Write the (possibly compressed) tile data

                Rectangle tileRect =
                    new Rectangle(sourceXOffset + ti*tileWidth*periodX,
                                  sourceYOffset + tj*tileLength*periodY,
                                  tileWidth*periodX,
                                  tileLength*periodY);
                // tileRect = tileRect.intersection(imageBounds); // XXX

                try {
                    int byteCount = writeTile(tileRect, compressor);

                    if(pos + byteCount > nextSpace) {
                        nextSpace = pos + byteCount;
                    }

                    pixelsDone += tileRect.width*tileRect.height;
                    processImageProgress(100.0F*pixelsDone/totalPixels);
        
                    // Fill in the offset and byte count for the file
                    stream.mark();
                    stream.seek(stripOrTileOffsetsPosition);
                    stream.writeInt((int)pos);
                    stripOrTileOffsetsPosition += 4;
                    
                    stream.seek(stripOrTileByteCountsPosition);
                    stream.writeInt(byteCount);
                    stripOrTileByteCountsPosition += 4;
                    stream.reset();
                } catch (IOException e) {
                    throw new IIOException("I/O error writing TIFF file!", e);
                }

                if (abortRequested()) {
                    processWriteAborted();
                    return;
                }
            }
        }
        
        processImageComplete();
    }

    public boolean canWriteSequence() {
	return true;
    }

    public void prepareWriteSequence(IIOMetadata streamMetadata)
	throws IOException {
        if (getOutput() == null) {
            throw new IllegalStateException("getOutput() == null!");
        }

        // Set up stream metadata.
	if (streamMetadata != null) {
            streamMetadata = convertStreamMetadata(streamMetadata, null);
	}
        if(streamMetadata == null) {
            streamMetadata = getDefaultStreamMetadata(null);
        }
        this.streamMetadata = (TIFFStreamMetadata)streamMetadata;

        // Write the header.
	writeHeader();

        // Set the sequence flag.
        this.isWritingSequence = true;
    }

    public void writeToSequence(IIOImage image, ImageWriteParam param)
 	throws IOException {
        // Check sequence flag.
        if(!this.isWritingSequence) {
            throw new IllegalStateException
                ("prepareWriteSequence() has not been called!");
        }

        // Append image.
	writeInsert(-1, image, param);
    }

    public void endWriteSequence() throws IOException {
        // Check output.
        if (getOutput() == null) {
            throw new IllegalStateException("getOutput() == null!");
        }

        // Check sequence flag.
        if(!isWritingSequence) {
            throw new IllegalStateException
                ("prepareWriteSequence() has not been called!");
        }

        // Unset sequence flag.
        this.isWritingSequence = false;
    }

    public boolean canInsertImage(int imageIndex) throws IOException {
        if (getOutput() == null) {
            throw new IllegalStateException("getOutput() == null!");
        }

        // Mark position as locateIFD() will seek to IFD at imageIndex.
        stream.mark();

        // locateIFD() will throw an IndexOutOfBoundsException if
        // imageIndex is < -1 or is too big thereby satisfying the spec.
        long[] ifdpos = new long[1];
        long[] ifd = new long[1];
        locateIFD(imageIndex, ifdpos, ifd);

        // Reset to position before locateIFD().
        stream.reset();

        return true;
    }

    // Locate start of IFD for image.
    // Throws IIOException if not at a TIFF header and
    // IndexOutOfBoundsException if imageIndex is < -1 or is too big.
    private void locateIFD(int imageIndex, long[] ifdpos, long[] ifd) 
        throws IOException {

        if(imageIndex < -1) {
            throw new IndexOutOfBoundsException("imageIndex < -1!");
        }

        long startPos = stream.getStreamPosition();

	stream.seek(headerPosition);
	int byteOrder = stream.readUnsignedShort();
	if (byteOrder == 0x4d4d) {
	    stream.setByteOrder(ByteOrder.BIG_ENDIAN);
	} else if (byteOrder == 0x4949) {
	    stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
	} else {
            stream.seek(startPos);
	    throw new IIOException("Illegal byte order");
	}
	if (stream.readUnsignedShort() != 42) {
            stream.seek(startPos);
	    throw new IIOException("Illegal magic number");
	}

	ifdpos[0] = stream.getStreamPosition();
	ifd[0] = stream.readUnsignedInt();
        if (ifd[0] == 0) {
            // imageIndex has to be >= -1 due to check above.
            if(imageIndex > 0) {
                stream.seek(startPos);
                throw new IndexOutOfBoundsException
                    ("imageIndex is greater than the largest available index!");
            }
            return;
        }
	stream.seek(ifd[0]);

	for (int i = 0; imageIndex == -1 || i < imageIndex; i++) {
            int numFields;
            try {
                numFields = stream.readShort();
            } catch (EOFException eof) {
                stream.seek(startPos);
                ifd[0] = 0;
                return;
            }

            stream.skipBytes(12*numFields);

	    ifdpos[0] = stream.getStreamPosition();
	    ifd[0] = stream.readUnsignedInt();
	    if (ifd[0] == 0) {
		if (imageIndex != -1 && i < imageIndex - 1) {
                    stream.seek(startPos);
		    throw new IndexOutOfBoundsException(
                    "imageIndex is greater than the largest available index!");
		}
		break;
	    }
	    stream.seek(ifd[0]);
	}
    }

    public void writeInsert(int imageIndex,
                            IIOImage image,
                            ImageWriteParam param) throws IOException {
        insert(imageIndex, image, param, true);
    }

    private void insert(int imageIndex,
                        IIOImage image,
                        ImageWriteParam param,
                        boolean writeData) throws IOException {
	if (stream == null) {
	    throw new IllegalStateException("Output not set!");
	}
	if (image == null) {
	    throw new IllegalArgumentException("image == null!");
	}

        // Locate the position of the old IFD (ifd) and the location
        // of the pointer to that position (ifdpos).
        long[] ifdpos = new long[1];
        long[] ifd = new long[1];

        // locateIFD() will throw an IndexOutOfBoundsException if
        // imageIndex is < -1 or is too big thereby satisfying the spec.
        locateIFD(imageIndex, ifdpos, ifd);

        // Seek to the position containing the pointer to the old IFD.
	stream.seek(ifdpos[0]);

        // Update next space pointer in anticipation of next write.
        if(ifdpos[0] + 4 > nextSpace) {
            nextSpace = ifdpos[0] + 4;
        }

        // Ensure IFD is written on a word boundary
        nextSpace = (nextSpace + 3) & ~0x3;

        // Update the value to point to the next available space.
	stream.writeInt((int)nextSpace);

        // Seek to the next available space.
	stream.seek(nextSpace);

        // Write the image (IFD and data).
	write(null, image, param, false, writeData);

        // Seek to the position containing the pointer in the new IFD.
	stream.seek(nextIFDPointerPos);

        // Update the new IFD to point to the old IFD.
	stream.writeInt((int)ifd[0]);
        // Don't need to update nextSpace here as already done in write().
    }

    // ----- BEGIN insert/writeEmpty methods -----

    // XXX Move local variable(s) up.
    private boolean isInsertingEmpty = false;
    private boolean isWritingEmpty = false;

    private boolean isEncodingEmpty() {
        return isInsertingEmpty || isWritingEmpty;
    }

    public boolean canInsertEmpty(int imageIndex) throws IOException {
        return canInsertImage(imageIndex);
    }

    public boolean canWriteEmpty() throws IOException {
        if (getOutput() == null) {
            throw new IllegalStateException("getOutput() == null!");
        }
        return true;
    }

    // Check state and parameters for writing or inserting empty images.
    private void checkParamsEmpty(ImageTypeSpecifier imageType,
                                  int width,
                                  int height,
                                  List thumbnails) {
        if (getOutput() == null) {
            throw new IllegalStateException("getOutput() == null!");
        }

        if(imageType == null) {
            throw new IllegalArgumentException("imageType == null!");
        }

        if(width < 1 || height < 1) {
            throw new IllegalArgumentException("width < 1 || height < 1!");
        }

        if(thumbnails != null) {
            int numThumbs = thumbnails.size();
            for(int i = 0; i < numThumbs; i++) {
                Object thumb = thumbnails.get(i);
                if(thumb == null || !(thumb instanceof BufferedImage)) {
                    throw new IllegalArgumentException
                        ("thumbnails contains null references or objects other than BufferedImages!");
                }
            }
        }

        if(this.isInsertingEmpty) {
            throw new IllegalStateException
                ("Previous call to prepareInsertEmpty() without corresponding call to endInsertEmpty()!");
        }

        if(this.isWritingEmpty) {
            throw new IllegalStateException
                ("Previous call to prepareWriteEmpty() without corresponding call to endWriteEmpty()!");
        }
    }

    public void prepareInsertEmpty(int imageIndex,
                                   ImageTypeSpecifier imageType,
                                   int width,
                                   int height,
                                   IIOMetadata imageMetadata,
                                   List thumbnails,
                                   ImageWriteParam param) throws IOException {
        checkParamsEmpty(imageType, width, height, thumbnails);

        this.isInsertingEmpty = true;

        SampleModel emptySM = imageType.getSampleModel();
        RenderedImage emptyImage =
            new EmptyImage(0, 0, width, height,
                           0, 0, emptySM.getWidth(), emptySM.getHeight(),
                           emptySM, imageType.getColorModel());

        insert(imageIndex, new IIOImage(emptyImage, null, imageMetadata),
               param, false);
    }

    public void prepareWriteEmpty(IIOMetadata streamMetadata,
                                  ImageTypeSpecifier imageType,
                                  int width,
                                  int height,
                                  IIOMetadata imageMetadata,
                                  List thumbnails,
                                  ImageWriteParam param) throws IOException {
        checkParamsEmpty(imageType, width, height, thumbnails);

        this.isWritingEmpty = true;

        SampleModel emptySM = imageType.getSampleModel();
        RenderedImage emptyImage =
            new EmptyImage(0, 0, width, height,
                           0, 0, emptySM.getWidth(), emptySM.getHeight(),
                           emptySM, imageType.getColorModel());

	write(streamMetadata, new IIOImage(emptyImage, null, imageMetadata),
              param, true, false);
    }

    public void endInsertEmpty() throws IOException {
        if (getOutput() == null) {
            throw new IllegalStateException("getOutput() == null!");
        }

        if(!this.isInsertingEmpty) {
            throw new IllegalStateException
                ("No previous call to prepareInsertEmpty()!");
        }

        if(this.isWritingEmpty) {
            throw new IllegalStateException
                ("Previous call to prepareWriteEmpty() without corresponding call to endWriteEmpty()!");
        }

        if (inReplacePixelsNest) {
            throw new IllegalStateException
                ("In nested call to prepareReplacePixels!");
        }

        this.isInsertingEmpty = false;
    }

    public void endWriteEmpty() throws IOException {
        if (getOutput() == null) {
            throw new IllegalStateException("getOutput() == null!");
        }

        if(!this.isWritingEmpty) {
            throw new IllegalStateException
                ("No previous call to prepareWriteEmpty()!");
        }

        if(this.isInsertingEmpty) {
            throw new IllegalStateException
                ("Previous call to prepareInsertEmpty() without corresponding call to endInsertEmpty()!");
        }

        if (inReplacePixelsNest) {
            throw new IllegalStateException
                ("In nested call to prepareReplacePixels!");
        }

        this.isWritingEmpty = false;
    }

    // ----- END insert/writeEmpty methods -----

    // ----- BEGIN replacePixels methods -----

    private TIFFIFD readIFD(int imageIndex) throws IOException {
 	if (stream == null) {
 	    throw new IllegalStateException("Output not set!");
 	}
 	if (imageIndex < 0) {
 	    throw new IndexOutOfBoundsException("imageIndex < 0!");
 	}

        stream.mark();
        long[] ifdpos = new long[1];
        long[] ifd = new long[1];
        locateIFD(imageIndex, ifdpos, ifd);
        if (ifd[0] == 0) {
            stream.reset();
            throw new IndexOutOfBoundsException
                ("imageIndex out of bounds!");
        }

        List tagSets = new ArrayList(1);
        tagSets.add(BaselineTIFFTagSet.getInstance());
        TIFFIFD rootIFD = new TIFFIFD(tagSets);
        // XXX Ignore unknown fields in metadata presumably because
        // any fields needed to write pixels would be known?
        rootIFD.initialize(stream, true);
        stream.reset();

        return rootIFD;
    }

    public boolean canReplacePixels(int imageIndex) throws IOException {
        if (getOutput() == null) {
            throw new IllegalStateException("getOutput() == null!");
        }

        TIFFIFD rootIFD = readIFD(imageIndex);
        TIFFField f = rootIFD.getTIFFField(BaselineTIFFTagSet.TAG_COMPRESSION);
        int compression = f.getAsInt(0);

        return compression == BaselineTIFFTagSet.COMPRESSION_NONE;
    }

    private Object replacePixelsLock = new Object();

    private int replacePixelsIndex = -1;
    private TIFFImageMetadata replacePixelsMetadata = null;
    private long[] replacePixelsTileOffsets = null;
    private long[] replacePixelsByteCounts = null;
    private long replacePixelsOffsetsPosition = 0L;
    private long replacePixelsByteCountsPosition = 0L;
    private Rectangle replacePixelsRegion = null;
    private boolean inReplacePixelsNest = false;

    private TIFFImageReader reader = null;

    public void prepareReplacePixels(int imageIndex,
                                     Rectangle region) throws IOException {
        synchronized(replacePixelsLock) {
            // Check state and parameters vis-a-vis ImageWriter specification.
            if (stream == null) {
                throw new IllegalStateException("Output not set!");
            }
            if (region == null) {
                throw new IllegalArgumentException("region == null!");
            }
            if (region.getWidth() < 1) {
                throw new IllegalArgumentException("region.getWidth() < 1!");
            }
            if (region.getHeight() < 1) {
                throw new IllegalArgumentException("region.getHeight() < 1!");
            }
            if (inReplacePixelsNest) {
                throw new IllegalStateException
                    ("In nested call to prepareReplacePixels!");
            }

            // Read the IFD for the pixel replacement index.
            TIFFIFD replacePixelsIFD = readIFD(imageIndex);

            // Ensure that compression is "none".
            TIFFField f =
                replacePixelsIFD.getTIFFField(BaselineTIFFTagSet.TAG_COMPRESSION);
            int compression = f.getAsInt(0);
            if (compression != BaselineTIFFTagSet.COMPRESSION_NONE) {
                throw new UnsupportedOperationException
                    ("canReplacePixels(imageIndex) == false!");
            }

            // Get the image dimensions.
            f =
                replacePixelsIFD.getTIFFField(BaselineTIFFTagSet.TAG_IMAGE_WIDTH);
            if(f == null) {
                throw new IIOException("Cannot read ImageWidth field.");
            }
            int w = f.getAsInt(0);

            f =
                replacePixelsIFD.getTIFFField(BaselineTIFFTagSet.TAG_IMAGE_LENGTH);
            if(f == null) {
                throw new IIOException("Cannot read ImageHeight field.");
            }
            int h = f.getAsInt(0);

            // Create image bounds.
            Rectangle bounds = new Rectangle(0, 0, w, h);

            // Intersect region with bounds.
            region = region.intersection(bounds);

            // Check for empty intersection.
            if(region.isEmpty()) {
                throw new IIOException("Region does not intersect image bounds");
            }

            // Save the region.
            replacePixelsRegion = region;

            // Get the tile offsets.
            f = replacePixelsIFD.getTIFFField(BaselineTIFFTagSet.TAG_TILE_OFFSETS);
            if(f == null) {
                f = replacePixelsIFD.getTIFFField(BaselineTIFFTagSet.TAG_STRIP_OFFSETS);
            }
            replacePixelsTileOffsets = f.getAsLongs();

            // Get the byte counts.
            f = replacePixelsIFD.getTIFFField(BaselineTIFFTagSet.TAG_TILE_BYTE_COUNTS);
            if(f == null) {
                f = replacePixelsIFD.getTIFFField(BaselineTIFFTagSet.TAG_STRIP_BYTE_COUNTS);
            }
            replacePixelsByteCounts = f.getAsLongs();

            replacePixelsOffsetsPosition =
                replacePixelsIFD.getStripOrTileOffsetsPosition();
            replacePixelsByteCountsPosition =
                replacePixelsIFD.getStripOrTileByteCountsPosition();

            // Get the image metadata.
            replacePixelsMetadata = new TIFFImageMetadata(replacePixelsIFD);

            // Save the image index.
            replacePixelsIndex = imageIndex;

            // Set the pixel replacement flag.
            inReplacePixelsNest = true;
        }
    }

    private Raster subsample(Raster raster, int[] sourceBands,
                             int subOriginX, int subOriginY,
                             int subPeriodX, int subPeriodY,
                             int dstOffsetX, int dstOffsetY,
                             Rectangle target) {

        int x = raster.getMinX();
        int y = raster.getMinY();
        int w = raster.getWidth();
        int h = raster.getHeight();
        int b = raster.getSampleModel().getNumBands();
        int t = raster.getSampleModel().getDataType();

        int outMinX = XToTileX(x, subOriginX, subPeriodX) + dstOffsetX;
        int outMinY = YToTileY(y, subOriginY, subPeriodY) + dstOffsetY;
        int outMaxX = XToTileX(x + w - 1, subOriginX, subPeriodX) + dstOffsetX;
        int outMaxY = YToTileY(y + h - 1, subOriginY, subPeriodY) + dstOffsetY;
        int outWidth = outMaxX - outMinX + 1;
        int outHeight = outMaxY - outMinY + 1;

        if(outWidth <= 0 || outHeight <= 0) return null;

        int inMinX = (outMinX - dstOffsetX)*subPeriodX + subOriginX;
        int inMaxX = (outMaxX - dstOffsetX)*subPeriodX + subOriginX;
        int inWidth = inMaxX - inMinX + 1;
        int inMinY = (outMinY - dstOffsetY)*subPeriodY + subOriginY;
        int inMaxY = (outMaxY - dstOffsetY)*subPeriodY + subOriginY;
        int inHeight = inMaxY - inMinY + 1;

        WritableRaster wr =
            raster.createCompatibleWritableRaster(outMinX, outMinY,
                                                  outWidth, outHeight);

        int jMax = inMinY + inHeight;

        if(t == DataBuffer.TYPE_FLOAT || t == DataBuffer.TYPE_DOUBLE) {
            float[] fsamples = new float[inWidth];
            float[] fsubsamples = new float[outWidth];

            for(int k = 0; k < b; k++) {
                int outY = outMinY;
                for(int j = inMinY; j < jMax; j += subPeriodY) {
                    raster.getSamples(inMinX, j, inWidth, 1, k, fsamples);
                    int s = 0;
                    for(int i = 0; i < inWidth; i += subPeriodX) {
                        fsubsamples[s++] = fsamples[i];
                    }
                    wr.setSamples(outMinX, outY++, outWidth, 1, k,
                                  fsubsamples);
                }
            }
        } else {
            int[] samples = new int[inWidth];
            int[] subsamples = new int[outWidth];

            for(int k = 0; k < b; k++) {
                int outY = outMinY;
                for(int j = inMinY; j < jMax; j += subPeriodY) {
                    raster.getSamples(inMinX, j, inWidth, 1, k, samples);
                    int s = 0;
                    for(int i = 0; i < inWidth; i += subPeriodX) {
                        subsamples[s++] = samples[i];
                    }
                    wr.setSamples(outMinX, outY++, outWidth, 1, k,
                                  subsamples);
                }
            }
        }

        return wr.createChild(outMinX, outMinY,
                              target.width, target.height,
                              target.x, target.y,
                              sourceBands);
    }

    public void replacePixels(RenderedImage image, ImageWriteParam param) 
        throws IOException {

        synchronized(replacePixelsLock) {
            // Check state and parameters vis-a-vis ImageWriter specification.
            if (stream == null) {
                throw new IllegalStateException("stream == null!");
            }

            if (image == null) {
                throw new IllegalArgumentException("image == null!");
            }

            if (!inReplacePixelsNest) {
                throw new IllegalStateException
                    ("No previous call to prepareReplacePixels!");
            }

            // Subsampling values.
            int stepX = 1, stepY = 1, gridX = 0, gridY = 0;

            // Initialize the ImageWriteParam.
            if (param == null) {
                // Use the default.
                param = getDefaultWriteParam();
            } else {
                // Make a copy of the ImageWriteParam.
                ImageWriteParam paramCopy = getDefaultWriteParam();

                // Force uncompressed.
                paramCopy.setCompressionMode(ImageWriteParam.MODE_DISABLED);

                // Force tiling to remain as in the already written image.
                paramCopy.setTilingMode(ImageWriteParam.MODE_COPY_FROM_METADATA);

                // Retain source and destination region and band settings.
                paramCopy.setDestinationOffset(param.getDestinationOffset());
                paramCopy.setSourceBands(param.getSourceBands());
                paramCopy.setSourceRegion(param.getSourceRegion());

                // Save original subsampling values for subsampling the
                // replacement data - not the data re-read from the image.
                stepX = param.getSourceXSubsampling();
                stepY = param.getSourceYSubsampling();
                gridX = param.getSubsamplingXOffset();
                gridY = param.getSubsamplingYOffset();

                // Replace the param.
                param = paramCopy;
            }

            // Check band count and bit depth compatibility.
            TIFFField f =
                replacePixelsMetadata.getTIFFField(BaselineTIFFTagSet.TAG_BITS_PER_SAMPLE);
            if(f == null) {
                throw new IIOException
                    ("Cannot read destination BitsPerSample");
            }
            int[] dstBitsPerSample = f.getAsInts();
            int[] srcBitsPerSample = image.getSampleModel().getSampleSize();
            int[] sourceBands = param.getSourceBands();
            if(sourceBands != null) {
                if(sourceBands.length != dstBitsPerSample.length) {
                    throw new IIOException
                        ("Source and destination have different SamplesPerPixel");
                }
                for(int i = 0; i < sourceBands.length; i++) {
                    if(dstBitsPerSample[i] !=
                       srcBitsPerSample[sourceBands[i]]) {
                        throw new IIOException
                            ("Source and destination have different BitsPerSample");
                    }
                }
            } else {
                int srcNumBands = image.getSampleModel().getNumBands();
                if(srcNumBands != dstBitsPerSample.length) {
                    throw new IIOException
                        ("Source and destination have different SamplesPerPixel");
                }
                for(int i = 0; i < srcNumBands; i++) {
                    if(dstBitsPerSample[i] != srcBitsPerSample[i]) {
                        throw new IIOException
                            ("Source and destination have different BitsPerSample");
                    }
                }
            }

            // Get the source image bounds.
            Rectangle srcImageBounds =
                new Rectangle(image.getMinX(), image.getMinY(),
                              image.getWidth(), image.getHeight());

            // Initialize the source rect.
            Rectangle srcRect = param.getSourceRegion();
            if(srcRect == null) {
                srcRect = srcImageBounds;
            }

            // Set subsampling grid parameters.
            int subPeriodX = stepX;
            int subPeriodY = stepY;
            int subOriginX = gridX + srcRect.x;
            int subOriginY = gridY + srcRect.y;

            // Intersect with the source bounds.
            if(!srcRect.equals(srcImageBounds)) {
                srcRect = srcRect.intersection(srcImageBounds);
                if(srcRect.isEmpty()) {
                    throw new IllegalArgumentException
                        ("Source region does not intersect source image!");
                }
            }

            // Get the destination offset.
            Point dstOffset = param.getDestinationOffset();

            // Forward map source rectangle to determine destination width.
            int dMinX = XToTileX(srcRect.x, subOriginX, subPeriodX) +
                dstOffset.x;
            int dMinY = YToTileY(srcRect.y, subOriginY, subPeriodY) +
                dstOffset.y;
            int dMaxX = XToTileX(srcRect.x + srcRect.width,
                                 subOriginX, subPeriodX) + dstOffset.x;
            int dMaxY = YToTileY(srcRect.y + srcRect.height,
                                 subOriginY, subPeriodY) + dstOffset.y;

            // Initialize the destination rectangle.
            Rectangle dstRect =
                new Rectangle(dstOffset.x, dstOffset.y,
                              dMaxX - dMinX, dMaxY - dMinY);

            // Intersect with the replacement region.
            dstRect = dstRect.intersection(replacePixelsRegion);
            if(dstRect.isEmpty()) {
                throw new IllegalArgumentException
                    ("Forward mapped source region does not intersect destination region!");
            }

            // Backward map to the active source region.
            int activeSrcMinX = (dstRect.x - dstOffset.x)*subPeriodX +
                subOriginX;
            int sxmax = 
                (dstRect.x + dstRect.width - 1 - dstOffset.x)*subPeriodX +
                subOriginX;
            int activeSrcWidth = sxmax - activeSrcMinX + 1;
        
            int activeSrcMinY = (dstRect.y - dstOffset.y)*subPeriodY +
                subOriginY;
            int symax =
                (dstRect.y + dstRect.height - 1 - dstOffset.y)*subPeriodY +
                subOriginY;
            int activeSrcHeight = symax - activeSrcMinY + 1;
            Rectangle activeSrcRect =
                new Rectangle(activeSrcMinX, activeSrcMinY,
                              activeSrcWidth, activeSrcHeight);
            if(activeSrcRect.intersection(srcImageBounds).isEmpty()) {
                throw new IllegalArgumentException
                    ("Backward mapped destination region does not intersect source image!");
            }

            if(reader == null) {
                reader = new TIFFImageReader(new TIFFImageReaderSpi());
            } else {
                reader.reset();
            }

            stream.mark();

            try {
                stream.seek(headerPosition);
                reader.setInput(stream);

                this.imageMetadata = replacePixelsMetadata;
                this.param = param;
                SampleModel sm = image.getSampleModel();
                ColorModel cm = image.getColorModel();
                this.numBands = sm.getNumBands();
                this.imageType = new ImageTypeSpecifier(image);
                this.periodX = param.getSourceXSubsampling();
                this.periodY = param.getSourceYSubsampling();
                this.sourceBands = null;
                int[] sBands = param.getSourceBands();
                if (sBands != null) {
                    this.sourceBands = sBands;
                    this.numBands = sourceBands.length;
                }
                setupMetadata(cm, sm,
                              reader.getWidth(replacePixelsIndex),
                              reader.getHeight(replacePixelsIndex));
                int[] scaleSampleSize = sm.getSampleSize();
                initializeScaleTables(scaleSampleSize);

                // Determine whether bilevel.
                this.isBilevel = ImageUtil.isBinary(image.getSampleModel());

                // Check for photometric inversion.
                this.isInverted =
                    (nativePhotometricInterpretation ==
                     BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO &&
                     photometricInterpretation ==
                     BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO) ||
                    (nativePhotometricInterpretation ==
                     BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO &&
                     photometricInterpretation ==
                     BaselineTIFFTagSet.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO);

                // Analyze image data suitability for direct copy.
                this.isImageSimple = 
                    (isBilevel ||
                     (!isInverted && ImageUtil.imageIsContiguous(image))) &&
                    !isRescaling &&                 // no value rescaling
                    sourceBands == null &&          // no subbanding
                    periodX == 1 && periodY == 1 && // no subsampling
                    colorConverter == null;

                int minTileX = XToTileX(dstRect.x, 0, tileWidth);
                int minTileY = YToTileY(dstRect.y, 0, tileLength);
                int maxTileX = XToTileX(dstRect.x + dstRect.width - 1,
                                        0, tileWidth);
                int maxTileY = YToTileY(dstRect.y + dstRect.height - 1,
                                        0, tileLength);

                TIFFCompressor encoder = new TIFFNullCompressor();
                encoder.setWriter(this);
                encoder.setStream(stream);
                encoder.setMetadata(this.imageMetadata);

                Rectangle tileRect = new Rectangle();
                for(int ty = minTileY; ty <= maxTileY; ty++) {
                    for(int tx = minTileX; tx <= maxTileX; tx++) {
                        int tileIndex = ty*tilesAcross + tx;
                        boolean isEmpty =
                            replacePixelsByteCounts[tileIndex] == 0L;
                        WritableRaster raster;
                        if(isEmpty) {
                            SampleModel tileSM =
                                sm.createCompatibleSampleModel(tileWidth,
                                                               tileLength);
                            raster = Raster.createWritableRaster(tileSM, null);
                        } else {
                            BufferedImage tileImage =
                                reader.readTile(replacePixelsIndex, tx, ty);
                            raster = tileImage.getRaster();
                        }

                        tileRect.setLocation(tx*tileWidth,
                                             ty*tileLength);
                        tileRect.setSize(raster.getWidth(),
                                         raster.getHeight());
                        raster =
                            raster.createWritableTranslatedChild(tileRect.x,
                                                                 tileRect.y);

                        Rectangle replacementRect =
                            tileRect.intersection(dstRect);

                        int srcMinX =
                            (replacementRect.x - dstOffset.x)*subPeriodX +
                            subOriginX;
                        int srcXmax = 
                            (replacementRect.x + replacementRect.width - 1 -
                             dstOffset.x)*subPeriodX + subOriginX;
                        int srcWidth = srcXmax - srcMinX + 1;
        
                        int srcMinY =
                            (replacementRect.y - dstOffset.y)*subPeriodY +
                            subOriginY;
                        int srcYMax =
                            (replacementRect.y + replacementRect.height - 1 -
                             dstOffset.y)*subPeriodY + subOriginY;
                        int srcHeight = srcYMax - srcMinY + 1;
                        Rectangle srcTileRect =
                            new Rectangle(srcMinX, srcMinY,
                                          srcWidth, srcHeight);

                        Raster replacementData = image.getData(srcTileRect);
                        if(subPeriodX == 1 && subPeriodY == 1 &&
                           subOriginX == 0 && subOriginY == 0) {
                            replacementData =
                                replacementData.createChild(srcTileRect.x,
                                                            srcTileRect.y,
                                                            srcTileRect.width,
                                                            srcTileRect.height,
                                                            replacementRect.x,
                                                            replacementRect.y,
                                                            sourceBands);
                        } else {
                            replacementData = subsample(replacementData,
                                                        sourceBands,
                                                        subOriginX,
                                                        subOriginY,
                                                        subPeriodX,
                                                        subPeriodY,
                                                        dstOffset.x,
                                                        dstOffset.y,
                                                        replacementRect);
                            if(replacementData == null) {
                                continue;
                            }
                        }

                        raster.setRect(replacementData);

                        if(isEmpty) {
                            stream.seek(nextSpace);
                        } else {
                            stream.seek(replacePixelsTileOffsets[tileIndex]);
                        }

                        this.image = new SingleTileRenderedImage(raster, cm);

                        int numBytes = writeTile(tileRect, encoder);

                        if(isEmpty) {
                            // Update Strip/TileOffsets and
                            // Strip/TileByteCounts fields.
                            stream.mark();
                            stream.seek(replacePixelsOffsetsPosition +
                                        4*tileIndex);
                            stream.writeInt((int)nextSpace);
                            stream.seek(replacePixelsByteCountsPosition +
                                        4*tileIndex);
                            stream.writeInt(numBytes);
                            stream.reset();

                            // Increment location of next available space.
                            nextSpace += numBytes;
                        }
                    }
                }

            } catch(IOException e) {
                throw e;
            } finally {
                stream.reset();
            }
        }
    }

    public void replacePixels(Raster raster, ImageWriteParam param)
        throws IOException {
        if (raster == null) {
            throw new IllegalArgumentException("raster == null!");
        }

        replacePixels(new SingleTileRenderedImage(raster,
                                                  image.getColorModel()),
                      param);
    }

    public void endReplacePixels() throws IOException {
        synchronized(replacePixelsLock) {
            if(!this.inReplacePixelsNest) {
                throw new IllegalStateException
                    ("No previous call to prepareReplacePixels()!");
            }
            replacePixelsIndex = -1;
            replacePixelsMetadata = null;
            replacePixelsTileOffsets = null;
            replacePixelsByteCounts = null;
            replacePixelsOffsetsPosition = 0L;
            replacePixelsByteCountsPosition = 0L;
            replacePixelsRegion = null;
            inReplacePixelsNest = false;
        }
    }

    // ----- END replacePixels methods -----

    public void reset() {
        super.reset();

        stream = null;
        image = null;
        imageType = null;
        byteOrder = null;
        param = null;
        compressor = null;
        colorConverter = null;
        streamMetadata = null;
        imageMetadata = null;

        isWritingSequence = false;
        isWritingEmpty = false;
        isInsertingEmpty = false;

        replacePixelsIndex = -1;
        replacePixelsMetadata = null;
        replacePixelsTileOffsets = null;
        replacePixelsByteCounts = null;
        replacePixelsOffsetsPosition = 0L;
        replacePixelsByteCountsPosition = 0L;
        replacePixelsRegion = null;
        inReplacePixelsNest = false;
    }
    
    public void dispose() {
	reset();
	super.dispose();
    }
}

class EmptyImage extends SimpleRenderedImage {
    EmptyImage(int minX, int minY, int width, int height,
               int tileGridXOffset, int tileGridYOffset,
               int tileWidth, int tileHeight,
               SampleModel sampleModel, ColorModel colorModel) {
        this.minX = minX;
        this.minY = minY;
        this.width = width;
        this.height = height;
        this.tileGridXOffset = tileGridXOffset;
        this.tileGridYOffset = tileGridYOffset;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.sampleModel = sampleModel;
        this.colorModel = colorModel;
    }

    public Raster getTile(int tileX, int tileY) {
        return null;
    }
}
