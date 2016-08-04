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
 * $RCSfile: Tiler.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:13 $
 * $State: Exp $
 *
 * Class:                   Tiler
 *
 * Description:             An object to create TiledImgData from
 *                          ImgData
 *
 *
 * COPYRIGHT:
 *
 * This software module was originally developed by Raphaël Grosbois and
 * Diego Santa Cruz (Swiss Federal Institute of Technology-EPFL); Joel
 * Askelöf (Ericsson Radio Systems AB); and Bertrand Berthelot, David
 * Bouchard, Félix Henry, Gerard Mozelle and Patrice Onno (Canon Research
 * Centre France S.A) in the course of development of the JPEG2000
 * standard as specified by ISO/IEC 15444 (JPEG 2000 Standard). This
 * software module is an implementation of a part of the JPEG 2000
 * Standard. Swiss Federal Institute of Technology-EPFL, Ericsson Radio
 * Systems AB and Canon Research Centre France S.A (collectively JJ2000
 * Partners) agree not to assert against ISO/IEC and users of the JPEG
 * 2000 Standard (Users) any of their rights under the copyright, not
 * including other intellectual property rights, for this software module
 * with respect to the usage by ISO/IEC and Users of this software module
 * or modifications thereof for use in hardware or software products
 * claiming conformance to the JPEG 2000 Standard. Those intending to use
 * this software module in hardware or software products are advised that
 * their use may infringe existing patents. The original developers of
 * this software module, JJ2000 Partners and ISO/IEC assume no liability
 * for use of this software module or modifications thereof. No license
 * or right to this software module is granted for non JPEG 2000 Standard
 * conforming products. JJ2000 Partners have full right to use this
 * software module for his/her own purpose, assign or donate this
 * software module to any third party and to inhibit third parties from
 * using this software module for non JPEG 2000 Standard conforming
 * products. This copyright notice must be included in all copies or
 * derivative works of this software module.
 *
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.image;
import java.awt.Point;

import jj2000.j2k.util.*;
import jj2000.j2k.*;

/**
 * This class places an image in the canvas coordinate system, tiles it, if so
 * specified, and performs the coordinate conversions transparently. The
 * source must be a 'BlkImgDataSrc' which is not tiled and has a the image
 * origin at the canvas origin (i.e. it is not "canvased"), or an exception is
 * thrown by the constructor. A tiled and "canvased" output is given through
 * the 'BlkImgDataSrc' interface. See the 'ImgData' interface for a
 * description of the canvas and tiling.
 *
 * <p>All tiles produced are rectangular, non-overlapping and their union
 * covers all the image. However, the tiling may not be uniform, depending on
 * the nominal tile size, tiling origin, component subsampling and other
 * factors. Therefore it might not be assumed that all tiles are of the same
 * width and height.</p>
 *
 * <p>The nominal dimension of the tiles is the maximal one, in the reference
 * grid. All the components of the image have the same number of tiles.</p>
 *
 * @see ImgData
 * @see BlkImgDataSrc
 * */
public class Tiler extends ImgDataAdapter implements BlkImgDataSrc {

    /** The source of image data */
    private BlkImgDataSrc src = null;

    /** Horizontal coordinate of the upper left hand reference grid point.*/
    private int x0siz;

    /** Vertical coordinate of the upper left hand reference grid point.*/
    private int y0siz;

    /** The horizontal coordinate of the tiling origin in the canvas system,
     * on the reference grid. */
    private int xt0siz;

    /** The vertical coordinate of the tiling origin in the canvas system, on
     * the reference grid. */
    private int yt0siz;

    /** The nominal width of the tiles, on the reference grid. If 0 then there
     * is no tiling in that direction. */
    private int xtsiz;

    /** The nominal height of the tiles, on the reference grid. If 0 then
     * there is no tiling in that direction. */
    private int ytsiz;

    /** The number of tiles in the horizontal direction. */
    private int ntX;

    /** The number of tiles in the vertical direction. */
    private int ntY;

    /** The component width in the current active tile, for each component */
    private int compW[] = null;

    /** The component height in the current active tile, for each component */
    private int compH[] = null;

    /** The horizontal coordinates of the upper-left corner of the components
     * in the current tile */
    private int tcx0[] = null;

