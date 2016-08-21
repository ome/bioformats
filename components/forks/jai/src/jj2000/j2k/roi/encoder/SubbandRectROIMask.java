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
 * $RCSfile: SubbandRectROIMask.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:24 $
 * $State: Exp $
 *
 * Class:                   ROI
 *
 * Description:             This class describes the ROI mask for a subband
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
package jj2000.j2k.roi.encoder;

import jj2000.j2k.codestream.writer.*;
import jj2000.j2k.wavelet.analysis.*;
import jj2000.j2k.quantization.*;
import jj2000.j2k.wavelet.*;
import jj2000.j2k.image.*;
import jj2000.j2k.util.*;
import jj2000.j2k.roi.*;

/**
 * This class describes the ROI mask for a single subband. Each object of the
 * class contains the mask for a particular subband and also has references to
 * the masks of the children subbands of the subband corresponding to this
 * mask. This class describes subband masks for images containing only
 * rectangular ROIS
 * */
public class SubbandRectROIMask extends SubbandROIMask{

    /** The upper left x coordinates of the applicable ROIs */
    public int[] ulxs;

    /** The upper left y coordinates of the applicable ROIs */
    public int[] ulys;

    /** The lower right x coordinates of the applicable ROIs */
    public int[] lrxs;

    /** The lower right y coordinates of the applicable ROIs */
    public int[] lrys;

    /**
     * The constructor of the SubbandROIMask takes the dimensions of the
     * subband as parameters. A tree of masks is generated from the subband
     * sb. Each Subband contains the boundaries of each ROI.
     *
     * @param sb The subband corresponding to this Subband Mask
     *
     * @param ulxs The upper left x coordinates of the ROIs
     *
     * @param ulys The upper left y coordinates of the ROIs
     *
     * @param lrxs The lower right x coordinates of the ROIs
     *
     * @param lrys The lower right y coordinates of the ROIs
     *
     * @param lrys The lower right y coordinates of the ROIs
     *
     * @param nr Number of ROIs that affect this tile
     * */
    public SubbandRectROIMask(Subband sb, int[] ulxs, int[] ulys, int[] lrxs,
                          int[] lrys, int nr){
        super(sb.ulx,sb.uly,sb.w,sb.h);
        this.ulxs = ulxs;
        this.ulys = ulys;
        this.lrxs = lrxs;
        this.lrys = lrys;
        int r;

        if(sb.isNode){
            isNode=true;
            // determine odd/even - high/low filters
            int horEvenLow = sb.ulcx%2;
            int verEvenLow = sb.ulcy%2;

            // Get filter support lengths
            WaveletFilter hFilter = sb.getHorWFilter();
            WaveletFilter vFilter = sb.getVerWFilter();
            int hlnSup = hFilter.getSynLowNegSupport();
            int hhnSup = hFilter.getSynHighNegSupport();
            int hlpSup = hFilter.getSynLowPosSupport();
            int hhpSup = hFilter.getSynHighPosSupport();
            int vlnSup = vFilter.getSynLowNegSupport();
            int vhnSup = vFilter.getSynHighNegSupport();
            int vlpSup = vFilter.getSynLowPosSupport();
            int vhpSup = vFilter.getSynHighPosSupport();

            // Generate arrays for children
            int x,y;
            int[] lulxs = new int[nr];
            int[] lulys = new int[nr];
            int[] llrxs = new int[nr];
            int[] llrys = new int[nr];
            int[] hulxs = new int[nr];
            int[] hulys = new int[nr];
            int[] hlrxs = new int[nr];
            int[] hlrys = new int[nr];
            for(r=nr-1;r>=0;r--){ // For all ROI calculate ...
                // Upper left x for all children
                x = ulxs[r];
                if(horEvenLow==0){
                    lulxs[r] = (x+1-hlnSup)/2;
                    hulxs[r] = (x-hhnSup)/2;
                }
                else{
                    lulxs[r] = (x-hlnSup)/2;
                    hulxs[r] = (x+1-hhnSup)/2;
                }
                // Upper left y for all children
                y = ulys[r];
                if(verEvenLow==0){
                    lulys[r] = (y+1-vlnSup)/2;
                    hulys[r] = (y-vhnSup)/2;
                }
                else{
                    lulys[r] = (y-vlnSup)/2;
                    hulys[r] = (y+1-vhnSup)/2;
                }
                // lower right x for all children
                x = lrxs[r];
                if(horEvenLow==0){
                    llrxs[r] = (x+hlpSup)/2;
                    hlrxs[r] = (x-1+hhpSup)/2;
                }
                else{
                    llrxs[r] = (x-1+hlpSup)/2;
                    hlrxs[r] = (x+hhpSup)/2;
                }
                // lower right y for all children
                y=lrys[r];
                if(verEvenLow==0){
                    llrys[r] = (y+vlpSup)/2;
                    hlrys[r] = (y-1+vhpSup)/2;
                }
                else{
                    llrys[r] = (y-1+vlpSup)/2;
                    hlrys[r] = (y+vhpSup)/2;
                }
            }
            // Create children
            hh = new SubbandRectROIMask(sb.getHH(),hulxs,hulys,hlrxs,hlrys,nr);
            lh = new SubbandRectROIMask(sb.getLH(),lulxs,hulys,llrxs,hlrys,nr);
            hl = new SubbandRectROIMask(sb.getHL(),hulxs,lulys,hlrxs,llrys,nr);
            ll = new SubbandRectROIMask(sb.getLL(),lulxs,lulys,llrxs,llrys,nr);

        }
    }
}











