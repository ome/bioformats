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
 * $RCSfile: RenderedImageSrc.java,v $
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
 * $Date: 2006/09/22 23:07:25 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg2000;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.awt.image.Raster;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;

import jj2000.j2k.image.*;
import jj2000.j2k.*;
import java.io.*;

import com.sun.media.imageioimpl.common.ImageUtil;

public class RenderedImageSrc implements BlkImgDataSrc {
    /** The width of the image */
    private int w;

    /** The height of the image */
    private int h;

    /** The tile width for encoding */
    int tileWidth;

    /** The tile height for encoding */
    int tileHeight;

    /** The tile grid offset for encoding */
    int tileXOffset, tileYOffset;

    /** The source -> destination transformation */
    int scaleX, scaleY, xOffset, yOffset;

    /** The source bands to be encoded. */
    int[] sourceBands = null;

    /** The destination upper-left corner */
    int minX, minY;

    /** The number of components in the image */
    private int nc;

    /** The number of bits that determine the nominal dynamic range */
    // XXX: Should be an int[] of length 'nc'.
    private int rb;

    /** Buffer for the 3 components of each pixel(in the current block) */
    private int[][] barr = null;

    /** Data block used only to store coordinates of the buffered blocks */
    private DataBlkInt dbi = new DataBlkInt();

    /** The line buffer. */
    private byte buf[];

    /** Temporary DataBlkInt object (needed when encoder uses floating-point
        filters). This avoid allocating new DataBlk at each time */
    private DataBlkInt intBlk;

    private RenderedImage src;
    private J2KImageWriteParamJava param;

    /** The input source raster. */
    private Raster raster;

    /** The raster for a destination tile */
    private Raster aTile;

    private Point co = new Point();

    private int dcOffset = 0;

    private boolean isBinary = false;

    private Rectangle destinationRegion;
    private Rectangle sourceRegion;

    private ColorModel cm;
    private SampleModel sm;

    private boolean noTransform = true;
    private boolean noSubband = true;

    /** Used to process abortion. */
    private J2KImageWriter writer;

    /** Indicates a <code>raster</code> rather than a <code>RenderedImage</code>
     *  to be encoded.
     */
    private boolean inputIsRaster = false;

    /**
     * Creates <code>RenderedImageSrc</code> for encoding a <code>Raster</code>.
     *
     * @param raster The <code>Raster</code> to be encoded.
     * @param param The <code>J2KImageWriteParamJava</code> used in encoding.
     * @param writer The <code>J2KImageWriter</code> performs the encoding.
     *
     * @throws IOException If an error occurs while opening the file.
     */
    public RenderedImageSrc(Raster raster,
                            J2KImageWriteParamJava param,
                            J2KImageWriter writer) {
        this.raster = raster;
        this.param = param;
        this.writer = writer;
        this.inputIsRaster = true;

        sourceRegion = param.getSourceRegion();

        if (sourceRegion == null)
            sourceRegion = new Rectangle(raster.getMinX(), raster.getMinY(),
                                         raster.getWidth(), raster.getHeight());
        else
            sourceRegion = sourceRegion.intersection(raster.getBounds());

        if (sourceRegion.isEmpty())
            throw new RuntimeException(I18N.getString("J2KImageWriterCodecLib0"));

        sm = raster.getSampleModel();
        getFromParam();
        setSampleModelAndMore();
        setTile(0, 0);
    }

    /**
     * Creates <code>RenderedImageSrc</code> for encoding a
     * <code>RenderedImage</code>.
     *
     * @param src The <code>RenderedImage</code> to be encoded.
     * @param param The <code>J2KImageWriteParamJava</code> used in encoding.
     * @param writer The <code>J2KImageWriter</code> performs the encoding.
     *
     * @throws IOException If an error occurs while opening the file.
     * */
    public RenderedImageSrc(RenderedImage src,
                            J2KImageWriteParamJava param,
                            J2KImageWriter writer) {
        this.src = src;
        this.param = param;
        this.writer = writer;

        sourceRegion = param.getSourceRegion();

        if (sourceRegion == null)
            sourceRegion = new Rectangle(src.getMinX(), src.getMinY(),
                                         src.getWidth(), src.getHeight());
        else
            sourceRegion = sourceRegion.intersection(new Rectangle(src.getMinX(),
                                                                   src.getMinY(),
                                                                   src.getWidth(),
                                                                   src.getHeight()));
        if (sourceRegion.isEmpty())
            throw new RuntimeException(I18N.getString("J2KImageWriterCodecLib0"));

        sm = src.getSampleModel();
        cm = src.getColorModel();
        getFromParam();
        setSampleModelAndMore();
    }

