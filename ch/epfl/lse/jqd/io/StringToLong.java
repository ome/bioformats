//                              -*- Mode: Java -*- 
// StringToLong.java --- 
// Author          : Matthias Wiesmann
// Created On      : Thu Dec 16 16:33:46 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Tue Nov 21 15:40:49 2000
// Update Count    : 2
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.io;

import ch.epfl.lse.jqd.basics.QDException;

/** Macintosh String have to have a certain length
 *  (31, 255 chars). 
 *  This exception is thrown when the expected length
 *  is not respected
 *  @author Matthias Wiesmann
 *  @version 1.0
 */

public class StringToLong extends QDException 
{
    protected int length ;
    StringToLong(int len) {
	length=len ; }
    
    public String toString(){ 
	return "String to long: "+length ;
    } // toString
} // StringToLong
