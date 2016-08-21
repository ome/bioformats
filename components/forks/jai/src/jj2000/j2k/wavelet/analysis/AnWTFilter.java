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
 * $RCSfile: AnWTFilter.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:28 $
 * $State: Exp $
 *
 * Class:                   AnWTFilter
 *
 * Description:             The abstract class for all analysis wavelet filters
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
package jj2000.j2k.wavelet.analysis;

import jj2000.j2k.codestream.writer.*;
import jj2000.j2k.wavelet.*;
import jj2000.j2k.image.*;
import jj2000.j2k.util.*;

import java.util.*;
import java.io.*;

/**
 * This abstract class defines the methods of all analysis wavelet
 * filters. Specialized abstract classes that work on particular data
 * types (int, float) provide more specific method calls while
 * retaining the generality of this one. See the AnWTFilterInt
 * and AnWTFilterFloat classes. Implementations of analysis
 * filters should inherit from one of those classes.
 *
 * <P>All analysis wavelet filters should follow the following conventions:
 *
 * <P>- The first sample to filter is the low-pass one. As a
 * consequence, if the input signal is of odd-length then the low-pass
 * output signal is one sample longer than the high-pass output
 * one. Therefore, if the length of input signal is N, the low-pass
 * output signal is of length N/2 if N is even and N/2+1/2 if N is
 * odd, while the high-pass output signal is of length N/2 if N is
 * even and N/2-1/2 if N is odd.
 *
 * <P>- The normalization is 1 for the DC gain and 2 for the Nyquist
 * gain (Type I normalization), for both reversible and non-reversible
 * filters.
 *
 * <P>If the length of input signal is N, the low-pass output signal
 * is of length N/2 if N is even and N/2+1/2 if N is odd, while the
 * high-pass output sample is of length N/2 if N is even and N/2-1/2
 * if N is odd.
 *
 * <P>The analyze method may seem very complicated, but is designed to
 * minimize the amount of data copying and redundant calculations when
 * used for block-based or line-based wavelet transform
 * implementations, while being applicable to full-frame transforms as
 * well.
 *
 * <P>All filters should implement the equals() method of the Object
 * class. The call x.equals(y) should test if the 'x' and 'y' filters are the
 * same or not, in what concerns the bit stream header syntax (two filters are
 * the same if the same filter code should be output to the bit stream).
 *
 * @see AnWTFilterInt
 *
 * @see AnWTFilterFloat
 * */
public abstract class AnWTFilter implements WaveletFilter{

    /** The prefix for wavelet filter options: 'F' */
    public final static char OPT_PREFIX = 'F';

    /** The list of parameters that is accepted for wavelet filters. Options
     * for wavelet filters start with a 'F'. */
    private final static String [][] pinfo = {
        { "Ffilters", "[<tile-component idx>] <id> "+
          "[ [<tile-component idx>] <id> ...]",
          "Specifies which filters to use for specified tile-component.\n"+
          "<tile-component idx>: see general note\n"+
          "<id>: ',' separates horizontal and vertical filters, ':' separates"+
          " decomposition levels filters. JPEG 2000 part I only supports w5x3"+
          " and w9x7 filters.",null},
    };