    private void getFromParam() {
        try {
            tileWidth = param.getTileWidth();
            tileHeight = param.getTileHeight();
            tileXOffset = param.getTileGridXOffset();
            tileYOffset = param.getTileGridYOffset();
        } catch(IllegalStateException e) {
            param.setTilingMode(param.MODE_EXPLICIT);
            if (inputIsRaster) {
                param.setTiling(raster.getWidth(), raster.getHeight(),
                                raster.getMinX(), raster.getMinY());
            } else {
                param.setTiling(src.getWidth(), src.getHeight(),
                                src.getMinX(), src.getMinY());
            }
            tileWidth = param.getTileWidth();
            tileHeight = param.getTileHeight();
            tileXOffset = param.getTileGridXOffset();
            tileYOffset = param.getTileGridYOffset();
        }

        scaleX = param.getSourceXSubsampling();
        scaleY = param.getSourceYSubsampling();
        xOffset = param.getSubsamplingXOffset();
        yOffset = param.getSubsamplingYOffset();

        sourceRegion.translate(xOffset, yOffset);
        sourceRegion.width -= xOffset;
        sourceRegion.height -= yOffset;

	xOffset = sourceRegion.x % scaleX;
	yOffset = sourceRegion.y % scaleY;

        minX = sourceRegion.x / scaleX;
        minY = sourceRegion.y / scaleY;

        w = (sourceRegion.width + scaleX - 1) / scaleX;
        h = (sourceRegion.height + scaleY - 1) / scaleY;

        tileXOffset += (minX - tileXOffset)/tileWidth * tileWidth;
        tileYOffset += (minY - tileYOffset)/tileHeight * tileHeight;

        destinationRegion = new Rectangle(minX, minY, w, h);

        if (!destinationRegion.equals(sourceRegion) ||
            tileWidth != sm.getWidth() ||
            tileHeight != sm.getHeight() ||
            (!inputIsRaster &&
             (tileXOffset != src.getTileGridXOffset() ||
             tileYOffset != src.getTileGridYOffset())) ||
            (inputIsRaster &&
             (tileXOffset != raster.getMinX() ||
             tileYOffset != raster.getMinY())))
            noTransform = false;

    }

    private void setSampleModelAndMore() {
        nc = sm.getNumBands();
        sourceBands = param.getSourceBands();
        if (sourceBands != null) {
            sm = sm.createSubsetSampleModel(sourceBands);
            noSubband = false;
        } else {
            sourceBands = new int[nc];
            for (int i = 0; i < nc; i++)
                sourceBands[i] = i;
        }

        sm = sm.createCompatibleSampleModel(tileWidth, tileHeight);
        nc = sm.getNumBands();
        isBinary = ImageUtil.isBinary(sm);

        if(cm != null) {
            // XXX: rb should be set to getComponentSize();
            rb = cm.getComponentSize(0);
            for (int i = 1; i < cm.getNumComponents(); i++)
                if (rb < cm.getComponentSize(i))
                    rb = cm.getComponentSize(i);
        } else {
            // XXX: rb should be set to getSampleSize();
            rb = sm.getSampleSize(0);
            for (int i = 1; i < sm.getNumBands(); i++)
                if (rb < sm.getSampleSize(i))
                    rb = sm.getSampleSize(i);
        }

	if (!isOrigSigned(0) && rb > 1)
            // XXX: if rb is an int[] this will have to change.
	    dcOffset = 1 << rb - 1;
    }


    public int getTilePartULX() {
	return tileXOffset;
    }

    public int getTilePartULY() {
	return tileYOffset;
    }

    /**
     * Returns the width of the current tile in pixels.
     *
     * @return The total image width in pixels.
     * */
    public int getTileWidth() {
        int width = tileWidth;
        int maxX = getImgULX() + getImgWidth();
        int x = co.x * tileWidth + tileXOffset;
        if (x + tileWidth >= maxX)
            width = maxX - x;
        return width;
    }

