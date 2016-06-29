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
 * $RCSfile: J2KImageWriter.java,v $
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
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:01:34 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg2000;

import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

import jj2000.j2k.codestream.writer.CodestreamWriter;
import jj2000.j2k.codestream.writer.FileCodestreamWriter;
import jj2000.j2k.codestream.writer.HeaderEncoder;
import jj2000.j2k.entropy.encoder.EntropyCoder;
import jj2000.j2k.entropy.encoder.PostCompRateAllocator;
import jj2000.j2k.fileformat.writer.FileFormatWriter;
import jj2000.j2k.image.ImgDataConverter;
import jj2000.j2k.image.Tiler;
import jj2000.j2k.image.forwcomptransf.ForwCompTransf;
import jj2000.j2k.quantization.quantizer.Quantizer;
import jj2000.j2k.roi.encoder.ROIScaler;
import jj2000.j2k.util.CodestreamManipulator;
import jj2000.j2k.wavelet.analysis.ForwardWT;

import com.sun.media.imageioimpl.common.ImageUtil;
import com.sun.media.imageio.plugins.jpeg2000.J2KImageWriteParam;
import org.w3c.dom.Node;

/**
 * The Java Image IO plugin writer for encoding a RenderedImage into
 * a JPEG 2000 part 1 file (JP2) format.
 *
 * This writer has the capability to (1) Losslessly encode
 * <code>RenderedImage</code>s with an <code>IndexColorModel</code> (for
 * example, bi-level or color indexed images).  (2) Losslessly or lossy encode
 * <code>RenderedImage</code> with a byte, short, ushort or integer types with
 * band number upto 16384.  (3) Encode an image with alpha channel.
 * (4) Write the provided metadata into the code stream.  It also can encode
 * a raster wrapped in the provided <code>IIOImage</code>.
 *
 * The encoding process may re-tile image, clip, subsample, and select bands
 * using the parameters specified in the <code>ImageWriteParam</code>.
 *
 * @see com.sun.media.imageio.plugins.jpeg2000.J2KImageWriteParam
 */
public class J2KImageWriter extends ImageWriter {
    /** Wrapper for the protected method <code>processImageProgress</code>
     *  So it can be access from the classes which are not in
     *  <code>ImageWriter</code> hierachy.
     */
    public void processImageProgressWrapper(float percentageDone) {
        processImageProgress(percentageDone);
    }


    /** When the writing is aborted, <code>RenderedImageSrc</code> throws a
     *  <code>RuntimeException</code>.
     */
    public static String WRITE_ABORTED = "Write aborted.";

    /** The output stream to write into */
    private ImageOutputStream stream = null;

    /** Constructs <code>J2KImageWriter</code> based on the provided
     *  <code>ImageWriterSpi</code>.
     */
    public J2KImageWriter(ImageWriterSpi originator) {
        super(originator);
    }

    public void setOutput(Object output) {
        super.setOutput(output); // validates output
        if (output != null) {
            if (!(output instanceof ImageOutputStream))
                throw new IllegalArgumentException(I18N.getString("J2KImageWriter0"));
            this.stream = (ImageOutputStream)output;
        } else
            this.stream = null;
    }

