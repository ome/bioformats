/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2016 Open Microscopy Environment:
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
 * $RCSfile: StdQuantizer.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:20 $
 * $State: Exp $
 *
 * Class:                   StdQuantizer
 *
 * Description:             Scalar deadzone quantizer of integer or float
 *                          data.
 *
 *                          Mergerd from StdQuantizerInt and
 *                          StdQuantizerFloat from Joel Askelof.
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
package jj2000.j2k.quantization.quantizer;
import jj2000.j2k.codestream.writer.*;
import jj2000.j2k.wavelet.analysis.*;
import jj2000.j2k.quantization.*;
import jj2000.j2k.wavelet.*;
import jj2000.j2k.image.*;
import jj2000.j2k.*;

import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriteParamJava;

/**
 * This class implements scalar quantization of integer or floating-point
 * valued source data. The source data is the wavelet transformed image data
 * and the output is the quantized wavelet coefficients represented in
 * sign-magnitude (see below).
 *
 * <P>Sign magnitude representation is used (instead of two's complement) for
 * the output data. The most significant bit is used for the sign (0 if
 * positive, 1 if negative). Then the magnitude of the quantized coefficient
 * is stored in the next M most significat bits. The rest of the bits (least
 * significant bits) can contain a fractional value of the quantized
 * coefficient. This fractional value is not to be coded by the entropy
 * coder. However, it can be used to compute rate-distortion measures with
 * greater precision.
 *
 * <P>The value of M is determined for each subband as the sum of the number
 * of guard bits G and the nominal range of quantized wavelet coefficients in
 * the corresponding subband (Rq), minus 1:
 *
 * <P>M = G + Rq -1
 *
 * <P>The value of G should be the same for all subbands. The value of Rq
 * depends on the quantization step size, the nominal range of the component
 * before the wavelet transform and the analysis gain of the subband (see
 * Subband).
 *
 * <P>The blocks of data that are requested should not cross subband
 * boundaries.
 *
 * @see Subband
 *
 * @see Quantizer
 * */
public class StdQuantizer extends Quantizer {

    /** The number of mantissa bits for the quantization steps */
    public final static int QSTEP_MANTISSA_BITS = 11;

    /** The number of exponent bits for the quantization steps */
    // NOTE: formulas in 'convertFromExpMantissa()' and
    // 'convertToExpMantissa()' methods do not support more than 5 bits.
    public final static int QSTEP_EXPONENT_BITS = 5;

    /** The maximum value of the mantissa for the quantization steps */
    public final static int QSTEP_MAX_MANTISSA = (1<<QSTEP_MANTISSA_BITS)-1;

    /** The maximum value of the exponent for the quantization steps */
    public final static int QSTEP_MAX_EXPONENT = (1<<QSTEP_EXPONENT_BITS)-1;

    /** Natural log of 2, used as a convenience variable */
    private static double log2 = Math.log(2);

    /** The quantization type specifications */
    private QuantTypeSpec qts;

    /** The quantization step size specifications */
    private QuantStepSizeSpec qsss;

    /** The guard bits specifications */
    private GuardBitsSpec gbs;

    /** The 'CBlkWTDataFloat' object used to request data, used when
     * quantizing floating-point data. */
    // This variable makes the class thread unsafe, but it avoids allocating
    // new objects for code-block that is quantized.
    private CBlkWTDataFloat infblk;

    /**
     * Initializes the source of wavelet transform coefficients. The
     * constructor takes information on whether the quantizer is in
     * reversible, derived or expounded mode. If the quantizer is reversible
     * the value of 'derived' is ignored. If the source data is not integer
     * (int) then the quantizer can not be reversible.
     *
     * <P> After initializing member attributes, getAnSubbandTree is called for
     * all components setting the 'stepWMSE' for all subbands in the current
     * tile.
     *
     * @param src The source of wavelet transform coefficients.
     *
     * @param wp The encoder parameters
     * */
    public StdQuantizer(CBlkWTDataSrc src,J2KImageWriteParamJava wp){
	super(src);
	qts  = wp.getQuantizationType();
        qsss = wp.getQuantizationStep();
        gbs  = wp.getGuardBits();
    }

    /**
     * Returns the quantization type spec object associated to the quantizer.
     *
     * @return The quantization type spec
     * */
    public QuantTypeSpec getQuantTypeSpec(){
	return qts;
    }