    /**
     * Returns the overall height of the current tile in pixels.
     *
     * @return The total image height in pixels.  */
    public int getTileHeight() {
        int height = tileHeight;
        int maxY = getImgULY() + getImgHeight();
        int y = co.y * tileHeight + tileYOffset;
        if (y + tileHeight >= maxY)
            height = maxY - y;

        return height;
    }

    public int getNomTileWidth() {
	return tileWidth;
    }

    public int getNomTileHeight() {
	return tileHeight;
    }

    /**
     * Returns the overall width of the image in pixels. This is the image's
     * width without accounting for any component subsampling or tiling. The
     * value of <tt>w</tt> is returned.
     *
     * @return The total image's width in pixels.
     * */
    public int getImgWidth() {
        return w;
    }

    /**
     * Returns the overall height of the image in pixels. This is the image's
     * height without accounting for any component subsampling or tiling. The
     * value of <tt>h</tt> is returned.
     *
     * @return The total image's height in pixels.
     * */
    public int getImgHeight() {
        return h;
    }

    /**
     * Returns the number of components in the image. The value of <tt>nc</tt>
     * is returned.
     *
     * @return The number of components in the image.
     * */
    public int getNumComps() {
        return nc;
    }

    public int getTileGridXOffset() {
        return param.getTileGridXOffset();
    }

    public int getTileGridYOffset() {
        return param.getTileGridYOffset();
    }

    public int getTileCompHeight(int t, int c) {
        return tileHeight;
    }

    public int getTileCompWidth(int t, int c) {
        return tileWidth;
    }

    /**
     * Returns the component subsampling factor in the horizontal direction,
     * for the specified component. This is, approximately, the ratio of
     * dimensions between the reference grid and the component itself, see the
     * 'ImgData' interface desription for details.
     *
     * @param c The index of the component (between 0 and C-1)
     *
     * @return The horizontal subsampling factor of component 'c'
     *
     * @see ImgData
     * */
    public int getCompSubsX(int c) {
        return 1;
    }

    /**
     * Returns the component subsampling factor in the vertical direction, for
     * the specified component. This is, approximately, the ratio of
     * dimensions between the reference grid and the component itself, see the
     * 'ImgData' interface desription for details.
     *
     * @param c The index of the component (between 0 and C-1)
     *
     * @return The vertical subsampling factor of component 'c'
     *
     * @see ImgData
     * */
    public int getCompSubsY(int c) {
        return 1;
    }

    /**
     * Returns the width in pixels of the specified component in the current
     * tile. This default implementation assumes no tiling and no component
     * subsampling (i.e., all components, or components, have the same
     * dimensions in pixels).
     *
     * @param n The index of the component, from 0 to C-1.
     *
     * @return The width in pixels of component <tt>n</tt> in the current
     * tile.
     * */
    public int getCompWidth(int n) {
        return w;
    }

    /**
     * Returns the height in pixels of the specified component in the current
     * tile. This default implementation assumes no tiling and no component
     * subsampling (i.e., all components, or components, have the same
     * dimensions in pixels).
     *
     * @param c The index of the component, from 0 to C-1.
     *
     * @return The height in pixels of component <tt>c</tt> in the current
     * tile.
     * */
    public int getCompHeight(int c) {
        return h;
    }

    /**
     * Returns the width in pixels of the specified component in the overall
     * image. This default implementation assumes no component, or component,
     * subsampling (i.e. all components have the same dimensions in pixels).
     *
     * @param c The index of the component, from 0 to C-1.
     *
     * @return The width in pixels of component <tt>c</tt> in the overall
     * image.
     * */
    public int getCompImgWidth(int c) {
        return w;
    }

    /**
     * Returns the height in pixels of the specified component in the overall
     * image. This default implementation assumes no component, or component,
     * subsampling (i.e. all components have the same dimensions in pixels).
     *
     * @param c The index of the component, from 0 to C-1.
     *
     * @return The height in pixels of component <tt>c</tt> in the overall
     * image.
     * */
    public int getCompImgHeight(int c) {
        return h;
    }

    /**
     * Changes the current tile, given the new coordinates.
     *
     * @param x The horizontal coordinate of the tile.
     *
     * @param y The vertical coordinate of the new tile.
     * */
    public void setTile(int x, int y) {
        if (x >= getNumXTiles()) {
            y += x/ getNumXTiles();
            x = x % getNumXTiles();
        }
        co.x = x;
        co.y = y;
        aTile = null;
    }

