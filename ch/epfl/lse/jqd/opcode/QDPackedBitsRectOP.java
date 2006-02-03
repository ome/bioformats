/*
Quickdraw QDBitsRectOP OpCodes 
*/
package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.image.*;

import ch.epfl.lse.jqd.io.QDInputStream;

public class QDPackedBitsRectOP extends QDBitsRectOP {

    public int read(QDInputStream theStream) 
	throws java.io.IOException, QDException {
	final short rowbytes = theStream.readShort();
	bMap = QDPackedBitMap.newMap(rowbytes);
	final int bSize = bMap.read(theStream);
	return(bSize+2);
    }// read

    public String toString() {
	return("Packed Bit Rect "+bMap);
    }
} // QDBitsRectOP
