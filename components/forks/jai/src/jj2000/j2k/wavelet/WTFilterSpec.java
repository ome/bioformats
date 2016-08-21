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
 * $RCSfile: WTFilterSpec.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:28 $
 * $State: Exp $
 * 
 * Class:                   WTFilterSpec
 * 
 * Description:             Generic class for storing wavelet filter specs
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


package jj2000.j2k.wavelet;

/**
 * This is the generic class from which the ones that hold the analysis or
 * synthesis filters to be used in each part of the image derive. See
 * AnWTFilterSpec and SynWTFilterSpec.
 *
 * <P>The filters to use are defined by a hierarchy. The hierarchy is:
 *
 * <P>- Tile and component specific filters<br>
 * - Tile specific default filters<br>
 * - Component main default filters<br>
 * - Main default filters<br>
 *
 * <P>At the moment tiles are not supported by this class.
 *
 * @see jj2000.j2k.wavelet.analysis.AnWTFilterSpec
 *
 * @see jj2000.j2k.wavelet.synthesis.SynWTFilterSpec
 * */

public abstract class WTFilterSpec {

    /** The identifier for "main default" specified filters */
    public final static byte FILTER_SPEC_MAIN_DEF = 0;

    /** The identifier for "component default" specified filters */
    public final static byte FILTER_SPEC_COMP_DEF = 1;

    /** The identifier for "tile specific default" specified filters */
    public final static byte FILTER_SPEC_TILE_DEF = 2;

    /** The identifier for "tile and component specific" specified filters */
    public final static byte FILTER_SPEC_TILE_COMP = 3;

    /** The spec type for each tile and component. The first index is the
     * component index, the second is the tile index. NOTE: The tile specific
     * things are not supported yet. */
    // Use byte to save memory (no need for speed here).
    protected byte specValType[];

    /**
     * Constructs a 'WTFilterSpec' object, initializing all the components and
     * tiles to the 'FILTER_SPEC_MAIN_DEF' spec type, for the specified number
     * of components and tiles.
     *
     * <P>NOTE: The tile specific things are not supported yet
     *
     * @param nc The number of components
     *
     * @param nt The number of tiles
     *
     *
     * */
    protected WTFilterSpec(int nc) {
        specValType = new byte[nc];
    }

    /**
     * Returns the data type used by the filters in this object, as defined in 
     * the 'DataBlk' interface.
     *
     * @return The data type of the filters in this object
     *
     * @see jj2000.j2k.image.DataBlk
     *
     *
     * */
    public abstract int getWTDataType();

    /**
     * Returns the type of specification for the filters in the specified
     * component and tile. The specification type is one of:
     * 'FILTER_SPEC_MAIN_DEF', 'FILTER_SPEC_COMP_DEF', 'FILTER_SPEC_TILE_DEF',
     * 'FILTER_SPEC_TILE_COMP'.
     *
     * <P>NOTE: The tile specific things are not supported yet
     *
     * @param n The component index
     *
     * @param t The tile index, in raster scan order.
     *
     * @return The specification type for component 'n' and tile 't'.
     *
     *
     * */
    public byte getKerSpecType(int n) {
        return specValType[n];
    }

}
