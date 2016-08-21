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
 * $RCSfile: LayersInfo.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:08 $
 * $State: Exp $
 *
 * Class:                   LayersInfo
 *
 * Description:             Specification of a layer
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
 *
 *
 *
 */


package jj2000.j2k.entropy.encoder;

/**
 * This class stores the specification of a layer distribution in the
 * bit stream. The specification is made of optimization points and a number of
 * extra layers to add between the optimization points. Each optimization
 * point creates a layer which is optimized by the rate allocator to the
 * specified target bitrate. The extra layers are added by the rate allocator
 * between the optimized layers, with the difference that they are not
 * optimized (i.e. they have no precise target bitrate).
 *
 * <P>The overall target bitrate for the bit stream is always added as the last
 * optimization point without any extra layers after it. If there are some
 * optimization points whose target bitrate is larger than the overall target
 * bitrate, the overall target bitrate will still appear as the last
 * optimization point, even though it does not follow the increasing target
 * bitrate order of the other optimization points. The rate allocator is
 * responsible for eliminating layers that have target bitrates larger than
 * the overall target bitrate.
 *
 * <P>Optimization points can be added with the addOptPoint() method. It takes
 * the target bitrate for the optimized layer and the number of extra layers
 * to add after it.
 *
 * <P>Information about the total number of layers, total number of
 * optimization points, target bitrates, etc. can be obtained with the other
 * methods.
 * */
public class LayersInfo {

    /** The initial size for the arrays: 10 */
    private final static int SZ_INIT = 10;

    /** The size increment for the arrays */
    private final static int SZ_INCR = 5;

    /** The total number of layers */
    // Starts at 1: overall target bitrate is always an extra optimized layer
    int totlyrs = 1;

    /** The overall target bitrate, for the whole bit stream */
    float totbrate;

    /** The number of optimized layers, or optimization points, without
     * counting the extra one coming from the overall target bitrate */
    int nopt;

    /** The target bitrate to which specified layers should be optimized. */
    float optbrate[] = new float[SZ_INIT];

    /** The number of extra layers to be added after an optimized layer. After
     * the layer that is optimized to optbrate[i], extralyrs[i] extra layers
     * should be added. These layers are allocated between the bitrate
     * optbrate[i] and the next optimized bitrate optbrate[i+1] or, if it does
     * not exist, the overall target bitrate. */
    int extralyrs[] = new int[SZ_INIT];

    /**
     * Creates a new LayersInfo object. The overall target bitrate 'brate' is
     * always an extra optimization point, with no extra layers are after
     * it. Note that any optimization points that are added with addOptPoint()
     * are always added before the overall target bitrate.
     *
     * @param brate The overall target bitrate for the bit stream
     *
     *
     * */
    public LayersInfo(float brate) {
        if (brate <= 0) {
            throw new IllegalArgumentException("Overall target bitrate must "+
                                               "be a positive number");
        }
        totbrate = brate;
    }

    /**
     * Returns the overall target bitrate for the entire bit stream.
     *
     * @return The overall target bitrate
     *
     *
     * */
    public float getTotBitrate() {
        return totbrate;
    }

    /**
     * Returns the total number of layers, according to the layer
     * specification of this object and the overall target bitrate.
     *
     * @return The total number of layers, according to the layer spec.
     *
     *
     * */
    public int getTotNumLayers() {
        return totlyrs;
    }

    /**
     * Returns the number of layers to optimize, or optimization points, as
     * specified by this object.
     *
     * @return The number of optimization points
     *
     *
     * */
    public int getNOptPoints() {
        // overall target bitrate is counted as extra
        return nopt+1;
    }

    /**
     * Returns the target bitrate of the optmimization point 'n'.
     *
     * @param n The optimization point index (starts at 0).
     *
     * @return The target bitrate (in bpp) for the optimization point 'n'.
     *
     *
     * */
    public float getTargetBitrate(int n) {
        // overall target bitrate is counted as extra
        return (n < nopt) ? optbrate[n] : totbrate;
    }

    /**
     * Returns the number of extra layers to add after the optimization point
     * 'n', but before optimization point 'n+1'. If there is no optimization
     * point 'n+1' then they should be added before the overall target bitrate.
     *
     * @param n The optimization point index (starts at 0).
     *
     * @return The number of extra (unoptimized) layers to add after the
     * optimization point 'n'
     *
     *
     * */
    public int getExtraLayers(int n) {
        // overall target bitrate is counted as extra
        return (n < nopt) ? extralyrs[n] : 0;
    }

    /**
     * Adds a new optimization point, with target bitrate 'brate' and with
     * 'elyrs' (unoptimized) extra layers after it. The target bitrate 'brate'
     * must be larger than the previous optimization point. The arguments are
     * checked and IllegalArgumentException is thrown if they are not correct.
     *
     * @param brate The target bitrate for the optimized layer.
     *
     * @param elyrs The number of extra (unoptimized) layers to add after the
     * optimized layer.
     *
     *
     * */
    public void addOptPoint(float brate, int elyrs) {
        // Check validity of arguments
        if (brate <= 0) {
            throw new
                IllegalArgumentException("Target bitrate must be positive");
        }
        if (elyrs < 0) {
            throw new IllegalArgumentException("The number of extra layers "+
                                               "must be 0 or more");
        }
        if (nopt > 0 && optbrate[nopt-1]>=brate) {
            throw new
                IllegalArgumentException("New optimization point must have "+
                                         "a target bitrate higher than the "+
                                         "preceding one");
        }
        // Check room for new optimization point
        if (optbrate.length == nopt) { // Need more room
            float tbr[] = optbrate;
            int tel[] = extralyrs;
            // both arrays always have same size
            optbrate = new float[optbrate.length+SZ_INCR];
            extralyrs = new int[extralyrs.length+SZ_INCR];
            System.arraycopy(tbr,0,optbrate,0,nopt);
            System.arraycopy(tel,0,extralyrs,0,nopt);
        }
        // Add new optimization point
        optbrate[nopt] = brate;
        extralyrs[nopt] = elyrs;
        nopt++;
        // Update total number of layers
        totlyrs += 1+elyrs;
    }
}
