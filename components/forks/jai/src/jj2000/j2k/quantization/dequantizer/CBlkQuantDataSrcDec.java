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
 * $RCSfile: CBlkQuantDataSrcDec.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:18 $
 * $State: Exp $
 *
 * Class:                   CBlkQuantDataSrcDec
 *
 * Description:             Interface that defines a source of
 *                          quantized wavelet data to be transferred in a
 *                          code-block by code-block basis (decoder side).
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
package jj2000.j2k.quantization.dequantizer;

import jj2000.j2k.wavelet.synthesis.*;
import jj2000.j2k.entropy.decoder.*;
import jj2000.j2k.image.*;

/**
 * This interface defines a source of quantized wavelet coefficients and
 * methods to transfer them in a code-block by code-block basis, fro the
 * decoder side. In each call to 'getCodeBlock()' or 'getInternCodeBlock()' a
 * new code-block is returned.
 *
 * <P>This class is the source of data for the dequantizer. See the
 * 'Dequantizer' class.
 *
 * <P>Code-block data is returned in sign-magnitude representation, instead of
 * the normal two's complement one. Only integral types are used. The sign
 * magnitude representation is more adequate for entropy coding. In sign
 * magnitude representation, the most significant bit is used for the sign (0
 * if positive, 1 if negative) and the magnitude of the coefficient is stored
 * in the next M most significant bits. The rest of the bits (least
 * significant bits) can contain a fractional value of the quantized
 * coefficient. The number 'M' of magnitude bits is communicated in the
 * 'magbits' member variable of the 'CBlkWTData'.
 *
 * @see InvWTData
 * @see CBlkWTDataSrcDec
 * @see Dequantizer
 * @see EntropyDecoder
 * */
public interface CBlkQuantDataSrcDec extends InvWTData {

    /**
     * Returns the specified code-block in the current tile for the specified
     * component, as a copy (see below).
     *
     * <p>The returned code-block may be progressive, which is indicated by
     * the 'progressive' variable of the returned 'DataBlk' object. If a
     * code-block is progressive it means that in a later request to this
     * method for the same code-block it is possible to retrieve data which is
     * a better approximation, since meanwhile more data to decode for the
     * code-block could have been received. If the code-block is not
     * progressive then later calls to this method for the same code-block
     * will return the exact same data values.</p>
     *
     * <p>The data returned by this method is always a copy of the internal
     * data of this object, if any, and it can be modified "in place" without
     * any problems after being returned. The 'offset' of the returned data is
     * 0, and the 'scanw' is the same as the code-block width. See the
     * 'DataBlk' class.</p>
     *
     * <p>The 'ulx' and 'uly' members of the returned 'DataBlk' object contain
     * the coordinates of the top-left corner of the block, with respect to
     * the tile, not the subband.</p>
     *
     * @param c The component for which to return the next code-block.
     *
     * @param m The vertical index of the code-block to return, in the
     * specified subband.
     *
     * @param n The horizontal index of the code-block to return, in the
     * specified subband.
     *
     * @param sb The subband in which the code-block to return is.
     *
     * @param cblk If non-null this object will be used to return the new
     * code-block. If null a new one will be allocated and returned. If the
     * "data" array of the object is non-null it will be reused, if possible,
     * to return the data.
     *
     * @return The next code-block in the current tile for component 'n', or
     * null if all code-blocks for the current tile have been returned.
     *
     * @see DataBlk
     * */
    public DataBlk getCodeBlock(int c,int m,int n,SubbandSyn sb,DataBlk cblk);

    /**
     * Returns the specified code-block in the current tile for the specified
     * component (as a reference or copy).
     *
     * <p>The returned code-block may be progressive, which is indicated by
     * the 'progressive' variable of the returned 'DataBlk' object. If a
     * code-block is progressive it means that in a later request to this
     * method for the same code-block it is possible to retrieve data which is
     * a better approximation, since meanwhile more data to decode for the
     * code-block could have been received. If the code-block is not
     * progressive then later calls to this method for the same code-block
     * will return the exact same data values.</p>
     *
     * <p>The data returned by this method can be the data in the internal
     * buffer of this object, if any, and thus can not be modified by the
     * caller. The 'offset' and 'scanw' of the returned data can be
     * arbitrary. See the 'DataBlk' class.</p>
     *
     * <p>The 'ulx' and 'uly' members of the returned 'DataBlk' object contain
     * the coordinates of the top-left corner of the block, with respect to
     * the tile, not the subband.</p>
     *
     * @param c The component for which to return the next code-block.
     *
     * @param m The vertical index of the code-block to return, in the
     * specified subband.
     *
     * @param n The horizontal index of the code-block to return, in the
     * specified subband.
     *
     * @param sb The subband in which the code-block to return is.
     *
     * @param cblk If non-null this object will be used to return the new
     * code-block. If null a new one will be allocated and returned. If the
     * "data" array of the object is non-null it will be reused, if possible,
     * to return the data.
     *
     * @return The next code-block in the current tile for component 'n', or
     * null if all code-blocks for the current tile have been returned.
     *
     * @see DataBlk
     * */
    public DataBlk getInternCodeBlock(int c,int m,int n,SubbandSyn sb,
                                      DataBlk cblk);
}
