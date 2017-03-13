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
 * $RCSfile: ArbROIMaskGenerator.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:22 $
 * $State: Exp $
 *
 * Class:                   ArbROIMaskGenerator
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

import jj2000.j2k.quantization.quantizer.*;
import jj2000.j2k.codestream.writer.*;
import jj2000.j2k.wavelet.analysis.*;
import jj2000.j2k.quantization.*;
import jj2000.j2k.image.input.*;
import jj2000.j2k.wavelet.*;
import jj2000.j2k.image.*;
import jj2000.j2k.util.*;
import jj2000.j2k.roi.*;

/**
 * This class generates the ROI bit-mask when, at least, one ROI is not
 * rectangular. In this case, the fast ROI bit-mask algorithm generation can
 * not be used.
 *
 * <P>The values are calculated from the scaling factors of the ROIs. The
 * values with which to scale are equal to u-umin where umin is the lowest
 * scaling factor within the block. The umin value is sent to the entropy
 * coder to be used for scaling the distortion values.
 *
 * @see ROIMaskGenerator
 *
 * @see ArbROIMaskGenerator
 * */
public class ArbROIMaskGenerator extends ROIMaskGenerator{

    /** The source of quantized wavelet transform coefficients */
    private Quantizer src;

    /** The ROI mask for the current tile for all components*/
    private int[][] roiMask;

    /** The low frequency part of a mask line */
    private int[] maskLineLow;

    /** The High frequency part of a mask line */
    private int[] maskLineHigh;

    /** A line or column of the mask with padding  */
    private int[] paddedMaskLine;

    /** Flag indicating if any ROI was found to be in this tile */
    private boolean roiInTile;

    /**
     * The constructor of the arbitrary mask generator
     *
     * @param rois The ROI info.
     *
     * @param nrc The number of components
     *
     * @param src The quantizer module
     * */
    public ArbROIMaskGenerator(ROI[] rois, int nrc, Quantizer src){
        super(rois,nrc);
        roiMask=new int[nrc][];
        this.src = src;
    }

