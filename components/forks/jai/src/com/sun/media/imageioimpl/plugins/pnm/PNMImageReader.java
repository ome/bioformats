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
 * $RCSfile: PNMImageReader.java,v $
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
 * $Date: 2005/02/11 05:01:40 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.pnm;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.sun.media.imageioimpl.common.ImageUtil;

/** This class is the Java Image IO plugin reader for PNM images.
 *  It may subsample the image, clip the image, select sub-bands,
 *  and shift the decoded image origin if the proper decoding parameter
 *  are set in the provided <code>PNMImageReadParam</code>.
 */
public class PNMImageReader extends ImageReader {
    private static final int PBM_ASCII  = '1';
    private static final int PGM_ASCII  = '2';
    private static final int PPM_ASCII  = '3';
    private static final int PBM_RAW    = '4';
    private static final int PGM_RAW    = '5';
    private static final int PPM_RAW    = '6';

    private static final int LINE_FEED = 0x0A;
    private static byte[] lineSeparator;

    static {
        if (lineSeparator == null) {
            String ls = (String)java.security.AccessController.doPrivileged(
               new sun.security.action.GetPropertyAction("line.separator"));
            lineSeparator = ls.getBytes();
        }
    }

    /** File variant: PBM/PGM/PPM, ASCII/RAW. */
    private int variant;

    /** Maximum pixel value. */
    private int maxValue;

    /** The input stream where reads from */
    private ImageInputStream iis = null;

    /** Indicates whether the header is read. */
    private boolean gotHeader = false;

    /** The stream position where the image data starts. */
    private long imageDataOffset;

    /** The original image width. */
    private int width;

    /** The original image height. */
    private int height;

    private String aLine;
    private StringTokenizer token;

    private PNMMetadata metadata;

    /** Constructs <code>PNMImageReader</code> from the provided
     *  <code>ImageReaderSpi</code>.
     */
    public PNMImageReader(ImageReaderSpi originator) {
        super(originator);
    }

    /** Overrides the method defined in the superclass. */
    public void setInput(Object input,
                         boolean seekForwardOnly,
                         boolean ignoreMetadata) {
        super.setInput(input, seekForwardOnly, ignoreMetadata);
        iis = (ImageInputStream) input; // Always works
    }

    /** Overrides the method defined in the superclass. */
    public int getNumImages(boolean allowSearch) throws IOException {
        return 1;
    }

    public int getWidth(int imageIndex) throws IOException {
        checkIndex(imageIndex);
        readHeader();
        return width;
    }

    public int getHeight(int imageIndex) throws IOException {
        checkIndex(imageIndex);
        readHeader();
        return height;
    }

    public int getVariant() {
        return variant;
    }

    public int getMaxValue() {
        return maxValue;
    }

    private void checkIndex(int imageIndex) {
        if (imageIndex != 0) {
            throw new IndexOutOfBoundsException(I18N.getString("PNMImageReader1"));
        }
    }

    public synchronized void readHeader() throws IOException {
        if (gotHeader) {
	    // Seek to where the image data starts, since that is where
	    // the stream pointer should be after header is read
	    iis.seek(imageDataOffset);
            return;
	}

        if (iis != null) {
            if (iis.readByte() != 'P') {	// magic number
                throw new RuntimeException(I18N.getString("PNMImageReader0"));
            }

            variant = iis.readByte();	// file variant
            if ((variant < PBM_ASCII) || (variant > PPM_RAW)) {
                throw new RuntimeException(I18N.getString("PNMImageReader0"));
            }

            // Create the metadata object.
            metadata = new PNMMetadata();

            // Set the variant.
            metadata.setVariant(variant);

            // Read the line separator.
            iis.readLine();

            readComments(iis, metadata);

            width = readInteger(iis);	// width
            height = readInteger(iis);	// height

            if (variant == PBM_ASCII || variant == PBM_RAW) {
                maxValue = 1;
            } else {
                maxValue = readInteger(iis);	// maximum value
            }

            metadata.setWidth(width);
            metadata.setHeight(height);
            metadata.setMaxBitDepth(maxValue);

            gotHeader = true;

	    // Store the stream position where the image data starts
	    imageDataOffset = iis.getStreamPosition();
        }
    }

