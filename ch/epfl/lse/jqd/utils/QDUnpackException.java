//                              -*- Mode: Java -*- 
// QDUnpackException.java --- 
// Author          : Matthias Wiesmann
// Created On      : Thu Dec 16 16:43:01 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 11:10:50 2000
// Update Count    : 2
// Status          : Renamed
// 

package ch.epfl.lse.jqd.utils;

/** This Exception is thrown when a problem occurs 
 *  during the unpacking of a packed line of data
 *  @author Matthias Wiesmann
 *  @version 1.1
 *  @see QDBitUtils#unpackLine
 */ 

import ch.epfl.lse.jqd.basics.QDException;

public class QDUnpackException extends QDException
{
    public String toString() {
	return ("Unpacking Exception");
    } // toString
} // QDUnpackException
