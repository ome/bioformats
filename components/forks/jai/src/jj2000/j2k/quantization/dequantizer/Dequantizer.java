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
 * $RCSfile: Dequantizer.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:18 $
 * $State: Exp $
 *
 * Class:                   Dequantizer
 *
 * Description:             The abstract class for all dequantizers.
 *
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
package jj2000.j2k.quantization.dequantizer;
import jj2000.j2k.image.invcomptransf.*;
import jj2000.j2k.wavelet.synthesis.*;
import jj2000.j2k.entropy.decoder.*;
import jj2000.j2k.codestream.*;
import jj2000.j2k.entropy.*;
import jj2000.j2k.decoder.*;
import jj2000.j2k.wavelet.*;
import jj2000.j2k.image.*;
import jj2000.j2k.io.*;
import jj2000.j2k.*;

import java.io.*;

/**
 * This is the abstract class from which all dequantizers must inherit. This
 * class has the concept of a current tile and all operations are performed on
 * the current tile.
 *
 * <p>This class provides default implemenations for most of the methods
 * (wherever it makes sense), under the assumption that the image and
 * component dimensions, and the tiles, are not modifed by the dequantizer. If
 * that is not the case for a particular implementation then the methods
 * should be overriden.</p>
 *
 * <p>Sign magnitude representation is used (instead of two's complement) for
 * the input data. The most significant bit is used for the sign (0 if
 * positive, 1 if negative). Then the magnitude of the quantized coefficient
 * is stored in the next most significat bits. The most significant magnitude
 * bit corresponds to the most significant bit-plane and so on.</p>
 *
 * <p>The output data is either in floating-point, or in fixed-point two's
 * complement. In case of floating-point data the the value returned by
 * getFixedPoint() must be 0. If the case of fixed-point data the number of
 * fractional bits must be defined at the constructor of the implementing
 * class and all operations must be performed accordingly. Each component may
 * have a different number of fractional bits.</p>
 * */
