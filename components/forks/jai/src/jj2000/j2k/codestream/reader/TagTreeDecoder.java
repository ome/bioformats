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
 * $RCSfile: TagTreeDecoder.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:02 $
 * $State: Exp $
 *
 * Class:                   TagTreeDecoder
 *
 * Description:             Decoder of tag trees
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


package jj2000.j2k.codestream.reader;

import jj2000.j2k.io.*;
import jj2000.j2k.util.*;
import java.io.*;

/**
 * This class implements the tag tree decoder. A tag tree codes a 2D
 * matrix of integer elements in an efficient way. The decoding
 * procedure 'update()' updates a value of the matrix from a stream of
 * coded data, given a threshold. This procedure decodes enough
 * information to identify whether or not the value is greater than
 * or equal to the threshold, and updates the value accordingly.
 *
 * <P>In general the decoding procedure must follow the same sequence
 * of elements and thresholds as the encoding one. The encoder is
 * implemented by the TagTreeEncoder class.
 *
 * <P>Tag trees that have one dimension, or both, as 0 are allowed for
 * convenience. Of course no values can be set or coded in such cases.
 *
 * @see jj2000.j2k.codestream.writer.TagTreeEncoder
 * */
public class TagTreeDecoder {

    /** The horizontal dimension of the base level */
    protected int w;

    /** The vertical dimensions of the base level */
    protected int h;

    /** The number of levels in the tag tree */
    protected int lvls;

    /** The tag tree values. The first index is the level,
     * starting at level 0 (leafs). The second index is the element
     * within the level, in lexicographical order. */
    protected int treeV[][];

    /** The tag tree state. The first index is the level, starting at
     * level 0 (leafs). The second index is the element within the
     * level, in lexicographical order. */
    protected int treeS[][];

    /**
     * Creates a tag tree decoder with 'w' elements along the
     * horizontal dimension and 'h' elements along the vertical
     * direction. The total number of elements is thus 'vdim' x
     * 'hdim'.
     *
     * <P>The values of all elements are initialized to
     * Integer.MAX_VALUE (i.e. no information decoded so far). The
     * states are initialized all to 0.
     *
     * @param h The number of elements along the vertical direction.
     *
     * @param w The number of elements along the horizontal direction.
     *
     *
     * */
    public TagTreeDecoder(int h, int w) {
        int i;

        // Check arguments
        if ( w < 0 || h < 0 ) {
            throw new IllegalArgumentException();
        }
        // Initialize dimensions
        this.w = w;
        this.h = h;
        // Calculate the number of levels
        if (w == 0 || h == 0) {
            lvls = 0; // Empty tree
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
        treeV = new int[lvls][];
        treeS = new int[lvls][];
        w = this.w;
        h = this.h;
        for (i=0; i<lvls; i++) {
            treeV[i] = new int[h*w];
            // Initialize to infinite value
            ArrayUtil.intArraySet(treeV[i],Integer.MAX_VALUE);

            // (no need to initialize to 0 since it's the default)
            treeS[i] = new int[h*w];
            w = (w+1)>>1;
            h = (h+1)>>1;
        }
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
     * Decodes information for the specified element of the tree,
     * given the threshold, and updates its value. The information
     * that can be decoded is whether or not the value of the element
     * is greater than, or equal to, the value of the
     * threshold.
     *
     * @param m The vertical index of the element.
     *
     * @param n The horizontal index of the element.
     *
     * @param t The threshold to use in decoding. It must be non-negative.
     *
     * @param in The stream from where to read the coded information.
     *
     * @return The updated value at position (m,n).
     *
     * @exception IOException If an I/O error occurs while reading
     * from 'in'.
     *
     * @exception EOFException If the ned of the 'in' stream is
     * reached before getting all the necessary data.
     *
     *
     * */
    public int update(int m, int n, int t, PktHeaderBitReader in)
        throws IOException {
        int k,tmin;
        int idx,ts,tv;

        // Check arguments
        if (m >= h || n >= w || t < 0) {
            throw new IllegalArgumentException();
        }

        // Initialize
        k = lvls-1;
        tmin = treeS[k][0];

        // Loop on levels
        idx = (m>>k)*((w+(1<<k)-1)>>k)+(n>>k);
        while (true) {
            // Cache state and value
            ts = treeS[k][idx];
            tv = treeV[k][idx];
            if (ts < tmin) {
                ts = tmin;
            }
            while (t > ts) {
                if (tv >= ts) { // We are not done yet
                    if (in.readBit() == 0) { // '0' bit
                        // We know that 'value' > treeS[k][idx]
                        ts++;
                    }
                    else { // '1' bit
                        // We know that 'value' = treeS[k][idx]
                        tv = ts++;
                    }
                    // Increment of treeS[k][idx] done above
                }
                else { // We are done, we can set ts and get out
                    ts = t;
                    break; // get out of this while
                }
            }
            // Update state and value
            treeS[k][idx] = ts;
            treeV[k][idx] = tv;
            // Update tmin or terminate
            if (k>0) {
                tmin = ts < tv ? ts : tv;
                k--;
                // Index of element for next iteration
                idx = (m>>k)*((w+(1<<k)-1)>>k)+(n>>k);
            }
            else {
                // Return the updated value
                return tv;
            }
        }
    }

    /**
     * Returns the current value of the specified element in the tag
     * tree. This is the value as last updated by the update() method.
     *
     * @param m The vertical index of the element.
     *
     * @param n The horizontal index of the element.
     *
     * @return The current value of the element.
     *
     * @see #update
     *
     *
     * */
    public int getValue(int m, int n) {
        // Check arguments
        if (m >= h || n >= w) {
            throw new IllegalArgumentException();
        }
        // Return value
        return treeV[0][m*w+n];
    }
}