    /**
     * Returns the number of guard bits used by this quantizer in the given
     * tile-component.
     *
     * @param t Tile index
     *
     * @param c Component index
     *
     * @return The number of guard bits
     * */
    public int getNumGuardBits(int t,int c){
        return ((Integer)gbs.getTileCompVal(t,c)).intValue();
    }

    /**
     * Returns true if the quantized data is reversible, for the specified
     * tile-component. For the quantized data to be reversible it is necessary
     * and sufficient that the quantization is reversible.
     *
     * @param t The tile to test for reversibility
     *
     * @param c The component to test for reversibility
     *
     * @return True if the quantized data is reversible, false if not.
     * */
    public boolean isReversible(int t,int c){
	return qts.isReversible(t,c);
    }

    /**
     * Returns true if given tile-component uses derived quantization step
     * sizes.
     *
     * @param t Tile index
     *
     * @param c Component index
     *
     * @return True if derived
     *
     */
    public boolean isDerived(int t,int c){
	return qts.isDerived(t,c);
    }

    /**
     * Returns the next code-block in the current tile for the specified
     * component, as a copy (see below). The order in which code-blocks are
     * returned is not specified. However each code-block is returned only
     * once and all code-blocks will be returned if the method is called 'N'
     * times, where 'N' is the number of code-blocks in the tile. After all
     * the code-blocks have been returned for the current tile calls to this
     * method will return 'null'.
     *
     * <P>When changing the current tile (through 'setTile()' or 'nextTile()')
     * this method will always return the first code-block, as if this method
     * was never called before for the new current tile.
     *
     * <P>The data returned by this method is always a copy of the
     * data. Therfore it can be modified "in place" without any problems after
     * being returned. The 'offset' of the returned data is 0, and the 'scanw'
     * is the same as the code-block width. See the 'CBlkWTData' class.
     *
     * <P>The 'ulx' and 'uly' members of the returned 'CBlkWTData' object
     * contain the coordinates of the top-left corner of the block, with
     * respect to the tile, not the subband.
     *
     * @param c The component for which to return the next code-block.
     *
     * @param cblk If non-null this object will be used to return the new
     * code-block. If null a new one will be allocated and returned. If the
     * "data" array of the object is non-null it will be reused, if possible,
     * to return the data.
     *
     * @return The next code-block in the current tile for component 'n', or
     * null if all code-blocks for the current tile have been returned.
     *
     * @see CBlkWTData
     * */
    public CBlkWTData getNextCodeBlock(int c,CBlkWTData cblk) {
        return getNextInternCodeBlock(c,cblk);
    }

