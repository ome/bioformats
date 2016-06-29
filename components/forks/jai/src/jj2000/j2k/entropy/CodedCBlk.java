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
 * $RCSfile: CodedCBlk.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:04 $
 * $State: Exp $
 *
 * Class:                   CodedCBlk
 *
 * Description:             The generic coded (compressed) code-block
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
 * This is the generic class to store coded (compressed) code-block. It stores
 * the compressed data as well as the necessary side-information.
 *
 * <P>This class is normally not used. Instead the EncRDCBlk, EncLyrdCBlk and
 * the DecLyrdCBlk subclasses are used.
 *
 * @see jj2000.j2k.entropy.encoder.CBlkRateDistStats
 *
 * @see jj2000.j2k.entropy.decoder.DecLyrdCBlk
 * */
public class CodedCBlk {

    /** The horizontal index of the code-block, within the subband. */
    public int n;

    /** The vertical index of the code-block, within the subband. */
    public int m;

    /** The number of skipped most significant bit-planes. */
    public int skipMSBP;

    /** The compressed data */
    public byte data[];

    /**
     * Creates a new CodedCBlk object wit the default values and without
     * allocating any space for its members.
     * */
    public CodedCBlk() {
    }

    /**
     * Creates a new CodedCBlk object with the specified values.
     *
     * @param m The horizontal index of the code-block, within the subband.
     *
     * @param n The vertical index of the code-block, within the subband.
     *
     * @param skipMSBP The number of skipped most significant bit-planes for
     * this code-block.
     *
     * @param data The compressed data. This array is referenced by this
     * object so it should not be modified after.
     * */
    public CodedCBlk(int m, int n, int skipMSBP, byte data[]) {
        this.m = m;
        this.n = n;
        this.skipMSBP = skipMSBP;
        this.data = data;
    }

    /**
     * Returns the contents of the object in a string. The string contains the
     * following data: 'm', 'n', 'skipMSBP' and 'data.length. This is used for
     * debugging.
     *
     * @return A string with the contents of the object
     * */
    public String toString() {
        return "m= " + m+", n= "+n+ ", skipMSBP= "+skipMSBP+
            ", data.length= " + ((data != null) ? ""+data.length : "(null)");
    }
}

