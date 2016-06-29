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
 * $RCSfile: ForwCompTransfSpec.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:13 $
 * $State: Exp $
 *
 * Class:                   ForwCompTransfSpec
 *
 * Description:             Component Transformation specification for encoder
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
package jj2000.j2k.image.forwcomptransf;

import jj2000.j2k.wavelet.analysis.*;
import jj2000.j2k.wavelet.*;
import jj2000.j2k.image.*;
import jj2000.j2k.util.*;
import jj2000.j2k.*;

import java.util.*;

import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriteParamJava;
/**
 * This class extends CompTransfSpec class in order to hold encoder specific
 * aspects of CompTransfSpec.
 *
 * @see CompTransfSpec
 * */
public class ForwCompTransfSpec extends CompTransfSpec implements FilterTypes {
    private String defaultValue = null;

    /**
     * Constructs a new 'ForwCompTransfSpec' for the specified number of
     * components and tiles and the arguments of <tt>optName</tt>
     * option. This constructor is called by the encoder. It also
     * checks that the arguments belongs to the recognized arguments
     * list.
     *
     * <P>This constructor chose the component transformation type
     * depending on the wavelet filters : RCT with w5x3 filter and ICT
     * with w9x7 filter. Note: All filters must use the same data
     * type.
     *
     * @param nt The number of tiles
     *
     * @param nc The number of components
     *
     * @param type the type of the specification module i.e. tile specific,
     * component specific or both.
     *
     * @param wfs The wavelet filter specifications
     * */
    public ForwCompTransfSpec(int nt, int nc, byte type, AnWTFilterSpec wfs,
                              J2KImageWriteParamJava wp, String values){
        super(nt,nc,type);

        String param = values;
        specified = values;
	if(values==null){
            // If less than three component, do not use any component
            // transformation
            if(nc<3) {
                setDefault("none");
                return;
            }
            // If the compression is lossless, uses RCT
            else if(wp.getLossless()) {
                setDefault("rct");
                return;
            } else {
                AnWTFilter[][] anfilt;
                int[] filtType = new int[nComp];
                for(int c=0; c<3; c++) {
                    anfilt = (AnWTFilter[][])wfs.getCompDef(c);
                    filtType[c] = anfilt[0][0].getFilterType();
                }

                // Check that the three first components use the same filters
                boolean reject = false;
                for(int c=1; c<3; c++){
                    if(filtType[c]!=filtType[0]) reject = true;
                }

                if(reject) {
                    setDefault("none");
                } else {
                    anfilt = (AnWTFilter[][])wfs.getCompDef(0);
                    if(anfilt[0][0].getFilterType()==W9X7) {
                        setDefault("ict");
                    } else {
                        setDefault("rct");
                    }
                }
            }

            // Each tile receives a component transform specification
            // according the type of wavelet filters that are used by the
            // three first components
            for(int t=0; t<nt; t++) {
                AnWTFilter[][] anfilt;
                int[] filtType = new int[nComp];
                for(int c=0; c<3; c++) {
                    anfilt = (AnWTFilter[][])wfs.getTileCompVal(t,c);
                    filtType[c] = anfilt[0][0].getFilterType();
                }

                // Check that the three components use the same filters
                boolean reject = false;
                for(int c=1; c<nComp;c++){
                    if(filtType[c]!=filtType[0])
                        reject = true;
                }

                if(reject) {
                    setTileDef(t,"none");
                } else {
                    anfilt = (AnWTFilter[][])wfs.getTileCompVal(t,0);
                    if(anfilt[0][0].getFilterType()==W9X7) {
                        setTileDef(t,"ict");
                    } else {
                        setTileDef(t,"rct");
                    }
                }
            }
            return;
        }

        if (param.equalsIgnoreCase("true")) {
            param = "on";
        }
        else if (param.equalsIgnoreCase("false")) {
            param = "off";
        }
        // Parse argument
        StringTokenizer stk = new StringTokenizer(param);
        String word; // current word
        byte curSpecType = SPEC_DEF; // Specification type of the
        // current parameter
        boolean[] tileSpec = null; // Tiles concerned by the
        // specification
        Boolean value;

        while(stk.hasMoreTokens()){
            word = stk.nextToken();

            switch(word.charAt(0)){
            case 't': // Tiles specification
                tileSpec = parseIdx(word,nTiles);
                if(curSpecType==SPEC_COMP_DEF) {
                    curSpecType = SPEC_TILE_COMP;
                } else {
                    curSpecType = SPEC_TILE_DEF;
                }
                break;
            case 'c': // Components specification
                throw new IllegalArgumentException("Component specific "+
                                                   " parameters"+
                                                   " not allowed with "+
                                                   "'-Mct' option");
            default:
                if(word.equals("off")) {
                    if(curSpecType==SPEC_DEF) {
                        setDefault("none");
                    } else if(curSpecType==SPEC_TILE_DEF) {
                        for(int i=tileSpec.length-1; i>=0; i--)
                            if(tileSpec[i]) {
                                setTileDef(i,"none");
                            }
                    }
                } else if(word.equals("on")) {
                    if(nc<3) {
                        setDefault("none");
                        break;
                    }

                    if(curSpecType==SPEC_DEF) { // Set arbitrarily the default
                        // value to RCT (later will be found the suitable
                        // component transform for each tile)
                        setDefault("rct");
                    } else if (curSpecType==SPEC_TILE_DEF) {
                        for(int i=tileSpec.length-1; i>=0; i--) {
                            if(tileSpec[i]) {
                                if(getFilterType(i,wfs)==W5X3) {
                                    setTileDef(i,"rct");
                                } else {
                                    setTileDef(i,"ict");
                                }
                            }
                        }
                    }
                } else {
                    throw new IllegalArgumentException("Default parameter of "+
                                                       "option Mct not"+
                                                       " recognized: "+param);
                }

                // Re-initialize
                curSpecType = SPEC_DEF;
                tileSpec = null;
                break;
            }
        }

        // Check that default value has been specified
        if(getDefault()==null) {
            // If not, set arbitrarily the default value to 'none' but
            // specifies explicitely a default value for each tile depending
            // on the wavelet transform that is used
            setDefault("none");

            for(int t=0; t<nt; t++) {
                if(isTileSpecified(t)) {
                    continue;
                }

                AnWTFilter[][] anfilt;
                int[] filtType = new int[nComp];
                for(int c=0; c<3; c++) {
                    anfilt = (AnWTFilter[][])wfs.getTileCompVal(t,c);
                    filtType[c] = anfilt[0][0].getFilterType();
                }

                // Check that the three components use the same filters
                boolean reject = false;
                for(int c=1; c<nComp;c++){
                    if(filtType[c]!=filtType[0])
                        reject = true;
                }

                if(reject) {
                    setTileDef(t,"none");
                } else {
                    anfilt = (AnWTFilter[][])wfs.getTileCompVal(t,0);
                    if(anfilt[0][0].getFilterType()==W9X7) {
                        setTileDef(t,"ict");
                    } else {
                        setTileDef(t,"rct");
                    }
                }
            }
        }

        // Check validity of component transformation of each tile compared to
        // the filter used.
        for(int t=nt-1; t>=0; t--) {

            if(((String)getTileDef(t)).equals("none")) {
                // No comp. transf is used. No check is needed
                continue;
            }
            else if(((String)getTileDef(t)).equals("rct")) {
                // Tile is using Reversible component transform
                int filterType = getFilterType(t,wfs);
                switch(filterType){
                case FilterTypes.W5X3: // OK
                    break;
                case FilterTypes.W9X7: // Must use ICT
                    if(isTileSpecified(t)){
                        // User has requested RCT -> Error
                        throw new IllegalArgumentException("Cannot use RCT "+
                                                           "with 9x7 filter "+
                                                           "in tile "+t);
                    }
                    else{ // Specify ICT for this tile
                        setTileDef(t,"ict");
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Default filter is "+
                                                       "not JPEG 2000 part"+
                                                       " I compliant");
                }
            } else { // ICT
                int filterType = getFilterType(t,wfs);
                switch(filterType) {
                case FilterTypes.W5X3: // Must use RCT
                    if(isTileSpecified(t)){
                        // User has requested ICT -> Error
                        throw new IllegalArgumentException("Cannot use ICT "+
                                                           "with filter 5x3 "+
                                                           "in tile "+t);
                    }
                    else{
                        setTileDef(t,"rct");
                    }
                    break;
                case FilterTypes.W9X7: // OK
                    break;
                default:
                    throw new IllegalArgumentException("Default filter is "+
                                                       "not JPEG 2000 part"+
                                                       " I compliant");

                }
            }
        }
    }

    /** Get the filter type common to all component of a given tile. If the
     * tile index is -1, it searches common filter type of default
     * specifications.
     *
     * @param t The tile index
     *
     * @param wfs The analysis filters specifications
     *
     * @return The filter type common to all the components
     *
     */
    private int getFilterType(int t, AnWTFilterSpec wfs){
        AnWTFilter[][] anfilt;
        int[] filtType = new int[nComp];
        for(int c=0;c<nComp; c++){
            if(t==-1)
                anfilt = (AnWTFilter[][])wfs.getCompDef(c);
            else
                anfilt = (AnWTFilter[][])wfs.getTileCompVal(t,c);
            filtType[c] = anfilt[0][0].getFilterType();
        }

        // Check that all filters are the same one
        boolean reject = false;
        for(int c=1; c<nComp;c++){
            if(filtType[c]!=filtType[0])
                reject = true;
        }
        if(reject){
            throw new IllegalArgumentException("Can not use component"+
                                               " transformation when "+
                                               "components do not use "+
                                               "the same filters");
        }
        return filtType[0];
    }
}
