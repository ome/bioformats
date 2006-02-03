//                              -*- Mode: Java -*- 
// QDException.java --- 
// Author          : Matthias Wiesmann
// Created On      : Thu Dec 16 16:53:55 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Dec 16 16:54:01 1999
// Update Count    : 1
// Status          : Renamed
// 

package ch.epfl.lse.jqd.basics;

/** This class is the root exception for all exception 
 *  in the <code>jqd</code> package.<br>
 *  @author Matthias Wiesmann
 *  @version 1.0 revised
 */

public class QDException extends Exception {

    public QDException(String s) {
	super(s);
    } // QDException
    
    public QDException() {
	super();
    } // QDException
    
    public String toString() {
	    return("QuickDraw Error");
    } 
} // QDException

