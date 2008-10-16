/*
 * $RCSfile: CLibJPEGImageWriter.java,v $
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
 * $Revision: 1.5 $
 * $Date: 2006/04/26 01:14:14 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.PackedColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import com.sun.media.imageioimpl.common.ImageUtil;
import com.sun.media.imageioimpl.plugins.clib.CLibImageWriter;
import com.sun.media.imageioimpl.plugins.clib.OutputStreamAdapter;
import com.sun.medialib.codec.jpeg.Encoder;
import com.sun.medialib.codec.jiio.Constants;
import com.sun.medialib.codec.jiio.mediaLibImage;

final class CLibJPEGImageWriter extends CLibImageWriter {
    private Encoder encoder;

    /**
     * Convert an IndexColorModel-based image to 3-band component RGB.
     *
     * @param im The source image.
     * @throws IllegalArgumentException if the parameter is <code>null</code>.
     * @throws IllegalArgumentException if the source does is not indexed.
     */
    private static BufferedImage convertTo3BandRGB(RenderedImage im) {
        // Check parameter.
        if(im == null) {
            throw new IllegalArgumentException("im == null");
        }

        ColorModel cm = im.getColorModel();
        if(!(cm instanceof IndexColorModel)) {
            throw new IllegalArgumentException
                ("!(im.getColorModel() instanceof IndexColorModel)");
        }

        Raster src;
        if(im.getNumXTiles() == 1 && im.getNumYTiles() == 1) {
            // Image is not tiled so just get a reference to the tile.
            src = im.getTile(im.getMinTileX(), im.getMinTileY());

            if (src.getWidth() != im.getWidth() ||
                src.getHeight() != im.getHeight()) {
                src = src.createChild(src.getMinX(), src.getMinY(),
                                      im.getWidth(), im.getHeight(),
                                      src.getMinX(), src.getMinY(),
                                      null);
            }
        } else {
            // Image is tiled so need to get a contiguous raster.
            src = im.getData();
        }

        // This is probably not the most efficient approach given that
        // the mediaLibImage will eventually need to be in component form.
        BufferedImage dst =
            ((IndexColorModel)cm).convertToIntDiscrete(src, false);

        if(dst.getSampleModel().getNumBands() == 4) {
            //
            // Without copying data create a BufferedImage which has
            // only the RGB bands, not the alpha band.
            //
            WritableRaster rgbaRas = dst.getRaster();
            WritableRaster rgbRas =
                rgbaRas.createWritableChild(0, 0,
                                            dst.getWidth(), dst.getHeight(),
                                            0, 0,
                                            new int[] {0, 1, 2});
            PackedColorModel pcm = (PackedColorModel)dst.getColorModel();
            int bits =
                pcm.getComponentSize(0) +
                pcm.getComponentSize(1) +
                pcm.getComponentSize(2);
            DirectColorModel dcm = new DirectColorModel(bits,
                                                        pcm.getMask(0),
                                                        pcm.getMask(1),
                                                        pcm.getMask(2));
            dst = new BufferedImage(dcm, rgbRas, false, null);
        }

        return dst;
    }

    CLibJPEGImageWriter(ImageWriterSpi originatingProvider)
        throws IOException {
        super(originatingProvider);

        try {
            encoder = new Encoder();
            encoder.setExtend(Encoder.JPEG_IMAGE_NONEXTENDED);
        } catch(Throwable t) {
            throw new IIOException("codecLib error", t);
        }
    }

    public ImageWriteParam getDefaultWriteParam() {
        return new CLibJPEGImageWriteParam(getLocale());
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

        if(renderedImage.getColorModel() instanceof IndexColorModel) {
            renderedImage = convertTo3BandRGB(renderedImage);
        }

        // Test for all.
	ImageUtil.canEncodeImage(this, renderedImage.getColorModel(),
                                 renderedImage.getSampleModel());

        // Test for baseline.
        int bitDepth = renderedImage.getColorModel().getComponentSize(0);
        if((param == null ||
            (param.getCompressionMode() == ImageWriteParam.MODE_EXPLICIT &&
             !param.isCompressionLossless())) &&
           bitDepth > 12) {
            throw new IIOException
                ("JPEG baseline encoding is limited to 12 bits: "+this);
        }

        // Set compression mode and quality from ImageWriteParam, if any.
        if(param != null &&
           param.getCompressionMode() == ImageWriteParam.MODE_EXPLICIT) {
            if(param.isCompressionLossless()) {
                try {
                    if(bitDepth >= 2 && bitDepth <= 16 && bitDepth % 8 != 0) {
                        encoder.setDepth(bitDepth);
                    }
                    if(param.getCompressionType().equalsIgnoreCase
                       (CLibJPEGImageWriteParam.LOSSLESS_COMPRESSION_TYPE)) {
                        encoder.setMode(Encoder.JPEG_MODE_LOSSLESS);
                    } else {
                        encoder.setMode(Encoder.JPEG_MODE_HPLOCO);
                    }
                } catch(Throwable t) {
                    throw new IIOException("codecLib error", t);
                }
            } else {
                try {
                    encoder.setMode(Encoder.JPEG_MODE_BASELINE);
                    // XXX Q == 100 caused a core dump during testing.
                    encoder.setQuality((int)(param.getCompressionQuality()*100));
                } catch(Throwable t) {
                    throw new IIOException("codecLib error", t);
                }
            }
        } else {
            try {
                encoder.setMode(Encoder.JPEG_MODE_BASELINE);
                encoder.setQuality(75);
            } catch(Throwable t) {
                throw new IIOException("codecLib error", t);
            }
        }

        int[] supportedFormats =
            param == null ||
            (param.getCompressionMode() == ImageWriteParam.MODE_EXPLICIT &&
             !param.isCompressionLossless()) ?
            new int [] {Constants.MLIB_FORMAT_GRAYSCALE,
                        Constants.MLIB_FORMAT_GRAYSCALE_ALPHA,
                        Constants.MLIB_FORMAT_BGR,
                        Constants.MLIB_FORMAT_RGB,
                        Constants.MLIB_FORMAT_CMYK  } : // baseline
            new int [] {Constants.MLIB_FORMAT_GRAYSCALE,
                        Constants.MLIB_FORMAT_RGB};  // lossless & LS
        mediaLibImage mlibImage = getMediaLibImage(renderedImage,
                                                   param,
                                                   false,
                                                   supportedFormats);

        try {
            if(mlibImage.getChannels() == 2) {
                // GRAYSCALE_ALPHA
                encoder.setType(Encoder.JPEG_TYPE_GRAYSCALE);
            } else if(mlibImage.getChannels() == 4) {
                // XXX The selection of CMYK (Adobe transform 0) or
                // YCCK (Adobe transform 2) should probably be made
                // on the basis of image metadata passed in so this
                // code should be modified once the writer supports
                // image metadata. Until then select CMYK type which
                // will generate Adobe transform 0 and non-subsampled
                // data.
                if(mlibImage.getFormat() == Constants.MLIB_FORMAT_CMYK) {
                    // CMYK
                    encoder.setType(Encoder.JPEG_TYPE_CMYK);
                } else if(mlibImage.getFormat() ==
                          Constants.MLIB_FORMAT_YCCK) {
                    // YCCK
                    encoder.setType(Encoder.JPEG_TYPE_YCCK);
                }
            }
            encoder.encode(stream, mlibImage);
        } catch(Throwable t) {
            throw new IIOException("codecLib error", t);
        }
    }
}

