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
 * $RCSfile: ModuleSpec.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:01:58 $
 * $State: Exp $
 *
 * Class:                   ModuleSpec
 *
 * Description:             Generic class for storing module specs
 *
 *                           from WTFilterSpec (Diego Santa Cruz)
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
package jj2000.j2k;

import java.awt.Point;
import java.util.*;

/**
 * This generic class is used to handle values to be used by a module for each
 * tile and component.  It uses attribute to determine which value to use. It
 * should be extended by each module needing this feature.
 *
 * This class might be used for values that are only tile specific or
 * component specific but not both.
 *
 * <P>The attributes to use are defined by a hierarchy. The hierarchy is:
 *
 * <ul>
 * <li> Tile and component specific attribute</li>
 * <li> Tile specific default attribute</li>
 * <li> Component main default attribute</li>
 * <li> Main default attribute</li>
 * </ul>
 * */

public class ModuleSpec implements Cloneable {

    /** The identifier for a specification module that applies only to
     * components */
    public final static byte SPEC_TYPE_COMP = 0;

    /** The identifier for a specification module that applies only to
        tiles */
    public final static byte SPEC_TYPE_TILE = 1;

    /** The identifier for a specification module that applies both to
     * tiles and components */
    public final static byte SPEC_TYPE_TILE_COMP = 2;

    /** The identifier for default specification */
    public final static byte SPEC_DEF = 0;

    /** The identifier for "component default" specification */
    public final static byte SPEC_COMP_DEF = 1;

    /** The identifier for "tile default" specification */
    public final static byte SPEC_TILE_DEF = 2;

    /** The identifier for a "tile-component" specification */
    public final static byte SPEC_TILE_COMP = 3;

    /** The type of the specification module */
    protected int specType;

    /** The number of tiles */
    protected int nTiles = 0;

    /** The number of components */
    protected int nComp = 0;

    /** The spec type for each tile-component. The first index is
     * the tile index, the second is the component index.
     */
    protected byte[][] specValType;

    /** Default value for each tile-component */
    protected Object def = null;

    /** The default value for each component. Null if no component
        specific value is defined */
    protected Object[] compDef = null;

    /** The default value for each tile. Null if no tile specific
        value is defined */
    protected Object[] tileDef = null;

    /** The specific value for each tile-component. Value of tile 16 component
     * 3 is accessible through the hash value "t16c3". Null if no
     * tile-component specific value is defined */
    protected Hashtable tileCompVal;

    /** The specified value in string format */
    protected String specified;

    public ModuleSpec getCopy() {
        return (ModuleSpec)this.clone();
    }

    /**
     * Constructs a 'ModuleSpec' object, initializing all the components and
     * tiles to the 'SPEC_DEF' spec val type, for the specified number of
     * components and tiles.
     *
     * @param nt The number of tiles
     *
     * @param nc The number of components
     *
     * @param type the type of the specification module i.e. tile specific,
     * component specific or both.
     * */
    public ModuleSpec(int nt, int nc, byte type) {

	nTiles = nt;
	nComp = nc;
        specValType = new byte[nt][nc];
        switch (type) {
        case SPEC_TYPE_TILE:
            specType = SPEC_TYPE_TILE;
            break;
        case SPEC_TYPE_COMP:
            specType = SPEC_TYPE_COMP;
            break;
        case SPEC_TYPE_TILE_COMP:
            specType = SPEC_TYPE_TILE_COMP;
            break;
        }
    }

    protected Object clone() {
        ModuleSpec ms;
        try {
            ms = (ModuleSpec)super.clone();
        } catch(CloneNotSupportedException e) {
            throw new Error("Error when cloning ModuleSpec instance");
        }
        // Create a copy of the specValType array
        ms.specValType = new byte[nTiles][nComp];
        for(int t=0; t<nTiles; t++) {
            for(int c=0; c<nComp; c++) {
                ms.specValType[t][c] = specValType[t][c];
            }
        }
        // Create a copy of tileDef
        if(tileDef!=null) {
            ms.tileDef = new Object[nTiles];
            for(int t=0; t<nTiles; t++) {
                ms.tileDef[t] = tileDef[t];
            }
        }
        // Create a copy of tileCompVal
        if(tileCompVal!=null) {
            ms.tileCompVal = new Hashtable();
            String tmpKey;
            Object tmpVal;
            for(Enumeration e=tileCompVal.keys(); e.hasMoreElements(); ) {
                tmpKey = (String)e.nextElement();
                tmpVal = tileCompVal.get(tmpKey);
                ms.tileCompVal.put(tmpKey,tmpVal);
            }
        }
        return ms;
    }

