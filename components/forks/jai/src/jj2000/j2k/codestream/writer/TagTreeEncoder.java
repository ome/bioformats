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
 * $RCSfile: TagTreeEncoder.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:03 $
 * $State: Exp $
 *
 * Class:                   TagTreeEncoder
 *
 * Description:             Encoder of tag trees
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
 *
 */
package jj2000.j2k.codestream.writer;

import jj2000.j2k.util.*;
import jj2000.j2k.io.*;
import java.io.*;

/**
 * This class implements the tag tree encoder. A tag tree codes a 2D
 * matrix of integer elements in an efficient way. The encoding
 * procedure 'encode()' codes information about a value of the matrix,
 * given a threshold. The procedure encodes the sufficient information
 * to identify whether or not the value is greater than or equal to
 * the threshold.
 *
 * <P>The tag tree saves encoded information to a BitOutputBuffer.
 *
 * <P>A particular and useful property of tag trees is that it is
 * possible to change a value of the matrix, provided both new and old
 * values of the element are both greater than or equal to the largest
 * threshold which has yet been supplied to the coding procedure
 * 'encode()'. This property can be exploited through the 'setValue()'
 * method.
 *
 * <P>This class allows saving the state of the tree at any point and
 * restoring it at a later time, by calling save() and restore().
 *
 * <P>A tag tree can also be reused, or restarted, if one of the
 * reset() methods is called.
 *
 * <P>The TagTreeDecoder class implements the tag tree decoder.
 *
 * <P>Tag trees that have one dimension, or both, as 0 are allowed for
 * convenience. Of course no values can be set or coded in such cases.
 *
 * @see BitOutputBuffer
 *
 * @see jj2000.j2k.codestream.reader.TagTreeDecoder
 * */
public class TagTreeEncoder {

    /** The horizontal dimension of the base level */
    protected int w;

    /** The vertical dimensions of the base level */
    protected int h;

    /** The number of levels in the tag tree */
    protected int lvls;

    /** The tag tree values. The first index is the level, starting at
     * level 0 (leafs). The second index is the element within the
     * level, in lexicographical order. */
    protected int treeV[][];

    /** The tag tree state. The first index is the level, starting at
     * level 0 (leafs). The second index is the element within the
     * level, in lexicographical order. */
    protected int treeS[][];

    /** The saved tag tree values. The first index is the level,
     * starting at level 0 (leafs). The second index is the element
     * within the level, in lexicographical order. */
    protected int treeVbak[][];

    /** The saved tag tree state. The first index is the level, starting at
     * level 0 (leafs). The second index is the element within the
     * level, in lexicographical order. */
    protected int treeSbak[][];

    /** The saved state. If true the values and states of the tree
     * have been saved since the creation or last reset. */
    protected boolean saved;

    /**
     * Creates a tag tree encoder with 'w' elements along the
     * horizontal dimension and 'h' elements along the vertical
     * direction. The total number of elements is thus 'vdim' x
     * 'hdim'.
     *
     * <P>The values of all elements are initialized to Integer.MAX_VALUE.
     *
     * @param h The number of elements along the horizontal direction.
     *
     * @param w The number of elements along the vertical direction.
     *
     *
     * */
    public TagTreeEncoder(int h, int w) {
        int k;
        // Check arguments
        if ( w < 0 || h < 0 ) {
            throw new IllegalArgumentException();
        }
        // Initialize elements
        init(w,h);
        // Set values to max
        for (k = treeV.length-1; k >= 0; k--) {
            ArrayUtil.intArraySet(treeV[k],Integer.MAX_VALUE);
        }
    }

    /**
     * Creates a tag tree encoder with 'w' elements along the
     * horizontal dimension and 'h' elements along the vertical
     * direction. The total number of elements is thus 'vdim' x
     * 'hdim'. The values of the leafs in the tag tree are initialized
     * to the values of the 'val' array.
     *
     * <P>The values in the 'val' array are supposed to appear in
     * lexicographical order, starting at index 0.
     *
     * @param h The number of elements along the horizontal direction.
     *
     * @param w The number of elements along the vertical direction.
     *
     * @param val The values with which initialize the leafs of the
     * tag tree.
     *
     *
     * */
    public TagTreeEncoder(int h, int w, int val[]) {
        int k;
        // Check arguments
        if ( w < 0 || h < 0 || val.length < w*h ) {
            throw new IllegalArgumentException();
        }
        // Initialize elements
        init(w,h);
        // Update leaf values
        for (k=w*h-1; k>=0; k--) {
            treeV[0][k]=val[k];
        }
        // Calculate values at other levels
        recalcTreeV();
    }

    /**
     * Returns the number of leafs along the horizontal direction.
     *
     * @return The number of leafs along the horizontal direction.
     *
     *
     * */
    public final int getWidth() {
        return w;
    }

    /**
     * Returns the number of leafs along the vertical direction.
     *
     * @return The number of leafs along the vertical direction.
     *
     *
     * */
    public final int getHeight() {
        return h;
    }