    /**
     * Advances to the next tile, in standard scan-line order (by rows then
     * columns).
     * */
    public void nextTile() {
        co.x++;
        if (co.x >= getNumXTiles()) {
            co.x = 0;
            co.y++;
        }
        setTile(co.x, co.y);
    }

    /**
     * Returns the coordinates of the current tile. This default
     * implementation assumes no-tiling, so (0,0) is returned.
     *
     * @param co If not null this object is used to return the information. If
     * null a new one is created and returned.
     *
     * @return The current tile's coordinates.
     * */
    public Point getTile(Point co) {
        if (co != null)
            return co;
        else
            return new Point(0, 0);
    }

    /**
     * Returns the index of the current tile, relative to a standard scan-line
     * order.
     *
     * @return The current tile's index (starts at 0).
     * */
    public int getTileIdx() {
        return getNumXTiles() * co.y + co.x;
    }

    /**
     * Returns the horizontal and vertical offset of the upper-left corner of
     * the current tile, in the specified component, relative to the canvas
     * origin, in the component coordinates (not in the reference grid
     * coordinates). These are the coordinates of the current tile's (not
     * active tile) upper-left corner relative to the canvas.
     *
     * @param p If not null the object is used to return the values, if null
     * a new one is created and returned.
     *
     * @param c The index of the component (between 0 and C-1)
     *
     * @return The horizontal and vertical offsets of the upper-left corner of
     * the current tile, for the specified component, relative to the canvas
     * origin, in the component coordinates.
     * */
    public Point getTileOff(Point p, int c) {
        if (p != null) {
            p.x = co.x * tileWidth + tileXOffset;
            p.y = co.y * tileHeight + tileYOffset;
            return co;
        } else
            return new Point(co.x * tileWidth + tileXOffset,
                             co.y * tileHeight + tileYOffset);
    }

    /**
     * Returns the horizontal coordinate of the upper-left corner of the
     * active tile, with respect to the canvas origin, in the component
     * coordinates, for the specified component.
     *
     * @param c The index of the component (between 0 and C-1)
     *
     * @return The horizontal coordinate of the upper-left corner of the
     * active tile, with respect to the canvas origin, for component 'c', in
     * the component coordinates.
     * */
    public int getCompULX(int c) {
        return raster.getMinX();
    }

    /**
     * Returns the vertical coordinate of the upper-left corner of the active
     * tile, with respect to the canvas origin, in the component coordinates,
     * for the specified component.
     *
     * @param c The index of the component (between 0 and C-1)
     *
     * @return The vertical coordinate of the upper-left corner of the active
     * tile, with respect to the canvas origin, for component 'c', in the
     * component coordinates.
     * */
    public int getCompULY(int c) {
        return raster.getMinY();
    }

    /**
     * Returns the horizontal coordinate of the image origin, the top-left
     * corner, in the canvas system, on the reference grid.
     *
     * @return The horizontal coordinate of the image origin in the canvas
     * system, on the reference grid.
     * */
    public int getImgULX() {
        return destinationRegion.x;
    }

    /**
     * Returns the vertical coordinate of the image origin, the top-left
     * corner, in the canvas system, on the reference grid.
     *
     * @return The vertical coordinate of the image origin in the canvas
     * system, on the reference grid.
     * */
    public int getImgULY() {
        return destinationRegion.y;
    }

    /**
     * Returns the number of tiles in the horizontal and vertical
     * directions.
     *
     * @param co If not null this object is used to return the information. If
     * null a new one is created and returned.
     *
     * @return The number of tiles in the horizontal (Point.x) and vertical
     * (Point.y) directions.
     * */
    public Point getNumTiles(Point co) {
        if (co != null) {
            co.x = getNumXTiles();
            co.y = getNumYTiles();
            return co;
        }
        else {
            return new Point(getNumXTiles(), getNumYTiles());
        }
    }

    /**
     * Returns the total number of tiles in the image. This default
     * implementation assumes no tiling, so 1 is always returned.
     *
     * @return The total number of tiles in the image.
     * */
    public int getNumTiles() {
        return getNumXTiles() * getNumYTiles();
    }

