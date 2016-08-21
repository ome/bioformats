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
 * $RCSfile: CodedCBlkDataSrcDec.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:06 $
 * $State: Exp $
 *
 * Class:                   CodedCBlkDataSrcDec
 *
 * Description:             Interface that defines a source of entropy coded
 *                          data that is transferred in a code-block by
 *                          code-block basis (decoder side).
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
 *  */
package jj2000.j2k.entropy.decoder;
import java.awt.Point;

import jj2000.j2k.wavelet.synthesis.*;
import jj2000.j2k.image.*;

/**
 * This interface defines a source of entropy coded data and methods to
 * transfer it in a code-block by code-block basis. In each call to
 * 'geCodeBlock()' a specified coded code-block is returned.
 *
 * <P>This interface is the source of data for the entropy decoder. See the
 * 'EntropyDecoder' class.
 *
 * <P>For each coded-code-block the entropy-coded data is returned along with
 * its truncation point information in a 'DecLyrdCBlk' object.
 *
 * @see EntropyDecoder
 *
 * @see DecLyrdCBlk
 *
 * @see jj2000.j2k.codestream.reader.BitstreamReaderAgent
 * */
public interface CodedCBlkDataSrcDec extends InvWTData {

    /**
     * Returns the specified coded code-block, for the specified component, in
     * the current tile. The first layer to return is indicated by 'fl'. The
     * number of layers that is returned depends on 'nl' and the amount of
     * data available.
     *
     * <P>The argument 'fl' is to be used by subsequent calls to this method
     * for the same code-block. In this way supplamental data can be retrieved
     * at a later time. The fact that data from more than one layer can be
     * returned means that several packets from the same code-block, of the
     * same component, and the same tile, have been concatenated.
     *
     * <P>The returned compressed code-block can have its progressive
     * attribute set. If this attribute is set it means that more data can be
     * obtained by subsequent calls to this method (subject to transmission
     * delays, etc). If the progressive attribute is not set it means that the
     * returned data is all the data that can be obtained for the specified
     * subblock.
     *
     * <P>The compressed code-block is uniquely specified by the current tile,
     * the component (identified by 'c'), the subband (indentified by 'sb')
     * and the code-bock vertical and horizontal indexes 'm' and 'n'.
     *
     * <P>The 'ulx' and 'uly' members of the returned 'DecLyrdCBlk' object
     * contain the coordinates of the top-left corner of the block, with
     * respect to the tile, not the subband.
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @param m The vertical index of the code-block to return, in the
     * specified subband.
     *
     * @param n The horizontal index of the code-block to return, in the
     * specified subband.
     *
     * @param sb The subband in whic the requested code-block is.
     *
     * @param fl The first layer to return.
     *
     * @param nl The number of layers to return, if negative all available
     * layers are returned, starting at 'fl'.
     *
     * @param ccb If not null this object is used to return the compressed
     * code-block. If null a new object is created and returned. If the data
     * array in ccb is not null then it can be reused to return the compressed
     * data.
     *
     * @return The compressed code-block, with a certain number of layers
     * determined by the available data and 'nl'.
     * */
    public DecLyrdCBlk getCodeBlock(int c, int m, int n,
                                         SubbandSyn sb, int fl, int nl,
                                         DecLyrdCBlk ccb);
}
