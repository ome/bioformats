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
 * $RCSfile: BitstreamReaderAgent.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:00 $
 * $State: Exp $
 *
 * Class:                   BitstreamReaderAgent
 *
 * Description:             The generic interface for bit stream
 *                          transport agents.
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
 *  */
package jj2000.j2k.codestream.reader;
import java.awt.Point;

import jj2000.j2k.quantization.dequantizer.*;
import jj2000.j2k.wavelet.synthesis.*;
import jj2000.j2k.entropy.decoder.*;
import jj2000.j2k.codestream.*;
import jj2000.j2k.wavelet.*;
import jj2000.j2k.decoder.*;
import jj2000.j2k.image.*;
import jj2000.j2k.util.*;
import jj2000.j2k.io.*;
import jj2000.j2k.*;

import java.io.*;

import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageReadParamJava;

/**
 * This is the generic interface for bit stream reader agents. A bit stream
 * reader agent is an entity that allows reading from a bit stream and
 * requesting compressed code-blocks. It can be a simple file reader, or a
 * network connection, or anything else.
 *
 * <P>The bit stream reader agent allows to make request for compressed block
 * data in any order. The amount of data returned would normally depend on the
 * data available at the time of the request, be it from a file or from a
 * network connection.
 *
 * <P>The bit stream reader agent has the notion of a current tile, and
 * coordinates are relative to the current tile, where applicable.
 *
 * <P>Resolution level 0 is the lowest resolution level, i.e. the LL subband
 * alone.
 * */
public abstract class BitstreamReaderAgent implements CodedCBlkDataSrcDec {

    /** The decoder specifications */
    protected DecoderSpecs decSpec;

    /**
     * Whether or not the components in the current tile uses a derived
     * quantization step size (only relevant in non reversible quantization
     * mode). This field is actualized by the setTile method in
     * FileBitstreamReaderAgent.
     *
     * @see FileBitstreamReaderAgent#initSubbandsFields
     * */
    protected boolean derived[] = null;

    /**
     * Number of guard bits off all component in the current tile. This field
     * is actualized by the setTile method in FileBitstreamReaderAgent.
     *
     * @see FileBitstreamReaderAgent#initSubbandsFields
     * */
    protected int[] gb = null;

    /**
     * Dequantization parameters of all subbands and all components in the
     * current tile. The value is actualized by the setTile method in
     * FileBitstreamReaderAgent.
     *
     * @see FileBitstreamReaderAgent#initSubbandsFields
     * */
    protected StdDequantizerParams params[] = null;

    /** The prefix for bit stream reader options: 'B' */
    public final static char OPT_PREFIX = 'B';

    /** The list of parameters that is accepted by the bit stream
     * readers. They start with 'B'. */
    private static final String [][] pinfo = null;

    /**
     * The maximum number of decompostion levels for each component of the
     * current tile. It means that component c has mdl[c]+1 resolution levels
     * (indexed from 0 to mdl[c])
     * */
    protected int mdl[];

    /** The number of components */
    protected final int nc;

    /** Image resolution level to generate */
    protected int targetRes;

    /**
     * The subband trees for each component in the current tile. Each element
     * in the array is the root element of the subband tree for a
     * component. The number of magnitude bits in each subband (magBits member
     * variable) is not initialized.
     * */
    protected SubbandSyn subbTrees[];

    /** The image width on the hi-res reference grid */
    protected final int imgW;

    /** The image width on the hi-res reference grid */
    protected final int imgH;

    /** The horizontal coordinate of the image origin in the canvas system, on
     * the reference grid. */
    protected final int ax;

    /** The vertical coordinate of the image origin in the canvas system, on
     * the reference grid. */
    protected final int ay;

    /** The horizontal coordinate of the tiling origin in the canvas system, on
     * the reference grid. */
    protected final int px;

    /** The vertical coordinate of the tiling origin in the canvas system, on
     * the reference grid. */
    protected final int py;

    /** The horizontal offsets of the upper-left corner of the current tile
     * (not active tile) with respect to the canvas origin, in the component
     * hi-res grid, for each component. */
    protected final int offX[];