    /**
     * Returns the number of bits corresponding to the nominal range of the
     * data in the specified component. This is the value rb (range bits) that
     * was specified in the constructor, which normally is 8 for non bilevel
     * data, and 1 for bilevel data.
     *
     * <P>If this number is <i>b</b> then the nominal range is between
     * -2^(b-1) and 2^(b-1)-1, since unsigned data is level shifted to have a
     * nominal avergae of 0.
     *
     * @param c The index of the component.
     *
     * @return The number of bits corresponding to the nominal range of the
     * data. For floating-point data this value is not applicable and the
     * return value is undefined.
     * */
    public int getNomRangeBits(int c) {
        // Check component index
        // XXX: Should be component-dependent.
        return rb;
    }

    /**
     * Returns the position of the fixed point in the specified component
     * (i.e. the number of fractional bits), which is always 0 for this
     * ImgReader.
     *
     * @param c The index of the component.
     *
     * @return The position of the fixed-point (i.e. the number of fractional
     * bits). Always 0 for this ImgReader.
     * */
    public int getFixedPoint(int c) {
        // Check component index
        return 0;
    }


    /**
     * Returns, in the blk argument, the block of image data containing the
     * specifed rectangular area, in the specified component. The data is
     * returned, as a reference to the internal data, if any, instead of as a
     * copy, therefore the returned data should not be modified.
     *
     * <P> After being read the coefficients are level shifted by subtracting
     * 2^(nominal bit range - 1)
     *
     * <P>The rectangular area to return is specified by the 'ulx', 'uly', 'w'
     * and 'h' members of the 'blk' argument, relative to the current
     * tile. These members are not modified by this method. The 'offset' and
     * 'scanw' of the returned data can be arbitrary. See the 'DataBlk' class.
     *
     * <P>If the data array in <tt>blk</tt> is <tt>null</tt>, then a new one
     * is created if necessary. The implementation of this interface may
     * choose to return the same array or a new one, depending on what is more
     * efficient. Therefore, the data array in <tt>blk</tt> prior to the
     * method call should not be considered to contain the returned data, a
     * new array may have been created. Instead, get the array from
     * <tt>blk</tt> after the method has returned.
     *
     * <P>The returned data always has its 'progressive' attribute unset
     * (i.e. false).
     *
     * <P>When an I/O exception is encountered the JJ2KExceptionHandler is
     * used. The exception is passed to its handleException method. The action
     * that is taken depends on the action that has been registered in
     * JJ2KExceptionHandler. See JJ2KExceptionHandler for details.
     *
     * <P>This method implements buffering for the 3 components: When the
     * first one is asked, all the 3 components are read and stored until they
     * are needed.
     *
     * @param blk Its coordinates and dimensions specify the area to
     * return. Some fields in this object are modified to return the data.
     *
     * @param c The index of the component from which to get the data. Only 0,
     * 1 and 3 are valid.
     *
     * @return The requested DataBlk
     *
     * @see #getCompData
     *
     * @see JJ2KExceptionHandler
     */
    public final DataBlk getInternCompData(DataBlk blk, int c) {
        if (writer != null && writer.getAbortRequest())
            throw new RuntimeException(J2KImageWriter.WRITE_ABORTED);

        if (barr == null)
            barr = new int[nc][];

	// Check type of block provided as an argument
	if(blk.getDataType()!=DataBlk.TYPE_INT){
	    if(intBlk==null)
		intBlk = new DataBlkInt(blk.ulx,blk.uly,blk.w,blk.h);
	    else{
		intBlk.ulx = blk.ulx;
		intBlk.uly = blk.uly;
		intBlk.w = blk.w;
		intBlk.h = blk.h;
	    }
	    blk = intBlk;
	}

        float percentage =
            (getTileIdx() + (blk.uly + 1.0F) / blk.h) / getNumTiles();
        writer.processImageProgressWrapper(percentage * 100.0F);

        // If asking a component for the first time for this block, read the 3
        // components
        if ((barr[c] == null) ||
            (dbi.ulx > blk.ulx) || (dbi.uly > blk.uly) ||
            (dbi.ulx+dbi.w < blk.ulx+blk.w) ||
            (dbi.uly+dbi.h < blk.uly+blk.h)) {
            int k,j,i,mi;

            // Reset data arrays if needed
            if (barr[c] == null || barr[c].length < blk.w*blk.h) {
                barr[c] = new int[blk.w*blk.h];
            }
            blk.setData(barr[c]);

            for (i = (c + 1) % nc; i != c; i = (i + 1) % nc)
                if (barr[i] == null || barr[i].length < blk.w*blk.h) {
                    barr[i] = new int[blk.w*blk.h];
                }

            // set attributes of the DataBlk used for buffering
            dbi.ulx = blk.ulx;
            dbi.uly = blk.uly;
            dbi.w = blk.w;
            dbi.h = blk.h;

            // get data from the image
            if (aTile == null) {
                aTile = getTile(co.x, co.y);
                Rectangle temp = aTile.getBounds();
                aTile = aTile.createTranslatedChild(temp.x-minX,
                                                    temp.y-minY);
            }

            for (i = 0; i < nc ; i++) {
                aTile.getSamples(blk.ulx, blk.uly, blk.w, blk.h, i, barr[i]);
                for (k = 0; k < barr[i].length; k++)
                    barr[i][k] -= dcOffset;
            }
            //getByteData(raster, new Rectangle(blk.ulx, blk.uly, blk.w, blk.h), barr);

            // Set buffer attributes
            blk.setData(barr[c]);
            blk.offset = 0;
            blk.scanw = blk.w;
        } else { //Asking for the 2nd or 3rd block component
            blk.setData(barr[c]);
            blk.offset = (blk.ulx-dbi.ulx)*dbi.w+blk.ulx-dbi.ulx;
            blk.scanw = dbi.scanw;
        }

        // Turn off the progressive attribute
        blk.progressive = false;
	return blk;
    }

