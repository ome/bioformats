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
 * $RCSfile: ROIDeScaler.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:21 $
 * $State: Exp $
 *
 *
 * Class:                   ROIDeScaler
 *
 * Description:             The class taking care of de-scaling ROI coeffs.
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
 * */
package jj2000.j2k.roi;

import jj2000.j2k.quantization.dequantizer.*;
import jj2000.j2k.codestream.reader.*;
import jj2000.j2k.wavelet.synthesis.*;
import jj2000.j2k.codestream.*;
import jj2000.j2k.entropy.*;
import jj2000.j2k.decoder.*;
import jj2000.j2k.image.*;
import jj2000.j2k.util.*;
import jj2000.j2k.io.*;
import jj2000.j2k.*;

import java.io.*;

import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageReadParamJava;

/**
 * This class takes care of the de-scaling of ROI coefficients. The de-scaler
 * works on a tile basis and any mask that is generated is for the current
 * mask only
 *
 * <P>Default implementations of the methods in 'MultiResImgData' are provided
 * through the 'MultiResImgDataAdapter' abstract class.
 *
 * <P>Sign magnitude representation is used (instead of two's complement) for
 * the output data. The most significant bit is used for the sign (0 if
 * positive, 1 if negative). Then the magnitude of the quantized coefficient
 * is stored in the next most significat bits. The most significant magnitude
 * bit corresponds to the most significant bit-plane and so on.
 * */