    /** The vertical offsets of the upper-left corner of the current tile (not
     * active tile) with respect to the canvas origin, in the component hi-res
     * grid, for each component. */
    protected final int offY[];

    /** The horizontal coordinates of the upper-left corner of the active
     * tile, with respect to the canvas origin, in the component hi-res grid,
     * for each component. */
    protected final int culx[];

    /** The vertical coordinates of the upper-left corner of the active tile,
     * with respect to the canvas origin, in the component hi-res grid, for
     * each component. */
    protected final int culy[];

    /** The nominal tile width, in the hi-res reference grid */
    protected final int ntW;

    /** The nominal tile height, in the hi-res reference grid */
    protected final int ntH;

    /** The number of tile in the horizontal direction */
    protected final int ntX;

    /** The number of tiles in the vertical direction */
    protected final int ntY;

    /** The total number of tiles. */
    protected final int nt;

    /** The current tile horizontal index */
    protected int ctX;

    /** The current tile vertical index */
    protected int ctY;

    /** The decoded bit stream header */
    protected final HeaderDecoder hd;

    /** Number of bytes targeted to be read */
    protected int tnbytes;

    /** Actual number of read bytes */
    protected int anbytes;

    /** Target decoding rate in bpp */
    protected float trate;

    /** Actual decoding rate in bpp */
    protected float arate;

    /**
     * Initializes members of this class. This constructor takes a
     * HeaderDecoder object. This object must be initialized by the
     * constructor of the implementing class from the header of the bit
     * stream.
     *
     * @param hd The decoded header of the bit stream from where to initialize
     * the values.
     *
     * @param decSpec The decoder specifications
     * */
    protected BitstreamReaderAgent(HeaderDecoder hd,DecoderSpecs decSpec){
        Point co;
        int i,j,max;

        this.decSpec = decSpec;
        this.hd = hd;

        // Number of components
        nc = hd.getNumComps();
        offX = new int[nc];
        offY = new int[nc];
        culx = new int[nc];
        culy = new int[nc];

        // Image size and origin
        imgW = hd.getImgWidth();
        imgH = hd.getImgHeight();
        ax = hd.getImgULX();
        ay = hd.getImgULY();

        // Tiles
        co = hd.getTilingOrigin(null);
        px = co.x;
        py = co.y;
        ntW = hd.getNomTileWidth();
        ntH = hd.getNomTileHeight();
        ntX = (ax+imgW-px+ntW-1) / ntW;
        ntY = (ay+imgH-py+ntH-1) / ntH;
	nt = ntX * ntY;
    }

    /**
     * Returns the vertical code-block partition origin. Allowable values are
     * 0 and 1, nothing else.
     * */
    public final int getCbULX() {
        return hd.getCbULX();
    }

    /**
     * Returns the vertical code-block partition origin. Allowable values are
     * 0 and 1, nothing else.
     * */
    public int getCbULY() {
        return hd.getCbULY();
    }