/**
 * This differs from the core JPEG ImageWriteParam in that:
 *
 * <ul>
 * <li>compression types are: "JPEG" (standard), "JPEG-LOSSLESS"
 * (lossless JPEG from 10918-1/ITU-T81), "JPEG-LS" (ISO 14495-1 lossless).</li>
 * <li>compression modes are: MODE_DEFAULT and MODE_EXPLICIT and the
 * other modes (MODE_DISABLED and MODE_COPY_FROM_METADATA) cause
 * an UnsupportedOperationException.</li>
 * <li>isCompressionLossless() will return true if type is NOT "JPEG".</li>
 * </ul>
 */
final class CLibJPEGImageWriteParam extends ImageWriteParam {
    private static final float DEFAULT_COMPRESSION_QUALITY = 0.75F;

    static final String LOSSY_COMPRESSION_TYPE = "JPEG";
    static final String LOSSLESS_COMPRESSION_TYPE = "JPEG-LOSSLESS";
    static final String LS_COMPRESSION_TYPE = "JPEG-LS";

    private static final String[] compressionQualityDescriptions =
        new String[] {
            I18N.getString("CLibJPEGImageWriteParam0"),
            I18N.getString("CLibJPEGImageWriteParam1"),
            I18N.getString("CLibJPEGImageWriteParam2")
        };

    CLibJPEGImageWriteParam(Locale locale) {
        super(locale);

        canWriteCompressed = true;
        compressionMode = MODE_EXPLICIT;
        compressionQuality = DEFAULT_COMPRESSION_QUALITY;
        compressionType = LOSSY_COMPRESSION_TYPE;
        compressionTypes = new String[] {LOSSY_COMPRESSION_TYPE,
                                         LOSSLESS_COMPRESSION_TYPE,
                                         LS_COMPRESSION_TYPE};
    }

    public String[] getCompressionQualityDescriptions() {
        super.getCompressionQualityDescriptions(); // Performs checks.

        return compressionQualityDescriptions;
    }

    public float[] getCompressionQualityValues() {
        super.getCompressionQualityValues(); // Performs checks.

        return new float[] { 0.05F,   // "Minimum useful"
                             0.75F,   // "Visually lossless"
                             0.95F }; // "Maximum useful"
    }

    public boolean isCompressionLossless() {
        super.isCompressionLossless(); // Performs checks.

        return !compressionType.equalsIgnoreCase(LOSSY_COMPRESSION_TYPE);
    }

    public void setCompressionMode(int mode) {
        if(mode == MODE_DISABLED ||
           mode == MODE_COPY_FROM_METADATA) {
            throw new UnsupportedOperationException
                ("mode == MODE_DISABLED || mode == MODE_COPY_FROM_METADATA");
        }

        super.setCompressionMode(mode); // This sets the instance variable.
    }

    public void unsetCompression() {
        super.unsetCompression(); // Performs checks.

        compressionQuality = DEFAULT_COMPRESSION_QUALITY;
        compressionType = LOSSY_COMPRESSION_TYPE;
    }
}
