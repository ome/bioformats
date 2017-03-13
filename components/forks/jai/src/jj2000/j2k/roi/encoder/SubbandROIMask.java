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
 * $RCSfile: SubbandROIMask.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:24 $
 * $State: Exp $
 *
 * Class:                   ROI
 *
 * Description:             This class describes the ROI mask for a subband
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
package jj2000.j2k.roi.encoder;

import jj2000.j2k.codestream.writer.*;
import jj2000.j2k.wavelet.analysis.*;
import jj2000.j2k.quantization.*;
import jj2000.j2k.wavelet.*;
import jj2000.j2k.image.*;
import jj2000.j2k.util.*;
import jj2000.j2k.roi.*;

/**
 * This abstract class describes the ROI mask for a single subband. Each
 * object of the class contains the mask for a particular subband and also has
 * references to the masks of the children subbands of the subband
 * corresponding to this mask.  */
public abstract class SubbandROIMask{

    /** The subband masks of the child LL */
    protected SubbandROIMask ll;

    /** The subband masks of the child LH */
    protected SubbandROIMask lh;

    /** The subband masks of the child HL */
    protected SubbandROIMask hl;

    /** The subband masks of the child HH */
    protected SubbandROIMask hh;

    /** Flag indicating whether this subband mask is a node or not */
    protected boolean isNode;

    /** Horizontal uper-left coordinate of the subband mask */
    public int ulx;

    /** Vertical uper-left coordinate of the subband mask */
    public int uly;

    /** Width of the subband mask */
    public int w;

    /** Height of the subband mask */
    public int h;

    /**
     * The constructor of the SubbandROIMask takes the dimensions of the
     * subband as parameters
     *
     * @param ulx The upper left x coordinate of corresponding subband
     *
     * @param uly The upper left y coordinate of corresponding subband
     *
     * @param w The width of corresponding subband
     *
     * @param h The height of corresponding subband
     * */
    public SubbandROIMask(int ulx, int uly, int w, int h){
        this.ulx=ulx;
        this.uly=uly;
        this.w=w;
        this.h=h;
    }

    /**
     * Returns a reference to the Subband mask element to which the specified
     * point belongs. The specified point must be inside this (i.e. the one
     * defined by this object) subband mask. This method searches through the
     * tree.
     *
     * @param x horizontal coordinate of the specified point.
     *
     * @param y horizontal coordinate of the specified point.
     * */
    public SubbandROIMask getSubbandRectROIMask(int x, int y) {
        SubbandROIMask cur,hhs;

        // Check that we are inside this subband
        if (x < ulx || y < uly || x >= ulx+w || y >= uly+h) {
            throw new IllegalArgumentException();
        }

        cur = this;
        while (cur.isNode) {
            hhs = cur.hh;
            // While we are still at a node -> continue
            if (x < hhs.ulx) {
                // Is the result of horizontal low-pass
                if (y < hhs.uly) {
                    // Vertical low-pass
                    cur = cur.ll;
                }
                else {
                    // Vertical high-pass
                    cur = cur.lh;
                }
            }
            else {
                // Is the result of horizontal high-pass
                if (y < hhs.uly) {
                    // Vertical low-pass
                    cur = cur.hl;
                }
                else {
                    // Vertical high-pass
                    cur = cur.hh;
                }
            }
        }
        return cur;
    }
}











