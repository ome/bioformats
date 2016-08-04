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
 * $RCSfile: ForwWT.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:30 $
 * $State: Exp $
 *
 * Class:                   ForwWT
 *
 * Description:             The interface for implementations of a forward
 *                          wavelet transform.
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

/**
 * This interface extends the WaveletTransform with the
 * specifics of forward wavelet transforms. Classes that implement forward
 * wavelet transfoms should implement this interface.
 *
 * <P>This class does not define the methods to transfer data, just the
 * specifics to forward wavelet transform. Different data transfer methods are
 * evisageable for different transforms.
 *
 * */
public interface ForwWT extends WaveletTransform, ForwWTDataProps {

    /**
     * Returns the horizontal analysis wavelet filters used in each
     * level, for the specified tile-component. The first element in
     * the array is the filter used to obtain the lowest resolution
     * (resolution level 0) subbands (i.e. lowest frequency LL
     * subband), the second element is the one used to generate the
     * resolution level 1 subbands, and so on. If there are less
     * elements in the array than the number of resolution levels,
     * then the last one is assumed to repeat itself.
     *
     * <P>The returned filters are applicable only to the specified
     * component and in the current tile.
     *
     * <P>The resolution level of a subband is the resolution level to
     * which a subband contributes, which is different from its
     * decomposition level.
     *
     * @param t The index of the tile for which to return the filters.
     *
     * @param c The index of the component for which to return the
     * filters.
     *
     * @return The horizontal analysis wavelet filters used in each
     * level.
     *
     *
     * */
    public AnWTFilter[] getHorAnWaveletFilters(int t, int c);

    /**
     * Returns the vertical analysis wavelet filters used in each
     * level, for the specified tile-component. The first element in
     * the array is the filter used to obtain the lowest resolution
     * (resolution level 0) subbands (i.e. lowest frequency LL
     * subband), the second element is the one used to generate the
     * resolution level 1 subbands, and so on. If there are less
     * elements in the array than the number of resolution levels,
     * then the last one is assumed to repeat itself.
     *
     * <P>The returned filters are applicable only to the specified
     * component and in the current tile.
     *
     * <P>The resolution level of a subband is the resolution level to
     * which a subband contributes, which is different from its
     * decomposition level.
     *
     * @param t The index of the tile for which to return the filters.
     *
     * @param c The index of the component for which to return the
     * filters.
     *
     * @return The vertical analysis wavelet filters used in each
     * level.
     *
     *
     * */
    public AnWTFilter[] getVertAnWaveletFilters(int t,int c);

    /**
     * Returns the number of decomposition levels that are applied to
     * obtain the LL band, in the specified tile-component. A value of
     * 0 means that no wavelet transform is applied.
     *
     * @param t The tile index
     *
     * @param c The index of the component.
     *
     * @return The number of decompositions applied to obtain the LL
     * band (0 for no wavelet transform).
     *
     *
     * */
    public int getDecompLevels(int t,int c);

    /**
     * Returns the wavelet tree decomposition. Only WT_DECOMP_DYADIC
     * is supported by JPEG 2000 part I.
     *
     * @param t The tile index
     *
     * @param c The index of the component.
     *
     * @return The wavelet decomposition.
     *
     *
     * */
    public int getDecomp(int t,int c);
}