    /**
     * Returns, in the blk argument, a block of image data containing the
     * specifed rectangular area, in the specified component. The data is
     * returned, as a copy of the internal data, therefore the returned data
     * can be modified "in place".
     *
     * <P> After being read the coefficients are level shifted by subtracting
     * 2^(nominal bit range - 1)
     *
     * <P>The rectangular area to return is specified by the 'ulx', 'uly', 'w'
     * and 'h' members of the 'blk' argument, relative to the current
     * tile. These members are not modified by this method. The 'offset' of
     * the returned data is 0, and the 'scanw' is the same as the block's
     * width. See the 'DataBlk' class.
     *
     * <P>If the data array in 'blk' is 'null', then a new one is created. If
     * the data array is not 'null' then it is reused, and it must be large
     * enough to contain the block's data. Otherwise an 'ArrayStoreException'
     * or an 'IndexOutOfBoundsException' is thrown by the Java system.
     *
     * <P>The returned data has its 'progressive' attribute unset
     * (i.e. false).
     *
     * <P>When an I/O exception is encountered the JJ2KExceptionHandler is
     * used. The exception is passed to its handleException method. The action
     * that is taken depends on the action that has been registered in
     * JJ2KExceptionHandler. See JJ2KExceptionHandler for details.
     *
     * @param blk Its coordinates and dimensions specify the area to
     * return. If it contains a non-null data array, then it must have the
     * correct dimensions. If it contains a null data array a new one is
     * created. The fields in this object are modified to return the data.
     *
     * @param c The index of the component from which to get the data. Only
     * 0,1 and 2 are valid.
     *
     * @return The requested DataBlk
     *
     * @see #getInternCompData
     *
     * @see JJ2KExceptionHandler
     * */
    public final DataBlk getCompData(DataBlk blk, int c) {
        // NOTE: can not directly call getInterCompData since that returns
        // internally buffered data.
        int ulx,uly,w,h;

	// Check type of block provided as an argument
	if(blk.getDataType()!=DataBlk.TYPE_INT){
	    DataBlkInt tmp = new DataBlkInt(blk.ulx,blk.uly,blk.w,blk.h);
	    blk = tmp;
	}

        int bakarr[] = (int[])blk.getData();
        // Save requested block size
        ulx = blk.ulx;
        uly = blk.uly;
        w = blk.w;
        h = blk.h;
        // Force internal data buffer to be different from external
        blk.setData(null);
        getInternCompData(blk,c);
        // Copy the data
        if (bakarr == null) {
            bakarr = new int[w*h];
        }
        if (blk.offset == 0 && blk.scanw == w) {
            // Requested and returned block buffer are the same size
            System.arraycopy(blk.getData(),0,bakarr,0,w*h);
        }
        else { // Requested and returned block are different
            for (int i=h-1; i>=0; i--) { // copy line by line
                System.arraycopy(blk.getData(),blk.offset+i*blk.scanw,
                                 bakarr,i*w,w);
            }
        }
        blk.setData(bakarr);
        blk.offset = 0;
        blk.scanw = blk.w;
	return blk;
    }

