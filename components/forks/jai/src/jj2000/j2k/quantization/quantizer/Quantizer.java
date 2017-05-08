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
 * $RCSfile: Quantizer.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:20 $
 * $State: Exp $
 *
 * Class:                   Quantizer
 *
 * Description:             An abstract class for quantizers
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
package jj2000.j2k.quantization.quantizer;

import jj2000.j2k.codestream.writer.*;
import jj2000.j2k.wavelet.analysis.*;
import jj2000.j2k.quantization.*;
import jj2000.j2k.wavelet.*;
//import jj2000.j2k.encoder.*;
import jj2000.j2k.image.*;
import jj2000.j2k.util.*;

import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriteParamJava;

/**
 * This abstract class provides the general interface for quantizers. The
 * input of a quantizer is the output of a wavelet transform. The output of
 * the quantizer is the set of quantized wavelet coefficients represented in
 * sign-magnitude notation (see below).
 *
 * <p>This class provides default implementation for most of the methods
 * (wherever it makes sense), under the assumption that the image, component
 * dimensions, and the tiles, are not modifed by the quantizer. If it is not
 * the case for a particular implementation, then the methods should be
 * overriden.</p>
 *
 * <p>Sign magnitude representation is used (instead of two's complement) for
 * the output data. The most significant bit is used for the sign (0 if
 * positive, 1 if negative). Then the magnitude of the quantized coefficient
 * is stored in the next M most significat bits. The rest of the bits (least
 * significant bits) can contain a fractional value of the quantized
 * coefficient. This fractional value is not to be coded by the entropy
 * coder. However, it can be used to compute rate-distortion measures with
 * greater precision.</p>
 *
 * <p>The value of M is determined for each subband as the sum of the number
 * of guard bits G and the nominal range of quantized wavelet coefficients in
 * the corresponding subband (Rq), minus 1:</p>
 *
 * <p>M = G + Rq -1</p>
 *
 * <p>The value of G should be the same for all subbands. The value of Rq
 * depends on the quantization step size, the nominal range of the component
 * before the wavelet transform and the analysis gain of the subband (see
 * Subband).</p>
 *
 * <p>The blocks of data that are requested should not cross subband
 * boundaries.</p>
 *
 * <p>NOTE: At the moment only quantizers that implement the
 * 'CBlkQuantDataSrcEnc' interface are supported.</p>
 *
 * @see Subband
 * */
