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
 * $RCSfile: StdEntropyCoder.java,v $
 * $Revision: 1.3 $
 * $Date: 2005/09/26 22:08:13 $
 * $State: Exp $
 *
 * Class:                   StdEntropyCoder
 *
 * Description:             Entropy coding engine of stripes in code-blocks
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
package jj2000.j2k.entropy.encoder;
import java.awt.Point;

import jj2000.j2k.quantization.quantizer.*;
import jj2000.j2k.wavelet.analysis.*;
import jj2000.j2k.codestream.*;
import jj2000.j2k.wavelet.*;
import jj2000.j2k.entropy.*;
import jj2000.j2k.image.*;
import jj2000.j2k.util.*;
import jj2000.j2k.io.*;
import jj2000.j2k.*;

import java.util.*;
import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriteParamJava;

/**
 * This class implements the JPEG 2000 entropy coder, which codes stripes in
 * code-blocks. This entropy coding engine can function in a single-threaded
 * mode where one code-block is encoded at a time, or in a multi-threaded mode
 * where multiple code-blocks are entropy coded in parallel. The interface
 * presented by this class is the same in both modes.
 *
 * <p>The number of threads used by this entropy coder is specified by the
 * "jj2000.j2k.entropy.encoder.StdEntropyCoder.nthreads" Java system
 * property. If set to "0" the single threaded implementation is used. If set
 * to 'n' ('n' larger than 0) then 'n' extra threads are started by this class
 * which are used to encode the code-blocks in parallel (i.e. ideally 'n'
 * code-blocks will be encoded in parallel at a time). On multiprocessor
 * machines under a "native threads" Java Virtual Machine implementation each
 * one of these threads can run on a separate processor speeding up the
 * encoding time. By default the single-threaded implementation is used. The
 * multi-threaded implementation currently assumes that the vast majority of
 * consecutive calls to 'getNextCodeBlock()' will be done on the same
 * component. If this is not the case, the speed-up that can be expected on
 * multiprocessor machines might be significantly decreased.
 *
 * <p>The code-blocks are rectangular, with dimensions which must be powers of
 * 2. Each dimension has to be no smaller than 4 and no larger than 256. The
 * product of the two dimensions (i.e. area of the code-block) may not exceed
 * 4096.
 *
 * <p>Context 0 of the MQ-coder is used as the uniform one (uniform,
 * non-adaptive probability distribution). Context 1 is used for RLC
 * coding. Contexts 2-10 are used for zero-coding (ZC), contexts 11-15 are
 * used for sign-coding (SC) and contexts 16-18 are used for
 * magnitude-refinement (MR).
 *
 * <p>This implementation buffers the symbols and calls the MQ coder only once
 * per stripe and per coding pass, to reduce the method call overhead.
 *
 * <p>This implementation also provides some timing features. They can be
 * enabled by setting the 'DO_TIMING' constant of this class to true and
 * recompiling. The timing uses the 'System.currentTimeMillis()' Java API
 * call, which returns wall clock time, not the actual CPU time used. The
 * timing results will be printed on the message output. Since the times
 * reported are wall clock times and not CPU usage times they can not be added
 * to find the total used time (i.e. some time might be counted in several
 * places). When timing is disabled ('DO_TIMING' is false) there is no penalty
 * if the compiler performs some basic optimizations. Even if not the penalty
 * should be negligeable.
 *
 * <p>The source module must implement the CBlkQuantDataSrcEnc interface and
 * code-block's data is received in a CBlkWTData instance. This modules sends
 * code-block's information in a CBlkRateDistStats instance.
 *
 * @see CBlkQuantDataSrcEnc
 * @see CBlkWTData
 * @see CBlkRateDistStats
 * */
