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
 *
 * $RCSfile: Subband.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:27 $
 * $State: Exp $
 *
 * Class:                   Subband
 *
 * Description:             Asbtract element for a tree strcuture for
 *                          a description of subbands.
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
package jj2000.j2k.wavelet;

import java.awt.Point;

/**
 * This abstract class represents a subband in a bidirectional tree structure
 * that describes the subband decomposition for a wavelet transform. This
 * class is implemented by the SubbandAn and SubbandSyn classes, which are for
 * the analysis and synthesis sides, respectively.
 *
 * <P>The element can be either a node or a leaf of the tree. If it is a node
 * then ther are 4 descendants (LL, HL, LH and HH). If it is a leaf ther are
 * no descendants.
 *
 * <P>The tree is bidirectional. Each element in the tree structure has a
 * "parent", which is the subband from which the element was obtained by
 * decomposition. The only exception is the root element which has no parent
 * (i.e.it's null), for obvious reasons.
 *
 * @see jj2000.j2k.wavelet.analysis.SubbandAn
 * @see jj2000.j2k.wavelet.synthesis.SubbandSyn
 * */
public abstract class Subband {

    /** The ID for the LL orientation */
    public final static int WT_ORIENT_LL = 0;

    /** The ID for the HL (horizontal high-pass) orientation */
    public final static int WT_ORIENT_HL = 1;

    /** The ID for the LH (vertical high-pass) orientation */
    public final static int WT_ORIENT_LH = 2;

    /** The ID for the HH orientation */
    public final static int WT_ORIENT_HH = 3;

    /**
     * True if it is a node in the tree, false if it is a leaf. False by
     * default.  */
    public boolean isNode;

    /**
     * The orientation of this subband (WT_ORIENT_LL, WT_ORIENT_HL,
     * WT_ORIENT_LH, WT_ORIENT_HH). It is WT_ORIENT_LL by default. The
     * orientation of the top-level node (i.e. the full image before any
     * decomposition) is WT_ORIENT_LL.  */
    // The default value is always 0, which is WT_ORIENT_LL.
    public int orientation;

    /**
     * The level in the tree to which this subband belongs, which is the
     * number of wavelet decompositions performed to produce this subband. It
     * is 0 for the top-level (i.e. root) node. It is 0 by default.
     * */
    public int level;

    /**
     * The resolution level to which this subband contributes. Level 0 is the
     * smallest resolution level (the one with the lowest frequency LL
     * subband). It is 0 by default.
     * */
    public int resLvl;

    /** The number of code-blocks (in both directions) contained in this
     * subband.  */
    public Point numCb = null;

    /**
     * The base 2 exponent of the analysis gain of the subband. The analysis
     * gain of a subband is defined as the gain of the previous subband
     * (i.e. the one from which this one was obtained) multiplied by the line
     * gain and by the column gain. The line (column) gain is the gain of the
     * line (column) filter that was used to obtain it, which is the DC gain
     * for a low-pass filter and the Nyquist gain for a high-pass filter. It
     * is 0 by default.
     *
     * <P>Using the base 2 exponent of the value contrains the possible gains
     * to powers of 2. However this is perfectly compatible to the filter
     * normalization policy assumed here. See the split() method for more
     * details.
     *
     * @see #split
     * */
    public int anGainExp;

    /**
     * The subband index within its resolution level. This value uniquely
     * identifies a subband within a resolution level and a decomposition
     * level within it. Note that only leaf elements represent "real"
     * subbands, while node elements represent only intermediate stages.
     *
     * <P>It is defined recursively. The root node gets a value of 0. For a
     * given node, with a subband index 'b', its LL descendant gets 4*b, its
     * HL descendant 4*b+1, its LH descendant 4*b+2, and its HH descendant
     * 4*b+3, for their subband indexes.
     * */
    public int sbandIdx = 0;

    /**
     * The horizontal coordinate of the upper-left corner of the subband, with
     * respect to the canvas origin, in the component's grid and subband's
     * decomposition level. This is the real horizontal index of the first
     * column of this subband. If even the horizontal decomposition of this
     * subband should be done with the low-pass-first convention. If odd it
     * should be done with the high-pass-first convention.
     * */
    public int ulcx;