    /**
     * Filters the input signal by this analysis filter, decomposing
     * it in a low-pass and a high-pass signal. This method performs
     * the filtering and the subsampling with the low pass first
     * filtering convention.
     *
     * <P>The input signal resides in the inSig array. The index of
     * the first sample to filter (i.e. that will generate the first
     * low-pass output sample) is given by inOff. The number of
     * samples to filter is given by inLen. This array must be of the
     * same type as the one for which the particular implementation
     * works with (which is returned by the getDataType() method).
     *
     * <P>The input signal can be interleaved with other signals in
     * the same inSig array, and this is determined by the inStep
     * argument. This means that the first sample of the input signal
     * is inSig[inOff], the second is inSig[inOff+inStep], the third
     * is inSig[inOff+2*inStep], and so on. Therefore if inStep is 1
     * there is no interleaving. This feature allows to filter columns
     * of a 2-D signal, when it is stored in a line by line order in
     * inSig, without having to copy the data, in this case the inStep
     * argument should be the line width.
     *
     * <P>This method also allows to apply the analysis wavelet filter
     * by parts in the input signal using an overlap and thus
     * producing the same coefficients at the output. The tailOvrlp
     * argument specifies how many samples in the input signal, before
     * the first one to be filtered, can be used for overlap. Then,
     * the filter instead of extending the input signal will use those
     * samples to calculate the first output samples. The argument
     * tailOvrlp can be 0 for no overlap, or some value that provides
     * partial or full overlap. There should be enough samples in the
     * input signal, before the first sample to be filtered, to
     * support the overlap. The headOvrlp provides the same
     * functionality but at the end of the input signal. The inStep
     * argument also applies to samples used for overlap. This overlap
     * feature can be used for line-based wavelet transforms (in which
     * case it will only be used when filtering the columns) or for
     * overlapping block-based wavelet transforms (in which case it
     * will be used when filtering lines and columns).
     *
     * <P>The low-pass output signal is placed in the lowSig
     * array. The lowOff and lowStep arguments are analogous to the
     * inOff and inStep ones, but they apply to the lowSig array. The
     * lowSig array must be long enough to hold the low-pass output
     * signal.
     *
     * <P>The high-pass output signal is placed in the highSig
     * array. The highOff and highStep arguments are analogous to the
     * inOff and inStep ones, but they apply to the highSig array. The
     * highSig array must be long enough to hold the high-pass output
     * signal.
     *
     * @param inSig This is the array that contains the input
     * signal. It must be of the correct type (e.g., it must be int[]
     * if getDataType() returns TYPE_INT).
     *
     * @param inOff This is the index in inSig of the first sample to
     * filter.
     *
     * @param inLen This is the number of samples in the input signal
     * to filter.
     *
     * @param inStep This is the step, or interleave factor, of the
     * input signal samples in the inSig array. See above.
     *
     * @param tailOvrlp This is the number of samples in the input
     * signal before the first sample to filter that can be used for
     * overlap. See above.
     *
     * @param headOvrlp This is the number of samples in the input
     * signal after the last sample to filter that can be used for
     * overlap. See above.
     *
     * @param lowSig This is the array where the low-pass output
     * signal is placed. It must be of the same type as inSig and it
     * should be long enough to contain the output signal.
     *
     * @param lowOff This is the index in lowSig of the element where
     * to put the first low-pass output sample.
     *
     * @param lowStep This is the step, or interleave factor, of the
     * low-pass output samples in the lowSig array. See above.
     *
     * @param highSig This is the array where the high-pass output
     * signal is placed. It must be of the same type as inSig and it
     * should be long enough to contain the output signal.
     *
     * @param highOff This is the index in highSig of the element where
     * to put the first high-pass output sample.
     *
     * @param highStep This is the step, or interleave factor, of the
     * high-pass output samples in the highSig array. See above.
     *
     * @see WaveletFilter#getDataType
     *
     *
     *
     *
     * */
    public abstract
        void analyze_lpf(Object inSig, int inOff, int inLen, int inStep,
                     Object lowSig, int lowOff, int lowStep,
                     Object highSig, int highOff, int highStep);