    /** The vertical coordinates of the upper-left corner of the components in
     * the current tile. */
    private int tcy0[] = null;

    /** The horizontal index of the current tile */
    private int tx;

    /** The vertical index of the current tile */
    private int ty;

    /** The width of the current tile, on the reference grid. */
    private int tileW;

    /** The height of the current tile, on the reference grid. */
    private int tileH;

    /**
     * Constructs a new tiler with the specified 'BlkImgDataSrc' source,
     * image origin, tiling origin and nominal tile size.
     *
     * @param src The 'BlkImgDataSrc' source from where to get the image
     * data. It must not be tiled and the image origin must be at '(0,0)' on
     * its canvas.
     *
     * @param ax The horizontal coordinate of the image origin in the canvas
     * system, on the reference grid (i.e. the image's top-left corner in the
     * reference grid).
     *
     * @param ay The vertical coordinate of the image origin in the canvas
     * system, on the reference grid (i.e. the image's top-left corner in the
     * reference grid).
     *
     * @param px The horizontal tiling origin, in the canvas system, on the
     * reference grid. It must satisfy 'px<=ax'.
     *
     * @param py The vertical tiling origin, in the canvas system, on the
     * reference grid. It must satisfy 'py<=ay'.
     *
     * @param nw The nominal tile width, on the reference grid. If 0 then
     * there is no tiling in that direction.
     *
     * @param nh The nominal tile height, on the reference grid. If 0 then
     * there is no tiling in that direction.
     *
     * @exception IllegalArgumentException If src is tiled or "canvased", or
     * if the arguments do not satisfy the specified constraints.
     * */
    public Tiler(BlkImgDataSrc src,int ax,int ay,int px,int py,int nw,int nh) {
        super(src);

        // Initialize
        this.src = src;
        this.x0siz = ax;
        this.y0siz = ay;
        this.xt0siz = px;
        this.yt0siz = py;
        this.xtsiz = nw;
        this.ytsiz = nh;

        // Verify that input is not tiled
/*
	if (src.getNumTiles()!=1) {
            throw new IllegalArgumentException("Source is tiled");
        }
*/
        // Verify that source is not "canvased"
/*
        if (src.getImgULX()!=0 || src.getImgULY()!=0) {
            throw new IllegalArgumentException("Source is \"canvased\"");
        }
*/
        // Verify that arguments satisfy trivial requirements
        if (x0siz<0 || y0siz<0 || xt0siz<0 || yt0siz<0 || xtsiz<0 || ytsiz<0
            || xt0siz>x0siz || yt0siz>y0siz) {
            throw new IllegalArgumentException("Invalid image origin, "+
                                               "tiling origin or nominal "+
                                               "tile size");
        }

        // If no tiling has been specified, creates a unique tile with maximum
        // dimension.
        if (xtsiz==0) xtsiz = x0siz+src.getImgWidth()-xt0siz;
        if (ytsiz==0) ytsiz = y0siz+src.getImgHeight()-yt0siz;

        // Automatically adjusts xt0siz,yt0siz so that tile (0,0) always
        // overlaps with the image.
        if (x0siz-xt0siz>=xtsiz) {
            xt0siz += ((x0siz-xt0siz)/xtsiz)*xtsiz;
        }
        if (y0siz-yt0siz>=ytsiz) {
            yt0siz += ((y0siz-yt0siz)/ytsiz)*ytsiz;
        }
        if (x0siz-xt0siz>=xtsiz || y0siz-yt0siz>=ytsiz) {
            FacilityManager.getMsgLogger().
                printmsg(MsgLogger.INFO,"Automatically adjusted tiling "+
                         "origin to equivalent one ("+xt0siz+","+
                         yt0siz+") so that "+
                         "first tile overlaps the image");
        }

        // Calculate the number of tiles
        ntX = (int)Math.ceil((x0siz+src.getImgWidth() - xt0siz)/(double)xtsiz);
        ntY = (int)Math.ceil((y0siz+src.getImgHeight() - yt0siz)/(double)ytsiz);
    }

    /**
     * Returns the overall width of the current tile in pixels. This is the
     * tile's width without accounting for any component subsampling.
     *
     * @return The total current tile width in pixels.
     * */
    public final int getTileWidth() {
        return tileW;
    }

