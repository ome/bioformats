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
 * $RCSfile: QuantTypeSpec.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:18 $
 * $State: Exp $
 *
 * Class:                   QuantTypeSpec
 *
 * Description:             Quantization type specifications
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
package jj2000.j2k.quantization;

import jj2000.j2k.util.*;
import jj2000.j2k.*;

import java.util.*;

import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriteParamJava;

/**
 * This class extends ModuleSpec class in order to hold specifications about
 * the quantization type to use in each tile-component. Supported quantization
 * type are:<br>
 *
 * <ul>
 * <li> Reversible (no quantization)</li>
 * <li> Derived (the quantization step size is derived from the one of the
 * LL-subband)</li>
 * <li> Expounded (the quantization step size of each subband is signalled in
 * the codestream headers) </li>
 * </ul>
 *
 * @see ModuleSpec
 * */
public class QuantTypeSpec extends ModuleSpec{

    /**
     * Constructs an empty 'QuantTypeSpec' with specified number of tile and
     * components. This constructor is called by the decoder.
     *
     * @param nt Number of tiles
     *
     * @param nc Number of components
     *
     * @param type the type of the specification module i.e. tile specific,
     * component specific or both.
     * */
    public QuantTypeSpec(int nt, int nc, byte type){
	super(nt, nc, type);
    }


    /**
     * Constructs a new 'QuantTypeSpec' for the specified number of components
     * and tiles and the arguments of "-Qtype" option. This constructor is
     * called by the encoder.
     *
     * @param nt The number of tiles
     *
     * @param nc The number of components
     *
     * @param type the type of the specification module i.e. tile specific,
     * component specific or both.
     * */
    public QuantTypeSpec(int nt, int nc, byte type, J2KImageWriteParamJava wp, String values){
        super(nt, nc, type);

	if(values==null){
            if(wp.getLossless())
                setDefault("reversible");
            else
                setDefault("expounded");
            return;
	}

        // XXX: need to define it
        specified = values;
        String param = values;
	// Parse argument
	StringTokenizer stk = new StringTokenizer(param);
	String word; // current word
	byte curSpecValType = SPEC_DEF; // Specification type of the
	// current parameter
	boolean[] tileSpec = null; // Tiles concerned by the specification
	boolean[] compSpec = null; // Components concerned by the specification

	while(stk.hasMoreTokens()){
	    word = stk.nextToken().toLowerCase();

	    switch(word.charAt(0)){
	    case 't': // Tiles specification
 		tileSpec = parseIdx(word,nTiles);
		if(curSpecValType==SPEC_COMP_DEF){
		    curSpecValType = SPEC_TILE_COMP;
		}
		else{
		    curSpecValType = SPEC_TILE_DEF;
		}
 		break;
	    case 'c': // Components specification
		compSpec = parseIdx(word,nComp);
		if(curSpecValType==SPEC_TILE_DEF){
		    curSpecValType = SPEC_TILE_COMP;
		}
		else
		    curSpecValType = SPEC_COMP_DEF;
		break;
	    case 'r': // reversible specification
	    case 'd': // derived quantization step size specification
	    case 'e': // expounded quantization step size specification
		if(!word.equalsIgnoreCase("reversible") &&
		   !word.equalsIgnoreCase("derived") &&
		   !word.equalsIgnoreCase("expounded"))
                    throw new IllegalArgumentException("Unknown parameter "+
                                                       "for "+
                                                       "'-Qtype' option: "+
                                                       word);

                if(wp.getLossless() &&
                   ( word.equalsIgnoreCase("derived") ||
                    word.equalsIgnoreCase("expounded") ) )
                    throw new IllegalArgumentException("Cannot use non "+
                                                       "reversible "+
                                                       "quantization with "+
                                                       "'-lossless' option");

		if(curSpecValType==SPEC_DEF){
		    setDefault(word);
		}
		else if(curSpecValType==SPEC_TILE_DEF){
		    for(int i=tileSpec.length-1; i>=0; i--)
			if(tileSpec[i]){
			    setTileDef(i,word);
                        }
		}
		else if(curSpecValType==SPEC_COMP_DEF){
		    for(int i=compSpec.length-1; i>=0; i--)
			if(compSpec[i]){
			    setCompDef(i,word);
                        }
		}
		else{
		    for(int i=tileSpec.length-1; i>=0; i--){
			for(int j=compSpec.length-1; j>=0 ; j--){
			    if(tileSpec[i] && compSpec[j]){
				setTileCompVal(i,j,word);
                            }
			}
		    }
		}

		// Re-initialize
		curSpecValType = SPEC_DEF;
		tileSpec = null;
		compSpec = null;
		break;

	    default:
		throw new IllegalArgumentException("Unknown parameter for "+
                                                   "'-Qtype' option: "+word);
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
                if(wp.getLossless())
                    setDefault("reversible");
                else
                    setDefault("expounded");
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
   }

    /**
     * Returns true if given tile-component uses derived quantization step
     * size.
     *
     * @param t Tile index
     *
     * @param c Component index
     *
     * @return True if derived quantization step size
     * */
    public boolean isDerived(int t,int c){
	if( ((String)getTileCompVal(t,c)).equals("derived") )
	    return true;
	else
	    return false;
    }

    /**
     * Check the reversibility of the given tile-component.
     *
     * @param t The index of the tile
     *
     * @param c The index of the component
     *
     * @return Whether or not the tile-component is reversible
     * */
    public boolean isReversible(int t,int c){
	if( ((String)getTileCompVal(t,c)).equals("reversible") )
	    return true;
	else
	    return false;
    }

    /**
     * Check the reversibility of the whole image.
     *
     * @return Whether or not the whole image is reversible
     * */
    public boolean isFullyReversible(){
	// The whole image is reversible if default specification is
	// rev and no tile default, component default and
	// tile-component value has been specificied
	if( ((String)getDefault()).equals("reversible") ){
	    for(int t=nTiles-1; t>=0; t--)
		for(int c=nComp-1; c>=0; c--)
		    if(specValType[t][c]!=SPEC_DEF)
			return false;
	    return true;
	}

	return false;
    }

    /**
     * Check the irreversibility of the whole image.
     *
     * @return Whether or not the whole image is reversible
     * */
    public boolean isFullyNonReversible(){
	// The whole image is irreversible no tile-component is reversible
	for(int t=nTiles-1; t>=0; t--)
	    for(int c=nComp-1; c>=0; c--)
		if( ((String)getSpec(t,c)).equals("reversible") )
		    return false;
	return true;
    }

}