    /**
     * Returns true if the data read was originally signed in the specified
     * component, false if not. This method always returns false since PPM
     * data is always unsigned.
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @return always false, since PPM data is always unsigned.
     * */
    public boolean isOrigSigned(int c) {
        if (isBinary) return true;

        // Check component index
        SampleModel sm = null;
        if (inputIsRaster)
            sm = raster.getSampleModel();
        else
            sm = src.getSampleModel();

        if (sm.getDataType() == DataBuffer.TYPE_USHORT ||
            sm.getDataType() == DataBuffer.TYPE_BYTE)
            return false;
        return true;
    }

    private int getNumXTiles() {
        int x = destinationRegion.x;
        int tx = tileXOffset;
        int tw = tileWidth;
        return ToTile(x + destinationRegion.width - 1, tx, tw) - ToTile(x, tx, tw) + 1;
    }

    private int getNumYTiles() {
        int y = destinationRegion.y;
        int ty = tileYOffset;
        int th = tileHeight;
        return ToTile(y + destinationRegion.height - 1, ty, th) - ToTile(y, ty, th) + 1;
    }

    private static int ToTile(int pos, int tileOffset, int tileSize) {
        pos -= tileOffset;
        if (pos < 0) {
            pos += 1 - tileSize;         // force round to -infinity (ceiling)
        }
        return pos/tileSize;
    }

    private Raster getTile(int tileX, int tileY) {
        int sx = tileXOffset + tileX * tileWidth;
        int sy = tileYOffset + tileY * tileHeight;
        tileX += tileXOffset / tileWidth;
        tileY += tileYOffset / tileHeight;

        if (inputIsRaster) {
            if (noTransform) {
                return raster.createChild(sx, sy, getTileWidth(), getTileHeight(),
                                          sx, sy, sourceBands);
            }

            WritableRaster ras =
                Raster.createWritableRaster(sm, new Point(sx, sy));

            int x = mapToSourceX(sx);
            int y = mapToSourceY(sy);

            int minY = raster.getMinY();
            int maxY = raster.getMinY() + raster.getHeight();

            int cTileWidth = getTileWidth();
            for (int j = 0; j < getTileHeight(); j++, sy++, y += scaleY) {
                if (y < minY || y >= maxY)
                    continue;
                Raster source = raster.createChild(x, y, (cTileWidth - 1) * scaleX + 1, 1,
                                                   x, y, null);
                int tempX = sx;
                for (int i = 0, offset = x; i < cTileWidth; i++, tempX++, offset += scaleX) {
                    for (int k = 0; k < nc; k++) {
                        int p = source.getSample(offset, y, sourceBands[k]);
                        ras.setSample(tempX, sy, k, p);
                    }
                }
            }

            return ras;

        } else {
            if (noTransform) {
                Raster ras = src.getTile(tileX, tileY);
                if (noSubband)
                    return ras;
                else {
                    return ras.createChild(sx, sy, tileWidth, tileHeight,
                                           sx, sy, sourceBands);
                }
            }

            WritableRaster ras = Raster.createWritableRaster(sm, new Point(sx, sy));

            int x = mapToSourceX(sx);
            int y = mapToSourceY(sy);

            int minY = src.getMinY();
            int maxY = src.getMinY() + src.getHeight();
	    int length = tileWidth * scaleX;

	    if (x + length >= src.getWidth())
		length = src.getWidth() - x;
	    int dLength = (length + scaleX -1 ) / scaleX;

            for (int j = 0; j < tileHeight; j++, sy++, y += scaleY) {
                if (y < minY || y >= maxY)
                    continue;

                Raster source = src.getData(new Rectangle(x, y, length, 1));

                int tempX = sx;
                for (int i = 0, offset = x; i < dLength; i++, tempX++, offset += scaleX) {

                    for (int k = 0; k < nc; k++) {
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
}