    /**
     * Returns the overall height of the current tile in pixels. This is the
     * tile's width without accounting for any component subsampling.
     *
     * @return The total current tile height in pixels.
     * */
    public final int getTileHeight() {
        return tileH;
    }

    /**
     * Returns the width in pixels of the specified tile-component.
     *
     * @param t Tile index
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @return The width of specified tile-component.
     * */
    public final int getTileCompWidth(int t,int c) {
        if(t!=getTileIdx()) {
            throw new Error("Asking the width of a tile-component which is "+
                            "not in the current tile (call setTile() or "+
                            "nextTile() methods before).");
        }
        return compW[c];
    }

    /**
     * Returns the height in pixels of the specified tile-component.
     *
     * @param t The tile index.
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @return The height of specified tile-component.
     * */
    public final int getTileCompHeight(int t,int c) {
        if(t!=getTileIdx()) {
            throw new Error("Asking the width of a tile-component which is "+
                            "not in the current tile (call setTile() or "+
                            "nextTile() methods before).");
        }
        return compH[c];
    }

    /**
     * Returns the position of the fixed point in the specified
     * component. This is the position of the least significant integral
     * (i.e. non-fractional) bit, which is equivalent to the number of
     * fractional bits. For instance, for fixed-point values with 2 fractional
     * bits, 2 is returned. For floating-point data this value does not apply
     * and 0 should be returned. Position 0 is the position of the least
     * significant bit in the data.
     *
     * @param c The index of the component.
     *
     * @return The position of the fixed-point, which is the same as the
     * number of fractional bits. For floating-point data 0 is returned.
     * */
    public int getFixedPoint(int c) {
        return src.getFixedPoint(c);
    }

    /**
     * Returns, in the blk argument, a block of image data containing the
     * specifed rectangular area, in the specified component. The data is
     * returned, as a reference to the internal data, if any, instead of as a
     * copy, therefore the returned data should not be modified.
     *
     * <p>The rectangular area to return is specified by the 'ulx', 'uly', 'w'
     * and 'h' members of the 'blk' argument, relative to the current
     * tile. These members are not modified by this method. The 'offset' and
     * 'scanw' of the returned data can be arbitrary. See the 'DataBlk'
     * class.</p>
     *
     * <p>This method, in general, is more efficient than the 'getCompData()'
     * method since it may not copy the data. However if the array of returned
     * data is to be modified by the caller then the other method is probably
     * preferable.</p>
     *
     * <p>If the data array in <tt>blk</tt> is <tt>null</tt>, then a new one
     * is created if necessary. The implementation of this interface may
     * choose to return the same array or a new one, depending on what is more
     * efficient. Therefore, the data array in <tt>blk</tt> prior to the
     * method call should not be considered to contain the returned data, a
     * new array may have been created. Instead, get the array from
     * <tt>blk</tt> after the method has returned.</p>
     *
     * <p>The returned data may have its 'progressive' attribute set. In this
     * case the returned data is only an approximation of the "final"
     * data.</p>
     *
     * @param blk Its coordinates and dimensions specify the area to return,
     * relative to the current tile. Some fields in this object are modified
     * to return the data.
     *
     * @param c The index of the component from which to get the data.
     *
     * @return The requested DataBlk
     *
     * @see #getCompData
     * */
    public final DataBlk getInternCompData(DataBlk blk,int c) {
        // Check that block is inside tile
        if (blk.ulx<0 || blk.uly<0 || blk.w>compW[c] || blk.h>compH[c]) {
            throw new IllegalArgumentException("Block is outside the tile");
        }
        // Translate to the sources coordinates
        int incx = (int)Math.ceil(x0siz/(double)src.getCompSubsX(c));
        int incy = (int)Math.ceil(y0siz/(double)src.getCompSubsY(c));
        blk.ulx -= incx;
        blk.uly -= incy;
        blk = src.getInternCompData(blk,c);
        // Translate back to the tiled coordinates
        blk.ulx += incx;
        blk.uly += incy;
	return blk;
    }