    /** 
     * Rotate the ModuleSpec instance by 90 degrees (this modifies only tile
     * and tile-component specifications).
     *
     * @param anT Number of tiles along horizontal and vertical axis after
     * rotation. 
     * */
    public void rotate90(Point anT) {
        // Rotate specValType
        byte[][] tmpsvt = new byte[nTiles][];
        int ax,ay;
        Point bnT = new Point(anT.y,anT.x);
        for(int by=0; by<bnT.y; by++) {
            for(int bx=0; bx<bnT.x; bx++) {
                ay = bx;
                ax = bnT.y-by-1;
                tmpsvt[ay*anT.x+ax] = specValType[by*bnT.x+bx];
            }
        }
        specValType = tmpsvt;

        // Rotate tileDef
        if(tileDef!=null) {
            Object[] tmptd = new Object[nTiles];
            for(int by=0; by<bnT.y; by++) {
                for(int bx=0; bx<bnT.x; bx++) {
                    ay = bx;
                    ax = bnT.y-by-1;
                    tmptd[ay*anT.x+ax] = tileDef[by*bnT.x+bx];
                }
            }
            tileDef = tmptd;
        }

        // Rotate tileCompVal
        if(tileCompVal!=null && tileCompVal.size()>0) {
            Hashtable tmptcv = new Hashtable();
            String tmpKey;
            Object tmpVal;
            int btIdx,atIdx;
            int i1,i2;
            int bx,by;
            for(Enumeration e=tileCompVal.keys(); e.hasMoreElements(); ) {
                tmpKey = (String)e.nextElement();
                tmpVal = tileCompVal.get(tmpKey);
                i1 = tmpKey.indexOf('t');
                i2 = tmpKey.indexOf('c');
                btIdx = (new Integer(tmpKey.substring(i1+1,i2))).intValue();
                bx = btIdx%bnT.x;
                by = btIdx/bnT.x;
                ay = bx;
                ax = bnT.y-by-1;
                atIdx = ax+ay*anT.x;
                tmptcv.put("t"+atIdx+tmpKey.substring(i2),tmpVal);
            }
            tileCompVal = tmptcv;
        }
    }

    /**
     * Sets default value for this module
     * */
    public void setDefault(Object value){
	def = value;
    }

    /**
     * Gets default value for this module.
     *
     * @return The default value (Must be casted before use)
     * */
    public Object getDefault(){
	return def;
    }

    /**
     * Sets default value for specified component and specValType tag if
     * allowed by its priority.
     *
     * @param c Component index
     * */
    public void setCompDef(int c, Object value){
        if ( specType == SPEC_TYPE_TILE ) {
            String errMsg = "Option whose value is '"+value+"' cannot be "
                +"specified for components as it is a 'tile only' specific "
                +"option";
            throw new Error(errMsg);
        }
	if(compDef==null)
	    compDef = new Object[nComp];
	for(int i=0; i<nTiles; i++){
	    if(specValType[i][c]<SPEC_COMP_DEF) {
		specValType[i][c] = SPEC_COMP_DEF;
            }
	}
	compDef[c] = value;
    }

    /**
     * Gets default value of the specified component. If no specification have
     * been entered for this component, returns default value.
     *
     * @param c Component index
     *
     * @return The default value for this component (Must be casted before
     * use)
     *
     * @see #setCompDef
     * */
    public Object getCompDef(int c){
        if ( specType == SPEC_TYPE_TILE ) {
            throw new Error("Illegal use of ModuleSpec class");
        }
	if(compDef==null || compDef[c]==null){
	    return getDefault();
	}
	else
	    return compDef[c];
    }

