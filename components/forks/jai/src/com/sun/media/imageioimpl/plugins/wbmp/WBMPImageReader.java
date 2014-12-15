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
 * $RCSfile: WBMPImageReader.java,v $
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
 * $Revision: 1.2 $
 * $Date: 2006/04/21 00:02:26 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.wbmp;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
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

import com.sun.media.imageioimpl.common.ImageUtil;

/** This class is the Java Image IO plugin reader for WBMP images.
 *  It may subsample the image, clip the image,
 *  and shift the decoded image origin if the proper decoding parameter
 *  are set in the provided <code>WBMPImageReadParam</code>.
 */
public class WBMPImageReader extends ImageReader {
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

    private int wbmpType;

    private WBMPMetadata metadata;

    /** Constructs <code>WBMPImageReader</code> from the provided
     *  <code>ImageReaderSpi</code>.
     */
    public WBMPImageReader(ImageReaderSpi originator) {
        super(originator);
    }

    /** Overrides the method defined in the superclass. */
    public void setInput(Object input,
                         boolean seekForwardOnly,
                         boolean ignoreMetadata) {
        super.setInput(input, seekForwardOnly, ignoreMetadata);
        iis = (ImageInputStream) input; // Always works
	gotHeader = false;
    }

    /** Overrides the method defined in the superclass. */
    public int getNumImages(boolean allowSearch) throws IOException {
	if (iis == null) {
	    throw new IllegalStateException(I18N.getString("GetNumImages0"));
	}
	if (seekForwardOnly && allowSearch) {
	    throw new IllegalStateException(I18N.getString("GetNumImages1"));
	}
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

    public boolean isRandomAccessEasy(int imageIndex) throws IOException {
        checkIndex(imageIndex);
        return true;
    }

    private void checkIndex(int imageIndex) {
        if (imageIndex != 0) {
            throw new IndexOutOfBoundsException(I18N.getString("WBMPImageReader0"));
        }
    }

    public void readHeader() throws IOException {
        if (gotHeader) {
	    // Seek to where the image data starts, since that is where
	    // the stream pointer should be after header is read
	    iis.seek(imageDataOffset);
            return;
	}

	if (iis == null) {
	    throw new IllegalStateException(I18N.getString("WBMPImageReader1"));
	}

	metadata = new WBMPMetadata();
	wbmpType = iis.readByte();   // TypeField
	byte fixHeaderField = iis.readByte();

	// check for valid wbmp image
	if (fixHeaderField != 0
	    || !isValidWbmpType(wbmpType)) {
	    throw new IIOException(I18N.getString("WBMPImageReader2"));
	}

	metadata.wbmpType = wbmpType;

        // Read image width
        width = ImageUtil.readMultiByteInteger(iis);
	metadata.width = width;

        // Read image height
        height = ImageUtil.readMultiByteInteger(iis);
	metadata.height = height;

        gotHeader = true;
	// Store the stream position where the image data starts
	imageDataOffset = iis.getStreamPosition();
    }

    public Iterator getImageTypes(int imageIndex)
        throws IOException {
        checkIndex(imageIndex);
	readHeader();

        BufferedImage bi =
            new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
        ArrayList list = new ArrayList(1);
        list.add(new ImageTypeSpecifier(bi));
        return list.iterator();
    }

    public ImageReadParam getDefaultReadParam() {
        return new ImageReadParam();
    }

    public IIOMetadata getImageMetadata(int imageIndex)
        throws IOException {
        checkIndex(imageIndex);
	if (metadata == null) {
	    readHeader();
	}
        return metadata;
    }

    public IIOMetadata getStreamMetadata() throws IOException {
        return null;
    }

    public BufferedImage read(int imageIndex, ImageReadParam param)
        throws IOException {

	if (iis == null) {
	    throw new IllegalStateException(I18N.getString("WBMPImageReader1"));
	}

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
        int xOffset = param.getSubsamplingXOffset();
        int yOffset = param.getSubsamplingYOffset();

        // If the destination is provided, then use it.  Otherwise, create new one
        BufferedImage bi = param.getDestination();

        if (bi == null)
            bi = new BufferedImage(destinationRegion.x + destinationRegion.width,
                              destinationRegion.y + destinationRegion.height,
                              BufferedImage.TYPE_BYTE_BINARY);

	boolean noTransform =
	    destinationRegion.equals(new Rectangle(0, 0, width, height)) &&
	    destinationRegion.equals(new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));

	// Get the image data.
        WritableRaster tile = bi.getWritableTile(0, 0);

        // Get the SampleModel.
        MultiPixelPackedSampleModel sm =
            (MultiPixelPackedSampleModel)bi.getSampleModel();

        if (noTransform) {
	    if (abortRequested()) {
		processReadAborted();
		return bi;
	    }

            // If noTransform is necessary, read the data.
            iis.read(((DataBufferByte)tile.getDataBuffer()).getData(),
                     0, height*sm.getScanlineStride());
            processImageUpdate(bi,
                               0, 0,
                               width, height, 1, 1,
                               new int[]{0});
            processImageProgress(100.0F);
        } else {
            int len = (this.width + 7) / 8;
            byte[] buf = new byte[len];
            byte[] data = ((DataBufferByte)tile.getDataBuffer()).getData();
            int lineStride = sm.getScanlineStride();
            iis.skipBytes(len * sourceRegion.y);
            int skipLength = len * (scaleY - 1);

            // cache the values to avoid duplicated computation
            int[] srcOff = new int[destinationRegion.width];
            int[] destOff = new int[destinationRegion.width];
            int[] srcPos = new int[destinationRegion.width];
            int[] destPos = new int[destinationRegion.width];

            for (int i = destinationRegion.x, x = sourceRegion.x, j = 0;
                i < destinationRegion.x + destinationRegion.width;
                    i++, j++, x += scaleX) {
                srcPos[j] = x >> 3;
                srcOff[j] = 7 - (x & 7);
                destPos[j] = i >> 3;
                destOff[j] = 7 - (i & 7);
            }

            for (int j = 0, y = sourceRegion.y,
                k = destinationRegion.y * lineStride;
                j < destinationRegion.height; j++, y+=scaleY) {

                if (abortRequested())
                    break;
                iis.read(buf, 0, len);
                for (int i = 0; i < destinationRegion.width; i++) {
                    //get the bit and assign to the data buffer of the raster
                    int v = (buf[srcPos[i]] >> srcOff[i]) & 1;
                    data[k + destPos[i]] |= v << destOff[i];
                }

                k += lineStride;
                iis.skipBytes(skipLength);
                        processImageUpdate(bi,
                                           0, j,
                                           destinationRegion.width, 1, 1, 1,
                                           new int[]{0});
                        processImageProgress(100.0F*j/destinationRegion.height);
            }
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
    }

    /*
     * This method verifies that given byte is valid wbmp type marker.
     * At the moment only 0x0 marker is described by wbmp spec.
     */
    boolean isValidWbmpType(int type) {
	return type == 0;
    }
}