public class ROIDeScaler extends MultiResImgDataAdapter
    implements CBlkQuantDataSrcDec{

    /** The MaxShiftSpec containing the scaling values for all tile-components
     * */
    private MaxShiftSpec mss;

    /** The prefix for ROI decoder options: 'R' */
    public final static char OPT_PREFIX = 'R';

    /** The list of parameters that is accepted by the entropy decoders. They
     * start with 'R'. */
    private final static String [][] pinfo = {
	{ "Rno_roi",null,
	  "This argument makes sure that the no ROI de-scaling is performed. "+
	  "Decompression is done like there is no ROI in the image",null},
    };

    /** The entropy decoder from where to get the compressed data (the source)
     * */
    private CBlkQuantDataSrcDec src;

    /**
     * Constructor of the ROI descaler, takes EntropyDEcoder as source of data
     * to de-scale.
     *
     * @param src The EntropyDecoder that is the source of data.
     *
     * @param mss The MaxShiftSpec containing the scaling values for all
     * tile-components
     * */
    public ROIDeScaler(CBlkQuantDataSrcDec src, MaxShiftSpec mss){
        super(src);
        this.src=src;
        this.mss=mss;
    }

    /**
     * Returns the subband tree, for the specified tile-component. This method
     * returns the root element of the subband tree structure, see Subband and
     * SubbandSyn. The tree comprises all the available resolution levels.
     *
     * <P>The number of magnitude bits ('magBits' member variable) for each
     * subband is not initialized.
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
     * Returns the specified code-block in the current tile for the specified
     * component, as a copy (see below).
     *
     * <P>The returned code-block may be progressive, which is indicated by
     * the 'progressive' variable of the returned 'DataBlk' object. If a
     * code-block is progressive it means that in a later request to this
     * method for the same code-block it is possible to retrieve data which is
     * a better approximation, since meanwhile more data to decode for the
     * code-block could have been received. If the code-block is not
     * progressive then later calls to this method for the same code-block
     * will return the exact same data values.
     *
     * <P>The data returned by this method is always a copy of the internal
     * data of this object, if any, and it can be modified "in place" without
     * any problems after being returned. The 'offset' of the returned data is
     * 0, and the 'scanw' is the same as the code-block width. See the
     * 'DataBlk' class.
     *
     * <P>The 'ulx' and 'uly' members of the returned 'DataBlk' object contain
     * the coordinates of the top-left corner of the block, with respect to
     * the tile, not the subband.
     *
     * @param c The component for which to return the next code-block.
     *
     * @param m The vertical index of the code-block to return, in the
     * specified subband.
     *
     * @param n The horizontal index of the code-block to return, in the
     * specified subband.
     *
     * @param sb The subband in which the code-block to return is.
     *
     * @param cblk If non-null this object will be used to return the new
     * code-block. If null a new one will be allocated and returned. If the
     * "data" array of the object is non-null it will be reused, if possible,
     * to return the data.
     *
     * @return The next code-block in the current tile for component 'n', or
     * null if all code-blocks for the current tile have been returned.
     *
     * @see DataBlk
     * */
    public DataBlk getCodeBlock(int c, int m, int n, SubbandSyn sb,
                                  DataBlk cblk){
        return getInternCodeBlock(c,m,n,sb,cblk);
    }

    /**
     * Returns the specified code-block in the current tile for the specified
     * component (as a reference or copy).
     *
     * <P>The returned code-block may be progressive, which is indicated by
     * the 'progressive' variable of the returned 'DataBlk' object. If a
     * code-block is progressive it means that in a later request to this
     * method for the same code-block it is possible to retrieve data which is
     * a better approximation, since meanwhile more data to decode for the
     * code-block could have been received. If the code-block is not
     * progressive then later calls to this method for the same code-block
     * will return the exact same data values.
     *
     * <P>The data returned by this method can be the data in the internal
     * buffer of this object, if any, and thus can not be modified by the
     * caller. The 'offset' and 'scanw' of the returned data can be
     * arbitrary. See the 'DataBlk' class.
     *
     * <P>The 'ulx' and 'uly' members of the returned 'DataBlk' object contain
     * the coordinates of the top-left corner of the block, with respect to
     * the tile, not the subband.
     *
     * @param c The component for which to return the next code-block.
     *
     * @param m The vertical index of the code-block to return, in the
     * specified subband.
     *
     * @param n The horizontal index of the code-block to return, in the
     * specified subband.
     *
     * @param sb The subband in which the code-block to return is.
     *
     * @param cblk If non-null this object will be used to return the new
     * code-block. If null a new one will be allocated and returned. If the
     * "data" array of the object is non-null it will be reused, if possible,
     * to return the data.
     *
     * @return The requested code-block in the current tile for component 'c'.
     *
     * @see DataBlk
     * */
    public DataBlk getInternCodeBlock(int c, int m, int n, SubbandSyn sb,
                                        DataBlk cblk){
        int mi,i,j,k,wrap;
        int ulx, uly, w, h;
        int[] data;                       // local copy of quantized data
        int tmp;
        int limit;

        // Get data block from entropy decoder
        cblk = src.getInternCodeBlock(c,m,n,sb,cblk);

        // If there are no ROIs in the tile, Or if we already got all blocks
        boolean noRoiInTile = false;
        if(mss==null || mss.getTileCompVal(getTileIdx(),c)==null )
            noRoiInTile = true;

        if (noRoiInTile || cblk==null) {
            return cblk;
        }
        data = (int[])cblk.getData();
        ulx = cblk.ulx;
        uly = cblk.uly;
        w = cblk.w;
        h = cblk.h;

	// Scale coefficients according to magnitude. If the magnitude of a
	// coefficient is lower than 2 pow 31-magbits then it is a background
	// coeff and should be up-scaled
	int boost = ((Integer) mss.getTileCompVal(getTileIdx(),c)).intValue();
        int mask = ((1<<sb.magbits)-1)<<(31-sb.magbits);
        int mask2 = (~mask)&0x7FFFFFFF;

	wrap=cblk.scanw-w;
	i=cblk.offset+cblk.scanw*(h-1)+w-1;
	for(j=h;j>0;j--){
	    for(k=w;k>0;k--,i--){
		tmp=data[i];
		if((tmp & mask) == 0 ) { // BG
		    data[i] = (tmp & 0x80000000) | (tmp << boost);
		}
                else { // ROI
                    if ((tmp & mask2) != 0) {
                        // decoded more than magbits bit-planes, set
                        // quantization mid-interval approx. bit just after
                        // the magbits.
                        data[i] = (tmp&(~mask2)) | (1<<(30-sb.magbits));
                    }
                }
	    }
	    i-=wrap;
	}
        return cblk;
    }

    /**
     * Creates a ROIDeScaler object. The information needed to create the
     * object is the Entropy decoder used and the parameters.
     *
     * @param src The source of data that is to be descaled
     *
     * @param pl The parameter list (or options).
     *
     * @param decSpec The decoding specifications
     *
     * @exception IllegalArgumentException If an error occurs while parsing
     * the options in 'pl'
     * */
    public static ROIDeScaler createInstance(CBlkQuantDataSrcDec src,
                                             J2KImageReadParamJava j2krparam,
                                             DecoderSpecs decSpec){
        // Check if no_roi specified in command line or no roi signalled
	// in bit stream
        boolean noRoi = j2krparam.getNoROIDescaling();
        if (noRoi || decSpec.rois == null) {
            // no_roi specified in commandline!
	    return new ROIDeScaler(src,null);
        }

        return new ROIDeScaler(src, decSpec.rois );
    }
}
