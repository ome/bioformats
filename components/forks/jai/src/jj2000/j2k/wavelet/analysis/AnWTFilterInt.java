/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2014 Open Microscopy Environment:
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
 * $RCSfile: AnWTFilterInt.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:29 $
 * $State: Exp $
 *
 * Class:                   AnWTFilterInt
 *
 * Description:             A specialized wavelet filter interface that
 *                          works on int data.
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


package jj2000.j2k.wavelet.analysis;

import jj2000.j2k.wavelet.*;
import jj2000.j2k.image.*;

/**
 * This extends the analysis wavelet filter general definitions of
 * AnWTFilter by adding methods that work for int data
 * specifically. Implementations that work on int data should inherit
 * from this class.
 *
 * <P>See the AnWTFilter class for details such as
 * normalization, how to split odd-length signals, etc.
 *
 * <P>The advantage of using the specialized method is that no casts
 * are performed.
 *
 * @see AnWTFilter
 * */
public abstract class AnWTFilterInt extends AnWTFilter {

    /**
     * A specific version of the analyze_lpf() method that works on int
     * data. See the general description of the analyze_lpf() method in
     * the AnWTFilter class for more details.
     *
     * @param inSig This is the array that contains the input
     * signal.
     *
     * @param inOff This is the index in inSig of the first sample to
     * filter.
     *
     * @param inLen This is the number of samples in the input signal
     * to filter.
     *
     * @param inStep This is the step, or interleave factor, of the
     * input signal samples in the inSig array.
     *
     * @param lowSig This is the array where the low-pass output
     * signal is placed.
     *
     * @param lowOff This is the index in lowSig of the element where
     * to put the first low-pass output sample.
     *
     * @param lowStep This is the step, or interleave factor, of the
     * low-pass output samples in the lowSig array.
     *
     * @param highSig This is the array where the high-pass output
     * signal is placed.
     *
     * @param highOff This is the index in highSig of the element where
     * to put the first high-pass output sample.
     *
     * @param highStep This is the step, or interleave factor, of the
     * high-pass output samples in the highSig array.
     *
     * @see AnWTFilter#analyze_lpf
     *
     *
     *
     *
     * */
    public abstract
        void analyze_lpf(int inSig[], int inOff, int inLen, int inStep,
                     int lowSig[], int lowOff, int lowStep,
                     int highSig[], int highOff, int highStep);

    /**
     * The general version of the analyze_lpf() method, it just calls the
     * specialized version. See the description of the analyze_lpf()
     * method of the AnWTFilter class for more details.
     *
     * @param inSig This is the array that contains the input
     * signal. It must be an int[].
     *
     * @param inOff This is the index in inSig of the first sample to
     * filter.
     *
     * @param inLen This is the number of samples in the input signal
     * to filter.
     *
     * @param inStep This is the step, or interleave factor, of the
     * input signal samples in the inSig array.
     *
     * @param lowSig This is the array where the low-pass output
     * signal is placed. It must be an int[].
     *
     * @param lowOff This is the index in lowSig of the element where
     * to put the first low-pass output sample.
     *
     * @param lowStep This is the step, or interleave factor, of the
     * low-pass output samples in the lowSig array.
     *
     * @param highSig This is the array where the high-pass output
     * signal is placed. It must be an int[].
     *
     * @param highOff This is the index in highSig of the element where
     * to put the first high-pass output sample.
     *
     * @param highStep This is the step, or interleave factor, of the
     * high-pass output samples in the highSig array.
     *
     * @see AnWTFilter#analyze_lpf
     *
     *
     *
     *
     * */

    public void analyze_lpf(Object inSig, int inOff, int inLen, int inStep,
                    Object lowSig, int lowOff, int lowStep,
                    Object highSig, int highOff, int highStep) {

        analyze_lpf((int[])inSig, inOff, inLen, inStep,
                (int[])lowSig, lowOff, lowStep,
                (int[])highSig, highOff, highStep);
    }

    /**
     * A specific version of the analyze_hpf() method that works on int
     * data. See the general description of the analyze_hpf() method in
     * the AnWTFilter class for more details.
     *
     * @param inSig This is the array that contains the input
     * signal.
     *
     * @param inOff This is the index in inSig of the first sample to
     * filter.
     *
     * @param inLen This is the number of samples in the input signal
     * to filter.
     *
     * @param inStep This is the step, or interleave factor, of the
     * input signal samples in the inSig array.
     *
     * @param lowSig This is the array where the low-pass output
     * signal is placed.
     *
     * @param lowOff This is the index in lowSig of the element where
     * to put the first low-pass output sample.
     *
     * @param lowStep This is the step, or interleave factor, of the
     * low-pass output samples in the lowSig array.
     *
     * @param highSig This is the array where the high-pass output
     * signal is placed.
     *
     * @param highOff This is the index in highSig of the element where
     * to put the first high-pass output sample.
     *
     * @param highStep This is the step, or interleave factor, of the
     * high-pass output samples in the highSig array.
     *
     * @see AnWTFilter#analyze_hpf
     *
     *
     *
     *
     * */
    public abstract
        void analyze_hpf(int inSig[], int inOff, int inLen, int inStep,
                     int lowSig[], int lowOff, int lowStep,
                     int highSig[], int highOff, int highStep);
    /**
     * The general version of the analyze_hpf() method, it just calls the
     * specialized version. See the description of the analyze_hpf()
     * method of the AnWTFilter class for more details.
     *
     * @param inSig This is the array that contains the input
     * signal. It must be an int[].
     *
     * @param inOff This is the index in inSig of the first sample to
     * filter.
     *
     * @param inLen This is the number of samples in the input signal
     * to filter.
     *
     * @param inStep This is the step, or interleave factor, of the
     * input signal samples in the inSig array.
     *
     * @param lowSig This is the array where the low-pass output
     * signal is placed. It must be an int[].
     *
     * @param lowOff This is the index in lowSig of the element where
     * to put the first low-pass output sample.
     *
     * @param lowStep This is the step, or interleave factor, of the
     * low-pass output samples in the lowSig array.
     *
     * @param highSig This is the array where the high-pass output
     * signal is placed. It must be an int[].
     *
     * @param highOff This is the index in highSig of the element where
     * to put the first high-pass output sample.
     *
     * @param highStep This is the step, or interleave factor, of the
     * high-pass output samples in the highSig array.
     *
     * @see AnWTFilter#analyze_hpf
     *
     *
     *
     *
     * */

    public void analyze_hpf(Object inSig, int inOff, int inLen, int inStep,
                    Object lowSig, int lowOff, int lowStep,
                    Object highSig, int highOff, int highStep) {

        analyze_hpf((int[])inSig, inOff, inLen, inStep,
                (int[])lowSig, lowOff, lowStep,
                (int[])highSig, highOff, highStep);
    }
    /**
     * Returns the type of data on which this filter works, as defined
     * in the DataBlk interface, which is always TYPE_INT for this
     * class.
     *
     * @return The type of data as defined in the DataBlk interface.
     *
     * @see jj2000.j2k.image.DataBlk
     *
     *
     * */
    public int getDataType() {
        return DataBlk.TYPE_INT;
    }

}