    /**
     * The vertical coordinate of the upper-left corner of the subband, with
     * respect to the canvas origin, in the component's grid and subband's
     * decomposition level. This is the real vertical index of the first
     * column of this subband. If even the vertical decomposition of this
     * subband should be done with the low-pass-first convention. If odd it
     * should be done with the high-pass-first convention.
     * */
    public int ulcy;

    /** The horizontal coordinate of the upper-left corner of the subband */
    public int ulx;

    /** The vertical coordinate of the upper-left corner of the subband */
    public int uly;

    /** The width of the subband */
    public int w;

    /** The height of the subband */
    public int h;

    /** The nominal code-block width */
    public int nomCBlkW;

    /** The nominal code-block height */
    public int nomCBlkH;

    /**
     * Returns the parent of this subband. The parent of a subband is the
     * subband from which this one was obtained by decomposition. The root
     * element has no parent subband (null).
     *
     * @return The parent subband, or null for the root one.
     * */
    public abstract Subband getParent();

    /**
     * Returns the LL child subband of this subband.
     *
     * @return The LL child subband, or null if there are no childs.
     * */
    public abstract Subband getLL();

    /**
     * Returns the HL (horizontal high-pass) child subband of this subband.
     *
     * @return The HL child subband, or null if there are no childs.
     * */
    public abstract Subband getHL();

    /**
     * Returns the LH (vertical high-pass) child subband of this subband.
     *
     * @return The LH child subband, or null if there are no childs.
     * */
    public abstract Subband getLH();

    /**
     * Returns the HH child subband of this subband.
     *
     * @return The HH child subband, or null if there are no childs.
     * */
    public abstract Subband getHH();

    /**
     * Splits the current subband in its four subbands. This creates the four
     * childs (LL, HL, LH and HH) and converts the leaf in a node.
     *
     * @param hfilter The horizontal wavelet filter used to decompose this
     * subband.
     *
     * @param vfilter The vertical wavelet filter used to decompose this
     * subband.
     *
     * @return  A reference to the LL leaf (getLL()).
     * */
    protected abstract Subband split(WaveletFilter hfilter,
                                     WaveletFilter vfilter);

    /**
     * Initializes the childs of this node with the correct values. The sizes
     * of the child subbands are calculated by taking into account the
     * position of the subband in the canvas.
     *
     * <P>For the analysis subband gain calculation it is assumed that
     * analysis filters are normalized with a DC gain of 1 and a Nyquist gain
     * of 2.
     * */
    protected void initChilds() {
        Subband subb_LL = getLL();
        Subband subb_HL = getHL();
        Subband subb_LH = getLH();
        Subband subb_HH = getHH();

        // LL subband
        subb_LL.level = level+1;
        subb_LL.ulcx = (ulcx+1)>>1;
        subb_LL.ulcy = (ulcy+1)>>1;
        subb_LL.ulx = ulx;
        subb_LL.uly = uly;
        subb_LL.w = ((ulcx+w+1)>>1)-subb_LL.ulcx;
        subb_LL.h = ((ulcy+h+1)>>1)-subb_LL.ulcy;
        // If this subband in in the all LL path (i.e. it's global orientation
        // is LL) then child LL band contributes to a lower resolution level.
        subb_LL.resLvl = (orientation == WT_ORIENT_LL) ? resLvl-1 : resLvl;
        subb_LL.anGainExp = anGainExp;
        subb_LL.sbandIdx = (sbandIdx<<2);
        // HL subband
        subb_HL.orientation = WT_ORIENT_HL;
        subb_HL.level = subb_LL.level;
        subb_HL.ulcx = ulcx>>1;
        subb_HL.ulcy = subb_LL.ulcy;
        subb_HL.ulx = ulx + subb_LL.w;
        subb_HL.uly = uly;
        subb_HL.w = ((ulcx+w)>>1)-subb_HL.ulcx;
        subb_HL.h = subb_LL.h;
        subb_HL.resLvl = resLvl;
        subb_HL.anGainExp = anGainExp+1;
        subb_HL.sbandIdx = (sbandIdx<<2)+1;
        // LH subband
        subb_LH.orientation = WT_ORIENT_LH;
        subb_LH.level = subb_LL.level;
        subb_LH.ulcx = subb_LL.ulcx;
        subb_LH.ulcy = ulcy>>1;
        subb_LH.ulx = ulx;
        subb_LH.uly = uly + subb_LL.h;
        subb_LH.w = subb_LL.w;
        subb_LH.h = ((ulcy+h)>>1)-subb_LH.ulcy;
        subb_LH.resLvl = resLvl;
        subb_LH.anGainExp = anGainExp+1;
        subb_LH.sbandIdx = (sbandIdx<<2)+2;
        // HH subband
        subb_HH.orientation = WT_ORIENT_HH;
        subb_HH.level = subb_LL.level;
        subb_HH.ulcx = subb_HL.ulcx;
        subb_HH.ulcy = subb_LH.ulcy;
        subb_HH.ulx = subb_HL.ulx;
        subb_HH.uly = subb_LH.uly;
        subb_HH.w = subb_HL.w;
        subb_HH.h = subb_LH.h;
        subb_HH.resLvl = resLvl;
        subb_HH.anGainExp = anGainExp+2;
        subb_HH.sbandIdx = (sbandIdx<<2)+3;
    }

