//                              -*- Mode: Java -*- 
// QDTextModeOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 14:47:21 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Tue Nov 21 14:47:28 2000
// Update Count    : 1
// Status          : Renamed
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;

public class QDTextModeOP extends QDPnMode {
    public String toString() {return("Text Mode "+QDModes.toString(mode));}
    public void	execute(QDPort thePort) {
	thePort.txMode = mode ;
    } // execute 
    
} // QDTextModeOP
