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
 * $RCSfile: RectROIMaskGenerator.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:23 $
 * $State: Exp $
 *
 * Class:                   RectROIMaskGenerator
 *
 * Description:             Generates masks when only rectangular ROIs exist
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
import jj2000.j2k.roi.*;

/**
 * This class generates the ROI masks when there are only rectangular ROIs in
 * the image. The ROI mask generation can then be simplified by only
 * calculating the boundaries of the ROI mask in the particular subbands
 *
 * <P>The values are calculated from the scaling factors of the ROIs. The
 * values with which to scale are equal to u-umin where umin is the lowest
 * scaling factor within the block. The umin value is sent to the entropy
 * coder to be used for scaling the distortion values.
 *
 * <P> To generate and to store the boundaries of the ROIs, the class
 * SubbandRectROIMask is used. There is one tree of SubbandMasks for each
 * component.
 *
 * @see SubbandRectROIMask
 *
 * @see ROIMaskGenerator
 *
 * @see ArbROIMaskGenerator
 *  */
public class RectROIMaskGenerator extends ROIMaskGenerator{

    /** The upper left xs of the ROIs*/
    private int[] ulxs;

    /** The upper left ys of the ROIs*/
    private int[] ulys;

    /** The lower right xs of the ROIs*/
    private int[] lrxs;

    /** The lower right ys of the ROIs*/
    private int[] lrys;

    /** Number of ROIs */
    private int nrROIs[];

    /** The tree of subbandmask. One for each component */
    private SubbandRectROIMask[] sMasks;


    /**
     * The constructor of the mask generator. The constructor is called with
     * the ROI data. This data is stored in arrays that are used to generate
     * the SubbandRectROIMask trees for each component.
     *
     * @param ROIs The ROI info.
     *
     * @param nrc number of components.
     * */
    public RectROIMaskGenerator(ROI[] ROIs, int nrc){
        super(ROIs, nrc);
        int nr=ROIs.length;
        int r,c;
        nrROIs=new int[nrc];
        sMasks=new SubbandRectROIMask[nrc];

        // Count number of ROIs per component
        for(r=nr-1;r>=0;r--){
            nrROIs[ROIs[r].comp]++;
        }
    }


    /**
     * This functions gets a DataBlk the size of the current code-block and
     * fills this block with the ROI mask.
     *
     * <P> In order to get the mask for a particular Subband, the subband tree
     * is traversed and at each decomposition, the ROI masks are computed. The
     * roi bondaries for each subband are stored in the SubbandRectROIMask
     * tree.
     *
     * @param db The data block that is to be filled with the mask
     *
     * @param sb The root of the subband tree to which db belongs
     *
     * @param magbits The max number of magnitude bits in any code-block
     *
     * @param c The component for which to get the mask
     *
     * @return Whether or not a mask was needed for this tile
     * */
    public boolean getROIMask(DataBlkInt db, Subband sb, int magbits, int c){
        int x = db.ulx;
        int y = db.uly;
        int w = db.w;
        int h = db.h;
        int[] mask = db.getDataInt();
        int i,j,k,r,mink,minj,maxk,maxj;
        int ulx=0,uly=0,lrx=0,lry=0;
        int wrap;
        int maxROI;
        int[] culxs;
        int[] culys;
        int[] clrxs;
        int[] clrys;
        SubbandRectROIMask srm;

        // If the ROI bounds have not been calculated for this tile and
        // component, do so now.
        if(!tileMaskMade[c]){
            makeMask(sb,magbits,c);
            tileMaskMade[c] = true;
        }

        if(!roiInTile) {
            return false;
        }

        // Find relevant subband mask and get ROI bounds
        srm = (SubbandRectROIMask)sMasks[c].getSubbandRectROIMask(x,y);
        culxs = srm.ulxs;
        culys = srm.ulys;
        clrxs = srm.lrxs;
        clrys = srm.lrys;
        maxROI = culxs.length-1;
        // Make sure that only parts of ROIs within the code-block are used
        // and make the bounds local to this block the LR bounds are counted
        // as the distance from the lower right corner of the block
        x -= srm.ulx;
        y -= srm.uly;
        for(r=maxROI; r>=0; r--){
            ulx = culxs[r]-x;
            if(ulx<0) {
                ulx = 0;
            } else if(ulx>=w) {
                ulx = w;
            }

            uly = culys[r]-y;
            if(uly<0) {
                uly = 0;
            } else if(uly>=h) {
                uly = h;
            }

            lrx = clrxs[r]-x;
            if(lrx<0) {
                lrx = -1;
            } else if(lrx>=w) {
                lrx = w-1;
            }

            lry = clrys[r]-y;
            if(lry<0) {
                lry = -1;
            } else if(lry>=h) {
                lry = h-1;
            }

            // Add the masks of the ROI
	    i = w*lry+lrx;
	    maxj = (lrx-ulx);
	    wrap = w-maxj-1;
	    maxk = lry-uly;

	    for(k=maxk; k>=0; k--){
		for(j=maxj;j>=0;j--,i--)
		    mask[i] = magbits;
		i-=wrap;
	    }
        }
	return true;
    }

    /**
     * This function returns the relevant data of the mask generator
     * */
    public String toString(){
        return("Fast rectangular ROI mask generator");
    }

    /**
     * This function generates the ROI mask for the entire tile. The mask is
     * generated for one component. This method is called once for each tile
     * and component.
     *
     * @param sb The root of the subband tree used in the decomposition
     *
     * @param n component number
     * */
    public void makeMask(Subband sb, int magbits, int n){
        int nr = nrROIs[n];
        int r;
        int ulx,uly,lrx,lry;
        int tileulx = sb.ulcx;
        int tileuly = sb.ulcy;
        int tilew = sb.w;
        int tileh = sb.h;
        ROI[] ROIs=rois; // local copy

        ulxs = new int[nr];
        ulys = new int[nr];
        lrxs = new int[nr];
        lrys = new int[nr];

        nr=0;

        for(r=ROIs.length-1;r>=0;r--){
            if(ROIs[r].comp==n){
                ulx = ROIs[r].ulx;
                uly = ROIs[r].uly;
                lrx = ROIs[r].w+ulx-1;
                lry = ROIs[r].h+uly-1;

                if( ulx > (tileulx + tilew -1 ) ||
                    uly > (tileuly + tileh -1 ) ||
                    lrx < tileulx || lry < tileuly ) // no part of ROI in tile
                    continue;

                // Check bounds
                ulx -= tileulx;
                lrx -= tileulx;
                uly -= tileuly;
                lry -= tileuly;

                ulx = (ulx<0) ? 0 : ulx;
                uly = (uly<0) ? 0 : uly;
                lrx = (lrx > (tilew-1)) ? tilew-1 : lrx;
                lry = (lry > (tileh-1)) ? tileh-1 : lry;

                ulxs[nr] = ulx;
                ulys[nr] = uly;
                lrxs[nr] = lrx;
                lrys[nr] = lry;
                nr++;
            }
        }
        if(nr==0) {
            roiInTile=false;
        }
        else {
            roiInTile=true;
        }
        sMasks[n]=new SubbandRectROIMask(sb,ulxs,ulys,lrxs,lrys,nr);
    }
}





