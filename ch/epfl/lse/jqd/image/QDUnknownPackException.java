//                              -*- Mode: Java -*- 
// QDUnknownPackException.java --- 
// Author          : Matthias Wiesmann
// Created On      : Wed Dec  6 11:46:15 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Wed Dec  6 11:49:41 2000
// Update Count    : 6
// Status          : OK
// 

package ch.epfl.lse.jqd.image;

import ch.epfl.lse.jqd.basics.QDException;

/** Exception signaling an illegal pack mode inside a copy-bit operation
 *  @version 1.0
 *  @author Matthias Wiesmann
 *  @see ch.epfl.lse.jqd.image.QDDirectPixMap
 */

public class QDUnknownPackException extends QDException
{
    /** the pack type */ 
    protected final int packType ;
    public QDUnknownPackException(int p) {
	packType=p ;
    } // QDUnknownPackException
    public String toString() {
	return "Unknown Pack type: "+packType;
    } // toString
    
} // QDUnknownPackException
