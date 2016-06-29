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
 * $RCSfile: InvWTAdapter.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:32 $
 * $State: Exp $
 *
 * Class:                   InvWTAdapter
 *
 * Description:             <short description of class>
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

import jj2000.j2k.decoder.*;
import jj2000.j2k.image.*;

/**
 * This class provides default implementation of the methods in the 'InvWT'
 * interface. The source is always a 'MultiResImgData', which is a
 * multi-resolution image. The default implementation is just to return the
 * value of the source at the current image resolution level, which is set by
 * the 'setImgResLevel()' method.
 *
 * <p>This abstract class can be used to facilitate the development of other
 * classes that implement the 'InvWT' interface, because most of the trivial
 * methods are already implemented.</p>
 *
 * <p>If the default implementation of a method provided in this class does
 * not suit a particular implementation of the 'InvWT' interface, the method
 * can be overriden to implement the proper behaviour.</p>
 *
 * <p>If the 'setImgResLevel()' method is overriden then it is very important
 * that the one of this class is called from the overriding method, so that
 * the other methods in this class return the correct values.</p>
 *
 * @see InvWT
 * */
public abstract class InvWTAdapter implements InvWT {

    /** The decoder specifications */
    protected DecoderSpecs decSpec;

    /** The 'MultiResImgData' source */
    protected MultiResImgData mressrc;

    /** The resquested image resolution level for reconstruction. */
    protected int reslvl;

    /** The maximum available image resolution level */
    protected int maxImgRes;

    /**
     * Instantiates the 'InvWTAdapter' object using the specified
     * 'MultiResImgData' source. The reconstruction resolution level is set to
     * full resolution (i.e. the maximum resolution level).
     *
     * @param src From where to obtain the values to return
     *
     * @param decSpec The decoder specifications
     * */
    protected InvWTAdapter(MultiResImgData src,DecoderSpecs decSpec) {
        mressrc = src;
	this.decSpec = decSpec;
        maxImgRes = decSpec.dls.getMin();
    }

    /**
     * Sets the image reconstruction resolution level. A value of 0 means
     * reconstruction of an image with the lowest resolution (dimension)
     * available.
     *
     * <p>Note: Image resolution level indexes may differ from tile-component
     * resolution index. They are indeed indexed starting from the lowest
     * number of decomposition levels of each component of each tile.</p>
     *
     * <p>Example: For an image (1 tile) with 2 components (component 0 having
     * 2 decomposition levels and component 1 having 3 decomposition levels),
     * the first (tile-) component has 3 resolution levels and the second one
     * has 4 resolution levels, whereas the image has only 3 resolution levels
     * available.</p>
     *
     * @param rl The image resolution level.
     *
     * */
    public void setImgResLevel(int rl) {
        if(rl<0) {
            throw new IllegalArgumentException("Resolution level index "+
                                               "cannot be negative.");
        }
        reslvl = rl;
    }

    /**
     * Returns the overall width of the current tile in pixels. This is the
     * tile's width without accounting for any component subsampling. This is
     * also referred as the reference grid width in the current tile.
     *
     * <p>This default implementation returns the value of the source at the
     * current reconstruction resolution level.</p>
     *
     * @return The total current tile's width in pixels.
     * */
    public int getTileWidth() {
        // Retrieves the tile maximum resolution level index and request the
        // width from the source module.
	int tIdx = getTileIdx();
        int rl = 10000;
        int mrl;
        int nc = mressrc.getNumComps();
        for(int c=0; c<nc; c++) {
            mrl = mressrc.getSynSubbandTree(tIdx,c).resLvl;
            if(mrl<rl) rl = mrl;
        }
        return mressrc.getTileWidth(rl);
    }

    /**
     * Returns the overall height of the current tile in pixels. This
     * is the tile's height without accounting for any component
     * subsampling. This is also referred as the reference grid height
     * in the current tile.
     *
     * <p>This default implementation returns the value of the source at the
     * current reconstruction resolution level.</p>
     *
     * @return The total current tile's height in pixels.
     * */
    public int getTileHeight() {
        // Retrieves the tile maximum resolution level index and request the
        // height from the source module.
	int tIdx = getTileIdx();
        int rl = 10000;
        int mrl;
        int nc = mressrc.getNumComps();
        for(int c=0; c<nc; c++) {
            mrl = mressrc.getSynSubbandTree(tIdx,c).resLvl;
            if(mrl<rl) rl = mrl;
        }
        return mressrc.getTileHeight(rl);
    }

    /** Returns the nominal width of tiles */
    public int getNomTileWidth() {
        return mressrc.getNomTileWidth();
    }

    /** Returns the nominal height of tiles */
    public int getNomTileHeight() {
        return mressrc.getNomTileHeight();
    }

    /**
     * Returns the overall width of the image in pixels. This is the
     * image's width without accounting for any component subsampling
     * or tiling.
     *
     * @return The total image's width in pixels.
     * */
    public int getImgWidth() {
        return mressrc.getImgWidth(reslvl);
    }

    /**
     * Returns the overall height of the image in pixels. This is the
     * image's height without accounting for any component subsampling
     * or tiling.
     *
     * @return The total image's height in pixels.
     * */
    public int getImgHeight() {
        return mressrc.getImgHeight(reslvl);
    }

    /**
     * Returns the number of components in the image.
     *
     * @return The number of components in the image.
     * */
    public int getNumComps() {
        return mressrc.getNumComps();
    }

