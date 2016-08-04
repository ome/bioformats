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
 * $RCSfile: DecLyrdCBlk.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:06 $
 * $State: Exp $
 *
 * Class:                   DecLyrdCBlk
 *
 * Description:             The coded (compressed) code-block
 *                          with layered organization for the decoder.
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


package jj2000.j2k.entropy.decoder;

import jj2000.j2k.entropy.*;

/**
 * This class stores coded (compressed) code-blocks that are organized
 * in layers. This object can contain either all the data of the
 * code-block (i.e. all layers), or a subset of all the layers that
 * make up the whole compressed-code-block. It is applicable to the
 * decoder engine only. Some data of the coded-block is stored
 * in the super class, see CodedCBlk.
 *
 * <P>A code-block may have its progressive attribute set (i.e. the
 * 'prog' flag is true). If a code-block is progressive then it means
 * that more data for it may be obtained for an improved quality. If
 * the progressive flag is false then no more data is available from
 * the source for this code-block.
 *
 * @see CodedCBlk
 * */
public class DecLyrdCBlk extends CodedCBlk {

    /** The horizontal coordinate of the upper-left corner of the code-block */
    public int ulx;

    /** The vertical coordinate of the upper left corner of the code-block */
    public int uly;

    /** The width of the code-block */
    public int w;

    /** The height of the code-block */
    public int h;

    /** The coded (compressed) data length. The data is stored in the
     * 'data' array (see super class).
     */
    public int dl;

    /** The progressive flag, false by default (see above). */
    public boolean prog;

    /** The number of layers in the coded data. */
    public int nl;

    /** The index of the first truncation point returned */
    public int ftpIdx;

    /** The total number of truncation points from layer 1 to the last one in
     * this object. The number of truncation points in 'data' is
     * 'nTrunc-ftpIdx'. */
    public int nTrunc;

    /** The length of each terminated segment. If null then there is only one
     * terminated segment, and its length is 'dl'. The number of terminated
     * segments is to be deduced from 'ftpIdx', 'nTrunc' and the coding
     * options. This array contains all terminated segments from the 'ftpIdx'
     * truncation point, upto, and including, the 'nTrunc-1' truncation
     * point. Any data after 'nTrunc-1' is not included in any length. */
    public int tsLengths[];

    /** Object information in a string
     *
     * @return Information in a string
     *
     *
     */
    public String toString(){
        String str=
            "Coded code-block ("+m+","+n+"): "+skipMSBP+" MSB skipped, "+
            dl+" bytes, "+nTrunc+" truncation points, "+nl+" layers, "+
            "progressive= "+prog+", ulx= "+ulx+", uly= "+uly+
            ", w= "+w+", h= "+h+", ftpIdx="+ftpIdx;
        if(tsLengths!=null){
            str += " {";
            for(int i=0; i<tsLengths.length; i++)
                str += " "+tsLengths[i];
            str += " }";
        }
        return str;
    }
}