    public ImageWriteParam getDefaultWriteParam() {
        return new J2KImageWriteParam();
    }

    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
        return null;
    }

    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType,
                                               ImageWriteParam param) {
        return new J2KMetadata(imageType, param, this);
    }

    public IIOMetadata convertStreamMetadata(IIOMetadata inData,
                                             ImageWriteParam param) {
        return null;
    }

    public IIOMetadata convertImageMetadata(IIOMetadata inData,
                                            ImageTypeSpecifier imageType,
                                            ImageWriteParam param) {
        // Check arguments.
        if(inData == null) {
            throw new IllegalArgumentException("inData == null!");
        }
        if(imageType == null) {
            throw new IllegalArgumentException("imageType == null!");
        }

        // If it's one of ours, return a clone.
        if (inData instanceof J2KMetadata) {
            return (IIOMetadata)((J2KMetadata)inData).clone();
        }

        try {
            J2KMetadata outData = new J2KMetadata();

            List formats = Arrays.asList(inData.getMetadataFormatNames());

            String format = null;
            if(formats.contains(J2KMetadata.nativeMetadataFormatName)) {
                // Initialize from native image metadata format.
                format = J2KMetadata.nativeMetadataFormatName;
            } else if(inData.isStandardMetadataFormatSupported()) {
                // Initialize from standard metadata form of the input tree.
                format = IIOMetadataFormatImpl.standardMetadataFormatName;
            }

            if(format != null) {
                outData.setFromTree(format, inData.getAsTree(format));
                return outData;
            }
        } catch(IIOInvalidTreeException e) {
            return null;
        }

        return null;
    }

    public boolean canWriteRasters() {
        return true;
    }

    public void write(IIOMetadata streamMetadata,
                      IIOImage image,
                      ImageWriteParam param) throws IOException {
        if (stream == null) {
            throw new IllegalStateException(I18N.getString("J2KImageWriter7"));
        }
        if (image == null) {
            throw new IllegalArgumentException(I18N.getString("J2KImageWriter8"));
        }

        clearAbortRequest();
        processImageStarted(0);
        RenderedImage input = null;

        boolean writeRaster = image.hasRaster();
        Raster raster = null;

        SampleModel sampleModel = null;
        if (writeRaster) {
            raster = image.getRaster();
            sampleModel = raster.getSampleModel();
        } else {
            input = image.getRenderedImage();
            sampleModel = input.getSampleModel();
        }

        checkSampleModel(sampleModel);
        if (param == null)
            param = getDefaultWriteParam();

        J2KImageWriteParamJava j2kwparam =
            new J2KImageWriteParamJava(image, param);

        // Packet header cannot exist in two places.
        if (j2kwparam.getPackPacketHeaderInTile() &&
            j2kwparam.getPackPacketHeaderInMain())
            throw new IllegalArgumentException(I18N.getString("J2KImageWriter1"));

        // Lossless and encoding rate cannot be set at the same time
        if (j2kwparam.getLossless() &&
            j2kwparam.getEncodingRate()!=Double.MAX_VALUE)
            throw new IllegalArgumentException(I18N.getString("J2KImageWriter2"));

        // If the source image is bilevel or color-indexed, or, the
        // encoding rate is Double.MAX_VALUE, use lossless
        if ((!writeRaster && input.getColorModel() instanceof IndexColorModel) ||
             (writeRaster &&
              raster.getSampleModel() instanceof MultiPixelPackedSampleModel)) {
            j2kwparam.setDecompositionLevel("0");
            j2kwparam.setLossless(true);
            j2kwparam.setEncodingRate(Double.MAX_VALUE);
            j2kwparam.setQuantizationType("reversible");
            j2kwparam.setFilters(J2KImageWriteParam.FILTER_53);
        } else if (j2kwparam.getEncodingRate() == Double.MAX_VALUE) {
            j2kwparam.setLossless(true);
            j2kwparam.setQuantizationType("reversible");
            j2kwparam.setFilters(J2KImageWriteParam.FILTER_53);
        }

        // Gets parameters from the write parameter
        boolean pphTile = j2kwparam.getPackPacketHeaderInTile();
        boolean pphMain = j2kwparam.getPackPacketHeaderInMain();
        boolean tempSop = false;
        boolean tempEph = false;

        int[] bands = param.getSourceBands();
        int ncomp = sampleModel.getNumBands();

        if (bands != null)
            ncomp = bands.length;

        // create the encoding source recognized by jj2000 packages
        RenderedImageSrc imgsrc = null;
        if (writeRaster)
            imgsrc = new RenderedImageSrc(raster, j2kwparam, this);
        else
            imgsrc = new RenderedImageSrc(input, j2kwparam, this);

        // if the components signed
        boolean[] imsigned = new boolean[ncomp];
        if (bands != null) {
            for (int i=0; i<ncomp; i++)
                imsigned[i] = ((RenderedImageSrc)imgsrc).isOrigSigned(bands[i]);
        } else {
            for (int i=0; i<ncomp; i++)
                imsigned[i] = ((RenderedImageSrc)imgsrc).isOrigSigned(i);
        }

        // Gets the tile dimensions
        int tw = j2kwparam.getTileWidth();
        int th = j2kwparam.getTileHeight();

        //Gets the image position
        int refx = j2kwparam.getMinX();
        int refy = j2kwparam.getMinY();
        if (refx < 0 || refy < 0)
            throw new IIOException(I18N.getString("J2KImageWriter3"));

        // Gets tile grid offsets and validates them
        int trefx = j2kwparam.getTileGridXOffset();
        int trefy = j2kwparam.getTileGridYOffset();
        if (trefx < 0 || trefy < 0 || trefx > refx || trefy > refy)
            throw new IIOException(I18N.getString("J2KImageWriter4"));

        // Instantiate tiler
        Tiler imgtiler = new Tiler(imgsrc,refx,refy,trefx,trefy,tw,th);

        // Creates the forward component transform
        ForwCompTransf fctransf = new ForwCompTransf(imgtiler, j2kwparam);

        // Creates ImgDataConverter
        ImgDataConverter converter = new ImgDataConverter(fctransf);

        // Creates ForwardWT (forward wavelet transform)
        ForwardWT dwt = ForwardWT.createInstance(converter, j2kwparam);

        // Creates Quantizer
        Quantizer quant = Quantizer.createInstance(dwt,j2kwparam);

        // Creates ROIScaler
        ROIScaler rois = ROIScaler.createInstance(quant, j2kwparam);

        // Creates EntropyCoder
        EntropyCoder ecoder =
	    EntropyCoder.createInstance(rois, j2kwparam,
		j2kwparam.getCodeBlockSize(),
		j2kwparam.getPrecinctPartition(),
		j2kwparam.getBypass(),
		j2kwparam.getResetMQ(),
		j2kwparam.getTerminateOnByte(),
		j2kwparam.getCausalCXInfo(),
		j2kwparam.getCodeSegSymbol(),
		j2kwparam.getMethodForMQLengthCalc(),
		j2kwparam.getMethodForMQTermination());

        // Rely on rate allocator to limit amount of data
        File tmpFile = File.createTempFile("jiio-", ".tmp");
        tmpFile.deleteOnExit();

        // Creates CodestreamWriter
        FileCodestreamWriter bwriter =
            new FileCodestreamWriter(tmpFile, Integer.MAX_VALUE);

        // Creates the rate allocator
        float rate = (float)j2kwparam.getEncodingRate();
        PostCompRateAllocator ralloc =
            PostCompRateAllocator.createInstance(ecoder,
                                                 rate,
                                                 bwriter,
                                                 j2kwparam);

        // Instantiates the HeaderEncoder
        HeaderEncoder headenc =
            new HeaderEncoder(imgsrc, imsigned, dwt, imgtiler,
                              j2kwparam, rois,ralloc);

        ralloc.setHeaderEncoder(headenc);

        // Writes header to be able to estimate header overhead
        headenc.encodeMainHeader();

        //Initializes rate allocator, with proper header
        // overhead. This will also encode all the data
        try {
            ralloc.initialize();
        } catch (RuntimeException e) {
            if (WRITE_ABORTED.equals(e.getMessage())) {
                bwriter.close();
                tmpFile.delete();
                processWriteAborted();
                return;
            } else throw e;
        }

        // Write header (final)
        headenc.reset();
        headenc.encodeMainHeader();

        // Insert header into the codestream
        bwriter.commitBitstreamHeader(headenc);

        // Now do the rate-allocation and write result
        ralloc.runAndWrite();

        //Done for data encoding
        bwriter.close();

        // Calculate file length
        int fileLength = bwriter.getLength();

        // Tile-parts and packed packet headers
        int pktspertp = j2kwparam.getPacketPerTilePart();
        int ntiles = imgtiler.getNumTiles();
        if (pktspertp>0 || pphTile || pphMain){
            CodestreamManipulator cm =
                new CodestreamManipulator(tmpFile, ntiles, pktspertp,
                                          pphMain, pphTile, tempSop,
                                          tempEph);
            fileLength += cm.doCodestreamManipulation();
        }

        // File Format
        int nc= imgsrc.getNumComps() ;
        int[] bpc = new int[nc];
        for(int comp = 0; comp<nc; comp++)
            bpc[comp]=imgsrc.getNomRangeBits(comp);

        ColorModel colorModel = (input != null) ? input.getColorModel() : null;
        if (bands != null) {
            ImageTypeSpecifier type= param.getDestinationType();
            if (type != null)
                colorModel = type.getColorModel();
            //XXX: other wise should create proper color model based
            // on the selected bands
        }
        if(colorModel == null) {
            colorModel = ImageUtil.createColorModel(sampleModel);
        }

        J2KMetadata metadata = null;

        if (param instanceof J2KImageWriteParam &&
            !((J2KImageWriteParam)param).getWriteCodeStreamOnly()) {
            IIOMetadata inMetadata = image.getMetadata();

            J2KMetadata metadata1 = new J2KMetadata(colorModel,
                                                    sampleModel,
                                                    imgsrc.getImgWidth(),
                                                    imgsrc.getImgHeight(),
                                                    param,
                                                    this);

            if (inMetadata == null) {
                metadata = metadata1;
            } else {
                // Convert the input metadata tree to a J2KMetadata.
                if(colorModel != null) {
                    ImageTypeSpecifier imageType = 
                        new ImageTypeSpecifier(colorModel, sampleModel);
                    metadata =
                        (J2KMetadata)convertImageMetadata(inMetadata,
                                                          imageType,
                                                          param);
                } else {
                    String metaFormat = null;
                    List metaFormats =
                        Arrays.asList(inMetadata.getMetadataFormatNames());
                    if(metaFormats.contains(J2KMetadata.nativeMetadataFormatName)) {
                        // Initialize from native image metadata format.
                        metaFormat = J2KMetadata.nativeMetadataFormatName;
                    } else if(inMetadata.isStandardMetadataFormatSupported()) {
                        // Initialize from standard metadata form of the
                        // input tree.
                        metaFormat = 
                            IIOMetadataFormatImpl.standardMetadataFormatName;
                    }

                    metadata = new J2KMetadata();
                    if(metaFormat != null) {
                        metadata.setFromTree(metaFormat,
                                             inMetadata.getAsTree(metaFormat));
                    }
                }

                metadata.mergeTree(J2KMetadata.nativeMetadataFormatName,
                                   metadata1.getAsTree(J2KMetadata.nativeMetadataFormatName));
            }
        }

        FileFormatWriter ffw =
            new FileFormatWriter(tmpFile, stream,
                                 imgsrc.getImgHeight(),
                                 imgsrc.getImgWidth(), nc, bpc,
                                 fileLength,
                                 colorModel,
                                 sampleModel,
                                 metadata);
        fileLength += ffw.writeFileFormat();
        tmpFile.delete();

        processImageComplete();
    }

    public synchronized void abort() {
        super.abort();
    }

    public void reset() {
        // reset local Java structures
        super.reset();
        stream = null;
    }

    /** This method wraps the protected method <code>abortRequested</code>
     *  to allow the abortions be monitored by <code>J2KRenderedImage</code>.
     */
    public boolean getAbortRequest() {
        return abortRequested();
    }

    private void checkSampleModel(SampleModel sm) {
        int type = sm.getDataType();

        if (type < DataBuffer.TYPE_BYTE || type > DataBuffer.TYPE_INT)
            throw new IllegalArgumentException(I18N.getString("J2KImageWriter5"));
        if (sm.getNumBands() > 16384)
            throw new IllegalArgumentException(I18N.getString("J2KImageWriter6"));
    }
}