    /**
     * Sets default value for specified tile and specValType tag if
     * allowed by its priority.
     *
     * @param t Tile index.
     * */
    public void setTileDef(int t, Object value){
        if ( specType == SPEC_TYPE_COMP ) {
            String errMsg = "Option whose value is '"+value+"' cannot be "
                + "specified for tiles as it is a 'component only' specific "
                + "option";
            throw new Error(errMsg);
        }
	if(tileDef==null)
	    tileDef = new Object[nTiles];
	for(int i=0; i<nComp; i++){
	    if(specValType[t][i]<SPEC_TILE_DEF){
		specValType[t][i] = SPEC_TILE_DEF;
	    }
	}
	tileDef[t] = value;
    }

    /**
     * Gets default value of the specified tile. If no specification
     * has been entered, it returns the default value.
     *
     * @param t Tile index
     *
     * @return The default value for this tile (Must be casted before use)
     *
     * @see #setTileDef
     * */
    public Object getTileDef(int t){
        if ( specType == SPEC_TYPE_COMP ) {
            throw new Error("Illegal use of ModuleSpec class");
        }
	if(tileDef==null || tileDef[t]==null){
	    return getDefault();
	}
	else
	    return tileDef[t];
    }

    /**
     * Sets value for specified tile-component.
     *
     * @param t Tie index
     *
     * @param c Component index
     * */
    public void setTileCompVal(int t,int c, Object value){
        if ( specType != SPEC_TYPE_TILE_COMP ) {
            String errMsg = "Option whose value is '"+value+"' cannot be "
                + "specified for ";
            switch (specType) {
            case SPEC_TYPE_TILE:
                errMsg += "components as it is a 'tile only' specific option";
                break;
            case SPEC_TYPE_COMP:
                errMsg += "tiles as it is a 'component only' specific option";
                break;
            }
            throw new Error(errMsg);
        }
	if(tileCompVal==null)
	    tileCompVal = new Hashtable();
	specValType[t][c] = SPEC_TILE_COMP;
	tileCompVal.put("t"+t+"c"+c,value);
    }

    /**
     * Gets value of specified tile-component. This method calls getSpec but
     * has a public access.
     *
     * @param t Tile index
     *
     * @param c Component index
     *
     * @return The value of this tile-component (Must be casted before use)
     *
     * @see #setTileCompVal
     *
     * @see #getSpec
     * */
    public Object getTileCompVal(int t,int c){
        if ( specType != SPEC_TYPE_TILE_COMP ) {
            throw new Error("Illegal use of ModuleSpec class");
        }
	return getSpec(t,c);
    }

    /**
     * Gets value of specified tile-component without knowing if a
     * specific tile-component value has been previously entered. It
     * first check if a tile-component specific value has been
     * entered, then if a tile specific value exist, then if a
     * component specific value exist. If not the default value is
     * returned.
     *
     * @param t Tile index
     *
     * @param c Component index
     *
     * @return Value for this tile component.
     * */
    protected Object getSpec(int t,int c){
	switch(specValType[t][c]){
	case SPEC_DEF:
	    return getDefault();
	case SPEC_COMP_DEF:
	    return getCompDef(c);
	case SPEC_TILE_DEF:
	    return getTileDef(t);
	case SPEC_TILE_COMP:
	    return tileCompVal.get("t"+t+"c"+c);
	default:
	    throw new IllegalArgumentException("Not recognized spec type");
	}
    }

    /**
     * Return the spec type of the given tile-component.
     *
     * @param t Tile index
     *
     * @param c Component index
     * */
    public byte getSpecValType(int t,int c){
	return specValType[t][c];
    }

    /**
     * Whether or not specifications have been entered for the given
     * component.
     *
     * @param c Index of the component
     *
     * @return True if component specification has been defined
     * */
    public boolean isCompSpecified(int c){
	if(compDef==null || compDef[c]==null)
	    return false;
	else
	    return true;
    }

    /**
     * Whether or not specifications have been entered for the given
     * tile.
     *
     * @param t Index of the tile
     *
     * @return True if tile specification has been entered
     * */
    public boolean isTileSpecified(int t){
	if(tileDef==null || tileDef[t]==null)
	    return false;
	else
	    return true;
    }

    /**
     * Whether or not a tile-component specification has been defined
     *
     * @param t Tile index
     *
     * @param c Component index
     *
     * @return True if a tile-component specification has been defined.
     * */
    public boolean isTileCompSpecified(int t,int c){
	if(tileCompVal==null || tileCompVal.get("t"+t+"c"+c)==null)
	    return false;
	else
	    return true;
    }