    /**
     * Returns the number of components in the image.
     *
     * @return The number of components in the image.
     * */
    public final int getNumComps() {
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
     * @see jj2000.j2k.image.ImgData
     * */
    public final int getCompSubsX(int c) {
        return hd.getCompSubsX(c);
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
        return hd.getCompSubsY(c);
    }

    /**
     * Returns the overall width of the current tile in pixels for the given
     * (tile) resolution level. This is the tile's width without accounting
     * for any component subsampling.
     *
     * <P>Note: Tile resolution level indexes may be different from
     * tile-component resolution index. They are indeed indexed starting from
     * the lowest number of decomposition levels of each component of the
     * tile.
     *
     * <P>For an image (1 tile) with 2 components (component 0 having 2
     * decomposition levels and component 1 having 3 decomposition levels),
     * the first (tile-)component has 3 resolution levels and the second one
     * has 4 resolution levels, whereas the tile has only 3 resolution levels
     * available.
     *
     * @param rl The (tile) resolution level.
     *
     * @return The current tile's width in pixels.
     * */
    public int getTileWidth(int rl){
	// The minumum number of decomposition levels between all the
	// components
	int mindl = decSpec.dls.getMinInTile(getTileIdx());
        if(rl>mindl){
            throw new IllegalArgumentException("Requested resolution level"+
                                               " is not available for, at "+
                                               "least, one component in "+
					       "tile: "+ctX+"x"+ctY);
        }
        int ctulx,ntulx;
        int dl = mindl-rl; // Number of decomposition to obtain this
	// resolution

        // Calculate starting X of current tile at hi-res
        ctulx = (ctX == 0) ? ax : px+ctX*ntW;
        // Calculate starting X of next tile X-wise at hi-res
        ntulx = (ctX < ntX-1) ? px+(ctX+1)*ntW : ax+imgW;
	dl = 1 << dl;
        // The difference at the rl resolution level is the width
        return (ntulx+dl-1)/dl-(ctulx+dl-1)/dl;
    }

    /**
     * Returns the overall height of the current tile in pixels, for the given
     * resolution level. This is the tile's height without accounting for any
     * component subsampling.
     *
     * <P>Note: Tile resolution level indexes may be different from
     * tile-component resolution index. They are indeed indexed starting from
     * the lowest number of decomposition levels of each component of the
     * tile.
     *
     * <P>For an image (1 tile) with 2 components (component 0 having 2
     * decomposition levels and component 1 having 3 decomposition levels),
     * the first (tile-)component has 3 resolution levels and the second one
     * has 4 resolution levels, whereas the tile has only 3 resolution levels
     * available.
     *
     * @param rl The (tile) resolution level.
     *
     * @return The total current tile's height in pixels.
     * */
    public int getTileHeight(int rl){
	// The minumum number of decomposition levels between all the
	// components
	int mindl = decSpec.dls.getMinInTile(getTileIdx());
        if(rl>mindl){
            throw new IllegalArgumentException("Requested resolution level"+
                                               " is not available for, at "+
                                               "least, one component in"+
					       " tile: "+ctX+"x"+ctY);
        }

        int ctuly,ntuly;
        int dl = mindl-rl; // Number of decomposition to obtain this
	// resolution

        // Calculate starting Y of current tile at hi-res
        ctuly = (ctY == 0) ? ay : py+ctY*ntH;
        // Calculate starting Y of next tile Y-wise at hi-res
        ntuly = (ctY < ntY-1) ? py+(ctY+1)*ntH : ay+imgH;
	dl = 1 <<dl;
        // The difference at the rl level is the height
        return (ntuly+dl-1)/dl-(ctuly+dl-1)/dl;
    }

    /**
     * Returns the overall width of the image in pixels, for the given (image)
     * resolution level. This is the image's width without accounting for any
     * component subsampling or tiling.
     *
     * <P>Note: Image resolution level indexes may differ from tile-component
     * resolution index. They are indeed indexed starting from the lowest
     * number of decomposition levels of each component of each tile.
     *
     * <P>Example: For an image (1 tile) with 2 components (component 0 having
     * 2 decomposition levels and component 1 having 3 decomposition levels),
     * the first (tile-) component has 3 resolution levels and the second one
     * has 4 resolution levels, whereas the image has only 3 resolution levels
     * available.
     *
     * @param rl The image resolution level.
     *
     * @return The total image's width in pixels.
     * */
    public int getImgWidth(int rl){
	// The minimum number of decomposition levels of each
	// tile-component
	int mindl = decSpec.dls.getMin();
        if(rl>mindl){
            throw new IllegalArgumentException("Requested resolution level"+
                                               " is not available for, at "+
                                               "least, one tile-component");
        }
	// Retrieve number of decomposition levels corresponding to
	// this resolution level
	int dl = 1 << mindl - rl;
        return (ax+imgW+dl-1)/dl-(ax+dl-1)/dl;
    }

    /**
     * Returns the overall height of the image in pixels, for the given
     * resolution level. This is the image's height without accounting for any
     * component subsampling or tiling.
     *
     * <P>Note: Image resolution level indexes may differ from tile-component
     * resolution index. They are indeed indexed starting from the lowest
     * number of decomposition levels of each component of each tile.
     *
     * <P>Example: For an image (1 tile) with 2 components (component 0 having
     * 2 decomposition levels and component 1 having 3 decomposition levels),
     * the first (tile-) component has 3 resolution levels and the second one
     * has 4 resolution levels, whereas the image has only 3 resolution levels
     * available.
     *
     * @param rl The image resolution level, from 0 to L.
     *
     * @return The total image's height in pixels.
     * */
    public int getImgHeight(int rl){
	int mindl = decSpec.dls.getMin();
        if(rl>mindl){
            throw new IllegalArgumentException("Requested resolution level"+
                                               " is not available for, at "+
                                               "least, one tile-component");
        }
	// Retrieve number of decomposition levels corresponding to this
	// resolution level
	int dl = 1 << mindl - rl;
        return (ay+imgH+dl-1)/dl-(ay+dl-1)/dl;
    }

    /**
     * Returns the horizontal coordinate of the image origin, the top-left
     * corner, in the canvas system, on the reference grid at the specified
     * resolution level.
     *
     * <P>Note: Image resolution level indexes may differ from tile-component
     * resolution index. They are indeed indexed starting from the lowest
     * number of decomposition levels of each component of each tile.
     *
     * <P>Example: For an image (1 tile) with 2 components (component 0 having
     * 2 decomposition levels and component 1 having 3 decomposition levels),
     * the first (tile-) component has 3 resolution levels and the second one
     * has 4 resolution levels, whereas the image has only 3 resolution levels
     * available.
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The horizontal coordinate of the image origin in the canvas
     * system, on the reference grid.
     * */
    public int getImgULX(int rl){
	int mindl = decSpec.dls.getMin();
        if(rl>mindl){
            throw new IllegalArgumentException("Requested resolution level"+
                                               " is not available for, at "+
                                               "least, one tile-component");
        }
	// Retrieve number of decomposition levels corresponding to this
	// resolution level
	int dl = 1 << mindl - rl;
        return (ax+dl-1)/dl;
    }

    /**
     * Returns the vertical coordinate of the image origin, the top-left
     * corner, in the canvas system, on the reference grid at the specified
     * resolution level.
     *
     * <P>Note: Image resolution level indexes may differ from tile-component
     * resolution index. They are indeed indexed starting from the lowest
     * number of decomposition levels of each component of each tile.
     *
     * <P>Example: For an image (1 tile) with 2 components (component 0 having
     * 2 decomposition levels and component 1 having 3 decomposition levels),
     * the first (tile-) component has 3 resolution levels and the second one
     * has 4 resolution levels, whereas the image has only 3 resolution levels
     * available.
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The vertical coordinate of the image origin in the canvas
     * system, on the reference grid.
     * */
    public int getImgULY(int rl){
	int mindl = decSpec.dls.getMin();
        if(rl>mindl){
            throw new IllegalArgumentException("Requested resolution level"+
                                               " is not available for, at "+
                                               "least, one tile-component");
        }
	// Retrieve number of decomposition levels corresponding to this
	// resolution level
	int dl = 1 << mindl - rl;
        return (ay+dl-1)/dl;
    }

    /**
     * Returns the width in pixels of the specified tile-component for the
     * given (tile-component) resolution level.
     *
     * @param t The tile index
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The width in pixels of component <tt>c</tt> in tile <tt>t</tt>
     * for resolution level <tt>rl</tt>.
     * */
    public final int getTileCompWidth(int t,int c,int rl) {
        int tIdx = getTileIdx();
        if(t!=tIdx) {
            throw new Error("Asking the tile-component width of a tile "+
                            "different  from the current one.");
        }
        // Calculate starting X of next tile X-wise at reference grid hi-res
        int ntulx = (ctX < ntX-1) ? px+(ctX+1)*ntW : ax+imgW;
        // Convert reference grid hi-res to component grid hi-res
        ntulx = (ntulx+hd.getCompSubsX(c)-1)/hd.getCompSubsX(c);
        int dl = 1 << mdl[c]-rl;
        // Starting X of current tile at component grid hi-res is culx[c]
        // The difference at the rl level is the width
        return (ntulx+dl-1)/dl-(culx[c]+dl-1)/dl;
    }

    /**
     * Returns the height in pixels of the specified tile-component for the
     * given (tile-component) resolution level.
     *
     * @param t The tile index.
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The height in pixels of component <tt>c</tt> in the current
     * tile.
     * */
    public final int getTileCompHeight(int t,int c,int rl) {
        int tIdx = getTileIdx();
        if(t!=tIdx) {
            throw new Error("Asking the tile-component width of a tile "+
                            "different  from the current one.");
        }
        // Calculate starting Y of next tile Y-wise at reference grid hi-res
        int ntuly = (ctY < ntY-1) ? py+(ctY+1)*ntH : ay+imgH;
        // Convert reference grid hi-res to component grid hi-res
        ntuly = (ntuly+hd.getCompSubsY(c)-1)/hd.getCompSubsY(c);
        int dl = 1 << mdl[c]-rl; // Revert level indexation (0 is hi-res)
        // Starting Y of current tile at component grid hi-res is culy[c]
        // The difference at the rl level is the height
        return (ntuly+dl-1)/dl-(culy[c]+dl-1)/dl;
    }


    /**
     * Returns the width in pixels of the specified component in the overall
     * image, for the given (component) resolution level.
     *
     * <P>Note: Component resolution level indexes may differ from
     * tile-component resolution index. They are indeed indexed starting from
     * the lowest number of decomposition levels of same component of each
     * tile.
     *
     * <P>Example: For an image (2 tiles) with 1 component (tile 0 having 2
     * decomposition levels and tile 1 having 3 decomposition levels), the
     * first tile(-component) has 3 resolution levels and the second one has 4
     * resolution levels, whereas the component has only 3 resolution levels
     * available.
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The width in pixels of component <tt>c</tt> in the overall
     * image.
     * */
    public final int getCompImgWidth(int c,int rl){
	// indexation (0 is hi-res)
        // Calculate image starting x at component hi-res grid
        int sx = (ax+hd.getCompSubsX(c)-1)/hd.getCompSubsX(c);
        // Calculate image ending (excluding) x at component hi-res grid
        int ex = (ax+imgW+hd.getCompSubsX(c)-1)/hd.getCompSubsX(c);
	int dl = 1 << decSpec.dls.getMinInComp(c)-rl;
        // The difference at the rl level is the width
        return (ex+dl-1)/dl-(sx+dl-1)/dl;
    }

    /**
     * Returns the height in pixels of the specified component in the overall
     * image, for the given (component) resolution level.
     *
     * <P>Note: Component resolution level indexes may differ from
     * tile-component resolution index. They are indeed indexed starting from
     * the lowest number of decomposition levels of same component of each
     * tile.
     *
     * <P>Example: For an image (2 tiles) with 1 component (tile 0 having 2
     * decomposition levels and tile 1 having 3 decomposition levels), the
     * first tile(-component) has 3 resolution levels and the second one has 4
     * resolution levels, whereas the component has only 3 resolution levels
     * available.
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The height in pixels of component <tt>c</tt> in the overall
     * image.
     * */
    public final int getCompImgHeight(int c,int rl){
	// indexation (0 is hi-res)
        // Calculate image starting x at component hi-res grid
        int sy = (ay+hd.getCompSubsY(c)-1)/hd.getCompSubsY(c);
        // Calculate image ending (excluding) x at component hi-res grid
        int ey = (ay+imgH+hd.getCompSubsY(c)-1)/hd.getCompSubsY(c);
	int dl = 1 << decSpec.dls.getMinInComp(c)-rl;
        // The difference at the rl level is the width
        return (ey+dl-1)/dl-(sy+dl-1)/dl;
    }

    /**
     * Changes the current tile, given the new indexes. An
     * IllegalArgumentException is thrown if the indexes do not correspond to
     * a valid tile.
     *
     * @param x The horizontal indexes the tile.
     *
     * @param y The vertical indexes of the new tile.
     * */
    public abstract void setTile(int x, int y);

    /**
     * Advances to the next tile, in standard scan-line order (by rows then
     * columns). An NoNextElementException is thrown if the current tile is
     * the last one (i.e. there is no next tile).
     * */
    public abstract void nextTile();

    /**
     * Returns the indexes of the current tile. These are the horizontal and
     * vertical indexes of the current tile.
     *
     * @param co If not null this object is used to return the information. If
     * null a new one is created and returned.
     *
     * @return The current tile's indexes (vertical and horizontal indexes).
     * */
    public final Point getTile(Point co) {
        if (co != null) {
            co.x = ctX;
            co.y = ctY;
            return co;
        }
        else {
            return new Point(ctX,ctY);
        }
    }

    /**
     * Returns the index of the current tile, relative to a standard scan-line
     * order.
     *
     * @return The current tile's index (starts at 0).
     * */
    public final int getTileIdx() {
        return ctY*ntX+ctX;
    }

    /** 
     * Returns the horizontal coordinate of the upper-left corner of the
     * specified resolution in the given component of the current tile.
     *  
     * @param c The component index.
     *  
     * @param rl The resolution level index.
     * */
    public final int getResULX(int c,int rl) {
        int dl = mdl[c]-rl;
        if(dl<0){
            throw new IllegalArgumentException("Requested resolution level"+
                                               " is not available for, at "+
                                               "least, one component in "+
                                               "tile: "+ctX+"x"+ctY);
        }
        int tx0 = (int)Math.max(px+ctX*ntW,ax);
        int tcx0 = (int)Math.ceil(tx0/(double)getCompSubsX(c));
        return (int)Math.ceil(tcx0/(double)(1<<dl));
    }

    /**
     * Returns the vertical coordinate of the upper-left corner of the
     * specified component in the given component of the current tile.
     *
     * @param c The component index.
     *
     * @param rl The resolution level index.
     * */
    public final int getResULY(int c,int rl) {
        int dl = mdl[c]-rl;
        if(dl<0){
            throw new IllegalArgumentException("Requested resolution level"+
                                               " is not available for, at "+
                                               "least, one component in "+
                                               "tile: "+ctX+"x"+ctY);
        }
        int ty0 = (int)Math.max(py+ctY*ntH,ay);
        int tcy0 = (int)Math.ceil(ty0/(double)getCompSubsY(c));
        return (int)Math.ceil(tcy0/(double)(1<<dl));
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
        }
        else {
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
     * Returns the subband tree, for the specified tile-component. This method
     * returns the root element of the subband tree structure, see Subband and
     * SubbandSyn. The tree comprises all the available resolution levels.
     *
     * <p>Note: this method is not able to return subband tree for a tile
     * different than the current one.</p>
     *
     * <p>The number of magnitude bits ('magBits' member variable) for each
     * subband is not initialized.</p>
     *
     * @param t The tile index
     *
     * @param c The index of the component, from 0 to C-1.
     *
     * @return The root of the tree structure.
     * */
    public final SubbandSyn getSynSubbandTree(int t,int c) {
        if(t!=getTileIdx()) {
            throw new IllegalArgumentException("Can not request subband"+
                                               " tree of a different tile"+
                                               " than the current one");
        }
        if(c<0 || c>=nc) {
            throw new IllegalArgumentException("Component index out of range");
        }
        return subbTrees[c];
    }


    /**
     * Creates a bit stream reader of the correct type that works on the
     * provided RandomAccessIO, with the special parameters from the parameter
     * list.
     *
     * @param in The RandomAccessIO source from which to read the bit stream.
     *
     * @param hd Header of the codestream.
     *
     * @param j2krparam The parameters applicable to the
     * bit stream read (other parameters may also be present).
     *
     * @param decSpec The decoder specifications
     *
     * @param cdstrInfo Whether or not to print information found in
     * codestream. 
     *
     * @param hi Reference to the HeaderInfo instance.
     *
     * @exception IOException If an I/O error occurs while reading initial
     * data from the bit stream.
     * @exception IllegalArgumentException If an unrecognised bit stream
     * reader option is present.
     * */
    public static BitstreamReaderAgent createInstance(RandomAccessIO in,
                                                      HeaderDecoder hd,
                                                      J2KImageReadParamJava j2krparam,
                                                      DecoderSpecs decSpec,
                                                      boolean cdstrInfo,
                                                      HeaderInfo hi)
        throws IOException {
        // Check header length
/*
	if (in.getPos() != hd.getTotalHeaderLength() + hd.initPos) {
            throw new IllegalArgumentException("Invalid header length");
        }
*/
        return new FileBitstreamReaderAgent(hd,in,decSpec,j2krparam,cdstrInfo,hi);
    }


    /**
     * Returns the parameters that are used in this class and implementing
     * classes. It returns a 2D String array. Each of the 1D arrays is for a
     * different option, and they have 3 elements. The first element is the
     * option name, the second one is the synopsis and the third one is a long
     * description of what the parameter is. The synopsis or description may
     * be 'null', in which case it is assumed that there is no synopsis or
     * description of the option, respectively. Null may be returned if no
     * options are supported.
     *
     * @return the options name, their synopsis and their explanation, or null
     * if no options are supported.
     * */
    public static String[][] getParameterInfo() {
        return pinfo;
    }

    /**
     * Returns the precinct partition width for the specified tile-component
     * and (tile-component) resolution level.
     *
     * @param t the tile index
     *
     * @param c The index of the component (between 0 and N-1)
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return the precinct partition width for the specified component,
     * resolution level and tile.
     * */
    public final int getPPX(int t,int c,int rl){
        return decSpec.pss.getPPX(t,c,rl);
    }

    /**
     * Returns the precinct partition height for the specified tile-component
     * and (tile-component) resolution level.
     *
     * @param t The tile index
     *
     * @param c The index of the component (between 0 and N-1)
     *
     * @param rl The resolution level, from 0 to L.
     *
     * @return The precinct partition height in the specified component, for
     * the specified resolution level, for the current tile.
     * */
    public final int getPPY(int t,int c,int rl){
        return decSpec.pss.getPPY(t,c,rl);
    }

    /**
     * Initialises subbands fields, such as code-blocks dimension and number
     * of magnitude bits, in the subband tree. The nominal code-block
     * width/height depends on the precincts dimensions if used. The way the
     * number of magnitude bits is computed depends on the quantization type
     * (reversible, derived, expounded).
     *
     * @param c The component index
     *
     * @param sb The subband tree to be initialised.
     * */
    protected void initSubbandsFields(int c,SubbandSyn sb){
        int t = getTileIdx();
        int rl = sb.resLvl;
        int cbw, cbh;

        cbw = decSpec.cblks.getCBlkWidth(ModuleSpec.SPEC_TILE_COMP,t,c);
        cbh = decSpec.cblks.getCBlkHeight(ModuleSpec.SPEC_TILE_COMP,t,c);

        if( !sb.isNode ){
            if( hd.precinctPartitionUsed() ){
                // The precinct partition is used
                int ppxExp, ppyExp, cbwExp, cbhExp;

                // Get exponents
                ppxExp = MathUtil.log2(getPPX(t,c,rl));
                ppyExp = MathUtil.log2(getPPY(t,c,rl));
                cbwExp = MathUtil.log2(cbw);
                cbhExp = MathUtil.log2(cbh);

                switch (sb.resLvl) {
                    case 0:
                        sb.nomCBlkW = ( cbwExp<ppxExp ?
                            (1<<cbwExp) : (1<<ppxExp) );
                        sb.nomCBlkH = ( cbhExp<ppyExp ?
                            (1<<cbhExp) : (1<<ppyExp) );
                        break;

                    default:
                        sb.nomCBlkW = ( cbwExp<ppxExp-1 ?
                            (1<<cbwExp) : (1<<(ppxExp-1)) );
                        sb.nomCBlkH = ( cbhExp<ppyExp-1 ?
                            (1<<cbhExp) : (1<<(ppyExp-1)) );
                        break;
                }
            }
            else {
                sb.nomCBlkW = cbw;
                sb.nomCBlkH = cbh;
            }

            // Number of code-blocks
            if(sb.numCb == null) sb.numCb = new Point();
            if (sb.w==0 || sb.h==0) {
                sb.numCb.x = 0;
                sb.numCb.y = 0;
            } else {
                int cb0x = getCbULX();
                int cb0y = getCbULY();
                int tmp;

                // Projects code-block partition origin to subband. Since the
                // origin is always 0 or 1, it projects to the low-pass side
                // (throught the ceil operator) as itself (i.e. no change) and
                // to the high-pass side (through the floor operator) as 0,
                // always.
                int acb0x = cb0x;
                int acb0y = cb0y;

                switch (sb.sbandIdx) {
                case Subband.WT_ORIENT_LL:
                    // No need to project since all low-pass => nothing to do
                    break;
                case Subband.WT_ORIENT_HL:
                    acb0x = 0;
                    break;
                case Subband.WT_ORIENT_LH:
                    acb0y = 0;
                    break;
                case Subband.WT_ORIENT_HH:
                    acb0x = 0;
                    acb0y = 0;
                    break;
                default:
                    throw new Error("Internal JJ2000 error");
                }
                if(sb.ulcx-acb0x<0 || sb.ulcy-acb0y<0) {
                    throw new IllegalArgumentException("Invalid code-blocks "+
                                                       "partition origin or "+
                                                       "image offset in the "+
                                                       "reference grid.");
                }

                // NOTE: when calculating "floor()" by integer division the
                // dividend and divisor must be positive, we ensure that by
                // adding the divisor to the dividend and then substracting 1
                // to the result of the division

                tmp = sb.ulcx-acb0x+sb.nomCBlkW;
                sb.numCb.x = (tmp+sb.w-1)/sb.nomCBlkW - (tmp/sb.nomCBlkW-1);

                tmp = sb.ulcy-acb0y+sb.nomCBlkH;
                sb.numCb.y = (tmp+sb.h-1)/sb.nomCBlkH - (tmp/sb.nomCBlkH-1);
            }

            if(derived[c]){
                sb.magbits = gb[c]+(params[c].exp[0][0]-(mdl[c]-sb.level))-1;
            }
            else {
                sb.magbits = gb[c]+params[c].exp[sb.resLvl][sb.sbandIdx]-1;
            }
        }
        else {
            initSubbandsFields(c,(SubbandSyn)sb.getLL());
            initSubbandsFields(c,(SubbandSyn)sb.getHL());
            initSubbandsFields(c,(SubbandSyn)sb.getLH());
            initSubbandsFields(c,(SubbandSyn)sb.getHH());
        }
    }

    /**
     * Returns the image resolution level to reconstruct from the
     * codestream. This value cannot be computed before every main and tile
     * headers are read.
     *
     * @return The image  resolution level
     * */
    public int getImgRes(){
	return targetRes;
    }

    /**
     * Return the target decoding rate in bits per pixel.
     *
     * @return Target decoding rate in bpp.
     * */
    public float getTargetRate(){
        return trate;
    }

    /**
     * Return the actual decoding rate in bits per pixel.
     *
     * @return Actual decoding rate in bpp.
     * */
    public float getActualRate() {
        arate = anbytes*8f/hd.getMaxCompImgWidth()/hd.getMaxCompImgHeight();
        return arate;
    }

    /**
     * Return the target number of read bytes.
     *
     * @return Target decoding rate in bytes.
     * */
    public int getTargetNbytes(){
        return tnbytes;
    }

    /**
     * Return the actual number of read bytes.
     *
     * @return Actual decoding rate in bytes.
     * */
    public int getActualNbytes(){
        return anbytes;
    }

    /** Returns the horizontal offset of tile partition */
    public int getTilePartULX() {
        return hd.getTilingOrigin(null).x;
    }

    /** Returns the vertical offset of tile partition */
    public int getTilePartULY() {
        return hd.getTilingOrigin(null).y;
    }

    /** Returns the nominal tile width */
    public int getNomTileWidth() {
        return hd.getNomTileWidth();
    }

    /** Returns the nominal tile height */
    public int getNomTileHeight() {
        return hd.getNomTileHeight();
    }
}
