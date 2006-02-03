//                              -*- Mode: Java -*- 
// QDGeneralUtils.java --- 
// Author          : Matthias Wiesmann
// Created On      : Thu Dec 16 16:40:54 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Dec 16 16:41:33 1999
// Update Count    : 2
// Status          : Renamed
// 

package ch.epfl.lse.jqd.utils;

import ch.epfl.lse.jqd.opcode.QDOpCode;
import java.util.Vector;

/** This class contains general utilities 
 * various stuff that could not go elsewhere
 * @author Matthias Wiesmann
 * @version 1.1
 */ 

public final class QDGeneralUtils
{
    /** Transforms a vector into an array of opcodes
     * @param v the vector
     * @return the array of opcodes
     */ 
    public static QDOpCode[] buildArray(Vector v) {
	final int l = v.size();
	QDOpCode[] array = new QDOpCode[l];
	for(int i=0;i<l;i++){
	    final Object o = v.elementAt(i);
	    array[i] = (QDOpCode) o ; 
	} // for
	return array ;
    } // buildArray
} // QDGeneralUtils
