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
 * $RCSfile: J2KImageReader.java,v $
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
 * $Date: 2006/10/05 01:08:30 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg2000;

import java.awt.Rectangle;
import java.awt.Point;

import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import com.sun.media.imageio.plugins.jpeg2000.J2KImageReadParam;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;

import java.io.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import jj2000.j2k.quantization.dequantizer.*;
import jj2000.j2k.wavelet.synthesis.*;
import jj2000.j2k.image.invcomptransf.*;
import jj2000.j2k.fileformat.reader.*;
import jj2000.j2k.codestream.reader.*;
import jj2000.j2k.entropy.decoder.*;
import jj2000.j2k.codestream.*;
import jj2000.j2k.decoder.*;
import jj2000.j2k.image.*;
import jj2000.j2k.util.*;
import jj2000.j2k.roi.*;
import jj2000.j2k.io.*;
import jj2000.j2k.*;

/** This class is the Java Image IO plugin reader for JPEG 2000 JP2 image file
 *  format.  It has the capability to load the compressed bilevel images,
 *  color-indexed byte images, or multi-band images in byte/ushort/short/int
 *  data type.  It may subsample the image, select bands, clip the image,
 *  and shift the decoded image origin if the proper decoding parameter
 *  are set in the provided <code>J2KImageReadParam</code>.
 */
public class J2KImageReader extends ImageReader implements MsgLogger {
    /** The input stream where reads from */
    private ImageInputStream iis = null;

    /** Stream position when setInput() was called. */
    private long streamPosition0;

    /** Indicates whether the header is read. */
    private boolean gotHeader = false;

    /** The image width. */
    private int width;

    /** The image height. */
    private int height;

    /** Image metadata, valid for the imageMetadataIndex only. */
    private J2KMetadata imageMetadata = null;

    /** The image index for the cached metadata. */
    private int imageMetadataIndex = -1;

    /** The J2K HeaderDecoder defined in jj2000 packages.  Used to extract image
     *  header information.
     */
    private HeaderDecoder hd;

    /** The J2KReadState for this reading session based on the current input
     *  and J2KImageReadParam.
     */
    private J2KReadState readState = null;

    /**
     * Whether to log JJ2000 messages.
     */
    private boolean logJJ2000Msg = false;

    /** Wrapper for the protected method <code>computeRegions</code>.  So it
     *  can be access from the classes which are not in <code>ImageReader</code>
     *  hierarchy.
     */
    public static void computeRegionsWrapper(ImageReadParam param,
                                             boolean allowZeroDestOffset,
                                             int srcWidth,
                                             int srcHeight,
                                             BufferedImage image,
                                             Rectangle srcRegion,
                                             Rectangle destRegion) {
        if (srcRegion == null) {
            throw new IllegalArgumentException(I18N.getString("J2KImageReader0"));
        }
        if (destRegion == null) {
            throw new IllegalArgumentException(I18N.getString("J2KImageReader1"));
        }

        // Clip that to the param region, if there is one
        int periodX = 1;
        int periodY = 1;
        int gridX = 0;
        int gridY = 0;
        if (param != null) {
            Rectangle paramSrcRegion = param.getSourceRegion();
            if (paramSrcRegion != null) {
                srcRegion.setBounds(srcRegion.intersection(paramSrcRegion));
            }
            periodX = param.getSourceXSubsampling();
            periodY = param.getSourceYSubsampling();
            gridX = param.getSubsamplingXOffset();
            gridY = param.getSubsamplingYOffset();
            srcRegion.translate(gridX, gridY);
            srcRegion.width -= gridX;
            srcRegion.height -= gridY;
            if(allowZeroDestOffset) {
                destRegion.setLocation(param.getDestinationOffset());
            } else {
                Point destOffset = param.getDestinationOffset();
                if(destOffset.x != 0 || destOffset.y != 0) {
                    destRegion.setLocation(param.getDestinationOffset());
                }
            }
        }

        // Now clip any negative destination offsets, i.e. clip
        // to the top and left of the destination image
        if (destRegion.x < 0) {
            int delta = -destRegion.x*periodX;
            srcRegion.x += delta;
            srcRegion.width -= delta;
            destRegion.x = 0;
        }
        if (destRegion.y < 0) {
            int delta = -destRegion.y*periodY;
            srcRegion.y += delta;
            srcRegion.height -= delta;
            destRegion.y = 0;
        }

        // Now clip the destination Region to the subsampled width and height
        int subsampledWidth = (srcRegion.width + periodX - 1)/periodX;
        int subsampledHeight = (srcRegion.height + periodY - 1)/periodY;
        destRegion.width = subsampledWidth;
        destRegion.height = subsampledHeight;

        // Now clip that to right and bottom of the destination image,
        // if there is one, taking subsampling into account
        if (image != null) {
            Rectangle destImageRect = new Rectangle(0, 0,
                                                    image.getWidth(),
                                                    image.getHeight());
            destRegion.setBounds(destRegion.intersection(destImageRect));
            if (destRegion.isEmpty()) {
                throw new IllegalArgumentException
                    (I18N.getString("J2KImageReader2"));
            }

            int deltaX = destRegion.x + subsampledWidth - image.getWidth();
            if (deltaX > 0) {
                srcRegion.width -= deltaX*periodX;
            }
            int deltaY =  destRegion.y + subsampledHeight - image.getHeight();
            if (deltaY > 0) {
                srcRegion.height -= deltaY*periodY;
            }
        }
        if (srcRegion.isEmpty() || destRegion.isEmpty()) {
            throw new IllegalArgumentException(I18N.getString("J2KImageReader3"));
        }
    }

