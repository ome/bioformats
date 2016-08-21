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
 * $RCSfile: Markers.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:00 $
 * $State: Exp $
 *
 * Class:                   Markers
 *
 * Description: Defines the values of the markers in JPEG 2000 codestream
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
package jj2000.j2k.codestream;

/**
 * This interface defines the values of the different markers in the JPEG 2000
 * codestream. They are 16 bit values, always appearing in big-endian (most
 * significant byte first) and byte-aligned in the codestream. This interface
 * also defines some other constants such as bit-masks and bit-shifts.
 * */
public interface Markers {

    // ----> Delimiting markers and marker segments <----

    /** Start of codestream (SOC): 0xFF4F */
    public final static short SOC = (short)0xff4f;

    /** Start of tile-part (SOT): 0xFF90 */
    public final static short SOT = (short)0xff90;

    /** Start of data (SOD): 0xFF93 */
    public final static short SOD = (short)0xff93;

    /** End of codestream (EOC): 0xFFD9 */
    public final static short EOC = (short)0xffd9;

    // ----> Fixed information marker segments <----

    // ** SIZ marker **

    /** SIZ marker (Image and tile size): 0xFF51 */
    public final static short SIZ = (short)0xff51;

    /** No special capabilities (baseline) in codestream, in Rsiz field of SIZ
     * marker: 0x00. All flag bits are turned off */
    public final static int RSIZ_BASELINE = 0x00;
    /** Error resilience marker flag bit in Rsiz field in SIZ marker: 0x01 */
    public final static int RSIZ_ER_FLAG = 0x01;
    /** ROI present marker flag bit in Rsiz field in SIZ marker: 0x02 */
    public final static int RSIZ_ROI = 0x02;
    /** Component bitdepth bits in Ssiz field in SIZ marker: 7 */
    public final static int SSIZ_DEPTH_BITS = 7;
    /** The maximum number of component bitdepth */
    public static final int MAX_COMP_BITDEPTH = 38;


    // ----> Functional marker segments <----

    // ** COD/COC marker **

    /** Coding style default (COD): 0xFF52 */
    public final static short COD = (short)0xff52;

    /** Coding style component (COC): 0xFF53 */
    public final static short COC = (short)0xff53;

    /** Precinct used flag */
    public final static int SCOX_PRECINCT_PARTITION = 1;
    /** Use start of packet marker */
    public final static int SCOX_USE_SOP = 2;
    /** Use end of packet header marker */
    public final static int SCOX_USE_EPH = 4;
    /** Horizontal code-block partition origin is at x=1 */
    public final static int SCOX_HOR_CB_PART = 8;
    /** Vertical code-block partition origin is at y=1 */
    public final static int SCOX_VER_CB_PART = 16;
    /** The default size exponent of the precincts */
    public final static int PRECINCT_PARTITION_DEF_SIZE = 0xffff;

    // ** RGN marker segment **
    /** Region-of-interest (RGN): 0xFF5E */
    public final static short RGN = (short)0xff5e;

    /** Implicit (i.e. max-shift) ROI flag for Srgn field in RGN marker
        segment: 0x00 */
    public final static int SRGN_IMPLICIT = 0x00;

    // ** QCD/QCC markers **

    /** Quantization default (QCD): 0xFF5C */
    public final static short QCD = (short)0xff5c;

    /** Quantization component (QCC): 0xFF5D */
    public final static short QCC = (short)0xff5d;

    /** Guard bits shift in SQCX field: 5 */
    public final static int SQCX_GB_SHIFT = 5;
    /** Guard bits mask in SQCX field: 7 */
    public final static int SQCX_GB_MSK = 7;
    /** No quantization (i.e. embedded reversible) flag for Sqcd or Sqcc
     * (Sqcx) fields: 0x00. */
    public final static int SQCX_NO_QUANTIZATION = 0x00;
    /** Scalar derived (i.e. LL values only) quantization flag for Sqcd or
     * Sqcc (Sqcx) fields: 0x01. */
    public final static int SQCX_SCALAR_DERIVED = 0x01;
    /** Scalar expounded (i.e. all values) quantization flag for Sqcd or Sqcc
     * (Sqcx) fields: 0x02. */
    public final static int SQCX_SCALAR_EXPOUNDED = 0x02;
    /** Exponent shift in SPQCX when no quantization: 3 */
    public final static int SQCX_EXP_SHIFT = 3;
    /** Exponent bitmask in SPQCX when no quantization: 3 */
    public final static int SQCX_EXP_MASK = (1<<5)-1;
    /** The "SOP marker segments used" flag within Sers: 1 */
    public final static int ERS_SOP = 1;
    /** The "segmentation symbols used" flag within Sers: 2 */
    public final static int ERS_SEG_SYMBOLS = 2;

    // ** Progression order change **
    public final static short POC = (short)0xff5f;

    // ----> Pointer marker segments <----

    /** Tile-part lengths (TLM): 0xFF55 */
    public final static short TLM = (short)0xff55;

    /** Packet length, main header (PLM): 0xFF57 */
    public final static short PLM = (short)0xff57;

    /** Packet length, tile-part header (PLT): 0xFF58 */
    public final static short PLT = (short)0xff58;

    /** Packed packet headers, main header (PPM): 0xFF60 */
    public final static short PPM = (short)0xff60;

    /** Packed packet headers, tile-part header (PPT): 0xFF61 */
    public final static short PPT = (short)0xff61;

    /** Maximum length of PPT marker segment */
    public final static int MAX_LPPT = 65535;

    /** Maximum length of PPM marker segment */
    public final static int MAX_LPPM = 65535;


    // ----> In bit stream markers and marker segments <----

    /** Start pf packet (SOP): 0xFF91 */
    public final static short SOP = (short)0xff91;

    /** Length of SOP marker (in bytes) */
    public final static short SOP_LENGTH = 6;

    /** End of packet header (EPH): 0xFF92 */
    public final static short EPH = (short)0xff92;

    /** Length of EPH marker (in bytes) */
    public final static short EPH_LENGTH = 2;

    // ----> Informational marker segments <----

    /** Component registration (CRG): 0xFF63 */
    public final static short CRG = (short)0xff63;

    /** Comment (COM): 0xFF64 */
    public final static short COM = (short)0xff64;

    /** General use registration value (COM): 0x0001 */
    public final static short RCOM_GEN_USE = (short)0x0001;
}
