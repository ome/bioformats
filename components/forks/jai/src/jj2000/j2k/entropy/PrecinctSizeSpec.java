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
 * $RCSfile: PrecinctSizeSpec.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:04 $
 * $State: Exp $
 *
 * Class:                   PrecinctSizeSpec
 *
 * Description:             Specification of the precinct sizes
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

import jj2000.j2k.codestream.*;
import jj2000.j2k.wavelet.*;
import jj2000.j2k.image.*;
import jj2000.j2k.util.*;
import jj2000.j2k.*;

import java.util.*;
import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriteParamJava;

/**
 * This class extends ModuleSpec class for precinct partition sizes holding
 * purposes.
 *
 * <p>It stores the size a of precinct when precinct partition is used or not.
 * If precinct partition is used, we can have several packets for a given
 * resolution level whereas there is only one packet per resolution level if
 * no precinct partition is used.
 * */
public class PrecinctSizeSpec extends ModuleSpec {

    /** Name of the option */
    private static final String optName = "Cpp";

    /** Reference to wavelet number of decomposition levels for each
     * tile-component.  */
    private IntegerSpec dls;

    /**
     * Creates a new PrecinctSizeSpec object for the specified number of tiles
     * and components.
     *
     * @param nt The number of tiles
     *
     * @param nc The number of components
     *
     * @param type the type of the specification module i.e. tile specific,
     * component specific or both.
     *
     * @param dls Reference to the number of decomposition levels
     * specification
     * */
    public PrecinctSizeSpec(int nt, int nc, byte type, IntegerSpec dls) {
        super(nt, nc, type);
        this.dls = dls;
    }

