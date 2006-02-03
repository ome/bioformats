//                              -*- Mode: Java -*- 
// QDPnLocHFrac.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 15:01:37 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 12:16:21 2000
// Update Count    : 3
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.QDInputStream;
import java.io.IOException;

/** QuickDraw fractionnal pen location.<br>
 *  <strong>Warning</strong> does nothing at the moment
 *  @author Matthias Wiesmann
 *  @version 1.0 revised
 */ 

public class QDPnLocHFrac implements QDOpCode{
    protected int hFrac ;
    public int read(QDInputStream stream) 
	throws IOException {
	hFrac = stream.readShort();
	return 2 ; 
    } // read

    public void execute(QDPort thePort) {
    } // execute

} // QDPnLocHFrac
