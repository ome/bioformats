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
 * $RCSfile: ImgDataJoiner.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:13 $
 * $State: Exp $
 *
 * Class:                   ImgDataJoiner
 *
 * Description:             Get ImgData from different sources
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

import jj2000.j2k.*;

/**
 * This class implements the ImgData interface and allows to obtain data from
 * different sources. Here, one source is represented by an ImgData and a
 * component index. The typical use of this class is when the encoder needs
 * different components (Red, Green, Blue, alpha, ...) from different input
 * files (i.e. from different ImgReader objects).
 *
 * <p>All input ImgData must not be tiled (i.e. must have only 1 tile) and the
 * image origin must be the canvas origin. The different inputs can have
 * different dimensions though (this will lead to different subsampling
 * factors for each component).</p>
 *
 * <p>The input ImgData and component index list must be defined when
 * constructing this class and can not be modified later.</p>
 *
 * @see ImgData
 * @see jj2000.j2k.image.input.ImgReader
 * */
public class ImgDataJoiner implements BlkImgDataSrc {

    /** The width of the image */
    private int w;

    /** The height of the image */
    private int h;

    /** The number of components in the image */
    private int nc;

    /** The list of input ImgData */
    private BlkImgDataSrc[] imageData;

    /** The component index associated with each ImgData */
    private int[] compIdx;

    /** The subsampling factor along the horizontal direction, for every
     * component */
    private int[] subsX;

    /** The subsampling factor along the vertical direction, for every
     * component */
    private int[] subsY;

    /**
     * Class constructor. Each input BlkImgDataSrc and its component index
     * must appear in the order wanted for the output components.<br>
     *
     * <u>Example:</u> Reading R,G,B components from 3 PGM files.<br>
     * <tt>
     * BlkImgDataSrc[] idList = <br>
     *   {<br>
     *   new ImgReaderPGM(new BEBufferedRandomAccessFile("R.pgm", "r")),<br>
     *   new ImgReaderPGM(new BEBufferedRandomAccessFile("G.pgm", "r")),<br>
     *   new ImgReaderPGM(new BEBufferedRandomAccessFile("B.pgm", "r"))<br>
     *   };<br>
     * int[] compIdx = {0,0,0};<br>
     * ImgDataJoiner idj = new ImgDataJoiner(idList, compIdx);
     * </tt>
     *
     * <p>Of course, the 2 arrays must have the same length (This length is
     * the number of output components). The image width and height are
     * definded to be the maximum values of all the input ImgData.
     *
     * @param imD The list of input BlkImgDataSrc in an array.
     *
     * @param cIdx The component index associated with each ImgData.
     * */
    public ImgDataJoiner(BlkImgDataSrc[] imD, int[] cIdx){
        int i;
        int maxW, maxH;

	// Initializes
	imageData = imD;
	compIdx = cIdx;
	if(imageData.length != compIdx.length)
	    throw new IllegalArgumentException("imD and cIdx must have the"+
					       " same length");

	nc = imD.length;

        subsX = new int[nc];
        subsY = new int[nc];

        // Check that no source is tiled and that the image origin is at the
        // canvas origin.
        for(i=0; i<nc; i++) {
            if (imD[i].getNumTiles() != 1 ||
                imD[i].getCompULX(cIdx[i])!=0 || 
                imD[i].getCompULY(cIdx[i])!=0) {
                throw
                    new IllegalArgumentException("All input components must, "+
                                                 "not use tiles and must "+
                                                 "have "+
                                                 "the origin at the canvas "+
                                                 "origin");
            }
        }

        // Guess component subsampling factors based on the fact that the
        // ceil() operation relates the reference grid size to the component's
        // size, through the subsampling factor.

        // Mhhh, difficult problem. For now just assume that one of the
        // subsampling factors is always 1 and that the component width is
        // always larger than its subsampling factor, which covers most of the
        // cases. We check the correctness of the solution once found to chek
        // out hypothesis.

        // Look for max width and height.
        maxW = 0;
        maxH = 0;
	for(i=0; i<nc; i++) {
	    if(imD[i].getCompImgWidth(cIdx[i]) > maxW)
		maxW = imD[i].getCompImgWidth(cIdx[i]);
	    if(imD[i].getCompImgHeight(cIdx[i]) > maxH)
		maxH = imD[i].getCompImgHeight(cIdx[i]);
	}
        // Set the image width and height as the maximum ones
	w = maxW;
	h = maxH;

        // Now get the sumsampling factors and check the subsampling factors,
        // just to see if above hypothesis were correct.
        for (i=0; i<nc; i++) {
            // This calculation only holds if the subsampling factor is less
            // than the component width
            subsX[i] = (maxW + imD[i].getCompImgWidth(cIdx[i])-1) /
                imD[i].getCompImgWidth(cIdx[i]);
            subsY[i] = (maxH + imD[i].getCompImgHeight(cIdx[i])-1) /
                imD[i].getCompImgHeight(cIdx[i]);
            if ((maxW+subsX[i]-1)/subsX[i] !=
                imD[i].getCompImgWidth(cIdx[i]) ||
                (maxH+subsY[i]-1)/subsY[i] !=
                imD[i].getCompImgHeight(cIdx[i])) {
                throw new Error("Can not compute component subsampling "+
                                "factors: strange subsampling.");
            }
        }
    }

