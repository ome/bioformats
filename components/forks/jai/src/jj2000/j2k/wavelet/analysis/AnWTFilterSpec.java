/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2017 Open Microscopy Environment:
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
 * $RCSfile: AnWTFilterSpec.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:29 $
 * $State: Exp $
 *
 * Class:                   AnWTFilterSpec
 *
 * Description:             Analysis filters specification
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
package jj2000.j2k.wavelet.analysis;

import jj2000.j2k.quantization.*;
import jj2000.j2k.util.*;
import jj2000.j2k.*;

import java.util.*;

import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriteParamJava;

/**
 * This class extends ModuleSpec class for analysis filters specification
 * holding purpose.
 *
 * @see ModuleSpec
 * */
public class AnWTFilterSpec extends ModuleSpec {

    /** The reversible default filter */
    private final static String REV_FILTER_STR = "w5x3";

    /** The non-reversible default filter */
    private final static String NON_REV_FILTER_STR = "w9x7";

    /**
     * Constructs a new 'AnWTFilterSpec' for the specified number of
     * components and tiles.
     *
     * @param nt The number of tiles
     *
     * @param nc The number of components
     *
     * @param type the type of the specification module i.e. tile specific,
     * component specific or both.
     *
     * @param qts Quantization specifications
     * */
    public AnWTFilterSpec(int nt, int nc, byte type,
                          QuantTypeSpec qts, J2KImageWriteParamJava wp, String values){
        super(nt, nc, type);
/*
        // Check parameters
        pl.checkList(AnWTFilter.OPT_PREFIX,
		     pl.toNameArray(AnWTFilter.getParameterInfo()));
*/
        specified = values;
	String param = specified;
        boolean isFilterSpecified = true;

	// No parameter specified
	if(values==null){
            isFilterSpecified = false;

            if(wp.getLossless()) {
                setDefault(parseFilters(REV_FILTER_STR));
                return;
            }

	    // If no filter is specified through the command-line, use
	    // REV_FILTER_STR or NON_REV_FILTER_STR according to the
	    // quantization type
	    for(int t=nt-1;t>=0;t--){
		for(int c=nc-1;c>=0;c--){
		    switch(qts.getSpecValType(t,c)){
		    case SPEC_DEF:
			if(getDefault()==null){
                            if( wp.getLossless() )
                                setDefault(parseFilters(REV_FILTER_STR));
			    if( ((String)qts.getDefault()).
                                equals("reversible") ){
				setDefault(parseFilters(REV_FILTER_STR));
			    }
			    else{
				setDefault(parseFilters(NON_REV_FILTER_STR));
			    }
			}
			specValType[t][c] = SPEC_DEF;
			break;
		    case SPEC_COMP_DEF:
			if(!isCompSpecified(c)){
			    if( ((String)qts.getCompDef(c)).
                                equals("reversible") ){
				setCompDef(c,parseFilters(REV_FILTER_STR));
			    }
			    else{
				setCompDef(c,parseFilters(NON_REV_FILTER_STR));
			    }
			}
			specValType[t][c] = SPEC_COMP_DEF;
			break;
		    case SPEC_TILE_DEF:
			if(!isTileSpecified(t)){
			    if( ((String)qts.getTileDef(t)).
                                equals("reversible") ){
				setTileDef(t,parseFilters(REV_FILTER_STR));
			    }
			    else{
				setTileDef(t,parseFilters(NON_REV_FILTER_STR));
			    }
			}
			specValType[t][c] = SPEC_TILE_DEF;
			break;
		    case SPEC_TILE_COMP:
			if(!isTileCompSpecified(t,c)){
			    if(((String)qts.getTileCompVal(t,c)).
                               equals("reversible")){
				setTileCompVal(t,c,parseFilters(REV_FILTER_STR));
			    }
			    else{
				setTileCompVal(t,c,
                                               parseFilters(NON_REV_FILTER_STR));
			    }
			}
			specValType[t][c] = SPEC_TILE_COMP;
			break;
		    default:
			throw new IllegalArgumentException("Unsupported "+
							   "specification type");
		    }
		}
	    }

            return;
	}

	// Parse argument
	StringTokenizer stk = new StringTokenizer(param);
	String word; // current word
	byte curSpecType = SPEC_DEF; // Specification type of the
	// current parameter
	boolean[] tileSpec = null; // Tiles concerned by the specification
	boolean[] compSpec = null; // Components concerned by the specification
	AnWTFilter[][] filter;

	while(stk.hasMoreTokens()){
	    word = stk.nextToken();

	    switch(word.charAt(0)){
	    case 't': // Tiles specification
	    case 'T': // Tiles specification
 		tileSpec = parseIdx(word,nTiles);
		if(curSpecType==SPEC_COMP_DEF)
		    curSpecType = SPEC_TILE_COMP;
		else
		    curSpecType = SPEC_TILE_DEF;
 		break;
	    case 'c': // Components specification
	    case 'C': // Components specification
		compSpec = parseIdx(word,nComp);
		if(curSpecType==SPEC_TILE_DEF)
		    curSpecType = SPEC_TILE_COMP;
		else
		    curSpecType = SPEC_COMP_DEF;
		break;
	    case 'w': // WT filters specification
	    case 'W': // WT filters specification
                if(wp.getLossless() &&
                   word.equalsIgnoreCase("w9x7") )  {
                    throw new IllegalArgumentException("Cannot use non "+
                                                       "reversible "+
                                                       "wavelet transform with"+
                                                       " '-lossless' option");

                }

		filter = parseFilters(word);
		if(curSpecType==SPEC_DEF){
		    setDefault(filter);
		}
		else if(curSpecType==SPEC_TILE_DEF){
		    for(int i=tileSpec.length-1; i>=0; i--)
			if(tileSpec[i]){
			    setTileDef(i,filter);
                        }
		}
		else if(curSpecType==SPEC_COMP_DEF){
		    for(int i=compSpec.length-1; i>=0; i--)
			if(compSpec[i]){
			    setCompDef(i,filter);
                        }
		}
		else{
		    for(int i=tileSpec.length-1; i>=0; i--){
			for(int j=compSpec.length-1; j>=0 ; j--){
			    if(tileSpec[i] && compSpec[j]){
				setTileCompVal(i,j,filter);
                            }
			}
		    }
		}

		// Re-initialize
		curSpecType = SPEC_DEF;
		tileSpec = null;
		compSpec = null;
		break;

	    default:
		throw new IllegalArgumentException("Bad construction for "+
						   "parameter: "+word);
	    }
	}

        // Check that default value has been specified
        if(getDefault()==null){
            int ndefspec = 0;
            for(int t=nt-1; t>=0; t--){
                for(int c=nc-1; c>=0 ; c--){
                    if(specValType[t][c] == SPEC_DEF){
                        ndefspec++;
                    }
                }
            }

            // If some tile-component have received no specification, it takes
            // the default value
            if(ndefspec!=0){
                if( ((String)qts.getDefault()).equals("reversible") )
                    setDefault(parseFilters(REV_FILTER_STR));
                else
                    setDefault(parseFilters(NON_REV_FILTER_STR));
            }
            else{
                // All tile-component have been specified, takes the first
                // tile-component value as default.
                setDefault(getTileCompVal(0,0));
                switch(specValType[0][0]){
                case SPEC_TILE_DEF:
                    for(int c=nc-1; c>=0; c--){
                        if(specValType[0][c]==SPEC_TILE_DEF)
                            specValType[0][c] = SPEC_DEF;
                    }
                    tileDef[0] = null;
                    break;
                case SPEC_COMP_DEF:
                    for(int t=nt-1; t>=0; t--){
                        if(specValType[t][0]==SPEC_COMP_DEF)
                            specValType[t][0] = SPEC_DEF;
                    }
                    compDef[0] = null;
                    break;
                case SPEC_TILE_COMP:
                    specValType[0][0] = SPEC_DEF;
                    tileCompVal.put("t0c0",null);
                    break;
                }
            }
        }

        // Check consistency between filter and quantization type
        // specification
        for(int t=nt-1;t>=0;t--){
            for(int c=nc-1;c>=0;c--){
                // Reversible quantization
                if( ((String)qts.getTileCompVal(t,c)).equals("reversible")){
                    // If filter is reversible, it is OK
                    if(isReversible(t,c)) continue;

                    // If no filter has been defined, use reversible filter
                    if(!isFilterSpecified){
                        setTileCompVal(t,c,parseFilters(REV_FILTER_STR));
                    }
                    else{
                        // Non reversible filter specified -> Error
                        throw new IllegalArgumentException("Filter of "+
                                                           "tile-component"+
                                                           " ("+t+","+c+") does"+
                                                           " not allow "+
                                                           "reversible "+
                                                           "quantization. "+
                                                           "Specify '-Qtype "+
                                                           "expounded' or "+
                                                           "'-Qtype derived'"+
                                                           "in "+
                                                           "the command line.");
                    }
                }
                else{ // No reversible quantization
                    // No reversible filter -> OK
                    if(!isReversible(t,c)) continue;

                    // If no filter has been specified, use non-reversible
                    // filter
                    if(!isFilterSpecified){
                        setTileCompVal(t,c,parseFilters(NON_REV_FILTER_STR));
                    }
                    else{
                        // Reversible filter specified -> Error
                        throw new IllegalArgumentException("Filter of "+
                                                           "tile-component"+
                                                           " ("+t+","+c+") does"+
                                                           " not allow "+
                                                           "non-reversible "+
                                                           "quantization. "+
                                                           "Specify '-Qtype "+
                                                           "reversible' in "+
                                                           "the command line");
                    }
                }
            }
        }
    }