    /**
     * Returns the next code-block in the current tile for the specified
     * component. The order in which code-blocks are returned is not
     * specified. However each code-block is returned only once and all
     * code-blocks will be returned if the method is called 'N' times, where
     * 'N' is the number of code-blocks in the tile. After all the code-blocks
     * have been returned for the current tile calls to this method will
     * return 'null'.
     *
     * <P>When changing the current tile (through 'setTile()' or 'nextTile()')
     * this method will always return the first code-block, as if this method
     * was never called before for the new current tile.
     *
     * <P>The data returned by this method can be the data in the internal
     * buffer of this object, if any, and thus can not be modified by the
     * caller. The 'offset' and 'scanw' of the returned data can be
     * arbitrary. See the 'CBlkWTData' class.
     *
     * <P>The 'ulx' and 'uly' members of the returned 'CBlkWTData' object
     * contain the coordinates of the top-left corner of the block, with
     * respect to the tile, not the subband.
     *
     * @param c The component for which to return the next code-block.
     *
     * @param cblk If non-null this object will be used to return the new
     * code-block. If null a new one will be allocated and returned. If the
     * "data" array of the object is non-null it will be reused, if possible,
     * to return the data.
     *
     * @return The next code-block in the current tile for component 'n', or
     * null if all code-blocks for the current tile have been returned.
     *
     * @see CBlkWTData
     * */
    public final CBlkWTData getNextInternCodeBlock(int c, CBlkWTData cblk) {
        // NOTE: this method is declared final since getNextCodeBlock() relies
        // on this particular implementation
        int k,j;
        int tmp,shiftBits,jmin;
        int w,h;
        int outarr[];
        float infarr[] = null;
        CBlkWTDataFloat infblk;
        float invstep;    // The inverse of the quantization step size
        boolean intq;     // flag for quantizig ints
        SubbandAn sb;
        float stepUDR;    // The quantization step size (for a dynamic
                          // range of 1, or unit)
        int g = ((Integer)gbs.getTileCompVal(tIdx,c)).intValue();

        // Are we quantizing ints or floats?
        intq = (src.getDataType(tIdx,c) == DataBlk.TYPE_INT);

        // Check that we have an output object
        if (cblk == null) {
            cblk = new CBlkWTDataInt();
        }

        // Cache input float code-block
        infblk = this.infblk;

        // Get data to quantize. When quantizing int data 'cblk' is used to
        // get the data to quantize and to return the quantized data as well,
        // that's why 'getNextCodeBlock()' is used. This can not be done when
        // quantizing float data because of the different data types, that's
        // why 'getNextInternCodeBlock()' is used in that case.
        if (intq) { // Source data is int
            cblk = src.getNextCodeBlock(c,cblk);
            if (cblk == null) {
                return null; // No more code-blocks in current tile for comp.
            }
            // Input and output arrays are the same (for "in place" quant.)
            outarr = (int[])cblk.getData();
        }
        else { // Source data is float
            // Can not use 'cblk' to get float data, use 'infblk'
            infblk = (CBlkWTDataFloat) src.getNextInternCodeBlock(c,infblk);
            if (infblk == null) {
                // Release buffer from infblk: this enables to garbage collect
                // the big buffer when we are done with last code-block of
                // component.
                this.infblk.setData(null);
                return null; // No more code-blocks in current tile for comp.
            }
            this.infblk = infblk; // Save local cache
            infarr = (float[])infblk.getData();
            // Get output data array and check that there is memory to put the
            // quantized coeffs in
            outarr = (int[]) cblk.getData();
            if (outarr == null || outarr.length < infblk.w*infblk.h) {
                outarr = new int[infblk.w*infblk.h];
                cblk.setData(outarr);
            }
            cblk.m = infblk.m;
            cblk.n = infblk.n;
            cblk.sb = infblk.sb;
            cblk.ulx = infblk.ulx;
            cblk.uly = infblk.uly;
            cblk.w = infblk.w;
            cblk.h = infblk.h;
            cblk.wmseScaling = infblk.wmseScaling;
            cblk.offset = 0;
            cblk.scanw = cblk.w;
        }

        // Cache width, height and subband of code-block
        w = cblk.w;
        h = cblk.h;
        sb = cblk.sb;

        if(isReversible(tIdx,c)) { // Reversible only for int data
            cblk.magbits = g-1+src.getNomRangeBits(c)+sb.anGainExp;
            shiftBits = 31-cblk.magbits;

            // Update the convertFactor field
            cblk.convertFactor = (1<<shiftBits);

            // Since we used getNextCodeBlock() to get the int data then
            // 'offset' is 0 and 'scanw' is the width of the code-block The
            // input and output arrays are the same (i.e. "in place")
            for(j=w*h-1; j>=0; j--){
                tmp = (outarr[j]<<shiftBits);
                outarr[j] = ((tmp < 0) ? (1<<31)|(-tmp) : tmp);
            }
        }
        else{ // Non-reversible, use step size
	    float baseStep =
		((Float)qsss.getTileCompVal(tIdx,c)).floatValue();

            // Calculate magnitude bits and quantization step size
            if(isDerived(tIdx,c)){
                cblk.magbits = g-1+sb.level-
                    (int)Math.floor(Math.log(baseStep)/log2);
                stepUDR = baseStep/(1<<sb.level);
            }
            else{
                cblk.magbits = g-1-(int)Math.floor(Math.log(baseStep/
                                        (sb.l2Norm*(1<<sb.anGainExp)))/
                                                   log2);
                stepUDR = baseStep/(sb.l2Norm*(1<<sb.anGainExp));
            }
            shiftBits = 31-cblk.magbits;
            // Calculate step that decoder will get and use that one.
            stepUDR =
                convertFromExpMantissa(convertToExpMantissa(stepUDR));
            invstep = 1.0f/((1L<<(src.getNomRangeBits(c)+sb.anGainExp))*
                            stepUDR);
            // Normalize to magnitude bits (output fractional point)
            invstep *= (1<<(shiftBits-src.getFixedPoint(c)));

            // Update convertFactor and stepSize fields
            cblk.convertFactor = invstep;
            cblk.stepSize = ((1L<<(src.getNomRangeBits(c)+sb.anGainExp))*
                             stepUDR);

            if(intq){ // Quantizing int data
                // Since we used getNextCodeBlock() to get the int data then
                // 'offset' is 0 and 'scanw' is the width of the code-block
                // The input and output arrays are the same (i.e. "in place")
                for (j=w*h-1; j>=0; j--) {
                    tmp = (int)(outarr[j]*invstep);
                    outarr[j] = ((tmp < 0) ? (1<<31)|(-tmp) : tmp);
                }
            }
            else { // Quantizing float data
                for (j=w*h-1, k = infblk.offset+(h-1)*infblk.scanw+w-1,
                         jmin = w*(h-1); j>=0; jmin -= w) {
                    for (; j>=jmin; k--, j--) {
                        tmp = (int)(infarr[k]*invstep);
                        outarr[j] = ((tmp < 0) ? (1<<31)|(-tmp) : tmp);
                    }
                    // Jump to beggining of previous line in input
                    k -= infblk.scanw - w;
                }
            }
        }
        // Return the quantized code-block
        return cblk;
    }

