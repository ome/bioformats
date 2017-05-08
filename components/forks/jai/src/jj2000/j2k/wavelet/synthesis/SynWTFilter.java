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
 * $RCSfile: SynWTFilter.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:34 $
 * $State: Exp $
 *
 * Class:                   SynWTFilter
 *
 * Description:             The abstract class for all synthesis wavelet
 *                          filters.
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


package jj2000.j2k.wavelet.synthesis;

import jj2000.j2k.wavelet.*;
import jj2000.j2k.codestream.*;
import jj2000.j2k.io.*;
import jj2000.j2k.*;
import java.io.*;

/**
 * This abstract class defines the methods of all synthesis wavelet
 * filters. Specialized abstract classes that work on particular data
 * types (int, float) provide more specific method calls while
 * retaining the generality of this one. See the SynWTFilterInt
 * and SynWTFilterFloat classes. Implementations of snythesis
 * filters should inherit from one of those classes.
 *
 * <P>The length of the output signal is always the sum of the length
 * of the low-pass and high-pass input signals.
 *
 * <P>All synthesis wavelet filters should follow the following conventions:
 *
 * <P>- The first sample of the output corresponds to the low-pass
 * one. As a consequence, if the output signal is of odd-length then
 * the low-pass input signal is one sample longer than the high-pass
 * input one. Therefore, if the length of output signal is N, the
 * low-pass input signal is of length N/2 if N is even and N/2+1/2 if
 * N is odd, while the high-pass input signal is of length N/2 if N
 * is even and N/2-1/2 if N is odd.
 *
 * <P>- The normalization of the analysis filters is 1 for the DC gain
 * and 2 for the Nyquist gain (Type I normalization), for both
 * reversible and non-reversible filters. The normalization of the
 * synthesis filters should ensure prefect reconstruction according to
 * this normalization of the analysis wavelet filters.
 *
 * <P>The synthetize method may seem very complicated, but is designed to
 * minimize the amount of data copying and redundant calculations when
 * used for block-based or line-based wavelet transform
 * implementations, while being applicable to full-frame transforms as
 * well.
 *
 * @see SynWTFilterInt
 *
 * @see SynWTFilterFloat
 * */