    /**
     * Parse filters from the given word
     *
     * @param word String to parse
     *
     * @return Analysis wavelet filter (first dimension: by direction,
     * second dimension: by decomposition levels)
     */
    private AnWTFilter[][] parseFilters(String word){
	AnWTFilter[][] filt=new AnWTFilter[2][1];
	if(word.equalsIgnoreCase("w5x3")){
	    filt[0][0]=new AnWTFilterIntLift5x3();
	    filt[1][0]=new AnWTFilterIntLift5x3();
	    return filt;
	}
	else if(word.equalsIgnoreCase("w9x7")){
	    filt[0][0]=new AnWTFilterFloatLift9x7();
	    filt[1][0]=new AnWTFilterFloatLift9x7();
	    return filt;
	}
	else{
	    throw new
		IllegalArgumentException("Non JPEG 2000 part I filter: "
					 +word);
	}
    }

    /**
     * Returns the data type used by the filters in this object, as defined in
     * the 'DataBlk' interface for specified tile-component.
     *
     * @param t Tile index
     *
     * @param c Component index
     *
     * @return The data type of the filters in this object
     *
     * @see jj2000.j2k.image.DataBlk
     * */
    public int getWTDataType(int t,int c){
	AnWTFilter[][] an = (AnWTFilter[][])getSpec(t,c);
	return an[0][0].getDataType();
    }