    /**
     * Filters the input signal by this analysis filter, decomposing
     * it in a low-pass and a high-pass signal. This method performs
     * the filtering and the subsampling with the high pass first filtering
     * convention.
     *
     * <P>The input signal resides in the inSig array. The index of
     * the first sample to filter (i.e. that will generate the first
     * high-pass output sample) is given by inOff. The number of
     * samples to filter is given by inLen. This array must be of the
     * same type as the one for which the particular implementation
     * works with (which is returned by the getDataType() method).
     *
     * <P>The input signal can be interleaved with other signals in
     * the same inSig array, and this is determined by the inStep
     * argument. This means that the first sample of the input signal
     * is inSig[inOff], the second is inSig[inOff+inStep], the third
     * is inSig[inOff+2*inStep], and so on. Therefore if inStep is 1
     * there is no interleaving. This feature allows to filter columns
     * of a 2-D signal, when it is stored in a line by line order in
     * inSig, without having to copy the data, in this case the inStep
     * argument should be the line width.
     *
     * <P>The low-pass output signal is placed in the lowSig
     * array. The lowOff and lowStep arguments are analogous to the
     * inOff and inStep ones, but they apply to the lowSig array. The
     * lowSig array must be long enough to hold the low-pass output
     * signal.
     *
     * <P>The high-pass output signal is placed in the highSig
     * array. The highOff and highStep arguments are analogous to the
     * inOff and inStep ones, but they apply to the highSig array. The
     * highSig array must be long enough to hold the high-pass output
     * signal.
     *
     * @param inSig This is the array that contains the input
     * signal. It must be of the correct type (e.g., it must be int[]
     * if getDataType() returns TYPE_INT).
     *
     * @param inOff This is the index in inSig of the first sample to
     * filter.
     *
     * @param inLen This is the number of samples in the input signal
     * to filter.
     *
     * @param inStep This is the step, or interleave factor, of the
     * input signal samples in the inSig array. See above.
     *
     * @param lowSig This is the array where the low-pass output
     * signal is placed. It must be of the same type as inSig and it
     * should be long enough to contain the output signal.
     *
     * @param lowOff This is the index in lowSig of the element where
     * to put the first low-pass output sample.
     *
     * @param lowStep This is the step, or interleave factor, of the
     * low-pass output samples in the lowSig array. See above.
     *
     * @param highSig This is the array where the high-pass output
     * signal is placed. It must be of the same type as inSig and it
     * should be long enough to contain the output signal.
     *
     * @param highOff This is the index in highSig of the element where
     * to put the first high-pass output sample.
     *
     * @param highStep This is the step, or interleave factor, of the
     * high-pass output samples in the highSig array. See above.
     *
     * @see WaveletFilter#getDataType
     *
     *
     *
     *
     * */
    public abstract
        void analyze_hpf(Object inSig, int inOff, int inLen, int inStep,
                     Object lowSig, int lowOff, int lowStep,
                     Object highSig, int highOff, int highStep);

    /**
     * Returns the time-reversed low-pass synthesis waveform of the
     * filter, which is the low-pass filter. This is the time-reversed
     * impulse response of the low-pass synthesis filter. It is used
     * to calculate the L2-norm of the synthesis basis functions for a
     * particular subband (also called energy weight).
     *
     * <P>The returned array may not be modified (i.e. a reference to
     * the internal array may be returned by the implementation of
     * this method).
     *
     * @return The time-reversed low-pass synthesis waveform of the
     * filter.
     *
     *
     * */
    public abstract float[] getLPSynthesisFilter();

    /**
     * Returns the time-reversed high-pass synthesis waveform of the
     * filter, which is the high-pass filter. This is the
     * time-reversed impulse response of the high-pass synthesis
     * filter. It is used to calculate the L2-norm of the synthesis
     * basis functions for a particular subband (also called energy
     * weight).
     *
     * <P>The returned array may not be modified (i.e. a reference to
     * the internal array may be returned by the implementation of
     * this method).
     *
     * @return The time-reversed high-pass synthesis waveform of the
     * filter.
     *
     *
     * */
    public abstract float[] getHPSynthesisFilter();

    /**
     * Returns the equivalent low-pass synthesis waveform of a cascade
     * of filters, given the syhthesis waveform of the previous
     * stage. This is the result of upsampling 'in' by 2, and
     * concolving it with the low-pass synthesis waveform of the
     * filter. The length of the returned signal is 2*in_l+lp_l-2,
     * where in_l is the length of 'in' and 'lp_l' is the lengthg of
     * the low-pass synthesis filter.
     *
     * <P>The length of the low-pass synthesis filter is
     * getSynLowNegSupport()+getSynLowPosSupport().
     *
     * @param in The synthesis waveform of the previous stage.
     *
     * @param out If non-null this array is used to store the
     * resulting signal. It must be long enough, or an
     * IndexOutOfBoundsException is thrown.
     *
     * @see #getSynLowNegSupport
     *
     * @see #getSynLowPosSupport
     *
     *
     * */
    public float[] getLPSynWaveForm(float in[], float out[]) {
        return upsampleAndConvolve(in,getLPSynthesisFilter(),out);
    }

