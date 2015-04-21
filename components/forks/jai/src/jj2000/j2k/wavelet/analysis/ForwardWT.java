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
 * $RCSfile: ForwardWT.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:31 $
 * $State: Exp $
 *
 * Class:                   ForwardWT
 *
 * Description:             This interface defines the specifics
 *                          of forward wavelet transforms
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
package jj2000.j2k.wavelet.analysis;

import jj2000.j2k.codestream.writer.*;
import jj2000.j2k.codestream.*;
import jj2000.j2k.wavelet.*;
import jj2000.j2k.image.*;
import jj2000.j2k.util.*;
import jj2000.j2k.*;

import java.io.*;
import java.util.*;

import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriteParamJava;
/**
 * This abstract class represents the forward wavelet transform functional
 * block. The functional block may actually be comprised of several classes
 * linked together, but a subclass of this abstract class is the one that is
 * returned as the functional block that performs the forward wavelet
 * transform.
 *
 * <P>This class assumes that data is transferred in code-blocks, as defined
 * by the 'CBlkWTDataSrc' interface. The internal calculation of the wavelet
 * transform may be done differently but a buffering class should convert to
 * that type of transfer.
 * */
public abstract class ForwardWT extends ImgDataAdapter
    implements ForwWT, CBlkWTDataSrc {

    /**
     * ID for the dyadic wavelet tree decomposition (also called "Mallat" in
     * JPEG 2000): 0x00.
     * */
    public final static int WT_DECOMP_DYADIC = 0;

    /** The prefix for wavelet transform options: 'W' */
    public final static char OPT_PREFIX = 'W';

    /** The list of parameters that is accepted for wavelet transform. Options
     * for the wavelet transform start with 'W'. */
    private final static String [][] pinfo = {
        { "Wlev", "<number of decomposition levels>",
          "Specifies the number of wavelet decomposition levels to apply to "+
          "the image. If 0 no wavelet transform is performed. All components "+
          "and all tiles have the same number of decomposition levels.","5"},
        { "Wwt", "[full]",
          "Specifies the wavelet transform to be used. Possible value is: "+
          "'full' (full page). The value 'full' performs a normal DWT.",
          "full"},
    };

    /**
     * Initializes this object for the specified number of tiles 'nt' and
     * components 'nc'.
     *
     * @param src The source of ImgData
     * */
    protected ForwardWT(ImgData src) {
        super(src);
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
     * Creates a ForwardWT object with the specified filters, and with other
     * options specified in the parameter list 'pl'.
     *
     * @param src The source of data to be transformed
     *
     * @param wp The writing parameters.
     *
     * @return A new ForwardWT object with the specified filters and options
     * from 'pl'.
     *
     * @exception IllegalArgumentException If mandatory parameters are missing
     * or if invalid values are given.
     * */
    public static ForwardWT createInstance(BlkImgDataSrc src,
                                           J2KImageWriteParamJava wp){
        int defdec,deflev;
        String decompstr;
        String wtstr;
        String pstr;
        StreamTokenizer stok;
        StringTokenizer strtok;
        int prefx,prefy;        // Partitioning reference point coordinates
/*
        // Check parameters
        pl.checkList(OPT_PREFIX, pl.toNameArray(pinfo));
*/
        deflev = ((Integer)wp.getDecompositionLevel().getDefault()).intValue();

        // partition reference point
        prefx = 0;
        prefy = 0;

        return new ForwWTFull(src, wp, prefx,prefy);
    }

}