    /**
     * Initializes the variables of this class, given the dimensions
     * at the base level (leaf level). All the state ('treeS' array)
     * and values ('treeV' array) are intialized to 0. This method is
     * called by the constructors.
     *
     * @param w The number of elements along the vertical direction.
     *
     * @param h The number of elements along the horizontal direction.
     *
     *
     * */
    private void init(int w, int h) {
        int i;
        // Initialize dimensions
        this.w = w;
        this.h = h;
        // Calculate the number of levels
        if (w == 0 || h == 0) {
            lvls = 0;
        }
        else {
            lvls = 1;
            while (h != 1 || w != 1) { // Loop until we reach root
                w = (w+1)>>1;
                h = (h+1)>>1;
                lvls++;
            }
        }
        // Allocate tree values and states
        // (no need to initialize to 0 since it's the default)
        treeV = new int[lvls][];
        treeS = new int[lvls][];
        w = this.w;
        h = this.h;
        for (i=0; i<lvls; i++) {
            treeV[i] = new int[h*w];
            treeS[i] = new int[h*w];
            w = (w+1)>>1;
            h = (h+1)>>1;
        }
    }

    /**
     * Recalculates the values of the elements in the tag tree, in
     * levels 1 and up, based on the values of the leafs (level 0).
     *
     *
     * */
    private void recalcTreeV() {
        int m,n,bi,lw,tm1,tm2,lh,k;
        // Loop on all other levels, updating minimum
        for (k=0; k<lvls-1; k++) {
            // Visit all elements in level
            lw = (w+(1<<k)-1)>>k;
            lh = (h+(1<<k)-1)>>k;
            for (m=((lh>>1)<<1)-2;m>=0;m-=2) { // All quads with 2 lines
                for (n=((lw>>1)<<1)-2;n>=0;n-=2) { // All quads with 2 columns
                    // Take minimum of 4 elements and put it in higher
                    // level
                    bi = m*lw+n;
                    tm1 = (treeV[k][bi] < treeV[k][bi+1]) ?
                        treeV[k][bi] : treeV[k][bi+1];
                    tm2 = (treeV[k][bi+lw] < treeV[k][bi+lw+1]) ?
                        treeV[k][bi+lw] : treeV[k][bi+lw+1];
                    treeV[k+1][(m>>1)*((lw+1)>>1)+(n>>1)] =
                        tm1 < tm2 ? tm1 : tm2;
                }
                // Now we may have quad with 1 column, 2 lines
                if (lw%2 != 0) {
                    n = ((lw>>1)<<1);
                    // Take minimum of 2 elements and put it in higher
                    // level
                    bi = m*lw+n;
                    treeV[k+1][(m>>1)*((lw+1)>>1)+(n>>1)] =
                        (treeV[k][bi] < treeV[k][bi+lw]) ?
                        treeV[k][bi] : treeV[k][bi+lw];
                }
            }
            // Now we may have quads with 1 line, 2 or 1 columns
            if (lh%2 != 0) {
                m = ((lh>>1)<<1);
                for (n=((lw>>1)<<1)-2;n>=0;n-=2) { // All quads with 2 columns
                    // Take minimum of 2 elements and put it in higher
                    // level
                    bi = m*lw+n;
                    treeV[k+1][(m>>1)*((lw+1)>>1)+(n>>1)] =
                        (treeV[k][bi] < treeV[k][bi+1]) ?
                        treeV[k][bi] : treeV[k][bi+1];
                }
                // Now we may have quad with 1 column, 1 line
                if (lw%2 != 0) {
                    // Just copy the value
                    n = ((lw>>1)<<1);
                    treeV[k+1][(m>>1)*((lw+1)>>1)+(n>>1)] =
                        treeV[k][m*lw+n];
                }
            }
        }
    }

    /**
     * Changes the value of a leaf in the tag tree. The new and old
     * values of the element must be not smaller than the largest
     * threshold which has yet been supplied to 'encode()'.
     *
     * @param m The vertical index of the element.
     *
     * @param n The horizontal index of the element.
     *
     * @param v The new value of the element.
     *
     *
     * */
    public void setValue(int m, int n, int v) {
        int k,idx;
        // Check arguments
        if (lvls == 0 || n < 0 || n >= w || v < treeS[lvls-1][0] ||
            treeV[0][m*w+n] < treeS[lvls-1][0]) {
            throw new IllegalArgumentException();
        }
        // Update the leaf value
        treeV[0][m*w+n] = v;
        // Update all parents
        for (k=1; k<lvls; k++) {
            idx = (m>>k)*((w+(1<<k)-1)>>k)+(n>>k);
            if (v < treeV[k][idx]) {
                // We need to update minimum and continue checking
                // in higher levels
                treeV[k][idx] = v;
            }
            else {
                // We are done: v is equal or less to minimum
                // in this level, no other minimums to update.
                break;
            }
        }
    }

