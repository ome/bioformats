/*
 * $RCSfile: CLibPNGImageWriter.java,v $
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
 * $Revision: 1.7 $
 * $Date: 2006/11/01 22:37:00 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.png;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import com.sun.media.imageioimpl.common.ImageUtil;
import com.sun.media.imageioimpl.plugins.clib.CLibImageWriter;
import com.sun.media.imageioimpl.plugins.clib.OutputStreamAdapter;
import com.sun.medialib.codec.png.Constants;
import com.sun.medialib.codec.png.Encoder;
import com.sun.medialib.codec.jiio.mediaLibImage;

final class CLibPNGImageWriter extends CLibImageWriter {

    CLibPNGImageWriter(ImageWriterSpi originatingProvider) {
        super(originatingProvider);
    }

    public ImageWriteParam getDefaultWriteParam() {
        return new CLibPNGImageWriteParam(getLocale());
    }

    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType,
                                               ImageWriteParam param) {
        CLibPNGMetadata m = new CLibPNGMetadata();
        if(param != null && param.getDestinationType() != null) {
            imageType = param.getDestinationType();
        }
        if(imageType != null) {
            m.initialize(imageType,
                         imageType.getSampleModel().getNumBands(),
                         param, 0);
        }
        return m;
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

        CLibPNGMetadata outData = null;

        // Obtain a CLibPNGMetadata object.
        if(inData instanceof CLibPNGMetadata) {
            // Clone the input metadata.
            outData = (CLibPNGMetadata)((CLibPNGMetadata)inData).clone();
        } else {
            try {
                outData = new CLibPNGMetadata(inData);
            } catch(IIOInvalidTreeException e) {
                // XXX Warning
                outData = new CLibPNGMetadata();
            }
        }

        // Update the metadata per the image type and param.
        outData.initialize(imageType,
                           imageType.getSampleModel().getNumBands(),
                           param, outData.IHDR_interlaceMethod);

        return outData;
    }

    public void write(IIOMetadata streamMetadata,
                      IIOImage image,
                      ImageWriteParam param) throws IOException {
        if(output == null) {
            throw new IllegalStateException("output == null");
        }

        OutputStream stream = null;
        if(output instanceof ImageOutputStream) {
            stream = new OutputStreamAdapter((ImageOutputStream)output);
        } else {
            throw new IllegalArgumentException
                ("!(output instanceof ImageOutputStream)");
        }

        RenderedImage renderedImage = image.getRenderedImage();
	ImageUtil.canEncodeImage(this, renderedImage.getColorModel(),
                                 renderedImage.getSampleModel());
        int[] supportedFormats = new int[] {
            Constants.MLIB_FORMAT_GRAYSCALE,
            Constants.MLIB_FORMAT_GRAYSCALE_ALPHA,
            Constants.MLIB_FORMAT_INDEXED,
            Constants.MLIB_FORMAT_BGR,
            Constants.MLIB_FORMAT_RGB,
            Constants.MLIB_FORMAT_BGRA,
            Constants.MLIB_FORMAT_RGBA
        };
        mediaLibImage mlImage = getMediaLibImage(renderedImage, param, true,
                                                 supportedFormats);

        Encoder encoder = null;
        try {
            encoder = new Encoder(mlImage);
        } catch(Throwable t) {
            throw new IIOException("codecLib error", t);
        }

        // Set the maximum length of the iDAT chunk.
        encoder.setIDATSize(8192);

        // Determine the image type.
        ImageTypeSpecifier imageType;
        if(param != null && param.getDestinationType() != null) {
            imageType = param.getDestinationType();
        } else if(mlImage.getType() == mediaLibImage.MLIB_BIT) {
            if(renderedImage.getColorModel() instanceof IndexColorModel) {
                imageType =
                    new ImageTypeSpecifier(renderedImage.getColorModel(),
                                           renderedImage.getSampleModel());
            } else {
                int dataType = renderedImage.getSampleModel().getDataType();
                imageType =
                    ImageTypeSpecifier.createGrayscale(1, dataType, false);
            }
        } else if(mlImage.getChannels() ==
                  renderedImage.getSampleModel().getNumBands()) {
            // Note: ImageTypeSpecifier.createFromRenderedImage() gave an
            // incorrect result here for an indexed BufferedImage as the
            // ImageTypeSpecifier generated by createFromBufferedImage()
            // does not match the actual image.
            imageType = new ImageTypeSpecifier(renderedImage);
        } else {
            SampleModel sm = renderedImage.getSampleModel();
            int dataType = sm.getDataType();
            int bitDepth = sm.getSampleSize(0);
            int numBands = mlImage.getChannels();
            switch(numBands) {
            case 1:
                imageType =
                    ImageTypeSpecifier.createGrayscale(bitDepth, dataType,
                                                       false);
                break;
            case 2:
                imageType =
                    ImageTypeSpecifier.createGrayscale(bitDepth, dataType,
                                                       false, false);
                break;
            case 3:
                ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
                imageType =
                    ImageTypeSpecifier.createInterleaved(cs,
                                                         new int[] {0, 1, 2},
                                                         dataType,
                                                         false, false);
                break;
            default:
                throw new IIOException("Cannot encode image with "+
                                       numBands+" bands!");
            }
        }

        // Get metadata.
        IIOMetadata imageMetadata = image.getMetadata();

        if(imageMetadata != null) {
            // Convert metadata.
            imageMetadata =
                convertImageMetadata(imageMetadata, imageType, param);
        } else {
            // Use default.
            imageMetadata = getDefaultImageMetadata(imageType, param);
        }

        // Set metadata on encoder.
        ((CLibPNGMetadata)imageMetadata).writeMetadata(encoder);

        ColorModel colorModel = null;
        if(param != null) {
            ImageTypeSpecifier destinationType = param.getDestinationType();
            if(destinationType != null) {
                colorModel = destinationType.getColorModel();
            }

            // Set compression level to (int)(9*(1.0F - compressionQuality)).
            if(param.getCompressionMode() == ImageWriteParam.MODE_EXPLICIT) {
                try {
                    int compressionLevel =
                        (int)(9*(1.0F - param.getCompressionQuality()));
                    encoder.setCompressionLevel(compressionLevel);
                } catch(Throwable t) {
                    throw new IIOException("codecLib error", t);
                }

                // Set the strategy if appropriate.
                if(param instanceof CLibPNGImageWriteParam) {
                    try {
                        encoder.setStrategy(
                            ((CLibPNGImageWriteParam)param).getStrategy());
                    } catch(Throwable t) {
                        throw new IIOException("codecLib error", t);
                    }
                }
            }
        } else { // null ImageWriteParam
            try {
                // Do not set the compression level: let it default.

                // Z_DEFAULT_STRATEGY
                encoder.setStrategy(0);
            } catch(Throwable t) {
                throw new IIOException("codecLib error", t);
            }
        }

        if(colorModel == null) {
            colorModel = renderedImage.getColorModel();
        }

        // If no iCCP chunk is already in the metadata and the color space
        // is a non-standard ICC color space, the write it to iCCP chunk.
        if(!((CLibPNGMetadata)imageMetadata).iCCP_present &&
           colorModel != null &&
           ImageUtil.isNonStandardICCColorSpace(colorModel.getColorSpace())) {
            // Get the profile data.
            ICC_ColorSpace iccColorSpace =
                (ICC_ColorSpace)colorModel.getColorSpace();
            ICC_Profile iccProfile = iccColorSpace.getProfile();
            byte[] iccProfileData = iccColorSpace.getProfile().getData();

            // Get the profile name.
            byte[] desc =
                iccProfile.getData(ICC_Profile.icSigProfileDescriptionTag);
            String profileName;
            if(desc != null) {
                long len = ((desc[8]&0xff) << 24) | ((desc[9]&0xff) << 16) |
                    ((desc[10]&0xff) << 8) | (desc[11]&0xff);
                profileName = new String(desc, 12, (int)len);
            } else {
                profileName = "ICCProfile";
            }

            // Set the profile on the Encoder.
            profileName = CLibPNGMetadata.toPrintableLatin1(profileName);
            encoder.setEmbeddedICCProfile(profileName, iccProfileData);
        }

        try {
            encoder.encode(stream);
        } catch(Throwable t) {
            throw new IIOException("codecLib error", t);
        }
    }
}

/**
 * This differs from the core PNG ImageWriteParam in that:
 *
 * . 'canWriteCompressed' is set to 'true' so that canWriteCompressed()
 * will return 'true'.
 * . compression types are: "DEFAULT", "FILTERED", and "HUFFMAN_ONLY"
 * and are used to set the encoder strategy to Z_DEFAULT, Z_FILTERED,
 * and Z_HUFFMAN_ONLY as described in the PNG specification.
 * . compression modes are: MODE_DEFAULT, MODE_EXPLICIT and
 * MODE_COPY_FROM_METADATA); MODE_DISABLED is not allowed.
 * . compression quality is used to set the compression level of the
 * encoder according to:
 *
 *     compressionLevel = (int)(9*(1.0F - compressionQuality))
 *
 * As in the core PNG writer, a progressiveMode of MODE_DEFAULT sets
 * Adam7 interlacing whereas MODE_DISABLED sets default interlacing,
 * i.e., none.
 */
