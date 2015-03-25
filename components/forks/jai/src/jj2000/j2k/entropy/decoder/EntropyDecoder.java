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
 * $RCSfile: EntropyDecoder.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:06 $
 * $State: Exp $
 *
 * Class:                   EntropyDecoder
 *
 * Description:             The abstract class for all entropy decoders.
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
package jj2000.j2k.entropy.decoder;

import jj2000.j2k.quantization.dequantizer.*;
import jj2000.j2k.codestream.reader.*;
import jj2000.j2k.wavelet.synthesis.*;
import jj2000.j2k.codestream.*;
import jj2000.j2k.entropy.*;
import jj2000.j2k.image.*;
import jj2000.j2k.io.*;
import jj2000.j2k.*;
import java.io.*;

/**
 * This is the abstract class from which all entropy decoders must
 * inherit. This class implements the 'MultiResImgData', therefore it has the
 * concept of a current tile and all operations are performed on the current
 * tile.
 *
 * <P>Default implementations of the methods in 'MultiResImgData' are provided
 * through the 'MultiResImgDataAdapter' abstract class.
 *
 * <P>Sign magnitude representation is used (instead of two's complement) for
 * the output data. The most significant bit is used for the sign (0 if
 * positive, 1 if negative). Then the magnitude of the quantized coefficient
 * is stored in the next most significat bits. The most significant magnitude
 * bit corresponds to the most significant bit-plane and so on.
 *
 * @see MultiResImgData
 * @see MultiResImgDataAdapter
 * */
public abstract class EntropyDecoder extends MultiResImgDataAdapter
    implements CBlkQuantDataSrcDec {

    /** The prefix for entropy decoder optiojns: 'C' */
    public final static char OPT_PREFIX = 'C';

    /** The list of parameters that is accepted by the entropy
     * decoders. They start with 'C'. */
    private final static String [][] pinfo = {
        {"Cverber", "[true|false]",
         "Specifies if the entropy decoder should be verbose about detected "+
         "errors. If 'true' a message is printed whenever an error is detected.",
         "true"},
        {"Cer", "[true|false]",
         "Specifies if error detection should be performed by the entropy "+
         "decoder engine. If errors are detected they will be concealed and "+
         "the resulting distortion will be less important. Note that errors "+
         "can only be detected if the encoder that generated the data "+
         "included error resilience information.", "true"},
    };

    /** The bit stream transport from where to get the compressed data
     * (the source) */
    protected CodedCBlkDataSrcDec src;

    /**
     * Initializes the source of compressed data.
     *
     * @param src From where to obtain the compressed data.
     * */
    public EntropyDecoder(CodedCBlkDataSrcDec src) {
        super(src);
        this.src = src;
    }

    /**
     * Returns the subband tree, for the specified tile-component. This method
     * returns the root element of the subband tree structure, see Subband and
     * SubbandSyn. The tree comprises all the available resolution levels.
     *
     * <P>The number of magnitude bits ('magBits' member variable) for
     * each subband is not initialized.
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

}
