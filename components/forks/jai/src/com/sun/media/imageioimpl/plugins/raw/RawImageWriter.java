/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2015 Open Microscopy Environment:
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
 * $RCSfile: RawImageWriter.java,v $
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
 * $Date: 2005/02/11 05:01:42 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.raw;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BandedSampleModel;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferDouble;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

import com.sun.media.imageioimpl.common.ImageUtil;

/**
 * The Java Image IO plugin writer for encoding a binary RenderedImage into
 * a Raw format.
 *
 * <p> The encoding process may clip, subsample or select bands using the
 * parameters specified in the <code>ImageWriteParam</code>.
 *
 * Thus, when read this raw image the proper image data type
 * should be provided.
 *
 * @see com.sun.media.imageio.plugins.RawImageWriteParam
 */

 // <p> If the destination data is packed packed, the data is written in the
 // order defined by the sample model. That the data is packed is defined as
 // (1) if the sample model is <code>SingleSamplePackedSampleModel</code> or
 // <code>BandedSampleModel</code>; or (2) the pixel stride or sanline stride
 // equals to the band number; or (3) the pixel stride equals to the band
 // number multiply the tile height; or (4) the scanline stride equals to the
 // band number multiply the tile width; or (5) the data for a band is stored
 // in a separate data bank.
 //
 // <p> Otherwise, the data is reordered in a packed pixel interleaved format,
 // and then written into the stream.  In this case the data order may be change.
 // For example, the original image data is in the order of
 // <pre>
 //        RRRRRRRRRRRRRRRRRRRR
 //        GGGGGGGGGGGGGGGGGGGG
 //        BBBBBBBBBBBBBBBBBBBB
 //        RRRRRRRRRRRRRRRRRRRR
 //        GGGGGGGGGGGGGGGGGGGG
 //        BBBBBBBBBBBBBBBBBBBB
 // </pre>
 //
 // , and only the G and B bands are written in the stream.  So the data in the
 // stream will be in the order of
 // <pre>
 //       GBGBGBGBGBGBGB
 // </pre>.
public class RawImageWriter extends ImageWriter {
    /** The output stream to write into */
    private ImageOutputStream stream = null;

    /** The image index in this stream. */
    private int imageIndex;

    /** The tile width for encoding */
    private int tileWidth;

    /** The tile height for encoding */
    private int tileHeight;

    /** The tile grid offset for encoding */
    private int tileXOffset, tileYOffset;

    /** The source -> destination transformation */
    private int scaleX, scaleY, xOffset, yOffset;

    /** The source bands to be encoded. */
    private int[] sourceBands = null;

    /** The number of components in the image */
    private int numBands;

    /** The source raster if write raster. */
    private RenderedImage input;

    /** The input source raster. */
    private Raster inputRaster;

    private Rectangle destinationRegion = null;

    private SampleModel sampleModel;

    /** Coordinate transform or sub selection is needed before encoding. */
    private boolean noTransform = true;
    private boolean noSubband = true;

    /** Indicates a <code>raster</code> rather than a <code>RenderedImage</code>
     *  to be encoded.
     */
    private boolean writeRaster = false;

    /** Whether can write optimally. */
    private boolean optimal = false;

    /** The strides for pixel, band, and scanline. */
    private int pxlStride, lineStride, bandStride;

    /** Constructs <code>RawImageWriter</code> based on the provided
     *  <code>ImageWriterSpi</code>.
     */
    public RawImageWriter(ImageWriterSpi originator) {
        super(originator);
    }