final class CLibPNGImageWriteParam extends ImageWriteParam {
    private static final float DEFAULT_COMPRESSION_QUALITY = 1.0F/3.0F;

    // Encoder strategies mapped to compression types.
    private static final String DEFAULT_COMPRESSION_TYPE = "DEFAULT";
    private static final String FILTERED_COMPRESSION_TYPE = "FILTERED";
    private static final String HUFFMAN_COMPRESSION_TYPE = "HUFFMAN_ONLY";

    // Compression descriptions
    private static final String[] compressionQualityDescriptions =
        new String[] {
            I18N.getString("CLibPNGImageWriteParam0"),
            I18N.getString("CLibPNGImageWriteParam1"),
            I18N.getString("CLibPNGImageWriteParam2")
        };

    CLibPNGImageWriteParam(Locale locale) {
        super(locale);

        canWriteCompressed = true;
        canWriteProgressive = true;
        compressionTypes = new String[] {DEFAULT_COMPRESSION_TYPE,
                                         FILTERED_COMPRESSION_TYPE,
                                         HUFFMAN_COMPRESSION_TYPE};

        compressionQuality = DEFAULT_COMPRESSION_QUALITY;
        compressionType    = DEFAULT_COMPRESSION_TYPE;
    }

    int getStrategy() {
        if(compressionType.equals(FILTERED_COMPRESSION_TYPE)) {
            return 1; // Z_FILTERED
        } else if(compressionType.equals(HUFFMAN_COMPRESSION_TYPE)) {
            return 2; // Z_HUFFMAN_ONLY
        } else {
            return 0; // Z_DEFAULT_STRATEGY
        }
    }

    public String[] getCompressionQualityDescriptions() {
        super.getCompressionQualityDescriptions(); // Performs checks.

        return compressionQualityDescriptions;
    }

    public float[] getCompressionQualityValues() {
        super.getCompressionQualityValues(); // Performs checks.

        // According to the java.util.zip.Deflater class, the Deflater
        // level 1 gives the best speed (short of no compression). Since
        // quality is derived from level as
        //
        //     quality = 1 - level/9
        //
        // this gives a value of 8.0/9.0 for the corresponding quality.
        return new float[] { 0.0F,               // "Best Compression"
                             (float)(8.0F/9.0F), // "Best Speed"
                             1.0F };             // "No Compression"
    }

    public void setCompressionMode(int mode) {
        if(mode == MODE_DISABLED) {
            throw new UnsupportedOperationException("mode == MODE_DISABLED");
        }

        super.setCompressionMode(mode); // This sets the instance variable.
    }

    public void unsetCompression() {
        super.unsetCompression(); // Performs checks.

        compressionQuality = DEFAULT_COMPRESSION_QUALITY;
        compressionType    = DEFAULT_COMPRESSION_TYPE;
    }
}