    /**
     * Creates a new PrecinctSizeSpec object for the specified number of tiles
     * and components and the J2KImageWriteParamJava instance.
     *
     * @param nt The number of tiles
     *
     * @param nc The number of components
     *
     * @param type the type of the specification module i.e. tile specific,
     * component specific or both.
     *
     * @param imgsrc The image source (used to get the image size)
     *
     * @param wp The J2KImageWriteParamJava instance
     * */
    public PrecinctSizeSpec(int nt, int nc, byte type, BlkImgDataSrc imgsrc,
                          IntegerSpec dls, J2KImageWriteParamJava wp, String values) {
        super(nt, nc, type);

        this.dls = dls;

        // The precinct sizes are stored in a 2 elements vector array, the
        // first element containing a vector for the precincts width for each
        // resolution level and the second element containing a vector for the
        // precincts height for each resolution level. The precincts sizes are
        // specified from the highest resolution level to the lowest one
        // (i.e. 0).  If there are less elements than the number of
        // decomposition levels, the last element is used for all remaining
        // resolution levels (i.e. if the precincts sizes are specified only
        // for resolutions levels 5, 4 and 3, then the precincts size for
        // resolution levels 2, 1 and 0 will be the same as the size used for
        // resolution level 3).

        // Boolean used to know if we were previously reading a precinct's
        // size or if we were reading something else.
        boolean wasReadingPrecinctSize = false;

        String param = values;
/*
        if (values == null)
            param = defaultValue;   // the default is null
*/
        // Set precinct sizes to default i.e. 2^15 =
        // Markers.PRECINCT_PARTITION_DEF_SIZE
        Vector tmpv[] = new Vector[2];
        tmpv[0] = new Vector(); // ppx
        tmpv[0].addElement(new Integer(Markers.PRECINCT_PARTITION_DEF_SIZE));
        tmpv[1] = new Vector(); // ppy
        tmpv[1].addElement(new Integer(Markers.PRECINCT_PARTITION_DEF_SIZE));
        setDefault(tmpv);

        if ( param==null ) {
            // No precinct size specified in the command line so we do not try
            // to parse it.
            return;
        }

        // Precinct partition is used : parse arguments
        StringTokenizer stk = new StringTokenizer(param);
        byte curSpecType = SPEC_DEF; // Specification type of the
                                     // current parameter
        boolean[] tileSpec = null; // Tiles concerned by the specification
        boolean[] compSpec = null; // Components concerned by the specification
        int i, xIdx, ci, ti;

        boolean endOfParamList = false;
        String word = null; // current word
        Integer w, h;
        String errMsg = null;

        while((stk.hasMoreTokens() || wasReadingPrecinctSize) &&
              !endOfParamList){

            Vector v[] = new Vector[2]; // v[0] : ppx, v[1] : ppy

            // We do not read the next token if we were reading a precinct's
            // size argument as we have already read the next token into word.
            if ( !wasReadingPrecinctSize ) {
                word = stk.nextToken();
            }

            wasReadingPrecinctSize = false;

            switch(word.charAt(0)){

            case 't': // Tiles specification
                tileSpec = parseIdx(word,nTiles);
                if(curSpecType==SPEC_COMP_DEF) {
                    curSpecType = SPEC_TILE_COMP;
                }
                else {
                    curSpecType = SPEC_TILE_DEF;
                }
                break;

            case 'c': // Components specification
                compSpec = parseIdx(word,nComp);
                if(curSpecType==SPEC_TILE_DEF) {
                    curSpecType = SPEC_TILE_COMP;
                }
                else {
                    curSpecType = SPEC_COMP_DEF;
                }
                break;

            default:
                if ( !Character.isDigit(word.charAt(0)) ) {
                    errMsg = "Bad construction for parameter: "+word;
                    throw new IllegalArgumentException(errMsg);
                }

                // Initialises Vector objects
                v[0] = new Vector(); // ppx
                v[1] = new Vector(); // ppy

                while ( true ) {

                    // Now get the precinct dimensions
                    try {
                        // Get precinct width
                        w = new Integer(word);

                        // Get next word in argument list
                        try {
                            word = stk.nextToken();
                        }
                        catch (NoSuchElementException e) {
                            errMsg = "'"+optName+"' option : could not "+
                                "parse the precinct's width";
                            throw new IllegalArgumentException(errMsg);

                        }
                        // Get precinct height
                        h = new Integer(word);
                        if (w.intValue() != (1<<MathUtil.log2(w.intValue()))
                            || h.intValue() !=
                            (1<<MathUtil.log2(h.intValue())) ) {
                            errMsg = "Precinct dimensions must be powers of 2";
                            throw new IllegalArgumentException(errMsg);
                        }
                    }
                    catch( NumberFormatException e) {
                        errMsg = "'"+optName+"' option : the argument '"+word+
                            "' could not be parsed.";
                        throw new IllegalArgumentException(errMsg);
                    }
                    // Store packet's dimensions in Vector arrays
                    v[0].addElement(w);
                    v[1].addElement(h);

                    // Try to get the next token
                    if ( stk.hasMoreTokens() ) {
                        word = stk.nextToken();
                        if ( !Character.isDigit(word.charAt(0)) ) {
                            // The next token does not start with a digit so
                            // it is not a precinct's size argument. We set
                            // the wasReadingPrecinctSize booleen such that we
                            // know that we don't have to read another token
                            // and check for the end of the parameters list.
                            wasReadingPrecinctSize = true;

                            if(curSpecType==SPEC_DEF){
                                setDefault(v);
                            }
                            else if(curSpecType==SPEC_TILE_DEF){
                                for(ti=tileSpec.length-1; ti>=0; ti--) {
                                    if( tileSpec[ti] ){
                                        setTileDef(ti,v);
                                    }
                                }
                            }
                            else if(curSpecType==SPEC_COMP_DEF){
                                for(ci=compSpec.length-1; ci>=0; ci--) {
                                    if( compSpec[ci] ){
                                        setCompDef(ci,v);
                                    }
                                }
                            }
                            else{
                                for(ti=tileSpec.length-1; ti>=0; ti--){
                                    for(ci=compSpec.length-1; ci>=0 ; ci--){
                                        if(tileSpec[ti] && compSpec[ci]){
                                            setTileCompVal(ti,ci,v);
                                        }
                                    }
                                }
                            }
                            // Re-initialize
                            curSpecType = SPEC_DEF;
                            tileSpec = null;
                            compSpec = null;

                            // Go back to 'normal' parsing
                            break;
                        }
                        else {
                            // Next token starts with a digit so read it
                        }
                    }
                    else {
                        // We have reached the end of the parameters list so
                        // we store the last precinct's sizes and we stop
                        if(curSpecType==SPEC_DEF){
                            setDefault(v);
                        }
                        else if(curSpecType==SPEC_TILE_DEF){
                            for(ti=tileSpec.length-1; ti>=0; ti--) {
                                if( tileSpec[ti] ){
                                    setTileDef(ti,v);
                                }
                            }
                        }
                        else if(curSpecType==SPEC_COMP_DEF){
                            for(ci=compSpec.length-1; ci>=0; ci--) {
                                if( compSpec[ci] ){
                                    setCompDef(ci,v);
                                }
                            }
                        }
                        else{
                            for(ti=tileSpec.length-1; ti>=0; ti--){
                                for(ci=compSpec.length-1; ci>=0 ; ci--){
                                    if( tileSpec[ti] && compSpec[ci] ){
                                        setTileCompVal(ti,ci,v);
                                    }
                                }
                            }
                        }
                        endOfParamList = true;
                        break;
                    }
                } // while (true)
                break;
            } // switch
        } // while
    }