public class StdEntropyCoder extends EntropyCoder
    implements StdEntropyCoderOptions {

    /** Whether to collect timing information or not: false. Used as a compile
     * time directive. */
    private final static boolean DO_TIMING = false;

    /** The cumulative wall time for the entropy coding engine, for each
     * component. In the single-threaded implementation it is the total time,
     * in the multi-threaded implementation it is the time spent managing the
     * compressor threads only. */
    private long time[];

    /** The Java system property name for the number of threads to use:
     jj2000.j2k.entropy.encoder.StdEntropyCoder.nthreads */
    public static final String THREADS_PROP_NAME =
        "jj2000.j2k.entropy.encoder.StdEntropyCoder.nthreads";

    /** The default value for the property in THREADS_PROP_NAME: 0 */
    public static final String DEF_THREADS_NUM = "0";

    /** The increase in priority for the compressor threads, currently 3. The
     * compressor threads will have a priority of THREADS_PRIORITY_INC more
     * than the priority of the thread calling this class constructor. Used
     * only in the multi-threaded implementation. */
    public static final int THREADS_PRIORITY_INC = 0;

    /** The pool of threads, for the threaded implementation. It is null, if
     * non threaded implementation is used */
    private ThreadPool tPool;

    /** The queue of idle compressors. Used in multithreaded
        implementation only */
    private Stack idleComps;

    /** The queue of completed compressors, for each component. Used
        in multithreaded implementation only. */
    private Stack completedComps[];

    /** The number of busy compressors, for each component. Used in
        multithreaded implementation only. */
    private int nBusyComps[];

    /** A flag indicating for each component if all the code-blocks of the *
        current tile have been returned. Used in multithreaded implementation
        only. */
    private boolean finishedTileComponent[];

    /** The MQ coder used, for each thread */
    private MQCoder mqT[];

    /** The raw bit output used, for each thread */
    private BitToByteOutput boutT[];

    /** The output stream used, for each thread */
    private ByteOutputBuffer outT[];

    /** The code-block size specifications */
    private CBlkSizeSpec cblks;

    /** The precinct partition specifications */
    private PrecinctSizeSpec pss;

    /** By-pass mode specifications */
    public StringSpec bms;

    /** MQ reset specifications */
    public StringSpec mqrs;

    /** Regular termination specifications */
    public StringSpec rts;

    /** Causal stripes specifications */
    public StringSpec css;

    /** Error resilience segment symbol use specifications */
    public StringSpec sss;

    /** The length calculation specifications */
    public StringSpec lcs;

    /** The termination type specifications */
    public StringSpec tts;

    /** The options that are turned on, as flag bits. One element for
     * each tile-component. The options are 'OPT_TERM_PASS',
     * 'OPT_RESET_MQ', 'OPT_VERT_STR_CAUSAL', 'OPT_BYPASS' and
     * 'OPT_SEG_SYMBOLS' as defined in the StdEntropyCoderOptions
     * interface
     *
     * @see StdEntropyCoderOptions
     * */
    private int[][] opts = null;

    /** The length calculation type for each tile-component */
    private int[][] lenCalc = null;

    /** The termination type for each tile-component */
    private int[][] tType = null;

    /** Number of bits used for the Zero Coding lookup table */
    private static final int ZC_LUT_BITS = 8;

    /** Zero Coding context lookup tables for the LH global orientation */
    private static final int ZC_LUT_LH[] = new int[1<<ZC_LUT_BITS];

    /** Zero Coding context lookup tables for the HL global orientation */
    private static final int ZC_LUT_HL[] = new int[1<<ZC_LUT_BITS];

    /** Zero Coding context lookup tables for the HH global orientation */
    private static final int ZC_LUT_HH[] = new int[1<<ZC_LUT_BITS];

    /** Number of bits used for the Sign Coding lookup table */
    private static final int SC_LUT_BITS = 9;

    /** Sign Coding context lookup table. The index into the table is a 9 bit
     * index, which correspond the the value in the 'state' array shifted by
     * 'SC_SHIFT'. Bits 8-5 are the signs of the horizontal-left,
     * horizontal-right, vertical-up and vertical-down neighbors,
     * respectively. Bit 4 is not used (0 or 1 makes no difference). Bits 3-0
     * are the significance of the horizontal-left, horizontal-right,
     * vertical-up and vertical-down neighbors, respectively. The least 4 bits
     * of the value in the lookup table define the context number and the sign
     * bit defines the "sign predictor". */
    private static final int SC_LUT[] = new int[1<<SC_LUT_BITS];

    /** The mask to obtain the context index from the 'SC_LUT' */
    private static final int SC_LUT_MASK = (1<<4)-1;

    /** The shift to obtain the sign predictor from the 'SC_LUT'. It must be
     * an unsigned shift. */
    private static final int SC_SPRED_SHIFT = 31;

    /** The sign bit for int data */
    private static final int INT_SIGN_BIT = 1<<31;

    /** The number of bits used for the Magnitude Refinement lookup table */
    private static final int MR_LUT_BITS = 9;

    /** Magnitude Refinement context lookup table */
    private static final int MR_LUT[] = new int[1<<MR_LUT_BITS];

    /** The number of contexts used */
    private static final int NUM_CTXTS = 19;

    /** The RLC context */
    private static final int RLC_CTXT = 1;

    /** The UNIFORM context (with a uniform probability distribution which
     * does not adapt) */
    private static final int UNIF_CTXT = 0;

    /** The initial states for the MQ coder */
    private static final int MQ_INIT[] = {46, 3, 4, 0, 0, 0, 0, 0, 0, 0, 0,
                                          0, 0, 0, 0, 0, 0, 0, 0};

    /** The 4 symbol segmentation marker (1010) */
    private static final int SEG_SYMBOLS[] = {1,0,1,0};

    /** The 4 contexts for the segmentation marker (always the UNIFORM context,
     * UNIF_CTXT) */
    private static final int SEG_SYMB_CTXTS[] = {UNIF_CTXT, UNIF_CTXT,
                                                 UNIF_CTXT, UNIF_CTXT};

    /**
     * The state array for each thread. Each element of the state array stores
     * the state of two coefficients. The lower 16 bits store the state of a
     * coefficient in row 'i' and column 'j', while the upper 16 bits store
     * the state of a coefficient in row 'i+1' and column 'j'. The 'i' row is
     * either the first or the third row of a stripe. This packing of the
     * states into 32 bit words allows a faster scan of all coefficients on
     * each coding pass and diminished the amount of data transferred. The
     * size of the state array is increased by 1 on each side (top, bottom,
     * left, right) to handle boundary conditions without any special logic.
     *
     * <P>The state of a coefficient is stored in the following way in the
     * lower 16 bits, where bit 0 is the least significant bit. Bit 15 is the
     * significance of a coefficient (0 if non-significant, 1 otherwise). Bit
     * 14 is the visited state (i.e. if a coefficient has been coded in the
     * significance propagation pass of the current bit-plane). Bit 13 is the
     * "non zero-context" state (i.e. if one of the eight immediate neighbors
     * is significant it is 1, otherwise is 0). Bits 12 to 9 store the sign of
     * the already significant left, right, up and down neighbors (1 for
     * negative, 0 for positive or not yet significant). Bit 8 indicates if
     * the magnitude refinement has already been applied to the
     * coefficient. Bits 7 to 4 store the significance of the left, right, up
     * and down neighbors (1 for significant, 0 for non significant). Bits 3
     * to 0 store the significance of the diagonal coefficients (up-left,
     * up-right, down-left and down-right; 1 for significant, 0 for non
     * significant).
     *
     * <P>The upper 16 bits the state is stored as in the lower 16 bits,
     * but with the bits shifted up by 16.
     *
     * <P>The lower 16 bits are referred to as "row 1" ("R1") while the upper
     * 16 bits are referred to as "row 2" ("R2").
     * */
    private int stateT[][];

    /* The separation between the upper and lower bits in the state array: 16
     * */
    private static final int STATE_SEP = 16;

    /** The flag bit for the significance in the state array, for row 1. */
    private static final int STATE_SIG_R1 = 1<<15;

    /** The flag bit for the "visited" bit in the state array, for row 1. */
    private static final int STATE_VISITED_R1 = 1<<14;

    /** The flag bit for the "not zero context" bit in the state array, for
     * row 1. This bit is always the OR of bits STATE_H_L_R1, STATE_H_R_R1,
     * STATE_V_U_R1, STATE_V_D_R1, STATE_D_UL_R1, STATE_D_UR_R1, STATE_D_DL_R1
     * and STATE_D_DR_R1. */
    private static final int STATE_NZ_CTXT_R1 = 1<<13;

    /** The flag bit for the horizontal-left sign in the state array, for row
     * 1. This bit can only be set if the STATE_H_L_R1 is also set. */
    private static final int STATE_H_L_SIGN_R1 = 1<<12;

    /** The flag bit for the horizontal-right sign in the state array, for
     * row 1. This bit can only be set if the STATE_H_R_R1 is also set. */
    private static final int STATE_H_R_SIGN_R1 = 1<<11;

    /** The flag bit for the vertical-up sign in the state array, for row
     * 1. This bit can only be set if the STATE_V_U_R1 is also set. */
    private static final int STATE_V_U_SIGN_R1 = 1<<10;

    /** The flag bit for the vertical-down sign in the state array, for row
     * 1. This bit can only be set if the STATE_V_D_R1 is also set. */
    private static final int STATE_V_D_SIGN_R1 = 1<<9;

    /** The flag bit for the previous MR primitive applied in the state array,
        for row 1. */
    private static final int STATE_PREV_MR_R1 = 1<<8;

    /** The flag bit for the horizontal-left significance in the state array,
        for row 1. */
    private static final int STATE_H_L_R1 = 1<<7;

    /** The flag bit for the horizontal-right significance in the state array,
        for row 1. */
    private static final int STATE_H_R_R1 = 1<<6;

    /** The flag bit for the vertical-up significance in the state array, for
     row 1.  */
    private static final int STATE_V_U_R1 = 1<<5;

    /** The flag bit for the vertical-down significance in the state array,
     for row 1.  */
    private static final int STATE_V_D_R1 = 1<<4;

    /** The flag bit for the diagonal up-left significance in the state array,
        for row 1. */
    private static final int STATE_D_UL_R1 = 1<<3;

    /** The flag bit for the diagonal up-right significance in the state
        array, for row 1.*/
    private static final int STATE_D_UR_R1 = 1<<2;

    /** The flag bit for the diagonal down-left significance in the state
        array, for row 1. */
    private static final int STATE_D_DL_R1 = 1<<1;

    /** The flag bit for the diagonal down-right significance in the state
        array , for row 1.*/
    private static final int STATE_D_DR_R1 = 1;

    /** The flag bit for the significance in the state array, for row 2. */
    private static final int STATE_SIG_R2 = STATE_SIG_R1<<STATE_SEP;

    /** The flag bit for the "visited" bit in the state array, for row 2. */
    private static final int STATE_VISITED_R2 = STATE_VISITED_R1<<STATE_SEP;

    /** The flag bit for the "not zero context" bit in the state array, for
     * row 2. This bit is always the OR of bits STATE_H_L_R2, STATE_H_R_R2,
     * STATE_V_U_R2, STATE_V_D_R2, STATE_D_UL_R2, STATE_D_UR_R2, STATE_D_DL_R2
     * and STATE_D_DR_R2. */
    private static final int STATE_NZ_CTXT_R2 = STATE_NZ_CTXT_R1<<STATE_SEP;

   /** The flag bit for the horizontal-left sign in the state array, for row
     * 2. This bit can only be set if the STATE_H_L_R2 is also set. */
    private static final int STATE_H_L_SIGN_R2 = STATE_H_L_SIGN_R1<<STATE_SEP;

    /** The flag bit for the horizontal-right sign in the state array, for
     * row 2. This bit can only be set if the STATE_H_R_R2 is also set. */
    private static final int STATE_H_R_SIGN_R2 = STATE_H_R_SIGN_R1<<STATE_SEP;

    /** The flag bit for the vertical-up sign in the state array, for row
     * 2. This bit can only be set if the STATE_V_U_R2 is also set. */
    private static final int STATE_V_U_SIGN_R2 = STATE_V_U_SIGN_R1<<STATE_SEP;

    /** The flag bit for the vertical-down sign in the state array, for row
     * 2. This bit can only be set if the STATE_V_D_R2 is also set. */
    private static final int STATE_V_D_SIGN_R2 = STATE_V_D_SIGN_R1<<STATE_SEP;

    /** The flag bit for the previous MR primitive applied in the state array,
        for row 2. */
    private static final int STATE_PREV_MR_R2 = STATE_PREV_MR_R1<<STATE_SEP;

    /** The flag bit for the horizontal-left significance in the state array,
        for row 2. */
    private static final int STATE_H_L_R2 = STATE_H_L_R1<<STATE_SEP;

    /** The flag bit for the horizontal-right significance in the state array,
        for row 2. */
    private static final int STATE_H_R_R2 = STATE_H_R_R1<<STATE_SEP;

    /** The flag bit for the vertical-up significance in the state array, for
     row 2.  */
    private static final int STATE_V_U_R2 = STATE_V_U_R1<<STATE_SEP;

    /** The flag bit for the vertical-down significance in the state array,
     for row 2.  */
    private static final int STATE_V_D_R2 = STATE_V_D_R1<<STATE_SEP;

    /** The flag bit for the diagonal up-left significance in the state array,
        for row 2. */
    private static final int STATE_D_UL_R2 = STATE_D_UL_R1<<STATE_SEP;

    /** The flag bit for the diagonal up-right significance in the state
        array, for row 2.*/
    private static final int STATE_D_UR_R2 = STATE_D_UR_R1<<STATE_SEP;

    /** The flag bit for the diagonal down-left significance in the state
        array, for row 2. */
    private static final int STATE_D_DL_R2 = STATE_D_DL_R1<<STATE_SEP;

    /** The flag bit for the diagonal down-right significance in the state
        array , for row 2.*/
    private static final int STATE_D_DR_R2 = STATE_D_DR_R1<<STATE_SEP;

    /** The mask to isolate the significance bits for row 1 and 2 of the state
     * array. */
    private static final int SIG_MASK_R1R2 = STATE_SIG_R1|STATE_SIG_R2;

    /** The mask to isolate the visited bits for row 1 and 2 of the state
     * array. */
    private static final int VSTD_MASK_R1R2 =
        STATE_VISITED_R1|STATE_VISITED_R2;

    /** The mask to isolate the bits necessary to identify RLC coding state
     * (significant, visited and non-zero context, for row 1 and 2). */
    private static final int RLC_MASK_R1R2 =
        STATE_SIG_R1|STATE_SIG_R2|
        STATE_VISITED_R1|STATE_VISITED_R2|
        STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2;

    /** The mask to obtain the ZC_LUT index from the state information */
    // This is needed because of the STATE_V_D_SIGN_R1, STATE_V_U_SIGN_R1,
    // STATE_H_R_SIGN_R1, and STATE_H_L_SIGN_R1 bits.
    private static final int ZC_MASK = (1<<8)-1;

    /** The shift to obtain the SC index to 'SC_LUT' from the state
     * information, for row 1. */
    private static final int SC_SHIFT_R1 = 4;

    /** The shift to obtain the SC index to 'SC_LUT' from the state
     * information, for row 2. */
    private static final int SC_SHIFT_R2 = SC_SHIFT_R1+STATE_SEP;

    /** The bit mask to isolate the state bits relative to the sign coding
     * lookup table ('SC_LUT'). */
    private static final int SC_MASK = (1<<SC_LUT_BITS)-1;

    /** The mask to obtain the MR index to 'MR_LUT' from the 'state'
     * information. It is to be applied after the 'MR_SHIFT'. */
    private static final int MR_MASK = (1<<MR_LUT_BITS)-1;

    /** The number of bits used to index in the 'fm' lookup table, 7. The 'fs'
     * table is indexed with one less bit. */
    private static final int MSE_LKP_BITS = 7;

    /** The number of fractional bits used to store data in the 'fm' and 'fs'
     * lookup tables. */
    private static final int MSE_LKP_FRAC_BITS = 13;

    /** Distortion estimation lookup table for bits coded using the sign-code
     * (SC) primative, for lossy coding (i.e. normal). */
    private static final int FS_LOSSY[] = new int[1<<(MSE_LKP_BITS-1)];

    /** Distortion estimation lookup table for bits coded using the
     * magnitude-refinement (MR) primative, for lossy coding (i.e. normal) */
    private static final int FM_LOSSY[] = new int[1<<MSE_LKP_BITS];

    /** Distortion estimation lookup table for bits coded using the sign-code
     * (SC) primative, for lossless coding and last bit-plane. This table is
     * different from 'fs_lossy' since when doing lossless coding the residual
     * distortion after the last bit-plane is coded is strictly 0. */
    private static final int FS_LOSSLESS[] = new int[1<<(MSE_LKP_BITS-1)];

    /** Distortion estimation lookup table for bits coded using the
     * magnitude-refinement (MR) primative, for lossless coding and last
     * bit-plane. This table is different from 'fs_lossless' since when doing
     * lossless coding the residual distortion after the last bit-plane is
     * coded is strictly 0.*/
    private static final int FM_LOSSLESS[] = new int[1<<MSE_LKP_BITS];

    /** The buffer for distortion values (avoids reallocation for each
        code-block), for each thread. */
    private double distbufT[][];

    /** The buffer for rate values (avoids reallocation for each
        code-block), for each thread. */
    private int ratebufT[][];

    /** The buffer for indicating terminated passes (avoids reallocation for
     * each code-block), for each thread. */
    private boolean istermbufT[][];

    /** The source code-block to entropy code (avoids reallocation for each
        code-block), for each thread. */
    private CBlkWTData srcblkT[];

    /** Buffer for symbols to send to the MQ-coder, for each thread. Used to
     * reduce the number of calls to the MQ coder. */
    // NOTE: The symbol buffer has not prooved to be of any great improvement
    // in encoding time, but it does not hurt. It's performance should be
    // better studied under different JVMs.
    private int symbufT[][];

    /** Buffer for the contexts to use when sending buffered symbols to the
     * MQ-coder, for each thread. Used to reduce the number of calls to the MQ
     * coder. */
    private int ctxtbufT[][];

    /** boolean used to signal if the precinct partition is used for
     *  each component and each tile.  */
    private boolean precinctPartition[][];

    /**
     * Class that takes care of running the 'compressCodeBlock()' method with
     * thread local arguments. Used only in multithreaded implementation.
     * */
    private class Compressor implements Runnable {
        /** The index of this compressor. Used to access thread local
         * variables */
        private final int idx;

        /** The object where to store the compressed code-block */
        // Should be private, but some buggy JDK 1.1 compilers complain
        CBlkRateDistStats ccb;

        /** The component on which to compress */
        // Should be private, but some buggy JDK 1.1 compilers complain
        int c;

        /** The options bitmask to use in compression */
        // Should be private, but some buggy JDK 1.1 compilers complain
        int options;

        /** The reversible flag to use in compression */
        // Should be private, but some buggy JDK 1.1 compilers complain
        boolean rev;

        /** The length calculation type to use in compression */
        // Should be private, but some buggy JDK 1.1 compilers complain
        int lcType;

        /** The MQ termination type to use in compression */
        // Should be private, but some buggy JDK 1.1 compilers complain
        int tType;

        /** The cumulative wall time for this compressor, for each
         * component. */
        private long time[];

        /**
         * Creates a new compressor object with the given index.
         *
         * @param idx The index of this compressor.
         * */
        Compressor(int idx) {
            this.idx = idx;
            if (DO_TIMING) time = new long[src.getNumComps()];
        }

        /**
         * Calls the 'compressCodeBlock()' method with thread local
         * arguments. Once completed it adds itself to the 'completedComps[c]'
         * stack, where 'c' is the component for which this compressor is
         * running. This last step occurs even if exceptions are thrown by the
         * 'compressCodeBlock()' method.
         * */
        public void run() {
	    // Start the code-block compression
            try {
                long stime = 0L;
                if (DO_TIMING) stime = System.currentTimeMillis();
                compressCodeBlock(c,ccb,srcblkT[idx],mqT[idx],boutT[idx],
                                  outT[idx],stateT[idx],distbufT[idx],
                                  ratebufT[idx],istermbufT[idx],
                                  symbufT[idx],ctxtbufT[idx],options,
                                  rev,lcType,tType);
                if (DO_TIMING) time[c] += System.currentTimeMillis()-stime;
            }
            finally {
                // Join the queue of completed compression, even if exceptions
                // occurred.
                completedComps[c].push(this);
            }
        }

        /**
         * Returns the wall time spent by this compressor for component 'c'
         * since the last call to this method (or the creation of this
         * compressor if not yet called). If DO_TIMING is false 0 is returned.
         *
         * @return The wall time in milliseconds spent by this compressor
         * since the last call to this method.
         * */
        synchronized long getTiming(int c) {
            if (DO_TIMING) {
                long t = time[c];
                time[c] = 0L;
                return t;
            }
            else {
                return 0L;
            }
        }

        /**
         * Returns the index of this compressor.
         *
         * @return The index of this compressor.
         * */
        public int getIdx() {
            return idx;
        }
    }

    /** Static initializer: initializes all the lookup tables. */
    static {
        int i,j;
        double val, deltaMSE;
        int inter_sc_lut[];
        int ds,us,rs,ls;
        int dsgn,usgn,rsgn,lsgn;
        int h,v;

        // Initialize the zero coding lookup tables

        // LH

        // - No neighbors significant
        ZC_LUT_LH[0] = 2;

        // - No horizontal or vertical neighbors significant
        for (i=1; i<16; i++) { // Two or more diagonal coeffs significant
            ZC_LUT_LH[i] = 4;
        }
        for (i=0; i<4; i++) { // Only one diagonal coeff significant
            ZC_LUT_LH[1<<i] = 3;
        }
        // - No horizontal neighbors significant, diagonal irrelevant
        for (i=0; i<16; i++) {
            // Only one vertical coeff significant
            ZC_LUT_LH[STATE_V_U_R1 | i] = 5;
            ZC_LUT_LH[STATE_V_D_R1 | i] = 5;
            // The two vertical coeffs significant
            ZC_LUT_LH[STATE_V_U_R1 | STATE_V_D_R1 | i] = 6;
        }
        // - One horiz. neighbor significant, diagonal/vertical non-significant
        ZC_LUT_LH[STATE_H_L_R1] = 7;
        ZC_LUT_LH[STATE_H_R_R1] = 7;
        // - One horiz. significant, no vertical significant, one or more
        // diagonal significant
        for (i=1; i<16; i++) {
            ZC_LUT_LH[STATE_H_L_R1 | i] = 8;
            ZC_LUT_LH[STATE_H_R_R1 | i] = 8;
        }
        // - One horiz. significant, one or more vertical significant,
        // diagonal irrelevant
        for (i=1; i<4; i++) {
            for (j=0; j<16; j++) {
                ZC_LUT_LH[STATE_H_L_R1 | (i<<4) | j] = 9;
                ZC_LUT_LH[STATE_H_R_R1 | (i<<4) | j] = 9;
            }
        }
        // - Two horiz. significant, others irrelevant
        for (i=0; i<64; i++) {
            ZC_LUT_LH[STATE_H_L_R1 | STATE_H_R_R1 | i] = 10;
        }

        // HL

        // - No neighbors significant
        ZC_LUT_HL[0] = 2;
        // - No horizontal or vertical neighbors significant
        for (i=1; i<16; i++) { // Two or more diagonal coeffs significant
            ZC_LUT_HL[i] = 4;
        }
        for (i=0; i<4; i++) { // Only one diagonal coeff significant
            ZC_LUT_HL[1<<i] = 3;
        }
        // - No vertical significant, diagonal irrelevant
        for (i=0; i<16; i++) {
            // One horiz. significant
            ZC_LUT_HL[STATE_H_L_R1 | i] = 5;
            ZC_LUT_HL[STATE_H_R_R1 | i] = 5;
            // Two horiz. significant
            ZC_LUT_HL[STATE_H_L_R1 | STATE_H_R_R1 | i] = 6;
        }
        // - One vert. significant, diagonal/horizontal non-significant
        ZC_LUT_HL[STATE_V_U_R1] = 7;
        ZC_LUT_HL[STATE_V_D_R1] = 7;
        // - One vert. significant, horizontal non-significant, one or more
        // diag. significant
        for (i=1; i<16; i++) {
            ZC_LUT_HL[STATE_V_U_R1 | i] = 8;
            ZC_LUT_HL[STATE_V_D_R1 | i] = 8;
        }
        // - One vertical significant, one or more horizontal significant,
        // diagonal irrelevant
        for (i=1; i<4; i++) {
            for (j=0; j<16; j++) {
                ZC_LUT_HL[(i<<6) | STATE_V_U_R1 | j] = 9;
                ZC_LUT_HL[(i<<6) | STATE_V_D_R1 | j] = 9;
            }
        }
        // - Two vertical significant, others irrelevant
        for (i=0; i<4; i++) {
            for (j=0; j<16; j++) {
                ZC_LUT_HL[(i<<6) | STATE_V_U_R1 | STATE_V_D_R1 | j] = 10;
            }
        }

        // HH
        int[] twoBits = {3,5,6,9,10,12}; // Figures (between 0 and 15)
        // countaning 2 and only 2 bits on in its binary representation.

        int[] oneBit  = {1,2,4,8}; // Figures (between 0 and 15)
        // countaning 1 and only 1 bit on in its binary representation.

        int[] twoLeast = {3,5,6,7,9,10,11,12,13,14,15}; // Figures
        // (between 0 and 15) countaining, at least, 2 bits on in its
        // binary representation.

        int[] threeLeast = {7,11,13,14,15}; // Figures
        // (between 0 and 15) countaining, at least, 3 bits on in its
        // binary representation.

        // - None significant
        ZC_LUT_HH[0] = 2;

        // - One horizontal+vertical significant, none diagonal
        for(i=0; i<oneBit.length; i++)
            ZC_LUT_HH[ oneBit[i]<<4 ] = 3;

        // - Two or more horizontal+vertical significant, diagonal non-signif
        for(i=0; i<twoLeast.length; i++)
            ZC_LUT_HH[ twoLeast[i]<<4 ] = 4;

        // - One diagonal significant, horiz./vert. non-significant
        for(i=0; i<oneBit.length; i++)
            ZC_LUT_HH[ oneBit[i] ] = 5;

        // - One diagonal significant, one horiz.+vert. significant
        for(i=0; i<oneBit.length; i++)
            for(j=0; j<oneBit.length; j++)
                ZC_LUT_HH[ (oneBit[i]<<4) | oneBit[j] ] = 6;

        // - One diag signif, two or more horiz+vert signif
        for(i=0; i<twoLeast.length; i++)
            for(j=0; j<oneBit.length; j++)
                ZC_LUT_HH[ (twoLeast[i]<<4) | oneBit[j] ] = 7;

        // - Two diagonal significant, none horiz+vert significant
        for(i=0; i<twoBits.length; i++)
            ZC_LUT_HH[ twoBits[i] ] = 8;

        // - Two diagonal significant, one or more horiz+vert significant
        for(j=0; j<twoBits.length; j++)
            for(i=1; i<16; i++)
                ZC_LUT_HH[ (i<<4) | twoBits[j] ] = 9;

        // - Three or more diagonal significant, horiz+vert irrelevant
        for(i=0; i<16; i++)
            for(j=0; j<threeLeast.length; j++)
                ZC_LUT_HH[ (i<<4) | threeLeast[j] ] = 10;

        // Initialize the SC lookup tables

        // Use an intermediate sign code lookup table that is similar to the
        // one in the VM text, in that it depends on the 'h' and 'v'
        // quantities. The index into this table is a 6 bit index, the top 3
        // bits are (h+1) and the low 3 bits (v+1).
        inter_sc_lut = new int[36];
        inter_sc_lut[(2<<3)|2] = 15;
        inter_sc_lut[(2<<3)|1] = 14;
        inter_sc_lut[(2<<3)|0] = 13;
        inter_sc_lut[(1<<3)|2] = 12;
        inter_sc_lut[(1<<3)|1] = 11;
        inter_sc_lut[(1<<3)|0] = 12 | INT_SIGN_BIT;
        inter_sc_lut[(0<<3)|2] = 13 | INT_SIGN_BIT;
        inter_sc_lut[(0<<3)|1] = 14 | INT_SIGN_BIT;
        inter_sc_lut[(0<<3)|0] = 15 | INT_SIGN_BIT;

        // Using the intermediate sign code lookup table create the final
        // one. The index into this table is a 9 bit index, the low 4 bits are
        // the significance of the 4 horizontal/vertical neighbors, while the
        // top 4 bits are the signs of those neighbors. The bit in the middle
        // is ignored. This index arrangement matches the state bits in the
        // 'state' array, thus direct addressing of the table can be done from
        // the sate information.
        for (i=0; i<(1<<SC_LUT_BITS)-1; i++) {
            ds = i & 0x01;        // significance of down neighbor
            us = (i >> 1) & 0x01; // significance of up neighbor
            rs = (i >> 2) & 0x01; // significance of right neighbor
            ls = (i >> 3) & 0x01; // significance of left neighbor
            dsgn = (i >> 5) & 0x01; // sign of down neighbor
            usgn = (i >> 6) & 0x01; // sign of up neighbor
            rsgn = (i >> 7) & 0x01; // sign of right neighbor
            lsgn = (i >> 8) & 0x01; // sign of left neighbor
            // Calculate 'h' and 'v' as in VM text
            h = ls*(1-2*lsgn)+rs*(1-2*rsgn);
            h = (h >= -1) ? h : -1;
            h = (h <= 1) ? h : 1;
            v = us*(1-2*usgn)+ds*(1-2*dsgn);
            v = (v >= -1) ? v : -1;
            v = (v <= 1) ? v : 1;
            // Get context and sign predictor from 'inter_sc_lut'
            SC_LUT[i] = inter_sc_lut[(h+1)<<3|(v+1)];
        }
        inter_sc_lut = null;

        // Initialize the MR lookup tables

        // None significant, prev MR off
        MR_LUT[0] = 16;
        // One or more significant, prev MR off
        for (i=1; i<(1<<(MR_LUT_BITS-1)); i++) {
            MR_LUT[i] = 17;
        }
        // Previous MR on, significance irrelevant
        for (; i<(1<<MR_LUT_BITS); i++) {
            MR_LUT[i] = 18;
        }

        // Initialize the distortion estimation lookup tables

        // fs tables
        for (i=0; i<(1<<(MSE_LKP_BITS-1)); i++) {
            // In fs we index by val-1, since val is really: 1 <= val < 2
            val = (double)i / (1<<(MSE_LKP_BITS-1)) + 1.0;
            deltaMSE = val*val;
            FS_LOSSLESS[i] =
                (int) Math.floor(deltaMSE *
                                 ((double)(1<<MSE_LKP_FRAC_BITS)) + 0.5);
            val -= 1.5;
            deltaMSE -= val*val;
            FS_LOSSY[i] =
                (int) Math.floor(deltaMSE *
                                 ((double)(1<<MSE_LKP_FRAC_BITS)) + 0.5);
        }

        // fm tables
        for (i=0; i<(1<<MSE_LKP_BITS); i++) {
            val = (double)i / (1<<(MSE_LKP_BITS-1));
            deltaMSE = (val-1.0)*(val-1.0);
            FM_LOSSLESS[i] =
                (int) Math.floor(deltaMSE *
                                 ((double)(1<<MSE_LKP_FRAC_BITS)) + 0.5);
            val -= (i<(1<<(MSE_LKP_BITS-1))) ? 0.5 : 1.5;
            deltaMSE -= val*val;
            FM_LOSSY[i] =
                (int) Math.floor(deltaMSE *
                                 ((double)(1<<MSE_LKP_FRAC_BITS)) + 0.5);
        }
    }


    /**
     * Instantiates a new entropy coder engine, with the specified source of
     * data, nominal block width and height.
     *
     * <p>If the 'OPT_PRED_TERM' option is given then the MQ termination must
     * be 'TERM_PRED_ER' or an exception is thrown.</p>
     *
     * @param src The source of data
     *
     * @param cblks Code-block size specifications
     *
     * @param pss Precinct partition specifications
     *
     * @param bms By-pass mode specifications
     *
     * @param mqrs MQ-reset specifications
     *
     * @param rts Regular termination specifications
     *
     * @param css Causal stripes specifications
     *
     * @param sss Error resolution segment symbol use specifications
     *
     * @param lcs Length computation specifications
     *
     * @param tts Termination type specifications
     *
     * @see MQCoder
     * */
    public StdEntropyCoder(CBlkQuantDataSrcEnc src,CBlkSizeSpec cblks,
                           PrecinctSizeSpec pss,StringSpec bms,StringSpec mqrs,
                           StringSpec rts,StringSpec css,StringSpec sss,
                           StringSpec lcs,StringSpec tts) {
        super(src);
        this.cblks = cblks;
        this.pss = pss;
        this.bms = bms;
        this.mqrs = mqrs;
        this.rts = rts;                          
        this.css = css;
        this.sss = sss;
        this.lcs = lcs;
        this.tts = tts;
        int maxCBlkWidth, maxCBlkHeight;
        int i;      // Counter
        int nt;     // The number of threads
        int tsl;    // Size for thread structures

        // Get the biggest width/height for the code-blocks
        maxCBlkWidth = cblks.getMaxCBlkWidth();
        maxCBlkHeight = cblks.getMaxCBlkHeight();

        // Get the number of threads to use, or default to one
        try {
            try {
                nt = Integer.parseInt(System.getProperty(THREADS_PROP_NAME,
                                                         DEF_THREADS_NUM));
            } catch(SecurityException se) {
                // Use the default value.
                nt = Integer.parseInt(DEF_THREADS_NUM);
            }
            if (nt < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number of threads "+
                                               "for "+
                                               "entropy coding in property "+
                                               THREADS_PROP_NAME);
        }

        // If we do timing create necessary structures
        if (DO_TIMING) {
            time = new long[src.getNumComps()];
            // If we are timing make sure that 'finalize' gets called.
            //System.runFinalizersOnExit(true);
        }

        // If using multithreaded implementation get necessasry objects
        if (nt > 0) {
            FacilityManager.getMsgLogger().
                printmsg(MsgLogger.INFO,
                         "Using multithreaded entropy coder "+
                         "with "+nt+" compressor threads.");
            tsl = nt;
            tPool = new ThreadPool(nt,Thread.currentThread().getPriority()+
                                   THREADS_PRIORITY_INC,"StdEntropyCoder");
	    idleComps = new Stack();
            completedComps = new Stack[src.getNumComps()];
            nBusyComps = new int[src.getNumComps()];
            finishedTileComponent = new boolean[src.getNumComps()];
            for (i=src.getNumComps()-1; i>=0; i--) {
                completedComps[i] = new Stack();
            }
	    for (i=0; i<nt; i++) {
		idleComps.push(new StdEntropyCoder.Compressor(i));
	    }
        }
        else {
            tsl = 1;
            tPool = null;
	    idleComps = null;
            completedComps = null;
            nBusyComps = null;
            finishedTileComponent = null;
        }

        // Allocate data structures
        outT = new ByteOutputBuffer[tsl];
        mqT = new MQCoder[tsl];
        boutT = new BitToByteOutput[tsl];
        stateT = new int[tsl][(maxCBlkWidth+2)*((maxCBlkHeight+1)/2+2)];
        symbufT = new int[tsl][maxCBlkWidth*(STRIPE_HEIGHT*2+2)];
        ctxtbufT =  new int[tsl][maxCBlkWidth*(STRIPE_HEIGHT*2+2)];
        distbufT = new double[tsl][32*NUM_PASSES];
        ratebufT = new int[tsl][32*NUM_PASSES];
        istermbufT = new boolean[tsl][32*NUM_PASSES];
        srcblkT = new CBlkWTData[tsl];
        for (i=0; i<tsl; i++) {
            outT[i] = new ByteOutputBuffer();
            mqT[i] = new MQCoder(outT[i],NUM_CTXTS,MQ_INIT);
        }
        precinctPartition = new boolean [src.getNumComps()][src.getNumTiles()];

        // Create the subband description for each component and each tile
        Point numTiles = src.getNumTiles(null);
        //Subband sb = null;
        int nc = getNumComps();
        initTileComp(getNumTiles(),nc);

        for (int c=0; c<nc; c++) {
            for (int tY=0; tY<numTiles.y; tY++) {
                for (int tX=0; tX<numTiles.x; tX++) {
                    precinctPartition[c][tIdx] = false;
                }
            }
        }
    }

    /**
     * Prints the timing information, if collected, and calls 'finalize' on
     * the super class.
     * */
    public void finalize() throws Throwable {
        if (DO_TIMING) {
            int c;
            StringBuffer sb;

            if (tPool == null) { // Single threaded implementation
                sb = new StringBuffer("StdEntropyCoder compression wall "+
                                      "clock time:");
                for (c=0; c<time.length; c++) {
                    sb.append("\n  component ");
                    sb.append(c);
                    sb.append(": ");
                    sb.append(time[c]);
                    sb.append(" ms");
                }
                FacilityManager.getMsgLogger().
                    printmsg(MsgLogger.INFO,sb.toString());
            }
            else { // Multithreaded implementation
                Compressor compr;
                MsgLogger msglog = FacilityManager.getMsgLogger();

                sb = new StringBuffer("StdEntropyCoder manager thread "+
                                      "wall clock time:");
                for (c=0; c<time.length; c++) {
                    sb.append("\n  component ");
                    sb.append(c);
                    sb.append(": ");
                    sb.append(time[c]);
                    sb.append(" ms");
                }
                Enumeration enumVar = idleComps.elements();
                sb.append("\nStdEntropyCoder compressor threads wall clock "+
                          "time:");
                while (enumVar.hasMoreElements()) {
                    compr = (Compressor)(enumVar.nextElement());
                    for (c=0; c<time.length; c++) {
                        sb.append("\n  compressor ");
                        sb.append(compr.getIdx());
                        sb.append(", component ");
                        sb.append(c);
                        sb.append(": ");
                        sb.append(compr.getTiming(c));
                        sb.append(" ms");
                    }
                }
                FacilityManager.getMsgLogger().
                    printmsg(MsgLogger.INFO,sb.toString());
            }
        }
        super.finalize();
    }

    /**
     * Returns the code-block width for the specified tile and component.
     *
     * @param t The tile index
     * 
     * @param c the component index
     *
     * @return The code-block width for the specified tile and component
     * */
    public int getCBlkWidth(int t, int c) {
        return cblks.getCBlkWidth(ModuleSpec.SPEC_TILE_COMP,t,c);
    }

    /**
     * Returns the code-block height for the specified tile and component.
     *
     * @param t The tile index
     *
     * @param c The component index
     *
     * @return The code-block height for the specified tile and component.
     * */
    public int getCBlkHeight(int t,int c) {
        return cblks.getCBlkHeight(ModuleSpec.SPEC_TILE_COMP,t,c);
    }

    /**
     * Returns the next coded code-block in the current tile for the specified
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
     * <P>The data returned by this method is always a copy of the internal
     * data of this object, if any, and it can be modified "in place" without
     * any problems after being returned.
     *
     * @param c The component for which to return the next code-block.
     *
     * @param ccb If non-null this object might be used in returning the coded
     * code-block in this or any subsequent call to this method. If null a new
     * one is created and returned. If the 'data' array of 'cbb' is not null
     * it may be reused to return the compressed data.
     *
     * @return The next coded code-block in the current tile for component
     * 'n', or null if all code-blocks for the current tile have been
     * returned.
     *
     * @see CBlkRateDistStats
     * */
    public CBlkRateDistStats getNextCodeBlock(int c, CBlkRateDistStats ccb) {
        long stime = 0L;     // Start time for timed sections
        if (tPool == null) { // Use single threaded implementation
            // Get code-block data from source
            srcblkT[0] = src.getNextInternCodeBlock(c,srcblkT[0]);

            if (DO_TIMING) stime = System.currentTimeMillis();
            if (srcblkT[0] == null) { // We got all code-blocks
                return null;
            }
            // Initialize thread local variables
            if((opts[tIdx][c]&OPT_BYPASS) != 0 && boutT[0] == null) {
                boutT[0] = new BitToByteOutput(outT[0]);
            }
            // Initialize output code-block
            if (ccb == null) {
                ccb = new CBlkRateDistStats();
            }
            // Compress code-block
            compressCodeBlock(c,ccb,srcblkT[0],mqT[0],boutT[0],outT[0],
                              stateT[0],distbufT[0],ratebufT[0],
                              istermbufT[0],symbufT[0],ctxtbufT[0],
                              opts[tIdx][c],isReversible(tIdx,c),
                              lenCalc[tIdx][c],tType[tIdx][c]);
            if (DO_TIMING) time[c] += System.currentTimeMillis()-stime;
            // Return result
            return ccb;
        }
        else { // Use multiple threaded implementation
            int cIdx;           // Compressor idx
            Compressor compr;   // Compressor

            if (DO_TIMING) stime = System.currentTimeMillis();
            // Give data to all free compressors, using the current component
            while (!finishedTileComponent[c] && !idleComps.empty()) {
                // Get an idle compressor
                compr = (Compressor) idleComps.pop();
                cIdx = compr.getIdx();
                // Get data for the compressor and wake it up
                if (DO_TIMING) time[c] += System.currentTimeMillis()-stime;
                srcblkT[cIdx] = src.getNextInternCodeBlock(c,srcblkT[cIdx]);
                if (DO_TIMING) stime = System.currentTimeMillis();
                if (srcblkT[cIdx] != null) {
                    // Initialize thread local variables
                    if((opts[tIdx][c]&OPT_BYPASS) != 0 && boutT[cIdx] == null){
                        boutT[cIdx] = new BitToByteOutput(outT[cIdx]);
                    }
                    // Initialize output code-block and compressor thread
                    if (ccb == null) ccb = new CBlkRateDistStats();
                    compr.ccb = ccb;
                    compr.c = c;
                    compr.options = opts[tIdx][c];
                    compr.rev = isReversible(tIdx,c);
                    compr.lcType = lenCalc[tIdx][c];
                    compr.tType = tType[tIdx][c];
                    nBusyComps[c]++;
                    ccb = null;
                    // Send compressor to execution in thread pool
		    tPool.runTarget(compr,completedComps[c]);
                }
                else {
                    // We finished with all the code-blocks in the current
                    // tile component
                    idleComps.push(compr);
                    finishedTileComponent[c] = true;
                }
            }
            // If there are threads for this component which result has not
            // been returned yet, get it
            if (nBusyComps[c] > 0) {
                synchronized (completedComps[c]) {
                    // If no compressor is done, wait until one is
                    if (completedComps[c].empty()) {
                        try {
                            if (DO_TIMING) {
                                time[c] += System.currentTimeMillis()-stime;
                            }
                            completedComps[c].wait();
                            if (DO_TIMING) {
                                stime = System.currentTimeMillis();
                            }
                        } catch (InterruptedException e) {
                        }
                    }
                    // Remove the thread from the completed queue and put it
                    // on the idle queue
                    compr = (Compressor) completedComps[c].pop();
                    cIdx = compr.getIdx();
                    nBusyComps[c]--;
                    idleComps.push(compr);
                    // Check targets error condition
                    tPool.checkTargetErrors();
                    // Get the result of compression and return that.
                    if (DO_TIMING) time[c] += System.currentTimeMillis()-stime;
                    return compr.ccb;
                }
            }
            else {
                // Check targets error condition
                tPool.checkTargetErrors();
                // Printing timing info if necessary
                if (DO_TIMING) time[c] += System.currentTimeMillis()-stime;
                // Nothing is running => no more code-blocks
                return null;
            }
        }
    }

    /**
     * Changes the current tile, given the new indexes. An
     * IllegalArgumentException is thrown if the indexes do not
     * correspond to a valid tile.
     *
     * <P>This default implementation just changes the tile in the
     * source.
     *
     * @param x The horizontal index of the tile.
     *
     * @param y The vertical index of the new tile.
     * */
    public void setTile(int x, int y) {
        super.setTile(x,y);
        // Reset the tilespecific variables
        if (finishedTileComponent != null) {
            for (int c=src.getNumComps()-1; c>=0; c--) {
                finishedTileComponent[c] = false;
            }
        }
    }

    /**
     * Advances to the next tile, in standard scan-line order (by rows
     * then columns). An NoNextElementException is thrown if the
     * current tile is the last one (i.e. there is no next tile).
     *
     * <P>This default implementation just advances to the next tile
     * in the source.
     * */
    public void nextTile() {
        // Reset the tilespecific variables
        if (finishedTileComponent != null) {
            for (int c=src.getNumComps()-1; c>=0; c--) {
                finishedTileComponent[c] = false;
            }
        }
        super.nextTile();
    }


    /**
     * Compresses the code-block in 'srcblk' and puts the results in 'ccb',
     * using the specified options and temporary storage.
     *
     * @param c The component for which to return the next code-block.
     *
     * @param ccb The object where the compressed data will be stored. If the
     * 'data' array of 'cbb' is not null it may be reused to return the
     * compressed data.
     *
     * @param srcblk The code-block data to code
     *
     * @param mq The MQ-coder to use
     *
     * @param bout The bit level output to use. Used only if 'OPT_BYPASS' is
     * turned on in the 'options' argument.
     *
     * @param out The byte buffer trough which the compressed data is stored.
     *
     * @param state The state information for the code-block
     *
     * @param distbuf The buffer where to store the distortion  at
     * the end of each coding pass.
     *
     * @param ratebuf The buffer where to store the rate (i.e. coded lenth) at
     * the end of each coding pass.
     *
     * @param istermbuf The buffer where to store the terminated flag for each
     * coding pass.
     *
     * @param symbuf The buffer to hold symbols to send to the MQ coder
     *
     * @param ctxtbuf A buffer to hold the contexts to use in sending the
     * buffered symbols to the MQ coder.
     *
     * @param options The options to use when coding this code-block
     *
     * @param rev The reversible flag. Should be true if the source of this
     * code-block's data is reversible.
     *
     * @param lcType The type of length calculation to use with the MQ coder.
     *
     * @param tType The type of termination to use with the MQ coder.
     *
     * @see #getNextCodeBlock
     * */
    static private void compressCodeBlock(int c, CBlkRateDistStats ccb,
                                          CBlkWTData srcblk, MQCoder mq,
                                          BitToByteOutput bout,
                                          ByteOutputBuffer out,
                                          int state[],
                                          double distbuf[], int ratebuf[],
                                          boolean istermbuf[], int symbuf[],
                                          int ctxtbuf[], int options,
                                          boolean rev,
                                          int lcType, int tType) {
        // NOTE: This method should not access any non-final instance or
        // static variables, either directly or indirectly through other
        // methods in order to be sure that the method is thread safe.

        int zc_lut[];  // The ZC lookup table to use
        int skipbp;    // The number of non-significant bit-planes to skip
        int curbp;     // The current magnitude bit-plane (starts at 30)
        int fm[];      // The distortion estimation lookup table for MR
        int fs[];      // The distortion estimation lookup table for SC
        int lmb;       // The least significant magnitude bit
        int npass;     // The number of coding passes, for R-D statistics
        double msew;   // The distortion (MSE weight) for the current bit-plane
        double totdist;// The total cumulative distortion decrease
        int ltpidx;    // The index of the last pass which is terminated


        // Check error-resilient termination
        if ((options & OPT_PRED_TERM) != 0 && tType != MQCoder.TERM_PRED_ER) {
            throw
                new IllegalArgumentException("Embedded error-resilient info "+
                                             "in MQ termination option "+
                                             "specified but incorrect MQ "+
                                             "termination "+
                                             "policy specified");
        }
        // Set MQ flags
        mq.setLenCalcType(lcType);
        mq.setTermType(tType);

        lmb = 30-srcblk.magbits+1;
        // If there are are more bit-planes to code than the implementation
        // bitdepth set lmb to 0
        lmb = (lmb < 0) ? 0:lmb;

        // Reset state
        ArrayUtil.intArraySet(state,0);

        // Find the most significant bit-plane
        skipbp = calcSkipMSBP(srcblk,lmb);

        // Initialize output code-block
        ccb.m = srcblk.m;
        ccb.n = srcblk.n;
        ccb.sb = srcblk.sb;
        ccb.nROIcoeff = srcblk.nROIcoeff;
        ccb.skipMSBP = skipbp;
        if(ccb.nROIcoeff!=0) {
            ccb.nROIcp = 3*(srcblk.nROIbp-skipbp-1)+1;
        } else {
            ccb.nROIcp = 0;
        }

        // Choose correct ZC lookup table for global orientation
        switch (srcblk.sb.orientation) {
        case Subband.WT_ORIENT_HL:
            zc_lut = ZC_LUT_HL;
            break;
        case Subband.WT_ORIENT_LL:
        case Subband.WT_ORIENT_LH:
            zc_lut = ZC_LUT_LH;
            break;
        case Subband.WT_ORIENT_HH:
            zc_lut = ZC_LUT_HH;
            break;
        default:
            throw new Error("JJ2000 internal error");
        }

        // Loop on significant magnitude bit-planes doing the 3 passes
        curbp = 30-skipbp;
        fs = FS_LOSSLESS;
        fm = FM_LOSSLESS;
        msew = Math.pow(2,((curbp-lmb)<<1)-MSE_LKP_FRAC_BITS)*
            srcblk.sb.stepWMSE*srcblk.wmseScaling;
        totdist = 0f;
        npass = 0;
        ltpidx = -1;
        // First significant bit-plane has only the pass pass
        if (curbp >= lmb) {
            // Do we need the "lossless" 'fs' table ?
            if (rev && curbp == lmb) {
                fs = FM_LOSSLESS;
            }
            // We terminate if regular termination, last bit-plane, or next
            // bit-plane is "raw".
            istermbuf[npass] = (options & OPT_TERM_PASS) != 0 || curbp == lmb ||
                ((options & OPT_BYPASS) != 0 &&
                 (31-NUM_NON_BYPASS_MS_BP-skipbp)>=curbp);
            totdist += cleanuppass(srcblk,mq,istermbuf[npass],curbp,state,
                                   fs,zc_lut,symbuf,ctxtbuf,ratebuf,
                                   npass,ltpidx,options)*msew;
            distbuf[npass] = totdist;
            if (istermbuf[npass]) ltpidx = npass;
            npass++;
            msew *= 0.25;
            curbp--;
        }
        // Other bit-planes have all passes
        while (curbp >= lmb) {
            // Do we need the "lossless" 'fs' and 'fm' tables ?
            if (rev && curbp == lmb) {
                fs = FS_LOSSLESS;
                fm = FM_LOSSLESS;
            }

            // Do the significance propagation pass
            // We terminate if regular termination only
            istermbuf[npass] = (options & OPT_TERM_PASS) != 0;
            if ((options & OPT_BYPASS) == 0 ||
                (31-NUM_NON_BYPASS_MS_BP-skipbp<=curbp)) { // No bypass coding
                totdist += sigProgPass(srcblk,mq,istermbuf[npass],curbp,
                                       state,fs,zc_lut,
                                       symbuf,ctxtbuf,ratebuf,
                                       npass,ltpidx,options)*msew;
            }
            else { // Bypass ("raw") coding
		bout.setPredTerm((options & OPT_PRED_TERM)!=0);
                totdist += rawSigProgPass(srcblk,bout,istermbuf[npass],curbp,
                                          state,fs,ratebuf,npass,ltpidx,
                                          options)*msew;
            }
            distbuf[npass] = totdist;
            if (istermbuf[npass]) ltpidx = npass;
            npass++;

            // Do the magnitude refinement pass
            // We terminate if regular termination or bypass ("raw") coding
            istermbuf[npass] = (options & OPT_TERM_PASS) != 0 ||
                ((options & OPT_BYPASS) != 0 &&
                 (31-NUM_NON_BYPASS_MS_BP-skipbp>curbp));
            if ((options & OPT_BYPASS) == 0 ||
                (31-NUM_NON_BYPASS_MS_BP-skipbp<=curbp)) { // No bypass coding
                totdist += magRefPass(srcblk,mq,istermbuf[npass],curbp,state,
                                      fm,symbuf,ctxtbuf,ratebuf,
                                      npass,ltpidx,options)*msew;
            }
            else { // Bypass ("raw") coding
		bout.setPredTerm((options & OPT_PRED_TERM)!=0);
                totdist += rawMagRefPass(srcblk,bout,istermbuf[npass],curbp,
                                         state,fm,ratebuf,
                                         npass,ltpidx,options)*msew;
            }
            distbuf[npass] = totdist;
            if (istermbuf[npass]) ltpidx = npass;
            npass++;

            // Do the clenup pass
            // We terminate if regular termination, last bit-plane, or next
            // bit-plane is "raw".
            istermbuf[npass] = (options & OPT_TERM_PASS) != 0 || curbp == lmb ||
                ((options & OPT_BYPASS) != 0 &&
                 (31-NUM_NON_BYPASS_MS_BP-skipbp)>=curbp);
            totdist += cleanuppass(srcblk,mq,istermbuf[npass],curbp,state,
                                   fs,zc_lut,symbuf,ctxtbuf,ratebuf,
                                   npass,ltpidx,options)*msew;
            distbuf[npass] = totdist;
            if (istermbuf[npass]) ltpidx = npass;
            npass++;

            // Goto next bit-plane
            msew *= 0.25;
            curbp--;
        }

        // Copy compressed data and rate-distortion statistics to output
        ccb.data = new byte[out.size()];
        out.toByteArray(0,out.size(),ccb.data,0);
        checkEndOfPassFF(ccb.data,ratebuf,istermbuf,npass);
        ccb.selectConvexHull(ratebuf,distbuf,
                             (options&(OPT_BYPASS|OPT_TERM_PASS))!=0?istermbuf:
                             null,npass,rev);

        // Reset MQ coder and bit output for next code-block
        mq.reset();
        if (bout != null) bout.reset();

        // Done
    }

    /**
     * Calculates the number of magnitude bit-planes that are to be skipped,
     * because they are non-significant. The algorithm looks for the largest
     * magnitude and calculates the most significant bit-plane of it.
     *
     * @param cblk The code-block of data to scan
     *
     * @param lmb The least significant magnitude bit in the data
     *
     * @return The number of magnitude bit-planes to skip (i.e. all zero most
     * significant bit-planes).
     **/
    static private int calcSkipMSBP(CBlkWTData cblk, int lmb) {
        int k,kmax,mask;
        int data[];
        int maxmag;
        int mag;
        int w,h;
        int msbp;
        int l;

        data = (int[]) cblk.getData();
        w = cblk.w;
        h = cblk.h;

        // First look for the maximum magnitude in the code-block
        maxmag = 0;
        // Consider only magnitude bits that are in non-fractional bit-planes.
        mask = 0x7FFFFFFF&(~((1<<lmb)-1));
        for (l=h-1, k=cblk.offset; l>=0; l--) {
            for (kmax = k+w; k<kmax; k++) {
                mag = data[k]&mask;
                if (mag > maxmag) maxmag = mag;
            }
            k += cblk.scanw-w;
        }
        // Now calculate the number of all zero most significant bit-planes for
        // the maximum magnitude.
        msbp = 30;
        do {
            if (((1<<msbp)&maxmag)!=0) break;
            msbp--;
        } while (msbp>=lmb);

        // Return the number of non-significant bit-planes to skip
        return 30-msbp;
    }

    /**
     * Performs the significance propagation pass on the specified data and
     * bit-plane. It codes all insignificant samples which have, at least, one
     * of its immediate eight neighbors already significant, using the ZC and
     * SC primitives as needed. It toggles the "visited" state bit to 1 for
     * all those samples.
     *
     * @param srcblk The code-block data to code
     *
     * @param mq The MQ-coder to use
     *
     * @param doterm If true it performs an MQ-coder termination after the end
     * of the pass
     *
     * @param bp The bit-plane to code
     *
     * @param state The state information for the code-block
     *
     * @param fs The distortion estimation lookup table for SC
     *
     * @param zc_lut The ZC lookup table to use in ZC.
     *
     * @param symbuf The buffer to hold symbols to send to the MQ coder
     *
     * @param ctxtbuf A buffer to hold the contexts to use in sending the
     * buffered symbols to the MQ coder.
     *
     * @param ratebuf The buffer where to store the rate (i.e. coded lenth) at
     * the end of this coding pass.
     *
     * @param pidx The coding pass index. Is the index in the 'ratebuf' array
     * where to store the coded length after this coding pass.
     *
     * @param ltpidx The index of the last pass that was terminated, or
     * negative if none.
     *
     * @param options The bitmask of entropy coding options to apply to the
     * code-block
     *
     * @return The decrease in distortion for this pass, in the fixed-point
     * normalized representation of the 'FS_LOSSY' and 'FS_LOSSLESS' tables.
     * */
    static private int sigProgPass(CBlkWTData srcblk, MQCoder mq,
                                   boolean doterm, int bp, int state[],
                                   int fs[], int zc_lut[],
                                   int symbuf[], int ctxtbuf[],
                                   int ratebuf[], int pidx, int ltpidx,
                                   int options) {
        int j,sj;        // The state index for line and stripe
        int k,sk;        // The data index for line and stripe
        int nsym;        // Symbol counter for symbol and context buffers
        int dscanw;      // The data scan-width
        int sscanw;      // The state and packed state scan-width
        int jstep;       // Stripe to stripe step for 'sj'
        int kstep;       // Stripe to stripe step for 'sk'
        int stopsk;      // The loop limit on the variable sk
        int csj;         // Local copy (i.e. cached) of 'state[j]'
        int mask;        // The mask for the current bit-plane
        int sym;         // The symbol to code
        int ctxt;        // The context to use
        int data[];      // The data buffer
        int dist;        // The distortion reduction for this pass
        int shift;       // Shift amount for distortion
        int upshift;     // Shift left amount for distortion
        int downshift;   // Shift right amount for distortion
        int normval;     // The normalized sample magnitude value
        int s;           // The stripe index
        boolean causal;  // Flag to indicate if stripe-causal context
                         // formation is to be used
        int nstripes;    // The number of stripes in the code-block
        int sheight;     // Height of the current stripe
        int off_ul,off_ur,off_dr,off_dl; // offsets

        // Initialize local variables
        dscanw = srcblk.scanw;
        sscanw = srcblk.w+2;
        jstep = sscanw*STRIPE_HEIGHT/2-srcblk.w;
        kstep = dscanw*STRIPE_HEIGHT-srcblk.w;
        mask = 1<<bp;
        data = (int[]) srcblk.getData();
        nstripes = (srcblk.h+STRIPE_HEIGHT-1)/STRIPE_HEIGHT;
        dist = 0;
        // We use the MSE_LKP_BITS-1 bits below the bit just coded for
        // distortion estimation.
        shift = bp-(MSE_LKP_BITS-1);
        upshift = (shift>=0) ? 0 : -shift;
        downshift = (shift<=0) ? 0 : shift;
        causal = (options & OPT_VERT_STR_CAUSAL) != 0;

        // Pre-calculate offsets in 'state' for diagonal neighbors
        off_ul = -sscanw-1; // up-left
        off_ur = -sscanw+1; // up-right
        off_dr = sscanw+1;  // down-right
        off_dl = sscanw-1;  // down-left

        // Code stripe by stripe
        sk = srcblk.offset;
        sj = sscanw+1;
        for (s = nstripes-1; s >= 0; s--, sk+=kstep, sj+=jstep) {
            sheight = (s != 0) ? STRIPE_HEIGHT :
                srcblk.h-(nstripes-1)*STRIPE_HEIGHT;
            stopsk = sk+srcblk.w;
            // Scan by set of 1 stripe column at a time
            for (nsym = 0; sk < stopsk; sk++, sj++) {
                // Do half top of column
                j = sj;
                csj = state[j];
                // If any of the two samples is not significant and has a
                // non-zero context (i.e. some neighbor is significant) we can
                // not skip them
                if ((((~csj) & (csj<<2)) & SIG_MASK_R1R2) != 0) {
                    k = sk;
                    // Scan first row
                    if ((csj & (STATE_SIG_R1|STATE_NZ_CTXT_R1)) ==
                        STATE_NZ_CTXT_R1) {
                        // Apply zero coding
                        ctxtbuf[nsym] = zc_lut[csj&ZC_MASK];
                        if ((symbuf[nsym++] = (data[k]&mask)>>>bp) != 0) {
                            // Became significant
                            // Apply sign coding
                            sym = data[k]>>>31;
                            ctxt = SC_LUT[(csj>>>SC_SHIFT_R1)&SC_MASK];
                            symbuf[nsym] = sym ^ (ctxt>>>SC_SPRED_SHIFT);
                            ctxtbuf[nsym++] = ctxt & SC_LUT_MASK;
                            // Update state information (significant bit,
                            // visited bit, neighbor significant bit of
                            // neighbors, non zero context of neighbors, sign
                            // of neighbors)
                            if (!causal) {
                                // If in causal mode do not change contexts of
                                // previous stripe.
                                state[j+off_ul] |=
                                    STATE_NZ_CTXT_R2|STATE_D_DR_R2;
                                state[j+off_ur] |=
                                    STATE_NZ_CTXT_R2|STATE_D_DL_R2;
                            }
                            // Update sign state information of neighbors
                            if (sym != 0) {
                                csj |= STATE_SIG_R1|STATE_VISITED_R1|
                                    STATE_NZ_CTXT_R2|
                                    STATE_V_U_R2|STATE_V_U_SIGN_R2;
                                if (!causal) {
                                    // If in causal mode do not change
                                    // contexts of previous stripe.
                                    state[j-sscanw] |= STATE_NZ_CTXT_R2|
                                        STATE_V_D_R2|STATE_V_D_SIGN_R2;
                                }
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_L_R1|STATE_H_L_SIGN_R1|
                                    STATE_D_UL_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_R_R1|STATE_H_R_SIGN_R1|
                                    STATE_D_UR_R2;
                            }
                            else {
                                csj |= STATE_SIG_R1|STATE_VISITED_R1|
                                    STATE_NZ_CTXT_R2|STATE_V_U_R2;
                                if (!causal) {
                                    // If in causal mode do not change
                                    // contexts of previous stripe.
                                    state[j-sscanw] |= STATE_NZ_CTXT_R2|
                                        STATE_V_D_R2;
                                }
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_L_R1|STATE_D_UL_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_R_R1|STATE_D_UR_R2;
                            }
                            // Update distortion
                            normval = (data[k] >> downshift) << upshift;
                            dist += fs[normval & ((1<<(MSE_LKP_BITS-1))-1)];
                        }
                        else {
                            csj |= STATE_VISITED_R1;
                        }
                    }
                    if (sheight < 2) {
                        state[j] = csj;
                        continue;
                    }
                    // Scan second row
                    if ((csj & (STATE_SIG_R2|STATE_NZ_CTXT_R2)) ==
                        STATE_NZ_CTXT_R2) {
                        k += dscanw;
                        // Apply zero coding
                        ctxtbuf[nsym] = zc_lut[(csj>>>STATE_SEP)&ZC_MASK];
                        if ((symbuf[nsym++] = (data[k]&mask)>>>bp) != 0) {
                            // Became significant
                            // Apply sign coding
                            sym = data[k]>>>31;
                            ctxt = SC_LUT[(csj>>>SC_SHIFT_R2)&SC_MASK];
                            symbuf[nsym] = sym ^ (ctxt>>>SC_SPRED_SHIFT);
                            ctxtbuf[nsym++] = ctxt & SC_LUT_MASK;
                            // Update state information (significant bit,
                            // visited bit, neighbor significant bit of
                            // neighbors, non zero context of neighbors, sign
                            // of neighbors)
                            state[j+off_dl] |= STATE_NZ_CTXT_R1|STATE_D_UR_R1;
                            state[j+off_dr] |= STATE_NZ_CTXT_R1|STATE_D_UL_R1;
                            // Update sign state information of neighbors
                            if (sym != 0) {
                                csj |= STATE_SIG_R2|STATE_VISITED_R2|
                                    STATE_NZ_CTXT_R1|
                                    STATE_V_D_R1|STATE_V_D_SIGN_R1;
                                state[j+sscanw] |= STATE_NZ_CTXT_R1|
                                    STATE_V_U_R1|STATE_V_U_SIGN_R1;
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DL_R1|
                                    STATE_H_L_R2|STATE_H_L_SIGN_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DR_R1|
                                    STATE_H_R_R2|STATE_H_R_SIGN_R2;
                            }
                            else {
                                csj |= STATE_SIG_R2|STATE_VISITED_R2|
                                    STATE_NZ_CTXT_R1|STATE_V_D_R1;
                                state[j+sscanw] |= STATE_NZ_CTXT_R1|
                                    STATE_V_U_R1;
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DL_R1|STATE_H_L_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DR_R1|STATE_H_R_R2;
                            }
                            // Update distortion
                            normval = (data[k] >> downshift) << upshift;
                            dist += fs[normval & ((1<<(MSE_LKP_BITS-1))-1)];
                        }
                        else {
                            csj |= STATE_VISITED_R2;
                        }
                    }
                    state[j] = csj;
                }
                // Do half bottom of column
                if (sheight < 3) continue;
                j += sscanw;
                csj = state[j];
                // If any of the two samples is not significant and has a
                // non-zero context (i.e. some neighbor is significant) we can
                // not skip them
                if ((((~csj) & (csj<<2)) & SIG_MASK_R1R2) != 0) {
                    k = sk+(dscanw<<1);
                    // Scan first row
                    if ((csj & (STATE_SIG_R1|STATE_NZ_CTXT_R1)) ==
                        STATE_NZ_CTXT_R1) {
                        // Apply zero coding
                        ctxtbuf[nsym] = zc_lut[csj&ZC_MASK];
                        if ((symbuf[nsym++] = (data[k]&mask)>>>bp) != 0) {
                            // Became significant
                            // Apply sign coding
                            sym = data[k]>>>31;
                            ctxt = SC_LUT[(csj>>>SC_SHIFT_R1)&SC_MASK];
                            symbuf[nsym] = sym ^ (ctxt>>>SC_SPRED_SHIFT);
                            ctxtbuf[nsym++] = ctxt & SC_LUT_MASK;
                            // Update state information (significant bit,
                            // visited bit, neighbor significant bit of
                            // neighbors, non zero context of neighbors, sign
                            // of neighbors)
                            state[j+off_ul] |= STATE_NZ_CTXT_R2|STATE_D_DR_R2;
                            state[j+off_ur] |= STATE_NZ_CTXT_R2|STATE_D_DL_R2;
                            // Update sign state information of neighbors
                            if (sym != 0) {
                                csj |= STATE_SIG_R1|STATE_VISITED_R1|
                                    STATE_NZ_CTXT_R2|
                                    STATE_V_U_R2|STATE_V_U_SIGN_R2;
                                state[j-sscanw] |= STATE_NZ_CTXT_R2|
                                    STATE_V_D_R2|STATE_V_D_SIGN_R2;
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_L_R1|STATE_H_L_SIGN_R1|
                                    STATE_D_UL_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_R_R1|STATE_H_R_SIGN_R1|
                                    STATE_D_UR_R2;
                            }
                            else {
                                csj |= STATE_SIG_R1|STATE_VISITED_R1|
                                    STATE_NZ_CTXT_R2|STATE_V_U_R2;
                                state[j-sscanw] |= STATE_NZ_CTXT_R2|
                                    STATE_V_D_R2;
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_L_R1|STATE_D_UL_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_R_R1|STATE_D_UR_R2;
                            }
                            // Update distortion
                            normval = (data[k] >> downshift) << upshift;
                            dist += fs[normval & ((1<<(MSE_LKP_BITS-1))-1)];
                        }
                        else {
                            csj |= STATE_VISITED_R1;
                        }
                    }
                    if (sheight < 4) {
                        state[j] = csj;
                        continue;
                    }
                    // Scan second row
                    if ((csj & (STATE_SIG_R2|STATE_NZ_CTXT_R2)) ==
                        STATE_NZ_CTXT_R2) {
                        k += dscanw;
                        // Apply zero coding
                        ctxtbuf[nsym] = zc_lut[(csj>>>STATE_SEP)&ZC_MASK];
                        if ((symbuf[nsym++] = (data[k]&mask)>>>bp) != 0) {
                            // Became significant
                            // Apply sign coding
                            sym = data[k]>>>31;
                            ctxt = SC_LUT[(csj>>>SC_SHIFT_R2)&SC_MASK];
                            symbuf[nsym] = sym ^ (ctxt>>>SC_SPRED_SHIFT);
                            ctxtbuf[nsym++] = ctxt & SC_LUT_MASK;
                            // Update state information (significant bit,
                            // visited bit, neighbor significant bit of
                            // neighbors, non zero context of neighbors, sign
                            // of neighbors)
                            state[j+off_dl] |= STATE_NZ_CTXT_R1|STATE_D_UR_R1;
                            state[j+off_dr] |= STATE_NZ_CTXT_R1|STATE_D_UL_R1;
                            // Update sign state information of neighbors
                            if (sym != 0) {
                                csj |= STATE_SIG_R2|STATE_VISITED_R2|
                                    STATE_NZ_CTXT_R1|
                                    STATE_V_D_R1|STATE_V_D_SIGN_R1;
                                state[j+sscanw] |= STATE_NZ_CTXT_R1|
                                    STATE_V_U_R1|STATE_V_U_SIGN_R1;
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DL_R1|
                                    STATE_H_L_R2|STATE_H_L_SIGN_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DR_R1|
                                    STATE_H_R_R2|STATE_H_R_SIGN_R2;
                            }
                            else {
                                csj |= STATE_SIG_R2|STATE_VISITED_R2|
                                    STATE_NZ_CTXT_R1|STATE_V_D_R1;
                                state[j+sscanw] |= STATE_NZ_CTXT_R1|
                                    STATE_V_U_R1;
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DL_R1|STATE_H_L_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DR_R1|STATE_H_R_R2;
                            }
                            // Update distortion
                            normval = (data[k] >> downshift) << upshift;
                            dist += fs[normval & ((1<<(MSE_LKP_BITS-1))-1)];
                        }
                        else {
                            csj |= STATE_VISITED_R2;
                        }
                    }
                    state[j] = csj;
                }
            }
            // Code all buffered symbols
            mq.codeSymbols(symbuf,ctxtbuf,nsym);
        }

        // Reset the MQ context states if we need to
        if ((options & OPT_RESET_MQ) != 0) {
            mq.resetCtxts();
        }

        // Terminate the MQ bit stream if we need to
        if (doterm) {
            ratebuf[pidx] = mq.terminate(); // Termination has special length
        }
        else { // Use normal length calculation
            ratebuf[pidx] = mq.getNumCodedBytes();
        }
        // Add length of previous segments, if any
        if (ltpidx >=0) {
            ratebuf[pidx] += ratebuf[ltpidx];
        }
        // Finish length calculation if needed
        if (doterm) {
            mq.finishLengthCalculation(ratebuf,pidx);
        }

        // Return the reduction in distortion
        return dist;
    }

    /**
     * Performs the significance propagation pass on the specified data and
     * bit-plane, without using the arithmetic coder. It codes all
     * insignificant samples which have, at least, one of its immediate eight
     * neighbors already significant, using the ZC and SC primitives as
     * needed. It toggles the "visited" state bit to 1 for all those samples.
     *
     * <P>In this method, the arithmetic coder is bypassed, and raw bits are
     * directly written in the bit stream (useful when distribution are close
     * to uniform, for intance, at high bit-rates and at lossless
     * compression).
     *
     * @param srcblk The code-block data to code
     *
     * @param bout The bit based output
     *
     * @param doterm If true the bit based output is byte aligned after the
     * end of the pass.
     *
     * @param bp The bit-plane to code
     *
     * @param state The state information for the code-block
     *
     * @param fs The distortion estimation lookup table for SC
     *
     * @param ratebuf The buffer where to store the rate (i.e. coded lenth) at
     * the end of this coding pass.
     *
     * @param pidx The coding pass index. Is the index in the 'ratebuf' array
     * where to store the coded length after this coding pass.
     *
     * @param ltpidx The index of the last pass that was terminated, or
     * negative if none.
     *
     * @param options The bitmask of entropy coding options to apply to the
     * code-block
     *
     * @return The decrease in distortion for this pass, in the fixed-point
     * normalized representation of the 'FS_LOSSY' and 'FS_LOSSLESS' tables.
     * */
    static private int rawSigProgPass(CBlkWTData srcblk, BitToByteOutput bout,
                                      boolean doterm, int bp, int state[],
                                      int fs[], int ratebuf[], int pidx,
                                      int ltpidx, int options) {
        int j,sj;        // The state index for line and stripe
        int k,sk;        // The data index for line and stripe
        int dscanw;      // The data scan-width
        int sscanw;      // The state scan-width
        int jstep;       // Stripe to stripe step for 'sj'
        int kstep;       // Stripe to stripe step for 'sk'
        int stopsk;      // The loop limit on the variable sk
        int csj;         // Local copy (i.e. cached) of 'state[j]'
        int mask;        // The mask for the current bit-plane
        int nsym = 0;    // Number of symbol
        int sym;         // The symbol to code
        int data[];      // The data buffer
        int dist;        // The distortion reduction for this pass
        int shift;       // Shift amount for distortion
        int upshift;     // Shift left amount for distortion
        int downshift;   // Shift right amount for distortion
        int normval;     // The normalized sample magnitude value
        int s;           // The stripe index
        boolean causal;  // Flag to indicate if stripe-causal context
                         // formation is to be used
        int nstripes;    // The number of stripes in the code-block
        int sheight;     // Height of the current stripe
        int off_ul,off_ur,off_dr,off_dl; // offsets

        // Initialize local variables
        dscanw = srcblk.scanw;
        sscanw = srcblk.w+2;
        jstep = sscanw*STRIPE_HEIGHT/2-srcblk.w;
        kstep = dscanw*STRIPE_HEIGHT-srcblk.w;
        mask = 1<<bp;
        data = (int[]) srcblk.getData();
        nstripes = (srcblk.h+STRIPE_HEIGHT-1)/STRIPE_HEIGHT;
        dist = 0;
        // We use the MSE_LKP_BITS-1 bits below the bit just coded for
        // distortion estimation.
        shift = bp-(MSE_LKP_BITS-1);
        upshift = (shift>=0) ? 0 : -shift;
        downshift = (shift<=0) ? 0 : shift;
        causal = (options & OPT_VERT_STR_CAUSAL) != 0;

        // Pre-calculate offsets in 'state' for neighbors
        off_ul = -sscanw-1;  // up-left
        off_ur =  -sscanw+1; // up-right
        off_dr = sscanw+1;   // down-right
        off_dl = sscanw-1;   // down-left

        // Code stripe by stripe
        sk = srcblk.offset;
        sj = sscanw+1;
        for (s = nstripes-1; s >= 0; s--, sk+=kstep, sj+=jstep) {
            sheight = (s != 0) ? STRIPE_HEIGHT :
                srcblk.h-(nstripes-1)*STRIPE_HEIGHT;
            stopsk = sk+srcblk.w;
            // Scan by set of 1 stripe column at a time
            for (; sk < stopsk; sk++, sj++) {
                // Do half top of column
                j = sj;
                csj = state[j];
                // If any of the two samples is not significant and has a
                // non-zero context (i.e. some neighbor is significant) we can
                // not skip them
                if ((((~csj) & (csj<<2)) & SIG_MASK_R1R2) != 0) {
                    k = sk;
                    // Scan first row
                    if ((csj & (STATE_SIG_R1|STATE_NZ_CTXT_R1)) ==
                        STATE_NZ_CTXT_R1) {
                        // Apply zero coding
                        sym = (data[k]&mask)>>>bp;
                        bout.writeBit(sym);
			nsym++;

                        if (sym != 0) {
                            // Became significant
                            // Apply sign coding
                            sym = data[k]>>>31;
                            bout.writeBit(sym);
			    nsym++;

                            // Update state information (significant bit,
                            // visited bit, neighbor significant bit of
                            // neighbors, non zero context of neighbors, sign
                            // of neighbors)
                            if (!causal) {
                                // If in causal mode do not change contexts of
                                // previous stripe.
                                state[j+off_ul] |=
                                    STATE_NZ_CTXT_R2|STATE_D_DR_R2;
                                state[j+off_ur] |=
                                    STATE_NZ_CTXT_R2|STATE_D_DL_R2;
                            }
                            // Update sign state information of neighbors
                            if (sym != 0) {
                                csj |= STATE_SIG_R1|STATE_VISITED_R1|
                                    STATE_NZ_CTXT_R2|
                                    STATE_V_U_R2|STATE_V_U_SIGN_R2;
                                if (!causal) {
                                    // If in causal mode do not change
                                    // contexts of previous stripe.
                                    state[j-sscanw] |= STATE_NZ_CTXT_R2|
                                        STATE_V_D_R2|STATE_V_D_SIGN_R2;
                                }
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_L_R1|STATE_H_L_SIGN_R1|
                                    STATE_D_UL_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_R_R1|STATE_H_R_SIGN_R1|
                                    STATE_D_UR_R2;
                            }
                            else {
                                csj |= STATE_SIG_R1|STATE_VISITED_R1|
                                    STATE_NZ_CTXT_R2|STATE_V_U_R2;
                                if (!causal) {
                                    // If in causal mode do not change
                                    // contexts of previous stripe.
                                    state[j-sscanw] |= STATE_NZ_CTXT_R2|
                                        STATE_V_D_R2;
                                }
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_L_R1|STATE_D_UL_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_R_R1|STATE_D_UR_R2;
                            }
                            // Update distortion
                            normval = (data[k] >> downshift) << upshift;
                            dist += fs[normval & ((1<<(MSE_LKP_BITS-1))-1)];
                        }
                        else {
                            csj |= STATE_VISITED_R1;
                        }
                    }
                    if (sheight < 2) {
                        state[j] = csj;
                        continue;
                    }
                    // Scan second row
                    if ((csj & (STATE_SIG_R2|STATE_NZ_CTXT_R2)) ==
                        STATE_NZ_CTXT_R2) {
                        k += dscanw;
                        // Apply zero coding
                        sym = (data[k]&mask)>>>bp;
                        bout.writeBit(sym);
			nsym++;
                        if (sym != 0) {
                            // Became significant
                            // Apply sign coding
                            sym = data[k]>>>31;
                            bout.writeBit(sym);
			    nsym++;
                            // Update state information (significant bit,
                            // visited bit, neighbor significant bit of
                            // neighbors, non zero context of neighbors, sign
                            // of neighbors)
                            state[j+off_dl] |= STATE_NZ_CTXT_R1|STATE_D_UR_R1;
                            state[j+off_dr] |= STATE_NZ_CTXT_R1|STATE_D_UL_R1;
                            // Update sign state information of neighbors
                            if (sym != 0) {
                                csj |= STATE_SIG_R2|STATE_VISITED_R2|
                                    STATE_NZ_CTXT_R1|
                                    STATE_V_D_R1|STATE_V_D_SIGN_R1;
                                state[j+sscanw] |= STATE_NZ_CTXT_R1|
                                    STATE_V_U_R1|STATE_V_U_SIGN_R1;
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DL_R1|
                                    STATE_H_L_R2|STATE_H_L_SIGN_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DR_R1|
                                    STATE_H_R_R2|STATE_H_R_SIGN_R2;
                            }
                            else {
                                csj |= STATE_SIG_R2|STATE_VISITED_R2|
                                    STATE_NZ_CTXT_R1|STATE_V_D_R1;
                                state[j+sscanw] |= STATE_NZ_CTXT_R1|
                                    STATE_V_U_R1;
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DL_R1|STATE_H_L_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DR_R1|STATE_H_R_R2;
                            }
                            // Update distortion
                            normval = (data[k] >> downshift) << upshift;
                            dist += fs[normval & ((1<<(MSE_LKP_BITS-1))-1)];
                        }
                        else {
                            csj |= STATE_VISITED_R2;
                        }
                    }
                    state[j] = csj;
                }
                // Do half bottom of column
                if (sheight < 3) continue;
                j += sscanw;
                csj = state[j];
                // If any of the two samples is not significant and has a
                // non-zero context (i.e. some neighbor is significant) we can
                // not skip them
                if ((((~csj) & (csj<<2)) & SIG_MASK_R1R2) != 0) {
                    k = sk+(dscanw<<1);
                    // Scan first row
                    if ((csj & (STATE_SIG_R1|STATE_NZ_CTXT_R1)) ==
                        STATE_NZ_CTXT_R1) {
                        sym = (data[k]&mask)>>>bp;
                        bout.writeBit(sym);
			nsym++;
                        if (sym != 0) {
                            // Became significant
                            // Apply sign coding
                            sym = data[k]>>>31;
                            bout.writeBit(sym);
			    nsym++;
                            // Update state information (significant bit,
                            // visited bit, neighbor significant bit of
                            // neighbors, non zero context of neighbors, sign
                            // of neighbors)
                            state[j+off_ul] |= STATE_NZ_CTXT_R2|STATE_D_DR_R2;
                            state[j+off_ur] |= STATE_NZ_CTXT_R2|STATE_D_DL_R2;
                            // Update sign state information of neighbors
                            if (sym != 0) {
                                csj |= STATE_SIG_R1|STATE_VISITED_R1|
                                    STATE_NZ_CTXT_R2|
                                    STATE_V_U_R2|STATE_V_U_SIGN_R2;
                                state[j-sscanw] |= STATE_NZ_CTXT_R2|
                                    STATE_V_D_R2|STATE_V_D_SIGN_R2;
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_L_R1|STATE_H_L_SIGN_R1|
                                    STATE_D_UL_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_R_R1|STATE_H_R_SIGN_R1|
                                    STATE_D_UR_R2;
                            }
                            else {
                                csj |= STATE_SIG_R1|STATE_VISITED_R1|
                                    STATE_NZ_CTXT_R2|STATE_V_U_R2;
                                state[j-sscanw] |= STATE_NZ_CTXT_R2|
                                    STATE_V_D_R2;
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_L_R1|STATE_D_UL_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_R_R1|STATE_D_UR_R2;
                            }
                            // Update distortion
                            normval = (data[k] >> downshift) << upshift;
                            dist += fs[normval & ((1<<(MSE_LKP_BITS-1))-1)];
                        }
                        else {
                            csj |= STATE_VISITED_R1;
                        }
                    }
                    if (sheight < 4) {
                        state[j] = csj;
                        continue;
                    }
                     if ((csj & (STATE_SIG_R2|STATE_NZ_CTXT_R2)) ==
                        STATE_NZ_CTXT_R2) {
                        k += dscanw;
                        // Apply zero coding
                        sym = (data[k]&mask)>>>bp;
                        bout.writeBit(sym);
			nsym++;
                        if (sym != 0) {
                            // Became significant
                            // Apply sign coding
                            sym = data[k]>>>31;
                            bout.writeBit(sym);
			    nsym++;
                            // Update state information (significant bit,
                            // visited bit, neighbor significant bit of
                            // neighbors, non zero context of neighbors, sign
                            // of neighbors)
                            state[j+off_dl] |= STATE_NZ_CTXT_R1|STATE_D_UR_R1;
                            state[j+off_dr] |= STATE_NZ_CTXT_R1|STATE_D_UL_R1;
                            // Update sign state information of neighbors
                            if (sym != 0) {
                                csj |= STATE_SIG_R2|STATE_VISITED_R2|
                                    STATE_NZ_CTXT_R1|
                                    STATE_V_D_R1|STATE_V_D_SIGN_R1;
                                state[j+sscanw] |= STATE_NZ_CTXT_R1|
                                    STATE_V_U_R1|STATE_V_U_SIGN_R1;
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DL_R1|
                                    STATE_H_L_R2|STATE_H_L_SIGN_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DR_R1|
                                    STATE_H_R_R2|STATE_H_R_SIGN_R2;
                            }
                            else {
                                csj |= STATE_SIG_R2|STATE_VISITED_R2|
                                    STATE_NZ_CTXT_R1|STATE_V_D_R1;
                                state[j+sscanw] |= STATE_NZ_CTXT_R1|
                                    STATE_V_U_R1;
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DL_R1|STATE_H_L_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DR_R1|STATE_H_R_R2;
                            }
                            // Update distortion
                            normval = (data[k] >> downshift) << upshift;
                            dist += fs[normval & ((1<<(MSE_LKP_BITS-1))-1)];
                        }
                        else {
                            csj |= STATE_VISITED_R2;
                        }
                    }
                    state[j] = csj;
                }
            }
        }

        // Get length and terminate if needed
        if (doterm) {
            ratebuf[pidx] = bout.terminate();
        } else {
            ratebuf[pidx] = bout.length();
        }       
        // Add length of previous segments, if any
        if (ltpidx >=0) {
            ratebuf[pidx] += ratebuf[ltpidx];
        }

        // Return the reduction in distortion
        return dist;
    }

    /**
     * Performs the magnitude refinement pass on the specified data and
     * bit-plane. It codes the samples which are significant and which do not
     * have the "visited" state bit turned on, using the MR primitive. The
     * "visited" state bit is not mofified for any samples.
     *
     * @param srcblk The code-block data to code
     *
     * @param mq The MQ-coder to use
     *
     * @param doterm If true it performs an MQ-coder termination after the end
     * of the pass
     *
     * @param bp The bit-plane to code
     *
     * @param state The state information for the code-block
     *
     * @param fm The distortion estimation lookup table for MR
     *
     * @param symbuf The buffer to hold symbols to send to the MQ coder
     *
     * @param ctxtbuf A buffer to hold the contexts to use in sending the
     * buffered symbols to the MQ coder.
     *
     * @param ratebuf The buffer where to store the rate (i.e. coded lenth) at
     * the end of this coding pass.
     *
     * @param pidx The coding pass index. Is the index in the 'ratebuf' array
     * where to store the coded length after this coding pass.
     *
     * @param ltpidx The index of the last pass that was terminated, or
     * negative if none.
     *
     * @param options The bitmask of entropy coding options to apply to the
     * code-block
     *
     * @return The decrease in distortion for this pass, in the fixed-point
     * normalized representation of the 'FS_LOSSY' and 'FS_LOSSLESS' tables.
     * */
    static private int magRefPass(CBlkWTData srcblk, MQCoder mq, boolean doterm,
                                  int bp, int state[], int fm[], int symbuf[],
                                  int ctxtbuf[], int ratebuf[], int pidx,
                                  int ltpidx, int options) {
        int j,sj;        // The state index for line and stripe
        int k,sk;        // The data index for line and stripe
        int nsym=0;        // Symbol counter for symbol and context buffers
        int dscanw;      // The data scan-width
        int sscanw;      // The state scan-width
        int jstep;       // Stripe to stripe step for 'sj'
        int kstep;       // Stripe to stripe step for 'sk'
        int stopsk;      // The loop limit on the variable sk
        int csj;         // Local copy (i.e. cached) of 'state[j]'
        int mask;        // The mask for the current bit-plane
        int data[];      // The data buffer
        int dist;        // The distortion reduction for this pass
        int shift;       // Shift amount for distortion
        int upshift;     // Shift left amount for distortion
        int downshift;   // Shift right amount for distortion
        int normval;     // The normalized sample magnitude value
        int s;           // The stripe index
        int nstripes;    // The number of stripes in the code-block
        int sheight;     // Height of the current stripe

        // Initialize local variables
        dscanw = srcblk.scanw;
        sscanw = srcblk.w+2;
        jstep = sscanw*STRIPE_HEIGHT/2-srcblk.w;
        kstep = dscanw*STRIPE_HEIGHT-srcblk.w;
        mask = 1<<bp;
        data = (int[]) srcblk.getData();
        nstripes = (srcblk.h+STRIPE_HEIGHT-1)/STRIPE_HEIGHT;
        dist = 0;
        // We use the bit just coded plus MSE_LKP_BITS-1 bits below the bit
        // just coded for distortion estimation.
        shift = bp-(MSE_LKP_BITS-1);
        upshift = (shift>=0) ? 0 : -shift;
        downshift = (shift<=0) ? 0 : shift;

        // Code stripe by stripe
        sk = srcblk.offset;
        sj = sscanw+1;
        for (s = nstripes-1; s >= 0; s--, sk+=kstep, sj+=jstep) {
            sheight = (s != 0) ? STRIPE_HEIGHT :
                srcblk.h-(nstripes-1)*STRIPE_HEIGHT;
            stopsk = sk+srcblk.w;
            // Scan by set of 1 stripe column at a time
            for (nsym = 0; sk < stopsk; sk++, sj++) {
                // Do half top of column
                j = sj;
                csj = state[j];
                // If any of the two samples is significant and not yet
                // visited in the current bit-plane we can not skip them
                if ((((csj >>> 1) & (~csj)) & VSTD_MASK_R1R2) != 0) {
                    k = sk;
                    // Scan first row
                    if ((csj & (STATE_SIG_R1|STATE_VISITED_R1)) ==
                        STATE_SIG_R1) {
                        // Apply MR primitive
                        symbuf[nsym] = (data[k]&mask)>>>bp;
                        ctxtbuf[nsym++] = MR_LUT[csj&MR_MASK];
                        // Update the STATE_PREV_MR bit
                        csj |= STATE_PREV_MR_R1;
                        // Update distortion
                        normval = (data[k] >> downshift) << upshift;
                        dist += fm[normval & ((1<<MSE_LKP_BITS)-1)];
                    }
                    if (sheight < 2) {
                        state[j] = csj;
                        continue;
                    }
                    // Scan second row
                    if ((csj & (STATE_SIG_R2|STATE_VISITED_R2)) ==
                        STATE_SIG_R2) {
                        k += dscanw;
                        // Apply MR primitive
                        symbuf[nsym] = (data[k]&mask)>>>bp;
                        ctxtbuf[nsym++] = MR_LUT[(csj>>>STATE_SEP)&MR_MASK];
                        // Update the STATE_PREV_MR bit
                        csj |= STATE_PREV_MR_R2;
                        // Update distortion
                        normval = (data[k] >> downshift) << upshift;
                        dist += fm[normval & ((1<<MSE_LKP_BITS)-1)];
                    }
                    state[j] = csj;
                }
                // Do half bottom of column
                if (sheight < 3) continue;
                j += sscanw;
                csj = state[j];
                // If any of the two samples is significant and not yet
                // visited in the current bit-plane we can not skip them
                if ((((csj >>> 1) & (~csj)) & VSTD_MASK_R1R2) != 0) {
                    k = sk+(dscanw<<1);
                    // Scan first row
                    if ((csj & (STATE_SIG_R1|STATE_VISITED_R1)) ==
                        STATE_SIG_R1) {
                        // Apply MR primitive
                        symbuf[nsym] = (data[k]&mask)>>>bp;
                        ctxtbuf[nsym++] = MR_LUT[csj&MR_MASK];
                        // Update the STATE_PREV_MR bit
                        csj |= STATE_PREV_MR_R1;
                        // Update distortion
                        normval = (data[k] >> downshift) << upshift;
                        dist += fm[normval & ((1<<MSE_LKP_BITS)-1)];
                    }
                    if (sheight < 4) {
                        state[j] = csj;
                        continue;
                    }
                    // Scan second row
                    if ((state[j] & (STATE_SIG_R2|STATE_VISITED_R2)) ==
                        STATE_SIG_R2) {
                        k += dscanw;
                        // Apply MR primitive
                        symbuf[nsym] = (data[k]&mask)>>>bp;
                        ctxtbuf[nsym++] = MR_LUT[(csj>>>STATE_SEP)&MR_MASK];
                        // Update the STATE_PREV_MR bit
                        csj |= STATE_PREV_MR_R2;
                        // Update distortion
                        normval = (data[k] >> downshift) << upshift;
                        dist += fm[normval & ((1<<MSE_LKP_BITS)-1)];
                    }
                    state[j] = csj;
                }
            }
            // Code all buffered symbols, if any
            if (nsym > 0) mq.codeSymbols(symbuf,ctxtbuf,nsym);
        }

        // Reset the MQ context states if we need to
        if ((options & OPT_RESET_MQ) != 0) {
            mq.resetCtxts();
        }

        // Terminate the MQ bit stream if we need to
        if (doterm) {
            ratebuf[pidx] = mq.terminate(); // Termination has special length
        }
        else { // Use normal length calculation
            ratebuf[pidx] = mq.getNumCodedBytes();
        }
        // Add length of previous segments, if any
        if (ltpidx >=0) {
            ratebuf[pidx] += ratebuf[ltpidx];
        }
        // Finish length calculation if needed
        if (doterm) {
            mq.finishLengthCalculation(ratebuf,pidx);
        }

        // Return the reduction in distortion
        return dist;
    }

    /**
     * Performs the magnitude refinement pass on the specified data and
     * bit-plane, without using the arithmetic coder. It codes the samples
     * which are significant and which do not have the "visited" state bit
     * turned on, using the MR primitive. The "visited" state bit is not
     * mofified for any samples.
     *
     * <P>In this method, the arithmetic coder is bypassed, and raw bits are
     * directly written in the bit stream (useful when distribution are close
     * to uniform, for intance, at high bit-rates and at lossless
     * compression). The 'STATE_PREV_MR_R1' and 'STATE_PREV_MR_R2' bits are
     * not set because they are used only when the arithmetic coder is not
     * bypassed.
     *
     * @param srcblk The code-block data to code
     *
     * @param bout The bit based output
     *
     * @param doterm If true the bit based output is byte aligned after the
     * end of the pass.
     *
     * @param bp The bit-plane to code
     *
     * @param state The state information for the code-block
     *
     * @param fm The distortion estimation lookup table for MR
     *
     * @param ratebuf The buffer where to store the rate (i.e. coded lenth) at
     * the end of this coding pass.
     *
     * @param pidx The coding pass index. Is the index in the 'ratebuf' array
     * where to store the coded length after this coding pass.
     *
     * @param ltpidx The index of the last pass that was terminated, or
     * negative if none.
     *
     * @param options The bitmask of entropy coding options to apply to the
     * code-block
     *
     * @return The decrease in distortion for this pass, in the fixed-point
     * normalized representation of the 'FS_LOSSY' and 'FS_LOSSLESS' tables.
     * */
    static private int rawMagRefPass(CBlkWTData srcblk, BitToByteOutput bout,
                                     boolean doterm, int bp, int state[],
                                     int fm[], int ratebuf[], int pidx,
                                     int ltpidx, int options) {
        int j,sj;        // The state index for line and stripe
        int k,sk;        // The data index for line and stripe
        int dscanw;      // The data scan-width
        int sscanw;      // The state scan-width
        int jstep;       // Stripe to stripe step for 'sj'
        int kstep;       // Stripe to stripe step for 'sk'
        int stopsk;      // The loop limit on the variable sk
        int csj;         // Local copy (i.e. cached) of 'state[j]'
        int mask;        // The mask for the current bit-plane
        int data[];      // The data buffer
        int dist;        // The distortion reduction for this pass
        int shift;       // Shift amount for distortion
        int upshift;     // Shift left amount for distortion
        int downshift;   // Shift right amount for distortion
        int normval;     // The normalized sample magnitude value
        int s;           // The stripe index
        int nstripes;    // The number of stripes in the code-block
        int sheight;     // Height of the current stripe
	int nsym = 0;

        // Initialize local variables
        dscanw = srcblk.scanw;
        sscanw = srcblk.w+2;
        jstep = sscanw*STRIPE_HEIGHT/2-srcblk.w;
        kstep = dscanw*STRIPE_HEIGHT-srcblk.w;
        mask = 1<<bp;
        data = (int[]) srcblk.getData();
        nstripes = (srcblk.h+STRIPE_HEIGHT-1)/STRIPE_HEIGHT;
        dist = 0;
        // We use the bit just coded plus MSE_LKP_BITS-1 bits below the bit
        // just coded for distortion estimation.
        shift = bp-(MSE_LKP_BITS-1);
        upshift = (shift>=0) ? 0 : -shift;
        downshift = (shift<=0) ? 0 : shift;

        // Code stripe by stripe
        sk = srcblk.offset;
        sj = sscanw+1;
        for (s = nstripes-1; s >= 0; s--, sk+=kstep, sj+=jstep) {
            sheight = (s != 0) ? STRIPE_HEIGHT :
                srcblk.h-(nstripes-1)*STRIPE_HEIGHT;
            stopsk = sk+srcblk.w;
            // Scan by set of 1 stripe column at a time
            for (; sk < stopsk; sk++, sj++) {
                // Do half top of column
                j = sj;
                csj = state[j];
                // If any of the two samples is significant and not yet
                // visited in the current bit-plane we can not skip them
                if ((((csj >>> 1) & (~csj)) & VSTD_MASK_R1R2) != 0) {
                    k = sk;
                    // Scan first row
                    if ((csj & (STATE_SIG_R1|STATE_VISITED_R1)) ==
                        STATE_SIG_R1) {
                        // Code bit "raw"
                        bout.writeBit((data[k]&mask)>>>bp);
			nsym++;
                        // No need to set STATE_PREV_MR_R1 since all magnitude
                        // refinement passes to follow are "raw"
                        // Update distortion
                        normval = (data[k] >> downshift) << upshift;
                        dist += fm[normval & ((1<<MSE_LKP_BITS)-1)];
                    }
                    if (sheight < 2) continue;
                    // Scan second row
                    if ((csj & (STATE_SIG_R2|STATE_VISITED_R2)) ==
                        STATE_SIG_R2) {
                        k += dscanw;
                        // Code bit "raw"
                        bout.writeBit((data[k]&mask)>>>bp);
			nsym++;
                        // No need to set STATE_PREV_MR_R2 since all magnitude
                        // refinement passes to follow are "raw"
                        // Update distortion
                        normval = (data[k] >> downshift) << upshift;
                        dist += fm[normval & ((1<<MSE_LKP_BITS)-1)];
                    }
                }
                // Do half bottom of column
                if (sheight < 3) continue;
                j += sscanw;
                csj = state[j];
                // If any of the two samples is significant and not yet
                // visited in the current bit-plane we can not skip them
                if ((((csj >>> 1) & (~csj)) & VSTD_MASK_R1R2) != 0) {
                    k = sk+(dscanw<<1);
                    // Scan first row
                    if ((csj & (STATE_SIG_R1|STATE_VISITED_R1)) ==
                        STATE_SIG_R1) {
                        // Code bit "raw"
                        bout.writeBit((data[k]&mask)>>>bp);
			nsym++;
                        // No need to set STATE_PREV_MR_R1 since all magnitude
                        // refinement passes to follow are "raw"
                        // Update distortion
                        normval = (data[k] >> downshift) << upshift;
                        dist += fm[normval & ((1<<MSE_LKP_BITS)-1)];
                    }
                    if (sheight < 4) continue;
                    // Scan second row
                    if ((state[j] & (STATE_SIG_R2|STATE_VISITED_R2)) ==
                        STATE_SIG_R2) {
                        k += dscanw;
                        // Code bit "raw"
                        bout.writeBit((data[k]&mask)>>>bp);
			nsym++;
                        // No need to set STATE_PREV_MR_R2 since all magnitude
                        // refinement passes to follow are "raw"
                        // Update distortion
                        normval = (data[k] >> downshift) << upshift;
                        dist += fm[normval & ((1<<MSE_LKP_BITS)-1)];
                    }
                }
            }
        }

        // Get length and terminate if needed
        if (doterm) {
            ratebuf[pidx] = bout.terminate();
        } else {
            ratebuf[pidx] = bout.length();
        }

        // Add length of previous segments, if any
        if (ltpidx >=0) {
            ratebuf[pidx] += ratebuf[ltpidx];
        }

        // Return the reduction in distortion
        return dist;
    }

    /**
     * Performs the cleanup pass on the specified data and bit-plane. It codes
     * all insignificant samples which have its "visited" state bit off, using
     * the ZC, SC, and RLC primitives. It toggles the "visited" state bit to 0
     * (off) for all samples in the code-block.
     *
     * @param srcblk The code-block data to code
     *
     * @param mq The MQ-coder to use
     *
     * @param doterm If true it performs an MQ-coder termination after the end
     * of the pass
     *
     * @param bp The bit-plane to code
     *
     * @param state The state information for the code-block
     *
     * @param fs The distortion estimation lookup table for SC
     *
     * @param zc_lut The ZC lookup table to use in ZC.
     *
     * @param symbuf The buffer to hold symbols to send to the MQ coder
     *
     * @param ctxtbuf A buffer to hold the contexts to use in sending the
     * buffered symbols to the MQ coder.
     *
     * @param ratebuf The buffer where to store the rate (i.e. coded lenth) at
     * the end of this coding pass.
     *
     * @param pidx The coding pass index. Is the index in the 'ratebuf' array
     * where to store the coded length after this coding pass.
     *
     * @param ltpidx The index of the last pass that was terminated, or
     * negative if none.
     *
     * @param options The bitmask of entropy coding options to apply to the
     * code-block
     *
     * @return The decrease in distortion for this pass, in the fixed-point
     * normalized representation of the 'FS_LOSSY' and 'FS_LOSSLESS' tables.
     * */
    static private int cleanuppass(CBlkWTData srcblk, MQCoder mq,
                                   boolean doterm, int bp, int state[],
                                   int fs[], int zc_lut[], int symbuf[],
                                   int ctxtbuf[], int ratebuf[], int pidx,
                                   int ltpidx, int options) {
        // NOTE: The speedup mode of the MQ coder has been briefly tried to
        // speed up the coding of insignificants RLCs, without any success
        // (i.e. no speedup whatsoever). The use of the speedup mode should be
        // revisisted more in depth and the implementationn of it in MQCoder
        // should be reviewed for optimization opportunities.
        int j,sj;        // The state index for line and stripe
        int k,sk;        // The data index for line and stripe
        int nsym=0;        // Symbol counter for symbol and context buffers
        int dscanw;      // The data scan-width
        int sscanw;      // The state scan-width
        int jstep;       // Stripe to stripe step for 'sj'
        int kstep;       // Stripe to stripe step for 'sk'
        int stopsk;      // The loop limit on the variable sk
        int csj;         // Local copy (i.e. cached) of 'state[j]'
        int mask;        // The mask for the current bit-plane
        int sym;         // The symbol to code
        int rlclen;      // Length of RLC
        int ctxt;        // The context to use
        int data[];      // The data buffer
        int dist;        // The distortion reduction for this pass
        int shift;       // Shift amount for distortion
        int upshift;     // Shift left amount for distortion
        int downshift;   // Shift right amount for distortion
        int normval;     // The normalized sample magnitude value
        int s;           // The stripe index
        boolean causal;  // Flag to indicate if stripe-causal context
                         // formation is to be used
        int nstripes;    // The number of stripes in the code-block
        int sheight;     // Height of the current stripe
        int off_ul,off_ur,off_dr,off_dl; // offsets

        // Initialize local variables
        dscanw = srcblk.scanw;
        sscanw = srcblk.w+2;
        jstep = sscanw*STRIPE_HEIGHT/2-srcblk.w;
        kstep = dscanw*STRIPE_HEIGHT-srcblk.w;
        mask = 1<<bp;
        data = (int[]) srcblk.getData();
        nstripes = (srcblk.h+STRIPE_HEIGHT-1)/STRIPE_HEIGHT;
        dist = 0;
        // We use the MSE_LKP_BITS-1 bits below the bit just coded for
        // distortion estimation.
        shift = bp-(MSE_LKP_BITS-1);
        upshift = (shift>=0) ? 0 : -shift;
        downshift = (shift<=0) ? 0 : shift;
        causal = (options & OPT_VERT_STR_CAUSAL) != 0;

        // Pre-calculate offsets in 'state' for diagonal neighbors
        off_ul = -sscanw-1; // up-left
        off_ur = -sscanw+1; // up-right
        off_dr = sscanw+1;  // down-right
        off_dl = sscanw-1;  // down-left

        // Code stripe by stripe
        sk = srcblk.offset;
        sj = sscanw+1;
        for (s = nstripes-1; s >= 0; s--, sk+=kstep, sj+=jstep) {
            sheight = (s != 0) ? STRIPE_HEIGHT :
                srcblk.h-(nstripes-1)*STRIPE_HEIGHT;
            stopsk = sk+srcblk.w;
            // Scan by set of 1 stripe column at a time
            for (nsym = 0; sk < stopsk; sk++, sj++) {
                // Start column
                j = sj;
                csj = state[j];
                boolean broken = false;
            top_half:
                {
                    // Check for RLC: if all samples are not significant, not
                    // visited and do not have a non-zero context, and column is
                    // full height, we do RLC.
                    if (csj == 0 && state[j+sscanw] == 0 &&
                        sheight == STRIPE_HEIGHT) {
                        k = sk;
                        if ((data[k]&mask) != 0) {
                            rlclen = 0;
                        }
                        else if ((data[k+=dscanw]&mask) != 0) {
                            rlclen = 1;
                        }
                        else if ((data[k+=dscanw]&mask) != 0) {
                            rlclen = 2;
                            j += sscanw;
                            csj = state[j];
                        }
                        else if ((data[k+=dscanw]&mask) != 0) {
                            rlclen = 3;
                            j += sscanw;
                            csj = state[j];
                        }
                        else {
                            // Code insignificant RLC
                            symbuf[nsym] = 0;
                            ctxtbuf[nsym++] = RLC_CTXT;
                            // Goto next column
                            continue;
                        }
                        // Code significant RLC
                        symbuf[nsym] = 1;
                        ctxtbuf[nsym++] = RLC_CTXT;
                        // Send MSB bit index
                        symbuf[nsym] = rlclen>>1;
                        ctxtbuf[nsym++] = UNIF_CTXT;
                        // Send LSB bit index
                        symbuf[nsym] = rlclen&0x01;
                        ctxtbuf[nsym++] = UNIF_CTXT;
                        // Code sign of sample that became significant
                        // Update distortion
                        normval = (data[k] >> downshift) << upshift;
                        dist += fs[normval & ((1<<(MSE_LKP_BITS-1))-1)];
                        // Apply sign coding
                        sym = data[k]>>>31;
                        if ((rlclen&0x01) == 0) {
                            // Sample that became significant is first row of
                            // its column half
                            ctxt = SC_LUT[(csj>>>SC_SHIFT_R1)&SC_MASK];
                            symbuf[nsym] = sym ^ (ctxt>>>SC_SPRED_SHIFT);
                            ctxtbuf[nsym++] = ctxt & SC_LUT_MASK;
                            // Update state information (significant bit,
                            // visited bit, neighbor significant bit of
                            // neighbors, non zero context of neighbors, sign
                            // of neighbors)
                            if (rlclen != 0 || !causal) {
                                // If in causal mode do not change contexts of
                                // previous stripe.
                                state[j+off_ul] |=
                                    STATE_NZ_CTXT_R2|STATE_D_DR_R2;
                                state[j+off_ur] |=
                                    STATE_NZ_CTXT_R2|STATE_D_DL_R2;
                            }
                            // Update sign state information of neighbors
                            if (sym != 0) {
                                csj |= STATE_SIG_R1|STATE_VISITED_R1|
                                    STATE_NZ_CTXT_R2|
                                    STATE_V_U_R2|STATE_V_U_SIGN_R2;
                                if (rlclen != 0 || !causal) {
                                    // If in causal mode do not change
                                    // contexts of previous stripe.
                                    state[j-sscanw] |= STATE_NZ_CTXT_R2|
                                        STATE_V_D_R2|STATE_V_D_SIGN_R2;
                                }
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_L_R1|STATE_H_L_SIGN_R1|
                                    STATE_D_UL_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_R_R1|STATE_H_R_SIGN_R1|
                                    STATE_D_UR_R2;
                            }
                            else {
                                csj |= STATE_SIG_R1|STATE_VISITED_R1|
                                    STATE_NZ_CTXT_R2|STATE_V_U_R2;
                                if (rlclen != 0 || !causal) {
                                    // If in causal mode do not change
                                    // contexts of previous stripe.
                                    state[j-sscanw] |= STATE_NZ_CTXT_R2|
                                        STATE_V_D_R2;
                                }
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_L_R1|STATE_D_UL_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_R_R1|STATE_D_UR_R2;
                            }
                            // Changes to csj are saved later
                            if ((rlclen>>1) != 0) {
                                // Sample that became significant is in bottom
                                // half of column => jump to bottom half
                                broken = true;
                            }
                            // Otherwise sample that became significant is in
                            // top half of column => continue on top half
                        }
                        else {
                            // Sample that became significant is second row of
                            // its column half
                            ctxt = SC_LUT[(csj>>>SC_SHIFT_R2)&SC_MASK];
                            symbuf[nsym] = sym ^ (ctxt>>>SC_SPRED_SHIFT);
                            ctxtbuf[nsym++] = ctxt & SC_LUT_MASK;
                            // Update state information (significant bit,
                            // neighbor significant bit of neighbors,
                            // non zero context of neighbors, sign of neighbors)
                            state[j+off_dl] |= STATE_NZ_CTXT_R1|STATE_D_UR_R1;
                            state[j+off_dr] |= STATE_NZ_CTXT_R1|STATE_D_UL_R1;
                            // Update sign state information of neighbors
                            if (sym != 0) {
                                csj |= STATE_SIG_R2|STATE_NZ_CTXT_R1|
                                    STATE_V_D_R1|STATE_V_D_SIGN_R1;
                                state[j+sscanw] |= STATE_NZ_CTXT_R1|
                                    STATE_V_U_R1|STATE_V_U_SIGN_R1;
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DL_R1|
                                    STATE_H_L_R2|STATE_H_L_SIGN_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DR_R1|
                                    STATE_H_R_R2|STATE_H_R_SIGN_R2;
                            }
                            else {
                                csj |= STATE_SIG_R2|STATE_NZ_CTXT_R1|
                                    STATE_V_D_R1;
                                state[j+sscanw] |= STATE_NZ_CTXT_R1|
                                    STATE_V_U_R1;
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DL_R1|STATE_H_L_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DR_R1|STATE_H_R_R2;
                            }
                            // Save changes to csj
                            state[j] = csj;
                            if ((rlclen>>1) != 0) {
                                // Sample that became significant is in bottom
                                // half of column => we're done with this
                                // column
                                continue;
                            }
                            // Otherwise sample that became significant is in
                            // top half of column => we're done with top
                            // column
                            j += sscanw;
                            csj = state[j];
                            broken = true;
                        }
                    }
                }
                if (!broken) {
                    // Do half top of column
                    // If any of the two samples is not significant and has
                    // not been visited in the current bit-plane we can not
                    // skip them
                    if ((((csj>>1)|csj) & VSTD_MASK_R1R2) != VSTD_MASK_R1R2) {
                        k = sk;
                        // Scan first row
                        if ((csj & (STATE_SIG_R1|STATE_VISITED_R1)) == 0) {
                            // Apply zero coding
                            ctxtbuf[nsym] = zc_lut[csj&ZC_MASK];
                            if ((symbuf[nsym++] = (data[k]&mask)>>>bp) != 0) {
                                // Became significant
                                // Apply sign coding
                                sym = data[k]>>>31;
                                ctxt = SC_LUT[(csj>>>SC_SHIFT_R1)&SC_MASK];
                                symbuf[nsym] = sym ^ (ctxt>>>SC_SPRED_SHIFT);
                                ctxtbuf[nsym++] = ctxt & SC_LUT_MASK;
                                // Update state information (significant bit,
                                // visited bit, neighbor significant bit of
                                // neighbors, non zero context of neighbors,
                                // sign of neighbors)
                                if (!causal) {
                                    // If in causal mode do not change
                                    // contexts of previous stripe.
                                    state[j+off_ul] |=
                                        STATE_NZ_CTXT_R2|STATE_D_DR_R2;
                                    state[j+off_ur] |=
                                        STATE_NZ_CTXT_R2|STATE_D_DL_R2;
                                }
                                // Update sign state information of neighbors
                                if (sym != 0) {
                                    csj |= STATE_SIG_R1|STATE_VISITED_R1|
                                        STATE_NZ_CTXT_R2|
                                        STATE_V_U_R2|STATE_V_U_SIGN_R2;
                                    if (!causal) {
                                        // If in causal mode do not change
                                        // contexts of previous stripe.
                                        state[j-sscanw] |= STATE_NZ_CTXT_R2|
                                            STATE_V_D_R2|STATE_V_D_SIGN_R2;
                                    }
                                    state[j+1] |=
                                        STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                        STATE_H_L_R1|STATE_H_L_SIGN_R1|
                                        STATE_D_UL_R2;
                                    state[j-1] |=
                                        STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                        STATE_H_R_R1|STATE_H_R_SIGN_R1|
                                        STATE_D_UR_R2;
                                }
                                else {
                                    csj |= STATE_SIG_R1|STATE_VISITED_R1|
                                        STATE_NZ_CTXT_R2|STATE_V_U_R2;
                                    if (!causal) {
                                        // If in causal mode do not change
                                        // contexts of previous stripe.
                                        state[j-sscanw] |= STATE_NZ_CTXT_R2|
                                            STATE_V_D_R2;
                                    }
                                    state[j+1] |=
                                        STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                        STATE_H_L_R1|STATE_D_UL_R2;
                                    state[j-1] |=
                                        STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                        STATE_H_R_R1|STATE_D_UR_R2;
                                }
                                // Update distortion
                                normval = (data[k] >> downshift) << upshift;
                                dist += fs[normval & ((1<<(MSE_LKP_BITS-1))-1)];
                            }
                        }
                        if (sheight < 2) {
                            csj &= ~(STATE_VISITED_R1|STATE_VISITED_R2);
                            state[j] = csj;
                            continue;
                        }
                        // Scan second row
                        if ((csj & (STATE_SIG_R2|STATE_VISITED_R2)) == 0) {
                            k += dscanw;
                            // Apply zero coding
                            ctxtbuf[nsym] = zc_lut[(csj>>>STATE_SEP)&ZC_MASK];
                            if ((symbuf[nsym++] = (data[k]&mask)>>>bp) != 0) {
                                // Became significant
                                // Apply sign coding
                                sym = data[k]>>>31;
                                ctxt = SC_LUT[(csj>>>SC_SHIFT_R2)&SC_MASK];
                                symbuf[nsym] = sym ^ (ctxt>>>SC_SPRED_SHIFT);
                                ctxtbuf[nsym++] = ctxt & SC_LUT_MASK;
                                // Update state information (significant bit,
                                // visited bit, neighbor significant bit of
                                // neighbors, non zero context of neighbors,
                                // sign of neighbors)
                                state[j+off_dl] |=
                                    STATE_NZ_CTXT_R1|STATE_D_UR_R1;
                                state[j+off_dr] |=
                                    STATE_NZ_CTXT_R1|STATE_D_UL_R1;
                                // Update sign state information of neighbors
                                if (sym != 0) {
                                    csj |= STATE_SIG_R2|STATE_VISITED_R2|
                                        STATE_NZ_CTXT_R1|
                                        STATE_V_D_R1|STATE_V_D_SIGN_R1;
                                    state[j+sscanw] |= STATE_NZ_CTXT_R1|
                                        STATE_V_U_R1|STATE_V_U_SIGN_R1;
                                    state[j+1] |=
                                        STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                        STATE_D_DL_R1|
                                        STATE_H_L_R2|STATE_H_L_SIGN_R2;
                                    state[j-1] |=
                                        STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                        STATE_D_DR_R1|
                                        STATE_H_R_R2|STATE_H_R_SIGN_R2;
                                }
                                else {
                                    csj |= STATE_SIG_R2|STATE_VISITED_R2|
                                        STATE_NZ_CTXT_R1|STATE_V_D_R1;
                                    state[j+sscanw] |= STATE_NZ_CTXT_R1|
                                        STATE_V_U_R1;
                                    state[j+1] |=
                                        STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                        STATE_D_DL_R1|STATE_H_L_R2;
                                    state[j-1] |=
                                        STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                        STATE_D_DR_R1|STATE_H_R_R2;
                                }
                                // Update distortion
                                normval = (data[k] >> downshift) << upshift;
                                dist += fs[normval & ((1<<(MSE_LKP_BITS-1))-1)];
                            }
                        }
                    }
                    csj &= ~(STATE_VISITED_R1|STATE_VISITED_R2);
                    state[j] = csj;
                    // Do half bottom of column
                    if (sheight < 3) continue;
                    j += sscanw;
                    csj = state[j];
                } // end of 'top_half' block
                // If any of the two samples is not significant and has
                // not been visited in the current bit-plane we can not
                // skip them
                if ((((csj>>1)|csj) & VSTD_MASK_R1R2) != VSTD_MASK_R1R2) {
                    k = sk+(dscanw<<1);
                    // Scan first row
                    if ((csj & (STATE_SIG_R1|STATE_VISITED_R1)) == 0) {
                        // Apply zero coding
                        ctxtbuf[nsym] = zc_lut[csj&ZC_MASK];
                        if ((symbuf[nsym++] = (data[k]&mask)>>>bp) != 0) {
                            // Became significant
                            // Apply sign coding
                            sym = data[k]>>>31;
                            ctxt = SC_LUT[(csj>>>SC_SHIFT_R1)&SC_MASK];
                            symbuf[nsym] = sym ^ (ctxt>>>SC_SPRED_SHIFT);
                            ctxtbuf[nsym++] = ctxt & SC_LUT_MASK;
                            // Update state information (significant bit,
                            // visited bit, neighbor significant bit of
                            // neighbors, non zero context of neighbors,
                            // sign of neighbors)
                            state[j+off_ul] |=
                                STATE_NZ_CTXT_R2|STATE_D_DR_R2;
                            state[j+off_ur] |=
                                STATE_NZ_CTXT_R2|STATE_D_DL_R2;
                            // Update sign state information of neighbors
                            if (sym != 0) {
                                csj |= STATE_SIG_R1|STATE_VISITED_R1|
                                    STATE_NZ_CTXT_R2|
                                    STATE_V_U_R2|STATE_V_U_SIGN_R2;
                                state[j-sscanw] |= STATE_NZ_CTXT_R2|
                                    STATE_V_D_R2|STATE_V_D_SIGN_R2;
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_L_R1|STATE_H_L_SIGN_R1|
                                    STATE_D_UL_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_R_R1|STATE_H_R_SIGN_R1|
                                    STATE_D_UR_R2;
                            }
                            else {
                                csj |= STATE_SIG_R1|STATE_VISITED_R1|
                                    STATE_NZ_CTXT_R2|STATE_V_U_R2;
                                state[j-sscanw] |= STATE_NZ_CTXT_R2|
                                    STATE_V_D_R2;
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_L_R1|STATE_D_UL_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_H_R_R1|STATE_D_UR_R2;
                            }
                            // Update distortion
                            normval = (data[k] >> downshift) << upshift;
                            dist += fs[normval & ((1<<(MSE_LKP_BITS-1))-1)];
                        }
                    }
                    if (sheight < 4) {
                        csj &= ~(STATE_VISITED_R1|STATE_VISITED_R2);
                        state[j] = csj;
                        continue;
                    }
                    // Scan second row
                    if ((csj & (STATE_SIG_R2|STATE_VISITED_R2)) == 0) {
                        k += dscanw;
                        // Apply zero coding
                        ctxtbuf[nsym] = zc_lut[(csj>>>STATE_SEP)&ZC_MASK];
                        if ((symbuf[nsym++] = (data[k]&mask)>>>bp) != 0) {
                            // Became significant
                            // Apply sign coding
                            sym = data[k]>>>31;
                            ctxt = SC_LUT[(csj>>>SC_SHIFT_R2)&SC_MASK];
                            symbuf[nsym] = sym ^ (ctxt>>>SC_SPRED_SHIFT);
                            ctxtbuf[nsym++] = ctxt & SC_LUT_MASK;
                            // Update state information (significant bit,
                            // visited bit, neighbor significant bit of
                            // neighbors, non zero context of neighbors,
                            // sign of neighbors)
                            state[j+off_dl] |=
                                STATE_NZ_CTXT_R1|STATE_D_UR_R1;
                            state[j+off_dr] |=
                                STATE_NZ_CTXT_R1|STATE_D_UL_R1;
                            // Update sign state information of neighbors
                            if (sym != 0) {
                                csj |= STATE_SIG_R2|STATE_VISITED_R2|
                                    STATE_NZ_CTXT_R1|
                                    STATE_V_D_R1|STATE_V_D_SIGN_R1;
                                state[j+sscanw] |= STATE_NZ_CTXT_R1|
                                    STATE_V_U_R1|STATE_V_U_SIGN_R1;
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DL_R1|
                                    STATE_H_L_R2|STATE_H_L_SIGN_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DR_R1|
                                    STATE_H_R_R2|STATE_H_R_SIGN_R2;
                            }
                            else {
                                csj |= STATE_SIG_R2|STATE_VISITED_R2|
                                    STATE_NZ_CTXT_R1|STATE_V_D_R1;
                                state[j+sscanw] |= STATE_NZ_CTXT_R1|
                                    STATE_V_U_R1;
                                state[j+1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DL_R1|STATE_H_L_R2;
                                state[j-1] |=
                                    STATE_NZ_CTXT_R1|STATE_NZ_CTXT_R2|
                                    STATE_D_DR_R1|STATE_H_R_R2;
                            }
                            // Update distortion
                            normval = (data[k] >> downshift) << upshift;
                            dist += fs[normval & ((1<<(MSE_LKP_BITS-1))-1)];
                        }
                    }
                }
                csj &= ~(STATE_VISITED_R1|STATE_VISITED_R2);
                state[j] = csj;
            }
            // Code all buffered symbols, if any
            if (nsym > 0) mq.codeSymbols(symbuf,ctxtbuf,nsym);
        }

        // Insert a segment marker if we need to
        if ((options & OPT_SEG_SYMBOLS) != 0) {
            mq.codeSymbols(SEG_SYMBOLS,SEG_SYMB_CTXTS,SEG_SYMBOLS.length);
        }

        // Reset the MQ context states if we need to
        if ((options & OPT_RESET_MQ) != 0) {
            mq.resetCtxts();
        }

        // Terminate the MQ bit stream if we need to
        if (doterm) {
            ratebuf[pidx] = mq.terminate(); // Termination has special length
        }
        else { // Use normal length calculation
            ratebuf[pidx] = mq.getNumCodedBytes();
        }
        // Add length of previous segments, if any
        if (ltpidx >=0) {
            ratebuf[pidx] += ratebuf[ltpidx];
        }
        // Finish length calculation if needed
        if (doterm) {
            mq.finishLengthCalculation(ratebuf,pidx);
        }

        // Return the reduction in distortion
        return dist;
    }

    /**
     * Ensures that at the end of a non-terminated coding pass there is not a
     * 0xFF byte, modifying the stored rates if necessary.
     *
     * <P>Due to error resiliance reasons, a coding pass should never have its
     * last byte be a 0xFF, since that can lead to the emulation of a resync
     * marker. This method checks if that is the case, and reduces the rate
     * for a given pass if necessary. The ommitted 0xFF will be synthetized by
     * the decoder if necessary, as required by JPEG 2000. This method should
     * only be called once that the entire code-block is coded.
     *
     * <P>Passes that are terminated are not checked for the 0xFF byte, since
     * it is assumed that the termination procedure does not output any
     * trailing 0xFF. Checking the terminated segments would involve much more
     * than just modifying the stored rates.
     *
     * <P>NOTE: It is assumed by this method that the coded data does not
     * contain consecutive 0xFF bytes, as is the case with the MQ and
     * 'arithemetic coding bypass' bit stuffing policy. However, the
     * termination policies used should also respect this requirement.
     *
     * @param data The coded data for the code-block
     *
     * @param rates The rate (i.e. accumulated number of bytes) for each
     * coding pass
     *
     * @param isterm An array of flags indicating, for each pass, if it is
     * terminated or not. If null it is assumed that no pass is terminated,
     * except the last one.
     *
     * @param n The number of coding passes
     * */
    static private void checkEndOfPassFF(byte data[], int rates[],
                                         boolean isterm[], int n) {
        int dp;  // the position to test in 'data'

        // If a pass ends in 0xFF we need to reduce the number of bytes in it,
        // so that it does not end in 0xFF. We only need to go back one byte
        // since there can be no consecutive 0xFF bytes.

        // If there are no terminated passes avoid the test on 'isterm'
        if (isterm == null) {
            for (n--; n>=0; n--) {
                dp = rates[n]-1;
                if (dp >= 0 && (data[dp] == (byte)0xFF)) {
                    rates[n]--;
                }
            }
        }
        else {
            for (n--; n>=0; n--) {
                if (!isterm[n]) {
                    dp = rates[n]-1;
                    if (dp >= 0 && (data[dp] == (byte)0xFF)) {
                        rates[n]--;
                    }
                }
            }
        }
    }

    /**
     * Load options, length calculation type and termination type for
     * each tile-component.
     *
     * @param nt The number of tiles
     *
     * @param nc The number of components
     * */
    public void initTileComp(int nt, int nc) {

        opts    = new int[nt][nc];
        lenCalc = new int[nt][nc];
        tType   = new int[nt][nc];

        for(int t=0; t<nt; t++){
            for(int c=0; c<nc; c++){
                opts[t][c] = 0;

                // Bypass coding mode ?
                if( ((String)bms.getTileCompVal(t,c)).
                    equalsIgnoreCase("true")) {
                    opts[t][c] |= OPT_BYPASS;
                }
                // MQ reset after each coding pass ?
                if( ((String)mqrs.getTileCompVal(t,c)).
                    equalsIgnoreCase("true")) {
                    opts[t][c] |= OPT_RESET_MQ;
                }
                // MQ termination after each arithmetically coded coding pass ?
                if(  ((String)rts.getTileCompVal(t,c)).
                     equalsIgnoreCase("true") ) {
                    opts[t][c] |= OPT_TERM_PASS;
                }
                // Vertically stripe-causal context mode ?
                if(  ((String)css.getTileCompVal(t,c)).
                     equalsIgnoreCase("true") ) {
                    opts[t][c] |= OPT_VERT_STR_CAUSAL;
                }
                // Error resilience segmentation symbol insertion ?
                if(  ((String)sss.getTileCompVal(t,c)).
                     equalsIgnoreCase("true")) {
                    opts[t][c] |= OPT_SEG_SYMBOLS;
                }

                // Set length calculation type of the MQ coder
                String lCalcType = (String)lcs.getTileCompVal(t,c);
                if(lCalcType.equals("near_opt")){
                    lenCalc[t][c] = MQCoder.LENGTH_NEAR_OPT;
                }
                else if(lCalcType.equals("lazy_good")){
                    lenCalc[t][c] = MQCoder.LENGTH_LAZY_GOOD;
                }
                else if(lCalcType.equals("lazy")){
                    lenCalc[t][c] = MQCoder.LENGTH_LAZY;
                }
                else {
                    throw new IllegalArgumentException("Unrecognized or "+
                                                       "unsupported MQ "+
                                                       "length calculation.");
                }

                // Set termination type of MQ coder
                String termType = (String)tts.getTileCompVal(t,c);
                if(termType.equalsIgnoreCase("easy")){
                    tType[t][c] = MQCoder.TERM_EASY;
                }
                else if(termType.equalsIgnoreCase("full")){
                    tType[t][c] = MQCoder.TERM_FULL;
                }
                else if(termType.equalsIgnoreCase("near_opt")){
                    tType[t][c] = MQCoder.TERM_NEAR_OPT;
                }
                else if (termType.equalsIgnoreCase("predict")) {
                    tType[t][c] = MQCoder.TERM_PRED_ER;
                    opts[t][c] |= OPT_PRED_TERM;
                    if ((opts[t][c] & (OPT_TERM_PASS|OPT_BYPASS)) == 0) {
                        FacilityManager.getMsgLogger().
                            printmsg(MsgLogger.INFO,"Using error resilient MQ"+
                                     " termination, but terminating only at "+
				     "the end of code-blocks. The error "+
				     "protection offered by this option will"+
				     " be very weak. Specify the 'Creg_term' "+
                                     "and/or 'Cbypass' option for "+
                                     "increased error resilience.");
                    }
                }
                else{
                    throw new IllegalArgumentException("Unrecognized or "+
                                                       "unsupported "+
                                                       "MQ coder termination.");
                }

            } // End loop on components
        } // End loop on tiles
    }

    /**
     * Returns the precinct partition width for the specified
     * component, tile and resolution level.
     *
     * @param t the tile index
     *
     * @param c the component
     *
     * @param rl the resolution level
     *
     * @return The precinct partition width for the specified
     * component, tile and resolution level
     * */
    public int getPPX(int t, int c, int rl) {
        return pss.getPPX(t, c, rl);
    }

    /**
     * Returns the precinct partition height for the specified
     * component, tile and resolution level.
     *
     * @param t the tile index
     *
     * @param c the component
     *
     * @param rl the resolution level
     *
     * @return The precinct partition height for the specified
     * component, tile and resolution level
     * */
    public int getPPY(int t, int c, int rl) {
        return pss.getPPY(t, c, rl);
    }

    /**
     * Returns true if precinct partition is used for the specified
     * component and tile, returns false otherwise.
     *
     * @param c The component
     *
     * @param t The tile
     *
     * @return True if precinct partition is used for the specified
     * component and tile, returns false otherwise.
     * */
    public boolean precinctPartitionUsed(int c, int t) {
        return precinctPartition[c][t];
    }
}
