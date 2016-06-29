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
 * $RCSfile: WBMPImageWriter.java,v $
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
 * $Date: 2005/02/11 05:01:52 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.wbmp;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import java.io.IOException;

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

/**
 * The Java Image IO plugin writer for encoding a binary RenderedImage into
 * a WBMP format.
 *
 * The encoding process may clip, subsample using the parameters
 * specified in the <code>ImageWriteParam</code>.
 *
 * @see ImageWriteParam
 */
public class WBMPImageWriter extends ImageWriter {
    /** The output stream to write into */
    private ImageOutputStream stream = null;

    // Get the number of bits required to represent an int.
    private static int getNumBits(int intValue) {
        int numBits = 32;
        int mask = 0x80000000;
        while(mask != 0 && (intValue & mask) == 0) {
            numBits--;
            mask >>>= 1;
        }
        return numBits;
    }

    // Convert an int value to WBMP multi-byte format.
    private static byte[] intToMultiByte(int intValue) {
        int numBitsLeft = getNumBits(intValue);
        byte[] multiBytes = new byte[(numBitsLeft + 6)/7];

        int maxIndex = multiBytes.length - 1;
        for(int b = 0; b <= maxIndex; b++) {
            multiBytes[b] = (byte)((intValue >>> ((maxIndex - b)*7))&0x7f);
            if(b != maxIndex) {
                multiBytes[b] |= (byte)0x80;
            }
        }

        return multiBytes;
    }

    /** Constructs <code>WBMPImageWriter</code> based on the provided
     *  <code>ImageWriterSpi</code>.
     */
    public WBMPImageWriter(ImageWriterSpi originator) {
        super(originator);
    }