public abstract class Dequantizer extends MultiResImgDataAdapter
    implements CBlkWTDataSrcDec {

    /** The prefix for dequantizer options: 'Q' */
    public final static char OPT_PREFIX = 'Q';

    /** The list of parameters that is accepted by the bit stream
     * readers. They start with 'Q' */
    private static final String [][] pinfo = null;

    /** The entropy decoder from where to get the quantized data (the
     * source). */
    protected CBlkQuantDataSrcDec src;

    /** The "range bits" for each transformed component */
    protected int rb[] = null;

    /** The "range bits" for each un-transformed component */
    protected int utrb[] = null;

    /** The inverse component transformation specifications */
    private CompTransfSpec cts;

    /** Reference to the wavelet filter specifications */
    private SynWTFilterSpec wfs;

    /**
     * Initializes the source of compressed data.
     *
     * @param src From where to obtain the quantized data.
     *
     * @param rb The number of "range bits" for each component (must be the
     * "range bits" of the un-transformed components. For a definition of
     * "range bits" see the getNomRangeBits() method.
     *
     * @see #getNomRangeBits
     * */
    public Dequantizer(CBlkQuantDataSrcDec src,int utrb[],
                       DecoderSpecs decSpec) {
        super(src);
        if (utrb.length != src.getNumComps()) {
            throw new IllegalArgumentException();
        }
        this.src  = src;
        this.utrb = utrb;
        this.cts  = decSpec.cts;
        this.wfs  = decSpec.wfs;
    }

    /**
     * Returns the number of bits, referred to as the "range bits",
     * corresponding to the nominal range of the data in the specified
     * component.
     *
     * <p>The returned value corresponds to the nominal dynamic range of the
     * reconstructed image data, not of the wavelet coefficients
     * themselves. This is because different subbands have different gains and
     * thus different nominal ranges. To have an idea of the nominal range in
     * each subband the subband analysis gain value from the subband tree
     * structure, returned by the getSynSubbandTree() method, can be used. See
     * the Subband class for more details.</p>
     *
     * <p>If this number is <i>b</b> then for unsigned data the nominal range
     * is between 0 and 2^b-1, and for signed data it is between -2^(b-1) and
     * 2^(b-1)-1.</p>
     *
     * @param c The index of the component
     *
     * @return The number of bits corresponding to the nominal range of the
     * data.
     *
     * @see Subband
     * */
    public int getNomRangeBits(int c) {
        return rb[c];
    }

    /**
     * Returns the subband tree, for the specified tile-component. This method
     * returns the root element of the subband tree structure, see Subband and
     * SubbandSyn. The tree comprises all the available resolution levels.
     *
     * <P>The number of magnitude bits ('magBits' member variable) for each
     * subband may have not been not initialized (it depends on the actual
     * dequantizer and its implementation). However, they are not necessary
     * for the subsequent steps in the decoder chain.
     *
     * @param t The index of the tile, from 0 to T-1.
     *
     * @param c The index of the component, from 0 to C-1.
     *
     * @return The root of the tree structure.
     * */
    public SubbandSyn getSynSubbandTree(int t,int c) {
        return src.getSynSubbandTree(t,c);
    }

    /**
     * Returns the horizontal code-block partition origin. Allowable values
     * are 0 and 1, nothing else.
     * */
    public int getCbULX() {
        return src.getCbULX();
    }

    /**
     * Returns the vertical code-block partition origin. Allowable values are
     * 0 and 1, nothing else.
     * */
    public int getCbULY() {
        return src.getCbULY();
    }

    /**
     * Returns the parameters that are used in this class and
     * implementing classes. It returns a 2D String array. Each of the
     * 1D arrays is for a different option, and they have 3
     * elements. The first element is the option name, the second one
     * is the synopsis and the third one is a long description of what
     * the parameter is. The synopsis or description may be 'null', in
     * which case it is assumed that there is no synopsis or
     * description of the option, respectively. Null may be returned
     * if no options are supported.
     *
     * @return the options name, their synopsis and their explanation, 
     * or null if no options are supported.
     * */
    public static String[][] getParameterInfo() {
        return pinfo;
    }

    /**
     * Changes the current tile, given the new indexes. An
     * IllegalArgumentException is thrown if the indexes do not
     * correspond to a valid tile.
     *
     * <P>This default implementation changes the tile in the source
     * and re-initializes properly component transformation variables..
     *
     * @param x The horizontal index of the tile.
     *
     * @param y The vertical index of the new tile.
     * */
    public void setTile(int x, int y) {
        src.setTile(x,y);
	tIdx = getTileIdx(); // index of the current tile

        // initializations
        int cttype = 0;
        if( ((Integer)cts.getTileDef(tIdx)).intValue()==InvCompTransf.NONE )
            cttype = InvCompTransf.NONE;
        else {
            int nc = src.getNumComps() > 3 ? 3 : src.getNumComps(); 
            int rev = 0;
            for(int c=0; c<nc; c++)
                rev += (wfs.isReversible(tIdx,c)?1:0);
            if(rev==3){
                // All WT are reversible
                cttype = InvCompTransf.INV_RCT;
            }
            else if(rev==0){
                // All WT irreversible
                cttype = InvCompTransf.INV_ICT;
            }
            else{
                // Error
                throw new IllegalArgumentException("Wavelet transformation "+
                                                   "and "+
                                                   "component transformation"+
                                                   " not coherent in tile"+
                                                   tIdx);
            }
        }

        switch(cttype){
        case InvCompTransf.NONE:
            rb = utrb;
            break;
        case InvCompTransf.INV_RCT:
            rb = InvCompTransf.
                calcMixedBitDepths(utrb,InvCompTransf.INV_RCT,null);
            break;
        case InvCompTransf.INV_ICT:
            rb = InvCompTransf.
                calcMixedBitDepths(utrb,InvCompTransf.INV_ICT,null);
            break;
        default:
            throw new IllegalArgumentException("Non JPEG 2000 part I "+
                                               "component"+
                                               " transformation for tile: "+
                                               tIdx);
        }
    }

    /**
     * Advances to the next tile, in standard scan-line order (by rows then
     * columns). An NoNextElementException is thrown if the current tile is
     * the last one (i.e. there is no next tile).
     *
     * <P>This default implementation just advances to the next tile in the
     * source and re-initializes properly component transformation variables.
     * */
    public void nextTile() {
        src.nextTile();
	tIdx = getTileIdx(); // index of the current tile

        // initializations
        int cttype = ((Integer)cts.getTileDef(tIdx)).intValue();
        switch(cttype){
        case InvCompTransf.NONE:
            rb = utrb;
            break;
        case InvCompTransf.INV_RCT:
            rb = InvCompTransf.
                calcMixedBitDepths(utrb,InvCompTransf.INV_RCT,null);
            break;
        case InvCompTransf.INV_ICT:
            rb = InvCompTransf.
                calcMixedBitDepths(utrb,InvCompTransf.INV_ICT,null);
            break;
        default:
            throw new IllegalArgumentException("Non JPEG 2000 part I "+
                                               "component"+
                                               " transformation for tile: "+
                                               tIdx);
        }
    }

}
