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
 * $RCSfile: MultiResImgDataAdapter.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:33 $
 * $State: Exp $
 *
 * Class:                   MultiResImgDataAdapter
 *
 * Description:             A default implementation of the MultiResImgData
 *                          interface that has and MultiResImgData source
 *                          and just returns the values of the source.
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
 * This class provides a default implementation for the methods of the
 * 'MultiResImgData' interface. The default implementation consists just in
 * returning the value of the source, where the source is another
 * 'MultiResImgData' object.
 *
 * <p>This abstract class can be used to facilitate the development of other
 * classes that implement 'MultiResImgData'. For example a dequantizer can
 * inherit from this class and all the trivial methods do not have to be
 * reimplemented.</p>
 *
 * <p>If the default implementation of a method provided in this class does
 * not suit a particular implementation of the 'MultiResImgData' interface,
 * the method can be overriden to implement the proper behaviour.</p>
 *
 * @see MultiResImgData
 * */
public abstract class MultiResImgDataAdapter implements MultiResImgData {

    /** Index of the current tile */
    protected int tIdx = 0;

    /** The MultiResImgData source */
    protected MultiResImgData mressrc;

    /**
     * Instantiates the MultiResImgDataAdapter object specifying the
     * MultiResImgData source.
     *
     * @param src From where to obrtain the MultiResImgData values.
     * */
    protected MultiResImgDataAdapter(MultiResImgData src) {
        mressrc = src;
    }

    /**
     * Returns the overall width of the current tile in pixels, for the given
     * resolution level. This is the tile's width without accounting for any
     * component subsampling.
     *
     * <p>This default implementation returns the value of the source.</p>
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The total current tile's width in pixels.
     * */
    public int getTileWidth(int rl) {
        return mressrc.getTileWidth(rl);
    }

    /**
     * Returns the overall height of the current tile in pixels, for the given
     * resolution level. This is the tile's height without accounting for any
     * component subsampling.
     *
     * <p>This default implementation returns the value of the source.</p>
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The total current tile's height in pixels.
     * */
    public int getTileHeight(int rl) {
        return mressrc.getTileHeight(rl);
    }

    /** Returns the nominal tiles width */
    public int getNomTileWidth() {
        return mressrc.getNomTileWidth();
    }

    /** Returns the nominal tiles height */
    public int getNomTileHeight() {
        return mressrc.getNomTileHeight();
    }

    /**
     * Returns the overall width of the image in pixels, for the given
     * resolution level. This is the image's width without accounting for any
     * component subsampling or tiling.
     *
     * <p>This default implementation returns the value of the source.</p>
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The total image's width in pixels.
     * */
    public int getImgWidth(int rl) {
        return mressrc.getImgWidth(rl);
    }

    /**
     * Returns the overall height of the image in pixels, for the given
     * resolution level. This is the image's height without accounting for any
     * component subsampling or tiling.
     *
     * <p>This default implementation returns the value of the source.</p>
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The total image's height in pixels.
     * */
    public int getImgHeight(int rl) {
        return mressrc.getImgHeight(rl);
    }
    
    /**
     * Returns the number of components in the image.
     *
     * <p>This default implementation returns the value of the source.</p>
     *
     * @return The number of components in the image.
     * */
    public int getNumComps() {
        return mressrc.getNumComps();
    }

    /**
     * Returns the component subsampling factor in the horizontal direction,
     * for the specified component. This is, approximately, the ratio of
     * dimensions between the reference grid and the component itself, see the
     * 'ImgData' interface desription for details.
     *
     * <p>This default implementation returns the value of the source.</p>
     *
     * @param c The index of the component (between 0 and N-1)
     *
     * @return The horizontal subsampling factor of component 'c'
     *
     * @see jj2000.j2k.image.ImgData
     * */
    public int getCompSubsX(int c) {
        return mressrc.getCompSubsX(c);
    }

    /**
     * Returns the component subsampling factor in the vertical direction, for
     * the specified component. This is, approximately, the ratio of
     * dimensions between the reference grid and the component itself, see the
     * 'ImgData' interface desription for details.
     *
     * <p>This default implementation returns the value of the source.</p>
     *
     * @param c The index of the component (between 0 and N-1)
     *
     * @return The vertical subsampling factor of component 'c'
     *
     * @see jj2000.j2k.image.ImgData
     * */
    public int getCompSubsY(int c) {
        return mressrc.getCompSubsY(c);
    }

    /**
     * Returns the width in pixels of the specified tile-component for the
     * given resolution level.
     *
     * <p>This default implementation returns the value of the source.</p>
     *
     * @param t Tile index.
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The width in pixels of component <tt>c</tt> in tile <tt>t</tt>
     * for resolution level <tt>rl</tt>.
     * */
    public int getTileCompWidth(int t,int c,int rl) {
        return mressrc.getTileCompWidth(t,c,rl);
    }