    /**
     * This method is responsible of parsing tile indexes set and
     * component indexes set for an option. Such an argument must
     * follow the following policy:<br>
     *
     * <tt>t\<indexes set\></tt> or <tt>c\<indexes set\></tt> where
     * tile or component indexes are separated by commas or a
     * dashes.
     *
     * <p><u>Example:</u><br>
     * <li> <tt>t0,3,4</tt> means tiles with indexes 0, 3 and 4.<br>
     * <li> <tt>t2-4</tt> means tiles with indexes 2,3 and 4.<br>
     *
     * It returns a boolean array skteching which tile or component are
     * concerned by the next parameters.
     *
     * @param word The word to parse.
     *
     * @param maxIdx Maximum authorized index
     *
     * @return Indexes concerned by this parameter.
     * */
    public static final boolean[] parseIdx(String word, int maxIdx){
	int nChar = word.length(); // Number of characters
	char c = word.charAt(0);   // current character
	int idx = -1;              // Current (tile or component) index
	int lastIdx = -1;          // Last (tile or component) index
	boolean isDash = false;    // Whether or not last separator was a dash

	boolean[] idxSet = new boolean[maxIdx];
	int i=1; // index of the current character

	while(i<nChar){
	    c = word.charAt(i);
	    if(Character.isDigit(c)){
		if(idx==-1)
		    idx = 0;
		idx = idx*10+ (c-'0');
	    }
	    else{
		if(idx==-1 || (c!=',' && c!='-')){
		   throw new IllegalArgumentException("Bad construction for "+
						      "parameter: "+word);
		}
		if(idx<0 || idx>=maxIdx){
		    throw new IllegalArgumentException("Out of range index in "+
						       "parameter `"+word+"' : "+
						       +idx);
		}

		// Found a comma
		if(c==','){
		    if(isDash){ // Previously found a dash, fill idxSet
			for(int j=lastIdx+1; j<idx; j++){
			    idxSet[j] = true;
			}
		    }
		    isDash = false;
		}
		else // Found a dash
		    isDash = true;

		// Udate idxSet
		idxSet[idx] = true;
		lastIdx = idx;
		idx=-1;
	    }
	    i++;
	}

	// Process last found index
	if(idx<0 || idx>=maxIdx){
	    throw new IllegalArgumentException("Out of range index in "+
					       "parameter `"+word+"' : "+idx);
	}
	if(isDash)
	    for(int j=lastIdx+1; j<idx; j++){
		idxSet[j] = true;
	    }
	idxSet[idx] = true;

	return idxSet;
    }

    /**
     * Returns a tile-component representative using default value.
     *
     * @return Tile component index in an array (first element: tile
     * index, second element: component index).
     * */
/*
    public int[] getDefRep(){
	int[] tcidx = new int[2];
	for(int t=nTiles-1; t>=0; t--){
	    for(int c=nComp-1; c>=0; c--){
		if(specValType[t][c]==SPEC_DEF){
		    tcidx[0] = t;
		    tcidx[1] = c;
		    return tcidx;
		}
	    }
	}

        throw new IllegalArgumentException("No representative for "+
                                           "default value");
    }
*/
    /**
     * Returns a component representative using tile default value.
     *
     * @param t Tile index
     *
     * @return component index of the representant
     * */
/*
    public int getTileDefRep(int t){
	for(int c=nComp-1; c>=0; c--)
	    if(specValType[t][c]==SPEC_TILE_DEF){
		return c;
	    }

	throw new IllegalArgumentException("No representative for tile "+
					   "default value");
    }
*/
    /**
     * Returns a tile representative using component default value.
     *
     * @param c Component index
     *
     * @return tile index of the representant
     * */
/*
    public int getCompDefRep(int c){
	for(int t=nTiles-1; t>=0; t--) {
	    if(specValType[t][c]==SPEC_COMP_DEF){
		return t;
	    }
        }

	throw new IllegalArgumentException("No representative for component "+
					   "default value, c="+c);
    }
*/
/*
    public String getSpecified() {
        return specified;
    }
*/
}