    /**
     * Calculates the parameters of the SubbandAn objects that depend on the
     * Quantizer. The 'stepWMSE' field is calculated for each subband which is
     * a leaf in the tree rooted at 'sb', for the specified component. The
     * subband tree 'sb' must be the one for the component 'n'.
     *
     * @param sb The root of the subband tree.
     *
     * @param c The component index
     *
     * @see SubbandAn#stepWMSE
     * */
    protected void calcSbParams(SubbandAn sb,int c){
	float baseStep;

        if(sb.stepWMSE>0f) // parameters already calculated
            return;
	if(!sb.isNode){
            if(isReversible(tIdx,c)){
                sb.stepWMSE = (float) Math.pow(2,-(src.getNomRangeBits(c)<<1))*
                    sb.l2Norm*sb.l2Norm;
            }
            else{
		baseStep = ((Float)qsss.getTileCompVal(tIdx,c)).floatValue();
                if(isDerived(tIdx,c)){
                    sb.stepWMSE = baseStep*baseStep*
                        (float)Math.pow(2,(sb.anGainExp-sb.level)<<1)*
                        sb.l2Norm*sb.l2Norm;
                }
                else{
                    sb.stepWMSE = baseStep*baseStep;
                }
            }
	}
	else{
	    calcSbParams((SubbandAn)sb.getLL(),c);
	    calcSbParams((SubbandAn)sb.getHL(),c);
	    calcSbParams((SubbandAn)sb.getLH(),c);
	    calcSbParams((SubbandAn)sb.getHH(),c);
            sb.stepWMSE = 1f; // Signal that we already calculated this branch
	}
    }

    /**
     * Converts the floating point value to its exponent-mantissa
     * representation. The mantissa occupies the 11 least significant bits
     * (bits 10-0), and the exponent the previous 5 bits (bits 15-11).
     *
     * @param step The quantization step, normalized to a dynamic range of 1.
     *
     * @return The exponent mantissa representation of the step.
     * */
    public static int convertToExpMantissa(float step) {
        int exp;

        exp = (int)Math.ceil(-Math.log(step)/log2);
        if (exp>QSTEP_MAX_EXPONENT) {
            // If step size is too small for exponent representation, use the
            // minimum, which is exponent QSTEP_MAX_EXPONENT and mantissa 0.
            return (QSTEP_MAX_EXPONENT<<QSTEP_MANTISSA_BITS);
        }
        // NOTE: this formula does not support more than 5 bits for the
        // exponent, otherwise (-1<<exp) might overflow (the - is used to be
        // able to represent 2**31)
        return (exp<<QSTEP_MANTISSA_BITS) |
            ((int)((-step*(-1<<exp)-1f)*(1<<QSTEP_MANTISSA_BITS)+0.5f));
    }

    /**
     * Converts the exponent-mantissa representation to its floating-point
     * value. The mantissa occupies the 11 least significant bits (bits 10-0),
     * and the exponent the previous 5 bits (bits 15-11).
     *
     * @param ems The exponent-mantissa representation of the step.
     *
     * @return The floating point representation of the step, normalized to a
     * dynamic range of 1.
     * */
    private static float convertFromExpMantissa(int ems) {
        // NOTE: this formula does not support more than 5 bits for the
        // exponent, otherwise (-1<<exp) might overflow (the - is used to be
        // able to represent 2**31)
        return (-1f-((float)(ems&QSTEP_MAX_MANTISSA)) /
                ((float)(1<<QSTEP_MANTISSA_BITS))) /
            (float)(-1<<((ems>>QSTEP_MANTISSA_BITS)&QSTEP_MAX_EXPONENT));
    }