    public Iterator getImageTypes(int imageIndex)
        throws IOException {
        checkIndex(imageIndex);

        readHeader();
        int tmp = (variant - '1') % 3 ;

        ArrayList list = new ArrayList(1);
        int dataType = DataBuffer.TYPE_INT;
        // Determine data type based on maxValue.
        if (maxValue < 0x100) {
            dataType = DataBuffer.TYPE_BYTE;
        } else if (maxValue < 0x10000) {
            dataType = DataBuffer.TYPE_USHORT;
        }

        // Choose an appropriate SampleModel.
        SampleModel sampleModel = null;
        ColorModel colorModel = null;
        if ((variant == PBM_ASCII) || (variant == PBM_RAW)) {
            // Each pixel takes 1 bit, pack 8 pixels into a byte.
            sampleModel = new MultiPixelPackedSampleModel(
                              DataBuffer.TYPE_BYTE,
                              width,
                              height,
                              1);
            byte[] color = {(byte)0xFF, (byte)0};
            colorModel = new IndexColorModel(1, 2, color, color, color);
        } else {
            sampleModel =
                new PixelInterleavedSampleModel(dataType,
                                                width,
                                                height,
                                                tmp == 1 ? 1 : 3,
                                                width * (tmp == 1 ? 1 : 3),
                                                tmp == 1 ? new int[]{0} : new int[]{0, 1, 2});

            colorModel = ImageUtil.createColorModel(null, sampleModel);
        }

        list.add(new ImageTypeSpecifier(colorModel, sampleModel));

        return list.iterator();
    }

    public ImageReadParam getDefaultReadParam() {
        return new ImageReadParam();
    }

    public IIOMetadata getImageMetadata(int imageIndex)
        throws IOException {
        checkIndex(imageIndex);
        readHeader();
        return metadata;
    }

    public IIOMetadata getStreamMetadata() throws IOException {
        return null;
    }

    public boolean isRandomAccessEasy(int imageIndex) throws IOException {
        checkIndex(imageIndex);
        return true;
    }