public abstract class SynWTFilter implements WaveletFilter,
    Markers {

    /**
     * Reconstructs the output signal by the synthesis filter,
     * recomposing the low-pass and high-pass input signals in one
     * output signal. This method performs the upsampling and
     * fitering with the low pass first filtering convention.
     *
     * <P>The input low-pass (high-pass) signal resides in the lowSig
     * array. The index of the first sample to filter (i.e. that will
     * generate the first (second) output sample). is given by lowOff
     * (highOff). This array must be of the same type as the one for
     * which the particular implementation works with (which is
     * returned by the getDataType() method).
     *
     * <P>The low-pass (high-pass) input signal can be interleaved
     * with other signals in the same lowSig (highSig) array, and this
     * is determined by the lowStep (highStep) argument. This means
     * that the first sample of the low-pass (high-pass) input signal
     * is lowSig[lowOff] (highSig[highOff]), the second is
     * lowSig[lowOff+lowStep] (highSig[highOff+highStep]), the third
     * is lowSig[lowOff+2*lowStep] (highSig[highOff+2*highStep]), and
     * so on. Therefore if lowStep (highStep) is 1 there is no
     * interleaving. This feature allows to filter columns of a 2-D
     * signal, when it is stored in a line by line order in lowSig
     * (highSig), without having to copy the data, in this case the
     * lowStep (highStep) argument should be the line width of the
     * low-pass (high-pass) signal.
     *
     * <P>The output signal is placed in the outSig array. The outOff
     * and outStep arguments are analogous to the lowOff and lowStep
     * ones, but they apply to the outSig array. The outSig array must
     * be long enough to hold the low-pass output signal.
     *
     * @param lowSig This is the array that contains the low-pass
     * input signal. It must be of the correct type (e.g., it must be
     * int[] if getDataType() returns TYPE_INT).
     *
     * @param lowOff This is the index in lowSig of the first sample to
     * filter.
     *
     * @param lowLen This is the number of samples in the low-pass
     * input signal to filter.
     *
     * @param lowStep This is the step, or interleave factor, of the
     * low-pass input signal samples in the lowSig array. See above.
     *
     * @param highSig This is the array that contains the high-pass
     * input signal. It must be of the correct type (e.g., it must be
     * int[] if getDataType() returns TYPE_INT).
     *
     * @param highOff This is the index in highSig of the first sample to
     * filter.
     *
     * @param highLen This is the number of samples in the high-pass
     * input signal to filter.
     *
     * @param highStep This is the step, or interleave factor, of the
     * high-pass input signal samples in the highSig array. See above.
     *
     * @param outSig This is the array where the output signal is
     * placed. It must be of the same type as lowSig and it should be
     * long enough to contain the output signal.
     *
     * @param outOff This is the index in outSig of the element where
     * to put the first output sample.
     *
     * @param outStep This is the step, or interleave factor, of the
     * output samples in the outSig array. See above.
     *
     *
     *
     *
     * */
    public abstract
        void synthetize_lpf(Object lowSig, int lowOff, int lowLen, int lowStep,
                        Object highSig, int highOff, int highLen, int highStep,
                        Object outSig, int outOff, int outStep);

    /**
     * Reconstructs the output signal by the synthesis filter,
     * recomposing the low-pass and high-pass input signals in one
     * output signal. This method performs the upsampling and
     * fitering with the high pass first filtering convention.
     *
     * <P>The input low-pass (high-pass) signal resides in the lowSig
     * array. The index of the first sample to filter (i.e. that will
     * generate the first (second) output sample). is given by lowOff
     * (highOff). This array must be of the same type as the one for
     * which the particular implementation works with (which is
     * returned by the getDataType() method).
     *
     * <P>The low-pass (high-pass) input signal can be interleaved
     * with other signals in the same lowSig (highSig) array, and this
     * is determined by the lowStep (highStep) argument. This means
     * that the first sample of the low-pass (high-pass) input signal
     * is lowSig[lowOff] (highSig[highOff]), the second is
     * lowSig[lowOff+lowStep] (highSig[highOff+highStep]), the third
     * is lowSig[lowOff+2*lowStep] (highSig[highOff+2*highStep]), and
     * so on. Therefore if lowStep (highStep) is 1 there is no
     * interleaving. This feature allows to filter columns of a 2-D
     * signal, when it is stored in a line by line order in lowSig
     * (highSig), without having to copy the data, in this case the
     * lowStep (highStep) argument should be the line width of the
     * low-pass (high-pass) signal.
     *
     * <P>The output signal is placed in the outSig array. The outOff
     * and outStep arguments are analogous to the lowOff and lowStep
     * ones, but they apply to the outSig array. The outSig array must
     * be long enough to hold the low-pass output signal.
     *
     * @param lowSig This is the array that contains the low-pass
     * input signal. It must be of the correct type (e.g., it must be
     * int[] if getDataType() returns TYPE_INT).
     *
     * @param lowOff This is the index in lowSig of the first sample to
     * filter.
     *
     * @param lowLen This is the number of samples in the low-pass
     * input signal to filter.
     *
     * @param lowStep This is the step, or interleave factor, of the
     * low-pass input signal samples in the lowSig array. See above.
     *
     * @param highSig This is the array that contains the high-pass
     * input signal. It must be of the correct type (e.g., it must be
     * int[] if getDataType() returns TYPE_INT).
     *
     * @param highOff This is the index in highSig of the first sample to
     * filter.
     *
     * @param highLen This is the number of samples in the high-pass
     * input signal to filter.
     *
     * @param highStep This is the step, or interleave factor, of the
     * high-pass input signal samples in the highSig array. See above.
     *
     * @param outSig This is the array where the output signal is
     * placed. It must be of the same type as lowSig and it should be
     * long enough to contain the output signal.
     *
     * @param outOff This is the index in outSig of the element where
     * to put the first output sample.
     *
     * @param outStep This is the step, or interleave factor, of the
     * output samples in the outSig array. See above.
     *
     *
     *
     *
     * */
    public abstract
        void synthetize_hpf(Object lowSig, int lowOff, int lowLen, int lowStep,
                        Object highSig, int highOff, int highLen, int highStep,
                        Object outSig, int outOff, int outStep);

}


