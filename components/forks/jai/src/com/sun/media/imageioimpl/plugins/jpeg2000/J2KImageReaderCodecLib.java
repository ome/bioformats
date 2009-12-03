/*
 * $RCSfile: J2KImageReaderCodecLib.java,v $
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
 * $Revision: 1.4 $
 * $Date: 2006/09/29 20:19:55 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg2000;

import com.sun.media.imageio.plugins.jpeg2000.J2KImageReadParam;

import java.awt.Point;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.metadata.IIOMetadata;
import java.awt.image.BufferedImage;

import com.sun.media.imageioimpl.common.SimpleRenderedImage;

import com.sun.medialib.codec.jp2k.Decoder;
import com.sun.medialib.codec.jp2k.Size;
import com.sun.medialib.codec.jiio.*;

public class J2KImageReaderCodecLib extends ImageReader {
    /** Wrapper for the protected method <code>processImageUpdate</code>
     *  So it can be access from the classes which are not in
     *  <code>ImageReader</code> hierachy.
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
     *  <code>ImageReader</code> hierachy.
     */
    public void processImageProgressWrapper(float percentageDone) {
        processImageProgress(percentageDone);
    }

    /** The input stream where reads from */
    private ImageInputStream iis = null;

    /** Stream position when setInput() was called. */
    private long streamPosition0;

    /** Indicates whether the header is read. */
    private boolean gotHeader = false;

    /** The image width. */
    private int width = -1;

    /** The image height. */
    private int height = -1;

    /** The image tile width. */
    private int tileWidth = -1;

    /** The image tile height. */
    private int tileHeight = -1;

    /** The image tile grid X offset. */
    private int tileGridXOffset = 0;

    /** The image tile grid Y offset. */
    private int tileGridYOffset = 0;

    /** Image metadata, valid for the imageMetadataIndex only. */
    private J2KMetadata imageMetadata = null;

    /** The RenderedImage decoded from the stream. */
    SimpleRenderedImage image = null;

    public J2KImageReaderCodecLib(ImageReaderSpi originator) {
          super(originator);
    }

    /** Overrides the method defined in the superclass. */
    public void setInput(Object input,
                         boolean seekForwardOnly,
                         boolean ignoreMetadata) {
        super.setInput(input, seekForwardOnly, ignoreMetadata);
        this.ignoreMetadata = ignoreMetadata;
        iis = (ImageInputStream) input; // Always works
        iis.mark(); // Mark the initial position.
        imageMetadata = null;
        try {
            this.streamPosition0 = iis.getStreamPosition();
        } catch(IOException e) {
            // XXX ignore
        }
    }

    public ImageReadParam getDefaultReadParam() {
        return new J2KImageReadParam();
    }
    public int getNumImages(boolean allowSearch) throws java.io.IOException {
        if(input == null) {
            throw new IllegalStateException(I18N.getString("J2KImageReader6"));
        } else if(seekForwardOnly) {
            throw new IllegalStateException(I18N.getString("J2KImageReader7"));
        }

        return 1;
    }

    public Iterator getImageTypes(int imageIndex) throws java.io.IOException {
        checkIndex(imageIndex);
	readHeader();
        if (image != null) {
            ArrayList list = new ArrayList();
            list.add(new ImageTypeSpecifier(image.getColorModel(),
                                            image.getSampleModel()));
            return list.iterator();
        }
        return null;
    }

    public int getWidth(int imageIndex) throws java.io.IOException {
        checkIndex(imageIndex);
        readHeader();
        return width;
    }

    public int getHeight(int imageIndex) throws java.io.IOException {
        checkIndex(imageIndex);
        readHeader();
        return height;
    }

    public int getTileGridXOffset(int imageIndex) throws IOException {
        checkIndex(imageIndex);
        readHeader();
        return tileGridXOffset;
    }

    public int getTileGridYOffset(int imageIndex) throws IOException {
        checkIndex(imageIndex);
        readHeader();
        return tileGridYOffset;
    }

    public int getTileWidth(int imageIndex) throws IOException {
        checkIndex(imageIndex);
        readHeader();
        return tileWidth;
    }

    public int getTileHeight(int imageIndex) throws IOException {
        checkIndex(imageIndex);
        readHeader();
        return tileHeight;
    }

    /**
     * Returns <code>true</code> if the image is organized into
     * <i>tiles</i>, that is, equal-sized non-overlapping rectangles.
     *
     * <p> A reader plug-in may choose whether or not to expose tiling
     * that is present in the image as it is stored.  It may even
     * choose to advertise tiling when none is explicitly present.  In
     * general, tiling should only be advertised if there is some
     * advantage (in speed or space) to accessing individual tiles.
     * Regardless of whether the reader advertises tiling, it must be
     * capable of reading an arbitrary rectangular region specified in
     * an <code>ImageReadParam</code>.
     *
     * <p> A reader for which all images are guaranteed to be tiled,
     * or are guaranteed not to be tiled, may return <code>true</code>
     * or <code>false</code> respectively without accessing any image
     * data.  In such cases, it is not necessary to throw an exception
     * even if no input source has been set or the image index is out
     * of bounds.
     *
     * <p> The default implementation just returns <code>false</code>.
     *
     * @param imageIndex the index of the image to be queried.
     *
     * @return <code>true</code> if the image is tiled.
     *
     * @exception IllegalStateException if an input source is required
     * to determine the return value, but none has been set.
     * @exception IndexOutOfBoundsException if an image must be
     * accessed to determine the return value, but the supplied index
     * is out of bounds.
     * @exception IOException if an error occurs during reading.
     */
    public boolean isImageTiled(int imageIndex) throws IOException {
        int w = getWidth(imageIndex);
        int tw = getTileWidth(imageIndex);
        if(tw > 0 && ((w + tw - 1)/tw) > 1) {
            return true;
        }

        int h = getHeight(imageIndex);
        int th = getTileHeight(imageIndex);
        if(th > 0 && ((h + th - 1)/th) > 1) {
            return true;
        }

        return false;
    }

    public IIOMetadata getStreamMetadata() throws java.io.IOException {
        return null;
    }

    public IIOMetadata getImageMetadata(int imageIndex) throws
                                                        java.io.IOException {
        if (ignoreMetadata)
            return null;

        checkIndex(imageIndex);

        if (imageMetadata == null) {
            try {
                iis.reset(); // Reset to initial position.
                iis.mark(); // Re-mark initial position.
                if (image == null ||
                    !(image instanceof J2KRenderedImageCodecLib))
                    image = new J2KRenderedImageCodecLib(iis, this, null);
                imageMetadata =
                    ((J2KRenderedImageCodecLib)image).readImageMetadata();
            } catch(IOException ioe) {
                throw ioe;
            } catch(RuntimeException re) {
                throw re;
            } finally {
                iis.reset(); // Reset to initial position.
                iis.mark(); // Re-mark initial position.
            }
        }
        return imageMetadata;
    }

    public boolean isRandomAccessEasy(int imageIndex) throws IOException {
        // No check done because neither input source nor image is
        // required to determine the return value.
        return true;
    }

    public RenderedImage readAsRenderedImage(int imageIndex,
                                            ImageReadParam param)
                                            throws java.io.IOException {
        checkIndex(imageIndex);
        if (param == null)
            param = getDefaultReadParam();
        clearAbortRequest();
        processImageStarted(0);

        if(param instanceof J2KImageReadParam &&
           ((J2KImageReadParam)param).getResolution() >= 0) {
            // XXX Workaround for java.sun.com change request 5089981:
            // fall back to Java reader for lower resolution levels.
            // This code should be removed when this problem is fixed
            // in the codecLib JPEG2000 decoder.
            ImageReader jreader = new J2KImageReader(null);
            iis.seek(streamPosition0);
            jreader.setInput(iis);
            image =
                (SimpleRenderedImage)jreader.readAsRenderedImage(imageIndex,
                                                                 param);
        } else {
            image = new J2KRenderedImageCodecLib(iis,
                                                 this,
                                                 param);
        }
        if (abortRequested())
            processReadAborted();
        else
            processImageComplete();
        return image;
    }

    public BufferedImage read(int imageIndex,
                              ImageReadParam param) throws java.io.IOException {
        checkIndex(imageIndex);
        clearAbortRequest();
        if (param == null)
            param = getDefaultReadParam();
        processImageStarted(imageIndex);

        if(param instanceof J2KImageReadParam &&
           ((J2KImageReadParam)param).getResolution() >= 0) {
            // XXX Workaround for java.sun.com change request 5089981:
            // fall back to Java reader for lower resolution levels.
            // This code should be removed when this problem is fixed
            // in the codecLib JPEG2000 decoder.
            ImageReader jreader = new J2KImageReader(null);
            iis.seek(streamPosition0);
            jreader.setInput(iis);
            if (abortRequested())
                processReadAborted();
            else
                processImageComplete();
            return jreader.read(imageIndex, param);
        }

        BufferedImage bi = param.getDestination();
        iis.reset(); // Reset to initial position.
        iis.mark(); // Re-mark initial position.
        // XXX Need to add a try-catch IOException block and reset/mark iis.
        image = new J2KRenderedImageCodecLib(iis,
                                             this,
                                             param);
        J2KRenderedImageCodecLib jclibImage = (J2KRenderedImageCodecLib)image;
        Point offset = param.getDestinationOffset();
        WritableRaster raster;

        if (bi == null) {
            ColorModel colorModel = jclibImage.getColorModel();
            SampleModel sampleModel = jclibImage.getSampleModel();

            // If the destination type is specified, use the color model of it.
            ImageTypeSpecifier type = param.getDestinationType();
            if (type != null)
                colorModel = type.getColorModel();

            raster = Raster.createWritableRaster(
                sampleModel.createCompatibleSampleModel(jclibImage.getMinX()+
                                                        jclibImage.getWidth(),
                                                        jclibImage.getMinY() +
                                                        jclibImage.getHeight()),
                new Point(0, 0));

            bi = new BufferedImage(colorModel,
                                   raster,
                                   colorModel != null ?
                                   colorModel.isAlphaPremultiplied() : false,
                                   new Hashtable());
        } else {
            raster = bi.getWritableTile(0, 0);
        }

        jclibImage.setDestImage(bi);
        jclibImage.readAsRaster(raster);
        jclibImage.clearDestImage();

        if (abortRequested())
            processReadAborted();
        else
            processImageComplete();
        return bi;
    }

    public Raster readRaster(int imageIndex,
                             ImageReadParam param) throws IOException {
        BufferedImage bi = read(imageIndex, param);
        return bi.getWritableTile(0, 0);
    }

    public void readHeader() throws java.io.IOException {
        if (gotHeader)
            return;

        try {
            iis.reset(); // Reset to initial position.
            iis.mark(); // Re-mark initial position.

            if (image == null)
                image = new J2KRenderedImageCodecLib(iis, this, null);

            this.width = image.getWidth();
            this.height = image.getHeight();
            this.tileWidth = image.getTileWidth();
            this.tileHeight = image.getTileHeight();
            this.tileGridXOffset = image.getTileGridXOffset();
            this.tileGridYOffset = image.getTileGridYOffset();
        } catch(IOException ioe) {
            throw ioe;
        } catch(RuntimeException re) {
            throw re;
        } finally {
            iis.reset(); // Reset to initial position.
            iis.mark(); // Re-mark initial position.
        }

        this.gotHeader = true;
    }

    private void checkIndex(int imageIndex) {
        if(input == null) {
            throw new IllegalStateException(I18N.getString("J2KImageReader6"));
        }
        if (imageIndex != 0) {
            throw new IndexOutOfBoundsException(I18N.getString("J2KImageReader4"));
        }
    }

    /** This method wraps the protected method <code>abortRequested</code>
     *  to allow the abortions be monitored by <code>J2KReadState</code>.
     */
    public boolean getAbortRequest() {
        return abortRequested();
    }

    public void reset() {
        super.reset();
        iis = null;
        gotHeader = false;
        width = -1;
        height = -1;
        tileWidth = -1;
        tileHeight = -1;
        tileGridXOffset = 0;
        tileGridYOffset = 0;
        imageMetadata = null;
        image = null;
    }
}
