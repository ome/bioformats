/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2017 Open Microscopy Environment:
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
 * $RCSfile: MultiResImgData.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:33 $
 * $State: Exp $
 *
 * Class:                   MultiResImgData
 *
 * Description:             The interface for classes that provide
 *                          multi-resolution image data.
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
 *
 */

package jj2000.j2k.wavelet.synthesis;
import java.awt.Point;
import jj2000.j2k.image.*;

/**
 * This interface defines methods to access image attributes (width, height,
 * number of components, etc.) of multiresolution images, such as those
 * resulting from an inverse wavelet transform. The image can be tiled or not
 * (i.e. if the image is not tiled then there is only 1 tile). It should be
 * implemented by all classes that provide multi-resolution image data, such
 * as entropy decoders, dequantizers, etc. This interface, however, does not
 * define methods to transfer image data (i.e. pixel data), that is defined by
 * other interfaces, such as 'CBlkQuantDataSrcDec'.
 *
 * <p>This interface is very similar to the 'ImgData' one. It differs only by
 * the fact that it handles multiple resolutions.</p>
 *
 * <p>Resolution levels are counted from 0 to L. Resolution level 0 is the
 * lower resolution, while L is the maximum resolution level, or full
 * resolution, which is returned by 'getMaxResLvl()'. Note that there are L+1
 * resolution levels available.</p>
 *
 * <p>As in the 'ImgData' interface a multi-resolution image lies on top of a
 * canvas. The canvas coordinates are mapped from the full resolution
 * reference grid (i.e. resolution level 'L' reference grid) to a resolution
 * level 'l' reference grid by '(x_l,y_l) =
 * (ceil(x_l/2^(L-l)),ceil(y_l/2^(L-l)))', where '(x,y)' are the full
 * resolution reference grid coordinates and '(x_l,y_l)' are the level 'l'
 * reference grid coordinates.</p>
 *
 * <p>For details on the canvas system and its implications consult the
 * 'ImgData' interface.</p>
 *
 * <p>Note that tile sizes may not be obtained by simply dividing the tile
 * size in the reference grid by the subsampling factor.</p>
 *
 * @see jj2000.j2k.image.ImgData
 *
 * @see jj2000.j2k.quantization.dequantizer.CBlkQuantDataSrcDec
 * */
public interface MultiResImgData {

    /**
     * Returns the overall width of the current tile in pixels for the given
     * resolution level. This is the tile's width without accounting for any
     * component subsampling. The resolution level is indexed from the lowest
     * number of resolution levels of all components of the current tile.
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The total current tile's width in pixels.
     * */
    public int getTileWidth(int rl);

    /**
     * Returns the overall height of the current tile in pixels, for the given
     * resolution level. This is the tile's height without accounting for any
     * component subsampling. The resolution level is indexed from the lowest
     * number of resolution levels of all components of the current tile.
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The total current tile's height in pixels.
     * */
    public int getTileHeight(int rl);

    /** Returns the nominal tiles width */
    public int getNomTileWidth();

    /** Returns the nominal tiles height */
    public int getNomTileHeight();

    /**
     * Returns the overall width of the image in pixels, for the given
     * resolution level. This is the image's width without accounting for any
     * component subsampling or tiling. The resolution level is indexed from
     * the lowest number of resolution levels of all components of the current
     * tile.
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The total image's width in pixels.
     * */
    public int getImgWidth(int rl);

    /**
     * Returns the overall height of the image in pixels, for the given
     * resolution level. This is the image's height without accounting for any
     * component subsampling or tiling. The resolution level is indexed from
     * the lowest number of resolution levels of all components of the current
     * tile.
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The total image's height in pixels.
     * */
    public int getImgHeight(int rl);

    /**
     * Returns the number of components in the image.
     *
     * @return The number of components in the image.
     * */
    public int getNumComps();

    /**
     * Returns the component subsampling factor in the horizontal direction,
     * for the specified component. This is, approximately, the ratio of
     * dimensions between the reference grid and the component itself, see the
     * 'ImgData' interface desription for details.
     *
     * @param c The index of the component (between 0 and N-1)
     *
     * @return The horizontal subsampling factor of component 'c'
     *
     * @see jj2000.j2k.image.ImgData
     * */
    public int getCompSubsX(int c);

