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
 * $RCSfile: ImgReader.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:14 $
 * $State: Exp $
 *
 * Class:                   ImgReader
 *
 * Description:             Generic interface for image readers (from
 *                          file or other resource)
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
 */
package jj2000.j2k.image.input;

import jj2000.j2k.image.*;
import jj2000.j2k.*;
import java.io.*;

import java.awt.Point;

/**
 * This is the generic interface to be implemented by all image file (or other
 * resource) readers for different image file formats.
 *
 * <p>An ImgReader behaves as an ImgData object. Whenever image data is
 * requested through the getInternCompData() or getCompData() methods, the
 * image data will be read (if it is not buffered) and returned. Implementing
 * classes should not buffer large amounts of data, so as to reduce memory
 * usage.</p>
 *
 * <p>This class sets the image origin to (0,0). All default implementations
 * of the methods assume this.</p>
 *
 * <p>This class provides default implementations of many methods. These
 * default implementations assume that there is no tiling (i.e., the only tile
 * is the entire image), that the image origin is (0,0) in the canvas system
 * and that there is no component subsampling (all components are the same
 * size), but they can be overloaded by the implementating class if need
 * be.</p>
 * */
public abstract class ImgReader implements BlkImgDataSrc {

    /** The width of the image */
    protected int w;

    /** The height of the image */
    protected int h;

    /** The number of components in the image */
    protected int nc;

    /**
     * Closes the underlying file or network connection from where the
     * image data is being read.
     *
     * @exception IOException If an I/O error occurs.
     */
    public abstract void close() throws IOException;

    /**
     * Returns the width of the current tile in pixels, assuming there is
     * no-tiling. Since no-tiling is assumed this is the same as the width of
     * the image. The value of <tt>w</tt> is returned.
     *
     * @return The total image width in pixels.
     * */
    public int getTileWidth() {
        return w;
    }

    /**
     * Returns the overall height of the current tile in pixels, assuming
     * there is no-tiling. Since no-tiling is assumed this is the same as the
     * width of the image. The value of <tt>h</tt> is returned.
     *
     * @return The total image height in pixels.  */
    public int getTileHeight() {
        return h;
    }

    /** Returns the nominal tiles width */
    public int getNomTileWidth() {
        return w;
    }

    /** Returns the nominal tiles height */
    public int getNomTileHeight() {
        return h;
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
     * @see jj2000.j2k.image.ImgData
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
     * @see jj2000.j2k.image.ImgData
     * */
    public int getCompSubsY(int c) {
        return 1;
    }

    /**
     * Returns the width in pixels of the specified tile-component. This
     * default implementation assumes no tiling and no component subsampling
     * (i.e., all components, or components, have the same dimensions in
     * pixels).
     *
     * @param t Tile index
     *
     * @param c The index of the component, from 0 to C-1.
     *
     * @return The width in pixels of component <tt>c</tt> in tile<tt>t</tt>.
     * */
    public int getTileCompWidth(int t,int c) {
        if(t!=0) {
            throw new Error("Asking a tile-component width for a tile index"+
                            " greater than 0 whereas there is only one tile");
        }
        return w;
    }

    /**
     * Returns the height in pixels of the specified tile-component. This
     * default implementation assumes no tiling and no component subsampling
     * (i.e., all components, or components, have the same dimensions in
     * pixels).
     *
     * @param t The tile index
     *
     * @param c The index of the component, from 0 to C-1.
     *
     * @return The height in pixels of component <tt>c</tt> in tile
     * <tt>t</tt>.
     * */
    public int getTileCompHeight(int t,int c) {
        if(t!=0) {
            throw new Error("Asking a tile-component width for a tile index"+
                            " greater than 0 whereas there is only one tile");
        }
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
     * Changes the current tile, given the new coordinates. An
     * IllegalArgumentException is thrown if the coordinates do not correspond
     * to a valid tile. This default implementation assumes no tiling so the
     * only valid arguments are x=0, y=0.
     *
     * @param x The horizontal coordinate of the tile.
     *
     * @param y The vertical coordinate of the new tile.
     * */
    public void setTile(int x, int y) {
        if (x!=0 || y != 0) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Advances to the next tile, in standard scan-line order (by rows then
     * columns). A NoNextElementException is thrown if the current tile is the
     * last one (i.e. there is no next tile). This default implementation
     * assumes no tiling, so NoNextElementException() is always thrown.
     * */
    public void nextTile() {
        throw new NoNextElementException();
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
        if (co != null) {
            co.x = 0;
            co.y = 0;
            return co;
        }
        else {
            return new Point(0,0);
        }
    }

    /**
     * Returns the index of the current tile, relative to a standard scan-line
     * order. This default implementations assumes no tiling, so 0 is always
     * returned.
     *
     * @return The current tile's index (starts at 0).
     * */
    public int getTileIdx() {
        return 0;
    }

    /**
     * Returns the horizontal coordinate of the upper-left corner of the
     * specified component in the current tile.
     *
     * @param c The component index.
     * */
    public int getCompULX(int c) {
        return 0;
    }

    /**
     * Returns the vertical coordinate of the upper-left corner of the
     * specified component in the current tile.
     *
     * @param c The component index.
     * */
    public int getCompULY(int c) {
        return 0;
    }

    /** Returns the horizontal tile partition offset in the reference grid */
    public int getTilePartULX() {
        return 0;
    }

    /** Returns the vertical tile partition offset in the reference grid */
    public int getTilePartULY() {
        return 0;
    }

    /**
     * Returns the horizontal coordinate of the image origin, the top-left
     * corner, in the canvas system, on the reference grid.
     *
     * @return The horizontal coordinate of the image origin in the canvas
     * system, on the reference grid.
     * */
    public int getImgULX() {
        return 0;
    }

    /**
     * Returns the vertical coordinate of the image origin, the top-left
     * corner, in the canvas system, on the reference grid.
     *
     * @return The vertical coordinate of the image origin in the canvas
     * system, on the reference grid.
     * */
    public int getImgULY() {
        return 0;
    }

    /**
     * Returns the number of tiles in the horizontal and vertical
     * directions. This default implementation assumes no tiling, so (1,1) is
     * always returned.
     *
     * @param co If not null this object is used to return the information. If
     * null a new one is created and returned.
     *
     * @return The number of tiles in the horizontal (Point.x) and vertical
     * (Point.y) directions.
     * */
    public Point getNumTiles(Point co) {
        if (co != null) {
            co.x = 1;
            co.y = 1;
            return co;
        }
        else {
            return new Point(1,1);
        }
    }

    /**
     * Returns the total number of tiles in the image. This default
     * implementation assumes no tiling, so 1 is always returned.
     *
     * @return The total number of tiles in the image.
     * */
    public int getNumTiles() {
        return 1;
    }

    /**
     * Returns true if the data read was originally signed in the specified
     * component, false if not.
     *
     * @param c The index of the component, from 0 to C-1.
     *
     * @return true if the data was originally signed, false if not.
     * */
    public abstract boolean isOrigSigned(int c);

}