    /**
     * Returns the component subsampling factor in the horizontal
     * direction, for the specified component. This is, approximately,
     * the ratio of dimensions between the reference grid and the
     * component itself, see the 'ImgData' interface desription for
     * details.
     *
     * @param c The index of the component (between 0 and N-1).
     *
     * @return The horizontal subsampling factor of component 'c'.
     *
     * @see jj2000.j2k.image.ImgData
     * */
    public int getCompSubsX(int c) {
        return mressrc.getCompSubsX(c);
    }

    /**
     * Returns the component subsampling factor in the vertical
     * direction, for the specified component. This is, approximately,
     * the ratio of dimensions between the reference grid and the
     * component itself, see the 'ImgData' interface desription for
     * details.
     *
     * @param c The index of the component (between 0 and N-1).
     *
     * @return The vertical subsampling factor of component 'c'.
     *
     * @see jj2000.j2k.image.ImgData
     * */
    public int getCompSubsY(int c) {
        return mressrc.getCompSubsY(c);
    }

    /**
     * Returns the width in pixels of the specified tile-component
     *
     * @param t Tile index
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @return The width in pixels of component <tt>n</tt> in tile <tt>t</tt>.
     * */
    public int getTileCompWidth(int t,int c) {
        // Retrieves the tile-component maximum resolution index and gets the
        // width from the source.
        int rl = mressrc.getSynSubbandTree(t,c).resLvl;
        return mressrc.getTileCompWidth(t,c,rl);
    }

    /**
     * Returns the height in pixels of the specified tile-component.
     *
     * <p>This default implementation returns the value of the source at the
     * current reconstruction resolution level.</p>
     *
     * @param t The tile index.
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @return The height in pixels of component <tt>n</tt> in tile
     * <tt>t</tt>. 
     * */
    public int getTileCompHeight(int t,int c) {
        // Retrieves the tile-component maximum resolution index and gets the
        // height from the source.
        int rl = mressrc.getSynSubbandTree(t,c).resLvl;
        return mressrc.getTileCompHeight(t,c,rl);
    }

    /**
     * Returns the width in pixels of the specified component in the overall
     * image.
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @return The width in pixels of component <tt>c</tt> in the overall
     * image.
     * */
    public int getCompImgWidth(int c) {
        // Retrieves the component maximum resolution index and gets the width
        // from the source module.
        int rl = decSpec.dls.getMinInComp(c);
        return mressrc.getCompImgWidth(c,rl);
    }

    /**
     * Returns the height in pixels of the specified component in the overall
     * image.
     *
     * <p>This default implementation returns the value of the source at the
     * current reconstruction resolution level.</p>
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @return The height in pixels of component <tt>n</tt> in the overall
     * image.
     * */
    public int getCompImgHeight(int c) {
        // Retrieves the component maximum resolution index and gets the
        // height from the source module.
        int rl = decSpec.dls.getMinInComp(c);
        return mressrc.getCompImgHeight(c,rl);
    }

    /**
     * Changes the current tile, given the new indices. An
     * IllegalArgumentException is thrown if the coordinates do not correspond
     * to a valid tile.
     *
     * <p>This default implementation calls the same method on the source.</p>
     *
     * @param x The horizontal index of the tile.
     *
     * @param y The vertical index of the new tile.
     * */
    public void setTile(int x, int y) {
        mressrc.setTile(x,y);
    }

    /**
     * Advances to the next tile, in standard scan-line order (by rows then
     * columns). An NoNextElementException is thrown if the current tile is
     * the last one (i.e. there is no next tile).
     *
     * <p>This default implementation calls the same method on the source.</p>
     * */
    public void nextTile() {
        mressrc.nextTile();
    }

    /**
     * Returns the indixes of the current tile. These are the horizontal and
     * vertical indexes of the current tile.
     *
     * <p>This default implementation returns the value of the source.</p>
     *
     * @param co If not null this object is used to return the information. If
     * null a new one is created and returned.
     *
     * @return The current tile's indices (vertical and horizontal indexes).
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
     * specified component in the current tile.
     *
     * @param c The component index.
     * */
    public int getCompULX(int c) {
	// Find tile-component maximum resolution index and gets information
        // from the source module.
	int tIdx = getTileIdx();
	int rl = mressrc.getSynSubbandTree(tIdx,c).resLvl;
        return mressrc.getResULX(c,rl);
    }

    /**
     * Returns the vertical coordinate of the upper-left corner of the
     * specified component in the current tile.
     *
     * @param c The component index.
     * */
    public int getCompULY(int c) {
	// Find tile-component maximum resolution index and gets information
        // from the source module.
	int tIdx = getTileIdx();
	int rl = mressrc.getSynSubbandTree(tIdx,c).resLvl;
        return mressrc.getResULY(c,rl);
    }

    /**
     * Returns the horizontal coordinate of the image origin, the top-left
     * corner, in the canvas system, on the reference grid.
     *
     * <p>This default implementation returns the value of the source at the
     * current reconstruction resolution level.</p>
     *
     * @return The horizontal coordinate of the image origin in the canvas
     * system, on the reference grid.
     * */
    public int getImgULX() {
        return mressrc.getImgULX(reslvl);
    }

    /**
     * Returns the vertical coordinate of the image origin, the top-left
     * corner, in the canvas system, on the reference grid.
     *
     * <p>This default implementation returns the value of the source at the
     * current reconstruction resolution level.</p>
     *
     * @return The vertical coordinate of the image origin in the canvas
     * system, on the reference grid.
     * */
    public int getImgULY() {
        return mressrc.getImgULY(reslvl);
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

    /** 
     * Returns the specified synthesis subband tree 
     * 
     * @param t Tile index.
     *
     * @param c Component index.
     * */
    public SubbandSyn getSynSubbandTree(int t,int c) {
        return mressrc.getSynSubbandTree(t,c);
    }
}
