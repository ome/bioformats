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
 * $RCSfile: StdEntropyCoderOptions.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:05 $
 * $State: Exp $
 *
 * Class:                   StdEntropyCoderOptions
 *
 * Description:             Entropy coding engine of stripes in
 *                          code-blocks options
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


package jj2000.j2k.entropy;

/**
 * This interface define the constants that identify the possible options for
 * the entropy coder, as well some fixed parameters of the JPEG 2000 entropy
 * coder.
 * */
public interface StdEntropyCoderOptions{

    /** The flag bit to indicate that selective arithmetic coding bypass
     * should be used. In this mode some of the coding passes bypass the
     * arithmetic coder and raw bits are output. If this flag is turned on and
     * the 'OPT_TERM_PASS' one is turned off then the any non-bypass coding
     * pass before a bypass coding pass must use MQ termination. */
    public final static int OPT_BYPASS = 1;

    /** The flag bit to indicate that the MQ states for all contexts should be
     * reset at the end of each non-bypass coding pass. */
    public final static int OPT_RESET_MQ = 1<<1;

    /** The flag bit to indicate that regular termination should be used. When
     * this is specified termination is performed after each coding
     * pass. Termination is applied to both arithmetically coded and bypass
     * (i.e. raw) passes . */
    public final static int OPT_TERM_PASS = 1<<2;

    /** The flag bit to indicate the vertically stripe-causal context
     * formation should be used. */
    public final static int OPT_VERT_STR_CAUSAL = 1<<3;

    /** The flag bit to indicate that error resilience info is embedded on MQ
     * termination. The predictable error-resilient MQ termination at the
     * encoder is necessary in this case. */
    public final static int OPT_PRED_TERM = 1<<4;

    /** The flag bit to indicate that a segmentation marker is to be
     * inserted at the end of each normalization coding pass. The segment
     * marker is the four symbol sequence 1010 that are sent through the MQ
     * coder using the UNIFORM context. */
    public final static int OPT_SEG_SYMBOLS = 1<<5;

    /** The minimum code-block dimension. The nominal width or height of a
     * code-block must never be less than this. It is 4. */
    public static final int MIN_CB_DIM = 4;

    /** The maximum code-block dimension. No code-block should be larger,
     * either in width or height, than this value. It is 1024. */
    public static final int MAX_CB_DIM = 1024;

    /** The maximum code-block area (width x height). The surface covered by
     * a nominal size block should never be larger than this. It is 4096 */
    public static final int MAX_CB_AREA = 4096;

    /** The stripe height. This is the nominal value of the stripe height. It
     * is 4. */
    public static final int STRIPE_HEIGHT = 4;

    /** The number of coding passes per bit-plane. This is the number of
     * passes per bit-plane. It is 3. */
    public static final int NUM_PASSES = 3;

    /** The number of most significant bit-planes where bypass mode is not to
     * be used, even if bypass mode is on: 4. */
    public static final int NUM_NON_BYPASS_MS_BP = 4;

    /** The number of empty passes in the most significant bit-plane. It is
        2. */
    public static final int NUM_EMPTY_PASSES_IN_MS_BP = 2;

    /** The index of the first "raw" pass, if bypass mode is on. */
    public static final int FIRST_BYPASS_PASS_IDX =
        NUM_PASSES*NUM_NON_BYPASS_MS_BP-NUM_EMPTY_PASSES_IN_MS_BP;

}
