//                              -*- Mode: Java -*- 
// QDRegionReservedOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Wed Jul 14 14:03:44 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 11:32:45 2000
// Update Count    : 6
// Status          : OK
// 

package ch.epfl.lse.jqd.opcode;

import java.io.IOException;
import ch.epfl.lse.jqd.io.QDInputStream;

import ch.epfl.lse.jqd.basics.QDRegion;

/** This opcodes handles region reserved opcodes 
 *  @version 1.0 
 *  @author Matthias Wiesmann
 *  @see RegionOP
 */ 

public class QDRegionReservedOP extends QDReservedOP
{
    public int read(QDInputStream stream) 
	throws IOException {
	final QDRegion region = new QDRegion(stream);
	return region.rgnSize ;
    } // read
} // QDRegionReservedOP