    public void setOutput(Object output) {
        super.setOutput(output); // validates output
        if (output != null) {
            if (!(output instanceof ImageOutputStream))
                throw new IllegalArgumentException(I18N.getString("WBMPImageWriter"));
            this.stream = (ImageOutputStream)output;
        } else
            this.stream = null;
    }

    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
        return null;
    }

    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType,
                                               ImageWriteParam param) {
        WBMPMetadata meta = new WBMPMetadata();
	meta.wbmpType = 0; // default wbmp level
	return meta;
    }

    public IIOMetadata convertStreamMetadata(IIOMetadata inData,
                                             ImageWriteParam param) {
        return null;
    }

    public IIOMetadata convertImageMetadata(IIOMetadata metadata,
                                            ImageTypeSpecifier type,
                                            ImageWriteParam param) {
        return null;
    }

    public boolean canWriteRasters() {
        return true;
    }

    public void write(IIOMetadata streamMetadata,
                      IIOImage image,
                      ImageWriteParam param) throws IOException {
        if (stream == null) {
	    throw new IllegalStateException(I18N.getString("WBMPImageWriter3"));
	}
			    
	if (image == null) {
	    throw new IllegalArgumentException(I18N.getString("WBMPImageWriter4"));
	}
							
	clearAbortRequest();
        processImageStarted(0);
        if (param == null)
            param = getDefaultWriteParam();

        RenderedImage input = null;
        Raster inputRaster = null;
        boolean writeRaster = image.hasRaster();
        Rectangle sourceRegion = param.getSourceRegion();
        SampleModel sampleModel = null;

        if (writeRaster) {
            inputRaster = image.getRaster();
            sampleModel = inputRaster.getSampleModel();
        } else {
            input = image.getRenderedImage();
            sampleModel = input.getSampleModel();

            inputRaster = input.getData();
        }

        checkSampleModel(sampleModel);
        if (sourceRegion == null)
            sourceRegion = inputRaster.getBounds();
        else
            sourceRegion = sourceRegion.intersection(inputRaster.getBounds());

        if (sourceRegion.isEmpty())
            throw new RuntimeException(I18N.getString("WBMPImageWriter1"));

        int scaleX = param.getSourceXSubsampling();
        int scaleY = param.getSourceYSubsampling();
        int xOffset = param.getSubsamplingXOffset();
        int yOffset = param.getSubsamplingYOffset();

        sourceRegion.translate(xOffset, yOffset);
        sourceRegion.width -= xOffset;
        sourceRegion.height -= yOffset;

        int minX = sourceRegion.x / scaleX;
        int minY = sourceRegion.y / scaleY;
        int w = (sourceRegion.width + scaleX - 1) / scaleX;
        int h = (sourceRegion.height + scaleY - 1) / scaleY;

        Rectangle destinationRegion = new Rectangle(minX, minY, w, h);
        sampleModel = sampleModel.createCompatibleSampleModel(w, h);

        SampleModel destSM= sampleModel;

        // If the data are not formatted nominally then reformat.
        if(sampleModel.getDataType() != DataBuffer.TYPE_BYTE ||
           !(sampleModel instanceof MultiPixelPackedSampleModel) ||
           ((MultiPixelPackedSampleModel)sampleModel).getDataBitOffset() != 0) {
           destSM =
                new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE,
                                                w, h, 1,
                                                w + 7 >> 3, 0);
        }

        if (!destinationRegion.equals(sourceRegion)) {
            if (scaleX == 1 && scaleY == 1)
                inputRaster = inputRaster.createChild(inputRaster.getMinX(),
                                                      inputRaster.getMinY(),
                                                      w, h, minX, minY, null);
            else {
                WritableRaster ras = Raster.createWritableRaster(destSM,
                                                                 new Point(minX, minY));

                byte[] data = ((DataBufferByte)ras.getDataBuffer()).getData();

                for(int j = minY, y = sourceRegion.y, k = 0;
                    j < minY + h; j++, y += scaleY) {

                    for (int i = 0, x = sourceRegion.x;
                        i <w; i++, x +=scaleX) {
                        int v = inputRaster.getSample(x, y, 0);
                        data[k + (i >> 3)] |= v << (7 - (i & 7));
                    }
                    k += w + 7 >> 3;
                }
                inputRaster = ras;
            }
        }

        // If the data are not formatted nominally then reformat.
        if(!destSM.equals(inputRaster.getSampleModel())) {
            WritableRaster raster =
                Raster.createWritableRaster(destSM,
                                            new Point(inputRaster.getMinX(),
                                                      inputRaster.getMinY()));
            raster.setRect(inputRaster);
            inputRaster = raster;
        }

        // Check whether the image is white-is-zero.
        boolean isWhiteZero = false;
        if(!writeRaster && input.getColorModel() instanceof IndexColorModel) {
            IndexColorModel icm = (IndexColorModel)input.getColorModel();
            isWhiteZero = icm.getRed(0) > icm.getRed(1);
        }

        // Get the line stride, bytes per row, and data array.
        int lineStride =
            ((MultiPixelPackedSampleModel)destSM).getScanlineStride();
        int bytesPerRow = (w + 7)/8;
        byte[] bdata = ((DataBufferByte)inputRaster.getDataBuffer()).getData();

        // Write WBMP header.
        stream.write(0); // TypeField
        stream.write(0); // FixHeaderField
        stream.write(intToMultiByte(w)); // width
        stream.write(intToMultiByte(h)); // height

        // Write the data.
        if(!isWhiteZero && lineStride == bytesPerRow) {
            // Write the entire image.
            stream.write(bdata, 0, h * bytesPerRow);
            processImageProgress(100.0F);
        } else {
            // Write the image row-by-row.
            int offset = 0;
            if(!isWhiteZero) {
                // Black-is-zero
                for(int row = 0; row < h; row++) {
                    if (abortRequested())
                        break;
                    stream.write(bdata, offset, bytesPerRow);
                    offset += lineStride;
                    processImageProgress(100.0F * row / h);
                }
            } else {
                // White-is-zero: need to invert data.
                byte[] inverted = new byte[bytesPerRow];
                for(int row = 0; row < h; row++) {
                    if (abortRequested())
                        break;
                    for(int col = 0; col < bytesPerRow; col++) {
                        inverted[col] = (byte)(~(bdata[col+offset]));
                    }
                    stream.write(inverted, 0, bytesPerRow);
                    offset += lineStride;
                    processImageProgress(100.0F * row / h);
                }
            }
        }

        if (abortRequested())
            processWriteAborted();
        else {
            processImageComplete();
	    stream.flushBefore(stream.getStreamPosition());
	}
    }

    public void reset() {
        super.reset();
        stream = null;
    }

    private void checkSampleModel(SampleModel sm) {
        int type = sm.getDataType();
        if (type < DataBuffer.TYPE_BYTE || type > DataBuffer.TYPE_INT
	    || sm.getNumBands() != 1 || sm.getSampleSize(0) != 1)
            throw new IllegalArgumentException(I18N.getString("WBMPImageWriter2"));
    }
}