    /**
     * Returns the overall width of the current tile in pixels. This is the
     * tile's width without accounting for any component subsampling.
     *
     * @return The total current tile's width in pixels.
     * */
    public int getTileWidth(){
	return w;
    }

    /**
     * Returns the overall height of the current tile in pixels. This is the
     * tile's height without accounting for any component subsampling.
     *
     * @return The total current tile's height in pixels.
     * */
    public int getTileHeight(){
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
     * width without accounting for any component subsampling or tiling.
     *
     * @return The total image's width in pixels.
     * */
    public int getImgWidth(){
	return w;
    }

    /**
     * Returns the overall height of the image in pixels. This is the image's
     * height without accounting for any component subsampling or tiling.
     *
     * @return The total image's height in pixels.
     * */
    public int getImgHeight(){
	return h;
    }

    /**
     * Returns the number of components in the image.
     *
     * @return The number of components in the image.
     * */
    public int getNumComps(){
	return nc;
    }

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
     * @see ImgData
     * */
    public int getCompSubsX(int c) {
        return subsX[c];
    }

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
     * @see ImgData
     * */
    public int getCompSubsY(int c) {
        return subsY[c];
    }


    /**
     * Returns the width in pixels of the specified tile-component
     *
     * @param t Tile index
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @return The width in pixels of component <tt>c</tt> in tile<tt>t</tt>.
     * */
    public int getTileCompWidth(int t,int c){
	return imageData[c].getTileCompWidth(t,compIdx[c]);
    }

    /**
     * Returns the height in pixels of the specified tile-component.
     *
     * @param t The tile index.
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @return The height in pixels of component <tt>c</tt> in the current
     * tile.
     * */
    public int getTileCompHeight(int t,int c){
	return imageData[c].getTileCompHeight(t,compIdx[c]);
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
    public int getCompImgWidth(int c){
	return imageData[c].getCompImgWidth(compIdx[c]);
    }

    /**
     * Returns the height in pixels of the specified component in the
     * overall image.
     *
     * @param n The index of the component, from 0 to N-1.
     *
     * @return The height in pixels of component <tt>n</tt> in the overall
     * image.
     *
     *
     * */
    public int getCompImgHeight(int n){
	return imageData[n].getCompImgHeight(compIdx[n]);	
    }

    /**
     * Returns the number of bits, referred to as the "range bits",
     * corresponding to the nominal range of the data in the specified
     * component. If this number is <i>b</b> then for unsigned data the
     * nominal range is between 0 and 2^b-1, and for signed data it is between
     * -2^(b-1) and 2^(b-1)-1. For floating point data this value is not
     * applicable.
     *
     * @param c The index of the component.
     *
     * @return The number of bits corresponding to the nominal range of the
     * data. Fro floating-point data this value is not applicable and the
     * return value is undefined.
     * */
    public int getNomRangeBits(int c){
	return imageData[c].getNomRangeBits(compIdx[c]);
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
    public int getFixedPoint(int c){
	return imageData[c].getFixedPoint(compIdx[c]);
    }

    /**
     * Returns, in the blk argument, a block of image data containing the
     * specifed rectangular area, in the specified component. The data is
     * returned, as a reference to the internal data, if any, instead of as a
     * copy, therefore the returned data should not be modified.
     *
     * <P>The rectangular area to return is specified by the 'ulx', 'uly', 'w'
     * and 'h' members of the 'blk' argument, relative to the current
     * tile. These members are not modified by this method. The 'offset' and
     * 'scanw' of the returned data can be arbitrary. See the 'DataBlk' class.
     *
     * <P>This method, in general, is more efficient than the 'getCompData()'
     * method since it may not copy the data. However if the array of returned
     * data is to be modified by the caller then the other method is probably
     * preferable.
     *
     * <P>If the data array in <tt>blk</tt> is <tt>null</tt>, then a new one
     * is created if necessary. The implementation of this interface may
     * choose to return the same array or a new one, depending on what is more
     * efficient. Therefore, the data array in <tt>blk</tt> prior to the
     * method call should not be considered to contain the returned data, a
     * new array may have been created. Instead, get the array from
     * <tt>blk</tt> after the method has returned.
     *
     * <P>The returned data may have its 'progressive' attribute set. In this
     * case the returned data is only an approximation of the "final" data.
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
    public DataBlk getInternCompData(DataBlk blk, int c){
	return imageData[c].getInternCompData(blk, compIdx[c]);
    }

    /**
     * Returns, in the blk argument, a block of image data containing the
     * specifed rectangular area, in the specified component. The data is
     * returned, as a copy of the internal data, therefore the returned data
     * can be modified "in place".
     *
     * <P>The rectangular area to return is specified by the 'ulx', 'uly', 'w'
     * and 'h' members of the 'blk' argument, relative to the current
     * tile. These members are not modified by this method. The 'offset' of
     * the returned data is 0, and the 'scanw' is the same as the block's
     * width. See the 'DataBlk' class.
     *
     * <P>This method, in general, is less efficient than the
     * 'getInternCompData()' method since, in general, it copies the
     * data. However if the array of returned data is to be modified by the
     * caller then this method is preferable.
     *
     * <P>If the data array in 'blk' is 'null', then a new one is created. If
     * the data array is not 'null' then it is reused, and it must be large
     * enough to contain the block's data. Otherwise an 'ArrayStoreException'
     * or an 'IndexOutOfBoundsException' is thrown by the Java system.
     *
     * <P>The returned data may have its 'progressive' attribute set. In this
     * case the returned data is only an approximation of the "final" data.
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
    public DataBlk getCompData(DataBlk blk, int c){
	return imageData[c].getCompData(blk, compIdx[c]);
    }

    /**
     * Changes the current tile, given the new coordinates. An
     * IllegalArgumentException is thrown if the coordinates do not correspond
     * to a valid tile.
     *
     * @param x The horizontal coordinate of the tile.
     *
     * @param y The vertical coordinate of the new tile.
     * */
    public void setTile(int x, int y){
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
     * Returns a string of information about the object, more than 1 line
     * long. The information string includes information from the several
     * input ImgData (their toString() method are called one after the other).
     *
     * @return A string of information about the object.
     * */
    public String toString() {
        String string = "ImgDataJoiner: WxH = " + w + "x" + h;
	for(int i=0; i<nc; i++){
	    string += "\n- Component "+i+" "+imageData[i];
	}
	return string;
    }    
}