    public void setOutput(Object output) {
        super.setOutput(output); // validates output
        if (output != null) {
            if (!(output instanceof ImageOutputStream))
                throw new IllegalArgumentException(I18N.getString("RawImageWriter0"));
            this.stream = (ImageOutputStream)output;
        } else
            this.stream = null;
    }

    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
        return null;
    }

    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType,
                                               ImageWriteParam param) {
        return null;
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

    public ImageWriteParam getDefaultWriteParam() {
	return new RawImageWriteParam(getLocale());
    }

    public void write(IIOMetadata streamMetadata,
                      IIOImage image,
                      ImageWriteParam param) throws IOException {
        clearAbortRequest();
        processImageStarted(imageIndex++);

        if (param == null)
            param = getDefaultWriteParam();

        writeRaster = image.hasRaster();
        Rectangle sourceRegion = param.getSourceRegion();
        ColorModel colorModel = null;
        Rectangle originalRegion = null;

        if (writeRaster) {
            inputRaster = image.getRaster();
            sampleModel = inputRaster.getSampleModel();
            originalRegion = inputRaster.getBounds();
        } else {
            input = image.getRenderedImage();
            sampleModel = input.getSampleModel();

            originalRegion = new Rectangle(input.getMinX(), input.getMinY(),
                                           input.getWidth(), input.getHeight());

            colorModel = input.getColorModel();
        }

        if (sourceRegion == null)
            sourceRegion = (Rectangle)originalRegion.clone();
        else
            sourceRegion = sourceRegion.intersection(originalRegion);

        if (sourceRegion.isEmpty())
            throw new RuntimeException(I18N.getString("RawImageWriter1"));

        scaleX = param.getSourceXSubsampling();
        scaleY = param.getSourceYSubsampling();
        xOffset = param.getSubsamplingXOffset();
        yOffset = param.getSubsamplingYOffset();

        sourceRegion.translate(xOffset, yOffset);
        sourceRegion.width -= xOffset;
        sourceRegion.height -= yOffset;

	xOffset = sourceRegion.x % scaleX;
	yOffset = sourceRegion.y % scaleY;

        int minX = sourceRegion.x / scaleX;
        int minY = sourceRegion.y / scaleY;
        int w = (sourceRegion.width + scaleX - 1) / scaleX;
        int h = (sourceRegion.height + scaleY - 1) / scaleY;

        destinationRegion = new Rectangle(minX, minY, w, h);
        noTransform = destinationRegion.equals(originalRegion);

        tileHeight = sampleModel.getHeight();
        tileWidth = sampleModel.getWidth();
        if (noTransform) {
            if (writeRaster) {
                tileXOffset = inputRaster.getMinX();
                tileYOffset = inputRaster.getMinY();
            } else {
                tileXOffset = input.getTileGridXOffset();
                tileYOffset = input.getTileGridYOffset();
            }
        } else {
            tileXOffset = destinationRegion.x;
            tileYOffset = destinationRegion.y;
        }

        sourceBands = param.getSourceBands();
        boolean noSubband = true;
        numBands = sampleModel.getNumBands();

        if (sourceBands != null) {
            sampleModel = sampleModel.createSubsetSampleModel(sourceBands);
            colorModel = null;
            noSubband = false;
            numBands = sampleModel.getNumBands();
        } else {
            sourceBands = new int[numBands];
            for (int i = 0; i < numBands; i++)
                sourceBands[i] = i;
        }

        if (sampleModel instanceof ComponentSampleModel) {
            ComponentSampleModel csm = (ComponentSampleModel)sampleModel;
            int[] bandOffsets = csm.getBandOffsets();

            bandStride = bandOffsets[0];

            for (int i = 1; i < bandOffsets.length; i++)
                if (bandStride > bandOffsets[i])
                    bandStride = bandOffsets[i];

            int[] bankIndices = csm.getBankIndices();
            int numBank = bankIndices[0];
            for (int i = 1; i < bankIndices.length; i++)
                if (numBank > bankIndices[i])
                    numBank = bankIndices[i];

            pxlStride = csm.getPixelStride();
            lineStride = csm.getScanlineStride();

            optimal = bandStride == 0 ||
                      (pxlStride < lineStride && pxlStride == numBands) ||
                      (lineStride < pxlStride && lineStride == numBands) ||
                      (pxlStride < lineStride &&
                       lineStride == numBands * csm.getWidth()) ||
                      (lineStride < pxlStride &&
                       pxlStride == numBands * csm.getHeight()) ||
                      csm instanceof BandedSampleModel;
        } else if (sampleModel instanceof SinglePixelPackedSampleModel ||
                   sampleModel instanceof MultiPixelPackedSampleModel) {
            optimal = true;
        }

        int numXTiles = getMaxTileX() - getMinTileX() + 1;
        int totalTiles = numXTiles * (getMaxTileY() - getMinTileY() + 1);

        for (int y = getMinTileY(); y <= getMaxTileY(); y++) {
            for (int x = getMinTileX(); x <= getMaxTileX(); x++) {
                writeRaster(getTile(x, y));

                float percentage = (x + y * numXTiles + 1.0F) / totalTiles;
                processImageProgress(percentage * 100.0F);
            }
        }

        stream.flush();
        if (abortRequested())
            processWriteAborted();
        else
            processImageComplete();
    }

    //XXX: just for test
    public int getWidth() {
        return destinationRegion.width;
    }

    public int getHeight() {
        return destinationRegion.height;
    }

    private void writeRaster(Raster raster) throws IOException {
        int numBank = 0;
        int bandStride = 0;
        int[] bankIndices = null;
        int[] bandOffsets = null;
        int bandSize = 0;
        int numBand = sampleModel.getNumBands();
        int type = sampleModel.getDataType();

        if (sampleModel instanceof ComponentSampleModel) {
            ComponentSampleModel csm = (ComponentSampleModel)sampleModel;

            bandOffsets = csm.getBandOffsets();
            for (int i = 0; i < numBand; i++)
                if (bandStride < bandOffsets[i])
                    bandStride = bandOffsets[i];

            bankIndices = csm.getBankIndices();
            for (int i = 0; i < numBand; i++)
                if (numBank < bankIndices[i])
                    numBank = bankIndices[i];

            bandSize = (int)ImageUtil.getBandSize(sampleModel) ;
        }

        byte[] bdata = null;
        short[] sdata = null;
        int[] idata = null;
        float[] fdata = null;
        double[] ddata = null;

        if (raster.getParent() != null &&
            !sampleModel.equals(raster.getParent().getSampleModel())) {
            WritableRaster ras =
                Raster.createWritableRaster(sampleModel,
                                            new Point(raster.getMinX(),
                                                      raster.getMinY()));
            ras.setRect(raster);
            raster = ras;
        }

        DataBuffer data = raster.getDataBuffer();

        if (optimal) {
            if (numBank > 0) {  //multiple data bank
                for (int i = 0; i < numBands; i++) {
                    int bank = bankIndices[sourceBands[i]];
                    switch (type) {
                    case DataBuffer.TYPE_BYTE:
                        bdata = ((DataBufferByte)data).getData(bank);
                        stream.write(bdata, 0, bdata.length);
                        break;
                    case DataBuffer.TYPE_SHORT:
                        sdata = ((DataBufferShort)data).getData(bank);
                        stream.writeShorts(sdata, 0, sdata.length);
                        break;
                    case DataBuffer.TYPE_USHORT:
                        sdata = ((DataBufferUShort)data).getData(bank);
                        stream.writeShorts(sdata, 0, sdata.length);
                        break;
                    case DataBuffer.TYPE_INT:
                        idata = ((DataBufferInt)data).getData(bank);
                        stream.writeInts(idata, 0, idata.length);
                        break;
                    case DataBuffer.TYPE_FLOAT:
                        fdata = ((DataBufferFloat)data).getData(bank);
                        stream.writeFloats(fdata, 0, fdata.length);
                        break;
                    case DataBuffer.TYPE_DOUBLE:
                        ddata = ((DataBufferDouble)data).getData(bank);
                        stream.writeDoubles(ddata, 0, ddata.length);
                        break;
                    }
                }
            } else {  // Single data bank
                switch (type) {
                    case DataBuffer.TYPE_BYTE:
                        bdata = ((DataBufferByte)data).getData();
                        break;
                    case DataBuffer.TYPE_SHORT:
                        sdata = ((DataBufferShort)data).getData();
                        break;
                    case DataBuffer.TYPE_USHORT:
                        sdata = ((DataBufferUShort)data).getData();
                        break;
                    case DataBuffer.TYPE_INT:
                        idata = ((DataBufferInt)data).getData();
                        break;
                    case DataBuffer.TYPE_FLOAT:
                        fdata = ((DataBufferFloat)data).getData();
                        break;
                    case DataBuffer.TYPE_DOUBLE:
                        ddata = ((DataBufferDouble)data).getData();
                        break;
                }

                if (!noSubband &&
                    bandStride >= raster.getWidth() *
                                  raster.getHeight() * (numBands-1)) {

                    for (int i = 0; i < numBands; i++) {
                        int offset = bandOffsets[sourceBands[i]];
                        switch (type) {
                        case DataBuffer.TYPE_BYTE:
                            stream.write(bdata, offset, bandSize);
                            break;
                        case DataBuffer.TYPE_SHORT:
                        case DataBuffer.TYPE_USHORT:
                            stream.writeShorts(sdata, offset, bandSize);
                            break;
                        case DataBuffer.TYPE_INT:
                            stream.writeInts(idata, offset, bandSize);
                            break;
                        case DataBuffer.TYPE_FLOAT:
                            stream.writeFloats(fdata, offset, bandSize);
                            break;
                        case DataBuffer.TYPE_DOUBLE:
                            stream.writeDoubles(ddata, offset, bandSize);
                            break;
                        }
                    }
                } else {
                    switch (type) {
                        case DataBuffer.TYPE_BYTE:
                            stream.write(bdata, 0, bdata.length);
                            break;
                        case DataBuffer.TYPE_SHORT:
                        case DataBuffer.TYPE_USHORT:
                            stream.writeShorts(sdata, 0, sdata.length);
                            break;
                        case DataBuffer.TYPE_INT:
                            stream.writeInts(idata, 0, idata.length);
                            break;
                        case DataBuffer.TYPE_FLOAT:
                            stream.writeFloats(fdata, 0, fdata.length);
                            break;
                        case DataBuffer.TYPE_DOUBLE:
                            stream.writeDoubles(ddata, 0, ddata.length);
                            break;
                    }
                }
            }
        } else if (sampleModel instanceof ComponentSampleModel) {
            // The others, must be a ComponentSampleModel
            switch (type) {
                case DataBuffer.TYPE_BYTE:
                    bdata = ((DataBufferByte)data).getData();
                    break;
                case DataBuffer.TYPE_SHORT:
                    sdata = ((DataBufferShort)data).getData();
                    break;
                case DataBuffer.TYPE_USHORT:
                    sdata = ((DataBufferUShort)data).getData();
                    break;
                case DataBuffer.TYPE_INT:
                    idata = ((DataBufferInt)data).getData();
                    break;
                case DataBuffer.TYPE_FLOAT:
                    fdata = ((DataBufferFloat)data).getData();
                    break;
                case DataBuffer.TYPE_DOUBLE:
                    ddata = ((DataBufferDouble)data).getData();
                    break;
            }

            ComponentSampleModel csm = (ComponentSampleModel)sampleModel;
            int offset =
                csm.getOffset(raster.getMinX()-raster.getSampleModelTranslateX(),
                              raster.getMinY()-raster.getSampleModelTranslateY())
                              - bandOffsets[0];

            int srcSkip = pxlStride;
            int copyLength = 1;
            int innerStep = pxlStride;

            int width = raster.getWidth();
            int height = raster.getHeight();

            int innerBound = width;
            int outerBound = height;

            if (srcSkip < lineStride) {
                if (bandStride > pxlStride)
                    copyLength = width;
                srcSkip = lineStride;
            } else {
                if (bandStride > lineStride)
                    copyLength = height;
                innerStep = lineStride;
                innerBound = height;
                outerBound = width;
            }

            int writeLength = innerBound * numBands;
            byte[] destBBuf = null;
            short[] destSBuf = null;
            int[] destIBuf = null;
            float[] destFBuf = null;
            double[] destDBuf = null;
            Object srcBuf = null;
            Object dstBuf = null;

            switch (type) {
            case DataBuffer.TYPE_BYTE:
                srcBuf = bdata;
                dstBuf = destBBuf = new byte[writeLength];
                break;
            case DataBuffer.TYPE_SHORT:
            case DataBuffer.TYPE_USHORT:
                srcBuf = sdata;
                dstBuf = destSBuf = new short[writeLength];
                break;
            case DataBuffer.TYPE_INT:
                srcBuf = idata;
                dstBuf = destIBuf = new int[writeLength];
                break;
            case DataBuffer.TYPE_FLOAT:
                srcBuf = fdata;
                dstBuf = destFBuf = new float[writeLength];
                break;
            case DataBuffer.TYPE_DOUBLE:
                srcBuf = ddata;
                dstBuf = destDBuf = new double[writeLength];
                break;
            }

            if (copyLength > 1) {
                for (int i = 0; i < outerBound; i++) {
                    for (int b = 0; b < numBands; b++) {
                        int bandOffset = bandOffsets[b];

                        System.arraycopy(srcBuf, offset + bandOffset,
                                         dstBuf, b * innerBound, innerBound);
                    }

                    switch (type) {
                    case DataBuffer.TYPE_BYTE:
                        stream.write((byte[])dstBuf, 0, writeLength);
                        break;
                    case DataBuffer.TYPE_SHORT:
                    case DataBuffer.TYPE_USHORT:
                        stream.writeShorts((short[])dstBuf, 0, writeLength);
                        break;
                    case DataBuffer.TYPE_INT:
                        stream.writeInts((int[])dstBuf, 0, writeLength);
                        break;
                    case DataBuffer.TYPE_FLOAT:
                        stream.writeFloats((float[])dstBuf, 0, writeLength);
                        break;
                    case DataBuffer.TYPE_DOUBLE:
                        stream.writeDoubles((double[])dstBuf, 0, writeLength);
                        break;
                    }
                    offset += srcSkip;
                }
            } else {
                switch (type) {
                    case DataBuffer.TYPE_BYTE: {
                        for (int i = 0; i < outerBound; i++) {
                            for (int b = 0, k = 0; b < numBands; b++) {
                                int bandOffset = bandOffsets[b];

                                for (int j = 0, m = offset; j < innerBound;
                                     j++, m += innerStep)
                                    // copy one sample to the destination buffer
                                    destBBuf[k++] = bdata[m + bandOffset];
                            }

                            stream.write(destBBuf, 0, writeLength);
                            offset += srcSkip;
                        }
                    }
                    break;
                    case DataBuffer.TYPE_SHORT:
                    case DataBuffer.TYPE_USHORT: {
                        for (int i = 0; i < outerBound; i++) {
                            for (int b = 0, k = 0; b < numBands; b++) {
                                int bandOffset = bandOffsets[b];

                                for (int j = 0, m = offset; j < innerBound;
                                     j++, m += innerStep)
                                    // copy one sample to the destination buffer
                                    destSBuf[k++] = sdata[m + bandOffset];
                            }

                            stream.writeShorts(destSBuf, 0, writeLength);
                            offset += srcSkip;
                        }
                    }
                    break;
                    case DataBuffer.TYPE_INT: {
                        for (int i = 0; i < outerBound; i++) {
                            for (int b = 0, k = 0; b < numBands; b++) {
                                int bandOffset = bandOffsets[b];

                                for (int j = 0, m = offset; j < innerBound;
                                     j++, m += innerStep)
                                    // copy one sample to the destination buffer
                                    destIBuf[k++] = idata[m + bandOffset];
                            }

                            stream.writeInts(destIBuf, 0, writeLength);
                            offset += srcSkip;
                        }
                    }
                    break;
                    case DataBuffer.TYPE_FLOAT: {
                        for (int i = 0; i < outerBound; i++) {
                            for (int b = 0, k = 0; b < numBands; b++) {
                                int bandOffset = bandOffsets[b];

                                for (int j = 0, m = offset; j < innerBound;
                                     j++, m += innerStep)
                                    // copy one sample to the destination buffer
                                    destFBuf[k++] = fdata[m + bandOffset];
                            }

                            stream.writeFloats(destFBuf, 0, writeLength);
                            offset += srcSkip;
                        }
                    }
                    break;
                    case DataBuffer.TYPE_DOUBLE: {
                        for (int i = 0; i < outerBound; i++) {
                            for (int b = 0, k = 0; b < numBands; b++) {
                                int bandOffset = bandOffsets[b];

                                for (int j = 0, m = offset; j < innerBound;
                                     j++, m += innerStep)
                                    // copy one sample to the destination buffer
                                    destDBuf[k++] = ddata[m + bandOffset];
                            }

                            stream.writeDoubles(destDBuf, 0, writeLength);
                            offset += srcSkip;
                        }
                    }
                    break;
                }
            }
        }
    }

    private Raster getTile(int tileX, int tileY) {
        int sx = tileXOffset + tileX * tileWidth;
        int sy = tileYOffset + tileY * tileHeight;
        Rectangle bounds = new Rectangle(sx, sy, tileWidth, tileHeight);

        if (writeRaster) {
            bounds = bounds.intersection(destinationRegion);
            if (noTransform) {
                return inputRaster.createChild(bounds.x, bounds.y,
                                           bounds.width, bounds.height,
                                           bounds.x, bounds.y, sourceBands);
            }

            sx = bounds.x;
            sy = bounds.y;

            WritableRaster ras =
                Raster.createWritableRaster(sampleModel, new Point(sx, sy));

            int x = mapToSourceX(sx);
            int y = mapToSourceY(sy);

            int minY = inputRaster.getMinY();
            int maxY = inputRaster.getMinY() + inputRaster.getHeight();

            int cTileWidth = bounds.width;

            int length = (cTileWidth - 1) * scaleX + 1;

            for (int j = 0; j < bounds.height; j++, sy++, y += scaleY) {
                if (y < minY || y >= maxY)
                    continue;
                Raster source =
                    inputRaster.createChild(x, y, length, 1,
                                            x, y, null);
                int tempX = sx;
                for (int i = 0, offset = x; i < cTileWidth;
                    i++, tempX++, offset += scaleX) {
                    for (int k = 0; k < numBands; k++) {
                        int p = source.getSample(offset, y, sourceBands[k]);
                        ras.setSample(tempX, sy, k, p);
                    }
                }
            }

            return ras;

        } else {
            if (noTransform) {
                Raster ras = input.getTile(tileX, tileY);
                if (destinationRegion.contains(bounds) && noSubband)
                    return ras;
                else {
                    bounds = bounds.intersection(destinationRegion);
                    return ras.createChild(bounds.x, bounds.y,
                                           bounds.width, bounds.height,
                                           bounds.x, bounds.y, sourceBands);
                }
            }

            bounds = bounds.intersection(destinationRegion);
            sx = bounds.x;
            sy = bounds.y;

            WritableRaster ras =
                Raster.createWritableRaster(sampleModel, new Point(sx, sy));

            int x = mapToSourceX(sx);
            int y = mapToSourceY(sy);

            int minY = input.getMinY();
            int maxY = input.getMinY() + input.getHeight();

            int cTileWidth = bounds.width;
            int length = (cTileWidth -1) * scaleX + 1;

            for (int j = 0; j < bounds.height; j++, sy++, y += scaleY) {
                if (y < minY || y >= maxY)
                    continue;

                Raster source =
                    input.getData(new Rectangle(x, y, length, 1));

                int tempX = sx;
                for (int i = 0, offset = x; i < cTileWidth;
                    i++, tempX++, offset += scaleX) {
                    for (int k = 0; k < numBands; k++) {
                        int p = source.getSample(offset, y, sourceBands[k]);
                        ras.setSample(tempX, sy, k, p);
                    }
                }
            }
            return ras;
        }
    }

    private int mapToSourceX(int x) {
        return x * scaleX + xOffset;
    }

    private int mapToSourceY(int y) {
        return y * scaleY + yOffset;
    }

    private int getMinTileX() {
        return ToTile(destinationRegion.x, tileXOffset, tileWidth);
    }

    private int getMaxTileX() {
        return ToTile(destinationRegion.x + destinationRegion.width - 1,
                      tileXOffset, tileWidth);
    }

    private int getMinTileY() {
        return ToTile(destinationRegion.y, tileYOffset, tileHeight);
    }

    private int getMaxTileY() {
        return ToTile(destinationRegion.y + destinationRegion.height - 1,
                      tileYOffset, tileHeight);
    }

    private static int ToTile(int pos, int tileOffset, int tileSize) {
        pos -= tileOffset;
        if (pos < 0) {
            pos += 1 - tileSize;         // force round to -infinity (ceiling)
        }
        return pos/tileSize;
    }

    public void reset() {
        super.reset();
        stream = null;
        optimal = false;
        sourceBands = null;
        destinationRegion = null;
        noTransform = true;
        noSubband = true;
        writeRaster = false;
    }
}
