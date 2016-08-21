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
 * $RCSfile: WTDecompSpec.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:27 $
 * $State: Exp $
 *
 * Class:                   WTDecompSpec
 * 
 * Description:             <short description of class>
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

import jj2000.j2k.*;

/**
 * This class holds the decomposition type to be used in each part of the
 * image; the default one, the component specific ones, the tile default ones 
 * and the component-tile specific ones.
 *
 * <P>The decomposition type identifiers values are the same as in the
 * codestream.
 *
 * <P>The hierarchy is:<br>
 * - Tile and component specific decomposition<br>
 * - Tile specific default decomposition<br>
 * - Component main default decomposition<br>
 * - Main default decomposition<br>
 *
 * <P>At the moment tiles are not supported by this class.
 * */
public class WTDecompSpec {
    /**
     * ID for the dyadic wavelet tree decomposition (also called
     * "Mallat" in JPEG 2000): 0x00.
     */
    public final static int WT_DECOMP_DYADIC = 0;

    /**
     * ID for the SPACL (as defined in JPEG 2000) wavelet tree
     * decomposition (1 level of decomposition in the high bands and
     * some specified number for the lowest LL band): 0x02.  */
    public final static int WT_DECOMP_SPACL = 2;

    /**
     * ID for the PACKET (as defined in JPEG 2000) wavelet tree
     * decomposition (2 levels of decomposition in the high bands and
     * some specified number for the lowest LL band): 0x01. */
    public final static int WT_DECOMP_PACKET = 1;

    /** The identifier for "main default" specified decomposition */
    public final static byte DEC_SPEC_MAIN_DEF = 0;

    /** The identifier for "component default" specified decomposition */
    public final static byte DEC_SPEC_COMP_DEF = 1;

    /** The identifier for "tile specific default" specified decomposition */
    public final static byte DEC_SPEC_TILE_DEF = 2;

    /** The identifier for "tile and component specific" specified
        decomposition */
    public final static byte DEC_SPEC_TILE_COMP = 3;

    /** The spec type for each tile and component. The first index is the
     * component index, the second is the tile index. NOTE: The tile specific
     * things are not supported yet. */
    // Use byte to save memory (no need for speed here).
    private byte specValType[];

    /** The main default decomposition */
    private int mainDefDecompType;

    /** The main default number of decomposition levels */
    private int mainDefLevels;

    /** The component main default decomposition, for each component. */
    private int compMainDefDecompType[];

    /** The component main default decomposition levels, for each component */
    private int compMainDefLevels[];

    /**
     * Constructs a new 'WTDecompSpec' for the specified number of components
     * and tiles, with the given main default decomposition type and number of 
     * levels.
     *
     * <P>NOTE: The tile specific things are not supported yet
     *
     * @param nc The number of components
     *
     * @param nt The number of tiles
     *
     * @param dec The main default decomposition type
     *
     * @param lev The main default number of decomposition levels
     *
     *
     * */
    public WTDecompSpec(int nc, int dec, int lev) {
        mainDefDecompType = dec;
        mainDefLevels = lev;
        specValType = new byte[nc];
    }

    /**
     * Sets the "component main default" decomposition type and number of
     * levels for the specified component. Both 'dec' and 'lev' can not be
     * negative at the same time.
     *
     * @param n The component index
     *
     * @param dec The decomposition type. If negative then the main default is
     * used.
     *
     * @param lev The number of levels. If negative then the main defaul is
     * used.
     *
     *
     * */
    public void setMainCompDefDecompType(int n, int dec, int lev) {
        if (dec < 0 && lev < 0) {
            throw new IllegalArgumentException();
        }
        // Set spec type and decomp
        specValType[n] = DEC_SPEC_COMP_DEF;
        if (compMainDefDecompType == null) {
            compMainDefDecompType = new int[specValType.length];
            compMainDefLevels = new int[specValType.length];
        }
        compMainDefDecompType[n] = (dec >= 0) ? dec : mainDefDecompType;
        compMainDefLevels[n] = (lev >= 0) ? lev : mainDefLevels;
        // For the moment disable it since other parts of JJ2000 do not
        // support this
        throw new NotImplementedError("Currently, in JJ2000, all components "+
                                      "and tiles must have the same "+
                                      "decomposition type and number of "+
                                      "levels");
    }

    /**
     * Returns the type of specification for the decomposition in the
     * specified component and tile. The specification type is one of:
     * 'DEC_SPEC_MAIN_DEF', 'DEC_SPEC_COMP_DEF', 'DEC_SPEC_TILE_DEF',
     * 'DEC_SPEC_TILE_COMP'.
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
    public byte getDecSpecType(int n) {
        return specValType[n];
    }

    /**
     * Returns the main default decomposition type.
     *
     * @return The main default decomposition type.
     *
     *
     * */
    public int getMainDefDecompType() {
        return mainDefDecompType;
    }

    /**
     * Returns the main default decomposition number of levels.
     *
     * @return The main default decomposition number of levels.
     *
     *
     * */
    public int getMainDefLevels() {
        return mainDefLevels;
    }

    /**
     * Returns the decomposition type to be used in component 'n' and tile
     * 't'.
     *
     * <P>NOTE: The tile specific things are not supported yet
     *
     * @param n The component index.
     *
     * @param t The tile index, in raster scan order
     *
     * @return The decomposition type to be used.
     *
     *
     * */
    public int getDecompType(int n) {
        switch (specValType[n]) {
        case DEC_SPEC_MAIN_DEF:
            return mainDefDecompType;
        case DEC_SPEC_COMP_DEF:
            return compMainDefDecompType[n];
        case DEC_SPEC_TILE_DEF:
            throw new NotImplementedError();
        case DEC_SPEC_TILE_COMP:
            throw new NotImplementedError();
        default:
            throw new Error("Internal JJ2000 error");
        }
    }

    /**
     * Returns the decomposition number of levels in component 'n' and tile
     * 't'.
     *
     * <P>NOTE: The tile specific things are not supported yet
     *
     * @param n The component index.
     *
     * @param t The tile index, in raster scan order
     *
     * @return The decomposition number of levels.
     *
     *
     * */
    public int getLevels(int n) {
        switch (specValType[n]) {
        case DEC_SPEC_MAIN_DEF:
            return mainDefLevels;
        case DEC_SPEC_COMP_DEF:
            return compMainDefLevels[n];
        case DEC_SPEC_TILE_DEF:
            throw new NotImplementedError();
        case DEC_SPEC_TILE_COMP:
            throw new NotImplementedError();
        default:
            throw new Error("Internal JJ2000 error");
        }
    }
}