    public BufferedImage read(int imageIndex, ImageReadParam param)
        throws IOException {
        checkIndex(imageIndex);
        clearAbortRequest();
        processImageStarted(imageIndex);

        if (param == null)
            param = getDefaultReadParam();

        //read header
        readHeader();

        Rectangle sourceRegion = new Rectangle(0, 0, 0, 0);
        Rectangle destinationRegion = new Rectangle(0, 0, 0, 0);

        computeRegions(param, this.width, this.height,
                       param.getDestination(),
                       sourceRegion,
                       destinationRegion);

        int scaleX = param.getSourceXSubsampling();
        int scaleY = param.getSourceYSubsampling();

        // If the destination band is set used it
        int[] sourceBands = param.getSourceBands();
        int[] destBands = param.getDestinationBands();

        boolean seleBand = (sourceBands != null) && (destBands != null);
	boolean	noTransform =
	    destinationRegion.equals(new Rectangle(0, 0, width, height)) ||
                              seleBand;

        // The RAWBITS format can only support byte image data, which means
        // maxValue should be less than 0x100. In case there's a conflict,
        // base the maxValue on variant.
        if (isRaw(variant) && maxValue >= 0x100) {
            maxValue = 0xFF;
        }

        int numBands = 1;
        // Determine number of bands: pixmap (PPM) is 3 bands,
        // bitmap (PBM) and greymap (PGM) are 1 band.
        if (variant == PPM_ASCII || variant == PPM_RAW) {
            numBands = 3;
        }

        if (!seleBand) {
            sourceBands = new int[numBands];
            destBands = new int[numBands];
            for (int i = 0; i < numBands; i++)
                destBands[i] = sourceBands[i] = i;
        }

        int dataType = DataBuffer.TYPE_INT;
        // Determine data type based on maxValue.
        if (maxValue < 0x100) {
            dataType = DataBuffer.TYPE_BYTE;
        } else if (maxValue < 0x10000) {
            dataType = DataBuffer.TYPE_USHORT;
        }

        // Choose an appropriate SampleModel.
        SampleModel sampleModel = null;
        ColorModel colorModel = null;
        if ((variant == PBM_ASCII) || (variant == PBM_RAW)) {
            // Each pixel takes 1 bit, pack 8 pixels into a byte.
            sampleModel = new MultiPixelPackedSampleModel(
                              DataBuffer.TYPE_BYTE,
                              destinationRegion.width,
                              destinationRegion.height,
                              1);
            byte[] color = {(byte)0xFF, (byte)0};
            colorModel = new IndexColorModel(1, 2, color, color, color);
        } else {
            sampleModel = 
		new PixelInterleavedSampleModel(dataType,
						destinationRegion.width,
						destinationRegion.height,
						sourceBands.length,
						destinationRegion.width * 
						sourceBands.length,
						destBands);

            colorModel = ImageUtil.createColorModel(null, sampleModel);
        }

        // If the destination is provided, then use it. Otherwise, create new
	// one
        BufferedImage bi = param.getDestination();

        // Get the image data.
        WritableRaster raster = null;

        if (bi == null) {
            sampleModel = sampleModel.createCompatibleSampleModel(
			       destinationRegion.x + destinationRegion.width,
			       destinationRegion.y + destinationRegion.height);
            if (seleBand)
                sampleModel = sampleModel.createSubsetSampleModel(sourceBands);

            raster = Raster.createWritableRaster(sampleModel, new Point());
            bi = new BufferedImage(colorModel, raster, false, null);
        } else {
            raster = bi.getWritableTile(0, 0);
            sampleModel = bi.getSampleModel();
            colorModel = bi.getColorModel();
	    noTransform &= destinationRegion.equals(raster.getBounds());
        }

        switch (variant) {
            case PBM_RAW:
            {

                // SampleModel for these cases should be MultiPixelPacked.
                DataBuffer dataBuffer = raster.getDataBuffer();

                // Read the entire image.
                byte[] buf = ((DataBufferByte)dataBuffer).getData();
                if (noTransform) {
                    iis.readFully(buf, 0, buf.length);
                    processImageUpdate(bi,
                                       0, 0,
                                       width, height, 1, 1,
                                       destBands);
                    processImageProgress(100.0F);
                } else if (scaleX == 1  && sourceRegion.x % 8 == 0) {
                    int skip = sourceRegion.x >> 3;
                    int originalLS = width + 7 >> 3;
                    int destLS = raster.getWidth() + 7 >> 3;

                    int readLength = sourceRegion.width + 7 >> 3;
                    int offset = sourceRegion.y * originalLS;
                    iis.skipBytes(offset + skip);
                    offset = originalLS * (scaleY - 1) + originalLS - readLength;
                    byte[] lineData = new byte[readLength];

                    int bitoff = destinationRegion.x & 7;
                    boolean reformat = !(bitoff == 0);

                    for (int i = 0, j = 0,
                        k = destinationRegion.y * destLS + (destinationRegion.x >> 3);
                        i < destinationRegion.height; i++, j += scaleY) {
                        if (reformat) {
                            iis.read(lineData, 0, readLength);
                            int mask1 = (255 << bitoff) & 255;
                            int mask2 = ~mask1 & 255;
                            int shift = 8 - bitoff;

                            int n = 0;
                            int m = k;
                            for (; n < readLength -1; n++, m++)
                                buf[m] = (byte)(((lineData[n] & mask2) << shift) |
                                                (lineData[n + 1] & mask1) >>bitoff);
                            buf[m] = (byte)((lineData[n] & mask2) << shift);
                        } else {
                            iis.read(buf, k, readLength);
                        }

                        iis.skipBytes(offset);
                        k += destLS;

                        processImageUpdate(bi,
                                           0, i,
                                           destinationRegion.width, 1, 1, 1,
                                           destBands);
                        processImageProgress(100.0F*i/destinationRegion.height);
                    }
                } else {
                    int originalLS = width + 7 >> 3;
                    byte[] data = new byte[originalLS];
                    iis.skipBytes(sourceRegion.y * originalLS);
                    int destLS = bi.getWidth() + 7 >> 3;
                    int offset = originalLS * (scaleY - 1);
                    int dsx = destLS * destinationRegion.y +
                              (destinationRegion.x >> 3);
                    for (int i = 0, j = 0, n = dsx;
                        i < destinationRegion.height; i++, j += scaleY) {
                        iis.read(data, 0, originalLS);
                        iis.skipBytes(offset);

                        int b = 0;
                        int pos = 7 - (destinationRegion.x & 7);
                        for (int m = sourceRegion.x;
                            m < sourceRegion.x + sourceRegion.width;
                            m += scaleX) {
                            b |= (data[m >> 3] >> (7 - (m & 7)) & 1) << pos;
                            pos--;
                            if (pos == -1) {
                                buf[n++] = (byte)b;
                                b = 0;
                                pos = 7;
                            }
                        }

                        if (pos != 7)
                            buf[n++] = (byte)b;

                        n += destinationRegion.x >> 3;
                        processImageUpdate(bi,
                                           0, i,
                                           destinationRegion.width, 1, 1, 1,
                                           destBands);
                        processImageProgress(100.0F*i/destinationRegion.height);
                    }
                }
                break;
            }
            case PBM_ASCII:
            {
                DataBuffer dataBuffer = raster.getDataBuffer();
                byte[] buf = ((DataBufferByte)dataBuffer).getData();
                if (noTransform)
                    for (int i = 0, n = 0; i < height; i++) {
                        int b = 0;
                        int pos = 7;
                        for (int j = 0; j < width; j++) {
                            b |= (readInteger(iis) & 1) << pos;
                            pos--;
                            if (pos == -1 ) {
                                buf[n++] = (byte)b;
                                b = 0;
                                pos = 7;
                            }
                        }
                        if (pos != 7)
                            buf[n++] = (byte)b;
                        processImageUpdate(bi,
                                           0, i,
                                           width, 1, 1, 1,
                                           destBands);
                        processImageProgress(100.0F * i / height);
                    }
                else {
                    skipInteger(iis, sourceRegion.y * width + sourceRegion.x);
                    int skipX = scaleX - 1;
                    int skipY = (scaleY - 1) * width +
                                width - destinationRegion.width * scaleX;
                    int dsx = (bi.getWidth() + 7 >> 3) *
                              destinationRegion.y + (destinationRegion.x >> 3);
                    for (int i = 0, n = dsx; i < destinationRegion.height; i++) {
                        int b = 0;
                        int pos = 7 - (destinationRegion.x & 7);
                        for (int j = 0; j < destinationRegion.width; j++) {
                            b |= (readInteger(iis) & 1) << pos;
                            pos--;
                            if (pos == -1 ) {
                                buf[n++] = (byte)b;
                                b = 0;
                                pos = 7;
                            }
                            skipInteger(iis, skipX);
                        }
                        if (pos != 7)
                            buf[n++] = (byte)b;

                        n += destinationRegion.x >> 3;
                        skipInteger(iis, skipY);
                        processImageUpdate(bi,
                                           0, i,
                                           destinationRegion.width, 1, 1, 1,
                                           destBands);
                        processImageProgress(100.0F*i/destinationRegion.height);
                    }
                }
                break;
            }
            case PGM_ASCII:
            case PGM_RAW:
            case PPM_ASCII:
            case PPM_RAW:
                // SampleModel for these cases should be PixelInterleaved.
                int skipX = (scaleX - 1) * numBands;
                int skipY = (scaleY * width -
                            destinationRegion.width * scaleX) * numBands;
                int dsx = (bi.getWidth() * destinationRegion. y +
                          destinationRegion.x) * numBands;
                switch (dataType) {
                case DataBuffer.TYPE_BYTE:
                    DataBufferByte bbuf =
                        (DataBufferByte)raster.getDataBuffer();
                    byte[] byteArray = bbuf.getData();
                    if (isRaw(variant)) {
                        if (noTransform) {
                            iis.readFully(byteArray);
                            processImageUpdate(bi,
                                           0, 0,
                                           width, height, 1, 1,
                                           destBands);
                            processImageProgress(100.0F);
                        } else {
                            iis.skipBytes(sourceRegion.y * width * numBands);
                            int skip = (scaleY - 1) * width * numBands;
                            byte[] data = new byte[width * numBands];
                            int pixelStride = scaleX * numBands;
                            int sx = sourceRegion.x * numBands;
                            int ex = width;
                            for (int i = 0, n = dsx ; i < destinationRegion.height; i++) {
                                iis.read(data);
                                for (int j = sourceRegion.x, k = sx;
                                    j < sourceRegion.x + sourceRegion.width;
                                    j+= scaleX, k += pixelStride) {
                                    for (int m = 0; m < sourceBands.length; m++)
                                        byteArray[n+ destBands[m]] = data[k + sourceBands[m]];
                                    n += sourceBands.length;
                                }
                                n += destinationRegion.x * numBands;
                                iis.skipBytes(skip);
                                processImageUpdate(bi,
                                                   0, i,
                                                   destinationRegion.width, 1, 1, 1,
                                                   destBands);
                                processImageProgress(100.0F*i/destinationRegion.height);
                            }
                        }
                    } else {
                        skipInteger(iis,
                                    (sourceRegion.y * width + sourceRegion.x) *
                                    numBands);

                        if (seleBand) {
                            byte[] data = new byte[numBands];
                            for (int i = 0, n = dsx; i < destinationRegion.height; i++) {
                                for (int j = 0; j < destinationRegion.width; j++) {
                                    for (int k = 0; k < numBands; k++)
                                        data[k] = (byte)readInteger(iis);
                                    for (int k = 0; k < sourceBands.length; k++)
                                        byteArray[n+destBands[k]] = data[sourceBands[k]];
                                    n += sourceBands.length;
                                    skipInteger(iis, skipX);
                                }
                                n += destinationRegion.x * sourceBands.length;
                                skipInteger(iis, skipY);
                                processImageUpdate(bi,
                                                   0, i,
                                                   destinationRegion.width, 1, 1, 1,
                                                   destBands);
                                processImageProgress(100.0F*i/destinationRegion.height);
                            }
                        } else
                            for (int i = 0, n = dsx; i < destinationRegion.height; i++) {
                                for (int j = 0; j < destinationRegion.width; j++) {
                                    for (int k = 0; k < numBands; k++)
                                        byteArray[n++] = (byte)readInteger(iis);
                                    skipInteger(iis, skipX);
                                }
                                n += destinationRegion.x * sourceBands.length;
                                skipInteger(iis, skipY);
                                processImageUpdate(bi,
                                                   0, i,
                                                   destinationRegion.width, 1, 1, 1,
                                                   destBands);
                                processImageProgress(100.0F*i/destinationRegion.height);
                            }
                    }
                    break;

                case DataBuffer.TYPE_USHORT:
                    DataBufferUShort sbuf =
                        (DataBufferUShort)raster.getDataBuffer();
                    short[] shortArray = sbuf.getData();
                    skipInteger(iis, sourceRegion.y * width * numBands + sourceRegion.x);

                    if (seleBand) {
                        short[] data = new short[numBands];
                        for (int i = 0, n = dsx; i < destinationRegion.height; i++) {
                            for (int j = 0; j < destinationRegion.width; j++) {
                                for (int k = 0; k < numBands; k++)
                                    data[k] = (short)readInteger(iis);
                                for (int k = 0; k < sourceBands.length; k++)
                                    shortArray[n+destBands[k]] = data[sourceBands[k]];
                                n += sourceBands.length;
                                skipInteger(iis, skipX);
                            }
                            n += destinationRegion.x * sourceBands.length;
                            skipInteger(iis, skipY);
                            processImageUpdate(bi,
                                               0, i,
                                               destinationRegion.width, 1, 1, 1,
                                               destBands);
                            processImageProgress(100.0F*i/destinationRegion.height);
                        }
                    } else
                        for (int i = 0, n = dsx; i < destinationRegion.height; i++) {
                            for (int j = 0; j < destinationRegion.width; j++) {
                                for (int k = 0; k < numBands; k++)
                                    shortArray[n++] = (short)readInteger(iis);
                                skipInteger(iis, skipX);
                            }
                            n += destinationRegion.x * sourceBands.length;
                            skipInteger(iis, skipY);
                            processImageUpdate(bi,
                                               0, i,
                                               destinationRegion.width, 1, 1, 1,
                                               destBands);
                            processImageProgress(100.0F*i/destinationRegion.height);
                        }
                    break;

                case DataBuffer.TYPE_INT:
                    DataBufferInt ibuf =
                        (DataBufferInt)raster.getDataBuffer();
                    int[] intArray = ibuf.getData();
                    skipInteger(iis, sourceRegion.y * width * numBands + sourceRegion.x);
                    if (seleBand) {
                        int[] data = new int[numBands];
                        for (int i = 0, n = dsx; i < destinationRegion.height; i++) {
                            for (int j = 0; j < destinationRegion.width; j++) {
                                for (int k = 0; k < numBands; k++)
                                    data[k] = readInteger(iis);
                                for (int k = 0; k < sourceBands.length; k++)
                                    intArray[n+destBands[k]] = data[sourceBands[k]];
                                n += sourceBands.length;
                                skipInteger(iis, skipX);
                            }
                            n += destinationRegion.x * sourceBands.length;
                            skipInteger(iis, skipY);
                            processImageUpdate(bi,
                                               0, i,
                                               destinationRegion.width, 1, 1, 1,
                                               destBands);
                            processImageProgress(100.0F*i/destinationRegion.height);
                        }
                    } else
                        for (int i = 0, n = dsx; i < destinationRegion.height; i++) {
                            for (int j = 0; j < destinationRegion.width; j++) {
                                for (int k = 0; k < numBands; k++)
                                    intArray[n++] = readInteger(iis);
                                skipInteger(iis, skipX);
                            }
                            n += destinationRegion.x * sourceBands.length;
                            skipInteger(iis, skipY);
                            processImageUpdate(bi,
                                               0, i,
                                               destinationRegion.width, 1, 1, 1,
                                               destBands);
                            processImageProgress(100.0F*i/destinationRegion.height);
                        }
                    break;
                }
                break;
        }

        if (abortRequested())
            processReadAborted();
        else
            processImageComplete();
        return bi;
    }