    /**
     * Returns the equivalent high-pass synthesis waveform of a
     * cascade of filters, given the syhthesis waveform of the
     * previous stage. This is the result of upsampling 'in' by 2, and
     * concolving it with the high-pass synthesis waveform of the
     * filter. The length of the returned signal is 2*in_l+hp_l-2,
     * where in_l is the length of 'in' and 'hp_l' is the lengthg of
     * the high-pass synthesis filter.
     *
     * <P>The length of the high-pass synthesis filter is
     * getSynHighNegSupport()+getSynHighPosSupport().
     *
     * @param in The synthesis waveform of the previous stage.
     *
     * @param out If non-null this array is used to store the
     * resulting signal. It must be long enough, or an
     * IndexOutOfBoundsException is thrown.
     *
     * @see #getSynHighNegSupport
     *
     * @see #getSynHighPosSupport
     *
     *
     * */
    public float[] getHPSynWaveForm(float in[], float out[]) {
        return upsampleAndConvolve(in,getHPSynthesisFilter(),out);
    }

    /**
     * Returns the signal resulting of upsampling (by 2) the input
     * signal 'in' and then convolving it with the time-reversed
     * signal 'wf'. The returned signal is of length l_in*2+l_wf-2,
     * where l_in is the length of 'in', and l_wf is the length of
     * 'wf'.
     *
     * <P>The 'wf' signal has to be already time-reversed, therefore
     * only a dot-product is performed (instead of a
     * convolution). This is equivalent to convolving with the
     * non-time-reversed 'wf' signal.
     *
     * @param in The signal to upsample and filter. If null it is
     * considered to be a dirac.
     *
     * @param wf The time-reversed impulse response used for
     * filtering.
     *
     * @param out If non-null this array is used to store the
     * resulting signal, it must be of length in.length*2+wf.length-2
     * at least. An IndexOutOfBoundsException is thrown if this is not
     * the case.
     *
     * @return The resulting signal, of length in.length*2+wf.length-2
     *
     *
     * */
    private static
        float[] upsampleAndConvolve(float in[], float wf[], float out[]) {
        // NOTE: the effective length of the signal 'in' upsampled by
        // 2 is 2*in.length-1 (not 2*in.length), so the resulting signal
        // (after convolution) is of length 2*in.length-1+wf.length-1,
        // which is 2*in.length+wf.length-2

        int i,k,j;
        float tmp;
        int maxi,maxk;

        // If in null, then simulate dirac
        if (in == null) {
            in = new float[1];
            in[0] = 1.0f;
        }

        // Get output buffer if necessary
        if (out == null) {
            out = new float[in.length*2+wf.length-2];
        }
        // Convolve the signals
        for (i=0, maxi=in.length*2+wf.length-2; i<maxi; i++) {
            tmp = 0.0f;

            // Calculate limits of loop below
            k = (i-wf.length+2)/2;
            if (k<0) k = 0;
            maxk = i/2+1;
            if (maxk > in.length) maxk = in.length;

            // Calculate dot-product with upsampling of 'in' by 2.
            for (j = 2*k-i+wf.length-1; k<maxk; k++, j+=2) {
                tmp += in[k]*wf[j];
            }
            // Store result
            out[i] = tmp;
        }

        return out;
    }

    /**
     * Returns the type of filter used according to the FilterTypes
     * interface.
     *
     * @see FilterTypes
     *
     * @return The filter type.
     *
     */
    public abstract int getFilterType();

    /**
     * Returns the parameters that are used in this class and
     * implementing classes. It returns a 2D String array. Each of the
     * 1D arrays is for a different option, and they have 3
     * elements. The first element is the option name, the second one
     * is the synopsis, the third one is a long description of what
     * the parameter is and the fourth is its default value. The
     * synopsis or description may be 'null', in which case it is
     * assumed that there is no synopsis or description of the option,
     * respectively. Null may be returned if no options are supported.
     *
     * @return the options name, their synopsis and their explanation,
     * or null if no options are supported.
     *
     *
     * */
    public static String[][] getParameterInfo() {
        return pinfo;
    }

}