    /**
     * Returns the maximum number of magnitude bits in any subband of the
     * current tile.
     *
     * @param c the component number
     *
     * @return The maximum number of magnitude bits in all subbands of the
     * current tile.
     * */
    public int getMaxMagBits(int c){
        Subband sb = getAnSubbandTree(tIdx,c);
        if(isReversible(tIdx,c)){
            return getMaxMagBitsRev(sb,c);
        }
        else{
            if(isDerived(tIdx,c)){
                return getMaxMagBitsDerived(sb,tIdx,c);
            }
            else {
                return getMaxMagBitsExpounded(sb,tIdx,c);
            }
        }
    }


    /**
     * Returns the maximum number of magnitude bits in any subband of the
     * current tile if reversible quantization is used
     *
     * @param sb The root of the subband tree of the current tile
     *
     * @param c the component number
     *
     * @return The highest number of magnitude bit-planes
     * */
    private int getMaxMagBitsRev(Subband sb, int c){
        int tmp,max=0;
        int g = ((Integer)gbs.getTileCompVal(tIdx,c)).intValue();

        if(!sb.isNode)
            return g-1+src.getNomRangeBits(c)+sb.anGainExp;

        max=getMaxMagBitsRev(sb.getLL(),c);
        tmp=getMaxMagBitsRev(sb.getLH(),c);
        if(tmp>max)
            max=tmp;
        tmp=getMaxMagBitsRev(sb.getHL(),c);
        if(tmp>max)
            max=tmp;
        tmp=getMaxMagBitsRev(sb.getHH(),c);
        if(tmp>max)
            max=tmp;

        return max;
    }

    /**
     * Returns the maximum number of magnitude bits in any subband in the
     * given tile-component if derived quantization is used
     *
     * @param sb The root of the subband tree of the tile-component
     *
     * @param t Tile index
     *
     * @param c Component index
     *
     * @return The highest number of magnitude bit-planes
     * */
    private int getMaxMagBitsDerived(Subband sb,int t,int c){
        int tmp,max=0;
        int g = ((Integer)gbs.getTileCompVal(t,c)).intValue();

        if(!sb.isNode){
	    float baseStep = ((Float)qsss.getTileCompVal(t,c)).floatValue();
            return g-1+sb.level-(int)Math.floor(Math.log(baseStep)/log2);
	}

        max=getMaxMagBitsDerived(sb.getLL(),t,c);
        tmp=getMaxMagBitsDerived(sb.getLH(),t,c);
        if(tmp>max)
            max=tmp;
        tmp=getMaxMagBitsDerived(sb.getHL(),t,c);
        if(tmp>max)
            max=tmp;
        tmp=getMaxMagBitsDerived(sb.getHH(),t,c);
        if(tmp>max)
            max=tmp;

        return max;
    }


    /**
     * Returns the maximum number of magnitude bits in any subband in the
     * given tile-component if expounded quantization is used
     *
     * @param sb The root of the subband tree of the tile-component
     *
     * @param t Tile index
     *
     * @param c Component index
     *
     * @return The highest number of magnitude bit-planes
     * */
    private int getMaxMagBitsExpounded(Subband sb,int t,int c){
        int tmp,max=0;
        int g = ((Integer)gbs.getTileCompVal(t,c)).intValue();

        if(!sb.isNode){
	    float baseStep = ((Float)qsss.getTileCompVal(t,c)).floatValue();
            return g-1-
                (int)Math.floor(Math.log(baseStep/
                                (((SubbandAn)sb).l2Norm*(1<<sb.anGainExp)))/
                                log2);
	}

        max=getMaxMagBitsExpounded(sb.getLL(),t,c);
        tmp=getMaxMagBitsExpounded(sb.getLH(),t,c);
        if(tmp>max)
            max=tmp;
        tmp=getMaxMagBitsExpounded(sb.getHL(),t,c);
        if(tmp>max)
            max=tmp;
        tmp=getMaxMagBitsExpounded(sb.getHH(),t,c);
        if(tmp>max)
            max=tmp;

        return max;
    }
}
