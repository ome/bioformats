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
 * $RCSfile: ProgressionSpec.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:05 $
 * $State: Exp $
 *
 * Class:                   ProgressionSpec
 *
 * Description:             Specification of the progression(s) type(s) and
 *                          changes of progression.
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
package jj2000.j2k.entropy;

import java.util.*;

import jj2000.j2k.codestream.*;
import jj2000.j2k.wavelet.*;
import jj2000.j2k.image.*;
import jj2000.j2k.util.*;
import jj2000.j2k.*;

import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriteParamJava;

/**
 * This class extends ModuleSpec class for progression type(s) and progression
 * order changes holding purposes.
 *
 * <P>It stores the progression type(s) used in the codestream. There can be
 * only one progression type or several ones if progression order changes are
 * used (POC markers).
 * */
public class ProgressionSpec extends ModuleSpec {

    /**
     * Creates a new ProgressionSpec object for the specified number of tiles
     * and components.
     *
     * @param nt The number of tiles
     *
     * @param nc The number of components
     *
     * @param type the type of the specification module i.e. tile specific,
     * component specific or both. The ProgressionSpec class should only be
     * used only with the type ModuleSpec.SPEC_TYPE_TILE.
     * */
    public ProgressionSpec(int nt, int nc, byte type) {
        super(nt, nc, type);
        if ( type !=  ModuleSpec.SPEC_TYPE_TILE ) {
            throw new Error("Illegal use of class ProgressionSpec !");
        }
    }