    /**
     * Returns the precinct partition width in component 'n' and tile 't' at
     * resolution level 'rl'. If the tile index is equal to -1 or if the
     * component index is equal to -1 it means that those should not be taken
     * into account.
     *
     * @param t The tile index, in raster scan order. Specify -1 if it is not
     * a specific tile.
     *
     * @param c The component index. Specify -1 if it is not a specific
     * component.
     *
     * @param rl The resolution level
     *
     * @return The precinct partition width in component 'c' and tile 't' at
     * resolution level 'rl'.
     * */
    public int getPPX(int t, int c, int rl) {
        int mrl, idx;
        Vector[] v=null;
        boolean tileSpecified = (t!=-1 ? true : false);
        boolean compSpecified = (c!=-1 ? true : false);

        // Get the maximum number of decomposition levels and the object
        // (Vector array) containing the precinct dimensions (width and
        // height) for the specified (or not) tile/component
        if ( tileSpecified && compSpecified ) {
            mrl = ((Integer)dls.getTileCompVal(t, c)).intValue();
            v = (Vector[])getTileCompVal(t, c);
        }
        else if ( tileSpecified && !compSpecified ) {
            mrl = ((Integer)dls.getTileDef(t)).intValue();
            v = (Vector[])getTileDef(t);
        }
        else if ( !tileSpecified && compSpecified ) {
            mrl = ((Integer)dls.getCompDef(c)).intValue();
            v = (Vector[])getCompDef(c);
        }
        else {
            mrl = ((Integer)dls.getDefault()).intValue();
            v = (Vector[])getDefault();
        }
        idx = mrl - rl;
        if ( v[0].size() > idx ) {
             return ((Integer)v[0].elementAt(idx)).intValue();
        }
        else {
            return ((Integer)v[0].elementAt(v[0].size()-1)).intValue();
        }
    }

    /**
     * Returns the precinct partition height in component 'n' and tile 't' at
     * resolution level 'rl'. If the tile index is equal to -1 or if the
     * component index is equal to -1 it means that those should not be taken
     * into account.
     *
     * @param t The tile index, in raster scan order. Specify -1 if it is not
     * a specific tile.
     *
     * @param c The component index. Specify -1 if it is not a specific
     * component.
     *
     * @param rl The resolution level.
     *
     * @return The precinct partition width in component 'n' and tile 't' at
     * resolution level 'rl'.
     * */
    public int getPPY(int t, int c, int rl) {
        int mrl, idx;
        Vector[] v=null;
        boolean tileSpecified = (t!=-1 ? true : false);
        boolean compSpecified = (c!=-1 ? true : false);

        // Get the maximum number of decomposition levels and the object
        // (Vector array) containing the precinct dimensions (width and
        // height) for the specified (or not) tile/component
        if ( tileSpecified && compSpecified ) {
            mrl = ((Integer)dls.getTileCompVal(t, c)).intValue();
            v = (Vector[])getTileCompVal(t, c);
        }
        else if ( tileSpecified && !compSpecified ) {
            mrl = ((Integer)dls.getTileDef(t)).intValue();
            v = (Vector[])getTileDef(t);
        }
        else if ( !tileSpecified && compSpecified ) {
            mrl = ((Integer)dls.getCompDef(c)).intValue();
            v = (Vector[])getCompDef(c);
        }
        else {
            mrl = ((Integer)dls.getDefault()).intValue();
            v = (Vector[])getDefault();
        }
        idx = mrl - rl;
        if ( v[1].size() > idx ) {
            return ((Integer)v[1].elementAt(idx)).intValue();
        }
        else {
            return ((Integer)v[1].elementAt(v[1].size()-1)).intValue();
        }
    }
}