    public boolean canReadRaster() {
        return true;
    }

    public Raster readRaster(int imageIndex,
                             ImageReadParam param) throws IOException {
        BufferedImage bi = read(imageIndex, param);
        return bi.getData();
    }

    public void reset() {
        super.reset();
        iis = null;
        gotHeader = false;
        System.gc();
    }

    /** Returns true if file variant is raw format, false if ASCII. */
    private boolean isRaw(int v) {
        return (v >= PBM_RAW);
    }

    /** Reads the comments. */
    private void readComments(ImageInputStream stream,
                              PNMMetadata metadata) throws IOException {
        String line = null;
        int pos = -1;
        stream.mark();
        while ((line = stream.readLine()) != null &&
               (pos = line.indexOf("#")) >= 0) {
            metadata.addComment(line.substring(pos + 1).trim());
        }
        stream.reset();
    }

    /** Reads the next integer. */
    private int readInteger(ImageInputStream stream) throws IOException {
        boolean foundDigit = false;

        while (aLine == null) {
            aLine = stream.readLine();
            if (aLine == null)
                return 0;
            int pos = aLine.indexOf("#");
            if (pos == 0)
                aLine = null;
            else if (pos > 0)
                aLine = aLine.substring(0, pos - 1);

            if (aLine != null)
                token = new StringTokenizer(aLine);
        }

        while (token.hasMoreTokens()) {
            String s = token.nextToken();

            try {
                return new Integer(s).intValue();
            } catch (NumberFormatException e) {
                continue;
            }
        }

        if (!foundDigit) {
            aLine = null;
            return readInteger(stream);
        }

        return 0;
    }

    private void skipInteger(ImageInputStream stream, int num) throws IOException {
        for (int i = 0; i < num; i++)
            readInteger(stream);
    }
}