    /** Wrapper for the protected method <code>checkReadParamBandSettings</code>.
     *  So it can be access from the classes which are not in
     *  <code>ImageReader</code> hierarchy.
     */
    public static void checkReadParamBandSettingsWrapper(ImageReadParam param,
                                                  int numSrcBands,
                                                  int numDstBands) {
        checkReadParamBandSettings(param, numSrcBands, numDstBands);
    }

    /**
     * Convert a rectangle provided in the coordinate system of the JPEG2000
     * reference grid to coordinates at a lower resolution level where zero
     * denotes the lowest resolution level.
     *
     * @param r A rectangle in references grid coordinates.
     * @param maxLevel The highest resolution level in the image.
     * @param level The resolution level of the returned rectangle.
     * @param subX The horizontal subsampling step size.
     * @param subY The vertical subsampling step size.
     * @return The parameter rectangle converted to a lower resolution level.
     * @throws IllegalArgumentException if <core>r</code> is <code>null</code>,
     * <code>maxLevel</code> or <code>level</code> is negative, or
     * <code>level</code> is greater than <code>maxLevel</code>.
     */
    static Rectangle getReducedRect(Rectangle r, int maxLevel, int level,
                                    int subX, int subY) {
        if(r == null) {
            throw new IllegalArgumentException("r == null!");
        } else if(maxLevel < 0 || level < 0) {
            throw new IllegalArgumentException("maxLevel < 0 || level < 0!");
        } else if(level > maxLevel) {
            throw new IllegalArgumentException("level > maxLevel");
        }

        // At the highest level; return the parameter.
        if(level == maxLevel && subX == 1 && subY == 1) {
            return r;
        }

        // Resolution divisor is step*2^(maxLevel - level).
        int divisor = 1 << (maxLevel - level);
        int divX = divisor*subX;
        int divY = divisor*subY;

        // Convert upper left and lower right corners.
        int x1 = (r.x + divX - 1)/divX;
        int x2 = (r.x + r.width + divX - 1)/divX;
        int y1 = (r.y + divY - 1)/divY;
        int y2 = (r.y + r.height + divY - 1)/divY;

        // Create lower resolution rectangle and return.
        return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    /** Wrapper for the protected method <code>processImageUpdate</code>
     *  So it can be access from the classes which are not in
     *  <code>ImageReader</code> hierarchy.
     */
    public void processImageUpdateWrapper(BufferedImage theImage,
                                      int minX, int minY,
                                      int width, int height,
                                      int periodX, int periodY,
                                      int[] bands) {
        processImageUpdate(theImage,
                                  minX, minY,
                                  width, height,
                                  periodX, periodY,
                                  bands);
    }

    /** Wrapper for the protected method <code>processImageProgress</code>
     *  So it can be access from the classes which are not in
     *  <code>ImageReader</code> hierarchy.
     */
    public void processImageProgressWrapper(float percentageDone) {
        processImageProgress(percentageDone);
    }

    /** Constructs <code>J2KImageReader</code> from the provided
     *  <code>ImageReaderSpi</code>.
     */
    public J2KImageReader(ImageReaderSpi originator) {
        super(originator);

        this.logJJ2000Msg = Boolean.getBoolean("jj2000.j2k.decoder.log");

        FacilityManager.registerMsgLogger(null, this);
    }

    /** Overrides the method defined in the superclass. */
    public void setInput(Object input,
                         boolean seekForwardOnly,
                         boolean ignoreMetadata) {
        super.setInput(input, seekForwardOnly, ignoreMetadata);
        this.ignoreMetadata = ignoreMetadata;
        iis = (ImageInputStream) input; // Always works
        imageMetadata = null;
        try {
            this.streamPosition0 = iis.getStreamPosition();
        } catch(IOException e) {
            // XXX ignore
        }
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

    public int getTileGridXOffset(int imageIndex) throws IOException {
        checkIndex(imageIndex);
        readHeader();
        return hd.getTilingOrigin(null).x;
    }

    public int getTileGridYOffset(int imageIndex) throws IOException {
        checkIndex(imageIndex);
        readHeader();
        return hd.getTilingOrigin(null).y;
    }

    public int getTileWidth(int imageIndex) throws IOException {
        checkIndex(imageIndex);
        readHeader();
        return hd.getNomTileWidth();
    }

    public int getTileHeight(int imageIndex) throws IOException {
        checkIndex(imageIndex);
        readHeader();
        return hd.getNomTileHeight();
    }

    private void checkIndex(int imageIndex) {
        if (imageIndex != 0) {
            throw new IndexOutOfBoundsException(I18N.getString("J2KImageReader4"));
        }
    }

    public void readHeader() {
        if (gotHeader)
            return;

        if (readState == null) {
            try {
                iis.seek(streamPosition0);
            } catch(IOException e) {
                // XXX ignore
            }

            readState =
                new J2KReadState(iis,
                                 new J2KImageReadParamJava(getDefaultReadParam()),
                                 this);
        }

        hd = readState.getHeader();
        gotHeader = true;

        this.width = hd.getImgWidth();
        this.height = hd.getImgHeight();
    }

    public Iterator getImageTypes(int imageIndex)
        throws IOException {
        checkIndex(imageIndex);
        readHeader();
        if (readState != null) {
            ArrayList list = new ArrayList();
            list.add(new ImageTypeSpecifier(readState.getColorModel(),
                                            readState.getSampleModel()));
            return list.iterator();
        }
        return null;
    }

    public ImageReadParam getDefaultReadParam() {
        return new J2KImageReadParam();
    }

    public IIOMetadata getImageMetadata(int imageIndex)
        throws IOException {
        checkIndex(imageIndex);
        if (ignoreMetadata)
            return null;

        if (imageMetadata == null) {
            iis.mark();
            imageMetadata = new J2KMetadata(iis, this);
            iis.reset();
        }
        return imageMetadata;
    }

    public IIOMetadata getStreamMetadata() throws IOException {
        return null;
    }

    public BufferedImage read(int imageIndex, ImageReadParam param)
        throws IOException {
        checkIndex(imageIndex);
        clearAbortRequest();
        processImageStarted(imageIndex);

        if (param == null)
            param = getDefaultReadParam();

        param = new J2KImageReadParamJava(param);

        if (!ignoreMetadata) {
            imageMetadata = new J2KMetadata();
            iis.seek(streamPosition0);
            readState = new J2KReadState(iis,
                                         (J2KImageReadParamJava)param,
                                         imageMetadata,
                                         this);
        } else {
            iis.seek(streamPosition0);
            readState = new J2KReadState(iis,
                                         (J2KImageReadParamJava)param,
                                         this);
        }

        BufferedImage bi = readState.readBufferedImage();
        if (abortRequested())
            processReadAborted();
        else
            processImageComplete();
        return bi;
    }

    public RenderedImage readAsRenderedImage(int imageIndex,
                                             ImageReadParam param)
                                             throws IOException {
        checkIndex(imageIndex);
        RenderedImage ri = null;
        clearAbortRequest();
        processImageStarted(imageIndex);

        if (param == null)
            param = getDefaultReadParam();

        param = new J2KImageReadParamJava(param);
        if (!ignoreMetadata) {
            if (imageMetadata == null)
                imageMetadata = new J2KMetadata();
            ri = new J2KRenderedImage(iis,
                                        (J2KImageReadParamJava)param,
                                        imageMetadata,
                                        this);
        }
        else
            ri = new J2KRenderedImage(iis, (J2KImageReadParamJava)param, this);
        if (abortRequested())
            processReadAborted();
        else
            processImageComplete();
        return ri;
    }

    public boolean canReadRaster() {
        return true;
    }

    public boolean isRandomAccessEasy(int imageIndex) throws IOException {
        checkIndex(imageIndex);
        return false;
    }

    public Raster readRaster(int imageIndex,
                             ImageReadParam param) throws IOException {
        checkIndex(imageIndex);
        processImageStarted(imageIndex);

        if (param == null) {
            param = getDefaultReadParam();
        }
        param = new J2KImageReadParamJava(param);

        if (!ignoreMetadata) {
            imageMetadata = new J2KMetadata();
            iis.seek(streamPosition0);
            readState = new J2KReadState(iis,
                                         (J2KImageReadParamJava)param,
                                         imageMetadata,
                                         this);
        } else {
            iis.seek(streamPosition0);
            readState = new J2KReadState(iis,
                                         (J2KImageReadParamJava)param,
                                         this);
        }

        Raster ras = readState.readAsRaster();
        if (abortRequested())
            processReadAborted();
        else
            processImageComplete();
        return ras;
    }

    public boolean isImageTiled(int imageIndex) {
        checkIndex(imageIndex);
        readHeader();
        if (readState != null) {
            RenderedImage image = new J2KRenderedImage(readState);
            if (image.getNumXTiles() * image.getNumYTiles() > 0)
                return true;
            return false;
        }
        return false;
    }

    public void reset() {
        // reset local Java structures
        super.reset();

        iis = null;
        gotHeader = false;
        imageMetadata = null;
        readState = null;
        System.gc();
    }

    /** This method wraps the protected method <code>abortRequested</code>
     *  to allow the abortions be monitored by <code>J2KReadState</code>.
     */
    public boolean getAbortRequest() {
        return abortRequested();
    }

    private ImageTypeSpecifier getImageType(int imageIndex)
        throws IOException {
        checkIndex(imageIndex);
        readHeader();
        if (readState != null) {
            return new ImageTypeSpecifier(readState.getColorModel(),
                                          readState.getSampleModel());
        }
        return null;
    }

    // --- Begin jj2000.j2k.util.MsgLogger implementation ---
    public void flush() {
        // Do nothing.
    }

    public void println(String str, int flind, int ind) {
        printmsg(INFO, str);
    }

    public void printmsg(int sev, String msg) {
        if(logJJ2000Msg) {
            String msgSev;
            switch(sev) {
            case ERROR:
                msgSev = "ERROR";
                break;
            case INFO:
                msgSev = "INFO";
                break;
            case LOG:
                msgSev = "LOG";
                break;
            case WARNING:
            default:
                msgSev = "WARNING";
                break;
            }

            processWarningOccurred("[JJ2000 "+msgSev+"] "+msg);
        }
    }
    // --- End jj2000.j2k.util.MsgLogger implementation ---
}