    /**
     * Returns the component subsampling factor in the vertical direction, for
     * the specified component. This is, approximately, the ratio of
     * dimensions between the reference grid and the component itself, see the
     * 'ImgData' interface desription for details.
     *
     * @param c The index of the component (between 0 and N-1)
     *
     * @return The vertical subsampling factor of component 'c'
     *
     * @see jj2000.j2k.image.ImgData
     * */
    public int getCompSubsY(int c);

    /**
     * Returns the width in pixels of the specified tile-component for the
     * given resolution level.
     *
     * @param t Tile index
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The width in pixels of component <tt>c</tt> in tile <tt>t</tt>
     * for resolution <tt>rl</tt>.
     * */
    public int getTileCompWidth(int t,int c,int rl);

    /**
     * Returns the height in pixels of the specified tile-component for the
     * given resolution level.
     *
     * @param t The tile index.
     * 
     * @param c The index of the component, from 0 to N-1.
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The height in pixels of component <tt>c</tt> in tile
     * <tt>t</tt>.
     * */
    public int getTileCompHeight(int t,int c,int rl);

    /**
     * Returns the width in pixels of the specified component in the overall
     * image, for the given resolution level.
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The width in pixels of component <tt>c</tt> in the overall
     * image.
     * */
    public int getCompImgWidth(int c,int rl);

    /**
     * Returns the height in pixels of the specified component in the overall
     * image, for the given resolution level.
     *
     * @param n The index of the component, from 0 to N-1.
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The height in pixels of component <tt>n</tt> in the overall
     * image.
     * */
    public int getCompImgHeight(int n, int rl);

    /**
     * Changes the current tile, given the new indexes. An
     * IllegalArgumentException is thrown if the indexes do not correspond to
     * a valid tile.
     *
     * @param x The horizontal indexes the tile.
     *
     * @param y The vertical indexes of the new tile.
     * */
    public void setTile(int x,int y);

    /**
     * Advances to the next tile, in standard scan-line order (by rows then
     * columns). An NoNextElementException is thrown if the current tile is
     * the last one (i.e. there is no next tile).
     * */
    public void nextTile();

    /**
     * Returns the indexes of the current tile. These are the horizontal and
     * vertical indexes of the current tile.
     *
     * @param co If not null this object is used to return the information. If
     * null a new one is created and returned.
     *
     * @return The current tile's indexes (vertical and horizontal indexes).
     * */
    public Point getTile(Point co);

    /**
     * Returns the index of the current tile, relative to a standard scan-line
     * order.
     *
     * @return The current tile's index (starts at 0).
     * */
    public int getTileIdx();

    /**
     * Returns the horizontal coordinate of the upper-left corner of the
     * specified resolution in the given component of the current tile.
     *
     * @param c The component index.
     *
     * @param rl The resolution level index.
     * */
    public int getResULX(int c,int rl);

    /**
     * Returns the vertical coordinate of the upper-left corner of the
     * specified resolution in the given component of the current tile.
     *
     * @param c The component index.
     *
     * @param rl The resolution level index.
     * */
    public int getResULY(int c,int rl);

    /**
     * Returns the horizontal coordinate of the image origin, the top-left
     * corner, in the canvas system, on the reference grid at the specified
     * resolution level.  The resolution level is indexed from the lowest
     * number of resolution levels of all components of the current tile.
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The horizontal coordinate of the image origin in the canvas
     * system, on the reference grid.
     * */
    public int getImgULX(int rl);

    /**
     * Returns the vertical coordinate of the image origin, the top-left
     * corner, in the canvas system, on the reference grid at the specified
     * resolution level.  The resolution level is indexed from the lowest
     * number of resolution levels of all components of the current tile.
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The vertical coordinate of the image origin in the canvas
     * system, on the reference grid.
     * */
    public int getImgULY(int rl);

    /** Returns the horizontal tile partition offset in the reference grid */
    public int getTilePartULX();

    /** Returns the vertical tile partition offset in the reference grid */
    public int getTilePartULY();

    /**
     * Returns the number of tiles in the horizontal and vertical directions.
     *
     * @param co If not null this object is used to return the information. If
     * null a new one is created and returned.
     *
     * @return The number of tiles in the horizontal (Point.x) and vertical
     * (Point.y) directions.
     * */
    public Point getNumTiles(Point co);

    /**
     * Returns the total number of tiles in the image.
     *
     * @return The total number of tiles in the image.
     * */
    public int getNumTiles();

    /** 
     * Returns the specified synthesis subband tree 
     * 
     * @param t Tile index.
     *
     * @param c Component index.
     * */
    public SubbandSyn getSynSubbandTree(int t,int c);
}
