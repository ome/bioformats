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
 * $RCSfile: Progression.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:05 $
 * $State: Exp $
 *
 * Class:                   Progression
 *
 * Description:             Holds the type(s) of progression
 *
 * Modified by:
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
package jj2000.j2k.entropy;

import jj2000.j2k.codestream.*;

/**
 * This class holds one of the different progression orders defined in
 * the bit stream. The type(s) of progression order are defined in the
 * ProgressionType interface. A Progression object is totally defined
 * by its component start and end, resolution level start and end and
 * layer start and end indexes. If no progression order change is
 * defined, there is only Progression instance.
 *
 * @see ProgressionType
 */
public class Progression implements ProgressionType{

    /** Progression type as defined in ProgressionType interface */
    public int type;

    /** Component index for the start of a progression */
    public int cs;

    /** Component index for the end of a progression. */
    public int ce;

    /** Resolution index for the start of a progression */
    public int rs;

    /** Resolution index for the end of a progression. */
    public int re;

    /** The index of the last layer. */
    public int lye;

    /**
     * Constructor.
     *
     * Builds a new Progression object with specified type and bounds
     * of progression.
     *
     * @param type The progression type
     *
     * @param cs The component index start
     *
     * @param ce The component index end
     *
     * @param rs The resolution level index start
     *
     * @param re The resolution level index end
     *
     * @param lye The layer index end
     *
     */
    public Progression(int type,int cs,int ce,int rs,int re,int lye){
	this.type = type;
	this.cs = cs;
	this.ce = ce;
	this.rs = rs;
	this.re = re;
	this.lye = lye;
    }

    public String toString(){
	String str =  "type= ";
	switch(type){
	case LY_RES_COMP_POS_PROG:
	    str += "layer, ";
	    break;
	case RES_LY_COMP_POS_PROG:
	    str += "res, ";
	    break;
	case RES_POS_COMP_LY_PROG:
	    str += "res-pos, ";
	    break;
	case POS_COMP_RES_LY_PROG:
	    str += "pos-comp, ";
	    break;
	case COMP_POS_RES_LY_PROG:
	    str += "pos-comp, ";
	    break;
	default:
	    throw new Error("Unknown progression type");
	}
	str += "comp.: "+cs+"-"+ce+", ";
	str += "res.: "+rs+"-"+re+", ";
	str += "layer: up to "+lye;
	return str;
    }
}
