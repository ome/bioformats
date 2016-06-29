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

/**
 * $RCSfile: IntegerSpec.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:01:58 $
 * $State: Exp $
 *
 * Class:                   IntegerSpec
 *
 * Description:             Holds specs corresponding to an Integer
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
package jj2000.j2k;

import jj2000.j2k.util.*;
import jj2000.j2k.*;

import java.util.*;
import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriteParamJava;

/**
 * This class extends ModuleSpec and is responsible of Integer
 * specifications for each tile-component.
 *
 * @see ModuleSpec
 * */
public class IntegerSpec extends ModuleSpec{


    /** The largest value of type int */
    protected static int MAX_INT = Integer.MAX_VALUE;

    /**
     * Constructs a new 'IntegerSpec' for the specified number of
     * tiles and components and with allowed type of
     * specifications. This constructor is normally called at decoder
     * side.
     *
     * @param nt The number of tiles
     *
     * @param nc The number of components
     *
     * @param type The type of allowed specifications
     *
     * */
    public IntegerSpec(int nt,int nc,byte type){
        super(nt,nc,type);
    }

    /**
     * Constructs a new 'IntegerSpec' for the specified number of
     * tiles and components, the allowed specifications type
     * instance. This constructor is normally called at
     * encoder side and parse arguments of specified option.
     *
     * @param nt The number of tiles
     *
     * @param nc The number of components
     *
     * @param type The allowed specifications type
     *
     * @param defaultValue The name of the option to process
     *
     * */
    public IntegerSpec(int nt, int nc, byte type, J2KImageWriteParamJava wp, String values,
                         String defaultValue) {
        super(nt,nc,type);

        if(values==null){ // No parameter specified
            try{
                setDefault(new Integer(defaultValue));
            }
            catch(NumberFormatException e){
                    throw new IllegalArgumentException("Non recognized value"+
                                                       " for option -"+
                                                       ": "+defaultValue);
            }
            return;
        }

        Integer value;

	// Parse argument
	StringTokenizer stk = new StringTokenizer(values);
	String word; // current word
	byte curSpecType = SPEC_DEF; // Specification type of the
	// current parameter
	boolean[] tileSpec = null; // Tiles concerned by the specification
	boolean[] compSpec = null; // Components concerned by the specification

	while(stk.hasMoreTokens()){
	    word = stk.nextToken();

	    switch(word.charAt(0)){
	    case 't': // Tiles specification
  		tileSpec = parseIdx(word,nTiles);
 		if(curSpecType==SPEC_COMP_DEF)
 		    curSpecType = SPEC_TILE_COMP;
 		else
 		    curSpecType = SPEC_TILE_DEF;
  		break;
	    case 'c': // Components specification
 		compSpec = parseIdx(word,nComp);
 		if(curSpecType==SPEC_TILE_DEF)
 		    curSpecType = SPEC_TILE_COMP;
 		else
 		    curSpecType = SPEC_COMP_DEF;
 		break;
            default:
                try{
                    value = new Integer(word);
                }
                catch(NumberFormatException e){
                    throw new IllegalArgumentException("Non recognized value"+
                                                       " for option -: "+word);
                }

		if(curSpecType==SPEC_DEF){
		    setDefault(value);
		}
		else if(curSpecType==SPEC_TILE_DEF){
		    for(int i=tileSpec.length-1; i>=0; i--)
			if(tileSpec[i]){
			    setTileDef(i,value);
                        }
		}
		else if(curSpecType==SPEC_COMP_DEF){
		    for(int i=compSpec.length-1; i>=0; i--)
			if(compSpec[i]){
			    setCompDef(i,value);
                        }
		}
		else{
		    for(int i=tileSpec.length-1; i>=0; i--){
			for(int j=compSpec.length-1; j>=0 ; j--){
			    if(tileSpec[i] && compSpec[j]){
				setTileCompVal(i,j,value);
                            }
			}
		    }
		}

		// Re-initialize
		curSpecType = SPEC_DEF;
		tileSpec = null;
		compSpec = null;
		break;
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
                try{
                    setDefault(new Integer(defaultValue));
                }
                catch(NumberFormatException e){
                    throw new IllegalArgumentException("Non recognized value"+
                                                       " for option - : " + defaultValue);
                }
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
     * Get the maximum value of each tile-component
     *
     * @return The maximum value
     *
     */
    public int getMax(){
	int max = ((Integer)def).intValue();
	int tmp;

	for(int t=0; t<nTiles; t++){
	    for(int c=0; c<nComp; c++){
		tmp = ((Integer)getSpec(t,c)).intValue();
		if(max<tmp)
		    max = tmp;
	    }
	}

	return max;
    }

    /**
     * Get the minimum value of each tile-component
     *
     * @return The minimum value
     *
     */
    public int getMin(){
	int min = ((Integer)def).intValue();
	int tmp;

	for(int t=0; t<nTiles; t++){
	    for(int c=0; c<nComp; c++){
		tmp = ((Integer)getSpec(t,c)).intValue();
		if(min>tmp)
		    min = tmp;
	    }
	}

	return min;
    }

    /**
     * Get the maximum value of each tile for specified component
     *
     * @param c The component index
     *
     * @return The maximum value
     *
     */
    public int getMaxInComp(int c){
	int max = 0;
	int tmp;

	for(int t=0; t<nTiles; t++){
	    tmp = ((Integer)getSpec(t,c)).intValue();
	    if(max<tmp)
		max = tmp;
	}

	return max;
    }

    /**
     * Get the minimum value of each tile for specified component
     *
     * @param c The component index
     *
     * @return The minimum value
     *
     */
    public int getMinInComp(int c){
	int min = MAX_INT; // Big value
	int tmp;

	for(int t=0; t<nTiles; t++){
	    tmp = ((Integer)getSpec(t,c)).intValue();
	    if(min>tmp)
		min = tmp;
	}

	return min;
    }

    /**
     * Get the maximum value of each component in specified tile
     *
     * @param t The tile index
     *
     * @return The maximum value
     *
     */
    public int getMaxInTile(int t){
	int max = 0;
	int tmp;

	for(int c=0; c<nComp; c++){
	    tmp = ((Integer)getSpec(t,c)).intValue();
	    if(max<tmp)
		max = tmp;
	}

	return max;
    }

    /**
     * Get the minimum value of each component in specified tile
     *
     * @param t The tile index
     *
     * @return The minimum value
     *
     */
    public int getMinInTile(int t){
	int min = MAX_INT; // Big value
	int tmp;

	for(int c=0; c<nComp; c++){
	    tmp = ((Integer)getSpec(t,c)).intValue();
	    if(min>tmp)
		min = tmp;
	}

	return min;
    }
}