    /**
     * Returns the horizontal analysis filters to be used in component 'n' and
     * tile 't'.
     *
     * <P>The horizontal analysis filters are returned in an array of
     * AnWTFilter. Each element contains the horizontal filter for each
     * resolution level starting with resolution level 1 (i.e. the analysis
     * filter to go from resolution level 1 to resolution level 0). If there
     * are less elements than the maximum resolution level, then the last
     * element is assumed to be repeated.
     *
     * @param t The tile index, in raster scan order
     *
     * @param c The component index.
     *
     * @return The array of horizontal analysis filters for component 'n' and
     * tile 't'.
     * */
    public AnWTFilter[] getHFilters(int t, int c) {
	AnWTFilter[][] an = (AnWTFilter[][])getSpec(t,c);
	return an[0];
    }

    /**
     * Returns the vertical analysis filters to be used in component 'n' and
     * tile 't'.
     *
     * <P>The vertical analysis filters are returned in an array of
     * AnWTFilter. Each element contains the vertical filter for each
     * resolution level starting with resolution level 1 (i.e. the analysis
     * filter to go from resolution level 1 to resolution level 0). If there
     * are less elements than the maximum resolution level, then the last
     * element is assumed to be repeated.
     *
     * @param t The tile index, in raster scan order
     *
     * @param c The component index.
     *
     * @return The array of horizontal analysis filters for component 'n' and
     * tile 't'.
     * */
    public AnWTFilter[] getVFilters(int t,int c) {
	AnWTFilter[][] an = (AnWTFilter[][])getSpec(t,c);
	return an[1];
    }

    /** Debugging method */
    public String toString(){
	String str = "";
	AnWTFilter[][] an;

	str += "nTiles="+nTiles+"\nnComp="+nComp+"\n\n";

	for(int t=0; t<nTiles; t++){
	    for(int c=0; c<nComp; c++){
		an = (AnWTFilter[][])getSpec(t,c);

		str += "(t:"+t+",c:"+c+")\n";

		// Horizontal filters
		str += "\tH:";
		for(int i=0; i<an[0].length; i++)
		    str += " "+an[0][i];
		// Horizontal filters
		str += "\n\tV:";
		for(int i=0; i<an[1].length; i++)
		    str += " "+an[1][i];
		str += "\n";
	    }
	}

	return str;
    }

    /**
     * Check the reversibility of filters contained is the given
     * tile-component.
     *
     * @param t The index of the tile
     *
     * @param c The index of the component
     * */
    public boolean isReversible(int t,int c){
	// Note: no need to buffer the result since this method is
	// normally called once per tile-component.
	AnWTFilter[]
	    hfilter = getHFilters(t,c),
	    vfilter = getVFilters(t,c);

	// As soon as a filter is not reversible, false can be returned
	for(int i=hfilter.length-1; i>=0; i--)
	    if(!hfilter[i].isReversible() || !vfilter[i].isReversible())
		return false;
	return true;
    }
}