public abstract class Quantizer extends ImgDataAdapter
    implements CBlkQuantDataSrcEnc {

    /** The prefix for quantizer options: 'Q' */
    public final static char OPT_PREFIX = 'Q';

    /** The list of parameters that is accepted for quantization. Options 
     * for quantization start with 'Q'. */
    private final static String [][] pinfo = {
        { "Qtype", "[<tile-component idx>] <id> "+
          "[ [<tile-component idx>] <id> ...]",
          "Specifies which quantization type to use for specified "+
	  "tile-component. The default type is either 'reversible' or "+
          "'expounded' depending on whether or not the '-lossless' option "+
          " is specified.\n"+
          "<tile-component idx> : see general note.\n"+
          "<id>: Supported quantization types specification are : "+
          "'reversible' "+
	  "(no quantization), 'derived' (derived quantization step size) and "+
	  "'expounded'.\n"+
          "Example: -Qtype reversible or -Qtype t2,4-8 c2 reversible t9 "+
          "derived.",null},
        { "Qstep", "[<tile-component idx>] <bnss> "+
          "[ [<tile-component idx>] <bnss> ...]",
          "This option specifies the base normalized quantization step "+
	  "size (bnss) for tile-components. It is normalized to a "+
	  "dynamic range of 1 in the image domain. This parameter is "+
	  "ignored in reversible coding. The default value is '1/128'"+
          " (i.e. 0.0078125).",
          "0.0078125"},
        { "Qguard_bits", "[<tile-component idx>] <gb> "+
          "[ [<tile-component idx>] <gb> ...]",
          "The number of bits used for each tile-component in the quantizer"+
          " to avoid overflow (gb).","2"},
    };

    /** The source of wavelet transform coefficients */
    protected CBlkWTDataSrc src;

    /**
     * Initializes the source of wavelet transform coefficients.
     *
     * @param src The source of wavelet transform coefficients.
     * */
    public Quantizer(CBlkWTDataSrc src) {
        super(src);
        this.src = src;
    }

    /**
     * Returns the number of guard bits used by this quantizer in the
     * given tile-component.
     *
     * @param t Tile index
     *
     * @param c Component index
     *
     * @return The number of guard bits
     * */
    public abstract int getNumGuardBits(int t,int c);

    /**
     * Returns true if the quantizer of given tile-component uses derived
     * quantization step sizes.
     *
     * @param t Tile index
     *
     * @param c Component index
     *
     * @return True if derived quantization is used.
     * */
    public abstract boolean isDerived(int t,int c);

    /**
     * Calculates the parameters of the SubbandAn objects that depend on the
     * Quantizer. The 'stepWMSE' field is calculated for each subband which is
     * a leaf in the tree rooted at 'sb', for the specified component. The
     * subband tree 'sb' must be the one for the component 'n'.
     *
     * @param sb The root of the subband tree.
     *
     * @param n The component index.
     *
     * @see SubbandAn#stepWMSE
     * */
    protected abstract void calcSbParams(SubbandAn sb, int n);

    /**
     * Returns a reference to the subband tree structure representing the
     * subband decomposition for the specified tile-component.
     *
     * <P>This method gets the subband tree from the source and then
     * calculates the magnitude bits for each leaf using the method
     * calcSbParams().
     *
     * @param t The index of the tile.
     *
     * @param c The index of the component.
     *
     * @return The subband tree structure, see SubbandAn.
     *
     * @see SubbandAn
     *
     * @see Subband
     *
     * @see #calcSbParams
     * */
    public SubbandAn getAnSubbandTree(int t,int c) {
        SubbandAn sbba;

        // Ask for the wavelet tree of the source
        sbba = src.getAnSubbandTree(t,c);
        // Calculate the stepWMSE
        calcSbParams(sbba,c);
        return sbba;
    }

    /**
     * Returns the horizontal offset of the code-block partition. Allowable
     * values are 0 and 1, nothing else.
     * */
    public int getCbULX() {
        return src.getCbULX();
    }

    /**
     * Returns the vertical offset of the code-block partition. Allowable
     * values are 0 and 1, nothing else.
     * */
    public int getCbULY() {
        return src.getCbULY();
    }

    /**
     * Returns the parameters that are used in this class and implementing
     * classes. It returns a 2D String array. Each of the 1D arrays is for a
     * different option, and they have 3 elements. The first element is the
     * option name, the second one is the synopsis, the third one is a long
     * description of what the parameter is and the fourth is its default
     * value. The synopsis or description may be 'null', in which case it is
     * assumed that there is no synopsis or description of the option,
     * respectively. Null may be returned if no options are supported.
     *
     * @return the options name, their synopsis and their explanation, 
     * or null if no options are supported.
     * */
    public static String[][] getParameterInfo() {
        return pinfo;
    }

    /**
     * Creates a Quantizer object for the appropriate type of quantization
     * specified in the options in the parameter list 'pl', and having 'src'
     * as the source of data to be quantized. The 'rev' flag indicates if the
     * quantization should be reversible.
     *
     * NOTE: At the moment only sources of wavelet data that implement the
     * 'CBlkWTDataSrc' interface are supported.
     *
     * @param src The source of data to be quantized
     *
     * @param wp The encoder parameters
     *
     * @exception IllegalArgumentException If an error occurs while parsing
     * the options in 'pl'
     * */
    public static Quantizer createInstance(CBlkWTDataSrc src,
                                           J2KImageWriteParamJava wp) {
	// Instantiate quantizer
	return new StdQuantizer(src,wp);
    }

    /** 
     * Returns the maximum number of magnitude bits in any subband in the
     * current tile.
     *
     * @param c the component number
     *
     * @return The maximum number of magnitude bits in all subbands of the
     * current tile.
     * */
    public abstract int getMaxMagBits(int c);
 
}