    /**
     * Creates a new ProgressionSpec object for the specified number of
     * tiles, components and the J2KImageWriteParamJava instance.
     *
     * @param nt The number of tiles
     *
     * @param nc The number of components
     *
     * @param nl The number of layer
     *
     * @param dls The number of decomposition levels specifications
     *
     * @param type the type of the specification module. The ProgressionSpec
     * class should only be used only with the type ModuleSpec.SPEC_TYPE_TILE.
     *
     * @param wp The J2KImageWriteParamJava instance
     * */
    public ProgressionSpec(int nt,int nc,int nl,IntegerSpec dls,byte type,
			   J2KImageWriteParamJava wp, String values){
        super(nt,nc,type);

        specified = values;

        String param  = values;
	Progression[] prog;
	int mode=-1;

        if(values == null){ // No parameter specified
            if(wp.getROIs() == null) {
                mode = checkProgMode("res");
            }
            else {
                mode = checkProgMode("layer");
            }

	    if(mode==-1){
		String errMsg = "Unknown progression type : '"+param+"'";
		throw new IllegalArgumentException(errMsg);
	    }
	    prog = new Progression[1];
	    prog[0] = new Progression(mode,0,nc,0,dls.getMax()+1,nl);
            setDefault(prog);
            return;
        }

        StringTokenizer stk = new StringTokenizer(param);
        byte curSpecType = SPEC_DEF; // Specification type of the
                                     // current parameter
        boolean[] tileSpec = null; // Tiles concerned by the specification
        String word   = null; // current word
        String errMsg = null; // Error message
	boolean needInteger = false; // True if an integer value is expected
	int intType = 0; // Type of read integer value (0=index of first
	// component, 1= index of first resolution level, 2=index of last
	// layer, 3= index of last component, 4= index of last resolution
        // level)
	Vector progression = new Vector();
	int tmp = 0;
	Progression curProg = null;

        while(stk.hasMoreTokens()){
	    word = stk.nextToken();

            switch(word.charAt(0)){
            case 't':
		// If progression were previously found, store them
		if(progression.size()>0){
		    // Ensure that all information has been taken
		    curProg.ce = nc;
		    curProg.lye = nl;
		    curProg.re = dls.getMax()+1;
		    prog = new Progression[progression.size()];
		    progression.copyInto(prog);
		    if(curSpecType==SPEC_DEF){
			setDefault(prog);
		    }
		    else if(curSpecType==SPEC_TILE_DEF){
			for(int i=tileSpec.length-1; i>=0; i--)
			    if(tileSpec[i]){
				setTileDef(i,prog);
			    }
		    }
		}
		progression.removeAllElements();
		intType=-1;
		needInteger = false;

                // Tiles specification
                tileSpec = parseIdx(word,nTiles);
		curSpecType = SPEC_TILE_DEF;
  		break;
            default:
		// Here, words is either a Integer (progression bound
		// index) or a String (progression order type). This
		// is determined by the value of needInteger.
		if(needInteger){ // Progression bound info
		    try{
			tmp = (new Integer(word)).intValue();
		    }
		    catch(NumberFormatException e){
			// Progression has missing parameters
			throw new IllegalArgumentException("Progression "+
							   "order"+
							   " specification "+
							   "has missing "+
							   "parameters: "+
							   param);
		    }

		    switch(intType){
		    case 0: // cs
			if(tmp<0 || tmp>dls.getMax()+1)
			    throw new
				IllegalArgumentException("Invalid comp_start "+
							 "in '-Aptype' option");
			curProg.cs = tmp; break;
		    case 1: // rs
			if(tmp<0 || tmp>nc)
			    throw new
				IllegalArgumentException("Invalid res_start "+
							 "in '-Aptype' option");

			curProg.rs = tmp; break;
		    case 2: // lye
			if(tmp<0)
			    throw new
				IllegalArgumentException("Invalid layer_end "+
							 "in '-Aptype' option");
			if (tmp>nl) {
			    tmp = nl;
			}
			curProg.lye = tmp; break;
		    case 3: // ce
			if(tmp<0)
			    throw new
				IllegalArgumentException("Invalid comp_end "+
							 "in '-Aptype' option");
			if( tmp>(dls.getMax()+1)) {
			    tmp = dls.getMax()+1;
			}
			curProg.ce = tmp; break;
		    case 4: // re
			if(tmp<0)
			    throw new
				IllegalArgumentException("Invalid res_end "+
							 "in '-Aptype' option");
			if (tmp>nc) {
			    tmp = nc;
			}
			curProg.re = tmp; break;
		    }

		    if(intType<4){
			intType++;
			needInteger = true;
			break;
		    }
		    else if(intType==4){
			intType = 0;
			needInteger = false;
			break;
		    }
		    else{
			throw new Error("Error in usage of 'Aptype' "+
					"option: "+param);
		    }
		}

		if(!needInteger){ // Progression type info
		    mode = checkProgMode(word);
		    if(mode==-1 ){
			errMsg = "Unknown progression type : '"+word+"'";
			throw new IllegalArgumentException(errMsg);
		    }
		    needInteger = true;
		    intType = 0;
		    if(progression.size()==0)
			curProg = new Progression(mode,0,nc,0,dls.getMax()+1,
                                                  nl);
		    else{
			curProg = new Progression(mode,0,nc,0,dls.getMax()+1,
                                                  nl);
		    }
		    progression.addElement(curProg);
		}
            } // switch
        } // while

	if(progression.size()==0){ // No progression defined
            // Set it arbitrarily to layer progressive
            if(wp.getROIs() == null) {
                mode = checkProgMode("res");
            }
            else {
                mode = checkProgMode("layer");
            }

	    if(mode==-1){
		errMsg = "Unknown progression type : '"+param+"'";
		throw new IllegalArgumentException(errMsg);
	    }
	    prog = new Progression[1];
	    prog[0] = new Progression(mode,0,nc,0,dls.getMax()+1,nl);
            setDefault(prog);
            return;
 	}

	// Ensure that all information has been taken
	curProg.ce = nc;
	curProg.lye = nl;
	curProg.re = dls.getMax()+1;

	// Store found progression
	prog = new Progression[progression.size()];
	progression.copyInto(prog);

	if(curSpecType==SPEC_DEF){
	    setDefault(prog);
	}
	else if(curSpecType==SPEC_TILE_DEF){
	    for(int i=tileSpec.length-1; i>=0; i--)
		if(tileSpec[i]){
		    setTileDef(i,prog);
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

            // If some tile-component have received no specification, they are
            // arbitrarily set to 'layer' progressive.
            if(ndefspec!=0){
	        if(wp.getROIs() == null) {
                    mode = checkProgMode("res");
                } else {
                    mode = checkProgMode("layer");
                }
                if(mode==-1){
                    errMsg = "Unknown progression type : '"+param+"'";
                    throw new IllegalArgumentException(errMsg);
                }
                prog = new Progression[1];
                prog[0] = new Progression(mode,0,nc,0,dls.getMax()+1,nl);
                setDefault(prog);
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
     * Check if the progression mode exists and if so, return its integer
     * value. It returns -1 otherwise.
     *
     * @param mode The progression mode stored in a string
     *
     * @return The integer value of the progression mode or -1 if the
     * progression mode does not exist.
     *
     * @see ProgressionType
     * */
    private int checkProgMode(String mode) {
        if(mode.equals("res")){
            return ProgressionType.RES_LY_COMP_POS_PROG;
        }
        else if( mode.equals("layer") ) {
            return ProgressionType.LY_RES_COMP_POS_PROG;
        }
        else if( mode.equals("pos-comp") ) {
            return ProgressionType.POS_COMP_RES_LY_PROG;
        }
        else if ( mode.equals("comp-pos") ) {
            return ProgressionType.COMP_POS_RES_LY_PROG;
        }
        else if ( mode.equals("res-pos") ) {
            return ProgressionType.RES_POS_COMP_LY_PROG;
        }
        else {
            // No corresponding progression mode, we return -1.
            return -1;
        }
    }
}