    /**
     * Returns, in the blk argument, a block of image data containing the
     * specifed rectangular area, in the specified component. The data is
     * returned, as a copy of the internal data, therefore the returned data
     * can be modified "in place".
     *
     * <p>The rectangular area to return is specified by the 'ulx', 'uly', 'w'
     * and 'h' members of the 'blk' argument, relative to the current
     * tile. These members are not modified by this method. The 'offset' of
     * the returned data is 0, and the 'scanw' is the same as the block's
     * width. See the 'DataBlk' class.</p>
     *
     * <p>This method, in general, is less efficient than the
     * 'getInternCompData()' method since, in general, it copies the
     * data. However if the array of returned data is to be modified by the
     * caller then this method is preferable.</p>
     *
     * <p>If the data array in 'blk' is 'null', then a new one is created. If
     * the data array is not 'null' then it is reused, and it must be large
     * enough to contain the block's data. Otherwise an 'ArrayStoreException'
     * or an 'IndexOutOfBoundsException' is thrown by the Java system.</p>
     *
     * <p>The returned data may have its 'progressive' attribute set. In this
     * case the returned data is only an approximation of the "final"
     * data.</p>
     *
     * @param blk Its coordinates and dimensions specify the area to return,
     * relative to the current tile. If it contains a non-null data array,
     * then it must be large enough. If it contains a null data array a new
     * one is created. Some fields in this object are modified to return the
     * data.
     *
     * @param c The index of the component from which to get the data.
     *
     * @return The requested DataBlk
     *
     * @see #getInternCompData
     * */
    public final DataBlk getCompData(DataBlk blk,int c) {
        // Check that block is inside tile
        if (blk.ulx<0 || blk.uly<0 || blk.w>compW[c] || blk.h>compH[c]) {
            throw new IllegalArgumentException("Block is outside the tile");
        }
        // Translate to the source's coordinates
        int incx = (int)Math.ceil(x0siz/(double)src.getCompSubsX(c));
        int incy = (int)Math.ceil(y0siz/(double)src.getCompSubsY(c));
        blk.ulx -= incx;
        blk.uly -= incy;
        blk = src.getCompData(blk,c);
        // Translate back to the tiled coordinates
        blk.ulx += incx;
        blk.uly += incy;
	return blk;
    }

    /**
     * Changes the current tile, given the new tile indexes. An
     * IllegalArgumentException is thrown if the coordinates do not correspond
     * to a valid tile.
     *
     * @param x The horizontal index of the tile.
     *
     * @param y The vertical index of the new tile.
     * */
    public final void setTile(int x,int y) {
        src.setTile(x, y);

        // Check tile indexes
        if (x<0 || y<0 || x>=ntX || y>=ntY) {
            throw new IllegalArgumentException("Tile's indexes out of bounds");
        }

        // Set new current tile
        tx = x;
        ty = y;
        // Calculate tile origins
        int tx0 = (x!=0) ? xt0siz+x*xtsiz : x0siz;
        int ty0 = (y!=0) ? yt0siz+y*ytsiz : y0siz;
        int tx1 = (x!=ntX-1) ? (xt0siz+(x+1)*xtsiz) :
            (x0siz+src.getImgWidth());
        int ty1 = (y!=ntY-1) ? (yt0siz+(y+1)*ytsiz) :
            (y0siz+src.getImgHeight());
        // Set general variables
        tileW = tx1 - tx0;
        tileH = ty1 - ty0;
        // Set component specific variables
        int nc = src.getNumComps();
        if(compW==null) compW = new int[nc];
        if(compH==null) compH = new int[nc];
        if(tcx0==null) tcx0 = new int[nc];
        if(tcy0==null) tcy0 = new int[nc];
        for (int i=0; i<nc ; i++) {
            tcx0[i] = (int)Math.ceil(tx0/(double)src.getCompSubsX(i));
            tcy0[i] = (int)Math.ceil(ty0/(double)src.getCompSubsY(i));
	    compW[i] = (int)Math.ceil(tx1/(double)src.getCompSubsX(i)) -
                tcx0[i];
	    compH[i] = (int)Math.ceil(ty1/(double)src.getCompSubsY(i)) -
                tcy0[i];
        }
    }

