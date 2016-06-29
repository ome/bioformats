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
 * $RCSfile: ForwCompTransf.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:13 $
 * $State: Exp $
 *
 * Class:               ForwCompTransf
 *
 * Description:         Component transformations applied to tiles
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
package jj2000.j2k.image.forwcomptransf;

import jj2000.j2k.wavelet.analysis.*;
import jj2000.j2k.wavelet.*;
import jj2000.j2k.image.*;
import jj2000.j2k.util.*;
import jj2000.j2k.*;

import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriteParamJava;
/**
 * This class apply component transformations to the tiles depending
 * on user specifications. These transformations can be used to
 * improve compression efficiency but are not related to colour
 * transforms used to map colour values for display purposes. JPEG
 * 2000 part I defines 2 component transformations: RCT (Reversible
 * Component Transformation) and ICT (Irreversible Component
 * Transformation).
 *
 * @see ModuleSpec
 * */
public class ForwCompTransf extends ImgDataAdapter
    implements BlkImgDataSrc {

    /** Identifier for no component transformation. Value is 0. */
    public static final int NONE = 0;

    /** Identifier for the Forward Reversible Component Transformation
        (FORW_RCT). Value is 1. */
    public static final int FORW_RCT = 1;

    /** Identifier for the Forward Irreversible Component
        Transformation (FORW_ICT). Value is 2 */
    public static final int FORW_ICT = 2;

    /** The source of image data */
    private BlkImgDataSrc src;

    /** The component transformations specifications */
    private CompTransfSpec cts;

    /** The wavelet filter specifications */
    private AnWTFilterSpec wfs;

    /** The type of the current component transformation JPEG 2000
     * part I only support NONE, FORW_RCT and FORW_ICT types*/
    private int transfType = NONE;

    /** The bit-depths of transformed components */
    private int tdepth[];

    /** Output block used instead of the one provided as an argument
        if the later is DataBlkFloat.*/
    private DataBlk outBlk;

    /** Block used to request component with index 0 */
    private DataBlkInt block0;

    /** Block used to request component with index 1*/
    private DataBlkInt block1;

    /** Block used to request component with index 2*/
    private DataBlkInt block2;

     /**
     * Constructs a new ForwCompTransf object that operates on the
     * specified source of image data.
     *
     * @param imgSrc The source from where to get the data to be
     * transformed
     *
     * @param wp The encoder specifications
     *
     * @see BlkImgDataSrc
     * */
    public ForwCompTransf(BlkImgDataSrc imgSrc, J2KImageWriteParamJava wp) {
        super(imgSrc);
	this.cts = wp.getComponentTransformation();
        this.wfs = wp.getFilters();
        src = imgSrc;
    }

    /** The prefix for component transformation type: 'M' */
    public final static char OPT_PREFIX = 'M';

    /** The list of parameters that is accepted by the forward
     * component transformation module. Options start with an 'M'. */
    private final static String [][] pinfo = {
	{ "Mct", "[<tile index>] [true|false] ...",
	  "Specifies to use component transformation with some tiles. "+
	  " If the wavelet transform is reversible (w5x3 filter), the "+
          "Reversible Component Transformation (RCT) is applied. If not "+
          "(w9x7 filter), the Irreversible Component Transformation (ICT)"+
          " is used.", null},
    };

    /**
     * Returns the position of the fixed point in the specified
     * component. This is the position of the least significant integral
     * (i.e. non-fractional) bit, which is equivalent to the number of
     * fractional bits. For instance, for fixed-point values with 2 fractional
     * bits, 2 is returned. For floating-point data this value does not apply
     * and 0 should be returned. Position 0 is the position of the least
     * significant bit in the data.
     *
     * <P>This default implementation assumes that the number of
     * fractional bits is not modified by the component mixer.
     *
     * @param c The index of the component.
     *
     * @return The value of the fixed point position of the source
     * since the color transform does not affect it.
     * */
     public int getFixedPoint(int c){
         return src.getFixedPoint(c);
     }

    /**
     * Returns the parameters that are used in this class and implementing
     * classes. It returns a 2D String array. Each of the 1D arrays is for a
     * different option, and they have 4 elements. The first element is the
     * option name, the second one is the synopsis, the third one is a long
     * description of what the parameter is and the fourth is its default
     * value. The synopsis or description may be 'null', in which case it is
     * assumed that there is no synopsis or description of the option,
     * respectively. Null may be returned if no options are supported.
     *
     * @return the options name, their synopsis and their explanation,
     * or null if no options are supported.
     * */
    public static String[][] getParameterInfo(){
        return pinfo;
    }

    /**
     * Calculates the bitdepths of the transformed components, given the
     * bitdepth of the un-transformed components and the component
     * tranformation type.
     *
     * @param ntdepth The bitdepth of each non-transformed components.
     *
     * @param ttype The type ID of the component transformation.
     *
     * @param tdepth If not null the results are stored in this
     * array, otherwise a new array is allocated and returned.
     *
     * @return The bitdepth of each transformed component.
     * */
    public static
        int[] calcMixedBitDepths(int ntdepth[], int ttype, int tdepth[]) {

        if (ntdepth.length < 3 && ttype != NONE) {
            throw new IllegalArgumentException();
        }

        if (tdepth == null) {
            tdepth = new int[ntdepth.length];
        }

        switch (ttype) {
        case NONE:
            System.arraycopy(ntdepth,0,tdepth,0,ntdepth.length);
            break;
        case FORW_RCT:
            if (ntdepth.length >3) {
                System.arraycopy(ntdepth,3,tdepth,3,ntdepth.length-3);
            }
            // The formulas are:
            // tdepth[0] = ceil(log2(2^(ntdepth[0])+2^ntdepth[1]+
            //                        2^(ntdepth[2])))-2+1
            // tdepth[1] = ceil(log2(2^(ntdepth[1])+2^(ntdepth[2])-1))+1
            // tdepth[2] = ceil(log2(2^(ntdepth[0])+2^(ntdepth[1])-1))+1
            // The MathUtil.log2(x) function calculates floor(log2(x)), so we
            // use 'MathUtil.log2(2*x-1)+1', which calculates ceil(log2(x))
            // for any x>=1, x integer.
            tdepth[0] = MathUtil.log2((1<<ntdepth[0])+(2<<ntdepth[1])+
                                      (1<<ntdepth[2])-1)-2+1;
            tdepth[1] = MathUtil.log2((1<<ntdepth[2])+(1<<ntdepth[1])-1)+1;
            tdepth[2] = MathUtil.log2((1<<ntdepth[0])+(1<<ntdepth[1])-1)+1;
            break;
        case FORW_ICT:
            if (ntdepth.length >3) {
                System.arraycopy(ntdepth,3,tdepth,3,ntdepth.length-3);
            }
            // The MathUtil.log2(x) function calculates floor(log2(x)), so we
            // use 'MathUtil.log2(2*x-1)+1', which calculates ceil(log2(x))
            // for any x>=1, x integer.
            tdepth[0] =
                MathUtil.log2((int)Math.floor((1<<ntdepth[0])*0.299072+
                                              (1<<ntdepth[1])*0.586914+
                                              (1<<ntdepth[2])*0.114014)-1)+1;
            tdepth[1] =
                MathUtil.log2((int)Math.floor((1<<ntdepth[0])*0.168701+
                                              (1<<ntdepth[1])*0.331299+
                                              (1<<ntdepth[2])*0.5)-1)+1;
            tdepth[2] =
                MathUtil.log2((int)Math.floor((1<<ntdepth[0])*0.5+
                                              (1<<ntdepth[1])*0.418701+
                                              (1<<ntdepth[2])*0.081299)-1)+1;
            break;
        }

        return tdepth;
    }

    /**
     * Initialize some variables used with RCT. It must be called, at least,
     * at the beginning of each new tile.
     * */
    private void initForwRCT(){
        int i;
        int tIdx = getTileIdx();

        if (src.getNumComps() < 3) {
            throw new IllegalArgumentException();
        }
        // Check that the 3 components have the same dimensions
        if (src.getTileCompWidth(tIdx, 0) != src.getTileCompWidth(tIdx, 1) ||
            src.getTileCompWidth(tIdx, 0) != src.getTileCompWidth(tIdx, 2) ||
            src.getTileCompHeight(tIdx, 0) != src.getTileCompHeight(tIdx, 1) ||
            src.getTileCompHeight(tIdx, 0) != src.getTileCompHeight(tIdx, 2)) {
            throw new IllegalArgumentException("Can not use RCT "+
                                               "on components with different "+
                                               "dimensions");
        }
        // Initialize bitdepths
        int utd[]; // Premix bitdepths
        utd = new int[src.getNumComps()];
        for (i=utd.length-1; i>=0; i--) {
            utd[i] = src.getNomRangeBits(i);
        }
        tdepth = calcMixedBitDepths(utd,FORW_RCT,null);
    }

    /**
     * Initialize some variables used with ICT. It must be called, at least,
     * at the beginning of a new tile.
     * */
    private void initForwICT(){
        int i;
	int tIdx = getTileIdx();

        if (src.getNumComps() < 3) {
            throw new IllegalArgumentException();
        }
        // Check that the 3 components have the same dimensions
        if (src.getTileCompWidth(tIdx, 0) != src.getTileCompWidth(tIdx, 1) ||
            src.getTileCompWidth(tIdx, 0) != src.getTileCompWidth(tIdx, 2) ||
            src.getTileCompHeight(tIdx, 0) != src.getTileCompHeight(tIdx, 1) ||
            src.getTileCompHeight(tIdx, 0) != src.getTileCompHeight(tIdx, 2)) {
            throw new IllegalArgumentException("Can not use ICT "+
                                               "on components with different "+
                                               "dimensions");
        }
        // Initialize bitdepths
        int utd[]; // Premix bitdepths
        utd = new int[src.getNumComps()];
        for (i=utd.length-1; i>=0; i--) {
            utd[i] = src.getNomRangeBits(i);
        }
        tdepth = calcMixedBitDepths(utd,FORW_ICT,null);
    }

    /**
     * Returns a string with a descriptive text of which forward component
     * transformation is used. This can be either "Forward RCT" or "Forward
     * ICT" or "No component transformation" depending on the current tile.
     *
     * @return A descriptive string
     * */
    public String toString() {
        switch(transfType){
        case FORW_RCT:
	    return "Forward RCT";
        case FORW_ICT:
	    return "Forward ICT";
        case NONE:
	    return "No component transformation";
        default:
            throw new IllegalArgumentException("Non JPEG 2000 part I"+
                                               " component transformation");
        }
    }

    /**
     * Returns the number of bits, referred to as the "range bits",
     * corresponding to the nominal range of the data in the specified
     * component and in the current tile. If this number is <i>b</i> then for
     * unsigned data the nominal range is between 0 and 2^b-1, and for signed
     * data it is between -2^(b-1) and 2^(b-1)-1. Note that this value can be
     * affected by the multiple component transform.
     *
     * @param c The index of the component.
     *
     * @return The bitdepth of component 'c' after mixing.
     * */
    public int getNomRangeBits(int c) {
        switch(transfType){
        case FORW_RCT:
        case FORW_ICT:
            return tdepth[c];
        case NONE:
            return src.getNomRangeBits(c);
        default:
            throw new IllegalArgumentException("Non JPEG 2000 part I"+
                                               " component transformation");
        }
    }

    /**
     * Returns true if this transform is reversible in current
     * tile. Reversible component transformations are those which operation
     * can be completely reversed without any loss of information (not even
     * due to rounding).
     *
     * @return Reversibility of component transformation in current tile
     * */
    public boolean isReversible(){
        switch(transfType){
        case NONE:
        case FORW_RCT:
            return true;
        case FORW_ICT:
            return false;
        default:
            throw new IllegalArgumentException("Non JPEG 2000 part I"+
                                               " component transformation");
        }
    }

    /**
     * Apply forward component transformation associated with the current
     * tile. If no component transformation has been requested by the user,
     * data are not modified.
     *
     * <P>This method calls the getInternCompData() method, but respects the
     * definitions of the getCompData() method defined in the BlkImgDataSrc
     * interface.
     *
     * @param blk Determines the rectangular area to return, and the
     * data is returned in this object.
     *
     * @param c Index of the output component.
     *
     * @return The requested DataBlk
     *
     * @see BlkImgDataSrc#getCompData
     * */
    public DataBlk getCompData(DataBlk blk, int c){
        // If requesting a component whose index is greater than 3 or there is
        // no transform return a copy of data (getInternCompData returns the
        // actual data in those cases)
        if (c>=3 || transfType == NONE) {
            return src.getCompData(blk,c);
        }
        else { // We can use getInternCompData (since data is a copy anyways)
            return getInternCompData(blk,c);
        }
    }

    /**
     * Apply the component transformation associated with the current tile. If
     * no component transformation has been requested by the user, data are
     * not modified. Else, appropriate method is called (forwRCT or forwICT).
     *
     * @see #forwRCT
     *
     * @see #forwICT
     *
     * @param blk Determines the rectangular area to return.
     *
     * @param c Index of the output component.
     *
     * @return The requested DataBlk
     * */
    public DataBlk getInternCompData(DataBlk blk, int c){
        switch(transfType){
        case NONE:
 	    return src.getInternCompData(blk,c);
        case FORW_RCT:
	    return forwRCT(blk,c);
        case FORW_ICT:
	    return forwICT(blk,c);
        default:
            throw new IllegalArgumentException("Non JPEG 2000 part I component"+
                                               " transformation for tile: "+
                                               tIdx);
        }
    }

    /**
     * Apply forward component transformation to obtain requested component
     * from specified block of data. Whatever the type of requested DataBlk,
     * it always returns a DataBlkInt.
     *
     * @param blk Determine the rectangular area to return
     *
     * @param c The index of the requested component
     *
     * @return Data of requested component
     * */
    private DataBlk forwRCT(DataBlk blk,int c){
        int k,k0,k1,k2,mink,i;
        int w = blk.w; //width of output block
        int h = blk.h; //height of ouput block
        int  outdata[]; //array of output data

        //If asking for Yr, Ur or Vr do transform
        if(c >= 0 && c <= 2) {
	    // Check that request data type is int
	    if(blk.getDataType()!=DataBlk.TYPE_INT){
		if(outBlk==null || outBlk.getDataType() != DataBlk.TYPE_INT){
		    outBlk = new DataBlkInt();
		}
		outBlk.w = w;
		outBlk.h = h;
		outBlk.ulx = blk.ulx;
		outBlk.uly = blk.uly;
		blk = outBlk;
	    }

            //Reference to output block data array
            outdata = (int[]) blk.getData();

            //Create data array of blk if necessary
            if(outdata == null || outdata.length<h*w) {
                outdata = new int[h*w];
                blk.setData(outdata);
            }

            // Block buffers for input RGB data
            int data0[],data1[],bdata[]; // input data arrays

	    if(block0==null)
		block0 = new DataBlkInt();
	    if(block1==null)
		block1 = new DataBlkInt();
	    if(block2==null)
		block2 = new DataBlkInt();
	    block0.w = block1.w = block2.w = blk.w;
	    block0.h = block1.h = block2.h = blk.h;
	    block0.ulx = block1.ulx = block2.ulx = blk.ulx;
	    block0.uly = block1.uly = block2.uly = blk.uly;

            //Fill in buffer blocks (to be read only)
            // Returned blocks may have different size and position
            block0 = (DataBlkInt)src.getInternCompData(block0, 0);
            data0 = (int[]) block0.getData();
            block1 = (DataBlkInt)src.getInternCompData(block1, 1);
            data1 = (int[]) block1.getData();
            block2 = (DataBlkInt)src.getInternCompData(block2, 2);
            bdata = (int[]) block2.getData();

            // Set the progressiveness of the output data
            blk.progressive = block0.progressive || block1.progressive ||
                block2.progressive;
	    blk.offset = 0;
            blk.scanw = w;

            //Perform conversion

            // Initialize general indexes
            k = w*h-1;
            k0 = block0.offset+(h-1)*block0.scanw+w-1;
            k1 = block1.offset+(h-1)*block1.scanw+w-1;
            k2 = block2.offset+(h-1)*block2.scanw+w-1;

            switch(c) {
            case 0: //RGB to Yr conversion
                for (i = h-1; i >= 0; i--) {
                    for (mink = k-w; k > mink; k--, k0--, k1--, k2--) {
                        // Use int arithmetic with 12 fractional bits
                        // and rounding
                        outdata[k] =
                            ( data0[k] + 2 * data1[k] + bdata[k]
                              ) >> 2; // Same as / 4
                    }
                    // Jump to beggining of previous line in input
                    k0 -= block0.scanw - w;
                    k1 -= block1.scanw - w;
                    k2 -= block2.scanw - w;
                }
                break;

	    case 1: //RGB to Ur conversion
                for (i = h-1; i >= 0; i--) {
                    for (mink = k-w; k > mink; k--, k1--, k2--) {
                        // Use int arithmetic with 12 fractional bits
                        // and rounding
                        outdata[k] = bdata[k2] - data1[k1];
                    }
                    // Jump to beggining of previous line in input
                    k1 -= block1.scanw - w;
                    k2 -= block2.scanw - w;
                }
                break;

	    case 2:  //RGB to Vr conversion
                for (i = h-1; i >= 0; i--) {
                    for (mink = k-w; k > mink; k--, k0--, k1--) {
                        // Use int arithmetic with 12 fractional bits
                        // and rounding
                        outdata[k] = data0[k0] - data1[k1];
                    }
                    // Jump to beggining of previous line in input
                    k0 -= block0.scanw - w;
                    k1 -= block1.scanw - w;
                }
                break;

            }
        }
        else if (c >= 3) {
            // Requesting a component which is not Y, Ur or Vr =>
            // just pass the data
            return src.getInternCompData(blk,c);
        }
        else {
            // Requesting a non valid component index
            throw new IllegalArgumentException();
        }
	return blk;

    }

    /**
     * Apply forward irreversible component transformation to obtain requested
     * component from specified block of data. Whatever the type of requested
     * DataBlk, it always returns a DataBlkFloat.
     *
     * @param blk Determine the rectangular area to return
     *
     * @param c The index of the requested component
     *
     * @return Data of requested component
     * */
    private DataBlk forwICT(DataBlk blk,int c){
        int k,k0,k1,k2,mink,i;
        int w = blk.w; //width of output block
        int h = blk.h; //height of ouput block
        float  outdata[]; //array of output data

	if(blk.getDataType()!=DataBlk.TYPE_FLOAT){
	    if(outBlk==null || outBlk.getDataType() != DataBlk.TYPE_FLOAT){
		outBlk = new DataBlkFloat();
	    }
	    outBlk.w = w;
	    outBlk.h = h;
	    outBlk.ulx = blk.ulx;
	    outBlk.uly = blk.uly;
	    blk = outBlk;
	}

	//Reference to output block data array
	outdata = (float[]) blk.getData();

	//Create data array of blk if necessary
	if(outdata == null || outdata.length<w*h) {
	    outdata = new float[h * w];
	    blk.setData(outdata);
	}

        //If asking for Y, Cb or Cr do transform
        if(c>=0 && c<=2) {

            int data0[],data1[],data2[]; // input data arrays

	    if(block0==null)
		block0 = new DataBlkInt();
	    if(block1==null)
		block1 = new DataBlkInt();
	    if(block2==null)
		block2 = new DataBlkInt();
	    block0.w = block1.w = block2.w = blk.w;
	    block0.h = block1.h = block2.h = blk.h;
	    block0.ulx = block1.ulx = block2.ulx = blk.ulx;
	    block0.uly = block1.uly = block2.uly = blk.uly;

            // Returned blocks may have different size and position
            block0 = (DataBlkInt)src.getInternCompData(block0, 0);
            data0 = (int[]) block0.getData();
            block1 = (DataBlkInt)src.getInternCompData(block1, 1);
            data1 = (int[]) block1.getData();
            block2 = (DataBlkInt)src.getInternCompData(block2, 2);
            data2 = (int[]) block2.getData();

            // Set the progressiveness of the output data
            blk.progressive = block0.progressive || block1.progressive ||
                block2.progressive;
	    blk.offset = 0;
            blk.scanw = w;

            //Perform conversion

            // Initialize general indexes
            k = w*h-1;
            k0 = block0.offset+(h-1)*block0.scanw+w-1;
            k1 = block1.offset+(h-1)*block1.scanw+w-1;
            k2 = block2.offset+(h-1)*block2.scanw+w-1;

            switch(c) {
            case 0:
            //RGB to Y conversion
                for (i = h-1; i >= 0; i--) {
                    for (mink = k-w; k > mink; k--, k0--, k1--, k2--) {
                        outdata[k] =
                            0.299f * data0[k0]
                            + 0.587f * data1[k1]
                            + 0.114f * data2[k2];
		    }
                    // Jump to beggining of previous line in input
                    k0 -= block0.scanw - w;
                    k1 -= block1.scanw - w;
                    k2 -= block2.scanw - w;
		}
                break;

            case 1:
            //RGB to Cb conversion
                for (i = h-1; i >= 0; i--) {
                    for (mink = k-w; k > mink; k--, k0--, k1--, k2--) {
			outdata[k] =
			    - 0.16875f * data0[k0]
			    - 0.33126f * data1[k1]
			    + 0.5f * data2[k2];
		    }
                    // Jump to beggining of previous line in input
                    k0 -= block0.scanw - w;
                    k1 -= block1.scanw - w;
                    k2 -= block2.scanw - w;
		}
                break;

            case 2:
            //RGB to Cr conversion
                for (i = h-1; i >= 0; i--) {
                    for (mink = k-w; k > mink; k--, k0--, k1--, k2--) {
			outdata[k] =
			    0.5f * data0[k0]
			    - 0.41869f * data1[k1]
			    - 0.08131f * data2[k2];
		    }
                    // Jump to beggining of previous line in input
                    k0 -= block0.scanw - w;
                    k1 -= block1.scanw - w;
                    k2 -= block2.scanw - w;
		}
                break;
            }
        }
        else if(c>=3) {
            // Requesting a component which is not Y, Cb or Cr =>
            // just pass the data

            // Variables
            DataBlkInt indb = new DataBlkInt(blk.ulx,blk.uly,w,h);
            int indata[]; // input data array

            // Get the input data
            // (returned block may be larger than requested one)
            src.getInternCompData(indb,c);
            indata = (int[]) indb.getData();

            // Copy the data converting from int to float
            k = w*h-1;
            k0 = indb.offset+(h-1)*indb.scanw+w-1;
            for (i=h-1; i>=0; i--) {
                for (mink = k-w; k > mink; k--, k0--) {
                    outdata[k] = (float) indata[k0];
                }
                // Jump to beggining of next line in input
                k0 += indb.w - w;
            }

            // Set the progressivity
            blk.progressive = indb.progressive;
            blk.offset = 0;
            blk.scanw = w;
	    return blk;
        }
        else {
            // Requesting a non valid component index
            throw new IllegalArgumentException();
        }
	return blk;

    }

    /**
     * Changes the current tile, given the new indexes. An
     * IllegalArgumentException is thrown if the indexes do not correspond to
     * a valid tile.
     *
     * <P>This default implementation changes the tile in the source and
     * re-initializes properly component transformation variables..
     *
     * @param x The horizontal index of the tile.
     *
     * @param y The vertical index of the new tile.
     * */
    public void setTile(int x, int y) {
        src.setTile(x,y);
	tIdx = getTileIdx(); // index of the current tile

        // initializations
        String str = (String)cts.getTileDef(tIdx);
        if(str.equals("none")){
            transfType = NONE;
        }
        else if(str.equals("rct")){
            transfType = FORW_RCT;
            initForwRCT();
        }
        else if(str.equals("ict")){
            transfType = FORW_ICT;
            initForwICT();
        }
        else{
            throw new IllegalArgumentException("Component transformation"+
                                               " not recognized");
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
        String str = (String)cts.getTileDef(tIdx);
        if(str.equals("none")){
            transfType = NONE;
        }
        else if(str.equals("rct")){
            transfType = FORW_RCT;
            initForwRCT();
        }
        else if(str.equals("ict")){
            transfType = FORW_ICT;
            initForwICT();
        }
        else{
            throw new IllegalArgumentException("Component transformation"+
                                               " not recognized");
        }
    }

}
