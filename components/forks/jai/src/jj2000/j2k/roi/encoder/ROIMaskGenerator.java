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
 * $RCSfile: ROIMaskGenerator.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:22 $
 * $State: Exp $
 *
 * Class:                   ROIMaskGenerator
 *
 * Description:             This class describes generators of ROI masks
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
package jj2000.j2k.roi.encoder;

import jj2000.j2k.image.*;
import jj2000.j2k.quantization.*;
import jj2000.j2k.wavelet.*;
import jj2000.j2k.wavelet.analysis.*;
import jj2000.j2k.codestream.writer.*;
import jj2000.j2k.util.*;
import jj2000.j2k.roi.*;

/**
 * This class generates the ROI masks for the ROIScaler.It gives the scaler
 * the ROI mask for the current code-block.
 *
 * <P>The values are calculated from the scaling factors of the ROIs. The
 * values with which to scale are equal to u-umin where umin is the lowest
 * scaling factor within the block. The umin value is sent to the entropy
 * coder to be used for scaling the distortion values.
 *
 * @see RectROIMaskGenerator
 *
 * @see ArbROIMaskGenerator
 *  */
public abstract class ROIMaskGenerator{

    /** Array containing the ROIs */
    protected ROI[] rois;

    /** Number of components */
    protected int nrc;

    /** Flag indicating whether a mask has been made for the current tile */
    protected boolean tileMaskMade[];

    /* Flag indicating whether there are any ROIs in this tile */
    protected boolean roiInTile;

    /**
     * The constructor of the mask generator
     *
     * @param rois The ROIs in the image
     *
     * @param nrc The number of components
     */
    public ROIMaskGenerator(ROI[] rois, int nrc){
        this.rois=rois;
        this.nrc=nrc;
        tileMaskMade=new boolean[nrc];
    }

    /**
     * This function returns the ROIs in the image
     *
     * @return The ROIs in the image
     */
    public ROI[] getROIs(){
        return rois;
    }

    /**
     * This functions gets a DataBlk with the size of the current code-block
     * and fills it with the ROI mask. The lowest scaling value in the mask
     * for this code-block is returned by the function to be used for
     * modifying the rate distortion estimations.
     *
     * @param db The data block that is to be filled with the mask
     *
     * @param sb The root of the current subband tree
     *
     * @param magbits The number of magnitude bits in this code-block
     *
     * @param c Component number
     *
     * @return Whether or not a mask was needed for this tile */
    public abstract boolean getROIMask(DataBlkInt db, Subband sb,
                                       int magbits, int c);

    /**
     * This function generates the ROI mask for the entire tile. The mask is
     * generated for one component. This method is called once for each tile
     * and component.
     *
     * @param sb The root of the subband tree used in the decomposition
     *
     * @param magbits The max number of magnitude bits in any code-block
     *
     * @param n component number
     */
    public abstract void makeMask(Subband sb, int magbits, int n);

    /**
     * This function is called every time the tile is changed to indicate
     * that there is need to make a new mask
     */
    public void tileChanged(){
        for(int i=0;i<nrc;i++)
            tileMaskMade[i]=false;
    }

}