    /**
     * Returns the height in pixels of the specified tile-component for the
     * given resolution level.
     *
     * <p>This default implementation returns the value of the source.</p>
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
    public int getTileCompHeight(int t,int c,int rl) {
        return mressrc.getTileCompHeight(t,c,rl);
    }

    /**
     * Returns the width in pixels of the specified component in the overall
     * image, for the given resolution level.
     *
     * <p>This default implementation returns the value of the source.</p>
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The width in pixels of component <tt>c</tt> in the overall
     * image.
     * */
    public int getCompImgWidth(int c,int rl) {
        return mressrc.getCompImgWidth(c,rl);
    }
    
    /**
     * Returns the height in pixels of the specified component in the overall
     * image, for the given resolution level.
     *
     * <P>This default implementation returns the value of the source.
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The height in pixels of component <tt>c</tt> in the overall
     * image.
     * */
    public int getCompImgHeight(int c,int rl) {
        return mressrc.getCompImgHeight(c,rl);
    }

    /**
     * Changes the current tile, given the new indexes. An
     * IllegalArgumentException is thrown if the indexes do not correspond to
     * a valid tile.
     *
     * <p>This default implementation just changes the tile in the source.</p>
     *
     * @param x The horizontal indexes the tile.
     *
     * @param y The vertical indexes of the new tile.
     * */
    public void setTile(int x,int y) {
        mressrc.setTile(x,y);
	tIdx = getTileIdx();
    }

    /**
     * Advances to the next tile, in standard scan-line order (by rows then
     * columns). An NoNextElementException is thrown if the current tile is
     * the last one (i.e. there is no next tile).
     *
     * <p>This default implementation just changes the tile in the source.</p>
     * */
    public void nextTile() {
        mressrc.nextTile();
	tIdx = getTileIdx();
    }

    /**
     * Returns the indexes of the current tile. These are the horizontal and
     * vertical indexes of the current tile.
     *
     * <p>This default implementation returns the value of the source.</p>
     *
     * @param co If not null this object is used to return the information. If
     * null a new one is created and returned.
     *
     * @return The current tile's indexes (vertical and horizontal indexes).
     * */
    public Point getTile(Point co) {
        return mressrc.getTile(co);
    }

    /**
     * Returns the index of the current tile, relative to a standard scan-line
     * order.
     *
     * <p>This default implementation returns the value of the source.</p>
     *
     * @return The current tile's index (starts at 0).
     * */
    public int getTileIdx() {
        return mressrc.getTileIdx();
    }

    /**
     * Returns the horizontal coordinate of the upper-left corner of the
     * specified resolution level in the given component of the current tile. 
     *
     * <p>This default implementation returns the value of the source.</p>
     *
     * @param c The component index.
     *
     * @param rl The resolution level index.
     * */
    public int getResULX(int c,int rl) {
        return mressrc.getResULX(c,rl);
    }

    /**
     * Returns the vertical coordinate of the upper-left corner of the
     * specified resolution in the given component of the current tile. 
     *
     * <p>This default implementation returns the value of the source.</p>
     *
     * @param c The component index.
     *
     * @param rl The resolution level index.
     * */
    public int getResULY(int c,int rl) {
        return mressrc.getResULY(c,rl);
    }

    /** Returns the horizontal tile partition offset in the reference grid */
    public int getTilePartULX() {
        return mressrc.getTilePartULX();
    }

    /** Returns the vertical tile partition offset in the reference grid */
    public int getTilePartULY() {
        return mressrc.getTilePartULY();
    }

    /**
     * Returns the horizontal coordinate of the image origin, the top-left
     * corner, in the canvas system, on the reference grid at the specified
     * resolution level.
     *
     * <p>This default implementation returns the value of the source.</p>
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The horizontal coordinate of the image origin in the canvas
     * system, on the reference grid.
     * */
    public int getImgULX(int rl) {
        return mressrc.getImgULX(rl);
    }

    /**
     * Returns the vertical coordinate of the image origin, the top-left
     * corner, in the canvas system, on the reference grid at the specified
     * resolution level.
     *
     * <p>This default implementation returns the value of the source.</p>
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The vertical coordinate of the image origin in the canvas
     * system, on the reference grid.
     * */
    public int getImgULY(int rl) {
        return mressrc.getImgULY(rl);
    }

    /**
     * Returns the number of tiles in the horizontal and vertical directions.
     *
     * <p>This default implementation returns the value of the source.</p>
     *
     * @param co If not null this object is used to return the information. If
     * null a new one is created and returned.
     *
     * @return The number of tiles in the horizontal (Point.x) and vertical
     * (Point.y) directions.
     * */
    public Point getNumTiles(Point co) {
        return mressrc.getNumTiles(co);
    }

    /**
     * Returns the total number of tiles in the image.
     *
     * <p>This default implementation returns the value of the source.</p>
     *
     * @return The total number of tiles in the image.
     * */
    public int getNumTiles() {
        return mressrc.getNumTiles();
    }
}
