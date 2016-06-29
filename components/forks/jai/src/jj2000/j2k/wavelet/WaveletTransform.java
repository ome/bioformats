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
 * $RCSfile: WaveletTransform.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:28 $
 * $State: Exp $
 *
 * Class:                   WaveletTransform
 *
 * Description:             Interface that defines how a forward or
 *                          inverse wavelet transform should present
 *                          itself.
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


package jj2000.j2k.wavelet;

import jj2000.j2k.image.*;

/**
 * This interface defines how a forward or inverse wavelet transform
 * should present itself. As specified in the ImgData interface,
 * from which this class inherits, all operations are confined to the
 * current tile, and all coordinates are relative to it.
 *
 * <P>The definition of the methods in this interface allows for
 * different types of implementation, reversibility and levels of
 * decompositions for each component and each tile. An implementation
 * of this interface does not need to support all this flexibility
 * (e.g., it may provide the same implementation type and
 * decomposition levels for all tiles and components).
 * */
public interface WaveletTransform extends ImgData {

    /**
     * ID for line based implementations of wavelet transforms.
     */
    public final static int WT_IMPL_LINE = 0;

    /**
     * ID for full-page based implementations of wavelet transforms. Full-page
     * based implementations should be avoided since they require
     * large amounts of memory.  */
    public final static int WT_IMPL_FULL = 2;


    /**
     * Returns the reversibility of the wavelet transform for the
     * specified component and tile. A wavelet transform is reversible
     * when it is suitable for lossless and lossy-to-lossless
     * compression.
     *
     * @param t The index of the tile.
     *
     * @param c The index of the component.
     *
     * @return true is the wavelet transform is reversible, false if not.
     *
     *
     * */
    public boolean isReversible(int t,int c);

    /**
     * Returns the implementation type of this wavelet transform
     * (WT_IMPL_LINE or WT_IMPL_FRAME) for the specified component, 
     * in the current tile.
     *
     * @param n The index of the component.
     *
     * @return WT_IMPL_LINE or WT_IMPL_FULL for line,
     * block or full-page based transforms.
     *
     *
     * */
    public int getImplementationType(int n);

}
