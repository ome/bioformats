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
 * $RCSfile: FileFormatBoxes.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:10 $
 * $State: Exp $
 *
 * Class:                   FileFormatMarkers
 *
 * Description:             Contains definitions of boxes used in jp2 files
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
package jj2000.j2k.fileformat;


/**
 * This class contains all the markers used in the JPEG 2000 Part I file format
 *
 * @see jj2000.j2k.fileformat.writer.FileFormatWriter
 *
 * @see jj2000.j2k.fileformat.reader.FileFormatReader
 *
 */
public interface FileFormatBoxes{

    /**** Main boxes ****/

    public static final int JP2_SIGNATURE_BOX = 0x6a502020;

    public static final int FILE_TYPE_BOX       = 0x66747970;

    public static final int JP2_HEADER_BOX   = 0x6a703268;

    public static final int CONTIGUOUS_CODESTREAM_BOX = 0x6a703263;

    public static final int INTELLECTUAL_PROPERTY_BOX = 0x64703269;

    public static final int XML_BOX                   = 0x786d6c20;

    public static final int UUID_BOX                  = 0x75756964;

    public static final int UUID_INFO_BOX             = 0x75696e66;

    /** JP2 Header boxes */
    public static final int IMAGE_HEADER_BOX               = 0x69686472;

    public static final int BITS_PER_COMPONENT_BOX         = 0x62706363;

    public static final int COLOUR_SPECIFICATION_BOX       = 0x636f6c72;

    public static final int PALETTE_BOX                    = 0x70636c72;

    public static final int COMPONENT_MAPPING_BOX          = 0x636d6170;

    public static final int CHANNEL_DEFINITION_BOX         = 0x63646566;

    public static final int RESOLUTION_BOX                 = 0x72657320;

    public static final int CAPTURE_RESOLUTION_BOX         = 0x72657363;

    public static final int DEFAULT_DISPLAY_RESOLUTION_BOX = 0x72657364;

    /** End of JP2 Header boxes */

    /** UUID Info Boxes */
    public static final int UUID_LIST_BOX = 0x75637374;

    public static final int URL_BOX       = 0x75726c20;
    /** end of UUID Info boxes */

    /** Image Header Box Fields */
    public static final int IMB_VERS = 0x0100;

    public static final int IMB_C = 7;

    public static final int IMB_UnkC = 1;

    public static final int IMB_IPR = 0;
    /** end of Image Header Box Fields*/

    /** Colour Specification Box Fields */
    public static final int CSB_METH = 1;

    public static final int CSB_PREC = 0;

    public static final int CSB_APPROX = 0;

    public static final int CSB_ENUM_SRGB = 16;

    public static final int CSB_ENUM_GREY = 17;

    public static final int CSB_ENUM_YCC = 18;
    /** en of Colour Specification Box Fields */

    /** File Type Fields */
    public static final int FT_BR = 0x6a703220;



}