    /**
     * Advances to the next tile, in standard scan-line order (by rows then
     * columns). An NoNextElementException is thrown if the current tile is
     * the last one (i.e. there is no next tile).
     * */
    public final void nextTile() {
        if (tx==ntX-1 && ty==ntY-1) { // Already at last tile
            throw new NoNextElementException();
        } else if (tx<ntX-1) { // If not at end of current tile line
            setTile(tx+1,ty);
        } else { // First tile at next line
            setTile(0,ty+1);
        }
    }

    /**
     * Returns the horizontal and vertical indexes of the current tile.
     *
     * @param co If not null this object is used to return the
     * information. If null a new one is created and returned.
     *
     * @return The current tile's horizontal and vertical indexes..
     * */
    public final Point getTile(Point co) {
        if (co != null) {
            co.x = tx;
            co.y = ty;
            return co;
        } else {
            return new Point(tx,ty);
        }
    }

    /**
     * Returns the index of the current tile, relative to a standard scan-line
     * order.
     *
     * @return The current tile's index (starts at 0).
     * */
    public final int getTileIdx() {
        return ty*ntX+tx;
    }

    /**
     * Returns the horizontal coordinate of the upper-left corner of the
     * specified component in the current tile.
     *
     * @param c The component index.
     * */
    public final int getCompULX(int c) {
        return tcx0[c];
    }

    /**
     * Returns the vertical coordinate of the upper-left corner of the
     * specified component in the current tile.
     *
     * @param c The component index.
     * */
    public final int getCompULY(int c) {
        return tcy0[c];
    }

    /** Returns the horizontal tile partition offset in the reference grid */
    public int getTilePartULX() {
        return xt0siz;
    }

    /** Returns the vertical tile partition offset in the reference grid */
    public int getTilePartULY() {
        return yt0siz;
    }

    /**
     * Returns the horizontal coordinate of the image origin, the top-left
     * corner, in the canvas system, on the reference grid.
     *
     * @return The horizontal coordinate of the image origin in the canvas
     * system, on the reference grid.
     * */
    public final int getImgULX() {
        return x0siz;
    }

    /**
     * Returns the vertical coordinate of the image origin, the top-left
     * corner, in the canvas system, on the reference grid.
     *
     * @return The vertical coordinate of the image origin in the canvas
     * system, on the reference grid.
     * */
    public final int getImgULY() {
        return y0siz;
    }

    /**
     * Returns the number of tiles in the horizontal and vertical directions.
     *
     * @param co If not null this object is used to return the information. If
     * null a new one is created and returned.
     *
     * @return The number of tiles in the horizontal (Point.x) and vertical
     * (Point.y) directions.
     * */
    public final Point getNumTiles(Point co) {
        if (co != null) {
            co.x = ntX;
            co.y = ntY;
            return co;
        } else {
            return new Point(ntX,ntY);
        }
    }

    /**
     * Returns the total number of tiles in the image.
     *
     * @return The total number of tiles in the image.
     * */
    public final int getNumTiles() {
        return ntX*ntY;
    }

    /**
     * Returns the nominal width of the tiles in the reference grid.
     *
     * @return The nominal tile width, in the reference grid.
     * */
    public final int getNomTileWidth() {
        return xtsiz;
    }

    /**
     * Returns the nominal width of the tiles in the reference grid.
     *
     * @return The nominal tile width, in the reference grid.
     * */
    public final int getNomTileHeight() {
        return ytsiz;
    }

    /**
     * Returns the tiling origin, referred to as '(xt0siz,yt0siz)' in the
     * codestream header (SIZ marker segment).
     *
     * @param co If not null this object is used to return the information. If
     * null a new one is created and returned.
     *
     * @return The coordinate of the tiling origin, in the canvas system, on
     * the reference grid.
     *
     * @see ImgData
     * */
    public final Point getTilingOrigin(Point co) {
        if (co != null) {
            co.x = xt0siz;
            co.y = yt0siz;
            return co;
        } else {
            return new Point(xt0siz,yt0siz);
        }
    }

    /**
     * Returns a String object representing Tiler's informations
     *
     * @return Tiler's infos in a string
     * */
    public String toString() {
	return "Tiler: source= "+src+
	    "\n"+getNumTiles()+" tile(s), nominal width="+xtsiz+
	    ", nominal height="+ytsiz;
    }
}