    /**
     * This functions gets a DataBlk the size of the current code-block an
     * fills this block with the ROI mask.
     *
     * <P> In order to get the mask for a particular Subband, the subband tree
     * is traversed and at each decomposition, the ROI masks are computed.
     *
     * <P> The widths of the synthesis filters corresponding to the wavelet
     * filters used in the wavelet transform are used to expand the ROI masks
     * in the decompositions.
     *
     * @param db The data block that is to be filled with the mask
     *
     * @param sb The root of the subband tree to which db belongs
     *
     * @param magbits The max number of magnitude bits in any code-block
     *
     * @param c The number of the component
     *
     * @return Whether or not a mask was needed for this tile
     **/
    public boolean getROIMask(DataBlkInt db, Subband sb, int magbits, int c){
        int x = db.ulx;
        int y = db.uly;
        int w = db.w;
        int h = db.h;
        int tilew = sb.w;
        int tileh = sb.h;
        int[] maskData= (int[])db.getData();
        int i, j, k, bi, wrap;

        // If the ROI mask has not been calculated for this tile and
        // component, do so now.
        if(!tileMaskMade[c]){
            makeMask(sb,magbits,c);
            tileMaskMade[c]=true;
        }
        if(!roiInTile)
            return false;

        int[] mask = roiMask[c]; // local copy

        // Copy relevant part of the ROI mask to the datablock
        i=(y+h-1)*tilew+x+w-1;
        bi=w*h-1;
        wrap=tilew-w;
        for(j=h ; j>0 ; j--){
            for(k=w ; k>0 ; k--, i--, bi--){
                maskData[bi]=mask[i];
            }
            i-=wrap;
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
     * This function generates the ROI mask for one tile-component.
     *
     * <P> Once the mask is generated in the pixel domain. it is decomposed
     * following the same decomposition scheme as the wavelet transform.
     *
     * @param sb The root of the subband tree used in the decomposition
     *
     * @param magbits The max number of magnitude bits in any code-block
     *
     * @param c component number
     */
    public void makeMask(Subband sb, int magbits, int c){
        int mask[]; // local copy
        ROI rois[] = this.rois;  // local copy
        int i,j,k,r,mink,minj,maxj;
        int lrx,lry;
        int x,y,w,h;
        int cx,cy,rad;
        int wrap;
        int curScalVal;
        int tileulx = sb.ulcx;
        int tileuly = sb.ulcy;
        int tilew = sb.w;
        int tileh = sb.h;
        int lineLen = (tilew>tileh) ? tilew : tileh;

        // Make sure there is a sufficiently large mask buffer
        if(roiMask[c] == null || ( roiMask[c].length < (tilew*tileh ))){
            roiMask[c] = new int[tilew*tileh];
            mask = roiMask[c];
        }
        else{
            mask = roiMask[c];
            for(i=tilew*tileh-1; i>=0; i--)
                mask[i] = 0;
        }

        // Make sure there are sufficiently large line buffers
        if(maskLineLow == null || (maskLineLow.length < (lineLen+1)/2))
            maskLineLow = new int[(lineLen+1)/2];
        if(maskLineHigh == null || (maskLineHigh.length < (lineLen+1)/2))
            maskLineHigh = new int[(lineLen+1)/2];

        roiInTile = false;
        // Generate ROIs in pixel domain:
        for(r=rois.length-1; r>=0; r--) {
            if(rois[r].comp == c) {
		curScalVal = magbits;

                if (rois[r].arbShape) {
                    ImgReaderPGM maskPGM = rois[r].maskPGM; // Local copy

                    if( (src.getImgWidth() != maskPGM.getImgWidth()) ||
                        (src.getImgHeight() != maskPGM.getImgHeight()) )
                        throw new IllegalArgumentException("Input image and"+
                                                           " ROI mask must "+
                                                           "have the same "+
                                                           "size");
                    x = src.getImgULX();
                    y = src.getImgULY();
                    lrx = x+src.getImgWidth()-1;
                    lry = y+src.getImgHeight()-1;
                    if( (x>tileulx+tilew) || (y>tileuly+tileh) ||
                        (lrx<tileulx) || (lry<tileuly) ) // Roi not in tile
                        continue;

                    // Check bounds
                    x   -= tileulx;
                    lrx -= tileulx;
                    y   -= tileuly;
                    lry -= tileuly;

                    int offx = 0;
                    int offy = 0;
                    if(x<0) {
                        offx = -x;
                        x = 0;
                    }
                    if(y<0) {
                        offy = -y;
                        y = 0;
                    }
                    w = (lrx > (tilew-1))? tilew-x:lrx+1-x;
                    h = (lry > (tileh-1))? tileh-y:lry+1-y;


                    // Get shape line by line to reduce memory
                    DataBlkInt srcblk = new DataBlkInt();
                    int mDcOff = -ImgReaderPGM.DC_OFFSET;
                    int nROIcoeff = 0;
                    int[] src_data;
                    srcblk.ulx = offx;
                    srcblk.w = w;
                    srcblk.h = 1;

                    i = (y+h-1)*tilew+x+w-1;
                    maxj = w;
                    wrap = tilew-maxj;
                    for(k=h; k>0; k--){
                        srcblk.uly = offy+k-1;
                        srcblk = (DataBlkInt)maskPGM.
                            getInternCompData(srcblk,0);
                        src_data = srcblk.getDataInt();

                        for(j=maxj; j>0; j--,i--){
                            if(src_data[j-1] != mDcOff) {
                                mask[i] = curScalVal;
                                nROIcoeff++;
                            }
                        }
                        i -= wrap;
                    }

                    if(nROIcoeff != 0) {
                        roiInTile = true;
                    }
                }
                else if(rois[r].rect){ // Rectangular ROI
                    x = rois[r].ulx;
                    y = rois[r].uly;
                    lrx = rois[r].w+x-1;
                    lry = rois[r].h+y-1;

                    if( (x>tileulx+tilew) || (y>tileuly+tileh) ||
                        (lrx<tileulx) || (lry<tileuly) ) // Roi not in tile
                        continue;

                    roiInTile=true;

                    // Check bounds
                    x   -= tileulx;
                    lrx -= tileulx;
                    y   -= tileuly;
                    lry -= tileuly;

                    x = (x<0) ? 0:x;
                    y = (y<0) ? 0:y;
                    w = (lrx > (tilew-1))? tilew-x:lrx+1-x;
                    h = (lry > (tileh-1))? tileh-y:lry+1-y;

                    i = (y+h-1)*tilew+x+w-1;
                    maxj = w;
                    wrap = tilew-maxj;
                    for(k=h; k>0; k--){
                        for(j=maxj; j>0; j--,i--){
			    mask[i] = curScalVal;
                        }
                        i -= wrap;
                    }
                }
                else{ // Non-rectangular ROI. So far only circular case
                    cx = rois[r].x-tileulx;
                    cy = rois[r].y-tileuly;
                    rad = rois[r].r;
                    i = tileh*tilew-1;
                    for(k=tileh-1; k>=0; k--){
                        for(j=tilew-1; j>=0; j--,i--){
                            if(((j-cx)*(j-cx)+(k-cy)*(k-cy) < rad*rad)){
                                mask[i] = curScalVal;
                                roiInTile = true;
                            }
                        }
                    }
                }
            }
        }

        // If wavelet transform is used
        if(sb.isNode) {
            // Decompose the mask according to the subband tree
            // Calculate size of padded line buffer
            WaveletFilter vFilter = sb.getVerWFilter();
            WaveletFilter hFilter = sb.getHorWFilter();
            int lvsup =
                vFilter.getSynLowNegSupport()+vFilter.getSynLowPosSupport();
            int hvsup =
                vFilter.getSynHighNegSupport()+vFilter.getSynHighPosSupport();
            int lhsup =
                hFilter.getSynLowNegSupport()+hFilter.getSynLowPosSupport();
            int hhsup =
                hFilter.getSynHighNegSupport()+hFilter.getSynHighPosSupport();
            lvsup = (lvsup>hvsup)? lvsup:hvsup;
            lhsup = (lhsup>hhsup)? lhsup:hhsup;
            lvsup = (lvsup>lhsup)? lvsup:lhsup;
            paddedMaskLine = new int[lineLen+lvsup];

            if(roiInTile)
                decomp(sb,tilew,tileh,c);
        }
    }

    /**
     * This function decomposes the mask for a node in the subband tree.
     * after the mask is decomposed for a node, this function is called for
     * the children of the subband. The decomposition is done line by line
     * and column by column
     *
     * @param sb The subband that is to be used for the decomposition
     *
     * @param tilew The width of the current tile
     *
     * @param tileh The height of the current tile
     *
     * @param c component number
     */
    private void decomp(Subband sb, int tilew, int tileh, int c){
        int ulx = sb.ulx;
        int uly = sb.uly;
        int w = sb.w;
        int h = sb.h;
        int scalVal,maxVal = 0;
        int i,j,k,s,hi,mi = 0,pin,li;
        int hmax,lmax,smax;
        int wrap,lineoffs,lastlow;
        int[] mask = roiMask[c]; // local copy
        int[] low = maskLineLow; // local copy
        int[] high = maskLineHigh; // local copy
        int[] padLine = paddedMaskLine; // local copy
        int highFirst = 0;
        int lastpin;


        if(!sb.isNode)
            return;

        // HORIZONTAL DECOMPOSITION

        // Calculate number of high and low samples after decomposition
        // and get support for low and high filters
        WaveletFilter filter = sb.getHorWFilter();
        int lnSup = filter.getSynLowNegSupport();
        int hnSup = filter.getSynHighNegSupport();
        int lpSup = filter.getSynLowPosSupport();
        int hpSup = filter.getSynHighPosSupport();
        int lsup = lnSup+lpSup+1;
        int hsup = hnSup+hpSup+1;

        // Calculate number of high/low coeffis in subbands
        highFirst = sb.ulcx%2;
        if(sb.w%2==0){
            lmax = w/2-1;
            hmax = lmax;
        }
        else{
            if(highFirst==0){
                lmax = (w+1)/2-1;
                hmax = w/2-1;
            }
            else{
                hmax = (w+1)/2-1;
                lmax = w/2-1;
            }
        }

        int maxnSup = (lnSup>hnSup) ? lnSup:hnSup; // Maximum negative support
        int maxpSup = (lpSup>hpSup) ? lpSup:hpSup; // Maximum positive support


        // Set padding to 0
        for(pin=maxnSup-1;pin>=0;pin--)
            padLine[pin] = 0;
        for(pin=maxnSup+w-1+maxpSup;pin>=w;pin--)
            padLine[pin] = 0;

        // Do decomposition of all lines
        lineoffs = (uly+h)*tilew+ulx+w-1;
        for(j=h-1;j>=0;j--){
            lineoffs -= tilew;
            // Get the line to transform from the mask
            mi=lineoffs;
            for(k=w, pin=w-1+maxnSup ; k>0 ; k--,mi--,pin--){
                padLine[pin] = mask[mi];
            }

            lastpin = maxnSup+highFirst+2*lmax+lpSup;
            for(k=lmax; k>=0 ; k--,lastpin-=2){ // Low frequency samples
                pin = lastpin;
                for(s=lsup;s>0;s--,pin--){
                    scalVal = padLine[pin];
                    if(scalVal>maxVal)
                        maxVal = scalVal;
                }
                low[k] = maxVal;
                maxVal = 0;
            }
            lastpin = maxnSup-highFirst+2*hmax+1+hpSup;
            for(k=hmax; k>=0 ; k--,lastpin-=2){ // High frequency samples
                pin = lastpin;
                for(s=hsup;s>0;s--,pin--){
                    scalVal = padLine[pin];
                    if(scalVal>maxVal)
                        maxVal = scalVal;
                }
                high[k] = maxVal;
                maxVal = 0;
            }
            // Put the lows and highs back
            mi=lineoffs;
            for(k=hmax; k>=0; k--,mi--){
                mask[mi] = high[k];
            }
            for(k=lmax;k>=0;k--,mi--){
                mask[mi] = low[k];
            }
        }

        // VERTICAL DECOMPOSITION

        // Calculate number of high and low samples after decomposition
        // and get support for low and high filters
        filter = sb.getVerWFilter();
        lnSup = filter.getSynLowNegSupport();
        hnSup = filter.getSynHighNegSupport();
        lpSup = filter.getSynLowPosSupport();
        hpSup = filter.getSynHighPosSupport();
        lsup = lnSup+lpSup+1;
        hsup = hnSup+hpSup+1;

        // Calculate number of high/low coeffs in subbands
        highFirst = sb.ulcy%2;
        if(sb.h%2==0){
            lmax = h/2-1;
            hmax = lmax;
        }
        else{
            if(sb.ulcy%2==0){
                lmax = (h+1)/2-1;
                hmax = h/2-1;
            }
            else{
                hmax = (h+1)/2-1;
                lmax = h/2-1;
            }
        }

        maxnSup = (lnSup>hnSup) ? lnSup:hnSup; // Maximum negative support
        maxpSup = (lpSup>hpSup) ? lpSup:hpSup; // Maximum positive support

        // Set padding to 0
        for(pin=maxnSup-1;pin>=0;pin--)
            padLine[pin] = 0;
        for(pin=maxnSup+h-1+maxpSup;pin>=h;pin--)
            padLine[pin] = 0;

        // Do decomposition of all columns
        lineoffs=(uly+h-1)*tilew+ulx+w;
        for(j=w-1;j>=0;j--){
            lineoffs--;
            // Get the line to transform from the mask
            mi = lineoffs;
            for(k=h, pin=k-1+maxnSup ; k>0 ; k--,mi-=tilew,pin--){
                padLine[pin] = mask[mi];
            }
            lastpin=maxnSup+highFirst+2*lmax+lpSup;
            for(k=lmax; k>=0 ; k--,lastpin-=2){ // Low frequency samples
                pin = lastpin;
                for(s=lsup;s>0;s--,pin--){
                    scalVal = padLine[pin];
                    if(scalVal>maxVal)
                        maxVal = scalVal;
                }
                low[k] = maxVal;
                maxVal = 0;
            }
            lastpin = maxnSup-highFirst+2*hmax+1+hpSup;
            for(k=hmax; k>=0 ; k--,lastpin-=2){ // High frequency samples
                pin = lastpin;
                for(s=hsup;s>0;s--,pin--){
                    scalVal = padLine[pin];
                    if(scalVal>maxVal)
                        maxVal = scalVal;
                }
                high[k] = maxVal;
                maxVal = 0;
            }
            // Put the lows and highs back
            mi=lineoffs;
            for(k=hmax;k>=0;k--,mi-=tilew){
                mask[mi] = high[k];
            }
            for(k=lmax;k>=0;k--,mi-=tilew){
                mask[mi] = low[k];
            }
        }

        if(sb.isNode){
            decomp(sb.getHH(), tilew, tileh, c);
            decomp(sb.getLH(), tilew, tileh, c);
            decomp(sb.getHL(), tilew, tileh, c);
            decomp(sb.getLL(), tilew, tileh, c);
        }

    }
}