    /**
     * Creates a Subband element with all the default values. The dimensions
     * are (0,0), the upper left corner is (0,0) and the upper-left corner
     * with respect to the canvas is (0,0) too.
     * */
    public Subband() {
    }

    /**
     * Creates the top-level node and the entire subband tree, with the
     * top-level dimensions, the number of decompositions, and the
     * decomposition tree as specified.
     *
     * <P>For the analysis subband gain calculation it is assumed that
     * analysis filters are normalized with a DC gain of 1 and a Nyquist gain
     * of 2.
     *
     * <P>This constructor does not initialize the value of the magBits member
     * variable. This variable is normally initialized by the quantizer, on
     * the encoder side, or the bit stream reader, on the decoder side.
     *
     * @param w The top-level width
     *
     * @param h The top-level height
     *
     * @param ulcx The horizontal coordinate of the upper-left corner with
     * respect to the canvas origin, in the component grid.
     *
     * @param ulcy The vertical coordinate of the upper-left corner with
     * respect to the canvas origin, in the component grid.
     *
     * @param lvls The number of levels (or LL decompositions) in the tree.
     *
     * @param hfilters The horizontal wavelet filters (analysis or synthesis)
     * for each resolution level, starting at resolution level 0. If there are
     * less elements in the array than there are resolution levels, the last
     * element is used for the remaining resolution levels.
     *
     * @param vfilters The vertical wavelet filters (analysis or synthesis)
     * for each resolution level, starting at resolution level 0. If there are
     * less elements in the array than there are resolution levels, the last
     * element is used for the remaining resolution levels.
     *
     * @see WaveletTransform
     * */
    public Subband(int w, int h, int ulcx, int ulcy, int lvls,
                   WaveletFilter hfilters[], WaveletFilter vfilters[]) {
        int i,hi,vi;
        Subband cur; // The current subband

        // Initialize top-level node
        this.w = w;
        this.h = h;
        this.ulcx = ulcx;
        this.ulcy = ulcy;
        this.resLvl = lvls;
        // First create dyadic decomposition.
        cur = this;
        for (i=0; i<lvls; i++) {
            hi = (cur.resLvl <= hfilters.length) ? cur.resLvl-1 :
		hfilters.length-1;
            vi = (cur.resLvl <= vfilters.length) ? cur.resLvl-1 :
		vfilters.length-1;
            cur = cur.split(hfilters[hi],vfilters[vi]);
        }
    }

    /**
     * Returns the next subband in the same resolution level, following the
     * subband index order. If already at the last subband then null is
     * returned. If this subband is not a leaf an IllegalArgumentException is
     * thrown.
     *
     * @return The next subband in the same resolution level, following the
     * subband index order, or null if already at last subband.
     * */
    public Subband nextSubband() {
        Subband sb;

        if (isNode) {
            throw new IllegalArgumentException();
        }

        switch (orientation) {
        case WT_ORIENT_LL:
            sb = getParent();
            if (sb == null || sb.resLvl != resLvl) {
                // Already at top-level or last subband in res. level
                return null;
            }
            else {
                return sb.getHL();
            }
        case WT_ORIENT_HL:
            return getParent().getLH();
        case WT_ORIENT_LH:
            return getParent().getHH();
        case WT_ORIENT_HH:
            // This is the complicated one
            sb = this;
            while (sb.orientation == WT_ORIENT_HH) {
                sb = sb.getParent();
            }
            switch (sb.orientation) {
            case WT_ORIENT_LL:
                sb = sb.getParent();
                if (sb == null || sb.resLvl != resLvl) {
                    // Already at top-level or last subband in res. level
                    return null;
                }
                else {
                    sb = sb.getHL();
                }
                break;
            case WT_ORIENT_HL:
                sb = sb.getParent().getLH();
                break;
            case WT_ORIENT_LH:
                sb = sb.getParent().getHH();
                break;
            default:
                throw new Error("You have found a bug in JJ2000");
            }
            while (sb.isNode) {
                sb = sb.getLL();
            }
            return sb;
        default:
            throw new Error("You have found a bug in JJ2000");
        }
    }