    /**
     * Sets the values of the leafs to the new set of values and
     * updates the tag tree accordingly. No leaf can change its value
     * if either the new or old value is smaller than largest
     * threshold which has yet been supplied to 'encode()'. However
     * such a leaf can keep its old value (i.e. new and old value must
     * be identical.
     *
     * <P>This method is more efficient than the setValue() method if
     * a large proportion of the leafs change their value. Note that
     * for leafs which don't have their value defined yet the value
     * should be Integer.MAX_VALUE (which is the default
     * initialization value).
     *
     * @param val The new values for the leafs, in lexicographical order.
     *
     * @see #setValue
     *
     *
     * */
    public void setValues(int val[]) {
        int i,maxt;
        if (lvls == 0) { // Can't set values on empty tree
            throw new IllegalArgumentException();
        }
        // Check the values
        maxt = treeS[lvls-1][0];
        for (i=w*h-1; i>=0; i--) {
            if ((treeV[0][i] < maxt || val[i] < maxt) &&
                treeV[0][i] != val[i]) {
                throw new IllegalArgumentException();
            }
            // Update leaf value
            treeV[0][i] = val[i];
        }
        // Recalculate tree at other levels
        recalcTreeV();
    }

    /**
     * Encodes information for the specified element of the tree,
     * given the threshold and sends it to the 'out' stream. The
     * information that is coded is whether or not the value of the
     * element is greater than or equal to the value of the threshold.
     *
     * @param m The vertical index of the element.
     *
     * @param n The horizontal index of the element.
     *
     * @param t The threshold to use for encoding. It must be non-negative.
     *
     * @param out The stream where to write the coded information.
     *
     *
     * */
    public void encode(int m, int n, int t, BitOutputBuffer out) {
        int k,ts,idx,tmin;

        // Check arguments
        if (m >= h || n >= w || t < 0) {
            throw new IllegalArgumentException();
        }

        // Initialize
        k = lvls-1;
        tmin = treeS[k][0];

        // Loop on levels
        while (true) {
            // Index of element in level 'k'
            idx = (m>>k)*((w+(1<<k)-1)>>k)+(n>>k);
            // Cache state
            ts = treeS[k][idx];
            if (ts < tmin) {
                ts = tmin;
            }
            while (t > ts) {
                if (treeV[k][idx] > ts) {
                    out.writeBit(0); // Send '0' bit
                }
                else if (treeV[k][idx] == ts) {
                    out.writeBit(1); // Send '1' bit
                }
                else { // we are done: set ts and get out of this while
                    ts = t;
                    break;
                }
                // Increment of treeS[k][idx]
                ts++;
            }
            // Update state
            treeS[k][idx] = ts;
            // Update tmin or terminate
            if (k>0) {
                tmin = ts < treeV[k][idx] ? ts : treeV[k][idx];
                k--;
            }
            else {
                // Terminate
                return;
            }
        }
    }

    /**
     * Saves the current values and state of the tree. Calling
     * restore() restores the tag tree the saved state.
     *
     * @see #restore
     *
     *
     * */
    public void save() {
        int k,i;

        if (treeVbak == null) { // Nothing saved yet
            // Allocate saved arrays
            // treeV and treeS have the same dimensions
            treeVbak = new int[lvls][];
            treeSbak = new int[lvls][];
            for (k=lvls-1 ; k >= 0; k--) {
                treeVbak[k] = new int[treeV[k].length];
                treeSbak[k] = new int[treeV[k].length];
            }
        }

        // Copy the arrays
        for (k=treeV.length-1 ; k >= 0; k--) {
            System.arraycopy(treeV[k],0,treeVbak[k],0,treeV[k].length);
            System.arraycopy(treeS[k],0,treeSbak[k],0,treeS[k].length);
        }

        // Set saved state
        saved = true;
    }

    /**
     * Restores the saved values and state of the tree. An
     * IllegalArgumentException is thrown if the tree values and state
     * have not been saved yet.
     *
     * @see #save
     *
     *
     * */
    public void restore() {
        int k,i;

        if (!saved) { // Nothing saved yet
            throw new IllegalArgumentException();
        }

        // Copy the arrays
        for (k=lvls-1 ; k >= 0; k--) {
            System.arraycopy(treeVbak[k],0,treeV[k],0,treeV[k].length);
            System.arraycopy(treeSbak[k],0,treeS[k],0,treeS[k].length);
        }

    }

    /**
     * Resets the tree values and state. All the values are set to
     * Integer.MAX_VALUE and the states to 0.
     *
     *
     * */
    public void reset() {
        int k;
        // Set all values to Integer.MAX_VALUE
        // and states to 0
        for (k = lvls-1; k >= 0; k--) {
            ArrayUtil.intArraySet(treeV[k],Integer.MAX_VALUE);
            ArrayUtil.intArraySet(treeS[k],0);
        }
        // Invalidate saved tree
        saved = false;
    }

    /**
     * Resets the tree values and state. The values are set to the
     * values in 'val'. The states are all set to 0.
     *
     * @param val The new values for the leafs, in lexicographical order.
     *
     *
     * */
    public void reset(int val[]) {
        int k;
        // Set values for leaf level
        for (k=w*h-1; k>=0; k--) {
            treeV[0][k] = val[k];
        }
        // Calculate values at other levels
        recalcTreeV();
        // Set all states to 0
        for (k = lvls-1; k >= 0; k--) {
            ArrayUtil.intArraySet(treeS[k],0);
        }
        // Invalidate saved tree
        saved = false;
    }
}