    /**
     * Returns the first leaf subband element in the next higher resolution
     * level.
     *
     * @return The first leaf element in the next higher resolution level, or
     * null if there is no higher resolution level.
     * */
    public Subband getNextResLevel() {
        Subband sb;

        if (level == 0) { // No higher res. level
            return null;
        }
        // Go up until we get to a different resolution level
        sb = this;
        do {
            sb = sb.getParent();
            if (sb == null) { // No higher resolution level
                return null;
            }
        } while (sb.resLvl == resLvl);
        // Now go down to HL, which is in next higher resolution level
        sb = sb.getHL();
        // Now go down LL until get to a leaf
        while (sb.isNode) {
            sb = sb.getLL();
        }
        return sb;
    }

    /**
     * Returns a subband element in the tree, given its resolution level and
     * subband index. This method searches through the tree.
     *
     * @param rl The resolution level.
     *
     * @param sbi The subband index, within the resolution level.
     * */
    public Subband getSubbandByIdx(int rl, int sbi) {
        Subband sb = this;

        // Find the root subband for the resolution level
        if (rl>sb.resLvl || rl<0) {
            throw new IllegalArgumentException("Resolution level index "+
                                               "out of range");
        }

        // Returns directly if it is itself
        if(rl==sb.resLvl && sbi==sb.sbandIdx) return sb;

        if(sb.sbandIdx!=0) sb = sb.getParent();

        while(sb.resLvl>rl) sb = sb.getLL();
        while(sb.resLvl<rl) sb = sb.getParent();

        switch(sbi) {
        case 0:
        default:
            return sb;
        case 1:
            return sb.getHL();
        case 2:
            return sb.getLH();
        case 3:
            return sb.getHH();
        }

    }

    /**
     * Returns a reference to the Subband element to which the specified point
     * belongs. The specified point must be inside this (i.e. the one defined
     * by this object) subband. This method searches through the tree.
     *
     * @param x horizontal coordinate of the specified point.
     *
     * @param y horizontal coordinate of the specified point.
     * */
    public Subband getSubband(int x,int y) {
        Subband cur,hhs;

        // Check that we are inside this subband
        if (x<ulx || y<uly || x>=ulx+w || y>=uly+h) {
            throw new IllegalArgumentException();
        }

        cur = this;
        while (cur.isNode) {
            hhs = cur.getHH();
            // While we are still at a node -> continue
            if (x<hhs.ulx) {
                // Is the result of horizontal low-pass
                if (y<hhs.uly) {
                    // Vertical low-pass
                    cur = cur.getLL();
                } else {
                    // Vertical high-pass
                    cur = cur.getLH();
                }
            } else {
                // Is the result of horizontal high-pass
                if (y<hhs.uly) {
                    // Vertical low-pass
                    cur = cur.getHL();
                } else {
                    // Vertical high-pass
                    cur = cur.getHH();
                }
            }
        }

        return cur;
    }

    /**
     * Returns subband informations in a string.
     *
     * @return Subband informations
     * */
    public String toString() {

        String string =
            "w=" + w + ", h=" + h + ", ulx=" + ulx + ", uly=" + uly +
            ", ulcx= "+ulcx+", ulcy="+ulcy+", idx=" + sbandIdx +
            "\norient=" + orientation + ", node=" + isNode +
            ", level=" + level + ", resLvl=" + resLvl+ ", nomCBlkW=" +
            nomCBlkW + ", nomCBlkH=" + nomCBlkH;

        return string;
    }

    /**
     * This function returns the horizontal wavelet filter relevant to this
     * subband
     *
     * @return The horizontal wavelet filter
     * */
    public abstract WaveletFilter getHorWFilter();

    /**
     * This function returns the vertical wavelet filter relevant to this
     * subband
     *
     * @return The vertical wavelet filter
     * */
    public abstract WaveletFilter getVerWFilter();


}

